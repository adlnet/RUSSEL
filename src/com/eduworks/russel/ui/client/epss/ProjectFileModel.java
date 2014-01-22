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

package com.eduworks.russel.ui.client.epss;

import org.vectomatic.file.Blob;

import com.eduworks.gwt.client.net.CommunicationHub;
import com.eduworks.gwt.client.net.api.AlfrescoApi;
import com.eduworks.gwt.client.net.callback.AlfrescoCallback;
import com.eduworks.gwt.client.net.packet.AlfrescoPacket;
import com.eduworks.gwt.client.net.packet.StatusPacket;
import com.eduworks.gwt.client.util.BlobUtils;
import com.eduworks.gwt.client.util.Browser;
import com.eduworks.gwt.client.util.StringTokenizer;
import com.eduworks.gwt.client.util.Uint8Array;
import com.eduworks.russel.ui.client.Constants;
import com.eduworks.russel.ui.client.Russel;
import com.eduworks.russel.ui.client.handler.StatusWindowHandler;
import com.eduworks.russel.ui.client.pagebuilder.EpssTemplates;
import com.eduworks.russel.ui.client.pagebuilder.MetaBuilder;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.user.client.DOM;

/**
 * ProjectFileModel
 * Defines globals, methods for handling EPSS project files
 * 
 * @author Eduworks Corporation
 */
public class ProjectFileModel extends Constants {
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
	public JsArray<AlfrescoPacket> projectSections;
	public String projectNotes;
	public String projectLearningObjectives;
	public String projectImi;
	public String projectTaxo;
	public String projectUsage;
	public String projectNodeId;  // This needs to be fixed if the project is uploaded
	public AlfrescoPacket projectSectionNotes;
	public AlfrescoPacket projectSectionAssets;
	
	/**
	 * ProjectFileModel Parses a JSON project file and builds the constructor. 
	 * @param templateJSON string
	 */
	@SuppressWarnings("unchecked")
	public ProjectFileModel(String templateJSON) {
		AlfrescoPacket ap = AlfrescoPacket.wrap(CommunicationHub.parseJSON(templateJSON));
		this.projectTemplate = ap.toJSONString();
		this.projectTemplateName = ap.getValueString(TEMPLATE_TITLE);
		this.projectSections = (JsArray<AlfrescoPacket>) ap.getValue(TEMPLATE_SECTION_STAGES);
		this.projectTitle = "Click here to add a title";
		this.projectImi = "0,";
		this.projectTaxo = "0,";
		this.projectUsage = "0,";
		this.projectNotes = "";
		this.projectLearningObjectives = "";
		this.projectCreator = AlfrescoApi.username;
		this.projectNodeId = null;
		this.projectSectionNotes = AlfrescoPacket.makePacket();
		this.projectSectionAssets = AlfrescoPacket.makePacket();
	}
	
	/**
	 * ProjectFileModel Copies the values from the ap into the constructor.
	 * @param ap AlfrescoPacket
	 */
	@SuppressWarnings("unchecked")
	public ProjectFileModel(AlfrescoPacket ap) {
		this.projectTemplate = ap.getValueString(PROJECT_TEMPLATE);
		this.projectTitle = ap.getValueString(PROJECT_TITLE); 
		this.projectImi = ap.getValueString(PROJECT_IMI);
		this.projectSections = (JsArray<AlfrescoPacket>) AlfrescoPacket.wrap(CommunicationHub.parseJSON(this.projectTemplate)).getValue(TEMPLATE_SECTION_STAGES);
		this.projectTaxo = ap.getValueString(PROJECT_TAXONOMY);
		this.projectUsage = ap.getValueString(PROJECT_USAGE);
		this.projectNotes = ap.getValueString(PROJECT_NOTES);
		this.projectLearningObjectives = ap.getValueString(PROJECT_LEARNING_OBJECTIVES);
		this.projectTemplateName = ap.getValueString(PROJECT_TEMPLATE_NAME);
		this.projectCreator = ap.getValueString(PROJECT_CREATOR);
		this.projectNodeId = ap.getValueString(PROJECT_ID);
		this.projectSectionNotes = AlfrescoPacket.wrap(ap.getValue(PROJECT_SECTION_NOTES));
		this.projectSectionAssets = AlfrescoPacket.wrap(ap.getValue(PROJECT_SECTION_ASSETS));
	}
	
