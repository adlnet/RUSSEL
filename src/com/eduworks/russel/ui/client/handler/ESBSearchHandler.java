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

package com.eduworks.russel.ui.client.handler;

import java.util.Vector;

import com.eduworks.gwt.client.model.Record;
import com.eduworks.gwt.client.net.api.ESBApi;
import com.eduworks.gwt.client.net.callback.ESBCallback;
import com.eduworks.gwt.client.net.callback.EventCallback;
import com.eduworks.gwt.client.net.packet.ESBPacket;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.gwt.client.util.Date;
import com.eduworks.russel.ui.client.model.RUSSELFileRecord;
import com.eduworks.russel.ui.client.model.StatusRecord;
import com.eduworks.russel.ui.client.pagebuilder.screen.EPSSEditScreen;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * AlfrescoSearchHandler
 * Defines globals, methods and handlers for the Alfresco queries.
 * 
 * @author Eduworks Corporation
 */
public class ESBSearchHandler extends SearchHandler {
	
	public static final String RECENT_TYPE = "recent";
	public static final String SEARCH_TYPE = "search";
	public static final String PROJECT_TYPE = "project";
	public static final String EDIT_TYPE = "edit";
	public static final String ASSET_TYPE = "asset";
	public static final String NOTES_TYPE = "notes";
	public static final String TEMPLATE_TYPE = "template";
	public static final String COLLECTION_TYPE = "collection";
	public static final String FLR_TYPE = "FLR";
	public static final String STRATEGY_TYPE = "strategy";

	protected Vector<RUSSELFileRecord> pendingEdits;

	/**
	 * buildTile0 Initiates a tile in the Alfresco results panel.
	 * @param searchTermPacket ESBPacket Alfresco search results
	 * @param index int Index in the search results for the tile to be created
	 * @param objPanel String Name of target panel for the tile
	 * @param td Element Container for the tile
	 */
	protected void buildTile0(Record r, int index, int screenPosition, String objPanel, Element td) {
		RUSSELFileRecord fr = (RUSSELFileRecord) r;
		Vector<String> iDs = null;
		
		if ((td != null) && (searchType.equals(RECENT_TYPE)))
			iDs = PageAssembler.inject(td.getId(), "x", new HTML(templates.getObjectPanelWidget().getText()), false);
		else if (searchType.equals(COLLECTION_TYPE) || searchType.equals(FLR_TYPE) || searchType.equals(SEARCH_TYPE))
			iDs = PageAssembler.inject(objPanel, "x", new HTML(templates.getSearchPanelWidget().getText()), false);
		else if (searchType.equals(PROJECT_TYPE))
			iDs = PageAssembler.inject(objPanel, "x", new HTML(templates.getEPSSProjectObjectPanelWidget().getText()), false);
		else if ((td != null) && (searchType.equals(ASSET_TYPE)))
			iDs = PageAssembler.inject(td.getId(), "x", new HTML(templates.getEPSSAssetObjectPanelWidget().getText()), false);
		else if ((td != null) && (searchType.equals(NOTES_TYPE)))
			iDs = PageAssembler.inject(td.getId(), "x", new HTML(templates.getEPSSNoteAssetObjectWidget().getText()), false);
		else if ((td != null) && (searchType.equals(STRATEGY_TYPE))) {
			Window.alert("handling a strategy search");
			iDs = PageAssembler.inject(td.getId(), "x", new HTML(templates.getEPSSAssetObjectPanelWidget().getText()), false);
		}
		String idPrefix = iDs.firstElement().substring(0, iDs.firstElement().indexOf("-"));
		tileHandlers.add(new TileHandler(this, idPrefix, searchType, fr));
	}

	/**
	 * setWorkflowStates Sets the selection state of all tiles in a handler panel according to the current state in an application workflow.
	 */
	public void setWorkflowStates()
	{
		// Derivative applications have the option to add actions to be processed after processCallbacks has finished.
		// By default, this does not do anything. 
	}

	/**
	 * getTile0 Retrieves the tile handler for the given tile id.
	 * @param id String Desired tile id
	 * @return TileHandler
	 */
	private TileHandler getTile0(String id) {
		TileHandler tile = null;
		for (int i = 0; i<tileHandlers.size(); i++) {
			if (id.contains(tileHandlers.get(i).getIdPrefix())) {
				tile = tileHandlers.get(i);
			}
		}
		return tile;
	}

