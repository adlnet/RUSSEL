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

import java.util.Iterator;
import java.util.Vector;

import com.eduworks.gwt.client.component.HtmlTemplates;
import com.eduworks.gwt.client.model.StatusRecord;
import com.eduworks.gwt.client.net.api.Adl3DRApi;
import com.eduworks.gwt.client.net.api.FLRApi;
import com.eduworks.gwt.client.net.callback.ESBCallback;
import com.eduworks.gwt.client.net.callback.EventCallback;
import com.eduworks.gwt.client.net.packet.ESBPacket;
import com.eduworks.gwt.client.net.packet.FLRPacket;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.gwt.client.pagebuilder.modal.ModalDispatch;
import com.eduworks.gwt.client.pagebuilder.overlay.OverlayDispatch;
import com.eduworks.gwt.client.pagebuilder.screen.ScreenDispatch;
import com.eduworks.gwt.client.pagebuilder.screen.ScreenTemplate;
import com.eduworks.gwt.client.util.MathUtil;
import com.eduworks.russel.ui.client.Constants;
import com.eduworks.russel.ui.client.Russel;
import com.eduworks.russel.ui.client.extractor.AssetExtractor;
import com.eduworks.russel.ui.client.handler.StatusHandler;
import com.eduworks.russel.ui.client.handler.TileHandler;
import com.eduworks.russel.ui.client.model.CommentRecord;
import com.eduworks.russel.ui.client.model.FileRecord;
import com.eduworks.russel.ui.client.model.ProjectRecord;
import com.eduworks.russel.ui.client.model.RUSSELFileRecord;
import com.eduworks.russel.ui.client.net.FLRPacketGenerator;
import com.eduworks.russel.ui.client.net.RusselApi;
import com.eduworks.russel.ui.client.pagebuilder.MetaBuilder;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;


/**
 * DetailScreen class
 * Extends ScreenTemplate
 * Defines methods and handlers for the Detail screen.
 * 
 * @author Eduworks Corporation
 */
public class DetailScreen extends ScreenTemplate {
	protected RUSSELFileRecord record;
	
	protected MetaBuilder meta = new MetaBuilder(MetaBuilder.DETAIL_SCREEN);
	protected TileHandler tile = null;
	protected Boolean fullScreen = false;
	public static boolean FULL_SCREEN = true;
	public static boolean MODAL = false;
	
	/**
	 * lostFocus In place to handle any processing requirements required when this screen loses focus.
	 * Called by ScreenDispatch for all RUSSEL screens.
	 */
	public void lostFocus() {
		
	}
	
	/**
	 * DetailScreen Constructor for the class given just a node ID
	 * @param id String Node id corresponding to this view
	 */
	public DetailScreen(String id) {
		this.record = new RUSSELFileRecord();
		this.record.setGuid(id);
		fullScreen = true;
	}
	
	/**
	 * DetailScreen Constructor for the class given the packet and screen type
	 * @param r ESBPacket information for the node
	 * @param isFull Boolean True if in full screen view, false if in modal view. 
	 */
	public DetailScreen(RUSSELFileRecord r, boolean isFull) {
		this.record = r;
		this.fullScreen = isFull;
	}
	
	/** 
	 * DetailScreen Constructor for the class given the packet and associated search tile handler from which it was launched
	 * @param r ESBPacket information for the node
	 * @param sth TileHandler associated search tile handler
	 */
	public DetailScreen(RUSSELFileRecord r, TileHandler sth) {
		this.record = r;
		this.tile = sth;
		this.fullScreen = MODAL;
	}
	
	/**
	 * setDisplayIE Defines IE display state
	 * @param element Element 
	 * @param state String 
	 */
	protected native void setDisplayIE(Element element, String state) /*-{
		element.style.display = state;
	}-*/;
		
