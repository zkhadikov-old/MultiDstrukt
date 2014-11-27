package helpers;

import structures.HierarchieData;
import java.io.*;

/**
 * Diese Klasse erlaubt eine Hierarchie, in eine Datei auf der Festplatte abzuspeichern und zu laden. 
 * @author Hagedorn, Khadikov
 */
public class SerializeAndDeserializeHierarchie {
    /**
         * Diese Methode speichert die komplette Hierarchie eines IDC in eine Datei (Serialisier)
         * @param filename Dateiname unter der das hData gespeichert wird
         * @param hData HierarchieDaten, die gespeichert werden sollen
         */
	public static void serialize(String filename, HierarchieData hData)
	{
		try
		{
			FileOutputStream file = new FileOutputStream( filename );
			ObjectOutputStream o = new ObjectOutputStream( file );
			o.writeObject(hData);
			o.close();
		}
		catch(IOException e) { System.err.println(e);}
		
		
	}
        
        /**
         * Diese Methode stellt gespeicherte Hierarchiedaten wieder her und gib sie
         * als Java Objekt zurück.
         * @param filename Dateiname, unter der die HierarchieData gespeichert ist
         * @return HierarchieData
         */
	public static HierarchieData deserialize(String filename) {
		HierarchieData hData =null;
		try {
			FileInputStream file = new FileInputStream( filename );
			ObjectInputStream o = new ObjectInputStream( file );
			hData = (HierarchieData) o.readObject();
			o.close();
		}
		catch (IOException e) { System.err.println(e);}
		catch (ClassNotFoundException e) { System.err.println(e);}
		return hData;
	}

}
