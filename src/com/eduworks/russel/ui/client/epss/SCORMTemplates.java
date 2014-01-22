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

/**
 * SCORMTemplates
 * Extends ClientBundle
 * Defines globals, methods for asset extraction
 * 
 * @author Eduworks Corporation
 */
public interface SCORMTemplates extends ClientBundle {
	public static final SCORMTemplates INSTANCE = GWT.create(SCORMTemplates.class);

	/**
	 * getAdlcp_v1p3 Returns the text contents of the SCORM file called Adlcp_v1p3.xsd
	 * @return TextResource
	 */
	@Source("scorm/Adlcp_v1p3.xsd")
	public TextResource getAdlcp_v1p3();
	
	/**
	 * getAdlnav_v1p3 Returns the text contents of the SCORM file called Adlnav_v1p3.xsd
	 * @return TextResource
	 */

	@Source("scorm/Adlnav_v1p3.xsd")
	public TextResource getAdlnav_v1p3();
	
	/**
	 * getAdlseq_v1p3 Returns the text contents of the SCORM file called Adlseq_v1p3.xsd
	 * @return TextResource
	 */
	@Source("scorm/Adlseq_v1p3.xsd")
	public TextResource getAdlseq_v1p3();
	
	/**
	 * getCommonAnyElement Returns the text contents of the SCORM file called CommonAnyElement.xsd
	 * @return TextResource
	 */
	@Source("scorm/CommonAnyElement.xsd")
	public TextResource getCommonAnyElement();
	
	/**
	 * getCommonDataTypes Returns the text contents of the SCORM file called CommonDataTypes.xsd
	 * @return TextResource
	 */
	@Source("scorm/CommonDataTypes.xsd")
	public TextResource getCommonDataTypes();
	
	/**
	 * getCommonElementNames Returns the text contents of the SCORM file called CommonElementNames.xsd
	 * @return TextResource
	 */
	@Source("scorm/CommonElementNames.xsd")
	public TextResource getCommonElementNames();
	
	/**
	 * getCommonElementTypes Returns the text contents of the SCORM file called Adlcp_v1p3CommonElementTypes.xsd
	 * @return TextResource
	 */
	@Source("scorm/CommonElementTypes.xsd")
	public TextResource getCommonElementTypes();
	
	/**
	 * getCommonRootElement Returns the text contents of the SCORM file called CommonRootElement.xsd
	 * @return TextResource
	 */
	@Source("scorm/CommonRootElement.xsd")
	public TextResource getCommonRootElement();
	
	/**
	 * getCommonVocabTypes Returns the text contents of the SCORM file called CommonVocabTypes.xsd
	 * @return TextResource
	 */
	@Source("scorm/CommonVocabTypes.xsd")
	public TextResource getCommonVocabTypes();
	
	/**
	 * getCommonVocabValues Returns the text contents of the SCORM file called CommonVocabValues.xsd
	 * @return TextResource
	 */
	@Source("scorm/CommonVocabValues.xsd")
	public TextResource getCommonVocabValues();
	
	/**
	 * getDataTypes Returns the text contents of the SCORM file called DaaTypes.dtd
	 * @return TextResource
	 */
	@Source("scorm/Datatypes.dtd")
	public TextResource getDatatypes();
	
	/**
	 * getExtendCustom Returns the text contents of the SCORM file called ExtendCustom.xsd
	 * @return TextResource
	 */
	@Source("scorm/ExtendCustom.xsd")
	public TextResource getExtendCustom();
	
	/**
	 * getExtendStrict Returns the text contents of the SCORM file called ExtendStrict.xsd
	 * @return TextResource
	 */
	@Source("scorm/ExtendStrict.xsd")
	public TextResource getExtendStrict();
	
	/**
	 * getImsmanifest Returns the text contents of the SCORM file called Imsmanifest.xml
	 * @return TextResource
	 */
	@Source("scorm/Imsmanifest.xml")
	public TextResource getImsmanifest();
	
	/**
	 * getImss_v1p0 Returns the text contents of the SCORM file called Imss_v1p0.xsd
	 * @return TextResource
	 */
	@Source("scorm/Imsss_v1p0.xsd")
	public TextResource getImsss_v1p0();
	
	/**
	 * getImss_v1p0auxresource Returns the text contents of the SCORM file called Imss_v1p0auxresource.xsd
	 * @return TextResource
	 */
	@Source("scorm/Imsss_v1p0auxresource.xsd")
	public TextResource getImsss_v1p0auxresource();
	
	/**
	 * getImss_v1p0control Returns the text contents of the SCORM file called Imss_v1p0control.xsd
	 * @return TextResource
	 */
	@Source("scorm/Imsss_v1p0control.xsd")
	public TextResource getImsss_v1p0control();
	
