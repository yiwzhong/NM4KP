

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author yiwen zhong
 *
 */
public class Solution implements Comparable<Solution> {

	public Solution(boolean initial) {
		problem = Problems.getProblem();
		match = new int[problem.getItemNumber()];
		if ( initial ) { 
			densityList = problem.getDensityList();
			valueList = problem.getValueList();
			this.randPick(); 
		}
	}

	/**
	 * Use parameter to clone a new Solution
	 * 
	 * @param solution
	 */
	public Solution(Solution solution) {
		problem = Problems.getProblem();
		match = solution.match.clone(); 
		lastImproving = solution.lastImproving;
		isValid = solution.isValid;
		value = solution.value;
		noisedValue = solution.noisedValue;
		weight = solution.weight;
	}
	
	
	/**
	 * Use parameter to update this object
	 * 
	 * @param s
	 */
	public void update(Solution s) {
		for (int i = 0; i <match.length; i++) {
			match[i] = s.match[i];
		}
		lastImproving = s.lastImproving;
		isValid = s.isValid;
		value = s.value;
		noisedValue = s.noisedValue;
		weight = s.weight;
	}

	/*
	 * This method randomly selects items into knapsack
	 */
	private void randPick() {
		for (int i = 0; i < match.length; i++) {
			if (Solution.rand.nextDouble() < 0.5)  {
				match[i] = 1;
			} else {
				match[i] = 0;
			}
		}
		eval();
		repair(Solution.densityList);
		optimization(Solution.densityList);
		this.getNoisedValue();
	}
	
	public void eval() {
		value = 0;
		weight = 0;;
		for (int i=0; i<match.length; i++) {
			if ( match[i] == 1) {
			    value += problem.getItemValue(i);
			    weight += problem.getItemWeight(i);
			} 
		}
		isValid = weight <= problem.getCapacity();
	}
	
	public void setupNoises(double noiseRate) {
		Solution.noises = new double[match.length];
		noisedValue = value;
		for (int i = 0; i < noises.length; i++) {
			//Solution.noises[i] = 2 * noiseRate * (0.5 - Solution.rand.nextDouble());
			Solution.noises[i] = noiseRate * Solution.rand.nextDouble();
			noisedValue += Solution.noises[i];
		}
	}
	
	public double getNoisedValue(double noiseRate) {
		if (noiseRate <= 0) {
			noisedValue = value;
		} else {
			noisedValue = 0;
			for (int i=0; i<match.length; i++) {
				if ( match[i] == 1) {
					noisedValue += problem.getItemValue(i) + Solution.noises[i];
					//noisedValue += problem.getItemValue(i) + noiseRate * Solution.rand.nextDouble();
				    //noisedValue += problem.getItemValue(i) + noiseRate * 2 * ( 0.5 - Solution.rand.nextDouble());
				} 
			}
		}
		return noisedValue;
	}

	/**
	 * repair the solution and calculate the value
	 * 
	 * @return
	 */
	public void repair(List<Integer> itemList) {
		//repair invalid solution
		for(int i = itemList.size()-1; i >= 0 && !isValid; i--) {
			int item = itemList.get(i);
			if (match[item] == 1) {
 			    match[item] = 0;
			    value -= problem.getItemValue(item);
			    weight -= problem.getItemWeight(item);
			    isValid = weight <= problem.getCapacity();
			}
		}
	}
	

	/**
	 * try to add items into knapsack
	 */
	public void optimization(List<Integer> itemList) {
		for (int i = 0; i < itemList.size(); i++) {
			int item = itemList.get(i);
			if (match[item] == 0  && weight + problem.getItemWeight(item) <= problem.getCapacity()) {
				match[item] = 1;
				value += problem.getItemValue(item);
				weight += problem.getItemWeight(item);
			}
		}
	}

	
	/**
	 * Produce a random neighbor
	 * 
	 * @return
	 */
	public Solution randNeighbor(double densityProb) {
		Solution s = new Solution(this);
		s.randomFlip();
		if (Solution.rand.nextDouble() < densityProb) {
			s.repair(Solution.densityList);
			s.optimization(Solution.densityList);
		} else {
			s.repair(Solution.valueList);
			s.optimization(Solution.valueList);
		}
		return s;
	}
	

	private void randomFlip() {
		do {
			int idx = Solution.rand.nextInt(match.length);
			if (match[idx] == 0) {
				match[idx] = 1;
				value += problem.getItemValue(idx);
				weight += problem.getItemWeight(idx);
			} else {
				match[idx] = 0;
				value -= problem.getItemValue(idx);
				weight -= problem.getItemWeight(idx);
			}
		} while (Solution.rand.nextDouble() < 0.0);
		
		isValid = weight <= problem.getCapacity();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Solution s) {
		if ( value > s.value) {
			return 1;
		} else if ( value == s.value) {
			return 0;
		} else {
			return -1;
		}
	}

	public String toString() {
		String str = "";
        str += value + "," + weight + "\r\n";
        for (int i = 0; i < match.length; i++) {
        	str += match[i] + ",";
        }
        str += "\r\n";
        for (int i = 0; i < match.length; i++) {
        	str +=problem.getItemValue(i) + ",";
        }
		return str;
	}

	public int getItemNumber() { return match.length; }
	public List<Integer> getValueList() { return valueList;}
	public double getValue() {return value;}
	public double getNoisedValue() {return noisedValue;}
	public double getWeight() { return weight;}
	public void setLastImproving(int n) { this.lastImproving = n; }
	public int getLastImproving() { return lastImproving;}

	private Problems problem;
	private int[] match;
	private double value;
	private double noisedValue;
	private boolean isValid;
	private double weight;
	private int lastImproving = 0; //
	
    private static List<Integer> densityList;
    private static List<Integer> valueList;
    private static double[] noises = null;

	public static void main(String[] args) {
		String filePath = (new File("")).getAbsolutePath() + "/../f1-10/"; 
		String fileName = filePath+"f1.txt";
		Problems.setFileName(fileName);
		Solution s = new Solution(true);
		double d = 0;
		for (int i = 0; i < 10; i++) {
			s.randPick();
			System.out.println(s.value);
			d += s.value;
		}
		System.out.println("Best known value:" + Problems.getProblem().getBestValue() + ", Random solution:" + d / 10);
	}

	private static Random rand = new Random();
}

