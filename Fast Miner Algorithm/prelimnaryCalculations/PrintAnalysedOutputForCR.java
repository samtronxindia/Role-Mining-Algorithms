package prelimnaryCalculations;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.TreeSet;

public class PrintAnalysedOutputForCR extends PrintCR_And_Ratings {

	public static void printUpdatedRatingAndCR() {

		try {

			PrintWriter updateCrWriter = new PrintWriter("C:\\Users\\Samir\\Desktop\\workspace\\fastMinerTwo\\src\\outputs\\UpdatedCR"/*, "UTF-8"*/);
			for(int w = 0 ; w < c2R.size(); w ++){
				for(int pp = 0 ; pp < c2R.get(0).size(); pp++){
					if(c2R.get(w).get(pp) == 1){
						updateCrWriter.print(perms1.get(pp) + ",");
					}
				}
				updateCrWriter.print("\n");
			}
			
			updateCrWriter.close();
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