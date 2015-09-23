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

import com.eduworks.gwt.client.model.Record;
import com.eduworks.gwt.client.model.StatusRecord;
import com.eduworks.gwt.client.net.callback.ESBCallback;
import com.eduworks.gwt.client.net.callback.EventCallback;
import com.eduworks.gwt.client.net.packet.ESBPacket;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.gwt.client.util.Browser;
import com.eduworks.gwt.client.util.MathUtil;
import com.eduworks.russel.ui.client.Russel;
import com.eduworks.russel.ui.client.extractor.AssetExtractor;
import com.eduworks.russel.ui.client.model.ProjectRecord;
import com.eduworks.russel.ui.client.model.RUSSELFileRecord;
import com.eduworks.russel.ui.client.net.RusselApi;
import com.eduworks.russel.ui.client.pagebuilder.MetaBuilder;
import com.eduworks.russel.ui.client.pagebuilder.screen.DetailScreen;
import com.eduworks.russel.ui.client.pagebuilder.screen.EPSSScreen;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * TileHandler
 * Defines globals, methods and handlers for tiles.
 * 
 * @author Eduworks Corporation
 */
public class TileHandler {
	private SearchHandler ash;
	final private RUSSELFileRecord searchRecord;
	final private String tileType;
	private String idPrefix;
	private TileHandler tile;
	private MetaBuilder mb = new MetaBuilder(MetaBuilder.DETAIL_SCREEN);
	private Boolean selectState;

	/**
	 * TileHandler Constructor for the class
	 * @param asHandler SearchHandler Hook to search handler
	 * @param thumbIdPrefix String Thumbnail id
	 * @param searchTileType String
	 * @param searchTermRecord ESBPacket Information for the tile
	 */
	public TileHandler(SearchHandler asHandler, String thumbIdPrefix, String searchTileType, Record searchTermRecord) {
		this.tile = this;
		this.ash = asHandler;
		this.searchRecord = (RUSSELFileRecord) searchTermRecord;
		this.tileType = searchTileType;
		this.idPrefix = thumbIdPrefix;
		this.selectState = false;
		addHooks();
	}

	/**
	 * getIdPrefix Returns the ID value for the next tile
	 * @return String
	 */
	public String getIdPrefix() {
		return this.idPrefix;
	}

	/**
	 * getSelectState Returns the state value for tile selection
	 * @return Boolean true if selected, false if not
	 */
	public Boolean getSelectState() {
		return this.selectState;
	}

	/**
	 * getSearchRecord Returns the information tied to the tile
	 * @return ESBPacket
	 */
	public RUSSELFileRecord getSearchRecord() {
		return this.searchRecord;
	}

	/**
	 * select Forces tile to change to selected mode
	 */
	public void select() {
		if (!this.selectState) {
			this.selectState = true;
		}
	}

	/**
	 * deselect Forces tile to change to deselected mode
	 */
	public void deselect() {
		if (this.selectState) {
			this.selectState = false;
		}
	}

