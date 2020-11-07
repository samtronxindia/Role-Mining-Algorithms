package MinNoiseRMP;

import java.util.ArrayList;

public class UpdateCR extends InitAndDataCollection{
	
	public static void addIndividualPermToCR(){
		
		boolean found = false;
		int i = 0;
		int j = 0;
		
		//create identity matrix with row,column = size of role in CR
		ArrayList<ArrayList<Integer>> identityCR = new ArrayList<ArrayList<Integer>>();
		for(i = 0 ; i < CR.get(0).size(); i++){
			ArrayList<Integer> tempRole = new ArrayList<Integer>();
			for(j = 0 ; j < CR.get(0).size(); j++){
				if(i == j){
					tempRole.add(1);
				}
				else{
					tempRole.add(0);
				}
			}
			identityCR.add(new ArrayList<Integer>(tempRole));
		}
				
		//add single permission role to CR if one doesn't exist already
		for(int a = 0 ; a < identityCR.size(); a++){
			for(i = 0 ; i < CR.size(); i++){
				//i serves as the row counter 
				for(j = (CR.get(0).size() - 1) ; j >= 0  ; j--){
					//j serves as the column counter as well as which element should be 1 in a single permission role
					if(identityCR.get(a).get(j) == 1){//IOB
						if(CR.get(i).get(j) != 1){
							break;
						}
					}
					else if(identityCR.get(a).get(j) == 0){ 
						if(CR.get(i).get(j) != 0){
							break;
						}
					}
				}
				
				//if single permission role was found set found flag to be true
				if(j == -1){
					found = true;
				}
			}
			
			//if the single permission role was not found add it to CR
			if(found == false){
				CR.add(new ArrayList<Integer>(identityCR.get(a)));
			}
			
			//reset the found flag to false
			found = false;
		}
	}
}
