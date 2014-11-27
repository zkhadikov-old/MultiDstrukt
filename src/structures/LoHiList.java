package structures;

import java.io.Serializable;
import java.util.*;

/**
 * Liste zum abspeichern der Ranges von Hierarchien.
 * @author Khadikov, Hagedorn
 */
public class LoHiList implements Serializable {

    public LoHiNode head;       //erstes Element der Liste
    public LoHiNode tail;       //letztes Element der Liste
    private int size;           //Laenge der Liste

    /**
     * leerer Konstruktor
     */
    public LoHiList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    /**
     * Kontruktor, der zum Erzeugen einer kopierten Liste benoetigt wird.
     * @param size Groesse der Ursprungsliste
     */
    private LoHiList(int size) {
        this.head = null;
        this.tail = null;
        this.size = size;
    }

    /**
     * Methode, die Kopie der Liste erstellt.
     * @return Kopie von der Liste
     */
    @Override
    public LoHiList clone() {
        LoHiList result = new LoHiList(this.size);
        if (this.head != null) {
            result.head = this.head.clone();
            LoHiNode tmp = result.head;
            while (tmp.next() != null) {
                tmp = tmp.next();
            }
            result.tail = tmp;
        }
        return result;

    }

    /**
     * Fuegt ein Element hinten in die Liste ein.
     * @param node hinzuzufuegender Knoten
     */
    public void add(LoHiNode node) {
        if (node == null) {
            return;
        }
        if (this.head == null) {
            this.head = this.tail = new LoHiNode(node.low, node.high);
            this.tail = this.head;
            size = 1;
        } else {
            this.tail.setNext(new LoHiNode(node.low, node.high));
            this.tail = this.tail.next();
            size++;
        }
    }

    /**
     * Gibt die Groesse der Liste zurueck
     * @return Groesse der Liste
     */
    public int size() {
        return this.size;
    }

    /**
     * Erzeugt aus einer Liste von LoHiNodes eine minimale Ueberdeckung
     */
    public void mergeRange() {
        //erst Liste sortieren nach low wert
        this.sort();
        //Besteht Liste nur aus einem Element, dann fertig
        if (this.size() < 2) {
            return;
        }
        LoHiNode element1, element2;
        element1 = this.head;
        element2 = this.head.next();
        while (element2 != null) {
            // Prueft ob Elemente verschmolzen werden koennen
            if (element2.low <= element1.high + 1) {
                //entfernt eines der Elemente
                this.removeAfter(element1);
                //aendert evtl. die obere Grenze beim anderen Element
                if (element2.high > element1.high) {
                    element1.high = element2.high;
                }
                // geht zum naechsten Element weiter
                element2 = element1.next();
            } //keine Verschmelzung moeglich, weiter mit den naechsten Elementen
            else {
                element1 = element2;
                element2 = element1.next();
            }
        }
    }

    /**
     * Loescht Nachfolger eines Knotens
     * @param node Knoten, dessen Nachfolger entfernt werden muss.
     */
    public void removeAfter(LoHiNode node) {
        if (node == null || node == this.tail) {
            return;
        }
        LoHiNode tmp = node.next();
        if (tmp == this.tail) {
            this.tail = node;
            this.tail.setNext(null);
            this.size--;
            return;
        }
        node.setNext(tmp.next());
        this.size--;
        return;
    }

    /**
     * Verschmilzt zwei LoHiListen
     * @param lohi Liste, mit der verschmolzen wird.
     */
    public void addAll(LoHiList lohi) {
        if (this.head == null) {
            LoHiList result = lohi.clone();
            this.head = result.head;
            this.tail = result.tail;
            this.size = result.size;
        } else {
            //für Vorsortierung sorgen
            lohi = lohi.clone();
            lohi.sort();
            if (this.head.low <= lohi.head.low) {
                this.tail.setNext(lohi.head);
                this.tail = lohi.tail;
            } else {
                lohi.tail.setNext(this.head);
                this.head = lohi.head;
            }
            this.size += lohi.size();
        }
    }

    /**
     * Sortiermethode Bubble Sort
     */
    private void sort() {
        int k = this.size();
        if (k < 2) {
            return;
        }
        boolean finish = false;
        while (!finish) {
            finish = true;
            LoHiNode n1, n2, pred;
            pred = null;
            n1 = this.head;
            n2 = n1.next();
            for (int i = 0; i < k - 1; i++) {
                if (n1.low > n2.low) {
                    swap(n1, n2, pred);
                    finish = false;
                } else {
                    pred = n1;
                    n1 = n2;
                    n2 = n1.next();
                }
            }
            k--;
        }
    }

    /**
     * Hilfsmethode zum Vertauschen zweier Knoten n1 und n2
     * @param n1 erster Knoten
     * @param n2 zweiter Knoten (kommt auf jeden Fall hinter dem ersten)
     * @param pred Vorgaenger vom ersten Knoten
     */
    private void swap(LoHiNode n1, LoHiNode n2, LoHiNode pred) {
        LoHiNode tmp2;
        if (n1 == n2) {
            return;
        }
        tmp2 = n2.next();
        n1.setNext(tmp2);
        n2.setNext(n1);
        if (pred != null) {
            pred.setNext(n2);
        }
        if (n1 == this.head) {
            this.head = n2;
        }
        if (n2 == this.tail) {
            this.tail = n1;
        }
    }
}
