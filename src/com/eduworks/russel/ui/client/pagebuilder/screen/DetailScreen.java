/*
Copyright (c) 2012 Eduworks Corporation
All rights reserved.
 
This Software (including source code, binary code and documentation) is provided by Eduworks Corporation to
the Government pursuant to contract number W31P4Q-12 -C- 0119 dated 21 March, 2012 issued by the U.S. Army 
Contracting Command Redstone. This Software is a preliminary version in development. It does not fully operate
as intended and has not been fully tested. This Software is provided to the U.S. Government for testing and
evaluation under the following terms and conditions:

	--Any redistribution of source code, binary code, or documentation must include this notice in its entirety, 
	 starting with the above copyright notice and ending with the disclaimer below.
	 
	--Eduworks Corporation grants the U.S. Government the right to use, modify, reproduce, release, perform,
	 display, and disclose the source code, binary code, and documentation within the Government for the purpose
	 of evaluating and testing this Software.
	 
	--No other rights are granted and no other distribution or use is permitted, including without limitation 
	 any use undertaken for profit, without the express written permission of Eduworks Corporation.
	 
	--All modifications to source code must be reported to Eduworks Corporation. Evaluators and testers shall
	 additionally make best efforts to report test results, evaluation results and bugs to Eduworks Corporation
	 using in-system feedback mechanism or email to russel@eduworks.com.
	 
THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
THE COPYRIGHT HOLDER BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN 
IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
*/

package com.eduworks.russel.ui.client.pagebuilder.screen;

import java.util.Vector;

