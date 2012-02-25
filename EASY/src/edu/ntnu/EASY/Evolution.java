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
package edu.ntnu.EASY;

import edu.ntnu.EASY.incubator.Incubator;
import edu.ntnu.EASY.individual.Individual;
import edu.ntnu.EASY.selection.adult.AdultSelector;
import edu.ntnu.EASY.selection.parent.ParentSelector;

public class Evolution<GType, PType> {

	FitnessCalculator<PType> fitCalc;
	AdultSelector<PType> adultSelector;
	ParentSelector<PType> parentSelector;
	Incubator<GType, PType> incubator;	

	
	public Evolution(FitnessCalculator<PType> fitCalc, AdultSelector<PType> adultSelector, ParentSelector<PType> parentSelector, Incubator<GType,PType> incubator){
		this.fitCalc = fitCalc;
		this.adultSelector = adultSelector;
		this.parentSelector = parentSelector;
		this.incubator = incubator;
	}
	
	public void runEvolution(Environment env, Report<GType, PType> report){
		
		Population<GType,PType> population = new Population<GType,PType>(fitCalc);
		for(int i = 0; i < env.populationSize; i++){
			Individual<GType,PType> ind = incubator.randomIndividual();
			ind.growUp();
			population.add(ind);
		}
		
		Population<GType,PType> children = new Population<GType,PType>(fitCalc);
		Population<GType,PType> parents = new Population<GType,PType>(fitCalc);
		
		double maxFitness = 0.0;
		for(int generation = 1; generation <= env.maxGenerations && maxFitness < env.fitnessThreshold; generation++){
			population.updateFitness();
			
			report.log(generation,population);
			
			parents = parentSelector.select(population);

			children = incubator.makeChildren(parents);
			for(Individual<GType,PType> child : children){
				child.growUp();
			}
			population = adultSelector.select(population,children);
		}
	}
}
