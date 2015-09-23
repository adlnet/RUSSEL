package com.eduworks.russel.ui.client.model;

import com.eduworks.gwt.client.net.packet.ESBPacket;
import com.google.gwt.json.client.JSONObject;

public class RUSSELFileRecord extends FLRRecord {
	public static final String STRATEGY = "epssStrategy_s";
	
	private JSONObject strategy;

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
		if (esbPacket.containsKey(STRATEGY))
			strategy = esbPacket.getObject(STRATEGY).isObject()!=null?esbPacket.getObject(STRATEGY):new ESBPacket(esbPacket.getString(STRATEGY)).isObject();
	}
	
	@Override
	public String getFieldList() {
		return super.getFieldList() + " " + STRATEGY;
	}

	public JSONObject getStrategy() {
		return strategy;
	}

	public void setStrategy(JSONObject strategy) {
		this.strategy = strategy;
	}

	@Override
	public String toString() {
		return toObject().toString();
	}
	
	@Override
	public ESBPacket toObject() {
		ESBPacket esbPacket = super.toObject();
		esbPacket.put(STRATEGY, strategy);
		return esbPacket;
	}
}
