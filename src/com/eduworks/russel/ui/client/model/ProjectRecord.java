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
import java.util.Set;
import java.util.Vector;

import org.vectomatic.file.Blob;

import com.eduworks.gwt.client.model.FileRecord;
import com.eduworks.gwt.client.model.Record;
import com.eduworks.gwt.client.net.api.ESBApi;
import com.eduworks.gwt.client.net.callback.ESBCallback;
import com.eduworks.gwt.client.net.packet.AjaxPacket;
import com.eduworks.gwt.client.net.packet.ESBPacket;
import com.eduworks.gwt.client.util.BlobUtils;
import com.eduworks.gwt.client.util.Browser;
import com.eduworks.gwt.client.util.StringTokenizer;
import com.eduworks.gwt.client.util.Uint8Array;
import com.eduworks.russel.ui.client.handler.StatusWindowHandler;
import com.eduworks.russel.ui.client.pagebuilder.EpssTemplates;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.DOM;

/**
 * ProjectFileModel
 * Defines globals, methods for handling EPSS project files
 * 
 * @author Eduworks Corporation
 */
public class ProjectRecord extends Record {
	public final static String RUSSEL_PROJECT = "russel/project";
	public final static String TEMPLATE_TITLE = "templateName";
	public final static String TEMPLATE_LONG_TITLE = "templateTitle";
	public final static String TEMPLATE_SECTION_STAGES = "stages";
	public final static String TEMPLATE_SECTION_SHORT_TITLE = "shortTitle";
	public final static String TEMPLATE_SECTION_LONG_TITLE = "longTitle";
	public final static String TEMPLATE_SECTION_GUIDENCE = "guidance";
	public final static String TEMPLATE_SECTION_TERMS = "searchTerms";
	public final static String PROJECT_TEMPLATE = "projectTemplate";
	public final static String PROJECT_TITLE = "projectTitle";
	public final static String PROJECT_IMI = "projectImi";
	public final static String PROJECT_TAXONOMY = "projectTaxonomy";
	public final static String PROJECT_USAGE = "projectUsage";
	public final static String PROJECT_TEMPLATE_NAME = "projectTemplateName";
	public final static String PROJECT_NOTES = "projectNotes";
	public final static String PROJECT_ID = "projectNodeId";
	public final static String PROJECT_LEARNING_OBJECTIVES = "projectLearningObjectives";
	public final static String PROJECT_CREATOR = "projectCreator";
	public final static String PROJECT_SECTION_NOTES = "projectSectionNotes";
	public final static String PROJECT_SECTION_ASSETS = "projectSectionAssets";
	
	public String projectTitle;
	public String projectCreator;
	public String projectTemplateName;
	public String projectTemplate;
	public JSONArray projectSections;
	public String projectNotes;
	public String projectLearningObjectives;
	public String projectImi;
	public String projectTaxo;
	public String projectUsage;
	//public String projectNodeId;  // This needs to be fixed if the project is uploaded
	public ProjectNotesRecord projectSectionNotes;
	public ProjectAssetsRecord projectSectionAssets;
	
	/**
	 * ProjectFileModel Parses a JSON project file and builds the constructor. 
	 * @param templateJSON string
	 */
	public ProjectRecord(String templateJSON) {
		ESBPacket ap = new ESBPacket(AjaxPacket.parseJSON(templateJSON));
		this.projectTemplate = templateJSON;
		this.projectTemplateName = ap.getString(TEMPLATE_TITLE);
		this.projectSections = ap.getArray(TEMPLATE_SECTION_STAGES);
		this.projectTitle = "Click here to add a title";
		this.projectImi = "0,";
		this.projectTaxo = "0,";
		this.projectUsage = "0,";
		this.projectNotes = "";
		this.projectLearningObjectives = "";
		this.projectCreator = ESBApi.username;
		this.guid = null;
		this.projectSectionNotes = new ProjectNotesRecord();
		this.projectSectionAssets = new ProjectAssetsRecord();
	}
	
