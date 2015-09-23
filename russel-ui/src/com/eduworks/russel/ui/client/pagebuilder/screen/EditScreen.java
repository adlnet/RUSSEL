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
import org.vectomatic.file.File;

import com.eduworks.gwt.client.component.HtmlTemplates;
import com.eduworks.gwt.client.model.StatusRecord;
import com.eduworks.gwt.client.model.ZipRecord;
import com.eduworks.gwt.client.net.callback.ESBCallback;
import com.eduworks.gwt.client.net.callback.EventCallback;
import com.eduworks.gwt.client.net.packet.ESBPacket;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.gwt.client.pagebuilder.modal.ModalDispatch;
import com.eduworks.gwt.client.pagebuilder.overlay.OverlayDispatch;
import com.eduworks.gwt.client.pagebuilder.screen.ScreenDispatch;
import com.eduworks.gwt.client.pagebuilder.screen.ScreenTemplate;
import com.eduworks.gwt.client.ui.handler.DragDropHandler;
import com.eduworks.gwt.client.util.BlobUtils;
import com.eduworks.gwt.client.util.Browser;
import com.eduworks.gwt.client.util.Zip;
import com.eduworks.russel.ui.client.Russel;
import com.eduworks.russel.ui.client.extractor.AssetExtractor;
import com.eduworks.russel.ui.client.handler.FileHandler;
import com.eduworks.russel.ui.client.handler.SearchHandler;
import com.eduworks.russel.ui.client.handler.StatusHandler;
import com.eduworks.russel.ui.client.model.RUSSELFileRecord;
import com.eduworks.russel.ui.client.net.RusselApi;
import com.eduworks.russel.ui.client.pagebuilder.MetaBuilder;
import com.google.gwt.dom.client.Document;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HTML;
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
public class EditScreen extends ScreenTemplate {
	private static final String NO_PENDING_UPLOADS = "No Pending Uploads";
	private static final String PENDING_UPLOADS = " Pending Uploads";
	private static final String RUSSEL_LINK = "russel/link";
	private Vector<String> editIDs = new Vector<String>();
	private HashMap<String, RUSSELFileRecord> thumbIDs = new HashMap<String, RUSSELFileRecord>();
	protected Vector<RUSSELFileRecord> passedInEdits;
	private MetaBuilder meta = new MetaBuilder(MetaBuilder.EDIT_SCREEN);
		
	/**
	 * lostFocus In place to handle any processing requirements required when this screen loses focus.
	 * Called by ScreenDispatch for all RUSSEL screens.
	 */
	public void lostFocus() {

	}
	
	/**
	 * EditScreen Constructor for the class given a list of pending edits
	 * @param pendingEdits Vector<ESBPacket> 
	 */
	public EditScreen(Vector<RUSSELFileRecord> pendingEdits) {
		this.passedInEdits = pendingEdits;
	}
	
	/**
	 * EditScreen Constructor for the class with nothing pending
	 */
	public EditScreen() {
		this.passedInEdits = new Vector<RUSSELFileRecord>();
	}
	
	/**
	 * display Renders the EditScreen using appropriate templates and sets up handlers
	 */
	public void display() {
		editIDs = new Vector<String>();
		thumbIDs = new HashMap<String, RUSSELFileRecord>();

		if (Browser.isBadIE()) { 
			PageAssembler.ready(new HTML(Russel.htmlTemplates.getEditPanel().getText()));
			PageAssembler.buildContents();
		} else {
			final DropPanel dp = new DropPanel();
			dp.add(new HTML(Russel.htmlTemplates.getEditPanel().getText()));
			PageAssembler.ready(dp);
			PageAssembler.buildContents();
			hookDragDrop0(dp);
		}
		SearchHandler ash = new SearchHandler(this, false);
		
		PageAssembler.inject("flowContainer", "x", new HTML(Russel.htmlTemplates.getDetailModal().getText()), true);
		PageAssembler.inject("objDetailPanelWidget", "x", new HTML(Russel.htmlTemplates.getDetailPanel().getText()), true);
		
		ash.hookAndClear("r-menuSearchBar", "", SearchHandler.TYPE_EDIT);
		
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
																												      new HTML(Russel.htmlTemplates.getEditPanelWidget().getText()),
																												      true);
																			final String idPrefix = iDs.firstElement().substring(0, iDs.firstElement().indexOf("-"));
																			buildEmptyUploadTile0(idPrefix);
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
		
