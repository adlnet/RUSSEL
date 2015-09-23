package com.eduworks.russel.ui.client.net;

import java.util.Date;

import com.eduworks.gwt.client.net.CommunicationHub;
import com.eduworks.gwt.client.net.packet.ESBPacket;
import com.eduworks.russel.ui.client.model.FLRRecord;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONString;

/**
 * Generates FLR payload packets (NSDL definition or LR Paradata 1.0) based on
 * the given FLRRecord
 * 
 * @author Tom Buskirk
 * 
 */
public class FLRPacketGenerator {

   private static final String PARADATA_SCHEMA = "LR Paradata 1.0";
   private static final String NSDL_SCHEMA = "NSDL DC 1.02.020";
   private static final String NONE_SCHEMA = "none";

   private static final String FLR_DOC_TYPE_KEY = "doc_type";
   private static final String FLR_DOC_VERSION_KEY = "doc_version";
   private static final String FLR_RESOURCE_DATA_TYPE_KEY = "resource_data_type";
   private static final String FLR_ACTIVE_KEY = "active";
   private static final String FLR_SUBMITTER_TYPE_KEY = "submitter_type";
   private static final String FLR_SUBMITTER_KEY = "submitter";
   private static final String FLR_CURATOR_KEY = "curator";
   private static final String FLR_OWNER_KEY = "owner";
   private static final String FLR_IDENTITY_KEY = "identity";
   private static final String FLR_PUB_NODE_KEY = "publishing_node";
   private static final String FLR_SUBMIS_TOS_KEY = "submission_TOS";
   private static final String FLR_TOS_KEY = "TOS";
   private static final String FLR_RESOURCE_LOCATOR_KEY = "resource_locator";
   private static final String FLR_PL_PLACEMENT_KEY = "payload_placement";
   private static final String FLR_PL_SCHEMA_KEY = "payload_schema";
   private static final String FLR_PL_SCHEMA_LOC_KEY = "payload_schema_locator";
   private static final String FLR_RESOURCE_DATA_KEY = "resource_data";
   private static final String FLR_REPLACES_KEY = "replaces";
   private static final String FLR_KEYWORDS_KEY = "keys";

   private static final String FLR_DOC_TYPE_VALUE = "resource_data";
   private static final String FLR_DOC_VERSION_VALUE = "0.49.0";
   private static final String FLR_RESOURCE_DATA_TYPE_METADATA_VALUE = "metadata";
   private static final String FLR_RESOURCE_DATA_TYPE_PARADATA_VALUE = "paradata";
   private static final String FLR_RESOURCE_DATA_TYPE_DELETE_VALUE = "none";
   private static final String FLR_PL_PLACEMENT_VALUE = "inline";
   private static final String FLR_PL_PLACEMENT_NONE_VALUE = "none";
   private static final boolean FLR_ACTIVE_VALUE = true;
   private static final String FLR_SUBMITTER_TYPE_VALUE = "agent";
   private static final String FLR_SUBMIS_TOS_VALUE = "http://www.learningregistry.org/tos/cc0/v0-5/";
   private static final String FLR_PL_SCHEMA_LOC_VALUE_NSDL = "http://ns.nsdl.org/schemas/nsdl_dc/nsdl_dc_v1.02.xsd";
   private static final String FLR_PL_SCHEMA_LOC_VALUE_ACT = "https://docs.google.com/a/eduworks.com/document/d/1IrOYXd3S0FUwNozaEG5tM7Ki4_AZPrBn-pbyVUz-Bh0/edit?hl=en_US#";

   private static final String PD_COLLECTION_KEY = "collection";
   private static final String PD_TOT_ITEMS_KEY = "totalItems";
   private static final String PD_ITEMS_KEY = "items";
   private static final String PD_ACTIVITY_KEY = "activity";
   private static final String PD_ACTOR_KEY = "actor";
   private static final String PD_VERB_KEY = "verb";
   private static final String PD_MEASURE_KEY = "measure";
   private static final String PD_CONTENT_KEY = "content";
   private static final String PD_VALUE_KEY = "value";
   private static final String PD_MEASURE_TYPE_KEY = "measureType";
   private static final String PD_SCALE_MIN_KEY = "scaleMin";
   private static final String PD_SCALE_MAX_KEY = "scaleMax";
   private static final String PD_SAMPLE_SIZE_KEY = "sampleSize";
   private static final String PD_CONTEXT_KEY = "context";
   private static final String PD_DATE_KEY = "date";
   // private static final String PD_RELATED_KEY = "related";
   private static final String PD_OBJ_TYPE_KEY = "objectType";
   private static final String PD_DESC_KEY = "description";
   private static final String PD_ACTION_KEY = "action";

