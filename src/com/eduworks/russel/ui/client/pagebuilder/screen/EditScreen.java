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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.vectomatic.dnd.DropPanel;
import org.vectomatic.file.Blob;
import org.vectomatic.file.File;

import com.eduworks.gwt.client.net.CommunicationHub;
import com.eduworks.gwt.client.net.api.AlfrescoApi;
import com.eduworks.gwt.client.net.api.AlfrescoURL;
import com.eduworks.gwt.client.net.callback.AlfrescoCallback;
import com.eduworks.gwt.client.net.callback.EventCallback;
import com.eduworks.gwt.client.net.packet.AlfrescoPacket;
import com.eduworks.gwt.client.net.packet.StatusPacket;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.gwt.client.ui.handler.DragDropHandler;
import com.eduworks.gwt.client.util.Base64;
import com.eduworks.gwt.client.util.BlobUtils;
import com.eduworks.gwt.client.util.Browser;
import com.eduworks.gwt.client.util.Zip;
import com.eduworks.russel.ui.client.Russel;
import com.eduworks.russel.ui.client.epss.ProjectFileModel;
import com.eduworks.russel.ui.client.extractor.AssetExtractor;
import com.eduworks.russel.ui.client.handler.AlfrescoSearchHandler;
import com.eduworks.russel.ui.client.handler.SearchHandler;
import com.eduworks.russel.ui.client.handler.StatusWindowHandler;
import com.eduworks.russel.ui.client.pagebuilder.MetaBuilder;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * EditScreen class
 * Extends ScreenTemplate
 * Defines methods and handlers for the Edit screen.
 * 
 * @author Eduworks Corporation
 */
public class EditScreen extends Screen {
	private static final String NO_PENDING_UPLOADS = "No Pending Uploads";
	private static final String PENDING_UPLOADS = " Pending Uploads";
	private static final String RUSSEL_LINK = "russel/link";
	private Vector<String> editIDs = new Vector<String>();
	private HashMap<String, String> thumbIDs = new HashMap<String, String>();
	protected Vector<AlfrescoPacket> passedInEdits;
	private MetaBuilder meta = new MetaBuilder(MetaBuilder.EDIT_SCREEN);
		
	/**
	 * lostFocus In place to handle any processing requirements required when this screen loses focus.
	 * Called by ScreenDispatch for all RUSSEL screens.
	 */
	public void lostFocus() {

	}
	
	/**
	 * EditScreen Constructor for the class given a list of pending edits
	 * @param pendingEdits Vector<AlfrescoPacket> 
	 */
	public EditScreen(Vector<AlfrescoPacket> pendingEdits) {
		this.passedInEdits = pendingEdits;
	}
	
	/**
	 * EditScreen Constructor for the class with nothing pending
	 */
	public EditScreen() {
		this.passedInEdits = new Vector<AlfrescoPacket>();
	}
	
