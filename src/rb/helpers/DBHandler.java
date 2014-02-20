package rb.helpers;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import rb.persistentobjects.Statement;
import rb.persistentobjects.Word;


public class DBHandler {
	public static EntityManagerFactory emf = null;
	public static EntityManager em = null;
	public static ArrayList<String> stopwords = null;

	
	public static void initializeHandler(String dbname) {
		if (emf == null || em == null) {
			emf=Persistence.createEntityManagerFactory(dbname+".odb");
			em=emf.createEntityManager();
		}
		
		if (stopwords == null) {
			initStopwords();
		}
	}
	
	public static void closeHandler() {
		//System.out.println("# Stats:");
		
		TypedQuery<Long> query = em.createQuery("SELECT COUNT(c) FROM Statement c", Long.class);
	    long statementCount = query.getSingleResult();
	    
		TypedQuery<Long> queryW = em.createQuery("SELECT COUNT(c) FROM Word c", Long.class);
	    long wordCount = queryW.getSingleResult();
	    
		System.out.print("# DB has " + statementCount + " Statements ");
		System.out.print("and " + wordCount + " Words. ");
		
		if (em != null) {
			em.close();
			em = null;
		}
		if (emf != null) {
			emf.close();
			emf = null;
		}
		
		System.out.println("# Done!");

	}
	
	
	public static Word findWord(String text) {		
		Word word=(Word) em.find(Word.class,text);
		return word;
	}
	
	public static boolean addWord(Word word) {		
		boolean hadToOpenSession = false;
		
		if (!em.getTransaction().isActive()) {
			em.getTransaction().begin();
			hadToOpenSession = true;
		}
		
		if (findWord(word.word) == null && checkWord(word.word) == true) {
			em.persist(word);
			if (hadToOpenSession) {
				em.getTransaction().commit();
			}
		} else {
			return false;
		}

		return true;
	}
	
	public static void addStatement(Statement statement) {

		if (!em.getTransaction().isActive()) {
			em.getTransaction().begin();
		}
		
		if (findStatement(statement.getId()) == null) {
			em.persist(statement);
			
			List<String> wordStrings = new ArrayList<String>();
			
			for (String string : statement.text.split("\\s+")) {
				wordStrings.add(string.replaceAll("[^a-zA-Z]", "").toLowerCase());
			}
			
	
			for (String word : wordStrings) {
				//includedWords.add(arg0)
				Word wordInDB = DBHandler.findWord(word);
				
				if (wordInDB != null) {
					//Word is already in the DB, let's update it.
					wordInDB.count++;
					wordInDB.relatedStatements.add(statement);
					
					statement.includedWords.add(wordInDB);
					
					//wordInDB.getDirectlyRelatedWords().addAll(words);
				} else {
					Word newWord = new Word(word);
					newWord.count = 1;
					newWord.relatedStatements.add(statement);
					
					if (addWord(newWord)) {
						statement.includedWords.add(newWord);
					}
					
				}
			}
					
				em.getTransaction().commit();
			
			//refreshWordRelations();
		}
	}
	
	public static void refreshWordRelations() {
		System.out.println("Refreshing word relations...");
		
		boolean hadToOpenSession = false;

		if (!em.getTransaction().isActive()) {
			em.getTransaction().begin();
			hadToOpenSession = true;
		}
		
		//Get all Words.
		TypedQuery<Word> allWordsQuery=em.createQuery("Select word from Word word",Word.class);
		List<Word> allWords = allWordsQuery.getResultList();
		
		int i = 0;
		
		for (Word word : allWords) {
			System.out.print(" processing word " + ++i + "/" + allWords.size());
			boolean change = false;
			
			//Calculate directly related Words.
			for (Statement statement : word.getRelatedStatements()) {
				for (Word word2 : statement.includedWords) {
					if (!word.directlyRelatedWords.contains(word2)) {
						change = true;
						word.directlyRelatedWords.add(word2);
					}
				}
				
				if (statement.parentStatement != null) {
					for (Word word2 : statement.parentStatement.includedWords) {
						if (!word.indirectlyRelatedWords.contains(word2)) {
							change = true;
							word.indirectlyRelatedWords.add(word2);
						}
					}
				}
			}
			
			if (change) System.out.print("***");
			
			System.out.println("");
		}
		
		if (hadToOpenSession) {
			em.getTransaction().commit();
		}
		
	}
	
