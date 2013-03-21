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

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public interface HtmlTemplates extends ClientBundle {
	public static final HtmlTemplates INSTANCE = GWT.create(HtmlTemplates.class);

	@Source("template/RusselHeader.html")
	public TextResource getHeader();
	
	@Source("template/RusselFooter.html")
	public TextResource getFooter();
	
	@Source("template/RusselMainMenuPanel.html")
	public TextResource getMenuBar();

	@Source("template/RusselEditPanel.html")
	public TextResource getEditPanel();

	@Source("template/RusselEditPanelWidget.html")
	public TextResource getEditPanelWidget();

	@Source("template/RusselDetailModal.html")
	public TextResource getDetailModal();

	@Source("template/RusselDetailPanelWidget.html")
	public TextResource getDetailPanel();
	
	@Source("template/RusselLoginPanel.html")
	public TextResource getLoginWidget();

	@Source("template/RusselErrorWidget.html")
	public TextResource getErrorWidget();
	
	@Source("template/RusselRecentItemPanel.html")
	public TextResource getObjectPanel();
	
	@Source("template/RusselRecentItemPanelWidget.html")
	public TextResource getObjectPanelWidget();
	
	@Source("template/RusselDetailComment.html")
	public TextResource getDetailComment();
	
	@Source("template/RusselSearchPanelWidget.html")
	public TextResource getSearchPanelWidget();
	
	@Source("template/RusselResultsPanel.html")
	public TextResource getResultsPanel();
	
	@Source("template/RusselUtilityPanel.html")
	public TextResource getUtilityPanel();

	@Source("template/RusselFeatureHomePanel.html")
	public TextResource getFeatureHomePanel();

	@Source("template/RusselEPSSEditPanel.html")
	public TextResource getEPSSEdit();

	@Source("template/RusselEPSSProjectDialog.html")
	public TextResource getDialog();
	
	@Source("template/RusselEPSSAssetPanelWidget.html")
	public TextResource getEPSSAssetObjectPanelWidget();
	
	@Source("template/RusselEPSS3DRAssetPanelWidget.html")
	public TextResource getEPSS3DRAssetObjectPanelWidget();
	
	@Source("template/RusselEPSSHomePanelWidget.html")
	public TextResource getEPSSProjectObjectPanelWidget();
	
	@Source("template/RusselEPSSEditSectionWidgets.html")
	public TextResource getEPSSEditSectionWidgets();
	
	@Source("template/RusselEPSSNoteAssetPanelWidget.html")
	public TextResource getEPSSNoteAssetObjectWidget();
	
	@Source("template/Russel3DRPanelWidget.html")
	public TextResource get3DRObjectPanelWidget();}