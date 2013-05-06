package si.zitnik.research.lemmagen;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import si.zitnik.research.lemmagen.impl.Lemmatizer;
import si.zitnik.research.lemmagen.impl.LemmatizerSettings;
import si.zitnik.research.lemmagen.impl.MsdConsideration;

public class Train {
	public static void main(String[] args) throws NumberFormatException, IOException {
		String filename = "data/wfl-me-sl.tbl";
		String format = "WLM";
		String outputModelFilename = "data/lemmagenSLOModel.obj";
		
		BufferedReader br = new BufferedReader(new FileReader(filename));
		LemmatizerSettings settings = new LemmatizerSettings();
		settings.bUseFromInRules = false;
		settings.eMsdConsider = MsdConsideration.Ignore;
		settings.iMaxRulesPerNode = 0;
		settings.bBuildFrontLemmatizer = true;
		
		System.out.println("Building model...");
		Lemmatizer lm = new Lemmatizer(br, format, settings);
		lm.BuildModel();
		lm.DropExamples(); //to save space - we do not need examples as we have built model
		System.out.println("Model built");
		System.out.println("Saving model...");
		//LemmagenFactory.saveObject(lm, outputModelFilename);
		System.out.println("Model saved.");
	}
}
