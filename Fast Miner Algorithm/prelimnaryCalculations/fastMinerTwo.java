package prelimnaryCalculations;
import java.util.*;
import java.lang.*;
import java.io.*;

public class fastMinerTwo extends fastMinerPrintOutput{
	
	public static void main(String[] args){
		
		//initial data collection method
		fm(args[0]);
		
		//run the init phase for generating IR
		initPhase();
		
		//run the second phase for generating GR
		secondPhase();
		
		//gather the entire permissions list
		displayIr();
		
		//generates the "output" file for printing general results
		printOutput();
		
		//generates CR with maxPriority=100
		calcRating(100);
		 
		//outputs the Rating and CR to files Ratings, PermCR (file containing alphabetical permissions) &
		// CandidateRoles (file containing 0's and 1's for CR)
		printRating();
		
	}//end main
}//end class
