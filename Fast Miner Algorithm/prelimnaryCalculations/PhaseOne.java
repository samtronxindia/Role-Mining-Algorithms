package prelimnaryCalculations;
import java.util.ArrayList;

public class PhaseOne extends InitAndDataCollection {

	public static void initPhase() {
			int i = 0;
			int j = 0;		
			int k = 0;		
			boolean found = false;
			iR.add(uP.get(0));

			origCount.add(0 , 1);
			//for loop to check rows - for each user u E U do
			for(i = 1; i < uP.size() ; i++){
					for(j = 0; j < iR.size() ; j ++){
							
							for(k = uP.get(i).size() -1 ; k >= 0  ; k--){
									if(uP.get(i).get(k) != iR.get(j).get(k)){
											break;
									}
							}//end for k
							// Increment origCount of (iR)'th element by 1
							if(k == -1){
									found = true;
									origCount.set(j , origCount.get(j) + 1 ); 
									//break;
							}
					}//end for iR			
					//end for columns - if k = -1 means the element was the same in iR and uP - means increment origCount of that (iR)'th element 
					//if k is not -1 that means loop was broken before completion - means the element is not the same! - so add it to iR
	
					if(found == false){
							iR.add(new ArrayList<Integer>(uP.get(i)));
							origCount.add(1);
					}	
					found = false;
			}//end for uP
	}//end initPhase()

	public PhaseOne() {
		super();
	}

}