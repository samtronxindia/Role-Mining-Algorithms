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
		
		System.out.println("cardinality of UPA Matrix: " + calcUPACardinality());
		
		System.out.println("Calculating Area of Roles!");
		
		//calculate area of each role
		calcArea();
		//System.out.println(static_areaOfRole);

		System.out.println("Sorting Roles!");
		//sort the roles according to descending order of area
		sortRoles();
		//END EXECUTE STATIC METHODS==============================================================
		
		//ALL METHODS EXECUTED AFTER THIS ARE DYNAMIC AND REQUIRE A CLASS OBJECT
		
		/*System.out.println("Sorted static_CR:" + static_sortedCR);
		System.out.println("Sorted Area:" + static_areaOfSortedRole);*/
				
		//ArrayList<InitAndDataCollection> calcFinalCR = new ArrayList<InitAndDataCollection>();
		
		RunDeltaRMP objOne = new RunDeltaRMP();
		objOne.initializeFinalCR();
		objOne.calcFinalCR(350);

		
		/*for(int i = 5000 ; i > 50 ; i = (i - 100) ){
			RunDeltaRMP objOne = new RunDeltaRMP();
			
			//init the finalCR with first element and update tiling matrix
			System.out.println("Init static_CR 1st value for max delta = !" + i);
			objOne.initializeFinalCR();
			
			//calc rest of finalCR
			System.out.println("Calc rest of static_CR for max delta = " + i);
			objOne.calcFinalCR(i);
			try {
				objOne.finalize();
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
		System.out.println("Done!");
		
		/*try{
			PrintWriter roleCardinality = new PrintWriter("/home/samir/workspace/DeltaRMP/src/output/roleSetCardinality");
			PrintWriter privilegeDelta = new PrintWriter("/home/samir/workspace/DeltaRMP/src/output/PrivilegeDelta");
			PrintWriter permissionCoveragePrinter = new PrintWriter("/home/samir/workspace/DeltaRMP/src/output/PermissionCoverageDelta");

			roleCardinality.println(static_roleSetCardinality);
			privilegeDelta.println(static_privilege);
			permissionCoveragePrinter.println(static_permissionCoverage);

			roleCardinality.close();
			privilegeDelta.close();
			permissionCoveragePrinter.close();
		}catch(Exception e){
			e.printStackTrace();
		}*/

		
		/*System.out.println("UPATilingMatrix: "); 
		for(int i = 0 ; i < upaTilingMatrix.size(); i++){
			System.out.println(upaTilingMatrix.get(i));
		}*/
		
		/*System.out.println("Final static_CR: "); 
		for(int i = 0 ; i < finalCR.size(); i++){
			System.out.println(finalCR.get(i));
		}*/

	}

}
