package com.eduworks.russel.ui.client.model;

import com.eduworks.gwt.client.model.Record;
import com.eduworks.gwt.client.net.packet.ESBPacket;
import com.google.gwt.core.client.JsDate;

public class CommentRecord extends Record {
	public final static String COMMENT = "text";
	public final static String CREATE_DATE = "createDate";
	public final static String CREATED_BY = "createdBy";
	
	private String comment = "";
	private JsDate createDate = null;
	private String createdBy = "";
	
	public CommentRecord() {}
	
	public CommentRecord(ESBPacket commentRecord) {
		parseESBPacket(commentRecord);
	}
	
	public void parseESBPacket(ESBPacket metaDataPack) {
		ESBPacket esbPacket;
		if (metaDataPack.containsKey("obj"))
			esbPacket = new ESBPacket(metaDataPack.get("obj").isObject());
		else
			esbPacket = metaDataPack;
		if (esbPacket.containsKey(COMMENT))
			comment = esbPacket.getString(COMMENT);
		if (esbPacket.containsKey(CREATE_DATE))
			createDate = JsDate.create(JsDate.parse(esbPacket.getString(CREATE_DATE)));
		if (esbPacket.containsKey(CREATED_BY))
			createdBy = esbPacket.getString(CREATED_BY);
		if (esbPacket.containsKey(ID))
			this.setGuid(esbPacket.getString(ID));
	}
	
	public String getComment() {
		return comment;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public JsDate getCreateDate() {
		return createDate;
	}

	public void setCreateDate(JsDate createDate) {
		this.createDate = createDate;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
}
