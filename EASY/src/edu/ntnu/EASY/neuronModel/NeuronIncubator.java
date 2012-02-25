package edu.ntnu.EASY.neuronModel;

import edu.ntnu.EASY.blotto.AbstractIncubator;
import edu.ntnu.EASY.incubator.Replicator;
import edu.ntnu.EASY.individual.Individual;

public class NeuronIncubator extends AbstractIncubator<double[], double[]> {

	public NeuronIncubator(Replicator<double[]> replicator, int numChildren) {
		super(replicator, numChildren);
	}

	@Override
	public Individual<double[], double[]> randomIndividual() {
		return new NeuronIndividual(replicator.randomGenome());
	}

	@Override
	protected Individual<double[], double[]> makeChild(
			Individual<double[], double[]> mom,
			Individual<double[], double[]> dad) {
		double[] childsGenome;
		childsGenome = replicator.combine(mom.getGenome(), dad.getGenome());
		childsGenome = replicator.mutate(childsGenome);
		NeuronIndividual child = new NeuronIndividual(childsGenome);
		return child;
	}

}
