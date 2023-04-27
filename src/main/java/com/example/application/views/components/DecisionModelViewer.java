/*******************************************************************************
 * TODO: explanation what the class does
 *  
 * @author Kevin Feichtinger
 *  
 * Copyright 2023 Johannes Kepler University Linz
 * LIT Cyber-Physical Systems Lab
 * All rights reserved
 *******************************************************************************/
package com.example.application.views.components;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.example.application.data.DopplerDecisionData;
import com.example.application.views.main.ConvertView;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;

public class DecisionModelViewer extends VerticalLayout {

	/**
	 *
	 */
	private static final long serialVersionUID = -5344133026954898476L;
	private List<DopplerDecisionData> decisions = new ArrayList<>();
	private VerticalLayout content;
	private static final int MAX_COL = 6;
	private Workbook wb;

	public DecisionModelViewer(final InputStream fileData) {
		try {
			handleExcelSheet(fileData);
		} catch (IOException e) {
			ConvertView.showNotification("Error processing file", NotificationVariant.LUMO_ERROR);
			e.printStackTrace();
		}
	}

	private void handleExcelSheet(InputStream fileData) throws IOException {
		wb = new XSSFWorkbook(fileData);

		List<Tab> tabMap = new ArrayList<>();
		for (int i = 0; i < wb.getNumberOfSheets(); i++) {
			Sheet curr = wb.getSheetAt(i);
			for (int j = 1; j <= curr.getLastRowNum(); j++) {
				Row r = curr.getRow(j);
				DopplerDecisionData decision = new DopplerDecisionData();
				for (int k = 0; k <= MAX_COL; k++) {
					try {
						switch (k) {
						case 0:
							decision.setId(r.getCell(k).getStringCellValue());
							break;
						case 1:
							decision.setQuestion(r.getCell(k).getStringCellValue());
							break;
						case 2:
							decision.setType(r.getCell(k).getStringCellValue());
							break;
						case 3:
							decision.setRange(r.getCell(k).getStringCellValue());
							break;
						case 4:
							decision.setCardinality(r.getCell(k).getStringCellValue());
							break;
						case 5:
							decision.setConstraint(r.getCell(k).getStringCellValue());
							break;
						case 6:
							decision.setVisibility(r.getCell(k).getStringCellValue());
							break;
						}
					} catch (NullPointerException e) {
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
		tabs.addSelectedChangeListener(event -> setContent(event.getSelectedTab()));
		content = new VerticalLayout();
		content.setSpacing(false);
		setContent(tabs.getSelectedTab());
		add(tabs, content);

	}

	private void setContent(Tab selectedTab) {
		content.removeAll();
		Grid<DopplerDecisionData> currGrid = new Grid<>(DopplerDecisionData.class, false);
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
}
