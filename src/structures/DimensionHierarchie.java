package structures;

import java.io.Serializable;
import java.util.*;

/**
 * Klasse die Dimensionshierarchien definiert und speichert.
 * 
 * @author Philippe Hagedorn, Zurab Khadikov
 * 
 */
public class DimensionHierarchie implements Serializable {

    private String name;
    protected DimensionHierarchie[] children;
    private LoHiList lohi;

    /**
     * Konstruktor
     * @param name Name der Hierarchie
     * @param lohi Liste von Ranges
     */
    public DimensionHierarchie(String name, LoHiList lohi) {
        this.name = name;
        this.lohi = lohi;
    }

    /**
     * Konstruktor
     * @param name Name der Hierarchie
     * @param lohi Range der Hierarchie
     */
    public DimensionHierarchie(String name, LoHiNode lohi) {
        this.name = name;
        this.lohi = new LoHiList();
        this.lohi.add(lohi);
    }

    /**
     * Konstruktor, der aus Unterhierarchien eine Oberhierarchie erzeugt
     * @param name Name der Hierarchie
     * @param children Unterhierarchien
     */
    public DimensionHierarchie(String name, DimensionHierarchie[] children) {
        this.name = name;
        if (children != null) {
            this.children = children.clone();
        } else {
            this.children = null;
            return;
        }
        this.lohi = new LoHiList();
        for (int i = 0; i < this.children.length; i++) {
            this.lohi.addAll(this.children[i].lohi);
        }
        this.lohi.mergeRange();
        System.out.println();
    }

    /**
     * Get-Methode 
     * @return Liste mit Renges von Hierarchien 
     */
    public LoHiList getLoHi() {
        return this.lohi;
    }

    /**
     * Get-Methode
     * @return Name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get-Methode
     * @return Kinder 
     */
    public DimensionHierarchie[] getChildren() {
        if (this.children == null) {
            return null;
        }
        return this.children.clone();
    }
}
