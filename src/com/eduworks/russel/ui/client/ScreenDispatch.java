package com.eduworks.russel.ui.client;

import java.util.Vector;

import com.eduworks.gwt.client.model.FileRecord;
import com.eduworks.russel.ui.client.handler.TileHandler;
import com.eduworks.russel.ui.client.model.RUSSELFileRecord;
import com.eduworks.russel.ui.client.model.ProjectRecord;
import com.eduworks.russel.ui.client.pagebuilder.screen.DetailScreen;
import com.eduworks.russel.ui.client.pagebuilder.screen.EPSSEditScreen;
import com.eduworks.russel.ui.client.pagebuilder.screen.EditScreen;
import com.eduworks.russel.ui.client.pagebuilder.screen.FeatureScreen;
import com.eduworks.russel.ui.client.pagebuilder.screen.HomeScreen;
import com.eduworks.russel.ui.client.pagebuilder.screen.LoginScreen;
import com.eduworks.russel.ui.client.pagebuilder.screen.ResultsScreen;
import com.eduworks.russel.ui.client.pagebuilder.screen.UtilityScreen;

public class ScreenDispatch extends com.eduworks.gwt.client.pagebuilder.ScreenDispatch
{
	public void loadLoginScreen()
	{
		loadScreen(new LoginScreen(), true);
	}

	public void loadResultsScreen(String searchType)
	{
		ResultsScreen rs = new ResultsScreen();
   		rs.searchType = searchType;
		loadScreen(rs, true);
	}

	public void loadEditScreen(Vector<RUSSELFileRecord> pendingEdits)
	{
		loadScreen(new EditScreen(pendingEdits), true);
	}

	public void loadEditScreen()
	{
		loadScreen(new EditScreen(), true);
	}

	public void loadEPSSEditScreen(ProjectRecord fr)
	{
		loadScreen(new EPSSEditScreen(fr), true);
	}

	public void loadDetailScreen(FileRecord fr, TileHandler tile)
	{
		loadScreen(new DetailScreen(fr, tile), false);
	}

	public void loadDetailScreen(FileRecord fr, boolean isFull)
	{
		loadScreen(new DetailScreen(fr, isFull), true);
	}

	public void loadDetailScreen(String tempDetailId)
	{
		loadScreen(new DetailScreen(tempDetailId), true);
		
	}
	public void loadHomeScreen()
	{
		loadScreen(new HomeScreen(), true);
	}

	public void loadFeatureScreen(String featureType)
	{
		FeatureScreen fs = new FeatureScreen();
		fs.featureType = featureType;
		loadScreen(fs, true);
	}
	
//	public void loadEPSSEditScreen(String text)
//	{
//		   Russel.view.loadScreen(new EPSSEditScreen(new ProjectRecord(text)), true);
//	}

	public void loadUtilityScreen(String accountType)
	{
		UtilityScreen us = new UtilityScreen();
		us.utilType = accountType;
		Russel.view.loadScreen(us, true);
		
	}


}
