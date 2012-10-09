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

package com.eduworks.russel.ui.client.pagebuilder.screen;

import java.util.Vector;

import com.eduworks.gwt.russel.ui.client.net.AlfrescoApi;
import com.eduworks.gwt.russel.ui.client.net.AlfrescoCallback;
import com.eduworks.gwt.russel.ui.client.net.AlfrescoNullCallback;
import com.eduworks.gwt.russel.ui.client.net.AlfrescoPacket;
import com.eduworks.gwt.russel.ui.client.net.CommunicationHub;
import com.eduworks.russel.ui.client.Russel;
import com.eduworks.russel.ui.client.epss.EPSSPackBuilder;
import com.eduworks.russel.ui.client.epss.ProjectFileModel;
import com.eduworks.russel.ui.client.handler.AlfrescoSearchHandler;
import com.eduworks.russel.ui.client.handler.SearchTileHandler;
import com.eduworks.russel.ui.client.pagebuilder.HtmlTemplates;
import com.eduworks.russel.ui.client.pagebuilder.PageAssembler;
import com.eduworks.russel.ui.client.pagebuilder.ScreenTemplate;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;


public class EPSSEditScreen extends ScreenTemplate {

	private ProjectFileModel pfm;
	private Vector<String> searchTerms;
	private AlfrescoSearchHandler assetSearchHandler;
	private String activeSection;
	private String activeSectionId;
	private String activeAssetId;
	private String activeFilename;
	
	public EPSSEditScreen(ProjectFileModel incomingProject) {
		this.pfm = incomingProject; 
	}
	
