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

import com.eduworks.gwt.client.net.CommunicationHub;
import com.eduworks.gwt.client.net.api.Adl3DRApi;
import com.eduworks.gwt.client.net.api.FLRApi;
import com.eduworks.gwt.client.net.callback.EventCallback;
import com.eduworks.gwt.client.net.callback.FLRCallback;
import com.eduworks.gwt.client.net.packet.FLRPacket;
import com.eduworks.gwt.client.net.packet.StatusPacket;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.russel.ui.client.handler.StatusWindowHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * UtilityScreen class
 * Extends ScreenTemplate
 * Defines methods and handlers for the Utility screen.
 * 
 * @author Eduworks Corporation
 */
public class UtilityScreen extends Screen {
	
	public static final String ACCOUNT_TYPE = "account";
	public static final String USERS_TYPE = "users";
	public static final String GROUPS_TYPE = "groups";
	public static final String REPSETTINGS_TYPE = "repository";
	
	public String utilType;
	public String pageTitle;
	public String alfURL;
	
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
		PageAssembler.ready(new HTML(templates().getUtilityPanel().getText()));	
		PageAssembler.buildContents();
		
		DOM.getElementById("r-menuCollections").getParentElement().removeClassName("active");
		DOM.getElementById("r-menuProjects").getParentElement().removeClassName("active");
		DOM.getElementById("r-menuWorkspace").getParentElement().removeClassName("active");
		
		// Setup Title and utility panel
		if (utilType.equals(ACCOUNT_TYPE)) {
			pageTitle = "Account Settings";
			alfURL = "share/page/user/admin/profile";
		} 
		else if (utilType.equals(USERS_TYPE)) {
			pageTitle = "Manage Users";
			alfURL = "share/page/console/admin-console/users";
		} 
		else if (utilType.equals(GROUPS_TYPE)) {
			pageTitle = "Manage Groups";
			alfURL = "share/page/console/admin-console/groups";			
		}
		else if (utilType.equals(REPSETTINGS_TYPE)) {
			pageTitle = "Repository Settings";
			alfURL = null;	
			setDisplayIE0(DOM.getElementById("r-alfrescoUtil"), "none");
			setDisplayIE0(DOM.getElementById("r-repositorySettings"), "block");
		}
		else {
			pageTitle = "Unknown Alfresco Utility";
			alfURL = null;			
		}
		
		DOM.getElementById("r-pageTitle").setInnerHTML("<h4>"+pageTitle+"</h4>");
		
