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
import com.eduworks.gwt.client.net.api.ESBApi;
import com.eduworks.gwt.client.net.callback.ESBCallback;
import com.eduworks.gwt.client.net.callback.EventCallback;
import com.eduworks.gwt.client.net.packet.ESBPacket;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.gwt.client.pagebuilder.ScreenTemplate;
import com.eduworks.russel.ui.client.pagebuilder.HtmlTemplates;
import com.eduworks.russel.ui.client.pagebuilder.screen.LoginScreen;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Constants Defines constants for RUSSEL
 * 
 * @author Eduworks Corporation
 */
public abstract class Constants
{
	public static ScreenDispatch	view						= new ScreenDispatch();
	public static HtmlTemplates 	templates 					= GWT.create(HtmlTemplates.class);
	public static Utilities			util						= new Utilities();
	public static EventHandlers		handlers					= new EventHandlers();
	public static ScreenTemplate    defaultScreen               = new LoginScreen();
		
	public static int				DEFAULT_TIMEOUT				= 60000;
	public static int				WEIGHT_MULTIPLIER_WIDTH		= 100;
	public static int				WEIGHT_MULTIPLIER_HEIGHT	= 100;
	public static int				TIMEOUT						= 20000;
	public static int				RUSSEL_RESULT_MAX_WEIGHT	= 5;
	public static int				RUSSEL_RESULT_SIZE_DIVISOR	= 20;
	
	public static String			INCOMPLETE_FEATURE_MESSAGE	= "The function you are attempting to use is not implemented.";
	public static String			UNSUPPORTED_IE_FEATURE		= "The function you are trying to use is not available in Internet Explorer 7/8.";
	public static String			FOUO						= "For Official Use Only (FOUO)";
	public static String			SESSION_EXPIRED				= "Your RUSSEL session has expired. Please login again.";
	public static String			HTML_MIME					= "text/html";
	public static String			XML_MIME					= "application/xml";
	public static String			DTD_MIME					= "application/xml-dtd";
	
	public static String			siteName;
	public static String			helpURL;
	
	public static Timer				loginCheck					= new Timer()
																{
																	@Override
																	public void run()
																	{
																		ESBApi.validateSession(new ESBCallback<ESBPacket>()
																									{
																										@Override
																										public void onSuccess(ESBPacket ESBPacket) {}
					
																										@Override
																										public void onFailure(Throwable caught)
																										{
																											logout();
																										}
					
																									});
																	}
																};

	/**
	 * roundNumber Rounds number "num" to the number of "places" specified
	 * 
	 * @param num
	 * @param places
	 * @return rounded result
	 */
	public static void logout()
	{
		loginCheck.cancel();
		view.clearHistory();
		view.loadLoginScreen();
		final Element oldErrorDialog = (Element) Document.get().getElementById("errorDialog");
		if (oldErrorDialog != null)
			oldErrorDialog.removeFromParent();

		final HTML errorDialog = new HTML(templates.getErrorWidget().getText());
		RootPanel.get("errorContainer").add(errorDialog);
		((Label) PageAssembler.elementToWidget("errorMessage", PageAssembler.LABEL)).setText(SESSION_EXPIRED);
		PageAssembler.attachHandler("errorClose", Event.ONMOUSEUP, new EventCallback()
		{
			@Override
			public void onEvent(Event event)
			{
				errorDialog.removeFromParent();
			}
		});
	}

	public static void fetchProperties(final ESBCallback<ESBPacket> callback)
	{
		CommunicationHub.sendHTTP(CommunicationHub.GET, "../js/installation.settings", null, false,
				new ESBCallback<ESBPacket>()
				{
					public void onSuccess(ESBPacket ESBPacket)
					{
						String[] rawProperties = ESBPacket.getString("contentStream").split("\r\n|\r|\n");

						// Parsing of installation.properties and
						// module.properties is reserved for the entry point
						// application.
						// The following checks to verify that property
						// "site.name" is set to RUSSEL as the entry point.
						if ((rawProperties[0].indexOf("site.name") != -1))
						{

							for (int propertyIndex = 0; propertyIndex < rawProperties.length; propertyIndex++)
							{
								if (rawProperties[propertyIndex].indexOf("root.url") != -1)
								{
									CommunicationHub.rootURL = rawProperties[propertyIndex].substring(
											rawProperties[propertyIndex].indexOf("\"") + 1,
											rawProperties[propertyIndex].lastIndexOf("\""));
									//Logger.logInfo("Root URL at: " + CommunicationHub.rootURL);
								}
								else if (rawProperties[propertyIndex].indexOf("site.url") != -1)
								{
									CommunicationHub.siteURL = rawProperties[propertyIndex].substring(
											rawProperties[propertyIndex].indexOf("\"") + 1,
											rawProperties[propertyIndex].lastIndexOf("\""));
									//Logger.logInfo("Site URL at: " + CommunicationHub.siteURL);
								}
								else if (rawProperties[propertyIndex].indexOf("alfresco.url") != -1)
								{
									CommunicationHub.baseURL = rawProperties[propertyIndex].substring(
											rawProperties[propertyIndex].indexOf("\"") + 1,
											rawProperties[propertyIndex].lastIndexOf("\""));
									//Logger.logInfo("Alfresco URL at: " + CommunicationHub.baseURL);
								}
								else if (rawProperties[propertyIndex].indexOf("help.url") != -1)
								{
									helpURL = rawProperties[propertyIndex].substring(
											rawProperties[propertyIndex].indexOf("\"") + 1,
											rawProperties[propertyIndex].lastIndexOf("\""));
									PageAssembler.setHelp(helpURL);
									//Logger.logInfo("Help URL at: " + helpURL);
								}
								else if (rawProperties[propertyIndex].indexOf("site.name") != -1)
								{
									siteName = rawProperties[propertyIndex].substring(
											rawProperties[propertyIndex].indexOf("\"") + 1,
											rawProperties[propertyIndex].lastIndexOf("\""));
									PageAssembler.setSiteName(siteName);
									//Logger.logInfo("Site name: " + siteName);
								}
							}

							CommunicationHub.sendHTTP(CommunicationHub.GET, "../js/module.properties", null, false, callback);
						}
					}

					@Override
					public void onFailure(Throwable caught)
					{
						Window.alert("Couldn't find network settings");
					}
				});
	}
}