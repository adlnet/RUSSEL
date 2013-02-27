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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.vectomatic.dnd.DropPanel;
import org.vectomatic.file.Blob;
import org.vectomatic.file.File;

import com.eduworks.gwt.client.net.CommunicationHub;
import com.eduworks.gwt.client.net.api.AlfrescoApi;
import com.eduworks.gwt.client.net.api.AlfrescoURL;
import com.eduworks.gwt.client.net.api.FLRApi;
import com.eduworks.gwt.client.net.callback.AlfrescoCallback;
import com.eduworks.gwt.client.net.callback.EventCallback;
import com.eduworks.gwt.client.net.callback.FLRCallback;
import com.eduworks.gwt.client.net.packet.AlfrescoPacket;
import com.eduworks.gwt.client.net.packet.FLRPacket;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.gwt.client.pagebuilder.ScreenTemplate;
import com.eduworks.gwt.client.ui.handler.DragDropHandler;
import com.eduworks.gwt.client.util.Base64;
import com.eduworks.gwt.client.util.Browser;
import com.eduworks.gwt.client.util.Zip;
import com.eduworks.russel.ui.client.Russel;
import com.eduworks.russel.ui.client.epss.ProjectFileModel;
import com.eduworks.russel.ui.client.extractor.AssetExtractor;
import com.eduworks.russel.ui.client.handler.AlfrescoSearchHandler;
import com.eduworks.russel.ui.client.pagebuilder.HtmlTemplates;
import com.eduworks.russel.ui.client.pagebuilder.MetaBuilder;
import com.google.gwt.core.client.JsArray;
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

public class EditScreen extends ScreenTemplate {
	private static final String IMS_MANIFEST = "imsmanifest.xml";
	private static final String NO_PENDING_UPLOADS = "No Pending Uploads";
	private static final String PENDING_UPLOADS = " Pending Uploads";
	private Vector<String> editIDs = new Vector<String>();
	private HashMap<String, String> thumbIDs = new HashMap<String, String>();
	private Vector<AlfrescoPacket> pendingEdits;
	private Vector<AlfrescoPacket> pendingZipUploads;
	private int pendingFileUploads;
	private MetaBuilder meta = new MetaBuilder(MetaBuilder.EDIT_SCREEN);
	private DragDropHandler ddh = null;
		
	public void lostFocus() {
//		ddh.readQueue.clear();
//		pendingZipUploads.clear();
//		pendingFileUploads = 0;
	}
	
	public EditScreen(Vector<AlfrescoPacket> pendingEdits) {
		this.pendingEdits = pendingEdits;
	}
	
	public EditScreen() {
		this.pendingEdits = new Vector<AlfrescoPacket>();
	}
	
