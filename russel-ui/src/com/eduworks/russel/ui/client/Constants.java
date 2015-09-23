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

import com.eduworks.gwt.client.component.AppSettings;
import com.eduworks.gwt.client.net.callback.ESBCallback;
import com.eduworks.gwt.client.net.callback.EventCallback;
import com.eduworks.gwt.client.net.packet.ESBPacket;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.russel.ui.client.net.RusselApi;
import com.eduworks.russel.ui.client.pagebuilder.HtmlTemplates;
import com.eduworks.russel.ui.client.pagebuilder.screen.LoginScreen;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Constants Defines constants for RUSSEL
 * 
 * @author Eduworks Corporation
 */
public class Constants
{	
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
	
	public static Timer				loggedInCheck			= new Timer()
																{
																	@Override
																	public void run()
																	{
																		RusselApi.validateSession(new ESBCallback<ESBPacket>()
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
		loggedInCheck.cancel();
		AppSettings.dispatcher.clearHistory();
		Russel.screen.loadScreen(new LoginScreen(), true);
		final Element oldErrorDialog = (Element) Document.get().getElementById("errorDialog");
		if (oldErrorDialog != null)
			oldErrorDialog.removeFromParent();

		final HTML errorDialog = new HTML(((HtmlTemplates)AppSettings.templates).getErrorWidget().getText());
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
}