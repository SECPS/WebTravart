/*******************************************************************************
 * TODO: explanation what the class does
 *  
 * @author Kevin Feichtinger
 *  
 * Copyright 2023 Johannes Kepler University Linz
 * LIT Cyber-Physical Systems Lab
 * All rights reserved
 *******************************************************************************/
package com.example.application.views.data;

import com.example.application.data.Model;
import com.example.application.data.Tuple;

public class TransformationData {
	private String name;
	private Tuple<Model, Model> transformType;
	private long sourceVarCount;
	private long targetVarCount;
	private long sourceConstCount;
	private long targetConstCount;
	private RoundTripMetrics rtMetrics = null;

	public RoundTripMetrics getRtMetrics() {
		return rtMetrics;
	}

	public void setRtMetrics(RoundTripMetrics rtMetrics) {
		this.rtMetrics = rtMetrics;
	}

	public TransformationData(String name, Model source, Model target, long sourceVarCount, long sourceConstCount,
			long targetVarCount, long targetConstCount, RoundTripMetrics rtm) {
		this.name = name;
		this.rtMetrics = rtm;
		this.sourceVarCount = sourceVarCount;
		this.sourceConstCount = sourceConstCount;
		this.targetVarCount = targetVarCount;
		this.targetConstCount = targetConstCount;
		transformType = new Tuple<>(source, target);
	}
	
	public TransformationData(String name, Model source, Model target, long sourceVarCount, long sourceConstCount,
			long targetVarCount, long targetConstCount) {
		this(name,source,target,sourceVarCount,sourceConstCount,targetVarCount,targetConstCount,null);
	}

	public TransformationData() {
		// TODO Auto-generated constructor stub
	}

	public void setSourceVarCount(long count) {
		sourceVarCount = count;
	}

	public void setSourceConstCount(long count) {
		sourceConstCount = count;
	}

	public boolean isRoundTrip() {
		return rtMetrics != null;
	}

	public long getSourceVarCount() {
		return sourceVarCount;
	}

	public long getTargetVarCount() {
		return targetVarCount;
	}

	public long getSourceConstCount() {
		return sourceConstCount;
	}

	public long getTargetConstCount() {
		return targetConstCount;
	}

	public void setTransformType(Tuple<Model, Model> transformType) {
		this.transformType = transformType;
	}

	public void setTargetVarCount(long count) {
		targetVarCount = count;
	}

	public void setTargetConstCount(long count) {
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
	
	@Override
	public boolean equals(Object o) {
		if(o==null) {
			return false;
		}
		if(!(o instanceof TransformationData)) {
			return false;
		}
		if(hashCode()==o.hashCode()) {
			return true;
		}
		TransformationData other=(TransformationData)o;
		if(!name.equals(other.name)) return false;
//		if(!rtMetrics.equals(other.rtMetrics)) return false;
		if(sourceConstCount!=other.sourceConstCount)return false;
		if(sourceVarCount!=other.sourceVarCount)return false;
		if(targetConstCount!=other.targetConstCount)return false;
		if(targetVarCount!=other.targetVarCount)return false;
		if(!transformType.equals(other.transformType))return false;
		return true;		
	}
	
	@Override
	public int hashCode() {
		long hash=7;
		hash=31*hash+name.hashCode();
		hash=31*hash+sourceConstCount;
		hash=31*hash+sourceVarCount;
		hash=31*hash+targetConstCount;
		hash=31*hash+targetVarCount;
		hash=31*hash+transformType.hashCode();
		return (int)hash;
	}
}
