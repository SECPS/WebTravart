package com.example.application.views.main;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.example.application.data.DopplerDecisionData;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.neominik.uvl.UVLParser;
import de.neominik.uvl.ast.UVLModel;

@Route(value="upload", layout= MainLayout.class)
@PageTitle("Travart Online | UVL Upload")
public class UvlUpload extends VerticalLayout {
	
	private static final int MAX_COL = 6;
	private MemoryBuffer memoryBuffer = new MemoryBuffer();
	private Upload singleFileUpload = new Upload(memoryBuffer);
	private ProgressBar progressBar = new ProgressBar();
	private Div progressBarLabel = new Div();
	private VerticalLayout content;
	private Workbook wb;
	private List<DopplerDecisionData> decisions=new ArrayList<>();
	

	public UvlUpload() {
		H1 title = new H1("Upload UVL model file");
		Paragraph hint = new Paragraph("Only one .uvl or .txt file is allowed for upload.");
		singleFileUpload.setAcceptedFileTypes(".uvl", ".txt", ".xls", ".xlsx",".csv");
		singleFileUpload.addFileRejectedListener(event -> {
			throwError("Please only upload allowed file formats.");
		});
		singleFileUpload.addSucceededListener(event -> {
			// Get information about the uploaded file
			InputStream fileData = memoryBuffer.getInputStream();
			String fileName = event.getFileName();
			long contentLength = event.getContentLength();
			String mimeType = event.getMIMEType();
			addLoadingBar();
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			removeLoadingBar();
			// Do something with the file data
			if (fileName.endsWith(".uvl") || fileName.endsWith(".txt")) {
				processFile(fileData, fileName, contentLength, mimeType);
			} else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")|| fileName.endsWith(".csv")) {
				try {
					handleExcelSheet(fileData);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			printSuccess();
		});

		setMargin(false);
		add(title, hint, singleFileUpload);
	}

	private void handleExcelSheet(InputStream fileData) throws IOException {
		wb = new XSSFWorkbook(fileData);

		List<Tab> tabMap = new ArrayList<>();
		for (int i = 0; i < wb.getNumberOfSheets(); i++) {
			Sheet curr = wb.getSheetAt(i);
			for(int j=1;j<=curr.getLastRowNum();j++) {
				Row r =curr.getRow(j);
				DopplerDecisionData decision=new DopplerDecisionData();
				for(int k=0;k<=MAX_COL;k++) {
					try {
					switch(k) {
					case 0: decision.setId(r.getCell(k).getStringCellValue()); break;
					case 1: decision.setQuestion(r.getCell(k).getStringCellValue());break;
					case 2: decision.setType(r.getCell(k).getStringCellValue());break;
					case 3: decision.setRange(r.getCell(k).getStringCellValue()); break;
					case 4: decision.setCardinality(r.getCell(k).getStringCellValue()); break;
					case 5: decision.setConstraint(r.getCell(k).getStringCellValue());break;
					case 6: decision.setVisibility(r.getCell(k).getStringCellValue()); break;
					}
					}catch(NullPointerException e) {
//						throwError("Error occured in Row " +j+" and Column "+k);
					}
				}
				decisions.add(decision);
			}
			Tab currTab = new Tab(curr.getSheetName());
			tabMap.add(currTab);
		}

		Tabs tabs = new Tabs();
		for (Tab t : tabMap) {
			tabs.add(t);
		}
		tabs.addSelectedChangeListener(event-> setContent(event.getSelectedTab()));
		content = new VerticalLayout();
		content.setSpacing(false);
		setContent(tabs.getSelectedTab());
		add(tabs,content);
	}

	private void setContent(Tab selectedTab) {
		content.removeAll();
		Grid<DopplerDecisionData> currGrid=new Grid<>(DopplerDecisionData.class,false);
		currGrid.addColumn(DopplerDecisionData::getId).setHeader("ID");
		currGrid.addColumn(DopplerDecisionData::getQuestion).setHeader("Question").setResizable(true);
		currGrid.addColumn(DopplerDecisionData::getType).setHeader("Type");
		currGrid.addColumn(DopplerDecisionData::getRange).setHeader("Range").setResizable(true);
		currGrid.addColumn(DopplerDecisionData::getCardinality).setHeader("Cardinality");
		currGrid.addColumn(DopplerDecisionData::getConstraint).setHeader("Constraint").setResizable(true);
		currGrid.addColumn(DopplerDecisionData::getVisibility).setHeader("Visibility");
		currGrid.setItems(decisions);
		content.add(currGrid);
		
	}

	private void printSuccess() {
		H4 bonusHeader = new H4("Wow, what a great submission!");
		add(bonusHeader);
	}

	private void removeLoadingBar() {
		remove(progressBarLabel, progressBar);

	}

	void processFile(InputStream fileData, String fileName, long contentLength, String mimeType) {

		String contents = null;
		try {
			contents = IOUtils.toString(fileData, StandardCharsets.UTF_8);
			System.out.println(contents);
		} catch (IOException e) {
			e.printStackTrace();
		}
		UVLParser parser = new UVLParser();
		try {
			UVLModel model = (UVLModel) parser.parse(contents);
			TextArea textArea = new TextArea();
			textArea.setReadOnly(true);
			textArea.setWidthFull();
			textArea.setLabel("UVL Model");
			textArea.setValue(model.toString());
			add(textArea);
		} catch (Throwable e) {
			throwError("Could not successfully read UVL model");
		}

	}

	void addLoadingBar() {
		progressBar.setIndeterminate(true);
		progressBarLabel.setText("Calculating stuff");
		add(progressBarLabel, progressBar);
	}

	void throwError(String errorText) {
		Notification notification = new Notification();
		notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

		Div text = new Div(new Text(errorText));

		Button closeButton = new Button(new Icon("lumo", "cross"));
		closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		closeButton.getElement().setAttribute("aria-label", "Close");
		closeButton.addClickListener(event2 -> {
			notification.close();
		});

		HorizontalLayout layout = new HorizontalLayout(text, closeButton);
		layout.setAlignItems(Alignment.CENTER);

		notification.add(layout);
		notification.open();
	}

}
