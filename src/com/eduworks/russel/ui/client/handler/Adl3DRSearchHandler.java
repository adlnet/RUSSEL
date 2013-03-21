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

import com.eduworks.gwt.client.net.api.Adl3DRApi;
import com.eduworks.gwt.client.net.callback.Adl3DRCallback;
import com.eduworks.gwt.client.net.callback.EventCallback;
import com.eduworks.gwt.client.net.packet.Adl3DRPacket;
import com.eduworks.gwt.client.net.packet.AlfrescoPacket;
import com.eduworks.gwt.client.net.packet.StatusPacket;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.russel.ui.client.Russel;
import com.eduworks.russel.ui.client.pagebuilder.HtmlTemplates;
import com.eduworks.russel.ui.client.pagebuilder.screen.ResultsScreen;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

public class Adl3DRSearchHandler {
	public static final String SEARCH3DR_TYPE = "search3DR";
	public static final String ASSET3DR_TYPE = "asset3DR";
	public static final String ADL3DR_TYPE = "3DR";
	public static final String NO_SEARCH_RESULTS = "<p>No Search Results Found.</p>";	
	private boolean terminate = false;
	private boolean pendingSearch = false;
	private Vector<TileHandler> tileHandlers = new Vector<TileHandler>();
	private int retries = 0;
	private Timer t;
	private int tileIndex;
	private String customQuery = null;
	private String searchType;
	private HTML noResults = null;
	
	private void buildTile0(Adl3DRPacket searchTermPacket, int index, String objPanel, Element td) {
		Vector<String> iDs = null;
		if (index < searchTermPacket.getSearchRecords().length()) {
			if (searchTermPacket.getSearchRecords().get(index)!=null&&searchTermPacket.getSearchRecords().get(index).getNodeId()!=null) {
				if (searchType.equals(ADL3DR_TYPE) || searchType.equals(SEARCH3DR_TYPE)) 
					iDs = PageAssembler.inject(objPanel, "x", new HTML(HtmlTemplates.INSTANCE.get3DRObjectPanelWidget().getText()), false);
				else if ((td != null) && (searchType.equals(ASSET3DR_TYPE)))
					iDs = PageAssembler.inject(td.getId(), "x", new HTML(HtmlTemplates.INSTANCE.getEPSS3DRAssetObjectPanelWidget().getText()), false);
				String idPrefix = iDs.firstElement().substring(0, iDs.firstElement().indexOf("-"));
				//Need to retrieve the other info from 3DR
				AlfrescoPacket tileInfo = AlfrescoPacket.makePacket();
				tileInfo.addKeyValue("title", searchTermPacket.getSearchRecords().get(index).getTitle());
				tileInfo.addKeyValue("description", searchTermPacket.getSearchRecords().get(index).getDataLink());
				tileInfo.addKeyValue("name", searchTermPacket.getSearchRecords().get(index).getFilename());
				tileInfo.addKeyValue("id", searchTermPacket.getSearchRecords().get(index).getNodeId());
				tileHandlers.add(new TileHandler(null, idPrefix, searchType, tileInfo));
			}			
		}
	}
	
	public void buildThumbnails(String objPanel, Adl3DRPacket searchTermPacket) {
		
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
				if (searchType.equals(ASSET3DR_TYPE)) {
					// SEARCH3DR_TYPE uses the vertStack style, and will not use the table-based layout that requires insertion of cell separators.
					td = DOM.createTD();
					td.setId(x +"-" + rp.getElement().getId());
					rp.getElement().appendChild(td);					
				}
				buildTile0(searchTermPacket, x, objPanel, td);
				buildTile0(searchTermPacket, x+1, objPanel, td);	
			}
			
			processCallbacks();
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
	