		PageAssembler.attachHandler("r-permissionModal", Event.ONCLICK, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				if (editIDs.size()>0)
					Russel.screen.loadScreen(new PermissionScreen(PermissionScreen.TYPE_RESOURCE, thumbIDs.get(editIDs.get(0)).getGuid()), false);
			}
		});

		PageAssembler.attachHandler("r-editAddLink", Event.ONCLICK, new EventCallback() {
																		@Override
																		public void onEvent(Event event) {
																			final StatusRecord status = StatusHandler.createMessage(StatusHandler.getFileMessageBusy(""), StatusRecord.ALERT_BUSY);
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
																				status.setMessage(StatusHandler.getFileMessageBusy(filename + ".rlk"));
																				StatusHandler.alterMessage(status);
																				RusselApi.uploadResource(urlBody,
																										 filename + ".rlk",
																										 new ESBCallback<ESBPacket>() {
																											 @Override
																											 public void onSuccess(final ESBPacket esbPacket) {
																												 RUSSELFileRecord fr = new RUSSELFileRecord();
																												 fr.setGuid(esbPacket.getPayloadString());
																												 fr.setFilename(filename + ".rlk");
																												 Vector<String> iDs = PageAssembler.inject("r-previewArea", 
																																					       "x", 
																																					       new HTML(Russel.htmlTemplates.getEditPanelWidget().getText()),
																																					       true);
																												 final String idPrefix = iDs.firstElement().substring(0, iDs.firstElement().indexOf("-"));
																												 fillTemplateDetails0(fr, idPrefix);
																										   		 PageAssembler.closePopup("addLinkModal");
																										   		 status.setMessage(StatusHandler.getFileMessageDone(filename + ".rlk"));
																										   		 status.setState(StatusRecord.ALERT_SUCCESS);
																												 StatusHandler.alterMessage(status);
																											}
																											
																											public void onFailure(Throwable caught) {
																												status.setMessage(StatusHandler.DUPLICATE_NAME);
																												status.setState(StatusRecord.ALERT_WARNING);
																												StatusHandler.alterMessage(status);
																											}
																										 });
																			} else {
																				status.setMessage(StatusHandler.INVALID_NAME);
																				status.setState(StatusRecord.ALERT_WARNING);
																				StatusHandler.alterMessage(status);
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
																						final StatusRecord status = StatusHandler.createMessage(StatusHandler.getUpdateMetadataMessageBusy(filename), StatusRecord.ALERT_BUSY);
																						 
																						meta.saveMetadata(thumbIDs.get(editIDs.firstElement()), new ESBCallback<ESBPacket>() {
																							@Override
																							public void onSuccess(ESBPacket esbPacket) {
																								status.setMessage(StatusHandler.getUpdateMetadataMessageDone(filename));
																								status.setState(StatusRecord.ALERT_SUCCESS);
																								StatusHandler.alterMessage(status);
																								refreshInformation0();
																							}
																							
																							public void onFailure(Throwable caught) {
																								status.setMessage(StatusHandler.getUpdateMetadataMessageError(filename));
																								status.setState(StatusRecord.ALERT_ERROR);
																								StatusHandler.alterMessage(status);
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
												      new HTML(Russel.htmlTemplates.getEditPanelWidget().getText()),
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
		formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		formPanel.setAction(RusselApi.getESBActionURL("addResource") + "?inline=true");
		FileHandler.pendingFileUploads++;
		final StatusRecord status = StatusHandler.createMessage(StatusHandler.getFileMessageBusy(""), StatusRecord.ALERT_BUSY);
		formPanel.addSubmitCompleteHandler(new SubmitCompleteHandler() {
												@Override
												public void onSubmitComplete(SubmitCompleteEvent event) {
													ESBPacket node = new ESBPacket(event.getResults());
													RUSSELFileRecord fr = new RUSSELFileRecord();
													fr.setGuid(node.getPayloadString());
													String filename = fileUpload.getFilename();
													final String justFileName = filename.substring(filename.lastIndexOf("\\")+1);
													fr.setFilename(justFileName);
													if (DOM.getElementById(idPrefix + "-objectDescription")!=null) {
														RootPanel.get(idPrefix + "-objectDescription").getElement().setInnerText("");
														fillTemplateDetails0(fr, idPrefix);
														DOM.getElementById(idPrefix + "-objectDetailButton").removeAttribute("hidden");
													}
													FileHandler.pendingFileUploads--;
													status.setState(StatusRecord.ALERT_SUCCESS);
													status.setMessage(StatusHandler.getFileMessageDone(justFileName));
													StatusHandler.alterMessage(status);
//													if (justFileName.substring(justFileName.lastIndexOf(".")+1).equalsIgnoreCase("rpf"))
//														ProjectRecord.updatePfmNodeId(fr);
													
													thumbIDs.put(idPrefix + "-object", fr);
													//checkIEAndPromptServerDisaggregation0(fr);
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
												status.setState(StatusRecord.ALERT_WARNING);
												status.setMessage(StatusHandler.INVALID_FILENAME);
												StatusHandler.alterMessage(status);
												fileUpload.setName("data");
												DOM.getElementById(idPrefix + "-object").removeFromParent();
												FileHandler.pendingFileUploads--;
												event.cancel();
											} else {
												DOM.getElementById(idPrefix + "-objectDetailButton").setAttribute("hidden", "hidden");
												status.setMessage(StatusHandler.getFileMessageBusy(justFileName));
												StatusHandler.alterMessage(status);
												DOM.getElementById("addFileData").setAttribute("name", justFileName);
												DOM.getElementById("session").setAttribute("value", "{ \"sessionid\":\"" + RusselApi.sessionId + "\" }");
												if (justFileName.indexOf(".")!=-1&&justFileName.substring(justFileName.lastIndexOf(".")+1).toLowerCase().equals("zip")) { 
													if (!Browser.isBadIE()) {
														File file = BlobUtils.getFile("addFileData");
														String filename = file.getName();
														if (filename.substring(filename.lastIndexOf(".")+1).equalsIgnoreCase("zip")&&Window.confirm("Do you wish to disaggregate the zip " + filename + " package?")) {
															Zip.grabEntries(file, new AsyncCallback<Vector<ZipRecord>>() {
																@Override public void onFailure(Throwable caught) {}
										
																@Override
																public void onSuccess(Vector<ZipRecord> zipRecords) {
																	for (int x=0;x<zipRecords.size();x++) {
																		FileHandler.pendingZipUploads.add(zipRecords.get(x));
																	}
																	doPendingUploads0();
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
		final RUSSELFileRecord node = FileHandler.pendingServerZipUploads.remove(0);
		StatusHandler.createMessage(StatusHandler.getFileMessageDone(node.getFilename()), StatusRecord.ALERT_SUCCESS);
		if (DOM.getElementById("r-previewArea")!=null) {
 			Vector<String> iDs = PageAssembler.inject("r-previewArea", 
												      "x", 
												      new HTML(Russel.htmlTemplates.getEditPanelWidget().getText()),
												      true);
			String idPrefix = iDs.firstElement().substring(0, iDs.firstElement().indexOf("-"));
			fillTemplateDetails0(node, idPrefix);
		}
//		if (node.getFilename().substring(node.getFilename().lastIndexOf(".")+1).equalsIgnoreCase("rpf"))
//			ProjectRecord.updatePfmNodeId(node);
		//checkIEAndPromptServerDisaggregation0(node);
		if (FileHandler.pendingServerZipUploads.size()!=0)
			processServerZipFiles0();
	}
	
	/**
	 * processServerZipIds0 Processes the imported node IDs when a zip was unzipped on the server side.
	 * @param ESBPacket esbPacket
	 */
	private final void processServerZipIds0(ESBPacket esbPacket) {
 		JSONArray rawImport = esbPacket.getArray("importedIDs");
 		for (int importIndex=0;importIndex<rawImport.size();importIndex++) {
 			RUSSELFileRecord node = new RUSSELFileRecord();
 			String[] importPair = rawImport.get(importIndex).toString().split(";");
			node.setGuid(importPair[0]);
			node.setFilename(importPair[1]);
			FileHandler.addPendingServerZip(node);
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
				final RUSSELFileRecord record = thumbIDs.get(editIDs.lastElement());
				RusselApi.getResourceMetadata(record.getGuid(),
											  false,
											  new ESBCallback<ESBPacket>() {
													public void onSuccess(final ESBPacket esbPacket) {
														final StatusRecord sr = StatusHandler.createMessage(StatusHandler.getGenerateMetaDataBusy(record.getFilename()), StatusRecord.STATUS_BUSY);
														PageAssembler.removeHandler("generateMetadata");
														PageAssembler.attachHandler("generateMetadata", 
																Event.ONCLICK, 
																new EventCallback() {
																	@Override
																	public void onEvent(Event event) {
																		final ESBCallback<ESBPacket> callback = new ESBCallback<ESBPacket>() {
																													@Override
																													public void onFailure(Throwable caught) {
																														sr.setState(StatusRecord.STATUS_ERROR);
																														sr.setMessage(StatusHandler.getGenerateMetaDataError(record.getFilename()));
																														StatusHandler.alterMessage(sr);
																													}
																													
																													@Override
																													public void onSuccess(ESBPacket esbPacket) {
																														sr.setState(StatusRecord.STATUS_DONE);
																														sr.setMessage(StatusHandler.getGenerateMetaDataError(record.getFilename()));
																														StatusHandler.alterMessage(sr);
																														final RUSSELFileRecord fr = new RUSSELFileRecord(esbPacket);
																														meta.addMetaDataFields(fr);
																														addUnsavedEffects0();
																													}
																											   };
																							   
																		if (record.getFilename().endsWith(".rlk")) {
																			RusselApi.getResource(record.getGuid(),
																								  false,
																								  new ESBCallback<ESBPacket>() {
																									 	@Override
																									 	public void onSuccess(ESBPacket esbPacket) {
																									 		record.setFileContents(esbPacket.getContentString());
																									 		RusselApi.generateResourceMetadata(record.getGuid(), true, callback);
																									 	}
																									 	
																									 	@Override
																									 	public void onFailure(Throwable caught) {}
																								 });
																		} else
																			RusselApi.generateResourceMetadata(record.getGuid(), false, callback);
																	}
																});
														record.parseESBPacket(esbPacket);
														meta.addMetaDataFields(record);
													};
													
													public void onFailure(Throwable caught) {};
												});
			} else
				DOM.getElementById("r-metadataToolbar").addClassName("hide");
			
			if (thumbIDs.size()<=0)
				((Label)PageAssembler.elementToWidget("editCover", PageAssembler.LABEL)).removeStyleName("hide");
			else
				((Label)PageAssembler.elementToWidget("editCover", PageAssembler.LABEL)).addStyleName("hide");
			
			if (FileHandler.countUploads()==0)
				((Label)PageAssembler.elementToWidget("r-editPendingUploads", PageAssembler.LABEL)).setText(NO_PENDING_UPLOADS);
			else
				((Label)PageAssembler.elementToWidget("r-editPendingUploads", PageAssembler.LABEL)).setText(FileHandler.countUploads() + PENDING_UPLOADS);
			
			removeUnsavedEffects0();
		}
	}
	
	/**
	 * removeUnsavedEffects0 Changes the Update button back to saved state
	 */
	private void addUnsavedEffects0() {
		((Label)PageAssembler.elementToWidget("r-save-alert", PageAssembler.LABEL)).removeStyleName("hide");
		((Anchor)PageAssembler.elementToWidget("r-editSave", PageAssembler.A)).addStyleName("blue");
		((Anchor)PageAssembler.elementToWidget("r-editSave", PageAssembler.A)).removeStyleName("white");
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
			String nId = thumbIDs.remove(tId).getGuid();
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
			final StatusRecord status = StatusHandler.createMessage(StatusHandler.getDeleteMessageBusy(filename), StatusRecord.ALERT_BUSY);
			RusselApi.deleteResource(nodeId, 
									   new ESBCallback<ESBPacket>() {
											@Override
											public void onSuccess(ESBPacket result) {
												status.setMessage(StatusHandler.getDeleteMessageDone(filename));
												status.setState(StatusRecord.ALERT_SUCCESS);
												StatusHandler.alterMessage(status);
												DOM.getElementById(thumbId).removeFromParent();
												refreshInformation0();
											}
											
											@Override
											public void onFailure(Throwable caught) {
												status.setMessage(StatusHandler.getDeleteMessageError(filename));
												status.setState(StatusRecord.ALERT_ERROR);
												StatusHandler.alterMessage(status);
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
		if (FileHandler.pendingZipUploads.size()>0) {
			ZipRecord zipEntry = FileHandler.pendingZipUploads.remove(0);
			Zip.inflateEntry(zipEntry, 
							 true, 
							 new AsyncCallback<ZipRecord>() {
								@Override public void onFailure(Throwable caught) {}
								
								@Override
								public void onSuccess(ZipRecord zipEntry) {
									String FullFilename = zipEntry.getFilename();
									if (FullFilename.lastIndexOf(".")!=-1) {
										final String filename = (FullFilename.lastIndexOf("/")!=-1) ? FullFilename.substring(FullFilename.lastIndexOf("/")+1) : FullFilename;
										if (AssetExtractor.checkAsset(filename, zipEntry.getData())) {
											final StatusRecord status = StatusHandler.createMessage(StatusHandler.getFileMessageBusy(filename), StatusRecord.ALERT_BUSY);
											if (filename.substring(filename.lastIndexOf(".")+1).equalsIgnoreCase("zip")&&
													Window.confirm("Do you wish to disaggregate the zip " + zipEntry.getFilename() + " package?")) {
												Zip.grabEntries(zipEntry.getData(), 
																new AsyncCallback<Vector<ZipRecord>>() {
																	@Override public void onFailure(Throwable caught) {}
											
																	@Override
																	public void onSuccess(Vector<ZipRecord> zipEntries) {
																		for (int x=0;x<zipEntries.size();x++) {
																			FileHandler.pendingZipUploads.add(zipEntries.get(x));
																		}
																		doPendingUploads0();
																	}
																});
											}
											if (DOM.getElementById("r-previewArea")!=null) {
												final Vector<String> iDs = PageAssembler.inject("r-previewArea", 
																								"x", 
																								new HTML(Russel.htmlTemplates.getEditPanelWidget().getText()),
																								false);
												final String idNumPrefix = iDs.get(0).substring(0, iDs.get(0).indexOf("-"));
												RootPanel.get(idNumPrefix + "-objectDescription").add(new Image("images/orbit/loading.gif"));
												DOM.getElementById(idNumPrefix + "-objectTitle").setInnerText(filename);
												DOM.getElementById(idNumPrefix + "-objectDetailButton").setAttribute("hidden", "hidden");
												DOM.getElementById(idNumPrefix + "-objectDescription").setAttribute("style", "text-align:center");
												RusselApi.uploadResource(zipEntry.getData(),
																	   filename,
																	   new ESBCallback<ESBPacket>() {
																			@Override
																			public void onFailure(Throwable caught) {
																				status.setMessage(StatusHandler.getFileMessageError(filename));
																				status.setState(StatusRecord.ALERT_ERROR);
																				StatusHandler.alterMessage(status);
																			}
											
																			@Override
																			public void onSuccess(ESBPacket esbPacket) {
																				RUSSELFileRecord fr = new RUSSELFileRecord();
																				fr.setGuid(esbPacket.getPayloadString());
																				fr.setFilename(filename);
																				status.setMessage(StatusHandler.getFileMessageDone(filename));
																				status.setState(StatusRecord.ALERT_SUCCESS);
																				StatusHandler.alterMessage(status);
																				RootPanel.get(idNumPrefix + "-objectDescription").clear();
																				DOM.getElementById(idNumPrefix + "-objectDetailButton").removeAttribute("hidden");
																				DOM.getElementById(idNumPrefix + "-objectDescription").setAttribute("style", "");
																				fillTemplateDetails0(fr, idNumPrefix);
																				String filename = fr.getFilename();
//																				if (filename.substring(filename.lastIndexOf(".")+1).equalsIgnoreCase("rpf")) {
//																					ProjectRecord.updatePfmNodeId(fr);
//																				}
																				doPendingUploads0();
																			}
																		});
											} else
												RusselApi.uploadResource(zipEntry.getData(),
																	   filename,
																	   new ESBCallback<ESBPacket>() {
																			@Override
																			public void onFailure(Throwable caught) {
																				status.setMessage(StatusHandler.getFileMessageError(filename));
																				status.setState(StatusRecord.ALERT_ERROR);
																				StatusHandler.alterMessage(status);
																			}
											
																			@Override
																			public void onSuccess(ESBPacket esbPacket) {
																				RUSSELFileRecord fr = new RUSSELFileRecord();
																				fr.setGuid(esbPacket.getPayloadString());
																				fr.setFilename(filename);
																				status.setMessage(StatusHandler.getFileMessageDone(filename));
																				status.setState(StatusRecord.ALERT_SUCCESS);
																				StatusHandler.alterMessage(status);
																				String filename = fr.getFilename();
//																				if (filename.substring(filename.lastIndexOf(".")+1).equalsIgnoreCase("rpf")) {
//																					ProjectRecord.updatePfmNodeId(fr);
//																				}
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
		FileHandler.hookDropPanel(new DragDropHandler(w) {
					@Override
					public void run(final File file)
					{
						final String filename = file.getName();
						final StatusRecord status = StatusHandler.createMessage(StatusHandler.getFileMessageBusy(filename), StatusRecord.ALERT_BUSY);
						if (filename.lastIndexOf(".")!=-1&&filename.substring(filename.lastIndexOf(".")+1).equalsIgnoreCase("zip")) { 
							if (Window.confirm("Do you wish to disaggregate the zip " + filename + " package?")) {
								Zip.grabEntries(file, 
												new AsyncCallback<Vector<ZipRecord>>() {
													@Override public void onFailure(Throwable caught) {}
							
													@Override
													public void onSuccess(Vector<ZipRecord> zipEntries) {
														for (int x=0;x<zipEntries.size();x++) {
															FileHandler.pendingZipUploads.add(zipEntries.get(x));
														}
														doPendingUploads0();
													}
												});
							}
						}
						
						if (DOM.getElementById("r-previewArea")!=null) {
							final Vector<String> iDs = PageAssembler.inject("r-previewArea", 
																    	    "x", 
																    	    new HTML(Russel.htmlTemplates.getEditPanelWidget().getText()),
																    	    false);
							final String idNumPrefix = iDs.get(0).substring(0, iDs.get(0).indexOf("-"));
							RootPanel.get(idNumPrefix + "-objectDescription").add(new Image("images/orbit/loading.gif"));
							DOM.getElementById(idNumPrefix  + "-objectTitle").setInnerText(file.getName());
							DOM.getElementById(idNumPrefix + "-objectDescription").setAttribute("style", "text-align:center");
							DOM.getElementById(idNumPrefix + "-objectDetailButton").setAttribute("hidden", "hidden");
						
							RusselApi.uploadResource(file,
												   file.getName(),
												   new ESBCallback<ESBPacket>(){
														@Override
														public void onFailure(Throwable caught) {
															status.setMessage(StatusHandler.getFileMessageError(filename));
															status.setState(StatusRecord.ALERT_ERROR);
															StatusHandler.alterMessage(status);
														}
									
														@Override
														public void onSuccess(ESBPacket result) {
															RUSSELFileRecord fr = new RUSSELFileRecord();
															fr.setGuid(result.getPayloadString());
															fr.setFilename(filename);
															status.setMessage(StatusHandler.getFileMessageDone(filename));
															status.setState(StatusRecord.ALERT_SUCCESS);
															StatusHandler.alterMessage(status);
															if (DOM.getElementById(idNumPrefix + "-objectDescription")!=null) {
																RootPanel.get(idNumPrefix + "-objectDescription").clear();
																DOM.getElementById(idNumPrefix + "-objectDetailButton").removeAttribute("hidden");
																DOM.getElementById(idNumPrefix + "-objectDescription").setAttribute("style", "");
																fillTemplateDetails0(fr, idNumPrefix);
															}
															String filename = fr.getFilename();
//															if (filename.substring(filename.lastIndexOf(".")+1).equalsIgnoreCase("rpf")) {
//																ProjectRecord.updatePfmNodeId(fr);
//															}
															thumbIDs.put(idNumPrefix + "-object", fr);
															readNext();
														}
													});
							refreshInformation0();
						}
						else
							RusselApi.uploadResource(file,
												   file.getName(),
												   new ESBCallback<ESBPacket>(){
														@Override
														public void onFailure(Throwable caught) {
															status.setMessage(StatusHandler.getFileMessageError(filename));
															status.setState(StatusRecord.ALERT_ERROR);
															StatusHandler.alterMessage(status);
														}
									
														@Override
														public void onSuccess(ESBPacket result) {
															RUSSELFileRecord fr = new RUSSELFileRecord();
															fr.setFilename(filename);
															fr.setGuid(result.getPayloadString());
															status.setMessage(StatusHandler.getFileMessageDone(filename));
															status.setState(StatusRecord.ALERT_SUCCESS);
															StatusHandler.alterMessage(status);
															String filename = fr.getFilename();
//															if (filename.substring(filename.lastIndexOf(".")+1).equalsIgnoreCase("rpf")) {
//																ProjectRecord.updatePfmNodeId(fr);
//															}
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
	 * @param jsonResult ESBPacket
	 * @param idNumPrefix tile id prefix
	 */
	private void fillTemplateDetails0(final RUSSELFileRecord fr, final String idNumPrefix)
	{
		if (DOM.getElementById("r-previewArea")!=null) {
			final String nodeID = fr.getGuid();
			thumbIDs.put(idNumPrefix + "-object", fr);
			((Label)PageAssembler.elementToWidget(idNumPrefix + "-objectTitle", PageAssembler.LABEL)).setText(fr.getFilename());
			PageAssembler.attachHandler(idNumPrefix + "-objectDetail", Event.ONCLICK, new EventCallback() {
																	  	@Override
																	  	public void onEvent(Event event) {
																	  		Russel.screen.loadScreen(new DetailScreen(fr, DetailScreen.MODAL), true);
																	  	}
																	  });
			
			DOM.getElementById(idNumPrefix + "-objectDescription").setAttribute("style", "background-image:url(" + fr.getThumbnailURL() + ");");

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
