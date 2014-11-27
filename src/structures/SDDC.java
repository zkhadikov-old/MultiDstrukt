package structures;

import helpers.Counters;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The Space-efficient Dynamic Data Cube is another possible 
 * pre-aggregation strategy for the Iterative Data Cube (IDC).
 * It proceeds recursively, resulting in an asymptotic runtime
 * of O(log n) for both range queries and cell updates for each
 * row along this dimension, where n is the dimension length.
 * Note: This version does NOT support pre-defined hierarchies.
 * 
 * @author Tobias Lauer
 */
public class SDDC implements IStrategie, Serializable {

    protected int dimLength;			// the length of the associated dimension
    private ArrayList<Integer> index;	// stored indices to be returned for queries
    private int sign;					// will be either +1 or -1

    /**
     * Creates a new instance of SDDC. 
     *  
     * @param dimLength the length of the associated dimension
     */
    public SDDC(int dimLength) {
        this.dimLength = dimLength;
        index = new ArrayList<Integer>();
    }

    /*
     * (non-Javadoc)
     * @see structures.IStrategie#getCellsForRangeSum(int, int)
     */
    public ArrayList<Integer> getCellsForRangeSum(int low, int high) {
        if (high < low) {
            return null;
        }
        index.clear();		// empty the ArrayList

        sign = 1;			// higher region sum counts positive
        addCellsForRegionSum(high, 0, dimLength - 1);

        sign = -1;			// lower region sum counts negative
        addCellsForRegionSum(low - 1, 0, dimLength - 1);

        return index;
    }

    /**
     * Recursively adds to the ArrayList all cells necessary for 
     * the sum within the region defined by the given cell. 
     * (Indices are shifted by one and can also be negative.) 
     * 
     * @param cell the endpoint of the region
     * @param left the left end of the range to be queried here
     * @param right the right end of the range
     */
    private void addCellsForRegionSum(int cell, int left, int right) {
        if (cell < left) {			// Base case
            return;
        } else {					// Recursion
            int middle = left + (right - left) / 2;
            if (cell > middle) {
                index.add((middle + 2) * sign);				// includes shift by 1
                addCellsForRegionSum(cell, middle + 2, right);
            } else {
                index.add((left + 1) * sign);				// includes shift by 1
                addCellsForRegionSum(cell, left + 1, middle);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see structures.IStrategie#getCellsForCellUpdate(int)
     */
    public ArrayList<Integer> getCellsForCellUpdate(int coordinate) {
        index.clear();		// empty the ArrayList
        addCellsForCellUpdate(coordinate, 0, dimLength - 1);
        return index;
    }

    /**
     * Recursively adds to the ArrayList all the cells whose contents 
     * have to be changed for correctly updating the given cell.
     * 
     * @param cell the cell that is to be updated
     * @param left the left end of the range to be handled
     * @param right the right end of the range
     */
    private void addCellsForCellUpdate(int cell, int left, int right) {
        if (cell == right || cell < left) {			// Base case
            index.add(cell);
        } else {					// Recursion
            int middle = left + (right - left) / 2;
            if (cell > middle) {
                addCellsForCellUpdate(cell, middle + 2, right);
            } else {
                addCellsForCellUpdate(cell, left + 1, middle);
                index.add(middle + 1);
            }
        }
    }

    /**
     * Precomputes the given dimension according to the SDDC strategy.
     * 
     * @param idc the IDC
     * @param dimNr the number of the dimension to be precomputed
     */
    public void precompute(IDC idc, int dimNr) {
        int[] current = new int[idc.dimensionality];
        //System.out.println("Precomputing SDDC for dimension "+dimNr+"... ");
        int loops = idc.werte.length / dimLength;
        //System.out.println("Loops for dim "+dimNr+": "+loops);	
        for (int l = 0; l < loops; l++) {
            precomputeRange(idc, current, dimNr, 0, dimLength - 1);

            current[dimNr] = dimLength - 1;	// Set this dimension to highest value so counter counts correctly
            current = Counters.counternext(current, idc.getShape(), dimNr);
        }
    }

    /**
     * Recursive method for SDDC pre-computation.
     * 
     * @param idc the IDC this strategy belongs to
     * @param current a counter variable enumerating all relevant positions
     * @param left the left border of the sub-array
     * @param right the right border of the sub-array
     * @return the range-sum of the sub-array [left : right]
     */
    private double precomputeRange(IDC idc, int[] current, int dimNr, int left, int right) {
        double[] values = idc.werte;
        int diff = right - left;

        // Base cases
        if (diff < 0) {
            return 0;
        } else if (diff == 0) {
            current[dimNr] = left;
            return values[idc.getPosition(current)];
        }

        // Divide
        int middle = left + diff / 2;

        // Conquer
        current[dimNr] = left;
        double sum_1 = values[idc.getPosition(current)] + precomputeRange(idc, current, dimNr, left + 1, middle);
        current[dimNr] = middle + 1;
        double sum_2 = values[idc.getPosition(current)] + precomputeRange(idc, current, dimNr, middle + 2, right);

        // Merge
        current[dimNr] = middle + 1;
        values[idc.getPosition(current)] += sum_1;

        return sum_1 + sum_2;
    }

    /*
     * (non-Javadoc)
     * @see structures.IStrategie#getID()
     */
    public int getID() {
        // Not sure what to do here  	
    	
        return 201;
    }

    /*
     * (non-Javadoc)
     * @see structures.IStrategie#getName()
     */
    public String getName() {
        return "Space-efficient dynamic data cube";
    }

    /*
     * (non-Javadoc)
     * @see structures.IStrategie#getDimLength()
     */
    public int getDimLength() {
        return dimLength;
    }
}
