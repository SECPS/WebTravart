package com.example.application.views.main;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value="impressum", layout=MainView.class)
@PageTitle("Travart Online | Impressum")
public class ImpressumView extends VerticalLayout {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3814094465013323203L;

	public ImpressumView(){
		H2 title = new H2("Impressum");
		UnorderedList ol= new UnorderedList();
		List<ListItem> listItemList = new ArrayList<>();
		listItemList.add(new ListItem("Johannes Kepler Universität Linz - LIT CPS Lab"));
		listItemList.add(new ListItem("Altenbergerstraße 69"));
		listItemList.add(new ListItem("4040 Linz, Österreich"));
		listItemList.add(new ListItem(new Anchor("mailto:secps@jku.at","secps@jku.at")));
		listItemList.stream().forEach(ol::add);
		add(title,ol);
	}

}
