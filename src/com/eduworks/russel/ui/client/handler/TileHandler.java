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

import com.eduworks.gwt.client.net.CommunicationHub;
import com.eduworks.gwt.client.net.api.Adl3DRApi;
import com.eduworks.gwt.client.net.api.AlfrescoApi;
import com.eduworks.gwt.client.net.callback.Adl3DRCallback;
import com.eduworks.gwt.client.net.callback.AlfrescoCallback;
import com.eduworks.gwt.client.net.callback.EventCallback;
import com.eduworks.gwt.client.net.packet.Adl3DRPacket;
import com.eduworks.gwt.client.net.packet.AlfrescoPacket;
import com.eduworks.gwt.client.net.packet.StatusPacket;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.gwt.client.util.Browser;
import com.eduworks.russel.ui.client.Constants;
import com.eduworks.russel.ui.client.Russel;
import com.eduworks.russel.ui.client.epss.ProjectFileModel;
import com.eduworks.russel.ui.client.extractor.AssetExtractor;
import com.eduworks.russel.ui.client.pagebuilder.HtmlTemplates;
import com.eduworks.russel.ui.client.pagebuilder.MetaBuilder;
import com.eduworks.russel.ui.client.pagebuilder.screen.DetailScreen;
import com.eduworks.russel.ui.client.pagebuilder.screen.EPSSEditScreen;
import com.google.gwt.dom.client.Document;
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

public class TileHandler {
	private AlfrescoSearchHandler ash;
	private AlfrescoPacket searchRecord;
	private String tileType;
	private String idPrefix;
	private TileHandler tile;
	private MetaBuilder mb = new MetaBuilder(MetaBuilder.DETAIL_SCREEN);
	private Boolean selectState;
	
	public TileHandler(AlfrescoSearchHandler asHandler, String thumbIdPrefix, String searchTileType, AlfrescoPacket searchTermRecord) {
		this.tile = this;
		this.ash = asHandler;
		this.searchRecord = searchTermRecord;
		this.tileType = searchTileType;
		this.idPrefix = thumbIdPrefix;
		this.selectState = false;
		addHooks();
	}
	
	public String getIdPrefix() {
		return this.idPrefix;
	}
	
	public Boolean getSelectState() {
		return this.selectState;
	}
	
	public AlfrescoPacket getSearchRecord() {
		return this.searchRecord;
	}
	
	public void select() {
		if (!this.selectState) {
			this.selectState = true;
		}
	}
	
	public void deselect() {
		if (this.selectState) {
			this.selectState = false;
		}
	}
	
