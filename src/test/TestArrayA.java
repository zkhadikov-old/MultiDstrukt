/**
 * 
 */
package test;

import helpers.SerializeAndDeserializeCube;
import structures.*;

/**
 * For testing only
 * @author Zurab Khadikov, Philippe Hagedorn
 * 
 */
public class TestArrayA {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		IDC cube=new IDC();
		String file="src//helpers//test_year.csv";
		helpers.FileParser.parse(file, cube);
		//cube.dimension[0].setHierarchie(TestHierarchy.year());
		cube.precompute();
		System.out.println(cube.getSize());
		int[] lower={0,0};
		int[] upper={365,19};
		
		
		
	}

}
