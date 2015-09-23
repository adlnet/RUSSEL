///*
//Copyright 2012-2013 Eduworks Corporation
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//*/
package com.eduworks.russel.ui.client.net;

import org.vectomatic.file.Blob;

import com.eduworks.gwt.client.net.CommunicationHub;
import com.eduworks.gwt.client.net.MultipartPost;
import com.eduworks.gwt.client.net.callback.ESBCallback;
import com.eduworks.gwt.client.net.packet.ESBPacket;
import com.eduworks.russel.ui.client.handler.SearchHandler;
import com.eduworks.russel.ui.client.pagebuilder.screen.PermissionScreen;
import com.google.gwt.json.client.JSONArray;

public class RusselApi {
	public static final String GENERATE_LINK_METADATA = "link";
	public static final String GENERATE_FILE_METADATA = "file";
	public static String sessionId;
	public static String username;
	
	public static String getESBActionURL(String action) {
		return CommunicationHub.esbURL + action;
	}

	public static String createUser(String username, String password, ESBCallback<ESBPacket> callback) {
		MultipartPost mp = new MultipartPost();
		ESBPacket jo = new ESBPacket();
		jo.put("sessionid", sessionId);
		jo.put("username", username);
		jo.put("password", password);
		mp.appendMultipartFormData("session", jo);
		return CommunicationHub.sendMultipartPost(getESBActionURL("createUser"), mp, false, callback);
	}
	
	public static String deleteUser(String username, ESBCallback<ESBPacket> callback) {
		ESBPacket jo = new ESBPacket(); 
		MultipartPost mp = new MultipartPost();
		jo.put("sessionid", sessionId);
		jo.put("username", username);
		mp.appendMultipartFormData("session", jo);
		return CommunicationHub.sendMultipartPost(getESBActionURL("deleteUser"), mp, false, callback);
	}

	public static String getUsers(ESBCallback<ESBPacket> callback) {
		ESBPacket jo = new ESBPacket(); 
		MultipartPost mp = new MultipartPost();
		jo.put("sessionid", sessionId);
		mp.appendMultipartFormData("session", jo);
		return CommunicationHub.sendMultipartPost(getESBActionURL("getUsers"), mp, false, callback);
	}
	
	public static String getGroups(ESBCallback<ESBPacket> callback) {
		ESBPacket jo = new ESBPacket(); 
		MultipartPost mp = new MultipartPost();
		jo.put("sessionid", sessionId);
		mp.appendMultipartFormData("session", jo);
		return CommunicationHub.sendMultipartPost(getESBActionURL("getGroups"), mp, false, callback);
	}
	
	public static String toggleResourceSearch(String guid, String entityName, boolean add, ESBCallback<ESBPacket> callback) {
		ESBPacket jo = new ESBPacket(); 
		MultipartPost mp = new MultipartPost();
		jo.put("sessionid", sessionId);
		jo.put("resourceid", guid);
		jo.put("permissionname", entityName);
		mp.appendMultipartFormData("session", jo);
		if (add)
			return CommunicationHub.sendMultipartPost(getESBActionURL("enableResourceSearch"), mp, false, callback);
		else
			return CommunicationHub.sendMultipartPost(getESBActionURL("disableResourceSearch"), mp, false, callback);
	}
	
	public static String createGroup(String groupname, ESBCallback<ESBPacket> callback) {
		ESBPacket jo = new ESBPacket(); 
		MultipartPost mp = new MultipartPost();
		jo.put("sessionid", sessionId);
		jo.put("groupname", groupname);
		mp.appendMultipartFormData("session", jo);
		return CommunicationHub.sendMultipartPost(getESBActionURL("createGroup"), mp, false, callback);
	}
	
	public static String removeGroup(String groupname, ESBCallback<ESBPacket> callback) {
		ESBPacket jo = new ESBPacket(); 
		MultipartPost mp = new MultipartPost();
		jo.put("sessionid", sessionId);
		jo.put("groupname", groupname);
		mp.appendMultipartFormData("session", jo);
		return CommunicationHub.sendMultipartPost(getESBActionURL("deleteGroup"), mp, false, callback);
	}
	
	public static String getGroupMembers(String groupname, ESBCallback<ESBPacket> callback) {
		ESBPacket jo = new ESBPacket(); 
		MultipartPost mp = new MultipartPost();
		jo.put("sessionid", sessionId);
		jo.put("groupname", groupname);
		mp.appendMultipartFormData("session", jo);
		return CommunicationHub.sendMultipartPost(getESBActionURL("getGroupMembers"), mp, false, callback);
	}
	
