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
package com.eduworks.russel.ui.client.handler;

import java.util.Vector;

import com.eduworks.gwt.client.net.callback.EventCallback;
import com.eduworks.gwt.client.net.packet.AlfrescoPacket;
import com.eduworks.gwt.client.net.packet.StatusPacket;
import com.eduworks.gwt.client.pagebuilder.PageAssembler;
import com.eduworks.gwt.client.ui.handler.DragDropHandler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;

public class StatusWindowHandler {
	private static final String statusItemTemplate = "<div id=\"x-status\" class=\"alert-box\"><span id=\"x-statusMessage\"></span><a id=\"x-statusClose\" class=\"close finger\">&times;</a></div>";
	private static DragDropHandler ddh = null;
	private static Vector<StatusPacket> messages = new Vector<StatusPacket>();
	public static int pendingFileUploads = 0;
	public static Vector<AlfrescoPacket> pendingServerZipUploads;
	public static Vector<AlfrescoPacket> pendingZipUploads;
	public static final String INVALID_FILENAME = "Invalid filename. Please try again.";
	public static final String INVALID_NAME = "Invalid name. Please try again.";
	public static final String DUPLICATE_NAME = "Can't have a duplicate name please try again";

	public static void initialize() {
		PageAssembler.attachHandler("statusClear", 
									Event.ONCLICK, 
									new EventCallback() {
										@Override
										public void onEvent(Event event) {
											clearMessages();
										}
									});

		pendingZipUploads = new Vector<AlfrescoPacket>();
		pendingServerZipUploads = new Vector<AlfrescoPacket>();
		clearMessages();
	}
	
	public static void addPendingServerZip(AlfrescoPacket packet) {
		pendingServerZipUploads.add(packet);
	}
	
	public static void addPendingZip (AlfrescoPacket packet) {
		pendingZipUploads.add(packet);
	}
	
	public static int countUploads() {
		int acc = 0;
		if (ddh!=null&&ddh.readQueue!=null)
			acc += ddh.readQueue.size();
		if (pendingZipUploads!=null)
			acc += pendingZipUploads.size();
		if (pendingServerZipUploads!=null)
			acc += pendingServerZipUploads.size();
		acc += pendingFileUploads;
		return acc;
	}
	
	public static String getHomeMessageError(String directory) {
		return "This user does not have a folder in <span class=\"fileName\">" + directory+ "</span>. Defaulting to company home for file uploads";
	}
	
	public static String getZipImportMessageError(String filename) {
		return "Failed to import zip entries in <span class=\"fileName\">" + filename + "</span>";
	}
	
	public static String getZipExportMessageError(String filename) {
		return "Failed to exported <span class=\"fileName\">" + filename + "</span>";
	}
	
	public static String getZipExportMessageDone(String filename) {
		return "Exported <span class=\"fileName\">" + filename + "</span>";
	}
	
	public static String getSearchMessageError(String terms) {
		return "Search failed on <span class=\"fileName\">" + terms + "</span> Please try again.";
	}
	
	public static String getFileMessageBusy(String filename) {
		return "Uploading <span class=\"fileName\">" +filename+ "</span> ... ";
	}
	
	public static String getFileMessageDone(String filename) {
		return "<span class=\"fileName\">" +filename+ "</span> is ready. ";
	}
	
	public static String getFileMessageError(String filename) {
		return "There was a problem loading <span class=\"fileName\">" +filename+ "</span> Please try again. ";
	}
	
	public static String getDeleteMessageError(String filename) {
		return "failed deleting <span class=\"fileName\">" +filename+ "</span>";
	}
	
	public static String getDeleteMessageDone(String filename) {
		return "Deleted <span class=\"fileName\">" +filename+ "</span>";
	}
	
	public static String getDeleteMessageBusy(String filename) {
		return "Deleting <span class=\"\">" +filename+ "</span>";
	}
	
	public static String getUpdateMetadataMessageError(String filename) {
		return "Failed to update <span class=\"fileName\">" +filename+ "</span> metadata.";
	}
	
	public static String getUpdateMetadataMessageDone(String filename) {
		return "Updated <span class=\"fileName\">" +filename+ "</span> metadata.";
	}
	
