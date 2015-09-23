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

package com.eduworks.russel.ui.client;

import com.eduworks.gwt.client.component.AppEntry;
import com.eduworks.gwt.client.net.CommunicationHub;
import com.eduworks.gwt.client.pagebuilder.screen.ScreenDispatch;
import com.eduworks.gwt.client.pagebuilder.screen.ScreenTemplate;
import com.eduworks.russel.ui.client.handler.FileHandler;
import com.eduworks.russel.ui.client.net.RusselApi;
import com.eduworks.russel.ui.client.pagebuilder.EpssTemplates;
import com.eduworks.russel.ui.client.pagebuilder.HtmlTemplates;
import com.eduworks.russel.ui.client.pagebuilder.screen.LoginScreen;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Window;

/**
 * Russel
 * Extends Constants
 * Implements EntryPoint and ValueChangeHandler
 * 
 * @author Eduworks Corporation
 */
public class Russel extends AppEntry
{
	public boolean silent;
	
	private static String detailId = null;
	public static FileHandler files = new FileHandler();
	public static ScreenDispatch screen = new ScreenDispatch();
	public static HtmlTemplates htmlTemplates = GWT.create(HtmlTemplates.class);
	public static EpssTemplates epssTemplates = GWT.create(EpssTemplates.class);

	/**
	 * getDetailId  returns the value of private static detailId
	 * @return string
	 */
	public static String getDetailId() {
		String temp = detailId;
		detailId = null;
		return temp;
	}
	
		
	/**
	 *  onModuleLoad Initializes handlers, data, and history for the RUSSEL module.
	 *  This module reads in the contents of "../js/installation.settings" to configure the RUSSEl instance.
	 */
	@Override
	public void onModuleLoad()
	{
		silent = true;
		detailId = Window.Location.getParameter("id");
		defaultScreen = new LoginScreen();
		screen.loadScreen(new LoginScreen(), true);
	}
}