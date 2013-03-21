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

package com.eduworks.russel.ui.client.pagebuilder.screen;

import java.util.Vector;

import com.eduworks.gwt.client.net.api.AlfrescoApi;
import com.eduworks.gwt.client.net.api.AlfrescoURL;
import com.eduworks.gwt.client.net.callback.AlfrescoCallback;
import com.eduworks.gwt.client.net.callback.EventCallback;
import com.eduworks.gwt.client.net.packet.AlfrescoPacket;
import com.eduworks.gwt.client.net.packet.StatusPacket;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.gwt.client.pagebuilder.ScreenTemplate;
import com.eduworks.gwt.client.util.Browser;
import com.eduworks.russel.ui.client.Russel;
import com.eduworks.russel.ui.client.epss.EPSSPackBuilder;
import com.eduworks.russel.ui.client.epss.ProjectFileModel;
import com.eduworks.russel.ui.client.handler.AlfrescoSearchHandler;
import com.eduworks.russel.ui.client.handler.StatusWindowHandler;
import com.eduworks.russel.ui.client.handler.TileHandler;
import com.eduworks.russel.ui.client.pagebuilder.HtmlTemplates;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;


public class EPSSEditScreen extends ScreenTemplate {

	public static ProjectFileModel pfmNow, pfmLast;
	private static Vector<String> searchTerms;
	private static AlfrescoSearchHandler assetSearchHandler;
	private static String activeSection;
	private static String activeSectionId;
	private static String activeAssetId;
	private static String activeFilename;

	public void lostFocus() {
		assetSearchHandler.stop();
	}
	
	private final native String putObjectives(String s, String id) /*-{
		return $wnd.listObjectives(s, id);
	}-*/;

	private final native String getObjectives(String id) /*-{
		return $wnd.compressObjectives(id);
	}-*/;
	
	public EPSSEditScreen(ProjectFileModel incomingProject) {
		EPSSEditScreen.pfmNow = incomingProject; 
		EPSSEditScreen.pfmLast = pfmNow.copyProject(); 
	}
	
	private native void insertSection0(Element section, String title, int index) /*-{
		var cell = document.createElement("td");
		section.appendChild(cell);
		cell.innerText = title;
		cell.className = "templateSection empty";
		cell.id = "section" + index;
	}-*/;
	
	public void populateTemplate() {
		DOM.getElementById("template-name").setInnerText(pfmNow.projectTemplateName);
		
		for (int sectionIndex=0;sectionIndex<pfmNow.projectSections.length();sectionIndex++)
			insertSection0(DOM.getElementById("template-sections-area"),  
						  pfmNow.projectSections.get(sectionIndex).getValueString(ProjectFileModel.TEMPLATE_SECTION_SHORT_TITLE),
						  (sectionIndex+1));
	}
	
