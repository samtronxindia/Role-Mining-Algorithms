package prelimnaryCalculations;
import java.util.ArrayList;

public class PhaseTwo extends PhaseOne {

	public static void secondPhase() {
	
	  ArrayList<ArrayList<Integer>> newRole = new ArrayList<ArrayList<Integer>>();
	
			boolean found = false;
	
			gR.add(new ArrayList<Integer>());
			listOfContributors.add(new ArrayList<Integer>());
			count.add(0);
	
			for(int z = 0 ; z < iR.get(0).size(); z++){
				gR.get(0).add(z, 0);
			}
	
			newRole.add(new ArrayList<Integer>());
			//newRole.get(0).add(new ArrayList<Integer>());
	 
			for(int i = 0 ; i < iR.size() ; i++){
	
					for(int j = (i + 1) ; j < iR.size(); j++){
	
						//***System.out.println("i: " + i + " | " + "j: " + j);
	
							int b = 0;	
	
							//NR <-- i intersection j------------------------------------------//
							for(int k = 0 ; k < iR.get(j).size() ; k ++){			                   //
								//***System.out.println("k:" + k + "\n");          																 		//			
									if(iR.get(i).get(k) == 1 && iR.get(j).get(k) == 1){              //
										//***System.out.println("Adding 1");  		                    								//
											newRole.get(0).add(1);       																																		//
									}																							  																																							//
									else{																	  																																									//	
										//***System.out.println("Adding 0");  		                    								//
											newRole.get(0).add(0);																       																		//
									}//end else								   																																											//
							}//end internal for	k																																														//
							//-----------------------------------------------------------------//
							
							//gR.add(newRole);
							//***System.out.println("NewRole: " + newRole);
							//Simple initialization of GR at first execution
							int foundIndex = 0;
				
				
							//if NewRole does not belong to GenRoles then..(found = false)-----//
							for(int a = 0 ; a < gR.size() ; a++){								                      //
									for(b = gR.get(a).size() - 1 ; b >= 0 ; b--){																    //
										//***System.out.println("a: " + a + " b: " + b + "\n");
											if(gR.get(a).get(b) != newRole.get(0).get(b)){                 //
													break;                                                       //
											}                                                              //
									}                                                                //
									if(b == -1){                                                     //
										//***System.out.println("Found!       --------------");
											foundIndex = a;
											found = true;                                                  //
											break;
									}                                                                //
							}																																																																		//
							//-----------------------------------------------------------------//
	
							//***System.out.println("Found: " + found);
							if(found == false){
		
									found = false;							
									//***System.out.println("\n Inside! ++++++++++++++++++\n");
									//***System.out.println(newRole.get(0));
									gR.add(new ArrayList<Integer>(newRole.get(0)));
									count.add(origCount.get(i) + origCount.get(j));
									listOfContributors.add(new ArrayList<Integer>());
									listOfContributors.get(gR.size() - 1).add(i);
									listOfContributors.get(gR.size() - 1).add(j);
									//***System.out.println("LOC: found false" + listOfContributors);
							}
							if(found == true){
									if(!listOfContributors.get(foundIndex).contains(i)){
										listOfContributors.get(foundIndex).add(i);
										count.set(foundIndex, count.get(foundIndex)+ origCount.get(i));
									}
									if (!listOfContributors.get(foundIndex).contains(j)){
										listOfContributors.get(foundIndex).add(j);
										count.set(foundIndex, count.get(foundIndex)+ origCount.get(j));
									}
									//***System.out.println("LOC found true:" + listOfContributors);
							}
							newRole.get(0).clear();		
							found = false;	
							//***System.out.println("GR: " + gR);							
							//System.out.println(gR.size());
					}//end outer for
		 }//end main for
	}//end secondPhase()

	public PhaseTwo() {
		super();
	}

}