	/**
	 * ProjectFileModel Copies the values from the ap into the constructor.
	 * @param ap AlfrescoPacket
	 */
	public ProjectRecord(ESBPacket ap) {
		//EPSSFileRecord fr = new EPSSFileRecord();
		this.projectTemplate = ap.getString(PROJECT_TEMPLATE);
		this.projectTitle = ap.getString(PROJECT_TITLE); 
		this.projectImi = ap.getString(PROJECT_IMI);
		this.projectSections = new ESBPacket(ap.getString(PROJECT_TEMPLATE)).getArray(TEMPLATE_SECTION_STAGES);
		this.projectTaxo = ap.getString(PROJECT_TAXONOMY);
		this.projectUsage = ap.getString(PROJECT_USAGE);
		this.projectNotes = ap.getString(PROJECT_NOTES);
		this.projectLearningObjectives = ap.getString(PROJECT_LEARNING_OBJECTIVES);
		this.projectTemplateName = ap.getString(PROJECT_TEMPLATE_NAME);
		this.projectCreator = ap.getString(PROJECT_CREATOR);
		this.guid = ap.getString(PROJECT_ID);
		this.projectSectionNotes = new ProjectNotesRecord(ap.getObject(PROJECT_SECTION_NOTES));
		this.projectSectionAssets = new ProjectAssetsRecord(ap.getObject(PROJECT_SECTION_ASSETS));
	}
	
	/**
	 * makeJSONBlob Pulls values of globals into an AlfrescoPacket, and then creates a blob for storage.
	 * @return blob
	 */
	public Blob makeJSONBlob() {
		ESBPacket ap = new ESBPacket();
		ap.put(PROJECT_TEMPLATE, projectTemplate);
		ap.put(PROJECT_TITLE, projectTitle);
		ap.put(PROJECT_CREATOR, projectCreator);
		ap.put(PROJECT_TEMPLATE_NAME, projectTemplateName);
		ap.put(PROJECT_NOTES, projectNotes);
		ap.put(PROJECT_LEARNING_OBJECTIVES, projectLearningObjectives);
		ap.put(PROJECT_IMI, projectImi);
		ap.put(PROJECT_TAXONOMY, projectTaxo);
		ap.put(PROJECT_USAGE, projectUsage);
		ap.put(PROJECT_ID, guid);
		ap.put(PROJECT_SECTION_NOTES, projectSectionNotes.getObject());
		ap.put(PROJECT_SECTION_ASSETS, projectSectionAssets.getObject());
		return BlobUtils.buildBlob(RUSSEL_PROJECT, ap.toString());
	}
	
	/**
	 * toJSONString Creates a temporary AlfrescoPacket with the project globals and converts to JSON string.
	 * @return String
	 */
	public String toJSONString() {
		ESBPacket tempAp = new ESBPacket();
		tempAp.put(PROJECT_TEMPLATE, projectTemplate);
		tempAp.put(PROJECT_TITLE, projectTitle);
		tempAp.put(PROJECT_CREATOR, projectCreator);
		tempAp.put(PROJECT_TEMPLATE_NAME, projectTemplateName);
		tempAp.put(PROJECT_NOTES, projectNotes);
		tempAp.put(PROJECT_LEARNING_OBJECTIVES, projectLearningObjectives);
		tempAp.put(PROJECT_IMI, projectImi);
		tempAp.put(PROJECT_TAXONOMY, projectTaxo);
		tempAp.put(PROJECT_USAGE, projectUsage);
		tempAp.put(PROJECT_ID, guid);
		tempAp.put(PROJECT_SECTION_NOTES, projectSectionNotes.getObject());
		tempAp.put(PROJECT_SECTION_ASSETS, projectSectionAssets.getObject());
		return tempAp.toString();
	}
	
