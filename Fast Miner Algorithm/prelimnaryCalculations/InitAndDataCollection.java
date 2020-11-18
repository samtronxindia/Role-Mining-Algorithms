package prelimnaryCalculations;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class InitAndDataCollection {

	protected static ArrayList<ArrayList<Integer>> uP = new ArrayList<ArrayList<Integer>>();
	protected static ArrayList<ArrayList<Integer>> iR = new ArrayList<ArrayList<Integer>>();
	private static ArrayList<ArrayList<Integer>> cR = new ArrayList<ArrayList<Integer>>();
	protected static ArrayList<ArrayList<Integer>> gR = new ArrayList<ArrayList<Integer>>();
	protected static ArrayList<ArrayList<Integer>> c2R = new ArrayList<ArrayList<Integer>>();
	protected static ArrayList<ArrayList<Integer>> c3R = new ArrayList<ArrayList<Integer>>();
	protected static ArrayList<ArrayList<Integer>> listOfContributors = new ArrayList<ArrayList<Integer>>();
	protected static ArrayList<Integer> origCount = new ArrayList<Integer>();
	protected static ArrayList<Integer> count = new ArrayList<Integer>();
	protected static ArrayList<String> perms1 = new ArrayList<String>();
	protected static ArrayList<String> apps1 = new ArrayList<String>();
	protected static ArrayList<ArrayList<Integer>> rating = new ArrayList<ArrayList<Integer>>();
	protected static ArrayList<ArrayList<Integer>> rating2 = new ArrayList<ArrayList<Integer>>();
	protected static ArrayList<ArrayList<Integer>> rating3 = new ArrayList<ArrayList<Integer>>();
	protected static ArrayList<Integer> eachIrPermissions = new ArrayList<Integer>();
	protected static ArrayList<Integer> eachGrPermissions = new ArrayList<Integer>();
	protected static ArrayList<Integer> eachCrPermissions = new ArrayList<Integer>();
	protected static ArrayList<Integer> eachC2RPermissions = new ArrayList<Integer>();
	protected static ArrayList<Double> commonalityFactor = new ArrayList<Double>();
	protected static ArrayList<ArrayList<ArrayList<Integer>>> commonPermCR = new ArrayList<ArrayList<ArrayList<Integer>>>();
	protected static ArrayList<Integer> coveredRoles = new ArrayList<Integer>();

	static boolean init = true;
	protected static int commonElements = 0;

	public static void fm(String fileName) {
			try{
					Scanner input = new Scanner(new File(fileName)/*,"UTF-16le"*/);
					int count = 0;
					if(input.hasNextLine()){
							input.nextLine();
							}//end if
					while(input.hasNextLine())
					{
						count = count + 1;
					    Scanner colReader = new Scanner(input.nextLine()).useDelimiter(",");
					    ArrayList col = new ArrayList();
					    while(colReader.hasNextInt())
	    				{
					        col.add(colReader.nextInt());
					    }//inner while
					    colReader.close();
						uP.add(col);
					}//outer while
					input.close();
			}//end try 
			catch(Exception e){
					e.printStackTrace();
					}//end catch
	}//end fm

	public static void displayIr() {
		try{
				Scanner permReader = new Scanner(new File("C:\\Users\\Samir\\Desktop\\workspace\\fastMinerTwo\\src\\permissionsNew.csv")/*,"UTF-16le"*/).useDelimiter(",");			
				Scanner AppReader = new Scanner(new File("C:\\Users\\Samir\\Desktop\\workspace\\fastMinerTwo\\src\\Applications")/*,"UTF-16le"*/);			

				while(permReader.hasNext())
				{
				    ArrayList temp1 = new ArrayList();
					perms1.add(permReader.next());
				}//end while
				while(AppReader.hasNext())
				{
				    ArrayList temp2 = new ArrayList();
					apps1.add(AppReader.next());
				}//end while
		}//end try
		catch(Exception e){
				e.printStackTrace();
		}
	}//end disaplyIr()

	public InitAndDataCollection() {
		super();
	}

	/**
	 * @return the cR
	 */
	public static ArrayList<ArrayList<Integer>> getcR() {
		return cR;
	}

	/**
	 * @param cR the cR to set
	 */
	public static void setcR(ArrayList<ArrayList<Integer>> cR) {
		InitAndDataCollection.cR = cR;
	}

}