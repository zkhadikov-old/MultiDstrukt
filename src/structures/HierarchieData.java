
package structures;

import java.io.Serializable;

/**
 * Klasse, die Informationen enthaelt, um Hierarchien für den gesamten Cube zu laden und zu exportieren.
 * @author hagedorn
 */
public class HierarchieData implements Serializable{
    /**
     * enthaelt die Hierarchie fuer jede Dimension
     */
    public DimensionHierarchie[] hierarchies;
    /**
     * enthaelt Dimensionsgroessen. Wird benoetigt um zu pruefen, ob Hierarchie fuer Cube geeignet ist.
     */
    public int[] shape;
    
    /**
     * Leerer Konstruktor
     */
    public HierarchieData(){}
    
    /**
     * Konstruktor
     * @param hierarchies
     * @param shape
     */
    public HierarchieData(DimensionHierarchie[] hierarchies, int[] shape){
        this.hierarchies=hierarchies;
        this.shape=shape;
    }
}
