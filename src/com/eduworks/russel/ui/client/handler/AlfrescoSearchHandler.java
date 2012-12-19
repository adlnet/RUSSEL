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

package com.eduworks.russel.ui.client.handler;

import java.util.Vector;

import com.eduworks.gwt.client.util.Date;
import com.eduworks.gwt.russel.ui.client.net.AlfrescoApi;
import com.eduworks.gwt.russel.ui.client.net.AlfrescoCallback;
import com.eduworks.gwt.russel.ui.client.net.AlfrescoNullCallback;
import com.eduworks.gwt.russel.ui.client.net.AlfrescoPacket;
import com.eduworks.russel.ui.client.Russel;
import com.eduworks.russel.ui.client.pagebuilder.HtmlTemplates;
import com.eduworks.russel.ui.client.pagebuilder.PageAssembler;
import com.eduworks.russel.ui.client.pagebuilder.screen.EditScreen;
import com.eduworks.russel.ui.client.pagebuilder.screen.ResultsScreen;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

public class AlfrescoSearchHandler {
	public static final String RECENT_TYPE = "recent";
	public static final String SEARCH_TYPE = "search";
	public static final String PROJECT_TYPE = "project";
	public static final String EDIT_TYPE = "edit";
	public static final String ASSET_TYPE = "asset";
	public static final String NOTES_TYPE = "notes";
	public static final String TEMPLATE_TYPE = "template";
	public static final String COLLECTION_TYPE = "collection";
	public static final String STRATEGY_TYPE = "strategy";
	public static final String NO_SEARCH_RESULTS = "<p>No Search Results Found.</p>";
	
	private boolean pendingSearch = false;
	private Vector<SearchTileHandler> tileHandlers = new Vector<SearchTileHandler>();
	private Vector<AlfrescoPacket> pendingEdits;
	private int retries = 0;
	private Timer t;
	private boolean initialHook = true;
	private int tileIndex;
	private String customQuery = null;
	private String searchType;
	private HTML noResults = null;
	
	private void buildTile(AlfrescoPacket searchTermPacket, int index, String objPanel, Element td) {
		Vector<String> iDs = null;
		if (searchTermPacket.getShareSearchRecords().get(index)!=null&&searchTermPacket.getShareSearchRecords().get(index).getNodeId()!=null) {
			if ((td != null) && (searchType.equals(RECENT_TYPE)))
				iDs = PageAssembler.getInstance().inject(td.getId(), "x", new HTML(HtmlTemplates.INSTANCE.getObjectPanelWidget().getText()), false);
			else if (searchType.equals(SEARCH_TYPE))
				iDs = PageAssembler.getInstance().inject(objPanel, "x", new HTML(HtmlTemplates.INSTANCE.getSearchPanelWidget().getText()), false);
			else if (searchType.equals(COLLECTION_TYPE))
				iDs = PageAssembler.getInstance().inject(objPanel, "x", new HTML(HtmlTemplates.INSTANCE.getSearchPanelWidget().getText()), false);
			else if ((td != null) && (searchType.equals(PROJECT_TYPE)))
				iDs = PageAssembler.getInstance().inject(td.getId(), "x", new HTML(HtmlTemplates.INSTANCE.getEPSSProjectObjectPanelWidget().getText()), false);
			else if ((td != null) && (searchType.equals(ASSET_TYPE)))
				iDs = PageAssembler.getInstance().inject(td.getId(), "x", new HTML(HtmlTemplates.INSTANCE.getEPSSAssetObjectPanelWidget().getText()), false);
			else if ((td != null) && (searchType.equals(NOTES_TYPE)))
				iDs = PageAssembler.getInstance().inject(td.getId(), "x", new HTML(HtmlTemplates.INSTANCE.getEPSSNoteAssetObjectWidget().getText()), false);
			else if ((td != null) && (searchType.equals(STRATEGY_TYPE))) {
				Window.alert("handling a strategy search");
				iDs = PageAssembler.getInstance().inject(td.getId(), "x", new HTML(HtmlTemplates.INSTANCE.getEPSSAssetObjectPanelWidget().getText()), false);
			}
			String idPrefix = iDs.firstElement().substring(0, iDs.firstElement().indexOf("-"));
			tileHandlers.add(new SearchTileHandler(this, idPrefix, searchType, searchTermPacket.getShareSearchRecords().get(index)));
		}
	}
	
