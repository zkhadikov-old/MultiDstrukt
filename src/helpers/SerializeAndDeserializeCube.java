package helpers;

import java.io.*;
import java.util.*;

import structures.OlapCube;

/**
 * Diese Klasse erlaubt ein IDC Cube(Java Object), in eine Datei auf der Festplatte abzuspeichern und zu laden.
 * @author Philippe Hagedorn, Zurab Khadikov
 *
 */
public class SerializeAndDeserializeCube {

	/**
         * Diese Methode Speichert ein IDC Cube in eine Datei (Serialisier)
         * @param filename Dateiname unter der das Cube gespeichert wird
         * @param cube IDC Cube, der gespeichert werden muss
         */
	public static void serialize(String filename, OlapCube cube)
	{
		try
		{
			FileOutputStream file = new FileOutputStream( filename );
			ObjectOutputStream o = new ObjectOutputStream( file );
			o.writeObject(cube);
			o.close();
		}
		catch(IOException e) { System.err.println(e);}
		
		
	}
        
        /**
         * Diese Methode stellt ein gespeichertes IDC Cube wiederher und gib im
         * als ein Java Objekt zurück 
         * @param filename Dateiname unter der das Cube gespeichert ist
         * @return Java Objekt IDC
         */
	public static OlapCube deserialize(String filename) {
		OlapCube cube =null;
		try {
			FileInputStream file = new FileInputStream( filename );
			ObjectInputStream o = new ObjectInputStream( file );
			cube = (OlapCube) o.readObject();
			o.close();
		}
		catch (IOException e) { System.err.println(e);}
		catch (ClassNotFoundException e) { System.err.println(e);}
		return cube;
	}
}
