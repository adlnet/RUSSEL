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

import com.eduworks.gwt.client.net.CommunicationHub;
import com.eduworks.gwt.client.net.api.ESBApi;
import com.eduworks.gwt.client.net.callback.ESBCallback;
import com.eduworks.gwt.client.net.callback.EventCallback;
import com.eduworks.gwt.client.net.packet.ESBPacket;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.russel.ui.client.Russel;
import com.eduworks.russel.ui.client.epss.EPSSPackBuilder;
import com.eduworks.russel.ui.client.handler.ESBSearchHandler;
import com.eduworks.russel.ui.client.handler.SearchHandler;
import com.eduworks.russel.ui.client.handler.StatusWindowHandler;
import com.eduworks.russel.ui.client.handler.TileHandler;
import com.eduworks.russel.ui.client.model.ProjectRecord;
import com.eduworks.russel.ui.client.model.RUSSELFileRecord;
import com.eduworks.russel.ui.client.model.StatusRecord;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * EPSSEditScreen class
 * Extends ScreenTemplate
 * Defines methods and handlers for the EPSS Edit screen.
 * 
 * @author Eduworks Corporation
 */
public class EPSSEditScreen extends Screen {

	public static ProjectRecord pfmNow, pfmLast;
	private static Vector<String> searchTerms;
	private static SearchHandler assetSearchHandler;
	private static String activeSection;
	private static String activeSectionId;
	private static String activeAssetId;
	private static String activeFilename;

	/**
	 * lostFocus In place to handle any processing requirements required when this screen loses focus.
	 * Called by ScreenDispatch for all RUSSEL screens.
	 */
	public void lostFocus() {
		assetSearchHandler.stop();
	}

	/**
	 * putObjectives Calls the javascript function listObjectives to display the objectives in the metadata screen
	 * @param s String Screen type
	 * @param id String 
	 * @return String
	 */
	private final native String putObjectives0(String s, String id) /*-{
		return $wnd.listObjectives(s, id);
	}-*/;

	/**
	 * getObjectives Calls the javascript function compressObjectives to retrieve and save the objective metadata edits
	 * @param id String
	 * @return String
	 */
	private final native String getObjectives0(String id) /*-{
		return $wnd.compressObjectives(id);
	}-*/;
	
	/**
	 * EPSSEditScreen Constructor for the class given a project file
	 * @param incomingProject ProjectFileModel
	 */
	public EPSSEditScreen(ProjectRecord incomingProject) {
		EPSSEditScreen.pfmNow = incomingProject; 
		EPSSEditScreen.pfmLast = pfmNow.copyProject(); 
	}
	
	/**
	 * insertSection0 Adds a template section to the screen representation
	 * @param section Element Element that contains the new section
	 * @param title String Title of the section
	 * @param index int index of new section
	 */
	private native void insertSection0(Element section, String title, int index) /*-{
		var cell = document.createElement("td");
		section.appendChild(cell);
		cell.innerText = title;
		cell.className = "templateSection empty";
		cell.id = "section" + index;
	}-*/;
	
	/**
	 * populateTemplate Renders the top portion of the EPSS Edit screen containing the title and template sections
	 */
	public void populateTemplate() {
		DOM.getElementById("template-name").setInnerText(pfmNow.projectTemplateName);
		
		for (int sectionIndex=0;sectionIndex<pfmNow.projectSections.size();sectionIndex++)
			insertSection0(DOM.getElementById("template-sections-area"),  
						  new ESBPacket(pfmNow.projectSections.get(sectionIndex).isObject()).getString(ProjectRecord.TEMPLATE_SECTION_SHORT_TITLE),
						  (sectionIndex+1));
	}
	
