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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.StreamResource;

public class DownloadLinksArea extends VerticalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6503066404936968106L;

	private final File uploadFolder;

	public DownloadLinksArea(File uploadFolder) {
		this.uploadFolder = uploadFolder;
		refreshFileLinks();
		setMargin(false);
		setPadding(false);
	}

	public void refreshFileLinks() {
		removeAll();
		add(new H4("Download Links:"));
		if (uploadFolder.listFiles()!=null) {
			for (File file : uploadFolder.listFiles()) {
				addLinkToFile(file);
			}
		}
	}

	private void addLinkToFile(File file) {
		StreamResource streamResource = new StreamResource(file.getName(), () -> getStream(file));
		Anchor link = new Anchor(streamResource,
				String.format("%s (%d KB)", file.getName(), (int) file.length() / 1024));
		link.getElement().setAttribute("download", true);

		add(link);
	}

	private InputStream getStream(File file) {
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return stream;
	}
}
