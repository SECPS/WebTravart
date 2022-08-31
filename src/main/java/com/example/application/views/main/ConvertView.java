package com.example.application.views.main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.application.data.Model;
import com.example.application.data.Tuple;
import com.example.application.views.components.DownloadLinksArea;
import com.example.application.views.components.Footer;
import com.example.application.views.components.InformationLossGrid;
import com.example.application.views.components.ModelTypePicker;
import com.example.application.views.data.TransformationData;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;

import at.jku.cps.travart.core.common.exc.NotSupportedVariablityTypeException;
import at.jku.cps.travart.core.io.FeatureModelReader;
import at.jku.cps.travart.core.io.FeatureModelUVLWriter;
import at.jku.cps.travart.core.io.FeatureModelXMLWriter;
import at.jku.cps.travart.dopler.common.DecisionModelUtils;
import at.jku.cps.travart.dopler.decision.IDecisionModel;
import at.jku.cps.travart.dopler.io.DecisionModelReader;
import at.jku.cps.travart.dopler.io.DecisionModelWriter;
import at.jku.cps.travart.dopler.transformation.DecisionModeltoFeatureModelTransformer;
import at.jku.cps.travart.dopler.transformation.FeatureModeltoDecisionModelTransformer;
import at.jku.cps.travart.ovm.io.OvModelReader;
import at.jku.cps.travart.ovm.io.OvModelWriter;
import at.jku.cps.travart.ovm.model.IOvModel;
import at.jku.cps.travart.ovm.transformation.FeatureModeltoOvModelTransformer;
import at.jku.cps.travart.ovm.transformation.OvModelToFeatureModelTransformer;
import at.jku.cps.travart.ppr.dsl.common.PprDslUtils;
import at.jku.cps.travart.ppr.dsl.io.PprDslReader;
import at.jku.cps.travart.ppr.dsl.io.PprDslWriter;
import at.jku.cps.travart.ppr.dsl.transformation.FeatureModelToPprDslTransformer;
import at.jku.cps.travart.ppr.dsl.transformation.PprDslToFeatureModelTransformer;
import at.sqi.ppr.model.AssemblySequence;
import de.neominik.uvl.ast.UVLModel;
import de.ovgu.featureide.fm.core.base.IFeatureModel;

@Route(value = "", layout = MainView.class)
@PageTitle("TraVarT Online")
public class ConvertView extends VerticalLayout {

	/**
	 *
	 */
	private static final long serialVersionUID = 2306696766319627373L;
	private final MemoryBuffer memoryBuffer = new MemoryBuffer();
	private final Upload singleFileUpload = new Upload(memoryBuffer);
	private ModelTypePicker typePicker;
	private final DownloadLinksArea downloads;
	private Button convertButton;
	private String fileName;
	private final HorizontalLayout horizontal = new HorizontalLayout();
	private final VerticalLayout transformationLayout = new VerticalLayout();
	private final VerticalLayout site;
	private final InformationLossGrid infLossGrid = new InformationLossGrid();
	private final Logger log = LoggerFactory.getLogger("ConvertView");

	private final String READ_ERROR = "Problem reading uploaded file";
	private final String VAR_ERROR = "There was an unsupported variability type in the model";
	private final File UPLOAD_FOLDER = new File(
			System.getProperty("java.io.tmpdir") + "/" + VaadinSession.getCurrent().getPushId());

	private Object model = null;

