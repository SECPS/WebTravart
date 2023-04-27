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

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class Footer extends VerticalLayout {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4362064397098557576L;
	private final Span name=new Span("Johannes Kepler Universität Linz - LIT CPS Lab");
	private final Span street=new Span("Altenbergerstraße 69");
	private final Span plz= new Span("4040 Linz, Österreich");
	private final Anchor mail= new Anchor("mailto:secps@jku.at", "secps@jku.at");
	private final Anchor models=new Anchor("https://litcps.jku.at/vasics/travart/travart.zip","Example models");
	
	
	public Footer() {
	HorizontalLayout hor = new HorizontalLayout();
	VerticalLayout vert1 = new VerticalLayout();
	VerticalLayout vert2 = new VerticalLayout();
	hor.setPadding(false);
	setPadding(false);
	vert1.setSpacing(false);
	hor.add(vert1, vert2);

	VerticalLayout content = new VerticalLayout(name, street, plz, mail);
	content.setWidth("350px");
	content.setSpacing(false);
	content.setPadding(false);
	vert2.add(content);
	content.setJustifyContentMode(JustifyContentMode.END);

	Anchor cpsLink = new Anchor("https://www.jku.at/en/lit-cyber-physical-systems-lab/",
			"Cyber-Physical Systems Lab ");
	Anchor gitHubLink = new Anchor("https://github.com/SECPS/TraVarT", "GitHub");

	vert1.add(cpsLink, gitHubLink,models);

	add(hor);
	setAlignItems(Alignment.END);
	setJustifyContentMode(JustifyContentMode.END);
	addClassName("footer");
	}
}
