package com.example.application.views.data;

import com.example.application.data.Model;
import com.example.application.data.Tuple;

public class TransformationData {
	private String name;
	private Tuple<Model, Model> transformType;
	private int sourceVarCount;
	private int targetVarCount;
	private int sourceConstCount;
	private int targetConstCount;
	private RoundTripMetrics rtMetrics = null;

	public RoundTripMetrics getRtMetrics() {
		return rtMetrics;
	}

	public void setRtMetrics(RoundTripMetrics rtMetrics) {
		this.rtMetrics = rtMetrics;
	}

	public TransformationData(String name, Model source, Model target, int sourceVarCount, int sourceConstCount,
			int targetVarCount, int targetConstCount, RoundTripMetrics rtm) {
		this.name = name;
		this.rtMetrics = rtm;
		this.sourceVarCount = sourceVarCount;
		this.sourceConstCount = sourceConstCount;
		this.targetVarCount = targetVarCount;
		this.targetConstCount = targetConstCount;
		transformType = new Tuple<>(source, target);
	}

	public void setSourceVarCount(int count) {
		sourceVarCount = count;
	}

	public void setSourceConstCount(int count) {
		sourceConstCount = count;
	}

	public boolean isRoundTrip() {
		return rtMetrics != null;
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
		targetVarCount = count;
	}

	public void setTargetConstCount(int count) {
		targetConstCount = count;
	}

	public Tuple<Model, Model> getTransformType() {
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