	/**
	 * addHooks Assigns the appropriate handlers to the tile based on search type
	 */
	public void addHooks() {	
		PageAssembler.attachHandler(idPrefix + "-objectClick", Event.ONCLICK, new EventCallback() {
			@Override
			public void onEvent(Event event)
			{
				if (tile.tileType.equals(SearchHandler.TYPE_PROJECT)) {
					RusselApi.getResource(tile.searchRecord.getGuid(),
										  true,
										  new ESBCallback<ESBPacket>() {
												@Override
												public void onSuccess(ESBPacket alfrescoPacket) {
													ProjectRecord pr = new ProjectRecord(alfrescoPacket.getContentString(), searchRecord);
													Russel.screen.loadScreen(new EPSSScreen(pr), true);
												}
				
												@Override
												public void onFailure(Throwable caught) {
													StatusHandler.createMessage(StatusHandler.getProjectLoadMessageError(tile.searchRecord.getFilename()),
															  StatusRecord.ALERT_ERROR);
												}
										  });
				} else if (tile.tileType.equals(SearchHandler.TYPE_RECENT)
						|| tile.tileType.equals(SearchHandler.TYPE_ASSET)
						|| tile.tileType.equals(SearchHandler.TYPE_SEARCH)
						|| tile.tileType.equals(SearchHandler.TYPE_COLLECTION)
						|| tile.tileType.equals(SearchHandler.SOURCE_LEARNING_REGISTRY))
					Russel.screen.loadScreen(new DetailScreen(tile.searchRecord, tile), true);
			}
		});
		PageAssembler.attachHandler(idPrefix + "-objectSelect", Event.ONCLICK, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				ash.toggleSelection(idPrefix + "-object", searchRecord);
			}
		});

		PageAssembler.attachHandler(idPrefix + "-objectOpen", Event.ONCLICK, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				if (tileType.equals(SearchHandler.TYPE_PROJECT)) {
					RusselApi.getResource(tile.searchRecord.getGuid(),
										  true,
										  new ESBCallback<ESBPacket>() {
												@Override
												public void onSuccess(ESBPacket alfrescoPacket) {
													ProjectRecord pr = new ProjectRecord(alfrescoPacket.getContentString(), searchRecord);
													Russel.screen.loadScreen(new EPSSScreen(pr), true);
												}
				
												@Override
												public void onFailure(Throwable caught) {
													StatusHandler.createMessage(StatusHandler.getProjectLoadMessageError(tile.searchRecord.getFilename()),
															  StatusRecord.ALERT_ERROR);
												}
										  });
				} else if (tileType.equals(SearchHandler.TYPE_RECENT)||tileType.equals(SearchHandler.TYPE_ASSET) ||tileType.equals(SearchHandler.TYPE_SEARCH)||
					tileType.equals(SearchHandler.TYPE_COLLECTION)||tileType.equals(SearchHandler.SOURCE_LEARNING_REGISTRY)||tileType.equals(SearchHandler.TYPE_PROJECT_ASSET))
					Russel.screen.loadScreen(new DetailScreen(searchRecord, tile), true);
			}
		});

		PageAssembler.attachHandler(idPrefix + "-objectDelete", Event.ONCLICK, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				if (Window.confirm("Are you sure you wish to delete this item?")) {
					final StatusRecord status = StatusHandler.createMessage(StatusHandler.getDeleteMessageBusy(searchRecord.getFilename()),
							StatusRecord.ALERT_BUSY);
					RusselApi.deleteResource(searchRecord.getGuid(), new ESBCallback<ESBPacket>() {
						@Override
						public void onFailure(Throwable caught) {
							status.setMessage(StatusHandler.getDeleteMessageError(searchRecord.getFilename()));
							status.setState(StatusRecord.ALERT_ERROR);
							StatusHandler.alterMessage(status);
						}

						@Override
						public void onSuccess(ESBPacket result) {
							status.setMessage(StatusHandler.getDeleteMessageDone(searchRecord.getFilename()));
							status.setState(StatusRecord.ALERT_SUCCESS);
							StatusHandler.alterMessage(status);
							DOM.getElementById(idPrefix+"-object").removeFromParent();
						}
					});
				}
			}
		});
		
		PageAssembler.attachHandler(idPrefix + "-objectRemove", Event.ONCLICK, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				DOM.getElementById(idPrefix+"-object").getParentElement().getParentElement().removeFromParent();
				EPSSScreen s = ((EPSSScreen)ash.getScreen());
				s.removeAssetFromProject(searchRecord);
			}
		});

		PageAssembler.attachHandler(idPrefix + "-objectAdd", Event.ONCLICK, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				DOM.getElementById(idPrefix+"-object").removeFromParent();
				EPSSScreen s = ((EPSSScreen)ash.getScreen());
				s.addAssetToProject(searchRecord);
			}
		});
		
		PageAssembler.attachHandler(idPrefix + "-objectNotes", Event.ONCLICK, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				DOM.getElementById("projectAssetTitle").setInnerText(searchRecord.getFilename());
				EPSSScreen s = ((EPSSScreen)ash.getScreen());
				s.setActiveAsset(searchRecord);
			}
		});
	}

	/**
	 * missingFileTile Updates tile information and settings if the Alfresco node is no longer available.
	 */
	public void missingFileTile() {
		DOM.getElementById(idPrefix + "-objectState").addClassName("missing");
		DOM.getElementById(idPrefix + "-objectAlerts").removeClassName("hide");
		DOM.getElementById(idPrefix + "-objectOpen").addClassName("hide");
		((Label)PageAssembler.elementToWidget(idPrefix + "-objectTitle", PageAssembler.LABEL)).setText("MISSING FILE");
		((Label)PageAssembler.elementToWidget(idPrefix + "-objectDescription", PageAssembler.LABEL)).setText(searchRecord.getFilename());
		((Label)PageAssembler.elementToWidget(idPrefix + "-objectTitleBack", PageAssembler.LABEL)).setText("'"+ searchRecord.getFilename()+"' has been deleted.");
	}

	/**
	 * fillTile Renders the tile information in the tile display based on search type, maintaining current selection state
	 * @param callback EventCallback
	 */
	public void fillTile(final EventCallback callback) {
		if (this.getSelectState())
			((Label)PageAssembler.elementToWidget(idPrefix + "-objectState", PageAssembler.LABEL)).setStyleName("cube file active");
		else
			((Label)PageAssembler.elementToWidget(idPrefix + "-objectState", PageAssembler.LABEL)).setStyleName("cube file");
		((Label)PageAssembler.elementToWidget(idPrefix + "-objectState", PageAssembler.LABEL)).addStyleName(AssetExtractor.getFileType(searchRecord.getFilename()));	
		((Label)PageAssembler.elementToWidget(idPrefix + "-objectRating", PageAssembler.LABEL)).setText(searchRecord.getRating() + " stars");
		double percent = MathUtil.roundNumber(searchRecord.getRating()/5.0 * 100, 2);
		if (DOM.getElementById(idPrefix + "-objectRating")!=null) {
			DOM.getElementById(idPrefix + "-objectRating").setAttribute("style", "width: " +percent+"%");
			((Label)PageAssembler.elementToWidget(idPrefix + "-objectRating", PageAssembler.LABEL)).setText(searchRecord.getRating() + " stars");
		}

		Boolean fouo = searchRecord.getFOUO();
		if (fouo) {
			((Label)PageAssembler.elementToWidget(idPrefix + "-objectAlerts", PageAssembler.LABEL)).setStyleName("status-alert");
			((Label)PageAssembler.elementToWidget(idPrefix + "-objectAlertsBack", PageAssembler.LABEL)).setStyleName("status-alert");
			((Label)PageAssembler.elementToWidget(idPrefix + "-objectAlertText", PageAssembler.LABEL)).setText("FOUO");
		} else {
			((Label)PageAssembler.elementToWidget(idPrefix + "-objectAlerts", PageAssembler.LABEL)).setStyleName("status-alert hide");
			((Label)PageAssembler.elementToWidget(idPrefix + "-objectAlertsBack", PageAssembler.LABEL)).setStyleName("status-alert hide");
			((Label)PageAssembler.elementToWidget(idPrefix + "-objectAlertText", PageAssembler.LABEL)).setText("");
		}

		String val = searchRecord.getTitle();
		if (val!=null&&val.trim()!="")
			((Label)PageAssembler.elementToWidget(idPrefix + "-objectTitle", PageAssembler.LABEL)).setText(val);
		else 
			((Label)PageAssembler.elementToWidget(idPrefix + "-objectTitle", PageAssembler.LABEL)).setText(searchRecord.getFilename());

		if (searchRecord.getCommentCount()>0) {
			((Label)PageAssembler.elementToWidget(idPrefix + "-objectComments", PageAssembler.LABEL)).setText(searchRecord.getCommentCount()+"");
			((Label)PageAssembler.elementToWidget(idPrefix + "-objectComments", PageAssembler.LABEL)).removeStyleName("hidden");
		} else
			((Label)PageAssembler.elementToWidget(idPrefix + "-objectComments", PageAssembler.LABEL)).addStyleName("hidden");
		final String description = (searchRecord.getDescription()=="")?"Click to Edit":searchRecord.getDescription();
		((Label)PageAssembler.elementToWidget(idPrefix + "-objectTitleBack", PageAssembler.LABEL)).setText(searchRecord.getFilename() + "  --  " + description);
		
		if (searchRecord.getThumbnailURL()!="") {
			if (!Browser.isBadIE())
				DOM.getElementById(idPrefix + "-objectDescription").setAttribute("style", "background-image:url(" + searchRecord.getThumbnailURL() + ");");
			else {
				Image thumb = new Image();
				thumb.addErrorHandler(new ErrorHandler() {
					@Override
					public void onError(ErrorEvent event) {
						((Label)PageAssembler.elementToWidget(idPrefix + "-objectDescription", PageAssembler.LABEL)).setText(description);
					}
				});
				thumb.setUrl(searchRecord.getThumbnailURL());
				RootPanel.get(idPrefix + "-objectDescription").add(thumb);
			}
		}
		
		if (callback!=null)
			callback.onEvent(null);
	}

	/**
	 * refreshTile Retrieves the latest information pertaining to the node represented by the tile, maintaining current selection state
	 * @param callback EventCallback
	 */
	public void refreshTile(final EventCallback callback) {
		if (this.getSelectState())
			((Label)PageAssembler.elementToWidget(idPrefix + "-objectState", PageAssembler.LABEL)).setStyleName("cube file active");
		else
			((Label)PageAssembler.elementToWidget(idPrefix + "-objectState", PageAssembler.LABEL)).setStyleName("cube file");

		((Label)PageAssembler.elementToWidget(idPrefix + "-objectState", PageAssembler.LABEL)).addStyleName(AssetExtractor.getFileType(searchRecord.getFilename()));

		((Label)PageAssembler.elementToWidget(idPrefix + "-objectRating", PageAssembler.LABEL)).setText(searchRecord.getRating() + " stars");
		long percent = Math.round(searchRecord.getRating()/5.0 * 100);
		if (DOM.getElementById(idPrefix + "-objectRating")!=null)
			DOM.getElementById(idPrefix + "-objectRating").setAttribute("style", "width: " + percent+"%");
		
		RusselApi.getResourceMetadata(searchRecord.getGuid(),
									  false,
									  new ESBCallback<ESBPacket>() {
										@Override
										public void onSuccess(final ESBPacket ap) {
											searchRecord.parseESBPacket(ap);
											Boolean fouo = searchRecord.getFOUO();
											if (fouo) {
												((Label)PageAssembler.elementToWidget(idPrefix + "-objectAlerts", PageAssembler.LABEL)).setStyleName("status-alert");
												((Label)PageAssembler.elementToWidget(idPrefix + "-objectAlertsBack", PageAssembler.LABEL)).setStyleName("status-alert");
												((Label)PageAssembler.elementToWidget(idPrefix + "-objectAlertText", PageAssembler.LABEL)).setText("FOUO");
											} else {
												((Label)PageAssembler.elementToWidget(idPrefix + "-objectAlerts", PageAssembler.LABEL)).setStyleName("status-alert hide");
												((Label)PageAssembler.elementToWidget(idPrefix + "-objectAlertsBack", PageAssembler.LABEL)).setStyleName("status-alert hide");
												((Label)PageAssembler.elementToWidget(idPrefix + "-objectAlertText", PageAssembler.LABEL)).setText("");
											}
					
											if (searchRecord.getCommentCount()>0) {
												((Label)PageAssembler.elementToWidget(idPrefix + "-objectComments", PageAssembler.LABEL)).setText(searchRecord.getCommentCount()+"");
												((Label)PageAssembler.elementToWidget(idPrefix + "-objectComments", PageAssembler.LABEL)).removeStyleName("hidden");
											} else
												((Label)PageAssembler.elementToWidget(idPrefix + "-objectComments", PageAssembler.LABEL)).addStyleName("hidden");
					
											String title = searchRecord.getTitle();
											if (title!=null&&title.trim()!="")
												((Label)PageAssembler.elementToWidget(idPrefix + "-objectTitle", PageAssembler.LABEL)).setText(title);
											else 
												((Label)PageAssembler.elementToWidget(idPrefix + "-objectTitle", PageAssembler.LABEL)).setText(searchRecord.getFilename());
					
											final String description = searchRecord.getDescription();
											((Label)PageAssembler.elementToWidget(idPrefix + "-objectTitleBack", PageAssembler.LABEL)).setText(searchRecord.getFilename() + "  --  " + description);
					
											if (searchRecord.getThumbnailURL()!="") {
												if (!Browser.isBadIE())
													DOM.getElementById(idPrefix + "-objectDescription").setAttribute("style", "background-image:url(" + searchRecord.getThumbnailURL() + ");");
												else {
													Image thumb = new Image();
													thumb.addErrorHandler(new ErrorHandler() {
														@Override
														public void onError(ErrorEvent event) {
															mb.addMetaDataToField(idPrefix + "-objectDescription", description, true);
														}
													});
													thumb.setUrl(searchRecord.getThumbnailURL());
													RootPanel.get(idPrefix + "-objectDescription").add(thumb);
												}
											}
										}
					
										@Override
										public void onFailure(Throwable caught) {
											StatusHandler.createMessage(StatusHandler.getMetadataMessageError(searchRecord.getFilename()),
													StatusRecord.ALERT_ERROR);
										}
									});
	}
}