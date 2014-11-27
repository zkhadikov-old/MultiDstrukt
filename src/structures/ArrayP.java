package structures;

import java.io.Serializable;

/**
 * Diese Klasse implementiert im Paper mit Array P bezeichnete Datenstruktur,
 * anders gesagt ein multidimensionales Array fuer die Prefix-Sum-Werte und
 * wird von dem Interface OlapCube abgeleitet.
 * 
 * @author Philippe Hagedorn und Zurab Khadikov
 */
public class ArrayP implements OlapCube, Serializable {

    private int[] circularDim;
    /**
     * Speichert Anzahl der Dimensionen
     */
    private int dim;
    /**
     * Eindimensionales Hilfsarray feur Werte
     */
    protected double[] werte;
    protected int[] dimensions;
    /**
     * Feur Speicherung von Dimensionsnamen
     */
    protected String[] description;
    /**
     * gibt Anzahl der Zellen ungleich 0
     */
    protected int realSize;
    /**
     * Hilfsarray zur Bestimmung der Position im
     */
    protected int[] positions;


    /*
     * (non-Javadoc)
     * 
     * @see structures.OlapCube#initialize(int[])
     */
    public void initialize(int[] dimensions, String[] description) {
        try {
            this.dimensions = dimensions;
            this.description = description;
            dim = dimensions.length;
            this.positions = new int[dim];
            int tmp = 1;
            this.positions[dim - 1] = 1;
            for (int i = 2; i <= dim; i++) {
                tmp *= dimensions[dim - i + 1];
                this.positions[dim - i] = tmp;
            }
            int size = tmp * dimensions[0];
            werte = new double[size];
            for (int i = 0; i < werte.length; i++) {
                werte[i] = 0;
            }

            circularDim = new int[dim + 1];
            circularDim[dim] = 0;
            for (int i = 0; i < dim; i++) {
                circularDim[i] = dimensions[i];
            }

        } catch (Exception e) {
            System.err.println("DataCube konnte nicht initialisiert werden!");
            System.err.println("Es gab folgende Fehler: " + e);
        }
    }

    /**
     * Construktor
     */
    public ArrayP() {
    }

    /**
     * Erstellt aus einem ArrayA ein ArrayP
     * 
     * @param A ArrayA, aus dem ArrayP berechnet werden soll
     */
    public ArrayP(ArrayA A) {
        this.initialize(A.getShape(), A.description);
        this.realSize = A.realSize;
        precompute(A);
    }

    /**
     * Hilfsmethode zum Berechnen des ArrayP bei Eingabe eines ArrayA
     * 
     * @param A
     */
    private void precompute(ArrayA A) {
        this.werte = A.werte.clone();
        precompute(0);
    }

    /**
     * Hilfs Methode die umrechnent eingegebenen Koordinates auf position im
     * Wertearray;
     * 
     * @param coordinates
     * @return Position im linearisierten Array
     */
    protected int getPosition(int[] coordinates) {
        int pos = 0;
        for (int i = 0; i < coordinates.length; i++) {
            pos += coordinates[i] * this.positions[i];
        }
        return pos;
    }

    /**
     * Berechnet Prefixsummen ab einer bestimmten Dimension
     * 
     * @param actDim aktuelle Dimension
     */
    private void precompute(int actDim) {
        if (actDim == dim) {
            return;
        }
        int[] lower = new int[dim];
        int[] current = new int[dim];
        int[] pred;
        circularDim[dim] = actDim;
        for (int i = 0; i < werte.length; i++) {
            if (current[actDim] != 0) {
                pred = current.clone();
                pred[actDim]--;
                werte[this.getPosition(current)] += werte[this.getPosition(pred)];

            }
            current = this.counternext(current, lower);
        }
        precompute(actDim + 1);

    }

    /**
     * Diese Methode preuft ob die Koordinaten korrekt eingegeben sind.
     * 
     * @param coordinates
     * @return true, wenn Koordinaten korrekt
     */
    public boolean coordinatesValidation(int[] coordinates) {
        boolean valid = true;
        if (dimensions.length == 0) {
            valid = false;
            System.err.println("DataCube ist nicht initialisiert!");
        } else {
            if (coordinates.length != this.dimensions.length) {
                // System.err.println("Koordinaten Eingabe ist ungeultig!");
                valid = false;// ungeultige Eingabe
            } else {
                for (int i = 0; i < coordinates.length; i++) {
                    if (coordinates[i] < 0 || coordinates[i] >= this.dimensions[i]) {
                        /*
                         * System.err .println("Koordinaten sind ausserhalb des
                         * Dimensions-Bereichs!");
                         */
                        valid = false;
                    }
                }
            }
        }
        return valid;
    }

