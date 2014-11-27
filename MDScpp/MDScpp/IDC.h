#pragma once
#include <iostream>
#include <string>
#include <vector>
#include <cmath>
#include <boost/scoped_array.hpp>

using namespace std;

class IDCDimension;
class IStrategie;


/*! \class IDC 
* Datenstruktur zum Speichern von Cubes.
*
* @author Zurab Khadikov
* @version 2.0
*/

class IDC {
private:
	/**
	 * Vector mit Koordinaten fuer InitPoint-Zelle, die wahren Koordinaten entspricht                 
	 */
	const vector<int> initPoint;

	int dimensionality;
	/**
	 * Shape Vector mit Dimensions Größen                 
	 */
	vector<int> shape;

	vector<IDCDimension> dimensions;
	/**
	* Hilfsarray zur Bestimmung der Position im
	* eindimensionalen Array "werte"
	*/
	vector<int> positions;
	/**
	* Eindimensionales Hilfsarray feur Werte
	*/
	vector<double> werte;
	int realSize;
	
	/**
	* Gibt an, ob Vorberechnung schon stattgefunden hat.
	*/
	bool precomputed;

	/**
	 * Speichert komplette Summe von IDC                 
	 */
	double totalSum;

	/**
	 * Special Index                 
	 */
	boost::scoped_array<double> min_array;
	boost::scoped_array<double> max_array;

	/**
	* Hilfsmethode, die umrechnet eingegebene Koordinaten auf die Position im
	* eindimensionalen Wertearray.
	*/
	int getPosition(const vector<int> &coordinates);

	/**
	* Hilfsmethode, um Positionen von Indizes im Hilfsarray zu ermitteln.
	* @param coordinates Koordinaten bzgl. Hilfsarray
	* @param pos Positionsarray (wo faengt welche Dimension an)
	* @return Koordinate bzgl. Cube
	*/
	vector<int> getListPositions(const vector<int> &coordinates, const vector<int> &pos);
	
	/**
	* Aendert durch ein Zell-Update betroffene Zellen 
	* @param index Liste aller betroffenen Indizes
	* @param totalNumber Anzahl der betroffenen Indizes
	* @param shapeLocal Anzahl der Indizes in jeder Dimension
	* @param posIndex Erster Index jeder Dimension in der Liste
	* @param dif Wert, der addiert werden soll.
	*/
	void ConcernedCellsUpdateRoutine(const vector<int> &index, int totalNumber, const vector<int> &shapeLocal, const vector<int> &posIndex, double dif);

	/**
	* Hilfsmethode zum Aendern einer einzelnen Zelle um einen Summanden.
	* @param current Koordinaten der Zelle, die aktualisiert werden soll.
	* @param dif Wert, um den erhoeht wird.
	*/
	void OneCellUpdateDif(const vector<int> &current, double dif, double oldValue);

	/**
	* Hilfmethode zum Aendern einer einzelnen Zelle um einen Faktor
	* @param current Koordinaten der Zelle, die aktualisiert werden soll.
	* @param fac Faktor, um den erhoeht wird.
	*/	
	void OneCellUpdateFac(const vector<int> &current, double fac);

public:
	/** 
	 * Diese Klassen duerfen auf Array "werte" zugreifen und die verändern                  
	 */
	friend class SRPS;
	friend class LPS;
	friend class VariableLPS;
	friend class VariableSRPS;

	/**
	* Konstruktor
	*/
	IDC(const vector<int> &minCell, const vector<int> &maxCell) ;
	/**
	* Destruktor
	*/
	~IDC();

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
	void setCellValue(vector<int> &coordinates, double value);

	/**
	* Returns the value stored in the given cell.
	* 
	* @param coordinates
	*            the coordinates of the cell (given by the dimension
	*            parameters)
	* @return the value contained in the given cell
	*/
	double getCellValue(const vector<int> &coordinates);

	/**
	* Returns the sum of the values of all cells within the given range.
	* 
	* @param lower
	*            array with the lower bound of the range for each dimension
	* @param upper
	*            array with the upper bound of the range for each dimension
	* @return the range sum
	*/
	double getRangeSum(const vector<int> &lower, const vector<int> &upper);

	double getQueryRegion(double *lo_q, double *hi_q, int a_size);


