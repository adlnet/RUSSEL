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

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public interface SCORMTemplates extends ClientBundle {
	public static final SCORMTemplates INSTANCE = GWT.create(SCORMTemplates.class);

	@Source("scorm/Adlcp_v1p3.xsd")
	public TextResource getAdlcp_v1p3();
	
	@Source("scorm/Adlnav_v1p3.xsd")
	public TextResource getAdlnav_v1p3();
	
	@Source("scorm/Adlseq_v1p3.xsd")
	public TextResource getAdlseq_v1p3();
	
	@Source("scorm/CommonAnyElement.xsd")
	public TextResource getCommonAnyElement();
	
	@Source("scorm/CommonDataTypes.xsd")
	public TextResource getCommonDataTypes();
	
	@Source("scorm/CommonElementNames.xsd")
	public TextResource getCommonElementNames();
	
	@Source("scorm/CommonElementTypes.xsd")
	public TextResource getCommonElementTypes();
	
	@Source("scorm/CommonRootElement.xsd")
	public TextResource getCommonRootElement();
	
	@Source("scorm/CommonVocabTypes.xsd")
	public TextResource getCommonVocabTypes();
	
	@Source("scorm/CommonVocabValues.xsd")
	public TextResource getCommonVocabValues();
	
	@Source("scorm/Datatypes.dtd")
	public TextResource getDatatypes();
	
	@Source("scorm/ExtendCustom.xsd")
	public TextResource getExtendCustom();
	
	@Source("scorm/ExtendStrict.xsd")
	public TextResource getExtendStrict();
	
	@Source("scorm/Imsmanifest.xml")
	public TextResource getImsmanifest();
	
	@Source("scorm/Imsss_v1p0.xsd")
	public TextResource getImsss_v1p0();
	
	@Source("scorm/Imsss_v1p0auxresource.xsd")
	public TextResource getImsss_v1p0auxresource();
	
	@Source("scorm/Imsss_v1p0control.xsd")
	public TextResource getImsss_v1p0control();
	
	@Source("scorm/Imsss_v1p0delivery.xsd")
	public TextResource getImsss_v1p0delivery();
	
	@Source("scorm/Imsss_v1p0limit.xsd")
	public TextResource getImsss_v1p0limit();
	
	@Source("scorm/Imsss_v1p0objective.xsd")
	public TextResource getImsss_v1p0objective();
	
	@Source("scorm/Imsss_v1p0random.xsd")
	public TextResource getImsss_v1p0random();
	
	@Source("scorm/Imsss_v1p0rollup.xsd")
	public TextResource getImsss_v1p0rollup();
	
	@Source("scorm/Imsss_v1p0seqrule.xsd")
	public TextResource getImsss_v1p0seqrule();
	
	@Source("scorm/Imsss_v1p0util.xsd")
	public TextResource getImsss_v1p0util();
	
	@Source("scorm/InitPage.html")
	public TextResource getInitPage();
	
	@Source("scorm/Lom.xsd")
	public TextResource getLom();
	
	@Source("scorm/LomCustom.xsd")
	public TextResource getLomCustom();
	
	@Source("scorm/LomLoose.xsd")
	public TextResource getLomLoose();
	
	@Source("scorm/LomStrict.xsd")
	public TextResource getLomStrict();
	
	@Source("scorm/UniqueLoose.xsd")
	public TextResource getUniqueLoose();
	
	@Source("scorm/UniqueStrict.xsd")
	public TextResource getUniqueStrict();
	
	@Source("scorm/VocabAdlmd_Vocabv1p0.xsd")
	public TextResource getVocabAdlmd_Vocabv1p0();
	
	@Source("scorm/VocabCustom.xsd")
	public TextResource getVocabCustom();
	
	@Source("scorm/VocabLoose.xsd")
	public TextResource getVocabLoose();
	
	@Source("scorm/VocabStrict.xsd")
	public TextResource getVocabStrict();
	
	@Source("scorm/Xml.xsd")
	public TextResource getXml();
	
	@Source("scorm/XMLSchema.dtd")
	public TextResource getXMLSchema();
}