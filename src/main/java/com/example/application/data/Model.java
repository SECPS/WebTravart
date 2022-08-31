package com.example.application.data;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum Model {
	NONE("", "", ""), DECISION("Decision Model", "Decisions", "Rules", ".txt", ".csv", ".xls", ".xlsx"),
	FEATURE("Feature Model", "Features", "Constraints", ".xml", ".csv"),
	OVM("OVM Model", "VP & V", "Constraints", ".ovm", ".txt", ".xml"),
	PPRDSL("PPR DSL Model", "Products", "Constraints", ".txt", ".csv",".dsl"),
	UVL("UVL Model", "Features", "Constraints", ".uvl", ".txt");

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

	private Model(String label, String varName, String constName, String... extensions) {
		this.label = label;
		this.varPointName = varName;
		this.constraintName = constName;
		ArrayList<String> list = new ArrayList<>();
		list.addAll(Arrays.asList(extensions));
		this.extensions = list;
	}

	public static List<String> getExtensions(Model m) {
		return Collections.unmodifiableList(m.extensions);
	}

	public static String getLabel(Model m) {
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
