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

import com.eduworks.gwt.russel.ui.client.net.AlfrescoNullCallback;
import com.eduworks.gwt.russel.ui.client.net.AlfrescoPacket;
import com.eduworks.russel.ui.client.handler.AlfrescoSearchHandler;
import com.eduworks.russel.ui.client.pagebuilder.HtmlTemplates;
import com.eduworks.russel.ui.client.pagebuilder.PageAssembler;
import com.eduworks.russel.ui.client.pagebuilder.ScreenTemplate;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;

public class ResultsScreen extends ScreenTemplate {
	
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

	AlfrescoSearchHandler ash;
	
	public void display() {
		PageAssembler.getInstance().ready(new HTML(HtmlTemplates.INSTANCE.getDetailModel().getText()));
		PageAssembler.getInstance().ready(new HTML(HtmlTemplates.INSTANCE.getResultsPanel().getText()));
		PageAssembler.getInstance().buildContents();
		
		DOM.getElementById("r-menuProjects").removeClassName("active");
		DOM.getElementById("r-menuWorkspace").removeClassName("active");
		
		if (searchType.equals(AlfrescoSearchHandler.SEARCH_TYPE)) {
			pageTitle = "Search Results";
			DOM.getElementById("r-menuCollections").removeClassName("active");
		} 
		else if (searchType.equals(AlfrescoSearchHandler.COLLECTION_TYPE)) {
			pageTitle = "My Files";
			DOM.getElementById("r-menuCollections").addClassName("active");
		} 
		else {
//			Window.alert("ResultsScreen received request for "+searchType);
			pageTitle = "Unknown Search Type";
		}
		
		DOM.getElementById("r-pageTitle").setInnerHTML("<h4>"+pageTitle+"</h4>");

		ash = new AlfrescoSearchHandler();
		
		ash.hook("r-menuSearchBar", "searchPanelWidgetScroll", searchType);
	
	
		PageAssembler.attachHandler("resultsSearchSelectShow", Event.ONCHANGE, new AlfrescoNullCallback<AlfrescoPacket>() {
			@Override
			public void onEvent(Event event) {
				ash.forceSearch();
			}
		});
	
		PageAssembler.attachHandler("resultsSearchSelectDistribution", Event.ONCHANGE, new AlfrescoNullCallback<AlfrescoPacket>() {
			@Override
			public void onEvent(Event event) {
				ash.forceSearch();
			}
		});
	
		PageAssembler.attachHandler("resultsSearchSelectSort", Event.ONCHANGE, new AlfrescoNullCallback<AlfrescoPacket>() {
			@Override
			public void onEvent(Event event) {
				ash.forceSearch();
			}
		});
	
		PageAssembler.attachHandler("resultsSearchSelectReverse", Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {
			@Override
			public void onEvent(Event event) {
				ash.forceSearch();
			}
		});
		
	}

	public static String buildSearchSortString() {
		String acc = "";
		ListBox lb = (ListBox)PageAssembler.elementToWidget("resultsSearchSelectSort", PageAssembler.SELECT);
		if ((lb.getSelectedIndex() != OUTOFRANGE) && (lb.getItemText(lb.getSelectedIndex()) != DEFAULT))
			acc = lb.getItemText(lb.getSelectedIndex());	
		return acc;
	}
	
	public static String buildSearchQueryString() {
		String acc = "";
		ListBox lb = (ListBox)PageAssembler.elementToWidget("resultsSearchSelectShow", PageAssembler.SELECT);
		if ((lb.getSelectedIndex() != OUTOFRANGE) && (lb.getItemText(lb.getSelectedIndex())!=EVERYTHING))
			acc += " AND cm:name:(" + getFileExtensionString(lb.getItemText(lb.getSelectedIndex())) + ")";
		lb = (ListBox)PageAssembler.elementToWidget("resultsSearchSelectDistribution", PageAssembler.SELECT);
		if ((lb.getSelectedIndex() != OUTOFRANGE) && (lb.getItemText(lb.getSelectedIndex())!=EVERYTHING))
			acc += " AND russel:dist:\"" + lb.getItemText(lb.getSelectedIndex()) + "\"";
		return acc;
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
			acc = "\".rlk\"";
		else if (type==AUDIO)
			acc = "\".aif\" OR \".iff\" OR \".m3u\" OR \".m4a\" OR \".mid\" OR \".mp3\" OR \".mpa\" OR \".ra\" OR \".swa\" OR \".wav\" OR \".wma\"";
		return acc;
	}
}