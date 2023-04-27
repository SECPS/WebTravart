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
