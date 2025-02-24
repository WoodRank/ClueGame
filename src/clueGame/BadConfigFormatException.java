/*
 * Authors: Thomas Manfredo and Zaudan Wawhkyung
 * Purpose: This class will throw an exception if the configuration is in the wrong format, letting the program know what is wrong
 */

package clueGame;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class BadConfigFormatException extends Exception{
	
	public BadConfigFormatException() throws FileNotFoundException {
		super("File configuration is formatted incorrectly, check to make it follows required structure");
		PrintWriter log = new PrintWriter("log.txt");
		log.println("File configuration is formatted incorrectly, check to make it follows required structure");
		log.close();
	}
	
	public BadConfigFormatException(String msg) throws FileNotFoundException {
		super(msg);
		PrintWriter log = new PrintWriter("log.txt");
		log.println(msg);
		log.close();
	}
}
