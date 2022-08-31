package com.example.application.data;

public enum RQuality {
	NONE("-"),
	EQUAL("equal"),
	IDENTICAL("identical"),
	LOSS("loss");

	public final String label;
	
	RQuality(String string) {
		label=string;
	}
}
