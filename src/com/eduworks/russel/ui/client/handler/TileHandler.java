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

import com.eduworks.gwt.client.model.FileRecord;
import com.eduworks.gwt.client.model.Record;
import com.eduworks.gwt.client.model.ThreeDRRecord;
import com.eduworks.gwt.client.net.api.Adl3DRApi;
import com.eduworks.gwt.client.net.api.ESBApi;
import com.eduworks.gwt.client.net.callback.ESBCallback;
import com.eduworks.gwt.client.net.callback.EventCallback;
import com.eduworks.gwt.client.net.packet.ESBPacket;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.gwt.client.util.Browser;
import com.eduworks.russel.ui.client.Constants;
import com.eduworks.russel.ui.client.Russel;
import com.eduworks.russel.ui.client.ScreenDispatch;
import com.eduworks.russel.ui.client.extractor.AssetExtractor;
import com.eduworks.russel.ui.client.model.ProjectRecord;
import com.eduworks.russel.ui.client.model.RUSSELFileRecord;
import com.eduworks.russel.ui.client.model.StatusRecord;
import com.eduworks.russel.ui.client.pagebuilder.MetaBuilder;
import com.eduworks.russel.ui.client.pagebuilder.screen.EPSSEditScreen;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * TileHandler
 * Defines globals, methods and handlers for tiles.
 * 
 * @author Eduworks Corporation
 */
public class TileHandler extends Constants{
	protected SearchHandler ash;
	public FileRecord searchRecord;
	public String tileType;
	protected String idPrefix;
	protected TileHandler tile;
	protected MetaBuilder mb = new MetaBuilder(MetaBuilder.DETAIL_SCREEN);
	protected Boolean selectState;

	public ScreenDispatch view()
	{
		return view;
	}
	