	public void display() {
		editIDs = new Vector<String>();
		thumbIDs = new HashMap<String, String>();
		pendingZipUploads = new Vector<AlfrescoPacket>();
		pendingFileUploads = 0;

		if (DOM.getElementById("r-generalMetadata") == null) {
			PageAssembler.getInstance().inject("contentPane", "x", new HTML(HtmlTemplates.INSTANCE.getDetailModal().getText()), true);
			PageAssembler.getInstance().inject("objDetailPanelWidget", "x", new HTML(HtmlTemplates.INSTANCE.getDetailPanel().getText()), true);
		}

		if (Browser.isIE()) { 
			PageAssembler.getInstance().ready(new HTML(HtmlTemplates.INSTANCE.getEditPanel().getText()));
			PageAssembler.getInstance().buildContents();
		} else {
			final DropPanel dp = new DropPanel();
			dp.add(new HTML(HtmlTemplates.INSTANCE.getEditPanel().getText()));
			PageAssembler.getInstance().ready(dp);
			PageAssembler.getInstance().buildContents();
			hookDragDrop(dp);
		}
		
		AlfrescoSearchHandler ash = new AlfrescoSearchHandler();
		
		ash.hook("r-menuSearchBar", "", AlfrescoSearchHandler.EDIT_TYPE);
	
		PageAssembler.attachHandler("r-saveAs", Event.ONCLICK, Russel.nonFunctional);
		
		PageAssembler.attachHandler("zipImport", Event.ONCLICK, new EventCallback() {
																	@Override
																	public void onEvent(Event event) {
																		AlfrescoApi.importZipPackage(thumbIDs.get(editIDs.lastElement()),
																									 new AlfrescoCallback<AlfrescoPacket>() {
																								     	@Override
																								     	public void onFailure(Throwable caught) {
																								     		Window.alert("foobar'ed");
																								     	}
																								     	
																								     	@Override
																								     	public void onSuccess(AlfrescoPacket alfrescoPacket) {
																								     		Window.alert(alfrescoPacket.toJSONString());
																								     	}
																									 });
																	}
																});
		
		PageAssembler.attachHandler("zipExport", Event.ONCLICK, new EventCallback() {
																	@Override
																	public void onEvent(Event event) {
																		AlfrescoPacket zipPack = AlfrescoPacket.makePacket();
																		zipPack.addKeyValue("toZip", "[{\"id\":\"" + AlfrescoURL.ALFRESCO_STORE_TYPE + "://" + AlfrescoURL.ALFRESCO_STORE_ID + "/" + thumbIDs.get(editIDs.lastElement()) + "\"}]");
																		AlfrescoApi.exportZipPackage("test.zip",
																									 zipPack,
																									 new AlfrescoCallback<AlfrescoPacket>() {
																								     	@Override
																								     	public void onFailure(Throwable caught) {
																								     		Window.alert("foobar'ed");
																								     	}
																								     	
																								     	@Override
																								     	public void onSuccess(AlfrescoPacket alfrescoPacket) {
																								     		Window.alert(alfrescoPacket.toJSONString());
																								     	}
																									 });
																	}
																});

		
		PageAssembler.attachHandler("r-editDelete", 
									Event.ONCLICK, 
									new EventCallback() {
										@Override
										public void onEvent(Event event) {
											if (editIDs.size()==1) {
												if (Window.confirm("Are you sure you wish to delete the selected item?"))
													deleteObjects();
											} else if (editIDs.size()>=2) {
												if (Window.confirm("Are you sure you wish to delete " + editIDs.size() + " items?"))
													deleteObjects();
											}
										}
									});
		
		PageAssembler.attachHandler("r-editSelectAll", 
									Event.ONCLICK, 
									new EventCallback() {
										@Override
										public void onEvent(Event event) {
											selectAllObjects();
										}
									});
		
		PageAssembler.attachHandler("r-editSelectNone", 
									Event.ONCLICK, 
									new EventCallback() {
										@Override
										public void onEvent(Event event) {
											selectionClear();
										}
									});
		
		PageAssembler.attachHandler("r-harvestFLR", 
									Event.ONCLICK, 
									new EventCallback() {
										@Override
										public void onEvent(Event event) {
											launchFlrHarvest();
											//launchFlrPost("This is a test of the Eduworks broadcast system.");
										}
									});

		PageAssembler.attachHandler("r-editAddFile", Event.ONCLICK, new EventCallback() {
																		@Override
																		public void onEvent(Event event) {
																			Vector<String> iDs = PageAssembler.getInstance().inject("r-previewArea", 
																																    "x", 
																																    new HTML(HtmlTemplates.INSTANCE.getEditPanelWidget().getText()),
																																    true);
																			final String idPrefix = iDs.firstElement().substring(0, iDs.firstElement().indexOf("-"));
																			buildEmptyUploadTile(idPrefix);
																			thumbIDs.put(idPrefix + "-object", null);
																			doFileCancel();
																		}
																	});
		
		PageAssembler.attachHandler("r-editAddFile-reset", Event.ONCLICK, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				doFileReset();
			}
		});
		PageAssembler.attachHandler("r-editAddFile-cancel", Event.ONCLICK, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				doFileReset();
				doFileCancel();
			}
		});

		PageAssembler.attachHandler("r-editAddLink-reset", Event.ONCLICK, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				doLinkReset();
			}
		});
		PageAssembler.attachHandler("r-editAddLink-cancel", Event.ONCLICK, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				doLinkReset();
				doLinkCancel();
			}
		});

		PageAssembler.attachHandler("r-editAddLink", Event.ONCLICK, new EventCallback() {
																		@Override
																		public void onEvent(Event event) {
																			if (((TextBox)PageAssembler.elementToWidget("editTitleLinkField", PageAssembler.TEXT)).getText()!="") {
																				String rawFilename = ((TextBox)PageAssembler.elementToWidget("editTitleLinkField", PageAssembler.TEXT)).getText();
																				String filename = "";
																				for (int filenameIndex=0;filenameIndex<rawFilename.length();filenameIndex++)
																					if ((rawFilename.codePointAt(filenameIndex)>=48&&rawFilename.codePointAt(filenameIndex)<=57)||
																						(rawFilename.codePointAt(filenameIndex)>=65&&rawFilename.codePointAt(filenameIndex)<=90)||
																						(rawFilename.codePointAt(filenameIndex)>=97&&rawFilename.codePointAt(filenameIndex)<=122))
																						filename += rawFilename.charAt(filenameIndex);
																				String urlBody = ((TextBox)PageAssembler.elementToWidget("editLinkField", PageAssembler.TEXT)).getText();
																				if (urlBody.indexOf("://")==-1)
																					urlBody = "http://" + urlBody;
																				AlfrescoApi.createObjectNode(filename + ".rlk", 
																											 Base64.encode(urlBody), 
																											 "russel/link", 
																											 "User Homes/" + AlfrescoApi.username, 
																											 AlfrescoApi.DOCUMENT_OBJECT, 
																											 new AlfrescoCallback<AlfrescoPacket>() {
																												@Override
																												public void onSuccess(final AlfrescoPacket alfrescoAtom) {
																													String nodeRef = alfrescoAtom.getPropertyValue("@propertyDefinitionId", "cmis:objectId");
																													AlfrescoApi.addAspectToNode(nodeRef.substring(nodeRef.lastIndexOf("/")+1), 
																																				new String[]{"russel:metaTest"}, 
																																				new AlfrescoCallback<AlfrescoPacket>() {
																																					@Override 
																																					public void onSuccess(AlfrescoPacket alfrescoAspect) {
																																						Vector<String> iDs = PageAssembler.getInstance().inject("r-previewArea", 
																																																			    "x", 
																																																			    new HTML(HtmlTemplates.INSTANCE.getEditPanelWidget().getText()),
																																																			    true);
																																						final String idPrefix = iDs.firstElement().substring(0, iDs.firstElement().indexOf("-"));
																																						fillTemplateDetails(alfrescoAtom, idPrefix);
																																						doLinkCancel();
																																					}
																																					
																																					@Override
																																					public void onFailure(Throwable caught) {
																																						
																																					}
																																				});
																												}
																												
																												@Override
																												public void onFailure(Throwable caught) {
																													Window.alert("Can't have a duplicate name please try again");
																												}
																											});
																			} else 
																				Window.alert("Can't have a blank title");
																		}
																	}); 
		
		PageAssembler.attachHandler("r-editSave", Event.ONCLICK, new EventCallback() {
																	@Override
																	public void onEvent(Event event) {
																			new Timer() {
																				@Override
																				public void run() {
																					if (editIDs.size()>0)
																						meta.saveMetadata(thumbIDs.get(editIDs.firstElement()), new AlfrescoCallback<AlfrescoPacket>() {
																							@Override
																							public void onSuccess(AlfrescoPacket alfrescoPacket) {
																								refreshInformation();
																							}
																							
																							@Override
																							public void onFailure(Throwable caught) {
																								Window.alert("Failed to save metadata " + caught);
																							}
																						});
																				}
																			}.schedule(100); 
																		}
																 });
		
		while (pendingEdits.size()>0) {
			Vector<String> iDs = PageAssembler.getInstance().inject("r-previewArea", 
																    "x", 
																    new HTML(HtmlTemplates.INSTANCE.getEditPanelWidget().getText()),
																	false);
			final String idPrefix = iDs.firstElement().substring(0, iDs.firstElement().indexOf("-"));
			fillTemplateDetails(pendingEdits.remove(0), idPrefix);	
		}
		refreshInformation();
	}
	
	private void addLinkHandlers(final String idPrefix) {
		PageAssembler.attachHandler(idPrefix + "-object", 
									Event.ONCLICK, 
									new EventCallback() {
										@Override
										public void onEvent(Event event) {
											toggleSelection(idPrefix + "-object");
										}
									});
		
		PageAssembler.attachHandler(idPrefix + "-objectDescription", 
									Event.ONCLICK, 
									new EventCallback() {
										@Override
										public void onEvent(Event event) {
											toggleSelection(idPrefix + "-object");
										}
									});
	}  
	private void uploadLinkTile(TextBox textBox) {
		Window.alert("Caught upload link submission"+textBox.getValue());
	}

	private void buildEmptyUploadTile(final String idPrefix) {
		final FormPanel formPanel = (FormPanel)PageAssembler.elementToWidget("addFileForm", PageAssembler.FORM);
		final FileUpload fileUpload = (FileUpload)PageAssembler.elementToWidget("addFileData", PageAssembler.FILE);
		final Hidden hiddenDestination = (Hidden)PageAssembler.elementToWidget("addFileDestination", PageAssembler.HIDDEN);
		final Hidden hiddenOverwrite = (Hidden)PageAssembler.elementToWidget("addFileOverwrite", PageAssembler.HIDDEN);
		formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		formPanel.setAction(AlfrescoURL.getAlfrescoUploadURL());
		hiddenDestination.setValue(AlfrescoApi.currentDirectoryId);
		hiddenOverwrite.setValue("false");
		pendingFileUploads++;
		formPanel.addSubmitCompleteHandler(new SubmitCompleteHandler() {
										@Override
										public void onSubmitComplete(SubmitCompleteEvent event) {
											RootPanel.get(idPrefix + "-objectDescription").getElement().setInnerText("");
											fillTemplateDetails(AlfrescoPacket.wrap(CommunicationHub.parseJSON(CommunicationHub.unwrapJSONString(event.getResults()))), idPrefix);
											pendingFileUploads--;
											DOM.getElementById(idPrefix + "-objectDetailButton").removeAttribute("hidden");
											String filename = fileUpload.getFilename();
											if (filename.substring(filename.lastIndexOf(".")+1).equalsIgnoreCase("rpf")) {
//												Need to add server-side update to the PFM node id
											}

											refreshInformation();
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
																		toggleSelection(idPrefix + "-object");
																	}
																});
									if (fn=="") {
										Window.alert("Can't upload something that isn't there");
										fileUpload.setName("data");
										DOM.getElementById(idPrefix + "-object").removeFromParent();
										pendingFileUploads--;
										event.cancel();
									} else {
										DOM.getElementById(idPrefix + "-objectDetailButton").setAttribute("hidden", "hidden");

										if (justFileName.indexOf(".")!=-1&&justFileName.substring(justFileName.lastIndexOf(".")+1).toLowerCase().equals("zip")) { 
											if (!Browser.isIE()) {
												Window.alert("Warning: "+justFileName+" will not be broken up into assets.  To disaggregate a ZIP file, please use the Drag and Drop interface.");
//												We need to write a blob class for non drag-and-drop operation.  Documented as a feature in RedMine. 
//												OR we can use the new Alfresco beans to support disaggregation on the server side.

											}
											else Window.alert("Your browser does not support automatic disaggregation for "+fn+" package.");
										}				
										RootPanel.get(idPrefix + "-objectDescription").add(new Image("images/orbit/loading.gif"));
										DOM.getElementById(idPrefix + "-objectTitle").setInnerText(justFileName);
										DOM.getElementById(idPrefix + "-objectDescription").setAttribute("style", "text-align:center");
									}
								}
							});
		formPanel.submit();
		refreshInformation();
	}
	
	private final native String doColors(String s) /*-{
		return $wnd.getSecurityColor(s);
	}-*/;

	private final native String doFileReset() /*-{
		return $wnd.resetAddFileModal();
	}-*/;
	
	private final native String doFileCancel() /*-{
		return $wnd.$('#addFileModal').trigger('reveal:close');
	}-*/;

	private final native String doLinkReset() /*-{
		return $wnd.resetAddLinkModal();
	}-*/;

	private final native String doLinkCancel() /*-{
		return $wnd.$('#addLinkModal').trigger('reveal:close');
	}-*/;

	private void refreshInformation() {
		if (editIDs.size()>0) {
			DOM.getElementById("r-metadataToolbar").removeClassName("hide");
			CommunicationHub.sendHTTP(CommunicationHub.GET, 
									  AlfrescoURL.getAlfrescoNodeURL(thumbIDs.get(editIDs.lastElement())), 
									  null,
									  false,
									  new AlfrescoCallback<AlfrescoPacket>() {
										@Override
										public void onSuccess(final AlfrescoPacket nodeAP) {
											CommunicationHub.sendHTTP(CommunicationHub.GET,
																	  AlfrescoURL.getAlfrescoTagsURL(thumbIDs.get(editIDs.lastElement())),
																	  null,
																	  false, 
																	  new AlfrescoCallback<AlfrescoPacket>() {
																		@Override
																		public void onSuccess(AlfrescoPacket tagsAP) {
																			meta.addMetaDataFields("@propertyDefinitionId", nodeAP, tagsAP);
																			//refreshThumb(thumbIDs.get(editIDs.firstElement()));
																		}
																		
																		@Override
																		public void onFailure(Throwable caught) {
																			//Window.alert("Fooing failed trying to refresh node metadata" + caught);
																		}
																	});
										}
										
										@Override
										public void onFailure(Throwable caught) {
											//Window.alert("Fooing failed trying to refresh tags metadata -" + caught);
										}
									});

		} else
			DOM.getElementById("r-metadataToolbar").addClassName("hide");
		
		if (thumbIDs.size()<=0)
			((Label)PageAssembler.elementToWidget("editCover", PageAssembler.LABEL)).removeStyleName("hide");
		else
			((Label)PageAssembler.elementToWidget("editCover", PageAssembler.LABEL)).addStyleName("hide");
		
		if (countUploads()==0)
			((Label)PageAssembler.elementToWidget("r-editPendingUploads", PageAssembler.LABEL)).setText(NO_PENDING_UPLOADS);
		else
			((Label)PageAssembler.elementToWidget("r-editPendingUploads", PageAssembler.LABEL)).setText(countUploads() + PENDING_UPLOADS);
		
		AlfrescoPacket ap = AlfrescoPacket.makePacket();
		meta.addMetaDataFields("@propertyDefinitionId", ap, ap);
		removeUnsavedEffects();
	}
	
	private int countUploads() {
		int acc = 0;
		if (ddh!=null&&ddh.readQueue!=null)
			acc += ddh.readQueue.size();
		if (pendingZipUploads!=null)
			acc += pendingZipUploads.size();
		acc += pendingFileUploads;
		return acc;
	}
	
	private void removeUnsavedEffects() {
		((Label)PageAssembler.elementToWidget("r-save-alert", PageAssembler.LABEL)).addStyleName("hide");
		((Anchor)PageAssembler.elementToWidget("r-editSave", PageAssembler.A)).removeStyleName("blue");
		((Anchor)PageAssembler.elementToWidget("r-editSave", PageAssembler.A)).addStyleName("white");
	}
	
	private void selectAllObjects() {
		editIDs.clear();	
		for (Iterator<String> e = thumbIDs.keySet().iterator();e.hasNext();)
			editIDs.add(e.next());
		for (int x=0;x<editIDs.size();x++)
			((Label)PageAssembler.elementToWidget(editIDs.get(x), PageAssembler.LABEL)).addStyleName("active");
		refreshInformation();
	}
	
	private void selectionClear() {
		for (int x=0;x<editIDs.size();x++)
			((Label)PageAssembler.elementToWidget(editIDs.get(x), PageAssembler.LABEL)).removeStyleName("active");
		editIDs.clear();
		refreshInformation();
	}

	private void deleteObjects() {
		while (editIDs.size()>0) {
			String tId = editIDs.remove(0);
			String nId = thumbIDs.remove(tId);
			if (nId!=null)
				deleteObject(nId, tId);
			else {
				Document.get().getElementById(tId).removeFromParent();
				refreshInformation();
			}
		}
	}
	
	private void deleteObject(final String nodeId, final String thumbId) {
		if (nodeId!=null)
			AlfrescoApi.deleteDocument(nodeId, 
									   new AlfrescoCallback<AlfrescoPacket>() {
											@Override
											public void onSuccess(AlfrescoPacket result) {
												DOM.getElementById(thumbId).removeFromParent();
												refreshInformation();
											}
											
											@Override
											public void onFailure(Throwable caught) {
												Window.alert("Fooing Delete Object");
												DOM.getElementById(thumbId).removeFromParent();
												refreshInformation();
											}
										});
		else
			DOM.getElementById(thumbId).removeFromParent();
	}
	