	/**
	* Returns an array whose length is the number of dimensions of theis
	* OlapCube and whose elements represent the size of each dimension.
	* 
	* @return the dimension description
	*/
	const vector<int> getShape();

	/**
	* Returns the size of this OlapCube, i.e. the actual number of cells filled
	* with non-zero elements. The size should not be confused with the CAPACITY
	* of the OlapCube, which is the number of possible cells.
	* 
	* @return the number of filled cells
	*/
	int getSize();
	int getSizeAlt();

	/**
	* Updates the whole selected range or one value if lower==upper -> true
	* Bei strat=0 wird die Aenderung auf alle Zellen gleichmaessig verteilt, bei strat!=0 werden alle Zellen um denselben Faktor erhoeht.
	* 
	* @param lower array with the lower bound of the range for each dimension
	* @param upper array with the upper bound of the range for each dimension
	* @param value the new value of the range
	* @param strat gibt an, wie die Aenderung der Range auf einzelne Zellen verteilt werden soll.
	*/
	void rangeUpdate(const vector<int> &lower, const vector<int> &upper, double value, int strat);

	/**
	* Checkt ob eine Range korrekt ist
	* @param lower Koordinaten der Untergrenze
	* @param upper Koordinaten der Obergrenze
	*/
	bool rangeValidator(const vector<int> &lower, const vector<int> &upper);

	/**
	* Die Methode laesst fuer jede Dimension die Vorberechnungsstrategie anwenden.
	* Es wird das Visitor Pattern verwendet.
	*/
	void precompute();


	/**
	* Diese Methode prueft ob die Koordinaten korrekt eingegeben sind.
	*
	* @param coordinates Koordinate
	* @return "true" wenn ales in Ordnung ist
	*/
	bool coordinatesValidation(const vector<int> &coordinates);

	/**
	 * Gibt TotalSum von ganzem IDC zurueck                 
	 */
	const double getTotalSum();
	};


inline const double IDC::getTotalSum(){
	return double(totalSum);
	}
inline int IDC::getPosition(const vector<int> &coordinates) {
	int pos = 0;
	for (unsigned int i = 0; i < coordinates.size(); i++) {
		pos += coordinates[i] * positions[i];
		}
	return pos;
	}

inline bool IDC::rangeValidator(const vector<int> &lower, const vector<int> &upper) {
	bool valid = true;
	if (lower.size()==upper.size())
	{
	for (unsigned int i=0; i < lower.size(); i++) {
		if (lower[i]-upper[i] > 0){
			cerr << "Rangekoordinaten sind falsh geweahlt!" <<endl;
			valid=false;
			break;
			}
		}
	}
	else {
		cerr << "Rangekoordinaten sind falsh geweahlt!" <<endl;
		valid=false;
		}
	return valid;
	}

// TODO: Eventuel verbessern **********************************************!!!!!!!!!!!!!!!!!!!!!!!!
inline vector<int> IDC::getListPositions(const vector<int> &coordinates, const vector<int> &pos) {
	vector<int> result(coordinates.size());
	for (unsigned int i=0; i<coordinates.size();i++){
		result[i]=coordinates[i]+pos[i];
		}
	return result;
	}

// TODO: Bearbeiten ********************************************************!!!!!!!!!!!!!!!!!!!!!1
inline bool IDC::coordinatesValidation(const vector<int> &coordinates) {
	bool valid = true;
	if ( dimensionality == 0) {
		valid = false;
		cerr<<"DataCube ist nicht initialisiert!"<<endl;
		}
	else{ 
		if(coordinates.size() != dimensionality ){
			cout<<"Koordinaten Eingabe ist ungueltig!"<<endl;
			valid = false;// ungueltige Eingabe
			} else {
				vector<int> upper(shape);
				for (unsigned int i = 0; i < coordinates.size(); i++) {
					if ( coordinates[i] < 0 || coordinates[i] >= upper[i]) {
						cout<<"Koordinaten sind ausserhalb des Dimensions-Bereichs!"<<endl;
						valid = false;
						}
					}		
			}
		}
	return valid;
	}

inline const vector<int> IDC::getShape(){
	return vector<int> (shape);
	}

inline int IDC::getSize() {
	return realSize;
	}

inline int IDC::getSizeAlt() {
	int count=0;
	double nul=0.0;
	for( unsigned int i=0; i<werte.size(); i++) {
		if(werte[i]!=nul) count++;
		}
	return count;
	}
