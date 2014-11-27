#pragma once
#include "IStrategie.h"
#include <iostream>
#include <vector>

using namespace std;

/*!
* Diese Klasse implementiert die eindimensionale Strategie "Local prefix sum"
* mit gleicher Blockgroesse fuer alle Bloecke.

* @author  Zurab Khadikov
*/

class LPS : public IStrategie 
	{
	private:
		int dimLength;
		int blockSize;
	public:
		/*!
		* Konstruktor
		*
		* @param dL Laenge der Dimension
		* @param bS Groesse eines Blocks
		*/
		LPS(int dL, int bS): dimLength(dL), blockSize(bS) {} ;

		/*!
		 *	Destruktor
		 */
		~LPS();

		/*!
		* Gibt die Groesse der Dimension an
		* 
		* @return Groesse
		*/
		int getDimLength(); 

		/*!
		* Gibt den Namen der Strategie an.
		* 
		* @return Name
		*/
		string getName(); 

		/*!
		* Liefert die ID der Strategie aus.
		* 
		* @return ID der Strategie
		*/
		int getID(); 

		/*!
		* Liefert die zum Aktualisieren einer einzelnen Zelle benoetigten Zellenindizes
		*
		* @param coordinate Koordinate der Zelle
		* @return Liste von benoetigten Zellenindizes
		*/	
		vector<int> getCellsForCellUpdate(int coordinate);

		/*!
		* Liefert die zum ausrechnen der RangeSum benoetigten Zellenindizes
		* Hat ein Index ein negatives Vorzeichen, so muss es negativ einberechnet werden.
		* Die Indizes werden um 1 verschoben, damit bei Null das Vorzeichen unterschieden werden kann.
		*
		* @param low untere Grenze
		* @param high obere Grenze
		* @return Liste von Integer. Das Vorzeichen gibt an, ob addiert oder subtrahiert werden soll. Der Betrag minus 1 gibt den Index an.
		*/
		vector<int> getCellsForRangeSum(int low, int high);

		/*!
		* Wendet die Strategie auf ein Array an.
		* 
		* @param idc 
		* @param dimNr
		*/
		void precompute(IDC *idc, int dimNr);

	};

inline int LPS::getDimLength() {return dimLength;}

inline string LPS::getName() {return string("Local Prefix Sum");}

inline int LPS::getID() {return 110; }