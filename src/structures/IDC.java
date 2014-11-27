package structures;

import helpers.Counters;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Datenstruktur zum Speichern von Cubes.
 * @author Philippe Hagedorn und Zurab Khadikov
 * 
 */
public class IDC implements OlapCube, Serializable {

    /**
     * Eindimensionales Hilfsarray feur Werte
     */
    protected double werte[];
    protected int realSize;
    protected int dimensionality;
    public IDCDimension[] dimension;
    /**
     * Hilfsarray zur Bestimmung der Position im
     * eindimensionalen Array "werte"
     */
    private int[] positions;
    /**
     * Gibt an, ob Vorberechnung schon stattgefunden hat.
     */
    private boolean precomputed;

    /*
     * (non-Javadoc)
     * 
     * @see structures.OlapCube#initialize(int[], java.lang.String[])
     */
    public IDC() {
    }

    /**
     * Initialiesierungsmethode, dabei wird Hautstruktur von IDC Cube festgelegt
     * @param dimensions Dimensionssgoessen 
     * @param description Beschreiburng fuer jede Dimension
     */
    public void initialize(int[] dimensions, String[] description) {
        try {
            this.dimensionality = dimensions.length;
            this.positions = new int[dimensionality];
            this.dimension = new IDCDimension[dimensionality];

            //Setzen der Strategien sollte auch anders gehen koennen, jetzt Default Strategie
            for (int i = 0; i < dimensionality; i++) {
                this.dimension[i] = new IDCDimension(dimensions[i], description[i]);
            }

            int tmp = 1;
            this.positions[dimensionality - 1] = 1;
            for (int i = 2; i <= dimensionality; i++) {
                tmp *= dimensions[dimensionality - i + 1];
                this.positions[dimensionality - i] = tmp;
            }

            int size = tmp * dimensions[0];
            werte = new double[size];
            precomputed = false;

        } catch (Exception e) {
            System.err.println("IDC DataCube konnte nicht initialisiert werden!");
            System.err.println("Es gab folgende Fehler: " + e);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see structures.OlapCube#setCellValue(int[], double)
     */
    public void setCellValue(int[] coordinates, double value) {
        if (coordinatesValidation(coordinates)) {
            int pos = getPosition(coordinates);
            if (!precomputed) {
                if (value == 0) {
                    if (this.werte[pos] != 0) // Wert wird auf 0 gesetzt
                    {
                        realSize--;
                    }
                } else if (this.werte[pos] == 0) {
                    realSize++;
                }
                this.werte[pos] = value;
            } else {
                rangeUpdate(coordinates, coordinates, value, 0);
            }
        }
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
     * @see structures.OlapCube#getRangeSum(int[], int[])
     */
    public double getRangeSum(int[] lower, int[] upper) {
        if (!this.rangeValidator(lower, upper)) {
            return 0;
        }
        //Speichert das Zwischenergebnis
        double sum = 0;
        //Enthaelt die Indizes fuer alle Dimensionen
        ArrayList<Integer> Indizes = new ArrayList<Integer>();
        //pos[i] enthaelt die erste Position, die einen Index fuer die Dimension i enthaelt
        int[] pos = new int[this.dimensionality];
        //Hilfsvariable zur Berechnung des korrekten pos-Arrays
        int position = 0;
        //Speichert die Anzahl an Werten, die man zur Summe hinzufuegen muss
        int totalNumber = 1;
        //Fuegt die Indizes aller Dimensionen zu einer Liste zusammen
        for (int i = 0; i < this.dimensionality; i++) {
            ArrayList<Integer> tmp = this.dimension[i].getStrategie().getCellsForRangeSum(lower[i], upper[i]);
            Indizes.addAll(tmp);
            pos[i] = tmp.size();
            totalNumber *= pos[i];
            position += pos[i];
        }
        //Enthaelt fuer jede Dimension Anzahl der Indizes
        int[] shape = pos.clone();
        //Berechnet korrektes Indizes Array
        for (int i = this.dimensionality - 1; i >= 0; i--) {
            pos[i] = position - pos[i];
            position = pos[i];
        }
        //Hilfsarray, das dafuer sorgt, dass alle moeglichen Indexkombinationen durchlaufen werden.
        int[] current = new int[this.dimensionality];
        //enthaelt die Positionen in der Liste, wo die Koordinaten drinstehen
        int[] ListPos;

        for (int i = 0; i < totalNumber; i++) {
            ListPos = this.getListPositions(current, pos);
            //enthaelt die Koordinaten, die zur Summe dazugerechnet werden muessen.
            int[] coordinates = new int[this.dimensionality];
            //Vorzeichen des Summanden
            int vz = 1;
            for (int k = 0; k < this.dimensionality; k++) {
                //liest Koordinaten aus wandelt diese um
                coordinates[k] = Math.abs(Indizes.get(ListPos[k])) - 1;
                if (Indizes.get(ListPos[k]) < 0) {
                    vz *= -1;
                }

            }
            sum += vz * this.werte[this.getPosition(coordinates)];
            current = Counters.counternext(current, shape, 0);
        }

        return sum;
    }

    /**
     * Hilfsmethode, um Positionen von Indizes im Hilfsarray zu ermitteln.
     * @param coordinates Koordinaten bzgl. Hilfsarray
     * @param pos Positionsarray (wo faengt welche Dimension an)
     * @return Koordinate bzgl. Cube
     */
    protected int[] getListPositions(int[] coordinates, int[] pos) {
        int[] result = new int[coordinates.length];
        for (int i = 0; i < coordinates.length; i++) {
            result[i] = coordinates[i] + pos[i];
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see structures.OlapCube#getShape()
     */
    public int[] getShape() {
        int[] dimensions = new int[dimensionality];
        for (int i = 0; i < dimensionality; i++) {
            dimensions[i] = this.dimension[i].getSize();
        }
        return dimensions;
    }

    /*
     * (non-Javadoc)
     * 
     * @see structures.OlapCube#getDescription()
     */
    public String[] getDescription() {
        String[] description = new String[dimensionality];
        for (int i = 0; i < dimensionality; i++) {
            description[i] = this.dimension[i].getName();
        }
        return description;
    }

    /*
     * (non-Javadoc)
     * 
     * @see structures.OlapCube#getSize()
     */
    public int getSize() {
        return realSize;
    }
    
    /**
     * Alternative Methode um Real Size zurück zu geben.
     * @return Tatsaechliche Anzahl von Zellen im Cube, die nicht gleich Null sind.
     */
    public int getSizeAlt() {
        int realSize=0;
        for(int i=0; i<this.werte.length; i++){
            if(this.werte[i]!=0)
                realSize++;
        }   
        return realSize;
    }

    /*
     * (non-Javadoc)
     * 
     * @see structures.OlapCube#rangeUpdate(int[] lower, int[] upper, double
     *      value,int strategieNum)
     */
    public void rangeUpdate(int[] lower, int[] upper, double value, int strat) {
        if (!this.rangeValidator(lower, upper)) {
            return;
        }
        int[] upperBound = upper.clone();
        for (int i = 0; i < upperBound.length; i++) {
            upperBound[i]++;
        }
        //einfacher Fall, nur eine Zelle updaten
        if (lower == upper) {
            double oldValue = this.getCellValue(lower);
            if (oldValue == value) {
                return;
            }
            double dif = value - oldValue;
            int pos = this.getPosition(lower);
            
            //Hilfsmethode wird aufgerufen
            CellUpdateDif(lower, dif);
        } else {
            double oldValue = this.getRangeSum(lower, upper);            
            double dif = value - oldValue;
            if (dif == 0) {
                return;
            }
            //Anzahl der betroffenen Zellen ermitteln
            int count = 1;
            for (int i = 0; i < this.dimensionality; i++) {
                count *= upper[i] - lower[i] + 1;
            }
            //neuer Wert ist 0
            if (value==0){
                int[] current=lower.clone();
                //durchlaeuft alle betroffenen Zellen und setzt sie auf 0
                for (int i = 0; i < count; i++) {
                    this.setCellValue(current, 0);
                    current = Counters.counternextLow(current, lower, upperBound);
                }
                return;
            }
            if (strat == 0) {
                //Wieviel muss zu jeder Zelle hinzugefuegt werden
                dif = dif / count;
                int[] current = lower.clone();
                //durchlaeuft alle betroffenen Zellen und erhoeht dabei jede Zelle mithilfe der Hilfsmethode um dif.
                for (int i = 0; i < count; i++) {
                    CellUpdateDif(current, dif);
                    current = Counters.counternextLow(current, lower, upperBound);
                }
            } else {
                //alter Wert ist null. Keine Erhoehung um einen Faktor moeglich
                if (oldValue == 0) {
                    rangeUpdate(lower, upper, value, 0);
                    return;
                }
                //Um welchen Faktor wird erhoeht?
                double fac = value / oldValue;
                int[] current = lower.clone();
                //alles muss auf null gesetzt werden.
                if (fac == 0) {
                    for (int i = 0; i < count; i++) {                        
                        this.setCellValue(current, 0);
                        current = Counters.counternextLow(current, lower, upperBound);
                    }
                    return;
                }
                //durchlaeuft alle betroffenen Zellen und erhoeht dabei jede Zelle mithilfe der Hilfsmethode um fac
                for (int i = 0; i < count; i++) {
                    CellUpdateFac(current, fac);
                    current = Counters.counternextLow(current, lower, upperBound);
                }
            }

        }

    }

    /**
     * Hilfsmethode zum Aendern einer einzelnen Zelle um einen Summanden.
     * @param current Koordinaten der Zelle, die aktualisiert werden soll.
     * @param dif Wert, um den erhoeht wird.
     */
    private void CellUpdateDif(int[] current, double dif) {
        // Ermitteln der betroffenen Zellen			
        ArrayList<Integer> Index = new ArrayList<Integer>();
        int[] positions = new int[this.dimensionality];
        int[] shape = new int[this.dimensionality];
        int totalNumber = 1;
        //Fuegt alle betroffenen Indizes zu einer ArrayList zusammen
        for (int i = 0; i < this.dimensionality; i++) {
            ArrayList<Integer> tmp = this.dimension[i].getStrategie().getCellsForCellUpdate(current[i]);
            shape[i] = tmp.size();
            totalNumber *= shape[i];
            if (i > 0) {
                positions[i] = positions[i - 1] + shape[i - 1];
            }
            Index.addAll(tmp);
        }
        //Update der RealSize
        double oldValue=this.getCellValue(current);
        double value=oldValue+dif;
        // Problem mit double Genauigkeit!
        if (oldValue != 0 && Math.abs(oldValue) < 0.0001) {
            oldValue = 0;
        }        
        if (value != 0 && Math.abs(value) < 0.0001) {
            value = 0;
        }
        if (value == 0) {
            if (oldValue != 0) // Wert wird auf 0 gesetzt
            {
                realSize--;
            }
        } else if (oldValue == 0) {
            realSize++;
        }
        //ruft Hilfsmethode auf, die alle betroffen Zellen aendert
        CellsUpdate(Index, totalNumber, shape, positions, dif);

    }

    /**
     * Hilfmethode zum Aendern einer einzelnen Zelle um einen Faktor
     * @param current Koordinaten der Zelle, die aktualisiert werden soll.
     * @param fac Faktor, um den erhoeht wird.
     */
    private void CellUpdateFac(int[] current, double fac) {
        double oldValue = this.getCellValue(current);
        // alter Wert war 0
        if (Math.abs(oldValue)<0.0001)
            return;
        //Differenz zwischen neuem Wert fac*oldValue und altem Wert wird berechnet
        double dif = oldValue * (fac - 1);
        CellUpdateDif(current, dif);
    }

    /**
     * Aendert durch ein Zell-Update betroffene Zellen 
     * @param Index Liste aller betroffenen Indizes
     * @param totalNumber Anzahl der betroffenen Indizes
     * @param shape Anzahl der Indizes in jeder Dimension
     * @param positions Erster Index jeder Dimension in der Liste
     * @param dif Wert, der addiert werden soll.
     */
    private void CellsUpdate(ArrayList<Integer> Index, int totalNumber, int[] shape, int[] positions, double dif) {
        //Hilfsarray, das dafuer sorgt, dass alle moeglichen Indexkombinationen durchlaufen werden.
        int[] current = new int[this.dimensionality];
        //enthaelt die Positionen in der Liste, wo die Koordinaten drinstehen
        int[] ListPos;
        //Durchlaeuft alle betroffenen Zellen und updated diese
        for (int i = 0; i < totalNumber; i++) {
            ListPos = this.getListPositions(current, positions);
            //enthaelt die Koordinaten, die zur Summe dazugerechnet werden muessen.
            int[] coordinates = new int[this.dimensionality];
            for (int k = 0; k < this.dimensionality; k++) {
                //liest Koordinaten aus wandelt diese um
                coordinates[k] = Index.get(ListPos[k]);
            }
            //hier findet die tatsaechliche Aenderung statt.         
            this.werte[this.getPosition(coordinates)] += dif;          
            //geht zur naechsten Koordinate
            current = Counters.counternext(current, shape, 0);
        }
    }

    /**
     * Checkt ob eine Range korrekt ist
     * @param lower Koordinaten der Untergrenze
     * @param upper Koordinaten der Obergrenze
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

    /**
     * Die Methode laesst fuer jede Dimension die Vorberechnungsstrategie anwenden.
     * Es wird das Visitor Pattern verwendet.
     */
    public void precompute() {
        if (this.precomputed) {
            return;
        }
        for (int i = 0; i < this.dimensionality; i++) {
            IStrategie tmp = dimension[i].getStrategie();
            //Standardstrategie setzen
            if (tmp == null) {
                dimension[i].setStrategie(new LPS(dimension[i].size, (int) Math.sqrt(dimension[i].size)));
            }
            dimension[i].getStrategie().precompute(this, i);

        }
        this.precomputed = true;
    }

    /**
     * Hilfsmethode, die umrechnet eingegebene Koordinaten auf die Position im
     * eindimensionalen Wertearray.
     * 
     * @param coordinates mehrdimensionale Koordinate
     * @return Position im Werte Array
     */
    public int getPosition(int[] coordinates) {
        int pos = 0;
        for (int i = 0; i < coordinates.length; i++) {
            pos += coordinates[i] * this.positions[i];
        }
        return pos;
    }

    /**
     * Diese Methode prueft ob die Koordinaten korrekt eingegeben sind.
     * 
     * @param coordinates Koordinate
     * @return "true" wenn ales in Ordnung ist
     */
    public boolean coordinatesValidation(int[] coordinates) {
        boolean valid = true;
        if (this.dimensionality == 0) {
            valid = false;
            System.err.println("DataCube ist nicht initialisiert!");
        } else {
            if (coordinates.length != this.dimensionality) {
                System.err.println("Koordinaten Eingabe ist ungueltig!");
                valid = false;// ungueltige Eingabe
            } else {
                int[] upper = getShape();
                for (int i = 0; i < coordinates.length; i++) {
                    if (coordinates[i] < 0 || coordinates[i] >= upper[i]) {
                        System.err.println("Koordinaten sind ausserhalb des Dimensions-Bereichs!");
                        valid = false;
                    }
                }
            }
        }
        return valid;
    }
}