	@Override
	public void display() {
		PageAssembler.ready(new HTML(HtmlTemplates.INSTANCE.getEPSSEdit().getText()));
		PageAssembler.buildContents();
		populateTemplate();
		assetSearchHandler = new AlfrescoSearchHandler();
		
		assetSearchHandler.hook("r-projectAssetSearch", "epssAssetPanel", AlfrescoSearchHandler.ASSET_TYPE);
		
		PageAssembler.attachHandler("epssExportButton", Event.ONCLICK, new EventCallback() {
																		@Override
																		public void onEvent(Event event) {
																			RootPanel.get("epssDownloadArea").clear();
																		}
																   });
		
		PageAssembler.attachHandler("epssUpdate", Event.ONCLICK, new EventCallback() {	
																	@Override
																	public void onEvent(Event event) {
																		saveProject();
																	}
																 });
		
		PageAssembler.attachHandler("epssCancel", Event.ONCLICK, new EventCallback() {	
																	@Override
																	public void onEvent(Event event) {
																		FeatureScreen fs = new FeatureScreen();
																		fs.featureType = FeatureScreen.PROJECTS_TYPE;
																		Russel.view.loadScreen(fs, true);
																	}
																 });

		PageAssembler.attachHandler("epssSaveProperties", Event.ONCLICK, new EventCallback() {
																	@Override
																	public void onEvent(Event event) {
																		saveProject();
																   		PageAssembler.closePopup("projectProperties");
																	}															
																});
		
		PageAssembler.attachHandler("epssCancelProperties", Event.ONCLICK, new EventCallback() {
																	@Override
																	public void onEvent(Event event) {
																		fillPropData();
																   		PageAssembler.closePopup("projectProperties");
																	}													
																});
		
		PageAssembler.attachHandler("epssCloseProperties", Event.ONCLICK, new EventCallback() {
																	@Override
																	public void onEvent(Event event) {
																		fillPropData();
																   		PageAssembler.closePopup("projectProperties");
																	}															
																}); 
		
		PageAssembler.attachHandler("epssSaveAs", Event.ONCLICK, new EventCallback() {
																@Override
																public void onEvent(Event event) {
																	Window.alert(Russel.INCOMPLETE_FEATURE_MESSAGE);
																}		
															  });
		
		for (int sectionIndex=1;sectionIndex<=pfmNow.projectSections.length();sectionIndex++) {
			final int lockedSectionIndex = sectionIndex;
			PageAssembler.attachHandler("section" + lockedSectionIndex, 
										Event.ONCLICK, 
										new EventCallback() {
											@Override
											public void onEvent(Event event) {
												fillSectionNotes("section" + lockedSectionIndex, lockedSectionIndex-1);
											}
									   });
		}
		
		PageAssembler.attachHandler("epssExportSCORM", Event.ONCLICK, new EventCallback() {
															   	@Override
															   	public void onEvent(Event event) {
															   		saveProject();
															   		EPSSPackBuilder epb = new EPSSPackBuilder(pfmNow);
														   			AlfrescoApi.exportZipPackage(pfmNow.projectTitle.replaceAll(" ", "_") + ".zip", 
														   										 epb.buildPackIE(), 
														   										 new AlfrescoCallback<AlfrescoPacket>() {
														   											@Override
														   											public void onFailure(Throwable caught) {
														   												StatusWindowHandler.createMessage(StatusWindowHandler.getZipExportMessageError(pfmNow.projectTitle.replaceAll(" ", "_") + ".zip"), 
														   																			  	  StatusPacket.ALERT_ERROR);
														   												RootPanel.get("epssDownloadArea").clear();
														   											}
														   											
														   											@Override
														   											public void onSuccess(AlfrescoPacket zipPack) {
														   												if (Browser.isIE()) {
															   												Frame avoidPopup = new Frame();
															   												avoidPopup.addStyleName("hidden");
															   												avoidPopup.setUrl(AlfrescoURL.getObjectStreamAndDelete(zipPack.getNodeId(), 
					   																								 		  pfmNow.projectTitle.replaceAll(" ", "_") + ".zip"));
															   												RootPanel.get("epssDownloadArea").add(avoidPopup);
														   												} else 
														   													Window.open(AlfrescoURL.getObjectStreamAndDelete(zipPack.getNodeId(), 
														   																								 	 pfmNow.projectTitle.replaceAll(" ", "_") + ".zip"),
				   																								 	    "_blank",
														   																"");
														   												StatusWindowHandler.createMessage(StatusWindowHandler.getZipExportMessageDone(pfmNow.projectTitle.replaceAll(" ", "_") + ".zip"), 
														   																				  StatusPacket.ALERT_SUCCESS);
														   												RootPanel.get("epssDownloadArea").clear();
														   											}
														   										 });
															   		
															   		RootPanel.get("epssDownloadArea").add(new Image("images/orbit/loading.gif"));
															   	}
															   });
		
		PageAssembler.attachHandler("epssActiveAddAsset", Event.ONCHANGE, new EventCallback() {
																	@Override
																	public void onEvent(Event event) {
																		addAssetTrigger();
																	}
																});
		
		PageAssembler.attachHandler("epssActiveRemoveAsset", Event.ONCHANGE, new EventCallback() {
																	@Override
																	public void onEvent(Event event) {
																		removeAssetTrigger();
																	}
																});

		fillData();
	}
	
	public static void removeAssetTrigger() {
		DOM.getElementById("epssUpdate").removeClassName("white");
		DOM.getElementById("epssUpdate").addClassName("blue");
		DOM.getElementById("r-save-alert").removeClassName("hide");
		activeAssetId = ((Hidden)PageAssembler.elementToWidget("epssActiveRemoveAsset", PageAssembler.HIDDEN)).getValue();
		pfmNow.removeAsset(activeSection, activeAssetId);
	}