	/**
	 * makeJSONBlob Pulls values of globals into an AlfrescoPacket, and then creates a blob for storage.
	 * @return blob
	 */
	public Blob makeJSONBlob() {
		AlfrescoPacket ap = AlfrescoPacket.makePacket();
		ap.addKeyValue(PROJECT_TEMPLATE, projectTemplate);
		ap.addKeyValue(PROJECT_TITLE, projectTitle);
		ap.addKeyValue(PROJECT_CREATOR, projectCreator);
		ap.addKeyValue(PROJECT_TEMPLATE_NAME, projectTemplateName);
		ap.addKeyValue(PROJECT_NOTES, projectNotes);
		ap.addKeyValue(PROJECT_LEARNING_OBJECTIVES, projectLearningObjectives);
		ap.addKeyValue(PROJECT_IMI, projectImi);
		ap.addKeyValue(PROJECT_TAXONOMY, projectTaxo);
		ap.addKeyValue(PROJECT_USAGE, projectUsage);
		ap.addKeyValue(PROJECT_ID, projectNodeId);
		ap.addKeyValue(PROJECT_SECTION_NOTES, projectSectionNotes);
		ap.addKeyValue(PROJECT_SECTION_ASSETS, projectSectionAssets);
		return BlobUtils.buildBlob(RUSSEL_PROJECT, ap.toJSONString());
	}
	
	/**
	 * toJSONString Creates a temporary AlfrescoPacket with the project globals and converts to JSON string.
	 * @return String
	 */
	public String toJSONString() {
		AlfrescoPacket tempAp = AlfrescoPacket.makePacket();
		tempAp.addKeyValue(PROJECT_TEMPLATE, projectTemplate);
		tempAp.addKeyValue(PROJECT_TITLE, projectTitle);
		tempAp.addKeyValue(PROJECT_CREATOR, projectCreator);
		tempAp.addKeyValue(PROJECT_TEMPLATE_NAME, projectTemplateName);
		tempAp.addKeyValue(PROJECT_NOTES, projectNotes);
		tempAp.addKeyValue(PROJECT_LEARNING_OBJECTIVES, projectLearningObjectives);
		tempAp.addKeyValue(PROJECT_IMI, projectImi);
		tempAp.addKeyValue(PROJECT_TAXONOMY, projectTaxo);
		tempAp.addKeyValue(PROJECT_USAGE, projectUsage);
		tempAp.addKeyValue(PROJECT_ID, projectNodeId);
		tempAp.addKeyValue(PROJECT_SECTION_NOTES, projectSectionNotes);
		tempAp.addKeyValue(PROJECT_SECTION_ASSETS, projectSectionAssets);
		return tempAp.toJSONString();
	}
	
	/**
	 * copyProject Creates a new AlfrescoPacket using project global values and uses it to create a copy of the ProjectFileModel.
	 * @return ProjectFileModel
	 */
	public ProjectFileModel copyProject() {
		AlfrescoPacket tempAp = AlfrescoPacket.makePacket();
		tempAp.addKeyValue(PROJECT_TEMPLATE, projectTemplate);
		tempAp.addKeyValue(PROJECT_TITLE, projectTitle);
		tempAp.addKeyValue(PROJECT_CREATOR, projectCreator);
		tempAp.addKeyValue(PROJECT_TEMPLATE_NAME, projectTemplateName);
		tempAp.addKeyValue(PROJECT_NOTES, projectNotes);
		tempAp.addKeyValue(PROJECT_LEARNING_OBJECTIVES, projectLearningObjectives);
		tempAp.addKeyValue(PROJECT_IMI, projectImi);
		tempAp.addKeyValue(PROJECT_TAXONOMY, projectTaxo);
		tempAp.addKeyValue(PROJECT_USAGE, projectUsage);
		tempAp.addKeyValue(PROJECT_ID, projectNodeId);
		tempAp.addKeyValue(PROJECT_SECTION_NOTES, projectSectionNotes);
		tempAp.addKeyValue(PROJECT_SECTION_ASSETS, projectSectionAssets);
		ProjectFileModel newPfm = new ProjectFileModel(AlfrescoPacket.wrap(CommunicationHub.parseJSON(tempAp.toJSONString())));

		return newPfm;
	}
	
