package com.example.application.views.components;

import com.example.application.data.Model;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.select.Select;

public class ModelTypePicker extends Div{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4834182718882462946L;

	public ModelTypePicker () {
		Select<Model> select= new Select<>();
		select.setLabel("Convert to");
		select.setItems(Model.values());
		select.setItemLabelGenerator(Model::getLabel);
		add(select);
	}
}