	public static void addAssetTrigger() {
		Hidden activeAssetFilename = ((Hidden)PageAssembler.elementToWidget("epssActiveAddAsset", PageAssembler.HIDDEN));
		activeAssetId = activeAssetFilename.getValue().substring(0, activeAssetFilename.getValue().indexOf(","));
		activeFilename = activeAssetFilename.getValue().substring(activeAssetFilename.getValue().indexOf(",")+1);
		if (!isAssetInSection())
			pfmNow.addAsset(activeSection, activeAssetId, activeFilename, "");
		else {
			pfmNow.addAsset(activeSection, activeAssetId, activeFilename, getAssetNotesInSection());
			((TextBox)PageAssembler.elementToWidget("inputDevNotes", PageAssembler.TEXT)).setText(getAssetNotesInSection());
		}
		DOM.getElementById(activeSectionId).removeClassName("empty");
	}
	
	private void buildAssetTile (String nodeId, String filename) {
		Element td = DOM.createTD();
		td.appendChild(new HTML(HtmlTemplates.INSTANCE.getEPSSNoteAssetObjectWidget().getText()).getElement().getChild(0));
		Vector<String> iDs = PageAssembler.merge("epssCurrentSection", "x", td);
		String idPrefix = iDs.firstElement().substring(0,iDs.firstElement().indexOf("-"));
		td.setId(idPrefix + "-assetNote");
		AlfrescoPacket ap = AlfrescoPacket.makePacket();
		ap.addKeyValue("id", nodeId);
		ap.addKeyValue("fileName", filename);
		new TileHandler(null, idPrefix, AlfrescoSearchHandler.NOTES_TYPE, ap).refreshTile(null);
	}
	
	private static boolean isAssetInSection() {
		boolean acc = false;
		if (pfmNow.projectSectionAssets!=null)
			if (pfmNow.projectSectionAssets.hasKey(activeSection)) {
				JsArray<AlfrescoPacket> assets = pfmNow.projectSectionAssets.getValue(activeSection).cast();
				for (int x=0;x<assets.length();x++)
					if (!acc&&assets.get(x).getNodeId() == activeAssetId)
						acc = true;
			}
		return acc;
	}
	
	private static String getAssetNotesInSection() {
		if (pfmNow.projectSectionAssets!=null)
			if (pfmNow.projectSectionAssets.hasKey(activeSection)) {
				JsArray<AlfrescoPacket> assets = pfmNow.projectSectionAssets.getValue(activeSection).cast();
				for (int x=0;x<assets.length();x++)
					if (assets.get(x).getNodeId() == activeAssetId)
						return assets.get(x).getValueString("notes"); 
			}
		return "";
	}
	
	private native void mergeSection(Element section, Element sectionTools) /*-{
		var sectionChildCount = sectionTools.children.length;
		var childStorage = [];
		for (var sectionIndex=0;sectionIndex<sectionChildCount;sectionIndex++) {
			if (sectionTools.children[sectionIndex].id!="") {
				var cell = document.createElement('td');
				section.appendChild(cell);
				childStorage.push([cell, sectionTools.children[sectionIndex]]);
			}
		}
		for (var childIndex=0;childIndex<childStorage.length;childIndex++)
			childStorage[childIndex][0].appendChild(childStorage[childIndex][1]);
	}-*/;

