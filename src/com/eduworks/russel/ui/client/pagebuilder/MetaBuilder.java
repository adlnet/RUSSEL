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

package com.eduworks.russel.ui.client.pagebuilder;

import com.eduworks.gwt.russel.ui.client.net.AlfrescoApi;
import com.eduworks.gwt.russel.ui.client.net.AlfrescoCallback;
import com.eduworks.gwt.russel.ui.client.net.AlfrescoPacket;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public class MetaBuilder {
	public static final String DETAIL_SCREEN = "detail";
	public static final String EDIT_SCREEN = "edit";
	
	private String metaType;
	
	public MetaBuilder (String mType) {
		this.metaType = mType;
	}
	
	private final native String doColors(String s) /*-{
		return $wnd.getSecurityColor(s);
	}-*/;

	private final native String putObjectives(String s, String id) /*-{
		return $wnd.listObjectives(s, id);
	}-*/;

	private final native String getObjectives(String id) /*-{
		return $wnd.compressObjectives(id);
	}-*/;

	public void addMetaDataToField(String field, String property, String id, AlfrescoPacket alfrescoPacket) {
		String fieldVal = "Click to edit";
		if (property.equalsIgnoreCase("cm:title") || property.equalsIgnoreCase("cmis:versionLabel") || property.equalsIgnoreCase("cmis:contentStreamLength") ||
				property.equalsIgnoreCase("cmis:contentStreamMimeType"))
			fieldVal = "N/A";

		if (!alfrescoPacket.getPropertyValue(field, property).trim().equalsIgnoreCase(""))
			fieldVal = alfrescoPacket.getPropertyValue(field, property);
		
		if ((metaType.equals(EDIT_SCREEN)&&(property=="russel:objective")) ||
		    (metaType.equals(DETAIL_SCREEN)&&(property=="russel:objective"))) {
			putObjectives(fieldVal, id);
		}
		else if (metaType.equals(EDIT_SCREEN)&&(property=="russel:class"||property=="russel:level"||property=="russel:dist"))
			DOM.getElementById(id).setInnerHTML(doColors(fieldVal));
		else 
			((Label)PageAssembler.elementToWidget(id, PageAssembler.LABEL)).setText(fieldVal);
	}
	
	public void addMetaDataFields(String field, AlfrescoPacket ap, AlfrescoPacket apTags) {
		if (metaType.equals(EDIT_SCREEN)) {
			addMetaDataToField(field, "cm:title", "metaTitle", ap);
			addMetaDataToField(field, "cm:description", "metaDescription", ap);
			addMetaDataToField(field, "cm:author", "metaPublisher", ap);
			addMetaDataToField(field, "russel:class", "metaClassification", ap);
			addMetaDataToField(field, "russel:objective", "display-objective-list", ap);
			addMetaDataToField(field, "russel:activity", "metaInteractivity", ap);
			addMetaDataToField(field, "russel:env", "metaEnvironment", ap);
			addMetaDataToField(field, "russel:coverage", "metaCoverage", ap);
			addMetaDataToField(field, "russel:agerange", "metaSkill", ap);
			addMetaDataToField(field, "russel:language", "metaLanguage", ap);
			addMetaDataToField(field, "russel:duration", "metaDuration", ap);
			addMetaDataToField(field, "russel:techreqs", "metaTechnicalRequirements", ap);
			addMetaDataToField(field, "russel:dist", "metaDistribution", ap);
			addMetaDataToField(field, "russel:level", "metaLevel", ap);
			addMetaDataToField(field, "russel:partof", "metaPartOf", ap);
			addMetaDataToField(field, "russel:requires", "metaRequires", ap);
			addMetaDataToField(field, "cmis:contentStreamMimeType", "metaFormat", ap);
			addMetaDataToField(field, "cmis:versionLabel", "metaVersion", ap);
			addMetaDataToField(field, "cmis:contentStreamLength", "metaSize", ap);
		} else {
			addMetaDataToField(field, "cm:title", "r-detailTitle", ap);
			addMetaDataToField(field, "cm:title", "detailMetaTitle", ap);
			addMetaDataToField(field, "cm:description", "detailMetaDescription", ap);
			addMetaDataToField(field, "cm:author", "detailMetaPublisher", ap);
			addMetaDataToField(field, "russel:class", "detailMetaClassification", ap);
			addMetaDataToField(field, "russel:objective", "detail-objective-list", ap);
			addMetaDataToField(field, "russel:activity", "detailMetaInteractivity", ap);
			addMetaDataToField(field, "russel:env", "detailMetaEnvironment", ap);
			addMetaDataToField(field, "russel:coverage", "detailMetaCoverage", ap);
			addMetaDataToField(field, "russel:agerange", "detailMetaSkill", ap);
			addMetaDataToField(field, "russel:language", "detailMetaLanguage", ap);
			addMetaDataToField(field, "russel:duration", "detailMetaDuration", ap);
			addMetaDataToField(field, "russel:techreqs", "detailMetaTechnicalRequirements", ap);
			addMetaDataToField(field, "russel:dist", "detailMetaDistribution", ap);
			addMetaDataToField(field, "russel:level", "detailMetaLevel", ap);
			addMetaDataToField(field, "russel:partof", "detailMetaPartOf", ap);
			addMetaDataToField(field, "russel:requires", "detailMetaRequires", ap);
			addMetaDataToField(field, "russel:epssStrategy", "detailEpssStrategies", ap);
			addMetaDataToField(field, "cmis:contentStreamMimeType", "detailMetaFormat", ap);
			addMetaDataToField(field, "cmis:versionLabel", "detailMetaVersion", ap);
			addMetaDataToField(field, "cmis:contentStreamLength", "detailMetaSize", ap);
		}
		String acc = "";
		for (int x=0;x<apTags.getTags().length();x++)
			if (apTags.getTags().get(x).trim()!="")
				acc = "," + apTags.getTags().get(x).trim();
		if (acc!="") acc = acc.substring(1);
		else acc = "Click to edit";
		if (metaType.equals(EDIT_SCREEN))
			((Label)PageAssembler.elementToWidget("metaKeywords", PageAssembler.LABEL)).setText(acc);
		else
			((Label)PageAssembler.elementToWidget("detailMetaKeywords", PageAssembler.LABEL)).setText(acc);
	}
	
	public void saveMetadata(String nodeId, AlfrescoCallback<AlfrescoPacket> callback) {	
		String postString = buildMetaPacket();
		if (postString!=null&&nodeId!=null)
			AlfrescoApi.setObjectProperties(nodeId, postString, callback);
	}

	public String buildMetaPacket() {
		AlfrescoPacket ap = AlfrescoPacket.makePacket();
		AlfrescoPacket container = AlfrescoPacket.makePacket();
		if (metaType.equals(DETAIL_SCREEN)) {
			addProperty("cm:title", "detailMetaTitle", ap);
			addProperty("cm:description", "detailMetaDescription", ap);
			addProperty("cm:author", "detailMetaPublisher", ap);
			addProperty("russel:class", "detailMetaClassification", ap);
			addObjectiveProperty(ap, "detail-objective-list");
			addProperty("russel:activity", "detailMetaInteractivity", ap);
			addProperty("russel:env", "detailMetaEnvironment", ap);
			addProperty("russel:coverage", "detailMetaCoverage", ap);
			addProperty("russel:agerange", "detailMetaSkill", ap);
			addProperty("russel:language", "detailMetaLanguage", ap);
			addProperty("russel:duration", "detailMetaDuration", ap);
			addProperty("russel:techreqs", "detailMetaTechnicalRequirements", ap);
			addProperty("russel:dist", "detailMetaDistribution", ap);
			addProperty("russel:level", "detailMetaLevel", ap);
			addProperty("russel:partof", "detailMetaPartOf", ap);
			addProperty("russel:requires", "detailMetaRequires", ap);
			addProperty("russel:epssStrategy", "detailEpssStrategies", ap);
			addProperty("tags", "detailMetaKeywords", container);
		} else {
			addProperty("cm:title", "metaTitle", ap);
			addProperty("cm:description", "metaDescription", ap);
			addProperty("cm:author", "metaPublisher", ap);
			addProperty("russel:class", "metaClassification", ap);
			addObjectiveProperty(ap, "display-objective-list");
			addProperty("russel:activity", "metaInteractivity", ap);
			addProperty("russel:env", "metaEnvironment", ap);
			addProperty("russel:coverage", "metaCoverage", ap);
			addProperty("russel:agerange", "metaSkill", ap);
			addProperty("russel:language", "metaLanguage", ap);
			addProperty("russel:duration", "metaDuration", ap);
			addProperty("russel:techreqs", "metaTechnicalRequirements", ap);
			addProperty("russel:dist", "metaDistribution", ap);
			addProperty("russel:level", "metaLevel", ap);
			addProperty("russel:partof", "metaPartOf", ap);
			addProperty("russel:requires", "metaRequires", ap);
			addProperty("tags", "metaKeywords", container);
		}
		if (!ap.toJSONString().equals("{}"))
			container.addKeyValue("properties", ap.toJSONString());
		if (container.toJSONString().equals("{}"))
			return null;
		return container.toJSONString();
	}
	
	private AlfrescoPacket addProperty(String property, String elementID, AlfrescoPacket ap) {
		String val = ((Label)PageAssembler.elementToWidget(elementID, PageAssembler.LABEL)).getText();
		if (val==null||val=="")
			val = ((TextBox)PageAssembler.elementToWidget(elementID, PageAssembler.TEXT)).getText();
		if (val==null)
			val = "Click to edit";
		
		if (val.trim().equalsIgnoreCase(".")) 
			if (property!="tags")
				ap.addKeyValue(property, "\"\"");
			else
				ap.addKeyValue(property, "[]");
		else if (!val.equalsIgnoreCase("Click to edit") && !val.equalsIgnoreCase("N/A"))
			if (property!="tags")
				ap.addKeyValue(property, "\"" + val.replaceAll("\"", "\'").replaceAll("\r", " ").replaceAll("\n", " ").trim() + "\"");
			else
				ap.addKeyValue(property, "[\"" + val.replaceAll("w+,w+", "\",\"").trim() + "\"]");
		
		return ap;
	}

	private AlfrescoPacket addObjectiveProperty(AlfrescoPacket ap, String elementID) {
		String val = getObjectives(elementID);
		
		if (val==null)
			val = "Click to edit";
		if (!val.equalsIgnoreCase("Click to edit") && !val.equalsIgnoreCase("N/A"))
			ap.addKeyValue("russel:objective", "\"" + val.replaceAll("\"", "\'").replaceAll("\r", " ").replaceAll("\n", " ").trim() + "\"");	
		
		return ap;
	}

	public static String convertToMetaPacket(AlfrescoPacket ap) {
		AlfrescoPacket container = AlfrescoPacket.makePacket();
		if (!ap.toJSONString().equals("{}"))
			container.addKeyValue("properties", ap.toJSONWrappedString());
		if (container.toJSONString().equals("{}"))
			return null;
		return container.toJSONString();
	}	 
	
}