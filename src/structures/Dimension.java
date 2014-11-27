package structures;

import java.io.Serializable;

/**
 * Oberklasse fuer Dimensionen von OlapCubes mit minimalen Informationen
 * @author Philippe Hagedorn und Zurab Khadikov
 * 
 */
public class Dimension implements Serializable {

    protected String name;
    protected int size;

    /**
     * Konstruktor
     * @param size Laenge der Dimension
     * @param name Bezeichnung der Dimension
     */
    public Dimension(int size, String name) {
        this.size = size;
        this.name = name;
    }

    /**
     * Get-Methode 
     * @return Name von Dimension
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get-Methode
     * @return Groesse von Dimension
     */
    public int getSize() {
        return size;
    }
}
