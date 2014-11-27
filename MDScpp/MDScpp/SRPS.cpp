#include "SRPS.h"


SRPS::~SRPS() {}
			
vector<int> SRPS::getCellsForCellUpdate(int coordinate) {
	//ungueltige Koordinate
	vector<int> result(0);
	if (coordinate < 0 && coordinate > dimLength - 1) {
		return result;
		}
	int k = coordinate % blockSize;
	//falls Koordinate ist nicht Beginn eines Blocks, muss Rest des Blocks aktualisiert werden
	if (k != 0) {
		for (int j = k + 1; j < blockSize; j++) {
			if (coordinate - k + j < dimLength) {
				result.push_back(coordinate - k + j);
				}
			}
		}
	result.push_back(coordinate);                             //fuegt die Koordinate selbst hinzu
	int blockNr = coordinate / blockSize;						//in wievieltem Block ist die Koordinate
	int toUpdate = (dimLength - 1) / blockSize - blockNr;	    //wieviele Bloecke muessen aktualisiert werden.		
	k = (blockNr + 1) * blockSize;
	for (int j = 0; j < toUpdate; j++)                         //fuegt die restlichen Blockanfaenge hinzu
		{
		result.push_back(k+j*blockSize);
		}
	return result;
	}

vector<int> SRPS::getCellsForRangeSum(int low, int high) {
	vector<int> index(0);
	if (high < low) 
		{
		return index;
		}
	low--;
	int k;
	if (low >= 0) 
		{
		k = low%blockSize;
		if (k != 0) 
			{
			index.push_back(-(low-k)-1);
			}
		index.push_back(-low-1);
		}
	k = high%blockSize;
	if (k != 0) {
		index.push_back(high-k+1);
		}
	index.push_back(high +1);
	return index;
	}

void SRPS::precompute(IDC *idc, int dimNr) {
	vector<double> &werte = idc->werte;
	vector<int> shape = idc->getShape();
	double sum = 0;
	double sumLocal = 0;
	vector<int> current(shape.size());
	for (unsigned int i = 0; i < werte.size(); i++) {
		int tmp = idc->getPosition(current);
		sum += werte[tmp];
		if (( current[dimNr] % blockSize ) == 0) {
			werte[tmp] = sum;
			sumLocal = 0;
			} else {
				sumLocal += werte[tmp];
				werte[tmp] = sumLocal;
			}
		Counters::counternext(current, shape, dimNr);
		if ( current[dimNr] == 0) {
			sum = 0;
			}
		}
	sum = 0;
	}


		