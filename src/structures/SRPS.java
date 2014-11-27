package structures;

import helpers.Counters;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Diese Klasse implementiert die eindimensionale Strategie "Space efficient relative prefix sum"
 * mit gleicher Blockgroesse fuer alle Bloecke. Fuer Blockgroesse 1 hat man den Spezialfall "Prefix Sum"
 * @author Philippe Hagedorn, Zurab Khadikov
 *
 */
public class SRPS implements IStrategie, Serializable {

    protected int blockSize;
    protected int dimLength;
    protected int rest;

    /**
     * Konstruktor
     * @param dimLength Laenge der Dimension
     * @param blockSize Groesse eines Blocks
     */
    public SRPS(int dimLength, int blockSize) {
        this.dimLength = dimLength;
        this.blockSize = blockSize;
        this.rest = this.dimLength % this.blockSize;
    }

    /*
     * (non-Javadoc)
     * @see structures.IStrategie#getCellsForRangeSum(int, int)
     */
    public ArrayList<Integer> getCellsForRangeSum(int low, int high) {
        if (high < low) {
            return null;
        }
        low--;
        int k;
        ArrayList<Integer> index = new ArrayList<Integer>();
        if (low >= 0) {
            k = low % this.blockSize;
            if (k != 0) {
                index.add(new Integer(-(low - k) - 1));
            }
            index.add(new Integer(-low - 1));
        }
        k = high % this.blockSize;
        if (k != 0) {
            index.add(new Integer(high - k + 1));
        }
        index.add(new Integer(high + 1));
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

        return 100;
    }
    /*
     * (non-Javadoc)
     * @see structures.IStrategie#getName()
     */

    public String getName() {
        return "Space efficient relative prefix sum";
    }

    /*
     * (non-Javadoc)
     * @see structures.IStrategie#precompute(structures.IDC, int)
     */
    public void precompute(IDC idc, int dimNr) {
        double[] werte = idc.werte;
        int[] shape = idc.getShape();
        double sum = 0;
        double sumLocal = 0;
        int[] current = new int[shape.length];
        for (int i = 0; i < werte.length; i++) {
            int tmp = idc.getPosition(current);
            sum += idc.werte[tmp];
            if (current[dimNr] % this.blockSize == 0) {
                werte[tmp] = sum;
                sumLocal = 0;
            } else {
                sumLocal += werte[tmp];
                werte[tmp] = sumLocal;
            }
            current = Counters.counternext(current, shape, dimNr);
            if (current[dimNr] == 0) {
                sum = 0;
            }
        }
        sum = 0;


    }

    /*
     * (non-Javadoc)
     * @see structures.IStrategie#getCellsForCellUpdate(int)
     */
    public ArrayList<Integer> getCellsForCellUpdate(int coordinate) {
        //ungueltige Koordinate
        if (coordinate < 0 && coordinate > this.dimLength - 1) {
            return null;
        }
        ArrayList<Integer> result = new ArrayList<Integer>();
        int k = coordinate % this.blockSize;
        //falls Koordinate ist nicht Beginn eines Blocks, muss Rest des Blocks aktualisiert werden
        if (k != 0) {
            for (int j = k + 1; j < this.blockSize; j++) {
                if (coordinate - k + j < this.dimLength) {
                    result.add(new Integer(coordinate - k + j));
                }
            }
        }
        result.add(new Integer(coordinate));                                            //fuegt die Koordinate selbst hinzu
        int blockNr = coordinate / this.blockSize;						//in wievieltem Block ist die Koordinate
        int toUpdate = (this.dimLength - 1) / this.blockSize - blockNr;				//wieviele Bloecke muessen aktualisiert werden.		
        k = (blockNr + 1) * this.blockSize;
        for (int j = 0; j < toUpdate; j++) //fuegt die restlichen Blockanfaenge hinzu
        {
            result.add(k + j * this.blockSize);
        }
        return result;
    }
}
