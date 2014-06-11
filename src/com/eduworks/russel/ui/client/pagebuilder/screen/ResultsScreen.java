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

import com.eduworks.gwt.client.net.api.Adl3DRApi;
import com.eduworks.gwt.client.net.callback.EventCallback;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.russel.ui.client.Constants;
import com.eduworks.russel.ui.client.Utilities;
import com.eduworks.russel.ui.client.handler.ESBSearchHandler;
import com.eduworks.russel.ui.client.handler.SearchHandler;
import com.eduworks.russel.ui.client.handler.StatusWindowHandler;
import com.eduworks.russel.ui.client.model.StatusRecord;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;

/**
 * ResultsScreen class
 * Extends ScreenTemplate
 * Defines methods and handlers for the Results screen.
 * 
 * @author Eduworks Corporation
 */
public class ResultsScreen extends Screen {
	
	public String searchType;
	public String pageTitle;

	public SearchHandler		sh;

	/**
	 * resetScreen0 Resets screen display based on search type
	 */
	public void resetScreen()
	{
		if (searchType.equals(ESBSearchHandler.SEARCH_TYPE) || searchType.equals(ESBSearchHandler.EDIT_TYPE))
		{
			Utilities.searchSetting = 0;
			sh = new ESBSearchHandler();
			pageTitle = "Search Results";
			DOM.getElementById("searchOptions").removeClassName("hidden");
			DOM.getElementById("filterOptions").removeClassName("hidden");
		}
		else if (searchType.equals(ESBSearchHandler.COLLECTION_TYPE))
		{
			Utilities.searchSetting = 0;
			sh = new ESBSearchHandler();
			pageTitle = "My Files";
			DOM.getElementById("searchOptions").addClassName("hidden");
			DOM.getElementById("filterOptions").removeClassName("hidden");
		}
		else if (searchType.equals(ESBSearchHandler.FLR_TYPE))
		{
			Utilities.searchSetting = 0;
			sh = new ESBSearchHandler();
			pageTitle = "Federal Learning Registry Resources";
			DOM.getElementById("searchOptions").addClassName("hidden");
			DOM.getElementById("filterOptions").addClassName("hidden");
		}
		//TODO 3DR fix search
//		else if (searchType.equals(Adl3DRSearchHandler.SEARCH3DR_TYPE))
//		{
//			Utilities.searchSetting = 1;
//			sh = new Adl3DRSearchHandler();
//			pageTitle = "ADL 3D Repository Search Results";
//			DOM.getElementById("searchOptions").removeClassName("hidden");
//			DOM.getElementById("filterOptions").addClassName("hidden");
//		}
		else
		{
			sh = new ESBSearchHandler();
			Utilities.searchSetting = 0;
			pageTitle = "Unknown Search Type: "+searchType;
		}
		DOM.getElementById("r-pageTitle").setInnerHTML("<h4>" + pageTitle + "</h4>");
		rememberFilters0();

		sh.hook("r-menuSearchBar", "searchPanelWidgetScroll", searchType);
	}

	/**
	 * rememberFilters0 Restores to last state of filter options
	 */
	protected static void rememberFilters0() {
		ListBox lb = (ListBox)PageAssembler.elementToWidget("resultsSearchSelectSource", PageAssembler.SELECT);
		if (lb != null && Utilities.searchSetting != Utilities.OUTOFRANGE) 	lb.setSelectedIndex(Utilities.searchSetting);
		lb = (ListBox)PageAssembler.elementToWidget("resultsSearchSelectShow", PageAssembler.SELECT);
		if (lb != null && Utilities.showSetting != Utilities.OUTOFRANGE) 	lb.setSelectedIndex(Utilities.showSetting);
		lb = (ListBox)PageAssembler.elementToWidget("resultsSearchSelectDistribution", PageAssembler.SELECT);
		if (lb != null && Utilities.distributionSetting != Utilities.OUTOFRANGE) 	lb.setSelectedIndex(Utilities.distributionSetting);
		lb = (ListBox)PageAssembler.elementToWidget("resultsSearchSelectSort", PageAssembler.SELECT);
		if (lb != null && Utilities.sortSetting != Utilities.OUTOFRANGE) 	lb.setSelectedIndex(Utilities.sortSetting);
	}
	
	/**
	 * saveFilters0 Saves current state of filter options
	 */
	public static void saveFilters0() {
		ListBox lb = (ListBox)PageAssembler.elementToWidget("resultsSearchSelectSource", PageAssembler.SELECT);
		if (lb != null) 	Utilities.searchSetting = lb.getSelectedIndex();
		lb = (ListBox)PageAssembler.elementToWidget("resultsSearchSelectShow", PageAssembler.SELECT);
		if (lb != null) 	Utilities.showSetting = lb.getSelectedIndex();
		lb = (ListBox)PageAssembler.elementToWidget("resultsSearchSelectDistribution", PageAssembler.SELECT);
		if (lb != null) 	Utilities.distributionSetting = lb.getSelectedIndex();
		lb = (ListBox)PageAssembler.elementToWidget("resultsSearchSelectSort", PageAssembler.SELECT);
		if (lb != null) 	Utilities.sortSetting = lb.getSelectedIndex();
	}

