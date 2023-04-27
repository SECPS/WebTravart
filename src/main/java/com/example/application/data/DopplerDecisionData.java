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

public class DopplerDecisionData {
	public String id;
	public String question;
	public String type;
	public String range;
	public String cardinality;
	public String constraint;
	public String visibility;

	
	public DopplerDecisionData() {
	}
	
	public String getId() {
		return id;
	}
	public String getQuestion() {
		return question;
	}
	public String getType() {
		return type;
	}
	public String getRange() {
		return range;
	}
	public String getCardinality() {
		return cardinality;
	}
	public String getConstraint() {
		return constraint;
	}
	public String getVisibility() {
		return visibility;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setRange(String range) {
		this.range = range;
	}
	public void setCardinality(String cardinality) {
		this.cardinality = cardinality;
	}
	public void setConstraint(String constraint) {
		this.constraint = constraint;
	}
	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}
}