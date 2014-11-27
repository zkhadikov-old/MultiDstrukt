#pragma once
#include <iostream>
#include <vector>
#include "Counters.h"
#include "IDC.h"
using namespace std;



/**
* Interface fuer verschiedene eindimensionale Techniken zum Speichern eines
* Arrays.
* 
* @author Zurab Khadikov
*/
class IStrategie
	{
	public:
		/**
		* Default Konstruktor und Destruktor
		*/
		IStrategie() {}
		virtual ~IStrategie() {}
		/**
		* Liefert die zum ausrechnen der RangeSum benoetigten Zellenindizes
		* Hat ein Index ein negatives Vorzeichen, so muss es negativ einberechnet werden.
		* Die Indizes werden um 1 verschoben, damit bei Null das Vorzeichen unterschieden werden kann.
		* @param low untere Grenze
		* @param high obere Grenze
		* @return Liste von Integer. Das Vorzeichen gibt an, ob addiert oder subtrahiert werden soll. Der Betrag minus 1 gibt den Index an.
		*/
		virtual vector<int> getCellsForRangeSum(int low, int high)=0;

		/**
		* Liefert die zum Aktualisieren einer einzelnen Zelle benoetigten Zellenindizes
		* @param coordinate Koordinate der Zelle
		* @return Liste von benoetigten Zellenindizes
		*/
		virtual vector<int> getCellsForCellUpdate(int coordinate)=0;

		/**
		* Wendet die Strategie auf ein Array an.
		* 
		* @param idc 
		* @param dimNr
		*/
		virtual void precompute(IDC *idc, int dimNr)=0;

		/**
		* Liefert die ID der Strategie aus.
		* 
		* @return ID der Strategie
		*/

		virtual int getID()=0;

		/**
		* Gibt den Namen der Strategie an.
		* 
		* @return Name
		*/
		virtual string getName()=0;

		/**
		* Gibt die Groesse der Dimension an
		* 
		* @return Groesse
		*/
		virtual int getDimLength()=0;

	};
