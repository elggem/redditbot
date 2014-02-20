package rb.testprograms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.TypedQuery;

import rb.helpers.DBHandler;
import rb.persistentobjects.Statement;

public class AnalysisHistogramLengthOfStatement {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("  usage: java -jar JAR DBNAME");
			System.exit(1);
		}
		
		System.out.println("#Reddit Histogram of first 4k statements of db " + args[0]);		
		DBHandler.initializeHandler(args[0]);
		
		TypedQuery<Statement> query=DBHandler.em.createQuery("Select o from Statement o",Statement.class);
		List<Statement> statements = query.getResultList();
		Collections.shuffle(statements);
		
		ArrayList<Statement> croppedStatements = new ArrayList<Statement>();
		
		for (int i = 0; i < 2000; i++) {
			croppedStatements.add(statements.get(i));
		}
		
		int binsize = 10;
		int maxlength = 500;
		
		for (int i = 0; i < maxlength; i+=binsize) {
			int count = 0;
			
			for (Statement statement : croppedStatements) {
				if (statement.length>i && statement.length<i+binsize) {
					count++;
				}
			}
			
			
		    System.out.println(i+binsize + " " + count);
		}
		
		
		DBHandler.closeHandler();
	}

}
