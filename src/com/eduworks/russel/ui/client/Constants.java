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

package com.eduworks.russel.ui.client;


public class Constants {
	public static final int DEFAULT_TIMEOUT				= 60000;
	public static final int WEIGHT_MULTIPLIER_WIDTH		= 100;
	public static final int WEIGHT_MULTIPLIER_HEIGHT	= 100;
	public static final int TIMEOUT						= 20000;
	public static final int RUSSEL_RESULT_MAX_WEIGHT	= 5;
	public static final int RUSSEL_RESULT_SIZE_DIVISOR	= 20;
	public final static String INCOMPLETE_FEATURE_MESSAGE = "The function you are attempting to use is not implemented.";
	public final static String UNSUPPORTED_IE_FEATURE = "The function you are trying to use is not available in Internet Explorer 7/8.";
	public final static String FOUO = "For Official Use Only (FOUO)";
	public final static String RUSSEL_ASPECTS = "russel:metaTest,cm:versionable";
	
	public final static native double roundNumber(double num, int places) /*-{
		var result = Math.round(num * Math.pow(10, places)) / Math.pow(10 , places);
		return result;
	}-*/;
}