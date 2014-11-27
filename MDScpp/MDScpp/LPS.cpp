#include "LPS.h"

LPS::~LPS(){}

vector<int> LPS::getCellsForCellUpdate(int coordinate)
	{
	int k = coordinate%blockSize;
    int block = coordinate / blockSize;
    vector<int> index(0);
        int end = blockSize;
        //wenn die Koordinate im letzten Block liegt, end auf Größe des letzten Blocks setzen
        if (block == (dimLength - 1) / blockSize) {
            end = (dimLength - 1) % blockSize + 1;
        }
        for (int i = k; i < end; i++) {
			index.push_back(block*blockSize+i);
        }
        return index;
	}

vector<int> LPS::getCellsForRangeSum(int low, int high)
	{
	vector<int> index(0);
	if (high < low) {
            return index;
        }
    int k = low%blockSize;
    int blockOfLow = low/blockSize + 1;
    int blockOfHigh = high/blockSize + 1;
    
    //Was muss abgezogen werden
    if (k > 0) { 
		index.push_back(0-low); //-(low-1+1), wegen Index-Verschiebung			
        }
        //Fügt alle Blockenden, die zwischen low-1 und high liegen. Indexverschiebung ist einberechnet.
        for (int j = blockOfLow; j < blockOfHigh; j++) {
			index.push_back(j*blockSize);
        }
		index.push_back(high+1);
        return index;
	}

void LPS::precompute(IDC *idc, int dimNr)
	{
	vector<double> &werte = idc->werte;
    vector<int> shape = idc->getShape();
    vector<int> current(shape.size());
    vector<int> predecessor;
	for ( unsigned int i = 0; i < werte.size(); i++) {
		predecessor.assign(current.begin(),current.end());
		predecessor[dimNr]--;            
		if ( current[dimNr] % blockSize != 0) {
			werte[ idc->getPosition(current) ] += werte[ idc->getPosition(predecessor) ];
            }
		Counters::counternext(current, shape, dimNr);
		}
    }