	/**
	 * copyProject Creates a new AlfrescoPacket using project global values and uses it to create a copy of the ProjectFileModel.
	 * @return ProjectFileModel
	 */
	public ProjectRecord copyProject() {
		ESBPacket tempAp = new ESBPacket();
		tempAp.put(PROJECT_TEMPLATE, projectTemplate);
		tempAp.put(PROJECT_TITLE, projectTitle);
		tempAp.put(PROJECT_CREATOR, projectCreator);
		tempAp.put(PROJECT_TEMPLATE_NAME, projectTemplateName);
		tempAp.put(PROJECT_NOTES, projectNotes);
		tempAp.put(PROJECT_LEARNING_OBJECTIVES, projectLearningObjectives);
		tempAp.put(PROJECT_IMI, projectImi);
		tempAp.put(PROJECT_TAXONOMY, projectTaxo);
		tempAp.put(PROJECT_USAGE, projectUsage);
		tempAp.put(PROJECT_ID, guid);
		tempAp.put(PROJECT_SECTION_NOTES, projectSectionNotes.getObject());
		tempAp.put(PROJECT_SECTION_ASSETS, projectSectionAssets.getObject());
		ProjectRecord newPfm = new ProjectRecord(new ESBPacket(AjaxPacket.parseJSON(tempAp.toString())));

		return newPfm;
	}
	
	/**
	 * updatePfmNodeId Uses the Alfresco NodeId (contained in ap) to retrieve the original ProjectFile.
	 * Then replace the original node id in the retrieved Project File with the new Node Id contained in ap.
	 * NOTE: This function is needed when a RUSSEL export is imported back into RUSSEL as a new object. By replacing
	 * the Node Id, you are creating a new and separate copy of the original project. 
	 * @param ap AlfrescoPacket
	 */
	public static void updatePfmNodeId(final RUSSELFileRecord ap) {
		importFromServer(ap.getGuid(), 
							   new ESBCallback<ESBPacket>() {
									@Override
									public void onSuccess(ESBPacket nodeAp) {
										ProjectRecord pfm = new ProjectRecord(nodeAp);
										pfm.guid = ap.getGuid();
										ESBApi.updateResource(pfm.makeJSONBlob(),
															   pfm.projectTitle.replaceAll(" ", "_") + ".rpf",
															   pfm.guid,
															   new ESBCallback<ESBPacket>() {
																	@Override
																	public void onSuccess(ESBPacket esbPacket) {
																		
																	}
																	
																	@Override 
																	public void onFailure(Throwable caught) {
																		StatusWindowHandler.createMessage(StatusWindowHandler.getProjectSaveMessageError(ap.getFilename()), 
																			  	  StatusRecord.ALERT_ERROR);

																	}
															   });
									}
									
									@Override
									public void onFailure(Throwable caught) {
											StatusWindowHandler.createMessage(StatusWindowHandler.getProjectLoadMessageError(ap.getFilename()), 
													  	  StatusRecord.ALERT_ERROR);

									}
								});
	}
	
	/**
	 * addAsset Incorporates the provided Asset into the current Project.
	 * @param section String Represents the section of the template where the asset will be stored.
	 * @param assetId String Represents the NodeID for the asset file.
	 * @param assetFilename String Represents the Filename for the asset file.
	 * @param notes String Represents developer notes for the asset file.
	 */
	public void addAsset(final String section, final String assetId, final String assetFilename, final String notes) {
		ESBApi.getResourceMetadata(assetId, 
								   new ESBCallback<ESBPacket>() {
											@Override
											public void onSuccess(ESBPacket esbPacket) {
												RUSSELFileRecord fr = new RUSSELFileRecord(esbPacket);
												fr.setGuid(assetId);
												fr.setNotes(notes);
												projectSectionAssets.addSectionAsset(section, fr);	
											}
											
											@Override
											public void onFailure(Throwable caught) {
												StatusWindowHandler.createMessage(StatusWindowHandler.getUpdateMetadataMessageError(assetFilename), 
													  	  StatusRecord.ALERT_ERROR);
											}
	 								 });
	}
	