	public static String addGroupMemberUser(String username, String groupname, ESBCallback<ESBPacket> callback) {
		ESBPacket jo = new ESBPacket(); 
		MultipartPost mp = new MultipartPost();
		jo.put("sessionid", sessionId);
		jo.put("groupname", groupname);
		jo.put("username", username);
		mp.appendMultipartFormData("session", jo);
		return CommunicationHub.sendMultipartPost(getESBActionURL("addGroupMemberUser"), mp, false, callback);
	}
	
	public static String removeGroupMemberUser(String username, String groupname, ESBCallback<ESBPacket> callback) {
		ESBPacket jo = new ESBPacket(); 
		MultipartPost mp = new MultipartPost();
		jo.put("sessionid", sessionId);
		jo.put("groupname", groupname);
		jo.put("username", username);
		mp.appendMultipartFormData("session", jo);
		return CommunicationHub.sendMultipartPost(getESBActionURL("deleteGroupMemberUser"), mp, false, callback);
	}
	
	public static String addGroupMemberGroup(String targetGroup, String groupname, ESBCallback<ESBPacket> callback) {
		ESBPacket jo = new ESBPacket(); 
		MultipartPost mp = new MultipartPost();
		jo.put("sessionid", sessionId);
		jo.put("groupname", groupname);
		jo.put("targetgroup", targetGroup);
		mp.appendMultipartFormData("session", jo);
		return CommunicationHub.sendMultipartPost(getESBActionURL("addGroupMemberGroup"), mp, false, callback);
	}
	
	public static String removeGroupMemberGroup(String targetGroup, String groupname, ESBCallback<ESBPacket> callback) {
		ESBPacket jo = new ESBPacket(); 
		MultipartPost mp = new MultipartPost();
		jo.put("sessionid", sessionId);
		jo.put("groupname", groupname);
		jo.put("targetgroup", targetGroup);
		mp.appendMultipartFormData("session", jo);
		return CommunicationHub.sendMultipartPost(getESBActionURL("deleteGroupMemberGroup"), mp, false, callback);
	}
	
	public static String getServicePermissions(ESBCallback<ESBPacket> callback) {
		ESBPacket jo = new ESBPacket(); 
		MultipartPost mp = new MultipartPost();
		jo.put("sessionid", sessionId);
		mp.appendMultipartFormData("session", jo);
		return CommunicationHub.sendMultipartPost(getESBActionURL("getServicePermissionLists"), mp, false, callback);
	}

	public static String resetUserPassword(String username, String password, ESBCallback<ESBPacket> callback) {
		ESBPacket jo = new ESBPacket(); 
		MultipartPost mp = new MultipartPost();
		jo.put("sessionid", sessionId);
		jo.put("username", username);
		jo.put("password", password);
		mp.appendMultipartFormData("session", jo);
		return CommunicationHub.sendMultipartPost(getESBActionURL("resetUserPassword"), mp, false, callback);
	}
	
	public static String removePermission(String permission, String source, String sourceType, String destination, String destinationType, ESBCallback<ESBPacket> callback) {
		ESBPacket jo = new ESBPacket(); 
		MultipartPost mp = new MultipartPost();
		String url = "";
		jo.put("sessionid", sessionId);
		jo.put("permissionname", permission);
		if (sourceType.equals(PermissionScreen.TYPE_USER)&&destinationType.equals(PermissionScreen.TYPE_USER)) {
			jo.put("username", source);
			jo.put("targetuser", destination);
			url = "removeUserUserPermissions";
		} else if (sourceType.equals(PermissionScreen.TYPE_USER)&&destinationType.equals(PermissionScreen.TYPE_GROUP)) {
			jo.put("username", source);
			jo.put("groupname", destination);
			url = "removeUserGroupPermissions";
		} else if (sourceType.equals(PermissionScreen.TYPE_USER)&&destinationType.equals(PermissionScreen.TYPE_RESOURCE)) {
			jo.put("username", source);
			jo.put("resourceid", destination);
			url = "removeUserResourcePermissions";
		} else if (sourceType.equals(PermissionScreen.TYPE_USER)&&destinationType.equals(PermissionScreen.TYPE_SERVICE)) {
			jo.put("username", source);
			jo.put("servicename", destination);
			url = "removeUserServicePermissions";
		} else if (sourceType.equals(PermissionScreen.TYPE_GROUP)&&destinationType.equals(PermissionScreen.TYPE_USER)) {
			jo.put("groupname", source);
			jo.put("username", destination);
			url = "removeGroupUserPermissions";
		} else if (sourceType.equals(PermissionScreen.TYPE_GROUP)&&destinationType.equals(PermissionScreen.TYPE_GROUP)) {
			jo.put("groupname", source);
			jo.put("targetgroup", destination);
			url = "removeGroupGroupPermissions";
		} else if (sourceType.equals(PermissionScreen.TYPE_GROUP)&&destinationType.equals(PermissionScreen.TYPE_RESOURCE)) {
			jo.put("groupname", source);
			jo.put("resourceid", destination);
			url = "removeGroupResourcePermissions";
		} else if (sourceType.equals(PermissionScreen.TYPE_GROUP)&&destinationType.equals(PermissionScreen.TYPE_SERVICE)) {
			jo.put("groupname", source);
			jo.put("servicename", destination);
			url = "removeGroupServicePermissions";
		} 
		mp.appendMultipartFormData("session", jo);
		return CommunicationHub.sendMultipartPost(getESBActionURL(url), mp, false, callback);
	}