	public void addHooks() {	
		PageAssembler.attachHandler(idPrefix + "-objectClick", Event.ONCLICK, new EventCallback() {
																				@Override
																				public void onEvent(Event event) {
																					if (tileType.equals(AlfrescoSearchHandler.PROJECT_TYPE))
																						ProjectFileModel.importFromAlfrescoNode(searchRecord.getNodeId(), 
																																searchRecord.getFilename(), 
																																new AlfrescoCallback<AlfrescoPacket>() {
																																	@Override
																																	public void onSuccess(AlfrescoPacket alfrescoPacket) {
																																		Russel.view.loadScreen(new EPSSEditScreen(new ProjectFileModel(alfrescoPacket)), true);
																																	}
																																	
																																	@Override
																																	public void onFailure(Throwable caught) {
																																		Window.alert("Fooing couldn't load project file " + caught);
																																	}
																																});
																					else if (tileType.equals(AlfrescoSearchHandler.RECENT_TYPE)||tileType.equals(AlfrescoSearchHandler.ASSET_TYPE)||
																							 tileType.equals(AlfrescoSearchHandler.SEARCH_TYPE)||tileType.equals(AlfrescoSearchHandler.COLLECTION_TYPE)|| 
																							 tileType.equals(AlfrescoSearchHandler.FLR_TYPE)||tileType.equals(Adl3DRSearchHandler.SEARCH3DR_TYPE)||
																							 tileType.equals(Adl3DRSearchHandler.ASSET3DR_TYPE)) 
																						Russel.view.loadScreen(new DetailScreen(searchRecord, tile), false);
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
																					if (tileType.equals(AlfrescoSearchHandler.PROJECT_TYPE))
																						ProjectFileModel.importFromAlfrescoNode(searchRecord.getNodeId(), 
																																searchRecord.getFilename(),
																																new AlfrescoCallback<AlfrescoPacket>() {
																																	@Override
																																	public void onSuccess(AlfrescoPacket alfrescoPacket) {
																																		Russel.view.loadScreen(new EPSSEditScreen(new ProjectFileModel(alfrescoPacket)), true);
																																	}
																																	
																																	@Override
																																	public void onFailure(Throwable caught) {
																																		StatusWindowHandler.createMessage(StatusWindowHandler.getProjectLoadMessageError(searchRecord.getFilename()),
																																										  StatusPacket.ALERT_ERROR);
																																	}
																																});
																					else if (tileType.equals(AlfrescoSearchHandler.RECENT_TYPE)||tileType.equals(AlfrescoSearchHandler.ASSET_TYPE) ||
																							 tileType.equals(AlfrescoSearchHandler.NOTES_TYPE) ||tileType.equals(AlfrescoSearchHandler.SEARCH_TYPE)||
																							 tileType.equals(AlfrescoSearchHandler.COLLECTION_TYPE)||tileType.equals(AlfrescoSearchHandler.FLR_TYPE)||
																							 tileType.equals(Adl3DRSearchHandler.SEARCH3DR_TYPE)||tileType.equals(Adl3DRSearchHandler.ASSET3DR_TYPE))
																						Russel.view.loadScreen(new DetailScreen(searchRecord, tile), false);
																				}
																			 });
		
		PageAssembler.attachHandler(idPrefix + "-objectDelete", Event.ONCLICK, new EventCallback() {
																					@Override
																					public void onEvent(Event event) {
																						if (Window.confirm("Are you sure you wish to delete this item?")) {
																							final StatusPacket status = StatusWindowHandler.createMessage(StatusWindowHandler.getUpdateMetadataMessageBusy(searchRecord.getFilename()),
																																						  StatusPacket.ALERT_BUSY);
																							AlfrescoApi.deleteDocument(searchRecord.getNodeId(), new AlfrescoCallback<AlfrescoPacket>() {
																																					@Override
																																					public void onFailure(Throwable caught) {
																																						status.setMessage(StatusWindowHandler.getDeleteMessageError(searchRecord.getFilename()));
																																						status.setState(StatusPacket.ALERT_ERROR);
																																						StatusWindowHandler.alterMessage(status);
																																					}
																		
																																					@Override
																																					public void onSuccess(AlfrescoPacket result) {
																																						status.setMessage(StatusWindowHandler.getDeleteMessageDone(searchRecord.getFilename()));
																																						status.setState(StatusPacket.ALERT_SUCCESS);
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
																						removeTile();
																						((Hidden)PageAssembler.elementToWidget("epssActiveRemoveAsset", PageAssembler.HIDDEN)).setValue(searchRecord.getNodeId());
																						((Hidden)PageAssembler.elementToWidget("epssActiveAddAsset", PageAssembler.HIDDEN)).setValue("");
																						EPSSEditScreen.removeAssetTrigger();
																					}
																				});
		
		PageAssembler.attachHandler(idPrefix + "-objectAdd", Event.ONCLICK, new EventCallback() {
																				@Override
																				public void onEvent(Event event) {
																					Element e = DOM.getElementById(idPrefix + "-object");
																					e.removeFromParent();
																					Element td = DOM.createTD();
																					td.setInnerHTML(HtmlTemplates.INSTANCE.getEPSSNoteAssetObjectWidget().getText());
																					Vector<String> iDs = PageAssembler.merge("epssCurrentSection", "x", td);
																					idPrefix = iDs.firstElement().substring(0,iDs.firstElement().indexOf("-"));
																					td.setId(idPrefix + "-assetNote");
																					addHooks();
																					PageAssembler.runCustomJSHooks();
																					refreshTile(null);
																					((Hidden)PageAssembler.elementToWidget("epssActiveRemoveAsset", PageAssembler.HIDDEN)).setValue("");
																					((Hidden)PageAssembler.elementToWidget("epssActiveAddAsset", PageAssembler.HIDDEN)).setValue(searchRecord.getNodeId() + "," + searchRecord.getFilename());
																					DOM.getElementById("epssUpdate").removeClassName("white");
																					DOM.getElementById("epssUpdate").addClassName("blue");
																					DOM.getElementById("r-save-alert").removeClassName("hide");
																					EPSSEditScreen.addAssetTrigger();
																				}
																			});
		
		PageAssembler.attachHandler(idPrefix + "-objectNotes", Event.ONCLICK, new EventCallback() {
																		@Override
																		public void onEvent(Event event) {
																			((Label)PageAssembler.elementToWidget("projectAssetTitle", PageAssembler.LABEL)).setText(DOM.getElementById(idPrefix + "-objectTitle").getInnerText());
																			((Hidden)PageAssembler.elementToWidget("epssActiveRemoveAsset", PageAssembler.HIDDEN)).setValue("");
																			((Hidden)PageAssembler.elementToWidget("epssActiveAddAsset", PageAssembler.HIDDEN)).setValue(searchRecord.getNodeId() + "," + searchRecord.getFilename());
																			((TextBox)PageAssembler.elementToWidget("inputDevNotes", PageAssembler.TEXT)).setText("");
																			EPSSEditScreen.addAssetTrigger();
																		}
																	});
		
		//PageAssembler.attachHandler(idPrefix + "-objectDuplicate", Event.ONCLICK, Russel.nonFunctional);
	}
	
	public void missingFileTile() {
   		DOM.getElementById(idPrefix + "-objectState").addClassName("missing");
   		DOM.getElementById(idPrefix + "-objectAlerts").removeClassName("hide");
   		DOM.getElementById(idPrefix + "-objectOpen").addClassName("hide");
   		((Label)PageAssembler.elementToWidget(idPrefix + "-objectTitle", PageAssembler.LABEL)).setText("MISSING FILE");
   		((Label)PageAssembler.elementToWidget(idPrefix + "-objectDescription", PageAssembler.LABEL)).setText(searchRecord.getFilename());
   		((Label)PageAssembler.elementToWidget(idPrefix + "-objectTitleBack", PageAssembler.LABEL)).setText("'"+ searchRecord.getFilename()+"' has been deleted.");
	}

	public void removeTile() {
		if (DOM.getElementById(idPrefix + "-assetNote")!=null)
			DOM.getElementById(idPrefix + "-assetNote").removeFromParent();
	}
	
	public void fillTile(final EventCallback callback) {
		((Label)PageAssembler.elementToWidget(idPrefix + "-objectState", PageAssembler.LABEL)).setStyleName("cube file");
		((Label)PageAssembler.elementToWidget(idPrefix + "-objectState", PageAssembler.LABEL)).addStyleName(AssetExtractor.getFileType(searchRecord.getFilename()));	
		((Label)PageAssembler.elementToWidget(idPrefix + "-objectState", PageAssembler.LABEL)).addStyleName(AssetExtractor.getFileType(searchRecord.getFilename()));
		((Label)PageAssembler.elementToWidget(idPrefix + "-objectRating", PageAssembler.LABEL)).setText(searchRecord.getAverageRating() + " stars");
		long percent = 0;
		if (searchRecord.getAverageRating()>0)
			percent = Math.round(searchRecord.getAverageRating()/5.0 * 100);
		if (DOM.getElementById(idPrefix + "-objectRating")!=null)
			PageAssembler.setWidth(DOM.getElementById(idPrefix + "-objectRating"), percent+"%");
		
    	String fouo = searchRecord.getValueString("fouo");
		if (fouo.equalsIgnoreCase(Constants.FOUO)) {
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
    		Adl3DRApi.getADL3DRobject(searchRecord.getNodeId(), new Adl3DRCallback<Adl3DRPacket> () {
																	@Override
																	public void onFailure(Throwable caught) {
																		((Label)PageAssembler.elementToWidget(idPrefix + "-objectDescription", PageAssembler.LABEL)).setText(description);
																		callback.onEvent(null);
																	}
																	
																	@Override
																	public void onSuccess(Adl3DRPacket adlPacket) {
																		// merge it into the searchRecord and save it for DetailView
																		AlfrescoPacket moreData = adlPacket.convert2Russel();
																		searchRecord.mergePackets(moreData);
																		
																		if (!Browser.isIE())
																			DOM.getElementById(idPrefix + "-objectDescription").setAttribute("style", "background-image:url(" + searchRecord.getValueString("thumbnail") + ");");
																		else {
																			Image thumb = new Image();
																			thumb.addErrorHandler(new ErrorHandler() {
																									@Override
																									public void onError(ErrorEvent event) {
																										((Label)PageAssembler.elementToWidget(idPrefix + "-objectDescription", PageAssembler.LABEL)).setText(description);
																									}
																								  });
																			thumb.setUrl(searchRecord.getValue("thumbnail").toString());
																			RootPanel.get(idPrefix + "-objectDescription").add(thumb);
																		}
																		callback.onEvent(null);
																		
																	}
    		});
    		
    		Adl3DRApi.getADL3DRobjectReview(searchRecord.getNodeId(), new Adl3DRCallback<Adl3DRPacket> () {
																		@Override
																		public void onFailure(Throwable caught) {
//																			((Label)PageAssembler.elementToWidget(idPrefix + "-objectDescription", PageAssembler.LABEL)).setText(description);
																			callback.onEvent(null);
																		}
																		
																		@Override
																		public void onSuccess(Adl3DRPacket adlPacket) {
//																			// merge it into the searchRecord and save it for DetailView
																			searchRecord.addKeyValue("feedback",adlPacket);
																			((Label)PageAssembler.elementToWidget(idPrefix + "-objectRating", PageAssembler.LABEL)).setText(adlPacket.getAverageRating() + " stars");
																			long percent = 0;
																			if (adlPacket.getAverageRating()>0)
																				percent = Math.round(adlPacket.getAverageRating()/5.0 * 100);
																			if (Document.get().getElementById(idPrefix + "-objectRating")!=null)
																				Document.get().getElementById(idPrefix + "-objectRating").setAttribute("style", "width:"+percent+"%");
																	    	if (adlPacket.getCommentCount()>0) {
																				((Label)PageAssembler.elementToWidget(idPrefix + "-objectComments", PageAssembler.LABEL)).setText(adlPacket.getCommentCount()+"");
																				((Label)PageAssembler.elementToWidget(idPrefix + "-objectComments", PageAssembler.LABEL)).removeStyleName("hidden");
																			} else
																				((Label)PageAssembler.elementToWidget(idPrefix + "-objectComments", PageAssembler.LABEL)).addStyleName("hidden");
																			callback.onEvent(null);
																			
																		}
    		});
		}
		else {		
			((Label)PageAssembler.elementToWidget(idPrefix + "-objectRating", PageAssembler.LABEL)).setText(searchRecord.getAverageRating() + " stars");
			percent = 0;
			if (searchRecord.getAverageRating()>0)
				percent = Math.round(searchRecord.getAverageRating()/5.0 * 100);
			if (Document.get().getElementById(idPrefix + "-objectRating")!=null)
				Document.get().getElementById(idPrefix + "-objectRating").setAttribute("style", "width:"+percent+"%");
	    	if (searchRecord.getCommentCount()>0) {
				((Label)PageAssembler.elementToWidget(idPrefix + "-objectComments", PageAssembler.LABEL)).setText(searchRecord.getCommentCount()+"");
				((Label)PageAssembler.elementToWidget(idPrefix + "-objectComments", PageAssembler.LABEL)).removeStyleName("hidden");
			} else
				((Label)PageAssembler.elementToWidget(idPrefix + "-objectComments", PageAssembler.LABEL)).addStyleName("hidden");
	    	final String description = (searchRecord.getDescription()=="")?"Click to Edit":searchRecord.getDescription();
	    	((Label)PageAssembler.elementToWidget(idPrefix + "-objectTitleBack", PageAssembler.LABEL)).setText(searchRecord.getFilename() + "  --  " + description);

    		AlfrescoApi.getThumbnail(searchRecord.getNodeId(), new AlfrescoCallback<AlfrescoPacket>() {
																	@Override
																	public void onFailure(Throwable caught) {
																		((Label)PageAssembler.elementToWidget(idPrefix + "-objectDescription", PageAssembler.LABEL)).setText(description);
																		callback.onEvent(null);
																	}
																	
																	@Override
																	public void onSuccess(AlfrescoPacket alfrescoPacket) {
																		if (!Browser.isIE())
																			DOM.getElementById(idPrefix + "-objectDescription").setAttribute("style", "background-image:url(" + alfrescoPacket.getValueString("imageURL") + ");");
																		else {
																			Image thumb = new Image();
																			thumb.addErrorHandler(new ErrorHandler() {
																									@Override
																									public void onError(ErrorEvent event) {
																										((Label)PageAssembler.elementToWidget(idPrefix + "-objectDescription", PageAssembler.LABEL)).setText(description);
																									}
																								  });
																			thumb.setUrl(alfrescoPacket.getValueString("imageURL").toString());
																			RootPanel.get(idPrefix + "-objectDescription").add(thumb);
																		}
																		callback.onEvent(null);
																	}
    	   });
		}
	}
	
	public void refreshTile(final EventCallback callback) {
		((Label)PageAssembler.elementToWidget(idPrefix + "-objectState", PageAssembler.LABEL)).setStyleName("cube file");
		((Label)PageAssembler.elementToWidget(idPrefix + "-objectState", PageAssembler.LABEL)).addStyleName(AssetExtractor.getFileType(searchRecord.getFilename()));
		
		if (this.tileType.contains("3DR")) { 
			
		}
		else {
			AlfrescoApi.getObjectRatings(searchRecord.getNodeId(),
					  new AlfrescoCallback<AlfrescoPacket>() {
						@Override
						public void onFailure(Throwable caught) {
							AlfrescoPacket errorPacket = AlfrescoPacket.wrap(CommunicationHub.parseJSON(caught.getMessage()));
							if (errorPacket.getHttpStatus()=="404")
								missingFileTile();
							else Window.alert(caught.getMessage());
							if (callback!=null)
					    		callback.onEvent(null);
						}

						
					@Override
					public void onSuccess(AlfrescoPacket ratingRecord) {
						((Label)PageAssembler.elementToWidget(idPrefix + "-objectRating", PageAssembler.LABEL)).setText(ratingRecord.getAverageRating() + " stars");
						long percent = 0;
						if (ratingRecord.getAverageRating()>0)
							percent = Math.round(ratingRecord.getAverageRating()/5.0 * 100);
						if (DOM.getElementById(idPrefix + "-objectRating")!=null)
							PageAssembler.setWidth(DOM.getElementById(idPrefix + "-objectRating"), percent+"%");

					    	if (callback!=null)
					    		callback.onEvent(null);
							    AlfrescoApi.getObjectComments(searchRecord.getNodeId(), 
									  new AlfrescoCallback<AlfrescoPacket>() {
									    @Override
										public void onSuccess(final AlfrescoPacket commentPacket) {
									    	AlfrescoApi.getMetadata(searchRecord.getNodeId(), 
																	new AlfrescoCallback<AlfrescoPacket>() {
																	    @Override
																		public void onSuccess(final AlfrescoPacket ap) {
																	    	String fouo = ap.getAlfrescoPropertyValue("russel:level");
																			if (fouo.equalsIgnoreCase(Constants.FOUO)) {
																				((Label)PageAssembler.elementToWidget(idPrefix + "-objectAlerts", PageAssembler.LABEL)).setStyleName("status-alert");
																				((Label)PageAssembler.elementToWidget(idPrefix + "-objectAlertsBack", PageAssembler.LABEL)).setStyleName("status-alert");
																				((Label)PageAssembler.elementToWidget(idPrefix + "-objectAlertText", PageAssembler.LABEL)).setText("FOUO");
																			} else {
																				((Label)PageAssembler.elementToWidget(idPrefix + "-objectAlerts", PageAssembler.LABEL)).setStyleName("status-alert hide");
																				((Label)PageAssembler.elementToWidget(idPrefix + "-objectAlertsBack", PageAssembler.LABEL)).setStyleName("status-alert hide");
																				((Label)PageAssembler.elementToWidget(idPrefix + "-objectAlertText", PageAssembler.LABEL)).setText("");
																			}

																	    	if (commentPacket.getCommentCount()>0) {
																				((Label)PageAssembler.elementToWidget(idPrefix + "-objectComments", PageAssembler.LABEL)).setText(commentPacket.getCommentCount()+"");
																				((Label)PageAssembler.elementToWidget(idPrefix + "-objectComments", PageAssembler.LABEL)).removeStyleName("hidden");
																			} else
																				((Label)PageAssembler.elementToWidget(idPrefix + "-objectComments", PageAssembler.LABEL)).addStyleName("hidden");
																	    	
																	    	String val = ap.getAlfrescoPropertyValue("cm:title");
																	    	if (val!=null&&val.trim()!="")
																	    		((Label)PageAssembler.elementToWidget(idPrefix + "-objectTitle", PageAssembler.LABEL)).setText(val);
																	    	else 
																	    		((Label)PageAssembler.elementToWidget(idPrefix + "-objectTitle", PageAssembler.LABEL)).setText(searchRecord.getFilename());
																	    	
																	    	val = ap.getAlfrescoPropertyValue("cm:description");
																	    	((Label)PageAssembler.elementToWidget(idPrefix + "-objectTitleBack", PageAssembler.LABEL)).setText(searchRecord.getFilename() + "  --  " + val);

																			AlfrescoApi.getThumbnail(searchRecord.getNodeId(), new AlfrescoCallback<AlfrescoPacket>() {
																																	@Override
																																	public void onFailure(Throwable caught) {
																																		mb.addMetaDataToField("cm:description", idPrefix + "-objectDescription", ap);
																																	}
																																	
																																	@Override
																																	public void onSuccess(AlfrescoPacket alfrescoPacket) {
																																		if (!Browser.isIE())
																																			DOM.getElementById(idPrefix + "-objectDescription").setAttribute("style", "background-image:url(" + alfrescoPacket.getValueString("imageURL") + ");");
																																		else {
																																			Image thumb = new Image();
																																			thumb.addErrorHandler(new ErrorHandler() {
																																									@Override
																																									public void onError(ErrorEvent event) {
																																										mb.addMetaDataToField("cm:description", idPrefix + "-objectDescription", ap);
																																									}
																																								  });
																																			thumb.setUrl(alfrescoPacket.getValue("imageURL").toString());
																																			RootPanel.get(idPrefix + "-objectDescription").add(thumb);
																																		}
																																	}
																															   });
																		}
																	  
																	    @Override
																		public void onFailure(Throwable caught) {
																	    	StatusWindowHandler.createMessage(StatusWindowHandler.getMetadataMessageError(searchRecord.getFilename()),
													    													  StatusPacket.ALERT_ERROR);
																		}
																	});
										}
									  
									    @Override
										public void onFailure(Throwable caught) {
									    	StatusWindowHandler.createMessage(StatusWindowHandler.getCommentMessageError(searchRecord.getFilename()),
													  						  StatusPacket.ALERT_ERROR);
										}
									});
						}
					});
		}
	}
}