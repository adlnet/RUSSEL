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

package com.eduworks.russel.ui.client.epss;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

/**
 * SCORMTemplates
 * Extends ClientBundle
 * Defines globals, methods for asset extraction
 * 
 * @author Eduworks Corporation
 */
public interface SCORMTemplates extends ClientBundle {
	public static final SCORMTemplates INSTANCE = GWT.create(SCORMTemplates.class);

	/**
	 * getImsmanifest Returns the text contents of the SCORM file called Imsmanifest.xml
	 * @return TextResource
	 */
	@Source("scorm/imsmanifest.xml")
	public TextResource getImsmanifest();
	
	@Source("scorm/initPage.html")
	public TextResource getInitPage();
}