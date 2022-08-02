package com.example.application.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum Model {
	NONE(""),
	DECISION("Decision Model",".txt",".csv",".xls",".xlsx"),
	FEATURE("Feature Model",".csv"),
	OVM("OVM Model", ".txt"),
	UVL("UVL Model",".uvl",".txt");
	
	private static final Map<Model,ArrayList<String>> FILE_EXTENSIONS = new HashMap<>();
	
	static {
		for(Model m:values()) {
			ArrayList<String> list=new ArrayList<>();
			FILE_EXTENSIONS.put(m, list);
		}
	}
	
	public final String label;
	public final List<String> extensions;
	
	private Model(String label, String... extensions) {
        this.label = label;
        ArrayList<String> list= new ArrayList();
        list.addAll(Arrays.asList(extensions));
        this.extensions=list;
    }
	
	public static String getLabel(Model m) {
		return m.label;
	}
	
	public static String[] getAllFileExtensions() {
		List<String> l=new ArrayList<>();
		for(Model m:values()) {
			l.addAll(m.extensions);
		}
		return  l.stream().distinct().toList().toArray(size-> new String[size]);
	}
}
