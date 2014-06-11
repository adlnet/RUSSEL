package com.eduworks.russel.ui.client.model;

import com.eduworks.gwt.client.model.FLRRecord;
import com.eduworks.gwt.client.model.FileRecord;
import com.eduworks.gwt.client.net.packet.ESBPacket;

public class RUSSELFileRecord extends FLRRecord {
	public static final String NOTES = "notes_t";
	public static final String STRATEGY = "epssStrategy_t";
	public static final String USAGE_DELIMITER = "|";
	public static final String USAGE_STRATEGY_DELIMITER = "^";
	public static final String USAGE_COUNT_DELIMITER = "#";

	private String notes = "";
	private String strategy = "";

	public RUSSELFileRecord () {
		
	}
	
	public RUSSELFileRecord (ESBPacket esbPacket) {
		parseESBPacket(esbPacket);
	}
	
	@Override 
	public void parseESBPacket(ESBPacket metaDataPack) {
		ESBPacket esbPacket;
		if (metaDataPack.containsKey("obj"))
			esbPacket = new ESBPacket(metaDataPack.get("obj").isObject());
		else
			esbPacket = metaDataPack;
		super.parseESBPacket(esbPacket);
		if (esbPacket.containsKey(NOTES))
			notes = esbPacket.getString(NOTES);
		if (esbPacket.containsKey(STRATEGY))
			strategy = esbPacket.getString(STRATEGY);
	}
	
	@Override
	public String getFieldList() {
		return super.getFieldList() + " " + NOTES + " " + STRATEGY;
	}
	
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getStrategy() {
		return strategy;
	}

	public void setStrategy(String strategy) {
		this.strategy = strategy;
	}

	@Override
	public String toString() {
		ESBPacket esbPacket = super.toObject();
		esbPacket.put(NOTES, notes);
		esbPacket.put(STRATEGY, strategy);
		return esbPacket.toString();
	}
	
	@Override
	public ESBPacket toObject() {
		ESBPacket esbPacket = super.toObject();
		esbPacket.put(NOTES, notes);
		esbPacket.put(STRATEGY, strategy);
		return esbPacket;
	}
}
