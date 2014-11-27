package structures;

import java.io.Serializable;

/**
 * Die Klasse implementiert, die fuer ein IDC benoetigten Dimensionen, 
 * mit allen noetigen Informationen.
 * @author Philippe Hagedorn, Zurab Khadikov
 *
 */
public class IDCDimension extends Dimension implements Serializable {

    protected IStrategie strategie;
    protected DimensionHierarchie start;

    /**
     * Konstruktor
     * @param size Laenge der Dimension
     * @param name Name der Dimension
     */
    public IDCDimension(int size, String name) {
        super(size, name);
    }

    /**
     * Setzt die Hierarchie fuer die Dimension
     * @param hierarchie
     */
    public void setHierarchie(DimensionHierarchie hierarchie) {
        this.start = hierarchie;
    }

    /**
     * Get-Methode 
     * @return Hierarchie fuer entsprechende Dimension  
     */
    public DimensionHierarchie getHierarchie() {
        return this.start;
    }

    /**
     * Get-Methode
     * @return Strategie fuer entsprechende Dimension
     */
    public IStrategie getStrategie() {
        return this.strategie;
    }

    /**
     * Setzt die Strategie fuer die Dimension
     * @param strategie
     */
    public void setStrategie(IStrategie strategie) {
        if (this.strategie == null) {
            this.strategie = strategie;
        }
    }
}
