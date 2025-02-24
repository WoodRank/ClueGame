/*
 * Authors: Thomas Manfredo and Zaudan Wawhkyung
 * 
 * Purpose: This class will set up the actual board cells for the board, as well as store the 
 * adjacency list for each cell.
 */

package experiment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TestBoardCell {
	private int row, col;
	private boolean isRoom, isOccupied;
	private Set<TestBoardCell> adjList;
	
	//constructor, initialize variables and adjList
	public TestBoardCell(int row, int col) {
		this.row = row;
		this.col = col;
		adjList = new HashSet<TestBoardCell>();
	}
	//Add adjacent cell
	public void addAdjacency(TestBoardCell cell) {
		adjList.add(cell);
	}
	//return the adjacency list
	public Set<TestBoardCell> getAdjList() {
		return adjList;
	}
	
	//set the cell's room status
	public void setRoom(boolean isRoom) {
		this.isRoom = isRoom;
	}
	//return if the cell is a room
	public boolean isRoom() {
		return isRoom;
	}
	//Set the cell's occupancy status
	public void setOccupied(boolean isOccupied) {
		this.isOccupied = isOccupied;
	}
	//return if the room is occupied
	public boolean isOccupied() {
		return isOccupied;
	}
	

}
