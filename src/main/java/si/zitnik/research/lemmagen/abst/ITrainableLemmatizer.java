package si.zitnik.research.lemmagen.abst;

import si.zitnik.research.lemmagen.impl.ExampleList;



public abstract class ITrainableLemmatizer extends ILemmatizer {
	private static final long serialVersionUID = -2007667916741203342L;
	
	private ExampleList exampleList;
	private ILemmatizerModel model;
	
	public ExampleList getExamples() {
		return this.exampleList;
	}
	public ILemmatizerModel getModel() {
		return this.model;
	}

	public abstract void AddExample(String sWord, String sLemma);
	public abstract void AddExample(String sWord, String sLemma, double dWeight);
	public abstract void AddExample(String sWord, String sLemma, double dWeight, String sMsd);

	public abstract void BuildModel();
}

