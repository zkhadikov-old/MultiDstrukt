package structures;

import java.io.Serializable;

/**
 * Hilfsklasse, die die wichtigsten Informationen zum erzeugen einer Hierarchie enthält
 * @author Khadikov, Hagedorn
 */
public class HierarchieInfo implements Serializable {

    public String name;
    public LoHiList lohi;

    /**
     * Konstruktor mit Range (fuer Blaetter)
     * @param name Name
     * @param low untere Grenze
     * @param high obere Grenze
     */
    public HierarchieInfo(String name, int low, int high) {
        this.name = name;
        this.lohi = new LoHiList();
        this.lohi.add(new LoHiNode(low, high));
    }

    /**
     * Konstruktor ohne Range (fuer innere Knoten)
     * @param name Name
     */
    public HierarchieInfo(String name) {
        this.name = name;
        this.lohi = null;
    }

    /**
     * Name als String zurueck gibt
     * @return Name
     */
    public String toString() {
        return this.name;
    }
}
