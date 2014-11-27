#include "IDC.h"
#include "IDCDimension.h"
#include "IStrategie.h"
#include "LPS.h"
#include "SRPS.h"
#include "VariableLPS.h"
#include "VariableSRPS.h"

IDC::IDC(const vector<int> &minCell, const vector<int> &maxCell) : 
		initPoint(minCell), dimensionality(minCell.size()), shape(dimensionality,0),
		dimensions(dimensionality), positions(dimensionality,0), werte(0), realSize(0),
		precomputed(false), totalSum(0), min_array(new double[dimensionality]), max_array(new double[dimensionality])
		 {
	if (rangeValidator(minCell, maxCell))
		{
		// Shape wird gefuellt
		for (int i=0; i<dimensionality;i++)
			{
			shape[i]=maxCell[i]-minCell[i]+1;
			}
		// Dimensions wird gefuellt
		for (int i = 0; i < dimensionality; i++) {
			dimensions[i]=IDCDimension(shape[i]);
			}
		int tmp = 1;
		// Positions wird gefuelt
		positions[dimensionality - 1] = 1;
		for (int i = 2; i <= dimensionality; i++) {
			tmp *= shape[dimensionality - i + 1];
			positions[dimensionality - i] = tmp;
			}
		// Werte Array wird auf sine Kapazität vergroßert
		werte.resize(tmp*(shape[0]),0);

		for(int i=0; i<dimensionality; i++) {
			min_array[i]=minCell[i];
			max_array[i]=maxCell[i];
			}

		//cout<<"IDC Konstruktor wurde aufgerufen.\n";
		} 
	else
		{
		cerr<<"IDC koennte nicht gebaut werden!\n";
		}	
	}

IDC::~IDC() 
	{
		//cout<<"IDC Destruktor wurde aufgerufen!\n";
	}

void IDC::setCellValue( vector<int> &coordinates, double value) {

	for(int i=0; i< coordinates.size();i++)
		{
		coordinates[i] -= initPoint[i];
		}

		if (coordinatesValidation(coordinates)) {
		int pos = getPosition(coordinates);
		if (!precomputed) {
			if (value == 0) {
				if (werte[pos] != 0) {
					realSize--; // Wert wird auf 0 gesetzt
					totalSum=totalSum-value;
					}
				} 
			else if (werte[pos] == 0) {
				realSize++;
				totalSum=totalSum+value;
				}
			werte[pos] = value;
			} else {
				rangeUpdate(coordinates, coordinates, value, 0);
			}
		}
	}


void IDC::precompute(){
	 if (precomputed) {
		    //cout<<"It is precomuted!";
            return;
        }
	 else{
        for (int i = 0; i < dimensionality; i++) {
			IStrategie *tmp = dimensions[i].getStrategie();
            //Standardstrategie setzen
            if (tmp == 0) {
				int isize(dimensions[i].getSize());
				double dsize(isize);
				//tmp = new LPS( isize, static_cast<int>(sqrt(dsize)/1));
				tmp = new LPS( isize, dsize);
				dimensions[i].setStrategie(tmp);
            }
			tmp->precompute(this,i);

        }
		precomputed = true;
		//cout<<"Precompute is executed!\n";
	    }
	}


// TODO: Komentare durcharbeiten

