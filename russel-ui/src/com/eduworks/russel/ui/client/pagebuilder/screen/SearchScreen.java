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
import com.eduworks.gwt.client.net.callback.EventCallback;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.gwt.client.pagebuilder.modal.ModalDispatch;
import com.eduworks.gwt.client.pagebuilder.overlay.OverlayDispatch;
import com.eduworks.gwt.client.pagebuilder.screen.ScreenDispatch;
import com.eduworks.gwt.client.pagebuilder.screen.ScreenTemplate;
import com.eduworks.russel.ui.client.Russel;
import com.eduworks.russel.ui.client.handler.SearchHandler;
import com.eduworks.russel.ui.client.model.FileRecord;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

/**
 * ResultsScreen class
 * Extends ScreenTemplate
 * Defines methods and handlers for the Results screen.
 * 
 * @author Eduworks Corporation
 */
public class SearchScreen extends ScreenTemplate {
	public static final String RESOURCE_DOCUMENT = "Documents";
	public static final String RESOURCE_IMAGE = "Images";
	public static final String RESOURCE_VIDEO = "Videos";
	public static final String RESOURCE_PACKAGE = "Packages";
	public static final String RESOURCE_AUDIO = "Audio";
	public static final String RESOURCE_LINK = "Links";
	public static final String RESOURCE_EVERYTHING = "Everything";

	public SearchHandler		sh = new SearchHandler(this, false);
	private String pageTitle = "";
	private String searchType = "";
	private String setting;
	
	public SearchScreen(String searchType) {
		this.searchType = searchType;
	}
	
