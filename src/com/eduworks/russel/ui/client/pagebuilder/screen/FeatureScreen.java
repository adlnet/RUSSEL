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

import com.eduworks.gwt.client.net.callback.EventCallback;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.gwt.client.pagebuilder.ScreenTemplate;
import com.eduworks.russel.ui.client.Russel;
import com.eduworks.russel.ui.client.epss.ProjectFileModel;
import com.eduworks.russel.ui.client.handler.AlfrescoSearchHandler;
import com.eduworks.russel.ui.client.pagebuilder.HtmlTemplates;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;

public class FeatureScreen extends ScreenTemplate {

	public static final String PROJECTS_TYPE = "projects";
	public static final String COLLECTIONS_TYPE = "collections";
	public static final String FLR_TYPE = "flr";
	
	public String featureType;
	private String pageTitle;
	
	private AlfrescoSearchHandler ash;
	public void lostFocus() {
		ash.stop();
	}
	
	private final native String doClose(String dialog) /*-{
		return $wnd.$('#projectProperties').trigger('reveal:close');
	}-*/;
	
	public void display() {
		PageAssembler.getInstance().ready(new HTML(HtmlTemplates.INSTANCE.getFeatureHomePanel().getText()));
		PageAssembler.getInstance().buildContents();
		
		DOM.getElementById("r-menuWorkspace").removeClassName("active");
		DOM.getElementById("r-menuProjects").addClassName("active");
		DOM.getElementById("r-menuCollections").removeClassName("active");
		
		if (featureType.equals(PROJECTS_TYPE)) {
			pageTitle = "Projects";
			DOM.getElementById("r-menuProjects").addClassName("active");
			DOM.getElementById("r-menuCollections").removeClassName("active");
			DOM.getElementById("r-MyFilesTile").addClassName("hidden");
			DOM.getElementById("r-FLRfilesTile").addClassName("hidden");
		} 
		else if (featureType.equals(COLLECTIONS_TYPE)) {
			pageTitle = "Collections";
			DOM.getElementById("r-menuCollections").addClassName("active");
			DOM.getElementById("r-menuProjects").removeClassName("active");
			DOM.getElementById("r-MyFilesTile").removeClassName("hidden");
			DOM.getElementById("r-FLRfilesTile").removeClassName("hidden");
		} 
		else {
			Window.alert("FeatureScreen received request for "+featureType);
			pageTitle = "Unknown Feature Type";
		}

		DOM.getElementById("r-pageTitle").setInnerHTML("<h4>"+pageTitle+"</h4>");
				
		ash = new AlfrescoSearchHandler();
		if (featureType.equals(PROJECTS_TYPE)) {
			// The newCollectionModal is not "hooked" in the template, so it does not need to be removed for the Projects feature.
			DOM.getElementById("r-newEntityFront").setInnerHTML("<p class='title'>New Project</p>");
			DOM.getElementById("r-newEntityBack").setInnerHTML("<p class='status'><span class='status-label'>Click to create a new project...</span></p>");
			DOM.getElementById("r-newEntityAction").setTitle("Start a new project");
			ash.hook("r-menuSearchBar", "searchObjectPanelScroll", AlfrescoSearchHandler.PROJECT_TYPE);
		}
		else if (featureType.equals(COLLECTIONS_TYPE)) {
			// Currently, the newProjectModal is "hooked" in the template, so it must be removed for the Collections feature.
			// NOTE: Once we are creating collection nodes in the repository, the My Files and collection listing will be built the same way as it is for Projects.
			Element e = (Element)DOM.getElementById("newProjectModal");
			if (e!=null)  e.removeFromParent();
			DOM.getElementById("r-newEntityFront").setInnerHTML("<p class='title'>New Collection</p>");
			DOM.getElementById("r-newEntityBack").setInnerHTML("<p class='status'><span class='status-label'>Click to create a new collection...</span></p>");
			DOM.getElementById("r-newEntityAction").setTitle("Start a new collection");
			DOM.getElementById("r-newEntityAction").setAttribute("onclick", "$('#newCollectionModal').reveal();");
			// For now (since there is only one collection implemented), use of the search bar on this screen will initiate a global search. 
			// Later, when we have implemented the ability to build collections, this should probably change to a search of all collections.
			ash.hook("r-menuSearchBar", "searchPanelWidgetScroll", AlfrescoSearchHandler.SEARCH_TYPE);
		}
		
		// Handlers for EPSS Home Screen
		PageAssembler.attachHandler("epss-gagne", Event.ONCLICK, new EventCallback() {
												   	   @Override
												   	   public void onEvent(Event event) {
												   		   doClose("#newProjectModal");
												   		   Russel.view.loadScreen(new EPSSEditScreen(new ProjectFileModel(ProjectFileModel.GAGNE_TEMPLATE)), true);
													   }
												   });

		PageAssembler.attachHandler("epss-sim", Event.ONCLICK, new EventCallback() {
														@Override
														public void onEvent(Event event) {
													   		   doClose("#newProjectModal");
													   		   Russel.view.loadScreen(new EPSSEditScreen(new ProjectFileModel(ProjectFileModel.SIMULATION_TEMPLATE)), true);
														}
													});
		
		// Handlers for Collections Home Screen
		PageAssembler.attachHandler("myFiles", Event.ONCLICK, new EventCallback() {
												   	   @Override
												   	   public void onEvent(Event event) {
													   		ResultsScreen rs = new ResultsScreen();
													   		rs.searchType = AlfrescoSearchHandler.COLLECTION_TYPE; 
													   		Russel.view.loadScreen(rs, true);
													   }
												   });

		PageAssembler.attachHandler("FLRFiles", Event.ONCLICK, new EventCallback() {
												   	   @Override
												   	   public void onEvent(Event event) {
													   		ResultsScreen rs = new ResultsScreen();
													   		rs.searchType = AlfrescoSearchHandler.FLR_TYPE; 
													   		Russel.view.loadScreen(rs, true);
													   }
												   });

		PageAssembler.attachHandler("newCollectionModal", Event.ONCLICK, Russel.nonFunctional);


	}
}