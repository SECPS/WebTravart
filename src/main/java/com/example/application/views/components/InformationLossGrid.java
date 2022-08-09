package com.example.application.views.components;

import java.util.HashMap;
import java.util.Map;

import com.example.application.data.Model;
import com.example.application.data.Tuple;
import com.example.application.views.data.TransformationData;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class InformationLossGrid extends VerticalLayout {

	private static final long serialVersionUID = -95992439869670968L;

	Map<Tuple<Model, Model>, Grid<TransformationData>> gridList = new HashMap<>();
	
	public InformationLossGrid() {
		
	}

}
