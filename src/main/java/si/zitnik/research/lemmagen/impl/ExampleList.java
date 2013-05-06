package si.zitnik.research.lemmagen.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class ExampleList implements Serializable {
	private static final long serialVersionUID = 7360348115142636711L;
	
	private LemmatizerSettings lsett;
	private RuleList rlRules;
	private HashMap<String, LemmaExample> dictExamples;
	private ArrayList<LemmaExample> lstExamples;

	public ExampleList(LemmatizerSettings lsett) {
		this.lsett = lsett;

		this.dictExamples = new HashMap<String, LemmaExample>();
		this.lstExamples = null;
		this.rlRules = new RuleList(lsett);
	}
	
	public ExampleList(BufferedReader srIn, String sFormat, LemmatizerSettings lsett) throws NumberFormatException, IOException {
		this(lsett);
		AddMultextFile(srIn, sFormat);
	}

	public LemmaExample getExampleAt(int i) {
			if (lstExamples == null) FinalizeAdditions();
			return lstExamples.get(i);
	}
	public int getCount() {
			if (lstExamples == null) FinalizeAdditions();
			return lstExamples.size();
	}
	public double getWeightSum() {
			if (lstExamples == null) FinalizeAdditions();

			double dWeight = 0;

			for (LemmaExample exm : lstExamples)
			dWeight += exm.getWeight();

			return dWeight;
	}
	public RuleList getRules() {
			return rlRules;
	}
	public List<LemmaExample> getListExamples() {
			if (lstExamples == null) FinalizeAdditions();
			return lstExamples;
	}

	public void AddMultextFile(BufferedReader srIn, String sFormat) throws NumberFormatException, IOException {
		//read from file
		String sLine = null;
		int iError = 0;
		int iLine = 0;

		int iW = sFormat.indexOf('W');
		int iL = sFormat.indexOf('L');
		int iM = sFormat.indexOf('M');
		int iF = sFormat.indexOf('F');
		int iLen = Math.max(Math.max(iW, iL), Math.max(iM, iF))+1;

		if (iW < 0 || iL < 0) {
			System.out.println("  Can not find word and lemma location in the format specification");
			return;
		}

		while ((sLine = srIn.readLine()) != null && iError < 50) {
			iLine++;

			String[] asWords = sLine.split("\t");
			if (asWords.length < iLen) {
				System.out.println("  ERROR: Line doesn't confirm to the given format \"" + sFormat + "\"! Line " + iLine + ".");
				iError++;
				continue;
			}

			String sWord = asWords[iW];
			String sLemma = asWords[iL];
			if (sLemma == "=") sLemma = sWord;
			String sMsd = null;
			if (iM > -1) sMsd = asWords[iM];
			double dWeight = 1; ;
			if (iF > -1)
				dWeight = Double.parseDouble(asWords[iM]);

			AddExample(sWord, sLemma, dWeight, sMsd);
		}
		if (iError == 50) System.out.println("  Parsing stopped because of too many (50) errors. Check format specification");
	}
	public LemmaExample AddExample(String sWord, String sLemma, double dWeight, String sMsd) {
		String sNewMsd = lsett.eMsdConsider != MsdConsideration.Ignore ? sMsd : null;
		LemmaExample leNew = new LemmaExample(sWord, sLemma, dWeight, sNewMsd, rlRules, lsett);
		return Add(leNew);
	}

	private LemmaExample Add(LemmaExample leNew) {
		//TODO: is this ok rewritten???

		if (!dictExamples.containsKey(leNew.getSignature())) {
			dictExamples.put(leNew.getSignature(), leNew);
		}


		lstExamples = null;

		return leNew;
	}
	public void DropExamples() {
		dictExamples.clear();
		lstExamples = null;
	}
	public void FinalizeAdditions() {
		if (lstExamples != null) return;
		lstExamples = new ArrayList<LemmaExample>(dictExamples.values());
		Collections.sort(lstExamples);
	}

	public ExampleList GetFrontRearExampleList(Boolean front) {
		ExampleList elExamplesNew = new ExampleList(lsett);

		for (LemmaExample le : this.getListExamples()) {
			if (front)
				elExamplesNew.AddExample(le.getWordFront(), le.getLemmaFront(), le.getWeight(), le.getMsd());
			else
				elExamplesNew.AddExample(le.getWordRear(), le.getLemmaRear(), le.getWeight(), le.getMsd());
		}
		elExamplesNew.FinalizeAdditions();

		return elExamplesNew;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (LemmaExample exm : lstExamples) {
			sb.append(exm.toString()+System.getProperty("line.separator"));
		}

		return sb.toString();
	}



}
