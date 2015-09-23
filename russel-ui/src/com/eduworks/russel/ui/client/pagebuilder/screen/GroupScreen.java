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
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Node;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;

/**
 * UtilityScreen class
 * Extends ScreenTemplate
 * Defines methods and handlers for the Utility screen.
 * 
 * @author Eduworks Corporation
 */
public class GroupScreen extends ScreenTemplate {
	
	private JSONArray selectedUserMembers = null;
	private JSONArray selectedGroupMembers = null;
	
	/**
	 * lostFocus In place to handle any processing requirements required when this screen loses focus.
	 * Called by ScreenDispatch for all RUSSEL screens.
	 */
	public void lostFocus() {}
	
	/**
	 * display Renders the Utility screen using appropriate templates and sets up handlers
	 */
	public void display() {
		PageAssembler.ready(new HTML(Russel.htmlTemplates.getGroupManagementPanel().getText()));	
		PageAssembler.buildContents();
		
		DOM.getElementById("r-menuCollections").getParentElement().removeClassName("active");
		DOM.getElementById("r-menuProjects").getParentElement().removeClassName("active");
		DOM.getElementById("r-menuWorkspace").getParentElement().removeClassName("active");
		
		PageAssembler.attachHandler("groupPermission", Event.ONCLICK, new EventCallback() {
																			@Override
																			public void onEvent(Event event) {
																				Russel.screen.loadScreen(new PermissionScreen(PermissionScreen.TYPE_GROUP, DOM.getElementById("groupSelect").getPropertyString("value")), false);
																			}
																		});
		
		PageAssembler.attachHandler("groupUser", Event.ONCLICK, new EventCallback() {
																	@Override
																	public void onEvent(Event event) {
																		Russel.screen.loadScreen(new UserScreen(), true);
																	}
																});
		
		PageAssembler.attachHandler("groupSelect",
									Event.ONCHANGE,
									new EventCallback() {
										@Override
										public void onEvent(Event event) {
											fillGroupDetails(DOM.getElementById("groupSelect").getPropertyString("value"));
										}
									});
		
		RusselApi.getGroups(new ESBCallback<ESBPacket>() {
								@Override
								public void onSuccess(ESBPacket esbPacket) {
									JSONArray jsonArray = esbPacket.getArray("obj");
									for (int i = 0; i < jsonArray.size(); i++) {
										makeOption(jsonArray.get(i).isString().stringValue(), "groupSelect");
									}
									if (jsonArray.size()>0)
										fillGroupDetails(jsonArray.get(0).isString().stringValue());
								}
								
								@Override
								public void onFailure(Throwable caught) {}
							 });
		
		PageAssembler.attachHandler("groupCreate", Event.ONCLICK, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				PageAssembler.openPopup("createGroupModal");
			}
		});
		
		PageAssembler.attachHandler("groupDelete", Event.ONCLICK, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				deleteGroup(DOM.getElementById("groupSelect").getPropertyString("value"));
			}
		});
		
		PageAssembler.attachHandler("modalCreateCancel", Event.ONCLICK, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				DOM.getElementById("newGroupname").setPropertyString("value", "");
				PageAssembler.closePopup("createGroupModal");
			}
		});
		
		PageAssembler.attachHandler("modalCreateGroup", Event.ONCLICK, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				createGroup(DOM.getElementById("newGroupname").getPropertyString("value"));
				PageAssembler.closePopup("createGroupModal");
				DOM.getElementById("newGroupname").setPropertyString("value", "");
			}
		});
		
		PageAssembler.attachHandler("addMembers", Event.ONCLICK, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				Element e = DOM.getElementById("userList");
				JsArray entities = (JsArray) e.getPropertyObject("selectedOptions");
				for (int i = 0; i < entities.length(); i++) {
					final Element a = (Element) entities.get(i);
					RusselApi.addGroupMemberUser(a.getPropertyString("value"), 
												 DOM.getElementById("groupSelect").getPropertyString("value"), 
												 new ESBCallback<ESBPacket>() {
													@Override
													public void onFailure(Throwable caught) {}
													
													@Override
													public void onSuccess(ESBPacket esbPacket) {
														makeOption(a.getPropertyString("value"), "memberUserSelect");
													}
												 });	
				}
				int size = entities.length();
				for (int i = 0; i < size; i++)
					((Element)entities.get(0)).removeFromParent();
				
				e = DOM.getElementById("groupList");
				entities = (JsArray) e.getPropertyObject("selectedOptions");
				for (int i = 0; i < entities.length(); i++) {
					final Element a = (Element) entities.get(i);
					RusselApi.addGroupMemberGroup(a.getPropertyString("value"), 
												  DOM.getElementById("groupSelect").getPropertyString("value"), 
												  new ESBCallback<ESBPacket>() {
														@Override
														public void onFailure(Throwable caught) {}
														
														@Override
														public void onSuccess(ESBPacket esbPacket) {
															makeOption(a.getPropertyString("value"), "memberGroupSelect");
														}
 												   });	
				}
				size = entities.length();
				for (int i = 0; i < size; i++)
					((Element)entities.get(0)).removeFromParent();
			}
		});
		
		PageAssembler.attachHandler("removeMembers", Event.ONCLICK, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				Element e = DOM.getElementById("memberUserSelect");
				JsArray entities = (JsArray) e.getPropertyObject("selectedOptions");
				for (int i = 0; i < entities.length(); i++) {
					final Element a = (Element) entities.get(i);
					RusselApi.removeGroupMemberUser(a.getPropertyString("value"), 
												    DOM.getElementById("groupSelect").getPropertyString("value"), 
												    new ESBCallback<ESBPacket>() {
														@Override
														public void onFailure(Throwable caught) {}
														
														@Override
														public void onSuccess(ESBPacket esbPacket) {
															makeOption(a.getPropertyString("value"), "userList");
														}
													 });
				}
				int size = entities.length();
				for (int i = 0; i < size; i++)
					((Element)entities.get(0)).removeFromParent();
				
				e = DOM.getElementById("memberGroupSelect");
				entities = (JsArray) e.getPropertyObject("selectedOptions");
				for (int i = 0; i < entities.length(); i++) {
					final Element a = (Element) entities.get(i);
					RusselApi.removeGroupMemberGroup(a.getPropertyString("value"), 
												     DOM.getElementById("groupSelect").getPropertyString("value"), 
												     new ESBCallback<ESBPacket>() {
															@Override
															public void onFailure(Throwable caught) {}
															
															@Override
															public void onSuccess(ESBPacket esbPacket) {
																makeOption(a.getPropertyString("value"), "groupList");
															}
	 												   });
				}
				size = entities.length();
				for (int i = 0; i < size; i++)
					((Element)entities.get(0)).removeFromParent();
			}
		});
	}
	
	private boolean isMember(String x, JSONArray a) {
		boolean isMember = false;
		for (int i = 0; i < a.size(); i++)
			if (a.get(i).isString().stringValue().equals(x))
				isMember = true;
		return isMember;
	}
	
	private void fillGroupDetails(final String groupname) {
		DOM.getElementById("memberUserSelect").removeAllChildren();
		DOM.getElementById("memberGroupSelect").removeAllChildren();
		DOM.getElementById("userList").removeAllChildren();
		DOM.getElementById("groupList").removeAllChildren();
		RusselApi.getGroupMembers(groupname, new ESBCallback<ESBPacket>() {
			@Override
			public void onSuccess(ESBPacket esbPacket) {
				esbPacket = esbPacket.getObject("obj");
				selectedUserMembers = esbPacket.getArray("users");
				selectedGroupMembers = esbPacket.getArray("groups");
				for (int i = 0; i < selectedUserMembers.size(); i++)
					makeOption(selectedUserMembers.get(i).isString().stringValue(), "memberUserSelect");
				for (int i = 0; i < selectedGroupMembers.size(); i++)
					makeOption(selectedGroupMembers.get(i).isString().stringValue(), "memberGroupSelect");
				RusselApi.getUsers(new ESBCallback<ESBPacket>() {
					@Override
					public void onSuccess(ESBPacket esbPacket) {
						JSONArray jsonArray = esbPacket.getArray("obj");
						for (int i = 0; i < jsonArray.size(); i++) {
							String username = jsonArray.get(i).isString().stringValue();
							if (!isMember(username, selectedUserMembers))
								makeOption(username, "userList");
						}		
					}
					
					@Override
					public void onFailure(Throwable caught) {}
				});
				RusselApi.getGroups(new ESBCallback<ESBPacket>() {
					@Override
					public void onSuccess(ESBPacket esbPacket) {
						JSONArray jsonArray = esbPacket.getArray("obj");
						for (int i = 0; i < jsonArray.size(); i++) {
							String s = jsonArray.get(i).isString().stringValue();
							if (!isMember(s, selectedGroupMembers)&&groupname!=s)
								makeOption(s, "groupList");
						}
					}
					
					@Override
					public void onFailure(Throwable caught) {}
				 });
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
	}
	
	private void makeOption(final String val, String elementName) {
		Element e = DOM.getElementById(elementName);
		Element a = DOM.createOption();
		a.setInnerText(val);
		e.appendChild(a);
	}
	
	private void createGroup(final String groupname) {
		final StatusRecord sr = StatusHandler.createMessage(StatusHandler.getCreateGroupBusy(groupname), StatusRecord.STATUS_BUSY);
		RusselApi.createGroup(groupname,
					          new ESBCallback<ESBPacket>() {
							  	@Override
							  	public void onFailure(Throwable caught) {
									sr.setMessage(StatusHandler.getCreateGroupError(groupname));
									sr.setState(StatusRecord.STATUS_ERROR);
									StatusHandler.alterMessage(sr);
							  	}
							  	
							  	@Override
							  	public void onSuccess(ESBPacket esbPacket) {
						  			makeOption(groupname, "groupSelect");
						  			fillGroupDetails(groupname);
						  			DOM.getElementById("groupSelect").setPropertyString("value", groupname);
						  			sr.setMessage(StatusHandler.getCreateGroupDone(groupname));
									sr.setState(StatusRecord.STATUS_DONE);
									StatusHandler.alterMessage(sr);
									PageAssembler.closePopup("createGroupModal");
							  	}
							  });
	}
	
	private void deleteGroup(final String groupname) {
		final StatusRecord sr = StatusHandler.createMessage(StatusHandler.getDeleteGroupBusy(groupname), StatusRecord.STATUS_BUSY);
		RusselApi.removeGroup(groupname,
						      new ESBCallback<ESBPacket>() {
							  	@Override
							  	public void onFailure(Throwable caught) {
									sr.setMessage(StatusHandler.getDeleteGroupError(groupname));
									sr.setState(StatusRecord.STATUS_ERROR);
									StatusHandler.alterMessage(sr);
							  	}
							  	
							  	@Override
							  	public void onSuccess(ESBPacket esbPacket) {
							  		Element e = DOM.getElementById("groupSelect");
							  		Element a = null;
							  		for (int i = 0; i < e.getChildCount(); i++) {
							  			Node n = e.getChild(i);
							  			if (n instanceof Element) {
							  				if (((Element)n).getPropertyString("value").equals(groupname))
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
					  				sr.setMessage(StatusHandler.getDeleteGroupDone(groupname));
									sr.setState(StatusRecord.STATUS_DONE);
									StatusHandler.alterMessage(sr);
							  	}
			                  });
	}
	
	private void addGroupMemberUser(String username, String groupname) {
		
	}
	
	private void addGroupMemberGroup(String groupname, String targetgroup) {
		
	}
	
	private void removeGroupMemberUser(String username, String groupname) {
		
	}
	
	private void removeGroupMemberGroup(String groupname, String targetgroup) {
		
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