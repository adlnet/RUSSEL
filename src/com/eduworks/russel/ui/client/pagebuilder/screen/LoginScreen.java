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
import com.eduworks.gwt.client.net.callback.AlfrescoCallback;
import com.eduworks.gwt.client.net.callback.EventCallback;
import com.eduworks.gwt.client.net.packet.AlfrescoPacket;
import com.eduworks.gwt.client.net.packet.StatusPacket;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.gwt.client.pagebuilder.ScreenTemplate;
import com.eduworks.gwt.client.util.Browser;
import com.eduworks.russel.ui.client.Constants;
import com.eduworks.russel.ui.client.Russel;
import com.eduworks.russel.ui.client.handler.StatusWindowHandler;
import com.eduworks.russel.ui.client.pagebuilder.HtmlTemplates;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;


public class LoginScreen extends ScreenTemplate {
	private final String LOGIN_BAD_LOGIN = "Login name or password is not valid.";
	private final String SERVER_UNAVAILABLE = "The server is unavailable."; 

	public void lostFocus() {
 	}
	private EventCallback loginListener = new EventCallback() {
		@Override
		public void onEvent(Event event) {
			if (event.getTypeInt() == Event.ONCLICK || event.getKeyCode() == KeyCodes.KEY_ENTER) {
				enableLogin(false);
				AlfrescoApi.login(((TextBox)PageAssembler.elementToWidget("loginName", PageAssembler.TEXT)).getText(),
								  ((PasswordTextBox)PageAssembler.elementToWidget("loginPassword", PageAssembler.PASSWORD)).getText(),
								  new AlfrescoCallback<AlfrescoPacket>() {
									@Override
									public void onSuccess(AlfrescoPacket result) {
										if (result.getTicket()==null)
											onFailure(new Throwable(LOGIN_BAD_LOGIN));
										else
										{
											AlfrescoApi.ticket = result.getTicket();
											AlfrescoApi.updateCurrentDirectory(UriUtils.sanitizeUri("Company%20Home/" + 
																								    "User%20Homes/" + 
																								    AlfrescoApi.username.toLowerCase()),
																			   new AlfrescoCallback<AlfrescoPacket>() {
																					@Override
																					public void onSuccess(AlfrescoPacket alfrescoPacket) {
																						
																					}
																					
																					@Override
																					public void onFailure(Throwable caught) {
																						StatusWindowHandler.createMessage(StatusWindowHandler.getHomeMessageError("Company Home/User Homes/" + AlfrescoApi.username.toLowerCase()), 
																														  StatusPacket.ALERT_ERROR);
																						AlfrescoApi.updateCurrentDirectory(UriUtils.sanitizeUri("Company%20Home"));
																					}
																				});
											PageAssembler.setTemplate(HtmlTemplates.INSTANCE.getHeader().getText(),
																	  HtmlTemplates.INSTANCE.getFooter().getText(),
																	  "contentPane");
											prepTemplateHooks();
											Russel.loginCheck.scheduleRepeating(1800000);
											StatusWindowHandler.initialize();
											String tempDetailId = Russel.getDetailId();
											if (tempDetailId == null)
												Russel.view.loadScreen(new HomeScreen(), true);
											else
												Russel.view.loadScreen(new DetailScreen(tempDetailId), true);
										}
									}
		
									@Override
									public void onFailure(Throwable caught) {
										final Element oldErrorDialog = (Element)Document.get().getElementById("errorDialog");
										if (oldErrorDialog != null) oldErrorDialog.removeFromParent();

										final HTML errorDialog = new HTML(HtmlTemplates.INSTANCE.getErrorWidget().getText());
										RootPanel.get("errorContainer").add(errorDialog);
										enableLogin(true);
										if (caught.getMessage().indexOf("502")!=-1||caught.getMessage().indexOf("503")!=-1||caught.getMessage().indexOf("System Error")!=-1||
											caught.getMessage().indexOf("404")!=-1)
											((Label)PageAssembler.elementToWidget("errorMessage", PageAssembler.LABEL)).setText(SERVER_UNAVAILABLE);
										else
											((Label)PageAssembler.elementToWidget("errorMessage", PageAssembler.LABEL)).setText(LOGIN_BAD_LOGIN);
										PageAssembler.attachHandler("errorClose", Event.ONMOUSEUP, new EventCallback() {
																										@Override
																										public void onEvent(Event event) {
																												errorDialog.removeFromParent();
																										}
																									});
									}
								  }
				);
			}
		}
	};

	private void enableLogin(boolean s) {
		((TextBox)PageAssembler.elementToWidget("loginName", PageAssembler.TEXT)).setEnabled(s);
		((PasswordTextBox)PageAssembler.elementToWidget("loginPassword",  PageAssembler.PASSWORD)).setEnabled(s);
		((Anchor)PageAssembler.elementToWidget("loginButton",  PageAssembler.A)).setEnabled(s);
	}

	public void display()
	{
		PageAssembler.setTemplate("", "", "contentPane");
		PageAssembler.ready(new HTML(HtmlTemplates.INSTANCE.getLoginWidget().getText()));
		PageAssembler.buildContents();
		
		PageAssembler.attachHandler("loginButton", Event.ONCLICK, loginListener);
		PageAssembler.attachHandler("loginPassword", Event.ONKEYUP, loginListener);
//		PageAssembler.attachHandler("ForgotButton", Event.ONCLICK, Russel.nonFunctional);
//		PageAssembler.attachHandler("RequestButton", Event.ONCLICK, Russel.nonFunctional);
//		PageAssembler.attachHandler("rememberMe", Event.ONCLICK, Russel.nonFunctional);
		
		((TextBox)PageAssembler.elementToWidget("loginName", PageAssembler.TEXT)).setFocus(true);
	}
	
	/* Setup for template event handlers */
	private void prepTemplateHooks() {
		PageAssembler.attachHandler("r-menuLogout", Event.ONCLICK, new EventCallback() {
																       @Override
																       public void onEvent(Event event) {
																    	   AlfrescoApi.logout(new AlfrescoCallback<AlfrescoPacket>() {
																								@Override
																								public void onSuccess(AlfrescoPacket result) {
																									Russel.loginCheck.cancel();
																									Russel.view.clearHistory();
																									Russel.view.loadScreen(new LoginScreen(), true);
																								}
																								
																								@Override
																								public void onFailure(Throwable caught) {
																									Russel.loginCheck.cancel();
																									Russel.view.clearHistory();
																									Russel.view.loadScreen(new LoginScreen(), true);
																								}
																							  });
																		}
																	});
		PageAssembler.attachHandler("r-menuWorkspace", Event.ONCLICK, new EventCallback() {
																		@Override
																		public void onEvent(Event event) {
																			Russel.view.loadScreen(new HomeScreen(), true);
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

		PageAssembler.attachHandler("r-menuProjects", Event.ONCLICK, new EventCallback() {
																		@Override
																		public void onEvent(Event event) {
																			FeatureScreen fs = new FeatureScreen();
																			fs.featureType = FeatureScreen.PROJECTS_TYPE;
																			Russel.view.loadScreen(fs, true);															}
																	 });
	}
}
