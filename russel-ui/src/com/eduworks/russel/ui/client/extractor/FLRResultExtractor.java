package com.eduworks.russel.ui.client.extractor;

import java.util.ArrayList;

import com.eduworks.russel.ui.client.model.RUSSELFileRecord;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

public class FLRResultExtractor {
   private static final String THUMBNAIL_SOURCE_PATTERN_PREFIX = "http://search.learningregistry.net/webcap/";
   private static final String THUMBNAIL_SOURCE_PATTERN_SUFFIX = "/145/screencap.jpg";

	public static RUSSELFileRecord[] walkBasicSearch(JSONArray array) {
		ArrayList<RUSSELFileRecord> results = new ArrayList<RUSSELFileRecord>();
		for (int i = 0; i < array.size(); i++) {
			RUSSELFileRecord r = new RUSSELFileRecord();
			JSONObject rR = array.get(i).isObject();
			if (rR.containsKey("_id")) {
				String id = rR.get("_id").isString().stringValue();
				r.setFlrDocId(id);
				r.setFilename(id + ".flr");
		
				r.setFileContents(rR.toString());
				r.setMimeType("application/json");
				
				if (rR.containsKey("_source")) {
					rR = rR.get("_source").isObject();
					if (rR.containsKey("title"))
						r.setTitle(rR.get("title").isString().stringValue());
					if (rR.containsKey("description"))
						r.setDescription(rR.get("description").isString().stringValue());
					if (rR.containsKey("url"))
						r.setFlrResourceLocator(rR.get("url").isString().stringValue());
					if (rR.containsKey("publisher"))
						r.setPublisher(rR.get("publisher").isString().stringValue());
					if (rR.containsKey("hasScreenshot")&&rR.get("hasScreenshot").isBoolean().booleanValue())
						r.setThumbnail(THUMBNAIL_SOURCE_PATTERN_PREFIX + id + THUMBNAIL_SOURCE_PATTERN_SUFFIX);
				}
			}

			results.add(r);
		}
		return results.toArray(new RUSSELFileRecord[results.size()]);
	}
	
}
