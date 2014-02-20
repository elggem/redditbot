/*
Copyright 2013 Cory Dissinger

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/

package com.cd.reddit.json.mapping;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Implements the Java bean version of the JSON found <a href="https://github.com/reddit/reddit/wiki/JSON#account">here</a>.
 * 
 * @author <a href="https://github.com/reddit/reddit/wiki/JSON#message-implements-created">Cory Dissinger</a>
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class RedditMessage extends RedditType{

	//Thing
	private String id;
	private String name;
	private String kind;
	private String data;
	
	//Created
	private long created_utc;
	
	//Comment
	private String author;
	private String body;
	private String replies;
	private String subject;
	
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
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public long getCreated_utc() {
		return created_utc;
	}
	public void setCreated_utc(long created_utc) {
		this.created_utc = created_utc;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getReplies() {
		return replies;
	}
	public void setReplies(String replies) {
		this.replies = replies;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RedditMessage [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", kind=");
		builder.append(kind);
		builder.append(", data=");
		builder.append(data);
		builder.append(", created_utc=");
		builder.append(created_utc);
		builder.append(", author=");
		builder.append(author);
		builder.append(", body=");
		builder.append(body);
		builder.append(", replies=");
		builder.append(replies);
		builder.append(", subject=");
		builder.append(subject);
		builder.append("]");
		return builder.toString();
	}	
}
