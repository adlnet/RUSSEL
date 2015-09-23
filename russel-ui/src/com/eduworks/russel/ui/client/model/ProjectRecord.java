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

package com.eduworks.russel.ui.client.model;

import java.util.Iterator;
import java.util.Vector;

import com.eduworks.gwt.client.net.packet.ESBPacket;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

/**
 * ProjectFileModel
 * Defines globals, methods for handling EPSS project files
 * 
 * @author Eduworks Corporation
 */
public class ProjectRecord extends RUSSELFileRecord {
	public final static String MIME_RUSSEL_PROJECT = "russel/project";
	public final static String TEMPLATE_TITLE = "templateTitle";
	public final static String TEMPLATE_LONG_TITLE = "templateLongTitle";
	public final static String TEMPLATE_SECTIONS = "templateSections";
	public final static String TEMPLATE_SECTION_SHORT_TITLE = "shortTitle";
	public final static String TEMPLATE_SECTION_LONG_TITLE = "longTitle";
	public final static String TEMPLATE_SECTION_GUIDENCE = "guidance";
	public final static String TEMPLATE_SECTION_TERMS = "searchTerms";
	public final static String PROJECT_TITLE = "projectTitle";
	public final static String PROJECT_IMI = "projectImi";
	public final static String PROJECT_TAXONOMY = "projectTaxonomy";
	public final static String PROJECT_NOTES = "projectNotes";
	public final static String PROJECT_LEARNING_OBJECTIVES = "projectLearningObjectives";
	public final static String PROJECT_SECTION_NOTES = "projectSectionNotes";
	public final static String PROJECT_SECTION_ASSETS = "projectSectionAssets";
	public final static String PROJECT_ASSETS = "projectAssets";
	
	private String templateName;
	private String templateNameLong;
	private String notes;
	private String imi;
	private String bloomTaxonomy;
	private JSONArray sections;
	private JSONArray sectionAssets;
	private JSONArray sectionNotes;
	private JSONObject assets;
	private JSONArray changesSinceLastCommit = new JSONArray();
	
	/**
	 * ProjectFileModel Parses a JSON project file and builds the constructor. 
	 * @param templateJSON string
	 * @param record 
	 */
	public ProjectRecord(String templateJSON, RUSSELFileRecord record) {
		this(new ESBPacket(templateJSON));
		parseESBPacket(record.toObject());		
	}
	
	/**
	 * ProjectFileModel Copies the values from the ap into the constructor.
	 * @param ap AlfrescoPacket
	 */
	public ProjectRecord(ESBPacket ap) {
		this.setTitle(ap.containsKey(PROJECT_TITLE)?ap.getString(PROJECT_TITLE):"Click here to add a title");
		this.notes = ap.containsKey(PROJECT_NOTES)?ap.getString(PROJECT_NOTES):"";
		this.imi = ap.containsKey(PROJECT_IMI)?ap.getString(PROJECT_IMI):"";
		this.bloomTaxonomy = ap.containsKey(PROJECT_TAXONOMY)?ap.getString(PROJECT_TAXONOMY):"";
		this.setGuid(ap.containsKey(ID)?ap.getString(ID):"");
		this.setObjectives(ap.containsKey(PROJECT_LEARNING_OBJECTIVES)?convertToObject(ap.getArray(PROJECT_LEARNING_OBJECTIVES)):new JSONArray());
		this.sections = ap.getArray(TEMPLATE_SECTIONS);
		this.templateName = ap.getString(TEMPLATE_TITLE);
		this.templateNameLong = ap.getString(TEMPLATE_LONG_TITLE);
		if (ap.containsKey(PROJECT_SECTION_NOTES)) 
			this.sectionNotes = ap.getArray(PROJECT_SECTION_NOTES);
		else {
			this.sectionNotes = new JSONArray();
			for (int i = 0; i < this.sections.size(); i++)
				this.sectionNotes.set(i, new JSONString(""));
		}
		if (ap.containsKey(PROJECT_SECTION_ASSETS)) 
			this.sectionAssets = ap.getArray(PROJECT_SECTION_ASSETS);
		else {
			this.sectionAssets = new JSONArray();
			for (int i = 0; i < this.sections.size(); i++)
				this.sectionAssets.set(i, new JSONObject());
		}
		if (ap.containsKey(PROJECT_ASSETS))
			this.assets = ap.getObject(PROJECT_ASSETS);
		else {
			this.assets = new JSONObject();
		}
	}
	
