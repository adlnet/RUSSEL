package com.eduworks.russel.ui.client;

import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.google.gwt.user.client.ui.ListBox;

public class Utilities
{
	public static final String DOCUMENT = "Documents";
	public static final String IMAGE = "Images";
	public static final String VIDEO = "Videos";
	public static final String PACKAGE = "Packages";
	public static final String AUDIO = "Audio";
	public static final String LINK = "Links";
	public static final String EVERYTHING = "Everything";
	public static final String DEFAULT = "Default";
	public static final int OUTOFRANGE = -1;
	public static int showSetting = 0;
	public static int distributionSetting = 0;
	public static int sortSetting = 0;
	public static int searchSetting = 0;
	
	/**
	 * cleanString Prepares a string for query or storage
	 * @param dirty String
	 * @return String
	 */
	public static String cleanString(String dirty) {
		if (dirty==null)
			return "";
		else return dirty.replaceAll("\"", "'").replaceAll("[\r\n]", " ").trim();
	}

	/**
	 * buildSearchSortString Creates the appropriate search sort setting string based on selected sort option
	 * @return String
	 */
	public String buildSearchSortString() {
		String acc = "";
		ListBox lb = (ListBox)PageAssembler.elementToWidget("resultsSearchSelectSort", PageAssembler.SELECT);
		if (((sortSetting = lb.getSelectedIndex()) != OUTOFRANGE) && (lb.getItemText(lb.getSelectedIndex()) != DEFAULT))
			acc = lb.getItemText(sortSetting);	
		return acc;
	}
	
	/**
	 * buildSearchQueryString Creates the appropriate search query string based on filter options
	 * @return String
	 */
	public String buildSearchQueryString() {
		String acc = "";
		ListBox lb = (ListBox)PageAssembler.elementToWidget("resultsSearchSelectShow", PageAssembler.SELECT);
		if (((showSetting = lb.getSelectedIndex()) != OUTOFRANGE) && ((lb.getItemText(lb.getSelectedIndex()))!=EVERYTHING))
			acc += " cm:name:(" + getFileExtensionString(lb.getItemText(showSetting)) + ")";
		lb = (ListBox)PageAssembler.elementToWidget("resultsSearchSelectDistribution", PageAssembler.SELECT);
		if (((distributionSetting = lb.getSelectedIndex()) != OUTOFRANGE) && ((lb.getItemText(lb.getSelectedIndex()))!=EVERYTHING))
			acc += " russel:dist:\"" + lb.getItemText(distributionSetting) + "\"";
		return acc;
	}

	/**
	 * buildSearchSourceString Creates the appropriate query string based on selected source option
	 * @return String
	 */
	public String buildSearchSourceString() {
		String acc = "";
		ListBox lb = (ListBox)PageAssembler.elementToWidget("resultsSearchSelectSource", PageAssembler.SELECT);
		if ((searchSetting = lb.getSelectedIndex()) != OUTOFRANGE) {
			acc = lb.getItemText(searchSetting);
		}
		return acc;		
	}
	
	/**
	 * getFileExtensionString Generates list of appropriate file suffixes for a given file type
	 * @param type String
	 * @return String
	 */
	public String getFileExtensionString(String type) {
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
