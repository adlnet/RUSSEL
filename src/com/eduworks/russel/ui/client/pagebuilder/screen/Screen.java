package com.eduworks.russel.ui.client.pagebuilder.screen;

import com.eduworks.gwt.client.pagebuilder.ScreenTemplate;
import com.eduworks.russel.ui.client.Utilities;
import com.eduworks.russel.ui.client.ScreenDispatch;
import com.eduworks.russel.ui.client.pagebuilder.HtmlTemplates;
import com.eduworks.russel.ui.client.Constants;

public abstract class Screen extends ScreenTemplate
{
	public ScreenDispatch view(){return (ScreenDispatch) Constants.view;}
	public HtmlTemplates templates(){return (HtmlTemplates) Constants.templates;}
	public Utilities util(){return (Utilities) Constants.util;}
}
