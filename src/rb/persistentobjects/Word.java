package rb.persistentobjects;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.tartarus.snowball.SnowballProgram;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

import rb.helpers.StemmerHelper;

@Entity
public class Word {
	
	@Id public String word;
	public List<Word> directlyRelatedWords;
	public List<Word> indirectlyRelatedWords;
	public List<Statement> relatedStatements;
	public int count;

	public Word (String word) {
		String stemmed_word = StemmerHelper.stemWord(word);
		this.word = stemmed_word;
		
		directlyRelatedWords = new ArrayList<Word>();
		indirectlyRelatedWords = new ArrayList<Word>();
		relatedStatements = new ArrayList<Statement>();

		
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public List<Word> getDirectlyRelatedWords() {
		return directlyRelatedWords;
	}

	public void setDirectlyRelatedWords(List<Word> directlyRelatedWords) {
		this.directlyRelatedWords = directlyRelatedWords;
	}

	public List<Word> getIndirectlyRelatedWords() {
		return indirectlyRelatedWords;
	}

	public void setIndirectlyRelatedWords(List<Word> indirectlyRelatedWords) {
		this.indirectlyRelatedWords = indirectlyRelatedWords;
	}

	public List<Statement> getRelatedStatements() {
		return relatedStatements;
	}

	public void setRelatedStatements(List<Statement> relatedStatements) {
		this.relatedStatements = relatedStatements;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	
	public String asString() {
		return this.word;
	}
	

}
