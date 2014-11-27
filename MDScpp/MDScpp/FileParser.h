#pragma once

#include "IDC.h"
#include <string>
#include <iostream>
#include <fstream>

using namespace std;

/**
* Extract data from specific.csv file and construct Cube
* @author Zurab Khadikov
*/
class FileParser {
public:
	FileParser();
	~FileParser();
	static void parse(string filename, IDC &cube);
	};