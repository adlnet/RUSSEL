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

import com.eduworks.gwt.client.net.callback.ESBCallback;
import com.eduworks.gwt.client.net.packet.ESBPacket;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;

/**
 * Russel
 * Extends Constants
 * Implements EntryPoint and ValueChangeHandler
 * 
 * @author Eduworks Corporation
 */
public class Russel extends Constants implements EntryPoint, ValueChangeHandler<String>
{
	private static String detailId = null;
	//	public static EventCallback nonFunctional = new EventCallback() {
//													@Override
//													public void onEvent(Event event) {
//														Window.alert(Constants.INCOMPLETE_FEATURE_MESSAGE);
//													}
//												};
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
		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
			@Override
			public void onUncaughtException(Throwable e) {
				Window.alert("onUncaughtException errors");
				Window.alert(e.toString());
				Window.alert(e.getCause().getMessage());
				e.printStackTrace();
			}
		});
		
		view.clearHistory();
		
		History.addValueChangeHandler(this);
				
		Window.addWindowClosingHandler(new ClosingHandler() {
											@Override
											public void onWindowClosing(ClosingEvent event) {
												//event.setMessage("You are leaving the RUSSEL interface. Do you wish to continue?");
											}
										});
		
		detailId = Window.Location.getParameter("id");
		fetchProperties(new ESBCallback<ESBPacket>() {
							@Override
							public void onSuccess(ESBPacket ESBPacket)
							{
								PageAssembler.setBuildNumber(ESBPacket.getString("contentStream").substring(
										ESBPacket.getString("contentStream").lastIndexOf("=") + 1));
								view.setDefaultScreen(defaultScreen);
								History.fireCurrentHistoryState();
							}
				
							@Override
							public void onFailure(Throwable caught)
							{
								Window.alert("Couldn't find build number");
							}
						});
	}


	/**
	 * onValueChange Loads a new event into history.
	 */
	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		view.loadHistoryScreen(event.getValue());
	}
}