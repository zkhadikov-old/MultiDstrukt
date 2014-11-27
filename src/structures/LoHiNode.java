package structures;

import java.io.Serializable;

/**
 * Klasse zum Speichern einer einzelnen Range (Knoten)
 * @author Khadikov, Hagedorn
 */
public class LoHiNode implements Serializable {

    public int low,  high;
    private LoHiNode next;

    /**
     * Gibt Nachfolgerknoten zurueck
     * @return Nachfolger
     */
    public LoHiNode next() {
        return this.next;
    }

    /**
     * Setzt den Nachfolger
     * @param node neuer Nachfolger
     */
    public void setNext(LoHiNode node) {
        this.next = node;
    }

    /**
     * Konstruktor zum Erzeugen eines LoHiNodes anhand der Grenzen
     */
    public LoHiNode(int low, int high) {
        this.low = low;
        this.high = high;
        this.next = null;
    }

    /**
     * Standardkonstruktor
     */
    public LoHiNode() {
        this.next = null;
        this.low = 0;
        this.high = 0;
    }

    /**
     * kopiert einen Knoten und all dessen Nachfolger
     * @return Kopie
     */
    @Override
    public LoHiNode clone() {
        LoHiNode result = new LoHiNode(this.low, this.high);
        if (this.next != null) {
            result.setNext(this.next().clone());
        } else {
            result.next = null;
        }
        return result;
    }
}