	@Override
	public void display() {
		searchTerms = new Vector<String>();
		assetSearchHandler = new AlfrescoSearchHandler();
		PageAssembler.getInstance().ready(new HTML(HtmlTemplates.INSTANCE.getDetailModel().getText()));
		
		// Direct the PageAssembler to use the appropriate templates given the incomingProject type
		if (this.pfm.projectTemplate == ProjectFileModel.SIMULATION_TEMPLATE) {
			PageAssembler.getInstance().ready(new HTML(HtmlTemplates.INSTANCE.getEPSSEdit2().getText()));			
		} 
		else {
			PageAssembler.getInstance().ready(new HTML(HtmlTemplates.INSTANCE.getEPSSEdit().getText()));
		}
		
		PageAssembler.getInstance().buildContents();
		
		assetSearchHandler.hook("r-projectAssetSearch", "epssAssetPanel", AlfrescoSearchHandler.ASSET_TYPE);
		
		PageAssembler.attachHandler("epssUpdate", Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {	
																	@Override
																	public void onEvent(Event event) {
																		new Timer() {
																			@Override
																			public void run() {
																				saveProject();
																			}
																		}.schedule(75);
																	}
																 });
		
		PageAssembler.attachHandler("epssSaveProperties", Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {
																			@Override
																			public void onEvent(Event event) {
																				saveProject();
																			}															
																		});
		
		PageAssembler.attachHandler("epssSaveAs", Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {
																		@Override
																		public void onEvent(Event event) {
																			Window.alert(Russel.INCOMPLETE_FEATURE_MESSAGE);
																		}		
																	  });
		
		PageAssembler.attachHandler("section1", Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {
																	@Override
																	public void onEvent(Event event) {
																		fillSectionNotes("section1");
																	}
															   });
		
		PageAssembler.attachHandler("section2", Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {
																	@Override
																	public void onEvent(Event event) {
																		fillSectionNotes("section2");
																	}
															   });
		
		PageAssembler.attachHandler("section3", Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {
																	@Override
																	public void onEvent(Event event) {
																		fillSectionNotes("section3");
																	}
															   });
		
		PageAssembler.attachHandler("section4", Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {
																	@Override
																	public void onEvent(Event event) {
																		fillSectionNotes("section4");
																	}
															   });
		
		PageAssembler.attachHandler("section5", Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {
																	@Override
																	public void onEvent(Event event) {
																		fillSectionNotes("section5");
																	}
															   });
		
		PageAssembler.attachHandler("section6", Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {
																	@Override
																	public void onEvent(Event event) {
																		fillSectionNotes("section6");
																	}
															   });
		
		PageAssembler.attachHandler("section7", Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {
																	@Override
																	public void onEvent(Event event) {
																		fillSectionNotes("section7");
																	}
															   });
		
		PageAssembler.attachHandler("section8", Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {
																	@Override
																	public void onEvent(Event event) {
																		fillSectionNotes("section8");
																	}
															   });
		
		PageAssembler.attachHandler("section9", Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {
																	@Override
																	public void onEvent(Event event) {
																		fillSectionNotes("section9");
																	}
															   });
		
		PageAssembler.attachHandler("section10", Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {
			@Override
			public void onEvent(Event event) {
				fillSectionNotes("section10");
			}
	   });

		PageAssembler.attachHandler("section11", Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {
			@Override
			public void onEvent(Event event) {
				fillSectionNotes("section11");
			}
	   });

		PageAssembler.attachHandler("section12", Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {
			@Override
			public void onEvent(Event event) {
				fillSectionNotes("section12");
			}
	   });

		PageAssembler.attachHandler("section13", Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {
			@Override
			public void onEvent(Event event) {
				fillSectionNotes("section13");
			}
	   });

		PageAssembler.attachHandler("section14", Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {
			@Override
			public void onEvent(Event event) {
				fillSectionNotes("section14");
			}
	   });

		PageAssembler.attachHandler("epssTerm1", Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {
																	@Override
																	public void onEvent(Event event) {
																		toggleSearchTerms("epssTerm1", ((Anchor)PageAssembler.elementToWidget("epssTerm1", PageAssembler.A)).getText());
																	}
																});
		
		PageAssembler.attachHandler("epssTerm2", Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {
																	@Override
																	public void onEvent(Event event) {
																		toggleSearchTerms("epssTerm2", ((Anchor)PageAssembler.elementToWidget("epssTerm2", PageAssembler.A)).getText());
																	}
																});
		
		PageAssembler.attachHandler("epssTerm3", Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {
																	@Override
																	public void onEvent(Event event) {
																		toggleSearchTerms("epssTerm3", ((Anchor)PageAssembler.elementToWidget("epssTerm3", PageAssembler.A)).getText());
																	}
																});
		
		
		PageAssembler.attachHandler("epssTerm4", Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {
																	@Override
																	public void onEvent(Event event) {
																		toggleSearchTerms("epssTerm4", ((Anchor)PageAssembler.elementToWidget("epssTerm4", PageAssembler.A)).getText());
																	}
																});
		
		
		PageAssembler.attachHandler("epssTerm5", Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {
																	@Override
																	public void onEvent(Event event) {
																		toggleSearchTerms("epssTerm5", ((Anchor)PageAssembler.elementToWidget("epssTerm5", PageAssembler.A)).getText());
																	}
																});
		
		PageAssembler.attachHandler("epssTerm6", Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {
																	@Override
																	public void onEvent(Event event) {
																		toggleSearchTerms("epssTerm6", ((Anchor)PageAssembler.elementToWidget("epssTerm6", PageAssembler.A)).getText());
																	}
																});
		
		PageAssembler.attachHandler("epssTerm7", Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {
			@Override
			public void onEvent(Event event) {
				toggleSearchTerms("epssTerm7", ((Anchor)PageAssembler.elementToWidget("epssTerm7", PageAssembler.A)).getText());
			}
		});

		PageAssembler.attachHandler("epssTerm8", Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {
			@Override
			public void onEvent(Event event) {
				toggleSearchTerms("epssTerm8", ((Anchor)PageAssembler.elementToWidget("epssTerm8", PageAssembler.A)).getText());
			}
		});

		PageAssembler.attachHandler("epssExportSCORM", Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {
													   	@Override
													   	public void onEvent(Event event) {
													   		saveProject();
													   		EPSSPackBuilder epb = new EPSSPackBuilder(pfm);
													   		epb.buildPack();
													   		epb.addSCORMFile();
													   	}
													   });
		
		PageAssembler.attachHandler("epssActiveAddAsset", Event.ONCHANGE, new AlfrescoNullCallback<AlfrescoPacket>() {
																			@Override
																			public void onEvent(Event event) {
																				DOM.getElementById("epssUpdate").removeClassName("white");
																				DOM.getElementById("epssUpdate").addClassName("blue");
																				DOM.getElementById("r-save-alert").removeClassName("hide");
																				Hidden activeAssetFilename = ((Hidden)PageAssembler.elementToWidget("epssActiveAddAsset", PageAssembler.HIDDEN));
																				activeAssetId = activeAssetFilename.getValue().substring(0, activeAssetFilename.getValue().indexOf(","));
																				activeFilename = activeAssetFilename.getValue().substring(activeAssetFilename.getValue().indexOf(",")+1);
																				if (!isAssetInSection())
																					pfm.addAsset(activeSection, activeAssetId, activeFilename, "");
																				else {
																					pfm.addAsset(activeSection, activeAssetId, activeFilename, getAssetNotesInSection());
																					((TextBox)PageAssembler.elementToWidget("inputDevNotes", PageAssembler.TEXT)).setText(getAssetNotesInSection());
																				}
																				DOM.getElementById(activeSectionId).removeClassName("empty");
																			}
																		});
		
		PageAssembler.attachHandler("epssActiveRemoveAsset", Event.ONCHANGE, new AlfrescoNullCallback<AlfrescoPacket>() {
																			@Override
																			public void onEvent(Event event) {
																				DOM.getElementById("epssUpdate").removeClassName("white");
																				DOM.getElementById("epssUpdate").addClassName("blue");
																				DOM.getElementById("r-save-alert").removeClassName("hide");
																				activeAssetId = ((Hidden)PageAssembler.elementToWidget("epssActiveRemoveAsset", PageAssembler.HIDDEN)).getValue();
																				pfm.removeAsset(activeSection, activeAssetId);
																			}
																		});

		fillData();
	}
	
