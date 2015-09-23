package com.eduworks.russel.ui.client.model;

import java.util.Date;
import java.util.HashMap;

import com.eduworks.gwt.client.model.Record;
import com.eduworks.gwt.client.net.packet.ESBPacket;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

public abstract class FileRecord extends Record {
	public final static String FILENAME = "fileName_s";
	public final static String FILENAME2 = "fileName_t";
	public final static String DESCRIPTION = "description_txt_en";
	public final static String DESCRIPTION2 = "description_t";
	public final static String MIMETYPE = "mimeType_s";
	public final static String MIMETYPE2 = "mimeType_t";
	public final static String TITLE = "title_txt_en";
	public final static String TITLE2 = "title_t";
	public final static String CREATED_BY = "createdBy_s";
	public final static String CREATED_BY2 = "createdBy_t";
	public final static String PUBLISHER = "publisher_txt_en";
	public final static String PUBLISHER2 = "publisher_t";
	public final static String CLASSIFICATION = "classification_txt_en";
	public final static String CLASSIFICATION2 = "classification_t";
	public final static String ENVIRONMENT = "environment_txt_en";
	public final static String ENVIRONMENT2 = "environment_t";
	public final static String COVERAGE = "coverage_txt_en";
	public final static String COVERAGE2 = "coverage_t";
	public final static String LANGUAGE = "language_s";
	public final static String LANGUAGE2 = "language_t";
	public final static String TECHNICAL_REQUIREMENTS = "technicalRequirements_txt_en";
	public final static String TECHNICAL_REQUIREMENTS2 = "technicalRequirements_t";
	public final static String DISTRIBUTION = "distribution_txt_en";
	public final static String DISTRIBUTION2 = "distribution_t";
	public final static String VERSION = "version_s";
	public final static String VERSION2 = "version_t";
	public final static String THUMBNAIL = "thumbnail_s";
	public final static String THUMBNAIL2 = "thumbnail_t";
	public final static String PART_OF = "partOf_txt_en";
	public final static String PART_OF2 = "partOf_t";
	public final static String REQUIRES = "requires_txt_en";
	public final static String REQUIRES2 = "requires_t";
	public final static String OWNER = "uploadedBy_s";
	public final static String OWNER2 = "uploadedBy_t";
	public final static String GROUPS = "sharedBy_s";
	public final static String GROUPS2 = "sharedBy_t";
	public final static String INTERACTIVITY = "interactivity_txt_en";
	public final static String INTERACTIVITY2 = "interactivity_t";
	public final static String LEVEL = "level_s";
	public final static String LEVEL2 = "level_t";
	public final static String KEYWORDS = "keywords_txt_en";
	public final static String KEYWORDS2 = "keywords_t";
	public final static String OBJECTIVES = "attr_objective";
	public final static String OBJECTIVES2 = "objective_t";
	public final static String FILE_CONTENT = "fileContent_txt_en";
	public final static String FILE_CONTENT2 = "fileContent_t";
	public final static String FOUO = "fouo_b";
	public final static String COMMENTS = "comments";
	public final static String RATING = "rating_f";
	public final static String VOTES = "votes_l";
	public final static String SKILL = "skill_txt_en";
	public final static String SKILL2 = "skill_t";
	public final static String VIEW = "view_l";
	public final static String DOWNLOADS = "downloads_l";
	public final static String FILESIZE_BYTES = "fileSizeBytes_l";
	public final static String DURATION = "duration_f";
	public final static String UPDATED_DATE = "updatedDate_l";
	public final static String UPLOAD_DATE = "uploadDate_l";
	public final static String COMMENT_COUNT = "commentCount_l";
	
	public String getFieldList() {
		return FILENAME + " " + DESCRIPTION + " " + MIMETYPE + " " + TITLE + " " + CREATED_BY + " " + PUBLISHER + " " + CLASSIFICATION + " " +
			   ENVIRONMENT + " " + COVERAGE + " " + LANGUAGE + " " + TECHNICAL_REQUIREMENTS + " " + DISTRIBUTION + " " + VERSION + " " + THUMBNAIL + " " +
			   PART_OF + " " + REQUIRES + " " + OWNER + " "+ GROUPS + " " + INTERACTIVITY + " " + LEVEL + " " + KEYWORDS + " " + OBJECTIVES + " " + FILE_CONTENT + " " +
			   FOUO + " " + SKILL + " " + VIEW + " " + DOWNLOADS + " " + FILESIZE_BYTES + " " + DURATION + " " + UPLOAD_DATE + " " + UPDATED_DATE + " " + RATING + " " + 
			   VOTES + " " + COMMENT_COUNT;
	}
	
