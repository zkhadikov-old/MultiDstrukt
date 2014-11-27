#include "VariableSRPS.h"

VariableSRPS::~VariableSRPS(void) {}

vector<int> VariableSRPS::getCellsForCellUpdate(int coordinate) {
	vector<int> index(0);
	unsigned int blockNr=getBlockNr(coordinate);
	// fügt alle Blockanfaenge hinzu, die nach der Zelle kommen
	for (unsigned int i=blockNr+1; i<blockSizes.size(); i++) {
		index.push_back(blockBeginnings[i]);
		}
	// Fallunterscheidung:: Zelle ist Blockanfang
	if (coordinate == blockBeginnings[blockNr]) {
		index.push_back(coordinate);
		}
	else { 
		int end=0;
		if(blockNr < blockBeginnings.size()-1) {
			end=blockBeginnings[blockNr+1];
			}
		else { // ermittelt Ende des Blocks, in dem die Zelle liegt
			end = dimLength;
			}
		// fuegt alle Zellen von der Zelle bis Blockende hinzu
		for(int i = coordinate; i < end; i++) {
			index.push_back(i);
			}
		}
	return index;
	}

vector<int> VariableSRPS::getCellsForRangeSum(int low, int high) {
	vector<int> index(0);
	int k=0;
	low--;
	if(low >=0 ){
		int blockOfLow = getBlockNr(low);
		k = low - blockBeginnings[blockOfLow];
		if(k != 0) {
			index.push_back( 0-blockBeginnings[blockOfLow]-1 );
			}
		index.push_back(0-low-1);
		}
	int blockOfHigh = getBlockNr(high);
	k= high - blockBeginnings[blockOfHigh];
	if(k != 0) {
		index.push_back(blockBeginnings[blockOfHigh]+1);
		}
	index.push_back(high+1);
	return index;
	}

void VariableSRPS::precompute(IDC *idc, int dimNr) {
	vector<double> &werte = idc->werte;
	vector<int> shape = idc->getShape();
	vector<int> current(shape.size());
	double sum = 0;
	double sumLocal = 0;
	unsigned int blockNr = 0;
	for (unsigned int i = 0; i < werte.size(); i++){
		int tmp = idc->getPosition(current);
		sum += werte[tmp];
		// Blockanfang
		if ( current[dimNr] == blockBeginnings[blockNr]) {
			werte[tmp] = sum;
			sumLocal = 0;
			if (blockNr < blockBeginnings.size()-1) {
				blockNr++;
			} else {
				blockNr=0;
			}
		}// kein Blockanfang
		else {
			sumLocal += werte[tmp];
			werte[tmp] = sumLocal;
			}
		// nächste Zelle
		Counters::counternext(current, shape, dimNr);
		if (current[dimNr] == 0){
			sum=0;
			}
		}
	}
