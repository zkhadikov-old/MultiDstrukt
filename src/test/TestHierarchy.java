package test;
import structures.*;
/**
 * For testing only
 * @author Zurab Khadikov, Philippe Hagedorn
 */

public class TestHierarchy {

	
	
	public static void main(String[] args) {		
            DimensionHierarchie d1,d2,d3;
            LoHiNode lohi=new LoHiNode();
            lohi.low=0;
            lohi.high=30;            
            d1=new DimensionHierarchie("Januar", lohi);
            LoHiNode lohi2=new LoHiNode();
            lohi2.low=31;
            lohi2.high=60;
            d2=new DimensionHierarchie("Februar", lohi2);
            DimensionHierarchie[] children={d1,d2};
            d3=new DimensionHierarchie("1. Quartal", children);
            System.out.println();
	}

}
