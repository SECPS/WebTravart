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

import at.jku.cps.travart.ovm.model.impl.OvModel;
import de.neominik.uvl.UVLParser;
import de.neominik.uvl.ast.ParseError;
import de.neominik.uvl.ast.UVLModel;
import de.ovgu.featureide.fm.core.base.impl.FeatureModel;

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
	private HorizontalLayout horizontal;
	private DownloadLinksArea downloads;
	
	private UVLModel uvlModel=null;
	private OvModel ovmModel=null;
	private FeatureModel fmModel=null;

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
		horizontal = new HorizontalLayout();
		horizontal.setWidthFull();
		typePicker = new ModelTypePicker();
		typePicker.setVisible(false);
		downloads = new DownloadLinksArea(new File("./" + VaadinSession.getCurrent().getPushId()));
		downloads.setVisible(false);
		createUploader(Model.getAllFileExtensions());
		horizontal.add(singleFileUpload);
		add(horizontal, downloads);
		setMargin(false);
	}

	private Model detectModel(File file) {
		Model m = null;
		String extension = getExtensionByStringHandling(file.getName()).get();
		String contents=null;
		try {
			contents = IOUtils.toString(new FileInputStream(file),StandardCharsets.UTF_8);
		} catch (IOException e2) {
			showNotification("Problem reading uploaded file",NotificationVariant.LUMO_ERROR);
		}
		if (Model.UVL.extensions.stream().anyMatch(e -> e.endsWith(extension))) {
			Object parseResult = UVLParser.parse(contents);
			if (parseResult instanceof UVLModel) {
				uvlModel = (UVLModel) parseResult;
				showNotification("UVL Model detected",NotificationVariant.LUMO_SUCCESS);
				return Model.UVL;
			}			
		}
		if (Model.DECISION.extensions.stream().anyMatch(e -> e.equals(extension))) {
			showNotification("Decision Model detected",NotificationVariant.LUMO_SUCCESS);
		}
		if (Model.FEATURE.extensions.stream().anyMatch(e -> e.equals(extension))) {
			showNotification("Feature Model detected",NotificationVariant.LUMO_SUCCESS);
		}
		if (Model.OVM.extensions.stream().anyMatch(e -> e.equals(extension))) {
			showNotification("OVM Model detected",NotificationVariant.LUMO_SUCCESS);
		}
		showNotification("Model Type not detected",NotificationVariant.LUMO_ERROR);
		return m;
	}

	private File safeFile(InputStream fileData, String filename) {
		String contents = null;
		File f = null;
		try {
			contents = IOUtils.toString(fileData, StandardCharsets.UTF_8);
			File uploadFolder = new File("./upload/" + VaadinSession.getCurrent().getPushId());
			uploadFolder.mkdirs();
			uploadFolder.deleteOnExit();
			f = new File(uploadFolder + filename);
			FileWriter fw = new FileWriter(f);
			fw.write(contents);
			fw.flush();
			fw.close();
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
		singleFileUpload.addFileRejectedListener(event -> {
			showNotification("Please only upload allowed file formats.",NotificationVariant.LUMO_ERROR);
		});
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
	}
	
	private void addTypePicker(Model modeltype) {
		typePicker=new ModelTypePicker(modeltype);
		horizontal.add(typePicker);
		typePicker.setVisible(true);
		//TODO continue here
		typePicker.addValueChangedListener(event2->{});
	}

	private void removeLoadingBar() {
		remove(progressBarLabel, progressBar);
	}

	void addLoadingBar() {
		progressBar.setIndeterminate(true);
		progressBarLabel.setText("Calculating stuff");
		add(progressBarLabel, progressBar);
	}

	public static void showNotification(String errorText,NotificationVariant theme) {
		Notification notification = new Notification();
		notification.addThemeVariants(theme);
		notification.setPosition(Position.BOTTOM_CENTER);
		notification.setDuration(5000);
		Div text = new Div(new Text(errorText));

		Button closeButton = new Button(new Icon("lumo", "cross"));
		closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		closeButton.getElement().setAttribute("aria-label", "Close");
		closeButton.addClickListener(event2 -> 	notification.close());

		HorizontalLayout layout = new HorizontalLayout(text, closeButton);
		layout.setAlignItems(Alignment.END);

		notification.add(layout);
		notification.open();
	}
	

}
