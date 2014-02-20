package com.cd.reddit.json.mapping;

import java.util.List;

import org.codehaus.jackson.JsonNode;

/**
 * Implements the Java bean version of the JSON found <a href="https://github.com/reddit/reddit/wiki/JSON#more">here</a>.
 * 
 * @author <a href="https://github.com/reddit/reddit/wiki/JSON#message-implements-created">Cory Dissinger</a>
 */

public class RedditMore {
	private int count;
	private String parent_id;
	private String subreddit_id;
	private String subreddit;
	private String likes;
	private JsonNode replies;
	private String saved;
	private String body;
	private String edited;
	private String author_flair_css_class;
	private String ups;
	private String downs;
	private String body_html;
	private String link_id;
	private String score_hidden;
	private String score;
	private String created;
	private String author_flair_text;
	private String created_utc;
	private String distinguished;
	private String num_reports;


	private String approved_by;
	private String gilded;
	private String author;
	private String banned_by;
	private String id;
	private String name;
	private List<String> children;
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getParent_id() {
		return parent_id;
	}
	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSubreddit_id() {
		return subreddit_id;
	}
	public void setSubreddit_id(String name) {
		this.subreddit_id = name;
	}
	
	public String getSubreddit() {
		return subreddit;
	}
	public void setSubreddit(String name) {
		this.subreddit = name;
	}
	
	
	public String getBanned_by() {
		return banned_by;
	}
	public void setBanned_by(String name) {
		this.banned_by = name;
	}
	
	public String getLikes() {
		return likes;
	}
	public void setLikes(String name) {
		this.likes = name;
	}
	
	public JsonNode getReplies() {
		return replies;
	}
	public void setReplies(JsonNode name) {
		this.replies = name;
	}
	
	public String getSaved() {
		return saved;
	}
	public void setSaved(String name) {
		this.saved = name;
	}
	
	
	public String getGilded() {
		return gilded;
	}
	public void setGilded(String name) {
		this.gilded = name;
	}	
	
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String name) {
		this.author = name;
	}	
	
	public String getBody() {
		return body;
	}
	public void setBody(String name) {
		this.body = name;
	}
	
	
	
	public String getApproved_by() {
		return approved_by;
	}
	public void setApproved_by(String name) {
		this.approved_by = name;
	}
	
	
	
	public List<String> getChildren() {
		return children;
	}
	public void setChildren(List<String> children) {
		this.children = children;
	}
	public String getEdited() {
		return edited;
	}
	public void setEdited(String edited) {
		this.edited = edited;
	}
	public String getAuthor_flair_css_class() {
		return author_flair_css_class;
	}
	public void setAuthor_flair_css_class(String author_flair_css_class) {
		this.author_flair_css_class = author_flair_css_class;
	}
	public String getUps() {
		return ups;
	}
	public void setUps(String ups) {
		this.ups = ups;
	}
	public String getDowns() {
		return downs;
	}
	public void setDowns(String downs) {
		this.downs = downs;
	}
	public String getBody_html() {
		return body_html;
	}
	public void setBody_html(String body_html) {
		this.body_html = body_html;
	}
	public String getLink_id() {
		return link_id;
	}
	public void setLink_id(String link_id) {
		this.link_id = link_id;
	}
	public String getScore_hidden() {
		return score_hidden;
	}
	public void setScore_hidden(String score_hidden) {
		this.score_hidden = score_hidden;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public String getAuthor_flair_text() {
		return author_flair_text;
	}
	public void setAuthor_flair_text(String author_flair_text) {
		this.author_flair_text = author_flair_text;
	}
	public String getCreated_utc() {
		return created_utc;
	}
	public void setCreated_utc(String created_utc) {
		this.created_utc = created_utc;
	}
	public String getDistinguished() {
		return distinguished;
	}
	public void setDistinguished(String distinguished) {
		this.distinguished = distinguished;
	}
	public String getNum_reports() {
		return num_reports;
	}
	public void setNum_reports(String num_reports) {
		this.num_reports = num_reports;
	}
}