	public SearchScreen(String searchType, String setting) {
		this.searchType = searchType;
		this.setting = setting;
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
		PageAssembler.ready(new HTML(Russel.htmlTemplates.getResultsPanel().getText()));
		PageAssembler.buildContents();
		PageAssembler.inject("flowContainer", "x", new HTML(Russel.htmlTemplates.getDetailModal().getText()), true);
		PageAssembler.inject("objDetailPanelWidget", "x", new HTML(Russel.htmlTemplates.getDetailPanel().getText()), true);

		sh.hookAndClear("r-menuSearchBar", "searchPanelWidgetScroll", searchType);
		
		PageAssembler.attachHandler("r-menuSearchBar", Event.ONKEYUP, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				generateQuery();
			}
		});
		
		if (searchType.equals(SearchHandler.SOURCE_LEARNING_REGISTRY)) {
			pageTitle = "Learning Registry Resources";
			DOM.getElementById("searchOptions").removeClassName("hidden");
			DOM.getElementById("filterOptions").addClassName("hidden");
			DOM.getElementById("r-objectEditSelected").addClassName("hidden");
			DOM.getElementById("resultsSearchSourceLR").setAttribute("selected", "selected");
		} else {
			pageTitle = "Search Results";
			DOM.getElementById("searchOptions").removeClassName("hidden");
			DOM.getElementById("filterOptions").removeClassName("hidden");
			DOM.getElementById("r-objectEditSelected").removeClassName("hidden");
			if (setting==RESOURCE_LINK)
				DOM.getElementById("resultsSearchFilterLink").setAttribute("selected", "selected");
		}
		DOM.getElementById("r-pageTitle").setInnerHTML("<h4>" + pageTitle + "</h4>");
		
		PageAssembler.attachHandler("resultsSearchSelectShow", Event.ONCHANGE, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				generateQuery();
			}
		});
		PageAssembler.attachHandler("resultsSearchSelectDistribution", Event.ONCHANGE, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				generateQuery();
			}
		});
		PageAssembler.attachHandler("searchNextPage", Event.ONCLICK, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				generateQuery();
			}
		});
	
		PageAssembler.attachHandler("resultsSearchSelectSource", Event.ONCHANGE, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				ListBox lb = (ListBox)PageAssembler.elementToWidget("resultsSearchSelectSource", PageAssembler.SELECT);
				Russel.screen.loadScreen(new SearchScreen(lb.getItemText(lb.getSelectedIndex())), true);
			}
		});
	
		PageAssembler.attachHandler("searchPreviousPage", Event.ONCLICK, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				sh.previousPage();
				generateQuery();
			}
		});
		
		PageAssembler.attachHandler("r-objectEditSelected", Event.ONCLICK, new EventCallback() {
			@Override
			public void onEvent(Event event) {
				Russel.screen.loadScreen(new EditScreen(sh.getSelected()), true);
			}
		});

		generateQuery();
	}
	
	/**
	 * buildSearchQueryString Creates the appropriate search query string based on filter options
	 * @return String
	 */
	public String buildSearchQueryString() {
		StringBuilder sb = new StringBuilder();
		ListBox lb = (ListBox) PageAssembler.elementToWidget("resultsSearchSelectShow", PageAssembler.SELECT);
		String val = lb.getItemText(lb.getSelectedIndex());
		if (val!=RESOURCE_EVERYTHING)
			sb.append("fileName_s:(" + getFileExtensionString(val) + ")");
		lb = (ListBox)PageAssembler.elementToWidget("resultsSearchSelectDistribution", PageAssembler.SELECT);
		val = lb.getItemText(lb.getSelectedIndex());
		if (val!=RESOURCE_EVERYTHING)
			sb.append(" AND " + FileRecord.DISTRIBUTION + ":\"" + val + "\"");
		return sb.toString();
	}
		
	/**
	 * getFileExtensionString Generates list of appropriate file suffixes for a given file type
	 * @param type String
	 * @return String
	 */
	public String getFileExtensionString(String type) {
	    String acc = ""; 
		if (type==RESOURCE_DOCUMENT)
			acc = "*.doc OR *.docx OR *.log OR *.msg OR *.odt OR *.pages OR *.rtf OR *.tex OR *.txt OR *.wpd OR *.wps OR *.xlr OR *.xls OR " +
				  "*.xlsx OR *.indd OR *.pct OR *.pdf OR *.htm OR *.html OR *.ppt OR *.pptx OR .doc OR .docx OR .log OR .msg OR .odt OR .pages OR .rtf OR .tex OR .txt OR .wpd OR .wps OR .xlr OR .xls OR" +
				  ".xlsx OR .indd OR .pct OR .pdf OR .htm OR .html OR .ppt OR .pptx";
		else if (type==RESOURCE_VIDEO)
			acc = "*.fla OR *.3g2 OR *.3gp OR *.asf OR *.asx OR *.avi OR *.flv OR *.mov OR *.mp4 OR *.mpg OR *.rm OR *.srt OR *.swf OR *.vob OR *.wmv OR " +
					".fla OR .3g2 OR .3gp OR .asf OR .asx OR .avi OR .flv OR .mov OR .mp4 OR .mpg OR .rm OR .srt OR .swf OR .vob OR .wmv";
		else if (type==RESOURCE_IMAGE)
			acc = "*.ai OR *.eps OR *.ps OR *.svg OR *.gif OR *.giff OR *.jpeg OR *.jpg OR *.png OR *.bmp OR *.dng OR *.pspimage OR *.tga OR *.tif OR *.tiff OR *.yuv OR *.psd OR " +
				  "*.dds OR *.3dm OR *.3ds OR *.dwg OR *.dxf OR *.max OR *.obj OR " +
				  ".ai OR .eps OR .ps OR .svg OR .gif OR .giff OR .jpeg OR .jpg OR .png OR .bmp OR .dng OR .pspimage OR .tga OR .tif OR .tiff OR .yuv OR .psd OR " +
				  ".dds OR .3dm OR .3ds OR .dwg OR .dxf OR .max OR .obj";
		else if (type==RESOURCE_PACKAGE)
			acc = "*.zip OR *.rar OR *.zipx OR *.gz OR *.7z OR *.pkg OR *.jar OR *.deb OR *.rpm OR *.sit OR *.sitx OR *.tar.gz OR " +
				  ".zip OR .rar OR .zipx OR .gz OR .7z OR .pkg OR .jar OR .deb OR .rpm OR .sit OR .sitx OR .tar.gz";
		else if (type==RESOURCE_LINK)
			acc = "*.rlr OR *.rlk OR .rlr OR .rlk OR .flr OR *.flr";
		else if (type==RESOURCE_AUDIO)
			acc = "*.aif OR *.iff OR *.m3u OR *.m4a OR *.mid OR *.mp3 OR *.mpa OR *.ra OR *.swa OR *.wav OR *.wma OR " +
					".aif OR .iff OR .m3u OR .m4a OR .mid OR .mp3 OR .mpa OR .ra OR .swa OR .wav OR .wma";
		return acc;
	}
	
	private void generateQuery() {
		StringBuilder sb = new StringBuilder();
		if (!searchType.equalsIgnoreCase(SearchHandler.SOURCE_LEARNING_REGISTRY)) {
			String qp = buildSearchQueryString();
			if (qp!="")
				sb.append(qp + " AND ");
		}
		String q = ((TextBox) PageAssembler.elementToWidget("r-menuSearchBar", PageAssembler.TEXT)).getText();
		String b = SearchHandler.cleanQuery(q);
		if (b=="")
			b = "*";
		sb.append(b);
		sh.query(sb.toString());
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