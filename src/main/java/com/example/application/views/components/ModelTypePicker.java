package com.example.application.views.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.example.application.data.Model;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.select.Select;

public class ModelTypePicker extends Div{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4834182718882462946L;
	private List<Model> items=new ArrayList<>();
	private Select<Model> select=new Select<>();

	public ModelTypePicker () {
		this(Model.NONE);
	}
	
	public ModelTypePicker(Model leaveOut) {
		items.addAll(Arrays.asList(Model.values()));
		select.setLabel("Convert to");
		items.removeIf(s->s.equals(leaveOut));
		if(leaveOut != Model.NONE) items.removeIf(s-> s.equals(Model.NONE));
		select.setItems(items);
		select.setItemLabelGenerator(Model::getLabel);
		add(select);
	}
	
	public List<Model> getItems(){
		return Collections.unmodifiableList(items);
	}
	
	public void setItems(List<Model> newItems) {
		this.items=newItems;
		select.setItems(newItems);
	}
}