	public ESBSearchHandler()
	{
		doNotShow.add(SEARCH_TYPE);
		doNotShow.add(COLLECTION_TYPE);
		doNotShow.add(FLR_TYPE);
		doNotShow.add(PROJECT_TYPE);
	}
	/**
	 * toggleSelection Selects or deselects the given tile
	 * @param id String ID of desired tile
	 * @param record ESBPacket Information associated with the tile
	 */
	@Override
	public void toggleSelection(final String id, final Record recordx) {
		RUSSELFileRecord record = (RUSSELFileRecord) recordx;
		if (pendingEdits.contains(record)) {
			pendingEdits.remove(record);
			getTile0(id).deselect();
			((Label)PageAssembler.elementToWidget(id + "State", PageAssembler.LABEL)).removeStyleName("active");
			((Label)PageAssembler.elementToWidget(id + "Select", PageAssembler.LABEL)).removeStyleName("active");
		} else {
			pendingEdits.add(record);
			getTile0(id).select();
			((Label)PageAssembler.elementToWidget(id + "State", PageAssembler.LABEL)).addStyleName("active");
			((Label)PageAssembler.elementToWidget(id + "Select", PageAssembler.LABEL)).addStyleName("active");
		}
		
		if (pendingEdits.size()==0) {
			((Anchor)PageAssembler.elementToWidget("r-objectEditSelected", PageAssembler.A)).removeStyleName("blue");
			((Anchor)PageAssembler.elementToWidget("r-objectEditSelected", PageAssembler.A)).addStyleName("white");
		} else {
			((Anchor)PageAssembler.elementToWidget("r-objectEditSelected", PageAssembler.A)).addStyleName("blue");
			((Anchor)PageAssembler.elementToWidget("r-objectEditSelected", PageAssembler.A)).removeStyleName("white");
		}
	}
	
