package si.zitnik.research.lemmagen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import si.zitnik.research.lemmagen.impl.Lemmatizer;

public class LemmagenFactory {
	
	public static Lemmatizer instance(String filename) {
		return (Lemmatizer)readObject(filename);
	}
	
	public static void saveObject(Object obj, String filename) {
		File f = new File(filename);
		try {
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
			fos.close();
		} catch (Exception e) { 
			System.out.println("Error writing object to file "+filename+".");
			e.printStackTrace();
		}
	}

	public static Object readObject(String filename) {
		File f = new File(filename);
		Object retVal = null;
		try {
			FileInputStream fis = new FileInputStream(f);
			ObjectInputStream ois = new ObjectInputStream(fis);
			retVal = ois.readObject();
			fis.close();
		} catch (Exception e) {
			System.out.println("Error reading object from file "+filename+".");
			e.printStackTrace();
		}
		return retVal;
	}
	
	public static Object readObject(InputStream f) {
		Object retVal = null;
		try {
			ObjectInputStream ois = new ObjectInputStream(f);
			retVal = ois.readObject();
		} catch (Exception e) {
			System.out.println("Error reading object from stream.");
			e.printStackTrace();
		}
		return retVal;
	}
}