	public static String getUpdateMetadataMessageBusy(String filename) {
		return "Updating <span class=\"fileName\">" +filename+ "</span> metadata.";
	}
	
	public static String getMetadataMessageError(String filename) {
		return "Failed to get metadata for <span class=\"fileName\">" +filename+ "</span> metadata.";
	}
	
	public static String getProjectLoadMessageError(String filename) {
		return "Failed to load <span class=\"fileName\">" +filename+ "</span>";
	}
	
	public static String getProjectSaveMessageError(String filename) {
		return "Failed to save <span class=\"fileName\">" +filename+ "</span>";
	}
	
	public static String getCommentMessageError(String filename) {
		return "Failed to add a comment to <span class=\"fileName\">" +filename+ "</span>";
	}
	
	public static String getRemoveCommentMessageError(String filename) {
		return "Failed to remove comment from <span class=\"fileName\">" +filename+ "</span>";
	}
	
	public static String getRatingPostError(String filename) {
		return "Failed to add a rating to <span class=\"fileName\">" +filename+ "</span>";
	}
	
	public static String getRatingMessageError(String filename) {
		return "Failed to get the ratings for <span class=\"fileName\">" +filename+ "</span>";
	}
	
	public static String getRateOwnDocumentError(String filename) {
		return "Cannot rate <span class=\"fileName\">" +filename+ "</span> because you own it.";
	}
	
	public static String getFLRMessageBusy(String filename) {
		return "Posting <span class=\"fileName\">" +filename+ "</span> to FLR... ";
	}
	
	public static String getFLRMessageDone(String filename) {
		return "<span class=\"fileName\">" +filename+ "</span> is posted on FLR. ";
	}
	
	public static String getFLRMessageError(String filename) {
		return "There was a problem posting <span class=\"fileName\">" +filename+ "</span> to FLR. Please try again. ";
	}

	public static String getFLRActivityBusy(String filename) {
		return "Posting <span class=\"fileName\">" +filename+ "</span> activity to FLR... ";
	}
	
	public static String getFLRActivityDone(String filename) {
		return "<span class=\"fileName\">" +filename+ "</span> activity is posted on FLR. ";
	}
	
	public static String getFLRActivityError(String filename) {
		return "There was a problem posting <span class=\"fileName\">" +filename+ "</span> activity to FLR. Please try again. ";
	}

	public static String getFLRHarvestMessageBusy(String filename) {
		return "Harvesting the <span class=\"fileName\">" +filename+ "</span>... ";
	}
	
	public static String getFLRHarvestDone(String good, String bad, String ugly) {
		return "<span class=\"fileName\">FLR Harvest</span> complete.<br/> "+good+" records were duplicated, <br/>"+ugly+" partially duplicated, <br/>and "+bad+" had errors.";
	}
	
	public static String getFLRHarvestError() {
		return "<span class=\"fileName\">FLR Harvest</span> failed. Please try again.";
	}
	
	public static String getFLRDisabledError(String op) {
		return "<span class=\"fileName\"> FLR "+op+"</span> is currently disabled.";
	}
	
	public static String get3DRQueryMessageBusy() {
		return "Querying the <span class=\"fileName\">ADL 3D Repository</span>... ";
	}
	
	public static String get3DRQueryMessageDone() {
		return "<span class=\"fileName\">ADL 3DR</span> query complete.";
	}
	
	public static String get3DRQueryMessageError() {
		return "<span class=\"fileName\">ADL 3DR </span> query failed. Please try again.";
	}

	public static String get3DRQueryMessageEmpty() {
		return "<span class=\"fileName\">ADL 3DR </span> query is empty. Please provide search term.";
	}
	
	public static String get3DRReviewMessageDone(String op, String id) {
		return "<span class=\"fileName\">ADL 3DR</span> "+op+" of "+id+" complete.";
	}
	
	public static String get3DRReviewMessageError(String op, String id) {
		return "<span class=\"fileName\">ADL 3DR</span> "+op+" of "+id+" failed.";
	}
	
	public static String get3DRReviewMessageWarn() {
		return "<span class=\"fileName\">ADL 3DR</span> requires ratings to be submitted with comments.";
	}

	public static String get3DRDisabledError(String op) {
		return "<span class=\"fileName\"> 3DR "+op+"</span> is currently disabled.";
	}
	
