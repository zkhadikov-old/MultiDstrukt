package structures;

import helpers.Counters;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Diese Klasse implementiert die eindimensionale Strategie "Space efficient relative prefix sum"
 * mit unterschiedlichen Blockgroessen.
 * @author Philippe Hagedorn, Zurab Khadikov
 *
 */
public class VariableSRPS implements IStrategie, Serializable {

    protected int[] blockSizes;
    protected int dimLength;
    protected int[] blockBeginnings;

    /**
     * Konstruktor
     * @param dimSize Laenge der Dimension
     * @param blockSizes Groesse der einzelnen Bloecke
     */
    public VariableSRPS(int dimSize, int[] blockSizes) {
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
        ArrayList<Integer> result = new ArrayList<Integer>();
        //f�gt alle Blockanfaenge hinzu, die nach der Zelle kommen.
        for (int i = blockNr + 1; i < this.blockSizes.length; i++) {
            result.add(new Integer(this.blockBeginnings[i]));
        }

        //Fallunterscheidung: Zelle ist Blockanfang
        if (coordinate == this.blockBeginnings[blockNr]) {
            result.add(new Integer(coordinate));
        } else {
            int end;
            if (blockNr < this.blockBeginnings.length - 1) {
                end = this.blockBeginnings[blockNr + 1];
            } else //				ermittelt Ende des Blocks, in dem die Zelle liegt.
            {
                end = this.dimLength;
            }
            //fuegt alle Zellen von der Zelle bis Blockende hinzu.
            for (int i = coordinate; i < end; i++) {
                result.add(new Integer(i));
            }
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * @see structures.IStrategie#getCellsForRangeSum(int, int)
     */
    public ArrayList<Integer> getCellsForRangeSum(int low, int high) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        int k;
        low--;
        if (low >= 0) {
            int blockOfLow = this.getBlockNr(low);
            k = low - this.blockBeginnings[blockOfLow];
            if (k != 0) {
                result.add(new Integer(-this.blockBeginnings[blockOfLow] - 1));
            }
            result.add(new Integer(-low - 1));
        }

        int blockOfHigh = this.getBlockNr(high);
        k = high - this.blockBeginnings[blockOfHigh];
        if (k != 0) {
            result.add(new Integer(this.blockBeginnings[blockOfHigh] + 1));
        }
        result.add(new Integer(high + 1));
        return result;
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
        return 199;
    }

    /*
     * (non-Javadoc)
     * @see structures.IStrategie#getName()
     */
    public String getName() {
        return "Space Efficient Relative Prefix Sum with different Blocksizes";
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
        int blockNr = 0;
        int[] current = new int[shape.length];
        for (int i = 0; i < werte.length; i++) {
            int tmp = idc.getPosition(current);
            sum += werte[tmp];
            //Blockanfang
            if (current[dimNr] == this.blockBeginnings[blockNr]) {
                werte[tmp] = sum;
                sumLocal = 0;
                if (blockNr < this.blockBeginnings.length - 1) {
                    blockNr++;
                } else {
                    blockNr = 0;
                }
            } //kein Blockanfang
            else {
                sumLocal += werte[tmp];
                werte[tmp] = sumLocal;
            }
            //naechste Zelle
            current = Counters.counternext(current, shape, dimNr);
            if (current[dimNr] == 0) {
                sum = 0;
            }
        }
    }
}
