package MinNoiseRMP;

public class RunMinNoiseRMP extends MinNoiseRMPAlgorithm{

	public static void main(String[] args) {
		
		String[] fileName = {"C:\\Users\\Samir\\Desktop\\workspace\\fastMinerTwo\\src\\combineNew.csv"};
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
		
		//update CR according to Min Noise RMP algorithm to include single permission roles
		addIndividualPermToCR();
		
		//calculate area of each role
		System.out.println("Calculating Area of Roles!");
		calcArea();
		System.out.println("Done");

		//sort the roles according to descending order of area
		System.out.println("Sorting Roles!");
		sortRoles();
		System.out.println("Done");

		System.out.println("Init final CR");
		//PHASE 1 COMPLETE!
		initializeFinalCR();
		System.out.println("Done");
		
		System.out.println("Calc final CR");
		//calc final set of roles, the value inside brackets indicates the cut off point for the Cardinality of ROLES set
		calcFinalCR(Integer.parseInt(args[0]));
		
		 System.out.println("Assoc. addn. permissions to users!"); 
		 //assoc. addn. roles to users if |P(r) intersection P(u)| > (1/2)|P(r)|
		 associateAdditionalRoles(); 
		 System.out.println("Complete!");
	}
}
//Permission Coverage