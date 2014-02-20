package rb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;

import rb.helpers.DBHandler;
import rb.helpers.StemmerHelper;
import rb.persistentobjects.Statement;
import rb.persistentobjects.Word;

import ca.uwo.csd.ai.nlp.common.SparseVector;
import ca.uwo.csd.ai.nlp.kernel.KernelManager;
import ca.uwo.csd.ai.nlp.kernel.LinearKernel;
import ca.uwo.csd.ai.nlp.libsvm.svm_model;
import ca.uwo.csd.ai.nlp.libsvm.svm_parameter;
import ca.uwo.csd.ai.nlp.libsvm.ex.Instance;
import ca.uwo.csd.ai.nlp.libsvm.ex.SVMPredictor;
import ca.uwo.csd.ai.nlp.libsvm.ex.SVMTrainer;

public class ClassifySVM {

	
	static svm_model model = null;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.out.println("Reddit Analysis. v0.1");
		if (args.length < 4) {
			System.out.println("  usage: java -Xmx4g -jar JAR DBNAME MAXLENGTH RECALC(1/0) \" blabla alhsoushdou \"");
			System.exit(1);
		}
		
		
		DBHandler.initializeHandler(args[0]);
		
		//Get statements and Words
		TypedQuery<Statement> query=DBHandler.em.createQuery("Select o from Statement o where o.text != \"[deleted]\" and o.parentStatement!= null and o.length>0 and o.length < " + args[1],Statement.class);
		List<Statement> statements = query.getResultList();
		
		TypedQuery<Word> queryW=DBHandler.em.createQuery("Select o from Word o",Word.class);
		List<Word> words = queryW.getResultList();
		
		boolean modelLoaded = true;
		
        try {
			model = SVMPredictor.loadModel(args[0] + ".model");
		} catch (Exception e1) {
			modelLoaded = false;
		} 
		
        if (!modelLoaded || args[2].contentEquals("1")) {
        	
        	System.out.println("Couldnt load model or recalculation requested.");
        	
			//Make instances...
	        ArrayList<Instance> instanceList = new ArrayList<Instance>();
	
			for (Statement statement : statements) {
				//Statement = statement
				List<Word> includedWords = statement.parentStatement.includedWords;
				
				double label = statements.indexOf(statement);
				
		        SparseVector vec = new SparseVector(words.size());
	
		        for (Word word : words) {
		        	int value = 0;
		        	if(includedWords.contains(word))
		        		value = 1;
		            vec.add(words.indexOf(word), value);
				}
		        
		        Instance inst = new Instance(label, vec);
		        instanceList.add(inst);
			}
			
	        Instance [] trainingInstances = instanceList.toArray(new Instance[instanceList.size()]);
	
	        //Register kernel function
	        KernelManager.setCustomKernel(new LinearKernel());        
	        
	        //Setup parameters
	        svm_parameter param = new svm_parameter();                
	        
	        //Train the model
	        System.out.println("Training started...");
	        model = SVMTrainer.train(trainingInstances, param);
	        System.out.println("Training completed.");
	                
	        //Save the trained model
	        try {
				SVMTrainer.saveModel(model, args[0] + ".model");
			} catch (IOException e) {
				e.printStackTrace();
			}
        } else {
        	System.out.println("loaded model!!");
        	 //Register kernel function
	        KernelManager.setCustomKernel(new LinearKernel());        
	        
        }
          
		ArrayList<String> inputlist = convertStringToStemmedList(args[3]);
		
		//calculate instance list...

		SparseVector vec = new SparseVector();
		
        for (Word word : words) {
        	int value = 0;
        	if(inputlist.contains(word.word)) {
        		System.out.println("Found word.");
        		value = 1;
        	}
            vec.add(words.indexOf(word), value);
		}
        
        Instance inst = new Instance(0, vec);
		
		double result = SVMPredictor.predict(inst, model, false);
		
		System.out.println(inputlist + " is " + statements.get((int) result).text);
        
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
