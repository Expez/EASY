/*Copyright (C) 2012 Lars Andersen, Tormund S. Haus.
larsan@stud.ntnu.no
tormunds@stud.ntnu.no

EASY is free software: you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
 
EASY is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.
 
You should have received a copy of the GNU General Public License
    along with EASY.  If not, see <http://www.gnu.org/licenses/>.*/
package edu.ntnu.EASY.neuron;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import edu.ntnu.EASY.Environment;
import edu.ntnu.EASY.Evolution;
import edu.ntnu.EASY.FitnessCalculator;
import edu.ntnu.EASY.incubator.Incubator;
import edu.ntnu.EASY.selection.adult.AdultSelector;
import edu.ntnu.EASY.selection.adult.FullGenerationalReplacement;
import edu.ntnu.EASY.selection.adult.GenerationalMixing;
import edu.ntnu.EASY.selection.adult.Overproduction;
import edu.ntnu.EASY.selection.parent.ParentSelector;
import edu.ntnu.EASY.selection.parent.SigmaScaledSelector;
import edu.ntnu.EASY.selection.parent.StochasticTournamentSelector;
import edu.ntnu.EASY.selection.parent.TournamentSelector;
import edu.ntnu.EASY.util.Util;
import edu.ntnu.plotting.Plot;

public class Neuron {

	Environment env;
	
	public Neuron(){
		env = new Environment();
		env.populationSize = 100;
		env.maxGenerations = 1000;
		env.fitnessThreshold = 2.0;
		env.mutationRate = 0.01;
		env.crossoverRate = 0.01;
		env.numChildren = 100;
		env.numParents = 100;
		env.elitism = 1;
		env.e = 0.6;
		env.rank = 6;
		env.maxAge = 10;
	}
	
	public NeuronReport runNeuronEvolution(double[] target) {
		FitnessCalculator<double[]> fitCalc = new SpikeIntervalFitnessCalculator(target);
		AdultSelector<double[]> adultSelector = new GenerationalMixing<double[]>(env.populationSize, env.maxAge, env.elitism);
		ParentSelector<double[]> parentSelector = new StochasticTournamentSelector<double[]>(env.rank, env.numParents, env.e);
		Incubator<double[], double[]> incubator = new NeuronIncubator(new NeuronReplicator(env.mutationRate,env.crossoverRate), env.numChildren);	
		Evolution<double[],double[]> evo = new Evolution<double[], double[]>(fitCalc, adultSelector, parentSelector, incubator);

		NeuronReport report = new NeuronReport(env.maxGenerations);
		evo.runEvolution(env, report);
		return report;
	}
	
	public static void main(String[] args) throws IOException {
		Neuron neuron = new Neuron();
		double[] target = Util.readTargetSpikeTrain("training/izzy-train3.dat");
		PrintStream ps = new PrintStream(new FileOutputStream("file.out"));
		NeuronReport neuronReport = neuron.runNeuronEvolution(target); 
		neuronReport.writeToStream(ps);
		
		double[] bestPhenome = neuronReport.getBestPhenome();
		Plot.newPlot("Neuron")
			.setAxis("x","ms")
			.setAxis("y","activation")
			.with("bestPhenome",bestPhenome)
			.with("target",target)
			.make().plot();
		
		double[] averageFitness = neuronReport.getAverageFitness();
		double[] bestFitness = neuronReport.getBestFitness();

		Plot.newPlot("Fitness")
			.setAxis("x","generation")
			.setAxis("y","fitness")
			.with("average",averageFitness)
			.with("best",bestFitness)
			.make().plot();
	}
}
