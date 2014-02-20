package rb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.persistence.TypedQuery;


import rb.helpers.ClassificationResult;
import rb.helpers.DBHandler;
import rb.helpers.StemmerHelper;
import rb.persistentobjects.Statement;
import rb.persistentobjects.Word;

import ca.uwo.csd.ai.nlp.common.SparseVector;
import ca.uwo.csd.ai.nlp.kernel.KernelManager;
import ca.uwo.csd.ai.nlp.kernel.LinearKernel;
import ca.uwo.csd.ai.nlp.libsvm.svm_model;
import ca.uwo.csd.ai.nlp.libsvm.ex.Instance;
import ca.uwo.csd.ai.nlp.libsvm.ex.SVMPredictor;

import com.cd.reddit.Reddit;
import com.cd.reddit.RedditException;
import com.cd.reddit.json.jackson.RedditJsonParser;
import com.cd.reddit.json.mapping.RedditComment;
import com.cd.reddit.json.mapping.RedditLink;
import com.cd.reddit.json.util.RedditComments;

import de.daslaboratorium.machinelearning.classifier.BayesClassifier;
import de.daslaboratorium.machinelearning.classifier.Classification;

public class Poster {
	
    private static Random randomGenerator;

	static Reddit reddit = new Reddit("machinelearningbot/0.1 by elggem");
	static String subreddit = "";
	
	static BayesClassifier<String, String> bayes = null;

	static svm_model model = null;
	static List<Statement> statements = null;
	static List<Word> words = null;
	
	static int max_reply_length = 25;
	static int classifier = 0;
	
	static ArrayList<String> alreadyPostedComments = new ArrayList<String>();
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Reddit BOT Final stage. Armed and ready to go!");

		randomGenerator = new Random();
		
		if (args.length < 4) {
			System.out.println("  usage: java -Xmx4g -jar JAR SUBREDDIT CLASSIFIER USER PASS\n" +
							   "     with: SUBREDDIT = the subreddit to post to\n" +
					   		   "           CLASSIFIER = 1:BN, 2:SVM 3:Random\n" + 
		   					   "           USER/PASS = username and password\n");
			System.exit(1);
		}
		
		subreddit = args[0];
		classifier = Integer.valueOf(args[1]);
		
		try {
			reddit.login(args[2], args[3]);
		} catch (RedditException e) {
			e.printStackTrace();
		}
		
		DBHandler.initializeHandler(args[0]);

		if (statements == null) {
			TypedQuery<Statement> query=DBHandler.em.createQuery("Select o from Statement o where o.text != \"[deleted]\" and o.parentStatement!= null and o.length>0 and o.length < " + max_reply_length,Statement.class);
			statements = query.getResultList();
		}
		
		if (words == null) {
			TypedQuery<Word> queryW=DBHandler.em.createQuery("Select o from Word o",Word.class);
			words = queryW.getResultList();
		}
		
		
		//TIMER CODE
		Timer timer = new Timer();
		long thirty_minutes = 30*60*1000;
		long one_hour = thirty_minutes*2;

