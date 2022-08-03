package com.example.application.views.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.apache.commons.io.IOUtils;

import com.example.application.data.Model;
import com.example.application.views.components.DownloadLinksArea;
import com.example.application.views.components.ModelTypePicker;
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
import com.vaadin.flow.server.VaadinSession;

import at.jku.cps.travart.core.common.exc.NotSupportedVariablityTypeException;
import at.jku.cps.travart.core.io.FeatureModelReader;
import at.jku.cps.travart.dopler.decision.IDecisionModel;
import at.jku.cps.travart.dopler.io.DecisionModelReader;
import at.jku.cps.travart.dopler.transformation.DecisionModeltoFeatureModelTransformer;
import at.jku.cps.travart.ovm.io.OvModelReader;
import at.jku.cps.travart.ovm.model.IOvModel;
import at.jku.cps.travart.ovm.transformation.OvModelToFeatureModelTransformer;
import de.neominik.uvl.UVLParser;
import de.neominik.uvl.ast.UVLModel;
import de.ovgu.featureide.fm.core.base.IFeatureModel;

@Route(value = "convert", layout = MainView.class)
@PageTitle("Travart Online | Converter")
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
	
	private static final String READ_ERROR="Problem reading uploaded file";
	
	
	private Object model=null;

	public ConvertView() {
		H2 title = new H2("Convert model file");
		StringBuilder sb = new StringBuilder();
		sb.append("Please only upload allowed file formats: ");
		for (String s : Model.getAllFileExtensions()) {
			sb.append(s);
			sb.append(" ");
		}
		Paragraph hint = new Paragraph(sb.toString());
		add(title, hint);
		initTypePicker();
		downloads = new DownloadLinksArea(new File("./" + VaadinSession.getCurrent().getPushId()));
		downloads.setVisible(false);
		createUploader(Model.getAllFileExtensions());
		initConvertButton();
		add(singleFileUpload,typePicker,convertButton,downloads);
		setMargin(false);
	}
	
	private void initTypePicker() {
		typePicker = new ModelTypePicker();
		typePicker.setVisible(false);
		typePicker.addValueChangedListener(event ->{
			if(event.getValue()!=null)
				convertButton.setEnabled(true);
		});
	}
	
	private void initConvertButton() {
		convertButton= new Button("Convert");
		convertButton.setEnabled(false);
		convertButton.setVisible(false);
		convertButton.addClickListener(event->{
			//TODO convert here
		});
	}

	private Object convertModelToTarget(Object model,Model targetModelType) throws NotSupportedVariablityTypeException {
		IFeatureModel toConvert=null;
		if(model instanceof IOvModel ovModel) {
			OvModelToFeatureModelTransformer trans= new OvModelToFeatureModelTransformer();
			toConvert=trans.transform(ovModel);
		}else if(model instanceof IDecisionModel decModel) {
			DecisionModeltoFeatureModelTransformer trans= new DecisionModeltoFeatureModelTransformer();
			toConvert=trans.transform(decModel);
		}else if(model instanceof UVLModel uvlModel) {
			
		}else if(model instanceof IFeatureModel) {
			toConvert=(IFeatureModel)model;
		}
		
		//TODO continue here
		return toConvert;
	}
	
	private Model detectModel(File file) {
		Model m = null;
		Optional<String> ext=getExtensionByStringHandling(file.getName());
		if(!ext.isPresent()) {
			return Model.NONE;
		}
		String extension = ext.get();
		String contents = null;
		try {
			contents = IOUtils.toString(new FileInputStream(file), StandardCharsets.UTF_8);
		} catch (IOException e2) {
			showNotification(READ_ERROR, NotificationVariant.LUMO_ERROR);
		}
		if (Model.getExtensions(Model.UVL).stream().anyMatch(e -> e.endsWith(extension))) {
			return parseUVLModel(contents);
		}
		if (Model.getExtensions(Model.DECISION).stream().anyMatch(e -> e.equals(extension))) {
			return parseDecisionModel(file);			
		}
		if (Model.getExtensions(Model.FEATURE).stream().anyMatch(e -> e.equals(extension))) {
			return parseFeatureModel(file);
		}
		if(Model.getExtensions(Model.OVM).stream().anyMatch(e -> e.equals(extension))) {
			return parseOVMModel(file);
		}
		showNotification("Model Type not detected", NotificationVariant.LUMO_ERROR);
		return m;
	}
	
	private Model parseFeatureModel(File file) {
		FeatureModelReader fmr=new FeatureModelReader();
		try {
			model=fmr.read(file.toPath());
			showNotification("Feature Model detected", NotificationVariant.LUMO_SUCCESS);
			return Model.FEATURE;
		} catch (IOException e) {
			showNotification(READ_ERROR, NotificationVariant.LUMO_ERROR);
			e.printStackTrace();
		} catch (NotSupportedVariablityTypeException e) {
			showNotification("There was an unsupported variability type in the feature model", NotificationVariant.LUMO_ERROR);
			e.printStackTrace();
		}
		return Model.NONE;
	}
	
	private Model parseDecisionModel(File file) {
		DecisionModelReader dmr= new DecisionModelReader();
		try {
			model=dmr.read(file.toPath());
			showNotification("Decision Model detected", NotificationVariant.LUMO_SUCCESS);
			return Model.DECISION;
		} catch (IOException e) {
			showNotification(READ_ERROR, NotificationVariant.LUMO_ERROR);
			e.printStackTrace();
		} catch (NotSupportedVariablityTypeException e) {
			showNotification("There was an unsupported variability type in the decision model", NotificationVariant.LUMO_ERROR);
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
		OvModelReader ovReader=new OvModelReader();
		try {
			model= ovReader.read(f.toPath());
			showNotification("OVM Model detected", NotificationVariant.LUMO_SUCCESS);
			return Model.OVM;
		} catch (IOException e1) {
			showNotification(READ_ERROR, NotificationVariant.LUMO_ERROR);
			e1.printStackTrace();
		} catch (NotSupportedVariablityTypeException e1) {
			showNotification("There was an unsupported variability type in the OVM model", NotificationVariant.LUMO_ERROR);
			e1.printStackTrace();
		}
		return Model.NONE;
	}

	private File safeFile(InputStream fileData, String filename) {
		String contents = null;
		File f = null;
		try {
			contents = IOUtils.toString(fileData, StandardCharsets.UTF_8);
			File uploadFolder = new File("./upload/" + VaadinSession.getCurrent().getPushId());
			uploadFolder.mkdirs();
			uploadFolder.deleteOnExit();			
			f = new File(uploadFolder, filename);
			f.deleteOnExit();
			try (FileWriter fw = new FileWriter(f)) {
				fw.write(contents);
				fw.flush();
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
		singleFileUpload.addFileRejectedListener(
				event -> showNotification("Please only upload allowed file formats.", NotificationVariant.LUMO_ERROR));
		singleFileUpload.addSucceededListener(event -> {
			// Get information about the uploaded file
			InputStream fileData = memoryBuffer.getInputStream();
			String fileName = event.getFileName();
			addLoadingBar();
			File file = safeFile(fileData, fileName);
			Model modeltype = detectModel(file);
			addTypePicker(modeltype);
			removeLoadingBar();
		});
		singleFileUpload.getElement().addEventListener("file-remove", e->{
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
		remove(progressBarLabel, progressBar);
	}

	void addLoadingBar() {
		progressBar.setIndeterminate(true);
		progressBarLabel.setText("Calculating stuff");
		add(progressBarLabel, progressBar);
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
