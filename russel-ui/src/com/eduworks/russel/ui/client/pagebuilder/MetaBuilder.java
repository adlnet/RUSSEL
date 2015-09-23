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

import java.util.Set;

import com.eduworks.gwt.client.net.callback.ESBCallback;
import com.eduworks.gwt.client.net.packet.ESBPacket;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.russel.ui.client.model.FileRecord;
import com.eduworks.russel.ui.client.model.RUSSELFileRecord;
import com.eduworks.russel.ui.client.net.RusselApi;
import com.google.gwt.dom.client.Element;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

/**
 * MetaBuilder class
 * Defines globals and methods for the metadata.
 * 
 * @author Eduworks Corporation
 */
public class MetaBuilder {
	public static final String DETAIL_SCREEN = "detail";
	public static final String EDIT_SCREEN = "edit";
	
	private String metaType;
	
	/**
	 * MetaBuilder Constructor for the class
	 * @param mType String Type of metadata display (either detail or edit screen)
	 */
	public MetaBuilder (String mType) {
		this.metaType = mType;
	}
	
	/**
	 * doColors Calls the javascript function getSecurityColor to assign coloring scheme to Classification, Level, and Distribution fields
	 * @param s String
	 * @return String
	 */
	private final native String doColors0(String s) /*-{
		return $wnd.getSecurityColor(s);
	}-*/;

	/**
	 * putObjectives Calls the javascript function listObjectives to display the objectives in the metadata screen
	 * @param s String Screen type
	 * @param id String 
	 * @return String
	 */
	private final native String putObjectives0(JSONArray s, String id) /*-{
		return $wnd.listObjectives(s, id);
	}-*/;

	/**
	 * getObjectives Calls the javascript function compressObjectives to retrieve and save the objective metadata edits
	 * @param id String
	 * @return String
	 */
	private final native JSONArray getObjectives0(String id, JSONArray ja) /*-{
		return $wnd.compressObjectives(id, ja);
	}-*/;

	/**
	 * addProperty Extracts a specific field value from the metadata screen and prepares for save to node
	 * @param property String Name of metadata property
	 * @param elementID String id of metadata field
	 * @param ap ESBPacket to be updated with new value 
	 */
	private void addProperty0(String property, String elementID, ESBPacket ap) {
		String val = ((Label)PageAssembler.elementToWidget(elementID, PageAssembler.LABEL)).getText();
		if (val==null||val=="")
			val = ((TextBox)PageAssembler.elementToWidget(elementID, PageAssembler.TEXT)).getText();
		if (val==null)
			val = "Click to edit";
		
		if (val.trim().equalsIgnoreCase(".")) 
			if (property!="tags")
				ap.put(property, "");
			else {
				JSONArray ja = new JSONArray();
				ja.set(0, null);
				ap.put(property, ja);
			}
		else if (!val.equalsIgnoreCase("Click to edit") && !val.equalsIgnoreCase("N/A"))
			if (property!="tags")
				ap.put(property, val.replaceAll("\"", "'").replaceAll("[\r\n]", " ").trim());
			else
				ap.put(property, val.split(","));
	}

	/**
	 * addObjectiveProperty Extracts the list of objectives from the metadata screen and prepares for save to node
	 * @param ap ESBPacket to be updated with new objectives list
	 * @param elementID String id of objectives field
	 * @return ESBPacket
	 */
	private ESBPacket addObjectiveProperty0(ESBPacket ap, String elementID) {
		JSONArray val = getObjectives0(elementID, new JSONArray());
		
		if (val.size()>0)
			ap.put(RUSSELFileRecord.OBJECTIVES, val);
		
		return ap;
	}

	/**
	 * addMetadataToField Formats and places the appropriate value into the designated form field in the metadata screen indicated.
	 * @param id String Name of element in node packet
	 * @param clickToEdit
	 * @param alfrescoPacket ESBPacket information for the node
	 */
	public void addMetaDataToField(String id, String fieldVal, Boolean clickToEdit) {
		if (fieldVal=="") {
			fieldVal = "Click to edit";
			if (!clickToEdit)
				fieldVal = "N/A";
		}
		//TODO fix FLR metadata
//		if (metaType.equals(DETAIL_SCREEN)&&(property == "russel:FLRtag")) {
//			DOM.getElementById(id).setInnerHTML("<a href='"+fieldVal+"' target='_blank'>"+fieldVal+"</a");
//		}
		((Label)PageAssembler.elementToWidget(id, PageAssembler.LABEL)).setText(fieldVal);
	}
	
//	private void addEpssStrategyField(String id, String fieldVal) {
//		ProjectRecord.renderIsdUsage(fieldVal, id);
//	}
	