		long waiting_time = (long) (one_hour+(Math.random()*one_hour));
		
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
						System.out.println("-> Awake from Hibernation");
						RedditComment commentToReplyTo = findInterestingCommentPopularity(subreddit);
						System.out.println("-> Generating Reply for " + commentToReplyTo.getBody());
						String generatedReply = generateCommentFor(commentToReplyTo, classifier);
						System.out.println("-> Generated " + generatedReply);
						System.out.println("-> Posting..");
						postCommentAsReplyTo(generatedReply, commentToReplyTo);
						System.out.println("-> Going Back to Sleep...");
			}
		}, waiting_time, waiting_time);
		
		
		//-------------
		System.out.println("-> Awake from Hibernation");
		RedditComment commentToReplyTo = findInterestingCommentPopularity(subreddit);
		System.out.println("-> Generating Reply for " + commentToReplyTo.getBody());
		String generatedReply = generateCommentFor(commentToReplyTo, classifier);
		System.out.println("-> Generated " + generatedReply);
		System.out.println("-> Posting..");
		postCommentAsReplyTo(generatedReply, commentToReplyTo);
		System.out.println("-> Going Back to Sleep...");
		///---------------------
		
		
		//DBHandler.closeHandler();
	}
	
	public static RedditComment findInterestingCommentRelevance(String subreddit) {
		//Get the first 25 posts.
	    try {
			List<RedditLink> links = reddit.listingFor(subreddit, "");
			Collections.shuffle(links);
			
			RedditLink linkOfInterest = links.get(0);
			
			System.out.println(" findInterestingComment post: " + linkOfInterest);
						
			List<RedditComment> comments = getAllCommentsForLink(linkOfInterest, subreddit);
			ArrayList<ClassificationResult> classifications = new ArrayList<ClassificationResult>();
			
			for (RedditComment redditComment : comments) {
				classifications.add(classifyCommentBayes(redditComment));
			}
			
			RedditComment bestComment = null;
			double max_prob = 0;
			
			int i=0;
			for (RedditComment redditComment : comments) {
				double prob = classifications.get(i).probability;
				
				if (prob>max_prob && convertStringToStemmedList(redditComment.getBody()).size()>0) {
						max_prob = prob;
						bestComment = redditComment;
				}
				
				i++;
			}
			
			
			System.out.println(" findInterestingComment comment: " + bestComment);

			
			return bestComment;
			
		} catch (Exception e) {
			System.out.println("ERROR, trying next time: " + e.getLocalizedMessage());
		}

		return null;
	}
	
	public static RedditComment findInterestingCommentPopularity(String subreddit) {
		//Get the first 25 posts.
	    try {
			List<RedditLink> links = reddit.listingFor(subreddit, "");
			Collections.shuffle(links);
			
			RedditLink linkOfInterest = links.get(0);
			
			System.out.println(" findInterestingComment post: " + linkOfInterest);
						
			List<RedditComment> comments = getAllCommentsForLink(linkOfInterest, subreddit);
			
			RedditComment bestComment = null;
			long max_karma = 0;
			
			for (RedditComment redditComment : comments) {
				if (redditComment.getUps()-redditComment.getDowns() > max_karma) {
					//if (redditComment.getReplies().toString().length() <= 10) {
						max_karma = redditComment.getUps()-redditComment.getDowns();
						bestComment = redditComment;
					//}
					
				}
			}
			
			
			System.out.println(" findInterestingComment comment: " + bestComment);

			
			return bestComment;
			
		} catch (Exception e) {
			System.out.println("ERROR, trying next time: " + e.getLocalizedMessage());
		}

		return null;
	}
	
	public static String generateCommentFor(RedditComment comment, int classifierID) {
		String generated = "";
		
		if (classifierID == 1) {
			generated = classifyCommentBayes(comment).resultString;
		} else if (classifierID == 2) {
			generated = classifyCommentSVM(comment);
		} else if (classifierID == 3) {
			generated = classifyCommentRandom(comment);

		} else {
			System.out.println("Choose a valid classifier dude.");
			System.exit(1);
		}

		return generated;
	}
	
	public static void postCommentAsReplyTo(String comment, RedditComment parent) {
	    try {
			System.out.println(" postCommentAsReplyTo response: "+reddit.comment(comment, parent.getName()));
		} catch (RedditException e) {
			System.out.println("ERROR, trying next time: " + e.getLocalizedMessage());
		}
	}
	
	public static String classifyCommentSVM(RedditComment comment) {
		if (model == null) {
			
	        try {
				model = SVMPredictor.loadModel(subreddit + ".model");
			} catch (Exception e1) {
				System.out.println("ERROR: Couldnt load SVM model. Exitting");
				System.exit(1);
			} 

	        KernelManager.setCustomKernel(new LinearKernel());        
	        
	        System.out.println(" classifyCommentSVM: loaded model!!");
	            
	    }
			
		ArrayList<String> inputlist = convertStringToStemmedList(comment.getBody());
		
		SparseVector vec = new SparseVector();
		
        for (Word word : words) {
        	int value = 0;
        	if(inputlist.contains(word.word)) {
        		value = 1;
        	}
            vec.add(words.indexOf(word), value);
		}
        
        Instance inst = new Instance(0, vec);
		double result = SVMPredictor.predict(inst, model, false);
        		
		return statements.get((int) result).text;
	}
	
	public static ClassificationResult classifyCommentBayes(RedditComment comment) {
		
		if (bayes == null) {
			// Create a new bayes classifier with string categories and string features.
			bayes = new BayesClassifier<String, String>();
	
			// Change the memory capacity. New learned classifications (using
			// learn method are stored in a queue with the size given here and
			// used to classify unknown sentences.
			bayes.setMemoryCapacity(50000);
			
			TypedQuery<Statement> query=DBHandler.em.createQuery("Select o from Statement o where o.text != \"[deleted]\" and o.parentStatement!= null and o.length>0 and o.length < " + max_reply_length,Statement.class);
			List<Statement> statements = query.getResultList();
			
			System.out.println(" classifyCommentBayes analyzing " + statements.size() + " statements... ");
			
			for (Statement statement : statements) {
				ArrayList<String> wordStrings = new ArrayList<String>();
				for (Word word : statement.parentStatement.includedWords) {
					wordStrings.add(word.word);
				}
							
				//LEARN
				bayes.learn(statement.text, wordStrings);
				
	
			}
		}
		
		ArrayList<String> list = convertStringToStemmedList(comment.getBody());

		Classification<String,String> result = bayes.classify(list);
		
		System.out.println(" classifyCommentBayes " + list + " prob " + result.getProbability());

		return new ClassificationResult(result.getCategory(), result.getProbability());
	}
	
	
	public static String classifyCommentRandom(RedditComment comment) {
		String result = statements.get(randomGenerator.nextInt(statements.size())).text;

		System.out.println(" classifyCommentRandom ");

		return result;
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


	public static ArrayList<String> convertStringToStemmedList(String input) {
		
		ArrayList<String> wordStrings = new ArrayList<String>();
		
		for (String string : input.split("\\s+")) {
			String pruned = string.replaceAll("[^a-zA-Z]", "").toLowerCase();
			String stemmed = StemmerHelper.stemWord(pruned);
			
			if (DBHandler.checkWord(stemmed)) {
				wordStrings.add(stemmed);
			}
		}
		
		return wordStrings;
	}
	
}
