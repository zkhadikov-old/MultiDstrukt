package helpers;
import java.io.*;

/**
 * For testing only
 * @author Zurab Khadikov, Philippe Hagedorn
 */
public class CSVWriter {
	public static void main(String[] args){
		Writer fw=null; 
		 
		try 
		{ 
		  fw = new FileWriter( "test_year.csv" );
		  fw.write("[DIMENSIONS]\n");
                  fw.write("1\n");
		  fw.write("year 366\n");
		  fw.write("[DATA]\n");
		  
		  for (int i=0;i<100;i++){
			  int year=(int) Math.floor(Math.random() * (366));
			  
			  double value=Math.random()*100;
			  fw.write(year+";"+value+";\n");
		  }
		  
		  
		} 
		catch ( IOException e ) { 
		  System.err.println( "Konnte Datei nicht erstellen" ); 
		} 
		finally { 
		  if ( fw != null ) 
		    try { fw.close(); } catch ( IOException e ) { } 
		}
		
	}
	

}
