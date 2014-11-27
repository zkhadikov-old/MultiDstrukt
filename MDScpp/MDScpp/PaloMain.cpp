#include <iostream>
#include "IDC.h"
#include <string>
#include "FileParser.h"

using namespace std;

int main() {
	//test
	vector<int> minCell(6,0);;
	vector<int> maxCell;	
	maxCell.push_back(24);
	maxCell.push_back(18);
	maxCell.push_back(11);
	maxCell.push_back(7);
	maxCell.push_back(1);
	maxCell.push_back(2);
	
	IDC idc(minCell,maxCell);
	FileParser::parse("demo_database.csv",idc);

	cout<<idc.getSizeAlt()<<endl;
	cout<<idc.getSize()<<endl;

	idc.precompute();

	vector<int> lowCell;	
	lowCell.push_back(0);
	lowCell.push_back(0);
	lowCell.push_back(0);
	lowCell.push_back(0);
	lowCell.push_back(0);
	lowCell.push_back(0);

	vector<int> upperCell;	
	upperCell.push_back(24);
	upperCell.push_back(18);
	upperCell.push_back(11);
	upperCell.push_back(7);
	upperCell.push_back(1);
	upperCell.push_back(2);


	double v = idc.getRangeSum(lowCell,upperCell);
	cout<<"Resultat ist: "<<v<<endl;
	cout<<"TotalSum ist: "<<idc.getTotalSum()<<endl;
	cout<<"CellValue vor Update ist: "<<idc.getCellValue(lowCell)<<endl;
	idc.setCellValue(lowCell, 0);
	cout<<"CellValue nach Update ist: "<<idc.getCellValue(lowCell)<<endl;
	idc.rangeUpdate(lowCell,upperCell,5000000,0);
	cout<<"TotalSum ist: "<<idc.getRangeSum(lowCell,upperCell)<<endl;
	cout<<idc.getSizeAlt()<<endl;
	cout<<idc.getSize()<<endl;
	idc.rangeUpdate(lowCell,upperCell,0,0);
	cout<<"TotalSum ist: "<<idc.getRangeSum(lowCell,upperCell)<<endl;
	idc.setCellValue(lowCell, 1000);
	cout<<idc.getSizeAlt()<<endl;
	cout<<idc.getSize()<<endl;
	cout<<"TotalSum ist: "<<idc.getRangeSum(lowCell,upperCell)<<endl;
	idc.setCellValue(upperCell, 2000);
	cout<<idc.getSizeAlt()<<endl;
	cout<<idc.getSize()<<endl;
	cout<<"TotalSum ist: "<<idc.getRangeSum(lowCell,upperCell)<<endl;
	return 0;
	
	}