	/**
	 * display Renders the EditScreen using appropriate templates and sets up handlers
	 */
	public void display() {
		editIDs = new Vector<String>();
		thumbIDs = new HashMap<String, String>();

		if (DOM.getElementById("r-generalMetadata") == null) {
			PageAssembler.inject("contentPane", "x", new HTML(templates().getDetailModal().getText()), true);
			PageAssembler.inject("objDetailPanelWidget", "x", new HTML(templates().getDetailPanel().getText()), true);
		}

		if (Browser.isIE()) { 
			PageAssembler.ready(new HTML(templates().getEditPanel().getText()));
			PageAssembler.buildContents();
		} else {
			final DropPanel dp = new DropPanel();
			dp.add(new HTML(templates().getEditPanel().getText()));
			PageAssembler.ready(dp);
			PageAssembler.buildContents();
			hookDragDrop0(dp);
		}
		
		SearchHandler ash = new AlfrescoSearchHandler();
		
		ash.hook("r-menuSearchBar", "", AlfrescoSearchHandler.EDIT_TYPE);
	
		//PageAssembler.attachHandler("r-saveAs", Event.ONCLICK, Russel.nonFunctional);
		
		PageAssembler.attachHandler("r-editDelete", 
									Event.ONCLICK, 
									new EventCallback() {
										@Override
										public void onEvent(Event event) {
											if (editIDs.size()==1) {
												if (Window.confirm("Are you sure you wish to delete the selected item?"))
													deleteObjects0();
											} else if (editIDs.size()>=2) {
												if (Window.confirm("Are you sure you wish to delete " + editIDs.size() + " items?"))
													deleteObjects0();
											}
										}
									});
		
		PageAssembler.attachHandler("r-editSelectAll", 
									Event.ONCLICK, 
									new EventCallback() {
										@Override
										public void onEvent(Event event) {
											selectAllObjects0();
										}
									});
		
		PageAssembler.attachHandler("r-editSelectNone", 
									Event.ONCLICK, 
									new EventCallback() {
										@Override
										public void onEvent(Event event) {
											selectionClear0();
										}
									});

		PageAssembler.attachHandler("r-editAddFile", Event.ONCLICK, new EventCallback() {
																		@Override
																		public void onEvent(Event event) {
																			Vector<String> iDs = PageAssembler.inject("r-previewArea", 
																												      "x", 
																												      new HTML(templates().getEditPanelWidget().getText()),
																												      true);
																			final String idPrefix = iDs.firstElement().substring(0, iDs.firstElement().indexOf("-"));
																			buildEmptyUploadTile0(idPrefix);
																			thumbIDs.put(idPrefix + "-object", null);
																	   		PageAssembler.closePopup("addFileModal");

																		}
																	});
		
		PageAssembler.attachHandler("r-editAddFile-reset", Event.ONCLICK, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				doFileReset0();
			}
		});
		PageAssembler.attachHandler("r-editAddFile-cancel", Event.ONCLICK, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				doFileReset0();
		   		PageAssembler.closePopup("addFileModal");
			}
		});

		PageAssembler.attachHandler("r-editAddLink-reset", Event.ONCLICK, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				doLinkReset0();
			}
		});
		PageAssembler.attachHandler("r-editAddLink-cancel", Event.ONCLICK, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				doLinkReset0();
		   		PageAssembler.closePopup("addLinkModal");
			}
		});

		PageAssembler.attachHandler("r-editAddLink", Event.ONCLICK, new EventCallback() {
																		@Override
																		public void onEvent(Event event) {
																			final StatusPacket status = StatusWindowHandler.createMessage(StatusWindowHandler.getFileMessageBusy(""), StatusPacket.ALERT_BUSY);
																			if (((TextBox)PageAssembler.elementToWidget("editTitleLinkField", PageAssembler.TEXT)).getText()!="") {
																				String rawFilename = ((TextBox)PageAssembler.elementToWidget("editTitleLinkField", PageAssembler.TEXT)).getText();
																				String filenameRaw = "";
																				for (int filenameIndex=0;filenameIndex<rawFilename.length();filenameIndex++)
																					if ((rawFilename.codePointAt(filenameIndex)>=48&&rawFilename.codePointAt(filenameIndex)<=57)||
																						(rawFilename.codePointAt(filenameIndex)>=65&&rawFilename.codePointAt(filenameIndex)<=90)||
																						(rawFilename.codePointAt(filenameIndex)>=97&&rawFilename.codePointAt(filenameIndex)<=122))
																						filenameRaw += rawFilename.charAt(filenameIndex);
																				final String filename = filenameRaw;
																				String urlBody = ((TextBox)PageAssembler.elementToWidget("editLinkField", PageAssembler.TEXT)).getText();
																				if (urlBody.indexOf("://")==-1)
																					urlBody = "http://" + urlBody;
																				status.setMessage(StatusWindowHandler.getFileMessageBusy(filename + ".rlk"));
																				StatusWindowHandler.alterMessage(status);
																				AlfrescoApi.createObjectNode(filename + ".rlk", 
																											 Base64.encode(urlBody), 
																											 RUSSEL_LINK, 
																											 AlfrescoApi.getCurrentDirectoryPath(), 
																											 AlfrescoApi.DOCUMENT_OBJECT, 
																											 new AlfrescoCallback<AlfrescoPacket>() {
																												@Override
																												public void onSuccess(final AlfrescoPacket alfrescoAtom) {
																													String nodeRef = alfrescoAtom.getAlfrescoPropertyValue("cmis:objectId");
																													AlfrescoApi.addAspectToNode(nodeRef.substring(nodeRef.lastIndexOf("/")+1), 
																																				Russel.RUSSEL_ASPECTS.split(","), 
																																				new AlfrescoCallback<AlfrescoPacket>() {
																																					@Override 
																																					public void onSuccess(AlfrescoPacket alfrescoAspect) {
																																						Vector<String> iDs = PageAssembler.inject("r-previewArea", 
																																															      "x", 
																																															      new HTML(templates().getEditPanelWidget().getText()),
																																															      true);
																																						final String idPrefix = iDs.firstElement().substring(0, iDs.firstElement().indexOf("-"));
																																						fillTemplateDetails0(alfrescoAtom, idPrefix);
																																				   		PageAssembler.closePopup("addLinkModal");
																																				   		status.setMessage(StatusWindowHandler.getFileMessageDone(filename + ".rlk"));
																																				   		status.setState(StatusPacket.ALERT_SUCCESS);
																																						StatusWindowHandler.alterMessage(status);
																																					}
																																					
																																					@Override
																																					public void onFailure(Throwable caught) {
																																						status.setMessage(StatusWindowHandler.getFileMessageError(filename + ".rlk"));
																																						status.setState(StatusPacket.ALERT_ERROR);
																																						StatusWindowHandler.alterMessage(status);
																																					}
																																				});
																												}
																												
																												@Override
																												public void onFailure(Throwable caught) {
																													status.setMessage(StatusWindowHandler.DUPLICATE_NAME);
																													status.setState(StatusPacket.ALERT_WARNING);
																													StatusWindowHandler.alterMessage(status);
																												}
																											});
																			} else {
																				status.setMessage(StatusWindowHandler.INVALID_NAME);
																				status.setState(StatusPacket.ALERT_WARNING);
																				StatusWindowHandler.alterMessage(status);
																			}
																		}
																	}); 
		
		PageAssembler.attachHandler("r-editSave", Event.ONCLICK, new EventCallback() {
																	@Override
																	public void onEvent(Event event) {
																			new Timer() {
																				@Override
																				public void run() {
																					if (editIDs.size()>0) {
																						final String idNumPrefix = editIDs.firstElement().substring(0, editIDs.firstElement().indexOf("-"));
																						final String filename = DOM.getElementById(idNumPrefix + "-objectTitle").getInnerText();
																						final StatusPacket status = StatusWindowHandler.createMessage(StatusWindowHandler.getUpdateMetadataMessageBusy(filename), StatusPacket.ALERT_BUSY);
																						meta.saveMetadata(thumbIDs.get(editIDs.firstElement()), new AlfrescoCallback<AlfrescoPacket>() {
																							@Override
																							public void onSuccess(AlfrescoPacket alfrescoPacket) {
																								status.setMessage(StatusWindowHandler.getUpdateMetadataMessageDone(filename));
																								status.setState(StatusPacket.ALERT_SUCCESS);
																								StatusWindowHandler.alterMessage(status);
																								refreshInformation0();
																							}
																							
																							@Override
																							public void onFailure(Throwable caught) {
																								status.setMessage(StatusWindowHandler.getUpdateMetadataMessageError(filename));
																								status.setState(StatusPacket.ALERT_ERROR);
																								StatusWindowHandler.alterMessage(status);
																							}
																						});
																					}
																				}
																			}.schedule(100); 
																		}
																 });
		
		while (passedInEdits.size()>0) {
			Vector<String> iDs = PageAssembler.inject("r-previewArea", 
												      "x", 
												      new HTML(templates().getEditPanelWidget().getText()),
													  false);
			final String idPrefix = iDs.firstElement().substring(0, iDs.firstElement().indexOf("-"));
			fillTemplateDetails0(passedInEdits.remove(0), idPrefix);	
		}
		refreshInformation0();
	}
	
	/**
	 * addLinkHandlers0 Associates handlers for the designated object's links
	 * @param idPrefix String Id for the object
	 */
	private void addLinkHandlers0(final String idPrefix) {
		PageAssembler.attachHandler(idPrefix + "-object", 
									Event.ONCLICK, 
									new EventCallback() {
										@Override
										public void onEvent(Event event) {
											toggleSelection0(idPrefix + "-object");
										}
									});
		
		PageAssembler.attachHandler(idPrefix + "-objectDescription", 
									Event.ONCLICK, 
									new EventCallback() {
										@Override
										public void onEvent(Event event) {
											toggleSelection0(idPrefix + "-object");
										}
									});
	}  

	/**
	 * buildEmptyUploadTile0 Initiates a tile in the EditScreen to represent a file that is uploading
	 * @param idPrefix String id assigned to the new tile
	 */
	private void buildEmptyUploadTile0(final String idPrefix) {
		final FormPanel formPanel = (FormPanel)PageAssembler.elementToWidget("addFileForm", PageAssembler.FORM);
		final FileUpload fileUpload = (FileUpload)PageAssembler.elementToWidget("addFileData", PageAssembler.FILE);
		final Hidden hiddenDestination = (Hidden)PageAssembler.elementToWidget("addFileDestination", PageAssembler.HIDDEN);
		final Hidden hiddenOverwrite = (Hidden)PageAssembler.elementToWidget("addFileOverwrite", PageAssembler.HIDDEN);
		formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		formPanel.setAction(AlfrescoURL.getAlfrescoUploadURL());
		hiddenDestination.setValue(AlfrescoURL.ALFRESCO_STORE_TYPE + "://" + AlfrescoURL.ALFRESCO_STORE_ID + "/" + AlfrescoApi.currentDirectoryId);
		hiddenOverwrite.setValue("false");
		StatusWindowHandler.pendingFileUploads++;
		final StatusPacket status = StatusWindowHandler.createMessage(StatusWindowHandler.getFileMessageBusy(""), StatusPacket.ALERT_BUSY);
		formPanel.addSubmitCompleteHandler(new SubmitCompleteHandler() {
										@Override
										public void onSubmitComplete(SubmitCompleteEvent event) {
											AlfrescoPacket node = AlfrescoPacket.wrap(CommunicationHub.parseJSON(CommunicationHub.unwrapJSONString(event.getResults())));
											if (DOM.getElementById(idPrefix + "-objectDescription")!=null) {
												RootPanel.get(idPrefix + "-objectDescription").getElement().setInnerText("");
												fillTemplateDetails0(node, idPrefix);
												DOM.getElementById(idPrefix + "-objectDetailButton").removeAttribute("hidden");
											}
											StatusWindowHandler.pendingFileUploads--;
											String filename = fileUpload.getFilename();
											final String justFileName = filename.substring(filename.lastIndexOf("\\")+1);
											status.setState(StatusPacket.ALERT_SUCCESS);
											status.setMessage(StatusWindowHandler.getFileMessageDone(justFileName));
											StatusWindowHandler.alterMessage(status);
											if (justFileName.substring(justFileName.lastIndexOf(".")+1).equalsIgnoreCase("rpf"))
												ProjectFileModel.updatePfmNodeId(node);
											
											checkIEAndPromptServerDisaggregation0(node);
											refreshInformation0();
										}
									});
		formPanel.addSubmitHandler(new SubmitHandler() {
								@Override
								public void onSubmit(SubmitEvent event) {
									String fn = fileUpload.getFilename();
									String justFileName = fn.substring(fn.lastIndexOf("\\")+1);
									PageAssembler.attachHandler(idPrefix + "-objectDetail", 
																Event.ONCLICK, 
																new EventCallback() {
																	@Override
																	public void onEvent(Event event) {
																		toggleSelection0(idPrefix + "-object");
																	}
																});
									if (fn=="") {
										status.setState(StatusPacket.ALERT_WARNING);
										status.setMessage(StatusWindowHandler.INVALID_FILENAME);
										StatusWindowHandler.alterMessage(status);
										fileUpload.setName("data");
										DOM.getElementById(idPrefix + "-object").removeFromParent();
										StatusWindowHandler.pendingFileUploads--;
										event.cancel();
									} else {
										DOM.getElementById(idPrefix + "-objectDetailButton").setAttribute("hidden", "hidden");
										status.setMessage(StatusWindowHandler.getFileMessageBusy(justFileName));
										StatusWindowHandler.alterMessage(status);
										if (justFileName.indexOf(".")!=-1&&justFileName.substring(justFileName.lastIndexOf(".")+1).toLowerCase().equals("zip")) { 
											if (!Browser.isIE()) {
												File file = BlobUtils.getFile("addFileData");
												String filename = file.getName();
												if (filename.substring(filename.lastIndexOf(".")+1).equalsIgnoreCase("zip")&&Window.confirm("Do you wish to disaggregate the zip " + filename + " package?")) {
													Zip.grabEntries(file, new AlfrescoCallback<AlfrescoPacket>() {
														@Override public void onFailure(Throwable caught) {}
								
														@SuppressWarnings("unchecked")
														@Override
														public void onSuccess(AlfrescoPacket alfrescoPacket) {
															if (alfrescoPacket.hasKey("zipEntries")) {
																JsArray<AlfrescoPacket> zipEntries = ((JsArray<AlfrescoPacket>)alfrescoPacket.getValue("zipEntries"));
																for (int x=0;x<zipEntries.length();x++)
																	StatusWindowHandler.pendingZipUploads.add(zipEntries.get(x));
																doPendingUploads0();
															}
														}
													});
												}
											}
										}	
										RootPanel.get(idPrefix + "-objectDescription").add(new Image("images/orbit/loading.gif"));
										DOM.getElementById(idPrefix + "-objectTitle").setInnerText(justFileName);
										DOM.getElementById(idPrefix + "-objectDescription").setAttribute("style", "text-align:center");
									}
								}
							});
		formPanel.submit();
		refreshInformation0();
	}
	
	/**
	 * processServerZipFiles0 Continues the processing of the list of pendingServerZipUploads
	 */
	private void processServerZipFiles0() {
		final AlfrescoPacket node = StatusWindowHandler.pendingServerZipUploads.remove(0);
		StatusWindowHandler.createMessage(StatusWindowHandler.getFileMessageDone(node.getFilename()), StatusPacket.ALERT_SUCCESS);
		if (DOM.getElementById("r-previewArea")!=null) {
 			Vector<String> iDs = PageAssembler.inject("r-previewArea", 
												      "x", 
												      new HTML(templates().getEditPanelWidget().getText()),
												      true);
			String idPrefix = iDs.firstElement().substring(0, iDs.firstElement().indexOf("-"));
			fillTemplateDetails0(node, idPrefix);
		}
		if (node.getFilename().substring(node.getFilename().lastIndexOf(".")+1).equalsIgnoreCase("rpf"))
			ProjectFileModel.updatePfmNodeId(node);
		checkIEAndPromptServerDisaggregation0(node);
		if (StatusWindowHandler.pendingServerZipUploads.size()!=0)
			processServerZipFiles0();
	}
	
	/**
	 * checkIEAndPromptServerDisaggregation0 Determines if the zip is being uploaded in IE, and if so initiates disaggregation on the server side.
	 * @param node AlfrescoPacket 
	 */
	private final void checkIEAndPromptServerDisaggregation0(final AlfrescoPacket node) {
		if (Browser.isIE()&&node.getFilename().indexOf(".")!=-1&&node.getFilename().substring(node.getFilename().indexOf(".")+1).equalsIgnoreCase("zip")&&
				Window.confirm("Do you wish to disaggregate the zip " + node.getFilename() + " package?"))
				AlfrescoApi.importZipPackage(node.getNodeId(),
											 new AlfrescoCallback<AlfrescoPacket>() {
										     	@Override
										     	public void onFailure(Throwable caught) {
										     		StatusWindowHandler.createMessage(StatusWindowHandler.getZipImportMessageError(node.getFilename()), StatusPacket.ALERT_ERROR);
										     	}
										     	
										     	@Override
										     	public void onSuccess(AlfrescoPacket alfrescoPacket) {
										     		processServerZipIds0(alfrescoPacket);
										     		processServerZipFiles0();
										     	}
											 });
	}
	
	/**
	 * processServerZipIds0 Processes the imported node IDs when a zip was unzipped on the server side.
	 * @param alfrescoPacket AlfrescoPacket
	 */
	private final void processServerZipIds0(AlfrescoPacket alfrescoPacket) {
 		JsArrayString rawImport = (JsArrayString) alfrescoPacket.getValue("importedIDs");
 		for (int importIndex=0;importIndex<rawImport.length();importIndex++) {
 			AlfrescoPacket node = AlfrescoPacket.makePacket();
 			String[] importPair = rawImport.get(importIndex).split(";");
			node.addKeyValue("id", importPair[0]);
			node.addKeyValue("fileName", importPair[1]);
 			StatusWindowHandler.addPendingServerZip(node);
 		}
	}
	
	/**
	 * doColors0 Calls the javascript function getSecurityColor to apply appropriate color schemes to Classification, Level, and Distribution fields
	 * @param s String
	 * @return String
	 */
	private final native String doColors0(String s) /*-{
		return $wnd.getSecurityColor(s);
	}-*/;

	/**
	 * doFileReset0 Calls the javascript function resetAddFileModal to reset the Add File dialog
	 * @return
	 */
	private final native String doFileReset0() /*-{
		return $wnd.resetAddFileModal();
	}-*/;

	/**
	 * doLinkReset0 Calls the javascript function resetAddLinkModal to reset the Add Link dialog
	 * @return
	 */
	private final native String doLinkReset0() /*-{
		return $wnd.resetAddLinkModal();
	}-*/;

	/**
	 * refreshInformation0 Updates screen information with current count values
	 */
	private void refreshInformation0() {
		if (DOM.getElementById("r-previewArea")!=null) {
			if (editIDs.size()>0) {
				DOM.getElementById("r-metadataToolbar").removeClassName("hide");
				AlfrescoApi.getMetadataAndTags(thumbIDs.get(editIDs.lastElement()), 
															new AlfrescoCallback<AlfrescoPacket>() {
																public void onSuccess(AlfrescoPacket alfrescoPacket) {
																	meta.addMetaDataFields(alfrescoPacket);
																};
																
																public void onFailure(Throwable caught) {};
															});
			} else
				DOM.getElementById("r-metadataToolbar").addClassName("hide");
			
			if (thumbIDs.size()<=0)
				((Label)PageAssembler.elementToWidget("editCover", PageAssembler.LABEL)).removeStyleName("hide");
			else
				((Label)PageAssembler.elementToWidget("editCover", PageAssembler.LABEL)).addStyleName("hide");
			
			if (StatusWindowHandler.countUploads()==0)
				((Label)PageAssembler.elementToWidget("r-editPendingUploads", PageAssembler.LABEL)).setText(NO_PENDING_UPLOADS);
			else
				((Label)PageAssembler.elementToWidget("r-editPendingUploads", PageAssembler.LABEL)).setText(StatusWindowHandler.countUploads() + PENDING_UPLOADS);
			
			removeUnsavedEffects0();
		}
	}
	
	/**
	 * removeUnsavedEffects0 Changes the Update button back to saved state
	 */
	private void removeUnsavedEffects0() {
		((Label)PageAssembler.elementToWidget("r-save-alert", PageAssembler.LABEL)).addStyleName("hide");
		((Anchor)PageAssembler.elementToWidget("r-editSave", PageAssembler.A)).removeStyleName("blue");
		((Anchor)PageAssembler.elementToWidget("r-editSave", PageAssembler.A)).addStyleName("white");
	}
	
	/**
	 * selectAllObjects0 Changes the state of all tiles on the Edit panel to selected
	 */
	private void selectAllObjects0() {
		editIDs.clear();	
		for (Iterator<String> e = thumbIDs.keySet().iterator();e.hasNext();)
			editIDs.add(e.next());
		for (int x=0;x<editIDs.size();x++)
			((Label)PageAssembler.elementToWidget(editIDs.get(x), PageAssembler.LABEL)).addStyleName("active");
		refreshInformation0();
	}
	
	/**
	 * selectionClear0 Changes the state of all tiles on the Edit panel to deselected
	 */
	private void selectionClear0() {
		for (int x=0;x<editIDs.size();x++)
			((Label)PageAssembler.elementToWidget(editIDs.get(x), PageAssembler.LABEL)).removeStyleName("active");
		editIDs.clear();
		refreshInformation0();
	}

	/**
	 * deleteObjects0 Deletes all objects that are currently selected in the Edit panel
	 */
	private void deleteObjects0() {
		while (editIDs.size()>0) {
			String tId = editIDs.remove(0);
			String nId = thumbIDs.remove(tId);
			if (nId!=null)
				deleteObject0(nId, tId);
			else {
				Document.get().getElementById(tId).removeFromParent();
				refreshInformation0();
			}
		}
	}
	
	/**
	 * deleteObject0 Deletes the object indicated by the thumbId
	 * @param nodeId String
	 * @param thumbId String
	 */
	private void deleteObject0(final String nodeId, final String thumbId) {
		if (nodeId!=null) {
			final String idNumPrefix = thumbId.substring(0, thumbId.indexOf("-"));
			final String filename = DOM.getElementById(idNumPrefix + "-objectTitle").getInnerText();
			final StatusPacket status = StatusWindowHandler.createMessage(StatusWindowHandler.getDeleteMessageBusy(filename), StatusPacket.ALERT_BUSY);
			AlfrescoApi.deleteDocument(nodeId, 
									   new AlfrescoCallback<AlfrescoPacket>() {
											@Override
											public void onSuccess(AlfrescoPacket result) {
												status.setMessage(StatusWindowHandler.getDeleteMessageDone(filename));
												status.setState(StatusPacket.ALERT_SUCCESS);
												StatusWindowHandler.alterMessage(status);
												DOM.getElementById(thumbId).removeFromParent();
												refreshInformation0();
											}
											
											@Override
											public void onFailure(Throwable caught) {
												status.setMessage(StatusWindowHandler.getDeleteMessageError(filename));
												status.setState(StatusPacket.ALERT_ERROR);
												StatusWindowHandler.alterMessage(status);
												DOM.getElementById(thumbId).removeFromParent();
												refreshInformation0();
											}
										});
		} else
			DOM.getElementById(thumbId).removeFromParent();
	}
	
	/**
	 * doPendingUploads0 Continues to process all pendingZipUploads
	 */
	private void doPendingUploads0() {
		if (StatusWindowHandler.pendingZipUploads.size()>0) {
			AlfrescoPacket zipEntry = StatusWindowHandler.pendingZipUploads.remove(0);
			Zip.inflateEntry(zipEntry, 
							 true, 
							 new AlfrescoCallback<AlfrescoPacket>() {
								@Override public void onFailure(Throwable caught) {}
								
								@Override
								public void onSuccess(AlfrescoPacket alfrescoPacket) {
									String FullFilename = alfrescoPacket.getValueString("zipEntryFilename");
									if (FullFilename.lastIndexOf(".")!=-1) {
										final String filename = (FullFilename.lastIndexOf("/")!=-1)?FullFilename.substring(FullFilename.lastIndexOf("/")+1):FullFilename;
										if (AssetExtractor.checkAsset(filename, ((Blob)alfrescoPacket.getValue("zipEntryData").cast()))) {
											final StatusPacket status = StatusWindowHandler.createMessage(StatusWindowHandler.getFileMessageBusy(filename), StatusPacket.ALERT_BUSY);
											if (filename.substring(filename.lastIndexOf(".")+1).equalsIgnoreCase("zip")&&Window.confirm("Do you wish to disaggregate the zip " + alfrescoPacket.getValue("zipEntryFilename") + " package?")) {
												Zip.grabEntries(((Blob)alfrescoPacket.getValue("zipEntryData").cast()), new AlfrescoCallback<AlfrescoPacket>() {
													@Override public void onFailure(Throwable caught) {}
							
													@SuppressWarnings("unchecked")
													@Override
													public void onSuccess(AlfrescoPacket alfrescoPacket) {
														if (alfrescoPacket.hasKey("zipEntries")) {
															JsArray<AlfrescoPacket> zipEntries = ((JsArray<AlfrescoPacket>)alfrescoPacket.getValue("zipEntries"));
															for (int x=0;x<zipEntries.length();x++)
																StatusWindowHandler.pendingZipUploads.add(zipEntries.get(x));
															doPendingUploads0();
														}
													}
												});
											}
											if (DOM.getElementById("r-previewArea")!=null) {
												final Vector<String> iDs = PageAssembler.inject("r-previewArea", 
																								"x", 
																								new HTML(templates().getEditPanelWidget().getText()),
																								false);
												final String idNumPrefix = iDs.get(0).substring(0, iDs.get(0).indexOf("-"));
												RootPanel.get(idNumPrefix + "-objectDescription").add(new Image("images/orbit/loading.gif"));
												DOM.getElementById(idNumPrefix + "-objectTitle").setInnerText(filename);
												DOM.getElementById(idNumPrefix + "-objectDetailButton").setAttribute("hidden", "hidden");
												DOM.getElementById(idNumPrefix + "-objectDescription").setAttribute("style", "text-align:center");
												AlfrescoApi.uploadFile(((Blob)alfrescoPacket.getValue("zipEntryData").cast()),
																	   filename,
																	   Russel.RUSSEL_ASPECTS.split(","),
																	   new AlfrescoCallback<AlfrescoPacket>() {
																			@Override
																			public void onFailure(Throwable caught) {
																				status.setMessage(StatusWindowHandler.getFileMessageError(filename));
																				status.setState(StatusPacket.ALERT_ERROR);
																				StatusWindowHandler.alterMessage(status);
																			}
											
																			@Override
																			public void onSuccess(AlfrescoPacket alfrescoPacket) {
																				status.setMessage(StatusWindowHandler.getFileMessageDone(filename));
																				status.setState(StatusPacket.ALERT_SUCCESS);
																				StatusWindowHandler.alterMessage(status);
																				RootPanel.get(idNumPrefix + "-objectDescription").clear();
																				DOM.getElementById(idNumPrefix + "-objectDetailButton").removeAttribute("hidden");
																				DOM.getElementById(idNumPrefix + "-objectDescription").setAttribute("style", "");
																				fillTemplateDetails0(alfrescoPacket, idNumPrefix);
																				String filename = alfrescoPacket.getFilename();
																				if (filename.substring(filename.lastIndexOf(".")+1).equalsIgnoreCase("rpf")) {
																					ProjectFileModel.updatePfmNodeId(alfrescoPacket);
																				}
																				doPendingUploads0();
																			}
																		});
											} else
												AlfrescoApi.uploadFile(((Blob)alfrescoPacket.getValue("zipEntryData").cast()),
																	   filename,
																	   Russel.RUSSEL_ASPECTS.split(","),
																	   new AlfrescoCallback<AlfrescoPacket>() {
																			@Override
																			public void onFailure(Throwable caught) {
																				status.setMessage(StatusWindowHandler.getFileMessageError(filename));
																				status.setState(StatusPacket.ALERT_ERROR);
																				StatusWindowHandler.alterMessage(status);
																			}
											
																			@Override
																			public void onSuccess(AlfrescoPacket alfrescoPacket) {
																				status.setMessage(StatusWindowHandler.getFileMessageDone(filename));
																				status.setState(StatusPacket.ALERT_SUCCESS);
																				StatusWindowHandler.alterMessage(status);
																				String filename = alfrescoPacket.getFilename();
																				if (filename.substring(filename.lastIndexOf(".")+1).equalsIgnoreCase("rpf")) {
																					ProjectFileModel.updatePfmNodeId(alfrescoPacket);
																				}
																				doPendingUploads0();
																			}
																		});
										} else 
											doPendingUploads0();
									} else
										doPendingUploads0();
								}
							});
			refreshInformation0();
		}
	}
	
	
	/**
	 * hookDragDrop0 Hooks the status window to the drag and drop panel. Processes an upload.
	 * @param w DropPanel
	 */
	private void hookDragDrop0(DropPanel w) {
		StatusWindowHandler.hookDropPanel(new DragDropHandler(w) {
					@Override
					public void run(final File file)
					{
						final String filename = file.getName();
						final StatusPacket status = StatusWindowHandler.createMessage(StatusWindowHandler.getFileMessageBusy(filename), StatusPacket.ALERT_BUSY);
						if (filename.lastIndexOf(".")!=-1&&filename.substring(filename.lastIndexOf(".")+1).equalsIgnoreCase("zip")) { 
							if (Window.confirm("Do you wish to disaggregate the zip " + filename + " package?")) {
								Zip.grabEntries(file, 
												new AlfrescoCallback<AlfrescoPacket>() {
													@Override public void onFailure(Throwable caught) {}
							
													@SuppressWarnings("unchecked")
													@Override
													public void onSuccess(AlfrescoPacket alfrescoPacket) {
														if (alfrescoPacket.hasKey("zipEntries")) {
															JsArray<AlfrescoPacket> zipEntries = ((JsArray<AlfrescoPacket>)alfrescoPacket.getValue("zipEntries"));
															for (int x=0;x<zipEntries.length();x++)
																StatusWindowHandler.pendingZipUploads.add(zipEntries.get(x));
															doPendingUploads0();
														}
													}
												});
							}
						}
						
						if (DOM.getElementById("r-previewArea")!=null) {
							final Vector<String> iDs = PageAssembler.inject("r-previewArea", 
																    	    "x", 
																    	    new HTML(templates().getEditPanelWidget().getText()),
																    	    false);
							final String idNumPrefix = iDs.get(0).substring(0, iDs.get(0).indexOf("-"));
							RootPanel.get(idNumPrefix + "-objectDescription").add(new Image("images/orbit/loading.gif"));
							DOM.getElementById(idNumPrefix  + "-objectTitle").setInnerText(file.getName());
							DOM.getElementById(idNumPrefix + "-objectDescription").setAttribute("style", "text-align:center");
							DOM.getElementById(idNumPrefix + "-objectDetailButton").setAttribute("hidden", "hidden");
						
							AlfrescoApi.uploadFile(file,
												   file.getName(),
												   Russel.RUSSEL_ASPECTS.split(","),
												   new AlfrescoCallback<AlfrescoPacket>(){
														@Override
														public void onFailure(Throwable caught) {
															status.setMessage(StatusWindowHandler.getFileMessageError(filename));
															status.setState(StatusPacket.ALERT_ERROR);
															StatusWindowHandler.alterMessage(status);
														}
									
														@Override
														public void onSuccess(AlfrescoPacket result) {
															status.setMessage(StatusWindowHandler.getFileMessageDone(filename));
															status.setState(StatusPacket.ALERT_SUCCESS);
															StatusWindowHandler.alterMessage(status);
															if (DOM.getElementById(idNumPrefix + "-objectDescription")!=null) {
																RootPanel.get(idNumPrefix + "-objectDescription").clear();
																DOM.getElementById(idNumPrefix + "-objectDetailButton").removeAttribute("hidden");
																DOM.getElementById(idNumPrefix + "-objectDescription").setAttribute("style", "");
																fillTemplateDetails0(result, idNumPrefix);
															}
															String filename = result.getFilename();
															if (filename.substring(filename.lastIndexOf(".")+1).equalsIgnoreCase("rpf")) {
																ProjectFileModel.updatePfmNodeId(result);
															}
															readNext();
														}
													});
							thumbIDs.put(idNumPrefix + "-object", "");
							refreshInformation0();
						}
						else
							AlfrescoApi.uploadFile(file,
												   file.getName(),
												   Russel.RUSSEL_ASPECTS.split(","),
												   new AlfrescoCallback<AlfrescoPacket>(){
														@Override
														public void onFailure(Throwable caught) {
															status.setMessage(StatusWindowHandler.getFileMessageError(filename));
															status.setState(StatusPacket.ALERT_ERROR);
															StatusWindowHandler.alterMessage(status);
														}
									
														@Override
														public void onSuccess(AlfrescoPacket result) {
															status.setMessage(StatusWindowHandler.getFileMessageDone(filename));
															status.setState(StatusPacket.ALERT_SUCCESS);
															StatusWindowHandler.alterMessage(status);
															String filename = result.getFilename();
															if (filename.substring(filename.lastIndexOf(".")+1).equalsIgnoreCase("rpf")) {
																ProjectFileModel.updatePfmNodeId(result);
															}
															readNext();
														}
													});
					}
				});
	}
	
	/**
	 * toggleSelection0 Reverses selection state of an upload tile
	 * @param id String element id
	 */
	private void toggleSelection0(String id) {
		if (editIDs.contains(id)) {
			editIDs.remove(id);
			((Label)PageAssembler.elementToWidget(id, PageAssembler.LABEL)).removeStyleName("active");
		} else {
			editIDs.add(id);
			((Label)PageAssembler.elementToWidget(id, PageAssembler.LABEL)).addStyleName("active");
		}
		refreshInformation0();
	}
	
	/**
	 * fillTemplateDetails0 Updates tile with node info
	 * @param jsonResult AlfrescoPacket
	 * @param idNumPrefix tile id prefix
	 */
	private void fillTemplateDetails0(final AlfrescoPacket jsonResult, final String idNumPrefix)
	{
		if (DOM.getElementById("r-previewArea")!=null) {
			final String nodeID = jsonResult.getNodeId();
			thumbIDs.put(idNumPrefix + "-object", nodeID);
			((Label)PageAssembler.elementToWidget(idNumPrefix + "-objectTitle", PageAssembler.LABEL)).setText(jsonResult.getFilename());
			PageAssembler.attachHandler(idNumPrefix + "-objectDetail", Event.ONCLICK, new EventCallback() {
																	  	@Override
																	  	public void onEvent(Event event) {
																	  		view().loadDetailScreen(jsonResult, DetailScreen.MODAL);
																	  	}
																	  });
			AlfrescoApi.getThumbnail(nodeID, new AlfrescoCallback<AlfrescoPacket>() {
												@Override
												public void onSuccess(AlfrescoPacket alfrescoPacket) {
													DOM.getElementById(idNumPrefix + "-objectDescription").setAttribute("style", "background-image:url(" + alfrescoPacket.getValueString("imageURL") + ");");
												}
												
												@Override
												public void onFailure(Throwable caught) {
													((Label)PageAssembler.elementToWidget(idNumPrefix + "-objectDescription", PageAssembler.LABEL)).setText(jsonResult.getDescription());
												}
											});
			PageAssembler.attachHandler(idNumPrefix + "-objectDescription", 
										Event.ONCLICK, 
										new EventCallback() {
											@Override
											public void onEvent(Event event) {
												toggleSelection0(idNumPrefix + "-object");
											}
										});
			refreshInformation0();
		}
	}
}
