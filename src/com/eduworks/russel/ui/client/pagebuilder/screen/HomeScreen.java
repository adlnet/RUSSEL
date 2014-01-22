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

import com.eduworks.gwt.client.net.api.AlfrescoApi;
import com.eduworks.gwt.client.net.callback.EventCallback;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.russel.ui.client.handler.AlfrescoSearchHandler;
import com.eduworks.russel.ui.client.handler.SearchHandler;
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
public class HomeScreen extends Screen {

	protected static SearchHandler ash;  // The prior definition was "final" -- test to see if this breaks

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
		((Label)PageAssembler.elementToWidget("r-menuUserName", PageAssembler.LABEL)).setText(AlfrescoApi.username);

		PageAssembler.ready(new HTML(templates().getMenuBar().getText()));
		PageAssembler.ready(new HTML(templates().getObjectPanel().getText()));
		PageAssembler.buildContents();
		
		DOM.getElementById("r-menuWorkspace").getParentElement().addClassName("active");
		DOM.getElementById("r-menuCollections").getParentElement().removeClassName("active");
		DOM.getElementById("r-menuProjects").getParentElement().removeClassName("active");

		ash = new AlfrescoSearchHandler();
		
		PageAssembler.attachHandler("r-uploadContentTile", Event.ONCLICK, new EventCallback() {
																				@Override
																				public void onEvent(Event event) {
																					view().loadEditScreen();
																				}
																			});
		
		PageAssembler.attachHandler("r-projectsTile", Event.ONCLICK, new EventCallback() {
																			@Override
																			public void onEvent(Event event) {
																				view().loadFeatureScreen(FeatureScreen.PROJECTS_TYPE);
																			}
																	 });
		

		PageAssembler.attachHandler("r-menuProjects", Event.ONCLICK, new EventCallback() {
																		@Override
																		public void onEvent(Event event) {
																			view().loadFeatureScreen(FeatureScreen.PROJECTS_TYPE);
																		}
																	 });
		
		PageAssembler.attachHandler("r-menuCollections", Event.ONCLICK, new EventCallback() {
																		@Override
																		public void onEvent(Event event) {
																			view().loadFeatureScreen(FeatureScreen.COLLECTIONS_TYPE);
																		}
																	 });

		PageAssembler.attachHandler("r-collectionsTile", Event.ONCLICK, new EventCallback() {
																			@Override
																			public void onEvent(Event event) {
																				view().loadFeatureScreen(FeatureScreen.COLLECTIONS_TYPE);
																			}
																		 });
		
		PageAssembler.attachHandler("r-accountSettingsTile", Event.ONCLICK, new EventCallback() {
																			@Override
																			public void onEvent(Event event) {
																				view().loadUtilityScreen(UtilityScreen.ACCOUNT_TYPE);
																			}
																		 });	
		
		PageAssembler.attachHandler("r-manageUsersTile", Event.ONCLICK, new EventCallback() {
																			@Override
																			public void onEvent(Event event) {
																				view().loadUtilityScreen(UtilityScreen.USERS_TYPE);
																			}
																		 });
						
		PageAssembler.attachHandler("r-groupTile", Event.ONCLICK, new EventCallback() {
																			@Override
																			public void onEvent(Event event) {
																				view().loadUtilityScreen(UtilityScreen.GROUPS_TYPE);
																			}
																		 });
		
		PageAssembler.attachHandler("r-repositorySettingsTile", Event.ONCLICK, new EventCallback() {
																			@Override
																			public void onEvent(Event event) {
																				view().loadUtilityScreen(UtilityScreen.REPSETTINGS_TYPE);
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
		
//		PageAssembler.attachHandler("r-taxonomiesTile", Event.ONCLICK, Russel.nonFunctional);
//		PageAssembler.attachHandler("notebook", Event.ONCLICK, Russel.nonFunctional);

		((TextBox)PageAssembler.elementToWidget("r-menuSearchBar", PageAssembler.TEXT)).setFocus(true);
		
		ash.hook("r-menuSearchBar", "searchObjectPanel", AlfrescoSearchHandler.RECENT_TYPE);
	}
}