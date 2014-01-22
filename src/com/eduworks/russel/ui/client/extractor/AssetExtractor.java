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

package com.eduworks.russel.ui.client.extractor;

import org.vectomatic.file.Blob;

import com.eduworks.russel.ui.client.Constants;

/**
 * AssetExtractor
 * Defines globals, methods for asset extraction
 * 
 * @author Eduworks Corporation
 */
public class AssetExtractor extends Constants {
	public static final String IMAGE = "image";
	public static final String DOCUMENT = "document";
	public static final String COURSE = "course";
	public static final String VIDEO = "video";
	public static final String PROJECT = "project";
	public static final String LINK = "link";
	public static final String FLR = "flr";
	public static final String CORPUS = "corpus";
	public static final String OBJECTIVES = "objectives";
	public static final String DOMAINMODEL = "domain";
	public static final String EXPERTMODEL = "expertmodel";
	public static final String TUTORPROJECT = "tutorproject";
	public static final String TUTORSIMILE = "tutorsimile";
	public static final String TUTORCHAT = "tutorchat";
	public static final String TUTOR = "tutor";
		
//	Text	.log
//	Text	.rtf
//	Text	.txt

	/**
	 * extractAssetsFromPlainText 
	 * @param filedata Blob
	 */
	public static void extractAssetsFromPlainText(Blob filedata) {
		
	}
	
	/**
	 * getFileType Determines the file type from the suffix of the filename
	 * @param filename String
	 * @return String General file type associated with this file suffix
	 */
	public static String getFileType(String filename) {
		String ext = "";
		String acc = "";
		if (filename.indexOf(".")!=-1) {
			ext = filename.substring(filename.lastIndexOf(".")+1);
			if (ext.equalsIgnoreCase("ai")||ext.equalsIgnoreCase("eps")||ext.equalsIgnoreCase("ps")||ext.equalsIgnoreCase("svg")||ext.equalsIgnoreCase("gif")||ext.equalsIgnoreCase("giff")||
				ext.equalsIgnoreCase("jpeg")||ext.equalsIgnoreCase("jpg")||ext.equalsIgnoreCase("png")||ext.equalsIgnoreCase("bmp")||ext.equalsIgnoreCase("dng")||ext.equalsIgnoreCase("pspimage")||
				ext.equalsIgnoreCase("tga")||ext.equalsIgnoreCase("tif")||ext.equalsIgnoreCase("tiff")||ext.equalsIgnoreCase("yuv")||ext.equalsIgnoreCase("psd")||ext.equalsIgnoreCase("dds")||
				ext.equalsIgnoreCase("3dm")||ext.equalsIgnoreCase("3ds")||ext.equalsIgnoreCase("dwg")||ext.equalsIgnoreCase("dxf")||ext.equalsIgnoreCase("max")||ext.equalsIgnoreCase("obj"))
				acc = IMAGE;
			else if (ext.equalsIgnoreCase("rpf"))
				acc = PROJECT;
			else if (ext.equalsIgnoreCase("tcf"))
				acc = CORPUS;
			else if (ext.equalsIgnoreCase("tpf"))
				acc = TUTORPROJECT;
			else if (ext.equalsIgnoreCase("tdf"))
				acc = DOMAINMODEL;
			else if (ext.equalsIgnoreCase("tem"))
				acc = EXPERTMODEL;
			else if (ext.equalsIgnoreCase("tut")||ext.equalsIgnoreCase("tsz")||ext.equalsIgnoreCase("tcjson"))
				acc = TUTOR;
			else if (ext.equalsIgnoreCase("fnt")||ext.equalsIgnoreCase("fon")||ext.equalsIgnoreCase("otf")||ext.equalsIgnoreCase("ttf")||ext.equalsIgnoreCase("7z")||ext.equalsIgnoreCase("deb")||ext.equalsIgnoreCase("gz")||
					 ext.equalsIgnoreCase("pkg")||ext.equalsIgnoreCase("rar")||ext.equalsIgnoreCase("rpm")||ext.equalsIgnoreCase("sit")||ext.equalsIgnoreCase("sitx")||ext.equalsIgnoreCase("zip")||ext.equalsIgnoreCase("zipx")||
					 ext.equalsIgnoreCase("app")||ext.equalsIgnoreCase("bat")||ext.equalsIgnoreCase("cgi")||ext.equalsIgnoreCase("com")||ext.equalsIgnoreCase("exe")||ext.equalsIgnoreCase("gadget")||ext.equalsIgnoreCase("jar")||
					 ext.equalsIgnoreCase("msi")||ext.equalsIgnoreCase("pif")||ext.equalsIgnoreCase("vb")||ext.equalsIgnoreCase("wsf"))
				acc = COURSE;
			else if (ext.equalsIgnoreCase("fla")||ext.equalsIgnoreCase("aif")||ext.equalsIgnoreCase("iff")||ext.equalsIgnoreCase("m3u")||ext.equalsIgnoreCase("m4a")||ext.equalsIgnoreCase("mid")||ext.equalsIgnoreCase("mp3")||
					 ext.equalsIgnoreCase("mpa")||ext.equalsIgnoreCase("ra")||ext.equalsIgnoreCase("swa")||ext.equalsIgnoreCase("wav")||ext.equalsIgnoreCase("wma")||ext.equalsIgnoreCase("3g2")||ext.equalsIgnoreCase("3gp")||
					 ext.equalsIgnoreCase("asf")||ext.equalsIgnoreCase("asx")||ext.equalsIgnoreCase("avi")||ext.equalsIgnoreCase("flv")||ext.equalsIgnoreCase("mov")||ext.equalsIgnoreCase("mp4")||ext.equalsIgnoreCase("mpg")||
					 ext.equalsIgnoreCase("rm")||ext.equalsIgnoreCase("srt")||ext.equalsIgnoreCase("swf")||ext.equalsIgnoreCase("vob")||ext.equalsIgnoreCase("wmv"))
				acc = VIDEO;
			else if (ext.equalsIgnoreCase("doc")||ext.equalsIgnoreCase("docx")||ext.equalsIgnoreCase("log")||ext.equalsIgnoreCase("msg")||ext.equalsIgnoreCase("odt")||ext.equalsIgnoreCase("pages")||
					 ext.equalsIgnoreCase("rtf")||ext.equalsIgnoreCase("tex")||ext.equalsIgnoreCase("txt")||ext.equalsIgnoreCase("wpd")||ext.equalsIgnoreCase("wps")||ext.equalsIgnoreCase("xlr")||ext.equalsIgnoreCase("xls")||
					 ext.equalsIgnoreCase("xlsx")||ext.equalsIgnoreCase("indd")||ext.equalsIgnoreCase("pct")||ext.equalsIgnoreCase("pdf")||ext.equalsIgnoreCase("ppt")||ext.equalsIgnoreCase("pptx"))
				acc = DOCUMENT;
			else if (ext.equalsIgnoreCase("rlk")||ext.equalsIgnoreCase("rlr")||ext.equalsIgnoreCase("3dr"))
				acc = LINK;
			else
				acc = DOCUMENT;
		}
		return acc;
	}
	
