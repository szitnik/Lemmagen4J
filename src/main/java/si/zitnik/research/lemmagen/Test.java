package si.zitnik.research.lemmagen;

import si.zitnik.research.lemmagen.impl.Lemmatizer;

public class Test {
	public static void main(String[] args) {
		System.out.println("Loading model...");
		Lemmatizer lm = LemmagenFactory.instance("data/lemmagenSLOModel.obj");
		System.out.println("Model loaded.");
		
		System.out.println("Trying some test cases:");
		System.out.println(lm.Lemmatize("bureka"));
		System.out.println(lm.Lemmatize("Pavla"));
		System.out.println(lm.Lemmatize("Pavlu"));
		System.out.println(lm.Lemmatize("Slavkotu"));
		System.out.println(lm.Lemmatize("Marjana"));
		System.out.println(lm.Lemmatize("LDS-u"));
		System.out.println(lm.Lemmatize("Pavarottija"));
		System.out.println(lm.Lemmatize("Mazzinijem"));
	}
}
