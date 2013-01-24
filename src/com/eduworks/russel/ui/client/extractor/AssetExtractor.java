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

package com.eduworks.russel.ui.client.extractor;

import org.vectomatic.file.Blob;

public class AssetExtractor {
	public static final String IMAGE = "image";
	public static final String DOCUMENT = "document";
	public static final String COURSE = "course";
	public static final String VIDEO = "video";
	public static final String PROJECT = "project";
	public static final String LINK = "link";
	
	//private Vector<String> potentialAssets = new Vector<String>();
		
//	Text	.log
//	Text	.rtf
//	Text	.txt

	
	public static void extractAssetsFromPlainText(Blob filedata) {
		
	}
	
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
			else if (ext.equalsIgnoreCase("rlk"))
				acc = LINK;
			else
				acc = DOCUMENT;
		}
		return acc;
	}
	
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
			ext.equalsIgnoreCase("srt")||ext.equalsIgnoreCase("swf")||ext.equalsIgnoreCase("vob")||ext.equalsIgnoreCase("rpf")||ext.equalsIgnoreCase("rlk"))
			acc = true;
		else if ((fileKSize>10)&&(ext.equalsIgnoreCase("aif")||ext.equalsIgnoreCase("iff")||ext.equalsIgnoreCase("m3u")||ext.equalsIgnoreCase("m4a")||
				 ext.equalsIgnoreCase("mid")||ext.equalsIgnoreCase("mp3")||ext.equalsIgnoreCase("mpa")||ext.equalsIgnoreCase("ra")||ext.equalsIgnoreCase("swa")||
				 ext.equalsIgnoreCase("wav")||ext.equalsIgnoreCase("wma")))
			acc = true;
		else if ((fileKSize>50)&&(ext.equalsIgnoreCase("gif")||ext.equalsIgnoreCase("giff")||ext.equalsIgnoreCase("jpeg")||ext.equalsIgnoreCase("jpg")||ext.equalsIgnoreCase("png")))
			acc = true;
		else if ((fileKSize>100)&&(ext.equalsIgnoreCase("key")||ext.equalsIgnoreCase("pps")||ext.equalsIgnoreCase("ppt")||ext.equalsIgnoreCase("pptx")||ext.equalsIgnoreCase("yuv")||
				 ext.equalsIgnoreCase("psd")||ext.equalsIgnoreCase("dds")))
			acc = true;
		else if ((fileKSize>200)&&(ext.equalsIgnoreCase("pspimage")||ext.equalsIgnoreCase("tga")||ext.equalsIgnoreCase("tif")||ext.equalsIgnoreCase("tiff")))
			acc = true;
		else if ((fileKSize>400)&&(ext.equalsIgnoreCase("bmp")||ext.equalsIgnoreCase("dng")))
			acc = true;
		
		extractAssetsFromPlainText(filedata);
		return acc;
	}
}