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

import com.eduworks.gwt.client.net.api.AlfrescoApi;
import com.eduworks.gwt.client.net.callback.AlfrescoCallback;
import com.eduworks.gwt.client.net.callback.EventCallback;
import com.eduworks.gwt.client.net.packet.AlfrescoPacket;
import com.eduworks.gwt.client.net.packet.StatusPacket;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.gwt.client.util.Date;
import com.eduworks.russel.ui.client.Russel;
import com.eduworks.russel.ui.client.pagebuilder.HtmlTemplates;
import com.eduworks.russel.ui.client.pagebuilder.screen.EPSSEditScreen;
import com.eduworks.russel.ui.client.pagebuilder.screen.EditScreen;
import com.eduworks.russel.ui.client.pagebuilder.screen.ResultsScreen;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

public class AlfrescoSearchHandler {
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
	public static final String NO_SEARCH_RESULTS = "<p>No Search Results Found.</p>";
	
	private boolean terminate = false;
	private boolean pendingSearch = false;
	private Vector<TileHandler> tileHandlers = new Vector<TileHandler>();
	private Vector<AlfrescoPacket> pendingEdits;
	private int retries = 0;
	private Timer t;
	private int tileIndex;
	private String customQuery = null;
	private String searchType;
	private HTML noResults = null;
	
	private void buildTile(AlfrescoPacket searchTermPacket, int index, String objPanel, Element td) {
		Vector<String> iDs = null;
		if (searchTermPacket.getSearchRecords().get(index)!=null&&searchTermPacket.getSearchRecords().get(index).getNodeId()!=null) {
			if ((td != null) && (searchType.equals(RECENT_TYPE)))
				iDs = PageAssembler.inject(td.getId(), "x", new HTML(HtmlTemplates.INSTANCE.getObjectPanelWidget().getText()), false);
			else if (searchType.equals(COLLECTION_TYPE) || searchType.equals(FLR_TYPE) || searchType.equals(SEARCH_TYPE))
				iDs = PageAssembler.inject(objPanel, "x", new HTML(HtmlTemplates.INSTANCE.getSearchPanelWidget().getText()), false);
			else if (searchType.equals(PROJECT_TYPE))
				iDs = PageAssembler.inject(objPanel, "x", new HTML(HtmlTemplates.INSTANCE.getEPSSProjectObjectPanelWidget().getText()), false);
			else if ((td != null) && (searchType.equals(ASSET_TYPE)))
				iDs = PageAssembler.inject(td.getId(), "x", new HTML(HtmlTemplates.INSTANCE.getEPSSAssetObjectPanelWidget().getText()), false);
			else if ((td != null) && (searchType.equals(NOTES_TYPE)))
				iDs = PageAssembler.inject(td.getId(), "x", new HTML(HtmlTemplates.INSTANCE.getEPSSNoteAssetObjectWidget().getText()), false);
			else if ((td != null) && (searchType.equals(STRATEGY_TYPE))) {
				Window.alert("handling a strategy search");
				iDs = PageAssembler.inject(td.getId(), "x", new HTML(HtmlTemplates.INSTANCE.getEPSSAssetObjectPanelWidget().getText()), false);
			}
			String idPrefix = iDs.firstElement().substring(0, iDs.firstElement().indexOf("-"));
			tileHandlers.add(new TileHandler(this, idPrefix, searchType, searchTermPacket.getSearchRecords().get(index)));
		}
	}
	
	private TileHandler getTile(String id) {
		TileHandler tile = null;
		for (int i = 0; i<tileHandlers.size(); i++) {
			if (id.contains(tileHandlers.get(i).getIdPrefix())) {
				tile = tileHandlers.get(i);
			}
		}
		return tile;
	}
	
	public void buildThumbnails(String objPanel, AlfrescoPacket searchTermPacket) {
		
		RootPanel rp = RootPanel.get(objPanel);
		if (rp!=null) {
			Element td = null;
			tileIndex = 0;
			if (noResults!=null)
				rp.remove(noResults);
			
			if (searchTermPacket.getSearchRecords().length()==0) {
				rp.getElement().setAttribute("style", "text-align:center");
				noResults = new HTML(NO_SEARCH_RESULTS); 
				rp.add(noResults);
			} else 
				rp.getElement().setAttribute("style", "");
			
			for (int x=0;x<searchTermPacket.getSearchRecords().length();x+=2) {
				td = null;
				if (((!searchType.equals(SEARCH_TYPE)) && (!searchType.equals(COLLECTION_TYPE)) && (!searchType.equals(FLR_TYPE)) && (!searchType.equals(PROJECT_TYPE)))) {
					// SEARCH_TYPE, FLR_TYPE and COLLECTION_TYPE use the vertStack style, and will not use the table-based layout that requires insertion of cell separators.
					td = DOM.createTD();
					td.setId(x +"-" + rp.getElement().getId());
					rp.getElement().appendChild(td);					
				}
				buildTile(searchTermPacket, x, objPanel, td);
				buildTile(searchTermPacket, x+1, objPanel, td);	
			}
			
			processCallbacks();
		}
	}

