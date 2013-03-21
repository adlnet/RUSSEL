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
import com.eduworks.russel.ui.client.Russel;
import com.eduworks.russel.ui.client.handler.StatusWindowHandler;
import com.eduworks.russel.ui.client.pagebuilder.MetaBuilder;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayInteger;

public class ProjectFileModel {
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
	
	public void removeAsset(String section, String assetId) {
		projectSectionAssets.removeAsset(section, assetId);
	}

	public void addSectionNotes(String section, String notes) {
		projectSectionNotes.addKeyValue(section, notes);
	}

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

	/* EPSS Editor / Combine a template, strategy, and count into usage data entry. Ensure that input strings don't contain delimiters. */
	public static String buildIsdUsageEntry(String template, String strategy, String count) {
		String usageStr = "";
		template.replaceAll(AlfrescoPacket.USAGE_STRATEGY_DELIMITER, " ").replaceAll(AlfrescoPacket.USAGE_COUNT_DELIMITER, " ").replaceAll(AlfrescoPacket.USAGE_DELIMITER, " ").trim();
		strategy.replaceAll(AlfrescoPacket.USAGE_STRATEGY_DELIMITER, " ").replaceAll(AlfrescoPacket.USAGE_COUNT_DELIMITER, " ").replaceAll(AlfrescoPacket.USAGE_DELIMITER, " ").trim();
		count.replaceAll(AlfrescoPacket.USAGE_STRATEGY_DELIMITER, " ").replaceAll(AlfrescoPacket.USAGE_COUNT_DELIMITER, " ").replaceAll(AlfrescoPacket.USAGE_DELIMITER, " ").trim();
		usageStr = template + AlfrescoPacket.USAGE_STRATEGY_DELIMITER + strategy + AlfrescoPacket.USAGE_COUNT_DELIMITER + count + AlfrescoPacket.USAGE_DELIMITER ;
		return usageStr;
	}

	/* EPSS Editor / Add the epssStrategy property and return the updated Alfresco packet */
	private static AlfrescoPacket addEpssStrategyProperty(AlfrescoPacket ap, String epssStrategy) {
		if (!epssStrategy.equalsIgnoreCase("Click to edit") && !epssStrategy.equalsIgnoreCase("N/A"))
			ap.addKeyValue("russel:epssStrategy", epssStrategy.replaceAll("\"", "\'").replaceAll("\r", " ").replaceAll("\n", " ").trim());	
		return ap;
	}

	/* EPSS Editor / Parse existing ISD usage paradata, integrate new usage data, and return updated nodeUsage */
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