	private void addColoredMetadataToField(String elementId, FileRecord fr, String type) {
		String fieldVal = "";
				
		// If no values are assigned for Classification, Level, or Distribution, set them to the public default values
		if (type.equalsIgnoreCase("classification")) { 
			if (fr.getClassification() == "") 
				fieldVal = "Unclassified";
			else
				fieldVal = fr.getClassification();
		} else if (type.equalsIgnoreCase("level")) { 
			if (fr.getLevel() == "") 
				fieldVal = "None";
			else
				fieldVal = fr.getLevel();
		} else if (type.equalsIgnoreCase("distribution")) { 
			if (fr.getDistribution() == "") 
				fieldVal = "Statement A (Public)";
			else
				fieldVal = fr.getDistribution();
		}
		
		if (metaType.equals(EDIT_SCREEN)) {
			DOM.getElementById(elementId).setInnerHTML(doColors0(fieldVal));
		} else
			((Label)PageAssembler.elementToWidget(elementId, PageAssembler.LABEL)).setText(fieldVal);
	}
	
	private void addObjectiveToField0(String elementId, FileRecord fr) {
		putObjectives0(fr.getObjectives(), elementId);
	}
	
	/**
	 * addMetaDataFields Allocates all node information to the appropriate metadata screen fields
	 * @param ap ESBPacket information for the node
	 */
	public void addMetaDataFields(RUSSELFileRecord ap) {
		String ext = null;
		if (ap != null) {
			ext = ap.getFilename().substring(ap.getFilename().lastIndexOf(".")+1);
			if (metaType.equals(EDIT_SCREEN)) {
				addMetaDataToField("metaTitle", ap.getTitle(), false);
				addMetaDataToField("metaDescription", ap.getDescription(), true);
				addMetaDataToField("metaOwner", ap.getOwner(), true);
				addMetaDataToField("metaPublisher", ap.getPublisher(), true);
				addColoredMetadataToField("metaClassification", ap, "classification");
				addObjectiveToField0("display-objective-list", ap);
				addMetaDataToField("metaInteractivity", ap.getInteractivity(), true);
				addMetaDataToField("metaEnvironment", ap.getEnvironment(), true);
				addMetaDataToField("metaCoverage", ap.getCoverage(), true);
				addMetaDataToField("metaSkill", ap.getSkill(), true);
				addMetaDataToField("metaLanguage", ap.getLanguage(), true);
				addMetaDataToField("metaDuration", Double.toString(ap.getDuration()), true);
				addMetaDataToField("metaTechnicalRequirements", ap.getTechnicalRequirements(), true);
				addColoredMetadataToField("metaDistribution", ap, "distribution");
				addColoredMetadataToField("metaLevel", ap, "level");
				addMetaDataToField("metaPartOf", ap.getPartOf(), true);
				addMetaDataToField("metaRequires", ap.getRequires(), true);
				addMetaDataToField("metaFormat", ap.getMimeType(), false);
				addMetaDataToField("metaVersion", ap.getVersion(), false);
				addMetaDataToField("metaSize", Integer.toString(ap.getFilesize()), false);
				addMetaDataToField("metaKeywords", ap.getKeywords(), true);
			} else {
				addMetaDataToField("r-detailTitle", ap.getTitle(), false);
				addMetaDataToField("detailMetaTitle", ap.getTitle(), true);
				addMetaDataToField("detailMetaDescription", ap.getDescription(), true);
				addMetaDataToField("detailMetaFilename", ap.getFilename(), true);
				addMetaDataToField("detailMetaOwner", ap.getOwner(), true);
				addMetaDataToField("detailMetaPublisher", ap.getPublisher(), true);
				addColoredMetadataToField("detailMetaClassification", ap, "classification");
				addObjectiveToField0("detail-objective-list", ap);
				addMetaDataToField("detailMetaInteractivity", ap.getInteractivity(), true);
				addMetaDataToField("detailMetaEnvironment", ap.getEnvironment(), true);
				addMetaDataToField("detailMetaCoverage", ap.getCoverage(), true);
				addMetaDataToField("detailMetaSkill", ap.getSkill(), true);
				addMetaDataToField("detailMetaLanguage", ap.getLanguage(), true);
				addMetaDataToField("detailMetaDuration", Double.toString(ap.getDuration()), true);
				addMetaDataToField("detailMetaTechnicalRequirements", ap.getTechnicalRequirements(), true);
				addColoredMetadataToField("detailMetaDistribution", ap, "distribution");
				addColoredMetadataToField("detailMetaLevel", ap, "level");
				addMetaDataToField("detailMetaPartOf", ap.getPartOf(), true);
				addMetaDataToField("detailMetaRequires", ap.getRequires(), true);
				addMetaDataToField("detailMetaFLRLink", ap.getFlrDocId(), false);
				addMetaDataToField("detailMetaFLRParadataLink", ap.getFlrParadataId(), false);

				addEpssStrategyField("detailEpssStrategies", ap.getStrategy());
				addMetaDataToField("detailMetaFormat", ap.getMimeType(), false);
				addMetaDataToField("detailMetaVersion", ap.getVersion(), false);
				addMetaDataToField("detailMetaSize", Integer.toString(ap.getFilesize()), false);
				if (ext.equalsIgnoreCase("rlr")) {
					addMetaDataToField("r-preview", ap.getFileContents(), true);
				}
				addMetaDataToField("detailMetaKeywords", ap.getKeywords(), true);
			}
		}
	}
	
