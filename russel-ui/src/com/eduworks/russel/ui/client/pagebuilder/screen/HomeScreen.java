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
import com.eduworks.gwt.client.util.Date;
import com.eduworks.russel.ui.client.Russel;
import com.eduworks.russel.ui.client.handler.SearchHandler;
import com.eduworks.russel.ui.client.model.RUSSELFileRecord;
import com.eduworks.russel.ui.client.net.RusselApi;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

/**
 * HomeScreen class
 * Extends ScreenTemplate
 * Defines methods and handlers for the RUSSEL Home screen.
 * 
 * @author Eduworks Corporation
 */
public class HomeScreen extends ScreenTemplate {

	private final SearchHandler ash = new SearchHandler(this, true); 

	/**
	 * lostFocus In place to handle any processing requirements required when this screen loses focus.
	 * Called by ScreenDispatch for all RUSSEL screens.
	 */
	public void lostFocus() {
		ash.stop();
	}
	
	/**
	 * display Renders the RUSSEL home screen using appropriate templates and sets up handlers
	 */
	public void display() {
		((Label)PageAssembler.elementToWidget("r-menuUserName", PageAssembler.LABEL)).setText(RusselApi.username);
		
		PageAssembler.ready(new HTML(Russel.htmlTemplates.getMenuBar().getText()));
		PageAssembler.ready(new HTML(Russel.htmlTemplates.getObjectPanel().getText()));
		PageAssembler.buildContents();
		PageAssembler.inject("flowContainer", "x", new HTML(Russel.htmlTemplates.getDetailModal().getText()), true);
		PageAssembler.inject("objDetailPanelWidget", "x", new HTML(Russel.htmlTemplates.getDetailPanel().getText()), true);
		
		DOM.getElementById("r-menuWorkspace").getParentElement().addClassName("active");
		DOM.getElementById("r-menuCollections").getParentElement().removeClassName("active");
		DOM.getElementById("r-menuProjects").getParentElement().removeClassName("active");
		
		PageAssembler.attachHandler("r-uploadContentTile", Event.ONCLICK, new EventCallback() {
																				@Override
																				public void onEvent(Event event) {
																					Russel.screen.loadScreen(new EditScreen(), true);
																				}
																			});
		
		PageAssembler.attachHandler("r-projectsTile", Event.ONCLICK, new EventCallback() {
																			@Override
																			public void onEvent(Event event) {
																				Russel.screen.loadScreen(new FeatureScreen(FeatureScreen.PROJECTS_TYPE), true);
																			}
																	 });
		

		PageAssembler.attachHandler("r-menuProjects", Event.ONCLICK, new EventCallback() {
																		@Override
																		public void onEvent(Event event) {
																			Russel.screen.loadScreen(new FeatureScreen(FeatureScreen.PROJECTS_TYPE), true);
																		}
																	 });
		
		PageAssembler.attachHandler("r-menuCollections", Event.ONCLICK, new EventCallback() {
																		@Override
																		public void onEvent(Event event) {
																			Russel.screen.loadScreen(new FeatureScreen(FeatureScreen.COLLECTIONS_TYPE), true);
																		}
																	 });

		PageAssembler.attachHandler("r-collectionsTile", Event.ONCLICK, new EventCallback() {
																			@Override
																			public void onEvent(Event event) {
																				Russel.screen.loadScreen(new FeatureScreen(FeatureScreen.COLLECTIONS_TYPE), true);
																			}
																		 });
		
		PageAssembler.attachHandler("r-manageUsersTile", Event.ONCLICK, new EventCallback() {
																			@Override
																			public void onEvent(Event event) {
																				Russel.screen.loadScreen(new UserScreen(), true);
																			}
																		 });
						
		PageAssembler.attachHandler("r-groupTile", Event.ONCLICK, new EventCallback() {
																			@Override
																			public void onEvent(Event event) {
																				Russel.screen.loadScreen(new GroupScreen(), true);
																			}
																		 });
		
		PageAssembler.attachHandler("r-menuHelp", Event.ONCLICK, new EventCallback() {
																			@Override
																			public void onEvent(Event event) {
																				String help = PageAssembler.getHelp();
																				if (help != null && help != "")
																					Window.open(PageAssembler.getHelp(), "_blank", null);
																				else 
																					Window.alert("Help has not been configured for this installation of RUSSEL.");
																			}
																		 });
		
		((TextBox)PageAssembler.elementToWidget("r-menuSearchBar", PageAssembler.TEXT)).setFocus(true);
		
		ash.hookAndClear("r-menuSearchBar", "searchObjectPanelScroll", SearchHandler.TYPE_RECENT);
		Date currentDate = new Date();
		Date pastDate = new Date();
		pastDate.setDate(pastDate.getDate()-10);
		ash.query(RUSSELFileRecord.UPDATED_DATE + ":[" + pastDate.getTime() + " TO " + currentDate.getTime() + "]");
		
		PageAssembler.attachHandler("r-menuSearchBar", Event.ONKEYUP, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				Russel.screen.loadScreen(new SearchScreen(SearchHandler.TYPE_SEARCH), true);
			}
		});
		
		PageAssembler.attachHandler("r-objectEditSelected", Event.ONCLICK, new EventCallback() {
		   	@Override
		   	public void onEvent(Event event) {
		   		Russel.screen.loadScreen(new EditScreen(ash.getSelected()), true);
		   	}
	    });
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