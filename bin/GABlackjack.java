/**
	Genetic Algorithm for Blackjack 
	Jackson Black
	By: Ashley Scappaticci and Hayden Ross
	Date: May 2019

 Sampled code from: Robby the cleaning robot
 Author: J. Sumner
 */

// Import packages
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.lang.Math;

public class GABlackjack
{
    // Formatter
    public static final DecimalFormat df = new DecimalFormat("+#.##%;-#.##%");


    // Declare parameters and constants						
    public static final double pc = 1.0;                // Probability of crossover, will always happen
    public static double pm = 0.005;                    // Probability of mutation
    public static final int population = 50;           // Population size (must be even)
    public static final int chromosomes = 400;          // Chromosome length, number of possibilities
    public static final int generations = 4000;         // Number of generations
    public static final int elite = (int)(0.02 * population); // Percentage of solutions to clone


    // Allocate memory to store solutions and associated fitness
    public static int[][] solutions = new int[population][chromosomes]; // 2D array storing a chromosome for each member of the "population"
    public static double[] fitness = new double[population];            // 1D array storing the "fitness" of each member in the "population"
	
	public static ArrayList<Integer> deck = new ArrayList<Integer>();				//Deck


    // Start main method
    public static void main(String[] args)
    {
        // Open output files
        PrintWriter outputFile = null;
        try
        {
            outputFile = new PrintWriter(new FileOutputStream("Blackjack.txt",false));
        }
        catch(FileNotFoundException e)
        {
            System.out.println("File error.  Program aborted.");
            System.exit(0);
        }

        PrintWriter currentBestAsList = null;
        try
        {
            currentBestAsList = new PrintWriter(new FileOutputStream("CurrentBestAsList.txt",false));
        }
        catch(FileNotFoundException e)
        {
            System.out.println("File error.  Program aborted.");
            System.exit(0);
        }

        PrintWriter currentBestAsArray = null;
        try
        {
            currentBestAsArray = new PrintWriter(new FileOutputStream("CurrentBestAsArray.txt",false));
        }
        catch(FileNotFoundException e)
        {
            System.out.println("File error.  Program aborted.");
            System.exit(0);
        }


        // Initialize population randomly
        for (int i = 0; i < population; i++)
        {
            for (int j = 0; j < chromosomes; j++)
            {
                double bit = Math.random();
                if (bit < 0.5)
                    solutions[i][j] = 0;
                else if (bit < 1.)
                    solutions[i][j] = 1;
            }    
            fitness[i] = 0.;
        }


        // Output
        System.out.println("Generation 0");
        for (int i = 0; i < population; i++){
            printChromosome(solutions, i);
        }

        // Evaluate initial fitness
        fitness();


        // Keep track of best solutions in a given generation
        int[] best = new int[elite];
        for (int i = 0; i < elite; i++)
            best[i] = 0;


        // Start generation loop
        for (int i = 1; i < generations; i++)
        {
            // Worst fitness in current generation
            double minFitness = 1.;
            for (int k = 0; k < population; k++)
            {
                if (fitness[k] < minFitness)
                    minFitness = fitness[k];
            }
            //System.out.println("Min fitness = " + df.format(minFitness));


            // Temporary memory allocation for next generation
            int[][] tmp = new int[population][chromosomes];


            // Create new population
            int count = 0;
            while (count < population)
            {
                // Selection by roulette wheel
                int[] parents = rouletteWheel(Math.min(minFitness, -1.));
                //printChromosome(solutions,parents[0]);
                //printChromosome(solutions,parents[1]);


                // Crossover
                // TASK: Implement two-point crossover by copying from the
                //       solutions[][] matrix to the next two open rows in the
                //       tmp[][] matrix.
                //
                //       Remember the indices for the two rows in solutions[][]
                //       to be used for crossover are stored in parents[0] and
                //       parents[1].  You will have to use Math.random() to
                //       determine where the crossover points will occur.
                //
                //       Big hint: The code to copy from solutions[][] to tmp[][]
                //       should involve statements that look like this
                //
                //       tmp[count][j] = solutions[parents[0]][j];
                if (Math.random() < pc)
                {
					int crossover1 = (int) (Math.random() * chromosomes);
					int crossover2 = (int) (Math.random() * chromosomes); 
					
					int cross1 = Math.min(crossover1, crossover2);
					int cross2 = Math.max(crossover1, crossover2);
					
					for (int c = 0; c < cross1; c++)
					{
						tmp[count][c] = solutions[parents[0]][c];
						tmp[count+1][c] = solutions[parents[1]][c];
					}
					
					for (int c = cross1; c < cross2; c++)
					{
						tmp[count][c] = solutions[parents[1]][c];
						tmp[count+1][c] = solutions[parents[0]][c];
					}
					
					for (int c = cross2; c < chromosomes; c++)
					{
						tmp[count][c] = solutions[parents[0]][c];
						tmp[count+1][c] = solutions[parents[1]][c];
					}
				}
                else
                {
                    // Copy parents
                    //System.out.println("Clone parents");
                }


                // Mutation
                // TASK: Implement the mutation operation on the two new
                //       solutions that you just created in tmp[][].
                //
                //       Remember to loop over every 
				//  	The probability of
                //       mutation for each gene is stored as pm.
				for(int m = 0; m < chromosomes; m++)
				{
					if (Math.random() < pm)
					{
						tmp[count][m] = (int) (Math.random() * 7);
					}

					if (Math.random() < pm)
					{
						tmp[count+1][m] = (int) (Math.random() * 7);
					}
                }

                // Advance count by 2 as we have added two children i.e. two new
                // rows in tmp[][].
                count = count + 2;
            }

            // Copy tmp to solutions
            for (int j = 0; j < population; j++)
            {
                if (fitness[j] < fitness[best[elite-1]]) // Keep elites
                {
                    System.arraycopy(tmp[j], 0, solutions[j], 0, chromosomes);
                }
            }


            // Update objective function
            fitness();


            // Calculate average fitness of population and output
            double sumFitness = 0.0;
            for (int j = 0; j < population; j++){
                sumFitness += fitness[j];
            }

            double avgFitness = sumFitness / population;

            // Find elite solutions and output best
            double maxFitness = -1e3;
            for (int j = 0; j < population; j++)
            {
                if (fitness[j] > maxFitness)
                {
                    maxFitness = fitness[j];
                    best[0] = j;
                }
            }
            for (int getBest = 1; getBest < elite; getBest++)
            {
                maxFitness = -1e3;
                for (int j = 0; j < population; j++)
                {
                    if (fitness[j] > maxFitness && fitness[j] < fitness[best[getBest-1]])
                    {
                        maxFitness = fitness[j];
                        best[getBest] = j;
                    }
                }
            }

            // Output
            if (i%10==0)
            {
                System.out.println();
                System.out.println("Generation " + i);
                System.out.println("Avg fitness = " + df.format(avgFitness));
                System.out.println("Max fitness = " + df.format(fitness[best[0]]));
                System.out.println("Elites:");
                for (int print = 0; print < elite; print ++)
                {
                    System.out.println(best[print] + "\t" + df.format(fitness[best[print]]));
                }

                outputFile.printf("%d\t%1.6e\t%1.6e\r\n",i,avgFitness,fitness[best[0]]);
                currentBestAsList.printf("{");
                for(int p = 0; p < chromosomes; p++)
                    currentBestAsList.printf("%d,",solutions[best[0]][p]);
                currentBestAsList.printf("}\n");

                currentBestAsArray.printf("\n");
                for(int p = 0; p < chromosomes; p++)
                {
                    currentBestAsArray.printf("%d",solutions[best[0]][p]);
                }
                currentBestAsArray.printf("\n");

                outputFile.flush();
                currentBestAsList.flush();
                currentBestAsArray.flush();
            }
        }

        System.out.println("Best strategy after " + generations + " generations:");
        printChromosome(solutions,best[0]);

        outputFile.close();
        currentBestAsList.close();
        currentBestAsArray.close();
    } //End main method


