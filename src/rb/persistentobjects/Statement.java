package rb.persistentobjects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import rb.*;
import javax.persistence.*;

@Entity
public class Statement {
	@Id public String id;
	public String text;
	public String author;
	public long date;
	public String subreddit;
	public List<Word> includedWords;
	public Statement parentStatement;
	public int upvotes;
	public int downvotes;
	public int length;
	public char attributes;

	public Statement (String id, String text, String author, long date, String subreddit, Statement parentStatement, int upvotes, int downvotes, char attributes) {
		this.id = id;
		
		this.text = text;
		this.author = author;
		this.date = date;
		this.upvotes = upvotes;
		this.downvotes = downvotes;
		List<String> wordStrings = Arrays.asList(text.split("\\s+"));
		
		this.includedWords = new ArrayList<Word>();
		
		this.subreddit = subreddit;
		this.parentStatement = parentStatement;
		this.length = wordStrings.size();
		this.attributes = attributes;

		
	}
	
	public Statement getParentStatement() {
		return parentStatement;
	}

	public void setParentStatement(Statement parentStatement) {
		this.parentStatement = parentStatement;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public char getAttributes() {
		return attributes;
	}

	public void setAttributes(char attributes) {
		this.attributes = attributes;
	}

	public List<Word> getIncludedWords() {
		return includedWords;
	}

	public void setIncludedWords(List<Word> includedWords) {
		this.includedWords = includedWords;
	}
	

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public String getSubreddit() {
		return subreddit;
	}

	public void setSubreddit(String subreddit) {
		this.subreddit = subreddit;
	}

	public int getUpvotes() {
		return upvotes;
	}

	public void setUpvotes(int upvotes) {
		this.upvotes = upvotes;
	}

	public int getDownvotes() {
		return downvotes;
	}

	public void setDownvotes(int downvotes) {
		this.downvotes = downvotes;
	}
	

}