	/**
	 * getImss_v1p0delivery Returns the text contents of the SCORM file called Imss_v1p0delivery.xsd
	 * @return TextResource
	 */
	@Source("scorm/Imsss_v1p0delivery.xsd")
	public TextResource getImsss_v1p0delivery();
	
	/**
	 * getImss_v1p0limit Returns the text contents of the SCORM file called Imss_v1p0limit.xsd
	 * @return TextResource
	 */
	@Source("scorm/Imsss_v1p0limit.xsd")
	public TextResource getImsss_v1p0limit();
	
	/**
	 * getImss_v1p0objective Returns the text contents of the SCORM file called Imss_v1p0objective.xsd
	 * @return TextResource
	 */
	@Source("scorm/Imsss_v1p0objective.xsd")
	public TextResource getImsss_v1p0objective();
	
	/**
	 * getImss_v1p0random Returns the text contents of the SCORM file called Imss_v1p0random.xsd
	 * @return TextResource
	 */
	@Source("scorm/Imsss_v1p0random.xsd")
	public TextResource getImsss_v1p0random();
	
	/**
	 * getImss_v1p0rollup Returns the text contents of the SCORM file called Imss_v1p0rollup.xsd
	 * @return TextResource
	 */
	@Source("scorm/Imsss_v1p0rollup.xsd")
	public TextResource getImsss_v1p0rollup();
	
	/**
	 * getImss_v1p0seqrule Returns the text contents of the SCORM file called Imss_v1p0seqrule.xsd
	 * @return TextResource
	 */
	@Source("scorm/Imsss_v1p0seqrule.xsd")
	public TextResource getImsss_v1p0seqrule();
	
	/**
	 * getImss_v1p0util Returns the text contents of the SCORM file called Imss_v1p0util.xsd
	 * @return TextResource
	 */
	@Source("scorm/Imsss_v1p0util.xsd")
	public TextResource getImsss_v1p0util();
	
	/**
	 * getInitPage Returns the text contents of the SCORM file called InitPage.html
	 * @return TextResource
	 */
	@Source("scorm/InitPage.html")
	public TextResource getInitPage();
	
	/**
	 * getLom Returns the text contents of the SCORM file called Lom.xsd
	 * @return TextResource
	 */
	@Source("scorm/Lom.xsd")
	public TextResource getLom();
	
	/**
	 * getLomCustom Returns the text contents of the SCORM file called LomCustom.xsd
	 * @return TextResource
	 */
	@Source("scorm/LomCustom.xsd")
	public TextResource getLomCustom();
	
	/**
	 * getLomLoose Returns the text contents of the SCORM file called LomLoose.xsd
	 * @return TextResource
	 */
	@Source("scorm/LomLoose.xsd")
	public TextResource getLomLoose();
	
	/**
	 * getLomStrict Returns the text contents of the SCORM file called LomStrict.xsd
	 * @return TextResource
	 */
	@Source("scorm/LomStrict.xsd")
	public TextResource getLomStrict();
	
	/**
	 * getUniqueLoose Returns the text contents of the SCORM file called UniqueLoose.xsd
	 * @return TextResource
	 */
	@Source("scorm/UniqueLoose.xsd")
	public TextResource getUniqueLoose();
	
	/**
	 * getUniqueStrict Returns the text contents of the SCORM file called UniqueStrict.xsd
	 * @return TextResource
	 */
	@Source("scorm/UniqueStrict.xsd")
	public TextResource getUniqueStrict();
	
	/**
	 * getVocabAdlmd_Vocabv1p0 Returns the text contents of the SCORM file called VocabAdlmd_Vocabv1p0.xsd
	 * @return TextResource
	 */
	@Source("scorm/VocabAdlmd_Vocabv1p0.xsd")
	public TextResource getVocabAdlmd_Vocabv1p0();
	
	/**
	 * getVocabCustom Returns the text contents of the SCORM file called VocabCustom.xsd
	 * @return TextResource
	 */
	@Source("scorm/VocabCustom.xsd")
	public TextResource getVocabCustom();
	
	/**
	 * getVocabLoose Returns the text contents of the SCORM file called VocabLoose.xsd
	 * @return TextResource
	 */
	@Source("scorm/VocabLoose.xsd")
	public TextResource getVocabLoose();
	
	/**
	 * getVocabStrict Returns the text contents of the SCORM file called VocabStrict.xsd
	 * @return TextResource
	 */
	@Source("scorm/VocabStrict.xsd")
	public TextResource getVocabStrict();
	
	/**
	 * getXml Returns the text contents of the SCORM file called Xml.xsd
	 * @return TextResource
	 */
	@Source("scorm/Xml.xsd")
	public TextResource getXml();
	
	/**
	 * getXMLSchema Returns the text contents of the SCORM file called XMLSchema.dtd
	 * @return TextResource
	 */
	@Source("scorm/XMLSchema.dtd")
	public TextResource getXMLSchema();
}