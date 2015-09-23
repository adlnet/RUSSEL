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

import java.util.Vector;

import com.eduworks.gwt.client.component.HtmlTemplates;
import com.eduworks.gwt.client.net.callback.ESBCallback;
import com.eduworks.gwt.client.net.callback.EventCallback;
import com.eduworks.gwt.client.net.packet.ESBPacket;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.gwt.client.pagebuilder.modal.ModalDispatch;
import com.eduworks.gwt.client.pagebuilder.overlay.OverlayDispatch;
import com.eduworks.gwt.client.pagebuilder.screen.ScreenDispatch;
import com.eduworks.gwt.client.pagebuilder.screen.ScreenTemplate;
import com.eduworks.gwt.client.util.JSONUtils;
import com.eduworks.russel.ui.client.Russel;
import com.eduworks.russel.ui.client.net.RusselApi;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
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
public class PermissionScreen extends ScreenTemplate {
	public static final String TYPE_RESOURCE = "Resource";
	public static final String TYPE_USER = "User";
	public static final String TYPE_GROUP = "Group";

	public static final String TYPE_SERVICE = "Service";
	private static final String SOURCE_TYPE = "sourceType";
	private static final String SOURCE = "source";
	private static final String DESTINATION_ENTITY_TYPE = "destinationType";
	private static final String DESTINATION_ENTITY = "destination";
	
	private String type;
	private String source;
	private Vector<JSONObject> changes = new Vector<JSONObject>();
	
	public PermissionScreen(String type, String source) {
		this.type = type;
		this.source = source;
	}
	
	/**
	 * lostFocus In place to handle any processing requirements required when this screen loses focus.
	 * Called by ScreenDispatch for all RUSSEL screens.
	 */
	public void lostFocus() {
	}
	