	private void addEpssStrategyField(String elementName, JSONObject epssTags) {
		StringBuilder sb = new StringBuilder();
		Set<String> keys = epssTags.keySet();
		if (keys.size()!=0) {
			boolean f = true;
			for (String key : keys) {
				if (f) {
					f = false;
					sb.append(key);
				} else
					sb.append("<br />" + key);
			}
			DOM.getElementById(elementName).setInnerHTML(sb.toString());
		} else 
			DOM.getElementById(elementName).setInnerText("N/A");
	}
	
	/**
	 * saveMetadata Collects edited metadata and saves it to the node in Alfresco
	 * @param nodeId String Alfresco node id
	 * @param callback AlfrescoCallback<ESBPacket>
	 */
	public void saveMetadata(RUSSELFileRecord record, ESBCallback<ESBPacket> callback) {	
		RUSSELFileRecord postString = buildMetaPacket(record);
		if (record.getGuid()!="")
			RusselApi.updateResourceMetadata(record.getGuid(), postString.toObject(), callback);
	}

	/**
	 * buildMetaPacket Creates a new packet and adds the current field values for each metadata field on the screen for the node.
	 * @return String node packet in JSON format
	 */
	public RUSSELFileRecord buildMetaPacket(RUSSELFileRecord record) {
		ESBPacket ap = new ESBPacket();
		if (metaType.equals(DETAIL_SCREEN)) {
			addProperty0(RUSSELFileRecord.TITLE, "detailMetaTitle", ap);
			addProperty0(RUSSELFileRecord.DESCRIPTION, "detailMetaDescription", ap);
			addProperty0(RUSSELFileRecord.PUBLISHER, "detailMetaPublisher", ap);
			addProperty0(RUSSELFileRecord.CLASSIFICATION, "detailMetaClassification", ap);
			addObjectiveProperty0(ap, "detail-objective-list");
			addProperty0(RUSSELFileRecord.INTERACTIVITY, "detailMetaInteractivity", ap);
			addProperty0(RUSSELFileRecord.ENVIRONMENT, "detailMetaEnvironment", ap);
			addProperty0(RUSSELFileRecord.COVERAGE, "detailMetaCoverage", ap);
			addProperty0(RUSSELFileRecord.SKILL, "detailMetaSkill", ap);
			addProperty0(RUSSELFileRecord.LANGUAGE, "detailMetaLanguage", ap);
			addProperty0(RUSSELFileRecord.DURATION, "detailMetaDuration", ap);
			addProperty0(RUSSELFileRecord.TECHNICAL_REQUIREMENTS, "detailMetaTechnicalRequirements", ap);
			addProperty0(RUSSELFileRecord.DISTRIBUTION, "detailMetaDistribution", ap);
			addProperty0(RUSSELFileRecord.LEVEL, "detailMetaLevel", ap);
			addProperty0(RUSSELFileRecord.PART_OF, "detailMetaPartOf", ap);
			addProperty0(RUSSELFileRecord.REQUIRES, "detailMetaRequires", ap);
			addProperty0(RUSSELFileRecord.STRATEGY, "detailEpssStrategies", ap);
			addProperty0(RUSSELFileRecord.KEYWORDS, "detailMetaKeywords", ap);
		} else {
			addProperty0(RUSSELFileRecord.TITLE, "metaTitle", ap);
			addProperty0(RUSSELFileRecord.DESCRIPTION, "metaDescription", ap);
			addProperty0(RUSSELFileRecord.PUBLISHER, "metaPublisher", ap);
			addProperty0(RUSSELFileRecord.CLASSIFICATION, "metaClassification", ap);
			addObjectiveProperty0(ap, "display-objective-list");
			addProperty0(RUSSELFileRecord.INTERACTIVITY, "metaInteractivity", ap);
			addProperty0(RUSSELFileRecord.ENVIRONMENT, "metaEnvironment", ap);
			addProperty0(RUSSELFileRecord.COVERAGE, "metaCoverage", ap);
			addProperty0(RUSSELFileRecord.SKILL, "metaSkill", ap);
			addProperty0(RUSSELFileRecord.LANGUAGE, "metaLanguage", ap);
			addProperty0(RUSSELFileRecord.DURATION, "metaDuration", ap);
			addProperty0(RUSSELFileRecord.TECHNICAL_REQUIREMENTS, "metaTechnicalRequirements", ap);
			addProperty0(RUSSELFileRecord.DISTRIBUTION, "metaDistribution", ap);
			addProperty0(RUSSELFileRecord.LEVEL, "metaLevel", ap);
			addProperty0(RUSSELFileRecord.PART_OF, "metaPartOf", ap);
			addProperty0(RUSSELFileRecord.REQUIRES, "metaRequires", ap);
			addProperty0(RUSSELFileRecord.KEYWORDS, "metaKeywords", ap);
		}
		record.parseESBPacket(ap);
		return record;
	}	
}