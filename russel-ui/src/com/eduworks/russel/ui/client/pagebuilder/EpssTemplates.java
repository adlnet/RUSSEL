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

package com.eduworks.russel.ui.client.pagebuilder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

/**
 * EpssTemplates class
 * Extends ClientBundle
 * Defines methods for retrieving EPSS template structures.
 * 
 * @author Eduworks Corporation
 */
public interface EpssTemplates extends ClientBundle {
	/**
	 * EpssTemplates Constructor for the class
	 */
	public static final EpssTemplates INSTANCE = GWT.create(EpssTemplates.class);

	/**
	 * getGagneTemplate Retrieves the structure and information from the Gagne template
	 * @return TextResource
	 */
	@Source("template/epssTemplate_gagne.tep")
	public TextResource getGagneTemplate();
	
	/**
	 * getSimulationTemplate Retrieves the structure and information from the Modified Simulation template
	 * @return TextResource
	 */
	@Source("template/epssTemplate_simulation.tep")
	public TextResource getSimulationTemplate();
}