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
		
		//Analyse UP and get Vertical counts
		AnalyseUP();
		
		//checks to see common elements in IR, GR and CR
		/*checkIr();
		checkGr();
		checkCr();*/
		
		//generates the "output" file for printing general results
		printOutput();
		
		//generates CR with maxPriority=100
		calcRating(100);
		
		//calc #perms associated with each IR,GR and CR
		eachPermCalc();
		 
		//outputs the Rating and CR to files Ratings, PermCR (file containing alphabetical permissions) &
		// CandidateRoles (file containing 0's and 1's for CR)
		printRating();
		
		/*//calc the C2R and further
		try {
			updateCr();
		} 
		catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		CR_Analysis();
		
		System.out.println("C2R Size" + c2R.size());
		
		//output the updated calculations to files UpdatedCR,UpdatedRating, UpdatedC3r and UpdatedRating3
		printUpdatedRatingAndCR();*/
		
	}//end main
}//end class