	public static String addPermission(String permission, String source, String sourceType, String destination, String destinationType, ESBCallback<ESBPacket> callback) {
		ESBPacket jo = new ESBPacket(); 
		MultipartPost mp = new MultipartPost();
		String url = "";
		jo.put("sessionid", sessionId);
		jo.put("permissionname", permission);
		if (sourceType.equals(PermissionScreen.TYPE_USER)&&destinationType.equals(PermissionScreen.TYPE_USER)) {
			jo.put("username", source);
			jo.put("targetuser", destination);
			url = "addUserUserPermissions";
		} else if (sourceType.equals(PermissionScreen.TYPE_USER)&&destinationType.equals(PermissionScreen.TYPE_GROUP)) {
			jo.put("username", source);
			jo.put("groupname", destination);
			url = "addUserGroupPermissions";
		} else if (sourceType.equals(PermissionScreen.TYPE_USER)&&destinationType.equals(PermissionScreen.TYPE_RESOURCE)) {
			jo.put("username", source);
			jo.put("resourceid", destination);
			url = "addUserResourcePermissions";
		} else if (sourceType.equals(PermissionScreen.TYPE_USER)&&destinationType.equals(PermissionScreen.TYPE_SERVICE)) {
			jo.put("username", source);
			jo.put("servicename", destination);
			url = "addUserServicePermissions";
		} else if (sourceType.equals(PermissionScreen.TYPE_GROUP)&&destinationType.equals(PermissionScreen.TYPE_USER)) {
			jo.put("groupname", source);
			jo.put("username", destination);
			url = "addGroupUserPermissions";
		} else if (sourceType.equals(PermissionScreen.TYPE_GROUP)&&destinationType.equals(PermissionScreen.TYPE_GROUP)) {
			jo.put("groupname", source);
			jo.put("targetgroup", destination);
			url = "addGroupGroupPermissions";
		} else if (sourceType.equals(PermissionScreen.TYPE_GROUP)&&destinationType.equals(PermissionScreen.TYPE_RESOURCE)) {
			jo.put("groupname", source);
			jo.put("resourceid", destination);
			url = "addGroupResourcePermissions";
		} else if (sourceType.equals(PermissionScreen.TYPE_GROUP)&&destinationType.equals(PermissionScreen.TYPE_SERVICE)) {
			jo.put("groupname", source);
			jo.put("servicename", destination);
			url = "addGroupServicePermissions";
		} 
		mp.appendMultipartFormData("session", jo);
		return CommunicationHub.sendMultipartPost(getESBActionURL(url), mp, false, callback);
	}
	
