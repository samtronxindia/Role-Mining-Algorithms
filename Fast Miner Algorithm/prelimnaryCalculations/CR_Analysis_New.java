package prelimnaryCalculations;

public class CR_Analysis_New extends PrintCR_And_Ratings {
		
	public static void CR_Analysis(){
				
		for(int i = 0 ; i < getcR().size() ; i++){
			coveredRoles.add(0);
		}
		for(int i = 0 ; i < getcR().get(0).size() ; i++){
			if(eachCrVerticalPermissions.get(i) > 14000){
				for(int j = 0 ; j < getcR().size() ; j++){
					if(getcR().get(j).get(i) == 1 ){
						coveredRoles.set(j , 1);
					}
				}
			}
		}		
	}
		
	public CR_Analysis_New() {
		super();
	}

}