	private void fillSectionNotes(final String elementId, final int sectionIndex) {
		((TextBox)PageAssembler.elementToWidget("inputSectionNotes", PageAssembler.TEXT)).setText("");
		((TextBox)PageAssembler.elementToWidget("inputDevNotes", PageAssembler.TEXT)).setText("");
		Element currentSection = DOM.getElementById("epssCurrentSection");
		searchTerms = new Vector<String>();
		int elementCount = currentSection.getChildCount();
		for (int elementIndex=0;elementIndex<elementCount;elementIndex++)
			currentSection.getChild(0).removeFromParent();
		mergeSection(currentSection, (Element)new HTML(HtmlTemplates.INSTANCE.getEPSSEditSectionWidgets().getText()).getElement());
		
		activeSection = DOM.getElementById(elementId).getInnerText();
		activeSectionId = elementId;
		PageAssembler.attachHandler("inputSectionNotes", Event.ONCHANGE, new EventCallback() {
																			@Override
																			public void onEvent(Event event) {
																				pfmNow.addSectionNotes(activeSection, cleanString(((TextBox)PageAssembler.elementToWidget("inputSectionNotes", PageAssembler.TEXT)).getText()));
																				DOM.getElementById("epssUpdate").removeClassName("white");
																				DOM.getElementById("epssUpdate").addClassName("blue");
																				DOM.getElementById("r-save-alert").removeClassName("hide");
																				DOM.getElementById(elementId).removeClassName("empty");
																			}
																		});
		
		PageAssembler.attachHandler("inputDevNotes", Event.ONCHANGE, new EventCallback() {
																		@Override
																		public void onEvent(Event event) {
																			Hidden activeAssetFilename = ((Hidden)PageAssembler.elementToWidget("epssActiveAddAsset", PageAssembler.HIDDEN));
																			activeAssetId = activeAssetFilename.getValue().substring(0, activeAssetFilename.getValue().indexOf(","));
																			activeFilename = activeAssetFilename.getValue().substring(activeAssetFilename.getValue().indexOf(",")+1);
																			pfmNow.addAsset(activeSection, activeAssetId, activeFilename, cleanString(((TextBox)PageAssembler.elementToWidget("inputDevNotes", PageAssembler.TEXT)).getText()));
																			DOM.getElementById("epssUpdate").removeClassName("white");
																			DOM.getElementById("epssUpdate").addClassName("blue");
																			DOM.getElementById("r-save-alert").removeClassName("hide");
																			DOM.getElementById(elementId).removeClassName("empty");
																		}
																	});
		
		Element searchArea = DOM.getElementById("searchTerms");
		int termCount = searchArea.getChildCount();
		for (int termIndex=0;termIndex<termCount;termIndex++)
			searchArea.removeChild(searchArea.getChild(0));
		String[] searchTerms = pfmNow.projectSections.get(sectionIndex).getValueString(ProjectFileModel.TEMPLATE_SECTION_TERMS).split(", ");
		for (int termsIndex=0;termsIndex<searchTerms.length;termsIndex++)
			PageAssembler.merge("searchTerms", 
								"x", 
								(Element)new HTML("<a id=\"epssTerm" + (termsIndex+1) + "\" class=\"searchTerm small white button finger\">" + 
										searchTerms[termsIndex] + "</a>").getElement().getChild(0));
		DOM.getElementById("helptext-asset").setInnerText(pfmNow.projectSections.get(sectionIndex).getValueString(ProjectFileModel.TEMPLATE_SECTION_GUIDENCE));
		
		for (int termIndex=1;termIndex<=searchTerms.length;termIndex++) {
			final int lockedTermIndex = termIndex;
			PageAssembler.attachHandler("epssTerm" + lockedTermIndex, 
										Event.ONCLICK, 
										new EventCallback() {
											@Override
											public void onEvent(Event event) {
												toggleSearchTerms("epssTerm" + lockedTermIndex, 
																  ((Anchor)PageAssembler.elementToWidget("epssTerm" + lockedTermIndex, PageAssembler.A)).getText());
											}
										});	
		}
		
		if (pfmNow.projectSectionNotes!=null)
			((TextBox)PageAssembler.elementToWidget("inputSectionNotes", PageAssembler.TEXT)).setText(pfmNow.projectSectionNotes.getValueString(activeSection));
		
		if (pfmNow.projectSectionAssets!=null) {
			if (pfmNow.projectSectionAssets.hasKey(activeSection)) {
				JsArray<AlfrescoPacket> assets = pfmNow.projectSectionAssets.getValue(activeSection).cast();
				for (int x=0;x<assets.length();x++)
					buildAssetTile(assets.get(x).getNodeId(), assets.get(x).getFilename());
			}
			PageAssembler.runCustomJSHooks();
		}
	}
	
	private void toggleSearchTerms(String eId, String term) {
		if (!searchTerms.contains(term))
			searchTerms.add(term);
		else
			searchTerms.remove(term);
		
		String accQuery = buildQueryString(); 
		if (accQuery.trim()!="")
			assetSearchHandler.forceSearch(accQuery);
	}
	
