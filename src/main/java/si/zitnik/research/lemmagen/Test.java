package si.zitnik.research.lemmagen;

import si.zitnik.research.lemmagen.impl.Lemmatizer;

import java.text.NumberFormat;

public class Test {
	public static void main(String[] args) throws InterruptedException {
		System.out.println("Loading model...");
        checkMemory();
		Lemmatizer lm = LemmagenFactory.instance("data/lemmagenENModel.obj");
        System.out.println("Model loaded.");
        checkMemory();
        System.gc();
        Thread.sleep(10000);
        checkMemory();

		
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

    private static void checkMemory() {
        Runtime runtime = Runtime.getRuntime();

        NumberFormat format = NumberFormat.getInstance();

        StringBuilder sb = new StringBuilder();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();

        sb.append("free memory: " + format.format(freeMemory / 1024) + ", ");
        sb.append("allocated memory: " + format.format(allocatedMemory / 1024) + ", ");
        sb.append("max memory: " + format.format(maxMemory / 1024) + ", ");
        sb.append("total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024));

        System.out.println(sb.toString());

    }
}