		if (alfURL != null) {  
			// Customize the Alfresco utility screen for iFrame viewing
			String frameSrc = CommunicationHub.rootURL + alfURL + "?" + CommunicationHub.randomString(); 
	 		final Frame f = new Frame();
	 		setAttributeIE0(f.getElement(), "seamless", "seamless");
	 		setAttributeIE0(f.getElement(), "border", "0px");
	 		setAttributeIE0(f.getElement(), "width", "100%");
	 		setAttributeIE0(f.getElement(), "display", "none");
	 		f.addLoadHandler(new LoadHandler() {
								@Override
								public void onLoad(LoadEvent event) {
									new Timer() {
										public void run() {
											Element e = ((Element)PageAssembler.getIFrameElement(f.getElement(), "alf-hd"));
											if (e!=null)
												e.removeFromParent();
											e = ((Element)PageAssembler.getIFrameElement(f.getElement(), "alf-filters"));
											if (e!=null)
												e.removeFromParent();
											e = ((Element)PageAssembler.getIFrameElement(f.getElement(), "alf-ft"));
											if (e!=null)
												e.removeFromParent();
											e = ((Element)PageAssembler.getIFrameElement(f.getElement(),"global_x002e_header_x0023_default-app_sites-sites-menu"));
											if (e!=null)
												e.removeFromParent();
											e = ((Element)PageAssembler.getIFrameElement(f.getElement(), "alf-content"));
											if (e!=null)
												setAttributeIE0(e, "margin-left", "0px");
									 		setAttributeIE0(f.getElement(), "border", "0px");
									 		setAttributeIE0(f.getElement(), "width", "100%");
									 		setAttributeIE0(f.getElement(), "display", "block");
									 		setAttributeIE0(f.getElement(), "height", (f.getElement().getScrollHeight()+380) + "px");
										}
									}.schedule(500);
								}
							});
	 		f.setUrl(frameSrc);
	 		RootPanel.get("r-alfrescoUtil").add(f);
		}
		else { 
			// populate the Repository Settings panel
			fillFlrSettings0();
			fill3drSettings0();

			
			// Handlers for r-repositorySettings
			PageAssembler.attachHandler("r-harvestFLR", 
											Event.ONCLICK, 
											new EventCallback() {
												@Override
												public void onEvent(Event event) {
													if (FLRApi.FLR_IMPORT_MODE.equals(FLRApi.FLR_IMPORT_ENABLED)) {
														launchFlrHarvest0();
													}				
												}
											});
		
			PageAssembler.attachHandler("flrUrl", 
											Event.ONCHANGE, 
											new EventCallback() {
												@Override
												public void onEvent(Event event) {
													adjustFlrFields0(extractValue0("flrUrl"));
												}
											});
			
			PageAssembler.attachHandler("r-detailEditUpdate", 
											Event.ONCLICK, 
											new EventCallback() {
												@Override
												public void onEvent(Event event) {
													saveFlrSettings0();
													save3drSettings0();
													removeUnsavedEffects0();
													fillFlrSettings0();
													fill3drSettings0();
												}
											});

		
		}

	}
	
	/**
	 * setDisplayIE0 Sets IE display state for given element
	 * @param element Element
	 * @param state String
	 */
	protected native void setDisplayIE0(Element element, String state) /*-{
		element.style.display = state;
	}-*/;

	/**
	 * setAttributeIE0 Sets state for given element attribute 
	 * @param element Element
	 * @param attribute String
	 * @param state String
	 */
	protected native void setAttributeIE0(Element element, String attribute, String state) /*-{
		element.style[attribute] = state;
	}-*/;

	/**
	 * removeUnsavedEffects0 Changes Update button back to saved state
	 */
	private void removeUnsavedEffects0() {
		((Label)PageAssembler.elementToWidget("r-detailSaveAlert", PageAssembler.LABEL)).addStyleName("hide");
		((Anchor)PageAssembler.elementToWidget("r-detailEditUpdate", PageAssembler.A)).removeStyleName("blue");
		((Anchor)PageAssembler.elementToWidget("r-detailEditUpdate", PageAssembler.A)).addStyleName("white");
	}
	
	/**
	 * fillFlrSettings0 Displays current FLR configuration settings
	 */
	private void fillFlrSettings0() {
		((Label)PageAssembler.elementToWidget("flrUrl", PageAssembler.LABEL)).setText(FLRApi.getFLRsetting(FLRApi.FLR_REPOSITORY_SETTING));
		((Label)PageAssembler.elementToWidget("flrImport", PageAssembler.LABEL)).setText(FLRApi.getFLRsetting(FLRApi.FLR_IMPORT_SETTING));
		((Label)PageAssembler.elementToWidget("flrPublish", PageAssembler.LABEL)).setText(FLRApi.getFLRsetting(FLRApi.FLR_PUBLISH_SETTING));
		((Label)PageAssembler.elementToWidget("flrActivity", PageAssembler.LABEL)).setText(FLRApi.getFLRsetting(FLRApi.FLR_ACTIVITY_SETTING));
		adjustFlrFields0(extractValue0("flrUrl"));
	}
	
	/**
	 * fill3drSettings0 Displays current 3DR configuration settings
	 */
	private void fill3drSettings0() {
		((Label)PageAssembler.elementToWidget("3drOption", PageAssembler.LABEL)).setText(Adl3DRApi.getADL3DRsetting(Adl3DRApi.ADL3DR_OPTION_SETTING));
		((Label)PageAssembler.elementToWidget("3drActivity", PageAssembler.LABEL)).setText(Adl3DRApi.getADL3DRsetting(Adl3DRApi.ADL3DR_ACTIVITY_SETTING));
	}
	
	/**
	 * extractValue0 Extracts the current text value for the given element
	 * @param element String
	 * @return String
	 */
	private String extractValue0(String element) {
		String val = "";
		val = ((Label)PageAssembler.elementToWidget(element, PageAssembler.LABEL)).getText();
		if (val==null||val=="")
			val = ((TextBox)PageAssembler.elementToWidget(element, PageAssembler.TEXT)).getText();
		return val;
	}
	
	/**
	 * saveFlrSettings0 Saves current FLR configuration settings
	 */
	private void saveFlrSettings0() {
		String newFlrSetting = extractValue0("flrUrl");
		FLRApi.saveFLRsetting(FLRApi.FLR_IMPORT_SETTING, extractValue0("flrImport"));		
		FLRApi.saveFLRsetting(FLRApi.FLR_PUBLISH_SETTING, extractValue0("flrPublish"));		
		FLRApi.saveFLRsetting(FLRApi.FLR_ACTIVITY_SETTING, extractValue0("flrActivity"));
		if (!newFlrSetting.equalsIgnoreCase(FLRApi.FLR_REPOSITORY_MODE)) {
			adjustFlrFields0(newFlrSetting);
			FLRApi.saveFLRsetting(FLRApi.FLR_IMPORT_SETTING, extractValue0("flrImport"));		
			FLRApi.saveFLRsetting(FLRApi.FLR_PUBLISH_SETTING, extractValue0("flrPublish"));		
			FLRApi.saveFLRsetting(FLRApi.FLR_ACTIVITY_SETTING, extractValue0("flrActivity"));
		}
		FLRApi.saveFLRsetting(FLRApi.FLR_REPOSITORY_SETTING, newFlrSetting);
	}
	
	/**
	 * save3drSettings0 Saves current 3DR configuration settings
	 */
	private void save3drSettings0() {
		Adl3DRApi.saveAdl3DRsetting(Adl3DRApi.ADL3DR_OPTION_SETTING, extractValue0("3drOption"));		
		Adl3DRApi.saveAdl3DRsetting(Adl3DRApi.ADL3DR_ACTIVITY_SETTING, extractValue0("3drActivity"));
	}
	
	/**
	 * adjustFlrFields0 Dynamically adjusts the FLR configuration fields based on setting selections
	 * @param flrSetting String
	 */
	private void adjustFlrFields0(String flrSetting) {
		int opCount = 0;
		
		if (flrSetting.equalsIgnoreCase(FLRApi.FLR_SAND_BOX)) {
			DOM.getElementById("flrImport").addClassName("editable");																
			DOM.getElementById("flrPublish").addClassName("editable");																
			DOM.getElementById("flrActivity").addClassName("editable");		
		}
		else if (flrSetting.equalsIgnoreCase(FLRApi.FLR_NOT_IN_USE)) {
			((Label)PageAssembler.elementToWidget("flrImport", PageAssembler.LABEL)).setText("FLR-NoImport");	
			((Label)PageAssembler.elementToWidget("flrPublish", PageAssembler.LABEL)).setText("FLR-NoPublish");	
			((Label)PageAssembler.elementToWidget("flrActivity", PageAssembler.LABEL)).setText("FLR-NoActivity");	
			DOM.getElementById("flrImport").removeClassName("editable");																
			DOM.getElementById("flrPublish").removeClassName("editable");																
			DOM.getElementById("flrActivity").removeClassName("editable");																
		}
		if (FLRApi.FLR_IMPORT_MODE.equals(FLRApi.FLR_IMPORT_DISABLED)) {
			DOM.getElementById("r-harvestFLR").setAttribute("style", "display:none");
		}
		else {
			DOM.getElementById("r-harvestFLR").setAttribute("style", "");		
			opCount++;
		}
		if (opCount == 0) {
			((Label)PageAssembler.elementToWidget("flrActions", PageAssembler.LABEL)).setText("No FLR actions are currently available.");
		}
		else {
			((Label)PageAssembler.elementToWidget("flrActions", PageAssembler.LABEL)).setText("");			
		}
	}
	
	/**
	 * launchFlrHarvest0 Initiates a request to harvest the FLR and sets up handlers for the response.
	 */
	private void launchFlrHarvest0() {
		final StatusPacket flrStatus = StatusWindowHandler.createMessage(StatusWindowHandler.getFLRHarvestMessageBusy("FLR Sandbox"),
				  StatusPacket.ALERT_BUSY);
		FLRApi.getFLRdata(new FLRCallback<FLRPacket>() {
							@Override
							public void onSuccess(FLRPacket result) {
								flrStatus.setMessage(StatusWindowHandler.getFLRHarvestDone(result.getValueString("docCount"), result.getValueString("badCount"), result.getValueString("partialCount")));
								flrStatus.setState(StatusPacket.ALERT_SUCCESS);
								StatusWindowHandler.alterMessage(flrStatus);
							}
							
							@Override
							public void onFailure(Throwable caught) {
								flrStatus.setMessage(StatusWindowHandler.getFLRHarvestError());
								flrStatus.setState(StatusPacket.ALERT_ERROR);
								StatusWindowHandler.alterMessage(flrStatus);
							}			
						});
	}

}