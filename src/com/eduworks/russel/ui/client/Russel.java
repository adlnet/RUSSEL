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

import com.eduworks.gwt.client.net.CommunicationHub;
import com.eduworks.gwt.client.net.api.AlfrescoApi;
import com.eduworks.gwt.client.net.callback.AlfrescoCallback;
import com.eduworks.gwt.client.net.callback.EventCallback;
import com.eduworks.gwt.client.net.packet.AlfrescoPacket;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.gwt.client.pagebuilder.ScreenDispatch;
import com.eduworks.russel.ui.client.handler.StatusWindowHandler;
import com.eduworks.russel.ui.client.pagebuilder.HtmlTemplates;
import com.eduworks.russel.ui.client.pagebuilder.screen.LoginScreen;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

public class Russel extends Constants implements EntryPoint, ValueChangeHandler<String>
{
	public static final String SESSION_EXPIRED = "Your session has expired. Please login again.";
	private static String detailId = null;
	public static ScreenDispatch view = new ScreenDispatch();
	public static String helpURL; 
	public static Timer loginCheck = new Timer() {
		@Override
		public void run() {
			AlfrescoApi.validateTicket(new AlfrescoCallback<AlfrescoPacket>() {
				@Override
				public void onSuccess(AlfrescoPacket alfrescoPacket) {}
				
				@Override
				public void onFailure(Throwable caught) {
					loginCheck.cancel();
					view.clearHistory();
					view.loadScreen(new LoginScreen(), true);
					final Element oldErrorDialog = (Element)Document.get().getElementById("errorDialog");
					if (oldErrorDialog != null) oldErrorDialog.removeFromParent();

					final HTML errorDialog = new HTML(HtmlTemplates.INSTANCE.getErrorWidget().getText());
					RootPanel.get("errorContainer").add(errorDialog);
					((Label)PageAssembler.elementToWidget("errorMessage", PageAssembler.LABEL)).setText(SESSION_EXPIRED);
					PageAssembler.attachHandler("errorClose", Event.ONMOUSEUP, new EventCallback() {
																					@Override
																					public void onEvent(Event event) {
																							errorDialog.removeFromParent();
																					}
																				});
				}
			});
		}
	}; 
	
//	public static EventCallback nonFunctional = new EventCallback() {
//													@Override
//													public void onEvent(Event event) {
//														Window.alert(Constants.INCOMPLETE_FEATURE_MESSAGE);
//													}
//												};
	
	public static String getDetailId() {
		String temp = detailId;
		detailId = null;
		return temp;
	}
	
												
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
		CommunicationHub.sendHTTP(CommunicationHub.GET,
								  "../js/installation.settings", 
								  null, 
								  false, 
								  new AlfrescoCallback<AlfrescoPacket>() {
								  	@Override
								  	public void onSuccess(AlfrescoPacket alfrescoPacket) {
								  		String[] rawProperties = alfrescoPacket.getRawString().split("\r\n");
								  		for (int propertyIndex=0;propertyIndex<rawProperties.length;propertyIndex++) {
								  			if (rawProperties[propertyIndex].indexOf("root.url")!=-1) {
								  				CommunicationHub.rootURL = rawProperties[propertyIndex].substring(rawProperties[propertyIndex].indexOf("\"")+1, rawProperties[propertyIndex].lastIndexOf("\""));
								  				CommunicationHub.baseURL = CommunicationHub.rootURL + CommunicationHub.baseURL;
								  			} else if (rawProperties[propertyIndex].indexOf("site.url")!=-1)
								  				CommunicationHub.siteURL = rawProperties[propertyIndex].substring(rawProperties[propertyIndex].indexOf("\"")+1, rawProperties[propertyIndex].lastIndexOf("\""));
								  			else if (rawProperties[propertyIndex].indexOf("help.url")!=-1)
								  				helpURL = rawProperties[propertyIndex].substring(rawProperties[propertyIndex].indexOf("\"")+1, rawProperties[propertyIndex].lastIndexOf("\""));
								  				PageAssembler.setHelp(helpURL);
								  		}
								  		
								  		CommunicationHub.sendHTTP(CommunicationHub.GET, 
												  "../js/module.properties", 
												  null, 
												  false,
												  new AlfrescoCallback<AlfrescoPacket>() {
													@Override
													public void onSuccess(AlfrescoPacket alfrescoPacket) {
														PageAssembler.setBuildNumber(alfrescoPacket.getRawString().substring(alfrescoPacket.getRawString().lastIndexOf("=")+1));
														view.setDefaultScreen(new LoginScreen());
														History.fireCurrentHistoryState();
													}
													
													@Override
													public void onFailure(Throwable caught) {
														Window.alert("Couldn't find build number");
													}
												});
								  	}
								  	
								  	@Override
								  	public void onFailure(Throwable caught) {
								  		Window.alert("Couldn't find network settings");
								  	}
								  });
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		view.loadHistoryScreen(event.getValue());
	}
}