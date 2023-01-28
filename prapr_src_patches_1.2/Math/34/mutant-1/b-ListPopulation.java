/*   0*/package org.apache.commons.math3.genetics;
/*   0*/
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Collection;
/*   0*/import java.util.Collections;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.List;
/*   0*/import org.apache.commons.math3.exception.NotPositiveException;
/*   0*/import org.apache.commons.math3.exception.NullArgumentException;
/*   0*/import org.apache.commons.math3.exception.NumberIsTooLargeException;
/*   0*/import org.apache.commons.math3.exception.NumberIsTooSmallException;
/*   0*/import org.apache.commons.math3.exception.util.LocalizedFormats;
/*   0*/
/*   0*/public abstract class ListPopulation implements Population {
/*   0*/  private List<Chromosome> chromosomes;
/*   0*/  
/*   0*/  private int populationLimit;
/*   0*/  
/*   0*/  public ListPopulation(int populationLimit) {
/*  52*/    this(Collections.emptyList(), populationLimit);
/*   0*/  }
/*   0*/  
/*   0*/  public ListPopulation(List<Chromosome> chromosomes, int populationLimit) {
/*  65*/    if (chromosomes == null)
/*  66*/      throw new NullArgumentException(); 
/*  68*/    if (populationLimit <= 0)
/*  69*/      throw new NotPositiveException(LocalizedFormats.POPULATION_LIMIT_NOT_POSITIVE, populationLimit); 
/*  71*/    if (chromosomes.size() > populationLimit)
/*  72*/      throw new NumberIsTooLargeException(LocalizedFormats.LIST_OF_CHROMOSOMES_BIGGER_THAN_POPULATION_SIZE, chromosomes.size(), populationLimit, false); 
/*  75*/    this.populationLimit = populationLimit;
/*  76*/    this.chromosomes = new ArrayList<Chromosome>(populationLimit);
/*  77*/    this.chromosomes.addAll(chromosomes);
/*   0*/  }
/*   0*/  
/*   0*/  public void setChromosomes(List<Chromosome> chromosomes) {
/*  90*/    if (chromosomes == null)
/*  91*/      throw new NullArgumentException(); 
/*  93*/    if (chromosomes.size() > this.populationLimit)
/*  94*/      throw new NumberIsTooLargeException(LocalizedFormats.LIST_OF_CHROMOSOMES_BIGGER_THAN_POPULATION_SIZE, chromosomes.size(), this.populationLimit, false); 
/*  97*/    this.chromosomes.clear();
/*  98*/    this.chromosomes.addAll(chromosomes);
/*   0*/  }
/*   0*/  
/*   0*/  public void addChromosomes(Collection<Chromosome> chromosomeColl) {
/* 108*/    if (this.chromosomes.size() + chromosomeColl.size() > this.populationLimit)
/* 109*/      throw new NumberIsTooLargeException(LocalizedFormats.LIST_OF_CHROMOSOMES_BIGGER_THAN_POPULATION_SIZE, this.chromosomes.size(), this.populationLimit, false); 
/* 112*/    this.chromosomes.addAll(chromosomeColl);
/*   0*/  }
/*   0*/  
/*   0*/  public List<Chromosome> getChromosomes() {
/* 120*/    return Collections.unmodifiableList(this.chromosomes);
/*   0*/  }
/*   0*/  
/*   0*/  protected List<Chromosome> getChromosomeList() {
/* 128*/    return this.chromosomes;
/*   0*/  }
/*   0*/  
/*   0*/  public void addChromosome(Chromosome chromosome) {
/* 138*/    if (this.chromosomes.size() >= this.populationLimit)
/* 139*/      throw new NumberIsTooLargeException(LocalizedFormats.LIST_OF_CHROMOSOMES_BIGGER_THAN_POPULATION_SIZE, this.chromosomes.size(), this.populationLimit, false); 
/* 142*/    this.chromosomes.add(chromosome);
/*   0*/  }
/*   0*/  
/*   0*/  public Chromosome getFittestChromosome() {
/* 151*/    Chromosome bestChromosome = this.chromosomes.get(0);
/* 152*/    for (Chromosome chromosome : this.chromosomes) {
/* 153*/      if (chromosome.compareTo(bestChromosome) > 0)
/* 155*/        bestChromosome = chromosome; 
/*   0*/    } 
/* 158*/    return bestChromosome;
/*   0*/  }
/*   0*/  
/*   0*/  public int getPopulationLimit() {
/* 166*/    return this.populationLimit;
/*   0*/  }
/*   0*/  
/*   0*/  public void setPopulationLimit(int populationLimit) {
/* 177*/    if (populationLimit <= 0)
/* 178*/      throw new NotPositiveException(LocalizedFormats.POPULATION_LIMIT_NOT_POSITIVE, populationLimit); 
/* 180*/    if (populationLimit < this.chromosomes.size())
/* 181*/      throw new NumberIsTooSmallException(populationLimit, this.chromosomes.size(), true); 
/* 183*/    this.populationLimit = populationLimit;
/*   0*/  }
/*   0*/  
/*   0*/  public int getPopulationSize() {
/* 191*/    return this.chromosomes.size();
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 199*/    return this.chromosomes.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public Iterator<Chromosome> iterator() {
/* 209*/    return getChromosomes().iterator();
/*   0*/  }
/*   0*/}
