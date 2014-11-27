package structures;

import java.io.Serializable;

/**
 * Diese Klasse implementiert im Paper mit Array A bezeichnete Datenstruktur,
 * anders gesagt ein multidimensionales Array und wird von dem Interface
 * OlapCube abgeleitet.
 * 
 * @author Philippe Hagedorn, Zurab Khadikov
 */
public class ArrayA implements OlapCube, Serializable {

    /**
     * Eindimensionales Hilfsarray feur Werte
     */
    protected double[] werte;
    protected int[] dimensions;
    /**
     * Feur Speicherung von Dimensionsnamen
     */
    protected String[] description;
    protected int realSize;
    /**
     * Hilfsarray zur Bestimmung der Position im
     * eindimensionalen Array "werte"
     */
    protected int[] positions;
    /**
     * Speichert die Zwischenwerte von RangeSummen
     */
    private double rangeSum;

    /*
     * (non-Javadoc)
     * 
     * @see structures.OlapCube#initialize(int[])
     */
    public void initialize(int[] dimensions, String[] description) {
        try {
            this.dimensions = dimensions;
            this.description = description;
            int length = dimensions.length;
            this.positions = new int[length];
            int tmp = 1;
            this.positions[length - 1] = 1;
            for (int i = 2; i <= length; i++) {
                tmp *= dimensions[length - i + 1];
                this.positions[length - i] = tmp;
            }
            int size = tmp * dimensions[0];
            werte = new double[size];
            rangeSum = 0;
        } catch (Exception e) {
            System.err.println("DataCube konnte nicht initialisiert werden!");
            System.err.println("Es gab folgende Fehler: " + e);
        }
    }

    /**
     * Diese Methode prueft ob die Koordinaten korrekt eingegeben sind.
     *
     */
    public boolean coordinatesValidation(int[] coordinates) {
        boolean valid = true;
        if (dimensions.length == 0) {
            valid = false;
            System.err.println("DataCube ist nicht initialisiert!");
        } else {
            if (coordinates.length != this.dimensions.length) {
                valid = false;// ungueltige Eingabe
            } else {
                for (int i = 0; i < coordinates.length; i++) {
                    if (coordinates[i] < 0 || coordinates[i] >= this.dimensions[i]) {
                        valid = false;
                    }
                }
            }
        }
        return valid;
    }

    /**
     * Hilfsmethode, die umrechnet eingegebene Koordinaten auf die Position im
     * eindimensionalen Wertearray.
     * 
     * @param coordinates
     * @return Position
     */
    protected int getPosition(int[] coordinates) {
        int pos = 0;
        for (int i = 0; i < coordinates.length; i++) {
            pos += coordinates[i] * this.positions[i];
        }
        return pos;
    }

    /*
     * (non-Javadoc)
     * 
     * @see structures.OlapCube#setCellValue(int[], double)
     */
    public void setCellValue(int[] coordinates, double value) {
        if (coordinatesValidation(coordinates)) {
            int pos = getPosition(coordinates);
            if (value == 0) {
                if (this.werte[pos] != 0) // Wert wird auf 0 gesetzt
                {
                    realSize--;
                }
            } else if (this.werte[pos] == 0) {
                realSize++;
            }
            this.werte[pos] = value;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see structures.OlapCube#getCellValue(int[])
     */
    public double getCellValue(int[] coordinates) {
        double value = 0;
        if (coordinatesValidation(coordinates)) {
            int pos = this.getPosition(coordinates);
            value = werte[pos];
        }
        return value;
    }

    /**
     * Diese Methode addiert rekursiv alle Werte von Zellen, die zu Summieren
     * sind, zur Instanz-Variablen rangeSum.
     * 
     * @param lower untere Grenze
     * @param upper obere Grenze
     * @param dim aktuelle Dimension
     */
    private void getRangeSumRec(int[] lower, int[] upper, int dim) {
        if (lower == upper) {
            upper = lower.clone();
        }
        this.rangeSum += this.getCellValue(lower);
        for (int i = dim; i < lower.length; i++) {
            lower[i]++;
            if (lower[i] <= upper[i]) {
                getRangeSumRec(lower, upper, i); // Rekursive Aufruf
            }
            lower[i]--;
        }
    }

    /**
     * Diese Methode prueft ob Rangekoordinaten sinvoll gewaehlt sind.
     * 
     * @param lower untere Schranke
     * @param upper obere Schranke
     * @return true wenn ales O.K. ist
     */
    public boolean rangeValidator(int[] lower, int[] upper) {
        boolean valid = true;
        for (int i = 0; i < lower.length; i++) {
            if (lower[i] - upper[i] > 0) {
                System.err.println("Rangekoordinaten sind falsh geweahlt!");
                valid = false;
            }
        }
        return valid;
    }

    /*
     * (non-Javadoc)
     * 
     * @see structures.OlapCube#getRangeSum(int[], int[])
     */
    public double getRangeSum(int[] lower, int[] upper) {
        this.rangeSum = 0;
        if (coordinatesValidation(lower) && coordinatesValidation(upper) && rangeValidator(lower, upper)) {
            this.getRangeSumRec(lower, upper, 0);
        }
        return this.rangeSum;
    }

    /*
     * (non-Javadoc)
     * 
     * @see structures.OlapCube#getShape()
     */
    public int[] getShape() {
        return this.dimensions.clone();
    }

    /*
     * (non-Javadoc)
     * 
     * @see structures.OlapCube#getSize()
     */
    public int getSize() {
        return this.realSize;
    }

    /*
     * (non-Javadoc)
     * 
     * @see structures.OlapCube#getDescription()
     */
    public String[] getDescription() {
        return this.getDescription().clone();
    }

    public void rangeUpdate(int[] lower, int[] upper, double value, int strat) {

        return;
    }
}
