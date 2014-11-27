#include "Counters.h"
#include <vector>

using namespace std;

Counters::Counters(void) {}
Counters::~Counters(void) {}

void Counters::counternext(vector<int> &current, const vector<int> &shape, int actDim) 
	{
	bool ready = false;
	unsigned int j = actDim;
	int cycleCounter = 0;
	int dim=shape.size();
	while (!ready) {
		// Obergrenze fuer die Dimension erreicht? Wenn nein, fertig.
		if ( current[j] < (shape[j] - 1) ) {
			current[j]++;
			ready = true;
			break;
			}
		// setze Wert der Dimension auf 0 und gehe zur naechsten
		if (j < actDim) {
			for (unsigned int i = actDim; i < current.size(); i++)
				current[i] = 0;
			for (unsigned int i = 0; i <= j; i++)
				current[i] = 0;
			} else {
				for (unsigned int i = actDim; i <= j; i++)
					current[i] = 0;
			}
		if (j < current.size() - 1)
			j++;
		else
			j = 0;
		// Verhinderung einer Endlosschleife bei Spezialfaellen
		cycleCounter++;
		if (cycleCounter == dim)
			break;
		}
	}

void Counters::counternextLow(vector<int> &current, const vector<int> &lower, const vector<int> &shape)
	{
	bool ready = false;
	int j = 0;
	int dim=shape.size();
	while (!ready) {
		if ( current[j] < (shape[j] - 1) ) {
			current[j]++;
			ready = true;
			break;
			}
		for (int i = 0; i <= j; i++)
			current[i] = lower[i];
		j++;
		if (j == dim)
			break;
		}
	}
