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

package com.eduworks.russel.ui.client.handler;

import java.util.Vector;

import com.eduworks.gwt.client.net.CommunicationHub;
import com.eduworks.gwt.client.net.api.AlfrescoApi;
import com.eduworks.gwt.client.net.api.AlfrescoURL;
import com.eduworks.gwt.client.net.callback.AlfrescoCallback;
import com.eduworks.gwt.client.net.callback.EventCallback;
import com.eduworks.gwt.client.net.packet.AlfrescoPacket;
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
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

public class SearchTileHandler {
	private AlfrescoSearchHandler ash;
	private AlfrescoPacket searchRecord;
	private String tileType;
	private String idPrefix;
	private SearchTileHandler tile;
	private MetaBuilder mb = new MetaBuilder(MetaBuilder.DETAIL_SCREEN);
	
	public SearchTileHandler(AlfrescoSearchHandler asHandler, String thumbIdPrefix, String searchTileType, AlfrescoPacket searchTermRecord) {
		this.tile = this;
		this.ash = asHandler;
		this.searchRecord = searchTermRecord;
		this.tileType = searchTileType;
		this.idPrefix = thumbIdPrefix;
		addHooks();
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
																							 tileType.equals(AlfrescoSearchHandler.FLR_TYPE)) 
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
																																		Window.alert("Fooing couldn't load project file " + caught);
																																	}
																																});
																					else if (tileType.equals(AlfrescoSearchHandler.RECENT_TYPE)||tileType.equals(AlfrescoSearchHandler.ASSET_TYPE) ||
																							 tileType.equals(AlfrescoSearchHandler.NOTES_TYPE) ||tileType.equals(AlfrescoSearchHandler.SEARCH_TYPE)||
																							 tileType.equals(AlfrescoSearchHandler.COLLECTION_TYPE)||tileType.equals(AlfrescoSearchHandler.FLR_TYPE))
																						Russel.view.loadScreen(new DetailScreen(searchRecord, tile), false);
																				}
																			 });
		
		PageAssembler.attachHandler(idPrefix + "-objectDelete", Event.ONCLICK, new EventCallback() {
																					@Override
																					public void onEvent(Event event) {
																						if (Window.confirm("Are you sure you wish to delete this item?"))
																							AlfrescoApi.deleteDocument(searchRecord.getNodeId(), new AlfrescoCallback<AlfrescoPacket>() {
																																					@Override
																																					public void onFailure(Throwable caught) {
																																						Window.alert("Fooing Delete document " + caught.getMessage());
																																					}
																		
																																					@Override
																																					public void onSuccess(AlfrescoPacket result) {
																																						DOM.getElementById(idPrefix+"-object").removeFromParent();
																																					}
																																				});
																					}
																				});
		
		PageAssembler.attachHandler(idPrefix + "-objectRemove", Event.ONCLICK, new EventCallback() {
																					@Override
																					public void onEvent(Event event) {
																						removeTile();
																						((Hidden)PageAssembler.elementToWidget("epssActiveRemoveAsset", PageAssembler.HIDDEN)).setValue(searchRecord.getNodeId());
																						((Hidden)PageAssembler.elementToWidget("epssActiveAddAsset", PageAssembler.HIDDEN)).setValue("");
																						PageAssembler.fireOnChange("epssActiveRemoveAsset");
																					}
																				});
		
		PageAssembler.attachHandler(idPrefix + "-objectAdd", Event.ONCLICK, new EventCallback() {
																				@Override
																				public void onEvent(Event event) {
																					Element e = DOM.getElementById(idPrefix + "-object");
																					e.removeFromParent();
																					Element td = DOM.createTD();
																					td.setInnerHTML(HtmlTemplates.INSTANCE.getEPSSNoteAssetObjectWidget().getText());
																					Vector<String> iDs = PageAssembler.getInstance().merge("epssCurrentSection", "x", td);
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
																					PageAssembler.fireOnChange("epssActiveAddAsset");
																				}
																			});
		
		PageAssembler.attachHandler(idPrefix + "-objectNotes", Event.ONCLICK, new EventCallback() {
																		@Override
																		public void onEvent(Event event) {
																			((Label)PageAssembler.elementToWidget("projectAssetTitle", PageAssembler.LABEL)).setText(DOM.getElementById(idPrefix + "-objectTitle").getInnerText());
																			((Hidden)PageAssembler.elementToWidget("epssActiveRemoveAsset", PageAssembler.HIDDEN)).setValue("");
																			((Hidden)PageAssembler.elementToWidget("epssActiveAddAsset", PageAssembler.HIDDEN)).setValue(searchRecord.getNodeId() + "," + searchRecord.getFilename());
																			((TextBox)PageAssembler.elementToWidget("inputDevNotes", PageAssembler.TEXT)).setText("");
																			PageAssembler.fireOnChange("epssActiveAddAsset");
																		}
																	});
		
		PageAssembler.attachHandler(idPrefix + "-objectRelated", Event.ONCLICK, Russel.nonFunctional);
		PageAssembler.attachHandler(idPrefix + "-objectDuplicate", Event.ONCLICK, Russel.nonFunctional);
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
		
	}
	
	public void refreshTile(final EventCallback callback) {
		((Label)PageAssembler.elementToWidget(idPrefix + "-objectState", PageAssembler.LABEL)).setStyleName("cube file");
		((Label)PageAssembler.elementToWidget(idPrefix + "-objectState", PageAssembler.LABEL)).addStyleName(AssetExtractor.getFileType(searchRecord.getFilename()));
		CommunicationHub.sendHTTP(CommunicationHub.GET,
				  AlfrescoURL.getAlfrescoRatingURL(searchRecord.getNodeId()),
				  null,
				  false,
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
						if (Document.get().getElementById(idPrefix + "-objectRating")!=null)
							Document.get().getElementById(idPrefix + "-objectRating").setAttribute("style", "width:"+percent+"%");

				    	if (callback!=null)
				    		callback.onEvent(null);
						
						CommunicationHub.sendHTTP(CommunicationHub.GET,
								  AlfrescoURL.getAlfrescoNodeURL(searchRecord.getNodeId() + "/comments"), 
								  null, 
								  false,
								  new AlfrescoCallback<AlfrescoPacket>() {
								    @Override
									public void onSuccess(final AlfrescoPacket commentPacket) {
								    	CommunicationHub.sendHTTP(CommunicationHub.GET,
								    							  AlfrescoURL.getAlfrescoNodeURL(searchRecord.getNodeId()), 
																  null, 
																  false,
																  new AlfrescoCallback<AlfrescoPacket>() {
																    @Override
																	public void onSuccess(final AlfrescoPacket ap) {
																    	String fouo = ap.getPropertyValue("@propertyDefinitionId", "russel:level");
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
																    	
																    	String val = ap.getPropertyValue("@propertyDefinitionId", "cm:title");
																    	if (val!=null&&val.trim()!="")
																    		((Label)PageAssembler.elementToWidget(idPrefix + "-objectTitle", PageAssembler.LABEL)).setText(val);
																    	else 
																    		((Label)PageAssembler.elementToWidget(idPrefix + "-objectTitle", PageAssembler.LABEL)).setText(searchRecord.getFilename());
																    	
																    	val = ap.getPropertyValue("@propertyDefinitionId", "cm:description");
																    	((Label)PageAssembler.elementToWidget(idPrefix + "-objectTitleBack", PageAssembler.LABEL)).setText(searchRecord.getFilename() + "  --  " + val);

																		AlfrescoApi.getThumbnail(searchRecord.getNodeId(), new AlfrescoCallback<AlfrescoPacket>() {
																																@Override
																																public void onFailure(Throwable caught) {
																																	mb.addMetaDataToField("@propertyDefinitionId", "cm:description", idPrefix + "-objectDescription", ap);
																																}
																																
																																@Override
																																public void onSuccess(AlfrescoPacket alfrescoPacket) {
																																	if (!Browser.isIE())
																																		DOM.getElementById(idPrefix + "-objectDescription").setAttribute("style", "background-image:url(" + alfrescoPacket.getValueString("imageURL") + ");");
																																	else {
																																		Image thumb = new Image();
																																		thumb.setUrl(alfrescoPacket.getValue("imageURL").toString());
																																		RootPanel.get(idPrefix + "-objectDescription").add(thumb);
																																	}
																																}
																														   });
																	}
																  
																    @Override
																	public void onFailure(Throwable caught) {
																    	Window.alert(caught.getMessage());
																	}
																});
									}
								  
								    @Override
									public void onFailure(Throwable caught) {
								    	Window.alert(caught.getMessage());
									}
								});
					}
				});
	}
}