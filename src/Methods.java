
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Random;


/**
 * 
 * @author yiwen zhong
 *
 */
public class Methods {
	
	public static Solution noisingObjectiveFunction(Solution solution) {
		Solution current = new Solution(solution);
		Solution best = new Solution(solution);
		final int MAX_G = Simulations.MAX_GENERATION; //MAXIMUM GENERATION
		int trials = Problems.getProblem().getItemNumber() / Simulations.MARKOV_CHAIN_FACTOR;
		final int SCHEDULE_LENGTH = Math.max(200, trials);
		
		double maxValue = Problems.getProblem().getItemValue(Problems.getProblem().getValueOrder()[0]);
		double minValue = 0;//Problems.getProblem().getItemValue(Problems.getProblem().getValueOrder()[Problems.getProblem().getValueOrder().length-1]);
        double noiseRate = maxValue; //(Simulations.saType == EMethodType.TA)? maxValue / 2 : maxValue;
        double stepOfNoiseRate = (maxValue - minValue) / (MAX_G-1);
		double[] temperatures = new double[MAX_G];
		double[] values = new double[MAX_G];
		double[] bestvalues = new double[MAX_G];
		double densityProb = Simulations.densityProbFrom;
		double step = (Simulations.densityProbTo - Simulations.densityProbFrom) / (MAX_G-1);
	    double alpha = 0.99;//0.99
 		for (int q = 0; q < MAX_G; q++) {
			//System.out.println(t);
			temperatures[q] = noiseRate;
			for (int k = 0; k < SCHEDULE_LENGTH; k++) {
				Solution neighbor = current.randNeighbor(densityProb);
				double d = neighbor.getValue() - current.getValue(); //variation of objective function
				double threshold = noiseRate;
				if (Simulations.saType == EMethodType.NM_NOISING_OBJECTIVE_FUNCTION) {
					threshold = noiseRate * Methods.rand.nextDouble(); 
				} else if (Simulations.saType == EMethodType.TA) {
					threshold = noiseRate;
				}
				if ( d + threshold > 0) { //noised variation
					//accept
					current = neighbor;
					if (current.getValue() > best.getValue()) {
						best.update(current);
						best.setLastImproving(q);
					} 
				}
				values[q] += current.getValue();
			}
			values[q] /= SCHEDULE_LENGTH;
			bestvalues[q] = best.getValue();
			densityProb += step;
			if (Simulations.decreaseType == EDecreaseType.ARITHEMETIC) {
				noiseRate -= stepOfNoiseRate;
			} else {
				noiseRate *= alpha;
			}
			
			if (best.getValue() >= Problems.getProblem().getBestValue()) {
				break;
			}
		}

		if (Simulations.isSavingProcessData()) Methods.saveConvergenceData(temperatures, values, bestvalues, Problems.getProblem().getBestValue());
		return best;
	}
	
	public static Solution noisingData(Solution solution) {
		Solution current = new Solution(solution);
		Solution best = new Solution(solution);
		final int MAX_G = Simulations.MAX_GENERATION; //MAXIMUM GENERATION
		int trials = Problems.getProblem().getItemNumber() / Simulations.MARKOV_CHAIN_FACTOR;
		final int SCHEDULE_LENGTH = Math.max(200, trials);
		
		double maxValue = Problems.getProblem().getItemValue(Problems.getProblem().getValueOrder()[0]) / Problems.getProblem().getItemNumber();
		double minValue = 0;//Problems.getProblem().getItemValue(Problems.getProblem().getValueOrder()[solution.getItemNumber()-1]);
        double noiseRate = maxValue;
        double stepOfNoiseRate = (maxValue - minValue) / (MAX_G - 1);
		double[] temperatures = new double[MAX_G];
		double[] values = new double[MAX_G];
		double[] bestvalues = new double[MAX_G];
		double densityProb = Simulations.densityProbFrom;
		double step = (Simulations.densityProbTo - Simulations.densityProbFrom) / (MAX_G-1);
        double alpha = 0.99;//0.99
 		for (int q = 0; q < MAX_G; q++) {
			//System.out.println(t);
			temperatures[q] = noiseRate;
			current.setupNoises(noiseRate);
			for (int k = 0; k < SCHEDULE_LENGTH; k++) {
				Solution neighbor = current.randNeighbor(densityProb);
				double d = neighbor.getNoisedValue(noiseRate) - current.getValue();

				if ( d > 0) { //noised variation
					//accept
					current = neighbor;
					if (current.getValue() > best.getValue()) {
						best.update(current);
						best.setLastImproving(q);
					} 
				}
				values[q] += current.getValue();
			}
			values[q] /= SCHEDULE_LENGTH;
			bestvalues[q] = best.getValue();
			densityProb += step;
			if (Simulations.decreaseType == EDecreaseType.ARITHEMETIC) {
				noiseRate -= stepOfNoiseRate;
			} else {
				noiseRate *= alpha;
			}
			
			if (best.getValue() >= Problems.getProblem().getBestValue()) {
				break;
			}
		}

		if (Simulations.isSavingProcessData()) Methods.saveConvergenceData(temperatures, values, bestvalues, Problems.getProblem().getBestValue());
		return best;
	}


	private static void saveConvergenceData( double[] ts, double[] vs, double[] bs, double bestValue) {
		try {
			String f = Problems.fileName;
			File file = new File(f);
			f = (new File("")).getAbsolutePath() + "\\results\\Convergence\\" + file.getName();
			f += " " + Simulations.getParaSetting() + " for KP results.csv";
			System.out.println(f);
			PrintWriter printWriter = new PrintWriter(new FileWriter(f));
			for (int idx=0; idx<ts.length; idx++) {
				printWriter.println(ts[idx] + "," + vs[idx] + "," + bs[idx] + "," + (vs[idx] - bestValue) + "," + ( (bs[idx] - bestValue)));
			}
			printWriter.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private static Random rand = new Random();
}