	/**
	 * makeJSONBlob Pulls values of globals into an AlfrescoPacket, and then creates a blob for storage.
	 * @return blob
	 */
	public String toJSONString() {
		ESBPacket ap = new ESBPacket();
		ap.put(PROJECT_TITLE, getTitle());
		ap.put(PROJECT_NOTES, notes);
		ap.put(PROJECT_IMI, imi);
		ap.put(PROJECT_TAXONOMY, bloomTaxonomy);
		ap.put(PROJECT_LEARNING_OBJECTIVES, convertToReadable(this.getObjectives()));
		ap.put(TEMPLATE_LONG_TITLE, templateNameLong);
		ap.put(TEMPLATE_TITLE, templateName);
		ap.put(TEMPLATE_SECTIONS, sections);
		ap.put(PROJECT_ASSETS, assets);
		ap.put(PROJECT_SECTION_ASSETS, sectionAssets);
		ap.put(PROJECT_SECTION_NOTES, sectionNotes);
		ap.put(ID, getGuid());
		return ap.toString();
	}
	
	public JSONArray getChanges() {
		return changesSinceLastCommit;
	}
	
	public void commit() {
		Vector<String> idToRemove = new Vector<String>();
		for (String assetId : assets.keySet()) {
			boolean keep = false;
			for (int i = 0; i < sectionAssets.size() && !keep; i++) {
				boolean found = false;
				for (Iterator<String> cursorAssetId = sectionAssets.get(i).isObject().keySet().iterator(); cursorAssetId.hasNext() && !found;)
					if (cursorAssetId.next().equals(assetId))
						found = true;
				if (found)
					keep = true;
			}
			if (!keep)
				idToRemove.add(assetId);
		}
		for (int i = 0; i < idToRemove.size(); i++)
			assets.put(idToRemove.get(i), null);
		changesSinceLastCommit = new JSONArray();
	}
	
	public void addAssetText(int activeSection, RUSSELFileRecord r, String text) {
		if (assets.containsKey(r.getGuid()))
			sectionAssets.get(activeSection).isObject().put(r.getGuid(), new JSONString(text));
	}
	
	public void addAsset(int activeSection, RUSSELFileRecord r) {
		assets.put(r.getGuid(), r.toObject());
		sectionAssets.get(activeSection).isObject().put(r.getGuid(), new JSONString(""));
		JSONObject jo = new JSONObject();
		jo.put("r", new JSONString(r.getGuid()));
		jo.put("s", new JSONString(templateName + " - " + sections.get(activeSection).isObject().get(TEMPLATE_SECTION_SHORT_TITLE).isString().stringValue()));
		jo.put("op", new JSONString("1"));
		changesSinceLastCommit.set(changesSinceLastCommit.size(), jo);
	}
		
	public void removeAsset(int activeSection, RUSSELFileRecord r) {
		sectionAssets.get(activeSection).isObject().put(r.getGuid(), null);
		JSONObject jo = new JSONObject();
		jo.put("r", new JSONString(r.getGuid()));
		jo.put("s", new JSONString(templateName + " - " + sections.get(activeSection).isObject().get(TEMPLATE_SECTION_SHORT_TITLE).isString().stringValue()));
		jo.put("op", new JSONString("-1"));
		changesSinceLastCommit.set(changesSinceLastCommit.size(), jo);
	}
	
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getImi() {
		return imi;
	}

	public void setImi(String imi) {
		this.imi = imi;
	}

	public String getBloomTaxonomy() {
		return bloomTaxonomy;
	}

	public void setBloomTaxonomy(String bloomTaxonomy) {
		this.bloomTaxonomy = bloomTaxonomy;
	}

	public void setTitle(String title) {
		title = title.trim();
		if (title.equalsIgnoreCase("")||title.equalsIgnoreCase("Click here to add a title"))
			super.setTitle("DefaultName");
		else
			super.setTitle(title);
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getTemplateNameLong() {
		return templateNameLong;
	}

	public void setTemplateNameLong(String templateNameLong) {
		this.templateNameLong = templateNameLong;
	}

	public JSONArray getSections() {
		return sections;
	}

	public void setSections(JSONArray sections) {
		this.sections = sections;
	}

	public JSONArray getSectionAssets() {
		return sectionAssets;
	}

	public void setSectionAssets(JSONArray sectionAssets) {
		this.sectionAssets = sectionAssets;
	}

	public JSONArray getSectionNotes() {
		return sectionNotes;
	}

	public void setSectionNotes(int activeSection, String text) {
		this.sectionNotes.set(activeSection, new JSONString(text));
	}
	
	public JSONObject getAssets() {
		return assets;
	}
}

