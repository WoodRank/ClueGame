/*
 * Authors: Thomas Manfredo and Zaudan Wawhkyung
 * 
 * Purpose: This class will test a 4x4 board that will be able to calculate adjacent board cells,
 * and calculate targets based on a given roll (pathLength)
 */

package experiment;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TestBoard {

	final static int COLS = 4;
	final static int ROWS = 4;
	private TestBoardCell[][] grid;
	private Set<TestBoardCell> targets;
	private Set<TestBoardCell> visited;
	
	//constructor; for this board, initialized based on ROWS and COLS and adds a new cell to each.
	//At this time, the adjacency list is calculated so that it is known from the creation of the board
	public TestBoard() {
		grid = new TestBoardCell[ROWS][COLS];
		for(int row = 0; row < grid.length; row++) {
			for(int col = 0; col < grid[row].length; col++) {
				grid[row][col] = new TestBoardCell(row,col);
			}
		}
		calcAdjacency();
	}
	
	//calcAdjacency will calculate the adjacency of each cell in the grid, using the adjList in TestBoardCell
	public void calcAdjacency() {
		for(int row = 0; row < ROWS; row++) {
			for(int col = 0; col < COLS; col++) {
				if(row > 0) grid[row][col].addAdjacency(grid[row-1][col]);
				if(col > 0) grid[row][col].addAdjacency(grid[row][col-1]);
				if(row < ROWS-1) grid[row][col].addAdjacency(grid[row+1][col]);
				if(col < COLS-1) grid[row][col].addAdjacency(grid[row][col+1]);
			}
		}
	}
	
	//calcTargets will wipe any old visited and targets list and add the startCell to the visited list
	//then calls the recursive findAllTargets function
	public void calcTargets(TestBoardCell startCell, int pathLength) {
		targets = new HashSet<TestBoardCell>();
		visited = new HashSet<TestBoardCell>();
		visited.add(startCell);
		findAllTargets(startCell, pathLength);
	}
	
	//private so that the user only has access to the main, calcTargets function
	
	/*	This function will loop through the adjacency list of the starting cell, checking to see if it can
		continue on based on the pathLength, and if the cell is a room or occupied, it will add to targets 
		either when it reaches the end of its roll or when it reaches a room
	*/
	private void findAllTargets(TestBoardCell thisCell, int pathLength) {
		for(TestBoardCell adjCell : thisCell.getAdjList()) {
			if(!visited.contains(adjCell) && !adjCell.isOccupied()) {
				visited.add(adjCell);
				if((pathLength == 1 || adjCell.isRoom())) {
					targets.add(adjCell);
				}
				else findAllTargets(adjCell, pathLength-1);
				visited.remove(adjCell);
			}
		}
	}
	
	
	//getters
	
	public TestBoardCell getCell(int row, int col) {
		return grid[row][col];
	}
	
	//returns the last targets set that was retrieved
	public Set<TestBoardCell> getTargets(){
		return targets;
	}

}
