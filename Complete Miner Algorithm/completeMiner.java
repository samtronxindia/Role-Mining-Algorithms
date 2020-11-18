import java.util.*;
import java.lang.*;
import java.io.*;
public class completeMiner extends Thread {	
		static ArrayList<ArrayList<Integer>> uP = new ArrayList<ArrayList<Integer>>();
		static ArrayList<ArrayList<Integer>> iR = new ArrayList<ArrayList<Integer>>();
		static ArrayList<ArrayList<Integer>> cR = new ArrayList<ArrayList<Integer>>();
		static ArrayList<ArrayList<Integer>> gR = new ArrayList<ArrayList<Integer>>();
		static ArrayList<ArrayList<Integer>> listOfContributors = new ArrayList<ArrayList<Integer>>();
		static ArrayList<Integer> origCount = new ArrayList<Integer>();
		static ArrayList<Integer> count = new ArrayList<Integer>();
		static	ArrayList<String> perms1 = new ArrayList<String>();
		static	ArrayList<ArrayList<Integer>> rating = new ArrayList<ArrayList<Integer>>();
		static boolean init = true;
		
		//initialize the UP table in order for initPhase after reading UP from file
		public static void fm(){
				try{
						Scanner input = new Scanner(new File("C:\\Users\\Samir\\Desktop\\workspace\\completeMiner\\src\\combineNew.csv")/*,"UTF-16le"*/);
						
						if(input.hasNextLine()){
								input.nextLine();
								}//end if
						while(input.hasNextLine())
						{
						    Scanner colReader = new Scanner(input.nextLine()).useDelimiter(",");
						    ArrayList col = new ArrayList();
						    while(colReader.hasNextInt())
		    				{
						        col.add(colReader.nextInt());
						    }//inner while
    						uP.add(col);
						}//outer while
					
						input.close();
				}//end try 
				catch(Exception e){
						e.printStackTrace();
						}//end catch
		}//end fm
		
		//perform initPhase calculations which are to form iR and origCount
		public static void initPhase(){
				int i = 0;
				int j = 0;		
				int k = 0;		
				boolean found = false;
				iR.add(uP.get(0));
				origCount.add(0 , 1);
				
				//for loop to check rows - for each user u E U do
				for(i = 1; i < uP.size() ; i++){
						for(j = 0; j < iR.size() ; j ++){
								//for loop to check columns
								for(k = uP.get(i).size() -1 ; k >= 0  ; k--){
										if(uP.get(i).get(k) != iR.get(j).get(k)){
												break;
										}
								}//end for k
								// Increment origCount of (iR)'th element by 1
								if(k == -1){
										found = true;
										origCount.set(j , origCount.get(j) + 1 ); 
								}
						}//end for iR			
						//end for columns - if k = -1 means the element was the same in iR and uP - means increment origCount of that (iR)'th element 
						//if k is not -1 that means loop was broken before completion - means the element is not the same! - so add it to iR

						if(found == false){
								iR.add(uP.get(i));
								origCount.add(1);
						}	
						found = false;
				}//end for uP
		}//end initPhase()
	
		public static void displayIr(){
				try{
						Scanner permReader = new Scanner(new File("/home/samir/workspace/completeMiner/src/permissionsNew.csv")/*,"UTF-16le"*/).useDelimiter(",");			
						
						while(permReader.hasNext())
						{
						    ArrayList temp1 = new ArrayList();
							perms1.add(permReader.next());
						}//end while
				}//end try
				catch(Exception e){
						e.printStackTrace();
				}
		}//end disaplyIr()
			