	public void buildThumbnails(String objPanel, AlfrescoPacket searchTermPacket) {
		
		RootPanel rp = RootPanel.get(objPanel);
		if (rp!=null) {
			Element td = null;
			tileIndex = 0;
			if (noResults!=null)
				rp.remove(noResults);
			
			if (searchTermPacket.getShareSearchRecords().length()==0) {
				rp.getElement().setAttribute("style", "text-align:center");
				noResults = new HTML(NO_SEARCH_RESULTS); 
				rp.add(noResults);
			} else
				rp.getElement().setAttribute("style", "");
			
			for (int x=0;x<searchTermPacket.getShareSearchRecords().length();x+=2) {
				if (!searchType.equals(SEARCH_TYPE)&&!searchType.equals(COLLECTION_TYPE)) {
					// SEARCH_TYPE and COLLECTION_TYPE use the vertStack style, and will not use the table-based layout that requires insertion of cell separators.
					td = DOM.createTD();
					td.setId(x +"-" + rp.getElement().getId());
					rp.getElement().appendChild(td);					
				}
				buildTile(searchTermPacket, x, objPanel, td);
				buildTile(searchTermPacket, x+1, objPanel, td);
			}
			
			processCallbacks();
		}
	}

	public void toggleSelection(final String id, final AlfrescoPacket record) {
		if (pendingEdits.contains(record)) {
			pendingEdits.remove(record);
			((Label)PageAssembler.elementToWidget(id + "State", PageAssembler.LABEL)).removeStyleName("active");
			((Label)PageAssembler.elementToWidget(id + "Select", PageAssembler.LABEL)).removeStyleName("active");
		} else {
			pendingEdits.add(record);
			((Label)PageAssembler.elementToWidget(id + "State", PageAssembler.LABEL)).addStyleName("active");
			((Label)PageAssembler.elementToWidget(id + "Select", PageAssembler.LABEL)).addStyleName("active");
		}
		
		if (pendingEdits.size()==0) {
			((Anchor)PageAssembler.elementToWidget("r-objectEditSelected", PageAssembler.A)).removeStyleName("blue");
			((Anchor)PageAssembler.elementToWidget("r-objectEditSelected", PageAssembler.A)).addStyleName("white");
		} else {
			((Anchor)PageAssembler.elementToWidget("r-objectEditSelected", PageAssembler.A)).addStyleName("blue");
			((Anchor)PageAssembler.elementToWidget("r-objectEditSelected", PageAssembler.A)).removeStyleName("white");
		}
	}
	
	public void processCallbacks() {
		if (tileHandlers.size()!=0&&tileIndex<tileHandlers.size())
			tileHandlers.get(tileIndex).refreshTile(new AlfrescoNullCallback<AlfrescoPacket>() {
														@Override
														public void onEvent(Event event) {
															tileIndex++;
															processCallbacks();
														}
													});
	}
	
	public void forceSearch () {
		if (!pendingSearch)
			t.schedule(1);
	}
	
	public void forceSearch (String customQuery) {
		this.customQuery = customQuery;
		if (!pendingSearch)
			t.schedule(1);
	}
	
	public final static native String removeExtraANDS(String query) /*-{
		return query.replace(/ +/gi," ").replace(/ /gi, " AND ").replace(/(AND )+/gi, "AND ").replace(/AND OR AND/gi, "OR");
	}-*/;
	
