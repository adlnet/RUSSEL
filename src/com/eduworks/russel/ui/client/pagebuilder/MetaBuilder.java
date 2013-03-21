/*
Copyright 2012-2013 Eduworks Corporation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.eduworks.russel.ui.client.pagebuilder;

import com.eduworks.gwt.client.net.api.AlfrescoApi;
import com.eduworks.gwt.client.net.callback.AlfrescoCallback;
import com.eduworks.gwt.client.net.packet.AlfrescoPacket;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public class MetaBuilder {
	public static final String DETAIL_SCREEN = "detail";
	public static final String EDIT_SCREEN = "edit";
	
	private String metaType;
	
	public MetaBuilder (String mType) {
		this.metaType = mType;
	}
	
	private final native String doColors(String s) /*-{
		return $wnd.getSecurityColor(s);
	}-*/;

	private final native String putObjectives(String s, String id) /*-{
		return $wnd.listObjectives(s, id);
	}-*/;

	private final native String getObjectives(String id) /*-{
		return $wnd.compressObjectives(id);
	}-*/;

	private final native String putStrategies(String s, String id) /*-{
		return $wnd.listObjectives(s, id);
	}-*/;


	public void addMetaDataToField(String property, String id, AlfrescoPacket alfrescoPacket) {
		String fieldVal = "Click to edit";
		if (property.equalsIgnoreCase("cm:title") || property.equalsIgnoreCase("cmis:versionLabel") || property.equalsIgnoreCase("cmis:contentStreamLength") ||
				property.equalsIgnoreCase("cmis:contentStreamMimeType") || property.equalsIgnoreCase("cmis:contentStreamLength") || property.equalsIgnoreCase("russel:epssStrategy"))
			fieldVal = "N/A";

		if (!alfrescoPacket.getRusselValue(property).trim().equalsIgnoreCase(""))
			fieldVal = alfrescoPacket.getRusselValue(property);

		// If no values are assigned for Classification, Level, or Distribution, set them to the public default values
		if ((property == "russel:class") && (fieldVal == "Click to edit")) 
			fieldVal = "Unclassified";
		else if ((property == "russel:level") && (fieldVal == "Click to edit")) 
			fieldVal = "None";
		else if ((property == "russel:dist") && (fieldVal == "Click to edit")) 
			fieldVal = "Statement A (Public)";
		
		if ((metaType.equals(EDIT_SCREEN)&&(property=="russel:objective")) ||
		    (metaType.equals(DETAIL_SCREEN)&&(property=="russel:objective"))) {
			putObjectives(fieldVal, id);
		}
		else if (metaType.equals(EDIT_SCREEN)&&(property=="russel:class"||property=="russel:level"||property=="russel:dist")) {
			DOM.getElementById(id).setInnerHTML(doColors(fieldVal));
		}
		else if (metaType.equals(DETAIL_SCREEN)&&(property == "russel:FLRtag")) {
			DOM.getElementById(id).setInnerHTML("<a href='"+fieldVal+"' target='_blank'>"+fieldVal+"</a");
		}
		else {
			((Label)PageAssembler.elementToWidget(id, PageAssembler.LABEL)).setText(fieldVal);
		}
	}
	
	public void addMetaDataFields(AlfrescoPacket ap) {
		String ext = null;
		if ((ap != null)&&(!ap.toJSONString().equals("{}"))) {
			ext = ap.getFilename().substring(ap.getFilename().lastIndexOf(".")+1);
			if (metaType.equals(EDIT_SCREEN)) {
				addMetaDataToField("cm:title", "metaTitle", ap);
				addMetaDataToField("cm:description", "metaDescription", ap);
				addMetaDataToField("cmis:createdBy", "metaOwner", ap);
				addMetaDataToField("russel:publisher", "metaPublisher", ap);
				addMetaDataToField("russel:class", "metaClassification", ap);
				addMetaDataToField("russel:objective", "display-objective-list", ap);
				addMetaDataToField("russel:activity", "metaInteractivity", ap);
				addMetaDataToField("russel:env", "metaEnvironment", ap);
				addMetaDataToField("russel:coverage", "metaCoverage", ap);
				addMetaDataToField("russel:agerange", "metaSkill", ap);
				addMetaDataToField("russel:language", "metaLanguage", ap);
				addMetaDataToField("russel:duration", "metaDuration", ap);
				addMetaDataToField("russel:techreqs", "metaTechnicalRequirements", ap);
				addMetaDataToField("russel:dist", "metaDistribution", ap);
				addMetaDataToField("russel:level", "metaLevel", ap);
				addMetaDataToField("russel:partof", "metaPartOf", ap);
				addMetaDataToField("russel:requires", "metaRequires", ap);
				addMetaDataToField("cmis:contentStreamMimeType", "metaFormat", ap);
				addMetaDataToField("cmis:versionLabel", "metaVersion", ap);
				addMetaDataToField("cmis:contentStreamLength", "metaSize", ap);
			} else {
				addMetaDataToField("cm:title", "r-detailTitle", ap);
				addMetaDataToField("cm:title", "detailMetaTitle", ap);
				addMetaDataToField("cm:description", "detailMetaDescription", ap);
				addMetaDataToField("cmis:name", "detailMetaFilename", ap);
				addMetaDataToField("cmis:createdBy", "detailMetaOwner", ap);
				addMetaDataToField("russel:publisher", "detailMetaPublisher", ap);
				addMetaDataToField("russel:class", "detailMetaClassification", ap);
				addMetaDataToField("russel:objective", "detail-objective-list", ap);
				addMetaDataToField("russel:activity", "detailMetaInteractivity", ap);
				addMetaDataToField("russel:env", "detailMetaEnvironment", ap);
				addMetaDataToField("russel:coverage", "detailMetaCoverage", ap);
				addMetaDataToField("russel:agerange", "detailMetaSkill", ap);
				addMetaDataToField("russel:language", "detailMetaLanguage", ap);
				addMetaDataToField("russel:duration", "detailMetaDuration", ap);
				addMetaDataToField("russel:techreqs", "detailMetaTechnicalRequirements", ap);
				addMetaDataToField("russel:dist", "detailMetaDistribution", ap);
				addMetaDataToField("russel:level", "detailMetaLevel", ap);
				addMetaDataToField("russel:partof", "detailMetaPartOf", ap);
				addMetaDataToField("russel:requires", "detailMetaRequires", ap);
				addMetaDataToField("russel:epssStrategy", "detailEpssStrategies", ap);
				addMetaDataToField("cmis:contentStreamMimeType", "detailMetaFormat", ap);
				addMetaDataToField("cmis:versionLabel", "detailMetaVersion", ap);
				addMetaDataToField("cmis:contentStreamLength", "detailMetaSize", ap);
				if (ext.equalsIgnoreCase("rlr")) {
					addMetaDataToField("russel:FLRtag", "r-preview", ap);
				}
			}
		}
		String acc = "";
		for (int x=0;x<ap.getTags().length();x++)
			if (ap.getTags().get(x).trim()!="")
				acc += "," + ap.getTags().get(x).trim();
		if (acc!="") acc = acc.substring(1);
		else acc = "Click to edit";
		if (metaType.equals(EDIT_SCREEN))
			((Label)PageAssembler.elementToWidget("metaKeywords", PageAssembler.LABEL)).setText(acc);
		else
			((Label)PageAssembler.elementToWidget("detailMetaKeywords", PageAssembler.LABEL)).setText(acc);
	}
	
	public void saveMetadata(String nodeId, AlfrescoCallback<AlfrescoPacket> callback) {	
		String postString = buildMetaPacket();
		if (postString!=null&&nodeId!=null)
			AlfrescoApi.setObjectMetadata(nodeId, postString, callback);
	}

	public String buildMetaPacket() {
		AlfrescoPacket ap = AlfrescoPacket.makePacket();
		AlfrescoPacket container = AlfrescoPacket.makePacket();
		if (metaType.equals(DETAIL_SCREEN)) {
			addProperty("cm:title", "detailMetaTitle", ap);
			addProperty("cm:description", "detailMetaDescription", ap);
			addProperty("russel:publisher", "detailMetaPublisher", ap);
			addProperty("russel:class", "detailMetaClassification", ap);
			addObjectiveProperty(ap, "detail-objective-list");
			addProperty("russel:activity", "detailMetaInteractivity", ap);
			addProperty("russel:env", "detailMetaEnvironment", ap);
			addProperty("russel:coverage", "detailMetaCoverage", ap);
			addProperty("russel:agerange", "detailMetaSkill", ap);
			addProperty("russel:language", "detailMetaLanguage", ap);
			addProperty("russel:duration", "detailMetaDuration", ap);
			addProperty("russel:techreqs", "detailMetaTechnicalRequirements", ap);
			addProperty("russel:dist", "detailMetaDistribution", ap);
			addProperty("russel:level", "detailMetaLevel", ap);
			addProperty("russel:partof", "detailMetaPartOf", ap);
			addProperty("russel:requires", "detailMetaRequires", ap);
			addProperty("russel:epssStrategy", "detailEpssStrategies", ap);
			addProperty("tags", "detailMetaKeywords", container);
		} else {
			addProperty("cm:title", "metaTitle", ap);
			addProperty("cm:description", "metaDescription", ap);
			addProperty("russel:publisher", "metaPublisher", ap);
			addProperty("russel:class", "metaClassification", ap);
			addObjectiveProperty(ap, "display-objective-list");
			addProperty("russel:activity", "metaInteractivity", ap);
			addProperty("russel:env", "metaEnvironment", ap);
			addProperty("russel:coverage", "metaCoverage", ap);
			addProperty("russel:agerange", "metaSkill", ap);
			addProperty("russel:language", "metaLanguage", ap);
			addProperty("russel:duration", "metaDuration", ap);
			addProperty("russel:techreqs", "metaTechnicalRequirements", ap);
			addProperty("russel:dist", "metaDistribution", ap);
			addProperty("russel:level", "metaLevel", ap);
			addProperty("russel:partof", "metaPartOf", ap);
			addProperty("russel:requires", "metaRequires", ap);
			addProperty("tags", "metaKeywords", container);
		}
		if (!ap.toJSONString().equals("{}"))
			container.addKeyValue("properties", ap);
		if (container.toJSONString().equals("{}"))
			return null;
		return container.toJSONString();
	}
	
	private void addProperty(String property, String elementID, AlfrescoPacket ap) {
		String val = ((Label)PageAssembler.elementToWidget(elementID, PageAssembler.LABEL)).getText();
		if (val==null||val=="")
			val = ((TextBox)PageAssembler.elementToWidget(elementID, PageAssembler.TEXT)).getText();
		if (val==null)
			val = "Click to edit";
		
		if (val.trim().equalsIgnoreCase(".")) 
			if (property!="tags")
				ap.addKeyValue(property, "");
			else
				ap.addKeyArray(property, null);
		else if (!val.equalsIgnoreCase("Click to edit") && !val.equalsIgnoreCase("N/A"))
			if (property!="tags")
				ap.addKeyValue(property, val.replaceAll("\"", "'").replaceAll("[\r\n]", " ").trim());
			else
				ap.addKeyValue(property, val.split(","));
	}

	private AlfrescoPacket addObjectiveProperty(AlfrescoPacket ap, String elementID) {
		String val = getObjectives(elementID);
		
		if (val==null)
			val = "Click to edit";
		if (!val.equalsIgnoreCase("Click to edit") && !val.equalsIgnoreCase("N/A"))
			ap.addKeyValue("russel:objective", val.replaceAll("\"", "'").replaceAll("[\r\n]", " ").trim());
		
		return ap;
	}

	public static String convertToMetaPacket(AlfrescoPacket ap) {
		AlfrescoPacket container = AlfrescoPacket.makePacket();
		if (!ap.toJSONString().equals("{}"))
			container.addKeyValue("properties", ap);
		if (container.toJSONString().equals("{}"))
			return null;
		return container.toJSONString();
	}	 
	
}