	/**
	 * TileHandler Constructor for the class
	 * @param asHandler ESBSearchHandler Hook to search handler
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
	public FileRecord getSearchRecord() {
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
		PageAssembler.attachHandler(idPrefix + "-objectClick", Event.ONCLICK, handlers.tileClickHandler(this));
		
		PageAssembler.attachHandler(idPrefix + "-objectSelect", Event.ONCLICK, new EventCallback() {
																					@Override
																					public void onEvent(Event event) {
																						ash.toggleSelection(idPrefix + "-object", searchRecord);
																					}
																				});
		
		PageAssembler.attachHandler(idPrefix + "-objectOpen", Event.ONCLICK, new EventCallback() {
																				@Override
																				public void onEvent(Event event) {
																					if (tileType.equals(ESBSearchHandler.PROJECT_TYPE))
																						ProjectRecord.importFromServer(searchRecord.getGuid(), 
																															 new ESBCallback<ESBPacket>() {
																																@Override
																																public void onSuccess(ESBPacket alfrescoPacket) {
																																	ProjectRecord pr = new ProjectRecord(alfrescoPacket);
																																	Russel.view.loadEPSSEditScreen(pr);
																																}
																																
																																@Override
																																public void onFailure(Throwable caught) {
																																	StatusWindowHandler.createMessage(StatusWindowHandler.getProjectLoadMessageError(searchRecord.getFilename()),
																																									  StatusRecord.ALERT_ERROR);
																																}
																															 });
																					if (tileType.equals(ESBSearchHandler.RECENT_TYPE)||tileType.equals(ESBSearchHandler.ASSET_TYPE) ||
																							 tileType.equals(ESBSearchHandler.NOTES_TYPE) ||tileType.equals(ESBSearchHandler.SEARCH_TYPE)||
																							 tileType.equals(ESBSearchHandler.COLLECTION_TYPE)||tileType.equals(ESBSearchHandler.FLR_TYPE)||
																							 tileType.equals(Adl3DRSearchHandler.SEARCH3DR_TYPE)||tileType.equals(Adl3DRSearchHandler.ASSET3DR_TYPE))
																						view().loadDetailScreen(searchRecord, tile);
																				}
																			 });
		
		PageAssembler.attachHandler(idPrefix + "-objectDelete", Event.ONCLICK, new EventCallback() {
																					@Override
																					public void onEvent(Event event) {
																						if (Window.confirm("Are you sure you wish to delete this item?")) {
																							final StatusRecord status = StatusWindowHandler.createMessage(StatusWindowHandler.getDeleteMessageBusy(searchRecord.getFilename()),
																																						  StatusRecord.ALERT_BUSY);
																							ESBApi.deleteResource(searchRecord.getGuid(), new ESBCallback<ESBPacket>() {
																																					@Override
																																					public void onFailure(Throwable caught) {
																																						status.setMessage(StatusWindowHandler.getDeleteMessageError(searchRecord.getFilename()));
																																						status.setState(StatusRecord.ALERT_ERROR);
																																						StatusWindowHandler.alterMessage(status);
																																					}
																		
																																					@Override
																																					public void onSuccess(ESBPacket result) {
																																						status.setMessage(StatusWindowHandler.getDeleteMessageDone(searchRecord.getFilename()));
																																						status.setState(StatusRecord.ALERT_SUCCESS);
																																						StatusWindowHandler.alterMessage(status);
																																						DOM.getElementById(idPrefix+"-object").removeFromParent();
																																					}
																																				});
																						}
																					}
																				});
		
		PageAssembler.attachHandler(idPrefix + "-objectRemove", Event.ONCLICK, new EventCallback() {
																					@Override
																					public void onEvent(Event event) {
																						assetRemove();
																					}
																				});
		
		PageAssembler.attachHandler(idPrefix + "-objectAdd", Event.ONCLICK, new EventCallback() {
																					@Override
																					public void onEvent(Event event) {
																						assetAdd();
																					}
																				});
		
		PageAssembler.attachHandler(idPrefix + "-objectNotes", Event.ONCLICK, new EventCallback() {
																		@Override
																		public void onEvent(Event event) {
																			((Label)PageAssembler.elementToWidget("projectAssetTitle", PageAssembler.LABEL)).setText(DOM.getElementById(idPrefix + "-objectTitle").getInnerText());
																			((Hidden)PageAssembler.elementToWidget("epssActiveRemoveAsset", PageAssembler.HIDDEN)).setValue("");
																			((Hidden)PageAssembler.elementToWidget("epssActiveAddAsset", PageAssembler.HIDDEN)).setValue(searchRecord.getGuid() + "," + searchRecord.getFilename());
																			((TextBox)PageAssembler.elementToWidget("inputDevNotes", PageAssembler.TEXT)).setText("");
																			EPSSEditScreen.addAssetTrigger();
																		}
																	});
		
		//PageAssembler.attachHandler(idPrefix + "-objectDuplicate", Event.ONCLICK, Russel.nonFunctional);
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
	 * removeTile Removes the tile from the screen
	 */
	public void removeTile() {
		if (DOM.getElementById(idPrefix + "-assetNote")!=null)
			DOM.getElementById(idPrefix + "-assetNote").removeFromParent();
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
		long percent = 0;
		if (searchRecord.getRating()>0)
			percent = Math.round(searchRecord.getRating()/5.0 * 100);
		if (DOM.getElementById(idPrefix + "-objectRating")!=null)
			PageAssembler.setWidth(DOM.getElementById(idPrefix + "-objectRating"), percent+"%");
		
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
    	
		if (this.tileType.contains("3DR")) {
	    	final String description = searchRecord.getDescription();
	    	((Label)PageAssembler.elementToWidget(idPrefix + "-objectTitleBack", PageAssembler.LABEL)).setText(description);
	    	
	    	// Retrieve the rest of the ADL 3DR Metadata
    		Adl3DRApi.getADL3DRobject(searchRecord.getGuid(), new ESBCallback<ESBPacket> () {
																	@Override
																	public void onFailure(Throwable caught) {
																		((Label)PageAssembler.elementToWidget(idPrefix + "-objectDescription", PageAssembler.LABEL)).setText(description);
																		callback.onEvent(null);
																	}
																	
																	@Override
																	public void onSuccess(ESBPacket adlPacket) {
																		// merge it into the searchRecord and save it for DetailView
																		ThreeDRRecord record = (ThreeDRRecord) searchRecord;
																		record.parseESBPacket(adlPacket);
																		if (!Browser.isIE())
																			DOM.getElementById(idPrefix + "-objectDescription").setAttribute("style", "background-image:url(" + record.getThumbnail() + ");");
																		else {
																			Image thumb = new Image();
																			thumb.addErrorHandler(new ErrorHandler() {
																									@Override
																									public void onError(ErrorEvent event) {
																										((Label)PageAssembler.elementToWidget(idPrefix + "-objectDescription", PageAssembler.LABEL)).setText(description);
																									}
																								  });
																			thumb.setUrl(record.getThumbnail());
																			RootPanel.get(idPrefix + "-objectDescription").add(thumb);
																		}
																		callback.onEvent(null);
																		
																	}
    		});
    		
    		Adl3DRApi.getADL3DRobjectReview(searchRecord.getGuid(), new ESBCallback<ESBPacket> () {
																		@Override
																		public void onFailure(Throwable caught) {
																			((Label)PageAssembler.elementToWidget(idPrefix + "-objectDescription", PageAssembler.LABEL)).setText(description);
																			callback.onEvent(null);
																		}
																		
																		@Override
																		public void onSuccess(ESBPacket adlPacket) {
//																			// merge it into the searchRecord and save it for DetailView
																			ThreeDRRecord record = (ThreeDRRecord) searchRecord;
																			record.parseESBPacket(adlPacket);
																			((Label)PageAssembler.elementToWidget(idPrefix + "-objectRating", PageAssembler.LABEL)).setText(record.getRating() + " stars");
																			long percent = 0;
																			if (record.getRating()>0)
																				percent = Math.round(record.getRating()/5.0 * 100);
																			if (DOM.getElementById(idPrefix + "-objectRating")!=null)
																				DOM.getElementById(idPrefix + "-objectRating").setAttribute("style", "width:"+percent+"%");
																	    	if (record.getComments().size()>0) {
																				((Label)PageAssembler.elementToWidget(idPrefix + "-objectComments", PageAssembler.LABEL)).setText(record.getComments().size()+"");
																				((Label)PageAssembler.elementToWidget(idPrefix + "-objectComments", PageAssembler.LABEL)).removeStyleName("hidden");
																			} else
																				((Label)PageAssembler.elementToWidget(idPrefix + "-objectComments", PageAssembler.LABEL)).addStyleName("hidden");
																			callback.onEvent(null);
																			
																		}
    		});
		}
		else {
			((Label)PageAssembler.elementToWidget(idPrefix + "-objectRating", PageAssembler.LABEL)).setText(searchRecord.getRating() + " stars");
			percent = 0;
			if (searchRecord.getRating()>0)
				percent = Math.round(searchRecord.getRating()/5.0 * 100);
			if (DOM.getElementById(idPrefix + "-objectRating")!=null)
				DOM.getElementById(idPrefix + "-objectRating").setAttribute("style", "width:"+percent+"%");
	    	if (searchRecord.getComments().size()>0) {
				((Label)PageAssembler.elementToWidget(idPrefix + "-objectComments", PageAssembler.LABEL)).setText(searchRecord.getComments().size()+"");
				((Label)PageAssembler.elementToWidget(idPrefix + "-objectComments", PageAssembler.LABEL)).removeStyleName("hidden");
			} else
				((Label)PageAssembler.elementToWidget(idPrefix + "-objectComments", PageAssembler.LABEL)).addStyleName("hidden");
	    	final String description = (searchRecord.getDescription()=="")?"Click to Edit":searchRecord.getDescription();
	    	((Label)PageAssembler.elementToWidget(idPrefix + "-objectTitleBack", PageAssembler.LABEL)).setText(searchRecord.getFilename() + "  --  " + description);

    		ESBApi.getThumbnail(searchRecord.getGuid(), new ESBCallback<ESBPacket>() {
																	@Override
																	public void onFailure(Throwable caught) {
																		((Label)PageAssembler.elementToWidget(idPrefix + "-objectDescription", PageAssembler.LABEL)).setText(description);
																		callback.onEvent(null);
																	}
																	
																	@Override
																	public void onSuccess(ESBPacket alfrescoPacket) {
																		if (!Browser.isIE())
																			DOM.getElementById(idPrefix + "-objectDescription").setAttribute("style", "background-image:url(" + alfrescoPacket.getString("imageURL") + ");");
																		else {
																			Image thumb = new Image();
																			thumb.addErrorHandler(new ErrorHandler() {
																									@Override
																									public void onError(ErrorEvent event) {
																										((Label)PageAssembler.elementToWidget(idPrefix + "-objectDescription", PageAssembler.LABEL)).setText(description);
																									}
																								  });
																			thumb.setUrl(alfrescoPacket.getString("imageURL"));
																			RootPanel.get(idPrefix + "-objectDescription").add(thumb);
																		}
																		callback.onEvent(null);
																	}
    	   });
		}
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
		
		if (this.tileType.contains("3DR")) { 
    		Adl3DRApi.getADL3DRobjectReview(searchRecord.getGuid(), new ESBCallback<ESBPacket> () {
							public void onFailure(Throwable caught) {
								callback.onEvent(null);
							}
							
							public void onSuccess(ESBPacket adlPacket) {
								// merge it into the searchRecord and save it for DetailView
								ThreeDRRecord record = (ThreeDRRecord) searchRecord;
								record.parseESBPacket(adlPacket);
								((Label)PageAssembler.elementToWidget(idPrefix + "-objectRating", PageAssembler.LABEL)).setText(record.getRating() + " stars");
								long percent = 0;
								if (record.getRating()>0)
									percent = Math.round(record.getRating()/5.0 * 100);
								if (DOM.getElementById(idPrefix + "-objectRating")!=null)
									DOM.getElementById(idPrefix + "-objectRating").setAttribute("style", "width:"+percent+"%");
						    	if (record.getComments().size()>0) {
									((Label)PageAssembler.elementToWidget(idPrefix + "-objectComments", PageAssembler.LABEL)).setText(record.getComments().size()+"");
									((Label)PageAssembler.elementToWidget(idPrefix + "-objectComments", PageAssembler.LABEL)).removeStyleName("hidden");
								} else
									((Label)PageAssembler.elementToWidget(idPrefix + "-objectComments", PageAssembler.LABEL)).addStyleName("hidden");
								callback.onEvent(null);
							}
			});
	
		}
		else {
			ESBApi.getRatings(searchRecord.getGuid(),
					  new ESBCallback<ESBPacket>() {
						@Override
						public void onFailure(Throwable caught) {
							//TODO fix error message for ratings/show the tile is missing for epss assets (missingFileTile)
//							ESBPacket errorPacket = new ESBPacket(AjaxPacket.parseJSON(caught.getMessage()));
//							if (errorPacket.getHttpStatus()=="404")
//								missingFileTile();
//							else Window.alert(caught.getMessage());
							if (callback!=null)
					    		callback.onEvent(null);
						}
						
						@Override
						public void onSuccess(ESBPacket ratingRecord) {
							searchRecord.parseESBPacket(ratingRecord);
							((Label)PageAssembler.elementToWidget(idPrefix + "-objectRating", PageAssembler.LABEL)).setText(searchRecord.getRating() + " stars");
							long percent = 0;
							if (searchRecord.getRating()>0)
								percent = Math.round(searchRecord.getRating()/5.0 * 100);
							if (DOM.getElementById(idPrefix + "-objectRating")!=null)
								PageAssembler.setWidth(DOM.getElementById(idPrefix + "-objectRating"), percent+"%");
	
						    	if (callback!=null)
						    		callback.onEvent(null);
								    ESBApi.getComments(searchRecord.getGuid(), 
										  new ESBCallback<ESBPacket>() {
										    @Override
											public void onSuccess(final ESBPacket commentPacket) {
										    	ESBApi.getResourceMetadata(searchRecord.getGuid(), 
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
	
																		    	if (searchRecord.getComments().size()>0) {
																					((Label)PageAssembler.elementToWidget(idPrefix + "-objectComments", PageAssembler.LABEL)).setText(searchRecord.getComments().size()+"");
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
	
																				ESBApi.getThumbnail(searchRecord.getGuid(), new ESBCallback<ESBPacket>() {
																																		@Override
																																		public void onFailure(Throwable caught) {
																																			mb.addMetaDataToField(idPrefix + "-objectDescription", description, true);
																																		}
																																		
																																		@Override
																																		public void onSuccess(ESBPacket alfrescoPacket) {
																																			if (!Browser.isIE())
																																				DOM.getElementById(idPrefix + "-objectDescription").setAttribute("style", "background-image:url(" + alfrescoPacket.getString("imageURL") + ");");
																																			else {
																																				Image thumb = new Image();
																																				thumb.addErrorHandler(new ErrorHandler() {
																																										@Override
																																										public void onError(ErrorEvent event) {
																																											mb.addMetaDataToField(idPrefix + "-objectDescription", description, true);
																																										}
																																									  });
																																				thumb.setUrl(alfrescoPacket.getString("imageURL"));
																																				RootPanel.get(idPrefix + "-objectDescription").add(thumb);
																																			}
																																		}
																																   });
																			}
																		  
																		    @Override
																			public void onFailure(Throwable caught) {
																		    	StatusWindowHandler.createMessage(StatusWindowHandler.getMetadataMessageError(searchRecord.getFilename()),
														    													  StatusRecord.ALERT_ERROR);
																			}
																		});
											}
										  
										    @Override
											public void onFailure(Throwable caught) {
										    	StatusWindowHandler.createMessage(StatusWindowHandler.getCommentMessageError(searchRecord.getFilename()),
														  						  StatusRecord.ALERT_ERROR);
											}
										});
							}
					});
		}
	}

	public void assetAdd()
	{
		Element e = DOM.getElementById(idPrefix + "-object");
		e.removeFromParent();
		Element td = DOM.createTD();
		td.setInnerHTML(templates.getEPSSNoteAssetObjectWidget().getText());
		Vector<String> iDs = PageAssembler.merge("epssCurrentSection", "x", td);
		idPrefix = iDs.firstElement().substring(0,iDs.firstElement().indexOf("-"));
		td.setId(idPrefix + "-assetNote");
		addHooks();
		PageAssembler.runCustomJSHooks();
		refreshTile(null);
		((Hidden)PageAssembler.elementToWidget("epssActiveRemoveAsset", PageAssembler.HIDDEN)).setValue("");
		((Hidden)PageAssembler.elementToWidget("epssActiveAddAsset", PageAssembler.HIDDEN)).setValue(searchRecord.getGuid() + "," + searchRecord.getFilename());
		DOM.getElementById("epssUpdate").removeClassName("white");
		DOM.getElementById("epssUpdate").addClassName("blue");
		DOM.getElementById("r-save-alert").removeClassName("hide");
		EPSSEditScreen.addAssetTrigger();
	}
	
	public void assetRemove()
	{
		removeTile();
		((Hidden)PageAssembler.elementToWidget("epssActiveRemoveAsset", PageAssembler.HIDDEN)).setValue(searchRecord.getGuid());
		((Hidden)PageAssembler.elementToWidget("epssActiveAddAsset", PageAssembler.HIDDEN)).setValue("");
		EPSSEditScreen.removeAssetTrigger();
	}

}