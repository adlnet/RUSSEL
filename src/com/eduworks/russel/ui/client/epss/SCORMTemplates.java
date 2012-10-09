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