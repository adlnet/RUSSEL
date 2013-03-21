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
import java.util.Vector;

import com.eduworks.gwt.client.net.api.Adl3DRApi;
import com.eduworks.gwt.client.net.api.AlfrescoApi;
import com.eduworks.gwt.client.net.api.FLRApi;
import com.eduworks.gwt.client.net.callback.Adl3DRCallback;
import com.eduworks.gwt.client.net.callback.AlfrescoCallback;
import com.eduworks.gwt.client.net.callback.EventCallback;
import com.eduworks.gwt.client.net.callback.FLRCallback;
import com.eduworks.gwt.client.net.packet.Adl3DRPacket;
import com.eduworks.gwt.client.net.packet.AlfrescoPacket;
import com.eduworks.gwt.client.net.packet.FLRPacket;
import com.eduworks.gwt.client.net.packet.StatusPacket;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.gwt.client.pagebuilder.ScreenTemplate;
import com.eduworks.russel.ui.client.Constants;
import com.eduworks.russel.ui.client.Russel;
import com.eduworks.russel.ui.client.epss.ProjectFileModel;
import com.eduworks.russel.ui.client.extractor.AssetExtractor;
import com.eduworks.russel.ui.client.handler.AlfrescoSearchHandler;
import com.eduworks.russel.ui.client.handler.StatusWindowHandler;
import com.eduworks.russel.ui.client.handler.TileHandler;
import com.eduworks.russel.ui.client.pagebuilder.HtmlTemplates;
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


public class DetailScreen extends ScreenTemplate {
	private AlfrescoPacket record;
	private AlfrescoPacket fullRecord = null;
	private AlfrescoPacket ratings = null;
	private AlfrescoPacket comments = null;
	
	private MetaBuilder meta = new MetaBuilder(MetaBuilder.DETAIL_SCREEN);
	private TileHandler tile = null;
	private AlfrescoSearchHandler ash = null;
	private Boolean fullScreen = false;
	private static int adl3drRating = 0;
	private static String adl3drComment = "";
	private static StatusPacket adl3drPartialOp; 
	public static boolean FULL_SCREEN = true;
	public static boolean MODAL = false;
	
	public void lostFocus() {
		
	}
	
	public DetailScreen(String id) {
		this.record = AlfrescoPacket.makePacket();
		this.record.addKeyValue("id", id);
		fullScreen = true;
	}
	
	public DetailScreen(AlfrescoPacket r, boolean isModal) {
		this.record = r;
		this.fullScreen = isModal;
	}
	
	public DetailScreen(AlfrescoPacket r, TileHandler sth) {
		this.record = r;
		this.tile = sth;
	}
	
	private native void setDisplayIE(Element element, String state) /*-{
		element.style.display = state;
	}-*/;
	
