package rb;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;

import de.daslaboratorium.machinelearning.classifier.BayesClassifier;
import de.daslaboratorium.machinelearning.classifier.Classification;
import de.daslaboratorium.machinelearning.classifier.Classifier;

import rb.helpers.DBHandler;
import rb.helpers.StemmerHelper;
import rb.persistentobjects.Statement;
import rb.persistentobjects.Word;

public class ClassifyNaiveBayes {

	/**
	 * @param args
	 */
	public static void main(String[] args) {	
		System.out.println("Reddit Analysis. v0.1");
		if (args.length < 3) {
			System.out.println("  usage: java -jar JAR DBNAME MAXLENGTH SCORED(0/1) \" blabla alhsoushdou \"");
			System.exit(1);
		}
		
		if (args[2].contentEquals("1")) {
			System.out.println("Using Scored.");
		}
		
		
		DBHandler.initializeHandler(args[0]);

		// Create a new bayes classifier with string categories and string features.
		Classifier<String, String> bayes = new BayesClassifier<String, String>();

		// Change the memory capacity. New learned classifications (using
		// learn method are stored in a queue with the size given here and
		// used to classify unknown sentences.
		bayes.setMemoryCapacity(50000);
		
		TypedQuery<Statement> query=DBHandler.em.createQuery("Select o from Statement o where o.text != \"[deleted]\" and o.parentStatement!= null and o.length>0 and o.length < " + args[1],Statement.class);
		List<Statement> statements = query.getResultList();
		
		System.out.println("Analyzing " + statements.size() + " statements... ");
		
		for (Statement statement : statements) {
			ArrayList<String> wordStrings = new ArrayList<String>();
			for (Word word : statement.parentStatement.includedWords) {
				wordStrings.add(word.word);
			}
						
			//LEARN
			bayes.learn(statement.text, wordStrings);
			
			if (args[2].contentEquals("1")) {
				//SCORED
				for (int i = 0; i < Math.sqrt(statement.upvotes); i++) {
					bayes.incrementCategory(statement.text);
				}
				
				for (int i = 0; i < Math.sqrt(statement.downvotes); i++) {
					bayes.decrementCategory(statement.text);
				}
			}

		}
		
		ArrayList<String> list = convertStringToStemmedList(args[3]);
		
		Classification<String, String> classification = bayes.classify(list);
		
		System.out.println(list + " is " + classification.getCategory() + " with prob: " + classification.getProbability());
		
		
		DBHandler.closeHandler();
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
