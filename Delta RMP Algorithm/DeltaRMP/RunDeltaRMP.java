package DeltaRMP;
import java.io.PrintWriter;
import java.util.ArrayList;

import prelimnaryCalculations.fastMinerTwo;

public class RunDeltaRMP extends DeltaRMPAlgorithm{
	
	// getUPA(), getCR(), displayIr(), calcArea() and sortRoles() are static methods 

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String[] fileName = {"C:\\Users\\Samir\\Desktop\\workspace\\DeltaRMP\\src\\combineNew.csv"};
		String fileNameCR = "C:\\Users\\Samir\\Desktop\\workspace\\fastMinerTwo\\src\\outputs\\CandidateRoles";
		
		System.out.println("Running FastMiner");
		prelimnaryCalculations.fastMinerTwo.main(fileName);
		
		//EXECUTE STATIC METHODS================================================================
		//get the static_UPA from file
		getUPA(fileName[0]); 
		
		//get the static_CR from file
		getCR(fileNameCR);
		
		//get the permissions and applications from file
		displayIr();
		
		System.out.println("cardinality of UPA Matrix: " + calcUPACardinality());
		
		System.out.println("Calculating Area of Roles!");
		
		//calculate area of each role
		calcArea();

		System.out.println("Sorting Roles!");
		//sort the roles according to descending order of area
		sortRoles();
		//END EXECUTE STATIC METHODS==============================================================
		
		//ALL METHODS EXECUTED AFTER THIS ARE DYNAMIC AND REQUIRE A CLASS OBJECT		
		RunDeltaRMP objOne = new RunDeltaRMP();
		objOne.initializeFinalCR();
		objOne.calcFinalCR(350);

		System.out.println("Done!");
	}
}