   private static final String PD_STAR_AVERAGE_MEASURE_TYPE_VALUE = "star average";
   private static final String PD_COUNT_MEASURE_TYPE_VALUE = "count";
   private static final String PD_OBJ_TYPE_VALUE = "community";
   private static final int PD_SCALE_MIN_VALUE = 1;
   private static final int PD_SCALE_MAX_VALUE = 5;
   private static final String PD_RATINGS_ACTION_VALUE = "rated";
   private static final String PD_COMMENTS_ACTION_VALUE = "commented";
   // private static final String PD_ISD_ACTION_VALUE = "aligned";
   private static final String PD_CONTEXT_VALUE = "ADL RUSSEL repository";

   private static final String DEFAULT_FLR_SUBMITTER_VALUE = "ADL RUSSEL";
   private static final String DEFAULT_FLR_CURATOR_VALUE = "ADL RUSSEL";
   private static final String DEFAULT_FLR_PUB_NODE_VALUE = "RUSSEL";
   private static final String DEFAULT_PD_ACTOR_VALUE = "ADL RUSSEL user community";

   private static final String NSDL_DC_HEADER_TAG = "<nsdl_dc:nsdl_dc xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
         + " xmlns:dc=\"http://purl.org/dc/elements/1.1/\""
         + " xmlns:dct=\"http://purl.org/dc/terms/\""
         + " xmlns:ieee=\"http://www.ieee.org/xsd/LOMv1p0\""
         + " xmlns:nsdl_dc=\"http://ns.nsdl.org/nsdl_dc_v1.02/\" schemaVersion=\"1.02.020\""
         + " xsi:schemaLocation=\"http://ns.nsdl.org/nsdl_dc_v1.02/ http://ns.nsdl.org/schemas/nsdl_dc/nsdl_dc_v1.02.xsd\">";
   private static final String NSDL_DC_FOOTER_TAG = "</nsdl_dc:nsdl_dc>";


   // returns the given date as an ISO formatted string
   private static String getUtcIsoDateString(Date d) {
      return DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(d);
      // TimeZone tz = TimeZone.getTimeZone("UTC");
      // DateFormat df = new DateFormat() ("yyyy-MM-dd'T'HH:mm:ss'Z'");
      // df.setTimeZone(tz);
      // return df.format(d);
   }

   // returns the given date as yyyy-MM-dd formatted string
   private static String getParadataDateString(Date d) {
      return DateTimeFormat.getFormat("yyyy-MM-dd").format(d);
      // DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
      // return df.format(d);
   }

   // creates a dc dublin core element
   private static String createDcEntry(String key, String value) {
      if (value != null && !value.trim().isEmpty())
         return "<dc:" + key + ">" + value + "</dc:" + key + ">";
      else
         return "";
   }

   // builds the NSDL DC definition for the given FLR record
   private static String buildNsdlDcXml(FLRRecord flrRec) {
      StringBuffer nsdl = new StringBuffer();
      nsdl.append(NSDL_DC_HEADER_TAG);
      nsdl.append("<dc:identifier xsi:type=\"dct:URI\">" + getResourceUrl(flrRec) + "?id=" + flrRec.getGuid() + "</dc:identifier>");
      nsdl.append(createDcEntry("title", flrRec.getTitle()));
      nsdl.append(createDcEntry("description", flrRec.getDescription()));
      nsdl.append(createDcEntry("creator", flrRec.getPublisher()));
      nsdl.append(createDcEntry("language", flrRec.getLanguage()));
      if (flrRec.getSkill() != null && !flrRec.getSkill().trim().isEmpty()) nsdl.append("<dct:educationLevel xsi:type=\"nsdl_dc:NSDLEdLevel\">" + flrRec.getSkill() + "</dct:educationLevel>");
      nsdl.append(createDcEntry("format", flrRec.getMimeType()));
      nsdl.append(createDcEntry("date", getUtcIsoDateString(flrRec.getUploadDate())));
      nsdl.append(NSDL_DC_FOOTER_TAG);
      return nsdl.toString();
   }