	private void buildAssetTile (String nodeId, String filename) {
		Element td = DOM.createTD();
		td.setInnerHTML(HtmlTemplates.INSTANCE.getEPSSNoteAssetObjectWidget().getText());
		Vector<String> iDs = PageAssembler.getInstance().merge("epssCurrentSection", "x", td);
		String idPrefix = iDs.firstElement().substring(0,iDs.firstElement().indexOf("-"));
		td.setId(idPrefix + "-assetNote");
		AlfrescoPacket ap = AlfrescoPacket.makePacket();
		if (nodeId.startsWith("\""))
			nodeId = nodeId.substring(1, nodeId.length()-1);
		if (filename.startsWith("\""))
			filename = filename.substring(1, filename.length()-1);
		ap.addKeyValue("id", nodeId);
		ap.addKeyValue("fileName", filename);
		new SearchTileHandler(null, idPrefix, AlfrescoSearchHandler.NOTES_TYPE, ap).refreshTile(null);
	}
	
	private boolean isAssetInSection() {
		boolean acc = false;
		if (pfm.projectSectionAssets!=null)
			if (pfm.projectSectionAssets.hasKey(activeSection)) {
				JsArray<AlfrescoPacket> assets = pfm.projectSectionAssets.getValue(activeSection).cast();
				for (int x=0;x<assets.length();x++)
					if (!acc&&assets.get(x).getNodeId() == activeAssetId)
						acc = true;
			}
		return acc;
	}
	
	private String getAssetNotesInSection() {
		if (pfm.projectSectionAssets!=null)
			if (pfm.projectSectionAssets.hasKey(activeSection)) {
				JsArray<AlfrescoPacket> assets = pfm.projectSectionAssets.getValue(activeSection).cast();
				for (int x=0;x<assets.length();x++)
					if (assets.get(x).getNodeId() == activeAssetId)
						return assets.get(x).getValueString("notes"); 
			}
		return "";
	}

