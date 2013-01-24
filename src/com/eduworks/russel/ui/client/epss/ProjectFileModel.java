/*
Copyright (c) 2012 Eduworks Corporation
All rights reserved.
 
This Software (including source code, binary code and documentation) is provided by Eduworks Corporation to
the Government pursuant to contract number W31P4Q-12 -C- 0119 dated 21 March, 2012 issued by the U.S. Army 
Contracting Command Redstone. This Software is a preliminary version in development. It does not fully operate
as intended and has not been fully tested. This Software is provided to the U.S. Government for testing and
evaluation under the following terms and conditions:

	--Any redistribution of source code, binary code, or documentation must include this notice in its entirety, 
	 starting with the above copyright notice and ending with the disclaimer below.
	 
	--Eduworks Corporation grants the U.S. Government the right to use, modify, reproduce, release, perform,
	 display, and disclose the source code, binary code, and documentation within the Government for the purpose
	 of evaluating and testing this Software.
	 
	--No other rights are granted and no other distribution or use is permitted, including without limitation 
	 any use undertaken for profit, without the express written permission of Eduworks Corporation.
	 
	--All modifications to source code must be reported to Eduworks Corporation. Evaluators and testers shall
	 additionally make best efforts to report test results, evaluation results and bugs to Eduworks Corporation
	 using in-system feedback mechanism or email to russel@eduworks.com.
	 
THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
THE COPYRIGHT HOLDER BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN 
IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
*/

package com.eduworks.russel.ui.client.epss;

import org.vectomatic.arrays.ArrayBuffer;
import org.vectomatic.file.Blob;

import com.eduworks.gwt.client.util.StringTokenizer;
import com.eduworks.gwt.client.util.Uint8Array;
import com.eduworks.gwt.russel.ui.client.net.AlfrescoApi;
import com.eduworks.gwt.russel.ui.client.net.AlfrescoCallback;
import com.eduworks.gwt.russel.ui.client.net.AlfrescoPacket;
import com.eduworks.gwt.russel.ui.client.net.CommunicationHub;
import com.eduworks.russel.ui.client.pagebuilder.MetaBuilder;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.user.client.Window;

public class ProjectFileModel {
	public final static String GAGNE_TEMPLATE = "Gagne's Nine Events";
	public final static String SIMULATION_TEMPLATE = "Modified Simulation Model";
	public final static String RUSSEL_MIME_TYPE = "russel/project";
	public static final String USAGE_DELIMITER = "|";
	public static final String USAGE_STRATEGY_DELIMITER = "^";
	public static final String USAGE_COUNT_DELIMITER = "#";
	
	public String projectTitle;
	public String projectCreator;
	public String projectTemplate;
	public Integer projectMaxSections;
	public String projectNotes;
	public String projectLearningObjectives;
	public String projectImi;
	public String projectTaxo;
	public String projectUsage;
	public String projectNodeId;  // This needs to be fixed if the project is uploaded
	public AlfrescoPacket projectSectionNotes;
	public AlfrescoPacket projectSectionAssets;
	
