package structures;

import helpers.Counters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 * The Space-efficient Dynamic Data Cube is another possible 
 * pre-aggregation strategy for the Iterative Data Cube (IDC).
 * This version has been improved for efficiency and supports
 * a given hierarchy explicitly.
 * 
 * @author Tobias Lauer
 * @version 2008-03-27
 */
public class VariableSDDC implements IStrategie, Serializable {

    protected int dimLength;					// the length of the associated dimension
    private ArrayList<Integer> index;			// stores indices to be returned for queries
    
    private int[] blockEnds;					// end points of blocks defined by a hierarchy
    private ArrayList<Integer> splitPoints;		// split points for recursive pre-computation 
    private static ListIterator<Integer> iter;
    private Tree root;

    /**
     * Creates a new instance of SDDC. 
     *  
     * @param dimLength the length of the associated dimension
     */
    public VariableSDDC(int dimLength, int[] blockSizes) {
        this.dimLength = dimLength;
        index = new ArrayList<Integer>();
        blockEnds = new int[blockSizes.length];
        blockEnds[0] = blockSizes[0] - 1;
        for (int i = 1; i < blockSizes.length; i++) {
            this.blockEnds[i] = this.blockEnds[i - 1] + blockSizes[i];
        }
        if (this.blockEnds[blockSizes.length - 1] != this.dimLength - 1) {
            System.out.println("Error! Wrong block sizes!");
        }
    }

    /*
     * (non-Javadoc)
     * @see structures.IStrategie#getCellsForRangeSum(int, int)
     */
    public ArrayList<Integer> getCellsForRangeSum(int low, int high) {
        if (high < low) return null;
        
        index.clear();							// empty the ArrayList
        addCellsForHighRegionSum(high, 0, dimLength-1);
        addCellsForLowRegionSum(low - 1, 0, dimLength-1);
        
        /* The following variant may boost the "ALL" queries a bit but make others a bit slower (should be tested)
        if (high == dimLength-1) {				// if range ends at rightmost cell, 
        	index.add(dimLength);				// no recursion is necessary
        } else {  
        	addCellsForHighRegionSum(high, root.left);
        }
        if (low > 0)							
        	addCellsForLowRegionSum(low - 1, root.left);
        */
        
        //System.out.println("Number of cells: "+index.size());		// DEBUG

        return index;
    }
       
    /**
     * Recursively adds to the ArrayList all cells necessary to 
     * compute the region sum ending at the the given cell. 
     * Indices are shifted by one to the right. 
     * 
     * @param cell the endpoint of the region
     * @param N the current tree node
     */
    private void addCellsForHighRegionSum(int cell, int left, int right) {
    	if ( cell < left) return;
    	
    	if(cell == right) {
    		index.add(right+1);
    		return;
    	}
    	int split = divide(left, right);
    	
    	if (cell < split) {
    		addCellsForHighRegionSum(cell, left, split);
    	} else {
    		index.add(split + 1);			// key shifted by 1
    		addCellsForHighRegionSum(cell, split+1, right);
    	}
    }
    
    /**
     * Recursively adds to the ArrayList all cells necessary to 
     * compute the region sum ending at the given cell. 
     * Indices are shifted by one and multiplied by -1. If a cell 
     * to be added by this method is already contained in the list 
     * (counting positive), it will be neutralized, i.e. removed.
     * 
     * @param cell the endpoint of the region
     * @param N the current tree node
     */
    private void addCellsForLowRegionSum(int cell, int left, int right) {
    	if ( cell < left) return;
    	
    	if(cell == right) {
    		index.add(right+1);
    		return;
    	}
    	int split = divide(left, right);
    	
    	if (cell < split) {
    		addCellsForLowRegionSum(cell, left, split);
    	} else {
    		if (index.get(0) == split + 1)
    			index.remove(0);
    		else 
    			index.add(-(split + 1));	// key shifted by 1 and counted negative	
    		addCellsForLowRegionSum(cell, split +1, right);
    	}
    }
    

    /*
     * (non-Javadoc)
     * @see structures.IStrategie#getCellsForCellUpdate(int)
     */
    public ArrayList<Integer> getCellsForCellUpdate(int coordinate) {
        index.clear();				// empty the ArrayList
        addCellsForCellUpdate(coordinate, 0, dimLength-1);
        
       // System.out.println("Number of cells for UPDATE: "+index.size());		// DEBUG
        
        return index;
    }

