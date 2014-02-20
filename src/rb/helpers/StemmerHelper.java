package rb.helpers;

import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

public class StemmerHelper {
	
	public static SnowballStemmer stemmer = null;

	public static String stemWord(String word) {
		if (stemmer == null) {
			//System.out.println("Initializing stemmer...");

			Class<englishStemmer> stemClass = englishStemmer.class;
			try {
				stemmer = (SnowballStemmer) stemClass.newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		stemmer.setCurrent(word);
		stemmer.stem();
		return stemmer.getCurrent();
	}
	
}