	/**
	 * display Renders the Utility screen using appropriate templates and sets up handlers
	 */
	public void display() {
		if (source==null||source=="")
			return;
		
		if (DOM.getElementById("permissionModal")==null)
			PageAssembler.inject("flowContainer", "x", new HTML(Russel.htmlTemplates.getPermissionManagementPanelWidget().getText()), true);
		PageAssembler.openPopup("permissionModal");
		
		DOM.getElementById(SOURCE_TYPE).setInnerText(type);
		DOM.getElementById(SOURCE).setInnerText(source);
		
		if (type.equals(TYPE_RESOURCE)) {
			hide("optionService");
			hide("stdPermissionTitle");
			show("resourcePermissionTitle");
			show("resourceShareWith");
		}
		
		DOM.getElementById("permissionGroup").removeAllChildren();
		DOM.getElementById("permissionResource").removeAllChildren();
		DOM.getElementById("permissionSearch").removeAllChildren();
		DOM.getElementById("permissionService").removeAllChildren();
		DOM.getElementById("permissionUser").removeAllChildren();
		
		PageAssembler.attachHandler("resourceShareWith", Event.ONCLICK, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				final Element e = DOM.getElementById("resourceShareWith");
				final String s = e.getInnerText();
				RusselApi.toggleResourceSearch(source,
											   DOM.getElementById(DESTINATION_ENTITY).getPropertyString("value"),
											   s.equals("Searchable")?true:false,
											   new ESBCallback<ESBPacket>() {
													@Override
													public void onSuccess(ESBPacket esbPacket) {
														Element e = DOM.getElementById("resourceShareWith");
														if (!s.equals("Searchable")) {
															PageAssembler.addClass(e.getId(), "blue");
															PageAssembler.removeClass(e.getId(), "white");
														} else {
															PageAssembler.removeClass(e.getId(), "blue");
															PageAssembler.addClass(e.getId(), "white");
														}
														e.setInnerText(s.equals("Searchable")?"Unsearchable":"Searchable");
													}
													
													@Override
													public void onFailure(Throwable caught) {}
											   });
			}
		});
		
		PageAssembler.attachHandler("r-apply", Event.ONCLICK, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				PageAssembler.closePopup("permissionModal");
			}
		});
		
		PageAssembler.attachHandler("r-cancel", Event.ONCLICK, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				PageAssembler.closePopup("permissionModal");
			}
		});
		
		RusselApi.getServicePermissions(new ESBCallback<ESBPacket>() {
			@Override
			public void onSuccess(ESBPacket esbPacket) {
				fillServicePermissions(esbPacket.getObject("obj"));
				fillTarget();
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
		
		PageAssembler.attachHandler("destinationType", Event.ONCHANGE, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				fillTarget();
			}
		});
		
		PageAssembler.attachHandler("destination", Event.ONCHANGE, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				togglePermissions();
			}
		});
	}
	
	private void show(String name) {
		PageAssembler.removeClass(name, "hidden");
	}
	
	private void hide(String name) {
		PageAssembler.addClass(name, "hidden");
	}
	
	private void show(Element name) {
		PageAssembler.removeClass(name.getId(), "hidden");
	}
	
	private void hide(Element name) {
		PageAssembler.addClass(name.getId(), "hidden");
	}
	
	private void fillTarget() {
		DOM.getElementById(DESTINATION_ENTITY).removeAllChildren();
		final String dstType = DOM.getElementById(DESTINATION_ENTITY_TYPE).getPropertyString("value");
		
		show("destinationTag");
		show("destination");
		show(DOM.getParent(DOM.getElementById("permissionGroup")));
		show(DOM.getParent(DOM.getElementById("permissionResource")));
		show(DOM.getParent(DOM.getElementById("permissionSearch")));
		show(DOM.getParent(DOM.getElementById("permissionService")));
		show(DOM.getParent(DOM.getElementById("permissionUser")));
		DOM.getElementById("sourceDescription").setInnerText("");
		if (!type.equals(TYPE_RESOURCE)) {
			if (dstType.equals(TYPE_USER)) {
				RusselApi.getUsers(new ESBCallback<ESBPacket>() {
										@Override
										public void onSuccess(ESBPacket esbPacket) {
											JSONArray jsonArray = esbPacket.getArray("obj");
											for (int i = 0; i < jsonArray.size(); i++)
												makeOption(jsonArray.get(i).isString().stringValue(), DESTINATION_ENTITY);
											togglePermissions();
										}
										
										@Override
										public void onFailure(Throwable caught) {}
									});
				hide(DOM.getParent(DOM.getElementById("permissionGroup")));
				hide(DOM.getParent(DOM.getElementById("permissionResource")));
				hide(DOM.getParent(DOM.getElementById("permissionSearch")));
				hide(DOM.getParent(DOM.getElementById("permissionService")));
				DOM.getElementById("sourceDescription").setInnerText("perform the following actions on the user");
			} else if (dstType.equals(TYPE_GROUP)) {
				RusselApi.getGroups(new ESBCallback<ESBPacket>() {
										@Override
										public void onSuccess(ESBPacket esbPacket) {
											JSONArray jsonArray = esbPacket.getArray("obj");
											for (int i = 0; i < jsonArray.size(); i++)
												makeOption(jsonArray.get(i).isString().stringValue(), DESTINATION_ENTITY);
											togglePermissions();
										}
										
										@Override
										public void onFailure(Throwable caught) {}
									});
				hide(DOM.getParent(DOM.getElementById("permissionUser")));
				hide(DOM.getParent(DOM.getElementById("permissionResource")));
				hide(DOM.getParent(DOM.getElementById("permissionSearch")));
				hide(DOM.getParent(DOM.getElementById("permissionService")));
				DOM.getElementById("sourceDescription").setInnerText("perform the following actions on the group");
			} else if (dstType.equals(TYPE_SERVICE)) {
				hide(DESTINATION_ENTITY);
				hide("destinationTag");
				togglePermissions();
				DOM.getElementById("sourceDescription").setInnerText("make the use of the services below. If a user is not given permission to use a service they will not be able to perform the action even if they have permission on the object to do so");
			}
		} else if (type.equals(TYPE_RESOURCE)) {
			if (dstType.equals(TYPE_USER)) {
				RusselApi.getUsers(new ESBCallback<ESBPacket>() {
					@Override
					public void onSuccess(ESBPacket esbPacket) {
						JSONArray jsonArray = esbPacket.getArray("obj");
						for (int i = 0; i < jsonArray.size(); i++)
							makeOption(jsonArray.get(i).isString().stringValue(), DESTINATION_ENTITY);
						checkShared();
						togglePermissions();
					}
					
					@Override
					public void onFailure(Throwable caught) {}
				});
			} else if (dstType.equals(TYPE_GROUP)) {
				RusselApi.getGroups(new ESBCallback<ESBPacket>() {
								@Override
								public void onSuccess(ESBPacket esbPacket) {
									JSONArray jsonArray = esbPacket.getArray("obj");
									for (int i = 0; i < jsonArray.size(); i++)
										makeOption(jsonArray.get(i).isString().stringValue(), DESTINATION_ENTITY);
									checkShared();
									togglePermissions();
								}
								
								@Override
								public void onFailure(Throwable caught) {}
							});
			}
		
			DOM.getElementById("sourceDescription2").setInnerText(dstType);
			hide(DOM.getParent(DOM.getElementById("permissionGroup")));
			hide(DOM.getParent(DOM.getElementById("permissionUser")));
			hide(DOM.getParent(DOM.getElementById("permissionService")));
			hide(DOM.getParent(DOM.getElementById("permissionSearch")));
		}
	}
	
	private void checkShared() {
		RusselApi.checkSharedWith(source, 
								  DOM.getElementById(DESTINATION_ENTITY).getPropertyString("value"), 
								  new ESBCallback<ESBPacket>() {
									@Override
									public void onSuccess(ESBPacket esbPacket) {
										Element e = DOM.getElementById("resourceShareWith");
										boolean b = Boolean.parseBoolean(esbPacket.getString("obj"));
										if (!b) {
											PageAssembler.addClass(e.getId(), "blue");
											PageAssembler.removeClass(e.getId(), "white");
										} else {
											PageAssembler.removeClass(e.getId(), "blue");
											PageAssembler.addClass(e.getId(), "white");
										}
										e.setInnerText(b?"Unsearchable":"Searchable");
									}
									
									@Override
									public void onFailure(Throwable caught) {}
								  });
	}
	
	private void displayPermissions(JSONArray ja, String type) {
		 for (int i = 0; i < ja.size(); i++)
			 DOM.getElementById(ja.get(i).isString().stringValue() + type).setPropertyBoolean("checked", true);
	}
	
	private void displayServicePermissions(JSONObject jo) {
		for (String key : jo.keySet()) {
			 String parentName = key.substring(0, 1).toUpperCase() + key.substring(1);
			 JSONArray permissions = JSONUtils.sort(jo.get(key).isArray());
			 for (int i = 0; i < permissions.size(); i++) {
				 DOM.getElementById(permissions.get(i).isString().stringValue() + parentName).setPropertyBoolean("checked", true);
			 }
		 }
	}
	
	private void togglePermissions() {
		final String source = DOM.getElementById(DESTINATION_ENTITY).getPropertyString("value");
		final String type = DOM.getElementById(DESTINATION_ENTITY_TYPE).getPropertyString("value");
		final String thisType = this.type;
		Element[] es = PageAssembler.getElementsBySelector("input:checked");
		for (int i = 0; i < es.length; i++)
			es[i].setPropertyBoolean("checked", false);
		if (this.type.equals(TYPE_RESOURCE)) {
			if (source==""||type=="") {
				hide("resourceShareWith");
			} else {
				show("resourceShareWith");
			}
		}
		if (type.equals(TYPE_SERVICE)||(source!=""&&type!="")) {
			if (this.type.equals(TYPE_RESOURCE))
				checkShared();
			RusselApi.getPermissions(this.type.equals(TYPE_RESOURCE)?source:this.source,
									 this.type.equals(TYPE_RESOURCE)?type:this.type,
									 this.type.equals(TYPE_RESOURCE)?this.source:source,
									 this.type.equals(TYPE_RESOURCE)?this.type:type,
									 new ESBCallback<ESBPacket>() {
										@Override
										public void onFailure(Throwable caught) {}
										
										@Override
										public void onSuccess(ESBPacket esbPacket) {
											if (DOM.getElementById(DESTINATION_ENTITY_TYPE).getPropertyString("value").equals(TYPE_SERVICE))
												displayServicePermissions(esbPacket.getObject("obj"));
											else
												displayPermissions(esbPacket.getArray("obj"), thisType.equals(TYPE_RESOURCE)?thisType:type);
										}
									 });
		}
	}
	
	private native final Element createBreak() /*-{
		return document.createElement('br');
	}-*/;
	
	private void fillServicePermissions(JSONObject servicePermissions) {
		for (final String key : servicePermissions.keySet()) {
			String parentName = key.substring(0, 1).toUpperCase() + key.substring(1);
			Element e = DOM.getElementById("permission" + parentName);
			JSONArray permissions = JSONUtils.sort(servicePermissions.get(key).isArray());
			for (int i = 0; i < permissions.size(); i++) {
				final String permission = permissions.get(i).isString().stringValue();
				Element d = DOM.createDiv();
				Element l = DOM.createLabel();
				l.setInnerText(permission.replaceAll("_", " "));
				final Element c = DOM.createInputCheck();
				PageAssembler.attachHandler(c, Event.ONCHANGE, new EventCallback() {
					@Override
					public void onEvent(Event event) {
						String destinationType = DOM.getElementById(DESTINATION_ENTITY_TYPE).getPropertyString("value");
						if (!c.getPropertyBoolean("checked")) {
							RusselApi.removePermission(permission, 
													   !type.equals(TYPE_RESOURCE)?source:destinationType.equalsIgnoreCase("service")?key:DOM.getElementById(DESTINATION_ENTITY).getPropertyString("value"), 
													   !type.equals(TYPE_RESOURCE)?type:destinationType, 
													   !type.equals(TYPE_RESOURCE)?destinationType.equalsIgnoreCase("service")?key:DOM.getElementById(DESTINATION_ENTITY).getPropertyString("value"):source, 
													   !type.equals(TYPE_RESOURCE)?destinationType:type,
													   new ESBCallback<ESBPacket>() {
														@Override
														public void onSuccess(ESBPacket esbPacket) {
															c.setPropertyBoolean("checked", false);
														}
								
														@Override
														public void onFailure(Throwable caught) {
															c.setPropertyBoolean("checked", true);
														}
													   });
						} else {
							RusselApi.addPermission(permission, 
												    !type.equals(TYPE_RESOURCE)?source:destinationType.equalsIgnoreCase("service")?key:DOM.getElementById(DESTINATION_ENTITY).getPropertyString("value"), 
												    !type.equals(TYPE_RESOURCE)?type:destinationType, 
												    !type.equals(TYPE_RESOURCE)?destinationType.equalsIgnoreCase("service")?key:DOM.getElementById(DESTINATION_ENTITY).getPropertyString("value"):source, 
												    !type.equals(TYPE_RESOURCE)?destinationType:type,
													new ESBCallback<ESBPacket>() {
														@Override
														public void onSuccess(ESBPacket esbPacket) {
															c.setPropertyBoolean("checked", true);
														}
														
														@Override
														public void onFailure(Throwable caught) {
															c.setPropertyBoolean("checked", false);	
														}
													});
						}
					}
				});
				c.setId(permission + parentName);
				d.appendChild(c);
				d.appendChild(l);
				d.appendChild(createBreak());
				e.appendChild(d);
			}
		}
	}
	
	private void makeOption(final String val, String elementName) {
		Element e = DOM.getElementById(elementName);
		Element a = DOM.createOption();
		a.setInnerText(val);
		e.appendChild(a);
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