	private String filename = "";
	private String description = "";
	private String mimeType = "";
	private String title = "";
	private String createdBy = "";
	private String publisher = "";
	private String classification = "";
	private String activity = "";
	private String environment = "";
	private String coverage = "";
	private String language = "";
	private String technicalRequirements = "";
	private String distribution = "";
	private String version = "";
	private String thumbnail = "";
	private String skill = "";
	private String partOf = "";
	private String requires = "";
	private String owner = "";
	private String groups = "";
	private String interactivity = "";
	private String level = "";
	private String keywords = "";
	private String updatedDateStr = "";
	private String fileContents = "";
	private String uploadDateStr = "";
	private Boolean fouo = false;
	
	private HashMap<String, CommentRecord> comments = new HashMap<String, CommentRecord>();

	private JSONArray objectives = new JSONArray();
	private int votes = 0;	
	private int view = 0;
	private int downloads = 0;
	private int filesize = 0;
	private double duration = 0.0;
	private int commentCount = 0;
	private double rating = 2.5;
	
	public FileRecord() {
		
	}	
	
	public FileRecord(ESBPacket metaDataPack) {
		parseESBPacket(metaDataPack);
	}
	
	public void parseESBPacket(ESBPacket metaDataPack) {
		ESBPacket esbPacket;
		if (metaDataPack.containsKey("obj"))
			esbPacket = new ESBPacket(metaDataPack.get("obj").isObject());
		else
			esbPacket = metaDataPack;
		if (esbPacket.containsKey(FILENAME))
			filename = esbPacket.getString(FILENAME);
		if (esbPacket.containsKey(FILENAME2))
			filename = esbPacket.getString(FILENAME2);
		if (esbPacket.containsKey(ID))
			setGuid(esbPacket.getString(ID));
		if (esbPacket.containsKey(DESCRIPTION))
			description = esbPacket.getString(DESCRIPTION);
		if (esbPacket.containsKey(DESCRIPTION2))
			description = esbPacket.getString(DESCRIPTION2);
		if (esbPacket.containsKey(MIMETYPE))
			mimeType = esbPacket.getString(MIMETYPE);
		if (esbPacket.containsKey(MIMETYPE2))
			mimeType = esbPacket.getString(MIMETYPE2);
		if (esbPacket.containsKey(TITLE))
			title = esbPacket.getString(TITLE);
		if (esbPacket.containsKey(TITLE2))
			title = esbPacket.getString(TITLE2);
		if (esbPacket.containsKey(CREATED_BY))
			createdBy = esbPacket.getString(CREATED_BY);
		if (esbPacket.containsKey(CREATED_BY2))
			createdBy = esbPacket.getString(CREATED_BY2);
		if (esbPacket.containsKey(PUBLISHER))
			publisher = esbPacket.getString(PUBLISHER);
		if (esbPacket.containsKey(PUBLISHER2))
			publisher = esbPacket.getString(PUBLISHER2);
		if (esbPacket.containsKey(CLASSIFICATION))
			classification = esbPacket.getString(CLASSIFICATION);
		if (esbPacket.containsKey(CLASSIFICATION2))
			classification = esbPacket.getString(CLASSIFICATION2);
		if (esbPacket.containsKey(ENVIRONMENT))
			environment = esbPacket.getString(ENVIRONMENT);
		if (esbPacket.containsKey(ENVIRONMENT2))
			environment = esbPacket.getString(ENVIRONMENT2);
		if (esbPacket.containsKey(COVERAGE))
			coverage = esbPacket.getString(COVERAGE);
		if (esbPacket.containsKey(COVERAGE2))
			coverage = esbPacket.getString(COVERAGE2);
		if (esbPacket.containsKey(LANGUAGE))
			language = esbPacket.getString(LANGUAGE);
		if (esbPacket.containsKey(LANGUAGE2))
			language = esbPacket.getString(LANGUAGE2);
		if (esbPacket.containsKey(TECHNICAL_REQUIREMENTS))
			technicalRequirements = esbPacket.getString(TECHNICAL_REQUIREMENTS);
		if (esbPacket.containsKey(TECHNICAL_REQUIREMENTS2))
			technicalRequirements = esbPacket.getString(TECHNICAL_REQUIREMENTS2);
		if (esbPacket.containsKey(DISTRIBUTION))
			distribution = esbPacket.getString(DISTRIBUTION);
		if (esbPacket.containsKey(DISTRIBUTION2))
			distribution = esbPacket.getString(DISTRIBUTION2);
		if (esbPacket.containsKey(VERSION))
			version = esbPacket.getString(VERSION);
		if (esbPacket.containsKey(VERSION2))
			version = esbPacket.getString(VERSION2);
		if (esbPacket.containsKey(THUMBNAIL))
			thumbnail = esbPacket.getString(THUMBNAIL);
		if (esbPacket.containsKey(THUMBNAIL2))
			thumbnail = esbPacket.getString(THUMBNAIL2);
		if (esbPacket.containsKey(PART_OF))
			partOf = esbPacket.getString(PART_OF);
		if (esbPacket.containsKey(PART_OF2))
			partOf = esbPacket.getString(PART_OF2);
		if (esbPacket.containsKey(REQUIRES))
			requires = esbPacket.getString(REQUIRES);
		if (esbPacket.containsKey(REQUIRES2))
			requires = esbPacket.getString(REQUIRES2);
		if (esbPacket.containsKey(OWNER))
			owner = esbPacket.getString(OWNER);
		if (esbPacket.containsKey(OWNER2))
			owner = esbPacket.getString(OWNER2);
		if (esbPacket.containsKey(GROUPS))
			groups = esbPacket.getString(GROUPS);
		if (esbPacket.containsKey(GROUPS2))
			groups = esbPacket.getString(GROUPS2);
		if (esbPacket.containsKey(INTERACTIVITY))
			interactivity = esbPacket.getString(INTERACTIVITY);
		if (esbPacket.containsKey(INTERACTIVITY2))
			interactivity = esbPacket.getString(INTERACTIVITY2);
		if (esbPacket.containsKey(LEVEL))
			level = esbPacket.getString(LEVEL);
		if (esbPacket.containsKey(LEVEL2))
			level = esbPacket.getString(LEVEL2);
		if (esbPacket.containsKey(KEYWORDS))
			keywords = esbPacket.getString(KEYWORDS);
		if (esbPacket.containsKey(KEYWORDS2))
			keywords = esbPacket.getString(KEYWORDS2);
		if (esbPacket.containsKey(OBJECTIVES))
			objectives = convertToObject(esbPacket.getArray(OBJECTIVES));
		if (esbPacket.containsKey(OBJECTIVES2))
			objectives = convertToObject(esbPacket.getArray(OBJECTIVES2));
		if (esbPacket.containsKey(RATING))
			rating = esbPacket.get(RATING).isString()!=null?Double.parseDouble(esbPacket.getString(RATING)):esbPacket.getDouble(RATING);
		if (esbPacket.containsKey(VOTES))
			votes = esbPacket.get(VOTES).isString()!=null?Integer.parseInt(esbPacket.getString(VOTES)):esbPacket.getInteger(VOTES);
		if (esbPacket.containsKey(COMMENT_COUNT))
			commentCount = esbPacket.get(COMMENT_COUNT).isString()!=null?Integer.parseInt(esbPacket.getString(COMMENT_COUNT)):esbPacket.getInteger(COMMENT_COUNT);
		if (esbPacket.containsKey(SKILL))
			skill = esbPacket.getString(SKILL);
		if (esbPacket.containsKey(SKILL2))
			skill = esbPacket.getString(SKILL2);
		if (esbPacket.containsKey(UPLOAD_DATE))
			uploadDateStr = esbPacket.getString(UPLOAD_DATE);
		if (esbPacket.containsKey(UPDATED_DATE))
	         setUpdatedDateStr(esbPacket.getString(UPDATED_DATE));
		if (esbPacket.containsKey(FOUO)) {
			if (esbPacket.get(FOUO).isBoolean()!=null)
				fouo = esbPacket.getBoolean(FOUO);
			else if (esbPacket.get(FOUO).isString()!=null)
				fouo = Boolean.parseBoolean(esbPacket.getString(FOUO));
		}
		if (esbPacket.containsKey(FILESIZE_BYTES)) {
			if (esbPacket.get(FILESIZE_BYTES).isNumber()!=null)
				filesize = esbPacket.getInteger(FILESIZE_BYTES);
			else if (esbPacket.get(FILESIZE_BYTES).isString()!=null)
				filesize = Integer.valueOf(esbPacket.getString(FILESIZE_BYTES));
		}
		if (esbPacket.containsKey(DURATION)) {
			if (esbPacket.get(DURATION).isNumber()!=null)
				duration = esbPacket.getDouble(DURATION);
			else if (esbPacket.get(DURATION).isString()!=null)
				duration = Double.valueOf(esbPacket.getString(DURATION));
		}
	}
	

