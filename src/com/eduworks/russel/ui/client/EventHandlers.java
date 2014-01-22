package com.eduworks.russel.ui.client;

import com.eduworks.gwt.client.net.callback.AlfrescoCallback;
import com.eduworks.gwt.client.net.callback.EventCallback;
import com.eduworks.gwt.client.net.packet.AlfrescoPacket;
import com.eduworks.gwt.client.net.packet.StatusPacket;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.russel.ui.client.epss.ProjectFileModel;
import com.eduworks.russel.ui.client.handler.Adl3DRSearchHandler;
import com.eduworks.russel.ui.client.handler.AlfrescoSearchHandler;
import com.eduworks.russel.ui.client.handler.StatusWindowHandler;
import com.eduworks.russel.ui.client.handler.TileHandler;
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
				if (tile.tileType.equals(AlfrescoSearchHandler.PROJECT_TYPE))
					ProjectFileModel.importFromAlfrescoNode(tile.searchRecord.getNodeId(),
							tile.searchRecord.getFilename(), new AlfrescoCallback<AlfrescoPacket>()
							{
								@Override
								public void onSuccess(AlfrescoPacket alfrescoPacket)
								{
									Constants.view.loadEPSSEditScreen(alfrescoPacket);
								}

								@Override
								public void onFailure(Throwable caught)
								{
									Window.alert("Fooing couldn't load project file " + caught);
								}
							});
				else if (tile.tileType.equals(AlfrescoSearchHandler.RECENT_TYPE)
						|| tile.tileType.equals(AlfrescoSearchHandler.ASSET_TYPE)
						|| tile.tileType.equals(AlfrescoSearchHandler.SEARCH_TYPE)
						|| tile.tileType.equals(AlfrescoSearchHandler.COLLECTION_TYPE)
						|| tile.tileType.equals(AlfrescoSearchHandler.FLR_TYPE)
						|| tile.tileType.equals(Adl3DRSearchHandler.SEARCH3DR_TYPE)
						|| tile.tileType.equals(Adl3DRSearchHandler.ASSET3DR_TYPE))
					view().loadDetailScreen(tile.searchRecord, tile);
			}
		};
	}

	public EventCallback tileOpenHandler(TileHandler tile) {
		// TODO Auto-generated method stub
		return tileOpenHandlerActual(tile);
	}

	protected EventCallback tileOpenHandlerActual(final TileHandler tile)
	{
		return new EventCallback()
		{
			@Override
			public void onEvent(Event event)
			{
				if (tile.tileType.equals(AlfrescoSearchHandler.PROJECT_TYPE))
					ProjectFileModel.importFromAlfrescoNode(tile.searchRecord.getNodeId(),
							tile.searchRecord.getFilename(), new AlfrescoCallback<AlfrescoPacket>()
							{
								@Override
								public void onSuccess(AlfrescoPacket alfrescoPacket)
								{
									Constants.view.loadEPSSEditScreen(alfrescoPacket);
								}

								@Override
								public void onFailure(Throwable caught)
								{
									Window.alert("Fooing couldn't load project file " + caught);
								}
							});
				else if (tile.tileType.equals(AlfrescoSearchHandler.RECENT_TYPE)
						|| tile.tileType.equals(AlfrescoSearchHandler.ASSET_TYPE)
						|| tile.tileType.equals(AlfrescoSearchHandler.SEARCH_TYPE)
						|| tile.tileType.equals(AlfrescoSearchHandler.COLLECTION_TYPE)
						|| tile.tileType.equals(AlfrescoSearchHandler.FLR_TYPE)
						|| tile.tileType.equals(Adl3DRSearchHandler.SEARCH3DR_TYPE)
						|| tile.tileType.equals(Adl3DRSearchHandler.ASSET3DR_TYPE))
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

	public EventCallback goToEditEpssScreenFor(final AlfrescoPacket record)
	{
		return new EventCallback()
		{
			@Override
			public void onEvent(Event event)
			{
				ProjectFileModel.importFromAlfrescoNode(record.getNodeId(), record.getFilename(),
						new AlfrescoCallback<AlfrescoPacket>()
						{
							@Override
							public void onSuccess(AlfrescoPacket alfrescoPacket)
							{
								view().loadEPSSEditScreen(alfrescoPacket);
							}

							@Override
							public void onFailure(Throwable caught)
							{
								StatusWindowHandler.createMessage(
										StatusWindowHandler.getProjectLoadMessageError(record.getFilename()),
										StatusPacket.ALERT_ERROR);
							}
						});
			}
		};
	}


}
