package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.StringTokenizer;

import structures.ArrayA;
import structures.ArrayP;
import structures.OlapCube;

/**
 * Extract data from file /Demo2/database_CUBE_16.csv file and construct
 * HyperCube
 * 
 * @author Dominic Mai, modified by Philippe and Zurab
 * 
 */
public class FileParser2 {

	public FileParser2(String fileName) {

	}

	public static OlapCube parse(String filename, OlapCube cube) {

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filename));
		} catch (Exception e) {
			System.out.println("File not found: " + filename);
			System.exit(1);
		}

		// read lines and do appropriate stuff...
		int lineCount = 0;
		String line;
		StringTokenizer tok;

		int[] shape = { 1, 3, 9, 12, 42, 100, 80 };
		String[] description;
		int dimcount = -1;
		int[] pos = new int[0];
		double value;

		try {
			while ((line = br.readLine()) != null) {

				if (line.indexOf("#") != -1)
					continue;// comment found, ok a bit sloppy
				line.trim();

				if (line.equals("[DIMENSIONS]")) {// part with dimensions
													// begins
					line = br.readLine();
					dimcount = Integer.parseInt(line); // read #Dimensions
					pos = new int[dimcount];
					shape = new int[dimcount];
					description = new String[dimcount];

					for (int i = 0; i < dimcount; i++) {
						line = br.readLine();
						tok = new StringTokenizer(line, ",; ");
						description[i] = tok.nextToken();
						shape[i] = Integer.parseInt(tok.nextToken());
					}
					// build Cube
					cube.initialize(shape, description); // Z.K. habe hier
															// noch Parameter
															// description
															// zugef�gt
				}
				if (line.equals("[CUBE]")) {// part with dimensions begins
					line = br.readLine();
					// String line2=line;
					dimcount = -1;
					tok = new StringTokenizer(line, ",; ");
					while (tok.hasMoreTokens()) {
						dimcount++;
						System.out.println(tok.nextToken());
					}
					pos = new int[dimcount];
					description = new String[dimcount];
					/*
					 * tok=new StringTokenizer(line2, ",; "); String
					 * bla=tok.nextToken(); for (int i=0; i<dimcount;i++){
					 * shape[i]=Integer.parseInt(tok.nextToken()); }
					 */

					cube.initialize(shape, description); // Z.K. habe hier
															// noch Parameter
															// description
															// zugef�gt
				}
				if (line.equals("[DATA]")) { // fill the cube
					while ((line = br.readLine()) != null) {

						tok = new StringTokenizer(line, ";,");
						for (int i = 0; i < dimcount; i++) {// get position
							pos[i] = Integer.parseInt(tok.nextToken());
						}
						value = Double.parseDouble(tok.nextToken());// and value

						cube.setCellValue(pos, value);
						lineCount++;
					}
				}// END DATA

				if (line.equals("[NUMERIC]")) { // fill the cube
					while ((line = br.readLine()) != null) {
						if (line.indexOf("#") != -1)
							break;
						tok = new StringTokenizer(line, ";,");
						for (int i = 0; i < dimcount; i++) {// get position
							pos[i] = Integer.parseInt(tok.nextToken());
						}
						value = Double.parseDouble(tok.nextToken());// and value
						value = Double.parseDouble(tok.nextToken());

						cube.setCellValue(pos, value);
						lineCount++;
					}
				}// END DATA
			} // END OUTER WHILE

		} catch (IOException e) {
			System.out.println("an error occured reading file!");
			e.printStackTrace();
		}

		System.out.println(lineCount + " Points added to Cube");

		return cube;
	}// END parse()

	/**
	 * for testing only
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		

	}
}