	public String getUploadDateStr() {
	   return uploadDateStr;
	}
	
	public void setUploadDateStr(String dateStr) {
		uploadDateStr = dateStr;
	}
	
	/**
	 *Levr stores the date as a long I believe; 
	 */
	//TODO verify this
	public Date getUploadDate() {
	   try {
	      Date d = new Date();
	      d.setTime(Long.valueOf(getUploadDateStr()));
	      return d;
	   }
	   catch (Exception e) {
	      return null;
	   }
	}
	
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getGroups() {
		return groups;
	}
	
	public void setGroups(String groups) {
		this.groups = groups;
	}
	
	public String getInteractivity() {
		return interactivity;
	}

	public void setInteractivity(String interactivity) {
		this.interactivity = interactivity;
	}
	
	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getRequires() {
		return requires;
	}

	public void setRequires(String requires) {
		this.requires = requires;
	}
	
	public String getPartOf() {
		return partOf;
	}

	public void setPartOf(String partOf) {
		this.partOf = partOf;
	}	
	
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	
	public String getThumbnailURL() {
		return this.thumbnail;
	}
	
	public void setFOUO(Boolean fouo) {
		this.fouo = fouo;
	}
	
	public Boolean getFOUO() {
		return this.fouo;
	}
	
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public String getFilename() {
		return this.filename;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return this.description;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}
	
