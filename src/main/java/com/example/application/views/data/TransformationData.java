package com.example.application.views.data;

import com.example.application.data.Model;
import com.example.application.data.Tuple;

public class TransformationData {
	
	private Tuple<Model,Model> transformType;
	private int sourceVarCount;
	private int targetVarCount;
	private int sourceConstCount;
	private int targetConstCount;
	private RoundTripMetrics rtMetrics=null;
	
	public TransformationData(Model source, Model target,RoundTripMetrics rtm) {
		this.rtMetrics=rtm;
		transformType=new Tuple<>(source,target);
	}
	
	public void setSourceVarCount(int count) {
		sourceVarCount=count;
	}
	
	public void setSourceConstCount(int count) {
		sourceConstCount=count;
	}
	
	public boolean isRoundTrip() {
		return rtMetrics!=null;
	}

	public int getSourceVarCount() {
		return sourceVarCount;
	}

	public int getTargetVarCount() {
		return targetVarCount;
	}

	public int getSourceConstCount() {
		return sourceConstCount;
	}

	public int getTargetConstCount() {
		return targetConstCount;
	}

	public void setTransformType(Tuple<Model, Model> transformType) {
		this.transformType = transformType;
	}

	public void setTargetVarCount(int count) {
		targetVarCount=count;
	}
	
	public void setTargetConstCount(int count) {
		targetConstCount=count;
	}
	
	public Tuple<Model,Model> getTransformType(){
		return transformType;
	}
	
	public String getSourceVarName() {
		return transformType.getSource().varPointName;
	}
	
	public String getSourceConstName() {
		return transformType.getSource().constraintName;
	}
	
	public String getTargetVarName() {
		return transformType.getTarget().varPointName;
	}
	
	public String getTargetConstName() {
		return transformType.getTarget().constraintName;
	}
}