    public static void fitness()
    {
        // Loop over the population - keep
        for (int m = 0; m < population; m++)
        {
            // Loop over blackjack game 

			int taskTotal = 100;			//number of hands
			double bet = 10.;				//bet of 10$ per hand
			double totalMoney = bet * taskTotal;	//Initial $ player has
			double finalMoney = 0.;			//Final $ after 100 games 
            double diffBet = 0.;			
			boolean go = true;
			boolean dealerGo = true;
			double averagescore = 0;
			
			for (int tasks = 0; tasks < taskTotal; tasks++) //Loop over games
            {
				int k = 0;						//removes card from deck after one has been dealt
				int total1 = 0;
				int total2 = 0;
				int dealer1 = 0;
				int dealer2 = 0;
				
				//Creates deck of 52 cards
			    for (int i=0;i<4;i++) {
			      for (int j=1;j<=13;j++) {
			        if (j<10) {
			          deck.add(j);
			        }
			        else {
			          deck.add(10);
			        }
			      }
			    }
				
                // Shuffle deck and distribute 2 cards 
			    total1 = cardPicker(total1,k,'h');
			    k++;
			    dealer1 = cardPicker(dealer1,k,'d');
			    k++;
			    total2 = cardPicker(total1,k,'h');		//total of card 1 + card 2
			    k++;
				System.out.println(total2);
			    dealer2 = cardPicker(dealer1,k,'d');		//total of dealer card 1 + card 2
			    k++;
				System.out.println(dealer1);
				
				//if (go == true) {
                	//int score = 0;
                	for (int actions = 0; actions < 9; actions++) //max number of moves in a game - 2 cards dealt 
               	 	{
                    // Get gene number corresponding to this situation, (total = my hand, dealer's face up card) 
                    	int gene = situation(total2, dealer1, m);	//number between 0 and 399

						if (solutions[m][gene] == 0){	//Stand, do nothing
							System.out.println("Teststay");
							break;	//breaks out of for loop
						
						}
						if (solutions[m][gene] == 1){							//Hit
							System.out.println("Testhit");
							total2 = cardPicker(total2,k,'h');
							k++;		
						}
					}
				
					while (dealer2 < 17){							//After player stands, dealer hits until at least 17.
						dealer2 = cardPicker(dealer2,k,'d');	
							k++;
					}
					
					if (( dealer2 < total2 && total2 < 22) || (dealer2 > 21 && total2 < 22)) 
					{
					  	if (total2 == 21){				//win with a blackjack
						  	finalMoney += bet * 2.5; 
					  	}
	  
							finalMoney += bet*2;
					  
				    }
				    else {
					  finalMoney -= bet;

				    }

            //} 
				
			deck.clear();		//Clear all elements of deck
			
			//finalMoney += 
				
		} //Loop over games
            fitness[m] = finalMoney/totalMoney; 
			System.out.println(fitness[m]);
			
			
        }//End loop over population
    }	//End Fitness method