	private void fillSectionNotes(final String elementId) {
		((TextBox)PageAssembler.elementToWidget("inputDevNotes", PageAssembler.TEXT)).setText("");
		((TextBox)PageAssembler.elementToWidget("inputSectionNotes", PageAssembler.TEXT)).setText("");
		DOM.getElementById("epssCurrentSection").setInnerHTML(HtmlTemplates.INSTANCE.getEPSSEditSectionWidgets().getText());
		PageAssembler.runCustomJSHooks();
		activeSection = DOM.getElementById(elementId).getInnerText();
		activeSectionId = elementId;
		PageAssembler.attachHandler("inputSectionNotes", Event.ONBLUR, new AlfrescoNullCallback<AlfrescoPacket>() {
																			@Override
																			public void onEvent(Event event) {
																				pfm.addSectionNotes(activeSection, cleanString(((TextBox)PageAssembler.elementToWidget("inputSectionNotes", PageAssembler.TEXT)).getText()));
																				DOM.getElementById("epssUpdate").removeClassName("white");
																				DOM.getElementById("epssUpdate").addClassName("blue");
																				DOM.getElementById("r-save-alert").removeClassName("hide");
																				DOM.getElementById(elementId).removeClassName("empty");
																			}
																		});
		
		PageAssembler.attachHandler("inputDevNotes", Event.ONBLUR, new AlfrescoNullCallback<AlfrescoPacket>() {
																		@Override
																		public void onEvent(Event event) {
																			Hidden activeAssetFilename = ((Hidden)PageAssembler.elementToWidget("epssActiveAddAsset", PageAssembler.HIDDEN));
																			activeAssetId = activeAssetFilename.getValue().substring(0, activeAssetFilename.getValue().indexOf(","));
																			activeFilename = activeAssetFilename.getValue().substring(activeAssetFilename.getValue().indexOf(",")+1);
																			pfm.addAsset(activeSection, activeAssetId, activeFilename, cleanString(((TextBox)PageAssembler.elementToWidget("inputDevNotes", PageAssembler.TEXT)).getText()));
																			DOM.getElementById("epssUpdate").removeClassName("white");
																			DOM.getElementById("epssUpdate").addClassName("blue");
																			DOM.getElementById("r-save-alert").removeClassName("hide");
																			DOM.getElementById(elementId).removeClassName("empty");
																		}
																	});
		
		if (pfm.projectSectionNotes!=null)
			((TextBox)PageAssembler.elementToWidget("inputSectionNotes", PageAssembler.TEXT)).setText(pfm.projectSectionNotes.getValueString(activeSection));
		
		if (pfm.projectSectionAssets!=null) {
			if (pfm.projectSectionAssets.hasKey(activeSection)) {
				JsArray<AlfrescoPacket> assets = pfm.projectSectionAssets.getValue(activeSection).cast();
				for (int x=0;x<assets.length();x++)
					buildAssetTile(assets.get(x).getNodeId(), assets.get(x).getFilename());
			}
			PageAssembler.runCustomJSHooks();
		}
	}
	
	private void toggleSearchTerms(String eId, String term) {
		if (!searchTerms.contains("\""+term+"\"")) {
			searchTerms.add("\""+term+"\"");
			DOM.getElementById(eId).removeClassName("white");
			DOM.getElementById(eId).addClassName("blue");
		} else {
			searchTerms.remove("\""+term+"\"");
			DOM.getElementById(eId).removeClassName("blue");
			DOM.getElementById(eId).addClassName("white");
		}
		
		String enteredTerm = ((TextBox)PageAssembler.elementToWidget("r-projectAssetSearch", PageAssembler.TEXT)).getText().trim();
		
		String accQuery = "";
		if (searchTerms.size()!=0) {
			for (int x=0;x<searchTerms.size();x++) {
				if (enteredTerm=="")
					accQuery += " OR " + searchTerms.get(x);
				else
					accQuery += " OR (" + AlfrescoSearchHandler.removeExtraANDS(enteredTerm) + " AND " + searchTerms.get(x) + ")";
			}
			accQuery = accQuery.substring(" OR ".length()).trim();
		} else accQuery = enteredTerm;
		
		if (accQuery.trim()!="")
			assetSearchHandler.forceSearch("(" + accQuery + ")");
	}
	
	private String cleanString(String dirty) {
		if (dirty==null)
			return "";
		else return dirty.replaceAll("\"", "'").replaceAll("[\r\n]", " ").trim();
	}
	
