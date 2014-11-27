#pragma once
#include "IStrategie.h"
#include <iostream>
#include <vector>

using namespace std;

/*!\class SRPS
* Diese Klasse implementiert die eindimensionale Strategie "Space efficient relative prefix sum"
* mit gleicher Blockgroesse fuer alle Bloecke. Fuer Blockgroesse 1 hat man den Spezialfall "Prefix Sum"
* @author  Zurab Khadikov
*/
class SRPS : public IStrategie
	{
	private:
		int dimLength;
		int blockSize;
		int rest;
	public:
		/*!
		* Konstruktor
		*
		* @param dimLength Laenge der Dimension
		* @param blockSize Groesse eines Blocks
		*/
		SRPS(int dL, int bS) : dimLength(dL), blockSize(bS), rest(dL%bS){};

		/*!
		*	Destruktor
		*/
		~SRPS();

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

inline int SRPS::getDimLength() {return dimLength;}

inline string SRPS::getName() {return string("Space efficient relative prefix sum");}

inline int SRPS::getID() {return 100; }