		//perform secondPhase calculations which are to form gR and count from iR and origCount
		public static void secondPhase(){

		  ArrayList<ArrayList<Integer>> newRole = new ArrayList<ArrayList<Integer>>();

				boolean found = false;

				gR.add(new ArrayList<Integer>());

				for(int z = 0 ; z < iR.get(0).size(); z++){
					gR.get(0).add(z, 0);
				}

				newRole.add(new ArrayList<Integer>());

				for(int i = 0 ; i < iR.size() ; i++){

						for(int j = (i + 1) ; j < iR.size(); j++){
							
								int b = 0;	
								
								//NR <-- i intersection j------------------------------------------
								for(int k = 0 ; k < iR.get(j).size() ; k ++){			                   
										if(iR.get(i).get(k) == 1 && iR.get(j).get(k) == 1){              
												newRole.get(0).add(1);       																																		//
										}																							  																																							//
										else{																	  																																									//	
												newRole.get(0).add(0);																       																		//
										}//end else								   																																											//
								}//end internal for	k																																														//
								//-----------------------------------------------------------------					
								//if NewRole does not belong to GenRoles then..(found = false)-----
								for(int a = 0 ; a < gR.size() ; a++){								                      
										for(b = gR.get(a).size() - 1 ; b >= 0 ; b--){																    //
												if(gR.get(a).get(b) != newRole.get(0).get(b)){                 
														break;                                                       
												}                                                              
										}                                                                
										if(b == -1){                                                     
												found = true;                                                  
												break;
										}                                                                
								}																																																																		//
								if(found == false){
									
										gR.add(new ArrayList<Integer>(newRole.get(0)));
										//4/23/2018//count.add(origCount.get(i) + origCount.get(j));
								}
				
								newRole.get(0).clear();		
								found = false;	
						}//end outer for
						for(int k = 0 ; k < gR.size() ; k++){
							
							int a = 0;
							int b = 0;
							
							//loop for calculating intersection in IR -> i & GR -> k
							for(int l = 0 ; l < gR.get(k).size() ; l++){			                   
									if(iR.get(i).get(l) == 1 && gR.get(k).get(l) == 1){              
											newRole.get(0).add(1);       																																		//
									}																							  																																							//
									else{																	  																																									//	
											newRole.get(0).add(0);																       																		//
									}//end else								   																																											//
							}
														
							//loop for searching GR for a match
							for(a = gR.size() - 1 ; a >= 0 ; a--){								                      
								for(b = gR.get(a).size() - 1 ; b >= 0 ; b--){																    //
										if(gR.get(a).get(b) != newRole.get(0).get(b)){                 
												break;                                                       
										}                                                              
								}                                                                
								if(b == -1){                                                     
										found = true;                                                  
										break;
								}                                                                
							}
							
							//match not found, add new element to GR
							if(found == false){
								gR.add(new ArrayList<Integer>(newRole.get(0)));
							}
							
							//resetting the check variables and intersection variable
							found = false;
							newRole.get(0).clear();
						}
			 }//end main for
		}//end secondPhase()
		
		public static void thirdPhase(){
			
			boolean properSubset = false;
			int i = 0;
			int j = 0;
			int k = 0;

			for(i = 0 ; i < gR.size() ; i++){
				for(j = 0 ; j < iR.size(); j++){
					for(k = iR.get(0).size() -1  ; k >= 0  ; k--){
						if(gR.get(i).get(k) != iR.get(j).get(k)){
							if(gR.get(i).get(k) == 1){
								break;
							}
							if(iR.get(j).get(k) == 1){
								properSubset = true;
							}
						}
					}
					if(properSubset == true && k == -1){
						if(i > (count.size() -1) ){
							count.add(i , origCount.get(j));
						}
						else{
							count.set(i , count.get(i) + origCount.get(j));
						}
					}
					properSubset = false;
				}
			}
		}
		
		public static void calcRating(int maxPriority){
			
			int i = 0;
			int j = 0;
			int k = 0;
	
			for(i = 0 ; i < iR.size(); i++){
				for(j = 0 ; j < gR.size() ; j++){
					for(k = (gR.get(0).size() - 1) ; k >= 0 ; k--){
						if(iR.get(i).get(k) != gR.get(j).get(k)){
							break;
						}
					}
					if(k == -1){
						cR.add(new ArrayList<Integer>(iR.get(i)));
						ArrayList<Integer> tempCR = new ArrayList<Integer>(); 
						int priority = 10;
						while(priority <= maxPriority){
							priority = priority + 10;
							tempCR.add(origCount.get(i)*priority + count.get(j));
						}
						rating.add(new ArrayList<Integer>(tempCR));
						iR.remove(i);
						origCount.remove(i);
						gR.remove(j);
						count.remove(j);
						break;
					}
				}
			}
			for(i = 0 ; i < iR.size(); i++){
					cR.add(new ArrayList<Integer>(iR.get(i)));
					ArrayList<Integer> tempCR = new ArrayList<Integer>(); 
					int priority = 10;
					while(priority <= maxPriority){
						priority = priority + 10;
						tempCR.add(origCount.get(i)*priority);
					}
					rating.add(new ArrayList<Integer>(tempCR));
			}
			for(j = 0 ; j < gR.size(); j++){
				cR.add(new ArrayList<Integer>(gR.get(j)));
				ArrayList<Integer> tempCR = new ArrayList<Integer>(); 
				int priority = 10;
				while(priority <= maxPriority){
					priority = priority + 10;
					tempCR.add(count.get(j));
				}
				rating.add(new ArrayList<Integer>(tempCR));
			}
			
		}
		
