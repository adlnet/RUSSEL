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

package com.eduworks.russel.ui.client.epss;

import java.util.Vector;

import org.vectomatic.arrays.ArrayBuffer;
import org.vectomatic.file.Blob;

import com.eduworks.gwt.client.util.Zip;
import com.eduworks.gwt.russel.ui.client.net.AlfrescoApi;
import com.eduworks.gwt.russel.ui.client.net.AlfrescoCallback;
import com.eduworks.gwt.russel.ui.client.net.AlfrescoNullCallback;
import com.eduworks.gwt.russel.ui.client.net.AlfrescoPacket;
import com.eduworks.russel.ui.client.pagebuilder.PageAssembler;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.RootPanel;

public class EPSSPackBuilder {
	private static final String HTML_MIME = "text/html";
	private static final String XML_MIME = "application/xml";
	private static final String DTD_MIME = "application/xml-dtd";
	private Vector<String> mediaList = new Vector<String>();
	private ProjectFileModel pfm;
	private Vector<String> filenameAndPath = new Vector<String>();
	private JavaScriptObject zipWriter;
	private Blob scormZip;
	
	public EPSSPackBuilder (ProjectFileModel pfm) {
		this.pfm = pfm;
		buildFileList();
	}
	
	private void buildFileList() {
		filenameAndPath.add("adlcp_v1p3.xsd");
		filenameAndPath.add("adlnav_v1p3.xsd");
		filenameAndPath.add("adlseq_v1p3.xsd");
		filenameAndPath.add("/common/anyElement.xsd");
		filenameAndPath.add("/common/datatypes.xsd");
		filenameAndPath.add("/common/elementNames.xsd");
		filenameAndPath.add("/common/elementTypes.xsd");
		filenameAndPath.add("/common/rootElement.xsd");
		filenameAndPath.add("/common/vocabTypes.xsd");
		filenameAndPath.add("/common/vocabValues.xsd");
		filenameAndPath.add("datatypes.dtd");
		filenameAndPath.add("/extend/custom.xml");
		filenameAndPath.add("/extend/strict.xml");
		filenameAndPath.add("imsmanifest.xml");
		filenameAndPath.add("imsss_v1p0.xsd");
		filenameAndPath.add("imsss_v1p0auxresource.xsd");
		filenameAndPath.add("imsss_v1p0control.xsd");
		filenameAndPath.add("imsss_v1p0delivery.xsd");
		filenameAndPath.add("imsss_v1p0limit.xsd");
		filenameAndPath.add("imsss_v1p0objective.xsd");
		filenameAndPath.add("imsss_v1p0random.xsd");
		filenameAndPath.add("imsss_v1p0rollup.xsd");
		filenameAndPath.add("imsss_v1p0seqrule.xsd");
		filenameAndPath.add("imsss_v1p0util.xsd");
		filenameAndPath.add("initPage.html");
		filenameAndPath.add("lom.xsd");
		filenameAndPath.add("lomCustom.xsd");
		filenameAndPath.add("lomLoose.xsd");
		filenameAndPath.add("lomStrict.xsd");
		filenameAndPath.add("/unique/loose.xsd");
		filenameAndPath.add("/unique/strict.xsd");
		filenameAndPath.add("/vocab/adlmd_vocabv1p0.xsd");
		filenameAndPath.add("/vocab/custom.xsd");
		filenameAndPath.add("/vocab/loose.xsd");
		filenameAndPath.add("/vocab/strict.xsd");
		filenameAndPath.add("xml.xsd");
		filenameAndPath.add("XMLScheme.dtd");
	}
	
	public void buildPack() {
		Zip.getZipFileWriter(new AlfrescoCallback<AlfrescoPacket>() {
			@Override
			public void onSuccess(AlfrescoPacket alfrescoPacket) {
				zipWriter = alfrescoPacket.getValue("zipWriter");
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to create zip writer " + caught);
				PageAssembler.closePopup("exportProjectModal");
			}
		});
	}
	
