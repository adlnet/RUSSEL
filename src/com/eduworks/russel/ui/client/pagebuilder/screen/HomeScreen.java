/*
Copyright (c) 2012 Eduworks Corporation
All rights reserved.
 
This Software (including source code, binary code and documentation) is provided by Eduworks Corporation to
the Government pursuant to contract number W31P4Q-12 -C- 0119 dated 21 March, 2012 issued by the U.S. Army 
Contracting Command Redstone. This Software is a preliminary version in development. It does not fully operate
as intended and has not been fully tested. This Software is provided to the U.S. Government for testing and
evaluation under the following terms and conditions:

	--Any redistribution of source code, binary code, or documentation must include this notice in its entirety, 
	 starting with the above copyright notice and ending with the disclaimer below.
	 
	--Eduworks Corporation grants the U.S. Government the right to use, modify, reproduce, release, perform,
	 display, and disclose the source code, binary code, and documentation within the Government for the purpose
	 of evaluating and testing this Software.
	 
	--No other rights are granted and no other distribution or use is permitted, including without limitation 
	 any use undertaken for profit, without the express written permission of Eduworks Corporation.
	 
	--All modifications to source code must be reported to Eduworks Corporation. Evaluators and testers shall
	 additionally make best efforts to report test results, evaluation results and bugs to Eduworks Corporation
	 using in-system feedback mechanism or email to russel@eduworks.com.
	 
THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
THE COPYRIGHT HOLDER BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN 
IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
*/

package com.eduworks.russel.ui.client.pagebuilder.screen;

import com.eduworks.gwt.client.net.api.AlfrescoApi;
import com.eduworks.gwt.client.net.callback.EventCallback;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.gwt.client.pagebuilder.ScreenTemplate;
import com.eduworks.russel.ui.client.Russel;
import com.eduworks.russel.ui.client.handler.AlfrescoSearchHandler;
import com.eduworks.russel.ui.client.pagebuilder.HtmlTemplates;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public class HomeScreen extends ScreenTemplate {

	private AlfrescoSearchHandler ash;  // The prior definition was "final" -- test to see if this breaks
	public void lostFocus() {
		ash.stop();
	}
	
	public void display() {
		((Label)PageAssembler.elementToWidget("r-menuUserName", PageAssembler.LABEL)).setText(AlfrescoApi.username);

		PageAssembler.getInstance().ready(new HTML(HtmlTemplates.INSTANCE.getMenuBar().getText()));
		PageAssembler.getInstance().ready(new HTML(HtmlTemplates.INSTANCE.getObjectPanel().getText()));
		PageAssembler.getInstance().buildContents();
		
		DOM.getElementById("r-menuWorkspace").addClassName("active");
		DOM.getElementById("r-menuCollections").removeClassName("active");
		DOM.getElementById("r-menuProjects").removeClassName("active");

		ash = new AlfrescoSearchHandler();
		
		PageAssembler.attachHandler("r-uploadContentTile", Event.ONCLICK, new EventCallback() {
																				@Override
																				public void onEvent(Event event) {
																					Russel.view.loadScreen(new EditScreen(), true);
																				}
																			});
		
		PageAssembler.attachHandler("r-projectsTile", Event.ONCLICK, new EventCallback() {
																			@Override
																			public void onEvent(Event event) {
																				FeatureScreen fs = new FeatureScreen();
																				fs.featureType = FeatureScreen.PROJECTS_TYPE;
																				Russel.view.loadScreen(fs, true);
																			}
																	 });
		

		PageAssembler.attachHandler("r-menuProjects", Event.ONCLICK, new EventCallback() {
																		@Override
																		public void onEvent(Event event) {
																			FeatureScreen fs = new FeatureScreen();
																			fs.featureType = FeatureScreen.PROJECTS_TYPE;
																			Russel.view.loadScreen(fs, true);
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

		PageAssembler.attachHandler("r-collectionsTile", Event.ONCLICK, new EventCallback() {
																			@Override
																			public void onEvent(Event event) {
																				FeatureScreen fs = new FeatureScreen();
																				fs.featureType = FeatureScreen.COLLECTIONS_TYPE;
																				Russel.view.loadScreen(fs, true);
																			}
																		 });
		
		PageAssembler.attachHandler("r-accountSettingsTile", Event.ONCLICK, new EventCallback() {
																			@Override
																			public void onEvent(Event event) {
																				UtilityScreen us = new UtilityScreen();
																				us.utilType = UtilityScreen.ACCOUNT_TYPE;
																				Russel.view.loadScreen(us, true);
																			}
																		 });	
		
		PageAssembler.attachHandler("r-manageUsersTile", Event.ONCLICK, new EventCallback() {
																			@Override
																			public void onEvent(Event event) {
																				UtilityScreen us = new UtilityScreen();
																				us.utilType = UtilityScreen.USERS_TYPE;
																				Russel.view.loadScreen(us, true);
																			}
																		 });
						
		PageAssembler.attachHandler("r-groupTile", Event.ONCLICK, new EventCallback() {
																			@Override
																			public void onEvent(Event event) {
																				UtilityScreen us = new UtilityScreen();
																				us.utilType = UtilityScreen.GROUPS_TYPE;
																				Russel.view.loadScreen(us, true);
																			}
																		 });
		
		PageAssembler.attachHandler("r-repositorySettingsTile", Event.ONCLICK, new EventCallback() {
																			@Override
																			public void onEvent(Event event) {
																				UtilityScreen us = new UtilityScreen();
																				us.utilType = UtilityScreen.REPSETTINGS_TYPE;
																				Russel.view.loadScreen(us, true);
																			}
																		 });
		
		PageAssembler.attachHandler("r-menuHelp", Event.ONCLICK, Russel.nonFunctional);
		PageAssembler.attachHandler("r-taxonomiesTile", Event.ONCLICK, Russel.nonFunctional);
		PageAssembler.attachHandler("notebook", Event.ONCLICK, Russel.nonFunctional);

		((TextBox)PageAssembler.elementToWidget("r-menuSearchBar", PageAssembler.TEXT)).setFocus(true);
		
		ash.hook("r-menuSearchBar", "searchObjectPanel", AlfrescoSearchHandler.RECENT_TYPE);
	}
}