	public void hook(final String seachbarID, final String objectPanel, final String type) {

		searchType = type;
		customQuery = null;
		t = new Timer() {
				@Override
				public void run() {
					if (Adl3DRApi.ADL3DR_OPTION_MODE.equals(Adl3DRApi.ADL3DR_DISABLED)) {
						StatusWindowHandler.createMessage(StatusWindowHandler.get3DRDisabledError("Search"),
								  StatusPacket.ALERT_ERROR);						
					}
					else {
						final StatusPacket adl3drStatus = StatusWindowHandler.createMessage(StatusWindowHandler.get3DRQueryMessageBusy(),
								  StatusPacket.ALERT_BUSY);
						String rawSearchText = ((TextBox)PageAssembler.elementToWidget(seachbarID,PageAssembler.TEXT)).getText().trim();
						if (rawSearchText.equalsIgnoreCase("-")||rawSearchText.equalsIgnoreCase("!")||rawSearchText.equalsIgnoreCase("*")||rawSearchText.equalsIgnoreCase("not")||
								rawSearchText.equalsIgnoreCase("search...")||rawSearchText.equalsIgnoreCase("enter search terms..."))
								rawSearchText = "";
						if (rawSearchText.equals("")) {
							adl3drStatus.setMessage(StatusWindowHandler.get3DRQueryMessageEmpty());
							adl3drStatus.setState(StatusPacket.ALERT_WARNING);
							StatusWindowHandler.alterMessage(adl3drStatus);
						}
						else {
							String searchText = rawSearchText;
							
							Adl3DRApi.searchADL3DR(searchText, new Adl3DRCallback<Adl3DRPacket>() {
																@Override
																public void onSuccess(Adl3DRPacket result) {
																	adl3drStatus.setMessage(StatusWindowHandler.get3DRQueryMessageDone());
																	adl3drStatus.setState(StatusPacket.ALERT_SUCCESS);
																	StatusWindowHandler.alterMessage(adl3drStatus);
																	
																	tileHandlers.clear();
																	RootPanel rp = RootPanel.get(objectPanel);
																	rp.clear();
																	int childCount = rp.getElement().getChildCount();
																	int grabIndex = 0;
																	for (int childIndex=0;childIndex<childCount;childIndex++) { 
																		Element removeCursor = null;
																		while (((removeCursor= (Element) rp.getElement().getChild(grabIndex))!=null)&&removeCursor.getId().equals("r-newEntity"))
																			grabIndex++;
																		if (removeCursor!=null)
																			rp.getElement().removeChild(removeCursor);
																	}
																	buildThumbnails(objectPanel, result);
																	pendingSearch = false;
																	customQuery = null;
																}
																
																@Override
																public void onFailure(Throwable caught) {
																	adl3drStatus.setMessage(StatusWindowHandler.get3DRQueryMessageError());
																	adl3drStatus.setState(StatusPacket.ALERT_ERROR);
																	StatusWindowHandler.alterMessage(adl3drStatus);
																	if (retries>1) {
																		retries = 0;
																		tileHandlers.clear();
																		RootPanel rp = RootPanel.get(objectPanel);
																		rp.clear();
																		int childCount = rp.getElement().getChildCount();
																		int grabIndex = 0;
																		for (int childIndex=0;childIndex<childCount;childIndex++) { 
																			Element removeCursor = null;
																			while (((removeCursor=(Element) rp.getElement().getChild(grabIndex))!=null)&&removeCursor.getId().equals("r-newEntity")) {
																				grabIndex++;
																			}
																			if (removeCursor!=null)
																				rp.getElement().removeChild(removeCursor);
																		}
																	} 
																	else {
																		t.schedule(500);
																		retries++;
																	}
																	
																	pendingSearch = false;
																	customQuery = null;
																}
							});
						}
					}
				}
			};

					
		PageAssembler.attachHandler(seachbarID, Event.ONKEYUP, new EventCallback() {
																	@Override
																	public void onEvent(Event event) {
																		if (!pendingSearch) {
																			pendingSearch = true;
																			t.schedule(600);
																		} else {
																			t.cancel();
																			t.schedule(600);
																		}
																	}
																});
		
		
		t.schedule(250);
	}

	
}