package structures;

import helpers.Counters;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Diese Klasse implementiert die eindimensionale Strategie "Local prefix sum"
 * mit variablen Blockgroesse.
 * @author Philippe Hagedorn, Zurab Khadikov
 *
 */
public class VariableLPS implements Serializable, IStrategie {

    protected int[] blockSizes;
    protected int dimLength;
    protected int[] blockBeginnings;

    /**
     * Konstruktor
     * @param dimSize Laenge der Dimension
     * @param blockSizes Groesse der einzelnen Bloecke
     */
    public VariableLPS(int dimSize, int[] blockSizes) {
        this.dimLength = dimSize;
        this.blockSizes = blockSizes.clone();
        this.blockBeginnings = new int[this.blockSizes.length];
        for (int i = 1; i < this.blockSizes.length; i++) {
            this.blockBeginnings[i] = this.blockBeginnings[i - 1] + this.blockSizes[i - 1];
        }
        if (this.blockBeginnings[this.blockSizes.length - 1] + this.blockSizes[this.blockSizes.length - 1] != this.dimLength) {
            System.out.println("Wrong Block Sizes!");
        }

    }

    /**
     * schaut in welchem Block die Zelle liegt.
     * @param cellNr
     * @return Nummer
     */
    private int getBlockNr(int cellNr) {
        int i;
        for (i = this.blockSizes.length - 1; i >= 0; i--) {
            if (cellNr >= this.blockBeginnings[i]) {
                break;
            }
        }
        return i;
    }

    /*
     * (non-Javadoc)
     * @see structures.IStrategie#getCellsForCellUpdate(int)
     */
    public ArrayList<Integer> getCellsForCellUpdate(int coordinate) {
        int blockNr = this.getBlockNr(coordinate);
        int end;
        ArrayList<Integer> Index = new ArrayList<Integer>();
        if (blockNr == this.blockBeginnings.length - 1) {
            end = this.dimLength;
        } else {
            end = this.blockBeginnings[blockNr + 1];
        }
        for (int i = coordinate; i < end; i++) {
            Index.add(new Integer(i));
        }
        return Index;
    }

    /*
     * (non-Javadoc)
     * @see structures.IStrategie#getCellsForRangeSum(int, int)
     */
    public ArrayList<Integer> getCellsForRangeSum(int low, int high) {
        ArrayList<Integer> Index = new ArrayList<Integer>();
        int blockOfLow = this.getBlockNr(low);
        int blockOfHigh = this.getBlockNr(high);
        int k = low - this.blockBeginnings[blockOfLow];
        if (k > 0) {
            Index.add(new Integer(-low));
        }			//-(low-1+1), wegen Index-Verschiebung
//		F�gt alle Blockenden, die zwischen low-1 und high liegen. Indexverschiebung ist einberechnet.
        for (int j = blockOfLow; j < blockOfHigh; j++) {
            Index.add(new Integer(this.blockBeginnings[j] + this.blockSizes[j]));
        }
        Index.add(new Integer(high + 1));
        return Index;
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
        return 052;
    }

    /*
     * (non-Javadoc)
     * @see structures.IStrategie#getName()
     */
    public String getName() {
        return "Local Prefix Sum with variable block size";
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
        int blockNr = 0;
        for (int i = 0; i < werte.length; i++) {
            predecessor = current.clone();
            predecessor[dimNr]--;
            //Blockanfang
            if (current[dimNr] == this.blockBeginnings[blockNr]) {
                if (blockNr < this.blockBeginnings.length - 1) {
                    blockNr++;
                } else {
                    blockNr = 0;
                }
            } //kein Blockanfang
            else {
                werte[idc.getPosition(current)] += werte[idc.getPosition(predecessor)];
            }
            //n�chste Zelle
            current = Counters.counternext(current, shape, dimNr);
        }

    }
}
