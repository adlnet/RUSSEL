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

import com.eduworks.gwt.client.component.HtmlTemplates;
import com.eduworks.gwt.client.model.StatusRecord;
import com.eduworks.gwt.client.net.CommunicationHub;
import com.eduworks.gwt.client.net.callback.ESBCallback;
import com.eduworks.gwt.client.net.callback.EventCallback;
import com.eduworks.gwt.client.net.packet.ESBPacket;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.gwt.client.pagebuilder.modal.ModalDispatch;
import com.eduworks.gwt.client.pagebuilder.overlay.OverlayDispatch;
import com.eduworks.gwt.client.pagebuilder.screen.ScreenDispatch;
import com.eduworks.gwt.client.pagebuilder.screen.ScreenTemplate;
import com.eduworks.gwt.client.util.BlobUtils;
import com.eduworks.russel.ui.client.Russel;
import com.eduworks.russel.ui.client.epss.SCORMTemplates;
import com.eduworks.russel.ui.client.handler.SearchHandler;
import com.eduworks.russel.ui.client.handler.StatusHandler;
import com.eduworks.russel.ui.client.model.ProjectRecord;
import com.eduworks.russel.ui.client.model.RUSSELFileRecord;
import com.eduworks.russel.ui.client.net.RusselApi;
import com.eduworks.russel.ui.client.pagebuilder.EpssTemplates;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
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
public class EPSSScreen extends ScreenTemplate {
	public static final String PROPERTY_IMI = "IMI Level";
	public static final String PROPERTY_BLOOMS = "Bloom's Taxonomy";
	
	private ProjectRecord currentProject;
	private Vector<String> searchTerms;
	private SearchHandler assetSearchHandler;
	private int activeSection;
	private RUSSELFileRecord activeAsset;

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
	 * EPSSEditScreen Constructor for the class given a project file
	 * @param incomingProject ProjectFileModel
	 */
	public EPSSScreen(ProjectRecord incomingProject) {
		currentProject = incomingProject;
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
		if (cell.innerText!=null)
			cell.innerText = title;
		else
			cell.textContent = title;
		cell.className = "templateSection empty";
		cell.id = "section" + index;
	}-*/;	

	private void generateQuery() {
		StringBuilder sb = new StringBuilder();
		String q = ((TextBox) PageAssembler.elementToWidget("r-projectAssetSearch", PageAssembler.TEXT)).getText();
		sb.append(SearchHandler.cleanQuery(q));
		String a = sb.toString();
		if (a == "")
			a = "*";
		assetSearchHandler.query(a);
	}
	
