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

package com.eduworks.russel.ui.client.pagebuilder.screen;

import com.eduworks.gwt.client.net.callback.EventCallback;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.gwt.client.pagebuilder.ScreenTemplate;
import com.eduworks.russel.ui.client.Russel;
import com.eduworks.russel.ui.client.epss.ProjectFileModel;
import com.eduworks.russel.ui.client.handler.AlfrescoSearchHandler;
import com.eduworks.russel.ui.client.pagebuilder.EpssTemplates;
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
	
	public void display() {
		PageAssembler.ready(new HTML(HtmlTemplates.INSTANCE.getFeatureHomePanel().getText()));
		PageAssembler.buildContents();
		
		DOM.getElementById("r-menuWorkspace").getParentElement().removeClassName("active");
		DOM.getElementById("r-menuProjects").getParentElement().removeClassName("active");
		DOM.getElementById("r-menuCollections").getParentElement().removeClassName("active");
		
		if (featureType.equals(PROJECTS_TYPE)) {
			pageTitle = "Projects";
			DOM.getElementById("r-menuProjects").getParentElement().addClassName("active");
			DOM.getElementById("r-menuCollections").getParentElement().removeClassName("active");
			DOM.getElementById("r-MyFilesTile").addClassName("hidden");
			DOM.getElementById("r-FLRfilesTile").addClassName("hidden");
		} 
		else if (featureType.equals(COLLECTIONS_TYPE)) {
			pageTitle = "Collections";
			DOM.getElementById("r-menuCollections").getParentElement().addClassName("active");
			DOM.getElementById("r-menuProjects").getParentElement().removeClassName("active");
			DOM.getElementById("r-MyFilesTile").addClassName("hidden");
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
			DOM.getElementById("r-newEntityFront").setInnerHTML("<p class='title'>Collection</p>");
			DOM.getElementById("r-newEntityBack").setInnerHTML("<p class='status'><span class='status-label'>Open My Files...</span></p>");
			DOM.getElementById("r-newEntityAction").setTitle("Collection");
			PageAssembler.attachHandler("r-newEntityAction", Event.ONCLICK, new EventCallback() {
																   	   @Override
																   	   public void onEvent(Event event) {
																	   		ResultsScreen rs = new ResultsScreen();
																	   		rs.searchType = AlfrescoSearchHandler.COLLECTION_TYPE; 
																	   		Russel.view.loadScreen(rs, true);
																	   }
																   });
			//DOM.getElementById("r-newEntityAction").setAttribute("onclick", ""); //$('#newCollectionModal').reveal();
			// For now (since there is only one collection implemented), use of the search bar on this screen will initiate a global search. 
			// Later, when we have implemented the ability to build collections, this should probably change to a search of all collections.
			ash.hook("r-menuSearchBar", "searchPanelWidgetScroll", AlfrescoSearchHandler.SEARCH_TYPE);
		}
		
		// Handlers for EPSS Home Screen
		PageAssembler.attachHandler("epss-gagne", Event.ONCLICK, new EventCallback() {
												   	   @Override
												   	   public void onEvent(Event event) {
												   		   PageAssembler.closePopup("newProjectModal");
												   		   Russel.view.loadScreen(new EPSSEditScreen(new ProjectFileModel(EpssTemplates.INSTANCE.getGagneTemplate().getText())), true);
													   }
												   });

		PageAssembler.attachHandler("epss-sim", Event.ONCLICK, new EventCallback() {
														@Override
														public void onEvent(Event event) {
													   		   PageAssembler.closePopup("newProjectModal");
													   		   Russel.view.loadScreen(new EPSSEditScreen(new ProjectFileModel(EpssTemplates.INSTANCE.getSimulationTemplate().getText())), true);
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

		//PageAssembler.attachHandler("newCollectionModal", Event.ONCLICK, Russel.nonFunctional);


	}
}