package BasicRMP;

import java.util.ArrayList;

public class Initialization extends DataAquisition{
	
	public static void initializeUPAmmAndP(){
		
		//init coverage matrix upaTilingMatrix
		for(int i = 0 ; i < UPA.size() ; i++){
			ArrayList<Integer> temp = new ArrayList<Integer>();
			for(int j = 0 ; j < UPA.get(0).size(); j++){
					temp.add(j, 0);
			}
			upaTilingMatrix.add(i, new ArrayList<Integer>(temp));
		}
		
		//init LT 
		for(int i = 0 ; i < UPA.size() ; i++){
			ArrayList<Integer> temp = new ArrayList<Integer>();
			for(int j = 0 ; j < UPA.get(0).size(); j++){
					temp.add(j, 0);
			}
			LT.add(i, new ArrayList<Integer>(temp));
		}
		
		//init num perms per app to 0
		for(int i = 0 ; i < UPA.size() ; i++){
			numOfPermsPerAppUPA.add(i, 0);
		}
		
		//init conditionalUPA
		for(int i = 0 ; i < UPA.size() ; i++){
			ArrayList<Integer> temp = new ArrayList<Integer>();
			for(int j = 0 ; j < UPA.get(0).size(); j++){
					temp.add(j, 0);
			}
			conditionalUPA.add(i, new ArrayList<Integer>(temp));
		}
		
		//init nextConditionalUPA
		for(int i = 0 ; i < UPA.size() ; i++){
			ArrayList<Integer> temp = new ArrayList<Integer>();
			for(int j = 0 ; j < UPA.get(0).size(); j++){
					temp.add(j, 0);
			}
			nextConditionalUPA.add(i, new ArrayList<Integer>(temp));
		}
		
		//init currentConsideredTile
		for(int i = 0 ; i < UPA.size(); i++){
			ArrayList<Integer> templist = new ArrayList<Integer>();
			for(int j = 0 ; j < UPA.get(0).size(); j++){
				templist.add(0);
			}
			currentConsideredTile.add(i, new ArrayList<Integer>(templist));
		}
		
		//init reconstructed UPA
		for(int i = 0 ; i < UPA.size() ; i++){
			ArrayList<Integer> temp = new ArrayList<Integer>();
			for(int j = 0 ; j < UPA.get(0).size(); j++){
					temp.add(j, 0);
			}
			reconstructedUPA.add(i, new ArrayList<Integer>(temp));
		}
		
		//output UPA
		//**Print Commands**System.out.println("UPA:");
		for(int i = 0 ; i < UPA.size() ; i++){
			//**Print Commands**System.out.println(UPA.get(i));
		}
		//**Print Commands**System.out.println("============");
		
		for(int i = 0 ; i < UPA.get(0).size() ; i++){
			P.add(0);
		}
	}
	
	//method to check if all permissions in UPA have been covered, if yes add this permission to the set of excluded permissions P
	public static void checkPermissions(){
		
		int i = 0 ;
		int j = 0;
		
		for(i = 0 ; i < UPA.get(0).size() ; i++){
			for(j = (UPA.size() - 1) ; j >= 0 ; j--){
				if(UPA.get(j).get(i) == 1){
					if(upaTilingMatrix.get(j).get(i) == 0){
						break;
					}
				}
			}
			if(j == -1){
				P.set(i, 1);
			}
		}
	}
 
	public Initialization() {
		// TODO Auto-generated constructor stub
		super();
	}
}
