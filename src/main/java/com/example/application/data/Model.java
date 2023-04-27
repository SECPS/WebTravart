/*******************************************************************************
 * TODO: explanation what the class does
 *  
 * @author Kevin Feichtinger
 *  
 * Copyright 2023 Johannes Kepler University Linz
 * LIT Cyber-Physical Systems Lab
 * All rights reserved
 *******************************************************************************/
package com.example.application.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum Model {
	NONE("", "", ""), DECISION("Decision Model", "Decisions", "Rules", ".csv", ".xls", ".xlsx"),
	FEATURE("Feature Model", "Features", "Constraints", ".xml"), OVM("OVM Model", "VP & V", "Constraints", ".ovm"),
	PPRDSL("PPR DSL Model", "Products", "Constraints", ".dsl"), UVL("UVL Model", "Features", "Constraints", ".uvl");

	private static final Map<Model, ArrayList<String>> FILE_EXTENSIONS = new EnumMap<>(Model.class);

	static {
		for (Model m : values()) {
			ArrayList<String> list = new ArrayList<>();
			FILE_EXTENSIONS.put(m, list);
		}
	}

	private final String label;
	public final String varPointName;
	public final String constraintName;

	private final List<String> extensions;

	private Model(final String label, final String varName, final String constName, final String... extensions) {
		this.label = label;
		varPointName = varName;
		constraintName = constName;
		ArrayList<String> list = new ArrayList<>();
		list.addAll(Arrays.asList(extensions));
		this.extensions = list;
	}

	public static List<String> getExtensions(final Model m) {
		return Collections.unmodifiableList(m.extensions);
	}

	public static String getLabel(final Model m) {
		return m.label;
	}

	public static String[] getAllFileExtensions() {
		List<String> l = new ArrayList<>();
		for (Model m : values()) {
			l.addAll(m.extensions);
		}
		return l.stream().distinct().collect(Collectors.toList()).toArray(size -> new String[size]);
	}
}