    /**
     * Recursively adds to the ArrayList all the cells whose contents 
     * have to be changed for correctly updating the given cell.
     * 
     * @param cell the cell that is to be updated
     * @param N the current tree node
     */
    private void addCellsForCellUpdate(int cell, int left, int right) {
    	if (cell==right) {
    		index.add(cell);
    		return;
    	}
    	int split = divide(left, right);
    	if (cell > split) {
    		addCellsForCellUpdate(cell, split +1, right);
        } else {
            addCellsForCellUpdate(cell, left, split);
            index.add(right);
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
        System.out.println("Precomputing SDDC for dimension "+dimNr+"... ");
        int loops = idc.werte.length / dimLength;
        System.out.println("Loops for dim "+dimNr+": "+loops);	
        /*
        root = buildTree(0, dimLength - 1);
        splitPoints = new ArrayList<Integer>();
        orderSplitPoints(root.left);
        */
        for (int l = 0; l < loops; l++) {
        	/*
        	iter = splitPoints.listIterator();*/										// get list iterator for split points
            precomputeRange(idc, current, dimNr, 0, dimLength - 1);	// call recursive method
           /* current[dimNr] = dimLength - 1;											// go to last value of dimension 
            idc.werte[idc.getPosition(current)] += sum;*/ 							//d adjust it
            current = Counters.counternext(current, idc.getShape(), dimNr);			// go to the next "slice"
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
    private void precomputeRange(IDC idc, int[] current, int dimNr, int left, int right) {
    	//System.out.print("Precompute ["+left+" : "+right+"]   ");	// DEBUG
    	//double[] values = idc.werte;
        
        if (right > left) {
          
	        // Divide
	        int split = divide(left, right); 
	        
	        // Conquer
	        precomputeRange(idc, current, dimNr, left, split);
	        precomputeRange(idc, current, dimNr, split+1, right);
	
	        // Merge
	        current[dimNr] = split;
	        
	        double tmp=idc.werte[idc.getPosition(current)] ;			// set middle value to region sum
	        current[dimNr] = right;
	        idc.werte[idc.getPosition(current)] +=tmp;	// return complete range sum
        }
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
    
    /**
     * Builds a "pennant" tree over the given range. 
     * A pennant is a tree whose root has only one child (in this case, a left child).
     * 
     * @param left the left end point of the range
     * @param right the right end point of the range
     * @return the Tree object that forms the root 
     */
    private Tree buildTree(int left, int right) {
    	int diff = right - left;
    	
    	if (diff == 0) {
    		return new Tree(right);
    	} 
    	if (diff == 1) {
    		Tree t = new Tree(right);
    		t.left = new Tree(left);
    		return t;
    	}
    	
    	// Divide
    	int middle = divide(left, right);
    	
    	// Conquer
    	Tree t_left = buildTree(left, middle);
    	Tree t_right = buildTree(middle+1, right);
    	
    	// Merge
    	t_left.right = t_right.left;
    	t_right.left = t_left;
    	return t_right;
    }
    
    /**
     * Finds am optimal division line for splitting a range
     * in two halves while considering the hierarchy blocks. 
     *  
     * @param left the left end-pint of the array
     * @param right the right end-point of the array
     * @return the best splitting point
     */
    private int divide(int left, int right) {
    	// System.out.print("Divide ["+left+" : "+right+"]   ");	// DEBUG
    	
    	// Calculate the "middle" of the range [left : right-1]
    	int middle = (left + right) / 2;
    	
    	// Find minimum distance 
    	int min_diff = dimLength;
    	int optimum = -1;
    	for (int i=0; i<blockEnds.length; i++) {
    		if (blockEnds[i] > left && blockEnds[i] < right && Math.abs(blockEnds[i] - middle) < min_diff) {
    			min_diff = Math.abs(blockEnds[i] - middle);
    			optimum = i;
    		}
    	}
    	if (optimum > -1) {		// block end found in range
    		middle = blockEnds[optimum];
    	    //System.out.println("Optimum divide point found: "+middle);
    	} /* else {
    		System.out.println("No optimum found, using "+middle);
    	} */
    	
    	return middle;
    }
    
    /**
     * Recursively traverses the tree in pre-order and fills 
     * the keys of internal nodes in an array list. 
     * 
     * @param t the tree node to start at
     */
    private void orderSplitPoints(Tree t) {
    	if (t != null && (t.left != null || t.right != null)) {		// t is internal node
    		splitPoints.add(t.key);
    		//System.out.print(t.key+"  ");	// DEBUG
    		orderSplitPoints(t.left);
    		orderSplitPoints(t.right);
    	}
    }
    
    /**
     * Returns the next split point for the pre-computation.
     * 
     * @return the current split point
     */
    private int getSplitPosition() {
    	if (iter.hasNext()) {
    		return iter.next();
    	} else {	
    		System.out.println("ERROR in VariableSDDC! No split point available.");
    		return -1;
    	}
    }
}

/**
 * A simple tree structure.
 * 
 * @author Tobias Lauer
 */
class Tree implements Serializable{
	int key;
	Tree left, right;
	
	Tree(int k) {
		key = k;
		left = right = null;
	}
}