	public static String getPermissions(String source, String sourceType, String destination, String destinationType, ESBCallback<ESBPacket> callback) {
		ESBPacket jo = new ESBPacket(); 
		MultipartPost mp = new MultipartPost();
		String url = "";
		jo.put("sessionid", sessionId);
		if (sourceType.equals(PermissionScreen.TYPE_USER)&&destinationType.equals(PermissionScreen.TYPE_USER)) {
			jo.put("username", source);
			jo.put("targetuser", destination);
			url = "getUserUserPermissions";
		} else if (sourceType.equals(PermissionScreen.TYPE_USER)&&destinationType.equals(PermissionScreen.TYPE_GROUP)) {
			jo.put("username", source);
			jo.put("groupname", destination);
			url = "getUserGroupPermissions";
		} else if (sourceType.equals(PermissionScreen.TYPE_USER)&&destinationType.equals(PermissionScreen.TYPE_RESOURCE)) {
			jo.put("username", source);
			jo.put("resourceid", destination);
			url = "getUserResourcePermissions";
		} else if (sourceType.equals(PermissionScreen.TYPE_USER)&&destinationType.equals(PermissionScreen.TYPE_SERVICE)) {
			jo.put("username", source);
			url = "getUserServicePermissions";
		} else if (sourceType.equals(PermissionScreen.TYPE_GROUP)&&destinationType.equals(PermissionScreen.TYPE_USER)) {
			jo.put("groupname", source);
			jo.put("username", destination);
			url = "getGroupUserPermissions";
		} else if (sourceType.equals(PermissionScreen.TYPE_GROUP)&&destinationType.equals(PermissionScreen.TYPE_GROUP)) {
			jo.put("groupname", source);
			jo.put("targetgroup", destination);
			url = "getGroupGroupPermissions";
		} else if (sourceType.equals(PermissionScreen.TYPE_GROUP)&&destinationType.equals(PermissionScreen.TYPE_RESOURCE)) {
			jo.put("groupname", source);
			jo.put("resourceid", destination);
			url = "getGroupResourcePermissions";
		} else if (sourceType.equals(PermissionScreen.TYPE_GROUP)&&destinationType.equals(PermissionScreen.TYPE_SERVICE)) {
			jo.put("groupname", source);
			url = "getGroupServicePermissions";
		} 
		mp.appendMultipartFormData("session", jo);
		return CommunicationHub.sendMultipartPost(getESBActionURL(url), mp, false, callback);
	}
	
	public static String checkSharedWith(String guid, String source, ESBCallback<ESBPacket> callback) {
		MultipartPost mp = new MultipartPost();
		ESBPacket p = new ESBPacket();
		p.put("sessionid", sessionId);
		p.put("resourceid", guid);
		p.put("source", source);
		mp.appendMultipartFormData("session", p);
		return CommunicationHub.sendMultipartPost(getESBActionURL("checkResourceSearch"), mp, false, callback);
	}
	
	private static String russelSearch(ESBPacket ap, ESBCallback<ESBPacket> callback) {
		MultipartPost mp = new MultipartPost();
		ESBPacket p = new ESBPacket();
		p.put("sessionid", sessionId);
		p.put("queryparameters", ap);
		mp.appendMultipartFormData("session", p);
		return CommunicationHub.sendMultipartPost(getESBActionURL("search"), mp, false, callback);
	}
	
	public static String search(ESBPacket ap, String searchType, ESBCallback<ESBPacket> callback) {
		if (searchType.equalsIgnoreCase(SearchHandler.SOURCE_LEARNING_REGISTRY))
			return lrSearch(ap.getString("q"), ap.getInteger("rows"), ap.getInteger("page"), callback);
		else
			return russelSearch(ap, callback);
	}
	
    public static String generateResourceMetadata(String resourceId, boolean url, ESBCallback<ESBPacket> callback) {
       MultipartPost mp = new MultipartPost();
       ESBPacket jo = new ESBPacket();
       jo.put("sessionid", sessionId);
       jo.put("type", url?GENERATE_LINK_METADATA:GENERATE_FILE_METADATA);
       jo.put("resourceid", resourceId);
       mp.appendMultipartFormData("session", jo);
       return CommunicationHub.sendMultipartPost(getESBActionURL("generateResourceMetadata"), mp, false, callback);
    }
   
   /**
    * Perform a basic search
    * @param searchTerm The search term
    * @param rows The number of rows to return
    * @param page The page to start retrieval
    * @return Returns the query result JSON string
    */
    public static String lrSearch(String searchTerm, int rows, int page, ESBCallback<ESBPacket> callback) {
        MultipartPost mp = new MultipartPost();
        ESBPacket jo = new ESBPacket();
        ESBPacket j = new ESBPacket();
        jo.put("sessionid", sessionId);
        j.put("searchterm", searchTerm);
        j.put("itemsperpage", rows);
        j.put("page", page);
        jo.put("queryparameters", j);
        mp.appendMultipartFormData("session", jo);
        return CommunicationHub.sendMultipartPost(getESBActionURL("lrSearch"), mp, false, callback);
    }