   // generates an identity packet
   private static ESBPacket getIdentityPacket(String publisher, String submitter, String curator) {
      ESBPacket identPacket = new ESBPacket();
      identPacket.put(FLR_SUBMITTER_TYPE_KEY, FLR_SUBMITTER_TYPE_VALUE);
      identPacket.put(FLR_SUBMITTER_KEY, submitter);
      identPacket.put(FLR_CURATOR_KEY, curator);
      identPacket.put(FLR_OWNER_KEY, publisher);
      return identPacket;
   }

   // generates a TOS packet
   private static ESBPacket getTosPacket() {
      ESBPacket tosPacket = new ESBPacket();
      tosPacket.put(FLR_SUBMIS_TOS_KEY, FLR_SUBMIS_TOS_VALUE);
      return tosPacket;
   }

   // returns the resource URL
   private static String getResourceUrl(FLRRecord flrRec) {
      if (flrRec.getFlrResourceLocator() == null || flrRec.getFlrResourceLocator().trim().isEmpty()) {
         return CommunicationHub.siteURL + "?id=" + flrRec.getGuid();
      }
      else {
         return flrRec.getFlrResourceLocator();
      }
   }

   // adds the appropriate FLR payload schema information
   private static void addFlrPayloadSchemaInfo(String payloadSchemaType, ESBPacket flrPacket) {
      JSONArray psa = new JSONArray();
      if (PARADATA_SCHEMA.equalsIgnoreCase(payloadSchemaType)) {
         psa.set(0, new JSONString(PARADATA_SCHEMA));
         flrPacket.put(FLR_PL_SCHEMA_KEY, psa);
         flrPacket.put(FLR_PL_SCHEMA_LOC_KEY, FLR_PL_SCHEMA_LOC_VALUE_ACT);
      }
      else if (NONE_SCHEMA.equalsIgnoreCase(payloadSchemaType)) {}
      else {
         psa.set(0, new JSONString(NSDL_SCHEMA));
         flrPacket.put(FLR_PL_SCHEMA_KEY, psa);
         flrPacket.put(FLR_PL_SCHEMA_LOC_KEY, FLR_PL_SCHEMA_LOC_VALUE_NSDL);
      }
   }

   // generates some standard FLR packet info
   private static void addFlrHeaderInfo(ESBPacket flrPacket, String payloadSchemaType, String resourceDataType, FLRRecord flrRec, String publishingNode, String submitter, String curator) {   	  
      flrPacket.put(FLR_DOC_TYPE_KEY, FLR_DOC_TYPE_VALUE);
      flrPacket.put(FLR_DOC_VERSION_KEY, FLR_DOC_VERSION_VALUE);
      flrPacket.put(FLR_ACTIVE_KEY, FLR_ACTIVE_VALUE);
      flrPacket.put(FLR_IDENTITY_KEY, getIdentityPacket(flrRec.getPublisher(),submitter,curator));
      flrPacket.put(FLR_PUB_NODE_KEY, publishingNode);
      flrPacket.put(FLR_RESOURCE_DATA_TYPE_KEY, resourceDataType);
      flrPacket.put(FLR_TOS_KEY, getTosPacket());
      if (NONE_SCHEMA.equalsIgnoreCase(payloadSchemaType)) {
    	  flrPacket.put(FLR_PL_PLACEMENT_KEY, FLR_PL_PLACEMENT_NONE_VALUE);
      } else {
    	  flrPacket.put(FLR_RESOURCE_LOCATOR_KEY, getResourceUrl(flrRec));
    	  flrPacket.put(FLR_PL_PLACEMENT_KEY, FLR_PL_PLACEMENT_VALUE);
      }
      addFlrPayloadSchemaInfo(payloadSchemaType, flrPacket);
   }
   
   // generates keyword list if needed
   // this expects keywords to be in a comma separated string
   private static void addFlrKeywords(ESBPacket flrPacket, FLRRecord flrRec) {
      try {
         if (flrRec.getKeywords() == null || flrRec.getKeywords().trim().isEmpty()) return;
         String[] keys = flrRec.getKeywords().split(",");
         JSONArray ja = new JSONArray();
         int i = -1;
         for (String key:keys) {
            i++;
            ja.set(i,new JSONString(key));
         }
         flrPacket.put(FLR_KEYWORDS_KEY, ja);
      }
      catch (Exception e){}
   }

