package rb.helpers;

public class ClassificationResult {
	public String resultString;

	public double probability;
	
	public ClassificationResult(String resultString, double probability) {
		super();
		this.resultString = resultString;
		this.probability = probability;
	}

}