	private void putProjectFile () {
		buildPack();
		Zip.addFileToZipBlob(zipWriter,
							 pfm.projectTitle.replaceAll(" ", "_") + ".zip", 
							 scormZip, 
							 new AlfrescoCallback<AlfrescoPacket>() {
								@Override
								public void onFailure(Throwable caught) {
									Window.alert("failed putting SCORM zip file in zip " + caught);
								}
								
								@Override
								public void onSuccess(AlfrescoPacket alfrescoPacket) {
									Zip.addFileToZipBlob(alfrescoPacket.getValue("zipWriter"),
														 pfm.projectTitle.replaceAll(" ", "_") + ".rpf", 
														 pfm.makeJSONBlob(), 
														 new AlfrescoCallback<AlfrescoPacket>() {
															@Override
															public void onSuccess(AlfrescoPacket alfrescoPacket) {
																Zip.getZipBlobLocalURL(zipWriter, 
																					   new AlfrescoCallback<AlfrescoPacket>() {
																							@Override
																							public void onSuccess(AlfrescoPacket alfrescoPacket) {
																								//Window.alert(alfrescoPacket.getValue("zipURL").toString());
																								RootPanel.get("epssDownloadArea").clear();
																								Anchor a = new Anchor("Download readied package", alfrescoPacket.getValue("zipURL").toString());
																								RootPanel.get("epssDownloadArea").add(a);
																								a.getElement().setId("downloadPackage");
																								a.getElement().setAttribute("download", pfm.projectTitle.replaceAll(" ", "_") + ".zip");
																								PageAssembler.attachHandler("downloadPackage", Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {
																																			  	@Override
																																			  	public void onEvent(Event event) {
																																			  		PageAssembler.closePopup("exportProjectModal");	
																																			  	}
																																			  });
//																								Window.open(alfrescoPacket.getValue("zipURL").toString(),
//																											"_blank",
//																											"");
																							}
																							
																							@Override
																							public void onFailure(Throwable caught) {
																								PageAssembler.closePopup("exportProjectModal");
																							}
																						});
															}
										
															@Override
															public void onFailure(Throwable caught) {
																Window.alert("Failed to add project file " + caught);
															}
														});
								}
							});
		
	}
	
	private void addMediaFile () {
		if (mediaList.size()==0)
			Zip.getZipBlob(zipWriter, new AlfrescoCallback<AlfrescoPacket>() {
													@Override
													public void onSuccess(AlfrescoPacket alfrescoPacket) {
														scormZip = alfrescoPacket.getValue("zipBlob").cast(); 
														putProjectFile();
													}
													
													@Override
													public void onFailure(Throwable caught) {
														Window.alert("Failed to get media zip blob " + caught);
													}
												});
		else {
			String nodeIdFilename = mediaList.remove(0);
			final String nodeId = nodeIdFilename.substring(0, nodeIdFilename.indexOf(","));
			final String filename = nodeIdFilename.substring(nodeIdFilename.indexOf(",")+1);
			AlfrescoApi.getObjectStream(nodeId, 
										filename, 
										new AlfrescoCallback<AlfrescoPacket>() {
											@Override
											public void onSuccess(AlfrescoPacket alfrescoPacket) {
												Zip.addFileToZipBlob(zipWriter, 
																	 "/media/" + mediaList.size() + "-" + filename, 
																	 buildBlob(alfrescoPacket.getMimeType(), alfrescoPacket.getContents()), 
																	 new AlfrescoCallback<AlfrescoPacket>() {
																		@Override
																		public void onFailure(Throwable caught) {
																			Window.alert("Fooing zipping media file " + filename + "  " + caught);
																			addMediaFile();
																		}
																		
																		@Override
																		public void onSuccess(AlfrescoPacket alfrescoPacket) {
																			zipWriter = alfrescoPacket.getValue("zipWriter");
																			addMediaFile();
																		}
																 	 });
											}
											
											@Override
											public void onFailure(Throwable caught) {
												Window.alert("Fooing getting media files " + caught);
												addMediaFile();
											}
										});
		}
	}
	
	private void BuildMediaList() {
		JsArrayString keys = pfm.projectSectionAssets.getTopKeys();
		for (int x=0;x<keys.length();x++) {
			JsArray<AlfrescoPacket> assets = pfm.projectSectionAssets.getValue(keys.get(x)).cast();
			for (int y=0;y<assets.length();y++)
				mediaList.add(assets.get(y).getNodeId() + "," + assets.get(y).getFilename());
		}
	}
	
