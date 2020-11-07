package MinNoiseRMP;

public class RunMinNoiseRMP extends MinNoiseRMPAlgorithm{

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String[] fileName = {"C:\\Users\\Samir\\Desktop\\workspace\\fastMinerTwo\\src\\combineNew.csv"};
		String fileNameCR = "C:\\Users\\Samir\\Desktop\\workspace\\fastMinerTwo\\src\\outputs\\CandidateRoles";
		
		System.out.println("Running FastMiner");
		prelimnaryCalculations.fastMinerTwo.main(fileName);
		//System.out.println("\n Inside DeltaRMP! \n");
		
		//EXECUTE STATIC METHODS================================================================
		//get the static_UPA from file
		getUPA(fileName[0]); 
		
		//get the static_CR from file
		getCR(fileNameCR);
		
		//System.out.println(prelimnaryCalculations.InitAndDataCollection.getcR());
		//System.out.println(static_UPA);
		//System.out.println(static_CR);
		
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
/*		for(int i=10;i<101;i+=10) {
*/			calcFinalCR(Integer.parseInt(args[0]));
		/*
		 * System.out.println("Done: "+i);
		 */		
		
		
		 System.out.println("Assoc. addn. permissions to users!"); 
		 //assoc. addn. roles to users if |P(r) intersection P(u)| > (1/2)|P(r)|
		 associateAdditionalRoles(); 
		 //for(int i=0; i < TilingUsers.size(); i++) {
		 //for(int j=0; j < TilingUsers.get(0).size(); j++) {
		 //System.out.print(TilingUsers.get(i).get(j) + ","); } System.out.print("\n");
		 // }
		 
		/* } */
		System.out.println("Complete!");
	}
}
//Permission Coverage