	/**
	 * checkAsset Applies size filters to an asset to determine if it should be extracted and added as a separate node.
	 * NOTE: In the future, these filters should be configurable. Initial release uses hard-coded file size thresholds.
	 * @param filename String
	 * @param filedata Blob
	 * @return Boolean true if asset should be extracted separately, false if asset can be ignored.
	 */
	public static boolean checkAsset(String filename, Blob filedata) {
		boolean acc = false;
		String ext = "";
		int fileKSize = filedata.getSize() / 1024;
		if (filename.indexOf(".")!=-1)
			ext = filename.substring(filename.lastIndexOf(".")+1);
		if (ext.equalsIgnoreCase("fla")||ext.equalsIgnoreCase("7z")||ext.equalsIgnoreCase("deb")||ext.equalsIgnoreCase("gz")||ext.equalsIgnoreCase("wmv")||
			ext.equalsIgnoreCase("pkg")||ext.equalsIgnoreCase("rar")||ext.equalsIgnoreCase("rpm")||ext.equalsIgnoreCase("sit")||ext.equalsIgnoreCase("sitx")||
			ext.equalsIgnoreCase("zip")||ext.equalsIgnoreCase("zipx")||ext.equalsIgnoreCase("csv")||ext.equalsIgnoreCase("dat")||ext.equalsIgnoreCase("efx")||
			ext.equalsIgnoreCase("epub")||ext.equalsIgnoreCase("gbr")||ext.equalsIgnoreCase("ged")||ext.equalsIgnoreCase("ibooks")||ext.equalsIgnoreCase("sdf")||
			ext.equalsIgnoreCase("tar")||ext.equalsIgnoreCase("tax2010")||ext.equalsIgnoreCase("vcf")||ext.equalsIgnoreCase("accdb")||ext.equalsIgnoreCase("db")||
			ext.equalsIgnoreCase("dbf")||ext.equalsIgnoreCase("mdb")||ext.equalsIgnoreCase("sql")||ext.equalsIgnoreCase("app")||ext.equalsIgnoreCase("bat")||
			ext.equalsIgnoreCase("cgi")||ext.equalsIgnoreCase("com")||ext.equalsIgnoreCase("exe")||ext.equalsIgnoreCase("gadget")||ext.equalsIgnoreCase("jar")||
			ext.equalsIgnoreCase("msi")||ext.equalsIgnoreCase("pif")||ext.equalsIgnoreCase("vb")||ext.equalsIgnoreCase("wsf")||ext.equalsIgnoreCase("fnt")||
			ext.equalsIgnoreCase("fon")||ext.equalsIgnoreCase("otf")||ext.equalsIgnoreCase("ttf")||ext.equalsIgnoreCase("3dm")||ext.equalsIgnoreCase("3ds")||
			ext.equalsIgnoreCase("dwg")||ext.equalsIgnoreCase("dxf")||ext.equalsIgnoreCase("max")||ext.equalsIgnoreCase("obj")||ext.equalsIgnoreCase("ai")||
			ext.equalsIgnoreCase("eps")||ext.equalsIgnoreCase("ps")||ext.equalsIgnoreCase("svg")||ext.equalsIgnoreCase("indd")||ext.equalsIgnoreCase("pct")||
			ext.equalsIgnoreCase("pdf")||ext.equalsIgnoreCase("xlr")||ext.equalsIgnoreCase("xls")||ext.equalsIgnoreCase("xlsx")||ext.equalsIgnoreCase("doc")||
			ext.equalsIgnoreCase("docx")||ext.equalsIgnoreCase("log")||ext.equalsIgnoreCase("msg")||ext.equalsIgnoreCase("odt")||ext.equalsIgnoreCase("pages")||
			ext.equalsIgnoreCase("rtf")||ext.equalsIgnoreCase("tex")||ext.equalsIgnoreCase("txt")||ext.equalsIgnoreCase("wpd")||ext.equalsIgnoreCase("wps")||
			ext.equalsIgnoreCase("3g2")||ext.equalsIgnoreCase("3gp")||ext.equalsIgnoreCase("asf")||ext.equalsIgnoreCase("asx")||ext.equalsIgnoreCase("avi")||
			ext.equalsIgnoreCase("flv")||ext.equalsIgnoreCase("mov")||ext.equalsIgnoreCase("mp4")||ext.equalsIgnoreCase("mpg")||ext.equalsIgnoreCase("rm")||
			ext.equalsIgnoreCase("srt")||ext.equalsIgnoreCase("swf")||ext.equalsIgnoreCase("vob")||ext.equalsIgnoreCase("rpf")||ext.equalsIgnoreCase("rlr")||
			ext.equalsIgnoreCase("rlk")||ext.equalsIgnoreCase("tcf")||ext.equalsIgnoreCase("tpf")||ext.equalsIgnoreCase("tdf")||ext.equalsIgnoreCase("tsz")||
			ext.equalsIgnoreCase("tcz"))
			acc = true;
		else if ((fileKSize>10)&&((ext.equalsIgnoreCase("aif")||ext.equalsIgnoreCase("iff")||ext.equalsIgnoreCase("m3u")||ext.equalsIgnoreCase("m4a")||
				 ext.equalsIgnoreCase("mid")||ext.equalsIgnoreCase("mp3")||ext.equalsIgnoreCase("mpa")||ext.equalsIgnoreCase("ra")||ext.equalsIgnoreCase("swa")||
				 ext.equalsIgnoreCase("wav")||ext.equalsIgnoreCase("wma"))))
			acc = true;
		else if ((fileKSize>50)&&((ext.equalsIgnoreCase("gif")||ext.equalsIgnoreCase("giff")||ext.equalsIgnoreCase("jpeg")||ext.equalsIgnoreCase("jpg")||ext.equalsIgnoreCase("png"))))
			acc = true;
		else if ((fileKSize>100)&&((ext.equalsIgnoreCase("key")||ext.equalsIgnoreCase("pps")||ext.equalsIgnoreCase("ppt")||ext.equalsIgnoreCase("pptx")||ext.equalsIgnoreCase("yuv")||
				 ext.equalsIgnoreCase("psd")||ext.equalsIgnoreCase("dds"))))
			acc = true;
		else if ((fileKSize>200)&&((ext.equalsIgnoreCase("pspimage")||ext.equalsIgnoreCase("tga")||ext.equalsIgnoreCase("tif")||ext.equalsIgnoreCase("tiff"))))
			acc = true;
		else if ((fileKSize>400)&&((ext.equalsIgnoreCase("bmp")||ext.equalsIgnoreCase("dng"))))
			acc = true;
		
		//extractAssetsFromPlainText(filedata);
		return acc;
	}
}