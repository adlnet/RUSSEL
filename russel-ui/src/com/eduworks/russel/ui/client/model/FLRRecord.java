package com.eduworks.russel.ui.client.model;

import com.eduworks.gwt.client.net.packet.ESBPacket;


public class FLRRecord extends FileRecord {

   public final static String FLR_DOC_ID = "flrDocId_s";
   public final static String FLR_PARADATA_ID = "flrParadataId_s";
   public final static String FLR_RESOURCE_LOCATOR = "flrResourceLocator_s";  

   private String flrDocId = "";
   private String flrParadataId = "";
   private String flrResourceLocator = "";

   public FLRRecord() {}

   public FLRRecord (ESBPacket esbPacket) {
	   parseESBPacket(esbPacket);
   }

    @Override
	public void parseESBPacket(ESBPacket parsePacket) {
		ESBPacket esbPacket;
		if (parsePacket.containsKey("obj"))
			esbPacket = new ESBPacket(parsePacket.get("obj").isObject());
		else
			esbPacket = parsePacket;
		super.parseESBPacket(esbPacket);
        if (parsePacket.containsKey(FLR_DOC_ID)) flrDocId = esbPacket.getString(FLR_DOC_ID);
        if (parsePacket.containsKey(FLR_PARADATA_ID)) flrParadataId = esbPacket.getString(FLR_PARADATA_ID);
        if (parsePacket.containsKey(FLR_RESOURCE_LOCATOR)) flrResourceLocator = esbPacket.getString(FLR_RESOURCE_LOCATOR);
		super.parseESBPacket(parsePacket);
	}
   
	@Override
	public String getFieldList() {
		return super.getFieldList() + " " + FLR_RESOURCE_LOCATOR + " " + FLR_DOC_ID + " " + FLR_PARADATA_ID;
	}
   
   public String getFlrDocId() {return flrDocId;}
   public void setFlrDocId(String flrDocId) {this.flrDocId = flrDocId;}
   
   public String getFlrParadataId() {return flrParadataId;}
   public void setFlrParadataId(String flrParadataId) {this.flrParadataId = flrParadataId;}

   public String getFlrResourceLocator() {return flrResourceLocator;}
   public void setFlrResourceLocator(String flrResourceLocator) {this.flrResourceLocator = flrResourceLocator;}
   
	@Override
	public String toString() {
		return toObject().toString();
	}
	
	@Override
	public ESBPacket toObject() {
		ESBPacket esbPacket = super.toObject();
		esbPacket.put(FLR_RESOURCE_LOCATOR, flrResourceLocator);
		esbPacket.put(FLR_DOC_ID, flrDocId);
		esbPacket.put(FLR_PARADATA_ID, flrParadataId);
		return esbPacket;
	}
}
