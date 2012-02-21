package edu.ntnu.EASY.neuronModel;

import java.util.LinkedList;
import java.util.List;

import edu.ntnu.EASY.FitnessCalculator;
import edu.ntnu.EASY.Population;

/**
 * Fitness calculator using the Spike train distance measure called spike time distance measure.
 * This distance measure uses the difference between spike times to compare two spiketimes.
 *
 */
public class NeuronFitnessCalcalculatorSTDM implements FitnessCalculator<double[]>{
	
	private double[] target;
	private static final int ACTIVATION_THRESHOLD = 0;

	public NeuronFitnessCalcalculatorSTDM( double[] target ) {
		this.target = target;
	}

	@Override
	public void setPopulation(Population<?, double[]> population) {
		
	}

	@Override
	public double calculate(double[] phenome) {
		List<Integer> spikeTimes = getSpikeTimes(phenome);
		List<Integer> targetSpikeTimes = getSpikeTimes(target);
		return 1 / ( 1 + calculateDistance(spikeTimes, targetSpikeTimes ) - calculatePenalty(spikeTimes.size(), targetSpikeTimes.size(), phenome.length));		
	}
	
	private List<Integer> getSpikeTimes( double[] phenome) {
		List<Integer> spikeTimes = new LinkedList<Integer>();
		//Check for spikes in a window of size k. 
		//Define a single spike to be the maximum value in this window.
		int k = 5;
		for (int i = 0; i < phenome.length - k; i++) {
			int middle = i + 2;
			boolean spike = phenome[middle] >= ACTIVATION_THRESHOLD;
			for(int j = i; spike && j < i + k; j++){
				if(phenome[middle] < phenome[j]){	
					spike = false;
				}		
			}
			if(spike){
				spikeTimes.add(middle);
			}
		}
		return spikeTimes; 
	}
	
	double calculateDistance(List<Integer> spikeList, List<Integer> targetSpikeList) {
		
		//We have to find the shortest list to iterate over.
		int shortestListLength= Math.min(spikeList.size(),targetSpikeList.size());
		
		double sum = 0;
		for (int spikeTime = 0; spikeTime < shortestListLength; spikeTime++) {
			sum += Math.pow( spikeList.get(spikeTime) + targetSpikeList.get(spikeTime), 2);
		}
		sum = Math.pow(sum, 0.5);
		sum /= shortestListLength;
		return sum;
	}
	//TODO: Add this to the fitness calculator.
	//Calculate a penalty proportional to the difference in number of spikes.
	private double calculatePenalty(int spikes1, int spikes2, int L) {
		int N = Math.max(spikes1,spikes2);
		double M = Math.min(spikes1,spikes2);
		if( M == 0) {
			M = 0.001;
		}
		return ((N - M) * L )/ (2.0 * M);
	}
	
	
}
