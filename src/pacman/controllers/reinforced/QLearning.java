package pacman.controllers.QLearning;

import java.util.Random;
import java.util.ArrayList;
import pacman.controllers.Controller;
import pacman.game.Game;
import java.util.EnumMap;
import static pacman.game.Constants.*;
import pacman.game.internal.Maze;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

public class QLearning extends Controller<MOVE> {
    Controller<EnumMap<GHOST, MOVE>> spookies;
	public QLearning(Controller<EnumMap<GHOST, MOVE>> spookies){
		this.spookies = spookies; // SPOOKIES 
	}
	public MOVE getMove(Game game,long timeDue) {
                
                Game[] games = new Game[5];
		MOVE[] moves = MOVE.values();
                games[0] = game.copy();
                
            
                System.out.println(games[0].getPacmanCurrentNodeIndex());
                int start=games[0].getPacmanCurrentNodeIndex();
                int strtx=(start%50);
                int strty=(start/50);
                int lookup=(start/50)*2;
                lookup=1;
                
                double Q[][][]=new double[lookup][lookup][4];
                double alpha=0.5;
                double discountFactor=1; // could be one if don't favour current actions as compared to next one
                double costToMove=-0.4;
                double tempMaxQ=0;
                double tempBestMove=0;
                int bestMove = 0;
                int ctr=0;
                int reward=1;
                int maxScore = 0;
                int minimin=0;
                Random ran=new Random();
                //int[] scores = new int[5];
                
                
                
		
                
                int p = 0;
		for (MOVE move : MOVE.values()){
			moves[p] = move;
			p++;
		}
                
                for(int i=0;i<lookup;i++)
                    for (int j=0;j<lookup;j++)
                        for(int k=0;k<4;k++)
                            Q[i][j][k]=0;
                
                //Maze m =games[0].getMaze();
                                
xxx:            for(int i=0;;){
                    for(int j=0;;){
aaa:                    for(int k=0;k<4;k++){
    
                            ctr=0;
                            
                            for(int a=0;a<lookup;a++){
                                for(int b=0;b<lookup;b++){
                                    for(int c=0;c<4;c++){
                                        if(Q[a][b][c]!=0){
                                            
                                            ctr++;
                                        }
                                        
                                            
                                    }
                                }
                            }
                            
                            if(ctr==lookup*lookup*4)
                                break xxx;
                            /*minimin=15000000;
                            for(int z=0;z<4;z++){
                                
                                if(Q[i][j][0]==Q[i][j][1] && Q[i][j][1]==Q[i][j][2] && Q[i][j][2]==Q[i][j][3])
                                    k=ran.nextInt(4);
                                else if((k==0 && i==0) || (k==1 && j==0) || (k==2 && i==lookup-1) || (k==3 && j==lookup-1))
                                    k=ran.nextInt(4);
                                
                                else if(minimin>Q[i][j][z] && Q[i][j][z]==0)
                                    k=z;
                            }*/
                            System.out.println("Q["+i+"]["+j+"]["+k+"]="+Q[i][j][k]);
                                
                            if(k==0){
                                if(i==0){
                                    //thats it! its a wall!
                                    Q[i][j][k]=alpha*costToMove;
                                    System.out.println("Q["+i+"]["+j+"]["+k+"]="+Q[i][j][k]);
                                    continue aaa;
                                }
                                else {
                                    i=i-1;
                                    for(int x=0;x<4;x++){
                                        if(tempMaxQ<Q[i][j][x]){
                                            tempMaxQ=Q[i][j][x];
                                            tempBestMove=x;
                                        }
                                    }
                                    
                                    games[0].advanceGame(moves[k], spookies.getMove(games[0], 1));
                                    reward=games[0].getScore();
                                    games[0].advanceGame(moves[k], spookies.getMove(games[0], -1));
                                    
                                    Q[i+1][j][k]=alpha*(reward+costToMove+(discountFactor*tempMaxQ)-Q[i+1][j][k]); //rewrad is 1 For NOW
                                    //i=i+1;
                                    
                                    System.out.println("Q["+(i+1)+"]["+j+"]["+k+"]="+Q[i+1][j][k]);
                                    k=-1;
                                    continue aaa;
                                }
                            }
                            
                            else if(k==1){
                                if(j==0){
                                    //thats it! its a wall!
                                    Q[i][j][k]=alpha*costToMove;
                                    System.out.println("Q["+i+"]["+j+"]["+k+"]="+Q[i][j][k]);
                                    continue aaa;
                                }
                                else {
                                    j=j-1;
                                    for(int x=0;x<4;x++){
                                        if(tempMaxQ<Q[i][j][x]){
                                            tempMaxQ=Q[i][j][x];
                                            tempBestMove=x;
                                        }
                                    }    
                                    
                                    games[0].advanceGame(moves[k], spookies.getMove(games[0], 1));
                                    reward=games[0].getScore();
                                    games[0].advanceGame(moves[k], spookies.getMove(games[0], -1));
                                    
                                    Q[i][j+1][k]=alpha*(reward+costToMove+(discountFactor*tempMaxQ)-Q[i][j+1][k]); //rewrad is 1 For NOW
                                    //j=j+1;
                                    System.out.println("Q["+i+"]["+(j+1)+"]["+k+"]="+Q[i][j+1][k]);
                                    k=-1;
                                    continue aaa;
                                }
                            }
                            
                            else if(k==2){
                                if(i==lookup-1){
                                    //thats it! its a wall!
                                    Q[i][j][k]=alpha*costToMove;
                                    System.out.println("Q["+i+"]["+j+"]["+k+"]="+Q[i][j][k]);
                                    continue aaa;
                                }
                                else {
                                    i=i+1;
                                    for(int x=0;x<4;x++){
                                        if(tempMaxQ<Q[i][j][x]){
                                            tempMaxQ=Q[i][j][x];
                                            tempBestMove=x;
                                        }
                                    }    
                                    
                                    games[0].advanceGame(moves[k], spookies.getMove(games[0], 1));
                                    reward=games[0].getScore();
                                    games[0].advanceGame(moves[k], spookies.getMove(games[0], -1));
                                    
                                    Q[i-1][j][k]=alpha*(reward+costToMove+(discountFactor*tempMaxQ)-Q[i-1][j][k]); //rewrad is 1 For NOW
                                    //i=i-1;
                                    System.out.println("Q["+(i-1)+"]["+j+"]["+k+"]="+Q[i-1][j][k]);
                                    k=-1;
                                    continue aaa;
                                }
                            }
                            
                            else if(k==3){
                                if(j==lookup-1){
                                    //thats it! its a wall!
                                    Q[i][j][k]=alpha*costToMove;
                                    System.out.println("Q["+i+"]["+j+"]["+k+"]="+Q[i][j][k]);
                                    continue aaa;
                                }
                                else {
                                    j=j+1;
                                    for(int x=0;x<4;x++){
                                        if(tempMaxQ<Q[i][j][x]){
                                            tempMaxQ=Q[i][j][x];
                                            tempBestMove=x;
                                        }
                                    }    
                                    
                                    games[0].advanceGame(moves[k], spookies.getMove(games[0], 1));
                                    reward=games[0].getScore();
                                    games[0].advanceGame(moves[k], spookies.getMove(games[0], -1));
                                    
                                    Q[i][j-1][k]=alpha*(reward+costToMove+(discountFactor*tempMaxQ)-Q[i][j-1][k]); //rewrad is 1 For NOW
                                    //j=j-1;
                                    System.out.println("Q["+i+"]["+(j-1)+"]["+k+"]="+Q[i][j-1][k]);
                                    k=-1;
                                    continue aaa;
                                }
                            }
                            
                        }
                    }
                }
                    
                
                return moves[bestMove];
                
	}
    
}
