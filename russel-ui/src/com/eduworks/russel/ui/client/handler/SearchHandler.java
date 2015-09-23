package com.eduworks.russel.ui.client.handler;

import java.util.Vector;

import com.eduworks.gwt.client.model.Record;
import com.eduworks.gwt.client.model.StatusRecord;
import com.eduworks.gwt.client.net.callback.ESBCallback;
import com.eduworks.gwt.client.net.callback.EventCallback;
import com.eduworks.gwt.client.net.packet.AjaxPacket;
import com.eduworks.gwt.client.net.packet.ESBPacket;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.gwt.client.pagebuilder.screen.ScreenTemplate;
import com.eduworks.russel.ui.client.Russel;
import com.eduworks.russel.ui.client.extractor.FLRResultExtractor;
import com.eduworks.russel.ui.client.model.RUSSELFileRecord;
import com.eduworks.russel.ui.client.net.RusselApi;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

public class SearchHandler
{
	public static final String TYPE_RECENT = "recent";
	public static final String TYPE_SEARCH = "search";
	public static final String TYPE_PROJECT = "project";
	public static final String TYPE_EDIT = "edit";
	public static final String TYPE_PROJECT_ASSET = "projectAsset";
	public static final String TYPE_ASSET = "asset";
	public static final String TYPE_COLLECTION = "collection";
	public static final String TYPE_LINK = "link";
	public static final String SOURCE_LEARNING_REGISTRY = "Learning Registry";
	public static final String SOURCE_RUSSEL = "RUSSEL";
	
	private Vector<TileHandler>	tileHandlers	= new Vector<TileHandler>();
	private boolean	terminate	= false;
	private boolean	pendingSearch	= false;
	private Timer	runQuery;
	private int	tileIndex;
	private String	query	= "";
	private String	searchType;
	private HTML	noResults	= null;
	private boolean tableResults = false;
	private Vector<String> pagingTokens = new Vector<String>();
	private String lastQuery = "";
	private ScreenTemplate screen;
	private String objectPanel;
	
	public static final String	RESULTS_NONE	= "<p>No Search Results Found.</p>";
	
	protected Vector<RUSSELFileRecord> pendingEdits;
	
	/**
	 * cleanQuery Prepares the provided search text for query
	 * @param rawSearchText
	 * @return String
	 */
	public static String cleanQuery(String rawSearchText)
	{
		rawSearchText = rawSearchText.trim();
		if (rawSearchText.equalsIgnoreCase("-")||rawSearchText.equalsIgnoreCase("!")||rawSearchText.equalsIgnoreCase("*")||rawSearchText.equalsIgnoreCase("not")||
			rawSearchText.equalsIgnoreCase("search...")||rawSearchText.equalsIgnoreCase("Enter search terms..."))
			rawSearchText = "";
		String[] searchTerms = rawSearchText.split(" ");
		String fullSearch = "";
		char operator = ' ';
		for (int i=0 ; i<searchTerms.length; i++) {
			if (searchTerms[i] != "")  {
				operator = searchTerms[i].charAt(0);
				if (searchTerms[i].equalsIgnoreCase("AND")||searchTerms[i].equalsIgnoreCase("&&")||
					searchTerms[i].equalsIgnoreCase("OR")||searchTerms[i].equalsIgnoreCase("||")||
					searchTerms[i].equalsIgnoreCase("NOT")||searchTerms[i].equalsIgnoreCase("*")) {
					fullSearch += " "+searchTerms[i];
				} else if (searchTerms[i].indexOf(":") != -1) {
					fullSearch += " "+searchTerms[i];
				} else if (operator == '-'||operator == '+'||operator == '!'||operator == '|'||operator == '~'||operator == '='){
					fullSearch += " "+operator+searchTerms[i].substring(1);
				} else {
					fullSearch += " "+searchTerms[i];
				}
			}
		}
		return fullSearch.trim();
	}
	
	
	public SearchHandler(ScreenTemplate screen, boolean tableResults) {
		this.tableResults = tableResults;
		this.screen = screen;
		runQuery = new Timer() {
			@Override
			public void run() {
				pendingSearch = false;
				
				final ESBPacket ap = new ESBPacket();
				ESBPacket sortPack = new ESBPacket();
				sortPack.put("field", RUSSELFileRecord.UPDATED_DATE);
				sortPack.put("order", "desc");
				ap.put("sort", sortPack);
				
				ap.put("q", query);
				query = null;
				ap.put("rows", 100);
				
				if (lastQuery!=ap.getString("q"))
					pagingTokens.clear();
				lastQuery = ap.getString("q");
				
				ap.put("page", pagingTokens.size()+1);
				
				ap.put("fields", new RUSSELFileRecord().getFieldList());
				
				RusselApi.search(ap,
								 searchType,
							     new ESBCallback<ESBPacket>() {
									public void onFailure(Throwable caught) {
										tileHandlers.clear();
										generateRootPanel(null);
										StatusHandler.createMessage(StatusHandler.getSearchMessageError(query), StatusRecord.ALERT_ERROR);
									}
									
									public void onSuccess(final ESBPacket searchTermPacket) {
										tileHandlers.clear();
										if (searchTermPacket.getObject("obj").containsKey("cursor"))
											pagingTokens.add(0, searchTermPacket.getObject("obj").getString("cursor"));
										else
											pagingTokens.add(0, "*");
										generateRootPanel(searchTermPacket);
									}
								});	
			}
		};
	}
	
