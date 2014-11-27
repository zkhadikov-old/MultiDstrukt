#include "VariableLPS.h"

VariableLPS::~VariableLPS() {}


vector<int> VariableLPS::getCellsForCellUpdate(int coordinate) {
	vector<int> index(0);
	unsigned int blockNr=getBlockNr(coordinate);
	int end=0;
	if (blockNr == blockBeginnings.size()-1){
		end=dimLength;
		} 
	else {
		end=blockBeginnings[blockNr +1];
		}
	for(int i = coordinate; i<end; i++){
		index.push_back(i);
		}
	return index;
	}

vector<int> VariableLPS::getCellsForRangeSum(int low, int high) {
	vector<int> index(0);
	int blockOfLow = getBlockNr(low);
	int blockOfHigh = getBlockNr(high);
	int k = low - blockBeginnings[blockOfLow];
	if ( k > 0) {
		index.push_back(0-low); // -(low-1+1), wegen Index-Verschiebung
		}
	// Fügt alle Blockenden, die zwischen low-1 und high liegen. Indexverschiebung ist einberechnet.
	for (int j = blockOfLow; j < blockOfHigh; j++) {
		index.push_back( blockBeginnings[j] + blockSizes[j] );
		}
	index.push_back(high -1);
	return index;
	}

void VariableLPS::precompute(IDC *idc, int dimNr) {
	vector<double> &werte = idc->werte;
	vector<int> shape = idc->getShape();
	vector<int> current(shape.size());
	vector<int> predecessor(0);
	unsigned int blockNr=0;
	for (unsigned int i=0; i<werte.size(); i++) {
		predecessor.assign(current.begin(), current.end());
		predecessor[dimNr]--;
		// Blockanfang
		if( current[dimNr] == blockBeginnings[blockNr] ) {
			if(blockNr < (blockBeginnings.size()-1) ) {
				blockNr++;
				} else {
					blockNr=0;	
				}
			} // kein Blockanfang
		else {
			werte[ idc->getPosition(current) ] += werte[ idc->getPosition(predecessor) ]; 
			}
		// nächste Zeile 
		Counters::counternext(current, shape, dimNr);
		}
	}