	public static String validateSession(ESBCallback<ESBPacket> callback) {
		MultipartPost mp = new MultipartPost();
		ESBPacket jo = new ESBPacket();
		jo.put("sessionid", sessionId);
		mp.appendMultipartFormData("session", jo);
		return CommunicationHub.sendMultipartPost(getESBActionURL("validateSession"), mp, false, callback);
	}

	public static String deleteResource(String guid, ESBCallback<ESBPacket> callback) {
		MultipartPost mp = new MultipartPost();
		ESBPacket jo = new ESBPacket();
		jo.put("sessionid", sessionId);
		jo.put("resourceid", guid);
		mp.appendMultipartFormData("session", jo);
		return CommunicationHub.sendMultipartPost(getESBActionURL("removeResource"), mp, false, callback);
	}

	public static String getComments(String guid, ESBCallback<ESBPacket> callback) {
		MultipartPost mp = new MultipartPost();
		ESBPacket jo = new ESBPacket();
		jo.put("sessionid", sessionId);
		jo.put("resourceid", guid);
		mp.appendMultipartFormData("session", jo);
		return CommunicationHub.sendMultipartPost(getESBActionURL("getComments"), mp, false, callback);
	}

	public static String getResourceMetadata(String guid, boolean countView, ESBCallback<ESBPacket> callback) {
		MultipartPost mp = new MultipartPost();
		ESBPacket jo = new ESBPacket();
		jo.put("sessionid", sessionId);
		jo.put("resourceid", guid);
		if (countView)
			jo.put("type", "count");
		mp.appendMultipartFormData("session", jo);
		return CommunicationHub.sendMultipartPost(getESBActionURL("getResourceMetadata"), mp, false, callback);
	}
	
	public static String updateResourceMetadata(String guid, ESBPacket object, ESBCallback<ESBPacket> callback) {
		MultipartPost mp = new MultipartPost();
		ESBPacket jo = new ESBPacket();
		jo.put("sessionid", sessionId);
		jo.put("resourceid", guid);
		jo.put("resourcemetadata", object);
		mp.appendMultipartFormData("session", jo);
		return CommunicationHub.sendMultipartPost(getESBActionURL("updateResourceMetadata"), mp, false, callback);
	}

	public static String getResource(String nodeId, boolean b, ESBCallback<ESBPacket> callback) {
		MultipartPost mp = new MultipartPost();
		ESBPacket jo = new ESBPacket();
		jo.put("sessionid", sessionId);
		jo.put("resourceid", nodeId);
		mp.appendMultipartFormData("session", jo);
		return CommunicationHub.sendMultipartPost(getESBActionURL("getResource"), mp, b, callback);
	}

	public static String exportZipPackage(JSONArray assetIds, ESBCallback<ESBPacket> callback) {
		MultipartPost mp = new MultipartPost();
		ESBPacket jo = new ESBPacket();
		jo.put("sessionid", sessionId);
		jo.put("resourceid", assetIds);
		mp.appendMultipartFormData("session", jo);
		return CommunicationHub.sendMultipartPost(getESBActionURL("zipResources"), mp, true, callback);
	}
	
	public static String updateResourceEpss(JSONArray changes, ESBCallback<ESBPacket> callback) {
		MultipartPost mp = new MultipartPost();
		ESBPacket jo = new ESBPacket();
		jo.put("sessionid", sessionId);
		jo.put("resourcemetadata", changes);
		mp.appendMultipartFormData("session", jo);
		return CommunicationHub.sendMultipartPost(getESBActionURL("updateResourceEpss"), mp, true, callback);
	}
	
	public static String downloadContentUrl(String guid, boolean countDL) {
		StringBuilder sb = new StringBuilder();
		sb.append("getResource?sessionid=" + sessionId + "&resourceid=" + guid);
		if (countDL)
			sb.append("&type=count");
		return getESBActionURL(sb.toString());
	}

	public static String uploadResource(Blob data, String filename, ESBCallback<ESBPacket> callback) {
		MultipartPost mp = new MultipartPost();
		ESBPacket jo = new ESBPacket();
		jo.put("sessionid", sessionId);
		mp.appendMultipartFormData("session", jo);
		mp.appendMultipartFileData(filename, data);
		return CommunicationHub.sendMultipartPost(getESBActionURL("addResource"), mp, false, callback);
	}
	