   // adds a replaces key/value if the given flrRec already contains an FLR
   // DOC_ID.
   // This will cause an 'update' of sorts. The old record will be 'tombstoned'
   // and a new record will be created with the updated data.
   private static void addFlrReplaces(ESBPacket flrPacket, FLRRecord flrRec, String scheme) {
	   String flrId = "";
	   if (FLR_RESOURCE_DATA_TYPE_METADATA_VALUE.equalsIgnoreCase(scheme))
		   flrId = flrRec.getFlrDocId();
	   else if (FLR_RESOURCE_DATA_TYPE_PARADATA_VALUE.equalsIgnoreCase(scheme))
		   flrId = flrRec.getFlrParadataId();
	   
	   if (flrId != null && !flrId.trim().isEmpty()) {
         JSONArray replacesArray = new JSONArray();
         replacesArray.set(0, new JSONString(flrId));
         flrPacket.put(FLR_REPLACES_KEY, replacesArray);
      }
   }

   // generates a paradata actor packet
   private static ESBPacket getActorPacket(String actor) {
      ESBPacket actorPacket = new ESBPacket();
      actorPacket.put(PD_OBJ_TYPE_KEY, PD_OBJ_TYPE_VALUE);
      JSONArray ja = new JSONArray();
      ja.set(0, new JSONString(actor));
      actorPacket.put(PD_DESC_KEY, ja);
      return actorPacket;
   }

   // generates the ratings activity paradata packet
   private static ESBPacket getRatingsActivityPacket(FLRRecord flrRec, String actor) {
      int numRatings = flrRec.getVotes();
      String avgRating = NumberFormat.getFormat("#.##").format(flrRec.getRating());
      ESBPacket measurePacket = new ESBPacket();
      measurePacket.put(PD_MEASURE_TYPE_KEY, PD_STAR_AVERAGE_MEASURE_TYPE_VALUE);
      measurePacket.put(PD_VALUE_KEY, avgRating);
      measurePacket.put(PD_SCALE_MIN_KEY, PD_SCALE_MIN_VALUE);
      measurePacket.put(PD_SCALE_MAX_KEY, PD_SCALE_MAX_VALUE);
      measurePacket.put(PD_SAMPLE_SIZE_KEY, numRatings);
      ESBPacket verbPacket = new ESBPacket();
      verbPacket.put(PD_ACTION_KEY, PD_RATINGS_ACTION_VALUE);
      verbPacket.put(PD_MEASURE_KEY, measurePacket);
      verbPacket.put(PD_CONTEXT_KEY, PD_CONTEXT_VALUE);
      verbPacket.put(PD_DATE_KEY, getParadataDateString(flrRec.getUploadDate()) + "/" + getParadataDateString(new Date()));
      ESBPacket ratingsActPacket = new ESBPacket();
      ratingsActPacket.put(PD_ACTOR_KEY, getActorPacket(actor));
      ratingsActPacket.put(PD_VERB_KEY, verbPacket);
      ratingsActPacket.put(PD_CONTENT_KEY, numRatings + " member(s) of the " + actor + " gave '" + flrRec.getFilename() +
                                           "' a rating of " + avgRating + " out of " + PD_SCALE_MAX_VALUE + " stars");
      ESBPacket retPacket = new ESBPacket();
      retPacket.put(PD_ACTIVITY_KEY, ratingsActPacket);
      return retPacket;
   }

   // generates the ratings activity paradata packet
   private static ESBPacket getCommentsActivityPacket(FLRRecord flrRec, String actor) {
      int numComments = (flrRec.getComments() != null) ? flrRec.getComments().keySet().size() : 0;
      ESBPacket measurePacket = new ESBPacket();
      measurePacket.put(PD_MEASURE_TYPE_KEY, PD_COUNT_MEASURE_TYPE_VALUE);
      measurePacket.put(PD_VALUE_KEY, numComments);
      ESBPacket verbPacket = new ESBPacket();
      verbPacket.put(PD_ACTION_KEY, PD_COMMENTS_ACTION_VALUE);
      verbPacket.put(PD_MEASURE_KEY, measurePacket);
      verbPacket.put(PD_CONTEXT_KEY, PD_CONTEXT_VALUE);
      verbPacket.put(PD_DATE_KEY, getParadataDateString(flrRec.getUploadDate()) + "/" + getParadataDateString(new Date()));
      ESBPacket commentsActPacket = new ESBPacket();
      commentsActPacket.put(PD_ACTOR_KEY, getActorPacket(actor));
      commentsActPacket.put(PD_VERB_KEY, verbPacket);
      commentsActPacket.put(PD_CONTENT_KEY, numComments + " member(s) of the " + actor + " commented on '" + flrRec.getFilename() + "'");
      ESBPacket retPacket = new ESBPacket();
      retPacket.put(PD_ACTIVITY_KEY, commentsActPacket);
      return retPacket;
   }

