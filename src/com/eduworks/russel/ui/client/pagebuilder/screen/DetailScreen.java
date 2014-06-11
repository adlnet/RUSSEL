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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import com.eduworks.gwt.client.math.MathUtil;
import com.eduworks.gwt.client.model.CommentRecord;
import com.eduworks.gwt.client.model.FileRecord;
import com.eduworks.gwt.client.model.ThreeDRRecord;
import com.eduworks.gwt.client.net.api.Adl3DRApi;
import com.eduworks.gwt.client.net.api.ESBApi;
import com.eduworks.gwt.client.net.api.FLRApi;
import com.eduworks.gwt.client.net.callback.ESBCallback;
import com.eduworks.gwt.client.net.callback.EventCallback;
import com.eduworks.gwt.client.net.packet.ESBPacket;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.russel.ui.client.Constants;
import com.eduworks.russel.ui.client.Russel;
import com.eduworks.russel.ui.client.extractor.AssetExtractor;
import com.eduworks.russel.ui.client.handler.ESBSearchHandler;
import com.eduworks.russel.ui.client.handler.SearchHandler;
import com.eduworks.russel.ui.client.handler.StatusWindowHandler;
import com.eduworks.russel.ui.client.handler.TileHandler;
import com.eduworks.russel.ui.client.model.ProjectRecord;
import com.eduworks.russel.ui.client.model.RUSSELFileRecord;
import com.eduworks.russel.ui.client.model.StatusRecord;
import com.eduworks.russel.ui.client.pagebuilder.MetaBuilder;
import com.google.gwt.core.client.JsDate;
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
public class DetailScreen extends Screen {
	protected FileRecord record;
//	protected ESBPacket ratings = null;
//	protected ESBPacket comments = null;
	
	protected MetaBuilder meta = new MetaBuilder(MetaBuilder.DETAIL_SCREEN);
	protected TileHandler tile = null;
	protected SearchHandler ash = null;
	protected Boolean fullScreen = false;
	protected static int adl3drRating = 0;
	protected static String adl3drComment = "";
	protected static StatusRecord adl3drPartialOp; 
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
	public DetailScreen(FileRecord r, boolean isFull) {
		this.record = r;
		this.fullScreen = isFull;
	}
	