	public ProjectFileModel(String templateType) {
		this.projectTemplate = templateType;
		if (this.projectTemplate == GAGNE_TEMPLATE) 
			this.projectMaxSections = 9;
		else if (this.projectTemplate ==  SIMULATION_TEMPLATE) 
			this.projectMaxSections = 14;
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
	
	public ProjectFileModel(AlfrescoPacket ap) {
		this.projectTitle = ap.getValueString("projectTitle"); 
		this.projectImi = ap.getValueString("projectImi");
		this.projectTaxo = ap.getValueString("projectTaxo");
		this.projectUsage = ap.getValueString("projectUsage");
		this.projectNotes = ap.getValueString("projectNotes");
		this.projectLearningObjectives = ap.getValueString("projectLearningObjectives");
		this.projectTemplate = ap.getValueString("projectTemplate");
		if (this.projectTemplate == GAGNE_TEMPLATE) 
			this.projectMaxSections = 9;
		else if (this.projectTemplate ==  SIMULATION_TEMPLATE) 
			this.projectMaxSections = 14;
		this.projectCreator = ap.getValueString("projectCreator");
		this.projectNodeId = ap.getValueString("projectNodeId");
		this.projectSectionNotes = AlfrescoPacket.wrap(ap.getValue("projectSections"));
		this.projectSectionAssets = AlfrescoPacket.wrap(ap.getValue("projectSectionAssets"));
	}
	
	public Blob makeJSONBlob() {
		AlfrescoPacket ap = AlfrescoPacket.makePacket();
		ap.addKeyValue("projectTitle", "\"" + projectTitle + "\"");
		ap.addKeyValue("projectCreator", "\"" + projectCreator + "\"");
		ap.addKeyValue("projectTemplate", "\"" + projectTemplate + "\"");
		ap.addKeyValue("projectNotes", "\"" + projectNotes + "\"");
		ap.addKeyValue("projectLearningObjectives", "\"" + projectLearningObjectives + "\"");
		ap.addKeyValue("projectImi", "\"" + projectImi + "\"");
		ap.addKeyValue("projectTaxo", "\"" + projectTaxo + "\"");
		ap.addKeyValue("projectUsage", "\"" + projectUsage + "\"");
		ap.addKeyValue("projectNodeId", "\"" + projectNodeId + "\"");
		ap.addKeyValue("projectSections", projectSectionNotes.toJSONWrappedString());
		ap.addKeyValue("projectSectionAssets", projectSectionAssets.toJSONArrayString());
		return buildBlob(RUSSEL_MIME_TYPE, ap.toJSONString());
	}
	
	public ProjectFileModel copyProject() {
		AlfrescoPacket tempAp = AlfrescoPacket.makePacket();
		tempAp.addKeyValue("projectTitle", "\"" + this.projectTitle + "\"");
		tempAp.addKeyValue("projectCreator", "\"" + this.projectCreator + "\"");
		tempAp.addKeyValue("projectTemplate", "\"" + this.projectTemplate + "\"");
		tempAp.addKeyValue("projectNotes", "\"" + this.projectNotes + "\"");
		tempAp.addKeyValue("projectLearningObjectives", "\"" + this.projectLearningObjectives + "\"");
		tempAp.addKeyValue("projectImi", "\"" + this.projectImi + "\"");
		tempAp.addKeyValue("projectTaxo", "\"" + this.projectTaxo + "\"");
		tempAp.addKeyValue("projectUsage", "\"" + this.projectUsage + "\"");
		tempAp.addKeyValue("projectNodeId", "\"" + this.projectNodeId + "\"");
		tempAp.addKeyValue("projectSections", this.projectSectionNotes.toJSONWrappedString());
		tempAp.addKeyValue("projectSectionAssets", this.projectSectionAssets.toJSONArrayString());
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
						CommunicationHub.sendFormUpdate(CommunicationHub.getAlfrescoUploadURL(),
								pfm.projectTitle.replaceAll(" ", "_") + ".rpf", 
								CommunicationHub.ALFRESCO_STORE_TYPE + "://" + CommunicationHub.ALFRESCO_STORE_ID + "/" + pfm.projectNodeId, 
								pfm.makeJSONBlob(), 
								"russel:metaTest", 
								new AlfrescoCallback<AlfrescoPacket>() {
									@Override
									public void onSuccess(AlfrescoPacket alfrescoPacket) {
										return;
									}
									
									@Override 
									public void onFailure(Throwable caught) {
										Window.alert("Fooing Couldn't save updated project file " + caught);
									}
								});
					}
					
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Fooing couldn't retrieve new project file " + caught);
					}
				});		
		
		return;
	}
	
	public void addAsset(final String section, final String assetId, String assetFilename, String notes) {
		AlfrescoPacket ap = AlfrescoPacket.makePacket();
		ap.addKeyValue("id", assetId);
		ap.addKeyValue("fileName", assetFilename);
		ap.addKeyValue("notes", notes);		
		
		projectSectionAssets.addAsset(section, ap);
	}
	
	public void removeAsset(String section, String assetId) {
		projectSectionAssets.removeAsset(section, assetId);
	}

	public void addSectionNotes(String section, String notes) {
		projectSectionNotes.addKeyValue(section, notes);
	}

	public void updateAlfrescoAssetUsage(final String section, final String assetId, String assetFilename, final Boolean add) {
		CommunicationHub.sendHTTP(CommunicationHub.GET,
								  CommunicationHub.getAlfrescoNodeURL(assetId),
								  null,
								  false, 
								  new AlfrescoCallback<AlfrescoPacket>() {
									@Override
									public void onSuccess(AlfrescoPacket ap) {
										AlfrescoPacket apNew = updateIsdUsage(ap, projectTemplate, section, add);
										String postString = MetaBuilder.convertToMetaPacket(apNew);
//										Window.alert("Saving: "+postString);
										if (postString!=null)
											AlfrescoApi.setObjectProperties(assetId,
																			postString, 
																			new AlfrescoCallback<AlfrescoPacket>() {
																				@Override
																				public void onSuccess(final AlfrescoPacket nullPack) {
//																					Window.alert("Updated asset paradata ");
																				}
																				
																				@Override
																				public void onFailure(Throwable caught) {
																					Window.alert("Fooing failed to save metadata " + caught.getMessage());
																				}
																			});
									}
									
									@Override
									public void onFailure(Throwable caught) {
										
									}
								});
		return;
	}

	public static void importFromAlfrescoNode(String nodeId, String filename, final AlfrescoCallback<AlfrescoPacket> callback) {
		AlfrescoApi.getObjectStream(nodeId, 
									filename, 
									new AlfrescoCallback<AlfrescoPacket>() {
										@Override
										public void onSuccess(AlfrescoPacket alfrescoPacket) {
											JsArrayInteger chars = Uint8Array.createUint8Array(alfrescoPacket.getContents());
											String acc = "";
											for (int x=0;x<chars.length();x++)
												acc += (char)chars.get(x);
											callback.onSuccess(AlfrescoPacket.wrap(CommunicationHub.parseJSON(acc)));
										}
										
										@Override
										public void onFailure(Throwable caught) {
											callback.onFailure(caught);
										}
									});
	}
	
	public static native Blob buildBlob(String typ, String contents) /*-{
		if (window.BlobBuilder || window.MozBlobBuilder || window.WebKitBlobBuilder || window.OBlobBuilder || window.msBlobBuilder) {
			var bb = new (window.BlobBuilder || window.MozBlobBuilder || window.WebKitBlobBuilder || window.OBlobBuilder || window.msBlobBuilder);
			bb.append(contents);
			return bb.getBlob(typ);
		} else if (window.Blob!=undefined) {
			var bb = new window.Blob([contents], { "type": "\"" + typ + "\"" });
			return bb;
		} else {
			Window.alert("Blob building is failing");
		}
	}-*/;
	
	public static native Blob buildBlob(String typ, ArrayBuffer contents) /*-{
		if (window.BlobBuilder || window.MozBlobBuilder || window.WebKitBlobBuilder || window.OBlobBuilder || window.msBlobBuilder) {
			var bb = new (window.BlobBuilder || window.MozBlobBuilder || window.WebKitBlobBuilder || window.OBlobBuilder || window.msBlobBuilder);
			bb.append(contents);
			return bb.getBlob(typ);
		} else if (window.Blob!=undefined) {
			var bb = new window.Blob([contents], { "type": "\"" + typ + "\"" });
			return bb;
		} else {
			Window.alert("Blob building is failing");
		}
	}-*/;
	
	/* EPSS Editor / Parse existing ISD usage and report */
	public static Boolean reportIsdUsage(String nodeUsage) {
		StringTokenizer useList, tempList; 
		String useStr;
		String templateStr="";
		String strategyStr=""; 
		String countStr="";
		String msgStr="The current node is used in the following ISD situations: ";
		
		if (nodeUsage != null)  {
			useList = new StringTokenizer(nodeUsage, USAGE_DELIMITER);
			while (useList.hasMoreTokens()) {
				useStr = useList.nextToken();
				tempList = new StringTokenizer(useStr, USAGE_STRATEGY_DELIMITER); 
				if (tempList.hasMoreTokens()) {
					templateStr = tempList.nextToken();
					tempList = new StringTokenizer(tempList.nextToken(), USAGE_COUNT_DELIMITER);
					if (tempList.countTokens() == 2) {
						strategyStr = tempList.nextToken();
						countStr = tempList.nextToken();
					}
				}
				//TODO: Determine if/where we will list these for display to the user.
				msgStr = msgStr + "           Template="+templateStr+"       Strategy="+strategyStr+"       Count="+countStr; 
			}
		}
		Window.alert(msgStr);
		return true;
	}

	/* EPSS Editor / Combine a template, strategy, and count into usage data entry. Ensure that input strings don't contain delimiters. */
	public static String buildIsdUsageEntry(String template, String strategy, String count) {
		String usageStr = "";
		template.replaceAll(USAGE_STRATEGY_DELIMITER, " ").replaceAll(USAGE_COUNT_DELIMITER, " ").replaceAll(USAGE_DELIMITER, " ").trim();
		strategy.replaceAll(USAGE_STRATEGY_DELIMITER, " ").replaceAll(USAGE_COUNT_DELIMITER, " ").replaceAll(USAGE_DELIMITER, " ").trim();
		count.replaceAll(USAGE_STRATEGY_DELIMITER, " ").replaceAll(USAGE_COUNT_DELIMITER, " ").replaceAll(USAGE_DELIMITER, " ").trim();
		usageStr = template + USAGE_STRATEGY_DELIMITER + strategy + USAGE_COUNT_DELIMITER + count + USAGE_DELIMITER ;
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
		String nodeUsage = ap.getPropertyValue("@propertyDefinitionId","russel:epssStrategy");
		
		if ((nodeUsage != "") && (nodeUsage != null))  {
			useList = new StringTokenizer(nodeUsage, USAGE_DELIMITER);
			while (useList.hasMoreTokens()) {
				useStr = useList.nextToken();
				if (!found) {
					tempList = new StringTokenizer(useStr, USAGE_STRATEGY_DELIMITER); 
					if (tempList.countTokens() == 2) {
						templateStr = tempList.nextToken();
						if (templateStr == newTemplate) {
							tempList = new StringTokenizer(tempList.nextToken(), USAGE_COUNT_DELIMITER);
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
								else updatedNodeUsage = updatedNodeUsage + useStr + USAGE_DELIMITER;
							}
						}
						else updatedNodeUsage = updatedNodeUsage + useStr + USAGE_DELIMITER;
					}
				}
				else updatedNodeUsage = updatedNodeUsage + useStr + USAGE_DELIMITER;
			}
		}
		if ((!found) && (add)) {
			updatedNodeUsage = updatedNodeUsage + buildIsdUsageEntry(newTemplate, newStrategy, "1");
		}

		AlfrescoPacket apNew = AlfrescoPacket.makePacket();
		apNew = addEpssStrategyProperty(apNew,updatedNodeUsage);
//		reportIsdUsage(apNew.getValueString("russel:epssStrategy")); 
		
		return apNew;
	}
		
}