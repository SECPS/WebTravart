package com.example.application.views.main;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value="home", layout=MainLayout.class)
@PageTitle("Travart Online | Home")
public class HomeView extends VerticalLayout {
	
	public HomeView() {
		H1 title= new H1("Travart Online");
		Paragraph text= new Paragraph("Welcome to Travart Online! Transforming Variability Artifacts (TraVart) is a tool meant to enable users to transfer commonly used variability models into one another. This should help users to gain understanding between the limitations of each modeling approach, experiment with new approaches, or maybe change existing models into a more desirable format. The tool currently features support for FeatureIDE feature models (which are currently used as a pivot model), OVM models and DOPLER decision models. The software features conversion from each of those models into one another, including a full roundtrip which should maintain all possible configurations.");
		add(title,text);
	}

}