	public HashMap<String, CommentRecord> getComments() {
		return this.comments;
	}
	
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	
	public String getMimeType() {
		return this.mimeType;
	}
	
	public void setCreateBy(String createBy) {
		this.createdBy = createBy;
	}
	
	public String getCreateBy() {
		return this.createdBy;
	}

	public int getCommentCount() {
		return commentCount;
	}
	
	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}
	
	public int getView() {
		return view;
	}

	public void setView(int view) {
		this.view = view;
	}

	public int getDownloads() {
		return downloads;
	}

	public void setDownloads(int downloads) {
		this.downloads = downloads;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public int getVotes() {
		return votes;
	}

	public void setVotes(int votes) {
		this.votes = votes;
	}

	public double getRating() {
		return this.rating;
	}
	
	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getCoverage() {
		return coverage;
	}

	public void setCoverage(String coverage) {
		this.coverage = coverage;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	public String getTechnicalRequirements() {
		return technicalRequirements;
	}

	public void setTechnicalRequirements(String technicalRequirements) {
		this.technicalRequirements = technicalRequirements;
	}

	public String getDistribution() {
		return distribution;
	}

	public void setDistribution(String distribution) {
		this.distribution = distribution;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getFilesize() {
		return filesize;
	}

	public void setFilesize(int filesize) {
		this.filesize = filesize;
	}

	public void setComments(HashMap<String, CommentRecord> comments) {
		this.comments = comments;
	}
	
	public void addComments(CommentRecord commentRecord) {
		this.comments.put(commentRecord.getGuid(), commentRecord);
	}
	
	public void parseComments(ESBPacket esbPacket) {
		JSONArray commentsObject = esbPacket.getArray("obj");
		if (commentsObject!=null)
			for (int i = 0; i < commentsObject.size(); i++) {
				CommentRecord cr = new CommentRecord(new ESBPacket(commentsObject.get(i).isObject()));
				this.comments.put(cr.getGuid(), cr);
			}
	}

	public JSONArray getObjectives() {
		return objectives;
	}

	public void setObjectives(JSONArray objectives) {
		this.objectives = objectives;
	}

	public String getSkill() {
		return skill;
	}

	public void setSkill(String skill) {
		this.skill = skill;
	}

	public String getFileContents() {
		return fileContents;
	}

	public void setFileContents(String fileContents) {
		this.fileContents = fileContents;
	}

	public String getUpdatedDateStr() {
		return updatedDateStr;
	}

	public void setUpdatedDateStr(String updatedDateStr) {
		this.updatedDateStr = updatedDateStr;
	}
	
	@Override
	public String toString() {
		return toObject().toString();
	}
	
	public JSONArray convertToObject(JSONArray ja) {
		JSONArray acc = new JSONArray();
		JSONObject jo;
		for (int i = 0; i < ja.size(); i++) {
			if (ja.get(i).isString()!=null) {
				String titleAndDescription = ja.get(i).isString().stringValue();
				String title = titleAndDescription.substring(0, titleAndDescription.indexOf("<|>"));
				String description = titleAndDescription.substring(titleAndDescription.indexOf("<|>")+"<|>".length());
				jo = new JSONObject();
				jo.put("title", new JSONString(title));
				jo.put("description", new JSONString(description));
			} else
				jo = ja.get(i).isObject();
			
			acc.set(i, jo);
		}
		return acc;
	}
	
	public JSONArray convertToReadable(JSONArray ja) {
		JSONArray acc = new JSONArray();
		JSONString s;
		for (int i = 0; i < ja.size(); i++) {
			if (ja.get(i).isObject()!=null) {
				JSONObject jo = ja.get(i).isObject();
				s = new JSONString(jo.get("title").isString().stringValue() + " <|> " + jo.get("description").isString().stringValue());
			} else
				s = ja.get(i).isString();
			acc.set(i, s);
		}
		return acc;
	}
	
	public ESBPacket toObject() {
		ESBPacket esbPacket = new ESBPacket();
		esbPacket.put(FILENAME, filename);
		esbPacket.put(DESCRIPTION, description);
		esbPacket.put(MIMETYPE, mimeType);
		esbPacket.put(TITLE, title);
		esbPacket.put(CREATED_BY, createdBy);
		esbPacket.put(PUBLISHER, publisher);
		esbPacket.put(CLASSIFICATION, classification);
		esbPacket.put(ENVIRONMENT, environment);
		esbPacket.put(COVERAGE, coverage);
		esbPacket.put(LANGUAGE, language);
		esbPacket.put(TECHNICAL_REQUIREMENTS, technicalRequirements);
		esbPacket.put(DISTRIBUTION, distribution);
		esbPacket.put(VERSION, version);
		esbPacket.put(THUMBNAIL, thumbnail);
		esbPacket.put(PART_OF, partOf);
		esbPacket.put(REQUIRES, requires);
		esbPacket.put(OWNER, owner);
		esbPacket.put(GROUPS, groups);
		esbPacket.put(INTERACTIVITY, interactivity);
		esbPacket.put(LEVEL, level);
		esbPacket.put(KEYWORDS, keywords);
		esbPacket.put(OBJECTIVES, convertToReadable(objectives));
		esbPacket.put(FILE_CONTENT, fileContents);
		esbPacket.put(FOUO, fouo);
		esbPacket.put(SKILL, skill);
		esbPacket.put(VIEW, view);
		esbPacket.put(DOWNLOADS, downloads);
		esbPacket.put(FILESIZE_BYTES,filesize);
		esbPacket.put(ID, getGuid());
		esbPacket.put(DURATION, duration);
		esbPacket.put(UPLOAD_DATE, uploadDateStr);
		esbPacket.put(UPDATED_DATE, updatedDateStr);
		return esbPacket;
	}
}
