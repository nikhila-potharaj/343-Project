/**
 * TCSS 343A HW5
 * @author Wing-Sea Poon, Jude Guo, Nikhila Potharaj, Taylor Gorman
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class tcss343 {
	public static final String OUTPUT_FILE1 = "100x100.txt";
	public static final String OUTPUT_FILE2 = "200x200.txt";
	public static final String OUTPUT_FILE3 = "400x400.txt";
	public static final String OUTPUT_FILE4 = "600x600.txt";
	public static final String OUTPUT_FILE5 = "800x800.txt";
	
	public static final int INFINITY = Integer.MAX_VALUE;
	public static final int MAX_COST = 9;
	
	public static List<ArrayList<Integer>> bruteForcePath;
	public static int bruteForceMinCost = INFINITY;
	
	public static void main(String[] args) throws FileNotFoundException {
		// get R[][] from the input file
		generateCostTable(4, "input.txt");
		Scanner input = new Scanner(new File("input.txt"));
		List<List<Integer>> R = getR(input);
		input.close();
		
		bruteForcePath = new ArrayList<ArrayList<Integer>>();
		bruteForceMin(R);
		
		//Display the solution (Testing Purposes Only)
		System.out.println("Min: " + bruteForceMinCost);
		for(ArrayList<Integer> coordinate : bruteForcePath) {
			System.out.println(coordinate.get(0) + " to " + coordinate.get(1));
		}
		
		//System.out.println(dynamicProgrammingMin(R));
		
		
		
//		generateCostTable(100, OUTPUT_FILE1);
//		generateCostTable(200, OUTPUT_FILE2);
//		generateCostTable(400, OUTPUT_FILE3);
//		generateCostTable(600, OUTPUT_FILE4);
//		generateCostTable(800, OUTPUT_FILE5);
	}
	
	/**
	 * Get R[][] by parsing the input file.
	 * @param input A Scanner of the input file.
	 * @return R[][] The cost matrix
	 * @author Wing-Sea Poon
	 */
	public static List<List<Integer>> getR(Scanner input) {
		List<List<Integer>> R = new ArrayList<List<Integer>>();
		
		int i = 0;
		while(input.hasNextLine()) {
			R.add(new ArrayList<Integer>());
			String line = input.nextLine();
			Scanner lineScanner = new Scanner(line);
			
			while(lineScanner.hasNext()) {
				String token = lineScanner.next();
				if(token.equalsIgnoreCase("NA")) {
					R.get(i).add(INFINITY);
				}
				else {
					R.get(i).add(Integer.parseInt(token));
				}
			}
			
			i++;
			lineScanner.close();
		}
		
		return R;
	}
	
	public static int min(int x, int y) {
		if(x < y) return x;
		return y;
	}
	
	
	
	
	/*=============*
	 * Brute Force *
	 *=============*/
	
	
	/*
	 * bruteForceMin() updates Class Variables for the minimum cost and its associated path.
	 * The reason for this design choice was because the amount of parameters to be passed to bruteForceStep()
	 * was already at the high number of 5. I felt that increasing this to 7 would just decrease readability
	 * without actually increasing efficiency.
	 */
	
	
	/*
	 * While bruteForceMin() takes a recursive approach, it is still an example of a Brute Force algorithm.
	 * It sequentially checks every single possibility and makes zero comparisons between those possibilities.
	 * Instead, the possibility is kept isolated and only compared to the current solution.
	 */
	
	
	/**
	 * For a given 2D-array of canoe rental prices for various trading posts, set bruteForceMinValue
	 * to the minimal possible cost of going downstream, and bruteForcePath to the corresponding path of rentals.<p>
	 * 
	 * In particular, do so using a Brute Force approach, where all possibilities are tested
	 * before the minimum value is found.
	 * @author Jude Guo and Taylor Gorman
	 */
	public static void bruteForceMin(List<List<Integer>> costArray) {
		int numTradingPosts = costArray.size();
		int startingPoint = 0;
		
		//For every trading post, rent a canoe from the starting point and travel to it.
		//Then, recursively check all remaining paths that can be derived following that first rental.
		for(int firstStop = 1; firstStop < numTradingPosts; firstStop++) {
			bruteForceStep(startingPoint, firstStop, 0, new ArrayList<ArrayList<Integer>>(), costArray);
		}		
		return;
	}
	
	
	/*
	 * Update the cost and path, and then recursively check all remaining paths that can be derived
	 * from the current path. Once we reach the destination, check if the global min is beaten, and if so, update it.
	 */
	private static void bruteForceStep(int thePrevStop, int theCurrentStop, int theCurrentCost,
			ArrayList<ArrayList<Integer>> theCurrentPath, List<List<Integer>> theCostArray) {
		
		ArrayList<ArrayList<Integer>> pathInstance = new ArrayList<ArrayList<Integer>>();
		pathInstance.addAll(theCurrentPath);
		
		//Update our cost based on the rental fee to get from the previous stop to the current one.
		theCurrentCost += theCostArray.get(thePrevStop).get(theCurrentStop);
		
		//Update our rental path with the location that we have stopped at.
		ArrayList<Integer> currentStopLocation = new ArrayList<Integer>();
		currentStopLocation.add(thePrevStop);
		currentStopLocation.add(theCurrentStop);		
		pathInstance.add(currentStopLocation);
		
		if(theCurrentStop == theCostArray.size() - 1) {
			//We have reached our destination.
			
			if(theCurrentCost < bruteForceMinCost) {
				//Check to see if the current rental cost is lower than the global minimum.
				//If so, update the global minimum, along with the attached path.
				
				bruteForceMinCost = theCurrentCost;
				bruteForcePath = pathInstance;
			}
			

		} else {
			//We have not reached our destination.
			//Recursively check all possible paths that derive from the current Path.
			for(int nextStop = theCurrentStop + 1; nextStop < theCostArray.size(); nextStop++) {
				bruteForceStep(theCurrentStop, nextStop, theCurrentCost, pathInstance, theCostArray);
			}
		}	
		return;
	}
	
	
	
	/*====================*
	 * Divide and Conquer *
	 *====================*/
	
	/**
	 * @author Nikhila Potharaj
	 */
	public static int recursiveMin(List<List<Integer>> R) {
		return -1;
	}
	
	/**
	 * @author Nikhila Potharaj
	 */
	public static List<Integer> recursiveRecover(List<List<Integer>> R) {
		return null;
	}
	
	/**
	 * @author Wing-Sea Poon
	 */
	public static int dynamicProgrammingMin(List<List<Integer>> R) {
		if(R.isEmpty()) {
			return 0;
		}
		
		// declare
		List<Integer> result = new ArrayList<Integer>();
		int n = R.size();
		
		// initialize (BC)
		if(n >= 1) {
			result.add(0);
		}
		if(n >= 2) {
			result.add(R.get(0).get(1));
		}
		
		// iterate (RC)
		for(int row = 3; row <= n; row++) {
			int lastColMin = INFINITY;
			for(int k = 0; k < row - 2; k++) {
				int thisColValue = R.get(0).get(k) + R.get(k).get(row - 1);
				lastColMin = min(thisColValue, lastColMin);
			}
			
			int curr = R.get(row - 2).get(row - 1);
			int prevSolutionPlusCurr = result.get(row - 2) + curr;
			int min = min(prevSolutionPlusCurr, lastColMin);
			result.add(min);
		}
		
		return result.get(n - 1);
	}
	
	/**
	 * @author Wing-Sea Poon
	 */
	public static List<Integer> dynamicProgrammingRecover(List<List<Integer>> R) {
		return null;
	}
	
	/**
	 * Creates an n x n matrix of costs.
	 * @param n The dimension of the square matrix.
	 * @param fileName The name of the File to write to
	 * @throws FileNotFoundException
	 * @author Wing-Sea Poon
	 */
	public static void generateCostTable(int n, String fileName) 
	throws FileNotFoundException {
		PrintStream output = new PrintStream(new File(fileName));
		
		for(int i = 0; i < n; i++) {
			System.out.println("");
			for(int j = 0; j < n; j++) {
				if(i > j) {
					System.out.print("NA" + " ");
					output.print("NA\t");
				}
				else if(i == j) {
					System.out.print("0" + "  ");
					output.print(0 + "\t");
				}
				else {
					// randCost between 1 inclusive and MAX_COST inclusive
					int randCost = (new Random().nextInt(MAX_COST)) + 1; 
					System.out.print(randCost + "  ");
					output.print(randCost + "\t");
				}
			}
			output.println();
		}
		System.out.println("\n");
		output.close();
	}
}