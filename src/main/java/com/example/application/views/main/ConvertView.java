package com.example.application.views.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.application.data.Model;
import com.example.application.data.Tuple;
import com.example.application.views.components.CookieDialog;
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
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;

import at.jku.cps.travart.core.common.exc.NotSupportedVariablityTypeException;
import at.jku.cps.travart.core.io.FeatureModelReader;
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
import de.neominik.uvl.UVLParser;
import de.neominik.uvl.ast.UVLModel;
import de.ovgu.featureide.fm.core.base.IFeatureModel;

@Route(value = "convert", layout = MainView.class)
@PageTitle("Travart Online | Converter")

@RouteAlias(value = "", layout = MainView.class)
public class ConvertView extends VerticalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2306696766319627373L;
	private MemoryBuffer memoryBuffer = new MemoryBuffer();
	private Upload singleFileUpload = new Upload(memoryBuffer);
	private ProgressBar progressBar = new ProgressBar();
	private Div progressBarLabel = new Div();
	private ModelTypePicker typePicker;
	private DownloadLinksArea downloads;
	private Button convertButton;
	private String fileName;
	private Div progressBarSubLabel = new Div();
	private HorizontalLayout horizontal = new HorizontalLayout();
	private VerticalLayout transformationLayout = new VerticalLayout();
	private InformationLossGrid infLossGrid = new InformationLossGrid();
	private final Logger log = LoggerFactory.getLogger("ConvertView");

	private final String READ_ERROR = "Problem reading uploaded file";
	private final String VAR_ERROR = "There was an unsupported variability type in the model";
	private final File UPLOAD_FOLDER = new File("./upload/" + VaadinSession.getCurrent().getPushId());

	private Object model = null;

	public ConvertView() {
		VaadinService.getCurrent().addSessionDestroyListener(l -> {
			try {
				FileUtils.deleteDirectory(UPLOAD_FOLDER);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		UPLOAD_FOLDER.setExecutable(false);
		H2 title = new H2("Convert model file");
		StringBuilder sb = new StringBuilder();
		sb.append("Please only upload allowed file formats: ");
		for (String s : Model.getAllFileExtensions()) {
			sb.append(s);
			sb.append(" ");
		}
		setHeight("100%");
		Paragraph hint = new Paragraph(sb.toString());
		VerticalLayout content = new VerticalLayout();
		content.add(title, hint);
		add(content, horizontal);
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
		transformationLayout.setWidth("28%");
		horizontal.setWidth("100%");
		horizontal.add(transformationLayout, infLossGrid);
		setMargin(false);
	}

	private void initTypePicker() {
		typePicker = new ModelTypePicker();
		typePicker.setVisible(false);
		typePicker.addValueChangedListener(event -> {
			if (event.getValue() != null)
				convertButton.setEnabled(true);
		});
	}

	private void initConvertButton() {
		convertButton = new Button("Convert");
		convertButton.setEnabled(false);
		convertButton.setVisible(false);
		convertButton.addClickListener(event -> {
			addLoadingBar("Converting model...");
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
			removeLoadingBar();
			infLossGrid.addTransformation(transformation);
			downloads.refreshFileLinks();
			downloads.setVisible(true);
		});
	}

	private void setSourceMetrics(TransformationData data) {
		if (model instanceof IOvModel ovModel) {
			data.setSourceConstCount(ovModel.getConstraintCount());
			data.setSourceVarCount(ovModel.getNumberOfVariationPoints());
			data.setTransformType(new Tuple<>(Model.OVM, typePicker.getSelection()));
		} else if (model instanceof IDecisionModel decModel) {
			data.setSourceConstCount(DecisionModelUtils.countRules(decModel));
			data.setSourceVarCount(DecisionModelUtils.getNumberOfDecisions(decModel));
			data.setTransformType(new Tuple<>(Model.DECISION, typePicker.getSelection()));
		} else if (model instanceof UVLModel uvlModel) {
			// TODO integrate UVL
			showNotification("UVL currently not supported", NotificationVariant.LUMO_CONTRAST);
		} else if (model instanceof IFeatureModel featModel) {
			data.setSourceConstCount(featModel.getConstraintCount());
			data.setSourceVarCount(featModel.getNumberOfFeatures());
			data.setTransformType(new Tuple<>(Model.FEATURE, typePicker.getSelection()));
		} else if (model instanceof AssemblySequence pprModel) {
			data.setSourceConstCount(PprDslUtils.getNumberOfConstraints(pprModel));
			data.setSourceVarCount(PprDslUtils.getNumberOfProducts(pprModel));
			data.setTransformType(new Tuple<>(Model.PPRDSL, typePicker.getSelection()));
		}
	}

	private Path convertPivotToTarget(Path targetPath, TransformationData transformation)
			throws IOException, NotSupportedVariablityTypeException {
		IFeatureModel pivotModel = convertModelToPivot(model);
		File targetFile = null;
		int extensionLength = getExtensionByStringHandling(fileName).get().length() + 1;
		String newFileName = fileName.substring(0, fileName.length() - extensionLength);
		switch (typePicker.getSelection()) {
		case FEATURE:
			FeatureModelXMLWriter featWriter = new FeatureModelXMLWriter();
			transformation.setTargetConstCount(pivotModel.getConstraintCount());
			transformation.setTargetVarCount(pivotModel.getNumberOfFeatures());
			targetFile = new File(targetPath.toString(), newFileName + ".xml");
			featWriter.write(pivotModel, targetFile.toPath());
			break;
		case UVL:
			showNotification("UVL currently not supported", NotificationVariant.LUMO_ERROR);
			break; // TODO
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
		if (targetFile != null)
			targetFile.setReadOnly();
		return targetFile == null ? null : targetFile.toPath();
	}

	private IFeatureModel convertModelToPivot(Object model) {
		IFeatureModel toConvert = null;
		try {
			if (model instanceof IOvModel ovModel) {
				OvModelToFeatureModelTransformer trans = new OvModelToFeatureModelTransformer();
				toConvert = trans.transform(ovModel);
			} else if (model instanceof IDecisionModel decModel) {
				DecisionModeltoFeatureModelTransformer trans = new DecisionModeltoFeatureModelTransformer();
				toConvert = trans.transform(decModel);
			} else if (model instanceof UVLModel uvlModel) {
				// TODO integrate UVL
				showNotification("UVL currently not supported", NotificationVariant.LUMO_CONTRAST);
			} else if (model instanceof IFeatureModel featModel) {
				toConvert = featModel;
			} else if (model instanceof AssemblySequence pprModel) {
				PprDslToFeatureModelTransformer trans = new PprDslToFeatureModelTransformer();
				toConvert = trans.transform(pprModel);
			}
		} catch (NotSupportedVariablityTypeException e) {
			showNotification("Travart encountered an unsupported variability type during transformation",
					NotificationVariant.LUMO_ERROR);
		}
		return toConvert;
	}

	private Model detectModel(File file) {
		Model m = Model.NONE;
		Optional<String> ext = getExtensionByStringHandling(file.getName());
		if (!ext.isPresent()) {
			return Model.NONE;
		}
		String extension = ext.get();
		String contents = null;
		try (FileInputStream fileStream = new FileInputStream(file)) {
			contents = IOUtils.toString(fileStream, StandardCharsets.UTF_8);
		} catch (IOException e2) {
			showNotification(READ_ERROR, NotificationVariant.LUMO_ERROR);
		}
		if (Model.getExtensions(Model.UVL).stream().anyMatch(e -> e.endsWith(extension))) {
			m = parseUVLModel(contents);
		}
		if (m == Model.NONE && Model.getExtensions(Model.DECISION).stream().anyMatch(e -> e.endsWith(extension))) {
			m = parseDecisionModel(file);
		}
		if (m == Model.NONE && Model.getExtensions(Model.FEATURE).stream().anyMatch(e -> e.endsWith(extension))) {
			m = parseFeatureModel(file);
		}
		if (m == Model.NONE && Model.getExtensions(Model.PPRDSL).stream().anyMatch(e -> e.endsWith(extension))) {
			m = parsePPRDSLModel(file);
		}
		if (m == Model.NONE && Model.getExtensions(Model.OVM).stream().anyMatch(e -> e.endsWith(extension))) {
			m = parseOVMModel(file);
		}
		if (m == Model.NONE)
			showNotification("Model Type not detected", NotificationVariant.LUMO_ERROR);
		return m;
	}

	private Model parsePPRDSLModel(File file) {
		PprDslReader pprReader = new PprDslReader();
		try {
			model = pprReader.read(file.toPath());
		} catch (IOException e) {
			showNotification(READ_ERROR, NotificationVariant.LUMO_ERROR);
			e.printStackTrace();
		} catch (NotSupportedVariablityTypeException e) {
			showNotification(VAR_ERROR, NotificationVariant.LUMO_ERROR);
			e.printStackTrace();
		}
		return Model.PPRDSL;
	}

	private Model parseFeatureModel(File file) {
		FeatureModelReader fmr = new FeatureModelReader();
		try {
			model = fmr.read(file.toPath());
			showNotification("Feature Model detected", NotificationVariant.LUMO_SUCCESS);
			return Model.FEATURE;
		} catch (IOException e) {
			showNotification(READ_ERROR, NotificationVariant.LUMO_ERROR);
			e.printStackTrace();
		} catch (NotSupportedVariablityTypeException e) {
			showNotification(VAR_ERROR, NotificationVariant.LUMO_ERROR);
			e.printStackTrace();
		}
		return Model.NONE;
	}

	private Model parseDecisionModel(File file) {
		DecisionModelReader dmr = new DecisionModelReader();
		try {
			model = dmr.read(file.toPath());
			showNotification("Decision Model detected", NotificationVariant.LUMO_SUCCESS);
			return Model.DECISION;
		} catch (IOException e) {
			showNotification(READ_ERROR, NotificationVariant.LUMO_ERROR);
			e.printStackTrace();
		} catch (NotSupportedVariablityTypeException e) {
			showNotification(VAR_ERROR, NotificationVariant.LUMO_ERROR);
			e.printStackTrace();
		}
		return Model.NONE;
	}

	private Model parseUVLModel(String contents) {
		Object parseResult = UVLParser.parse(contents);
		if (parseResult instanceof UVLModel parsedModel) {
			model = parsedModel;
			showNotification("UVL Model detected", NotificationVariant.LUMO_SUCCESS);
			return Model.UVL;
		}
		return Model.NONE;
	}

	private Model parseOVMModel(File f) {
		OvModelReader ovReader = new OvModelReader();
		try {
			model = ovReader.read(f.toPath());
			showNotification("OVM Model detected", NotificationVariant.LUMO_SUCCESS);
			return Model.OVM;
		} catch (IOException e1) {
			showNotification(READ_ERROR, NotificationVariant.LUMO_ERROR);
			e1.printStackTrace();
		} catch (NotSupportedVariablityTypeException e1) {
			showNotification(VAR_ERROR, NotificationVariant.LUMO_ERROR);
			e1.printStackTrace();
		}
		return Model.NONE;
	}

	private File safeFile(InputStream fileData, String filename) {
		String contents = null;
		File f = null;
		try {
			contents = IOUtils.toString(fileData, StandardCharsets.UTF_8);
			f = new File(UPLOAD_FOLDER, filename);
			try (FileWriter fw = new FileWriter(f)) {
				fw.write(contents);
				fw.flush();
				f.setReadOnly();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return f;
	}

	private Optional<String> getExtensionByStringHandling(String filename) {
		return Optional.ofNullable(filename).filter(f -> f.contains("."))
				.map(f -> f.substring(filename.lastIndexOf(".") + 1));
	}

	private void createUploader(String... extensions) {
		singleFileUpload.setAcceptedFileTypes(extensions);
		singleFileUpload.addFileRejectedListener(event -> showNotification("Please only upload supported file formats.",
				NotificationVariant.LUMO_ERROR));
		singleFileUpload.addSucceededListener(event -> {
			// Get information about the uploaded file
			try (InputStream fileData = memoryBuffer.getInputStream()) {
				fileName = event.getFileName();
				addLoadingBar("Uploading...");
				File file = safeFile(fileData, fileName);
				file.deleteOnExit();
				Model modeltype = detectModel(file);
				addTypePicker(modeltype);
			} catch (IOException e1) {
				showNotification(READ_ERROR, NotificationVariant.LUMO_ERROR);
				e1.printStackTrace();
			}
			removeLoadingBar();
		});
		singleFileUpload.getElement().addEventListener("file-remove", e -> {
			typePicker.setEnabled(false);
			convertButton.setEnabled(false);
		});
	}

	private void addTypePicker(Model modelType) {
		typePicker.setItemsForSourceModel(modelType);
		typePicker.setVisible(true);
		typePicker.setEnabled(true);
		convertButton.setVisible(true);
	}

	private void removeLoadingBar() {
		remove(progressBarLabel, progressBar, progressBarSubLabel);
	}

	void addLoadingBar(String text) {
		progressBar.setIndeterminate(true);
		progressBarLabel.setText(text);
		progressBarSubLabel.getStyle().set("font-size", "var(--lumo-font-size-xs)");
		progressBarSubLabel.setText("Process can take a couple seconds for bigger models");
		add(progressBarLabel, progressBar, progressBarSubLabel);
	}

	public static void showNotification(String errorText, NotificationVariant theme) {
		Notification notification = new Notification();
		notification.addThemeVariants(theme);
		notification.setPosition(Position.BOTTOM_CENTER);
		notification.setDuration(5000);
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
