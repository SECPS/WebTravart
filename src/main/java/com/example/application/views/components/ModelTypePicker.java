package com.example.application.views.components;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.application.data.Model;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.shared.Registration;

public class ModelTypePicker extends Div{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4834182718882462946L;
	private List<Model> items=new ArrayList<>();
	private Select<Model> select=new Select<>();
	private static final String ROUNDTRIP=" (roundtrip)";
	private Model chosenModel=Model.NONE;

	public ModelTypePicker () {
		this(Model.NONE);
	}
	
	public ModelTypePicker(Model source) {
		chosenModel=source;
		select.setLabel("Convert to");
		items.addAll(Arrays.asList(Model.values()));
		if(chosenModel != Model.NONE) items.removeIf(s-> s.equals(Model.NONE));
		select.setItems(items);
		select.setPlaceholder("Target approach");
		select.setItemLabelGenerator(new ItemLabelGenerator<Model>() {
			private static final long serialVersionUID = -217514886549138284L;
			@Override
			public String apply(Model item) {
					String lab=Model.getLabel(item);
					if( item!=Model.FEATURE && lab.equals(Model.getLabel(chosenModel))) {
						lab+=ROUNDTRIP;
					}
				return lab;
			}});
		add(select);
		
	}
	
	public List<Model> getItems(){
		return Collections.unmodifiableList(items);
	}
	
	public void setItemsForSourceModel(Model source) {
		chosenModel=source;
		items.clear();
		items.addAll(Arrays.asList(Model.values()));
		items.removeIf(s->s.equals(Model.NONE));
		select.setItems(items);
	}
	
	public void setItems(List<Model> newItems) {
		this.items=newItems;
		select.setItems(newItems);
	}
	
	public Registration addValueChangedListener(ValueChangeListener<? super ComponentValueChangeEvent<Select<Model>, Model>> listener) {
		return select.addValueChangeListener(listener);
	}
	
	public Model getSelection() {
		return select.getValue();
	}
	
}