	/**
	 * removeAsset Removes the asset from the section indicated in the project.
	 * @param section String Represents the section where asset should be removed.
	 * @param assetId String Represents the NodeId of the asset to be removed.
	 */
	public void removeAsset(String section, String assetId) {
		projectSectionAssets.removeSectionAsset(section, assetId);
	}

	/**
	 * addSectionNotes Adds developer notes to a section of the project.
	 * @param section String Represents the name of the section to which the notes belong.
	 * @param notes String The developer notes for the section.
	 */
	public void addSectionNotes(String section, String notes) {
		projectSectionNotes.addSectionNote(section, notes);
	}

	/**
	 * updateAlfrescoAssetUsage Maintains the count of times that a particular asset is used for each template section.
	 * @param section String representing the name of the template section where the asset is found.
	 * @param assetId String representing the NodeId of the asset.
	 * @param assetFilename String representing the filename of the asset.
	 * @param add Boolean is set to true of the asset's count for this section should be incremented, set to false to be decremented.
	 */
	public void updateAlfrescoAssetUsage(final String section, final String assetId, final String assetFilename, final Boolean add) {
		ESBApi.getResourceMetadata(assetId,
								    new ESBCallback<ESBPacket>() {
										@Override
										public void onSuccess(ESBPacket ap) {
											RUSSELFileRecord apNew = updateIsdUsage(new RUSSELFileRecord(ap), projectTemplateName, section, add);
											apNew.setGuid(assetId);
											ESBApi.updateResourceMetadata(apNew.toObject(),
																		  new ESBCallback<ESBPacket>() {
																			@Override
																			public void onSuccess(ESBPacket nullPack) {
	
																			}
																			
																			@Override
																			public void onFailure(Throwable caught) {
																				StatusWindowHandler.createMessage(StatusWindowHandler.getUpdateMetadataMessageError(assetFilename), 
																					  	  StatusRecord.ALERT_ERROR);
																			}
																		  });
										}
										
										@Override
										public void onFailure(Throwable caught) {
											
										}
								   });
	}

	/**
	 * importFromAlfrescoNode Retrieves the actual node file contents from Alfresco.
	 * @param nodeId String represents the nodeId for the file
	 * @param filename String represents the filename for the node.
	 * @param callback AlfrescoCallback<AlfrescoPacket> provides transport for return handlers.
	 */
	public static void importFromServer(String nodeId, final ESBCallback<ESBPacket> callback) {
		ESBApi.getResource(nodeId,
						   true,
						   new ESBCallback<ESBPacket>() {
						   		@Override
								public void onSuccess(ESBPacket ESBPacket) {
									if (!Browser.isIE()) {
										JsArrayInteger chars = Uint8Array.createUint8Array(ESBPacket.getContents());
										String acc = "";
										for (int x=0;x<chars.length();x++)
											acc += (char)chars.get(x);
										callback.onSuccess(new ESBPacket(AjaxPacket.parseJSON(acc)));
									} else
										callback.onSuccess(ESBPacket);
								}
								
								@Override
								public void onFailure(Throwable caught) {
									callback.onFailure(caught);
								}
						   });
	}

	/* Detail View / Convert nodeUsage string into nicer display of Isd usage . */
	/**
	 * renderIsdUsage Converts the encoded representation of ISD usage for display in the DetailView.
	 * @param nodeUsage String original ISD usage string
	 * @param targetDiv String name of target div in the DetailView
	 */
	public static void renderIsdUsage(String nodeUsage, String targetDiv) {
		StringTokenizer useList, tempList; 
		String useStr = "";
		String usageBlock = "";
		String templateStr="";
		String strategyStr=""; 
		
		if ((nodeUsage != null) && (nodeUsage != "")) {
			useList = new StringTokenizer(nodeUsage, RUSSELFileRecord.USAGE_DELIMITER);
			while (useList.hasMoreTokens()) {
				useStr = useList.nextToken();
				tempList = new StringTokenizer(useStr, RUSSELFileRecord.USAGE_STRATEGY_DELIMITER); 
				if (tempList.countTokens() == 2) {
					templateStr = tempList.nextToken();
					tempList = new StringTokenizer(tempList.nextToken(), RUSSELFileRecord.USAGE_COUNT_DELIMITER);
					if (tempList.countTokens() == 2) {
						strategyStr = tempList.nextToken();
						usageBlock = usageBlock + buildIsdUsageDisplay(templateStr, strategyStr, tempList.nextToken());									
					}
				}
			}
			DOM.getElementById(targetDiv).setInnerHTML(usageBlock);
		}
	}