	/**
	 * display Renders the EPSS Edit screen using appropriate templates and assigns handlers
	 */
	@Override
	public void display() {
		PageAssembler.ready(new HTML(templates().getEPSSEdit().getText()));
		PageAssembler.buildContents();
		populateTemplate();
		assetSearchHandler = new ESBSearchHandler();
		
		assetSearchHandler.hook("r-projectAssetSearch", "epssAssetPanel", ESBSearchHandler.ASSET_TYPE);
		
		PageAssembler.attachHandler("epssExportButton", Event.ONCLICK, new EventCallback() {
																		@Override
																		public void onEvent(Event event) {
																			RootPanel.get("epssDownloadArea").clear();
																		}
																   });
		
		PageAssembler.attachHandler("epssUpdate", Event.ONCLICK, new EventCallback() {	
																	@Override
																	public void onEvent(Event event) {
																		saveProject0();
																	}
																 });
		
		PageAssembler.attachHandler("epssCancel", Event.ONCLICK, new EventCallback() {	
																	@Override
																	public void onEvent(Event event) {
																		view().loadFeatureScreen(FeatureScreen.PROJECTS_TYPE);
																	}
																 });

		PageAssembler.attachHandler("epssSaveProperties", Event.ONCLICK, new EventCallback() {
																	@Override
																	public void onEvent(Event event) {
																		saveProject0();
																   		PageAssembler.closePopup("projectProperties");
																	}															
																});
		
		PageAssembler.attachHandler("epssCancelProperties", Event.ONCLICK, new EventCallback() {
																	@Override
																	public void onEvent(Event event) {
																		fillPropData0();
																   		PageAssembler.closePopup("projectProperties");
																	}													
																});
		
		PageAssembler.attachHandler("epssCloseProperties", Event.ONCLICK, new EventCallback() {
																	@Override
																	public void onEvent(Event event) {
																		fillPropData0();
																   		PageAssembler.closePopup("projectProperties");
																	}															
																}); 
		
		PageAssembler.attachHandler("epssSaveAs", Event.ONCLICK, new EventCallback() {
																@Override
																public void onEvent(Event event) {
																	Window.alert(Russel.INCOMPLETE_FEATURE_MESSAGE);
																}		
															  });
		
		for (int sectionIndex=1;sectionIndex<=pfmNow.projectSections.size();sectionIndex++) {
			final int lockedSectionIndex = sectionIndex;
			PageAssembler.attachHandler("section" + lockedSectionIndex, 
										Event.ONCLICK, 
										new EventCallback() {
											@Override
											public void onEvent(Event event) {
												fillSectionNotes0("section" + lockedSectionIndex, lockedSectionIndex-1);
											}
									   });
		}
		
		PageAssembler.attachHandler("epssExportSCORM", Event.ONCLICK, new EventCallback() {
															   	@Override
															   	public void onEvent(Event event) {
															   		saveProject0();
															   		EPSSPackBuilder epb = new EPSSPackBuilder(pfmNow);
															   		FormPanel fp = ((FormPanel)PageAssembler.elementToWidget("epssExportSCORMForm", PageAssembler.FORM));
															   		fp.setAction(ESBApi.getESBActionURL("exportToZip"));
															   		fp.setMethod(CommunicationHub.POST);
															   		fp.setEncoding(FormPanel.ENCODING_MULTIPART);
															   		ESBPacket postData = epb.buildPackIE();
															   		postData.put("sessionId", ESBApi.sessionId);
															   		((Hidden)PageAssembler.elementToWidget("epssExportSCORMPayload", PageAssembler.HIDDEN)).setValue(postData.toString());
															   		fp.addSubmitHandler(new SubmitHandler() {
																									@Override
																									public void onSubmit(SubmitEvent event) {
														   												StatusWindowHandler.createMessage(StatusWindowHandler.getZipExportMessageDone(pfmNow.projectTitle.replaceAll(" ", "_") + ".zip"), 
																											  StatusRecord.ALERT_SUCCESS);
													   													RootPanel.get("epssDownloadArea").clear();
													   													PageAssembler.closePopup("exportProjectModal");
																									}
																								});
															   		fp.submit();															   		
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

		fillData0();
	}
	
	/**
	 * removeAssetTrigger Removes the currently active asset from its location and changes Update button to unsaved state
	 */
	public static void removeAssetTrigger() {
		DOM.getElementById("epssUpdate").removeClassName("white");
		DOM.getElementById("epssUpdate").addClassName("blue");
		DOM.getElementById("r-save-alert").removeClassName("hide");
		activeAssetId = ((Hidden)PageAssembler.elementToWidget("epssActiveRemoveAsset", PageAssembler.HIDDEN)).getValue();
		pfmNow.removeAsset(activeSection, activeAssetId);
	}

	/**
	 * addAssetTrigger Adds the currently active asset to the project section and changes the Update button to unsaved state
	 */
	public static void addAssetTrigger() {
		Hidden activeAssetFilename = ((Hidden)PageAssembler.elementToWidget("epssActiveAddAsset", PageAssembler.HIDDEN));
		activeAssetId = activeAssetFilename.getValue().substring(0, activeAssetFilename.getValue().indexOf(","));
		activeFilename = activeAssetFilename.getValue().substring(activeAssetFilename.getValue().indexOf(",")+1);
		if (!isAssetInSection0())
			pfmNow.addAsset(activeSection, activeAssetId, activeFilename, "");
		else {
			pfmNow.addAsset(activeSection, activeAssetId, activeFilename, getAssetNotesInSection0());
			((TextBox)PageAssembler.elementToWidget("inputDevNotes", PageAssembler.TEXT)).setText(getAssetNotesInSection0());
		}
		DOM.getElementById(activeSectionId).removeClassName("empty");
	}
	
	/**
	 * buildAssetTile0 Creates a new tile to represent an asset
	 * @param assetsDiv String target div name for the tile
	 * @param nodeId String 
	 * @param filename String
	 */
	private void buildAssetTile0(String assetsDiv, String nodeId, String filename) {
		Element td = null;
		RootPanel rp = RootPanel.get(assetsDiv);
		if (rp!=null) {
			td = DOM.createTD();
			td.setId(PageAssembler.getIdCount() +"-assetNote");
			td.appendChild(new HTML(templates().getEPSSNoteAssetObjectWidget().getText()).getElement());
			Vector<String> iDs = PageAssembler.merge(assetsDiv, "x", td);
			String idPrefix = iDs.firstElement().substring(0,iDs.firstElement().indexOf("-"));	
			RUSSELFileRecord ap = new RUSSELFileRecord();
			ap.setGuid(nodeId);
			ap.setFilename(filename);
			new TileHandler(null, idPrefix, ESBSearchHandler.NOTES_TYPE, ap).refreshTile(null);
		}
	}
	
	/**
	 * isAssetInSection0 Determines if the asset is already listed in the selected section
	 * @return Boolean true if the asset is already there, false if not
	 */
	private static boolean isAssetInSection0() {
		boolean acc = false;
		if (pfmNow.projectSectionAssets!=null)
			return pfmNow.projectSectionAssets.hasSectionAsset(activeSection, activeAssetId);
		return acc;
	}
	
	/**
	 * getAssetNotesInSection0 Retrieves the notes for the selected asset in a particular section
	 * @return String 
	 */
	private static String getAssetNotesInSection0() {
		if (pfmNow.projectSectionAssets!=null)
			return pfmNow.projectSectionAssets.getSectionAssetNotes(activeSection, activeAssetId);
		return "";
	}
	
	/**
	 * mergeSection0 Merges section assets and notes with the sections
	 * @param section
	 * @param sectionTools
	 */
	private native void mergeSection0(Element section, Element sectionTools) /*-{
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

	/**
	 * fillSectionNotes0 Retrieves section notes and search terms
	 * @param elementId String
	 * @param sectionIndex int
	 */
	private void fillSectionNotes0(final String elementId, final int sectionIndex) {
		((TextBox)PageAssembler.elementToWidget("inputSectionNotes", PageAssembler.TEXT)).setText("");
		((TextBox)PageAssembler.elementToWidget("inputDevNotes", PageAssembler.TEXT)).setText("");
		String sectionPanel = "epssCurrentSection";
		Element currentSection = DOM.getElementById(sectionPanel);
		searchTerms = new Vector<String>();
		int elementCount = currentSection.getChildCount();
		for (int elementIndex=0;elementIndex<elementCount;elementIndex++)
			currentSection.getChild(0).removeFromParent();
		mergeSection0(currentSection, (Element)new HTML(templates().getEPSSEditSectionWidgets().getText()).getElement());
		
		activeSection = DOM.getElementById(elementId).getInnerText();
		activeSectionId = elementId;
		PageAssembler.attachHandler("inputSectionNotes", Event.ONCHANGE, new EventCallback() {
																			@Override
																			public void onEvent(Event event) {
																				pfmNow.addSectionNotes(activeSection, cleanString0(((TextBox)PageAssembler.elementToWidget("inputSectionNotes", PageAssembler.TEXT)).getText()));
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
																			pfmNow.addAsset(activeSection, activeAssetId, activeFilename, cleanString0(((TextBox)PageAssembler.elementToWidget("inputDevNotes", PageAssembler.TEXT)).getText()));
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
		String[] searchTerms = new ESBPacket(pfmNow.projectSections.get(sectionIndex).isObject()).getString(ProjectRecord.TEMPLATE_SECTION_TERMS).split(", ");
		for (int termsIndex=0;termsIndex<searchTerms.length;termsIndex++)
			PageAssembler.merge("searchTerms", 
								"x", 
								(Element)new HTML("<a id=\"epssTerm" + (termsIndex+1) + "\" class=\"searchTerm small white button finger\">" + 
										searchTerms[termsIndex] + "</a>").getElement().getChild(0));
		DOM.getElementById("helptext-asset").setInnerText(new ESBPacket(pfmNow.projectSections.get(sectionIndex).isObject()).getString(ProjectRecord.TEMPLATE_SECTION_GUIDENCE));
		
		for (int termIndex=1;termIndex<=searchTerms.length;termIndex++) {
			final int lockedTermIndex = termIndex;
			PageAssembler.attachHandler("epssTerm" + lockedTermIndex, 
										Event.ONCLICK, 
										new EventCallback() {
											@Override
											public void onEvent(Event event) {
												toggleSearchTerms0("epssTerm" + lockedTermIndex, 
																  ((Anchor)PageAssembler.elementToWidget("epssTerm" + lockedTermIndex, PageAssembler.A)).getText());
											}
										});	
		}
		
		if (pfmNow.projectSectionNotes!=null)
			((TextBox)PageAssembler.elementToWidget("inputSectionNotes", PageAssembler.TEXT)).setText(pfmNow.projectSectionNotes.getSectionNotes(activeSection));
		
		if (pfmNow.projectSectionAssets!=null) {
			Vector<RUSSELFileRecord> assets = pfmNow.projectSectionAssets.getSectionAssets(activeSection);
			for (int x=0;x<assets.size();x++) {
				buildAssetTile0(sectionPanel, assets.get(x).getGuid(), assets.get(x).getFilename());
			}
			PageAssembler.runCustomJSHooks();
		}
	}
	
	/**
	 * toggleSearchTerms0 Reverses the selection state of a search term button
	 * @param eId String
	 * @param term String 
	 */
	private void toggleSearchTerms0(String eId, String term) {
		if (!searchTerms.contains(term))
			searchTerms.add(term);
		else
			searchTerms.remove(term);
		
		String accQuery = buildQueryString(); 
		if (accQuery.trim()!="")
			assetSearchHandler.forceSearch(accQuery);
	}
	
	/**
	 * buildQueryString Constructs the query string for asset search
	 * @return String
	 */
	public static String buildQueryString() {
		String enteredTerm = ((TextBox)PageAssembler.elementToWidget("r-projectAssetSearch", PageAssembler.TEXT)).getText().trim();
		
		String accQuery = "";
		if (searchTerms.size()!=0) {
			for (int x=0;x<searchTerms.size();x++) {
				if (ESBSearchHandler.cleanQuery(enteredTerm) =="")
					accQuery += " OR \"" + searchTerms.get(x) + "\"";
				else
					accQuery += " OR (\"" + ESBSearchHandler.cleanQuery(enteredTerm) + "\" \"" + searchTerms.get(x) + "\")";
			}
			accQuery = accQuery.substring(" OR ".length()).trim();
		} else accQuery = enteredTerm;
		
		return accQuery.trim();
	}
	
	/**
	 * cleanString0 Prepares a string for query or storage
	 * @param dirty String
	 * @return String
	 */
	private String cleanString0(String dirty) {
		if (dirty==null)
			return "";
		else return dirty.replaceAll("\"", "'").replaceAll("[\r\n]", " ").trim();
	}
	
	/**
	 * saveProject0 Saves the current state of the EPSS project file
	 */
	private void saveProject0() {
		DOM.getElementById("epssUpdate").addClassName("white");
		DOM.getElementById("epssUpdate").removeClassName("blue");
		DOM.getElementById("r-save-alert").addClassName("hide");
		pfmNow.projectTitle = ((Anchor)PageAssembler.elementToWidget("projectTitleText", PageAssembler.A)).getText();
		if (pfmNow.projectTitle==null||pfmNow.projectTitle.equalsIgnoreCase("Click here to add a title"))
			pfmNow.projectTitle = "DefaultName";
		pfmNow.projectCreator = ESBApi.username;
		pfmNow.projectNotes = cleanString0(((TextBox)PageAssembler.elementToWidget("epssProjectNotes", PageAssembler.TEXT)).getText());
		pfmNow.projectLearningObjectives = cleanString0(getObjectives0("project-objective-list"));
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
			for (section=1; section<=pfmNow.projectSections.size(); section++) {
				sectionName = DOM.getElementById("section" + section).getInnerText();
				Vector<RUSSELFileRecord> assetsNow = null;
				Vector<RUSSELFileRecord> assetsLast = null;
				if (pfmNow.projectSectionAssets.hasSection(sectionName)) {
					assetsNow = pfmNow.projectSectionAssets.getSectionAssets(sectionName);
				}
				if (pfmLast.projectSectionAssets.hasSection(sectionName)) {
					assetsLast = pfmLast.projectSectionAssets.getSectionAssets(sectionName);
				}
				// check for assets that are in the last save but not the current version
				if (assetsLast != null) {
					for (x=0;x<assetsLast.size();x++) {
						found = false;
						if (assetsNow != null)
							for (y=0;y<assetsNow.size();y++) {
								if (assetsLast.get(x).getGuid() == assetsNow.get(y).getGuid())  found = true;
						}
						if (!found) {
							// this asset has been removed from the section
							pfmLast.updateAlfrescoAssetUsage(sectionName, assetsLast.get(x).getGuid(), assetsLast.get(x).getFilename(), false);							
						}
					}
				}
				// check for assets that are in the current version but not the last save
				if (assetsNow != null) {
					for (x=0;x<assetsNow.size();x++) {
						found = false;
						if (assetsLast != null)
							for (y=0;y<assetsLast.size();y++) {
								if (assetsLast.get(y).getGuid() == assetsNow.get(x).getGuid())  found = true;
						}
						if (!found) {
							// this asset has been added to the section
							pfmLast.updateAlfrescoAssetUsage(sectionName, assetsNow.get(x).getGuid(), assetsNow.get(x).getFilename(), true);		
						}
					}
				}
			}
		}
		
		if (pfmNow.getGuid()==null) {
			ESBApi.uploadResource(pfmNow.projectTitle.replaceAll(" ", "_") + ".rpf",
								  pfmNow.toJSONString(),
								  ProjectRecord.RUSSEL_PROJECT,
			 								new ESBCallback<ESBPacket>() {
												@Override
												public void onSuccess(ESBPacket alfrescoPacket) {
													pfmNow.setGuid(alfrescoPacket.getPayloadString());
													ESBApi.updateResource(pfmNow.getGuid(),
																		  pfmNow.projectTitle.replaceAll(" ", "_") + ".rpf",
																	      pfmNow.toJSONString(),
																		  ProjectRecord.RUSSEL_PROJECT,
																		  new ESBCallback<ESBPacket>() {
																			  @Override
																			  public void onSuccess(ESBPacket alfrescoPacket) {
																					
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
		} else
			ESBApi.updateResource(pfmNow.getGuid(), 
								  pfmNow.projectTitle.replaceAll(" ", "_") + ".rpf",
								  pfmNow.toJSONString(),
								  ProjectRecord.RUSSEL_PROJECT,
							      new ESBCallback<ESBPacket>() {
									  @Override
									  public void onSuccess(ESBPacket alfrescoPacket) {
  										  fillData0();
									  }
									
									  @Override 
									  public void onFailure(Throwable caught) {
										
  									  }
							      });

		pfmLast = pfmNow.copyProject() ;
		PageAssembler.closePopup("epssSaveProperties");
	}

	/**
	 * fillPropData0 Populates the project properties dialog with current information
	 */
	private void fillPropData0() {
		((TextBox)PageAssembler.elementToWidget("epssProjectNotes", PageAssembler.TEXT)).setText(pfmNow.projectNotes);
		putObjectives0(pfmNow.projectLearningObjectives, "project-objective-list");
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

	/**
	 * fillData0 Populates the EPSS editor with current project assets and notes
	 */
	private void fillData0() {
		fillPropData0();
		if (pfmNow.projectSectionNotes!=null)
			for (int x = 1;x<=pfmNow.projectSections.size() ;x++)
				if (pfmNow.projectSectionNotes.hasSection(DOM.getElementById("section"+x).getInnerText()))
					DOM.getElementById("section"+x).removeClassName("empty");
		if (pfmNow.projectSectionAssets!=null)
			for (int x=1;x<=pfmNow.projectSections.size() ;x++) { 
				String sectionName = DOM.getElementById("section" + x).getInnerText();
				if (pfmNow.projectSectionAssets.hasSection(sectionName)) {
					Vector<RUSSELFileRecord> assets = pfmNow.projectSectionAssets.getSectionAssets(sectionName);
					if (assets.size()>0)
						DOM.getElementById("section" + x).removeClassName("empty");
				}
			}
	}

}