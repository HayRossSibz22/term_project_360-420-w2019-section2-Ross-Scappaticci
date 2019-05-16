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
    public static final int population = 200;           // Population size (must be even)
    public static final int chromosomes = 400;          // Chromosome length, number of possibilities
    public static final int generations = 1000;         // Number of generations
    public static final int elite = (int)(0.02 * population); // Percentage of solutions to clone, put back at 0.02

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
				
				for(int m = 0; m < chromosomes; m++)
				{
					if (Math.random() < pm)
					{
						tmp[count][m] = (int) (Math.random() * 2);
					}

					if (Math.random() < pm)
					{
						tmp[count+1][m] = (int) (Math.random() * 2);
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
			int taskTotal = 100;					//number of hands
			double avgMoney = 0;
			double bet = 10.;						//bet of 10$ per hand
			double totalMoney = bet * taskTotal;	//Initial $ player has
								//Final $ after 100 games 
			double maxMoney = totalMoney * 2;		//wins every hand, not considering blackjack hands
            double diffBet = 0.;			
			boolean go = true;
			//boolean dontgo = false;
			double finalMoney = totalMoney; 	//final money after each game
			
			for (int tasks = 1; tasks <= taskTotal; tasks++) //Loop over 100 games
            {
				int k = 0;			//used to remove card from deck after one has been dealt
				int gene = 0;		//either zero or 1				
				int total1 = 0;
				int total2 = 0;
				int dealer1 = 0;
				int dealer2 = 0;
				int totalAce = 0;
				int total11 = 0;
				double finalmoneyAce = 0;
				double finalmoney11 = 0;
				
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
				
                // Shuffle deck and distribute 2 cards to player and dealer 
			    total1 = cardPicker(total1,k,'h');
			    k++;
			    dealer1 = cardPicker(dealer1,k,'d');	//dealer's face up card
			    k++;
			    total2 = cardPicker(total1,k,'h');		//total of card 1 + card 2
			    k++;
			    dealer2 = cardPicker(dealer1,k,'d');	//total of dealer card 1 + card 2
			    k++;

				if (total1 == 1 || (total2-total1)==1 )		//for differentiating between 1 and 11, run GA twice
				{ 
					totalAce = total2;	//totalAce is total with ace = 1 
					finalmoneyAce = finalMoney;
					

					//**********Playing the hand with ace as 1
                	for (int actions = 0; actions < 9; actions++) //max number of moves in a game - 2 cards dealt 
               	 	{
                    	go = false;
						
						// Get gene number corresponding to this situation, (total = my hand, dealer's face up card) 
                    	gene = geneNum(totalAce, dealer1, m, go);		//hit or stay, 1 or 0
						//System.out.println(gene);
						
						if (gene == 0)								//Stay, do nothing
						{								
							break;	
						
						}
						if (gene == 1)								//Hit
						{								
							totalAce = cardPicker(totalAce,k,'h');
							k++;		
						}
					}
					
					//After player stands, dealer hits until at least 17.
					while (dealer2 < 17){							
						dealer2 = cardPicker(dealer2,k,'d');	
							k++;
					}
					
					//Determine who won
					if (( dealer2 < totalAce && totalAce < 22) || (dealer2 > 21 && totalAce < 22)) 
					{
					  	if (totalAce == 21){				//win with a blackjack, higher payout
						  	finalmoneyAce += bet * 1.5; 
					  	}
						else {
							finalmoneyAce += bet;
						}
					  
				    }
				    else {
					  finalmoneyAce -= bet;

				    } 
					
					//************Playing the hand with ace as 11
                	for (int actions = 0; actions < 9; actions++) //max number of moves in a game - 2 cards dealt 
               	 	{
						total11 = total2 + 10; 
						finalmoney11 = finalMoney;
						go = true;
                    
						// Get gene number corresponding to this situation, (total = my hand, dealer's face up card) 
                    	gene = geneNum(total11, dealer1, m, go);		//hit or stay, 1 or 0
					
						if (gene == 0)								//Stay, do nothing
						{				
							break;	
						}
						if (gene == 1)								//Hit
						{
							total2 = cardPicker(total2,k,'h');
							k++;		
						}
					}
					
					//After player stands, dealer hits until at least 17.
					while (dealer2 < 17){							
						dealer2 = cardPicker(dealer2,k,'d');	
							k++;
					}
					
					//Determine who won
					if (( dealer2 < total11 && total11 < 22) || (dealer2 > 21 && total11 < 22)) 
					{
					  	if (total11 == 21){				//win with a blackjack, higher payout
						  	finalmoney11 += bet * 1.5; 
					  	}
						else {
							finalmoney11 += bet;
						}
					  
				    }
				    else {
					  finalmoney11 -= bet;
				  }
				  
				  //Determine which game won and update final money
				  if (finalmoneyAce > finalMoney){
					  finalMoney = finalmoneyAce;
				  }
				  
				  else{
					  finalMoney = finalmoney11;
				  }
				  
				}//end ace loop
				
				else{	
					//Playing the hand
                	for (int actions = 0; actions < 9; actions++) //max number of moves in a game - 2 cards dealt 
               	 	{
						go = false;
                    
						// Get gene number corresponding to this situation, (total = my hand, dealer's face up card) 
                    	gene = geneNum(total2, dealer1, m, go);			//hit or stay, 1 or 0
						//System.out.println(gene);
						
						if (gene == 0){								//Stay, do nothing
							//System.out.println("Teststay");
							//System.out.println(solutions[m][gene] + " is the gene number");
							break;	
						
						}
						if (gene == 1){								//Hit
							//System.out.println("Testhit");
							total2 = cardPicker(total2,k,'h');
							k++;		
						}
					}
					
					//After player stands, dealer hits until at least 17.
					while (dealer2 < 17){							
						dealer2 = cardPicker(dealer2,k,'d');	
							k++;
					}
					
					//Determine who won
					if (( dealer2 < total2 && total2 < 22) || (dealer2 > 21 && total2 < 22)) 
					{
					  	if (total2 == 21){				//win with a blackjack, higher payout
						  	finalMoney += bet * 1.5; 
					  	}
						else {
							finalMoney += bet;
						}
					  
				    }
				    else {
					  finalMoney -= bet;

				    } 
				}

					deck.clear();		//Clear all elements of deck

				} //Loop over games
			
			fitness[m]= finalMoney/totalMoney; //what I originally had
			
			
			
        }//End loop over population
    }//End Fitness method


    /*
     The situation() method outputs the gene number associated to a given situation.*/
    
    public static int geneNum(int total2, int dealer1, int m, boolean move)
    {
        int situation = 0;		//determining which gene number matches what my situation is (my total, dealer up card)s
		int geneNum = 0;
		int k=0;
		
			for (int i=2;i<22;i++)				//Hard 
			{ 				 
				for (int j=1;j<11;j++) 
				{
	
					if (total2 == i && dealer1 == j) 
					{
						//System.out.println(total2 + " in hand" + dealer1 +" dealer");
						//System.out.println(k + " gene location");
						geneNum = solutions[m][k];
					}
						k++;
				}
			}
			
		
		
		//Genes 200-399
		if (move == true) {						//Soft hand (ace=11), only does second loop if move = true
				for (int i=2;i<22;i++) { 		
					for (int j=1;j<11;j++) {
						if (total2 == i && dealer1 == j) {
							geneNum = solutions[m][k];
						}
						else {
							k++;
						}
					}
				}
		}

					
			return geneNum;
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
