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

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public interface HtmlTemplates extends ClientBundle {
	public static final HtmlTemplates INSTANCE = GWT.create(HtmlTemplates.class);

	@Source("templates/RusselHeader.html")
	public TextResource getHeader();
	
	@Source("templates/RusselFooter.html")
	public TextResource getFooter();
	
	@Source("templates/RusselMainMenuPanel.html")
	public TextResource getMenuBar();

	@Source("templates/RusselEditPanel.html")
	public TextResource getEditPanel();

	@Source("templates/RusselEditPanelWidget.html")
	public TextResource getEditPanelWidget();

	@Source("templates/RusselDetailModel.html")
	public TextResource getDetailModel();

	@Source("templates/RusselLoginPanel.html")
	public TextResource getLoginWidget();

	@Source("templates/RusselErrorWidget.html")
	public TextResource getErrorWidget();
	
	@Source("templates/RusselRecentItemPanel.html")
	public TextResource getObjectPanel();
	
	@Source("templates/RusselRecentItemPanelWidget.html")
	public TextResource getObjectPanelWidget();
	
	@Source("templates/RusselDetailComment.html")
	public TextResource getDetailComment();
	
	@Source("templates/RusselEPSSHomePanel.html")
	public TextResource getEPSSHome();
	
	@Source("templates/RusselEPSSEditPanel.html")
	public TextResource getEPSSEdit();

	@Source("templates/RusselEPSSEditPanel2.html")
	public TextResource getEPSSEdit2();

	@Source("templates/RusselEPSSProjectDialog.html")
	public TextResource getDialog();
	
	@Source("templates/RusselEPSSAssetPanelWidget.html")
	public TextResource getEPSSAssetObjectPanelWidget();
	
	@Source("templates/RusselEPSSProjectPanelWidget.html")
	public TextResource getEPSSProjectObjectPanelWidget();
	
	@Source("templates/RusselEPSSNewProjectPanelWidget.html")
	public TextResource getEPSSNewProjectWidget();
	
	@Source("templates/RusselEPSSEditSectionWidgets.html")
	public TextResource getEPSSEditSectionWidgets();
	
	@Source("templates/RusselSearchPanel.html")
	public TextResource getSearchPanel();
	
	@Source("templates/RusselSearchPanelWidget.html")
	public TextResource getSearchPanelWidget();
	
	@Source("templates/RusselEPSSNoteAssetPanelWidget.html")
	public TextResource getEPSSNoteAssetObjectWidget();
}