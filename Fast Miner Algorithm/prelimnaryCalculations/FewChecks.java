package prelimnaryCalculations;
public class FewChecks extends CalcCandidateRolesAndRatings {

	public static void checkIr() {
		
		int w = 0 ;
		int x = 0 ;
		int y = 0 ;
		
		for(w = 0 ; w < iR.size(); w++){
			for(x = 0 ; x < iR.size() ; x++){
				if(w == x){
					continue;
				}
				for(y = iR.get(0).size() -1 ; y >= 0 ; y--){
					if(iR.get(w).get(y) != iR.get(x).get(y)){
						break;
					}
				}
				if(y == -1){
					System.out.println("Problem in IR!");
				}
			}
		}
		
	}
 
	public static void checkGr() {
		
		int w = 0 ;
		int x = 0 ;
		int y = 0 ;
		
		for(w = 0 ; w < gR.size(); w++){
			for(x = 0 ; x < gR.size() ; x++){
				if(w == x){
					continue;
				}
				for(y = gR.get(0).size() -1 ; y >= 0 ; y--){
					if(gR.get(w).get(y) != gR.get(x).get(y)){
						break;
					}
				}
				if(y == -1){
					System.out.println("Problem in GR!");
				}
			}
		}
		
	}

	public static void checkCr() {
		
		int w = 0 ;
		int x = 0 ;
		int y = 0 ;
		
		for(w = 0 ; w < getcR().size(); w++){
			for(x = 0 ; x < getcR().size() ; x++){
				if(w == x){
					continue;
				}
				for(y = getcR().get(0).size() -1 ; y >= 0 ; y--){
					if(getcR().get(w).get(y) != getcR().get(x).get(y)){
						break;
					}
				}
				if(y == -1){
					System.out.println("Problem in CR!");
				}
			}
		}
		
	}

	public FewChecks() {
		super();
	}

}