	public void displayGuts() {
		
		if (!fullScreen) {
			if (DOM.getElementById("objDetailPanelWidget") == null) {
				PageAssembler.inject("flowContainer", "x", new HTML(HtmlTemplates.INSTANCE.getDetailModal().getText()), true);
				PageAssembler.inject("objDetailPanelWidget", "x", new HTML(HtmlTemplates.INSTANCE.getDetailPanel().getText()), true);
			}
		} else {
			PageAssembler.ready(new HTML(HtmlTemplates.INSTANCE.getDetailPanel().getText()));
			PageAssembler.buildContents();
			DOM.getElementById("r-menuWorkspace").getParentElement().removeClassName("active");
			DOM.getElementById("r-menuCollections").getParentElement().removeClassName("active");
			DOM.getElementById("r-menuProjects").getParentElement().removeClassName("active");
			ash = new AlfrescoSearchHandler();
			ash.hook("r-menuSearchBar", "searchObjectPanel", AlfrescoSearchHandler.SEARCH_TYPE);
		}
			
		if (!fullScreen)
			DOM.getElementById("r-fullScreen").setAttribute("style", "");
		else
			DOM.getElementById("r-fullScreen").setAttribute("style", "display:none");
		
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
														   		ProjectFileModel.importFromAlfrescoNode(record.getNodeId(), 
														   												record.getFilename(), 
																										new AlfrescoCallback<AlfrescoPacket>() {
																											@Override
																											public void onSuccess(AlfrescoPacket alfrescoPacket) {
																												Russel.view.loadScreen(new EPSSEditScreen(new ProjectFileModel(alfrescoPacket)), true);
																											}
																											
																											@Override
																											public void onFailure(Throwable caught) {
																												StatusWindowHandler.createMessage(StatusWindowHandler.getProjectLoadMessageError(record.getFilename()),
																																				  StatusPacket.ALERT_ERROR);
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
											String postString = meta.buildMetaPacket();
											if (postString!=null) {
												final StatusPacket status = StatusWindowHandler.createMessage(StatusWindowHandler.getUpdateMetadataMessageBusy(record.getFilename()),
																											  StatusPacket.ALERT_BUSY);
												AlfrescoApi.setObjectMetadata(record.getNodeId(),
																			  postString, 
																			  new AlfrescoCallback<AlfrescoPacket>() {
																					@Override
																					public void onSuccess(final AlfrescoPacket nullPack) {
																						status.setMessage(StatusWindowHandler.getUpdateMetadataMessageDone(record.getFilename()));
																						status.setState(StatusPacket.ALERT_SUCCESS);
																						StatusWindowHandler.alterMessage(status);
																						AlfrescoApi.getMetadataAndTags(record.getNodeId(),
																													   new AlfrescoCallback<AlfrescoPacket>() {
																															@Override
																															public void onSuccess(AlfrescoPacket ap) {
																																removeUnsavedEffects0();
																																meta.addMetaDataFields(ap);
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
																						status.setState(StatusPacket.ALERT_ERROR);
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
																			Russel.view.loadScreen(new DetailScreen(record, FULL_SCREEN), true);
																		}
																	});

		
		PageAssembler.attachHandler("r-deleteDoc", Event.ONCLICK, new EventCallback() {
																		@Override
																		public void onEvent(Event event) {
																			if (Window.confirm("Are you sure you wish to delete this item?")) {
																				final StatusPacket status = StatusWindowHandler.createMessage(StatusWindowHandler.getDeleteMessageBusy(record.getFilename()),
																						 																							   StatusPacket.ALERT_BUSY);
																				AlfrescoApi.deleteDocument(record.getNodeId(), new AlfrescoCallback<AlfrescoPacket>() {
																							@Override
																							public void onFailure(Throwable caught) {
																								status.setMessage(StatusWindowHandler.getDeleteMessageError(record.getFilename()));
																								status.setState(StatusPacket.ALERT_ERROR);
																								StatusWindowHandler.alterMessage(status);
																							}
		
																							@Override
																							public void onSuccess(AlfrescoPacket result) {
																								status.setMessage(StatusWindowHandler.getDeleteMessageDone(record.getFilename()));
																								status.setState(StatusPacket.ALERT_SUCCESS);
																								StatusWindowHandler.alterMessage(status);
																								((TextBox)PageAssembler.elementToWidget("r-menuSearchBar", PageAssembler.TEXT)).setText("");
																								Russel.view.loadScreen(new HomeScreen(), true);	
																							}
																						});
																			}
																		}
																	});

		PageAssembler.attachHandler("comment-submit", Event.ONCLICK, new EventCallback() {
																		@Override
																		public void onEvent(Event event) {
																			String comment = ((TextBox)PageAssembler.elementToWidget("input-comment", PageAssembler.TEXT)).getText().trim();
																			if (comment!=null&&comment.trim()!=""&&!comment.equalsIgnoreCase("add comment")) {
																				if (record.getMimeType().contains(Adl3DRApi.ADL3DR_RUSSEL_MIME_TYPE)) {
																					if (Adl3DRApi.ADL3DR_ACTIVITY_MODE.equals(Adl3DRApi.ADL3DR_ACTIVITY_ACTIONS_NONE)) {
																						StatusWindowHandler.createMessage(StatusWindowHandler.get3DRDisabledError("Review"), 
																								  StatusPacket.ALERT_ERROR);																						
																					}
																					else {
																						if (adl3drPartialOp != null) {
																							StatusWindowHandler.removeMessage(adl3drPartialOp);
																						}
																						final StatusPacket adl3drStatus = StatusWindowHandler.createMessage(StatusWindowHandler.get3DRReviewMessageWarn(), 
																								  StatusPacket.ALERT_BUSY);
																						adl3drPartialOp = adl3drStatus; 
																						adl3drComment = comment.replaceAll("\r", " ").replaceAll("\n", " ");
																						if (adl3drRating > 0) {
																							final Adl3DRPacket review = Adl3DRPacket.makePacketReview(adl3drComment, adl3drRating);
																							Adl3DRApi.putADL3DRactivity(record.getNodeId(), review.toJSONString(), new Adl3DRCallback<Adl3DRPacket>() {
																										@Override
																										public void onSuccess(Adl3DRPacket adlPacket) {
																											adl3drStatus.setMessage(StatusWindowHandler.get3DRReviewMessageDone("review",record.getNodeId()));
																											adl3drStatus.setState(StatusPacket.ALERT_SUCCESS);	
																											StatusWindowHandler.alterMessage(adl3drStatus); 	
																											Adl3DRApi.getADL3DRobjectReview(record.getNodeId(), new Adl3DRCallback<Adl3DRPacket> () {
																												@Override
																												public void onFailure(Throwable caught) {
																													StatusWindowHandler.createMessage(StatusWindowHandler.get3DRReviewMessageError("refresh",record.getNodeId()), 
																															  StatusPacket.ALERT_ERROR);
																													}
																												
																												@Override
																												public void onSuccess(Adl3DRPacket adlPacket) {
																													// merge it into the searchRecord and save it for DetailView
																													record.addKeyValue("feedback",adlPacket);
																													((Label)PageAssembler.elementToWidget("r-rating-info", 
																															  PageAssembler.LABEL)).setText("Current rating: " + Constants.roundNumber(adlPacket.getAverageRating(),2) + " (" + adlPacket.getRatingCount() + " votes)");
																													Document.get().getElementById("r-ratingLabel").setAttribute("style", "width:" + Constants.roundNumber((adlPacket.getAverageRating()/5.0)*100,2) + "%");
																												}
																											});
																											fillComment0(review);
																											((TextBox)PageAssembler.elementToWidget("input-comment", PageAssembler.TEXT)).setText("");	
																											adl3drRating = 0;
																											adl3drComment = "";
																											adl3drPartialOp = null;
																											// merge it into the searchRecord and save it for DetailView
																											record.addKeyValue("feedback",adlPacket);
																										}
																										
																										@Override
																										public void onFailure(Throwable caught) {
																											((TextBox)PageAssembler.elementToWidget("input-comment", PageAssembler.TEXT)).setText("");
																											adl3drStatus.setMessage(StatusWindowHandler.getCommentMessageError("3D Repository: "+record.getNodeId()));
																											adl3drStatus.setState(StatusPacket.ALERT_ERROR); 
																											StatusWindowHandler.alterMessage(adl3drStatus);
																										}
																							});
																						}
																						else {
																							adl3drStatus.setState(StatusPacket.ALERT_WARNING);
																							StatusWindowHandler.alterMessage(adl3drStatus); 	
																						}
																					}
																				} 
																				else {
																					AlfrescoApi.addObjectComment(record.getNodeId(),
																							 "",
																							 comment.replaceAll("\r", " ").replaceAll("\n", " "),
																							 new AlfrescoCallback<AlfrescoPacket>() {
																								@Override
																								public void onSuccess(AlfrescoPacket alfrescoPacket) {
																									fillComment0(alfrescoPacket);
																									((TextBox)PageAssembler.elementToWidget("input-comment", PageAssembler.TEXT)).setText("");
																								}
																								
																								@Override
																								public void onFailure(Throwable caught) {
																									((TextBox)PageAssembler.elementToWidget("input-comment", PageAssembler.TEXT)).setText("");
																									StatusWindowHandler.createMessage(StatusWindowHandler.getCommentMessageError(record.getFilename()), 
																																	  StatusPacket.ALERT_ERROR);
																								}
																							});																					
																				}
																		}
																	}
																}
															);
		
		Document.get().getElementById("r-downloadDoc").setAttribute("href", AlfrescoApi.downloadContentURL(record.getNodeId(), record.getFilename()));
				
		if (record.getMimeType().contains(Adl3DRApi.ADL3DR_RUSSEL_MIME_TYPE)) {
			Adl3DRPacket feedback = record.getFeedbackRecords();
			((Label)PageAssembler.elementToWidget("r-rating-info", PageAssembler.LABEL)).setText("Current rating: " + Constants.roundNumber(((feedback.getAverageRating()>0)?feedback.getAverageRating():0),2) + " (" + feedback.getRatingCount() + " votes)");
			long percent = 0;
			if (feedback.getAverageRating()>0)
				percent = Math.round(feedback.getAverageRating()/5.0 * 100);
			Document.get().getElementById("r-ratingLabel").setAttribute("style", "width:"+percent+"%");
			fillComments0(feedback);
		}
		else {
			AlfrescoApi.getObjectRatings(record.getNodeId(),
								  	 new AlfrescoCallback<AlfrescoPacket>() {
										@Override
										public void onSuccess(AlfrescoPacket result) {
											((Label)PageAssembler.elementToWidget("r-rating-info", PageAssembler.LABEL)).setText("Current rating: " + Constants.roundNumber(((result.getAverageRating()>0)?result.getAverageRating():0),2) + " (" + result.getRatingCount() + " votes)");
											double percent = 0;
											if (result.getAverageRating()>0)
												percent = Constants.roundNumber(result.getAverageRating()/5.0 * 100, 0);
											PageAssembler.setWidth(DOM.getElementById("r-ratingLabel"), percent+"%");
											ratings = result;
										}
										
									 	@Override
										public void onFailure(Throwable caught) {
									 		StatusWindowHandler.createMessage(StatusWindowHandler.getRatingMessageError(record.getFilename()), 
													  						  StatusPacket.ALERT_ERROR);
										}
									});

			AlfrescoApi.getObjectComments(record.getNodeId(), 
								      new AlfrescoCallback<AlfrescoPacket>() {
										    @Override
											public void onSuccess(AlfrescoPacket alfrescoPacket) {
												fillComments0(alfrescoPacket);
												comments = alfrescoPacket;
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
			DOM.getElementById("r-preview").setInnerHTML("<a href=\"" + record.getValueString("russel:FLRtag") + "\" target=\"_blank\">" + record.getValueString("russel:FLRtag") + "</a><br/><img src='"+record.getValueString("screenshot")+"' class='gwt-Image'>");
		}
		else if (ext.equalsIgnoreCase("png")||ext.equalsIgnoreCase("tiff")||ext.equalsIgnoreCase("tif")||ext.equalsIgnoreCase("bmp")||ext.equalsIgnoreCase("jpg")||ext.equalsIgnoreCase("jpeg")||ext.equalsIgnoreCase("gif")) {
			DOM.getElementById("r-preview").setInnerHTML("");
			RootPanel.get("r-preview").add(new Image(AlfrescoApi.downloadContentURL(record.getNodeId(), record.getFilename())));
		} else if (ext.equalsIgnoreCase("rlk")) {
			//NOTE: rlr previews are set in MetaBuilder.addMetaDataToField because these are using the FLRtag field which is not in this record.
			AlfrescoApi.getObjectString(record.getNodeId(), record.getFilename(), new AlfrescoCallback<AlfrescoPacket>() {
										 	@Override
										 	public void onSuccess(AlfrescoPacket alfrescoPacket) {
										 		DOM.getElementById("r-preview").setInnerHTML("<a href=\"" + alfrescoPacket.getRawString() + "\" target=\"_blank\">" + alfrescoPacket.getRawString() + "</a>");
										 	}
										 	
										 	@Override
										 	public void onFailure(Throwable caught) {}
										 });
		} else if (ext.equalsIgnoreCase("txt")||ext.equalsIgnoreCase("rtf")||ext.equalsIgnoreCase("log")||ext.equalsIgnoreCase("tep")) {
			AlfrescoApi.getObjectString(record.getNodeId(), record.getFilename(), new AlfrescoCallback<AlfrescoPacket>() {
													 	@Override
													 	public void onSuccess(AlfrescoPacket alfrescoPacket) {
													 		DOM.getElementById("r-preview").setInnerHTML(alfrescoPacket.getRawString());
													 	}
													 	
													 	@Override
													 	public void onFailure(Throwable caught) {}
													 });
		} else if (ext.equalsIgnoreCase("Mp4")||ext.equalsIgnoreCase("WebM")||ext.equalsIgnoreCase("Ogg")) {
			String videoType = (ext.equalsIgnoreCase("Mp4"))? "audio/mp4" : (ext.equalsIgnoreCase("WebM"))? "audio/webm" : (ext.equalsIgnoreCase("Ogg"))? "audio/ogg" : "";
			String htmlString = "<video controls=\"controls\"><source src=\"" + AlfrescoApi.downloadContentURL(record.getNodeId(), record.getFilename()) + "\" type=\"" + videoType + "\"></source></video>";			
			RootPanel.get("r-preview").getElement().setInnerHTML(htmlString);
		} else if (ext.equalsIgnoreCase("Mp3")||ext.equalsIgnoreCase("Wav")||ext.equalsIgnoreCase("Ogg")) {
			String audioType = (ext.equalsIgnoreCase("Mp3"))? "audio/mp3" : (ext.equalsIgnoreCase("Wav"))? "audio/wav" : (ext.equalsIgnoreCase("Ogg"))? "audio/ogg" : "";
			String htmlString = "<audio controls=\"controls\"><source src=\"" + AlfrescoApi.downloadContentURL(record.getNodeId(), record.getFilename()) + "\" type=\"" + audioType + "\"></source></audio>";			
			RootPanel.get("r-preview").getElement().setInnerHTML(htmlString);
		} else if (ext.equalsIgnoreCase("swf")) {
			String htmlString = "<object id=\"FlashID\" classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\" data=\"" + AlfrescoApi.downloadContentURL(record.getNodeId(), record.getFilename()) + "\" height=\"100%\" width=\"100%\">" +
									"<param name=\"movie\" value=\"" + AlfrescoApi.downloadContentURL(record.getNodeId(), record.getFilename()) + "\">" + 
									"<param name=\"quality\" value=\"high\">" +
									"<param name=\"wmode\" value=\"transparent\">" +
									"<param name=\"swfversion\" value=\"10.0\">" +
									"<param name=\"allowScriptAccess\" value=\"always\">" +
									"<param name=\"BGCOLOR\" value=\"#000000\">" +
									"<param name=\"expressinstall\" value=\"Scripts/expressInstall.swf\">" +
									"<!--[if !IE]>-->" +
								    	"<object id=\"FlashID2\" type=\"application/x-shockwave-flash\" data=\"" + AlfrescoApi.downloadContentURL(record.getNodeId(), record.getFilename()) + "\" height=\"100%\" width=\"100%\">" +
								    "<!--<![endif]-->" +
								    "<param name=\"movie\" value=\"" + AlfrescoApi.downloadContentURL(record.getNodeId(), record.getFilename()) + "\">" +
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
			fullRecord = record;
		}
		else {
			AlfrescoApi.getMetadataAndTags(record.getNodeId(),
					   new AlfrescoCallback<AlfrescoPacket>() {
							@Override
							public void onSuccess(AlfrescoPacket ap) {
								meta.addMetaDataFields(ap);
								fullRecord = ap;
								String fouo = ap.getAlfrescoPropertyValue("russel:level");
								if (fouo.equalsIgnoreCase(Constants.FOUO)) {
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
																  StatusPacket.ALERT_ERROR);
							}
						});				
		}


		
		PageAssembler.attachHandler("r-postFlr", Event.ONCLICK, new EventCallback() {
																		@Override
																		public void onEvent(Event event) {
																			if (FLRApi.FLR_PUBLISH_MODE.equals(FLRApi.FLR_PUBLISH_ACTIONS_GENERAL)||
																				FLRApi.FLR_PUBLISH_MODE.equals(FLRApi.FLR_PUBLISH_ACTIONS_ALL)) {
																				launchFlrPost0(fullRecord);
																			}
																			else {
																				StatusWindowHandler.createMessage(StatusWindowHandler.getFLRDisabledError("object publish"), 
																						  StatusPacket.ALERT_WARNING);
																			}
																			if (FLRApi.FLR_ACTIVITY_MODE.equals(FLRApi.FLR_ACTIVITY_ACTIONS_FEEDBACK)||
																				FLRApi.FLR_ACTIVITY_MODE.equals(FLRApi.FLR_ACTIVITY_ACTIONS_ISD)||
																				FLRApi.FLR_ACTIVITY_MODE.equals(FLRApi.FLR_ACTIVITY_ACTIONS_ALL)) {
																				launchFlrActivity0(fullRecord, ratings, comments);
																			}
																			else {
																				StatusWindowHandler.createMessage(StatusWindowHandler.getFLRDisabledError("Activity Stream publish"), 
																						  StatusPacket.ALERT_WARNING);
																			}
																		}
																	});
		
//		PageAssembler.attachHandler("r-findSimilar", Event.ONCLICK, Russel.nonFunctional);
//		PageAssembler.attachHandler("r-duplicate", Event.ONCLICK, Russel.nonFunctional);
//		PageAssembler.attachHandler("r-commentCount", Event.ONCLICK, Russel.nonFunctional);
		
		attachRatingListeners0();
	}
	
	public void display() {
		adl3drRating = 0;
		if (record.getFilename()=="")
			AlfrescoApi.getMetadata(record.getNodeId(), 
									new AlfrescoCallback<AlfrescoPacket>() {
										@Override
										public void onSuccess(final AlfrescoPacket ap) {
											record = ap;
											displayGuts();
										}
										
										@Override
										public void onFailure(Throwable caught) {
											removeUnsavedEffects0();
											StatusWindowHandler.createMessage(StatusWindowHandler.getMetadataMessageError(record.getFilename()), 
																			  StatusPacket.ALERT_ERROR);
										}
									});
		else 
			displayGuts();
	}
	
	private void removeUnsavedEffects0() {
		((Label)PageAssembler.elementToWidget("r-detailSaveAlert", PageAssembler.LABEL)).addStyleName("hide");
		((Anchor)PageAssembler.elementToWidget("r-detailEditUpdate", PageAssembler.A)).removeStyleName("blue");
		((Anchor)PageAssembler.elementToWidget("r-detailEditUpdate", PageAssembler.A)).addStyleName("white");
	}
	
	private void fillComments0(AlfrescoPacket ap) {
		RootPanel.get("r-commentArea").clear();
		for (int x=0;x<ap.getCommentCount();x++) 
			if (x<10) 
				fillComment0(ap.getCommentRecords().get(x));
		
		if (!(ap.getCommentCount()-10<0))
			((Label)PageAssembler.elementToWidget("r-commentCount", PageAssembler.LABEL)).setText("Show " + (ap.getCommentCount() - 10) + " more comments");
		else
			((Label)PageAssembler.elementToWidget("r-commentCount", PageAssembler.LABEL)).setText("No additional comments are available");
	}
	
	private void fillComment0(final AlfrescoPacket commentNode) {
		Vector<String> iDs = PageAssembler.inject("r-commentArea", 
												  "x", 
												  new HTML(HtmlTemplates.INSTANCE.getDetailComment().getText()),
												  true);
		final String iDPrefix = iDs.firstElement().substring(0, iDs.firstElement().indexOf("-"));
		((Label)PageAssembler.elementToWidget(iDPrefix + "-comment-text", PageAssembler.LABEL)).setText(commentNode.getCommentContents()); 
		((Label)PageAssembler.elementToWidget(iDPrefix + "-comment-user", PageAssembler.LABEL)).setText(commentNode.getCommentAuthorUsername());
		PageAssembler.attachHandler(iDPrefix + "-comment-delete", Event.ONCLICK, new EventCallback() {
																					@Override
																					public void onEvent(Event event) {
																						AlfrescoApi.deleteObjectComment(commentNode.getNodeId(), 
																														new AlfrescoCallback<AlfrescoPacket>() {
																															@Override
																															public void onSuccess(AlfrescoPacket alfrescoPacket) {
																																DOM.getElementById(iDPrefix + "-comment").removeFromParent();
																															}
																															
																															@Override
																															public void onFailure(Throwable caught) {
																																StatusWindowHandler.createMessage(StatusWindowHandler.getRemoveCommentMessageError(record.getFilename()),
																																								  StatusPacket.ALERT_ERROR);
																															}
																														});
																					}
																				});
	}
	
	private void fillComments0(Adl3DRPacket feedback) {
		RootPanel.get("r-commentArea").clear();
		for (int x=0;x<feedback.getCommentCount();x++) 
			if (x<10) 
				fillComment0(feedback.getSearchRecords().get(x));
		
		if (!(feedback.getCommentCount()-10<0))
			((Label)PageAssembler.elementToWidget("r-commentCount", PageAssembler.LABEL)).setText("Show " + (feedback.getCommentCount() - 10) + " more comments");
		else
			((Label)PageAssembler.elementToWidget("r-commentCount", PageAssembler.LABEL)).setText("No additional comments are available");
	}
	


	private void fillComment0(final Adl3DRPacket feedback) {
		if (feedback.getComment() != null) {
			Vector<String> iDs = PageAssembler.inject("r-commentArea", 
													  "x", 
													  new HTML(HtmlTemplates.INSTANCE.getDetailComment().getText()),
													  true);
			final String iDPrefix = iDs.firstElement().substring(0, iDs.firstElement().indexOf("-"));
			((Label)PageAssembler.elementToWidget(iDPrefix + "-comment-text", PageAssembler.LABEL)).setText(feedback.getComment()); 
			((Label)PageAssembler.elementToWidget(iDPrefix + "-comment-user", PageAssembler.LABEL)).setText(feedback.getValueString("Submitter"));
		}
	}

	private void launchFlrPost0(AlfrescoPacket ap) {
		ArrayList<String> docs = new ArrayList<String>();
		String fpJson = null;
		final AlfrescoPacket apSaved = ap;
		final StatusPacket flrStatus = StatusWindowHandler.createMessage(StatusWindowHandler.getFLRMessageBusy(apSaved.getFilename()),
				  StatusPacket.ALERT_BUSY);

		if (ap != null && ap.getRusselValue("russel:FLRid") == "") {
			String data = FLRApi.buildFLRResourceDataDescription(ap);
			if (data != null) {
				docs.add(data);
				fpJson = FLRApi.buildFLRDocuments(docs);
				FLRApi.putFLRdata(fpJson, new FLRCallback<FLRPacket>() {
					@Override
					public void onSuccess(FLRPacket result) {
						AlfrescoPacket status = FLRApi.parseFLRResponse(FLRApi.FLR_PUBLISH_SETTING, result, apSaved);
						if (status.getValue("status").equals(FLRApi.FLR_SUCCESS)) {
							flrStatus.setMessage(StatusWindowHandler.getFLRMessageDone(apSaved.getFilename()));
							flrStatus.setState(StatusPacket.ALERT_SUCCESS);
							StatusWindowHandler.alterMessage(flrStatus);
							// save the FLR id
							AlfrescoPacket addFlrId = AlfrescoPacket.makePacket();
							String flrId = status.getValueString("flr_ID");
							if ((flrId != "") && (flrId != null)) {
								addFlrId.addKeyValue("russel:FLRid", flrId);
								AlfrescoPacket container = AlfrescoPacket.makePacket();
								if (!addFlrId.toJSONString().equals("{}")) {
									container.addKeyValue("properties", addFlrId);
									String postString = container.toJSONString();
									AlfrescoApi.setObjectMetadata(apSaved.getNodeId(),
											  postString, 
											  new AlfrescoCallback<AlfrescoPacket>() {
													@Override
													public void onSuccess(final AlfrescoPacket nullPack) {
														AlfrescoApi.getMetadataAndTags(apSaved.getNodeId(),
																					   new AlfrescoCallback<AlfrescoPacket>() {
																							@Override
																							public void onSuccess(AlfrescoPacket ap) {
																								removeUnsavedEffects0();
																								meta.addMetaDataFields(ap);
																								Label title = ((Label)PageAssembler.elementToWidget("r-detailTitle", PageAssembler.LABEL));
																								if (title.getText().equalsIgnoreCase("n/a"))
																									title.setText(apSaved.getFilename());
																							}
																							
																							@Override
																							public void onFailure(Throwable caught) {
																								StatusWindowHandler.createMessage(StatusWindowHandler.getUpdateMetadataMessageError(apSaved.getFilename()),
																										  StatusPacket.ALERT_ERROR);
																								removeUnsavedEffects0();
																							}
																						});
																			  
													}
																	
													@Override
													public void onFailure(Throwable caught) {
														StatusWindowHandler.createMessage(StatusWindowHandler.getMetadataMessageError(apSaved.getFilename()),
																  StatusPacket.ALERT_ERROR);
													}
												});

								}
							}
						}
						else {
							flrStatus.setMessage(StatusWindowHandler.getFLRMessageError(apSaved.getFilename()));
							flrStatus.setState(StatusPacket.ALERT_ERROR);
							StatusWindowHandler.alterMessage(flrStatus);							
						}
					}
					
					@Override
					public void onFailure(Throwable caught) {
						flrStatus.setMessage(StatusWindowHandler.getFLRMessageError(apSaved.getFilename()));
						flrStatus.setState(StatusPacket.ALERT_ERROR);
						StatusWindowHandler.alterMessage(flrStatus);
					}			
				});			
			}
		}
		// If the FLR id isn't blank, then this object has already been posted.
		else if (ap.getRusselValue("russel:FLRid") != null) {
			flrStatus.setMessage(StatusWindowHandler.getFLRMessageDone(apSaved.getFilename()));
			flrStatus.setState(StatusPacket.ALERT_SUCCESS);
			StatusWindowHandler.alterMessage(flrStatus);
		}
	}
	
	private void launchFlrActivity0(AlfrescoPacket ap, AlfrescoPacket ratings,  AlfrescoPacket comments) {
		ArrayList<String> docs = new ArrayList<String>();
		final AlfrescoPacket apSaved = ap;
		String fpJson = null;
		String activity = null;
		final StatusPacket flrStatus = StatusWindowHandler.createMessage(StatusWindowHandler.getFLRActivityBusy(apSaved.getFilename()),
				  StatusPacket.ALERT_BUSY);
		
		if (ap != null) {
			if (FLRApi.FLR_ACTIVITY_MODE.equals(FLRApi.FLR_ACTIVITY_ACTIONS_ALL) || FLRApi.FLR_ACTIVITY_MODE.equals(FLRApi.FLR_ACTIVITY_ACTIONS_FEEDBACK)) {
				if ((ratings != null) && (ratings.getRatingCount() > 0)) {
					activity = FLRApi.buildFLRResourceDataActivity(ap, ratings, FLRApi.FLR_ACTIVITY_RATINGS);	
					docs.add(activity);
				}
				if ((comments != null) && (comments.getCommentCount() > 0)) {
					activity = FLRApi.buildFLRResourceDataActivity(ap, comments, FLRApi.FLR_ACTIVITY_COMMENTS);	
					docs.add(activity);
				}				
			}
			if (FLRApi.FLR_ACTIVITY_MODE.equals(FLRApi.FLR_ACTIVITY_ACTIONS_ALL) || FLRApi.FLR_ACTIVITY_MODE.equals(FLRApi.FLR_ACTIVITY_ACTIONS_ISD)) {
				if (ap.getRusselValue("russel:epssStrategy") != null) {
					ArrayList<AlfrescoPacket> isdUsage = ap.parseIsdUsage();
					for (int i=0; i<isdUsage.size(); i++) {
						activity = FLRApi.buildFLRResourceDataActivity(ap, isdUsage.get(i), FLRApi.FLR_ACTIVITY_ISD);	
						docs.add(activity);
					}

				}	
			}
			
			if (activity != null) {
				fpJson = FLRApi.buildFLRDocuments(docs);
				FLRApi.putFLRactivity(fpJson, new FLRCallback<FLRPacket>() {
					@Override
					public void onSuccess(FLRPacket result) {
						AlfrescoPacket status = FLRApi.parseFLRResponse(FLRApi.FLR_ACTIVITY_SETTING, result, apSaved);
						if (status.getValue("status").equals(FLRApi.FLR_SUCCESS)) {
							flrStatus.setMessage(StatusWindowHandler.getFLRActivityDone(apSaved.getFilename()));
							flrStatus.setState(StatusPacket.ALERT_SUCCESS);
							StatusWindowHandler.alterMessage(flrStatus);
						}
						else {
							flrStatus.setMessage(StatusWindowHandler.getFLRActivityError(apSaved.getFilename()));
							flrStatus.setState(StatusPacket.ALERT_ERROR);
							StatusWindowHandler.alterMessage(flrStatus);
						}
					}
					
					@Override
					public void onFailure(Throwable caught) {
						flrStatus.setMessage(StatusWindowHandler.getFLRActivityError(apSaved.getFilename()));
						flrStatus.setState(StatusPacket.ALERT_ERROR);
						StatusWindowHandler.alterMessage(flrStatus);					}			
				});			
			}
			else {
				StatusWindowHandler.removeMessage(flrStatus);
			}
		}
	}

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
																  StatusPacket.ALERT_ERROR);																						
													}
													else {
														if (adl3drPartialOp != null) {
															StatusWindowHandler.removeMessage(adl3drPartialOp);
														}
														final StatusPacket adl3drStatus = StatusWindowHandler.createMessage(StatusWindowHandler.get3DRReviewMessageWarn(), 
																  StatusPacket.ALERT_BUSY);
														adl3drPartialOp = adl3drStatus; 
														adl3drRating = rating; 
														if (adl3drComment != "") {												
															final Adl3DRPacket review = Adl3DRPacket.makePacketReview(adl3drComment, adl3drRating);
															Adl3DRApi.putADL3DRactivity(record.getNodeId(), review.toJSONString(), new Adl3DRCallback<Adl3DRPacket>() {
																		@Override
																		public void onSuccess(Adl3DRPacket adlPacket) {
																			adl3drStatus.setMessage(StatusWindowHandler.get3DRReviewMessageDone("review",record.getNodeId()));
																			adl3drStatus.setState(StatusPacket.ALERT_SUCCESS);
																			StatusWindowHandler.alterMessage(adl3drStatus); 																		
																			
																			Adl3DRApi.getADL3DRobjectReview(record.getNodeId(), new Adl3DRCallback<Adl3DRPacket> () {
																				@Override
																				public void onFailure(Throwable caught) {
																					StatusWindowHandler.createMessage(StatusWindowHandler.get3DRReviewMessageError("refresh",record.getNodeId()), 
																							  StatusPacket.ALERT_ERROR);
																					}
																				
																				@Override
																				public void onSuccess(Adl3DRPacket adlPacket) {
																					// merge it into the searchRecord and save it for DetailView
																					record.addKeyValue("feedback",adlPacket);
																					((Label)PageAssembler.elementToWidget("r-rating-info", 
																							  PageAssembler.LABEL)).setText("Current rating: " + Constants.roundNumber(adlPacket.getAverageRating(),2) + " (" + adlPacket.getRatingCount() + " votes)");
																					Document.get().getElementById("r-ratingLabel").setAttribute("style", "width:" + Constants.roundNumber((adlPacket.getAverageRating()/5.0)*100,2) + "%");
																					fillComment0(review);
																					adl3drRating = 0;
																					adl3drComment = "";
																					adl3drPartialOp = null;
																				}
																			});
		
																		}
																		
																		@Override
																		public void onFailure(Throwable caught) {
																			adl3drStatus.setMessage(StatusWindowHandler.getRatingPostError("3D Repository: "+record.getNodeId()));
																			adl3drStatus.setState(StatusPacket.ALERT_ERROR);
																			StatusWindowHandler.alterMessage(adl3drStatus); 
																		}
															});
															
														}
														else {
															adl3drStatus.setState(StatusPacket.ALERT_WARNING);
															StatusWindowHandler.alterMessage(adl3drStatus); 														
														}
													}
													
												} 
												else {
													AlfrescoApi.rateObject(record.getNodeId(), 
																		  rating, 
																		  new AlfrescoCallback<AlfrescoPacket>() {
																				@Override
																				public void onSuccess(AlfrescoPacket result) {
																					((Label)PageAssembler.elementToWidget("r-rating-info", 
																														  PageAssembler.LABEL)).setText("Current rating: " + Constants.roundNumber(result.getAverageRating(),2) + " (" + result.getRatingCount() + " votes)");
																					PageAssembler.setWidth(DOM.getElementById("r-ratingLabel"), Constants.roundNumber((result.getAverageRating()/5.0)*100,2) + "%");
																				}
																				
																				@Override
																				public void onFailure(Throwable caught) {
																					StatusWindowHandler.createMessage(StatusWindowHandler.getRateOwnDocumentError(record.getFilename()),
																													  StatusPacket.ALERT_ERROR);
																				}
																			});
												}
											}
										});
		}
	}
}
