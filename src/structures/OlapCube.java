package structures;

/**
 * Describes the methods which must be supported by a data structure in order to
 * be used for OLAP range sum queries.
 * 
 * @author Tobias Lauer, modified by Philippe and Zurab
 */
public interface OlapCube {

    /**
     * Initializes this OlapCube with the given dimensions and their sizes. The
     * length of the array determines the number of dimensions.
     * 
     * @param dimensions
     *            an array containing the sizes of each dimension
     */
    public void initialize(int[] dimensions, String[] description);

    /**
     * Sets the value of a given cell to the given value. This method is used to
     * build the initial cube.
     * 
     * @param coordinates
     *            the coordinates of the cell (given by the dimension
     *            parameters)
     * @param value
     *            the new value of the cell
     */
    public void setCellValue(int[] coordinates, double value);

    /**
     * Returns the value stored in the given cell.
     * 
     * @param coordinates
     *            the coordinates of the cell (given by the dimension
     *            parameters)
     * @return the value contained in the given cell
     */
    public double getCellValue(int[] coordinates);

    /**
     * Returns the sum of the values of all cells within the given range.
     * 
     * @param lower
     *            array with the lower bound of the range for each dimension
     * @param upper
     *            array with the upper bound of the range for each dimension
     * @return the range sum
     */
    public double getRangeSum(int[] lower, int[] upper);

    /**
     * Returns an array whose length is the number of dimensions of theis
     * OlapCube and whose elements represent the size of each dimension.
     * 
     * @return the dimension description
     */
    public int[] getShape();

    /**
     * Returns the description of this cube, i.e. the labels of the dimensions.
     * 
     * @return an array with the dimension names
     */
    public String[] getDescription();

    /**
     * Returns the size of this OlapCube, i.e. the actual number of cells filled
     * with non-zero elements. The size should not be confused with the CAPACITY
     * of the OlapCube, which is the number of possible cells.
     * 
     * @return the number of filled cells
     */
    public int getSize();

    /**
     * Updates the whole selected range or one value if lower==upper -> true
     * Bei strat=0 wird die Aenderung auf alle Zellen gleichmaessig verteilt, bei strat!=0 werden alle Zellen um denselben Faktor erhoeht.
     * 
     * @param lower array with the lower bound of the range for each dimension
     * @param upper array with the upper bound of the range for each dimension
     * @param value the new value of the range
     * @param strat gibt an, wie die Aenderung der Range auf einzelne Zellen verteilt werden soll.
     */
    public void rangeUpdate(int[] lower, int[] upper, double value, int strat);
}
