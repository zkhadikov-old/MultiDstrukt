package structures;

import java.util.ArrayList;

/**
 * Interface fuer verschiedene eindimensionale Techniken zum Speichern eines
 * Arrays.
 * 
 * @author Philippe Hagedorn und Zurab Khadikov
 */
public interface IStrategie {

    /**
     * Liefert die zum ausrechnen der RangeSum benoetigten Zellenindizes
     * Hat ein Index ein negatives Vorzeichen, so muss es negativ einberechnet werden.
     * Die Indizes werden um 1 verschoben, damit bei Null das Vorzeichen unterschieden werden kann.
     * @param low untere Grenze
     * @param high obere Grenze
     * @return Liste von Integer. Das Vorzeichen gibt an, ob addiert oder subtrahiert werden soll. Der Betrag minus 1 gibt den Index an.
     */
    public ArrayList<Integer> getCellsForRangeSum(int low, int high);

    /**
     * Liefert die zum Aktualisieren einer einzelnen Zelle benoetigten Zellenindizes
     * @param coordinate Koordinate der Zelle
     * @return Liste von benoetigten Zellenindizes
     */
    public ArrayList<Integer> getCellsForCellUpdate(int coordinate);

    /**
     * Wendet die Strategie auf ein Array an.
     * 
     * @param idc 
     * @param dimNr
     */
    public void precompute(IDC idc, int dimNr);

    /**
     * Liefert die ID der Strategie aus.
     * 
     * @return ID der Strategie
     */
    
    public int getID();

    /**
     * Gibt den Namen der Strategie an.
     * 
     * @return Name
     */
    public String getName();

    /**
     * Gibt die Groesse der Dimension an
     * 
     * @return Groesse
     */
    public int getDimLength();
}
