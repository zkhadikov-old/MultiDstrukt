package helpers;


/**
 * Diese Klasse liefert alle benoetigten Zaehler, die fuer die Strategien benoetigt werden.
 * @author Philippe Hagedorn, Zurab Khadikov
 *
 */
public class Counters {
	/**
         * Erhoeht einen Zaehler um eins. Dieser Zaehler hat fuer jede Stelle eine Obergrenze,
         * versucht erst bei der aktuellen Dimension zu erhoehen, falls dies nicht geht, so setzt er diesen
         * Wert auf 0, geht die Dimensionen zyklisch nach rechts durch und versucht dort dasselbe bis eine Dimension
         * erhoeht werde konnte ohne die Grenze zu ueberschreiten.
         * @param current aktuelle Werte des Zaehlers
         * @param shape obere Grenzen fuer den Zaehler
         * @param actDim Stelle, bei der zuerst erhoeht wird.
         * @return erhoehter Zaehler
         */
	
	public static int[] counternext(int [] current,int[] shape,int actDim) {
		boolean ready = false;
		int j = actDim;
		int cycleCounter = 0;
		int dim=shape.length;
		while (!ready) {
                        // Obergrenze fuer die Dimension erreicht? Wenn nein, fertig.
			if (current[j] < shape[j] - 1) {
				current[j]++;
				ready = true;
				break;
			}
                        // setze Wert der Dimension auf 0 und gehe zur naechsten
			if (j < actDim) {
				for (int i = actDim; i < current.length; i++)
					current[i] = 0;
				for (int i = 0; i <= j; i++)
					current[i] = 0;
			} else {
				for (int i = actDim; i <= j; i++)
					current[i] = 0;
			}
			if (j < current.length - 1)
				j++;
			else
				j = 0;
                        // Verhinderung einer Endlosschleife bei Spezialfaellen
			cycleCounter++;
			if (cycleCounter == dim)
				break;
		}
		return current;
	}
        /**
         * Genau wie counternext, nur dass der Zaehler beim erreichen der Obergrenze
         * statt auf 0 auf lower gesetzt wird.
         * @param current aktueller Wert des Zaehlers
         * @param lower untere Grenze des Zaehlers
         * @param shape obere Grenze des Zaehlers
         * @return erhoehter Zaehler
         */
	public static int[] counternextLow(int[] current, int[] lower, int[] shape) {
		boolean ready = false;
		int j = 0;
		int dim=shape.length;
		while (!ready) {
			if (current[j] < shape[j] - 1) {
				current[j]++;
				ready = true;
				break;
			}
			for (int i = 0; i <= j; i++)
					current[i] = lower[i];
			j++;
			if (j == dim)
				break;
		}
		return current;
		
	}

}
