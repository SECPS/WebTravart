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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.application.data.Model;
import com.example.application.data.Tuple;
import com.example.application.views.data.TransformationData;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.HeaderRow.HeaderCell;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

@CssImport("styles/textClasses.css")
public class InformationLossGrid extends VerticalLayout {

	private static final long serialVersionUID = -95992439869670968L;

	private transient Map<Tuple<Model, Model>, Grid<TransformationData>> gridList = new HashMap<>();
	private transient Map<Tuple<Model, Model>, List<TransformationData>> gridData = new HashMap<>();
	private static final String ROTATE_TEXT = "rotateText";
	private Button clearButton= new Button(new Icon(VaadinIcon.TRASH));
	
	public InformationLossGrid() {
		clearButton.setVisible(false);
		clearButton.setEnabled(false);
		clearButton.addThemeVariants(ButtonVariant.LUMO_ICON);
		clearButton.getElement().setAttribute("aria-label", "remove tables");
		clearButton.addClickListener(e->{
			clearTables();
			clearButton.setEnabled(false);
		});
		add(clearButton);
	}
	
	private void clearTables() {
		gridList.clear();
		gridData.clear();
		this.removeAll();
		add(clearButton);
	}

	public void addTransformation(TransformationData data) {
		if(!clearButton.isVisible()|| !clearButton.isEnabled()) {
			clearButton.setVisible(true);
			clearButton.setEnabled(true);
		}
		if (gridData.get(data.getTransformType()) == null) {
			gridData.put(data.getTransformType(), new ArrayList<>());
			gridData.get(data.getTransformType()).add(data);
		} else {
			gridData.get(data.getTransformType()).add(data);
			gridList.get(data.getTransformType()).getDataProvider().refreshAll();
		}
		if (gridList.get(data.getTransformType()) == null) {
			Grid<TransformationData> newGrid = new Grid<>(TransformationData.class, false);
			newGrid.setAllRowsVisible(true);
			newGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
			newGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
			newGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
			newGrid.setItems(gridData.get(data.getTransformType()));
			Div headSourceVarCount = new Div();
			headSourceVarCount.add(data.getTransformType().getSource().varPointName);
			headSourceVarCount.addClassNames(ROTATE_TEXT);
			Div headSourceConstCount = new Div();
			headSourceConstCount.add(data.getTransformType().getSource().constraintName);
			headSourceConstCount.addClassNames(ROTATE_TEXT);
			Div headTargetVarCount = new Div();
			headTargetVarCount.add(data.getTransformType().getTarget().varPointName);
			headTargetVarCount.addClassNames(ROTATE_TEXT);
			Div headTargetConstCount = new Div();
			headTargetConstCount.add(data.getTransformType().getTarget().constraintName);
			headTargetConstCount.addClassNames(ROTATE_TEXT);
			Grid.Column<TransformationData> nameColumn = newGrid.addColumn(TransformationData::getName)
					.setHeader(Model.getLabel(data.getTransformType().getSource()) + "->"
							+ Model.getLabel(data.getTransformType().getTarget()))
					.setSortable(isAttached()).setResizable(true).setAutoWidth(isAttached());
			Grid.Column<TransformationData> sourceVarCountColumn = newGrid
					.addColumn(TransformationData::getSourceVarCount).setHeader(headSourceVarCount)
					.setTextAlign(ColumnTextAlign.CENTER).setSortable(isAttached()).setResizable(true)
					.setAutoWidth(isAttached());
			Grid.Column<TransformationData> sourceConstCountColumn = newGrid
					.addColumn(TransformationData::getSourceConstCount).setHeader(headSourceConstCount)
					.setTextAlign(ColumnTextAlign.CENTER).setSortable(isAttached()).setResizable(true)
					.setAutoWidth(isAttached());
			Grid.Column<TransformationData> targetVarCountColumn = newGrid
					.addColumn(TransformationData::getTargetVarCount).setHeader(headTargetVarCount)
					.setTextAlign(ColumnTextAlign.CENTER).setSortable(isAttached()).setResizable(true)
					.setAutoWidth(isAttached());
			Grid.Column<TransformationData> targetConstCountColumn = newGrid
					.addColumn(TransformationData::getTargetConstCount).setHeader(headTargetConstCount)
					.setTextAlign(ColumnTextAlign.CENTER).setSortable(isAttached()).setResizable(true)
					.setAutoWidth(isAttached());
			HeaderRow headerRow = newGrid.prependHeaderRow();
			HeaderCell head1 = headerRow.join(nameColumn, sourceVarCountColumn, sourceConstCountColumn);
			head1.setText("Original Model");
			if (data.getRtMetrics() == null) {
				headerRow.join(targetVarCountColumn, targetConstCountColumn).setText("Target Model");
			} else {
				headerRow.join(targetVarCountColumn, targetConstCountColumn).setText("Intermediate Model");
			}
			if(data.getRtMetrics()!=null) {
			Div headConfigLoss = new Div();
			headConfigLoss.add("ConfigLoss");
			headConfigLoss.addClassNames(ROTATE_TEXT);
			Div headSemLoss = new Div();
			headSemLoss.add("SemLoss");
			headSemLoss.addClassNames(ROTATE_TEXT);
			Div headStructLoss = new Div();
			headStructLoss.add("StructLoss");
			headStructLoss.addClassNames(ROTATE_TEXT);
			Div headRQuality = new Div();
			headRQuality.add("R-Quality");
			headRQuality.addClassNames(ROTATE_TEXT);
			Grid.Column<TransformationData> configLossColumn = newGrid
					.addColumn(e -> e.getRtMetrics() != null ? e.getRtMetrics().getConfigLoss() : "-")
					.setHeader(headConfigLoss).setTextAlign(ColumnTextAlign.CENTER).setSortable(isAttached())
					.setResizable(true).setAutoWidth(isAttached());
			Grid.Column<TransformationData> semLossColumn = newGrid
					.addColumn(e -> e.getRtMetrics() != null ? e.getRtMetrics().getSemLoss() : "-")
					.setHeader(headSemLoss).setTextAlign(ColumnTextAlign.CENTER).setSortable(isAttached())
					.setResizable(true).setAutoWidth(isAttached());
			Grid.Column<TransformationData> structLossColumn = newGrid
					.addColumn(e -> e.getRtMetrics() != null ? e.getRtMetrics().getStructLoss() : "-")
					.setHeader(headStructLoss).setTextAlign(ColumnTextAlign.CENTER).setSortable(isAttached())
					.setResizable(true).setAutoWidth(isAttached());
			Grid.Column<TransformationData> rQualityColumn = newGrid
					.addColumn(e -> e.getRtMetrics() != null ? e.getRtMetrics().getrQuality().label : "-")
					.setHeader(headRQuality).setTextAlign(ColumnTextAlign.CENTER).setSortable(isAttached())
					.setResizable(true).setAutoWidth(isAttached());
			headerRow.join(configLossColumn, semLossColumn, structLossColumn, rQualityColumn)
			.setText("Roundtrip Metrics");
			}
			gridList.put(data.getTransformType(), newGrid);
			add(newGrid);
		}
	}

}