	public ConvertView() {
		VaadinService.getCurrent().addSessionDestroyListener(l -> {
			try {
				FileUtils.deleteDirectory(UPLOAD_FOLDER);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		if (UPLOAD_FOLDER.setExecutable(false)) {
			log.info("Successfully blocking execution on upload folder.");
		}
		H2 title = new H2("Convert model file");
		StringBuilder sb = new StringBuilder();
		sb.append("Please only upload allowed file formats: ");
		for (String s : Model.getAllFileExtensions()) {
			sb.append(s);
			sb.append(" ");
		}

		setHeight("100%");

		Div hint = new Div();
		hint.add(sb.toString());
		Div hint2 = new Div();
		hint2.add("Files will be deleted once you leave the site. Applies also to generated files.");
		site = new VerticalLayout();
		site.setJustifyContentMode(JustifyContentMode.START);
		VerticalLayout headline = new VerticalLayout();
		headline.add(title, hint, hint2);
		site.add(headline, horizontal);
		add(site);
		setPadding(false);
		VerticalLayout footer = new Footer();
		footer.setHeight("136px");
		add(footer);
		setJustifyContentMode(JustifyContentMode.BETWEEN);
		initTypePicker();
		File downloadLinks = new File(UPLOAD_FOLDER, "convert");
		downloadLinks.mkdirs();
		downloads = new DownloadLinksArea(downloadLinks);
		downloads.setAlignItems(Alignment.START);
		downloads.setVisible(false);
		createUploader(Model.getAllFileExtensions());
		initConvertButton();
		transformationLayout.add(singleFileUpload, typePicker, convertButton, downloads);
		transformationLayout.setJustifyContentMode(JustifyContentMode.START);
		transformationLayout.setWidth("28%");
		horizontal.setWidth("100%");
		horizontal.add(transformationLayout, infLossGrid);
		setMargin(false);
	}

	private void initTypePicker() {
		typePicker = new ModelTypePicker();
		typePicker.setVisible(false);
		typePicker.addValueChangedListener(event -> {
			if (event.getValue() != null) {
				convertButton.setEnabled(true);
			}
		});
	}

	private void initConvertButton() {
		convertButton = new Button("Convert");
		convertButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		convertButton.setEnabled(false);
		convertButton.setVisible(false);
		convertButton.addClickListener(event -> {
			Path targetPath = new File(UPLOAD_FOLDER, "convert").toPath();
			TransformationData transformation = new TransformationData();
			transformation.setName(fileName);
			setSourceMetrics(transformation);
			try {
				targetPath = convertPivotToTarget(targetPath, transformation);
			} catch (IOException e) {
				showNotification("Error happened accessing target file.", NotificationVariant.LUMO_ERROR);
				e.printStackTrace();
			} catch (NotSupportedVariablityTypeException e) {
				showNotification(VAR_ERROR, NotificationVariant.LUMO_ERROR);
				e.printStackTrace();
			}
			infLossGrid.addTransformation(transformation);
			downloads.refreshFileLinks();
			downloads.setVisible(true);
			downloads.scrollIntoView();
		});
	}

	private void setSourceMetrics(final TransformationData data) {
		if (model instanceof IOvModel) {
			IOvModel ovModel = (IOvModel) model;
			data.setSourceConstCount(ovModel.getConstraintCount());
			data.setSourceVarCount(ovModel.getNumberOfVariationPoints());
			data.setTransformType(new Tuple<>(Model.OVM, typePicker.getSelection()));
		} else if (model instanceof IDecisionModel) {
			IDecisionModel decModel = (IDecisionModel) model;
			data.setSourceConstCount(DecisionModelUtils.countRules(decModel));
			data.setSourceVarCount(DecisionModelUtils.getNumberOfDecisions(decModel));
			data.setTransformType(new Tuple<>(Model.DECISION, typePicker.getSelection()));
		} else if (model instanceof UVLModel) {
			// TODO integrate UVL
			showNotification("UVL currently not supported", NotificationVariant.LUMO_CONTRAST);
		} else if (model instanceof IFeatureModel) {
			IFeatureModel featModel = (IFeatureModel) model;
			data.setSourceConstCount(featModel.getConstraintCount());
			data.setSourceVarCount(featModel.getNumberOfFeatures());
			data.setTransformType(new Tuple<>(Model.FEATURE, typePicker.getSelection()));
		} else if (model instanceof AssemblySequence) {
			AssemblySequence pprModel = (AssemblySequence) model;
			data.setSourceConstCount(PprDslUtils.getNumberOfConstraints(pprModel));
			data.setSourceVarCount(PprDslUtils.getNumberOfProducts(pprModel));
			data.setTransformType(new Tuple<>(Model.PPRDSL, typePicker.getSelection()));
		}
	}

	private Path convertPivotToTarget(final Path targetPath, final TransformationData transformation)
			throws IOException, NotSupportedVariablityTypeException {
		IFeatureModel pivotModel = convertModelToPivot(model);
		File targetFile = null;
		int extensionLength = getExtensionByStringHandling(fileName).get().length() + 1;

		String newFileName = fileName.substring(0, fileName.length() - extensionLength);
		boolean duplicate = false;
		int i = 1;
		do {
			try (Stream<Path> paths = Files.walk(targetPath)) {
				String tempName = new String(newFileName);
				if (paths.filter(Files::isRegularFile).anyMatch(f -> f.getFileName().toString().contains(tempName))) {
					newFileName = fileName.substring(0, fileName.length() - extensionLength) + "(" + i + ")";
					i++;
					duplicate = true;
				} else {
					duplicate = false;
				}
			}

		} while (duplicate);
		switch (typePicker.getSelection()) {
		case FEATURE:
			FeatureModelXMLWriter featWriter = new FeatureModelXMLWriter();
			transformation.setTargetConstCount(pivotModel.getConstraintCount());
			transformation.setTargetVarCount(pivotModel.getNumberOfFeatures());
			targetFile = new File(targetPath.toString(), newFileName + ".xml");
			featWriter.write(pivotModel, targetFile.toPath());
			break;
		case UVL:
			FeatureModelUVLWriter uvlWriter = new FeatureModelUVLWriter();
			targetFile = new File(targetPath.toString(), newFileName + ".uvl");
			transformation.setTargetConstCount(pivotModel.getConstraintCount());
			transformation.setTargetVarCount(pivotModel.getNumberOfFeatures());
			uvlWriter.write(pivotModel, targetFile.toPath());
			break;
		case OVM:
			FeatureModeltoOvModelTransformer ovnmTransformer = new FeatureModeltoOvModelTransformer();
			OvModelWriter ovmWriter = new OvModelWriter();
			targetFile = new File(targetPath.toString(), newFileName + Model.getExtensions(Model.OVM).get(0));
			IOvModel ovTargetModel = ovnmTransformer.transform(pivotModel);
			transformation.setTargetConstCount(ovTargetModel.getConstraintCount());
			transformation.setTargetVarCount(ovTargetModel.getNumberOfVariationPoints());
			ovmWriter.write(ovTargetModel, targetFile.toPath());
			break;
		case PPRDSL:
			FeatureModelToPprDslTransformer pprTransformer = new FeatureModelToPprDslTransformer();
			PprDslWriter pprWriter = new PprDslWriter();
			targetFile = new File(targetPath.toString(), newFileName + Model.getExtensions(Model.PPRDSL).get(0));
			AssemblySequence pprTargetModel = pprTransformer.transform(pivotModel);
			transformation.setTargetConstCount(PprDslUtils.getNumberOfConstraints(pprTargetModel));
			transformation.setTargetVarCount(PprDslUtils.getNumberOfProducts(pprTargetModel));
			pprWriter.write(pprTargetModel, targetFile.toPath());
			break;
		case DECISION:
			FeatureModeltoDecisionModelTransformer decisionTransformer = new FeatureModeltoDecisionModelTransformer();
			DecisionModelWriter decisionWriter = new DecisionModelWriter();
			targetFile = new File(targetPath.toString(), newFileName + Model.getExtensions(Model.DECISION).get(0));
			IDecisionModel decTargetModel = decisionTransformer.transform(pivotModel);
			transformation.setTargetConstCount(DecisionModelUtils.countRules(decTargetModel));
			transformation.setTargetVarCount(DecisionModelUtils.getNumberOfDecisions(decTargetModel));
			decisionWriter.write(decTargetModel, targetFile.toPath());
			break;
		default:
			showNotification("Error recognizing target model.", NotificationVariant.LUMO_ERROR);
		}
		if (targetFile != null) {
			targetFile.setReadOnly();
		}
		return targetFile == null ? null : targetFile.toPath();
	}

	private IFeatureModel convertModelToPivot(final Object model) {
		IFeatureModel toConvert = null;
		try {
			if (model instanceof IOvModel) {
				IOvModel ovModel = (IOvModel) model;
				OvModelToFeatureModelTransformer trans = new OvModelToFeatureModelTransformer();
				toConvert = trans.transform(ovModel);
			} else if (model instanceof IDecisionModel) {
				IDecisionModel decModel = (IDecisionModel) model;
				DecisionModeltoFeatureModelTransformer trans = new DecisionModeltoFeatureModelTransformer();
				toConvert = trans.transform(decModel);
			} else if (model instanceof UVLModel) {
				toConvert = (IFeatureModel) model;
			} else if (model instanceof IFeatureModel) {
				IFeatureModel featModel = (IFeatureModel) model;
				toConvert = featModel;
			} else if (model instanceof AssemblySequence) {
				AssemblySequence pprModel = (AssemblySequence) model;
				PprDslToFeatureModelTransformer trans = new PprDslToFeatureModelTransformer();
				toConvert = trans.transform(pprModel);
			}
		} catch (NotSupportedVariablityTypeException e) {
			showNotification("Travart encountered an unsupported variability type during transformation",
					NotificationVariant.LUMO_ERROR);
		}
		return toConvert;
	}

	private Model detectModel(final File file) throws IOException {
		model = null;
		Model m = Model.NONE;
		Optional<String> ext = getExtensionByStringHandling(file.getName());
		if (!ext.isPresent()) {
			return Model.NONE;
		}
		String extension = ext.get();
		if (Model.getExtensions(Model.UVL).stream().anyMatch(e -> e.endsWith(extension))) {
			try {
				m = parseUVLModel(file);
			} catch (IOException | NotSupportedVariablityTypeException e1) {

			}
		}
		if (m == Model.NONE && Model.getExtensions(Model.DECISION).stream().anyMatch(e -> e.endsWith(extension))) {
			try {
				m = parseDecisionModel(file);
			} catch (IOException | NotSupportedVariablityTypeException e1) {
			}
		}
		if (m == Model.NONE && Model.getExtensions(Model.FEATURE).stream().anyMatch(e -> e.endsWith(extension))) {
			try {
				m = parseFeatureModel(file, false);
			} catch (IOException | NotSupportedVariablityTypeException e1) {
			}
		}
		if (m == Model.NONE && Model.getExtensions(Model.PPRDSL).stream().anyMatch(e -> e.endsWith(extension))) {
			try {
				m = parsePPRDSLModel(file);
			} catch (IOException | NotSupportedVariablityTypeException e1) {
			}
		}
		if (m == Model.NONE && Model.getExtensions(Model.OVM).stream().anyMatch(e -> e.endsWith(extension))) {
			try {
				m = parseOVMModel(file);
			} catch (IOException | NotSupportedVariablityTypeException e1) {
			}
		}
		if (m == Model.NONE) {
			throw new IOException();
		}
		return m;
	}

	private Model parsePPRDSLModel(final File file) throws IOException, NotSupportedVariablityTypeException {
		PprDslReader pprReader = new PprDslReader();
		model = pprReader.read(file.toPath());
		return Model.PPRDSL;
	}

	private Model parseFeatureModel(final File file, final boolean uvl)
			throws IOException, NotSupportedVariablityTypeException {
		FeatureModelReader fmr = new FeatureModelReader();
		model = fmr.read(file.toPath());
		if (uvl) {
			if (model == null) {
				showNotification("Error reading UVL Model", NotificationVariant.LUMO_ERROR);
				throw new IOException();
			}
			showNotification("UVL Model detected", NotificationVariant.LUMO_SUCCESS);
			return Model.UVL;
		}
		if (model == null) {
			showNotification("Error reading Feature Model", NotificationVariant.LUMO_ERROR);
			throw new IOException();
		}
		showNotification("Feature Model detected", NotificationVariant.LUMO_SUCCESS);

		return Model.FEATURE;
	}

	private Model parseDecisionModel(final File file) throws IOException, NotSupportedVariablityTypeException {
		DecisionModelReader dmr = new DecisionModelReader();
		model = dmr.read(file.toPath());
		showNotification("Decision Model detected", NotificationVariant.LUMO_SUCCESS);
		return Model.DECISION;
	}

	private Model parseUVLModel(final File file) throws IOException, NotSupportedVariablityTypeException {

		return parseFeatureModel(file, true);
	}

	private Model parseOVMModel(final File f) throws IOException, NotSupportedVariablityTypeException {
		OvModelReader ovReader = new OvModelReader();
		model = ovReader.read(f.toPath());
		showNotification("OVM Model detected", NotificationVariant.LUMO_SUCCESS);
		return Model.OVM;
	}

	private File safeFile(final InputStream fileData, final String filename) {
		String contents = null;
		File f = null;
		try {
			contents = IOUtils.toString(fileData, StandardCharsets.UTF_8);
			f = new File(UPLOAD_FOLDER, filename);
			try (FileWriter fw = new FileWriter(f)) {
				fw.write(contents);
				fw.flush();
				if (!f.setReadOnly()) {
					log.error("Failed to make " + filename + " read only.");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return f;
	}

	private Optional<String> getExtensionByStringHandling(final String filename) {
		return Optional.ofNullable(filename).filter(f -> f.contains("."))
				.map(f -> f.substring(filename.lastIndexOf(".") + 1));
	}

	private void createUploader(final String... extensions) {
		Button uploadButton = new Button("Upload model...");
		uploadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		singleFileUpload.setUploadButton(uploadButton);
		Span dropLabel = createDropLabel();
		singleFileUpload.setDropLabel(dropLabel);
		singleFileUpload.setAcceptedFileTypes(extensions);
		singleFileUpload.addFileRejectedListener(event -> showNotification("Please only upload supported file formats.",
				NotificationVariant.LUMO_ERROR));
		singleFileUpload.setMaxFileSize(52428800);
		singleFileUpload.addSucceededListener(event -> {
			// Get information about the uploaded file
			try (InputStream fileData = memoryBuffer.getInputStream()) {
				fileName = event.getFileName();
				File file = safeFile(fileData, fileName);
				file.deleteOnExit();
				Model modeltype = detectModel(file);
				addTypePicker(modeltype);
			} catch (IOException e) {
				showNotification(READ_ERROR, NotificationVariant.LUMO_ERROR);
				e.printStackTrace();
				typePicker.setEnabled(false);
				convertButton.setEnabled(false);
			}
		});
		singleFileUpload.getElement().addEventListener("file-remove", e -> {
			typePicker.setEnabled(false);
			convertButton.setEnabled(false);
		});
	}

	private static Span createDropLabel() {
		Span cloudHint = new Span("Drop model file here.");
		return new Span(cloudHint);
	}

	private void addTypePicker(final Model modelType) {
		typePicker.setItemsForSourceModel(modelType);
		typePicker.setVisible(true);
		typePicker.setEnabled(true);
		convertButton.setVisible(true);
	}

	public static void showNotification(final String errorText, final NotificationVariant theme) {
		Notification notification = new Notification();
		notification.addThemeVariants(theme);
		notification.setPosition(Position.BOTTOM_CENTER);
		notification.setDuration(8000);
		Div text = new Div(new Text(errorText));

		Button closeButton = new Button(new Icon("lumo", "cross"));
		closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		closeButton.getElement().setAttribute("aria-label", "Close");
		closeButton.addClickListener(event2 -> notification.close());

		HorizontalLayout layout = new HorizontalLayout(text, closeButton);
		layout.setAlignItems(Alignment.END);

		notification.add(layout);
		notification.open();
	}

}
