#include <string>
#include <iostream>
#include <fstream>
#include <boost/tokenizer.hpp>
#include <boost/algorithm/string.hpp>

#include "IDC.h"
#include "FileParser.h"

using namespace std;
using namespace boost;


FileParser::FileParser(){}


void FileParser::parse(string filename, IDC &cube) {
	ifstream fr(filename.c_str() );
	if ( !fr) {
		cerr << "Konnte Datei " << filename<< " nicht öffnen\n";
		exit(-1);
		} else {
			//read lines and do appropriate stuff...
			int lineCount = 0;
			// Buffer;
			string line;

			// Erzeuge Hilfsvectoren
			vector<int> *shape=new vector<int>;
			vector<string> *description=new vector<string>;
			vector<int> pos(0);

			int dimcount = -1;
			double value;
			typedef boost::tokenizer<boost::char_separator<char>> tokenizer;

			while (getline(fr, line) ) {
				if (line.find('#', 0) != string::npos)
					continue;//comment found
				trim(line);
				if (line.compare("[DIMENSIONS]")==0) { //part with dimensions begins
					getline(fr, line);
					dimcount = atoi(line.c_str()); //read #Dimensions

					// Setze die Größe von Vectoren
					shape->resize(dimcount);
					description->resize(dimcount);
					pos.resize(dimcount);

					// Lese die Cube Info aus und initialisiere Cube
					// kann man auch anders machen
					for (int i=0; i<dimcount; i++) {
						getline(fr, line);
						(*description)[i]=line.substr(0, line.find(' '));
						(*shape)[i]=atoi((line.substr(line.find(' ')+1)).c_str() );
						}
					//build Cube
					//cube->initialize(shape,description);
					// Cube ist schon mit dem Konstructor initializiert

					}
				if (line.compare("[DATA]")==0) { //fill the cube
					string temp;
					boost::char_separator<char> sep(",;");
					while (getline(fr, line)) {

						tokenizer tokens(line, sep);
						tokenizer::iterator tok_iter = tokens.begin();

						for(int i=0; i<dimcount;i++){
							temp=*tok_iter;
							pos[i]=strtol(temp.c_str(),NULL,10);
							tok_iter++;
							}
						temp=*tok_iter;
						value=strtod(temp.c_str(),NULL);
						cube.setCellValue(pos,value);
						lineCount++;

						}
					}
				}
			delete shape;
			delete description;
			cout<<lineCount<<" Points added to Cube"<<endl;

		}

	}
FileParser::~FileParser() {
	}
