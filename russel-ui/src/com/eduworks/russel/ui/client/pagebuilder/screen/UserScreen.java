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
import com.eduworks.gwt.client.model.StatusRecord;
import com.eduworks.gwt.client.net.callback.ESBCallback;
import com.eduworks.gwt.client.net.callback.EventCallback;
import com.eduworks.gwt.client.net.packet.ESBPacket;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.gwt.client.pagebuilder.modal.ModalDispatch;
import com.eduworks.gwt.client.pagebuilder.overlay.OverlayDispatch;
import com.eduworks.gwt.client.pagebuilder.screen.ScreenDispatch;
import com.eduworks.gwt.client.pagebuilder.screen.ScreenTemplate;
import com.eduworks.russel.ui.client.Russel;
import com.eduworks.russel.ui.client.handler.StatusHandler;
import com.eduworks.russel.ui.client.net.RusselApi;
import com.google.gwt.dom.client.Node;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

/**
 * UtilityScreen class
 * Extends ScreenTemplate
 * Defines methods and handlers for the Utility screen.
 * 
 * @author Eduworks Corporation
 */
public class UserScreen extends ScreenTemplate {
	
	private static String CREATE = "create";
	private static String RESET = "reset";
	
	/**
	 * lostFocus In place to handle any processing requirements required when this screen loses focus.
	 * Called by ScreenDispatch for all RUSSEL screens.
	 */
	public void lostFocus() {}
	
