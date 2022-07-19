package com.example.application.views.main;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value="impressum", layout=MainLayout.class)
@PageTitle("Travart Online | Impressum")
public class ImpressumView extends VerticalLayout {
	
	
	public ImpressumView(){
		H1 title = new H1("Impressum");
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