	/**
	 * hook Launches appropriate Alfresco query and assigns handlers for the response
	 * @param seachbarID String Name of the search bar that informs the Alfresco query
	 * @param objectPanel String Name of the target panel for Alfresco results
	 * @param type String Name of the type of Alfresco search
	 */
	public void hook(final String seachbarID, final String objectPanel, final String type) {
		searchType = type;
		customQuery = null;
		pendingEdits = new Vector<RUSSELFileRecord>();
		t = new Timer() {
				@Override
				public void run() {
					String rawSearchText = ((TextBox)PageAssembler.elementToWidget(seachbarID, 
				   																   PageAssembler.TEXT)).getText().trim();
					final ESBPacket ap = new ESBPacket();
					final String searchText = cleanQuery(rawSearchText);
					ESBPacket sortPack = new ESBPacket();
					if (customQuery!=null&&!searchType.equals(ASSET_TYPE))
						ap.put("sq", customQuery);
					else if (searchType.equals(ASSET_TYPE))
						ap.put("sq", EPSSEditScreen.buildQueryString());
					else if (searchText=="")
						ap.put("sq", "");
					else
						ap.put("sq", searchText);
					ap.put("rows", "100");
					
					if (searchType.equals(PROJECT_TYPE)) {
						sortPack.put("field", "updatedDate_l");
						sortPack.put("order", "desc");
						ap.put("sort", sortPack);
						if (searchText=="")
							ap.put("sq", "mimeType_t:russel/project");
						else
							ap.put("sq", searchText + " AND mimeType_t:russel/project");						
					}
					
					if (searchType.equals(SEARCH_TYPE) || searchType.equals(EDIT_TYPE)) {
						if (searchText=="")
							ap.put("sq", util.buildSearchQueryString());
						else
							ap.put("sq", searchText + util.buildSearchQueryString());
						//TODO fix sorting
						//ap.put("sort", util.buildSearchSortString());
					}
					
					if (searchType.equals(COLLECTION_TYPE)) {
						if (searchText=="")
							ap.put("sq", "uploadedBy_t:" + ESBApi.username + " " + util.buildSearchQueryString());
						else
							ap.put("sq", searchText + " uploadedBy_t:" + ESBApi.username + util.buildSearchQueryString());
						//ap.put("sort", util.buildSearchSortString());
					}
					
					if (searchType.equals(FLR_TYPE)) {	
						if (searchText=="")
							ap.put("sq", "fileName_t:rlr " + util.buildSearchQueryString());
						else
							ap.put("sq", searchText + " fileName_t:rlr " + util.buildSearchQueryString());
						//ap.put("sort", util.buildSearchSortString());
					} 
					
					if (searchType.equals(STRATEGY_TYPE)) {
						if (searchText=="")
							ap.put("sq", util.buildSearchQueryString());
						else
							ap.put("sq", searchText + util.buildSearchQueryString());
						//ap.put("sort", util.buildSearchSortString());
					}
					
					if (searchText==""&&searchType.equals(RECENT_TYPE)) {
						Date currentDate = new Date();
						Date pastDate = new Date();
						pastDate.setDate(pastDate.getDate()-10);
						ap.put("sq", "updatedDate_l:[" + pastDate.getTime() + " TO " + currentDate.getTime() + "]");
						sortPack.put("field", "updatedDate_l");
						sortPack.put("order", "desc");
						ap.put("sort", sortPack);
					}
					
					if (lastQuery!=ap.getString("sq"))
						pagingTokens.clear();
					lastQuery = ap.getString("sq");
					
					if (pagingTokens.size()!=0&&flipSearchPage) {
						flipSearchPage = false;
						ap.put("cursor", pagingTokens.get(0));
					}
					
					ap.put("fields", new RUSSELFileRecord().getFieldList());
					
					ESBApi.search(ap,
								   new ESBCallback<ESBPacket>() {
										public void onFailure(Throwable caught) {
											if (retries>2) {
												retries = 0;
												tileHandlers.clear();
												RootPanel rp = RootPanel.get(objectPanel);
												if (rp!=null) {
													rp.clear();
													int childCount = rp.getElement().getChildCount();
													int grabIndex = 0;
													for (int childIndex=0;childIndex<childCount-((searchType.equals(PROJECT_TYPE))?1:0);childIndex++) { 
														Element removeCursor = null;
														while (((removeCursor= (Element) rp.getElement().getChild(grabIndex))!=null)&&removeCursor.getId().equals("r-newEntity"))
															grabIndex++;
														if (removeCursor!=null)
															rp.getElement().removeChild(removeCursor);
													}
												}
												StatusWindowHandler.createMessage(StatusWindowHandler.getSearchMessageError(searchText), StatusRecord.ALERT_ERROR);
											} else {
												t.schedule(500);
												retries++;
											}
											
											pendingSearch = false;
											customQuery = null;
										}
										
										public void onSuccess(final ESBPacket searchTermPacket) {
											tileHandlers.clear();
											RootPanel rp = RootPanel.get(objectPanel);
											pagingTokens.add(0, searchTermPacket.getObject("obj").getString("cursor"));
											if (rp!=null) {
												rp.clear();
												int childCount = rp.getElement().getChildCount();
												int grabIndex = 0;
												for (int childIndex=0;childIndex<childCount-((searchType.equals(PROJECT_TYPE))?1:0);childIndex++) { 
													Element removeCursor = null;
													while (((removeCursor= (Element) rp.getElement().getChild(grabIndex))!=null)&&removeCursor.getId().equals("r-newEntity"))
														grabIndex++;
													if (removeCursor!=null)
														rp.getElement().removeChild(removeCursor);
												}
												buildThumbnails(objectPanel, searchTermPacket);
												setWorkflowStates();
											}
											pendingSearch = false;
											customQuery = null;
										}
									});	
			}
		};
	
					
		PageAssembler.attachHandler(seachbarID, Event.ONKEYUP, new EventCallback() {
																	@Override
																	public void onEvent(Event event) {
																		if (event.getKeyCode() == KeyCodes.KEY_ENTER&&type!=ASSET_TYPE&&type!=PROJECT_TYPE&&type!=STRATEGY_TYPE&&type!=SEARCH_TYPE) {
																			if (searchType == RECENT_TYPE || searchType == EDIT_TYPE) searchType = SEARCH_TYPE;
																			view.loadResultsScreen(searchType);
																		} 
																		else if (type!=EDIT_TYPE) {
																			if (!pendingSearch) {
																				pendingSearch = true;
																				t.schedule(600);
																			} else {
																				t.cancel();
																				t.schedule(600);
																			}
																		}
																	}
																});
		
		PageAssembler.attachHandler("r-objectEditSelected", Event.ONCLICK, new EventCallback() {
																		   	@Override
																		   	public void onEvent(Event event) {
																		   		view.loadEditScreen(pendingEdits);
																		   	}
																		   });
		
		if (type != EDIT_TYPE && type != ASSET_TYPE) t.schedule(250);
	}
}