	public ScreenTemplate getScreen() {
		return screen;
	}
	
	public Vector<RUSSELFileRecord> getSelected() {
		return pendingEdits;
	}
	
	public boolean isPendingSearch() {
		return pendingSearch;
	}
	
	public void generateRootPanel(ESBPacket searchTermPacket) {
		RootPanel rp = RootPanel.get(objectPanel);
		if (rp!=null) {
			rp.clear();
			int childCount = rp.getElement().getChildCount();
				int grabIndex = 0;
				for (int childIndex=0;childIndex<childCount-((searchType.equals(TYPE_PROJECT))?1:0);childIndex++) { 
					Element removeCursor = null;
					while (((removeCursor= (Element) rp.getElement().getChild(grabIndex))!=null)&&removeCursor.getId().equals("r-newEntity"))
						grabIndex++;
					if (removeCursor!=null)
						rp.getElement().removeChild(removeCursor);
				}
			if (searchTermPacket!=null)
				buildThumbnails(objectPanel, tableResults, searchTermPacket);
		}
	}
	

	/**
	 * buildTile0 Initiates a tile in the Alfresco results panel.
	 * @param searchTermPacket ESBPacket Alfresco search results
	 * @param index int Index in the search results for the tile to be created
	 * @param objPanel String Name of target panel for the tile
	 * @param td Element Container for the tile
	 * @return TileHandler
	 */
	public TileHandler buildTile(final RUSSELFileRecord r, String searchType, String objPanel) {
		Vector<String> iDs = null;
		
		if (searchType.equals(TYPE_RECENT))
			iDs = PageAssembler.inject(objPanel, "x", new HTML(Russel.htmlTemplates.getObjectPanelWidget().getText()), false);
		else if (searchType.equals(TYPE_ASSET))
			iDs = PageAssembler.inject(objPanel, "x", new HTML(Russel.htmlTemplates.getEPSSAssetObjectPanelWidget().getText()), false);
		else if (searchType.equals(TYPE_COLLECTION) || searchType.equals(TYPE_SEARCH) || searchType.equals(SOURCE_LEARNING_REGISTRY) ||
				 searchType.equals(TYPE_LINK))
			iDs = PageAssembler.inject(objPanel, "x", new HTML(Russel.htmlTemplates.getSearchPanelWidget().getText()), false);
		else if (searchType.equals(TYPE_PROJECT))
			iDs = PageAssembler.inject(objPanel, "x", new HTML(Russel.htmlTemplates.getEPSSProjectObjectPanelWidget().getText()), false);
		else if (searchType.equals(TYPE_PROJECT_ASSET))
			iDs = PageAssembler.inject(objPanel, "x", new HTML(Russel.htmlTemplates.getEPSSNoteAssetObjectWidget().getText()), false);
		
		String idPrefix = iDs.firstElement().substring(0, iDs.firstElement().indexOf("-"));
		TileHandler th = new TileHandler(this, idPrefix, searchType, r);
		tileHandlers.add(th);
		return th;
	}
	
