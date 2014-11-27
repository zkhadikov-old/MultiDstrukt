#include "IDCDimension.h"
#include <string>

using namespace std;

IDCDimension::~IDCDimension(void)
	{
	}

IStrategie* IDCDimension::getStrategie()
	{
	return this->strategie;
	}

void IDCDimension::setStrategie(IStrategie *strategie)
	{
	if(this->strategie==0){
		this->strategie=strategie;
		}
	}
