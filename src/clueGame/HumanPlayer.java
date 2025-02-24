/*
 * Authors: Zaudan Wawhkyung and Thomas Manfredo
 * Purpose: A class for the human player and its functionality
 * 
 */

package clueGame;

import java.awt.Color;

public class HumanPlayer extends Player{
	//isFinished is to check for player's turn
	private boolean isFinished = false;
	
	public HumanPlayer(String name, Color color, int row, int col) {
		super(name, color, row, col);
	}
	


	@Override
	public void setIsFinished(boolean turn) {
		isFinished = turn;
	}

	@Override
	public boolean getIsFinished() {
		return isFinished;
	}


	@Override
	public BoardCell selectTarget(Board board) {
		// TODO Auto-generated method stub
		return null;
	}
	







}
