package prelimnaryCalculations;
import java.util.ArrayList;

public class PhaseOne extends UP_Analysis {

	public static void initPhase() {
			int i = 0;
			int j = 0;		
			int k = 0;		
			boolean found = false;
			//iR.add(new ArrayList<Integer>(uP.get(0).size()));
			iR.add(uP.get(0));
			//*System.out.println("IR 1st element: " + iR.get(0) + "\n");
			//*System.out.println(" First element of IR:" + iR);
			origCount.add(0 , 1);
			//*System.out.println("0st UP:" + uP.get(0) + "\n");
			//*System.out.println("1nd UP:" + uP.get(1) + "\n");
			//for loop to check rows - for each user u E U do
			for(i = 1; i < uP.size() ; i++){
					//System.out.println("Processing " + i + "th element of UP from" + uP.size() + " elements!");
					for(j = 0; j < iR.size() ; j ++){
							//System.out.println("Processing " + j + "th element of IR from" + iR.size() + " elements!");
							//for loop to check columns
							//*System.out.println("Comparing " + (i+1) + "th row of UP " + uP.get(i) + "to " + (j+1) + "th row of IR :" + iR.get(j) + "\n");
							//*System.out.println(" Max value of k: " +  uP.get(i).size());
							for(k = uP.get(i).size() -1 ; k >= 0  ; k--){
									//*System.out.println("Comparing " + (k+1) + "th element of UP and IR \n");
									if(uP.get(i).get(k) != iR.get(j).get(k)){
											//*System.out.println("val of k break:" + (k+1) + "\n");
											break;
									}//System.out.println("val of k:" + k);
							}//end for k
							// Increment origCount of (iR)'th element by 1
							if(k == -1){
									//*System.out.println("Caught!");
									found = true;
									origCount.set(j , origCount.get(j) + 1 ); 
									//break;
							}
							//*System.out.println("Next j \n");
							//System.out.println("val of k:" + k);
					}//end for iR			
					//end for columns - if k = -1 means the element was the same in iR and uP - means increment origCount of that (iR)'th element 
					//if k is not -1 that means loop was broken before completion - means the element is not the same! - so add it to iR
	
					if(found == false){
							//iR.add(new ArrayList<Integer>(uP.get(i).size()));
							iR.add(new ArrayList<Integer>(uP.get(i)));
							origCount.add(1);
							//*System.out.println("New IR: " + (j+1) + "added IR: " + iR.get(j));
							//break;
					}	
					//*System.out.println("Updated IR: " + iR + "\n");
					found = false;
					//*System.out.println("Next i -------------------------------------------------------------------------" + "\n");
			}//end for uP
	}//end initPhase()

	public PhaseOne() {
		super();
	}

}