   @SuppressWarnings("unused")
   private static ESBPacket getIsdActivityPacket(FLRRecord flrRec, String actor) {
      // TODO further define this

      // FLR API entry
      // else if (type == FLR_ACTIVITY_ISD) {
      // FLRPacket payload = new FLRPacket();
      // payload.put("measureType", "count");
      // payload.put("value", feedback.getString("count"));
      // FLRPacket measure = new FLRPacket();
      // measure.put("action", "aligned");
      // measure.put("measure", payload);
      // measure.put("date", ap.getCreateDate() + "/" + dateStr.substring(0,
      // dateStr.indexOf('T')));
      // measure.put("context",
      // feedback.getString("template")+" instructional strategy");
      // fp.put("verb", measure);
      // FLRPacket object = new FLRPacket();
      // object.put("objectType", "Instructional Strategy");
      // object.put("description", feedback.getString("strategy"));
      // ja = new JSONArray();
      // ja.set(0, object);
      // fp.put("related", ja);
      // fp.put("content", "'"+ap.getTitle() +
      // "' has been aligned with the '"+feedback.getString("strategy")+"' part of the '"+
      // feedback.getString("template") +
      // "' template "+feedback.getString("count")+" time(s).");
      // }

      ESBPacket isdActPacket = new ESBPacket();
      isdActPacket.put(PD_ACTOR_KEY, getActorPacket(actor));

      ESBPacket retPacket = new ESBPacket();
      retPacket.put(PD_ACTIVITY_KEY, isdActPacket);
      return retPacket;
   }

   //builds the paradata activity packet
   private static ESBPacket buildActivityPacket(FLRRecord flrRec, String actor) {
      JSONArray actItemsArray = new JSONArray();
      actItemsArray.set(0, getRatingsActivityPacket(flrRec, actor));
      actItemsArray.set(1, getCommentsActivityPacket(flrRec, actor));
      // actItemsArray.set(2,getIsdActivityPacket(flrRec));
      ESBPacket itemsPacket = new ESBPacket();
      itemsPacket.put(PD_ITEMS_KEY, actItemsArray);
      itemsPacket.put(PD_TOT_ITEMS_KEY, actItemsArray.size());
      ESBPacket activityPacket = new ESBPacket();
      activityPacket.put(PD_COLLECTION_KEY, itemsPacket);
      return activityPacket;
   }

   /**
    * Builds and returns an FLR record metadata/description (NSDL Dublin Core) packet for the given FLRRecord.
    * 
    * @param flrRec The FLRRecord to base the packet on.
    * @param publishingNode The publishing node value.
    * @param submitter The submitter value.
    * @param curator The curator to value.
    * @return Returns an FLR record metadata/description (NSDL Dublin Core) packet for the given FLRRecord.
    */
   public static ESBPacket buildFlrNsdlPacket(FLRRecord flrRec, String publishingNode, String submitter, String curator) {
      ESBPacket retPacket = new ESBPacket();
      addFlrHeaderInfo(retPacket, NSDL_SCHEMA, FLR_RESOURCE_DATA_TYPE_METADATA_VALUE, flrRec, publishingNode, submitter, curator);
      addFlrKeywords(retPacket,flrRec);
      retPacket.put(FLR_RESOURCE_DATA_KEY, buildNsdlDcXml(flrRec));
      addFlrReplaces(retPacket, flrRec, FLR_RESOURCE_DATA_TYPE_METADATA_VALUE);
      return retPacket;
   }
   
   /**
    * Builds and returns an FLR record metadata/description (NSDL Dublin Core) packet for the given FLRRecord.
    * 
    * @param flrRec The FLRRecord to base the packet on.
    * @return Returns an FLR record metadata/description (NSDL Dublin Core) packet for the given FLRRecord.
    */
   public static ESBPacket buildFlrNsdlPacket(FLRRecord flrRec) {     
      return buildFlrNsdlPacket(flrRec,DEFAULT_FLR_PUB_NODE_VALUE,DEFAULT_FLR_SUBMITTER_VALUE,DEFAULT_FLR_CURATOR_VALUE);
   }

