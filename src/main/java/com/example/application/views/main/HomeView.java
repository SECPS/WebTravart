package com.example.application.views.main;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

//@Route(value="home", layout=MainView.class)
@PageTitle("Travart Online | Home")

public class HomeView extends VerticalLayout {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2127362591074186330L;

	public HomeView() {
		H2 title= new H2("Travart Online");
		Div wrapper= new Div();
		Text text=new Text("Welcome to Travart Online! Transforming Variability Artifacts (TraVart) is a tool meant to enable users to transfer commonly used variability models into one another. This should help users to gain understanding between the limitations of each modeling approach, experiment with new approaches, or maybe change existing models into a more desirable format. The tool currently features support for FeatureIDE feature models (which are currently used as a pivot model), OVM models and DOPLER decision models. The software features conversion from each of those models into one another, including a full roundtrip which should maintain all possible configurations."
				+ "You can find the source code, and additional information to TravarT on ");
		Anchor gitHubLink = new Anchor("https://github.com/SECPS/TraVarT","GitHub");
		wrapper.add(text,gitHubLink,new Text("."));
		
		add(title,wrapper);
	}

}
