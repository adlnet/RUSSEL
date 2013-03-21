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
import com.eduworks.gwt.client.pagebuilder.ScreenTemplate;
import com.eduworks.russel.ui.client.Russel;
import com.eduworks.russel.ui.client.handler.AlfrescoSearchHandler;
import com.eduworks.russel.ui.client.pagebuilder.HtmlTemplates;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public class HomeScreen extends ScreenTemplate {

	private AlfrescoSearchHandler ash;  // The prior definition was "final" -- test to see if this breaks
	public void lostFocus() {
		ash.stop();
	}
	
	public void display() {
		((Label)PageAssembler.elementToWidget("r-menuUserName", PageAssembler.LABEL)).setText(AlfrescoApi.username);

		PageAssembler.ready(new HTML(HtmlTemplates.INSTANCE.getMenuBar().getText()));
		PageAssembler.ready(new HTML(HtmlTemplates.INSTANCE.getObjectPanel().getText()));
		PageAssembler.buildContents();
		
		DOM.getElementById("r-menuWorkspace").getParentElement().addClassName("active");
		DOM.getElementById("r-menuCollections").getParentElement().removeClassName("active");
		DOM.getElementById("r-menuProjects").getParentElement().removeClassName("active");

		ash = new AlfrescoSearchHandler();
		
		PageAssembler.attachHandler("r-uploadContentTile", Event.ONCLICK, new EventCallback() {
																				@Override
																				public void onEvent(Event event) {
																					Russel.view.loadScreen(new EditScreen(), true);
																				}
																			});
		
		PageAssembler.attachHandler("r-projectsTile", Event.ONCLICK, new EventCallback() {
																			@Override
																			public void onEvent(Event event) {
																				FeatureScreen fs = new FeatureScreen();
																				fs.featureType = FeatureScreen.PROJECTS_TYPE;
																				Russel.view.loadScreen(fs, true);
																			}
																	 });
		

		PageAssembler.attachHandler("r-menuProjects", Event.ONCLICK, new EventCallback() {
																		@Override
																		public void onEvent(Event event) {
																			FeatureScreen fs = new FeatureScreen();
																			fs.featureType = FeatureScreen.PROJECTS_TYPE;
																			Russel.view.loadScreen(fs, true);
																		}
																	 });
		
		PageAssembler.attachHandler("r-menuCollections", Event.ONCLICK, new EventCallback() {
																		@Override
																		public void onEvent(Event event) {
																			FeatureScreen fs = new FeatureScreen();
																			fs.featureType = FeatureScreen.COLLECTIONS_TYPE;
																			Russel.view.loadScreen(fs, true);
																		}
																	 });

		PageAssembler.attachHandler("r-collectionsTile", Event.ONCLICK, new EventCallback() {
																			@Override
																			public void onEvent(Event event) {
																				FeatureScreen fs = new FeatureScreen();
																				fs.featureType = FeatureScreen.COLLECTIONS_TYPE;
																				Russel.view.loadScreen(fs, true);
																			}
																		 });
		
		PageAssembler.attachHandler("r-accountSettingsTile", Event.ONCLICK, new EventCallback() {
																			@Override
																			public void onEvent(Event event) {
																				UtilityScreen us = new UtilityScreen();
																				us.utilType = UtilityScreen.ACCOUNT_TYPE;
																				Russel.view.loadScreen(us, true);
																			}
																		 });	
		
		PageAssembler.attachHandler("r-manageUsersTile", Event.ONCLICK, new EventCallback() {
																			@Override
																			public void onEvent(Event event) {
																				UtilityScreen us = new UtilityScreen();
																				us.utilType = UtilityScreen.USERS_TYPE;
																				Russel.view.loadScreen(us, true);
																			}
																		 });
						
		PageAssembler.attachHandler("r-groupTile", Event.ONCLICK, new EventCallback() {
																			@Override
																			public void onEvent(Event event) {
																				UtilityScreen us = new UtilityScreen();
																				us.utilType = UtilityScreen.GROUPS_TYPE;
																				Russel.view.loadScreen(us, true);
																			}
																		 });
		
		PageAssembler.attachHandler("r-repositorySettingsTile", Event.ONCLICK, new EventCallback() {
																			@Override
																			public void onEvent(Event event) {
																				UtilityScreen us = new UtilityScreen();
																				us.utilType = UtilityScreen.REPSETTINGS_TYPE;
																				Russel.view.loadScreen(us, true);
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
		//PageAssembler.attachHandler("notebook", Event.ONCLICK, Russel.nonFunctional);

		((TextBox)PageAssembler.elementToWidget("r-menuSearchBar", PageAssembler.TEXT)).setFocus(true);
		
		ash.hook("r-menuSearchBar", "searchObjectPanel", AlfrescoSearchHandler.RECENT_TYPE);
	}
}