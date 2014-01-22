package com.eduworks.russel.ui.client.handler;

import java.util.Vector;

import com.eduworks.gwt.client.net.callback.EventCallback;
import com.eduworks.gwt.client.net.packet.AjaxPacket;
import com.eduworks.gwt.client.net.packet.AlfrescoPacket;
import com.eduworks.gwt.client.net.packet.SearchResultsPacket;
import com.eduworks.russel.ui.client.Constants;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

public abstract class SearchHandler extends Constants
{

	public Vector<TileHandler>	tileHandlers	= new Vector<TileHandler>();
	protected boolean	terminate	= false;
	protected boolean	pendingSearch	= false;
	protected int	retries	= 0;
	protected Timer	t;
	protected int	tileIndex;
	public String	customQuery	= null;
	protected String	searchType;
	protected HTML	noResults	= null;
	public Vector<String> filter = null;
	protected Vector<String>	doNotShow	= new Vector<String>();
	protected Vector<String>	showOnly	= new Vector<String>();
	
	public static final String	NO_SEARCH_RESULTS	= "<p>No Search Results Found.</p>";
	
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
					fullSearch += " "+operator+"ALL:"+searchTerms[i].substring(1);
				} else {
					fullSearch += " ALL:"+searchTerms[i];
				}
			}
		}
		return fullSearch.trim();
	}

	/**
	 * buildTile0 Creates a tile for the given searchTermPacket using the provided index and placing it in the provided objPanel
	 * @param searchTermPacket AjaxPacket Packet containing the information for a particular object in search results
	 * @param index int The object index for the new tile
	 * @param screenPosition int The desired position on the screen
	 * @param objPanel String The name of the object panel where the tile should be placed
	 * @param td Element The window element to which the tile should be appended
	 */
	protected abstract void buildTile0(AjaxPacket searchTermPacket, int index, int screenPosition, String objPanel, Element td);

	/**
	 * hook Assigns the search handler to an object panel and designates a particular search type
	 * @param string String Search bar ID 
	 * @param string2 String Object panel name
	 * @param searchType2 String The type of search
	 */
	public abstract void hook(String string, String string2, String searchType2);

	/**
	 * toggleSelection Reverses the current select status of the object represented by the given id and record
	 * @param id String
	 * @param record AjaxPacket
	 */
	public void toggleSelection(String id, AjaxPacket record){};
	
	/**
	 * buildThumbnails Builds all of the tiles for the items in the search results
	 * @param objPanel String Name of target panel for the tiles
	 * @param searchTermPacket Adl3DRPacket 3DR search results
	 */
	public void buildThumbnails(String objPanel, SearchResultsPacket searchTermPacket)
	{
		RootPanel rp = RootPanel.get(objPanel);
		if (rp!=null) {
			Element td = null;
			tileIndex = 0;
			if (noResults!=null)
				rp.remove(noResults);
			
			if (searchTermPacket.getSearchRecords().length()==0) {
				rp.getElement().setAttribute("style", "text-align:center");
				noResults = new HTML(NO_SEARCH_RESULTS); 
				rp.add(noResults);
			} else 
				rp.getElement().setAttribute("style", "");
			
			int screenPosition = 0;
			for (int x=0;x<searchTermPacket.getSearchRecords().length();x++) {
				if (filter != null)
					if (filter.contains(((AlfrescoPacket)searchTermPacket.getSearchRecords().get(x)).getNodeId()))
						continue;

				if (screenPosition % 2 == 0 && !doNotShow.contains(searchType) && (showOnly.isEmpty() || showOnly.contains(searchType)))
				{
					// SEARCH3DR_TYPE uses the vertStack style, and will not use the table-based layout that requires insertion of cell separators.
					td = DOM.createTD();
					td.setId(x +"-" + rp.getElement().getId());
					rp.getElement().appendChild(td);					
				}
				buildTile0(searchTermPacket, x, screenPosition++, objPanel, td);
			}
			
			processCallbacks();
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
	 * forceSearch Schedules another query if there isn't already a pending search
	 */
	public void forceSearch()
	{
		if (!pendingSearch)
			t.schedule(1);
	}

	/**
	 * forceSearch Schedules another custom query if there isn't already a pending search
	 * @param customQuery String
	 */
	public void forceSearch(String customQuery)
	{
		this.customQuery = customQuery;
		if (!pendingSearch)
			t.schedule(1);
	}

	/**
	 * processCallbacks Sets up tile information and handlers for each item in search results
	 */
	public void processCallbacks()
	{
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
	 * setWorkflowStates Sets the selection state of all tiles in a handler panel according to the current state in an application workflow.
	 */
	public void setWorkflowStates()
	{
		// Derivative applications have the option to add actions to be processed after processCallbacks has finished.
		// By default, this does not do anything. 
	}


}
