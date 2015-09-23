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
import com.eduworks.gwt.client.net.callback.ESBCallback;
import com.eduworks.gwt.client.net.callback.EventCallback;
import com.eduworks.gwt.client.net.packet.ESBPacket;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.gwt.client.pagebuilder.modal.ModalDispatch;
import com.eduworks.gwt.client.pagebuilder.overlay.OverlayDispatch;
import com.eduworks.gwt.client.pagebuilder.screen.ScreenDispatch;
import com.eduworks.gwt.client.pagebuilder.screen.ScreenTemplate;
import com.eduworks.russel.ui.client.Constants;
import com.eduworks.russel.ui.client.Russel;
import com.eduworks.russel.ui.client.handler.StatusHandler;
import com.eduworks.russel.ui.client.net.RusselApi;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * LoginScreen class
 * Extends ScreenTemplate
 * Defines methods and handlers for the Login screen.
 * 
 * @author Eduworks Corporation
 */
public class LoginScreen extends ScreenTemplate {
	private final String LOGIN_BAD_LOGIN = "Login name or password is not valid.";
	private final String SERVER_UNAVAILABLE = "The server is unavailable."; 

	/**
	 * lostFocus In place to handle any processing requirements required when this screen loses focus.
	 * Called by ScreenDispatch for all RUSSEL screens.
	 */
	public void lostFocus() {
 	}
	
	/**
	 * loginListener Processes the username and password entered on the login screen.
	 */
	protected EventCallback loginListener = new EventCallback() {
		@Override
		public void onEvent(Event event) {
			if (event.getTypeInt() == Event.ONCLICK || event.getKeyCode() == KeyCodes.KEY_ENTER) {
				final String loginName = ((TextBox)PageAssembler.elementToWidget("loginName", PageAssembler.TEXT)).getText();
				if (loginName.equalsIgnoreCase("guest")) {
					final Element oldErrorDialog = (Element)Document.get().getElementById("errorDialog");
					if (oldErrorDialog != null) oldErrorDialog.removeFromParent();
					final HTML errorDialog = new HTML(Russel.htmlTemplates.getErrorWidget().getText());
					RootPanel.get("errorContainer").add(errorDialog);
					enableLogin0(true);
					((Label)PageAssembler.elementToWidget("errorMessage", PageAssembler.LABEL)).setText(LOGIN_BAD_LOGIN);
					PageAssembler.attachHandler("errorClose", Event.ONMOUSEUP, new EventCallback() {
																					@Override
																					public void onEvent(Event event) {
																							errorDialog.removeFromParent();
																					}
																				});
				}
				else {
					enableLogin0(false);
					RusselApi.login(loginName,
									  ((PasswordTextBox)PageAssembler.elementToWidget("loginPassword", PageAssembler.PASSWORD)).getText(),
									  new ESBCallback<ESBPacket>() {
										@Override
										public void onSuccess(ESBPacket result) {
											if (result.getPayloadString()==null)
												onFailure(new Throwable(LOGIN_BAD_LOGIN));
											else
											{
												RusselApi.sessionId = result.getPayloadString();
												RusselApi.username = loginName;
												PageAssembler.setTemplate(Russel.htmlTemplates.getHeader().getText(),
																	Russel.htmlTemplates.getFooter().getText(),
																		  "contentPane");
												prepTemplateHooks0();
												Constants.loggedInCheck.scheduleRepeating(1800000);
												StatusHandler.initialize();
												String tempDetailId = Russel.getDetailId();
												if (tempDetailId == null)
													Russel.screen.loadScreen(new HomeScreen(), true);
												else
													Russel.screen.loadScreen(new DetailScreen(tempDetailId), true);
											}
										}
			
										@Override
										public void onFailure(Throwable caught) {
											final Element oldErrorDialog = (Element)Document.get().getElementById("errorDialog");
											if (oldErrorDialog != null) oldErrorDialog.removeFromParent();

											final HTML errorDialog = new HTML(Russel.htmlTemplates.getErrorWidget().getText());
											RootPanel.get("errorContainer").add(errorDialog);
											enableLogin0(true);
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
		}
	};

	/**
	 * enableLogin Sets the state of the login form.
	 * @param s Enables the login form if set to true, disables it if set to false.
	 */
	private void enableLogin0(boolean s) {
		((TextBox)PageAssembler.elementToWidget("loginName", PageAssembler.TEXT)).setEnabled(s);
		((PasswordTextBox)PageAssembler.elementToWidget("loginPassword",  PageAssembler.PASSWORD)).setEnabled(s);
		((Anchor)PageAssembler.elementToWidget("loginButton",  PageAssembler.A)).setEnabled(s);
	}

	private void loggingOut() {
		Constants.loggedInCheck.cancel();
		Russel.screen.clearHistory();
		Russel.screen.loadScreen(new LoginScreen(), true);
	}
	
	/**
	 * prepTemplateHooks Performs setup for LoginScreen template event handlers  
	 */
	private void prepTemplateHooks0() {
		PageAssembler.attachHandler("r-menuLogout", Event.ONCLICK, new EventCallback() {
																       @Override
																       public void onEvent(Event event) {
																    	   RusselApi.logout(new ESBCallback<ESBPacket>() {
																								@Override
																								public void onSuccess(ESBPacket result) {
																									loggingOut();
																								}
																								
																								@Override
																								public void onFailure(Throwable caught) {
																									loggingOut();
																								}
																							  });
																		}
																	});
		PageAssembler.attachHandler("r-menuWorkspace", Event.ONCLICK, new EventCallback() {
																		@Override
																		public void onEvent(Event event) {
																			Russel.screen.loadScreen(new HomeScreen(), true);
																		}
																	 });
		
		PageAssembler.attachHandler("r-menuCollections", Event.ONCLICK, new EventCallback() {
																		@Override
																		public void onEvent(Event event) {
																			Russel.screen.loadScreen(new FeatureScreen(FeatureScreen.COLLECTIONS_TYPE), true);
																		}
																	 });

		PageAssembler.attachHandler("r-menuProjects", Event.ONCLICK, new EventCallback() {
																		@Override
																		public void onEvent(Event event) {
																			Russel.screen.loadScreen(new FeatureScreen(FeatureScreen.PROJECTS_TYPE), true);
																		}
																	 });
	}

	/**
	 * display Renders the Login screen.
	 */
	public void display()
	{
		PageAssembler.setTemplate("", "", "contentPane");
		PageAssembler.ready(new HTML(Russel.htmlTemplates.getLoginWidget().getText()));
		PageAssembler.buildContents();
		
		PageAssembler.attachHandler("loginButton", Event.ONCLICK, loginListener);
		PageAssembler.attachHandler("loginPassword", Event.ONKEYUP, loginListener);
		
		((TextBox)PageAssembler.elementToWidget("loginName", PageAssembler.TEXT)).setFocus(true);
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


