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

import com.eduworks.gwt.client.component.HtmlTemplates;
import com.eduworks.gwt.client.net.callback.EventCallback;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.gwt.client.pagebuilder.modal.ModalDispatch;
import com.eduworks.gwt.client.pagebuilder.overlay.OverlayDispatch;
import com.eduworks.gwt.client.pagebuilder.screen.ScreenDispatch;
import com.eduworks.gwt.client.pagebuilder.screen.ScreenTemplate;
import com.eduworks.russel.ui.client.Russel;
import com.eduworks.russel.ui.client.handler.SearchHandler;
import com.eduworks.russel.ui.client.model.ProjectRecord;
import com.eduworks.russel.ui.client.model.RUSSELFileRecord;
import com.eduworks.russel.ui.client.net.RusselApi;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;

/**
 * FeatureScreen class
 * Extends ScreenTemplate
 * Defines methods and handlers for the Feature screen.
 * 
 * @author Eduworks Corporation
 */
public class FeatureScreen extends ScreenTemplate {

	public static final String PROJECTS_TYPE = "projects";
	public static final String COLLECTIONS_TYPE = "collections";
	public static final String FLR_TYPE = "flr";
	
	private String featureType;
	private String pageTitle;
	
	private SearchHandler ash;

	public FeatureScreen(String featureType) {
		this.featureType = featureType;
	}
	
	/**
	 * lostFocus In place to handle any processing requirements required when this screen loses focus.
	 * Called by ScreenDispatch for all RUSSEL screens.
	 */
	public void lostFocus() {
		ash.stop();
	}
	
	public void generateQuery() {
		StringBuilder sb = new StringBuilder();
		
		if (featureType.equals(COLLECTIONS_TYPE))
			sb.append(RUSSELFileRecord.OWNER + ":" + RusselApi.username);
		else if (featureType.equals(PROJECTS_TYPE))
			sb.append(RUSSELFileRecord.MIMETYPE + ":\"russel/project\"");
		
		String q = SearchHandler.cleanQuery(((TextBox)PageAssembler.elementToWidget("", PageAssembler.TEXT)).getText().trim());
		if (q!="")
			sb.append(" *");
		
		ash.query(sb.toString());
	}
	
	/**
	 * display Renders the Feature home screen using appropriate templates and sets up handlers
	 */
	public void display() {
		PageAssembler.ready(new HTML(Russel.htmlTemplates.getFeatureHomePanel().getText()));
		PageAssembler.buildContents();
		PageAssembler.inject("flowContainer", "x", new HTML(Russel.htmlTemplates.getDetailModal().getText()), true);
		PageAssembler.inject("objDetailPanelWidget", "x", new HTML(Russel.htmlTemplates.getDetailPanel().getText()), true);
		
		DOM.getElementById("r-menuWorkspace").getParentElement().removeClassName("active");
		DOM.getElementById("r-menuProjects").getParentElement().removeClassName("active");
		DOM.getElementById("r-menuCollections").getParentElement().removeClassName("active");
		
		ash = new SearchHandler(this, true);
		
		if (featureType.equals(PROJECTS_TYPE)) {
			pageTitle = "Projects";
			DOM.getElementById("r-menuProjects").getParentElement().addClassName("active");
			DOM.getElementById("r-menuCollections").getParentElement().removeClassName("active");
			DOM.getElementById("r-MyFilesTile").addClassName("hidden");
			DOM.getElementById("r-FLRfilesTile").addClassName("hidden");
			DOM.getElementById("r-newEntityFront").setInnerHTML("<p class='title'>New Project</p>");
			DOM.getElementById("r-newEntityBack").setInnerHTML("<p class='status'><span class='status-label'>Click to create a new project...</span></p>");
			DOM.getElementById("r-newEntityAction").setTitle("Start a new project");
			ash.hookAndClear("r-menuSearchBar", "searchObjectPanelScroll", SearchHandler.TYPE_PROJECT);
		} else if (featureType.equals(COLLECTIONS_TYPE)) {
			pageTitle = "Collections";
			DOM.getElementById("r-menuCollections").getParentElement().addClassName("active");
			DOM.getElementById("r-menuProjects").getParentElement().removeClassName("active");
			DOM.getElementById("r-MyFilesTile").addClassName("hidden");
			DOM.getElementById("r-FLRfilesTile").removeClassName("hidden");
			Element e = (Element)DOM.getElementById("newProjectModal");
			if (e!=null)  e.removeFromParent();
			DOM.getElementById("r-newEntityFront").setInnerHTML("<p class='title'>Collection</p>");
			DOM.getElementById("r-newEntityBack").setInnerHTML("<p class='status'><span class='status-label'>Open My Files...</span></p>");
			DOM.getElementById("r-newEntityAction").setTitle("Collection");
			PageAssembler.attachHandler("r-newEntityAction", Event.ONCLICK, new EventCallback() {
																   	   @Override
																   	   public void onEvent(Event event) {
																   		   Russel.screen.loadScreen(new SearchScreen(SearchHandler.TYPE_COLLECTION), true);
																	   }
																   });
			ash.hookAndClear("r-menuSearchBar", "searchPanelWidgetScroll", SearchHandler.TYPE_SEARCH);
		}
		
		PageAssembler.attachHandler("r-menuSearchBar", Event.ONCHANGE, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				generateQuery();
			}
		});

		DOM.getElementById("r-pageTitle").setInnerHTML("<h4>"+pageTitle+"</h4>");
		
		// Handlers for EPSS Home Screen
		PageAssembler.attachHandler("epss-gagne", Event.ONCLICK, new EventCallback() {
												   	   @Override
												   	   public void onEvent(Event event) {
												   		   PageAssembler.closePopup("newProjectModal");
												   		   ProjectRecord pr = new ProjectRecord(Russel.epssTemplates.getGagneTemplate().getText(), new RUSSELFileRecord());
												   		   Russel.screen.loadScreen(new EPSSScreen(pr), true);
													   }
												   });

		PageAssembler.attachHandler("epss-sim", Event.ONCLICK, new EventCallback() {
														@Override
														public void onEvent(Event event) {
												   		   PageAssembler.closePopup("newProjectModal");
												   		   ProjectRecord pr = new ProjectRecord(Russel.epssTemplates.getSimulationTemplate().getText(), new RUSSELFileRecord());
												   		   Russel.screen.loadScreen(new EPSSScreen(pr), true);
														}
													});
		
		// Handlers for Collections Home Screen
		PageAssembler.attachHandler("myFiles", Event.ONCLICK, new EventCallback() {
												   	   @Override
												   	   public void onEvent(Event event) {
												   		Russel.screen.loadScreen(new SearchScreen(SearchHandler.TYPE_COLLECTION), true);
													   }
												   });

		PageAssembler.attachHandler("FLRFiles", Event.ONCLICK, new EventCallback() {
												   	   @Override
												   	   public void onEvent(Event event) {
												   		Russel.screen.loadScreen(new SearchScreen(SearchHandler.TYPE_COLLECTION, SearchScreen.RESOURCE_LINK), true);
													   }
												   });	
		generateQuery();
	}

	@Override
	public ScreenDispatch getDispatcher() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OverlayDispatch getOverlayDispatcher() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ModalDispatch getModalDispatcher() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HtmlTemplates getTemplates() {
		// TODO Auto-generated method stub
		return null;
	}
}