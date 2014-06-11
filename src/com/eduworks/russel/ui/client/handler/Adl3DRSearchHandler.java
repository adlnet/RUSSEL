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
import com.eduworks.gwt.client.model.ThreeDRRecord;
import com.eduworks.gwt.client.net.api.Adl3DRApi;
import com.eduworks.gwt.client.net.callback.ESBCallback;
import com.eduworks.gwt.client.net.callback.EventCallback;
import com.eduworks.gwt.client.net.packet.ESBPacket;
import com.eduworks.gwt.client.net.packet.AjaxPacket;
import com.eduworks.gwt.client.net.packet.ESBPacket;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.russel.ui.client.model.StatusRecord;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Adl3DRSearchHandler
 * Defines globals, methods and handlers for the ADL 3DR queries.
 * 
 * @author Eduworks Corporation
 */
public class Adl3DRSearchHandler extends SearchHandler{
	public static final String SEARCH3DR_TYPE = "search3DR";
	public static final String ASSET3DR_TYPE = "asset3DR";
	public static final String ADL3DR_TYPE = "3DR";
	/**
	 * buildTile0 Initiates a tile in the 3DR results panel.
	 * @param searchTermPacket ESBPacket 3DR search results
	 * @param index int Index in the search results for the tile to be created
	 * @param objPanel String Name of target panel for the tile
	 * @param td Element Container for the tile
	 */
	protected void buildTile0(Record searchTermPacketx, int index, int screenPosition, String objPanel, Element td) {
		ThreeDRRecord searchTermPacket = (ThreeDRRecord) searchTermPacketx;
		Vector<String> iDs = null;
		if (index < searchTermPacket.getSearchRecords().size()) {
			ThreeDRRecord alfrescoPacket = searchTermPacket.getSearchRecords().get(index);
			if (searchTermPacket.getSearchRecords().get(index)!=null&&alfrescoPacket.getGuid()!=null) {
				if (searchType.equals(ADL3DR_TYPE) || searchType.equals(SEARCH3DR_TYPE)) 
					iDs = PageAssembler.inject(objPanel, "x", new HTML(templates.get3DRObjectPanelWidget().getText()), false);
				else if ((td != null) && (searchType.equals(ASSET3DR_TYPE)))
					iDs = PageAssembler.inject(td.getId(), "x", new HTML(templates.getEPSS3DRAssetObjectPanelWidget().getText()), false);
				String idPrefix = iDs.firstElement().substring(0, iDs.firstElement().indexOf("-"));
				//Need to retrieve the other info from 3DR
//				ESBPacket tileInfo = new ESBPacket();
//				alfrescoPacket.put("title", alfrescoPacket.getTitle());
//				alfrescoPacket.put("description", alfrescoPacket.getDataLink());
//				alfrescoPacket.setFilename(alfrescoPacket.getFilename());
//				tileInfo.put("id", alfrescoPacket.getGuid());
				tileHandlers.add(new TileHandler(null, idPrefix, searchType, alfrescoPacket));
			}			
		}
	}
	public Adl3DRSearchHandler()
	{
		showOnly.add(ASSET3DR_TYPE);
	}
	/**
	 * hook Launches appropriate ADL 3DR query and assigns handlers for the response
	 * @param seachbarID String Name of the search bar that informs the 3DR query
	 * @param objectPanel String Name of the target panel for 3DR results
	 * @param type String Name of the type of 3DR search
	 */
	public void hook(final String seachbarID, final String objectPanel, final String type) {

		searchType = type;
		customQuery = null;
		t = new Timer() {
				@Override
				public void run() {
					if (Adl3DRApi.ADL3DR_OPTION_MODE.equals(Adl3DRApi.ADL3DR_DISABLED)) {
						StatusWindowHandler.createMessage(StatusWindowHandler.get3DRDisabledError("Search"),
								  StatusRecord.ALERT_ERROR);						
					}
					else {
						final StatusRecord adl3drStatus = StatusWindowHandler.createMessage(StatusWindowHandler.get3DRQueryMessageBusy(),
								  StatusRecord.ALERT_BUSY);
						String rawSearchText = ((TextBox)PageAssembler.elementToWidget(seachbarID,PageAssembler.TEXT)).getText().trim();
						if (rawSearchText.equalsIgnoreCase("-")||rawSearchText.equalsIgnoreCase("!")||rawSearchText.equalsIgnoreCase("*")||rawSearchText.equalsIgnoreCase("not")||
								rawSearchText.equalsIgnoreCase("search...")||rawSearchText.equalsIgnoreCase("enter search terms..."))
								rawSearchText = "";
						if (rawSearchText.equals("")) {
							adl3drStatus.setMessage(StatusWindowHandler.get3DRQueryMessageEmpty());
							adl3drStatus.setState(StatusRecord.ALERT_WARNING);
							StatusWindowHandler.alterMessage(adl3drStatus);
						}
						else {
							String searchText = rawSearchText;
							
							Adl3DRApi.searchADL3DR(searchText, new ESBCallback<ESBPacket>() {
																@Override
																public void onSuccess(ESBPacket result) {
																	adl3drStatus.setMessage(StatusWindowHandler.get3DRQueryMessageDone());
																	adl3drStatus.setState(StatusRecord.ALERT_SUCCESS);
																	StatusWindowHandler.alterMessage(adl3drStatus);
																	
																	tileHandlers.clear();
																	RootPanel rp = RootPanel.get(objectPanel);
																	if (rp!=null) {
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
																	}
																	pendingSearch = false;
																	customQuery = null;
																}
																
																@Override
																public void onFailure(Throwable caught) {
																	adl3drStatus.setMessage(StatusWindowHandler.get3DRQueryMessageError());
																	adl3drStatus.setState(StatusRecord.ALERT_ERROR);
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
	@Override
	public void toggleSelection(String id, Record record)
	{
		throw new RuntimeException("Cannot modify 3dr metadata");
	}

}