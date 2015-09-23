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

package com.eduworks.russel.ui.client.pagebuilder;

import com.google.gwt.resources.client.TextResource;

/**
 * HtmlTemplates class
 * Extends ClientBundle
 * Defines methods to retrieve templates.
 * 
 * @author Eduworks Corporation
 */
public interface HtmlTemplates extends com.eduworks.gwt.client.component.HtmlTemplates {
	/**
	 * getHeader Retrieves the screen header source code
	 * @return TextResource
	 */
	@Source("template/RusselHeader.html")
	public TextResource getHeader();
	
	/**
	 * getFooter Retrieves the screen footer source code
	 * @return TextResource
	 */
	@Source("template/RusselFooter.html")
	public TextResource getFooter();
	
	/**
	 * getMenuBar Retrieves the screen menu bar source code
	 * @return TextResource
	 */
	@Source("template/RusselMainMenuPanel.html")
	public TextResource getMenuBar();

	/**
	 * getEditPanel Retrieves the Edit Panel source code
	 * @return TextResource
	 */
	@Source("template/RusselEditPanel.html")
	public TextResource getEditPanel();

	/**
	 * getEditPanelWidget Retrieves the Edit Panel widget source code
	 * @return TextResource
	 */
	@Source("template/RusselEditPanelWidget.html")
	public TextResource getEditPanelWidget();

	/**
	 * getDetailModal Retrieves the DetailView modal source code
	 * @return TextResource
	 */
	@Source("template/RusselDetailModal.html")
	public TextResource getDetailModal();

	/**
	 * getDetailPanel Retrieves the DetailView information panel source code
	 * @return TextResource
	 */
	@Source("template/RusselDetailPanelWidget.html")
	public TextResource getDetailPanel();
	
	/**
	 * getLoginWidget Retrieves the login widget source code
	 * @return TextResource
	 */
	@Source("template/RusselLoginPanel.html")
	public TextResource getLoginWidget();

	/**
	 * getErrorWidget Retrieves the status modal widget source code
	 * @return TextResource
	 */
	@Source("template/RusselErrorWidget.html")
	public TextResource getErrorWidget();
	
	/**
	 * getObjectPanel Retrieves the recent items panel source code
	 * @return TextResource
	 */
	@Source("template/RusselRecentItemPanel.html")
	public TextResource getObjectPanel();
	
	/**
	 * getObjectPanelWidget Retrieves the recent items panel tile source code
	 * @return TextResource
	 */
	@Source("template/RusselRecentItemPanelWidget.html")
	public TextResource getObjectPanelWidget();
	
	/**
	 * getDetailComment Retrieves the DetailView comment item source code
	 * @return TextResource
	 */
	@Source("template/RusselDetailComment.html")
	public TextResource getDetailComment();
	
	/**
	 * getSearchPanelWidget Retrieves the search panel tile source code
	 * @return TextResource
	 */
	@Source("template/RusselSearchPanelWidget.html")
	public TextResource getSearchPanelWidget();
	
	/**
	 * getResultsPanel Retrieves the search results panel source code
	 * @return TextResource
	 */
	@Source("template/RusselResultsPanel.html")
	public TextResource getResultsPanel();
	
	/**
	 * getUtilityPanel Retrieves the utility screen source code
	 * @return TextResource
	 */
	@Source("template/RusselPermissionManagementPanelWidget.html")
	public TextResource getPermissionManagementPanelWidget();
	
	/**
	 * getFeatureHomePanel Retrieves the feature home screen source code
	 * @return TextResource
	 */
	@Source("template/RusselFeatureHomePanel.html")
	public TextResource getFeatureHomePanel();

	/**
	 * getEPSSEdit Retrieves the EPSS Edit screen source code
	 * @return TextResource
	 */
	@Source("template/RusselEPSSEditPanel.html")
	public TextResource getEPSSEdit();

	/**
	 * getDialog Retrieves the EPSS new project dialog source code
	 * @return TextResource
	 */
	@Source("template/RusselEPSSProjectDialog.html")
	public TextResource getDialog();
	
	/**
	 * getEPSSAssetObjectPanelWidget Retrieves the EPSS project Alfresco asset tile source code
	 * @return TextResource
	 */
	@Source("template/RusselEPSSAssetPanelWidget.html")
	public TextResource getEPSSAssetObjectPanelWidget();
	
	
	/**
	 * getEPSSProjectObjectPanelWidget Retrieves the EPSS Home panel tile source code
	 * @return TextResource
	 */
	@Source("template/RusselEPSSHomePanelWidget.html")
	public TextResource getEPSSProjectObjectPanelWidget();
	
	/**
	 * getEPSSEditSectionWidgets Retrieves the EPSS template section tile source code
	 * @return TextResource
	 */
	@Source("template/RusselEPSSEditSectionWidgets.html")
	public TextResource getEPSSEditSectionWidgets();
	
	/**
	 * getEPSSNoteAssetObjectWidget Retrieves the EPSS developer notes widget source code
	 * @return TextResource
	 */
	@Source("template/RusselEPSSNoteAssetPanelWidget.html")
	public TextResource getEPSSNoteAssetObjectWidget();
	

	@Source("template/RusselUserManagementPanel.html")
	public TextResource getUserManagementPanel();
	
	@Source("template/RusselGroupManagementPanel.html")
	public TextResource getGroupManagementPanel();
}