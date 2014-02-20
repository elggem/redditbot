package rb;
import java.util.ArrayList;
import java.util.List;

import rb.helpers.DBHandler;
import rb.persistentobjects.Statement;

import com.cd.reddit.Reddit;
import com.cd.reddit.RedditException;
import com.cd.reddit.json.jackson.RedditJsonParser;
import com.cd.reddit.json.mapping.RedditComment;
import com.cd.reddit.json.mapping.RedditLink;
import com.cd.reddit.json.util.RedditComments;


public class Collector {
	
	static Reddit reddit = new Reddit("machinelearningbot/0.1 by elggem");

	public static void main(String[] args) {
		System.out.println("Reddit Scraper for analysis. v0.1");
		
		if (args.length < 4) {
			System.out.println("  usage: java -Xmx4g -jar JAR COUNT SUBREDDIT USER PASS\n" +
							   "     with: COUNT = number of links to get, 0 is for relating DB\n" +
							   "           SUBREDDIT = the subreddit to scrape and db output name\n" +
							   "           USER/PASS = username and password\n");
			System.exit(1);
		}
		
		System.out.println(" scraping " + Integer.parseInt(args[0]) + " posts from subreddit " + args[1]);
		
		try {
			reddit.login(args[2], args[3]);
		} catch (RedditException e) {
			e.printStackTrace();
		}
		
		DBHandler.initializeHandler(args[1]);
		
		addFromSubreddit(args[1], Integer.parseInt(args[0]));	
		
		DBHandler.refreshWordRelations();
		DBHandler.closeHandler();		
	}
	
	public static void addFromSubreddit(String subreddit, int count) {
		try {
		    List<RedditLink> links = new ArrayList<RedditLink>();
		    int counter = 0;
		    
		    if (count == 0)
		    	return;
		    
		    while (counter < count) {
		    	
		    	if (links.size() == 0) {
		    		links = reddit.listingFor(subreddit, "");
		    	} else {
		    		String last_id = links.get(links.size()-1).getId();
		    		links = reddit.listingForAfter(subreddit, "", last_id);
		    	}
		    	
		    	if (links.size()==0) {
		    		break;
		    	}
		    			    	
			    for (RedditLink redditLink : links) {
					
					counter++;
					
					if (counter > count) {
						break;
					}
					
					System.out.println("Adding " + counter + "/" + count + " - by " + redditLink.getAuthor() + " cmts: " + redditLink.getNum_comments());

					List<RedditComment> comments = getAllCommentsForLink(redditLink, subreddit);
					
					System.out.print(" ");
					
					for (RedditComment redditComment : comments) {
						System.out.print(".");
						Statement parent = null;
						
						if (redditComment.parent != null) {
							parent = DBHandler.findStatement(redditComment.parent.id);
						}
						
						DBHandler.addStatement(new Statement(redditComment.id,
															 redditComment.getBody(), 
															 redditComment.getAuthor(),
															 redditComment.getCreated_utc(),
															 subreddit, 
															 parent, 
															 redditComment.getUps(), redditComment.getDowns(), '0'));
						
						
					}
					System.out.println("");

				}
		    }
		} catch( Exception re ) {
		    //RedditException merely inherits Exception.
		    re.printStackTrace();
		}
	}
	
	public static List<RedditComment>getAllRepliesForComment(RedditComment redditComment) {
		List<RedditComment> thelist = new ArrayList<RedditComment>();
		try {
			thelist.add(redditComment);
			
			if (redditComment.getReplies().toString().length() >= 10) {
				final RedditJsonParser parser = new RedditJsonParser(redditComment.getReplies());
				List<RedditComment> redditReplies = parser.parseCommentsOnly();
				
				for (RedditComment redditCommentReply : redditReplies) {
					List<RedditComment> additionalList = getAllRepliesForComment(redditCommentReply);
					
					for (RedditComment redditComment2 : additionalList) {
						redditComment2.parent = redditComment;
					}
					
					thelist.addAll(additionalList);
				}
			}

		
		} catch (RedditException e) {
			e.printStackTrace();
		}
		
		return thelist;
	}

	public static List<RedditComment>getAllCommentsForLink(RedditLink redditLink, String subreddit) {
		List<RedditComment> thelist = new ArrayList<RedditComment>();
		try {

		if (redditLink.getNum_comments()>0) {
			RedditComments comments;
			comments = reddit.commentsFor(subreddit, redditLink.getId());
			
			
			for (RedditComment redditComment : comments.getComments()) {				
				List<RedditComment> additionalList = getAllRepliesForComment(redditComment);
				thelist.addAll(additionalList);
			}
			
		}
		
		} catch (RedditException e) {
			e.printStackTrace();
		}
		
		
		return thelist;
	}

}
