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

import org.vectomatic.file.Blob;

import com.eduworks.gwt.client.util.Uint8Array;
import com.eduworks.gwt.russel.ui.client.net.AlfrescoApi;
import com.eduworks.gwt.russel.ui.client.net.AlfrescoCallback;
import com.eduworks.gwt.russel.ui.client.net.AlfrescoPacket;
import com.eduworks.gwt.russel.ui.client.net.CommunicationHub;
import com.google.gwt.core.client.JsArrayInteger;

public class ProjectFileModel {
	public final static String GAGNE_TEMPLATE = "Gagne's Nine Events";
	public final static String SIMULATION_TEMPLATE = "Modified Simulation Model";
	public final static String RUSSEL_MIME_TYPE = "russel/project";
	
	public String projectTitle;
	public String projectCreator;
	public String projectTemplate;
	public String projectNotes;
	public String projectLearningObjectives;
	public String projectImi;
	public String projectTaxo;
	public String projectUsage;
	public String projectNodeId;
	public AlfrescoPacket projectSectionNotes;
	public AlfrescoPacket projectSectionAssets;
	
	public ProjectFileModel(String templateType) {
		this.projectTemplate = templateType;
		this.projectTitle = "Click here to add a title";
		this.projectImi = "0,";
		this.projectTaxo = "0,";
		this.projectUsage = "0,";
		this.projectNotes = "";
		this.projectLearningObjectives = "";
		this.projectCreator = AlfrescoApi.username;
		this.projectNodeId = null;
		this.projectSectionNotes = AlfrescoPacket.makePacket();
		this.projectSectionAssets = AlfrescoPacket.makePacket();
	}
	
	public ProjectFileModel(AlfrescoPacket ap) {
		this.projectTitle = ap.getValueString("projectTitle"); 
		this.projectImi = ap.getValueString("projectImi");
		this.projectTaxo = ap.getValueString("projectTaxo");
		this.projectUsage = ap.getValueString("projectUsage");
		this.projectNotes = ap.getValueString("projectNotes");
		this.projectLearningObjectives = ap.getValueString("projectLearningObjectives");
		this.projectTemplate = ap.getValueString("projectTemplate");
		this.projectCreator = ap.getValueString("projectCreator");
		this.projectNodeId = ap.getValueString("projectNodeId");
		this.projectSectionNotes = AlfrescoPacket.wrap(ap.getValue("projectSections"));
		this.projectSectionAssets = AlfrescoPacket.wrap(ap.getValue("projectSectionAssets"));
	}
	
	public Blob makeJSONBlob() {
		AlfrescoPacket ap = AlfrescoPacket.makePacket();
		ap.addKeyValue("projectTitle", "\"" + projectTitle + "\"");
		ap.addKeyValue("projectCreator", "\"" + projectCreator + "\"");
		ap.addKeyValue("projectTemplate", "\"" + projectTemplate + "\"");
		ap.addKeyValue("projectNotes", "\"" + projectNotes + "\"");
		ap.addKeyValue("projectLearningObjectives", "\"" + projectLearningObjectives + "\"");
		ap.addKeyValue("projectImi", "\"" + projectImi + "\"");
		ap.addKeyValue("projectTaxo", "\"" + projectTaxo + "\"");
		ap.addKeyValue("projectUsage", "\"" + projectUsage + "\"");
		ap.addKeyValue("projectNodeId", "\"" + projectNodeId + "\"");
		ap.addKeyValue("projectSections", projectSectionNotes.toJSONWrappedString());
		ap.addKeyValue("projectSectionAssets", projectSectionAssets.toJSONArrayString());
		return buildBlob(RUSSEL_MIME_TYPE, ap.toJSONString());
	}
	
	public void addAsset(String section, String assetId, String assetFilename, String notes) {
		AlfrescoPacket ap = AlfrescoPacket.makePacket();
		ap.addKeyValue("id", assetId);
		ap.addKeyValue("fileName", assetFilename);
		ap.addKeyValue("notes", notes);
		projectSectionAssets.addAsset(section, ap);
	}
	
	public void removeAsset(String section, String nodeId) {
		projectSectionAssets.removeAsset(section, nodeId);
	}
	
	public void addSectionNotes(String section, String notes) {
		projectSectionNotes.addKeyValue(section, notes);
	}

	public static void importFromAlfrescoNode(String nodeId, String filename, final AlfrescoCallback<AlfrescoPacket> callback) {
		AlfrescoApi.getObjectStream(nodeId, 
									filename, 
									new AlfrescoCallback<AlfrescoPacket>() {
										@Override
										public void onSuccess(AlfrescoPacket alfrescoPacket) {
											JsArrayInteger chars = Uint8Array.createUint8Array(alfrescoPacket.getContents());
											String acc = "";
											for (int x=0;x<chars.length();x++)
												acc += (char)chars.get(x);
											callback.onSuccess(AlfrescoPacket.wrap(CommunicationHub.parseJSON(acc)));
										}
										
										@Override
										public void onFailure(Throwable caught) {
											callback.onFailure(caught);
										}
									});
	}
	
	private native Blob buildBlob(String typ, String contents) /*-{
		var bb = new (window.BlobBuilder || window.MozBlobBuilder || window.WebKitBlobBuilder || window.OBlobBuilder || window.msBlobBuilder);
		bb.append(contents);
		return bb.getBlob(typ);
	}-*/;
}