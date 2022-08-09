package com.example.application.views.data;

import com.example.application.data.RQuality;

public class RoundTripMetrics {
	private int semLoss=0;
	private int configLoss=0;
	private int structLoss=0;
	private RQuality rQuality;
	
	public RoundTripMetrics(int configLoss, int semLoss, int structLoss, RQuality rq) {
		this.configLoss=configLoss;
		this.semLoss=semLoss;
		this.structLoss=structLoss;
		this.rQuality=rq;
	}
	public int getConfigLoss() {
		return configLoss;
	}
	public void setConfigLoss(int configLoss) {
		this.configLoss = configLoss;
	}
	public int getSemLoss() {
		return semLoss;
	}
	public void setSemLoss(int semLoss) {
		this.semLoss = semLoss;
	}
	public int getStructLoss() {
		return structLoss;
	}
	public void setStructLoss(int structLoss) {
		this.structLoss = structLoss;
	}
	public RQuality getrQuality() {
		return rQuality;
	}
	public void setrQuality(RQuality rQuality) {
		this.rQuality = rQuality;
	}
}