	public static Statement findStatement(String text) {		
		Statement s=(Statement) em.find(Statement.class,text);
		return s;
	}
	
	public static boolean checkWord(String word) {
		if (word.contentEquals("") || word.contains("http")) {
			return false;
		}
		
		if (stopwords.contains(word)) {
			return false;
		}
		
		
		return true;
	}

	
	/**
	   * initializes the stopwords (based on Rainbow).
	   */
	  public static void initStopwords() {
	    stopwords= new ArrayList<String>();

	    //Stopwords list from Rainbow
	   stopwords.add("a");
	   stopwords.add("able");
	   stopwords.add("about");
	   stopwords.add("above");
	   stopwords.add("according");
	   stopwords.add("accordingly");
	   stopwords.add("across");
	   stopwords.add("actually");
	   stopwords.add("after");
	   stopwords.add("afterwards");
	   stopwords.add("again");
	   stopwords.add("against");
	   stopwords.add("all");
	   stopwords.add("allow");
	   stopwords.add("allows");
	   stopwords.add("almost");
	   stopwords.add("alone");
	   stopwords.add("along");
	   stopwords.add("already");
	   stopwords.add("also");
	   stopwords.add("although");
	   stopwords.add("always");
	   stopwords.add("am");
	   stopwords.add("among");
	   stopwords.add("amongst");
	   stopwords.add("an");
	   stopwords.add("and");
	   stopwords.add("another");
	   stopwords.add("any");
	   stopwords.add("anybody");
	   stopwords.add("anyhow");
	   stopwords.add("anyone");
	   stopwords.add("anything");
	   stopwords.add("anyway");
	   stopwords.add("anyways");
	   stopwords.add("anywhere");
	   stopwords.add("apart");
	   stopwords.add("appear");
	   stopwords.add("appreciate");
	   stopwords.add("appropriate");
	   stopwords.add("are");
	   stopwords.add("around");
	   stopwords.add("as");
	   stopwords.add("aside");
	   stopwords.add("ask");
	   stopwords.add("asking");
	   stopwords.add("associated");
	   stopwords.add("at");
	   stopwords.add("available");
	   stopwords.add("away");
	   stopwords.add("awfully");
	   stopwords.add("b");
	   stopwords.add("be");
	   stopwords.add("became");
	   stopwords.add("because");
	   stopwords.add("become");
	   stopwords.add("becomes");
	   stopwords.add("becoming");
	   stopwords.add("been");
	   stopwords.add("before");
	   stopwords.add("beforehand");
	   stopwords.add("behind");
	   stopwords.add("being");
	   stopwords.add("believe");
	   stopwords.add("below");
	   stopwords.add("beside");
	   stopwords.add("besides");
	   stopwords.add("best");
	   stopwords.add("better");
	   stopwords.add("between");
	   stopwords.add("beyond");
	   stopwords.add("both");
	   stopwords.add("brief");
	   stopwords.add("but");
	   stopwords.add("by");
	   stopwords.add("c");
	   stopwords.add("came");
	   stopwords.add("can");
	   stopwords.add("cannot");
	   stopwords.add("cant");
	   stopwords.add("cause");
	   stopwords.add("causes");
	   stopwords.add("certain");
	   stopwords.add("certainly");
	   stopwords.add("changes");
	   stopwords.add("clearly");
	   stopwords.add("co");
	   stopwords.add("com");
	   stopwords.add("come");
	   stopwords.add("comes");
	   stopwords.add("concerning");
	   stopwords.add("consequently");
	   stopwords.add("consider");
	   stopwords.add("considering");
	   stopwords.add("contain");
	   stopwords.add("containing");
	   stopwords.add("contains");
	   stopwords.add("corresponding");
	   stopwords.add("could");
	   stopwords.add("course");
	   stopwords.add("currently");
	   stopwords.add("d");
	   stopwords.add("definitely");
	   stopwords.add("described");
	   stopwords.add("despite");
	   stopwords.add("did");
	   stopwords.add("different");
	   stopwords.add("do");
	   stopwords.add("does");
	   stopwords.add("doing");
	   stopwords.add("done");
	   stopwords.add("down");
	   stopwords.add("downwards");
	   stopwords.add("during");
	   stopwords.add("e");
	   stopwords.add("each");
	   stopwords.add("edu");
	   stopwords.add("eg");
	   stopwords.add("eight");
	   stopwords.add("either");
	   stopwords.add("else");
	   stopwords.add("elsewhere");
	   stopwords.add("enough");
	   stopwords.add("entirely");
	   stopwords.add("especially");
	   stopwords.add("et");
	   stopwords.add("etc");
	   stopwords.add("even");
	   stopwords.add("ever");
	   stopwords.add("every");
	   stopwords.add("everybody");
	   stopwords.add("everyone");
	   stopwords.add("everything");
	   stopwords.add("everywhere");
	   stopwords.add("ex");
	   stopwords.add("exactly");
	   stopwords.add("example");
	   stopwords.add("except");
	   stopwords.add("f");
	   stopwords.add("far");
	   stopwords.add("few");
	   stopwords.add("fifth");
	   stopwords.add("first");
	   stopwords.add("five");
	   stopwords.add("followed");
	   stopwords.add("following");
	   stopwords.add("follows");
	   stopwords.add("for");
	   stopwords.add("former");
	   stopwords.add("formerly");
	   stopwords.add("forth");
	   stopwords.add("four");
	   stopwords.add("from");
	   stopwords.add("further");
	   stopwords.add("furthermore");
	   stopwords.add("g");
	   stopwords.add("get");
	   stopwords.add("gets");
	   stopwords.add("getting");
	   stopwords.add("given");
	   stopwords.add("gives");
	   stopwords.add("go");
	   stopwords.add("goes");
	   stopwords.add("going");
	   stopwords.add("gone");
	   stopwords.add("got");
	   stopwords.add("gotten");
	   stopwords.add("greetings");
	   stopwords.add("h");
	   stopwords.add("had");
	   stopwords.add("happens");
	   stopwords.add("hardly");
	   stopwords.add("has");
	   stopwords.add("have");
	   stopwords.add("having");
	   stopwords.add("he");
	   stopwords.add("hello");
	   stopwords.add("help");
	   stopwords.add("hence");
	   stopwords.add("her");
	   stopwords.add("here");
	   stopwords.add("hereafter");
	   stopwords.add("hereby");
	   stopwords.add("herein");
	   stopwords.add("hereupon");
	   stopwords.add("hers");
	   stopwords.add("herself");
	   stopwords.add("hi");
	   stopwords.add("him");
	   stopwords.add("himself");
	   stopwords.add("his");
	   stopwords.add("hither");
	   stopwords.add("hopefully");
	   stopwords.add("how");
	   stopwords.add("howbeit");
	   stopwords.add("however");
	   stopwords.add("i");
	   stopwords.add("ie");
	   stopwords.add("if");
	   stopwords.add("ignored");
	   stopwords.add("immediate");
	   stopwords.add("in");
	   stopwords.add("inasmuch");
	   stopwords.add("inc");
	   stopwords.add("indeed");
	   stopwords.add("indicate");
	   stopwords.add("indicated");
	   stopwords.add("indicates");
	   stopwords.add("inner");
	   stopwords.add("insofar");
	   stopwords.add("instead");
	   stopwords.add("into");
	   stopwords.add("inward");
	   stopwords.add("is");
	   stopwords.add("it");
	   stopwords.add("its");
	   stopwords.add("itself");
	   stopwords.add("j");
	   stopwords.add("just");
	   stopwords.add("k");
	   stopwords.add("keep");
	   stopwords.add("keeps");
	   stopwords.add("kept");
	   stopwords.add("know");
	   stopwords.add("knows");
	   stopwords.add("known");
	   stopwords.add("l");
	   stopwords.add("last");
	   stopwords.add("lately");
	   stopwords.add("later");
	   stopwords.add("latter");
	   stopwords.add("latterly");
	   stopwords.add("least");
	   stopwords.add("less");
	   stopwords.add("lest");
	   stopwords.add("let");
	   stopwords.add("like");
	   stopwords.add("liked");
	   stopwords.add("likely");
	   stopwords.add("little");
	   stopwords.add("ll"); //added to avoid words like you'll,I'll etc.
	   stopwords.add("look");
	   stopwords.add("looking");
	   stopwords.add("looks");
	   stopwords.add("ltd");
	   stopwords.add("m");
	   stopwords.add("mainly");
	   stopwords.add("many");
	   stopwords.add("may");
	   stopwords.add("maybe");
	   stopwords.add("me");
	   stopwords.add("mean");
	   stopwords.add("meanwhile");
	   stopwords.add("merely");
	   stopwords.add("might");
	   stopwords.add("more");
	   stopwords.add("moreover");
	   stopwords.add("most");
	   stopwords.add("mostly");
	   stopwords.add("much");
	   stopwords.add("must");
	   stopwords.add("my");
	   stopwords.add("myself");
	   stopwords.add("n");
	   stopwords.add("name");
	   stopwords.add("namely");
	   stopwords.add("nd");
	   stopwords.add("near");
	   stopwords.add("nearly");
	   stopwords.add("necessary");
	   stopwords.add("need");
	   stopwords.add("needs");
	   stopwords.add("neither");
	   stopwords.add("never");
	   stopwords.add("nevertheless");
	   stopwords.add("new");
	   stopwords.add("next");
	   stopwords.add("nine");
	   stopwords.add("no");
	   stopwords.add("nobody");
	   stopwords.add("non");
	   stopwords.add("none");
	   stopwords.add("noone");
	   stopwords.add("nor");
	   stopwords.add("normally");
	   stopwords.add("not");
	   stopwords.add("nothing");
	   stopwords.add("novel");
	   stopwords.add("now");
	   stopwords.add("nowhere");
	   stopwords.add("o");
	   stopwords.add("obviously");
	   stopwords.add("of");
	   stopwords.add("off");
	   stopwords.add("often");
	   stopwords.add("oh");
	   stopwords.add("ok");
	   stopwords.add("okay");
	   stopwords.add("old");
	   stopwords.add("on");
	   stopwords.add("once");
	   stopwords.add("one");
	   stopwords.add("ones");
	   stopwords.add("only");
	   stopwords.add("onto");
	   stopwords.add("or");
	   stopwords.add("other");
	   stopwords.add("others");
	   stopwords.add("otherwise");
	   stopwords.add("ought");
	   stopwords.add("our");
	   stopwords.add("ours");
	   stopwords.add("ourselves");
	   stopwords.add("out");
	   stopwords.add("outside");
	   stopwords.add("over");
	   stopwords.add("overall");
	   stopwords.add("own");
	   stopwords.add("p");
	   stopwords.add("particular");
	   stopwords.add("particularly");
	   stopwords.add("per");
	   stopwords.add("perhaps");
	   stopwords.add("placed");
	   stopwords.add("please");
	   stopwords.add("plus");
	   stopwords.add("possible");
	   stopwords.add("presumably");
	   stopwords.add("probably");
	   stopwords.add("provides");
	   stopwords.add("q");
	   stopwords.add("que");
	   stopwords.add("quite");
	   stopwords.add("qv");
	   stopwords.add("r");
	   stopwords.add("rather");
	   stopwords.add("rd");
	   stopwords.add("re");
	   stopwords.add("really");
	   stopwords.add("reasonably");
	   stopwords.add("regarding");
	   stopwords.add("regardless");
	   stopwords.add("regards");
	   stopwords.add("relatively");
	   stopwords.add("respectively");
	   stopwords.add("right");
	   stopwords.add("s");
	   stopwords.add("said");
	   stopwords.add("same");
	   stopwords.add("saw");
	   stopwords.add("say");
	   stopwords.add("saying");
	   stopwords.add("says");
	   stopwords.add("second");
	   stopwords.add("secondly");
	   stopwords.add("see");
	   stopwords.add("seeing");
	   stopwords.add("seem");
	   stopwords.add("seemed");
	   stopwords.add("seeming");
	   stopwords.add("seems");
	   stopwords.add("seen");
	   stopwords.add("self");
	   stopwords.add("selves");
	   stopwords.add("sensible");
	   stopwords.add("sent");
	   stopwords.add("serious");
	   stopwords.add("seriously");
	   stopwords.add("seven");
	   stopwords.add("several");
	   stopwords.add("shall");
	   stopwords.add("she");
	   stopwords.add("should");
	   stopwords.add("since");
	   stopwords.add("six");
	   stopwords.add("so");
	   stopwords.add("some");
	   stopwords.add("somebody");
	   stopwords.add("somehow");
	   stopwords.add("someone");
	   stopwords.add("something");
	   stopwords.add("sometime");
	   stopwords.add("sometimes");
	   stopwords.add("somewhat");
	   stopwords.add("somewhere");
	   stopwords.add("soon");
	   stopwords.add("sorry");
	   stopwords.add("specified");
	   stopwords.add("specify");
	   stopwords.add("specifying");
	   stopwords.add("still");
	   stopwords.add("sub");
	   stopwords.add("such");
	   stopwords.add("sup");
	   stopwords.add("sure");
	   stopwords.add("t");
	   stopwords.add("take");
	   stopwords.add("taken");
	   stopwords.add("tell");
	   stopwords.add("tends");
	   stopwords.add("th");
	   stopwords.add("than");
	   stopwords.add("thank");
	   stopwords.add("thanks");
	   stopwords.add("thanx");
	   stopwords.add("that");
	   stopwords.add("thats");
	   stopwords.add("the");
	   stopwords.add("their");
	   stopwords.add("theirs");
	   stopwords.add("them");
	   stopwords.add("themselves");
	   stopwords.add("then");
	   stopwords.add("thence");
	   stopwords.add("there");
	   stopwords.add("thereafter");
	   stopwords.add("thereby");
	   stopwords.add("therefore");
	   stopwords.add("therein");
	   stopwords.add("theres");
	   stopwords.add("thereupon");
	   stopwords.add("these");
	   stopwords.add("they");
	   stopwords.add("think");
	   stopwords.add("third");
	   stopwords.add("this");
	   stopwords.add("thorough");
	   stopwords.add("thoroughly");
	   stopwords.add("those");
	   stopwords.add("though");
	   stopwords.add("three");
	   stopwords.add("through");
	   stopwords.add("throughout");
	   stopwords.add("thru");
	   stopwords.add("thus");
	   stopwords.add("to");
	   stopwords.add("together");
	   stopwords.add("too");
	   stopwords.add("took");
	   stopwords.add("toward");
	   stopwords.add("towards");
	   stopwords.add("tried");
	   stopwords.add("tries");
	   stopwords.add("truly");
	   stopwords.add("try");
	   stopwords.add("trying");
	   stopwords.add("twice");
	   stopwords.add("two");
	   stopwords.add("u");
	   stopwords.add("un");
	   stopwords.add("under");
	   stopwords.add("unfortunately");
	   stopwords.add("unless");
	   stopwords.add("unlikely");
	   stopwords.add("until");
	   stopwords.add("unto");
	   stopwords.add("up");
	   stopwords.add("upon");
	   stopwords.add("us");
	   stopwords.add("use");
	   stopwords.add("used");
	   stopwords.add("useful");
	   stopwords.add("uses");
	   stopwords.add("using");
	   stopwords.add("usually");
	   stopwords.add("uucp");
	   stopwords.add("v");
	   stopwords.add("value");
	   stopwords.add("various");
	   stopwords.add("ve"); //added to avoid words like I've,you've etc.
	   stopwords.add("very");
	   stopwords.add("via");
	   stopwords.add("viz");
	   stopwords.add("vs");
	   stopwords.add("w");
	   stopwords.add("want");
	   stopwords.add("wants");
	   stopwords.add("was");
	   stopwords.add("way");
	   stopwords.add("we");
	   stopwords.add("welcome");
	   stopwords.add("well");
	   stopwords.add("went");
	   stopwords.add("were");
	   stopwords.add("what");
	   stopwords.add("whatever");
	   stopwords.add("when");
	   stopwords.add("whence");
	   stopwords.add("whenever");
	   stopwords.add("where");
	   stopwords.add("whereafter");
	   stopwords.add("whereas");
	   stopwords.add("whereby");
	   stopwords.add("wherein");
	   stopwords.add("whereupon");
	   stopwords.add("wherever");
	   stopwords.add("whether");
	   stopwords.add("which");
	   stopwords.add("while");
	   stopwords.add("whither");
	   stopwords.add("who");
	   stopwords.add("whoever");
	   stopwords.add("whole");
	   stopwords.add("whom");
	   stopwords.add("whose");
	   stopwords.add("why");
	   stopwords.add("will");
	   stopwords.add("willing");
	   stopwords.add("wish");
	   stopwords.add("with");
	   stopwords.add("within");
	   stopwords.add("without");
	   stopwords.add("wonder");
	   stopwords.add("would");
	   stopwords.add("would");
	   stopwords.add("x");
	   stopwords.add("y");
	   stopwords.add("yes");
	   stopwords.add("yet");
	   stopwords.add("you");
	   stopwords.add("your");
	   stopwords.add("yours");
	   stopwords.add("yourself");
	   stopwords.add("yourselves");
	   stopwords.add("z");
	   stopwords.add("zero");
	  }
	
}
