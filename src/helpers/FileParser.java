package helpers;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;


import structures.*;
import helpers.SerializeAndDeserializeCube;

/**
 * Extract data from specific .csv file and construct HyperCube
 * @author Dominic Mai
 *
 */
public class FileParser {

	public FileParser(String fileName) {
		
	}
	
	public static OlapCube parse(String filename, OlapCube cube) {
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filename));
		} catch (Exception e) {
			System.out.println("File not found: "+ filename);
			System.exit(1);
		}
		
		//read lines and do appropriate stuff...
		int lineCount = 0;
		String line;
		StringTokenizer tok;
		
		int[] shape;
		String[] description;
		int dimcount = -1;
		int[] pos= new int[0];
		double value;
		
		try {
			while ((line = br.readLine()) != null) {
				
				if (line.indexOf("#") != -1) continue;//comment found, ok a bit sloppy
				line.trim();
		
				if (line.equals("[DIMENSIONS]")) {//part with dimensions begins
					line = br.readLine();
					dimcount = Integer.parseInt(line);	//read #Dimensions
					pos = new int[dimcount];
					shape = new int[dimcount];
					description = new String[dimcount];
					
					for (int i=0; i < dimcount; i++) {
						line = br.readLine();
						tok = new StringTokenizer(line, ",; ");
						description[i] = tok.nextToken();
						shape[i] = Integer.parseInt(tok.nextToken());
					}
					//build Cube
					cube.initialize(shape,description); 
				}
				if (line.equals("[DATA]")) {	//fill the cube
					while ((line = br.readLine()) != null) {
						
						tok = new StringTokenizer(line, ";,");
						for (int i=0; i<dimcount; i++) {//get position
							pos[i] = Integer.parseInt(tok.nextToken());
						}
						value = Double.parseDouble(tok.nextToken());//and value
						
						cube.setCellValue(pos, value);
						lineCount++;
					}
				}//END DATA
			} //END OUTER WHILE
			
			
		} catch (IOException e) {
			System.out.println("an error occured reading file!");
			e.printStackTrace();
		}

		System.out.println(lineCount + " Points added to Cube");
		return cube;
	}//END parse()
	/**
	 * for testing only
	 * @param args
	 */
	public static void main(String[] args) {
		
                
		String filename="/home/hagedorn/cube2.idc";
		IDC cube=(IDC) SerializeAndDeserializeCube.deserialize(filename);
		int[] lower={0,0,0,0,0,0};
		int[] upper={0,0,0,0,0,0};
		int[] middle=new int[6];
		for(int i=0;i<cube.getShape().length;i++){
			upper[i]=cube.getShape()[i]-1;
			middle[i]=cube.getShape()[i]/2;
		}
		System.out.println(cube.getRangeSum(lower, upper));
		System.out.println(cube.getRangeSum(lower, middle));
		System.out.println(cube.getCellValue(upper));
		System.out.println(cube.getRangeSum(lower, lower));
		
		
		
	}
}