	/* Detail View / Retrieve the corresponding long titles for template items . */
	/**
	 * getIsdItemTitle Retrieves the long title for the ISD template item from the JSON template definition file.
	 * @param template String represents the name of the template to be searched.
	 * @param item String represents the element within the template for which we need the long title.
	 * @return String
	 */
	public static String getIsdItemTitle(String template, String item) {
		String valueStr = "";
		ESBPacket ap = null;
		if (template.contains("Gagne")) {
			ap = new ESBPacket(AjaxPacket.parseJSON(EpssTemplates.INSTANCE.getGagneTemplate().getText()));
		}
		else if (template.contains("Simulation")) {
			ap = new ESBPacket(AjaxPacket.parseJSON(EpssTemplates.INSTANCE.getSimulationTemplate().getText()));			
		}
		
		if (item.equals(ap.getString(TEMPLATE_TITLE))) {
			valueStr = ap.getString(TEMPLATE_LONG_TITLE);
		}
		else {
			JSONArray sections = ap.getArray(TEMPLATE_SECTION_STAGES);
			for (int i=0 ; i<sections.size() ; i++) {
				ESBPacket stage = new ESBPacket(sections.get(i).isObject()); 
				if (item.equals(stage.getString(TEMPLATE_SECTION_SHORT_TITLE))) {
					valueStr = stage.getString(TEMPLATE_SECTION_LONG_TITLE);
					i = sections.size();
				}
			}
		}
		return valueStr;
	}
	
	/* Detail View / Combine a template, strategy, and count into display of Isd usage . */
	/**
	 * buildIsdUsageDisplay Builds the HTML to render the ISD usage of an asset for a particular template strategy.
	 * @param template String represents the template being used.
	 * @param strategy String represents the strategy being built.
	 * @param count String represents the number of times the asset was used for the strategy.
	 * @return
	 */
	public static String buildIsdUsageDisplay(String template, String strategy, String count) {
		String templateHelp = getIsdItemTitle(template,template);
		String strategyHelp = getIsdItemTitle(template,strategy);
		String usageStr = "<span id=\"detailEpssStrategies\" class=\"meta-value\"><span class=\"info\" title=\""+templateHelp+"\">" + template + "</span> ";
		usageStr += "> <span class=\"info\" title=\""+strategyHelp+"\">" + strategy + "</span> "; 
		usageStr += "(<span class=\"info\" title=\"Number of uses in this category\">" + count + "</span>)</span>";
		return usageStr.replaceAll("'", "\'").replaceAll("\r", " ").replaceAll("\n", " ").trim();
	}
	
