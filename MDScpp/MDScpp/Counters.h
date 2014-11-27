#pragma once
#include <vector>

using namespace std;

/**
* Diese Klasse liefert alle benoetigten Zaehler, die fuer die Strategien benoetigt werden.
* @author Zurab Khadikov
*
*/
class Counters
	{
	public:
		Counters();
		~Counters();
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
		static void counternext(vector<int> &current, const vector<int> &shape, int actDim);

		/**
		* Genau wie counternext, nur dass der Zaehler beim erreichen der Obergrenze
		* statt auf 0 auf lower gesetzt wird.
		* @param current aktueller Wert des Zaehlers
		* @param lower untere Grenze des Zaehlers
		* @param shape obere Grenze des Zaehlers
		* @return erhoehter Zaehler
		*/
		static void counternextLow(vector<int> &current, const vector<int> &lower, const vector<int> &shape);
	};
