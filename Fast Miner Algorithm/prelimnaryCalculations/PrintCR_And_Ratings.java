package prelimnaryCalculations;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public class PrintCR_And_Ratings extends FewChecks {
	
	/*
	 * public static Set<String> topperms = new HashSet<String>();
	 */
	public static void printRating() {
		
		
		try {
			PrintWriter writer1 = new PrintWriter("C:\\Users\\Samir\\Desktop\\workspace\\fastMinerTwo\\src\\outputs\\ratings"/*, "UTF-8"*/);
			PrintWriter crPermWriter = new PrintWriter("C:\\Users\\Samir\\Desktop\\workspace\\fastMinerTwo\\src\\outputs\\PermCR"/*, "UTF-8"*/);
			PrintWriter numCRPermWriter = new PrintWriter("C:\\Users\\Samir\\Desktop\\workspace\\fastMinerTwo\\src\\outputs\\numberofPermCR"/*, "UTF-8"*/);

			//print rating
			for(int i = 0 ; i < rating.size() ; i++){
				writer1.println(rating.get(i));
			}
						
			for(int i = 0 ; i < eachCrPermissions.size() ; i++){
				numCRPermWriter.println(eachCrPermissions.get(i));
			}
			 
			int counterroles = 0;
			//print perms assoc with each CR
			for(int w = 0 ; w < getcR().size(); w ++){
				for(int pp = 0 ; pp < getcR().get(0).size(); pp++){
					if(getcR().get(w).get(pp) == 1){
						crPermWriter.print(perms1.get(pp) + ",");
					}
				}
				crPermWriter.print("\n");
				counterroles++;
			}
			
			//print sizes of rating
			writer1.println("Size of rating: " + rating.size());
			System.out.println("Size of rating: " + rating.size());
			
			//print CR (0's and 1's)	
			PrintWriter writer2 = new PrintWriter("C:\\Users\\Samir\\Desktop\\workspace\\fastMinerTwo\\src\\outputs\\CandidateRoles");
			writer2.print("\n");
			for(int i = 0 ; i < getcR().size() ; i++){
				for(int j = 0 ; j < getcR().get(0).size(); j++){
					writer2.print(getcR().get(i).get(j) + ",");
				}
				writer2.print("\n");
			}
			//writer2.println("Size of CR: " + cR.size());
			writer2.close();
			
			//end print CR (o's and 1's)
						
			System.out.println("Size of CR: " + getcR().size());
			writer1.close();
			crPermWriter.close();
			numCRPermWriter.close();
			
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public PrintCR_And_Ratings() {
		super();
	}

}