	/**
	 * displayGuts Renders the guts of the DetailView and sets up handlers for the node record after it has been retrieved and is available
	 */
	public void displayGuts() {
		
		if (fullScreen) {
			PageAssembler.ready(new HTML(Russel.htmlTemplates.getDetailPanel().getText()));
			PageAssembler.buildContents();
			DOM.getElementById("r-menuWorkspace").getParentElement().removeClassName("active");
			DOM.getElementById("r-menuCollections").getParentElement().removeClassName("active");
			DOM.getElementById("r-menuProjects").getParentElement().removeClassName("active");
			DOM.getElementById("r-fullScreen").setAttribute("style", "display:none");
			DOM.getElementById("r-metadata-hide").setAttribute("style", "display:none");
			DOM.getElementById("r-metadata-show").setAttribute("style", "");
		} else {
			DOM.getElementById("r-fullScreen").setAttribute("style", "");
			PageAssembler.attachHandler(PageAssembler.getElementByClass(".reveal-modal-bg"), Event.ONCLICK, new EventCallback() {
				@Override
				public void onEvent(Event event) {
					if (tile!=null)
						tile.refreshTile(null);
				}
			});
			
			PageAssembler.attachHandler(PageAssembler.getElementByClass(".close-reveal-modal"), Event.ONCLICK, new EventCallback() {
				@Override
				public void onEvent(Event event) {
					if (tile!=null)
						tile.refreshTile(null);
				}
			});
		}
				
		if (record.getFilename().substring(record.getFilename().lastIndexOf(".")+1).equalsIgnoreCase("rpf")) {
			DOM.getElementById("r-editEPSSContainer").removeAttribute("style");
			PageAssembler.attachHandler("r-editEPSS", Event.ONCLICK, new EventCallback() {
														   	@Override
														   	public void onEvent(Event event) {					
														   		RusselApi.getResource(tile.getSearchRecord().getGuid(),
																					  true,
																					  new ESBCallback<ESBPacket>() {
																							@Override
																							public void onSuccess(ESBPacket alfrescoPacket) {
																								ProjectRecord pr = new ProjectRecord(alfrescoPacket.getContentString(), record);
																								Russel.screen.loadScreen(new EPSSScreen(pr), true);
																							}
															
																							@Override
																							public void onFailure(Throwable caught) {
																								StatusHandler.createMessage(StatusHandler.getProjectLoadMessageError(record.getFilename()),
																										  StatusRecord.ALERT_ERROR);
																							}
																					  });
														   	}
														   });
		} else
			DOM.getElementById("r-editEPSSContainer").setAttribute("style", "display:none");
		
		setDisplayIE(DOM.getElementById("r-metadata-hide"), "block");
		setDisplayIE(DOM.getElementById("r-metadata-show"), "none");
		setDisplayIE(DOM.getElementById("r-generalMetadata"), "block");
		setDisplayIE(DOM.getElementById("r-educationalMetadata"), "none");
		setDisplayIE(DOM.getElementById("r-technicalMetadata"), "none");
		setDisplayIE(DOM.getElementById("r-relatedMetadata"), "none");
		if (record.getGuid()==""&&record.getFilename().endsWith(".flr")) {
			setDisplayIE(DOM.getElementById("r-postFlr"), "none");
			setDisplayIE(DOM.getElementById("r-saveFromFlr"), "block");
			setDisplayIE(DOM.getElementById("r-detailGenerateMetadata"), "none");
		} else {
			setDisplayIE(DOM.getElementById("r-postFlr"), "block");
			setDisplayIE(DOM.getElementById("r-saveFromFlr"), "none");
			setDisplayIE(DOM.getElementById("r-detailGenerateMetadata"), "block");
		}
		((Label)PageAssembler.elementToWidget("general-section", PageAssembler.LABEL)).removeStyleName("collapsed");
		((Label)PageAssembler.elementToWidget("educational-section", PageAssembler.LABEL)).addStyleName("collapsed");
		((Label)PageAssembler.elementToWidget("technical-section", PageAssembler.LABEL)).addStyleName("collapsed");
		((Label)PageAssembler.elementToWidget("related-section", PageAssembler.LABEL)).addStyleName("collapsed");
		((TextBox)PageAssembler.elementToWidget("input-comment", PageAssembler.TEXT)).setText("");
		
		DOM.getElementById("detailLevel1").setAttribute("disabled", "");
		DOM.getElementById("detailDistribution1").setAttribute("disabled", "");

		((Label)PageAssembler.elementToWidget("r-detailIcon", PageAssembler.LABEL)).setStyleName("r-icon");
		((Label)PageAssembler.elementToWidget("r-detailIcon", PageAssembler.LABEL)).addStyleName(AssetExtractor.getFileType(record.getFilename()));
		removeUnsavedEffects0();
				
		PageAssembler.attachHandler("r-saveFromFlr", Event.ONCLICK, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				RusselApi.uploadResource(record.getFileContents(), record.getFilename(), new ESBCallback<ESBPacket>() {
					@Override
					public void onSuccess(ESBPacket esbPacket) {
						record.setGuid(esbPacket.getPayloadString());
						ESBPacket pack = new ESBPacket(record.toString());
						pack.remove("uploadDate_l");
						pack.remove("updatedDate_l");
						RusselApi.updateResourceMetadata(record.getGuid(), pack, new ESBCallback<ESBPacket>() {
																	@Override
																	public void onSuccess(ESBPacket esbPacket) {
																		PageAssembler.closePopup("objDetailModal");
																	}
																	
																	@Override
																	public void onFailure(Throwable caught) {
																		
																	}
																});
					}
					
					@Override
					public void onFailure(Throwable caught) {}
				});
			}
		});
		
		PageAssembler.attachHandler("r-detailGenerateMetadata", Event.ONCLICK, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				final StatusRecord status = StatusHandler.createMessage(StatusHandler.getGenerateMetaDataBusy(record.getFilename()), StatusRecord.ALERT_BUSY);
				
				final ESBCallback<ESBPacket> callback = new ESBCallback<ESBPacket>() {
					@Override
					public void onFailure(Throwable caught) {
						status.setState(StatusRecord.ALERT_ERROR);
						status.setMessage(StatusHandler.getGenerateMetaDataError(record.getFilename()));
						StatusHandler.alterMessage(status);
					}
					
					@Override
					public void onSuccess(ESBPacket esbPacket) {
						status.setState(StatusRecord.ALERT_SUCCESS);
						status.setMessage(StatusHandler.getGenerateMetaDataDone(record.getFilename()));
						StatusHandler.alterMessage(status);
						
						final RUSSELFileRecord fr = new RUSSELFileRecord(esbPacket);
						meta.addMetaDataFields(fr);
						addUnsavedEffects0();
					}
			    };
   
				RusselApi.generateResourceMetadata(record.getGuid(), record.getFilename().endsWith(".rlk")?true:false, callback);
			}
		});
		
		PageAssembler.attachHandler("r-detailEditUpdate", 
									Event.ONCLICK, 
									new EventCallback() {
										@Override
										public void onEvent(Event event) {
											final StatusRecord status = StatusHandler.createMessage(StatusHandler.getUpdateMetadataMessageBusy(record.getFilename()), StatusRecord.ALERT_BUSY);
											RusselApi.updateResourceMetadata(record.getGuid(),
																			 meta.buildMetaPacket(record).toObject(),
																			 new ESBCallback<ESBPacket>() {
																					@Override
																					public void onSuccess(final ESBPacket nullPack) {
																						status.setMessage(StatusHandler.getUpdateMetadataMessageDone(record.getFilename()));
																						status.setState(StatusRecord.ALERT_SUCCESS);
																						StatusHandler.alterMessage(status);
																						RusselApi.getResourceMetadata(record.getGuid(),
																													  false,
																													   new ESBCallback<ESBPacket>() {
																															@Override
																															public void onSuccess(ESBPacket ap) {
																																removeUnsavedEffects0();
																																record.parseESBPacket(ap);
																																meta.addMetaDataFields(record);
																																Label title = ((Label)PageAssembler.elementToWidget("r-detailTitle", PageAssembler.LABEL));
																																if (tile!=null)
																																	tile.fillTile(null);
																																if (title.getText().equalsIgnoreCase("n/a"))
																																	title.setText(record.getFilename());
																															}
																															
																															@Override
																															public void onFailure(Throwable caught) {
																																removeUnsavedEffects0();
																															}
																														});
																											  
																					}
																									
																					@Override
																					public void onFailure(Throwable caught) {
																						status.setMessage(StatusHandler.getUpdateMetadataMessageError(record.getFilename()));
																						status.setState(StatusRecord.ALERT_ERROR);
																						StatusHandler.alterMessage(status);
																					}
																			    });
										}
									});
		
		PageAssembler.attachHandler("r-fullScreen", Event.ONCLICK, new EventCallback() {
																		@Override
																		public void onEvent(Event event) {	
																			PageAssembler.closePopup("objDetailModal");
																			PageAssembler.removeElement("objDetailModal");
																			PageAssembler.removeByClass("reveal-modal-bg");
																			Russel.screen.loadScreen(new DetailScreen(record, FULL_SCREEN), false);
																		}
																	});

		
		PageAssembler.attachHandler("r-deleteDoc", Event.ONCLICK, new EventCallback() {
																		@Override
																		public void onEvent(Event event) {
																			if (Window.confirm("Are you sure you wish to delete this item?")) {
																				final StatusRecord status = StatusHandler.createMessage(StatusHandler.getDeleteMessageBusy(record.getFilename()),
																						 																							   StatusRecord.ALERT_BUSY);
																				RusselApi.deleteResource(record.getGuid(), new ESBCallback<ESBPacket>() {
																							@Override
																							public void onFailure(Throwable caught) {
																								status.setMessage(StatusHandler.getDeleteMessageError(record.getFilename()));
																								status.setState(StatusRecord.ALERT_ERROR);
																								StatusHandler.alterMessage(status);
																							}
		
																							@Override
																							public void onSuccess(ESBPacket result) {
																								status.setMessage(StatusHandler.getDeleteMessageDone(record.getFilename()));
																								status.setState(StatusRecord.ALERT_SUCCESS);
																								StatusHandler.alterMessage(status);
																								((TextBox)PageAssembler.elementToWidget("r-menuSearchBar", PageAssembler.TEXT)).setText("");
																								PageAssembler.closePopup("objDetailModal");
																								if (record instanceof RUSSELFileRecord) {
																									if (((RUSSELFileRecord)record).getFlrDocId()!="")
																										RusselApi.publishToFlr(record.getGuid(),
																																FLRPacketGenerator.buildFlrDeleteNsdlPacket(((RUSSELFileRecord)record)),
																												      		new ESBCallback<ESBPacket>() {
																															  	@Override
																															  	public void onFailure(Throwable caught) {
																																	 StatusHandler.createMessage(StatusHandler.getFLRDeleteMessageError(record.getFilename()), 
																																									   StatusRecord.ALERT_ERROR);
																															  	}
		
																																@Override
																																public void onSuccess(ESBPacket esbPacket) {
																																	final StatusRecord status = StatusHandler.createMessage(StatusHandler.getFLRDeleteMessageDone(record.getFilename()),
												 																							   													  StatusRecord.ALERT_SUCCESS);
																																}
																															});
																									if (((RUSSELFileRecord)record).getFlrParadataId()!="")
																										RusselApi.publishToFlr(record.getGuid(),
																																FLRPacketGenerator.buildFlrDeleteParadataPacket(((RUSSELFileRecord)record)),
																												      		new ESBCallback<ESBPacket>() {
																															  	@Override
																															  	public void onFailure(Throwable caught) {
																																	 StatusHandler.createMessage(StatusHandler.getFLRDeleteActivityError(record.getFilename()), 
																																									   StatusRecord.ALERT_ERROR);
																															  	}
				
																																@Override
																																public void onSuccess(ESBPacket esbPacket) {
																																	final StatusRecord status = StatusHandler.createMessage(StatusHandler.getFLRDeleteParadataDone(record.getFilename()),
																																																  StatusRecord.ALERT_SUCCESS);
																																	
																																}
																															});
																								}
																							}
																						});
																			}
																		}
																	});

		PageAssembler.attachHandler("comment-submit", 
									Event.ONCLICK, 
									new EventCallback() {
										@Override
										public void onEvent(Event event) {
											final String comment = ((TextBox)PageAssembler.elementToWidget("input-comment", PageAssembler.TEXT)).getText().trim();
											if (comment!=null&&comment.trim()!=""&&!comment.equalsIgnoreCase("add comment")) {
												RusselApi.addComment(record.getGuid(),
																	 comment,
																	 new ESBCallback<ESBPacket>() {
																		 @Override
																		 public void onSuccess(ESBPacket esbPacket) {
																			esbPacket = esbPacket.getObject("obj");
																			record.parseESBPacket(esbPacket.getObject("m"));
																			CommentRecord commentRecord = new CommentRecord(new ESBPacket(esbPacket.getString("c")));
																			fillComment0(commentRecord);
																			((TextBox)PageAssembler.elementToWidget("input-comment", PageAssembler.TEXT)).setText("");
																		 }
																		
																		 @Override
																		 public void onFailure(Throwable caught) {
																			 ((TextBox)PageAssembler.elementToWidget("input-comment", PageAssembler.TEXT)).setText("");
																			 StatusHandler.createMessage(StatusHandler.getCommentMessageError(record.getFilename()), StatusRecord.ALERT_ERROR);
																		 }
																	 });
										}
									}
								});
		
		Document.get().getElementById("r-downloadDoc").setAttribute("href", RusselApi.downloadContentUrl(record.getGuid(), true));
				
		((Label)PageAssembler.elementToWidget("r-rating-info", PageAssembler.LABEL)).setText("Current rating: " + MathUtil.roundNumber(record.getRating(),2) + " (" + record.getVotes() + " votes)");
		DOM.getElementById("r-ratingLabel").setAttribute("style", "width: " + MathUtil.roundNumber((record.getRating()/5.0)*100,2) + "%");

		fillComments0();
		
		((Label)PageAssembler.elementToWidget("detailMetaFilename", PageAssembler.LABEL)).setText(record.getFilename());
		
		String ext = record.getFilename().substring(record.getFilename().lastIndexOf(".")+1);
		if (ext.equalsIgnoreCase("png")||ext.equalsIgnoreCase("tiff")||ext.equalsIgnoreCase("tif")||ext.equalsIgnoreCase("bmp")||ext.equalsIgnoreCase("jpg")||ext.equalsIgnoreCase("jpeg")||ext.equalsIgnoreCase("gif")) {
			DOM.getElementById("r-preview").setInnerHTML("");
			RootPanel.get("r-preview").add(new Image(RusselApi.downloadContentUrl(record.getGuid(), false)));
		} 
		else if (ext.equalsIgnoreCase("rlk")) {
			RusselApi.getResource(record.getGuid(),
							   false,
							   new ESBCallback<ESBPacket>() {
								 	@Override
								 	public void onSuccess(ESBPacket esbPacket) {
								 		record.setFileContents(esbPacket.getContentString());
								 		DOM.getElementById("r-preview").setInnerHTML("<a href=" + record.getFileContents() + " target=\"_blank\">" + record.getFileContents().replaceAll("\"", "") + "</a>");
								 	}
								 	
								 	@Override
								 	public void onFailure(Throwable caught) {}
								});
		} else if (isTextFormat(ext)) {
			RusselApi.getResource(record.getGuid(),
							   false,
							   new ESBCallback<ESBPacket>() {
								 	@Override
								 	public void onSuccess(ESBPacket esbPacket) {
								 		record.setFileContents(esbPacket.getContentString());
								 		DOM.getElementById("r-preview").setInnerHTML(record.getFileContents());
								 	}
								 	
								 	@Override
								 	public void onFailure(Throwable caught) {}
								 });
		} else if (ext.equalsIgnoreCase("Mp4")||ext.equalsIgnoreCase("WebM")||ext.equalsIgnoreCase("Ogg")) {
			String videoType = (ext.equalsIgnoreCase("Mp4"))? "audio/mp4" : (ext.equalsIgnoreCase("WebM"))? "audio/webm" : (ext.equalsIgnoreCase("Ogg"))? "audio/ogg" : "";
			String htmlString = "<video controls=\"controls\"><source src=\"" + RusselApi.downloadContentUrl(record.getGuid(), false) + "\" type=\"" + videoType + "\"></source></video>";			
			RootPanel.get("r-preview").getElement().setInnerHTML(htmlString);
		} else if (ext.equalsIgnoreCase("Mp3")||ext.equalsIgnoreCase("Wav")||ext.equalsIgnoreCase("Ogg")) {
			String audioType = (ext.equalsIgnoreCase("Mp3"))? "audio/mp3" : (ext.equalsIgnoreCase("Wav"))? "audio/wav" : (ext.equalsIgnoreCase("Ogg"))? "audio/ogg" : "";
			String htmlString = "<audio controls=\"controls\"><source src=\"" + RusselApi.downloadContentUrl(record.getGuid(), false) + "\" type=\"" + audioType + "\"></source></audio>";			
			RootPanel.get("r-preview").getElement().setInnerHTML(htmlString);
		} else if (ext.equalsIgnoreCase("swf")) {
			String htmlString = "<object id=\"FlashID\" classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\" data=\"" + RusselApi.downloadContentUrl(record.getGuid(), false) + "\" height=\"100%\" width=\"100%\">" +
									"<param name=\"movie\" value=\"" + RusselApi.downloadContentUrl(record.getGuid(), false) + "\">" + 
									"<param name=\"quality\" value=\"high\">" +
									"<param name=\"wmode\" value=\"transparent\">" +
									"<param name=\"swfversion\" value=\"10.0\">" +
									"<param name=\"allowScriptAccess\" value=\"always\">" +
									"<param name=\"BGCOLOR\" value=\"#000000\">" +
									"<param name=\"expressinstall\" value=\"Scripts/expressInstall.swf\">" +
									"<!--[if !IE]>-->" +
								    	"<object id=\"FlashID2\" type=\"application/x-shockwave-flash\" data=\"" + RusselApi.downloadContentUrl(record.getGuid(), false) + "\" height=\"100%\" width=\"100%\">" +
								    "<!--<![endif]-->" +
								    "<param name=\"movie\" value=\"" + RusselApi.downloadContentUrl(record.getGuid(), false) + "\">" +
								    "<param name=\"quality\" value=\"high\">" +
									"<param name=\"wmode\" value=\"transparent\">" +
									"<param name=\"swfversion\" value=\"10.0\">" +
									"<param name=\"allowScriptAccess\" value=\"always\">" +
									"<param name=\"BGCOLOR\" value=\"#000000\">" +
									"<param name=\"expressinstall\" value=\"Scripts/expressInstall.swf\">" +
									"<div>" +
									  "<h4>Content on this page requires a newer version of Adobe Flash Player.</h4>" +
									  "<p><a href=\"http://www.adobe.com/go/getflashplayer\"><img src=\"http://www.adobe.com/images/shared/download_buttons/get_flash_player.gif\" alt=\"Get Adobe Flash player\" width=\"112\" height=\"33\" /></a></p>" +
									"</div>" +
								    "<!--[if !IE]>-->" +
								    	"</object>" +
								    "<!--<![endif]-->"+
							    "</object>" +
							    "<!--[if !IE]>-->" +
			                        "<script type=\"text/javascript\">" +
			                            "swfobject.registerObject(\"FlashID2\");" +
			                        "</script>" +
		                        "<!--<![endif]-->" +
		                        "<!--[if IE]>" +
			                        "<script type=\"text/javascript\">" +
			                            "swfobject.registerObject(\"FlashID\");" +
			                        "</script>" +
		                        "<![endif]-->";
			RootPanel.get("r-preview").getElement().setInnerHTML(htmlString);
		} else
			DOM.getElementById("r-preview").setInnerHTML("<p>No Preview Available</p>");
		
		meta.addMetaDataFields(record);
		if (record.getFOUO()) {
			((Label)PageAssembler.elementToWidget("r-detailWarning", PageAssembler.LABEL)).setText(Constants.FOUO);
			((Label)PageAssembler.elementToWidget("r-detailWarning", PageAssembler.LABEL)).setStyleName("r-warning");
		} else {
			((Label)PageAssembler.elementToWidget("r-detailWarning", PageAssembler.LABEL)).setText("");
			((Label)PageAssembler.elementToWidget("r-detailWarning", PageAssembler.LABEL)).setStyleName("r-warning hide");
		}
		if (DOM.getElementById("r-detailTitle").getInnerText().equalsIgnoreCase("n/a"))
			((Label)PageAssembler.elementToWidget("r-detailTitle", PageAssembler.LABEL)).setText(record.getFilename());

		PageAssembler.attachHandler("r-postFlr", Event.ONCLICK, new EventCallback() {
																		@Override
																		public void onEvent(Event event) {
																			if (FLRApi.FLR_ACTIVITY_MODE.equals(FLRApi.FLR_ACTIVITY_ACTIONS_FEEDBACK)||
																					FLRApi.FLR_ACTIVITY_MODE.equals(FLRApi.FLR_ACTIVITY_ACTIONS_ISD)||
																					FLRApi.FLR_ACTIVITY_MODE.equals(FLRApi.FLR_ACTIVITY_ACTIONS_ALL)) {
																					final StatusRecord flrStatus = StatusHandler.createMessage(StatusHandler.getFLRActivityBusy(record.getFilename()),
																							   StatusRecord.ALERT_BUSY);
																					if (record instanceof RUSSELFileRecord) {
																						RusselApi.publishToFlr(record.getGuid(),
																											   FLRPacketGenerator.buildFlrParadataPacket(((RUSSELFileRecord)record)),
																							      new ESBCallback<ESBPacket>() {
																								  	@Override
																								  	public void onFailure(Throwable caught) {
																										flrStatus.setMessage(StatusHandler.getFLRActivityError(record.getFilename()));
																										flrStatus.setState(StatusRecord.ALERT_ERROR);
																										StatusHandler.alterMessage(flrStatus);
																								  	}
																								  	
																								  	@Override
																								  	public void onSuccess(ESBPacket esbPacket) {
																										flrStatus.setMessage(StatusHandler.getFLRActivityDone(record.getFilename()));
																										flrStatus.setState(StatusRecord.ALERT_SUCCESS);
																										StatusHandler.alterMessage(flrStatus);
																										FLRPacket packet = new FLRPacket(esbPacket.getObject(ESBPacket.OBJECT));
																										record.setFlrParadataId(packet.getResponseDocID());
																										RusselApi.updateResourceMetadata(((RUSSELFileRecord)record).getGuid(), ((RUSSELFileRecord)record).toObject(), new ESBCallback<ESBPacket>() {
																											@Override
																											public void onSuccess(ESBPacket esbPacket) {
																												postFlrNsdl();
																											}
																											
																											@Override
																											public void onFailure(Throwable caught) {
																												StatusHandler.createMessage(StatusHandler.getMetadataMessageError(record.getFilename()),
																																				  StatusRecord.ALERT_ERROR);
																											}
																										});
																								  	}
																								  });
																					}
																				}
																				else {
																					StatusHandler.createMessage(StatusHandler.getFLRDisabledError("Activity Stream publish"), 
																							  StatusRecord.ALERT_WARNING);
																				}
																		}
																	});
		
		attachRatingListeners0();
	}

	private void postFlrNsdl() {
		if (FLRApi.FLR_PUBLISH_MODE.equals(FLRApi.FLR_PUBLISH_ACTIONS_GENERAL)||
				FLRApi.FLR_PUBLISH_MODE.equals(FLRApi.FLR_PUBLISH_ACTIONS_ALL)) {
				final StatusRecord flrStatus = StatusHandler.createMessage(StatusHandler.getFLRMessageBusy(record.getFilename()),
																													   StatusRecord.ALERT_BUSY);
				if (record instanceof RUSSELFileRecord) {
					RusselApi.publishToFlr(record.getGuid(),
											FLRPacketGenerator.buildFlrNsdlPacket(((RUSSELFileRecord)record)),
								      new ESBCallback<ESBPacket>() {
									  	@Override
									  	public void onFailure(Throwable caught) {
											flrStatus.setMessage(StatusHandler.getFLRMessageError(record.getFilename()));
											flrStatus.setState(StatusRecord.ALERT_ERROR);
											StatusHandler.alterMessage(flrStatus);
									  	}
									  	
									  	@Override
									  	public void onSuccess(ESBPacket esbPacket) {
											flrStatus.setMessage(StatusHandler.getFLRMessageDone(record.getFilename()));
											flrStatus.setState(StatusRecord.ALERT_SUCCESS);
											StatusHandler.alterMessage(flrStatus);
											FLRPacket packet = new FLRPacket(esbPacket.getObject(ESBPacket.OBJECT));
											record.setFlrDocId(packet.getResponseDocID());
											RusselApi.updateResourceMetadata(((RUSSELFileRecord)record).getGuid(), ((RUSSELFileRecord)record).toObject(), new ESBCallback<ESBPacket>() {
												@Override
												public void onSuccess(ESBPacket esbPacket) {
													RusselApi.getResourceMetadata(record.getGuid(),
																				  false,
																				   new ESBCallback<ESBPacket>() {
																						@Override
																						public void onSuccess(ESBPacket ap) {
																							removeUnsavedEffects0();
																							record.parseESBPacket(ap);
																							meta.addMetaDataFields(record);
																							Label title = ((Label)PageAssembler.elementToWidget("r-detailTitle", PageAssembler.LABEL));
																							if (title.getText().equalsIgnoreCase("n/a"))
																								title.setText(record.getFilename());
																						}
																						
																						@Override
																						public void onFailure(Throwable caught) {
																							removeUnsavedEffects0();
																						}
																					});
													}
												
												@Override
												public void onFailure(Throwable caught) {
													StatusHandler.createMessage(StatusHandler.getMetadataMessageError(record.getFilename()),
																					  StatusRecord.ALERT_ERROR);
												}
											});
									  	}
									  });
				}
			}
			else {
				StatusHandler.createMessage(StatusHandler.getFLRDisabledError("object publish"), 
						  StatusRecord.ALERT_WARNING);
			}
	}
	
	public boolean isTextFormat(String ext)
	{
		return ext.equalsIgnoreCase("txt")||ext.equalsIgnoreCase("rtf")||ext.equalsIgnoreCase("log")||ext.equalsIgnoreCase("tep");
	}
	
	/**
	 * display Initializes the screen, retrieves the node record if not on hand
	 */
	public void display() {
		if (record.getGuid()!="")
			RusselApi.getResourceMetadata(record.getGuid(),
										  true,
										  new ESBCallback<ESBPacket>() {
												@Override
												public void onSuccess(final ESBPacket ap) {
													record.parseESBPacket(ap);
													displayGuts();
													RusselApi.getComments(record.getGuid(), 
																	      new ESBCallback<ESBPacket>() {
																			    @Override
																				public void onSuccess(ESBPacket esbPacket) {
																			    	record.getComments().clear();
																			    	record.parseComments(esbPacket);										
																			    	fillComments0();
																				}
																			  
																			    @Override
																				public void onFailure(Throwable caught) {
																			    	((Label)PageAssembler.elementToWidget("r-commentCount", PageAssembler.LABEL)).setText("No comments are available");
																				}
																		  });
												}
												
												@Override
												public void onFailure(Throwable caught) {
													displayGuts();
													removeUnsavedEffects0();
													StatusHandler.createMessage(StatusHandler.getMetadataMessageError(record.getFilename()), StatusRecord.ALERT_ERROR);
												}
										   });
		else
			displayGuts();
	}
	
	/**
	 * removeUnsavedEffects0 Changes the Update button back to saved state
	 */
	private void addUnsavedEffects0() {
		((Label)PageAssembler.elementToWidget("r-detailSaveAlert", PageAssembler.LABEL)).removeStyleName("hide");
		((Anchor)PageAssembler.elementToWidget("r-detailEditUpdate", PageAssembler.A)).addStyleName("blue");
		((Anchor)PageAssembler.elementToWidget("r-detailEditUpdate", PageAssembler.A)).removeStyleName("white");
	}
	
	/**
	 * removeUnsavedEffects0 Changes the Update button back to saved state
	 */
	private void removeUnsavedEffects0() {
		((Label)PageAssembler.elementToWidget("r-detailSaveAlert", PageAssembler.LABEL)).addStyleName("hide");
		((Anchor)PageAssembler.elementToWidget("r-detailEditUpdate", PageAssembler.A)).removeStyleName("blue");
		((Anchor)PageAssembler.elementToWidget("r-detailEditUpdate", PageAssembler.A)).addStyleName("white");
	}
	
	/**
	 * fillComments0 Displays the list of retrieved Alfresco comments in the DetailView
	 * @param ap ESBPacket retrieved list of Alfresco comments
	 */
	private void fillComments0() {
		RootPanel.get("r-commentArea").clear();
		int count = 0;
		for (Iterator<String> commentPointer=record.getComments().keySet().iterator();commentPointer.hasNext();) { 
			String commentKey = commentPointer.next();
			if (count<10) 
				fillComment0(record.getComments().get(commentKey));
			count++;
		}
		
		if (!(record.getComments().size()-10<0))
			((Label)PageAssembler.elementToWidget("r-commentCount", PageAssembler.LABEL)).setText("Show " + (record.getComments().size() - 10) + " more comments");
		else
			((Label)PageAssembler.elementToWidget("r-commentCount", PageAssembler.LABEL)).setText("No additional comments are available");
	}
	
	/**
	 * fillComment0 Displays a particular Alfresco comment in the list of comments
	 * @param commentNode ESBPacket packet for a particular comment
	 */
	private void fillComment0(final CommentRecord commentNode) {
		Vector<String> iDs = PageAssembler.inject("r-commentArea", 
												  "x", 
												  new HTML(Russel.htmlTemplates.getDetailComment().getText()),
												  true);
		final String iDPrefix = iDs.firstElement().substring(0, iDs.firstElement().indexOf("-"));
		((Label)PageAssembler.elementToWidget(iDPrefix + "-comment-text", PageAssembler.LABEL)).setText(commentNode.getComment()); 
		((Label)PageAssembler.elementToWidget(iDPrefix + "-comment-user", PageAssembler.LABEL)).setText(commentNode.getCreatedBy());
		PageAssembler.attachHandler(iDPrefix + "-comment-delete", Event.ONCLICK, new EventCallback() {
																					@Override
																					public void onEvent(Event event) {
																						RusselApi.deleteComment(record.getGuid(),
																												commentNode.getGuid(), 
																												new ESBCallback<ESBPacket>() {
																													@Override
																													public void onSuccess(ESBPacket packet) {
																														record.parseESBPacket(packet.getObject("obj").getObject("m"));
																														DOM.getElementById(iDPrefix + "-comment").removeFromParent();
																													}
																													
																													@Override
																													public void onFailure(Throwable caught) {
																														StatusHandler.createMessage(StatusHandler.getRemoveCommentMessageError(record.getFilename()),
																																						  StatusRecord.ALERT_ERROR);
																													}
																												});
																					}
																				});
	}
	
	/**
	 * attachRatingListeners0 Associates handlers to appropriate rating controls
	 */
	private void attachRatingListeners0() {
		for (int i = 1; i <= 5; i++) {
			final Integer rating = new Integer(i);

			PageAssembler.attachHandler("r-rating-" + rating,
									   Event.ONCLICK, 
									   new EventCallback() {
											@Override
											public void onEvent(Event event) {
												RusselApi.rateObject(record.getGuid(), 
																  	 rating, 
																	 new ESBCallback<ESBPacket>() {
																			@Override
																			public void onSuccess(ESBPacket result) {
																				record.parseESBPacket(result);
																				((Label)PageAssembler.elementToWidget("r-rating-info", PageAssembler.LABEL)).setText("Current rating: " + MathUtil.roundNumber(record.getRating(),2) + " (" + record.getVotes() + " votes)");
																				DOM.getElementById("r-ratingLabel").setAttribute("style", "width: " + MathUtil.roundNumber((record.getRating()/5.0)*100,2) + "%");
																				if (tile!=null)
																					tile.fillTile(null);
																			}
																			
																			public void onFailure(Throwable caught) {
																				StatusHandler.createMessage(StatusHandler.getRateOwnDocumentError(record.getFilename()),
																												  StatusRecord.ALERT_ERROR);
																			}
																	  });
											}
										});
		}
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