    /*
     The situation() method outputs the gene number associated to a given situation.
     You do not need to modify it.
     */
    public static int situation(int total2, int dealer1, int m)
    {
        int situation = 0;		//determining which gene number matches what my situation is (my total, dealer up card)
		System.out.println("Test40.");
		int k=0;
		boolean move = true;
		
		//for (int k=0; k<population/2;k++){
		
			outerloop:
			for (int i=2;i<22;i++) { 				//Hard 
				for (int j=1;j<11;i++) {
					if (total2 == i && dealer1 == j) {
						situation = solutions[m][k];
						//move = false;
						break outerloop;
					}
					else {
						k++;
						if (k>200){
							break outerloop;
						}
						
					}
			}
			System.out.println("Test41.");
		}
		
		//0-199 and 200-399
		if (move == true) {						//only does second loop if move = true
			outerloop:
				for (int i=2;i<22;i++) { 		//Soft
					for (int j=1;j<11;i++) {
						if (total2 == i && dealer1 == j) {
							situation = solutions[m][k];
							break outerloop;
						}
						else {
							k++;
							if (k>400)
								break outerloop;
							
						}
					}
				}
			}
			System.out.println("Test42.");

        return situation;
    }


    /*
     The rouletteWheel() method selects next parents based on fitness.
     You do not need to modify it.
     */
    public static int[] rouletteWheel(double minFitness)
    {
        double sum = 0.0;
        for (int i = 0; i < population; i++){
            sum += fitness[i] - minFitness;
        }

        int[] indices = new int[2];
        double luckyNumber, findParent;
        int search;


        // Spin number 1 to get first parent
        luckyNumber = Math.random();
        findParent = 0.0;
        search = 0;
        while (findParent <= luckyNumber && search < fitness.length)
        {
            findParent += (fitness[search] - minFitness) / sum;
            search ++;
        }

        indices[0] = search - 1;
        //System.out.println("Parent 1 = " + indices[0]);


        // Spin number 2 to get second parent
        luckyNumber = Math.random();
        findParent = 0.0;
        search = 0;
        while (findParent <= luckyNumber && search < fitness.length)
        {
            findParent += (fitness[search] - minFitness) / sum;
            search ++;
        }

        indices[1] = search - 1;
        //System.out.println("Parent 2 = " + indices[1]);

        return indices;
    }


    /*
     The printChromosome() method outputs a given solution to screen.
     You do not need to modify it.
     */
    public static void printChromosome(int[][] array, int index)
    {
        System.out.print("\t");

        for (int j = 0; j < chromosomes; j++){
            System.out.print(array[index][j]);
        }

        System.out.println();
    }
   
   
    public static int cardPicker(int total, int k, char a) {
      int random = (int) (Math.random()*(52-k));
	  int card = 0;
	  
      if (a == 'h') {
        card = deck.get(random);
      }
      else {
        card = deck.get(random);
      }
	  total = total + card; 
	  deck.remove(random);

      return total;
	  
    }

}// End class GABlackjack