	public void addSCORMFile() {
		if (filenameAndPath.size()==0) {
			BuildMediaList();
			addMediaFile();
		} else { 
			String filename = filenameAndPath.remove(0);
			Blob filedata = null;
			
			if (filename.equals("adlcp_v1p3.xsd"))
				filedata = buildBlob(XML_MIME, SCORMTemplates.INSTANCE.getAdlcp_v1p3().getText());
			else if (filename.equals("adlnav_v1p3.xsd"))
				filedata = buildBlob(XML_MIME, SCORMTemplates.INSTANCE.getAdlnav_v1p3().getText());
			else if (filename.equals("adlseq_v1p3.xsd"))
				filedata = buildBlob(XML_MIME, SCORMTemplates.INSTANCE.getAdlseq_v1p3().getText());
			else if (filename.equals("/common/anyElement.xsd"))
				filedata = buildBlob(XML_MIME, SCORMTemplates.INSTANCE.getCommonAnyElement().getText());
			else if (filename.equals("/common/datatypes.xsd"))
				filedata = buildBlob(XML_MIME, SCORMTemplates.INSTANCE.getCommonDataTypes().getText());
			else if (filename.equals("/common/elementNames.xsd"))
				filedata = buildBlob(XML_MIME, SCORMTemplates.INSTANCE.getCommonElementNames().getText());
			else if (filename.equals("/common/elementTypes.xsd"))
				filedata = buildBlob(XML_MIME, SCORMTemplates.INSTANCE.getCommonElementTypes().getText());
			else if (filename.equals("/common/rootElement.xsd"))
				filedata = buildBlob(XML_MIME, SCORMTemplates.INSTANCE.getCommonRootElement().getText());
			else if (filename.equals("/common/vocabTypes.xsd"))
				filedata = buildBlob(XML_MIME, SCORMTemplates.INSTANCE.getCommonVocabTypes().getText());
			else if (filename.equals("/common/vocabValues.xsd"))
				filedata = buildBlob(XML_MIME, SCORMTemplates.INSTANCE.getCommonVocabValues().getText());
			else if (filename.equals("datatypes.dtd"))
				filedata = buildBlob(DTD_MIME, SCORMTemplates.INSTANCE.getDatatypes().getText());
			else if (filename.equals("/extend/custom.xml"))
				filedata = buildBlob(XML_MIME, SCORMTemplates.INSTANCE.getExtendCustom().getText());
			else if (filename.equals("/extend/strict.xml"))
				filedata = buildBlob(XML_MIME, SCORMTemplates.INSTANCE.getExtendStrict().getText());
			else if (filename.equals("imsmanifest.xml"))
				filedata = buildBlob(HTML_MIME, SCORMTemplates.INSTANCE.getImsmanifest().getText());
			else if (filename.equals("imsss_v1p0.xsd"))
				filedata = buildBlob(XML_MIME, SCORMTemplates.INSTANCE.getImsss_v1p0().getText());
			else if (filename.equals("imsss_v1p0auxresource.xsd"))
				filedata = buildBlob(XML_MIME, SCORMTemplates.INSTANCE.getImsss_v1p0auxresource().getText());
			else if (filename.equals("imsss_v1p0control.xsd"))
				filedata = buildBlob(XML_MIME, SCORMTemplates.INSTANCE.getImsss_v1p0control().getText());
			else if (filename.equals("imsss_v1p0delivery.xsd"))
				filedata = buildBlob(XML_MIME, SCORMTemplates.INSTANCE.getImsss_v1p0delivery().getText());
			else if (filename.equals("imsss_v1p0limit.xsd"))
				filedata = buildBlob(XML_MIME, SCORMTemplates.INSTANCE.getImsss_v1p0limit().getText());
			else if (filename.equals("imsss_v1p0objective.xsd"))
				filedata = buildBlob(XML_MIME, SCORMTemplates.INSTANCE.getImsss_v1p0objective().getText());
			else if (filename.equals("imsss_v1p0random.xsd"))
				filedata = buildBlob(XML_MIME, SCORMTemplates.INSTANCE.getImsss_v1p0random().getText());
			else if (filename.equals("imsss_v1p0rollup.xsd"))
				filedata = buildBlob(XML_MIME, SCORMTemplates.INSTANCE.getImsss_v1p0rollup().getText());
			else if (filename.equals("imsss_v1p0seqrule.xsd"))
				filedata = buildBlob(XML_MIME, SCORMTemplates.INSTANCE.getImsss_v1p0seqrule().getText());
			else if (filename.equals("imsss_v1p0util.xsd"))
				filedata = buildBlob(XML_MIME, SCORMTemplates.INSTANCE.getImsss_v1p0util().getText());
			else if (filename.equals("initPage.html"))
				filedata = buildBlob(HTML_MIME, SCORMTemplates.INSTANCE.getInitPage().getText());
			else if (filename.equals("lom.xsd"))
				filedata = buildBlob(XML_MIME, SCORMTemplates.INSTANCE.getLom().getText());
			else if (filename.equals("lomCustom.xsd"))
				filedata = buildBlob(XML_MIME, SCORMTemplates.INSTANCE.getLomCustom().getText());
			else if (filename.equals("lomLoose.xsd"))
				filedata = buildBlob(XML_MIME, SCORMTemplates.INSTANCE.getLomLoose().getText());
			else if (filename.equals("lomStrict.xsd"))
				filedata = buildBlob(XML_MIME, SCORMTemplates.INSTANCE.getLomStrict().getText());
			else if (filename.equals("/unique/loose.xsd"))
				filedata = buildBlob(XML_MIME, SCORMTemplates.INSTANCE.getUniqueLoose().getText());
			else if (filename.equals("/unique/strict.xsd"))
				filedata = buildBlob(XML_MIME, SCORMTemplates.INSTANCE.getUniqueStrict().getText());
			else if (filename.equals("/vocab/adlmd_vocabv1p0.xsd"))
				filedata = buildBlob(XML_MIME, SCORMTemplates.INSTANCE.getVocabAdlmd_Vocabv1p0().getText());
			else if (filename.equals("/vocab/custom.xsd"))
				filedata = buildBlob(XML_MIME, SCORMTemplates.INSTANCE.getVocabCustom().getText());
			else if (filename.equals("/vocab/loose.xsd"))
				filedata = buildBlob(XML_MIME, SCORMTemplates.INSTANCE.getVocabLoose().getText());
			else if (filename.equals("/vocab/strict.xsd"))
				filedata = buildBlob(XML_MIME, SCORMTemplates.INSTANCE.getVocabLoose().getText());
			else if (filename.equals("xml.xsd"))
				filedata = buildBlob(XML_MIME, SCORMTemplates.INSTANCE.getXml().getText());
			else if (filename.equals("XMLScheme.dtd"))
				filedata = buildBlob(DTD_MIME, SCORMTemplates.INSTANCE.getXMLSchema().getText());
			Zip.addFileToZipBlob(zipWriter, 
								 filename, 
								 filedata, 
								 new AlfrescoCallback<AlfrescoPacket>() {
									@Override
									public void onFailure(Throwable caught) {
										addSCORMFile();
									}
									
									@Override
									public void onSuccess(AlfrescoPacket alfrescoPacket) {
										zipWriter = alfrescoPacket.getValue("zipWriter");
										addSCORMFile();
									}
							 	 });
		}
	}
	
	private native Blob buildBlob(String typ, String contents) /*-{
		var bb = new (window.BlobBuilder || window.MozBlobBuilder || window.WebKitBlobBuilder || window.OBlobBuilder || window.msBlobBuilder);
		bb.append(contents);
		return bb.getBlob(typ);
	}-*/;
	
	private native Blob buildBlob(String typ, ArrayBuffer contents) /*-{
		var bb = new (window.BlobBuilder || window.MozBlobBuilder || window.WebKitBlobBuilder || window.OBlobBuilder || window.msBlobBuilder);
		bb.append(contents);
		return bb.getBlob(typ);
	}-*/;
}