import com.eduworks.gwt.client.util.Browser;
import com.eduworks.gwt.russel.ui.client.net.AlfrescoApi;
import com.eduworks.gwt.russel.ui.client.net.AlfrescoCallback;
import com.eduworks.gwt.russel.ui.client.net.AlfrescoNullCallback;
import com.eduworks.gwt.russel.ui.client.net.AlfrescoPacket;
import com.eduworks.gwt.russel.ui.client.net.CommunicationHub;
import com.eduworks.russel.ui.client.Constants;
import com.eduworks.russel.ui.client.Russel;
import com.eduworks.russel.ui.client.epss.ProjectFileModel;
import com.eduworks.russel.ui.client.extractor.AssetExtractor;
import com.eduworks.russel.ui.client.handler.SearchTileHandler;
import com.eduworks.russel.ui.client.pagebuilder.HtmlTemplates;
import com.eduworks.russel.ui.client.pagebuilder.MetaBuilder;
import com.eduworks.russel.ui.client.pagebuilder.PageAssembler;
import com.eduworks.russel.ui.client.pagebuilder.ScreenTemplate;
import com.google.gwt.dom.client.Document;
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
	private MetaBuilder meta = new MetaBuilder(MetaBuilder.DETAIL_SCREEN);
	private SearchTileHandler tile;
	
	public DetailScreen(AlfrescoPacket r) {
		this.record = r;
		this.tile = null;
	}
	
	public DetailScreen(AlfrescoPacket r, SearchTileHandler sth) {
		this.record = r;
		this.tile = sth;
	}
	
	public void display() {
		DOM.getElementById("r-metadata-hide").setAttribute("style", "");
		DOM.getElementById("r-metadata-show").setAttribute("style", "display:none");
		DOM.getElementById("r-generalMetadata").setAttribute("style", "display: block");
		DOM.getElementById("r-educationalMetadata").setAttribute("style", "display: none");
		DOM.getElementById("r-technicalMetadata").setAttribute("style", "display: none");
		DOM.getElementById("r-relatedMetadata").setAttribute("style", "display: none");
		((Label)PageAssembler.elementToWidget("general-section", PageAssembler.LABEL)).removeStyleName("collapsed");
		((Label)PageAssembler.elementToWidget("educational-section", PageAssembler.LABEL)).addStyleName("collapsed");
		((Label)PageAssembler.elementToWidget("technical-section", PageAssembler.LABEL)).addStyleName("collapsed");
		((Label)PageAssembler.elementToWidget("related-section", PageAssembler.LABEL)).addStyleName("collapsed");
		((TextBox)PageAssembler.elementToWidget("input-comment", PageAssembler.TEXT)).setText("");
		DOM.getElementById("detailLevel1").setAttribute("disabled", "");
		DOM.getElementById("detailDistribution1").setAttribute("disabled", ""); 

		if ((record.getFilename().substring(record.getFilename().lastIndexOf(".")+1).equalsIgnoreCase("rpf")) && (!Browser.isIE())) {
			DOM.getElementById("r-editEPSSContainer").removeAttribute("style");
			PageAssembler.attachHandler("r-editEPSS", Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {
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
																												Window.alert("Fooing couldn't load project file " + caught);
																											}
																										});
														   	}
														   });
		} else
			DOM.getElementById("r-editEPSSContainer").setAttribute("style", "display:none");
		
		((Label)PageAssembler.elementToWidget("r-detailIcon", PageAssembler.LABEL)).setStyleName("r-icon");
		((Label)PageAssembler.elementToWidget("r-detailIcon", PageAssembler.LABEL)).addStyleName(AssetExtractor.getFileType(record.getFilename()));
		removeUnsavedEffects();
		
		PageAssembler.attachHandler(PageAssembler.getElementByClass(".reveal-modal-bg"), Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {
																							@Override
																							public void onEvent(Event event) {
																								if (tile!=null)
																									tile.refreshTile(null);
																							}
																						});

		PageAssembler.getElementByClass(".reveal-modal-bg").setAttribute("style", "z-index:300; opacity: 0.8");
		
		PageAssembler.attachHandler("r-detailEditUpdate", Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {
																		@Override
																		public void onEvent(Event event) {																		
																			String postString = meta.buildMetaPacket();
																			if (postString!=null)
																				AlfrescoApi.setObjectProperties(record.getNodeId(),
																												postString, 
																												new AlfrescoCallback<AlfrescoPacket>() {
																													@Override
																													public void onSuccess(final AlfrescoPacket nullPack) {
																														CommunicationHub.sendHTTP(CommunicationHub.GET, 
																																  CommunicationHub.getAlfrescoNodeURL(record.getNodeId()), 
																																  null,
																																  false,
																																  new AlfrescoCallback<AlfrescoPacket>() {
																																	@Override
																																	public void onSuccess(final AlfrescoPacket ap) {
																																		CommunicationHub.sendHTTP(CommunicationHub.GET,
																																			  CommunicationHub.getAlfrescoTagsURL(record.getNodeId()),
																																			  null,
																																			  false, 
																																			  new AlfrescoCallback<AlfrescoPacket>() {
																																				@Override
																																				public void onSuccess(AlfrescoPacket tagsAP) {
																																					meta.addMetaDataFields("@propertyDefinitionId", ap, tagsAP);
																																					Label title = ((Label)PageAssembler.elementToWidget("r-detailTitle", PageAssembler.LABEL));
																																					if (title.getText().equalsIgnoreCase("n/a"))
																																						title.setText(record.getFilename());
																																					removeUnsavedEffects();
																																				}
																																				
																																				@Override
																																				public void onFailure(Throwable caught) {
																																					removeUnsavedEffects();
																																				}
																																			});
																																	}
																																	
																																	@Override
																																	public void onFailure(Throwable caught) {
																																		removeUnsavedEffects();
																																	}
																																});
																													}
																													
																													@Override
																													public void onFailure(Throwable caught) {
																														Window.alert("Fooing failed to save metadata " + caught.getMessage());
																														removeUnsavedEffects();
																													}
																												});
																		}
																	});
		
		PageAssembler.attachHandler("r-deleteDoc", Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {
																		@Override
																		public void onEvent(Event event) {
																			if (Window.confirm("Are you sure you wish to delete this item?"))
																				AlfrescoApi.deleteDocument(record.getNodeId(), new AlfrescoCallback<AlfrescoPacket>() {
																							@Override
																							public void onFailure(Throwable caught) {
																								Window.alert("Fooing Delete document " + caught.getMessage());
																							}
		
																							@Override
																							public void onSuccess(AlfrescoPacket result) {
																								((TextBox)PageAssembler.elementToWidget("r-menuSearchBar", PageAssembler.TEXT)).setText("");
																								Russel.view.loadScreen(new HomeScreen(), true);	
																							}
																						});
																		}
																	});
		
		PageAssembler.attachHandler("comment-submit", Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {
																		@Override
																		public void onEvent(Event event) {
																			String comment = ((TextBox)PageAssembler.elementToWidget("input-comment", PageAssembler.TEXT)).getText().trim();
																			AlfrescoPacket ap = AlfrescoPacket.makePacket();
																			ap.addKeyValue("title", "\"\"");
																			ap.addKeyValue("content", "\"" + comment.replaceAll("\r", " ").replaceAll("\n", " ") + "\"");
																			if (comment!=null&&comment.trim()!=""&&!comment.equalsIgnoreCase("add comment"))
																				CommunicationHub.sendHTTP(CommunicationHub.POST, 
																						  CommunicationHub.getAlfrescoNodeURL(record.getNodeId() + "/comments"), 
																						  ap.toJSONString(), 
																						  false,
																						  new AlfrescoCallback<AlfrescoPacket>() {
																							@Override
																							public void onSuccess(AlfrescoPacket alfrescoPacket) {
																								fillComment(alfrescoPacket.getNodeId(),
																										    alfrescoPacket.getCommentAuthorUsername(), 
																											alfrescoPacket.getCommentContents());
																								((TextBox)PageAssembler.elementToWidget("input-comment", PageAssembler.TEXT)).setText("");
																							}
																							
																							@Override
																							public void onFailure(Throwable caught) {
																								((TextBox)PageAssembler.elementToWidget("input-comment", PageAssembler.TEXT)).setText("");
																								Window.alert("Fooing on posting a comment to an object " + caught.getMessage());
																							}
																						});
																		}
																	});
		
		Document.get().getElementById("r-downloadDoc").setAttribute("href", AlfrescoApi.downloadContentURL(record.getNodeId(), record.getFilename()));
				
		CommunicationHub.sendHTTP(CommunicationHub.GET,
								  CommunicationHub.getAlfrescoRatingURL(record.getNodeId()),
								  null,
								  false,
								  new AlfrescoCallback<AlfrescoPacket>() {
									@Override
									public void onFailure(Throwable caught) {
										Window.alert(caught.getMessage());
									}
				
									@Override
									public void onSuccess(AlfrescoPacket result) {
										((Label)PageAssembler.elementToWidget("r-rating-info", PageAssembler.LABEL)).setText("Current rating: " + Constants.roundNumber(((result.getAverageRating()>0)?result.getAverageRating():0),2) + " (" + result.getRatingCount() + " votes)");
										long percent = 0;
										if (result.getAverageRating()>0)
											percent = Math.round(result.getAverageRating()/5.0 * 100);
										Document.get().getElementById("r-ratingLabel").setAttribute("style", "width:"+percent+"%");
									}
								});
		
		CommunicationHub.sendHTTP(CommunicationHub.GET,
								  CommunicationHub.getAlfrescoNodeURL(record.getNodeId() + "/comments"), 
								  null, 
								  false,
								  new AlfrescoCallback<AlfrescoPacket>() {
								    @Override
									public void onSuccess(AlfrescoPacket alfrescoPacket) {
										fillComments(alfrescoPacket);
									}
								  
								    @Override
									public void onFailure(Throwable caught) {
								    	((Label)PageAssembler.elementToWidget("r-commentCount", PageAssembler.LABEL)).setText("No comments are available");
									}
								});
		
		((Label)PageAssembler.elementToWidget("detailMetaFilename", PageAssembler.LABEL)).setText(record.getFilename());
		
		String ext = record.getFilename().substring(record.getFilename().lastIndexOf(".")+1);
		if (ext.equalsIgnoreCase("png")||ext.equalsIgnoreCase("tiff")||ext.equalsIgnoreCase("tif")||ext.equalsIgnoreCase("bmp")||ext.equalsIgnoreCase("jpg")||ext.equalsIgnoreCase("jpeg")||ext.equalsIgnoreCase("gif")) {
			DOM.getElementById("r-preview").setInnerHTML("");
			RootPanel.get("r-preview").add(new Image(AlfrescoApi.downloadContentURL(record.getNodeId(), record.getFilename())));
		} else if (ext.equalsIgnoreCase("rlk")) {
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
		
		CommunicationHub.sendHTTP(CommunicationHub.GET,
								  CommunicationHub.getAlfrescoTagsURL(record.getNodeId()),
								  null,
								  false,
								  new AlfrescoCallback<AlfrescoPacket>() {
										@Override
										public void onSuccess(final AlfrescoPacket apTags) {
											CommunicationHub.sendHTTP(CommunicationHub.GET,
																	  CommunicationHub.getAlfrescoNodeURL(record.getNodeId()),
																	  null,
																	  false, 
																	  new AlfrescoCallback<AlfrescoPacket>() {
																		@Override
																		public void onSuccess(AlfrescoPacket ap) {
																			meta.addMetaDataFields("@propertyDefinitionId", ap, apTags);
																			String fouo = ap.getPropertyValue("@propertyDefinitionId", "russel:level");
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
																			
																		}
																	});
										}
										
										@Override
										public void onFailure(Throwable caught) {
											
										}
									});
		
		PageAssembler.attachHandler("r-findSimilar", Event.ONCLICK, Russel.nonFunctional);
		PageAssembler.attachHandler("r-duplicate", Event.ONCLICK, Russel.nonFunctional);
		PageAssembler.attachHandler("r-commentCount", Event.ONCLICK, Russel.nonFunctional);
		
		attachRatingListeners();
	}
	
	private void removeUnsavedEffects() {
		((Label)PageAssembler.elementToWidget("r-detailSaveAlert", PageAssembler.LABEL)).addStyleName("hide");
		((Anchor)PageAssembler.elementToWidget("r-detailEditUpdate", PageAssembler.A)).removeStyleName("blue");
		((Anchor)PageAssembler.elementToWidget("r-detailEditUpdate", PageAssembler.A)).addStyleName("white");
	}
	
	private void fillComments(AlfrescoPacket ap) {
		RootPanel.get("r-commentArea").clear();
		for (int x=0;x<ap.getCommentCount();x++) 
			if (x<10) 
				fillComment(ap.getCommentRecords().get(x).getNodeId(),
						    ap.getCommentRecords().get(x).getCommentAuthorUsername(), 
							ap.getCommentRecords().get(x).getCommentContents());
		
		if (!(ap.getCommentCount()-10<0))
			((Label)PageAssembler.elementToWidget("r-commentCount", PageAssembler.LABEL)).setText("Show " + (ap.getCommentCount() - 10) + " more comments");
		else
			((Label)PageAssembler.elementToWidget("r-commentCount", PageAssembler.LABEL)).setText("No additional comments are available");
	}
	
	private void fillComment(final String nodeId, String title, String content) {
		Vector<String> iDs = PageAssembler.getInstance().inject("r-commentArea", 
																"x", 
																new HTML(HtmlTemplates.INSTANCE.getDetailComment().getText()),
																true);
		final String iDPrefix = iDs.firstElement().substring(0, iDs.firstElement().indexOf("-"));
		((Label)PageAssembler.elementToWidget(iDPrefix + "-comment-text", PageAssembler.LABEL)).setText(content); 
		((Label)PageAssembler.elementToWidget(iDPrefix + "-comment-user", PageAssembler.LABEL)).setText(title);
		PageAssembler.attachHandler(iDPrefix + "-comment-delete", Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {
																					@Override
																					public void onEvent(Event event) {
																						AlfrescoApi.deleteObjectComment(nodeId, new AlfrescoCallback<AlfrescoPacket>() {
																							@Override
																							public void onSuccess(AlfrescoPacket alfrescoPacket) {
																								DOM.getElementById(iDPrefix + "-comment").removeFromParent();
																							}
																							
																							@Override
																							public void onFailure(Throwable caught) {
																								Window.alert("Failed to remove comment " + caught);
																							}
																						});																		
																					}
																				});
	}

	private void attachRatingListeners() {
		for (int i = 1; i <= 5; i++) {
			final Integer rating = new Integer(i);

			PageAssembler.attachHandler("r-rating-" + rating,
									   Event.ONCLICK, 
									   new AlfrescoNullCallback<AlfrescoPacket>() {
											@Override
											public void onEvent(Event event) {
													AlfrescoApi.rateObject(record.getNodeId(), 
																		  rating, 
																		  new AlfrescoCallback<AlfrescoPacket>() {
																				@Override
																				public void onSuccess(AlfrescoPacket result) {
																					((Label)PageAssembler.elementToWidget("r-rating-info", 
																														  PageAssembler.LABEL)).setText("Current rating: " + Constants.roundNumber(result.getAverageRating(),2) + " (" + result.getRatingCount() + " votes)");
																					Document.get().getElementById("r-ratingLabel").setAttribute("style", "width:" + Constants.roundNumber((result.getAverageRating()/5.0)*100,2) + "%");
																				}
																				
																				@Override
																				public void onFailure(Throwable caught) {
																					Window.alert("Can't rate your own documents");
																				}
																			});
											}
										});
		}
	}
}