	public static String buildQueryString() {
		String enteredTerm = ((TextBox)PageAssembler.elementToWidget("r-projectAssetSearch", PageAssembler.TEXT)).getText().trim();
		
		String accQuery = "";
		if (searchTerms.size()!=0) {
			for (int x=0;x<searchTerms.size();x++) {
				if (AlfrescoSearchHandler.cleanQuery(enteredTerm) =="")
					accQuery += " OR ALL:\"" + searchTerms.get(x) + "\"";
				else
					accQuery += " OR (\"" + AlfrescoSearchHandler.cleanQuery(enteredTerm) + "\" ALL:\"" + searchTerms.get(x) + "\")";
			}
			accQuery = accQuery.substring(" OR ".length()).trim();
		} else accQuery = enteredTerm;
		
		return accQuery.trim();
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
		pfmNow.projectTitle = ((Anchor)PageAssembler.elementToWidget("projectTitleText", PageAssembler.A)).getText();
		if (pfmNow.projectTitle==null||pfmNow.projectTitle.equalsIgnoreCase("Click here to add a title"))
			pfmNow.projectTitle = "DefaultName";
		pfmNow.projectCreator = AlfrescoApi.username;
		pfmNow.projectNotes = cleanString(((TextBox)PageAssembler.elementToWidget("epssProjectNotes", PageAssembler.TEXT)).getText());
		pfmNow.projectLearningObjectives = cleanString(getObjectives("project-objective-list"));
		int imiIndex = ((ListBox)PageAssembler.elementToWidget("projectImiLevel", PageAssembler.SELECT)).getSelectedIndex();
		int taxIndex = ((ListBox)PageAssembler.elementToWidget("projectBlooms", PageAssembler.SELECT)).getSelectedIndex();
		int usageIndex = ((ListBox)PageAssembler.elementToWidget("projectParadata", PageAssembler.SELECT)).getSelectedIndex();
		if (imiIndex!=-1)
			pfmNow.projectImi = imiIndex + "," +((ListBox)PageAssembler.elementToWidget("projectImiLevel", PageAssembler.SELECT)).getItemText(imiIndex);
		if (taxIndex!=-1) 
			pfmNow.projectTaxo = taxIndex + "," + ((ListBox)PageAssembler.elementToWidget("projectBlooms", PageAssembler.SELECT)).getItemText(taxIndex);
		if (usageIndex!=-1) 
			pfmNow.projectUsage = usageIndex + "," + ((ListBox)PageAssembler.elementToWidget("projectParadata", PageAssembler.SELECT)).getItemText(usageIndex);
		
		// Look through each section's assets in the current project file and compare with last saved project file
		// If differences are found, then update the ISD strategy usage of affected nodes directly
		if ((pfmNow.projectSectionAssets!=null) || (pfmLast.projectSectionAssets!=null)) {
			int section, x, y;
			String sectionName = null;
			Boolean found;
			for (section=1; section<=pfmNow.projectSections.length(); section++) {
				sectionName = DOM.getElementById("section" + section).getInnerText();
				JsArray<AlfrescoPacket> assetsNow = null;
				JsArray<AlfrescoPacket> assetsLast = null;
				if (pfmNow.projectSectionAssets.hasKey(sectionName)) {
					assetsNow = pfmNow.projectSectionAssets.getValue(sectionName).cast();
				}
				if (pfmLast.projectSectionAssets.hasKey(sectionName)) {
					assetsLast = pfmLast.projectSectionAssets.getValue(sectionName).cast();
				}
				// check for assets that are in the last save but not the current version
				if (assetsLast != null) {
					for (x=0;x<assetsLast.length();x++) {
						found = false;
						if (assetsNow != null)
							for (y=0;y<assetsNow.length();y++) {
								if (assetsLast.get(x).getNodeId() == assetsNow.get(y).getNodeId())  found = true;
						}
						if (!found) {
							// this asset has been removed from the section
							pfmLast.updateAlfrescoAssetUsage(sectionName, assetsLast.get(x).getNodeId(), assetsLast.get(x).getFilename(), false);							
						}
					}
				}
				// check for assets that are in the current version but not the last save
				if (assetsNow != null) {
					for (x=0;x<assetsNow.length();x++) {
						found = false;
						if (assetsLast != null)
							for (y=0;y<assetsLast.length();y++) {
								if (assetsLast.get(y).getNodeId() == assetsNow.get(x).getNodeId())  found = true;
						}
						if (!found) {
							// this asset has been added to the section
							pfmLast.updateAlfrescoAssetUsage(sectionName, assetsNow.get(x).getNodeId(), assetsNow.get(x).getFilename(), true);		
						}
					}
				}
			}
		}
		
		if (pfmNow.projectNodeId==null) {
			AlfrescoPacket ap = AlfrescoPacket.makePacket();
			ap.addKeyValue("filename", pfmNow.projectTitle.replaceAll(" ", "_") + ".rpf");
			ap.addKeyValue("filecontent", pfmNow.toJSONString());
			AlfrescoApi.uploadContentStream(ap.toJSONString(),
			 								new AlfrescoCallback<AlfrescoPacket>() {
												@Override
												public void onSuccess(AlfrescoPacket alfrescoPacket) {
													pfmNow.projectNodeId = alfrescoPacket.getNodeId();
													AlfrescoApi.addAspectToNode(pfmNow.projectNodeId, 
																				Russel.RUSSEL_ASPECTS.split(","), 
																				new AlfrescoCallback<AlfrescoPacket>() {
																					@Override
																					public void onFailure(Throwable caught) {
																						
																					}
																					
																					@Override
																					public void onSuccess(AlfrescoPacket alfrescoPacket) {
																						AlfrescoApi.updateContentStream(pfmNow.projectNodeId, 
																														"{\"nodeData\":" + pfmNow.toJSONString() + "}",
																													    new AlfrescoCallback<AlfrescoPacket>() {
																															@Override
																															public void onSuccess(AlfrescoPacket alfrescoPacket) {
																																
																															}
																															
																															@Override 
																															public void onFailure(Throwable caught) {
																																
																															}
																													    });
																					}
																				});
												}
												
												@Override
												public void onFailure(Throwable caught) {
													
												}
											});
		} else
			AlfrescoApi.updateContentStream(pfmNow.projectNodeId, 
											"{\"nodeData\":" + pfmNow.toJSONString() + "}",
										    new AlfrescoCallback<AlfrescoPacket>() {
												@Override
												public void onSuccess(AlfrescoPacket alfrescoPacket) {
													fillData();
												}
												
												@Override 
												public void onFailure(Throwable caught) {
													
												}
										    });
		
		pfmLast = pfmNow.copyProject() ;
		PageAssembler.closePopup("epssSaveProperties");
	}