	/**
	 * toggleSelection Selects or deselects the given tile
	 * @param id String ID of desired tile
	 * @param record ESBPacket Information associated with the tile
	 */
	public void toggleSelection(final String id, final Record recordx) {
		RUSSELFileRecord record = (RUSSELFileRecord) recordx;
		if (pendingEdits.contains(record)) {
			pendingEdits.remove(record);
			getTile0(id).deselect();
			((Label)PageAssembler.elementToWidget(id + "State", PageAssembler.LABEL)).removeStyleName("active");
			((Label)PageAssembler.elementToWidget(id + "Select", PageAssembler.LABEL)).removeStyleName("active");
		} else {
			pendingEdits.add(record);
			getTile0(id).select();
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
	
	
	/**
	 * buildThumbnails Builds all of the tiles for the items in the search results
	 * @param objPanel String Name of target panel for the tiles
	 * @param searchTermPacket Adl3DRPacket 3DR search results
	 */
	public void buildThumbnails(String objPanel, boolean table, AjaxPacket searchTermPacket)
	{
		if (searchTermPacket != null) {
			RootPanel rp = RootPanel.get(objPanel);
			if (rp!=null) {
				tileIndex = 0;
				if (noResults!=null)
					rp.remove(noResults);

				Element td = null;
				
				if (searchTermPacket.containsKey("obj")) {
					JSONObject jo = searchTermPacket.getObject("obj").isObject();
					if ((jo.containsKey("items")&&jo.get("items").isArray().size()==0) || (jo.containsKey("hits")&&jo.get("hits").isObject().get("hits").isArray().size()==0)) {
						rp.getElement().setAttribute("style", "text-align:center");
						noResults = new HTML(RESULTS_NONE); 
						rp.add(noResults);
					} else 
						rp.getElement().setAttribute("style", "");
				
					if (searchType.equals(SOURCE_LEARNING_REGISTRY)) {
						JSONArray ja = searchTermPacket.getObject("obj").get("hits").isObject().get("hits").isArray();
						if (ja.size()==0)
							return;
						RUSSELFileRecord[] fileSet = FLRResultExtractor.walkBasicSearch(ja);
						for (int x=0;x<fileSet.length;x++)
							if (table) {
								if (x % 2 == 0) {
									td = DOM.createTD();
									td.setId(x +"-" + rp.getElement().getId());
									rp.getElement().appendChild(td);					
								}
								buildTile(fileSet[x], searchType, td.getId()).fillTile(null);
							} else {
								buildTile(fileSet[x], searchType, objPanel).fillTile(null);
							}
					} else {
						for (int x=0;x<searchTermPacket.getObject("obj").get("items").isArray().size();x++) {
							if (table) {
								if (x % 2 == 0) {
									td = DOM.createTD();
									td.setId(x +"-" + rp.getElement().getId());
									rp.getElement().appendChild(td);					
								}
								buildTile(new RUSSELFileRecord(new ESBPacket(searchTermPacket.getObject("obj").isObject().get("items").isArray().get(x).isObject())),
										  searchType,
										  td.getId());
							} else {
								buildTile(new RUSSELFileRecord(new ESBPacket(searchTermPacket.getObject("obj").isObject().get("items").isArray().get(x).isObject())),
										  searchType,
										  objPanel);
							}
						}
						
						processCallbacks();
					}
				}
			}
		}
	}

	/**
	 * stop Sets terminate global to true
	 */
	public void stop()
	{
		terminate = true;
	}

	/**
	 * selectAll Sets all tiles to selected state
	 */
	public void selectAll()
	{
		TileHandler tile = null;
		for (int i = 0; i<tileHandlers.size(); i++) {
			tile = tileHandlers.get(i);
			if (!tile.getSelectState()) {
				toggleSelection(tile.getIdPrefix(), tile.getSearchRecord());
			}
		}
	}

	/**
	 * selectNone Sets all tiles to deselected state
	 */
	public void selectNone()
	{
		TileHandler tile = null;
		for (int i = 0; i<tileHandlers.size(); i++) {
			tile = tileHandlers.get(i);
			if (tile.getSelectState()) {
				toggleSelection(tile.getIdPrefix(), tile.getSearchRecord());
			}
		}		
	}

	/**
	 * getTile0 Retrieves the tile handler for the given tile id.
	 * @param id String Desired tile id
	 * @return TileHandler
	 */
	private TileHandler getTile0(String id) {
		TileHandler tile = null;
		for (int i = 0; i<tileHandlers.size(); i++) {
			if (id.contains(tileHandlers.get(i).getIdPrefix())) {
				tile = tileHandlers.get(i);
			}
		}
		return tile;
	}
	
	/**
	 * forceSearch Schedules another custom query if there isn't already a pending search
	 * @param customQuery String
	 */
	public void query(String query) {
		this.query = query;
		if (!pendingSearch) {
			pendingSearch = true;
			runQuery.schedule(1000);
		} else {
			runQuery.cancel();
			runQuery.schedule(1000);
		}
	}
	
	public void previousPage() {
		if (pagingTokens.size()>0)
			pagingTokens.remove(0);
		if (pagingTokens.size()>0)
			pagingTokens.remove(0);
	}

	/**
	 * processCallbacks Sets up tile information and handlers for each item in search results
	 */
	public void processCallbacks() {
		if ((!terminate) && ((tileHandlers.size()!=0&&tileIndex<tileHandlers.size())))
			tileHandlers.get(tileIndex).fillTile(new EventCallback() {
														@Override
														public void onEvent(Event event) {
															tileIndex++;
															processCallbacks();
														}
													});
	}
	
	/**
	 * hook Launches appropriate Alfresco query and assigns handlers for the response
	 * @param seachbarID String Name of the search bar that informs the Alfresco query
	 * @param objectPanel String Name of the target panel for Alfresco results
	 * @param type String Name of the type of Alfresco search
	 */
	public void hookAndClear(String seachbarID, String objectPanel, String searchType) {
		this.objectPanel = objectPanel;
		this.searchType = searchType;
		query = null;
		pendingEdits = new Vector<RUSSELFileRecord>();
		PageAssembler.removeHandler(seachbarID);
		PageAssembler.removeHandler(objectPanel);
		generateRootPanel(null);
	}
}
