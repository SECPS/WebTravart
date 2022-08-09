package com.example.application.views.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.application.data.Model;
import com.example.application.data.Tuple;
import com.example.application.views.data.TransformationData;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class InformationLossGrid extends VerticalLayout {

	private static final long serialVersionUID = -95992439869670968L;

	private transient Map<Tuple<Model, Model>, Grid<TransformationData>> gridList = new HashMap<>();
	private transient Map<Tuple<Model, Model>, List<TransformationData>> gridData = new HashMap<>();

	public void addTransformation(Tuple<Model, Model> type, TransformationData data) {
		if (gridData.get(type) == null) {
			gridData.put(type, new ArrayList<>());
		} else {
			gridData.get(type).add(data);
			gridList.get(type).getDataProvider().refreshAll();
		}
		if (gridList.get(type) == null) {
			Grid<TransformationData> newGrid = new Grid<>(TransformationData.class,false);
			newGrid.setAllRowsVisible(true);
			newGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
			newGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
			newGrid.setItems(data);
			Grid.Column<TransformationData> nameColumn=newGrid.addColumn(TransformationData::getName).setHeader(Model.getLabel(type.getSource())+"->"+Model.getLabel(type.getTarget())).setResizable(true);
			Grid.Column<TransformationData> sourceVarCountColumn=newGrid.addColumn(TransformationData::getSourceVarCount).setHeader(type.getSource().varPointName).setResizable(true);
			Grid.Column<TransformationData> sourceConstCountColumn=newGrid.addColumn(TransformationData::getSourceConstCount).setHeader(type.getSource().constraintName).setResizable(true);
			Grid.Column<TransformationData> targetVarCountColumn=newGrid.addColumn(TransformationData::getTargetVarCount).setHeader(type.getTarget().varPointName).setResizable(true);
			Grid.Column<TransformationData> targetConstCountColumn=newGrid.addColumn(TransformationData::getTargetConstCount).setHeader(type.getTarget().constraintName).setResizable(true);
			Grid.Column<TransformationData> configLossColumn=newGrid.addColumn(e -> e.getRtMetrics()!= null ? e.getRtMetrics().getConfigLoss() : "-")
					.setHeader("ConfigLoss").setResizable(true);
			Grid.Column<TransformationData> semLossColumn=newGrid.addColumn(e -> e.getRtMetrics()!= null ? e.getRtMetrics().getSemLoss() : "-")
					.setHeader("SemLoss").setResizable(true);
			Grid.Column<TransformationData> structLossColumn=newGrid.addColumn(e -> e.getRtMetrics()!= null ? e.getRtMetrics().getStructLoss() : "-")
					.setHeader("StructLoss").setResizable(true);
			Grid.Column<TransformationData> rQualityColumn=newGrid.addColumn(e -> e.getRtMetrics()!= null? e.getRtMetrics().getrQuality().label:"-").setHeader("R-Quality").setResizable(true);
			HeaderRow headerRow = newGrid.prependHeaderRow();
			headerRow.join(nameColumn,sourceVarCountColumn,sourceConstCountColumn).setText("Original Model");
			if(data.getRtMetrics()==null) {
				headerRow.join(targetVarCountColumn,targetConstCountColumn).setText("Target Model");
			}else {
				headerRow.join(targetVarCountColumn,targetConstCountColumn).setText("Intermediate Model");
			}
			headerRow.join(configLossColumn,semLossColumn,structLossColumn,rQualityColumn).setText("Roundtrip Metrics");
			
			gridList.put(type, newGrid);
			add(newGrid);
		}
	}

}
