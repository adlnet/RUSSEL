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
import com.eduworks.gwt.client.net.packet.StatusPacket;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.gwt.client.pagebuilder.ScreenTemplate;
import com.eduworks.russel.ui.client.handler.Adl3DRSearchHandler;
import com.eduworks.russel.ui.client.handler.AlfrescoSearchHandler;
import com.eduworks.russel.ui.client.handler.StatusWindowHandler;
import com.eduworks.russel.ui.client.pagebuilder.HtmlTemplates;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;

public class ResultsScreen extends ScreenTemplate {
	
	public static final String RESULTS_ALFRESCO_TYPE = "RUSSEL";
	public static final String RESULTS_ADL3DR_TYPE = "ADL 3DR";
	public static String RESULTS_MODE = RESULTS_ALFRESCO_TYPE;
	
	public static final String DOCUMENT = "Documents";
	public static final String IMAGE = "Images";
	public static final String VIDEO = "Videos";
	public static final String PACKAGE = "Packages";
	public static final String AUDIO = "Audio";
	public static final String LINK = "Links";
	public static final String EVERYTHING = "Everything";
	public static final String DEFAULT = "Default";
	public static final int OUTOFRANGE = -1;
	public String searchType;
	private String pageTitle;
	private static int showSetting = 0;
	private static int distributionSetting = 0;
	private static int sortSetting = 0;
	private static int searchSetting = 0;


	private Adl3DRSearchHandler adl3drsh;
	private AlfrescoSearchHandler ash;

	private void resetScreen0() {
		if (searchType.equals(AlfrescoSearchHandler.SEARCH_TYPE) || 
				searchType.equals(AlfrescoSearchHandler.EDIT_TYPE)) {
			searchSetting = 0;
			RESULTS_MODE = RESULTS_ALFRESCO_TYPE;
			pageTitle = "Search Results";
			DOM.getElementById("r-menuCollections").getParentElement().removeClassName("active");
			DOM.getElementById("searchOptions").removeClassName("hidden"); 
			DOM.getElementById("filterOptions").removeClassName("hidden");  
		} 
		else if (searchType.equals(AlfrescoSearchHandler.COLLECTION_TYPE)) {
			searchSetting = 0;
			RESULTS_MODE = RESULTS_ALFRESCO_TYPE;
			pageTitle = "My Files";
			DOM.getElementById("r-menuCollections").getParentElement().addClassName("active");
			DOM.getElementById("searchOptions").addClassName("hidden");   
			DOM.getElementById("filterOptions").removeClassName("hidden");  
		} 
		else if (searchType.equals(AlfrescoSearchHandler.FLR_TYPE)) {
			searchSetting = 0;
			RESULTS_MODE = RESULTS_ALFRESCO_TYPE;
			pageTitle = "Federal Learning Registry Resources";   
			DOM.getElementById("r-menuCollections").getParentElement().addClassName("active"); 
			DOM.getElementById("searchOptions").addClassName("hidden");  
			DOM.getElementById("filterOptions").addClassName("hidden");  
		} 
		else if (searchType.equals(Adl3DRSearchHandler.SEARCH3DR_TYPE)) {
			searchSetting = 1;
			RESULTS_MODE = RESULTS_ADL3DR_TYPE;
			pageTitle = "ADL 3D Repository Search Results";   
			DOM.getElementById("r-menuCollections").getParentElement().removeClassName("active"); 
			DOM.getElementById("searchOptions").removeClassName("hidden");   
			DOM.getElementById("filterOptions").addClassName("hidden");    
		} 
		else {
			RESULTS_MODE = RESULTS_ALFRESCO_TYPE;
			searchSetting = 0;
			pageTitle = "Unknown Search Type";
		}
		DOM.getElementById("r-pageTitle").setInnerHTML("<h4>"+pageTitle+"</h4>");
		rememberFilters0();
		
		if (RESULTS_MODE == RESULTS_ADL3DR_TYPE) 
			adl3drsh.hook("r-menuSearchBar", "searchPanelWidgetScroll", searchType);
		else
			ash.hook("r-menuSearchBar", "searchPanelWidgetScroll", searchType);
	}
	
	public void lostFocus() {
		ash.stop();
		adl3drsh.stop();
	}
	
