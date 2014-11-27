package test;

import structures.*;
/**
 * For testing only
 * @author Zurab Khadikov, Philippe Hagedorn
 */

public class TestArrayA2 {
	public static void main(String[] args) {
		int[] dimensions = { 366, 10 };
		String[] descript = { "Tage", "Produkt" };
		//ArrayA a = new ArrayA();
		IDC a=new IDC();
		
		a.initialize(dimensions, descript);
		int[] coord = { 1, 1, 1 };
		a.setCellValue(coord, 3.4);
		coord[0] = 0;
		coord[1] = 0;
		coord[2] = 2;
		a.setCellValue(coord, 2);
		coord[0] = 0;
		coord[1] = 1;
		coord[2] = 2;
		a.setCellValue(coord, 3);
		coord[0] = 1;
		coord[1] = 1;
		coord[2] = 2;
		a.setCellValue(coord, 8);
		coord[0] = 1;
		coord[1] = 3;
		coord[2] = 5;
		a.setCellValue(coord, 11);
		a.precompute();
		System.out.println(a.getSize());
		
		
		System.out.println(a.getSize());
		// System.out.println(a.getCellValue(coord));
		int[] lower = { 0, 0, 0 };
		int[] higher = { 2, 3, 5 };
		// System.out.println(a.getCellValue(coord));
		System.out.println(a.getRangeSum(lower, higher));
		// ArrayP P=new ArrayP(a);
		// System.out.println(P.getCellValue(higher));

	}

}
