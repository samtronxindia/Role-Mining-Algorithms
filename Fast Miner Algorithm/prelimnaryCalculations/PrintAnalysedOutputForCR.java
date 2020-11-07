package prelimnaryCalculations;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.TreeSet;


public class PrintAnalysedOutputForCR extends CR_Analysis {

	public static void printUpdatedRatingAndCR() {

		try {
			//PrintWriter updateRatingWriter = new PrintWriter("/home/samir/workspace/fastMinerTwo/src/outputs/UpdatedRatings"/*, "UTF-8"*/);
			//PrintWriter updateC3RWriter = new PrintWriter("/home/samir/workspace/fastMinerTwo/src/outputs/UpdatedC3R"/*, "UTF-8"*/);
			//PrintWriter updateRating3Writer = new PrintWriter("/home/samir/workspace/fastMinerTwo/src/outputs/UpdatedRating3"/*, "UTF-8"*/);
			
			PrintWriter updateCrWriter = new PrintWriter("/home/samir/workspace/fastMinerTwo/src/outputs/UpdatedCR"/*, "UTF-8"*/);
			for(int w = 0 ; w < c2R.size(); w ++){
				for(int pp = 0 ; pp < c2R.get(0).size(); pp++){
					if(c2R.get(w).get(pp) == 1){
						updateCrWriter.print(perms1.get(pp) + ",");
					}
				}
				updateCrWriter.print("\n");
			}
			
			/*int numCoveredRolesCR = 0;
			PrintWriter coveredCR = new PrintWriter("/home/samir/workspace/fastMinerTwo/src/outputs/CoveredCR14000", "UTF-8");
			for(int i = 0 ; i < coveredRoles.size(); i++){
				//System.out.println("Inside");
				if(coveredRoles.get(i) == 1){
					numCoveredRolesCR++;
				}
				if(coveredRoles.get(i) == 0){
					coveredCR.print(rating.get(i) + ";");
					for(int j = 0 ; j < cR.get(0).size(); j++){
						if(cR.get(i).get(j) == 1){
							coveredCR.print(perms1.get(j) + ",");
						}
					}
				}
				coveredCR.print("\n");
			}
			coveredCR.print("Number of Roles Covered which have 14000+ roles assigned: " + numCoveredRolesCR + "\n");
			coveredCR.close();*/
			
			PrintWriter UPAnalysis = new PrintWriter("/home/samir/workspace/fastMinerTwo/src/outputs/UP_Analysis.txt");
			for(int i = 0 ; i < uP.get(0).size(); i++){
				if(eachUpVerticalPermissions.get(i) != 0){
					UPAnalysis.print(perms1.get(i) + ";" + eachUpVerticalPermissions.get(i) + ";" + eachCrVerticalPermissions.get(i) + ";");
					for(int j = 0 ; j < apps1.size(); j++){
						if(uP.get(j).get(i) == 1){
							UPAnalysis.print(apps1.get(j) + "|");
						}
					}
					UPAnalysis.print("\n");
				}
			}
			UPAnalysis.close();
			
			/*for(int w = 0 ; w < commonPermCR.size(); w ++){
				for(int pp = 0 ; pp < commonPermCR.get(0).size(); pp++){
					for(int m = 0 ; m < commonPermCR.get(0).get(0).size() ; m++){
						if(commonPermCR.get(w).get(pp).get(m) == 1){
							permissionCommonalityWriter.print(perms1.get(m) + ",");
						}
					}
					permissionCommonalityWriter.print(";");
				}
				permissionCommonalityWriter.print("\n");
			}*/
			
			//calc role perm rating
			/*double useRoles = 161;
			PrintWriter rolePRatingWriter = new PrintWriter("/home/samir/workspace/fastMinerTwo/src/outputs/RolePRating");
			for(int i = 0 ; i < cR.size(); i++){
				double pRating = 0;
				for(int j = 0 ; j < cR.get(0).size() ; j++){
					if(cR.get(i).get(j) == 1){
						pRating = pRating + ((double)eachCrVerticalPermissions.get(j));
					}
				}
				pRating = (double)pRating / (double)useRoles;
				rolePRatingWriter.println(pRating);
			}
			rolePRatingWriter.close();*/
			
			/*//calc role rating
			//Print using formulea : U((1+commonRoles)/coveredRoles) / #Roles => to give importance of every role
			//double useRoles = 161;
			PrintWriter rolePRatingWriter = new PrintWriter("/home/samir/workspace/fastMinerTwo/src/outputs/RolePRating");
			for(int i = 0 ; i < cR.size(); i++){
				double pRating = 0;
				for(int j = 0 ; j < cR.size() ; j++){
					int commonPerm = 0 ;
					int coveredPerm = 0 ;
					int toSkip = 0;
					for(int k = 0 ; k < cR.get(0).size() ; k++){
						if(cR.get(i).get(k) == 0 && cR.get(j).get(k) == 0){
							toSkip++;
							continue;
						}
						if(cR.get(i).get(k) == cR.get(j).get(k)){
							if(cR.get(i).get(k) == 1){
								commonPerm++;
							}
						}
						if((cR.get(i).get(k) == 1) || (cR.get(j).get(k) == 1)){
								coveredPerm++;
						}
					}
					if(toSkip == cR.get(0).size()){
						continue;
					}
					pRating = pRating + ((double)commonPerm / (double) coveredPerm);
				}
				pRating = (double)pRating / (double)cR.size();
				rolePRatingWriter.println(pRating);
				if((i%1460) == 0){
					System.out.println((i/1460) + "0% complete");
				}
			}
			rolePRatingWriter.close();*/
			
			
			//takes long time to exec: Print common permissions into different files accordingly to number of roles
			/*for(int i = 0 ; i < cR.size(); i++){
				PrintWriter permissionCommonalityWriter = new PrintWriter("/home/samir/workspace/fastMinerTwo/src/outputs/commonPermCR/CommonPermInCRNames" + i + ".txt");
				for(int j = 0 ; j < cR.size() ; j++){
					for(int k = cR.get(0).size() - 1 ; k >= 0 ; k--){
						if(cR.get(i).get(k) == cR.get(j).get(k)){
							if(cR.get(i).get(k) == 1){
								permissionCommonalityWriter.print(perms1.get(k) + ",");
							}
							else{
							}
						}
					}
					permissionCommonalityWriter.print(";");
				}
				permissionCommonalityWriter.print("\n");
				permissionCommonalityWriter.close();
			}*/
			
			/*for(int i = 0 ; i < permissionCommonalityFactor.size(); i++){
				permissionCommonalityWriter.println(perms1.get(i) + ":" + permissionCommonalityFactor.get(i));
			}*/
			
			//print subset commonality of roles
			PrintWriter commonalityWriter = new PrintWriter("/home/samir/workspace/fastMinerTwo/src/outputs/CommonalityFactor");
			for(int i = 0 ; i < commonalityFactor.size(); i++){
				//if(justCommonalityFactor.get(i) == 0){
					commonalityWriter.print(justCommonalityFactor.get(i) + ":");
					commonalityWriter.print(commonalityFactor.get(i) + ":");
					commonalityWriter.print(rating.get(i) + ":");
					for(int pp = 0 ; pp < getcR().get(0).size(); pp++){
						if(getcR().get(i).get(pp) == 1){
							commonalityWriter.print(perms1.get(pp) + ",");
						}
					}
					commonalityWriter.print("\n");
				//}
			}
			commonalityWriter.close();
			 
			/*for(int i = 0 ; i < rating3.size() ; i++){
				updateRatingWriter.println(rating3.get(i));
			}*/
					 
			/*for(int w = 0 ; w < c3R.size(); w ++){
				for(int pp = 0 ; pp < c3R.get(0).size(); pp++){
					if(c3R.get(w).get(pp) == 1){
						updateCrWriter.print(perms1.get(pp) + ",");
					}
				}
				updateCrWriter.print("\n");
			}*/
			 
			/*for(int i = 0 ; i < rating2.size() ; i++){
				updateRatingWriter.println(rating2.get(i));
			}*/
			
			updateCrWriter.close();
			//updateRatingWriter.close();
			//updateC3RWriter.close();
			//updateRating3Writer.close();
			} 
			catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public PrintAnalysedOutputForCR() {
		super();
	}

}