	public void toggleSelection(final String id, final AlfrescoPacket record) {
		if (pendingEdits.contains(record)) {
			pendingEdits.remove(record);
			getTile(id).deselect();
			((Label)PageAssembler.elementToWidget(id + "State", PageAssembler.LABEL)).removeStyleName("active");
			((Label)PageAssembler.elementToWidget(id + "Select", PageAssembler.LABEL)).removeStyleName("active");
		} else {
			pendingEdits.add(record);
			getTile(id).select();
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
	
	public void selectAll() {
		TileHandler tile = null;
		for (int i = 0; i<tileHandlers.size(); i++) {
			tile = tileHandlers.get(i);
			if (!tile.getSelectState()) {
				toggleSelection(tile.getIdPrefix(), tile.getSearchRecord());
			}
		}
	}
	
	public void selectNone() {
		TileHandler tile = null;
		for (int i = 0; i<tileHandlers.size(); i++) {
			tile = tileHandlers.get(i);
			if (tile.getSelectState()) {
				toggleSelection(tile.getIdPrefix(), tile.getSearchRecord());
			}
		}		
	}
	
	public void processCallbacks() {
		if ((!terminate) && ((tileHandlers.size()!=0&&tileIndex<tileHandlers.size())))
			tileHandlers.get(tileIndex).fillTile(new EventCallback() {
														@Override
														public void onEvent(Event event) {
															tileIndex++;
															processCallbacks();
														}
													});
	}
	
	public void stop () {
		terminate = true;
		
	}
	
	public void forceSearch () {
		if (!pendingSearch)
			t.schedule(1);
	}
	
	public void forceSearch (String customQuery) {
		this.customQuery = customQuery;
		if (!pendingSearch)
			t.schedule(1);
	}
	
	public static String cleanQuery(String rawSearchText) {
		rawSearchText = rawSearchText.trim();
		if (rawSearchText.equalsIgnoreCase("-")||rawSearchText.equalsIgnoreCase("!")||rawSearchText.equalsIgnoreCase("*")||rawSearchText.equalsIgnoreCase("not")||
			rawSearchText.equalsIgnoreCase("search...")||rawSearchText.equalsIgnoreCase("Enter search terms..."))
			rawSearchText = "";
		String[] searchTerms = rawSearchText.split(" ");
		String fullSearch = "";
		char operator = ' ';
		for (int i=0 ; i<searchTerms.length; i++) {
			if (searchTerms[i] != "")  {
				operator = searchTerms[i].charAt(0);
				if (searchTerms[i].equalsIgnoreCase("AND")||searchTerms[i].equalsIgnoreCase("&&")||
					searchTerms[i].equalsIgnoreCase("OR")||searchTerms[i].equalsIgnoreCase("||")||
					searchTerms[i].equalsIgnoreCase("NOT")||searchTerms[i].equalsIgnoreCase("*")) {
					fullSearch += " "+searchTerms[i];
				} else if (searchTerms[i].indexOf(":") != -1) {
					fullSearch += " "+searchTerms[i];
				} else if (operator == '-'||operator == '+'||operator == '!'||operator == '|'||operator == '~'||operator == '='){
					fullSearch += " "+operator+"ALL:"+searchTerms[i].substring(1);
				} else {
					fullSearch += " ALL:"+searchTerms[i];
				}
			}
		}
		return fullSearch.trim();
	}
	
	public void hook(final String seachbarID, final String objectPanel, final String type) {
		searchType = type;
		customQuery = null;
		pendingEdits = new Vector<AlfrescoPacket>();
		t = new Timer() {
				@Override
				public void run() {
					String rawSearchText = ((TextBox)PageAssembler.elementToWidget(seachbarID, 
				   																   PageAssembler.TEXT)).getText().trim();
					final AlfrescoPacket ap = AlfrescoPacket.makePacket();
					final String searchText = cleanQuery(rawSearchText);
					if (customQuery!=null&&!searchType.equals(ASSET_TYPE))
						ap.addKeyValue("terms", customQuery + " ASPECT:\"russel:metaTest\"");
					else if (searchType.equals(ASSET_TYPE))
						ap.addKeyValue("terms", EPSSEditScreen.buildQueryString() + " ASPECT:\"russel:metaTest\"");
					else if (searchText=="")
						ap.addKeyValue("terms", "ASPECT:\"russel:metaTest\"");
					else
						ap.addKeyValue("terms", searchText + " ASPECT:\"russel:metaTest\"");
					ap.addKeyValue("rowLimit", 100);
					ap.addKeyValue("sort", "");
					ap.addKeyValue("page", 0);
					
					if (searchType.equals(PROJECT_TYPE)) {
						ap.addKeyValue("sort", "cm:modified|false");
						if (searchText=="")
							ap.addKeyValue("terms", "cm:name:rpf ASPECT:\"russel:metaTest\"");
						else
							ap.addKeyValue("terms", searchText + " cm:name:rpf ASPECT:\"russel:metaTest\"");						
					}
					
					if (searchType.equals(SEARCH_TYPE)) {
						if (searchText=="")
							ap.addKeyValue("terms", "ASPECT:\"russel:metaTest\"" + ResultsScreen.buildSearchQueryString());
						else
							ap.addKeyValue("terms", searchText + " ASPECT:\"russel:metaTest\"" + ResultsScreen.buildSearchQueryString());
						ap.addKeyValue("sort", ResultsScreen.buildSearchSortString());
					}
					
					if (searchType.equals(COLLECTION_TYPE)) {
						if (searchText=="")
							ap.addKeyValue("terms", "creator:" + AlfrescoApi.username + " ASPECT:\"russel:metaTest\"" + ResultsScreen.buildSearchQueryString());
						else
							ap.addKeyValue("terms", searchText + " creator:" + AlfrescoApi.username + " ASPECT:\"russel:metaTest\"" + ResultsScreen.buildSearchQueryString());
						ap.addKeyValue("sort", ResultsScreen.buildSearchSortString());
					}
					
					if (searchType.equals(FLR_TYPE)) {	
						if (searchText=="")
							ap.addKeyValue("terms", "cm:name:rlr ASPECT:\"russel:metaTest\"" + ResultsScreen.buildSearchQueryString());
						else
							ap.addKeyValue("terms", searchText + " cm:name:rlr ASPECT:\"russel:metaTest\"" + ResultsScreen.buildSearchQueryString());
						ap.addKeyValue("sort", ResultsScreen.buildSearchSortString());
					} 
					
					if (searchType.equals(STRATEGY_TYPE)) {
						if (searchText=="")
							ap.addKeyValue("terms", "ASPECT:\"russel:metaTest\"" + ResultsScreen.buildSearchQueryString());
						else
							ap.addKeyValue("terms", searchText + " ASPECT:\"russel:metaTest\"" + ResultsScreen.buildSearchQueryString());
						ap.addKeyValue("sort", ResultsScreen.buildSearchSortString());
					}
					
					if (searchText==""&&searchType.equals(RECENT_TYPE)) {
						Date currentDate = new Date();
						Date pastDate = new Date();
						pastDate.setDate(pastDate.getDate()-10);
						String currentMonthPadded = ((currentDate.getMonth()+1)<10)? "0" + (currentDate.getMonth()+1):""+(currentDate.getMonth()+1);
						String currentDayPadded = ((currentDate.getDate())<10)? "0" + (currentDate.getDate()):""+(currentDate.getDate());
						String pastMonthPadded = ((pastDate.getMonth()+1)<10)? "0" + (pastDate.getMonth()+1):""+(pastDate.getMonth()+1);
						String pastDayPadded = ((pastDate.getDate())<10)? "0" + (pastDate.getDate()):""+(pastDate.getDate());
						ap.addKeyValue("terms", "modified:[\"" + pastDate.getYear() + "-" + pastMonthPadded + "-" + pastDayPadded + "\" to \"" +  currentDate.getYear() + "-" + currentMonthPadded + "-" + currentDayPadded + "\"] ASPECT:\"russel:metaTest\"");
						ap.addKeyValue("sort", "cm:modified|false");
					}
					
					AlfrescoApi.search(ap,
									   new AlfrescoCallback<AlfrescoPacket>() {
											@Override
											public void onFailure(Throwable caught) {
												if (retries>1) {
													retries = 0;
													tileHandlers.clear();
													RootPanel rp = RootPanel.get(objectPanel);
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
													StatusWindowHandler.createMessage(StatusWindowHandler.getSearchMessageError(searchText), StatusPacket.ALERT_ERROR);
												} else {
													t.schedule(500);
													retries++;
												}
												
												pendingSearch = false;
												customQuery = null;
											}
											
											@Override
											public void onSuccess(final AlfrescoPacket SearchTermPacket) {
												tileHandlers.clear();
												RootPanel rp = RootPanel.get(objectPanel);
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
												buildThumbnails(objectPanel, SearchTermPacket);
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
																			if (searchType == RECENT_TYPE)  searchType = SEARCH_TYPE;
																			ResultsScreen rs = new ResultsScreen();
																	   		rs.searchType = searchType;
																			Russel.view.loadScreen(rs, true);
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
																		   		Russel.view.loadScreen(new EditScreen(pendingEdits), true);
																		   	}
																		   });
		
		if (type != EDIT_TYPE && type != ASSET_TYPE) t.schedule(250);
	}
}