package BasicRMP;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.ObjectOutputStream.PutField;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class RunMain extends RMP {

	/**
	 * @param args
	 */
	
	public static Set<String> toppermsbasicrmp = new HashSet<String>();
	
	public static void printRoleInformation(){
		
		System.out.println("Number of roles:" + TilingPermissions.size());
		
		System.out.println("PA:");
		for(int i = 0 ; i < TilingPermissions.size() ; i ++){
		}
		
		System.out.println("UA:");
		for(int i = 0 ; i < TilingUsers.size() ; i ++){
		}
	}
	
	public static void outputUAAndPa(){
		
		try {
			//print PA
			PrintWriter outputPA = new PrintWriter("C:\\Users\\Samir\\Desktop\\workspace\\BasicRMP\\src\\output\\PA.txt");
		
			int rolecounterbasicrmp = 0;
			for(int i = 0 ; i < TilingPermissions.size() ; i ++){
				for(int j = 0 ; j < TilingPermissions.get(0).size() ; j ++){
					if(TilingPermissions.get(i).get(j) == 1){
						outputPA.print(perms1.get(j) + ",");
						toppermsbasicrmp.add(perms1.get(j));
					}
				}
				outputPA.print("\n");
				rolecounterbasicrmp++;
				if(rolecounterbasicrmp % 10 == 0) {
					System.out.println("CounterBRMP:" + rolecounterbasicrmp + ", Unique perms:" + toppermsbasicrmp.size());
				}
			}
			outputPA.close();
			
			//print UA
			PrintWriter outputUA = new PrintWriter("C:\\Users\\Samir\\Desktop\\workspace\\BasicRMP\\src\\output\\UA.txt");
			
			for(int i = 0 ; i < TilingUsers.size() ; i ++){
				for(int j = 0 ; j < TilingUsers.get(0).size() ; j ++){
					if(TilingUsers.get(i).get(j) == 1){
						outputUA.print(apps1.get(j) + ",");
					}
				}
				outputUA.print("\n");
			}
			outputUA.close();
		

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static int compareMatrix(){
		
		//calc the reconstructed matrix UPA^
		for(int i = 0 ; i < TilingUsers.get(0).size() ; i++){//i denotes the column number of UA
			for(int j = 0 ; j < TilingUsers.size() ; j++){//j denotes the row number of UA
				if(TilingUsers.get(j).get(i) == 1){
					for(int k = 0 ; k < TilingPermissions.get(0).size() ; k++){//k denotes the column number of PA
						if(TilingPermissions.get(j).get(k) == 1){//j denotes row number for PA
							reconstructedUPA.get(i).set(k, 1);
						}
					}
				}
			}
		}
		
		//init num perms per app to 0
		for(int i = 0 ; i < reconstructedUPA.size() ; i++){
			numOfPermsPerAppReconstructedUPA.add(i, 0);
		}
		
		int distanceUPA = 0;
		//calc l1 norm between reconstructed UPA and UPA
		for(int i = 0 ; i < UPA.size() ; i++){
			for(int j = 0 ; j < UPA.get(0).size() ; j++){
				distanceUPA = distanceUPA + Math.abs((reconstructedUPA.get(i).get(j)) - (UPA.get(i).get(j)));
			}
		}
		
		try{
			PrintWriter outputReconstructedUPA = new PrintWriter("C:\\Users\\Samir\\Desktop\\workspace\\BasicRMP\\src\\output\\ReconstructedUPA.txt");
				
			for(int i = 0 ; i < reconstructedUPA.size() ; i ++){
				outputReconstructedUPA.println(reconstructedUPA.get(i));
			}
			outputReconstructedUPA.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			PrintWriter outputRoleCount = new PrintWriter("C:\\Users\\Samir\\Desktop\\workspace\\BasicRMP\\src\\output\\RoleCount.txt");
			for(int i = 0 ; i < TilingUsers.size() ; i++){
				int tempvar = 0;
				for(int j = 0 ; j < TilingUsers.get(0).size() ; j++){
					if(TilingUsers.get(i).get(j) == 1){
						tempvar++;
					}
				}
				outputRoleCount.println(tempvar);
			}
			outputRoleCount.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			PrintWriter outputRolePermissionCount = new PrintWriter("C:\\Users\\Samir\\Desktop\\workspace\\BasicRMP\\src\\output\\RoleCount.txt");
			for(int i = 0 ; i < TilingUsers.size() ; i++){
				int tempvar = 0;
				for(int j = 0 ; j < TilingUsers.get(0).size() ; j++){
					if(TilingUsers.get(i).get(j) == 1){
						tempvar++;
					}
				}
				outputRolePermissionCount.println(tempvar);
			}
			outputRolePermissionCount.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return distanceUPA;
	}
	
	public static void main(String[] args) {
		//run data acquisition and gather UPA
		fm();
		displayIr();		
	
		//exec the basic-RMP algorithm
		RMPExecute();
		
		printRoleInformation();
		
		//print UA and PA
		outputUAAndPa();
		
		int norm = compareMatrix();
		
		System.out.println("Distance : " + norm);
		
		System.out.println("Perms size:" + perms1.size());
		System.out.println("Perms " + perms1);
	}
}