	/**
	 * setShowFilter Sets current state of Show filter option
	 */
	public void setShowFilter(String searchType) {
		String showType = "";
		ListBox lb = (ListBox)PageAssembler.elementToWidget("resultsSearchSelectShow", PageAssembler.SELECT);
		if (lb != null) {
			if (searchType.equals(ESBSearchHandler.FLR_TYPE))
			{
				showType = Utilities.LINK;
			}
			else
			{
				showType = Utilities.EVERYTHING;
			}
			for (int x=0; x<lb.getItemCount(); x++) {
				if (lb.getItemText(x).equals(showType)) {
					lb.setItemSelected(x, true);
					Utilities.showSetting = x;
				}
			}
		}
	}

	/**
	 * setSearchType Sets current search type based on selected Show filter option
	 */
	public void setSearchType(String showType) {
		ListBox lb = (ListBox)PageAssembler.elementToWidget("resultsSearchSelectShow", PageAssembler.SELECT);
		if (lb != null) {
//			RUSSEL is currently using file suffixes to satisfy the Show filter options in its Results screen... 
//			nothing special needed here for now other than switch to generic search.
			String searchType = ESBSearchHandler.SEARCH_TYPE;
			sh.hook("r-menuSearchBar", "searchPanelWidgetScroll", searchType);
		}
	}

	/**
	 * lostFocus In place to handle any processing requirements required when this screen loses focus.
	 * Called by ScreenDispatch for all RUSSEL screens.
	 */
	public void lostFocus() {
		sh.stop();
	}

	public void display()
	{
	PageAssembler.ready(template());
	PageAssembler.buildContents();
	resetScreen();

	PageAssembler.attachHandler("resultsSearchSelectShow", Event.ONCHANGE, new EventCallback()
	{
		@Override
		public void onEvent(Event event)
		{
			ListBox lb = (ListBox)PageAssembler.elementToWidget("resultsSearchSelectShow", PageAssembler.SELECT);
			setSearchType(lb.getItemText(lb.getSelectedIndex()));
			saveFilters0();
			sh.forceSearch();
		}
	});

	PageAssembler.attachHandler("resultsSearchSelectDistribution", Event.ONCHANGE, new EventCallback()
	{
		@Override
		public void onEvent(Event event)
		{
			saveFilters0();
			sh.forceSearch();
		}
	});

	PageAssembler.attachHandler("resultsSearchSelectSort", Event.ONCHANGE, new EventCallback()
	{
		@Override
		public void onEvent(Event event)
		{
			saveFilters0();
			sh.forceSearch();
		}
	});

	PageAssembler.attachHandler("resultsSearchSelectReverse", Event.ONCLICK, new EventCallback()
	{
		@Override
		public void onEvent(Event event)
		{
			saveFilters0();
			sh.forceSearch();
		}
	});

	PageAssembler.attachHandler("resultsSearchSelectSource", Event.ONCHANGE, new EventCallback()
	{
		@Override
		public void onEvent(Event event)
		{
			String val = Constants.util.buildSearchSourceString();
			if (val.equals("ADL 3DR"))
			{
				if (Adl3DRApi.ADL3DR_OPTION_MODE.equals(Adl3DRApi.ADL3DR_DISABLED))
				{
					StatusWindowHandler.createMessage(StatusWindowHandler.get3DRDisabledError("Search"), StatusRecord.ALERT_ERROR);
				}
				else
				{
					//TODO fix 3dr search
					//searchType = Adl3DRSearchHandler.SEARCH3DR_TYPE;
					saveFilters0();
					resetScreen();
				}
			}
			else
			{
				searchType = ESBSearchHandler.SEARCH_TYPE;
				saveFilters0();
				resetScreen();
			}
		}
	});

	
	PageAssembler.attachHandler("searchPreviousPage", Event.ONCLICK, new EventCallback() {
		@Override
		public void onEvent(Event event) {
			sh.popPagingToken();
			sh.popPagingToken();
			sh.pageSearch();
		}
	});
	
	PageAssembler.attachHandler("searchNextPage", Event.ONCLICK, new EventCallback() {
		@Override
		public void onEvent(Event event) {
			sh.pageSearch();
		}
	});
}

	protected HTML template()
	{
		return new HTML(templates().getResultsPanel().getText());
	}
}