double IDC::getRangeSum(const vector<int> &lower, const vector<int> &upper) {
	if(!rangeValidator(lower,upper) && !precomputed){
		return -1;
		}
	else {
		//Speichert das Zwischenergebnis
		double sum=0;
		//Enthaelt die Indizes fuer alle Dimensionen
		vector<int> indizies(0);
		//vector<int> pos enthaelt die erste Position, die einen Index fuer die Dimension i enthaelt
		vector<int> pos(dimensionality);
		//Hilfsvariable zur Berechnung des korrekten pos-Arrays
		int position = 0;
		//Speichert die Anzahl an Werten, die man zur Summe hinzufuegen muss
		int totalNumber = 1;
		//Fuegt die Indizes aller Dimensionen zu einer Liste zusammen
		for (int i = 0; i < dimensionality; i++ ) {
			vector<int> tmp( dimensions[i].getStrategie()->getCellsForRangeSum(lower[i], upper[i]));
			indizies.insert(indizies.end(),tmp.begin(), tmp.end());
			pos[i] = tmp.size();
			totalNumber *= pos[i];
			position += pos[i];
			}
		//Enthaelt fuer jede Dimension Anzahl der Indizes
		vector<int> shape(pos.begin(),pos.end());
		//Berechnet korrektes Indizes Array
		for (int i=dimensionality-1; i>=0; i--) {
			pos[i]=position - pos[i];
			position = pos[i];
			}
		//Hilfsarray, das dafuer sorgt, dass alle moeglichen Indexkombinationen durchlaufen werden.
		vector<int> current(dimensionality);
		//enthaelt die Positionen in der Liste, wo die Koordinaten drinstehen
		vector<int> ListPos(0);

		for(int i=0; i < totalNumber; i++) {
			ListPos = getListPositions(current,pos);
			//enthaelt die Koordinaten, die zur Summe dazugerechnet werden muessen
			vector<int> coordinates(dimensionality);
			//Vorzeichen des Summanden
			int vz = 1;
			for(int k=0; k<dimensionality; k++) {
				//liest Koordinaten aus wandelt diese um
				coordinates[k]= abs( indizies[ ListPos[k] ] ) - 1;
				if( indizies[ ListPos[k] ] < 0) {
					vz *= -1;
					}
				}
			sum += vz * werte[getPosition(coordinates)];
			Counters::counternext(current,shape,0);
			}
		return sum;
		}
	}
	
double IDC::getCellValue(const vector<int> &coordinates) {
	return getRangeSum(coordinates, coordinates);
	}

void IDC::ConcernedCellsUpdateRoutine(const vector<int> &index, int totalNumber, const vector<int> &shape, const vector<int> &posIndex, double dif) {
	//Hilfsarray, das dafuer sorgt, dass alle moeglichen Indexkombinationen durchlaufen werden.
	vector<int> current(dimensionality,0);
	//enthaelt die Positionen in der Liste, wo die Koordinaten drinstehen
	vector<int> listPos(0);
	//enthaelt die Koordinaten, die zur Summe dazugerechnet werden muessen.
	vector<int> coordinates(dimensionality,0);
	//Durchlaeuft alle betroffenen Zellen und updated diese
	for (int i=0; i<totalNumber; i++){
		listPos=getListPositions(current, posIndex);
		for (int k=0; k<dimensionality; k++){
			//liest Koordinaten aus, wandelt diese um
			coordinates[k]=index[ listPos[k] ];
		}
		//hier findet die tatsaechliche Aenderung statt. 
		werte[getPosition(coordinates)] += dif;
		//geht zur naechsten Koordinate
		Counters::counternext(current,shape,0);
		}
	}

// TODO: Durch arbeiten, es ist nicht sauber implementiert!
void IDC::OneCellUpdateDif(const vector<int> &current, double dif, double oldValue) {
	// Ermitteln der betroffenen Zellen
	vector<int> index(0);
	vector<int> posIndex(dimensionality, 0);
	vector<int> shapeLocal(dimensionality, 0);
	int totalNumber = 1;
	vector<int> tmp(0);
	//Fuegt alle betroffenen Indizes zu einer ArrayList zusammen
	for (int i=0; i<dimensionality; i++) {
		tmp= (dimensions[i].getStrategie())->getCellsForCellUpdate(current[i]);
		shapeLocal[i] = tmp.size();
		totalNumber *= shapeLocal[i];
		if (i > 0){
			posIndex[i] = posIndex[i-1] + shapeLocal[i-1];
			}
		index.insert(index.end(),tmp.begin(),tmp.end());
		}
	//Update der RealSize
	double newValue=oldValue + dif;
	// TODO: Problem mit double Genauigkeit beachten!
	if (oldValue != 0 && abs(oldValue) < 0.00000001) {
		oldValue = 0;
		}        
	if (newValue != 0 && abs(newValue) < 0.00000001) {
		newValue = 0;
		}

	if (newValue == 0) {
		if (oldValue != 0) // Wert wird auf 0 gesetzt
			{
			realSize--;
			}
		} else if ( oldValue == 0 ){
			realSize++;
		}
		//ruft Hilfsmethode auf, die alle betroffen Zellen aendert
	ConcernedCellsUpdateRoutine(index, totalNumber, shapeLocal, posIndex, dif);
	}