		public static void checkIr(){
			
			int w = 0 ;
			int x = 0 ;
			int y = 0 ;
			
			for(w = 0 ; w < iR.size(); w++){
				for(x = 0 ; x < iR.size() ; x++){
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
		
		public static void checkGr(){
			
			int w = 0 ;
			int x = 0 ;
			int y = 0 ;
			
			for(w = 0 ; w < gR.size(); w++){
				for(x = 0 ; x < gR.size() ; x++){
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
		
		public static void checkCr(){
			
			int w = 0 ;
			int x = 0 ;
			int y = 0 ;
			
			for(w = 0 ; w < cR.size(); w++){
				for(x = 0 ; x < cR.size() ; x++){
					for(y = cR.get(0).size() -1 ; y >= 0 ; y--){
						if(cR.get(w).get(y) != cR.get(x).get(y)){
							break;
						}
					}
					if(y == -1){
						System.out.println("Problem in CR!");
					}
				}
			}
			
		}

		public static void main(String[] args){
				fm();
				System.out.println("UP SIZE:" + uP.size() + "\n");
				initPhase();
				System.out.println("IR SIZE:" + iR.size() + "\n" );
				secondPhase();
				System.out.println("GR SIZE:" + gR.size() + "\n" );
				thirdPhase();
				displayIr();
				checkIr();
				checkGr();
				checkCr();
				System.out.println("1st GR" + gR.get(0) + "\n" );
				System.out.println("Perms size" + perms1.size() + "\n" );
				System.out.println("Orig count:" + origCount + "\n");
				System.out.println("Orig count size:" + origCount.size() + "\n");
				System.out.println("Count Size:" + count.size() + "\n");
		
				ArrayList<Integer> verticalCount = new ArrayList<Integer>();
				ArrayList<Integer> verticalCountIr = new ArrayList<Integer>();
				ArrayList<Integer> verticalCountGr = new ArrayList<Integer>();
				ArrayList<Integer> horizontalCount = new ArrayList<Integer>();
				
				try {
					for(int w = 0 ; w < uP.get(0).size(); w++){
						int tempcounting = 0;
						for(int pp = 0 ; pp < uP.size() ; pp++){
							tempcounting = tempcounting + uP.get(pp).get(w);
						}
						verticalCount.add(w, tempcounting);
					}
					for(int w = 0 ; w < iR.get(0).size(); w++){
						int tempcounting = 0;
						for(int pp = 0 ; pp < iR.size() ; pp++){
							tempcounting = tempcounting + iR.get(pp).get(w);
						}
						verticalCountIr.add(w, tempcounting);
					}
					for(int w = 0 ; w < gR.get(0).size(); w++){
						int tempcounting = 0;
						for(int pp = 0 ; pp < gR.size() ; pp++){
							tempcounting = tempcounting + gR.get(pp).get(w);
						}
						verticalCountGr.add(w, tempcounting);
					}
					for(int w = 0 ; w < uP.size(); w++){
						int tempcounting = 0;
						for(int pp = 0 ; pp < uP.get(0).size() ; pp++){
							tempcounting = tempcounting + uP.get(w).get(pp);
						}
						horizontalCount.add(w, tempcounting);
					}
					
					PrintWriter writer = new PrintWriter("/home/samir/workspace/completeMiner/src/outputs/output"/*, "UTF-8"*/);
					PrintWriter irPermWriter = new PrintWriter("/home/samir/workspace/completeMiner/src/outputs/IRPerms"/*, "UTF-8"*/);
					PrintWriter grPermWriter = new PrintWriter("/home/samir/workspace/completeMiner/src/outputs/GRPerms"/*, "UTF-8"*/);

					for(int w = 0 ; w < iR.size(); w++){
						irPermWriter.print("\n");
						for(int pp = 0 ; pp < iR.get(0).size(); pp++){
							if(iR.get(w).get(pp) == 1){
								irPermWriter.print(perms1.get(pp) + ",");
							}
						}
					}
					
					for(int w = 0 ; w < gR.size(); w++){
						grPermWriter.print("\n");
						for(int pp = 0 ; pp < gR.get(0).size(); pp++){
							if(gR.get(w).get(pp) == 1){
								grPermWriter.print(perms1.get(pp) + ",");
							}
						}
					}

					writer.println("Perm distribution: " + verticalCount);
					writer.println("Total number of Perms: " + verticalCount.size());
					writer.println("App distribution: " + horizontalCount);
					writer.println("Total number of Apps: " + horizontalCount.size());
					int appsWithZeroPerms= 0;
					int appsWithNonZeroPerms = 0;
					for(int w = 0 ; w < verticalCount.size(); w++){
						if(verticalCount.get(w) != 0){
							appsWithNonZeroPerms++;
						}
					}
					int irPermUsage= 0;
					for(int w = 0 ; w < verticalCountIr.size(); w++){
						if(verticalCountIr.get(w) != 0){
							irPermUsage++;
						}
					}
					int grPermUsage= 0;
					for(int w = 0 ; w < verticalCountGr.size(); w++){
						if(verticalCountGr.get(w) != 0){
							grPermUsage++;
						}
					}
					for(int w = 0 ; w < verticalCount.size(); w++){
						if(verticalCount.get(w) == 0){
							appsWithZeroPerms++;
						}
					}
					
					ArrayList<Integer> eachIrPermissions = new ArrayList<Integer>();
					ArrayList<Integer> eachGrPermissions = new ArrayList<Integer>();
					
					//2 loops for getting number of perms assoc. w/each IR/GR
					for(int w = 0 ; w < iR.size(); w ++){
						int eachIrPermissionsTemp = 0;
						for(int pp = 0 ; pp < iR.get(0).size(); pp++){
							if(iR.get(w).get(pp) == 1){
								eachIrPermissionsTemp++;
							}
						}
						eachIrPermissions.add(w , eachIrPermissionsTemp);
					}
					
					for(int w = 0 ; w < gR.size(); w ++){
						int eachGrPermissionsTemp = 0;
						for(int pp = 0 ; pp < gR.get(0).size(); pp++){
							if(gR.get(w).get(pp) == 1){
								eachGrPermissionsTemp++;
							}
						}
						eachGrPermissions.add(w , eachGrPermissionsTemp);
					}
					
					writer.println("\n Used permissions/Total Permissions: " + appsWithNonZeroPerms + "/" + (appsWithNonZeroPerms + appsWithZeroPerms));
					writer.println("\n Used permissions/Total Permissions: (IR) " + irPermUsage + "/" + (appsWithNonZeroPerms + appsWithZeroPerms));
					writer.println("\n Used permissions/Total Permissions: (GR) " + grPermUsage + "/" + (appsWithNonZeroPerms + appsWithZeroPerms));
					writer.println("\n Number of Perms associated with each IR: " + eachIrPermissions + "\n");
										
					writer.println("Orig count:" + origCount + "\n");
					writer.println("Orig count size:" + origCount.size() + "\n");
					writer.println("\n Number of Perms associated with each GR: " + eachGrPermissions);
					writer.println("Count:" + count + "\n");
					writer.println("GR SIZE:" + gR.size() + "\n" );
					writer.println("1st GR" + gR.get(0) + "\n" );
					writer.println("Count Size:" + count.size() + "\n");
					writer.close();
					irPermWriter.close();
					grPermWriter.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				calcRating(100);
				try {
					PrintWriter writer1 = new PrintWriter("/home/samir/workspace/completeMiner/src/outputs/ratings"/*, "UTF-8"*/);
					PrintWriter crNumPermWriter = new PrintWriter("/home/samir/workspace/completeMiner/src/outputs/NumPermCR"/*, "UTF-8"*/);
					PrintWriter crPermWriter = new PrintWriter("/home/samir/workspace/completeMiner/src/outputs/PermCR"/*, "UTF-8"*/);
					
					for(int i = 0 ; i < rating.size() ; i++){
						writer1.println(rating.get(i));
					}
					writer1.println("Size of rating: " + rating.size());
					System.out.println("Size of rating: " + rating.size());

					System.out.println("Size of CR: " + cR.size());
					writer1.close();
					crNumPermWriter.close();
					crPermWriter.close();
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				
		}//end main
}//end class