	/* EPSS Editor / Combine a template, strategy, and count into usage data entry. Ensure that input strings don't contain delimiters. */
	/**
	 * buildIsdUsageEntry Builds the encrypted string portion that represents the asset's usage for the given strategy for storage in metadata.
	 * @param template String represents the name of the template
	 * @param strategy String represents the name of the strategy
	 * @param count String represents the count of the asset usage for the strategy.
	 * @return
	 */
	public static String buildIsdUsageEntry(String template, String strategy, String count) {
		String usageStr = "";
		template.replaceAll(RUSSELFileRecord.USAGE_STRATEGY_DELIMITER, " ").replaceAll(RUSSELFileRecord.USAGE_COUNT_DELIMITER, " ").replaceAll(RUSSELFileRecord.USAGE_DELIMITER, " ").trim();
		strategy.replaceAll(RUSSELFileRecord.USAGE_STRATEGY_DELIMITER, " ").replaceAll(RUSSELFileRecord.USAGE_COUNT_DELIMITER, " ").replaceAll(RUSSELFileRecord.USAGE_DELIMITER, " ").trim();
		count.replaceAll(RUSSELFileRecord.USAGE_STRATEGY_DELIMITER, " ").replaceAll(RUSSELFileRecord.USAGE_COUNT_DELIMITER, " ").replaceAll(RUSSELFileRecord.USAGE_DELIMITER, " ").trim();
		usageStr = template + RUSSELFileRecord.USAGE_STRATEGY_DELIMITER + strategy + RUSSELFileRecord.USAGE_COUNT_DELIMITER + count + RUSSELFileRecord.USAGE_DELIMITER ;
		return usageStr;
	}

	/* EPSS Editor / Add the epssStrategy property and return the updated Alfresco packet */
	/**
	 * addEpssStrategyProperty adds the ISD usage data to the metadata for the asset's AlfrescoPacket.
	 * @param ap AlfrescoPacket of the asset
	 * @param epssStrategy String represents all of the ISD uses for the asset in encoded form.
	 * @return AlfrescoPacket
	 */
	private static RUSSELFileRecord addEpssStrategyProperty(RUSSELFileRecord ap, String epssStrategy) {
		if (!epssStrategy.equalsIgnoreCase("Click to edit") && !epssStrategy.equalsIgnoreCase("N/A"))
			ap.setStrategy(epssStrategy.replaceAll("\"", "\'").replaceAll("\r", " ").replaceAll("\n", " ").trim());	
		return ap;
	}

	/* EPSS Editor / Parse existing ISD usage paradata, integrate new usage data, and return updated nodeUsage */
	/**
	 * updateIsdUsage Retrieves an asset's prior ISD usage string and updates it a new template/strategy use
	 * @param ap AlfrescoPacket of the asset node
	 * @param newTemplate String represents the new usage template value.
	 * @param newStrategy String represents the new strategy value
	 * @param add Boolean set to true when the usage was added to the asset, false when subtracted from the asset.
	 * @return
	 */
	public static RUSSELFileRecord updateIsdUsage(RUSSELFileRecord ap, String newTemplate, String newStrategy, Boolean add) {
		StringTokenizer useList, tempList; 
		String useStr = "";
		String updatedNodeUsage = "";
		String templateStr="";
		String strategyStr=""; 
		int count=0;
		Boolean found = false;
		String nodeUsage = ap.getStrategy();
		
		if ((nodeUsage != "") && (nodeUsage != null))  {
			useList = new StringTokenizer(nodeUsage, RUSSELFileRecord.USAGE_DELIMITER);
			while (useList.hasMoreTokens()) {
				useStr = useList.nextToken();
				if (!found) {
					tempList = new StringTokenizer(useStr, RUSSELFileRecord.USAGE_STRATEGY_DELIMITER); 
					if (tempList.countTokens() == 2) {
						templateStr = tempList.nextToken();
						if (templateStr == newTemplate) {
							tempList = new StringTokenizer(tempList.nextToken(), RUSSELFileRecord.USAGE_COUNT_DELIMITER);
							if (tempList.countTokens() == 2) {
								strategyStr = tempList.nextToken();
								if (strategyStr == newStrategy) {
									found = true;
									count = (int)Integer.parseInt(tempList.nextToken());
									if (add) {
										updatedNodeUsage = updatedNodeUsage + buildIsdUsageEntry(newTemplate, newStrategy, String.valueOf(count + 1));									
									} 
									else if (count > 1) {
										updatedNodeUsage = updatedNodeUsage + buildIsdUsageEntry(newTemplate, newStrategy, String.valueOf(count - 1));
									}
								}
								else updatedNodeUsage = updatedNodeUsage + useStr + RUSSELFileRecord.USAGE_DELIMITER;
							}
						}
						else updatedNodeUsage = updatedNodeUsage + useStr + RUSSELFileRecord.USAGE_DELIMITER;
					}
				}
				else updatedNodeUsage = updatedNodeUsage + useStr + RUSSELFileRecord.USAGE_DELIMITER;
			}
		}
		if ((!found) && (add)) {
			updatedNodeUsage = updatedNodeUsage + buildIsdUsageEntry(newTemplate, newStrategy, "1");
		}

		addEpssStrategyProperty(ap,updatedNodeUsage);
	
		return ap;
	}