	/**
	 * display Renders the EPSS Edit screen using appropriate templates and assigns handlers
	 */
	@Override
	public void display() {
		PageAssembler.ready(new HTML(Russel.htmlTemplates.getEPSSEdit().getText()));
		PageAssembler.buildContents();

		DOM.getElementById("r-menuWorkspace").getParentElement().removeClassName("active");
		DOM.getElementById("r-menuCollections").getParentElement().removeClassName("active");
		DOM.getElementById("r-menuProjects").getParentElement().addClassName("active");
		
		DOM.getElementById("template-name").setInnerText(currentProject.getTemplateName());
		
		for (int sectionIndex=0;sectionIndex<currentProject.getSections().size();sectionIndex++)
			insertSection0(DOM.getElementById("template-sections-area"),  
						   new ESBPacket(currentProject.getSections().get(sectionIndex).isObject()).getString(ProjectRecord.TEMPLATE_SECTION_SHORT_TITLE),
						   sectionIndex);
		
		PageAssembler.inject("flowContainer", "x", new HTML(Russel.htmlTemplates.getDetailModal().getText()), true);
		PageAssembler.inject("objDetailPanelWidget", "x", new HTML(Russel.htmlTemplates.getDetailPanel().getText()), true);
		assetSearchHandler = new SearchHandler(this, true);
		
		assetSearchHandler.hookAndClear("r-projectAssetSearch", "epssAssetPanel", SearchHandler.TYPE_ASSET);
		
		PageAssembler.attachHandler("r-projectAssetSearch", Event.ONKEYUP, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				generateQuery();
			}
		});
		
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
																		Russel.screen.loadScreen(new FeatureScreen(FeatureScreen.PROJECTS_TYPE), true);
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
		
		for (int sectionIndex = 0; sectionIndex < currentProject.getSections().size(); sectionIndex++) {
			final int lockedSectionIndex = sectionIndex;
			PageAssembler.attachHandler("section" + lockedSectionIndex, 
										Event.ONCLICK, 
										new EventCallback() {
											@Override
											public void onEvent(Event event) {
												fillSectionNotes0("section" + lockedSectionIndex, lockedSectionIndex);
											}
									   });
		}
				
		PageAssembler.attachHandler("epssExportSCORM", Event.ONCLICK, new EventCallback() {
															   	@Override
															   	public void onEvent(Event event) {
															   		saveProject0();
															   		ESBPacket postData = new ESBPacket();
															   		FormPanel fp = ((FormPanel)PageAssembler.elementToWidget("epssExportSCORMForm", PageAssembler.FORM));
															   		fp.setAction(RusselApi.getESBActionURL("zipResources"));
															   		fp.setMethod(CommunicationHub.POST);
															   		fp.setEncoding(FormPanel.ENCODING_MULTIPART);
															   		postData.put("sessionid", RusselApi.sessionId);
															   		JSONArray ja = new JSONArray();
															   		int c = 0;
															   		for (String key : currentProject.getAssets().keySet())
															   			ja.set(c++, new JSONString(key));
															   		ja.set(c++, new JSONString(currentProject.getGuid()));
															   		postData.put("resourcemetadata", ja);
															   		postData.put("resourceid", currentProject.getTitle().replaceAll(" ", "_") + ".zip");
															   		((Hidden)PageAssembler.elementToWidget("epssExportSCORMPayload", PageAssembler.HIDDEN)).setValue(postData.toString());
															   		((Hidden)PageAssembler.elementToWidget("epssExportImsmanifestPayload", PageAssembler.HIDDEN)).setValue(SCORMTemplates.INSTANCE.getImsmanifest().getText());
															   		((Hidden)PageAssembler.elementToWidget("epssExportInitPagePayload", PageAssembler.HIDDEN)).setValue(SCORMTemplates.INSTANCE.getInitPage().getText());
															   		fp.addSubmitHandler(new SubmitHandler() {
																									@Override
																									public void onSubmit(SubmitEvent event) {
														   												StatusHandler.createMessage(StatusHandler.getZipExportMessageDone(currentProject.getTitle().replaceAll(" ", "_") + ".zip"), 
																											  StatusRecord.ALERT_SUCCESS);
													   													RootPanel.get("epssDownloadArea").clear();
													   													PageAssembler.closePopup("exportProjectModal");
																									}
																								});
															   		fp.submit();															   		
															   		RootPanel.get("epssDownloadArea").add(new Image("images/orbit/loading.gif"));
															   	}
															   });

		fillData0();
	}
	
	/**
	 * removeAssetTrigger Removes the currently active asset from its location and changes Update button to unsaved state
	 */
	public void removeAssetFromProject(RUSSELFileRecord r) {
		DOM.getElementById("epssUpdate").removeClassName("white");
		DOM.getElementById("epssUpdate").addClassName("blue");
		DOM.getElementById("r-save-alert").removeClassName("hide");
		currentProject.removeAsset(activeSection, r);
	}

	/**
	 * addAssetTrigger Adds the currently active asset to the project section and changes the Update button to unsaved state
	 */
	public void addAssetToProject(RUSSELFileRecord r) {
		Element td = DOM.createTD();
		int prefix = DOM.getElementById("epssCurrentSection").getChildCount()-2;
		PageAssembler.merge("epssCurrentSection", "x", td);
		td.setId(prefix + "-assetNote");
		assetSearchHandler.buildTile(r, SearchHandler.TYPE_PROJECT_ASSET, td.getId()).fillTile(null);
		
		DOM.getElementById("epssUpdate").removeClassName("white");
		DOM.getElementById("epssUpdate").addClassName("blue");
		DOM.getElementById("r-save-alert").removeClassName("hide");
		currentProject.addAsset(activeSection, r);
		DOM.getElementById("section" + activeSection).removeClassName("empty");
	}
	
	public void setActiveAsset(RUSSELFileRecord r) {
		activeAsset = r;
		((TextBox)PageAssembler.elementToWidget("inputDevNotes", PageAssembler.TEXT)).setText(currentProject.getSectionAssets().get(activeSection).isObject().get(r.getGuid()).isString().stringValue());
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
		mergeSection0(currentSection, (Element)new HTML(Russel.htmlTemplates.getEPSSEditSectionWidgets().getText()).getElement());
		
		activeSection = sectionIndex;
		
		PageAssembler.attachHandler("sectionTool2", Event.ONCLICK, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				generateQuery();
			}
		});
		
		PageAssembler.attachHandler("inputSectionNotes", Event.ONCHANGE, new EventCallback() {
																			@Override
																			public void onEvent(Event event) {
																				currentProject.setSectionNotes(activeSection, ((TextBox)PageAssembler.elementToWidget("inputSectionNotes", PageAssembler.TEXT)).getText());
																				DOM.getElementById("epssUpdate").removeClassName("white");
																				DOM.getElementById("epssUpdate").addClassName("blue");
																				DOM.getElementById("r-save-alert").removeClassName("hide");
																				DOM.getElementById(elementId).removeClassName("empty");
																			}
																		});
		
		PageAssembler.attachHandler("inputDevNotes", Event.ONCHANGE, new EventCallback() {
																		@Override
																		public void onEvent(Event event) {
																			currentProject.addAssetText(activeSection, activeAsset, ((TextBox)PageAssembler.elementToWidget("inputDevNotes", PageAssembler.TEXT)).getText());
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
		DOM.getElementById("helptext-asset").setInnerText(new ESBPacket(currentProject.getSections().get(sectionIndex).isObject()).getString(ProjectRecord.TEMPLATE_SECTION_GUIDENCE));
		
		JSONObject jo = currentProject.getAssets();
		for (String key : currentProject.getSectionAssets().get(activeSection).isObject().keySet()) {
			RUSSELFileRecord r = new RUSSELFileRecord(new ESBPacket(jo.get(key).isObject()));
			Element td = DOM.createTD();
			int prefix = DOM.getElementById("epssCurrentSection").getChildCount()-2;
			PageAssembler.merge("epssCurrentSection", "x", td);
			td.setId(prefix + "-assetNote");
			assetSearchHandler.buildTile(r, SearchHandler.TYPE_PROJECT_ASSET, td.getId()).fillTile(null);
		}
		
//		String[] searchTerms = new ESBPacket(pfmNow.projectSections.get(sectionIndex).isObject()).getString(ProjectRecord.TEMPLATE_SECTION_TERMS).split(", ");
//		for (int termsIndex=0;termsIndex<searchTerms.length;termsIndex++)
//			PageAssembler.merge("searchTerms", 
//								"x", 
//								(Element)new HTML("<a id=\"epssTerm" + (termsIndex+1) + "\" class=\"searchTerm small white button finger\">" + 
//										searchTerms[termsIndex] + "</a>").getElement().getChild(0));
		
		
//		for (int termIndex=1;termIndex<=searchTerms.length;termIndex++) {
//			final int lockedTermIndex = termIndex;
//			PageAssembler.attachHandler("epssTerm" + lockedTermIndex, 
//										Event.ONCLICK, 
//										new EventCallback() {
//											@Override
//											public void onEvent(Event event) {
//												toggleSearchTerms0("epssTerm" + lockedTermIndex, 
//																  ((Anchor)PageAssembler.elementToWidget("epssTerm" + lockedTermIndex, PageAssembler.A)).getText());
//											}
//										});	
//		}
		
		((TextBox)PageAssembler.elementToWidget("inputSectionNotes", PageAssembler.TEXT)).setText(currentProject.getSectionNotes().get(activeSection).isString().stringValue());
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
			assetSearchHandler.query(accQuery);
	}
	
	/**
	 * buildQueryString Constructs the query string for asset search
	 * @return String
	 */
	public static String buildQueryString() {
		String enteredTerm = ((TextBox)PageAssembler.elementToWidget("r-projectAssetSearch", PageAssembler.TEXT)).getText().trim();
		
		String accQuery = "";
//		if (searchTerms.size()!=0) {
//			for (int x=0;x<searchTerms.size();x++) {
//				if (SearchHandler.cleanQuery(enteredTerm) =="")
//					accQuery += " OR \"" + searchTerms.get(x) + "\"";
//				else
//					accQuery += " OR (\"" + SearchHandler.cleanQuery(enteredTerm) + "\" \"" + searchTerms.get(x) + "\")";
//			}
//			accQuery = accQuery.substring(" OR ".length()).trim();
//		} else accQuery = enteredTerm;
		
		return accQuery.trim();
	}
	
	/**
	 * saveProject0 Saves the current state of the EPSS project file
	 */
	private void saveProject0() {
		DOM.getElementById("epssUpdate").addClassName("white");
		DOM.getElementById("epssUpdate").removeClassName("blue");
		DOM.getElementById("r-save-alert").addClassName("hide");
		currentProject.setTitle(((Anchor)PageAssembler.elementToWidget("projectTitleText", PageAssembler.A)).getText());
		currentProject.setNotes(((TextBox)PageAssembler.elementToWidget("epssProjectNotes", PageAssembler.TEXT)).getText());
		currentProject.setObjectives(getObjectives0("project-objective-list", new JSONArray()));
		int imiIndex = ((ListBox)PageAssembler.elementToWidget("projectImiLevel", PageAssembler.SELECT)).getSelectedIndex();
		if (imiIndex!=-1)
			currentProject.setImi(((ListBox)PageAssembler.elementToWidget("projectImiLevel", PageAssembler.SELECT)).getItemText(imiIndex));
		int taxIndex = ((ListBox)PageAssembler.elementToWidget("projectBlooms", PageAssembler.SELECT)).getSelectedIndex();
		if (taxIndex!=-1) 
			currentProject.setBloomTaxonomy(((ListBox)PageAssembler.elementToWidget("projectBlooms", PageAssembler.SELECT)).getItemText(taxIndex));

		JSONArray changes = currentProject.getChanges();
		RusselApi.updateResourceEpss(changes,
									 new ESBCallback<ESBPacket>() {
										@Override
										public void onFailure(Throwable caught) {}
										@Override
										public void onSuccess(ESBPacket esbPacket) {}
								     });
		currentProject.commit();
		
		if (currentProject.getGuid()==null||currentProject.getGuid()=="") {
			RusselApi.uploadResource(BlobUtils.buildBlob(ProjectRecord.MIME_RUSSEL_PROJECT, currentProject.toJSONString()),
									 currentProject.getTitle().replaceAll(" ", "_") + ".rpf",
	 								 new ESBCallback<ESBPacket>() {
											@Override
											public void onSuccess(ESBPacket alfrescoPacket) {
												currentProject.setGuid(alfrescoPacket.getPayloadString());
												updateMetadata();
												RusselApi.updateResource(currentProject.getGuid(),
																		 BlobUtils.buildBlob(ProjectRecord.MIME_RUSSEL_PROJECT, currentProject.toJSONString()), 
																		 currentProject.getTitle().replaceAll(" ", "_") + ".rpf",
																		 new ESBCallback<ESBPacket>() {
																			  @Override
																			  public void onSuccess(ESBPacket alfrescoPacket) {}
																				
																			  @Override 
																			  public void onFailure(Throwable caught) {}
																	 	 });
											}
											
											@Override
											public void onFailure(Throwable caught) {
												
											}
										});
		} else
			RusselApi.updateResource(currentProject.getGuid(),
									 BlobUtils.buildBlob(ProjectRecord.MIME_RUSSEL_PROJECT, currentProject.toJSONString()),
									 currentProject.getTitle().replaceAll(" ", "_") + ".rpf",
								  	 new ESBCallback<ESBPacket>() {
									  	@Override
									  	public void onSuccess(ESBPacket alfrescoPacket) {
  										  	fillData0();
  										    updateMetadata();
									  	}
									
									  	@Override 
									  	public void onFailure(Throwable caught) {}
							      	 });

		PageAssembler.closePopup("epssSaveProperties");
	}

	private void updateMetadata() {
		RusselApi.updateResourceMetadata(currentProject.getGuid(), currentProject.toObject(), new ESBCallback<ESBPacket>() {
			@Override
			public void onFailure(Throwable caught) {}
			
			@Override
			public void onSuccess(ESBPacket esbPacket) {}
		});
	}
	
	/**
	 * fillPropData0 Populates the project properties dialog with current information
	 */
	private void fillPropData0() {
		((TextBox)PageAssembler.elementToWidget("epssProjectNotes", PageAssembler.TEXT)).setText(currentProject.getNotes());
		putObjectives0(currentProject.getObjectives(), "project-objective-list");
		if (!currentProject.getTitle().trim().equals(""))
			((Anchor)PageAssembler.elementToWidget("projectTitleText", PageAssembler.A)).setText(currentProject.getTitle());
		ListBox lb = ((ListBox)PageAssembler.elementToWidget("projectImiLevel", PageAssembler.SELECT));
		for (int i = 0; i < lb.getItemCount(); i++)
			if (lb.getItemText(i).equalsIgnoreCase(currentProject.getImi()))
				lb.setSelectedIndex(i);
		lb = ((ListBox)PageAssembler.elementToWidget("projectBlooms", PageAssembler.SELECT));
		for (int i = 0; i < lb.getItemCount(); i++)
			if (lb.getItemText(i).equalsIgnoreCase(currentProject.getBloomTaxonomy()))
				lb.setSelectedIndex(i);
	}

	/**
	 * fillData0 Populates the EPSS editor with current project assets and notes
	 */
	private void fillData0() {
		fillPropData0();
		for (int x = 0;x<currentProject.getSectionNotes().size(); x++)
			if (!currentProject.getSectionNotes().get(x).equals(new JSONString("")))
				DOM.getElementById("section"+x).removeClassName("empty");
		for (int x = 0; x < currentProject.getSectionAssets().size(); x++) 
			if (currentProject.getSectionAssets().get(x).isObject().size()>0)
				DOM.getElementById("section"+x).removeClassName("empty");
	}

	@Override
	public ScreenDispatch getDispatcher() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OverlayDispatch getOverlayDispatcher() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ModalDispatch getModalDispatcher() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HtmlTemplates getTemplates() {
		// TODO Auto-generated method stub
		return null;
	}
}