	public static String uploadResource(String data, String filename, ESBCallback<ESBPacket> callback) {
		MultipartPost mp = new MultipartPost();
		ESBPacket jo = new ESBPacket();
		jo.put("sessionid", sessionId);
		mp.appendMultipartFormData("session", jo);
		mp.appendMultipartFormData(filename, data);
		return CommunicationHub.sendMultipartPost(getESBActionURL("addResource"), mp, false, callback);
	}
	
	public static String updateResource(String guid, Blob data, String filename, ESBCallback<ESBPacket> callback) {
		MultipartPost mp = new MultipartPost();
		ESBPacket jo = new ESBPacket();
		jo.put("sessionid", sessionId);
		jo.put("resourceid", guid);
		mp.appendMultipartFormData("session", jo);
		mp.appendMultipartFileData(filename, data);
		return CommunicationHub.sendMultipartPost(getESBActionURL("updateResource"), mp, false, callback);
	}
	
	public static String updateResource(String guid, String data, String filename, ESBCallback<ESBPacket> callback) {
		MultipartPost mp = new MultipartPost();
		ESBPacket jo = new ESBPacket();
		jo.put("sessionid", sessionId);
		jo.put("resourceid", guid);
		mp.appendMultipartFormData("session", jo);
		mp.appendMultipartFormData(filename, data);
		return CommunicationHub.sendMultipartPost(getESBActionURL("updateResource"), mp, false, callback);
	}

	public static String addComment(String guid, String text, ESBCallback<ESBPacket> callback) {
		MultipartPost mp = new MultipartPost();
		ESBPacket jo = new ESBPacket();
		ESBPacket j = new ESBPacket();
		j.put("text", text);
		jo.put("commentparameters", j);
		jo.put("resourceid", guid);
		jo.put("sessionid", sessionId);
		mp.appendMultipartFormData("session", jo);
		return CommunicationHub.sendMultipartPost(getESBActionURL("addComment"), mp, false, callback);
	}
	
	public static String deleteComment(String resourceId, String commentId, ESBCallback<ESBPacket> callback) {
		MultipartPost mp = new MultipartPost();
		ESBPacket jo = new ESBPacket();
		jo.put("sessionid", sessionId);
		jo.put("resourceid", resourceId);
		jo.put("commentid", commentId);
		mp.appendMultipartFormData("session", jo);
		return CommunicationHub.sendMultipartPost(getESBActionURL("deleteComment"), mp, false, callback);
	}

	public static String rateObject(String guid, Integer rating, ESBCallback<ESBPacket> callback) {
		MultipartPost mp = new MultipartPost();
		ESBPacket jo = new ESBPacket();
		ESBPacket j = new ESBPacket();
		j.put("rating", rating);
		jo.put("ratingparameters", j);
		jo.put("resourceid", guid);
		jo.put("sessionid", sessionId);
		mp.appendMultipartFormData("session", jo);
		return CommunicationHub.sendMultipartPost(getESBActionURL("addRating"), mp, false, callback);
	}
	
	public static String publishToFlr(String guid, ESBPacket buildFlrDeleteNsdlPacket, ESBCallback<ESBPacket> callback) {
		MultipartPost mp = new MultipartPost();
		ESBPacket jo = new ESBPacket();
		jo.put("sessionid", sessionId);
		jo.put("resourceid", guid);
		jo.put("resourcemetadata", buildFlrDeleteNsdlPacket);
		mp.appendMultipartFormData("session", jo);
		return CommunicationHub.sendMultipartPost(getESBActionURL("publishToFlr"), mp, false, callback);
	}

	public static String login(String username, String password, ESBCallback<ESBPacket> callback) {
		MultipartPost mp = new MultipartPost();
		ESBPacket jo = new ESBPacket();
		jo.put("username", username);
		jo.put("password", password);
		mp.appendMultipartFormData("session", jo);
		return CommunicationHub.sendMultipartPost(getESBActionURL("login"), mp, false, callback);
	}
	
	public static String logout(ESBCallback<ESBPacket> callback) {
		MultipartPost mp = new MultipartPost();
		ESBPacket jo = new ESBPacket();
		jo.put("sessionid", sessionId);
		mp.appendMultipartFormData("session", jo);
		return CommunicationHub.sendMultipartPost(getESBActionURL("logout"), mp, false, callback);
	}
}
