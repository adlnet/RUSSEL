package com.eduworks.russel.ui.client;

import com.eduworks.gwt.client.model.FileRecord;
import com.eduworks.gwt.client.net.callback.ESBCallback;
import com.eduworks.gwt.client.net.callback.EventCallback;
import com.eduworks.gwt.client.net.packet.ESBPacket;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.russel.ui.client.handler.ESBSearchHandler;
import com.eduworks.russel.ui.client.handler.StatusWindowHandler;
import com.eduworks.russel.ui.client.handler.TileHandler;
import com.eduworks.russel.ui.client.model.ProjectRecord;
import com.eduworks.russel.ui.client.model.StatusRecord;
import com.eduworks.russel.ui.client.pagebuilder.screen.FeatureScreen;
import com.eduworks.russel.ui.client.pagebuilder.screen.UtilityScreen;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;

public class EventHandlers
{
	// Make sure you use something that will not be changing. Passing in
	// parameters during the event handler creation -- THEY DO NOT CHANGE EVER.
	// So, make sure they are static or that you use properties of the objects
	// passed in.
	public EventCallback tileClickHandler(final TileHandler tile)
	{
		return tileClickHandlerActual(tile);
	}

	protected EventCallback tileClickHandlerActual(final TileHandler tile)
	{
		return new EventCallback()
		{
			@Override
			public void onEvent(Event event)
			{
				//TODO fix 3dr search
				if (tile.tileType.equals(ESBSearchHandler.PROJECT_TYPE))
					ProjectRecord.importFromServer(tile.searchRecord.getGuid(), 
														 new ESBCallback<ESBPacket>()
														 {
															@Override
															public void onSuccess(ESBPacket alfrescoPacket)
															{
																ProjectRecord pr = new ProjectRecord(alfrescoPacket);
																Constants.view.loadEPSSEditScreen(pr);
															}
							
															@Override
															public void onFailure(Throwable caught)
															{
																Window.alert("Fooing couldn't load project file " + caught);
															}
														});
				else if (tile.tileType.equals(ESBSearchHandler.RECENT_TYPE)
						|| tile.tileType.equals(ESBSearchHandler.ASSET_TYPE)
						|| tile.tileType.equals(ESBSearchHandler.SEARCH_TYPE)
						|| tile.tileType.equals(ESBSearchHandler.COLLECTION_TYPE)
						|| tile.tileType.equals(ESBSearchHandler.FLR_TYPE)/*
						|| tile.tileType.equals(Adl3DRSearchHandler.SEARCH3DR_TYPE)
						|| tile.tileType.equals(Adl3DRSearchHandler.ASSET3DR_TYPE)*/)
					view().loadDetailScreen(tile.searchRecord, tile);
			}
		};
	}

	public EventCallback tileOpenHandler(TileHandler tile) {
		return tileOpenHandlerActual(tile);
	}

	protected EventCallback tileOpenHandlerActual(final TileHandler tile)
	{
		return new EventCallback()
		{
			@Override
			public void onEvent(Event event)
			{
				if (tile.tileType.equals(ESBSearchHandler.PROJECT_TYPE))
					ProjectRecord.importFromServer(tile.searchRecord.getGuid(),
														 new ESBCallback<ESBPacket>()
														 {
															@Override
															public void onSuccess(ESBPacket alfrescoPacket)
															{
																ProjectRecord pr = new ProjectRecord(alfrescoPacket);
																Constants.view.loadEPSSEditScreen(pr);
															}
							
															@Override
															public void onFailure(Throwable caught)
															{
																Window.alert("Fooing couldn't load project file " + caught);
															}
														});
				else if (tile.tileType.equals(ESBSearchHandler.RECENT_TYPE)
						|| tile.tileType.equals(ESBSearchHandler.ASSET_TYPE)
						|| tile.tileType.equals(ESBSearchHandler.SEARCH_TYPE)
						|| tile.tileType.equals(ESBSearchHandler.COLLECTION_TYPE)
						|| tile.tileType.equals(ESBSearchHandler.FLR_TYPE) /*
						|| tile.tileType.equals(Adl3DRSearchHandler.SEARCH3DR_TYPE)
						|| tile.tileType.equals(Adl3DRSearchHandler.ASSET3DR_TYPE)*/)
					view().loadDetailScreen(tile.searchRecord, tile);
			}
		};
	}
	
	protected ScreenDispatch view()
	{
		return Constants.view;
	}

	public EventCallback goToEditScreen()
	{
		return new EventCallback()
		{
			@Override
			public void onEvent(Event event)
			{
				view().loadEditScreen();
			}
		};
	}

	public EventCallback goToProjectsScreen()
	{
		return new EventCallback()
		{
			@Override
			public void onEvent(Event event)
			{
				view().loadFeatureScreen(FeatureScreen.PROJECTS_TYPE);
			}
		};
	}

	public EventCallback goToCollectionsScreen()
	{
		return new EventCallback()
		{
			@Override
			public void onEvent(Event event)
			{
				view().loadFeatureScreen(FeatureScreen.COLLECTIONS_TYPE);
			}
		};
	}

	public EventCallback goToAccountScreen()
	{
		return new EventCallback()
		{
			@Override
			public void onEvent(Event event)
			{
				view().loadUtilityScreen(UtilityScreen.ACCOUNT_TYPE);
			}
		};
	}

	public EventCallback goToUsersScreen()
	{
		return new EventCallback()
		{
			@Override
			public void onEvent(Event event)
			{
				view().loadUtilityScreen(UtilityScreen.USERS_TYPE);
			}
		};
	}

	public EventCallback goToGroupsScreen()
	{
		return new EventCallback()
		{
			@Override
			public void onEvent(Event event)
			{
				view().loadUtilityScreen(UtilityScreen.GROUPS_TYPE);
			}
		};
	}

	public EventCallback goToRepositorySettingsScreen()
	{
		return new EventCallback()
		{
			@Override
			public void onEvent(Event event)
			{
				view().loadUtilityScreen(UtilityScreen.REPSETTINGS_TYPE);
			}
		};
	}

	public EventCallback goToHelpScreen()
	{
		return new EventCallback()
		{
			@Override
			public void onEvent(Event event)
			{
				String help = PageAssembler.getHelp();
				if (help != null && help != "")
					Window.open(PageAssembler.getHelp(), "_blank", null);
				else
					Window.alert("Help has not been configured for this installation of RUSSEL.");
			}
		};
	}

	public EventCallback goToEditEpssScreenFor(final FileRecord record)
	{
		return new EventCallback()
		{
			@Override
			public void onEvent(Event event)
			{
				ProjectRecord.importFromServer(record.getGuid(),
													 new ESBCallback<ESBPacket>()
													 {
														 @Override
														 public void onSuccess(ESBPacket alfrescoPacket)
														 {
														    ProjectRecord pr = new ProjectRecord(alfrescoPacket);
															view().loadEPSSEditScreen(pr);
														 }
							
														 @Override
														 public void onFailure(Throwable caught)
														 {
															 StatusWindowHandler.createMessage(
																	 StatusWindowHandler.getProjectLoadMessageError(record.getFilename()),
																	 StatusRecord.ALERT_ERROR);
														 }
													 });
			}
		};
	}


}