	public static StatusPacket createMessage(String message, String initialState) {
		StatusPacket msg = StatusPacket.makePacket();
		msg.setMessage(message);
		msg.setState(initialState);
		msg.setRendered(false);
		messages.add(msg);
		refreshMessages();
		return msg;
	}
	
	public static void removeMessage(StatusPacket message) {
		DOM.getElementById(message.getStatusIdPrefix() + "-status").removeFromParent();
		messages.remove(message);
		refreshMessages();
	}
	
	private static void refreshMessages() {
		int messageCount = messages.size();
		for (int messageIndex=0;messageIndex<messageCount;messageIndex++) {
			if (!messages.get(messageIndex).getStatusRendered()) {
				Vector<String> ids = PageAssembler.inject("statusList", "x", new HTML(statusItemTemplate), true);
				String idNumPrefix = ids.get(0).substring(0, ids.get(0).indexOf("-"));
				final StatusPacket message = messages.get(messageIndex);
				message.setIdPrefix(idNumPrefix);
				PageAssembler.merge(idNumPrefix + "-statusMessage", "x", new HTML(message.getStatusMessage()).getElement());
				message.setRendered(true);
				if (message.getStatusState()!="")
					DOM.getElementById(idNumPrefix + "-status").addClassName(message.getStatusState());
				PageAssembler.attachHandler(idNumPrefix + "-statusClose", 
											Event.ONCLICK, 
											new EventCallback() {
												@Override
												public void onEvent(Event event) {
													removeMessage(message);
												}
											});
			}
		}
		if (messageCount>0) {
			DOM.getElementById("statusWindow").removeClassName("hidden");
			DOM.getElementById("count").setInnerText(String.valueOf(messageCount));
			DOM.getElementById("count").removeClassName("hidden");
		} else {
			DOM.getElementById("count").addClassName("hidden");
			DOM.getElementById("statusWindow").addClassName("hidden");
			hideStatusList0(DOM.getElementById("statusList"));
			DOM.getElementById("statusWindow").removeClassName("expand");
		}
		
		boolean hasError = false;
		boolean isBusy = false;
		for (int messageIndex=0;messageIndex<messageCount;messageIndex++)
			if (messages.get(messageIndex).getStatusState()==StatusPacket.ALERT_ERROR)
				hasError = true;
			else if (messages.get(messageIndex).getStatusState()==StatusPacket.ALERT_BUSY)
				isBusy = true;
		DOM.getElementById("icon").removeClassName(StatusPacket.STATUS_BUSY);
		DOM.getElementById("icon").removeClassName(StatusPacket.STATUS_ERROR);
		DOM.getElementById("icon").removeClassName(StatusPacket.STATUS_DONE);
		if (hasError)
			DOM.getElementById("icon").addClassName(StatusPacket.STATUS_ERROR);
		else if (isBusy)
			DOM.getElementById("icon").addClassName(StatusPacket.STATUS_BUSY);
		else
			DOM.getElementById("icon").addClassName(StatusPacket.STATUS_DONE);
	}
	
	public static void alterMessage(StatusPacket message) {
		int messageCount = messages.size();
		for (int messageIndex=0;messageIndex<messageCount;messageIndex++) {
			if (messages.get(messageIndex).getStatusIdPrefix()==message.getStatusIdPrefix()) {
				messages.get(messageIndex).setMessage(message.getStatusMessage());
				messages.get(messageIndex).setState(message.getStatusState());
				messages.get(messageIndex).setRendered(false);
				DOM.getElementById(message.getStatusIdPrefix() + "-status").getParentElement().removeFromParent();
			}
		}		
		refreshMessages();
	}
	
	private static native void hideStatusList0(Element element) /*-{
		element.style.display = "none";
	}-*/;
	
	public static void hookDropPanel(DragDropHandler dropHandler) {
		ddh = dropHandler;
	}
	
	public static void clearMessages() {
		int messageCount = DOM.getElementById("statusList").getChildCount();
		for (int messageIndex=0;messageIndex<messageCount;messageIndex++)
			DOM.getElementById("statusList").removeChild(DOM.getElementById("statusList").getChild(0));
		messages.clear();
		refreshMessages();
	}
}