void IDC::OneCellUpdateFac(const vector<int> &current, double fac) {
	double oldValue=getCellValue(current);
	// alter Wert war 0
	if (abs(oldValue)<0.0001)
		return;
	//Differenz zwischen neuem Wert fac*oldValue und altem Wert wird berechnet
	double dif = abs(oldValue * (fac-1));
	OneCellUpdateDif(current, dif, oldValue);
	totalSum=totalSum+dif;
	}


// Komplet bearbeiten und verbessern. 
void IDC::rangeUpdate(const vector<int> &lower, const vector<int> &upper, double value, int strat) {
	if( !rangeValidator(lower,upper)) {
		return;
		}
	vector<int> upperBound(upper);
	for (unsigned int i=0; i<upperBound.size(); i++){
		upperBound[i]++;
		}
	// einfacher Fall, nur eine Zelle update

	if (lower == upper) {
		double oldValue = getCellValue(lower);
		if (oldValue == value){
			return;
			}
		double dif = value - oldValue;
		// Hilfsmethode wird aufgerufen
		OneCellUpdateDif(lower, dif, oldValue);
		totalSum=totalSum-oldValue+value;
		} else {
			double oldValue = getRangeSum(lower, upper);
			double dif = value - oldValue;
			if ( dif == 0) {
				return;
				}
			//Anzahl der betroffenen Zellen ermitteln
			int count = 1;
			for ( int i = 0; i < dimensionality; i++){
				count *= upper[i] - lower[i] + 1;
			}
			// neuer Wert ist 0
			if (value == 0 ) {
				vector<int> current(lower);
				//durchlaeuft alle betroffenen Zellen und setzt sie auf 0
				double currentValue = 0;
				for (int i = 0; i<count; i++) {
					currentValue = getCellValue(current);
					OneCellUpdateDif(current,(0-currentValue), currentValue);
					Counters::counternextLow(current, lower, upperBound);
					}
				totalSum=totalSum-oldValue;
				return;
			}
			if ( strat == 0){
				//Wieviel muss zu jeder Zelle hinzugefuegt werden
				dif = dif/count;
				vector<int> current(lower);
				//durchlaeuft alle betroffenen Zellen und erhoeht dabei jede Zelle mithilfe der Hilfsmethode um dif.
				double currentValue=0; 
				for ( int i = 0; i < count; i++){
					currentValue = getCellValue(current);
					OneCellUpdateDif(current,dif,currentValue);
					Counters::counternextLow(current, lower, upperBound);
				}
				totalSum=totalSum-oldValue+value;
			} 
			else{
				//alter Wert ist null. Keine Erhoehung um einen Faktor moeglich
				if(oldValue == 0) {
					rangeUpdate(lower, upper, value, 0);
					return;
				}
				// Um welchen Faktor wird erhoeht?
				double fac = value/oldValue;
				vector<int> current(lower);
				// alles muss auf null gesetzt werden
				if (fac==0){
					double currentValue = 0;
					for (int i=0; i<count; i++)	{
						currentValue = getCellValue(current);
						OneCellUpdateDif(current,(0-currentValue), currentValue);
						Counters::counternextLow(current, lower, upperBound);
					}
					totalSum=totalSum-oldValue+value;
					return;
				}
				//durchlaeuft alle betroffenen Zellen und erhoeht dabei jede Zelle mithilfe der Hilfsmethode um fac
				for(int i=0; i<count; i++){
					OneCellUpdateFac(current, fac);
					Counters::counternextLow(current, lower, upperBound);
					}
			}
		}
	}




	