	/** 
	 * DetailScreen Constructor for the class given the packet and associated search tile handler from which it was launched
	 * @param r ESBPacket information for the node
	 * @param sth TileHandler associated search tile handler
	 */
	public DetailScreen(FileRecord r, TileHandler sth) {
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
	 * setActionsForNodeType Determines what the valid actions on the top of Details need to be for the current node and sets them.
	 */
	private void setActionsForNodeType() {
		if (FLRApi.FLR_PUBLISH_MODE.equals(FLRApi.FLR_PUBLISH_ACTIONS_NONE) &&
				FLRApi.FLR_ACTIVITY_MODE.equals(FLRApi.FLR_ACTIVITY_ACTIONS_NONE)) {
			DOM.getElementById("r-postFlr").setAttribute("style", "display:none");
		}
		
		if (record.getMimeType().contains(Adl3DRApi.ADL3DR_RUSSEL_MIME_TYPE)) {  
			DOM.getElementById("r-postFlr").setAttribute("style", "display:none");
			DOM.getElementById("r-detailEditUpdate").setAttribute("style", "display:none");
			DOM.getElementById("r-deleteDoc").setAttribute("style", "display:none");
			DOM.getElementById("r-downloadDoc").setAttribute("style", "display:none");
		}
		
		if (record.getFilename().substring(record.getFilename().lastIndexOf(".")+1).equalsIgnoreCase("rpf")) {
			DOM.getElementById("r-editEPSSContainer").removeAttribute("style");
			PageAssembler.attachHandler("r-editEPSS", Event.ONCLICK, new EventCallback() {
														   	@Override
														   	public void onEvent(Event event) {
														   		ProjectRecord.importFromServer(record.getGuid(),
																										new ESBCallback<ESBPacket>() {
																											@Override
																											public void onSuccess(ESBPacket esbPacket) {
																												ProjectRecord pr = new ProjectRecord(esbPacket);
																												Constants.view.loadEPSSEditScreen(pr);
																											}
																											
																											@Override
																											public void onFailure(Throwable caught) {
																												StatusWindowHandler.createMessage(StatusWindowHandler.getProjectLoadMessageError(record.getFilename()),
																																				  StatusRecord.ALERT_ERROR);
																											}
																										});
														   	}
														   });
		} else
			DOM.getElementById("r-editEPSSContainer").setAttribute("style", "display:none");
		
		
	}
	
	/**
	 * displayGuts Renders the guts of the DetailView and sets up handlers for the node record after it has been retrieved and is available
	 */
	public void displayGuts() {
		
		if (!fullScreen) {
			if (DOM.getElementById("objDetailPanelWidget") == null) {
				PageAssembler.inject("flowContainer", "x", new HTML(templates().getDetailModal().getText()), true);
				PageAssembler.inject("objDetailPanelWidget", "x", new HTML(templates().getDetailPanel().getText()), true);
			}
		} else {
			PageAssembler.ready(new HTML(templates().getDetailPanel().getText()));
			PageAssembler.buildContents();
			DOM.getElementById("r-menuWorkspace").getParentElement().removeClassName("active");
			DOM.getElementById("r-menuCollections").getParentElement().removeClassName("active");
			DOM.getElementById("r-menuProjects").getParentElement().removeClassName("active");
			ash = new ESBSearchHandler();
			ash.hook("r-menuSearchBar", "searchObjectPanel", ESBSearchHandler.SEARCH_TYPE);
		}
			
		if (!fullScreen)
			DOM.getElementById("r-fullScreen").setAttribute("style", "");
		else
			DOM.getElementById("r-fullScreen").setAttribute("style", "display:none");
		
		setActionsForNodeType();
		
		setDisplayIE(DOM.getElementById("r-metadata-hide"), "block");
		setDisplayIE(DOM.getElementById("r-metadata-show"), "none");
		setDisplayIE(DOM.getElementById("r-generalMetadata"), "block");
		setDisplayIE(DOM.getElementById("r-educationalMetadata"), "none");
		setDisplayIE(DOM.getElementById("r-technicalMetadata"), "none");
		setDisplayIE(DOM.getElementById("r-relatedMetadata"), "none");
		((Label)PageAssembler.elementToWidget("general-section", PageAssembler.LABEL)).removeStyleName("collapsed");
		((Label)PageAssembler.elementToWidget("educational-section", PageAssembler.LABEL)).addStyleName("collapsed");
		((Label)PageAssembler.elementToWidget("technical-section", PageAssembler.LABEL)).addStyleName("collapsed");
		((Label)PageAssembler.elementToWidget("related-section", PageAssembler.LABEL)).addStyleName("collapsed");
		((TextBox)PageAssembler.elementToWidget("input-comment", PageAssembler.TEXT)).setText("");
		DOM.getElementById("detailLevel1").setAttribute("disabled", "");
		DOM.getElementById("detailDistribution1").setAttribute("disabled", "");
		if (fullScreen) {
			DOM.getElementById("r-metadata-hide").setAttribute("style", "display:none");
			DOM.getElementById("r-metadata-show").setAttribute("style", "");
		}

		if (record.getFilename().substring(record.getFilename().lastIndexOf(".")+1).equalsIgnoreCase("rpf")) {
			DOM.getElementById("r-editEPSSContainer").removeAttribute("style");
			PageAssembler.attachHandler("r-editEPSS", Event.ONCLICK, new EventCallback() {
														   	@Override
														   	public void onEvent(Event event) {
														   		ProjectRecord.importFromServer(record.getGuid(), 
																										new ESBCallback<ESBPacket>() {
																											@Override
																											public void onSuccess(ESBPacket alfrescoPacket) {
																												ProjectRecord pr = new ProjectRecord(alfrescoPacket);
																												Constants.view.loadEPSSEditScreen(pr);
																											}
																											
																											@Override
																											public void onFailure(Throwable caught) {
																												StatusWindowHandler.createMessage(StatusWindowHandler.getProjectLoadMessageError(record.getFilename()),
																																				  StatusRecord.ALERT_ERROR);
																											}
																										});
														   	}
														   });
		} else
			DOM.getElementById("r-editEPSSContainer").setAttribute("style", "display:none");
		
		((Label)PageAssembler.elementToWidget("r-detailIcon", PageAssembler.LABEL)).setStyleName("r-icon");
		((Label)PageAssembler.elementToWidget("r-detailIcon", PageAssembler.LABEL)).addStyleName(AssetExtractor.getFileType(record.getFilename()));
		removeUnsavedEffects0();
		
		if (!fullScreen) {
			PageAssembler.attachHandler(PageAssembler.getElementByClass(".reveal-modal-bg"), Event.ONCLICK, new EventCallback() {
																								@Override
																								public void onEvent(Event event) {
																									if (tile!=null)
																										tile.refreshTile(null);
																								}
																							});
	
			PageAssembler.getElementByClass(".reveal-modal-bg").setAttribute("style", "z-index:300; opacity: 0.8");
		}
		
		PageAssembler.attachHandler("r-detailEditUpdate", 
									Event.ONCLICK, 
									new EventCallback() {
										@Override
										public void onEvent(Event event) {																		
											FileRecord metadataPack = meta.buildMetaPacket(record);
											if (metadataPack!=null) {
												final StatusRecord status = StatusWindowHandler.createMessage(StatusWindowHandler.getUpdateMetadataMessageBusy(record.getFilename()),
																											  StatusRecord.ALERT_BUSY);
												metadataPack.setGuid(record.getGuid());
												ESBApi.updateResourceMetadata(metadataPack.toObject(), 
																			  new ESBCallback<ESBPacket>() {
																					@Override
																					public void onSuccess(final ESBPacket nullPack) {
																						status.setMessage(StatusWindowHandler.getUpdateMetadataMessageDone(record.getFilename()));
																						status.setState(StatusRecord.ALERT_SUCCESS);
																						StatusWindowHandler.alterMessage(status);
																						ESBApi.getResourceMetadata(record.getGuid(),
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
																						status.setMessage(StatusWindowHandler.getUpdateMetadataMessageError(record.getFilename()));
																						status.setState(StatusRecord.ALERT_ERROR);
																						StatusWindowHandler.alterMessage(status);
																					}
																				});
											}
																		
										}
									});
		
		PageAssembler.attachHandler("r-fullScreen", Event.ONCLICK, new EventCallback() {
																		@Override
																		public void onEvent(Event event) {	
																			PageAssembler.closePopup("objDetailModal");
																			PageAssembler.removeElement("objDetailModal");
																			PageAssembler.removeByClass("reveal-modal-bg");
																			Russel.view.loadDetailScreen(record, FULL_SCREEN);
																		}
																	});

		
		PageAssembler.attachHandler("r-deleteDoc", Event.ONCLICK, new EventCallback() {
																		@Override
																		public void onEvent(Event event) {
																			if (Window.confirm("Are you sure you wish to delete this item?")) {
																				final StatusRecord status = StatusWindowHandler.createMessage(StatusWindowHandler.getDeleteMessageBusy(record.getFilename()),
																						 																							   StatusRecord.ALERT_BUSY);
																				ESBApi.deleteResource(record.getGuid(), new ESBCallback<ESBPacket>() {
																							@Override
																							public void onFailure(Throwable caught) {
																								status.setMessage(StatusWindowHandler.getDeleteMessageError(record.getFilename()));
																								status.setState(StatusRecord.ALERT_ERROR);
																								StatusWindowHandler.alterMessage(status);
																							}
		
																							@Override
																							public void onSuccess(ESBPacket result) {
																								status.setMessage(StatusWindowHandler.getDeleteMessageDone(record.getFilename()));
																								status.setState(StatusRecord.ALERT_SUCCESS);
																								StatusWindowHandler.alterMessage(status);
																								((TextBox)PageAssembler.elementToWidget("r-menuSearchBar", PageAssembler.TEXT)).setText("");
																								Russel.view.loadHomeScreen();	
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
											String comment = ((TextBox)PageAssembler.elementToWidget("input-comment", PageAssembler.TEXT)).getText().trim();
											if (comment!=null&&comment.trim()!=""&&!comment.equalsIgnoreCase("add comment")) {
												if (record.getMimeType().contains(Adl3DRApi.ADL3DR_RUSSEL_MIME_TYPE)) {
													if (Adl3DRApi.ADL3DR_ACTIVITY_MODE.equals(Adl3DRApi.ADL3DR_ACTIVITY_ACTIONS_NONE)) {
														StatusWindowHandler.createMessage(StatusWindowHandler.get3DRDisabledError("Review"), 
																  StatusRecord.ALERT_ERROR);																						
													}
													else {
														if (adl3drPartialOp != null) {
															StatusWindowHandler.removeMessage(adl3drPartialOp);
														}
														final StatusRecord adl3drStatus = StatusWindowHandler.createMessage(StatusWindowHandler.get3DRReviewMessageWarn(), 
																  StatusRecord.ALERT_BUSY);
														adl3drPartialOp = adl3drStatus; 
														adl3drComment = comment.replaceAll("\r", " ").replaceAll("\n", " ");
														if (adl3drRating > 0) {
															final ESBPacket review = new ESBPacket();
															//The ADL 3DR UI uses a default rating of 3 for each comment, unless manually adjusted by the reviewer. 
															if (adl3drRating == 0) {
																adl3drRating = 3;
															}
																
															JsDate now = JsDate.create();
															review.put("DateTime", ThreeDRRecord.getADLdate(now));
															review.put("Rating", adl3drRating);
															review.put("ReviewText", adl3drComment);
															review.put("Submitter", Adl3DRApi.ADL3DR_RUSSEL_SUBMITTER);

															Adl3DRApi.putADL3DRactivity(record.getGuid(), review.toString(), new ESBCallback<ESBPacket>() {
																		@Override
																		public void onSuccess(ESBPacket adlPacket) {
																			adl3drStatus.setMessage(StatusWindowHandler.get3DRReviewMessageDone("review",record.getGuid()));
																			adl3drStatus.setState(StatusRecord.ALERT_SUCCESS);	
																			StatusWindowHandler.alterMessage(adl3drStatus); 	
																			Adl3DRApi.getADL3DRobjectReview(record.getGuid(), new ESBCallback<ESBPacket> () {
																				@Override
																				public void onFailure(Throwable caught) {
																					StatusWindowHandler.createMessage(StatusWindowHandler.get3DRReviewMessageError("refresh",record.getGuid()), 
																							  StatusRecord.ALERT_ERROR);
																					}
																				
																				@Override
																				public void onSuccess(ESBPacket adlPacket) {
																					// merge it into the searchRecord and save it for DetailView
																					//TODO double check what feedback is used for 3DR
																					//record.put("feedback",adlPacket);
//																					((Label)PageAssembler.elementToWidget("r-rating-info", 
//																							  PageAssembler.LABEL)).setText("Current rating: " + Constants.roundNumber(adlPacket.getAverageRating(),2) + " (" + adlPacket.getRatingCount() + " votes)");
//																					Document.get().getElementById("r-ratingLabel").setAttribute("style", "width:" + Constants.roundNumber((adlPacket.getAverageRating()/5.0)*100,2) + "%");
																				}
																			});
																			fillComment0(review);
																			((TextBox)PageAssembler.elementToWidget("input-comment", PageAssembler.TEXT)).setText("");	
																			adl3drRating = 0;
																			adl3drComment = "";
																			adl3drPartialOp = null;
																			// merge it into the searchRecord and save it for DetailView
																			//TODO double check what feedback is used for 3DR
																			//record.put("feedback",adlPacket);
																		}
																		
																		@Override
																		public void onFailure(Throwable caught) {
																			((TextBox)PageAssembler.elementToWidget("input-comment", PageAssembler.TEXT)).setText("");
																			adl3drStatus.setMessage(StatusWindowHandler.getCommentMessageError("3D Repository: "+record.getGuid()));
																			adl3drStatus.setState(StatusRecord.ALERT_ERROR); 
																			StatusWindowHandler.alterMessage(adl3drStatus);
																		}
															});
														}
														else {
															adl3drStatus.setState(StatusRecord.ALERT_WARNING);
															StatusWindowHandler.alterMessage(adl3drStatus); 	
														}
													}
												} 
												else {
													ESBApi.addComment(record.getGuid(),
																	  ((TextBox)PageAssembler.elementToWidget("input-comment", PageAssembler.TEXT)).getText(),
																	  new ESBCallback<ESBPacket>() {
																		 @Override
																		 public void onSuccess(ESBPacket esbPacket) {
																			CommentRecord commentRecord = new CommentRecord();
																			commentRecord.setComment(((TextBox)PageAssembler.elementToWidget("input-comment", PageAssembler.TEXT)).getText());
																			commentRecord.setFileGuid(record.getGuid());
																			commentRecord.setGuid(esbPacket.getPayloadString());
																			commentRecord.setCreatedBy(ESBApi.username);
																			commentRecord.setUpdatedBy(ESBApi.username);
																			fillComment0(commentRecord);
																			((TextBox)PageAssembler.elementToWidget("input-comment", PageAssembler.TEXT)).setText("");
																		 }
																		
																		 @Override
																		 public void onFailure(Throwable caught) {
																			 ((TextBox)PageAssembler.elementToWidget("input-comment", PageAssembler.TEXT)).setText("");
																			 StatusWindowHandler.createMessage(StatusWindowHandler.getCommentMessageError(record.getFilename()), 
																											   StatusRecord.ALERT_ERROR);
																		 }
																	 });																					
												}
										}
									}
								});
		
		Document.get().getElementById("r-downloadDoc").setAttribute("href", ESBApi.downloadContentUrl(record.getGuid(), record.getFilename()));
				
		if (record.getMimeType().contains(Adl3DRApi.ADL3DR_RUSSEL_MIME_TYPE)) {
			//TODO more feedback fixing for 3DR
//			ESBPacket feedback = record.getFeedbackRecords();
//			((Label)PageAssembler.elementToWidget("r-rating-info", PageAssembler.LABEL)).setText("Current rating: " + Constants.roundNumber(((feedback.getAverageRating()>0)?feedback.getAverageRating():0),2) + " (" + feedback.getRatingCount() + " votes)");
//			long percent = 0;
//			if (feedback.getAverageRating()>0)
//				percent = Math.round(feedback.getAverageRating()/5.0 * 100);
//			Document.get().getElementById("r-ratingLabel").setAttribute("style", "width:"+percent+"%");
//			fillComments0(feedback);
		}
		else {
			ESBApi.getRatings(record.getGuid(),
							  	 new ESBCallback<ESBPacket>() {
									@Override
									public void onSuccess(ESBPacket result) {
										record.parseRatings(result);
										((Label)PageAssembler.elementToWidget("r-rating-info", PageAssembler.LABEL)).setText("Current rating: " + MathUtil.roundNumber(((record.getRating()>0)?record.getRating():0),2) + " (" + record.getRatings().size() + " votes)");
										double percent = 0;
										if (record.getRating()>0)
											percent = MathUtil.roundNumber(record.getRating()/5.0 * 100, 0);
										PageAssembler.setWidth(DOM.getElementById("r-ratingLabel"), percent+"%");
										//ratings = result;
									}
									
								 	@Override
									public void onFailure(Throwable caught) {
								 		StatusWindowHandler.createMessage(StatusWindowHandler.getRatingMessageError(record.getFilename()), 
												  						  StatusRecord.ALERT_ERROR);
									}
								});

			ESBApi.getComments(record.getGuid(), 
							      new ESBCallback<ESBPacket>() {
									    @Override
										public void onSuccess(ESBPacket esbPacket) {
									    	record.getComments().clear();
									    	record.parseComments(esbPacket);										
									    	fillComments0();
											//comments = alfrescoPacket;
										}
									  
									    @Override
										public void onFailure(Throwable caught) {
									    	((Label)PageAssembler.elementToWidget("r-commentCount", PageAssembler.LABEL)).setText("No comments are available");
										}
								  });

		}
		
		((Label)PageAssembler.elementToWidget("detailMetaFilename", PageAssembler.LABEL)).setText(record.getFilename());
		
		String ext = record.getFilename().substring(record.getFilename().lastIndexOf(".")+1);
		if  (ext.equalsIgnoreCase("3dr")) {
			DOM.getElementById("r-preview").setInnerHTML("");
			//TODO look into custom metadata fields from FLR record
			//DOM.getElementById("r-preview").setInnerHTML("<a href=\"" + record.getString("russel:FLRtag") + "\" target=\"_blank\">" + record.getString("russel:FLRtag") + "</a><br/><img src='"+record.getString("screenshot")+"' class='gwt-Image'>");
		}
		else if (ext.equalsIgnoreCase("png")||ext.equalsIgnoreCase("tiff")||ext.equalsIgnoreCase("tif")||ext.equalsIgnoreCase("bmp")||ext.equalsIgnoreCase("jpg")||ext.equalsIgnoreCase("jpeg")||ext.equalsIgnoreCase("gif")) {
			DOM.getElementById("r-preview").setInnerHTML("");
			RootPanel.get("r-preview").add(new Image(ESBApi.downloadContentUrl(record.getGuid(), record.getFilename())));
		} 
		else if (ext.equalsIgnoreCase("rlk")) {
			//NOTE: rlr previews are set in MetaBuilder.addMetaDataToField because these are using the FLRtag field which is not in this record.
			ESBApi.getResource(record.getGuid(),
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
			ESBApi.getResource(record.getGuid(),
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
			String htmlString = "<video controls=\"controls\"><source src=\"" + ESBApi.downloadContentUrl(record.getGuid(), record.getFilename()) + "\" type=\"" + videoType + "\"></source></video>";			
			RootPanel.get("r-preview").getElement().setInnerHTML(htmlString);
		} else if (ext.equalsIgnoreCase("Mp3")||ext.equalsIgnoreCase("Wav")||ext.equalsIgnoreCase("Ogg")) {
			String audioType = (ext.equalsIgnoreCase("Mp3"))? "audio/mp3" : (ext.equalsIgnoreCase("Wav"))? "audio/wav" : (ext.equalsIgnoreCase("Ogg"))? "audio/ogg" : "";
			String htmlString = "<audio controls=\"controls\"><source src=\"" + ESBApi.downloadContentUrl(record.getGuid(), record.getFilename()) + "\" type=\"" + audioType + "\"></source></audio>";			
			RootPanel.get("r-preview").getElement().setInnerHTML(htmlString);
		} else if (ext.equalsIgnoreCase("swf")) {
			String htmlString = "<object id=\"FlashID\" classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\" data=\"" + ESBApi.downloadContentUrl(record.getGuid(), record.getFilename()) + "\" height=\"100%\" width=\"100%\">" +
									"<param name=\"movie\" value=\"" + ESBApi.downloadContentUrl(record.getGuid(), record.getFilename()) + "\">" + 
									"<param name=\"quality\" value=\"high\">" +
									"<param name=\"wmode\" value=\"transparent\">" +
									"<param name=\"swfversion\" value=\"10.0\">" +
									"<param name=\"allowScriptAccess\" value=\"always\">" +
									"<param name=\"BGCOLOR\" value=\"#000000\">" +
									"<param name=\"expressinstall\" value=\"Scripts/expressInstall.swf\">" +
									"<!--[if !IE]>-->" +
								    	"<object id=\"FlashID2\" type=\"application/x-shockwave-flash\" data=\"" + ESBApi.downloadContentUrl(record.getGuid(), record.getFilename()) + "\" height=\"100%\" width=\"100%\">" +
								    "<!--<![endif]-->" +
								    "<param name=\"movie\" value=\"" + ESBApi.downloadContentUrl(record.getGuid(), record.getFilename()) + "\">" +
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
		
		if (record.getMimeType().contains(Adl3DRApi.ADL3DR_RUSSEL_MIME_TYPE)) {
			meta.addMetaDataFields(record);
			//record = record;
		}
		else {
			ESBApi.getResourceMetadata(record.getGuid(),
					   new ESBCallback<ESBPacket>() {
							@Override
							public void onSuccess(ESBPacket ap) {
								record.parseESBPacket(ap); 
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
							}
							
							@Override
							public void onFailure(Throwable caught) {
								StatusWindowHandler.createMessage(StatusWindowHandler.getMetadataMessageError(record.getFilename()), 
																  StatusRecord.ALERT_ERROR);
							}
						});				
		}


		
		PageAssembler.attachHandler("r-postFlr", Event.ONCLICK, new EventCallback() {
																		@Override
																		public void onEvent(Event event) {
																			if (FLRApi.FLR_PUBLISH_MODE.equals(FLRApi.FLR_PUBLISH_ACTIONS_GENERAL)||
																				FLRApi.FLR_PUBLISH_MODE.equals(FLRApi.FLR_PUBLISH_ACTIONS_ALL)) {
																				//TODO redirect FLR post to ESB Api
																				launchFlrPost0(record);
																			}
																			else {
																				StatusWindowHandler.createMessage(StatusWindowHandler.getFLRDisabledError("object publish"), 
																						  StatusRecord.ALERT_WARNING);
																			}
																			if (FLRApi.FLR_ACTIVITY_MODE.equals(FLRApi.FLR_ACTIVITY_ACTIONS_FEEDBACK)||
																				FLRApi.FLR_ACTIVITY_MODE.equals(FLRApi.FLR_ACTIVITY_ACTIONS_ISD)||
																				FLRApi.FLR_ACTIVITY_MODE.equals(FLRApi.FLR_ACTIVITY_ACTIONS_ALL)) {
																				//TODO FLR 
																				//launchFlrActivity0(record, ratings, comments);
																			}
																			else {
																				StatusWindowHandler.createMessage(StatusWindowHandler.getFLRDisabledError("Activity Stream publish"), 
																						  StatusRecord.ALERT_WARNING);
																			}
																		}
																	});
		
//		PageAssembler.attachHandler("r-findSimilar", Event.ONCLICK, Russel.nonFunctional);
//		PageAssembler.attachHandler("r-duplicate", Event.ONCLICK, Russel.nonFunctional);
//		PageAssembler.attachHandler("r-commentCount", Event.ONCLICK, Russel.nonFunctional);
		
		attachRatingListeners0();
	}

	public boolean isTextFormat(String ext)
	{
		return ext.equalsIgnoreCase("txt")||ext.equalsIgnoreCase("rtf")||ext.equalsIgnoreCase("log")||ext.equalsIgnoreCase("tep");
	}
	
	/**
	 * display Initializes the screen, retrieves the node record if not on hand
	 */
	public void display() {
		adl3drRating = 0;
		if (record.getFilename()=="")
			ESBApi.getResourceMetadata(record.getGuid(), 
									new ESBCallback<ESBPacket>() {
										@Override
										public void onSuccess(final ESBPacket ap) {
											record.parseESBPacket(ap);
											displayGuts();
										}
										
										@Override
										public void onFailure(Throwable caught) {
											removeUnsavedEffects0();
											StatusWindowHandler.createMessage(StatusWindowHandler.getMetadataMessageError(record.getFilename()), 
																			  StatusRecord.ALERT_ERROR);
										}
									});
		else 
			displayGuts();
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
												  new HTML(templates().getDetailComment().getText()),
												  true);
		final String iDPrefix = iDs.firstElement().substring(0, iDs.firstElement().indexOf("-"));
		((Label)PageAssembler.elementToWidget(iDPrefix + "-comment-text", PageAssembler.LABEL)).setText(commentNode.getComment()); 
		((Label)PageAssembler.elementToWidget(iDPrefix + "-comment-user", PageAssembler.LABEL)).setText(commentNode.getCreatedBy());
		PageAssembler.attachHandler(iDPrefix + "-comment-delete", Event.ONCLICK, new EventCallback() {
																					@Override
																					public void onEvent(Event event) {
																						ESBApi.deleteComment(commentNode.getGuid(), 
																											new ESBCallback<ESBPacket>() {
																												@Override
																												public void onSuccess(ESBPacket alfrescoPacket) {
																													DOM.getElementById(iDPrefix + "-comment").removeFromParent();
																												}
																												
																												@Override
																												public void onFailure(Throwable caught) {
																													StatusWindowHandler.createMessage(StatusWindowHandler.getRemoveCommentMessageError(record.getFilename()),
																																					  StatusRecord.ALERT_ERROR);
																												}
																											});
																					}
																				});
	}
	
	/**
	 * fillComments0 Displays the list of retrieved 3DR comments in the DetailView
	 * @param feedback ESBPacket list of 3DR feedback records
	 */
	private void fillComments0(ESBPacket feedback) {
		RootPanel.get("r-commentArea").clear();
		//TODO 3DR comments fill
//		for (int x=0;x<feedback.getCommentCount();x++) 
//			if (x<10) 
//				fillComment0((ESBPacket)feedback.getSearchRecords().get(x));
//		
//		if (!(feedback.getCommentCount()-10<0))
//			((Label)PageAssembler.elementToWidget("r-commentCount", PageAssembler.LABEL)).setText("Show " + (feedback.getCommentCount() - 10) + " more comments");
//		else
//			((Label)PageAssembler.elementToWidget("r-commentCount", PageAssembler.LABEL)).setText("No additional comments are available");
	}
	
	/**
	 * fillComment0 Displays a particular 3DR comment in the list of comments
	 * @param feedback ESBPacket a particular 3DR feedback record
	 */
	private void fillComment0(final ESBPacket feedback) {
		//TODO 3dr comment fill
//		if (feedback.getComment() != null) {
//			Vector<String> iDs = PageAssembler.inject("r-commentArea", 
//													  "x", 
//													  new HTML(templates().getDetailComment().getText()),
//													  true);
//			final String iDPrefix = iDs.firstElement().substring(0, iDs.firstElement().indexOf("-"));
//			((Label)PageAssembler.elementToWidget(iDPrefix + "-comment-text", PageAssembler.LABEL)).setText(feedback.getComment()); 
//			((Label)PageAssembler.elementToWidget(iDPrefix + "-comment-user", PageAssembler.LABEL)).setText(feedback.getString("Submitter"));
//		}
	}

	/**
	 * launchFlrPost0 Initiates post of details for the current node to the FLR, saves FLR id from the response
	 * @param record2 ESBPacket information on the node
	 */
	private void launchFlrPost0(FileRecord record2) {
		ArrayList<String> docs = new ArrayList<String>();
		String fpJson = null;
		//TODO fix FLR
//		final ESBPacket apSaved = ap;
//		final StatusRecord flrStatus = StatusWindowHandler.createMessage(StatusWindowHandler.getFLRMessageBusy(apSaved.getFilename()),
//				  StatusRecord.ALERT_BUSY);
//		if (ap != null && ap.getRusselValue("russel:FLRid") == "") {
//			String data = FLRApi.buildFLRResourceDataDescription(ap);
//			if (data != null) {
//				docs.add(data);
//				fpJson = FLRApi.buildFLRDocuments(docs);
//				FLRApi.putFLRdata(fpJson, new FLRCallback<FLRPacket>() {
//					@Override
//					public void onSuccess(FLRPacket result) {
//						ESBPacket status = FLRApi.parseFLRResponse(FLRApi.FLR_PUBLISH_SETTING, result, apSaved);
//						if (status.getString("status").equals(FLRApi.FLR_SUCCESS)) {
//							flrStatus.setMessage(StatusWindowHandler.getFLRMessageDone(apSaved.getFilename()));
//							flrStatus.setState(StatusRecord.ALERT_SUCCESS);
//							StatusWindowHandler.alterMessage(flrStatus);
//							// save the FLR id
//							ESBPacket addFlrId = new ESBPacket();
//							String flrId = status.getString("flr_ID");
//							if ((flrId != "") && (flrId != null)) {
//								addFlrId.put("russel:FLRid", flrId);
//								ESBPacket container = new ESBPacket();
//								if (!addFlrId.toString().equals("{}")) {
//									container.put("properties", addFlrId);
//									String postString = container.toString();
////									ESBApi.updateResourceMetadata(apSaved.getGuid(),
////											  postString, 
////											  new ESBCallback<ESBPacket>() {
////													public void onSuccess(final ESBPacket nullPack) {
////														ESBApi.getFileMetadata(apSaved.getGuid(),
////																					   new ESBCallback<ESBPacket>() {
////																							@Override
////																							public void onSuccess(ESBPacket ap) {
////																								removeUnsavedEffects0();
////																								meta.addMetaDataFields(ap);
////																								Label title = ((Label)PageAssembler.elementToWidget("r-detailTitle", PageAssembler.LABEL));
////																								if (title.getText().equalsIgnoreCase("n/a"))
////																									title.setText(apSaved.getFilename());
////																							}
////																							
////																							@Override
////																							public void onFailure(Throwable caught) {
////																								StatusWindowHandler.createMessage(StatusWindowHandler.getUpdateMetadataMessageError(apSaved.getFilename()),
////																										  StatusRecord.ALERT_ERROR);
////																								removeUnsavedEffects0();
////																							}
////																						});
////																			  
////													}
////																	
////													public void onFailure(Throwable caught) {
////														StatusWindowHandler.createMessage(StatusWindowHandler.getMetadataMessageError(apSaved.getFilename()),
////																  StatusRecord.ALERT_ERROR);
////													}
////												});
//
//								}
//							}
//						}
//						else {
//							flrStatus.setMessage(StatusWindowHandler.getFLRMessageError(apSaved.getFilename()));
//							flrStatus.setState(StatusRecord.ALERT_ERROR);
//							StatusWindowHandler.alterMessage(flrStatus);							
//						}
//					}
//					
//					@Override
//					public void onFailure(Throwable caught) {
//						flrStatus.setMessage(StatusWindowHandler.getFLRMessageError(apSaved.getFilename()));
//						flrStatus.setState(StatusRecord.ALERT_ERROR);
//						StatusWindowHandler.alterMessage(flrStatus);
//					}			
//				});			
//			}
//		}
//		// If the FLR id isn't blank, then this object has already been posted.
//		else if (ap.getRusselValue("russel:FLRid") != null) {
//			flrStatus.setMessage(StatusWindowHandler.getFLRMessageDone(apSaved.getFilename()));
//			flrStatus.setState(StatusRecord.ALERT_SUCCESS);
//			StatusWindowHandler.alterMessage(flrStatus);
//		}
	}
	
	/**
	 * launchFlrActivity0 Initiates post of feedback and usage for the node to the FLR
	 * @param ap ESBPacket general information on the node
	 * @param ratings ESBPacket ratings on the node
	 * @param comments ESBPacket comments on the node
	 */
	private void launchFlrActivity0(ESBPacket ap, ESBPacket ratings,  ESBPacket comments) {
		ArrayList<String> docs = new ArrayList<String>();
		final ESBPacket apSaved = ap;
		String fpJson = null;
		String activity = null;
		//TODO fix FLR
//		final StatusRecord flrStatus = StatusWindowHandler.createMessage(StatusWindowHandler.getFLRActivityBusy(apSaved.getFilename()),
//				  StatusRecord.ALERT_BUSY);
//		
//		if (ap != null) {
//			if (FLRApi.FLR_ACTIVITY_MODE.equals(FLRApi.FLR_ACTIVITY_ACTIONS_ALL) || FLRApi.FLR_ACTIVITY_MODE.equals(FLRApi.FLR_ACTIVITY_ACTIONS_FEEDBACK)) {
//				if ((ratings != null) && (ratings.getRatingCount() > 0)) {
//					activity = FLRApi.buildFLRResourceDataActivity(ap, ratings, FLRApi.FLR_ACTIVITY_RATINGS);	
//					docs.add(activity);
//				}
//				if ((comments != null) && (comments.getCommentCount() > 0)) {
//					activity = FLRApi.buildFLRResourceDataActivity(ap, comments, FLRApi.FLR_ACTIVITY_COMMENTS);	
//					docs.add(activity);
//				}				
//			}
//			if (FLRApi.FLR_ACTIVITY_MODE.equals(FLRApi.FLR_ACTIVITY_ACTIONS_ALL) || FLRApi.FLR_ACTIVITY_MODE.equals(FLRApi.FLR_ACTIVITY_ACTIONS_ISD)) {
//				if (ap.getRusselValue("russel:epssStrategy") != null) {
//					ArrayList<ESBPacket> isdUsage = ap.parseIsdUsage();
//					for (int i=0; i<isdUsage.size(); i++) {
//						activity = FLRApi.buildFLRResourceDataActivity(ap, isdUsage.get(i), FLRApi.FLR_ACTIVITY_ISD);	
//						docs.add(activity);
//					}
//
//				}	
//			}
//			
//			if (activity != null) {
//				fpJson = FLRApi.buildFLRDocuments(docs);
//				FLRApi.putFLRactivity(fpJson, new FLRCallback<FLRPacket>() {
//					@Override
//					public void onSuccess(FLRPacket result) {
//						ESBPacket status = FLRApi.parseFLRResponse(FLRApi.FLR_ACTIVITY_SETTING, result, apSaved);
//						if (status.getString("status").equals(FLRApi.FLR_SUCCESS)) {
//							flrStatus.setMessage(StatusWindowHandler.getFLRActivityDone(apSaved.getFilename()));
//							flrStatus.setState(StatusRecord.ALERT_SUCCESS);
//							StatusWindowHandler.alterMessage(flrStatus);
//						}
//						else {
//							flrStatus.setMessage(StatusWindowHandler.getFLRActivityError(apSaved.getFilename()));
//							flrStatus.setState(StatusRecord.ALERT_ERROR);
//							StatusWindowHandler.alterMessage(flrStatus);
//						}
//					}
//					
//					@Override
//					public void onFailure(Throwable caught) {
//						flrStatus.setMessage(StatusWindowHandler.getFLRActivityError(apSaved.getFilename()));
//						flrStatus.setState(StatusRecord.ALERT_ERROR);
//						StatusWindowHandler.alterMessage(flrStatus);					}			
//				});			
//			}
//			else {
//				StatusWindowHandler.removeMessage(flrStatus);
//			}
//		}
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
												if (record.getMimeType().contains(Adl3DRApi.ADL3DR_RUSSEL_MIME_TYPE)) {
													if (Adl3DRApi.ADL3DR_ACTIVITY_MODE.equals(Adl3DRApi.ADL3DR_ACTIVITY_ACTIONS_NONE)) {
														StatusWindowHandler.createMessage(StatusWindowHandler.get3DRDisabledError("Review"), 
																  StatusRecord.ALERT_ERROR);																						
													}
													else {
														if (adl3drPartialOp != null) {
															StatusWindowHandler.removeMessage(adl3drPartialOp);
														}
														final StatusRecord adl3drStatus = StatusWindowHandler.createMessage(StatusWindowHandler.get3DRReviewMessageWarn(), 
																  StatusRecord.ALERT_BUSY);
														adl3drPartialOp = adl3drStatus; 
														adl3drRating = rating; 
														if (adl3drComment != "") {												
															final ESBPacket review = new ESBPacket();
															//The ADL 3DR UI uses a default rating of 3 for each comment, unless manually adjusted by the reviewer. 
															if (adl3drRating == 0) {
																adl3drRating = 3;
															}
																
															JsDate now = JsDate.create();
															review.put("DateTime", ThreeDRRecord.getADLdate(now));
															review.put("Rating", adl3drRating);
															review.put("ReviewText", adl3drComment);
															review.put("Submitter", Adl3DRApi.ADL3DR_RUSSEL_SUBMITTER);
															//TODO Fix 3DR
//															Adl3DRApi.putADL3DRactivity(record.getGuid(), review.toString(), new ESBCallback<ESBPacket>() {
//																		@Override
//																		public void onSuccess(ESBPacket adlPacket) {
//																			adl3drStatus.setMessage(StatusWindowHandler.get3DRReviewMessageDone("review",record.getGuid()));
//																			adl3drStatus.setState(StatusRecord.ALERT_SUCCESS);
//																			StatusWindowHandler.alterMessage(adl3drStatus); 																		
//																			
//																			Adl3DRApi.getADL3DRobjectReview(record.getGuid(), new ESBCallback<ESBPacket> () {
//																				@Override
//																				public void onFailure(Throwable caught) {
//																					StatusWindowHandler.createMessage(StatusWindowHandler.get3DRReviewMessageError("refresh",record.getGuid()), 
//																							  StatusRecord.ALERT_ERROR);
//																					}
//																				
//																				@Override
//																				public void onSuccess(ESBPacket adlPacket) {
//																					// merge it into the searchRecord and save it for DetailView
//																					//TODO more adl feedback
//																					//record.put("feedback",adlPacket);
//																					((Label)PageAssembler.elementToWidget("r-rating-info", 
//																							  PageAssembler.LABEL)).setText("Current rating: " + Constants.roundNumber(adlPacket.getAverageRating(),2) + " (" + adlPacket.getRatingCount() + " votes)");
//																					Document.get().getElementById("r-ratingLabel").setAttribute("style", "width:" + Constants.roundNumber((adlPacket.getAverageRating()/5.0)*100,2) + "%");
//																					fillComment0(review);
//																					adl3drRating = 0;
//																					adl3drComment = "";
//																					adl3drPartialOp = null;
//																				}
//																			});
//		
//																		}
//																		
//																		public void onFailure(Throwable caught) {
//																			adl3drStatus.setMessage(StatusWindowHandler.getRatingPostError("3D Repository: "+record.getGuid()));
//																			adl3drStatus.setState(StatusRecord.ALERT_ERROR);
//																			StatusWindowHandler.alterMessage(adl3drStatus); 
//																		}
//															});
															
														}
														else {
															adl3drStatus.setState(StatusRecord.ALERT_WARNING);
															StatusWindowHandler.alterMessage(adl3drStatus); 														
														}
													}
													
												} 
												else {
													ESBApi.rateObject(record.getGuid(), 
																	  rating, 
																	  new ESBCallback<ESBPacket>() {
																			@Override
																			public void onSuccess(ESBPacket result) {
																				ESBApi.getRatings(record.getGuid(), new ESBCallback<ESBPacket>() {
																					@Override
																					public void onFailure(Throwable caught) {}
																					
																					@Override 
																					public void onSuccess(ESBPacket esbPacket) {
																						record.parseRatings(esbPacket);
																						((Label)PageAssembler.elementToWidget("r-rating-info", 
																								  PageAssembler.LABEL)).setText("Current rating: " + MathUtil.roundNumber(record.getRating(),2) + " (" + record.getRatings().size() + " votes)");
																						PageAssembler.setWidth(DOM.getElementById("r-ratingLabel"), MathUtil.roundNumber((record.getRating()/5.0)*100,2) + "%");	
																					}
																				});																					
																			}
																			
																			public void onFailure(Throwable caught) {
																				StatusWindowHandler.createMessage(StatusWindowHandler.getRateOwnDocumentError(record.getFilename()),
																												  StatusRecord.ALERT_ERROR);
																			}
																		});
												}
											}
										});
		}
	}
}