   /**
    * Builds and returns an FLR record paradata packet for the given FLRRecord.
    * 
    * @param flrRec The FLRRecord to base the packet on.
    * @param publishingNode The publishing node value.
    * @param submitter The submitter value.
    * @param curator The curator to value.
    * @param actor The actor value
    * @return Returns an FLR record paradata packet for the given FLRRecord.
    */
   public static ESBPacket buildFlrParadataPacket(FLRRecord flrRec, String publishingNode, String submitter, String curator, String actor) {
      ESBPacket retPacket = new ESBPacket();
      addFlrHeaderInfo(retPacket, PARADATA_SCHEMA, FLR_RESOURCE_DATA_TYPE_PARADATA_VALUE, flrRec, publishingNode, submitter, curator);
      retPacket.put(FLR_RESOURCE_DATA_KEY, buildActivityPacket(flrRec, actor));
      addFlrReplaces(retPacket, flrRec, FLR_RESOURCE_DATA_TYPE_PARADATA_VALUE);
      return retPacket;
   }
   
   /**
    * Builds and returns an FLR record paradata packet for the given FLRRecord.
    * 
    * @param flrRec The FLRRecord to base the packet on.
    * @return Returns an FLR record paradata packet for the given FLRRecord.
    */
   public static ESBPacket buildFlrParadataPacket(FLRRecord flrRec) {
      return buildFlrParadataPacket(flrRec,DEFAULT_FLR_PUB_NODE_VALUE,DEFAULT_FLR_SUBMITTER_VALUE,DEFAULT_FLR_CURATOR_VALUE,DEFAULT_PD_ACTOR_VALUE);
   }
   
   /**
    * Builds and returns an FLR record delete metadata packet for the given FLRRecord.
    * 
    * @param flrRec The FLRRecord to base the packet on.
    * @param publishingNode The publishing node value.
    * @param submitter The submitter value.
    * @param curator The curator to value.
    * @return Returns an FLR record paradata packet for the given FLRRecord.
    */
   public static ESBPacket buildFlrDeleteNsdlPacket(FLRRecord flrRec, String publishingNode, String submitter, String curator) {
	   ESBPacket retPacket = new ESBPacket();
	   addFlrHeaderInfo(retPacket, NONE_SCHEMA, FLR_RESOURCE_DATA_TYPE_DELETE_VALUE, flrRec, publishingNode, submitter, curator);
	   addFlrReplaces(retPacket, flrRec, FLR_RESOURCE_DATA_TYPE_METADATA_VALUE);
	   return retPacket;
   }
   
   /**
    * Builds and returns an FLR record delete metadata packet for the given FLRRecord.
    * 
    * @param flrRec The FLRRecord to base the packet on.
    * @return Returns an FLR record paradata packet for the given FLRRecord.
    */
   public static ESBPacket buildFlrDeleteNsdlPacket(FLRRecord flrRec) {
     return buildFlrDeleteNsdlPacket(flrRec,DEFAULT_FLR_PUB_NODE_VALUE,DEFAULT_FLR_SUBMITTER_VALUE,DEFAULT_FLR_CURATOR_VALUE);
   }
   
   /**
    * Builds and returns an FLR record delete paradata packet for the given FLRRecord.
    * 
    * @param flrRec The FLRRecord to base the packet on.
    * @param publishingNode The publishing node value.
    * @param submitter The submitter value.
    * @param curator The curator to value.
    * @return Returns an FLR record paradata packet for the given FLRRecord.
    */
   public static ESBPacket buildFlrDeleteParadataPacket(FLRRecord flrRec, String publishingNode, String submitter, String curator) {
	   ESBPacket retPacket = new ESBPacket();
	   addFlrHeaderInfo(retPacket, NONE_SCHEMA, FLR_RESOURCE_DATA_TYPE_DELETE_VALUE, flrRec, publishingNode, submitter, curator);
	   addFlrReplaces(retPacket, flrRec, FLR_RESOURCE_DATA_TYPE_PARADATA_VALUE);
	   return retPacket;
   }
   
   /**
    * Builds and returns an FLR record delete paradata packet for the given FLRRecord.
    * 
    * @param flrRec The FLRRecord to base the packet on.
    * @return Returns an FLR record paradata packet for the given FLRRecord.
    */
   public static ESBPacket buildFlrDeleteParadataPacket(FLRRecord flrRec) {
      return buildFlrDeleteParadataPacket(flrRec,DEFAULT_FLR_PUB_NODE_VALUE,DEFAULT_FLR_SUBMITTER_VALUE,DEFAULT_FLR_CURATOR_VALUE);
   }
}
