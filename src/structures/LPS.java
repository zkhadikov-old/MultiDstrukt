package structures;

import helpers.Counters;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Diese Klasse implementiert die eindimensionale Strategie "Local prefix sum"
 * mit gleicher Blockgroesse fuer alle Bloecke.
 * @author Philippe Hagedorn, Zurab Khadikov
 *
 */
public class LPS implements IStrategie, Serializable {

    protected int blockSize;
    protected int dimLength;

    /**
     * Konstruktor
     * @param dimLength Laenge der Dimension
     * @param blockSize Groesse eines Blocks
     */
    public LPS(int dimLength, int blockSize) {
        this.dimLength = dimLength;
        this.blockSize = blockSize;
    }
    /*
     * (non-Javadoc)
     * @see structures.IStrategie#getCellsForCellUpdate(int)
     */

    public ArrayList<Integer> getCellsForCellUpdate(int coordinate) {
        int k = coordinate % this.blockSize;
        int block = coordinate / this.blockSize;
        ArrayList<Integer> Index = new ArrayList<Integer>();
        int end = blockSize;
        //wenn die Koordinate im letzten Block liegt, end auf Gr��e des letzten Blocks setzen
        if (block == (this.dimLength - 1) / this.blockSize) {
            end = (this.dimLength - 1) % this.blockSize + 1;
        }
        for (int i = k; i < end; i++) {
            Index.add(new Integer(block * this.blockSize + i));
        }
        return Index;
    }
    /*
     * (non-Javadoc)
     * @see structures.SRPS#getCellsForRangeSum(int, int)
     */

    public ArrayList<Integer> getCellsForRangeSum(int low, int high) {
        if (high < low) {
            return null;
        }
        int k = low % this.blockSize;
        int blockOfLow = low / this.blockSize + 1;
        int blockOfHigh = high / this.blockSize + 1;
        ArrayList<Integer> index = new ArrayList<Integer>();
        //Was muss abgezogen werden
        if (k > 0) {
            index.add(new Integer(-low));				//-(low-1+1), wegen Index-Verschiebung			
        }
        //F�gt alle Blockenden, die zwischen low-1 und high liegen. Indexverschiebung ist einberechnet.
        for (int j = blockOfLow; j < blockOfHigh; j++) {
            index.add(new Integer(j * this.blockSize));
        }
        index.add(high + 1);
        return index;
    }

    /*
     * (non-Javadoc)
     * @see structures.IStrategie#getDimLength()
     */
    public int getDimLength() {
        return this.dimLength;
    }

    /*
     * (non-Javadoc)
     * @see structures.IStrategie#getID()
     */
    public int getID() {
        return 101;
    }

    /*
     * (non-Javadoc)
     * @see structures.IStrategie#getName()
     */
    public String getName() {
        return "Local Prefix Sum";
    }

    /*
     * (non-Javadoc)
     * @see structures.IStrategie#precompute(structures.IDC, int)
     */
    public void precompute(IDC idc, int dimNr) {
        double[] werte = idc.werte;
        int[] shape = idc.getShape();
        int[] current = new int[shape.length];
        int[] predecessor;
        for (int i = 0; i < werte.length; i++) {
            predecessor = current.clone();
            predecessor[dimNr]--;
            if (current[dimNr] % this.blockSize != 0) {
                werte[idc.getPosition(current)] += werte[idc.getPosition(predecessor)];
            }
            current = Counters.counternext(current, shape, dimNr);
        }
    }
}
