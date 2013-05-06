package si.zitnik.research.lemmagen.abst;

import java.io.Serializable;

public abstract class ILemmatizer implements Serializable {
	private static final long serialVersionUID = 9097168731990427214L;

	public abstract String Lemmatize(String sWord);
}