	/**
	 * updatePfmNodeId Uses the Alfresco NodeId (contained in ap) to retrieve the original ProjectFile.
	 * Then replace the original node id in the retrieved Project File with the new Node Id contained in ap.
	 * NOTE: This function is needed when a RUSSEL export is imported back into RUSSEL as a new object. By replacing
	 * the Node Id, you are creating a new and separate copy of the original project. 
	 * @param ap AlfrescoPacket
	 */
	public static void updatePfmNodeId(final AlfrescoPacket ap) {
		importFromAlfrescoNode(ap.getNodeId(), 
							   ap.getFilename(), 
							   new AlfrescoCallback<AlfrescoPacket>() {
									@Override
									public void onSuccess(AlfrescoPacket nodeAp) {
										ProjectFileModel pfm = new ProjectFileModel(nodeAp);
										pfm.projectNodeId = ap.getNodeId();
										AlfrescoApi.updateFile(pfm.makeJSONBlob(),
															   pfm.projectTitle.replaceAll(" ", "_") + ".rpf",
															   pfm.projectNodeId,
															   Russel.RUSSEL_ASPECTS.split(","),
															   new AlfrescoCallback<AlfrescoPacket>() {
																	@Override
																	public void onSuccess(AlfrescoPacket alfrescoPacket) {
																		
																	}
																	
																	@Override 
																	public void onFailure(Throwable caught) {
																		StatusWindowHandler.createMessage(StatusWindowHandler.getProjectSaveMessageError(ap.getFilename()), 
																			  	  StatusPacket.ALERT_ERROR);

																	}
															   });
									}
									
									@Override
									public void onFailure(Throwable caught) {
											StatusWindowHandler.createMessage(StatusWindowHandler.getProjectLoadMessageError(ap.getFilename()), 
													  	  StatusPacket.ALERT_ERROR);

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
	public void addAsset(final String section, final String assetId, final String assetFilename, String notes) {
		final AlfrescoPacket ap = AlfrescoPacket.makePacket();
		ap.addKeyValue("id", assetId);
		ap.addKeyValue("fileName", assetFilename);
		if (notes!=null)
			ap.addKeyValue("notes", notes);
		AlfrescoApi.getMetadata(assetId, new AlfrescoCallback<AlfrescoPacket>() {
												@Override
												public void onSuccess(AlfrescoPacket alfrescoPacket) {
													ap.addKeyValue("metadata", alfrescoPacket);
													projectSectionAssets.addAsset(section, ap);	
												}
												
												@Override
												public void onFailure(Throwable caught) {
													StatusWindowHandler.createMessage(StatusWindowHandler.getUpdateMetadataMessageError(assetFilename), 
														  	  StatusPacket.ALERT_ERROR);
												}
		 								 });
	}
	
	/**
	 * removeAsset Removes the asset from the section indicated in the project.
	 * @param section String Represents the section where asset should be removed.
	 * @param assetId String Represents the NodeId of the asset to be removed.
	 */
	public void removeAsset(String section, String assetId) {
		projectSectionAssets.removeAsset(section, assetId);
	}

	/**
	 * addSectionNotes Adds developer notes to a section of the project.
	 * @param section String Represents the name of the section to which the notes belong.
	 * @param notes String The developer notes for the section.
	 */
	public void addSectionNotes(String section, String notes) {
		projectSectionNotes.addKeyValue(section, notes);
	}

	/**
	 * updateAlfrescoAssetUsage Maintains the count of times that a particular asset is used for each template section.
	 * @param section String representing the name of the template section where the asset is found.
	 * @param assetId String representing the NodeId of the asset.
	 * @param assetFilename String representing the filename of the asset.
	 * @param add Boolean is set to true of the asset's count for this section should be incremented, set to false to be decremented.
	 */
	public void updateAlfrescoAssetUsage(final String section, final String assetId, final String assetFilename, final Boolean add) {
		AlfrescoApi.getMetadata(assetId,
							    new AlfrescoCallback<AlfrescoPacket>() {
									@Override
									public void onSuccess(AlfrescoPacket ap) {
										AlfrescoPacket apNew = updateIsdUsage(ap, projectTemplateName, section, add);
										String postString = MetaBuilder.convertToMetaPacket(apNew);
										if (postString!=null)
											AlfrescoApi.setObjectMetadata(assetId,
																		  postString, 
																		  new AlfrescoCallback<AlfrescoPacket>() {
																			@Override
																			public void onSuccess(final AlfrescoPacket nullPack) {

																			}
																			
																			@Override
																			public void onFailure(Throwable caught) {
																				StatusWindowHandler.createMessage(StatusWindowHandler.getUpdateMetadataMessageError(assetFilename), 
																					  	  StatusPacket.ALERT_ERROR);
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
	public static void importFromAlfrescoNode(String nodeId, String filename, final AlfrescoCallback<AlfrescoPacket> callback) {
		AlfrescoApi.getObjectStream(nodeId, 
									filename, 
									new AlfrescoCallback<AlfrescoPacket>() {
										@Override
										public void onSuccess(AlfrescoPacket alfrescoPacket) {
											if (!Browser.isIE()) {
												JsArrayInteger chars = Uint8Array.createUint8Array(alfrescoPacket.getContents());
												String acc = "";
												for (int x=0;x<chars.length();x++)
													acc += (char)chars.get(x);
												callback.onSuccess(AlfrescoPacket.wrap(CommunicationHub.parseJSON(acc)));
											} else
												callback.onSuccess(alfrescoPacket);
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
			useList = new StringTokenizer(nodeUsage, AlfrescoPacket.USAGE_DELIMITER);
			while (useList.hasMoreTokens()) {
				useStr = useList.nextToken();
				tempList = new StringTokenizer(useStr, AlfrescoPacket.USAGE_STRATEGY_DELIMITER); 
				if (tempList.countTokens() == 2) {
					templateStr = tempList.nextToken();
					tempList = new StringTokenizer(tempList.nextToken(), AlfrescoPacket.USAGE_COUNT_DELIMITER);
					if (tempList.countTokens() == 2) {
						strategyStr = tempList.nextToken();
						usageBlock = usageBlock + buildIsdUsageDisplay(templateStr, strategyStr, tempList.nextToken());									
					}
				}
			}
			DOM.getElementById(targetDiv).setInnerHTML(usageBlock);
		}
		return;
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
		AlfrescoPacket ap = null;
		if (template.contains("Gagne")) {
			ap = AlfrescoPacket.wrap(CommunicationHub.parseJSON(EpssTemplates.INSTANCE.getGagneTemplate().getText()));
		}
		else if (template.contains("Simulation")) {
			ap = AlfrescoPacket.wrap(CommunicationHub.parseJSON(EpssTemplates.INSTANCE.getSimulationTemplate().getText()));			
		}
		
		if (item.equals(ap.getValueString(TEMPLATE_TITLE))) {
			valueStr = ap.getValueString(TEMPLATE_LONG_TITLE);
		}
		else {
			@SuppressWarnings("unchecked")
			JsArray<AlfrescoPacket> sections = (JsArray<AlfrescoPacket>) ap.getValue(TEMPLATE_SECTION_STAGES);
			for (int i=0 ; i<sections.length() ; i++) {
				AlfrescoPacket stage = sections.get(i); 
				if (item.equals(stage.getValueString(TEMPLATE_SECTION_SHORT_TITLE))) {
					valueStr = stage.getValueString(TEMPLATE_SECTION_LONG_TITLE);
					i = sections.length();
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
		template.replaceAll(AlfrescoPacket.USAGE_STRATEGY_DELIMITER, " ").replaceAll(AlfrescoPacket.USAGE_COUNT_DELIMITER, " ").replaceAll(AlfrescoPacket.USAGE_DELIMITER, " ").trim();
		strategy.replaceAll(AlfrescoPacket.USAGE_STRATEGY_DELIMITER, " ").replaceAll(AlfrescoPacket.USAGE_COUNT_DELIMITER, " ").replaceAll(AlfrescoPacket.USAGE_DELIMITER, " ").trim();
		count.replaceAll(AlfrescoPacket.USAGE_STRATEGY_DELIMITER, " ").replaceAll(AlfrescoPacket.USAGE_COUNT_DELIMITER, " ").replaceAll(AlfrescoPacket.USAGE_DELIMITER, " ").trim();
		usageStr = template + AlfrescoPacket.USAGE_STRATEGY_DELIMITER + strategy + AlfrescoPacket.USAGE_COUNT_DELIMITER + count + AlfrescoPacket.USAGE_DELIMITER ;
		return usageStr;
	}

	/* EPSS Editor / Add the epssStrategy property and return the updated Alfresco packet */
	/**
	 * addEpssStrategyProperty adds the ISD usage data to the metadata for the asset's AlfrescoPacket.
	 * @param ap AlfrescoPacket of the asset
	 * @param epssStrategy String represents all of the ISD uses for the asset in encoded form.
	 * @return AlfrescoPacket
	 */
	private static AlfrescoPacket addEpssStrategyProperty(AlfrescoPacket ap, String epssStrategy) {
		if (!epssStrategy.equalsIgnoreCase("Click to edit") && !epssStrategy.equalsIgnoreCase("N/A"))
			ap.addKeyValue("russel:epssStrategy", epssStrategy.replaceAll("\"", "\'").replaceAll("\r", " ").replaceAll("\n", " ").trim());	
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
	public static AlfrescoPacket updateIsdUsage(AlfrescoPacket ap, String newTemplate, String newStrategy, Boolean add) {
		StringTokenizer useList, tempList; 
		String useStr = "";
		String updatedNodeUsage = "";
		String templateStr="";
		String strategyStr=""; 
		int count=0;
		Boolean found = false;
		String nodeUsage = ap.getRusselValue("russel:epssStrategy");
		
		if ((nodeUsage != "") && (nodeUsage != null))  {
			useList = new StringTokenizer(nodeUsage, AlfrescoPacket.USAGE_DELIMITER);
			while (useList.hasMoreTokens()) {
				useStr = useList.nextToken();
				if (!found) {
					tempList = new StringTokenizer(useStr, AlfrescoPacket.USAGE_STRATEGY_DELIMITER); 
					if (tempList.countTokens() == 2) {
						templateStr = tempList.nextToken();
						if (templateStr == newTemplate) {
							tempList = new StringTokenizer(tempList.nextToken(), AlfrescoPacket.USAGE_COUNT_DELIMITER);
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
								else updatedNodeUsage = updatedNodeUsage + useStr + AlfrescoPacket.USAGE_DELIMITER;
							}
						}
						else updatedNodeUsage = updatedNodeUsage + useStr + AlfrescoPacket.USAGE_DELIMITER;
					}
				}
				else updatedNodeUsage = updatedNodeUsage + useStr + AlfrescoPacket.USAGE_DELIMITER;
			}
		}
		if ((!found) && (add)) {
			updatedNodeUsage = updatedNodeUsage + buildIsdUsageEntry(newTemplate, newStrategy, "1");
		}

		AlfrescoPacket apNew = AlfrescoPacket.makePacket();
		apNew = addEpssStrategyProperty(apNew,updatedNodeUsage);
	
		return apNew;
	}
		
}