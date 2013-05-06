package si.zitnik.research.lemmagen.impl;

import java.io.Serializable;


public class LemmaRule implements Serializable {
	private static final long serialVersionUID = -2962477781793221124L;
	
	private int iId;
	private int iFrom;
	private String sFrom;
	private String sTo;
	private String sSignature;
	@SuppressWarnings("unused")
	private LemmatizerSettings lsett;

	public LemmaRule(String sWord, String sLemma, int iId, LemmatizerSettings lsett) {
		this.lsett = lsett;
		this.iId = iId;

		int iSameStem = SameStem(sWord, sLemma);
		sTo = sLemma.substring(iSameStem);
		iFrom = sWord.length() - iSameStem;

		if (lsett.bUseFromInRules) {
			sFrom = sWord.substring(iSameStem);
			sSignature = "[" + sFrom + "]==>[" + sTo + "]";
		}
		else {
			sFrom = null;
			sSignature = "[#" + iFrom + "]==>[" + sTo + "]";
		}
	}

	public String getSignature() {
		return sSignature;
	}
	public int getId() {
		return iId;
	}


	private static int SameStem(String sStr1, String sStr2) {
		int iLen1 = sStr1.length();
		int iLen2 = sStr2.length();
		int iMaxLen = Math.min(iLen1, iLen2);

		for (int iPos = 0; iPos < iMaxLen; iPos++)
			if (sStr1.charAt(iPos) != sStr2.charAt(iPos)) return iPos;

		return iMaxLen;
	}
	public Boolean IsApplicableToGroup(int iGroupCondLen) {
		return iGroupCondLen >= iFrom; 
	}
	public String Lemmatize(String sWord) {
		return sWord.substring(0, sWord.length() - iFrom) + sTo;
	}


	@Override
	public String toString() {
		return iId + ":" + sSignature;
	}



}

