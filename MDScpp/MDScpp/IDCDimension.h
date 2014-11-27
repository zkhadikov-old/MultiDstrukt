#pragma once
#include <string>
using namespace std;

class IStrategie;

/**
* Die Klasse implementiert, die fuer ein IDC benoetigten Dimensionen, 
* mit allen noetigen Informationen.
*
* @author Zurab Khadikov
*/
class IDCDimension
	{
	private:
		int size;	
		IStrategie * strategie;
		//DimensionHierarchie start;
	public:
		/**
		* Konstruktor 
		* @param size Laenge der Dimension
		*/
		IDCDimension(int s) : size(s), strategie(0) {};
		/**
		* Default Konstruktor 
		*/
		IDCDimension() : size(0), strategie(0) {};
		/**
		* Destruktor
		*/
		~IDCDimension();

		/**
		 * Gibt die Dimension Groeße zurück                 
		 */
		int getSize();

		/**
		* Setzt die Hierarchie fuer die Dimension
		* @param hierarchie
		*/
		//void setHierarchie(DimensionHierarchie hierarchie);

		/**
		* Get-Methode 
		* @return Hierarchie fuer entsprechende Dimension  
		*/
		//DimensionHierarchie getHierarchie();

		/**
		* Get-Methode
		* @return Strategie fuer entsprechende Dimension
		*/
		IStrategie* getStrategie();

		/**
		* Setzt die Strategie fuer die Dimension
		* @param strategie
		*/
		void setStrategie(IStrategie* strategie);
	};

inline int IDCDimension::getSize()
	{
	return size;
	}