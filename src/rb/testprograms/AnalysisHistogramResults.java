package rb.testprograms;

import java.util.List;

import com.cd.reddit.Reddit;
import com.cd.reddit.RedditException;
import com.cd.reddit.json.mapping.RedditComment;
import com.cd.reddit.json.mapping.RedditLink;
import com.cd.reddit.json.util.RedditComments;

public class AnalysisHistogramResults {

	static Reddit reddit = new Reddit("machinelearningbot/0.1 by elggem");

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (args.length < 1) {
			System.out.println("  usage: java -jar JAR USER1 USER2 ... USERN");
			System.exit(1);
		}
		
		System.out.println("#Reddit Histogram of upvotes by comment");
		
		try {
			for (int i=0; i<args.length; i++) {				
				String user = args[i];
				
				List<RedditComment> comments = reddit.commentsForUser(user);
					
				System.out.print("\"" + user + " upvotes\"");
				for (RedditComment comment : comments) {
					System.out.print(" " + (comment.getUps()-1));
					System.out.print(" -" + (comment.getDowns()));

				}
				
				/*System.out.println("");
				
				System.out.print("\"" + user + " downvotes\"");
				for (RedditComment comment : comments) {
					System.out.print(" " + (comment.getDowns()));
				}*/
				
				System.out.println("");
			
			}
			
		} catch (RedditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

		
		
	}

}