	/**
	 * display Renders the Utility screen using appropriate templates and sets up handlers
	 */
	public void display() {
		PageAssembler.ready(new HTML(Russel.htmlTemplates.getUserManagementPanel().getText()));	
		PageAssembler.buildContents();
		
		DOM.getElementById("r-menuCollections").getParentElement().removeClassName("active");
		DOM.getElementById("r-menuProjects").getParentElement().removeClassName("active");
		DOM.getElementById("r-menuWorkspace").getParentElement().removeClassName("active");
		
		PageAssembler.attachHandler("userGroups", Event.ONCLICK, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				Russel.screen.loadScreen(new GroupScreen(), true);
			}
		});
		
		PageAssembler.attachHandler("userPermissions", Event.ONCLICK, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				Russel.screen.loadScreen(new PermissionScreen(PermissionScreen.TYPE_USER, DOM.getElementById("userList").getPropertyString("value")), false);
			}
		});
		
		RusselApi.getUsers(new ESBCallback<ESBPacket>() {
			@Override
			public void onSuccess(ESBPacket esbPacket) {
				JSONArray jsonArray = esbPacket.getArray("obj");
				for (int i = 0; i < jsonArray.size(); i++) {
					String username = jsonArray.get(i).isString().stringValue();
					makeOption(username, "userList");
				}		
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
		
		PageAssembler.attachHandler("userCreate", 
									Event.ONCLICK, 
									new EventCallback() {
										@Override
										public void onEvent(Event event) {
											openPopup(CREATE);
											((TextBox)PageAssembler.elementToWidget("newUsername", PageAssembler.TEXT)).setText("");
											((PasswordTextBox)PageAssembler.elementToWidget("newPassword", PageAssembler.PASSWORD)).setText("");
											((PasswordTextBox)PageAssembler.elementToWidget("newPasswordConfirmation", PageAssembler.PASSWORD)).setText("");
											
											PageAssembler.attachHandler("modalNewUserCreate",
																	 	Event.ONCLICK,
																	 	new EventCallback() {
																			@Override
																			public void onEvent(Event event) {
																				String username = ((TextBox)PageAssembler.elementToWidget("newUsername", PageAssembler.TEXT)).getText().trim();
																				if (matchingPasswords("newPassword", "newPasswordConfirmation")&&username!="") { 
																					String password = ((PasswordTextBox)PageAssembler.elementToWidget("newPassword", PageAssembler.PASSWORD)).getText(); 
																					createUser(username, password);
																				}
																			}
																		});
											
											PageAssembler.attachHandler("modalNewUserCancel",
																	 	Event.ONCLICK,
																	 	new EventCallback() {
																			@Override
																			public void onEvent(Event event) {
																				PageAssembler.closePopup("createUserModal");
																			}
																		});
										}
									});
		

		PageAssembler.attachHandler("userPasswordReset", 
									Event.ONCLICK, 
									new EventCallback() {
										@Override
										public void onEvent(Event event) {
											openPopup(RESET);
											((PasswordTextBox)PageAssembler.elementToWidget("resetPassword", PageAssembler.PASSWORD)).setText("");
											((PasswordTextBox)PageAssembler.elementToWidget("resetPasswordConfirmation", PageAssembler.PASSWORD)).setText("");
											
											PageAssembler.attachHandler("modalResetPasswordAccept",
																	 	Event.ONCLICK,
																	 	new EventCallback() {
																			@Override
																			public void onEvent(Event event) {
																				if (matchingPasswords("resetPassword", "resetPasswordConfirmation")) {
																					String password = ((PasswordTextBox)PageAssembler.elementToWidget("resetPassword", PageAssembler.PASSWORD)).getText(); 
																					resetPassword(DOM.getElementById("userList").getPropertyString("value"), password);
																				}
																			}
																		});
						
											PageAssembler.attachHandler("modalResetPasswordCancel",
																	 	Event.ONCLICK,
																	 	new EventCallback() {
																			@Override
																			public void onEvent(Event event) {
																				PageAssembler.closePopup("resetPasswordModal");
																			}
																		});
										}
									});		

		PageAssembler.attachHandler("userDelete", 
									Event.ONCLICK, 
									new EventCallback() {
										@Override
										public void onEvent(Event event) {
											deleteUser(DOM.getElementById("userList").getPropertyString("value"));
										}
									});
	}
	
	private boolean matchingPasswords(String element1, String element2) {
		String password1 = ((PasswordTextBox)PageAssembler.elementToWidget(element1, PageAssembler.PASSWORD)).getText();
		String password2 = ((PasswordTextBox)PageAssembler.elementToWidget(element2, PageAssembler.PASSWORD)).getText();
		return password1.equals(password2);
	}
	
	private void makeOption(final String username, String eName) {
		Element e = DOM.getElementById(eName);
		Element a = DOM.createOption();
		a.setInnerText(username);
		e.appendChild(a);
	}
	
	private void openPopup(String type) {
		if (type.equals(RESET)) {
			PageAssembler.openPopup("resetPasswordModal");
		} else if (type.equals(CREATE)) {
			PageAssembler.openPopup("createUserModal");
		}
	}

	private void resetPassword(final String username, String password) {
		final StatusRecord sr = StatusHandler.createMessage(StatusHandler.getResetUserPasswordBusy(username), StatusRecord.STATUS_BUSY);
		RusselApi.resetUserPassword(username, 
								 password, 
								 new ESBCallback<ESBPacket>() {
									@Override
									public void onSuccess(ESBPacket esbPacket) {
										sr.setMessage(StatusHandler.getResetUserPasswordDone(username));
										sr.setState(StatusRecord.STATUS_DONE);
										StatusHandler.alterMessage(sr);
										PageAssembler.closePopup("resetPasswordModal");
									}
									
									@Override
									public void onFailure(Throwable caught) {
										sr.setMessage(StatusHandler.getResetUserPasswordError(username));
										sr.setState(StatusRecord.STATUS_ERROR);
										StatusHandler.alterMessage(sr);
									}
								 });
	}
	
	private void createUser(final String username, String password) {
		final StatusRecord sr = StatusHandler.createMessage(StatusHandler.getCreateUserBusy(username), StatusRecord.STATUS_BUSY);
		RusselApi.createUser(username, 
					         password, 
					      new ESBCallback<ESBPacket>() {
						  	@Override
						  	public void onFailure(Throwable caught) {
								sr.setMessage(StatusHandler.getCreateUserError(username));
								sr.setState(StatusRecord.STATUS_ERROR);
								StatusHandler.alterMessage(sr);
						  	}
						  	
						  	@Override
						  	public void onSuccess(ESBPacket esbPacket) {
					  			makeOption(username, "userList");
					  			sr.setMessage(StatusHandler.getCreateUserDone(username));
								sr.setState(StatusRecord.STATUS_DONE);
								StatusHandler.alterMessage(sr);
								PageAssembler.closePopup("createUserModal");
						  	}
						  });
	}
	
	private void deleteUser(final String username) {
		final StatusRecord sr = StatusHandler.createMessage(StatusHandler.getDeleteUserBusy(username), StatusRecord.STATUS_BUSY);
		RusselApi.deleteUser(username,
						  new ESBCallback<ESBPacket>() {
						  	@Override
						  	public void onFailure(Throwable caught) {
								sr.setMessage(StatusHandler.getDeleteUserError(username));
								sr.setState(StatusRecord.STATUS_ERROR);
								StatusHandler.alterMessage(sr);
						  	}
						  	
						  	@Override
						  	public void onSuccess(ESBPacket esbPacket) {
						  		Element e = DOM.getElementById("userList");
						  		Element a = null;
						  		for (int i = 0; i < e.getChildCount(); i++) {
						  			Node n = e.getChild(i);
						  			if (n instanceof Element) {
						  				if (((Element)n).getPropertyString("value").equals(username))
						  					a = (Element) n;
						  			}
						  		}
						  		if (a!=null) {
						  			a.removeFromParent();
						  			DOM.getElementById("memberUserSelect").removeAllChildren();
						  			DOM.getElementById("memberGroupSelect").removeAllChildren();
						  			DOM.getElementById("userList").removeAllChildren();
						  			DOM.getElementById("groupList").removeAllChildren();
						  		}
				  				sr.setMessage(StatusHandler.getDeleteUserDone(username));
								sr.setState(StatusRecord.STATUS_DONE);
								StatusHandler.alterMessage(sr);
						  	}
		                  });
	}

	@Override
	public ScreenDispatch getDispatcher() {
		return null;
	}

	@Override
	public OverlayDispatch getOverlayDispatcher() {
		return null;
	}

	@Override
	public ModalDispatch getModalDispatcher() {
		return null;
	}

	@Override
	public HtmlTemplates getTemplates() {
		return null;
	}
}