	public void display() {
		PageAssembler.ready(new HTML(HtmlTemplates.INSTANCE.getResultsPanel().getText()));
		PageAssembler.buildContents();
		
		DOM.getElementById("r-menuProjects").getParentElement().removeClassName("active");
		DOM.getElementById("r-menuWorkspace").getParentElement().removeClassName("active");
		
		ash = new AlfrescoSearchHandler();
		adl3drsh = new Adl3DRSearchHandler();
		
		resetScreen0();
		

			
		PageAssembler.attachHandler("resultsSearchSelectShow", Event.ONCHANGE, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				saveFilters0();
				if (RESULTS_MODE.equals(RESULTS_ALFRESCO_TYPE))
					ash.forceSearch();
				else if (RESULTS_MODE.equals(RESULTS_ADL3DR_TYPE)) 
					adl3drsh.forceSearch();	
			}
		});
	
		PageAssembler.attachHandler("resultsSearchSelectDistribution", Event.ONCHANGE, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				saveFilters0();
				if (RESULTS_MODE.equals(RESULTS_ALFRESCO_TYPE))
					ash.forceSearch();
				else if (RESULTS_MODE.equals(RESULTS_ADL3DR_TYPE)) 
					adl3drsh.forceSearch();	
			}
		});
	
		PageAssembler.attachHandler("resultsSearchSelectSort", Event.ONCHANGE, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				saveFilters0();
				if (RESULTS_MODE.equals(RESULTS_ALFRESCO_TYPE))
					ash.forceSearch();
				else if (RESULTS_MODE.equals(RESULTS_ADL3DR_TYPE)) 
					adl3drsh.forceSearch();	
			}
		});
	
		PageAssembler.attachHandler("resultsSearchSelectReverse", Event.ONCLICK, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				saveFilters0();
				if (RESULTS_MODE.equals(RESULTS_ALFRESCO_TYPE))
					ash.forceSearch();
				else if (RESULTS_MODE.equals(RESULTS_ADL3DR_TYPE)) 
					adl3drsh.forceSearch();	
			}
		});
		
		PageAssembler.attachHandler("resultsSearchSelectSource", Event.ONCHANGE, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				String val = buildSearchSourceString();
				if (val.equals("ADL 3DR")) {
					if (Adl3DRApi.ADL3DR_OPTION_MODE.equals(Adl3DRApi.ADL3DR_DISABLED)) {
						StatusWindowHandler.createMessage(StatusWindowHandler.get3DRDisabledError("Search"), 
								  StatusPacket.ALERT_ERROR);																						
					}
					else {
						searchType = Adl3DRSearchHandler.SEARCH3DR_TYPE;
						saveFilters0();
						resetScreen0();					
					}	
				}
				else {
					searchType = AlfrescoSearchHandler.SEARCH_TYPE;
					saveFilters0();
					resetScreen0();
				}
			}
		});
		
	}
	
	public static String buildSearchSortString() {
		String acc = "";
		ListBox lb = (ListBox)PageAssembler.elementToWidget("resultsSearchSelectSort", PageAssembler.SELECT);
		if (((sortSetting = lb.getSelectedIndex()) != OUTOFRANGE) && (lb.getItemText(lb.getSelectedIndex()) != DEFAULT))
			acc = lb.getItemText(sortSetting);	
		return acc;
	}
	
	public static String buildSearchQueryString() {
		String acc = "";
		ListBox lb = (ListBox)PageAssembler.elementToWidget("resultsSearchSelectShow", PageAssembler.SELECT);
		if (((showSetting = lb.getSelectedIndex()) != OUTOFRANGE) && ((lb.getItemText(lb.getSelectedIndex()))!=EVERYTHING))
			acc += " cm:name:(" + getFileExtensionString(lb.getItemText(showSetting)) + ")";
		lb = (ListBox)PageAssembler.elementToWidget("resultsSearchSelectDistribution", PageAssembler.SELECT);
		if (((distributionSetting = lb.getSelectedIndex()) != OUTOFRANGE) && ((lb.getItemText(lb.getSelectedIndex()))!=EVERYTHING))
			acc += " russel:dist:\"" + lb.getItemText(distributionSetting) + "\"";
		return acc;
	}

	public static String buildSearchSourceString() {
		String acc = "";
		ListBox lb = (ListBox)PageAssembler.elementToWidget("resultsSearchSelectSource", PageAssembler.SELECT);
		if ((searchSetting = lb.getSelectedIndex()) != OUTOFRANGE) {
			acc = lb.getItemText(searchSetting);
		}
		return acc;		
	}
	
	private static void rememberFilters0() {
		ListBox lb = (ListBox)PageAssembler.elementToWidget("resultsSearchSelectSource", PageAssembler.SELECT);
		if (lb != null && searchSetting != OUTOFRANGE) 	lb.setSelectedIndex(searchSetting);
		lb = (ListBox)PageAssembler.elementToWidget("resultsSearchSelectShow", PageAssembler.SELECT);
		if (lb != null && showSetting != OUTOFRANGE) 	lb.setSelectedIndex(showSetting);
		lb = (ListBox)PageAssembler.elementToWidget("resultsSearchSelectDistribution", PageAssembler.SELECT);
		if (lb != null && distributionSetting != OUTOFRANGE) 	lb.setSelectedIndex(distributionSetting);
		lb = (ListBox)PageAssembler.elementToWidget("resultsSearchSelectSort", PageAssembler.SELECT);
		if (lb != null && sortSetting != OUTOFRANGE) 	lb.setSelectedIndex(sortSetting);
	}
	
	private static void saveFilters0() {
		ListBox lb = (ListBox)PageAssembler.elementToWidget("resultsSearchSelectSource", PageAssembler.SELECT);
		if (lb != null) 	searchSetting = lb.getSelectedIndex();
		lb = (ListBox)PageAssembler.elementToWidget("resultsSearchSelectShow", PageAssembler.SELECT);
		if (lb != null) 	showSetting = lb.getSelectedIndex();
		lb = (ListBox)PageAssembler.elementToWidget("resultsSearchSelectDistribution", PageAssembler.SELECT);
		if (lb != null) 	distributionSetting = lb.getSelectedIndex();
		lb = (ListBox)PageAssembler.elementToWidget("resultsSearchSelectSort", PageAssembler.SELECT);
		if (lb != null) 	sortSetting = lb.getSelectedIndex();
	}
	
	public static String getFileExtensionString(String type) {
	    String acc = ""; 
		if (type==DOCUMENT)
			acc = "\".doc\" OR \".docx\" OR \".log\" OR \".msg\" OR \".odt\" OR \".pages\" OR \".rtf\" OR \".tex\" OR \".txt\" OR \".wpd\" OR \".wps\" OR \".xlr\" OR \".xls\" OR" +
				  "\".xlsx\" OR \".indd\" OR \".pct\" OR \".pdf\" OR \".htm\" OR \".html\" OR \".ppt\" OR \".pptx\"";
		else if (type==VIDEO)
			acc = "\".fla\" OR \".3g2\" OR \".3gp\" OR \".asf\" OR \".asx\" OR \".avi\" OR \".flv\" OR \".mov\" OR \".mp4\" OR \".mpg\" OR \".rm\" OR \".srt\" OR \".swf\" OR \".vob\" OR \".wmv\"";
		else if (type==IMAGE)
			acc = "\".ai\" OR \".eps\" OR \".ps\" OR \".svg\" OR \".gif\" OR \".giff\" OR \".jpeg\" OR \".jpg\" OR \".png\" OR \".bmp\" OR \".dng\" OR \".pspimage\" OR \".tga\" OR \".tif\" OR \".tiff\" OR \".yuv\" OR \".psd\" OR " +
				  "\".dds\" OR \".3dm\" OR \".3ds\" OR \".dwg\" OR \".dxf\" OR \".max\" OR \".obj\"";
		else if (type==PACKAGE)
			acc = "\".zip\" OR \".rar\" OR \".zipx\" OR \".gz\" OR \".7z\" OR \".pkg\" OR \".jar\" OR \".deb\" OR \".rpm\" OR \".sit\" OR \".sitx\" OR \".tar.gz\"";
		else if (type==LINK)
			acc = "\".rlr\" OR \".rlk\"";
		else if (type==AUDIO)
			acc = "\".aif\" OR \".iff\" OR \".m3u\" OR \".m4a\" OR \".mid\" OR \".mp3\" OR \".mpa\" OR \".ra\" OR \".swa\" OR \".wav\" OR \".wma\"";
		return acc;
	}
}