	public void hook(final String seachbarID, final String objectPanel, final String type) {
		searchType = type;
		customQuery = null;
		pendingEdits = new Vector<AlfrescoPacket>();
		t = new Timer() {
				@Override
				public void run() {
					String searchText = ((TextBox)PageAssembler.elementToWidget(seachbarID, 
				   																PageAssembler.TEXT)).getText().trim();
					AlfrescoPacket ap = AlfrescoPacket.makePacket();
					if (customQuery!=null)
						ap.addKeyValue("terms", customQuery + " AND ASPECT:\"russel:metaTest\"");
					else if (searchText=="")
						ap.addKeyValue("terms", "ASPECT:\"russel:metaTest\"");
					else
						ap.addKeyValue("terms", removeExtraANDS(searchText) + " AND ASPECT:\"russel:metaTest\"");
					ap.addKeyValue("tags", "");
					ap.addKeyValue("maxResults", 100);
					ap.addKeyValue("sort", "");
					ap.addKeyValue("site", "");
					
					if (searchType.equals(PROJECT_TYPE)) {
						ap.addKeyValue("query", "{\"datatype\":\"cm:content\",\"prop_mimetype\":\"russel/project\"}");
						if (initialHook)
							ap.addKeyValue("terms", "ASPECT:\"russel:metaTest\"");
						else
							ap.addKeyValue("terms", removeExtraANDS(searchText) + " AND ASPECT:\"russel:metaTest\"");						
					} else
						ap.addKeyValue("query", "{\"datatype\":\"cm:content\"}");
					
					if (searchType.equals(SEARCH_TYPE)) {
						if (searchText=="")
							ap.addKeyValue("terms", "ASPECT:\"russel:metaTest\"" + ResultsScreen.buildSearchQueryString());
						else
							ap.addKeyValue("terms", removeExtraANDS(searchText) + " AND ASPECT:\"russel:metaTest\"" + ResultsScreen.buildSearchQueryString());
						ap.addKeyValue("sort", ResultsScreen.buildSearchSortString());
					}
					
					if (searchType.equals(COLLECTION_TYPE)) {
						if (searchText=="")
							ap.addKeyValue("terms", "creator:" + AlfrescoApi.username + " AND ASPECT:\"russel:metaTest\"" + ResultsScreen.buildSearchQueryString());
						else
							ap.addKeyValue("terms", removeExtraANDS(searchText) + " AND creator:" + AlfrescoApi.username + " AND ASPECT:\"russel:metaTest\"" + ResultsScreen.buildSearchQueryString());
						ap.addKeyValue("sort", ResultsScreen.buildSearchSortString());
					}
					
					if (searchType.equals(STRATEGY_TYPE)) {
						ap.addKeyValue("query", "{\"russel:objective\":\"cm:content\"}");
						if (searchText=="")
							ap.addKeyValue("terms", "ASPECT:\"russel:metaTest\"" + ResultsScreen.buildSearchQueryString());
						else
							ap.addKeyValue("terms", removeExtraANDS(searchText) + " AND ASPECT:\"russel:metaTest\"" + ResultsScreen.buildSearchQueryString());
						ap.addKeyValue("sort", ResultsScreen.buildSearchSortString());
					}
					
					if (searchText==""&&searchType.equals(RECENT_TYPE)) {
						Date currentDate = new Date();
						Date pastDate = new Date();
						pastDate.setDate(pastDate.getDate()-10);
						String currentMonthPadded = ((currentDate.getMonth()+1)<10)? "0" + (currentDate.getMonth()+1):""+(currentDate.getMonth()+1);
						String currentDayPadded = ((currentDate.getDate())<10)? "0" + (currentDate.getDate()):""+(currentDate.getDate());
						String pastMonthPadded = ((pastDate.getMonth()+1)<10)? "0" + (pastDate.getMonth()+1):""+(pastDate.getMonth()+1);
						String pastDayPadded = ((pastDate.getDate())<10)? "0" + (pastDate.getDate()):""+(pastDate.getDate());
						ap.addKeyValue("terms", "modified:[\"" + pastDate.getYear() + "-" + pastMonthPadded + "-" + pastDayPadded + "\" to \"" +  currentDate.getYear() + "-" + currentMonthPadded + "-" + currentDayPadded + "\"] AND ASPECT:\"russel:metaTest\"");
						ap.addKeyValue("sort", "cm:modified|false");
					}
						
					AlfrescoApi.search(ap,
									   new AlfrescoCallback<AlfrescoPacket>() {
											@Override
											public void onFailure(Throwable caught) {
												if (retries>3) {
													retries = 0;
													tileHandlers.clear();
													RootPanel rp = RootPanel.get(objectPanel);
													rp.clear();
													rp.getElement().setInnerHTML("");
													Window.alert("Fooing Search by terms failed " + caught.getMessage());
												} else {
													t.schedule(500);
													retries++;
												}
												
												pendingSearch = false;
												initialHook = false;
												customQuery = null;
											}
											
											@Override
											public void onSuccess(final AlfrescoPacket SearchTermPacket) {
												tileHandlers.clear();
												RootPanel rp = RootPanel.get(objectPanel);
												rp.clear();
												rp.getElement().setInnerHTML("");
												if (searchType.equals(PROJECT_TYPE))
													PageAssembler.getInstance().inject(objectPanel, "x", new HTML(HtmlTemplates.INSTANCE.getEPSSNewProjectWidget().getText()), true);
												new Timer() {
													@Override
													public void run() {
														buildThumbnails(objectPanel, SearchTermPacket);
													}
												}.schedule(100);
												pendingSearch = false;
												initialHook = false;
												customQuery = null;
											}
										});	
				}
			};
	
					
		PageAssembler.attachHandler(seachbarID, Event.ONKEYUP, new AlfrescoNullCallback<AlfrescoPacket>() {
																	@Override
																	public void onEvent(Event event) {
																		if (event.getKeyCode() == KeyCodes.KEY_ENTER&&type!=ASSET_TYPE&&type!=PROJECT_TYPE&&type!=STRATEGY_TYPE) {
																			if (searchType == RECENT_TYPE)  searchType = SEARCH_TYPE;
																			ResultsScreen rs = new ResultsScreen();
																	   		rs.searchType = searchType;
																			Russel.view.loadScreen(rs, true);
																		} 
																		else if (type!=EDIT_TYPE) {
																			if (!pendingSearch) {
																				pendingSearch = true;
																				t.schedule(400);
																			} else {
																				t.cancel();
																				t.schedule(400);
																			}
																		}
																	}
																});
		
		PageAssembler.attachHandler("r-objectEditSelected", Event.ONCLICK, new AlfrescoNullCallback<AlfrescoPacket>() {
																		   	@Override
																		   	public void onEvent(Event event) {
																		   		Russel.view.loadScreen(new EditScreen(pendingEdits), true);
																		   	}
																		   });
		
		if (type != EDIT_TYPE) t.schedule(250);
	}
}