	public class ProjectAssetsRecord {
		private ESBPacket sectionAssets;
		
		public ProjectAssetsRecord() {
			sectionAssets = new ESBPacket();
		}
		
		public ProjectAssetsRecord(ESBPacket esbPacket) {
			parseESBPacket(esbPacket);
		}
		
		private void parseESBPacket(ESBPacket esbPacket) {
			sectionAssets = esbPacket;
		}

		public void removeSectionAsset(String section, String assetId) {
			if (sectionAssets.containsKey(section)) {
				ESBPacket esbP = sectionAssets.getObject(section);
				esbP.remove(assetId);
			}
		}

		public void addSectionAsset(String section, FileRecord fr) {
			if (!sectionAssets.containsKey(section))
				sectionAssets.put(section, new ESBPacket());
			
			ESBPacket esbP = sectionAssets.getObject(section);
			esbP.put(fr.getGuid(), fr.toString());
		}

		public String getSectionAssetNotes(String activeSection, String activeAssetId) {
			if (sectionAssets.containsKey(activeSection))
				if (sectionAssets.getObject(activeSection).containsKey(activeAssetId))
					return new ESBPacket(sectionAssets.getObject(activeSection).getString(activeAssetId)).getString(RUSSELFileRecord.NOTES);
			return "";
		}

		public boolean hasSectionAsset(String activeSection, String activeAssetId) {
			if (sectionAssets.containsKey(activeSection))
				if (sectionAssets.getObject(activeSection).containsKey(activeAssetId))
					return true;
			return false;
		}
		
		public Vector<RUSSELFileRecord> getSectionAssets(String activeSection) {
			Vector<RUSSELFileRecord> fileRecords = new Vector<RUSSELFileRecord>();
			if (sectionAssets.containsKey(activeSection)) {
				ESBPacket section = sectionAssets.getObject(activeSection);
				for (Iterator<String> assetPointer = section.keySet().iterator(); assetPointer.hasNext();) {
					String assetId = assetPointer.next();
					RUSSELFileRecord fr = new RUSSELFileRecord(new ESBPacket(section.getString(assetId)));
					fileRecords.add(fr);
				}
			}
			return fileRecords;
		}

		public Set<String> getSectionList() {
			return sectionAssets.keySet();
		}

		@Override
		public String toString() {
			return sectionAssets.toString();
		}

		public JSONValue getObject() {
			return sectionAssets;
		}

		public boolean hasSection(String sectionName) {
			return sectionAssets.containsKey(sectionName);
		}
	}
	
	public class ProjectNotesRecord {
		private ESBPacket sectionNotes;
		
		public ProjectNotesRecord() {
			sectionNotes = new ESBPacket();
		}
		
		public ProjectNotesRecord(ESBPacket esbPacket) {
			parseESBPacket(esbPacket);
		}
		
		private void parseESBPacket(ESBPacket esbPacket) {
			sectionNotes = esbPacket;
		}

		public void addSectionNote(String section, String notes) {
			sectionNotes.put(section, notes);
		}

		public String getSectionNotes(String activeSection) {
			return sectionNotes.getString(activeSection);
		}
		
		@Override
		public String toString() {
			return sectionNotes.toString();
		}

		public JSONValue getObject() {
			return sectionNotes;
		}

		public boolean hasSection(String section) {
			return sectionNotes.containsKey(section);
		}
	}

}

