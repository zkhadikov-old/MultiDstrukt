#pragma once
#include "IStrategie.h"
#include <vector>
#include <string>

using namespace std;
/*!
* Diese Klasse implementiert die eindimensionale Strategie "Local prefix sum"
* mit variablen Blockgroesse.
*
* @author Zurab Khadikov
*/
class VariableLPS : public IStrategie 
	{
	private:
		int dimLength;
		vector<int> blockSizes;
		vector<int> blockBeginnings;

		/*!
		* schaut in welchem Block die Zelle liegt.
		* @param cellNr
		* @return Nummer
		*/
		int getBlockNr(int celNr);
	public:
		/*!
		* Konstruktor
		*
		* @param dimSize Laenge der Dimension
		* @param blockSizes Groesse der einzelnen Bloecke
		*/
		VariableLPS(int dL,const vector<int> &bS) : dimLength(dL), blockSizes(bS), blockBeginnings(bS.size())
			{
			unsigned int size = bS.size();
			for(unsigned int i=1; i < size; i++){
				blockBeginnings[i]=blockBeginnings[i-1]+this->blockSizes[i-1];
				}
			if( (blockBeginnings[size-1]+this->blockSizes[size-1]) != dimLength){
				cerr<<"Wrong Block Sizes!"<<endl;
				}
			};

		/*!
		 *	Destruktor
		 */
		~VariableLPS();
		
		/*!
		* @see IStrategie#getCellsForCellUpdate(int)
		*/
		vector<int> getCellsForCellUpdate(int coordinate);

		/*!
		* @see IStrategie#getCellsForRangeSum(int, int)
		*/
		vector<int> getCellsForRangeSum(int low, int high);
		
		/*!
		* @see IStrategie#getDimLength()
		*/
		int getDimLength();

		/*!
		 *	@see IStrategie#getID()
		 */
		int getID();

		/*!
		 *	@see IStrategie#getName()
		 */
		string getName();

		/*!
		 *	@see IStrategie#precompute(IDC, int)
		 */
		void precompute(IDC *idc, int dimNr);
	};

inline int VariableLPS::getBlockNr(int celNr) {
	unsigned int i=0;
	for(i=blockSizes.size()-1; i >= 0; i--){
		if(celNr >= blockBeginnings[i]) break;
		}
	return i;
	}

inline int VariableLPS::getID() {return 120;}

inline string VariableLPS::getName() {return string("Local Prefix Sum with variable block size");}

inline int VariableLPS::getDimLength() {return dimLength;}