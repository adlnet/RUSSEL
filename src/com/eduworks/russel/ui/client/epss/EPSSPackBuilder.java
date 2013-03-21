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

package com.eduworks.russel.ui.client.epss;

import java.util.Vector;

import org.vectomatic.file.Blob;

import com.eduworks.gwt.client.net.api.AlfrescoApi;
import com.eduworks.gwt.client.net.api.AlfrescoURL;
import com.eduworks.gwt.client.net.callback.AlfrescoCallback;
import com.eduworks.gwt.client.net.callback.EventCallback;
import com.eduworks.gwt.client.net.packet.AlfrescoPacket;
import com.eduworks.gwt.client.net.packet.StatusPacket;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.gwt.client.util.BlobUtils;
import com.eduworks.gwt.client.util.Zip;
import com.eduworks.russel.ui.client.handler.StatusWindowHandler;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.user.client.DOM;
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
	private String missingFiles;
	private Vector<String> filenameAndPath = new Vector<String>();
	private JavaScriptObject zipWriter;
	private Blob scormZip;
	
	public EPSSPackBuilder (ProjectFileModel pfm) {
		this.pfm = pfm;
		missingFiles = "";
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

	public AlfrescoPacket buildPackIE() {
		JsArray<AlfrescoPacket> storage = (JsArray<AlfrescoPacket>) AlfrescoPacket.createArray();
		
		BuildMediaList();
		
		for (int filenameIndex=0;filenameIndex<filenameAndPath.size();filenameIndex++) {
			AlfrescoPacket zipPack = AlfrescoPacket.makePacket();
			zipPack.addKeyValue("filename", filenameAndPath.get(filenameIndex));
			zipPack.addKeyValue("filecontent", filenameToContents(filenameAndPath.get(filenameIndex)));
			storage.set(filenameIndex, zipPack);
		}
		
		int mediaIndex = storage.length();
		for (int filenameIndex=0;filenameIndex<mediaList.size();filenameIndex++) {
			AlfrescoPacket zipPack = AlfrescoPacket.makePacket();
			String[] mediaPair = mediaList.get(filenameIndex).split(",");
			zipPack.addKeyValue("id", AlfrescoURL.ALFRESCO_STORE_TYPE + "://" + AlfrescoURL.ALFRESCO_STORE_ID + "/" + mediaPair[0]);
			zipPack.addKeyValue("location", "/media/");
			storage.set(mediaIndex, zipPack);
			mediaIndex++;
		}
		
		AlfrescoPacket projectPack = AlfrescoPacket.makePacket();
		projectPack.addKeyValue("projectName", pfm.projectTitle.replaceAll(" ", "_") + ".rpf");
		projectPack.addKeyValue("projectNodeId", AlfrescoURL.ALFRESCO_STORE_TYPE + "://" + AlfrescoURL.ALFRESCO_STORE_ID + "/" + pfm.projectNodeId);
		
		
		AlfrescoPacket outgoing = AlfrescoPacket.makePacket();
		outgoing.addKeyValue("mediaToZip", storage);
		outgoing.addKeyValue("projectToZip", projectPack);
		
		return outgoing;
	}
	
	private void missingFile(String filename) {
		// Not sure what we want to do here...
		missingFiles = missingFiles + "  - " + filename + "<br/>";     
	}
	
	private void BuildMediaList() {
		JsArrayString keys = pfm.projectSectionAssets.getRootKeys();
		for (int x=0;x<keys.length();x++) {
			JsArray<AlfrescoPacket> assets = pfm.projectSectionAssets.getValue(keys.get(x)).cast();
			for (int y=0;y<assets.length();y++)
				mediaList.add(assets.get(y).getNodeId() + "," + assets.get(y).getFilename());
		}
	}
	
	private String filenameToContents(String filename) {
		String filedata = "";
		
		if (filename.equals("adlcp_v1p3.xsd"))
			filedata = SCORMTemplates.INSTANCE.getAdlcp_v1p3().getText();
		else if (filename.equals("adlnav_v1p3.xsd"))
			filedata = SCORMTemplates.INSTANCE.getAdlnav_v1p3().getText();
		else if (filename.equals("adlseq_v1p3.xsd"))
			filedata = SCORMTemplates.INSTANCE.getAdlseq_v1p3().getText();
		else if (filename.equals("/common/anyElement.xsd"))
			filedata = SCORMTemplates.INSTANCE.getCommonAnyElement().getText();
		else if (filename.equals("/common/datatypes.xsd"))
			filedata = SCORMTemplates.INSTANCE.getCommonDataTypes().getText();
		else if (filename.equals("/common/elementNames.xsd"))
			filedata = SCORMTemplates.INSTANCE.getCommonElementNames().getText();
		else if (filename.equals("/common/elementTypes.xsd"))
			filedata = SCORMTemplates.INSTANCE.getCommonElementTypes().getText();
		else if (filename.equals("/common/rootElement.xsd"))
			filedata = SCORMTemplates.INSTANCE.getCommonRootElement().getText();
		else if (filename.equals("/common/vocabTypes.xsd"))
			filedata = SCORMTemplates.INSTANCE.getCommonVocabTypes().getText();
		else if (filename.equals("/common/vocabValues.xsd"))
			filedata = SCORMTemplates.INSTANCE.getCommonVocabValues().getText();
		else if (filename.equals("datatypes.dtd"))
			filedata = SCORMTemplates.INSTANCE.getDatatypes().getText();
		else if (filename.equals("/extend/custom.xml"))
			filedata = SCORMTemplates.INSTANCE.getExtendCustom().getText();
		else if (filename.equals("/extend/strict.xml"))
			filedata = SCORMTemplates.INSTANCE.getExtendStrict().getText();
		else if (filename.equals("imsmanifest.xml"))
			filedata = SCORMTemplates.INSTANCE.getImsmanifest().getText();
		else if (filename.equals("imsss_v1p0.xsd"))
			filedata = SCORMTemplates.INSTANCE.getImsss_v1p0().getText();
		else if (filename.equals("imsss_v1p0auxresource.xsd"))
			filedata = SCORMTemplates.INSTANCE.getImsss_v1p0auxresource().getText();
		else if (filename.equals("imsss_v1p0control.xsd"))
			filedata = SCORMTemplates.INSTANCE.getImsss_v1p0control().getText();
		else if (filename.equals("imsss_v1p0delivery.xsd"))
			filedata = SCORMTemplates.INSTANCE.getImsss_v1p0delivery().getText();
		else if (filename.equals("imsss_v1p0limit.xsd"))
			filedata = SCORMTemplates.INSTANCE.getImsss_v1p0limit().getText();
		else if (filename.equals("imsss_v1p0objective.xsd"))
			filedata = SCORMTemplates.INSTANCE.getImsss_v1p0objective().getText();
		else if (filename.equals("imsss_v1p0random.xsd"))
			filedata = SCORMTemplates.INSTANCE.getImsss_v1p0random().getText();
		else if (filename.equals("imsss_v1p0rollup.xsd"))
			filedata = SCORMTemplates.INSTANCE.getImsss_v1p0rollup().getText();
		else if (filename.equals("imsss_v1p0seqrule.xsd"))
			filedata = SCORMTemplates.INSTANCE.getImsss_v1p0seqrule().getText();
		else if (filename.equals("imsss_v1p0util.xsd"))
			filedata = SCORMTemplates.INSTANCE.getImsss_v1p0util().getText();
		else if (filename.equals("initPage.html"))
			filedata = SCORMTemplates.INSTANCE.getInitPage().getText();
		else if (filename.equals("lom.xsd"))
			filedata = SCORMTemplates.INSTANCE.getLom().getText();
		else if (filename.equals("lomCustom.xsd"))
			filedata = SCORMTemplates.INSTANCE.getLomCustom().getText();
		else if (filename.equals("lomLoose.xsd"))
			filedata = SCORMTemplates.INSTANCE.getLomLoose().getText();
		else if (filename.equals("lomStrict.xsd"))
			filedata = SCORMTemplates.INSTANCE.getLomStrict().getText();
		else if (filename.equals("/unique/loose.xsd"))
			filedata = SCORMTemplates.INSTANCE.getUniqueLoose().getText();
		else if (filename.equals("/unique/strict.xsd"))
			filedata = SCORMTemplates.INSTANCE.getUniqueStrict().getText();
		else if (filename.equals("/vocab/adlmd_vocabv1p0.xsd"))
			filedata = SCORMTemplates.INSTANCE.getVocabAdlmd_Vocabv1p0().getText();
		else if (filename.equals("/vocab/custom.xsd"))
			filedata = SCORMTemplates.INSTANCE.getVocabCustom().getText();
		else if (filename.equals("/vocab/loose.xsd"))
			filedata = SCORMTemplates.INSTANCE.getVocabLoose().getText();
		else if (filename.equals("/vocab/strict.xsd"))
			filedata = SCORMTemplates.INSTANCE.getVocabLoose().getText();
		else if (filename.equals("xml.xsd"))
			filedata = SCORMTemplates.INSTANCE.getXml().getText();
		else if (filename.equals("XMLScheme.dtd"))
			filedata = SCORMTemplates.INSTANCE.getXMLSchema().getText();
		
		return filedata;
	}
}