	public void saveProject() {
		DOM.getElementById("epssUpdate").addClassName("white");
		DOM.getElementById("epssUpdate").removeClassName("blue");
		DOM.getElementById("r-save-alert").addClassName("hide");
		pfm.projectTitle = ((Anchor)PageAssembler.elementToWidget("projectTitleText", PageAssembler.A)).getText();
		if (pfm.projectTitle==null||pfm.projectTitle.equalsIgnoreCase("Click here to add a title"))
			pfm.projectTitle = "DefaultName";
		pfm.projectCreator = AlfrescoApi.username;
		pfm.projectNotes = cleanString(((TextBox)PageAssembler.elementToWidget("epssProjectNotes", PageAssembler.TEXT)).getText());
		pfm.projectLearningObjectives = cleanString(((TextBox)PageAssembler.elementToWidget("epssProjectObjectives", PageAssembler.TEXT)).getText());
		int imiIndex = ((ListBox)PageAssembler.elementToWidget("projectImiLevel", PageAssembler.SELECT)).getSelectedIndex();
		int taxIndex = ((ListBox)PageAssembler.elementToWidget("projectBlooms", PageAssembler.SELECT)).getSelectedIndex();
		if (imiIndex!=-1)
			pfm.projectImi = imiIndex + "," +((ListBox)PageAssembler.elementToWidget("projectImiLevel", PageAssembler.SELECT)).getItemText(imiIndex);
		if (taxIndex!=-1) 
			pfm.projectTaxo = taxIndex + "," + ((ListBox)PageAssembler.elementToWidget("projectBlooms", PageAssembler.SELECT)).getItemText(taxIndex);
		
		if (pfm.projectNodeId==null)
			CommunicationHub.sendForm(CommunicationHub.getAlfrescoUploadURL(),
									  pfm.projectTitle.replaceAll(" ", "_") + ".rpf",
									  AlfrescoApi.currentDirectoryId, 
									  pfm.makeJSONBlob(), 
									  "russel:metaTest", 
									  new AlfrescoCallback<AlfrescoPacket>() {
										@Override
										public void onSuccess(AlfrescoPacket alfrescoPacket) {
											pfm.projectNodeId = alfrescoPacket.getNodeId();
											CommunicationHub.sendFormUpdate(CommunicationHub.getAlfrescoUploadURL(),
																			pfm.projectTitle.replaceAll(" ", "_") + ".rpf", 
																			CommunicationHub.ALFRESCO_STORE_TYPE + "://" + CommunicationHub.ALFRESCO_STORE_ID + "/" + alfrescoPacket.getNodeId(), 
																			pfm.makeJSONBlob(), 
																			"russel:metaTest", 
																			new AlfrescoCallback<AlfrescoPacket>() {
																				@Override
																				public void onSuccess(AlfrescoPacket alfrescoPacket) {
																					
																				}
																				
																				@Override 
																				public void onFailure(Throwable caught) {
																					
																				}
																			});
										}
										
										@Override
										public void onFailure(Throwable caught) {
											
										}
									 });
		else
			CommunicationHub.sendFormUpdate(CommunicationHub.getAlfrescoUploadURL(),
											pfm.projectTitle.replaceAll(" ", "_") + ".rpf", 
											CommunicationHub.ALFRESCO_STORE_TYPE + "://" + CommunicationHub.ALFRESCO_STORE_ID + "/" + pfm.projectNodeId, 
											pfm.makeJSONBlob(), 
											"russel:metaTest", 
											new AlfrescoCallback<AlfrescoPacket>() {
												@Override
												public void onSuccess(AlfrescoPacket alfrescoPacket) {
													fillData();
												}
												
												@Override 
												public void onFailure(Throwable caught) {
													Window.alert("Fooing Couldn't save project file " + caught);
												}
											});
		
		PageAssembler.closePopup("epssSaveProperties");
	}
	
	private void fillData() {
		((TextBox)PageAssembler.elementToWidget("epssProjectNotes", PageAssembler.TEXT)).setText(pfm.projectNotes);
		((TextBox)PageAssembler.elementToWidget("epssProjectObjectives", PageAssembler.TEXT)).setText(pfm.projectLearningObjectives);
		((Anchor)PageAssembler.elementToWidget("projectTitleText", PageAssembler.A)).setText(pfm.projectTitle);
		ListBox lb = ((ListBox)PageAssembler.elementToWidget("projectImiLevel", PageAssembler.SELECT));
		if (pfm.projectImi.indexOf(",")!=-1)
			lb.setSelectedIndex(Integer.valueOf(pfm.projectImi.substring(0,pfm.projectImi.indexOf(","))));
		lb = ((ListBox)PageAssembler.elementToWidget("projectBlooms", PageAssembler.SELECT));
		if (pfm.projectImi.indexOf(",")!=-1)
			lb.setSelectedIndex(Integer.valueOf(pfm.projectTaxo.substring(0,pfm.projectImi.indexOf(","))));
		if (pfm.projectSectionNotes!=null)
			for (int x = 1;x<10;x++)
				if (pfm.projectSectionNotes.hasKey(DOM.getElementById("section"+x).getInnerText()))
					DOM.getElementById("section"+x).removeClassName("empty");
		if (pfm.projectSectionAssets!=null)
			for (int x=1;x<10;x++) { 
				String sectionName = DOM.getElementById("section" + x).getInnerText();
				if (pfm.projectSectionAssets.hasKey(sectionName)) {
					JsArray<AlfrescoPacket> assets = pfm.projectSectionAssets.getValue(sectionName).cast(); 
					if (assets.length()>0)
						DOM.getElementById("section" + x).removeClassName("empty");
				}
			}	
	}
}