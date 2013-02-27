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

import com.eduworks.gwt.client.net.CommunicationHub;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.gwt.client.pagebuilder.ScreenTemplate;
import com.eduworks.russel.ui.client.pagebuilder.HtmlTemplates;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

public class UtilityScreen extends ScreenTemplate {
	
	public static final String ACCOUNT_TYPE = "account";
	public static final String USERS_TYPE = "users";
	public static final String GROUPS_TYPE = "groups";
	public static final String REPSETTINGS_TYPE = "repository";
	
	public String utilType;
	private String pageTitle;
	private String alfURL;
	
	public void lostFocus() {
	}
	
	public void display() {
		PageAssembler.getInstance().ready(new HTML(HtmlTemplates.INSTANCE.getUtilityPanel().getText()));
		
		PageAssembler.getInstance().buildContents();
		
		DOM.getElementById("r-menuCollections").removeClassName("active");
		DOM.getElementById("r-menuProjects").removeClassName("active");
		DOM.getElementById("r-menuWorkspace").removeClassName("active");
		
		if (utilType.equals(ACCOUNT_TYPE)) {
			pageTitle = "Account Settings";
			alfURL = "share/page/user/admin/profile";
		} 
		else if (utilType.equals(USERS_TYPE)) {
			pageTitle = "Manage Users";
			alfURL = "share/page/console/admin-console/users";
		} 
		else if (utilType.equals(GROUPS_TYPE)) {
			pageTitle = "Manage Groups";
			alfURL = "share/page/console/admin-console/groups";			
		}
		else if (utilType.equals(REPSETTINGS_TYPE)) {
			pageTitle = "Repository Settings";
			alfURL = null;			
		}
		else {
			pageTitle = "Unknown Alfresco Utility";
			alfURL = null;			
		}
		
		DOM.getElementById("r-pageTitle").setInnerHTML("<h4>"+pageTitle+"</h4>");
		
		if (alfURL != null) {
			String frameSrc = CommunicationHub.ROOT_URL + alfURL + "?" + CommunicationHub.randomString(); 
	 		final Frame f = new Frame();
	 		f.getElement().setAttribute("seamless", "seamless");
	 		f.getElement().setAttribute("style", "border:0px; width:100%; display:none;");
	 		f.addLoadHandler(new LoadHandler() {
								@Override
								public void onLoad(LoadEvent event) {
									new Timer() {
										public void run() {
											Element e = ((Element)PageAssembler.getIFrameElement(f.getElement(), "alf-hd"));
											if (e!=null)
												e.removeFromParent();
											e = ((Element)PageAssembler.getIFrameElement(f.getElement(), "alf-filters"));
											if (e!=null)
												e.removeFromParent();
											e = ((Element)PageAssembler.getIFrameElement(f.getElement(), "alf-ft"));
											if (e!=null)
												e.removeFromParent();
											e = ((Element)PageAssembler.getIFrameElement(f.getElement(), "alf-content"));
											if (e!=null)
												e.setAttribute("style", "margin-left:0px;");
											f.getElement().setAttribute("style", "border:0px; width:100%; display:block; height:" + (f.getElement().getScrollHeight()+380) + "px");
										}
									}.schedule(400);
								}
							});
	 		f.setUrl(frameSrc);
	 		RootPanel.get("r-alfrescoUtil").add(f);
		}
		else if (utilType.equals(REPSETTINGS_TYPE)) {
			DOM.getElementById("r-alfrescoUtil").setInnerHTML("new repository settings form will go here...");
		}
	}
}