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

import com.eduworks.gwt.client.util.Browser;
import com.eduworks.gwt.russel.ui.client.net.AlfrescoApi;
import com.eduworks.gwt.russel.ui.client.net.AlfrescoCallback;
import com.eduworks.gwt.russel.ui.client.net.AlfrescoNullCallback;
import com.eduworks.gwt.russel.ui.client.net.AlfrescoPacket;
import com.eduworks.russel.ui.client.Constants;
import com.eduworks.russel.ui.client.Russel;
import com.eduworks.russel.ui.client.pagebuilder.HtmlTemplates;
import com.eduworks.russel.ui.client.pagebuilder.PageAssembler;
import com.eduworks.russel.ui.client.pagebuilder.ScreenTemplate;
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
	final String LOGIN_BAD_LOGIN = "Login name or password is not valid.";

	private AlfrescoNullCallback<AlfrescoPacket> loginListener = new AlfrescoNullCallback<AlfrescoPacket>() {
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
																								    AlfrescoApi.username.toLowerCase()));
											PageAssembler.getInstance().setTemplate(HtmlTemplates.INSTANCE.getHeader().getText(),
																					HtmlTemplates.INSTANCE.getFooter().getText(),
																					"contentPane");
											prepTemplateHooks();
											Russel.view.loadScreen(new HomeScreen(), true);
										}
									}
		
									@Override
									public void onFailure(Throwable caught) {
										final Element oldErrorDialog = (Element)Document.get().getElementById("errorDialog");
										if (oldErrorDialog != null) oldErrorDialog.removeFromParent();

										final HTML errorDialog = new HTML(HtmlTemplates.INSTANCE.getErrorWidget().getText());
										RootPanel.get("errorContainer").add(errorDialog);
										enableLogin(true);
										((Label)PageAssembler.elementToWidget("errorMessage", PageAssembler.LABEL)).setText(LOGIN_BAD_LOGIN);
										PageAssembler.attachHandler("errorClose", Event.ONMOUSEUP, new AlfrescoNullCallback<AlfrescoPacket>() {
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
		PageAssembler.getInstance().setTemplate("", "", "contentPane");
		PageAssembler.getInstance().ready(new HTML(HtmlTemplates.INSTANCE.getLoginWidget().getText()));
		PageAssembler.getInstance().buildContents();
		
		PageAssembler.attachHandler("loginButton", Event.ONCLICK, loginListener);
		PageAssembler.attachHandler("loginPassword", Event.ONKEYUP, loginListener);
		PageAssembler.attachHandler("ForgotButton", Event.ONCLICK, Russel.nonFunctional);
		PageAssembler.attachHandler("RequestButton", Event.ONCLICK, Russel.nonFunctional);
		PageAssembler.attachHandler("rememberMe", Event.ONCLICK, Russel.nonFunctional);
		
		((TextBox)PageAssembler.elementToWidget("loginName", PageAssembler.TEXT)).setFocus(true);
	}
	
	/** Setup for template event handlers */
	private void prepTemplateHooks() {
		PageAssembler.attachHandler("r-menuLogout", Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {
																       @Override
																       public void onEvent(Event event) {
																    	   AlfrescoApi.logout(new AlfrescoCallback<AlfrescoPacket>() {
																								@Override
																								public void onSuccess(AlfrescoPacket result) {
																									Russel.view.loadScreen(new LoginScreen(), true);
																								}
																								
																								@Override
																								public void onFailure(Throwable caught) {
																									Russel.view.loadScreen(new LoginScreen(), true);
																								}
																							  });
																		}
																	});
		PageAssembler.attachHandler("r-menuWorkspace", Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {
																		@Override
																		public void onEvent(Event event) {
																			Russel.view.loadScreen(new HomeScreen(), true);
																		}
																	 });
		
		PageAssembler.attachHandler("r-menuCollections", Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {
																		@Override
																		public void onEvent(Event event) {
																			FeatureScreen fs = new FeatureScreen();
																			fs.featureType = FeatureScreen.COLLECTIONS_TYPE;
																			Russel.view.loadScreen(fs, true);
																		}
																	 });

		PageAssembler.attachHandler("r-menuProjects", Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {
																		@Override
																		public void onEvent(Event event) {
																			if (!Browser.isIE()) {
																				FeatureScreen fs = new FeatureScreen();
																				fs.featureType = FeatureScreen.PROJECTS_TYPE;
																				Russel.view.loadScreen(fs, true);
																			}
																			else Window.alert(Constants.UNSUPPORTED_IE_FEATURE);																		}
																	 });
	}
}