    /*
     * (non-Javadoc)
     * 
     * @see structures.OlapCube#getRangeSum(int[],int[])
     */
    public double getRangeSum(int[] lower, int[] upper) {
        int[] current = new int[dim]; // Koordinate des Summanden
        int[] lohi = new int[dim]; // sorgt dafeur, dass alle Kombinationen
        // feur lohi durchlaufen werden.
        int vz = 1; // gibt an, ob Summand abgezogen oder addiert werden soll
        double result = 0;
        boolean ready = false; // gibt an, ob schon alle Meoglichkeiten
        // durchlaufen worden sind.
        while (!ready) {
            ready = true;
            vz = 1;
            for (int i = 0; i < lohi.length; i++) {
                if (lohi[i] == 0) {
                    current[i] = lower[i] - 1;
                    vz *= -1;
                    ready = false; // wenn einer der Werte im lohi-Array 0 ist,
                // dann sind noch nicht alle Werte
                // durchlaufen worden.
                } else {
                    current[i] = upper[i];
                }

            }
            // Falls eine Koordinate kleiner als 0 ist, so soll Summand nicht in
            // die Summe einfliessen
            if (this.coordinatesValidation(current)) {
                result += this.werte[this.getPosition(current)] * vz;
            }
            lohi = this.increment(lohi);
        }

        return result;
    }

    /**
     * Incrementer feur Dualzahlen in Arrayform
     * 
     * @param a Werte des Zaehlers
     * @return increment array
     */
    private int[] increment(int[] a) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] == 0) {
                a[i]++;
                break;
            } else {
                a[i] = 0;
            }
        }
        return a;
    }

    /**
     * Diese Methode berechnet die naechste benoetigte Zelle fuer PrefixSum
     * Berechnung. Das lower Array gibt eine untere Schranke fuer jede Dimension
     * an.
     * 
     * @param current aktuelle Koordinate
     * @param lower untere Schranke
     */
    private int[] counternext(int[] current, int[] lower) {
        int actDim = this.circularDim[this.dim];
        boolean ready = false;
        int j = actDim;
        int cycleCounter = 0;
        while (!ready) {
            if (current[j] < dimensions[j] - 1) {
                current[j]++;
                ready = true;
                break;
            }
            if (j < actDim) {
                for (int i = actDim; i < current.length; i++) {
                    current[i] = lower[i];
                }
                for (int i = 0; i <= j; i++) {
                    current[i] = lower[i];
                }
            } else {
                for (int i = actDim; i <= j; i++) {
                    current[i] = lower[i];
                }
            }
            if (j < current.length - 1) {
                j++;
            } else {
                j = 0;
            }
            cycleCounter++;
            if (cycleCounter == dim) {
                break;
            }
        }
        return current;
    }

    /*
     * (non-Javadoc)
     * 
     * @see structures.OlapCube#getCellValue(int[])
     */
    public double getCellValue(int[] coordinates) {

        return this.getRangeSum(coordinates, coordinates);
    }

    /*
     * (non-Javadoc)
     * 
     * @see structures.OlapCube#getShape()
     */
    public int[] getShape() {
        return dimensions.clone();
    }

    /*
     * (non-Javadoc)
     * 
     * @see structures.OlapCube#getSize()
     */
    public int getSize() {
        return realSize;
    }

    /*
     * (non-Javadoc)
     * 
     * @see structures.OlapCube#setCellValue(int[], double)
     */
    public void setCellValue(int[] coordinates, double value) {
        double oldValue = this.getCellValue(coordinates);
        // wegen Genauigkeit von double muss geprüft werden, ob der berechnete
        // Wertnicht betraglich so klein ist, dass eigentlich 0 da stehen
        // sollte.
        if (oldValue != 0 && Math.abs(oldValue) < 0.00001) {
            oldValue = 0;
        }
        if (coordinatesValidation(coordinates)) {
            if (value == 0) {
                if (oldValue != 0) // Wert wird auf 0 gesetzt
                {
                    realSize--;
                }
            } else if (oldValue == 0) {
                realSize++;
            }
        } else {
            return;
        }
        double dif = value - oldValue;
        int count = 1;
        int[] current = coordinates.clone();
        // Anzahl der Werte berechnen, die abge�ndert werden muessen
        for (int i = 0; i < dim; i++) {
            count *= dimensions[i] - coordinates[i];
        }
        // Werte abaendern
        for (int i = 0; i < count; i++) {
            werte[getPosition(current)] += dif;
            current = counternext(current, coordinates);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see structures.OlapCube#getDescription()
     */
    public String[] getDescription() {
        return this.description;
    }

    /**
     * Im Array A wird nicht unterstuetzt 
     * @param lower -
     * @param upper -
     * @param value -
     * @param strat -
     */
    public void rangeUpdate(int[] lower, int[] upper, double value, int strat) {

        return;
    }
}