	private void fillPropData() {
		((TextBox)PageAssembler.elementToWidget("epssProjectNotes", PageAssembler.TEXT)).setText(pfmNow.projectNotes);
		putObjectives(pfmNow.projectLearningObjectives, "project-objective-list");
		((Anchor)PageAssembler.elementToWidget("projectTitleText", PageAssembler.A)).setText(pfmNow.projectTitle);
		ListBox lb = ((ListBox)PageAssembler.elementToWidget("projectImiLevel", PageAssembler.SELECT));
		if (pfmNow.projectImi.indexOf(",")!=-1)
			lb.setSelectedIndex(Integer.valueOf(pfmNow.projectImi.substring(0,pfmNow.projectImi.indexOf(","))));
		lb = ((ListBox)PageAssembler.elementToWidget("projectBlooms", PageAssembler.SELECT));
		if (pfmNow.projectTaxo.indexOf(",")!=-1)
			lb.setSelectedIndex(Integer.valueOf(pfmNow.projectTaxo.substring(0,pfmNow.projectTaxo.indexOf(","))));
		lb = ((ListBox)PageAssembler.elementToWidget("projectParadata", PageAssembler.SELECT));
		if (pfmNow.projectUsage.indexOf(",")!=-1)
			lb.setSelectedIndex(Integer.valueOf(pfmNow.projectUsage.substring(0,pfmNow.projectUsage.indexOf(","))));
	}

	private void fillData() {
		fillPropData();		
		if (pfmNow.projectSectionNotes!=null)
			for (int x = 1;x<=pfmNow.projectSections.length() ;x++)
				if (pfmNow.projectSectionNotes.hasKey(DOM.getElementById("section"+x).getInnerText()))
					DOM.getElementById("section"+x).removeClassName("empty");
		if (pfmNow.projectSectionAssets!=null)
			for (int x=1;x<=pfmNow.projectSections.length() ;x++) { 
				String sectionName = DOM.getElementById("section" + x).getInnerText();
				if (pfmNow.projectSectionAssets.hasKey(sectionName)) {
					JsArray<AlfrescoPacket> assets = pfmNow.projectSectionAssets.getValue(sectionName).cast(); 
					if (assets.length()>0)
						DOM.getElementById("section" + x).removeClassName("empty");
				}
			}
	}

}