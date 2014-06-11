package com.eduworks.russel.ui.client.model;

import com.eduworks.gwt.client.model.Record;

public class StatusRecord extends Record {
	public static final String STATUS_BUSY = "busy";
	public static final String STATUS_DONE = "done";
	public static final String STATUS_ERROR = "error";
	public static final String ALERT_SUCCESS = "success";
	public static final String ALERT_ERROR = "error";
	public static final String ALERT_WARNING = "warning";
	public static final String ALERT_BUSY = "";
	
	private String message = "";
	private String state = "";
	private Boolean rendered = false;
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public String getState() {
		return this.state;
	}
	
	public void setRendered(Boolean rendered) {
		this.rendered = rendered;
	}
	
	public Boolean getRendered() {
		return this.rendered;
	}
}