//	private void launchFlrPost(String data) {
//		FLRApi.putFLRdata(data, new FLRCallback<FLRPacket>() {
//			@Override
//			public void onSuccess(FLRPacket result) {
//				Window.alert("putFLRdata reports success      " + result.toJSONString());
//			}
//			
//			@Override
//			public void onFailure(Throwable caught) {
//				Window.alert("putFLRdata reports failure"+caught.getMessage());
//			}			
//		});
//	}
	
	private void launchFlrHarvest() {
		FLRApi.getFLRdata(new FLRCallback<FLRPacket>() {
			@Override
			public void onSuccess(FLRPacket result) {
				Window.alert("getFLRdata reports success      " + result.toJSONString());
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("getFLRdata reports failure"+caught.getMessage());
			}			
		});
	}
	

	
	private void doPendingUploads() {
		if (pendingZipUploads.size()>0) {
			AlfrescoPacket zipEntry = pendingZipUploads.remove(0);
			Zip.inflateEntry(zipEntry, true, new AlfrescoCallback<AlfrescoPacket>() {
												@Override public void onFailure(Throwable caught) {}
												
												@Override
												public void onSuccess(AlfrescoPacket alfrescoPacket) {
													String filename = alfrescoPacket.getValue("zipEntryFilename").toString();
													if (filename.lastIndexOf(".")!=-1) {
														if (filename.lastIndexOf("/")!=-1) filename = filename.substring(filename.lastIndexOf("/")+1);
														if (AssetExtractor.checkAsset(filename, ((Blob)alfrescoPacket.getValue("zipEntryData").cast()))) {
															if (filename.substring(filename.lastIndexOf(".")+1).equalsIgnoreCase("zip")&&Window.confirm("Do you wish to disaggregate the zip " + alfrescoPacket.getValue("zipEntryFilename") + " package?")) {
																Zip.grabEntries(((Blob)alfrescoPacket.getValue("zipEntryData").cast()), new AlfrescoCallback<AlfrescoPacket>() {
																	@Override public void onFailure(Throwable caught) {}
											
																	@SuppressWarnings("unchecked")
																	@Override
																	public void onSuccess(AlfrescoPacket alfrescoPacket) {
																		if (alfrescoPacket.hasKey("zipEntries")) {
																			JsArray<AlfrescoPacket> zipEntries = ((JsArray<AlfrescoPacket>)alfrescoPacket.getValue("zipEntries"));
																			for (int x=0;x<zipEntries.length();x++)
																				pendingZipUploads.add(zipEntries.get(x));
																			doPendingUploads();
																		}
																	}
																});
															}
															final Vector<String> iDs = PageAssembler.getInstance().inject("r-previewArea", 
			    																									"x", 
			    																									new HTML(HtmlTemplates.INSTANCE.getEditPanelWidget().getText()),
			    																									false);
															final String idNumPrefix = iDs.get(0).substring(0, iDs.get(0).indexOf("-"));
															RootPanel.get(idNumPrefix + "-objectDescription").add(new Image("images/orbit/loading.gif"));
															DOM.getElementById(idNumPrefix + "-objectTitle").setInnerText(filename);
															DOM.getElementById(idNumPrefix + "-objectDetailButton").setAttribute("hidden", "hidden");
															DOM.getElementById(idNumPrefix + "-objectDescription").setAttribute("style", "text-align:center");
															CommunicationHub.sendForm(AlfrescoURL.getAlfrescoUploadURL(),
																					  filename,
																					  AlfrescoApi.currentDirectoryId, 
																					  ((Blob)alfrescoPacket.getValue("zipEntryData").cast()),
																					  "russel:metaTest,cm:versionable",
																					  new AlfrescoCallback<AlfrescoPacket>() {
																							@Override
																							public void onFailure(Throwable caught) {
																								Window.alert("Failed uploading pending packets " + caught);
																							}
															
																							@Override
																							public void onSuccess(AlfrescoPacket alfrescoPacket) {
																								RootPanel.get(idNumPrefix + "-objectDescription").clear();
																								DOM.getElementById(idNumPrefix + "-objectDetailButton").removeAttribute("hidden");
																								DOM.getElementById(idNumPrefix + "-objectDescription").setAttribute("style", "");
																								fillTemplateDetails(alfrescoPacket, idNumPrefix);
																								String filename = alfrescoPacket.getFilename();
																								if (filename.substring(filename.lastIndexOf(".")+1).equalsIgnoreCase("rpf")) {
																									ProjectFileModel.updatePfmNodeId(alfrescoPacket);
																								}
																								doPendingUploads();
																							}
																						});
															
														} else 
															doPendingUploads();
													} else
														doPendingUploads();
												}
											});
			refreshInformation();
		}
	}
	
	private void hookDragDrop(DropPanel w) {
		ddh = new DragDropHandler(w) {
					@Override
					public void run(final File file)
					{
						String filename = file.getName();
						if (filename.lastIndexOf(".")!=-1&&filename.substring(filename.lastIndexOf(".")+1).equalsIgnoreCase("zip")) { 
							if (Window.confirm("Do you wish to disaggregate the zip " + filename + " package?")) {
								Zip.grabEntries(file, new AlfrescoCallback<AlfrescoPacket>() {
									@Override public void onFailure(Throwable caught) {}
			
									@SuppressWarnings("unchecked")
									@Override
									public void onSuccess(AlfrescoPacket alfrescoPacket) {
										if (alfrescoPacket.hasKey("zipEntries")) {
											JsArray<AlfrescoPacket> zipEntries = ((JsArray<AlfrescoPacket>)alfrescoPacket.getValue("zipEntries"));
											for (int x=0;x<zipEntries.length();x++)
												pendingZipUploads.add(zipEntries.get(x));
											doPendingUploads();
										}
									}
								});
							}
						}

						final Vector<String> iDs = PageAssembler.getInstance().inject("r-previewArea", 
																			    	  "x", 
																			    	  new HTML(HtmlTemplates.INSTANCE.getEditPanelWidget().getText()),
																			    	  false);
						final String idNumPrefix = iDs.get(0).substring(0, iDs.get(0).indexOf("-"));
						RootPanel.get(idNumPrefix + "-objectDescription").add(new Image("images/orbit/loading.gif"));
						DOM.getElementById(idNumPrefix  + "-objectTitle").setInnerText(file.getName());
						DOM.getElementById(idNumPrefix + "-objectDescription").setAttribute("style", "text-align:center");
						DOM.getElementById(idNumPrefix + "-objectDetailButton").setAttribute("hidden", "hidden");
						CommunicationHub.sendForm(AlfrescoURL.getAlfrescoUploadURL(), 
												  file.getName(), 
												  AlfrescoApi.currentDirectoryId, 
												  file, 
												  "russel:metaTest,cm:versionable",
												  new AlfrescoCallback<AlfrescoPacket>(){
													@Override
													public void onFailure(Throwable caught) {
														Window.alert("Fooing Drag and Drop failed - " + caught.getMessage());
													}
								
													@Override
													public void onSuccess(AlfrescoPacket result) {
														RootPanel.get(idNumPrefix + "-objectDescription").clear();
														DOM.getElementById(idNumPrefix + "-objectDetailButton").removeAttribute("hidden");
														DOM.getElementById(idNumPrefix + "-objectDescription").setAttribute("style", "");
														fillTemplateDetails(result, idNumPrefix);
														String filename = result.getFilename();
														if (filename.substring(filename.lastIndexOf(".")+1).equalsIgnoreCase("rpf")) {
															ProjectFileModel.updatePfmNodeId(result);
														}
														readNext();
													}
												});
						
						thumbIDs.put(idNumPrefix + "-object", "");
						refreshInformation();
					}
				};
	}

	private void toggleSelection(String id) {
		if (editIDs.contains(id)) {
			editIDs.remove(id);
			((Label)PageAssembler.elementToWidget(id, PageAssembler.LABEL)).removeStyleName("active");
		} else {
			editIDs.add(id);
			((Label)PageAssembler.elementToWidget(id, PageAssembler.LABEL)).addStyleName("active");
		}
		refreshInformation();
	}
	
	private void fillTemplateDetails(final AlfrescoPacket jsonResult, final String idNumPrefix)
	{
		final String nodeID = jsonResult.getNodeId();
		thumbIDs.put(idNumPrefix + "-object", nodeID);
		((Label)PageAssembler.elementToWidget(idNumPrefix + "-objectTitle", PageAssembler.LABEL)).setText(jsonResult.getFilename());
		PageAssembler.attachHandler(idNumPrefix + "-objectDetail", Event.ONCLICK, new EventCallback() {
																  	@Override
																  	public void onEvent(Event event) {
																  		Russel.view.loadScreen(new DetailScreen(jsonResult), true);
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
											toggleSelection(idNumPrefix + "-object");
										}
									});
		refreshInformation();
	}
}
