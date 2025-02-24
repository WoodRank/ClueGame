/*
 * Authors: Thomas Manfredo and Zaudan Wawhkyung
 * Purpose: This class will layout everything needed for each board cell with relevant details 
 * like row, col, and what kind of cell it is.
 */

package clueGame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;



public class BoardCell extends JPanel {

	private int row, col;
	private char initial, secretPassage;
	private boolean roomLabel, roomCenter;
	private boolean isOccupied;
	private Set<BoardCell> adjList;
	private  DoorDirection doorDirection;

	//constructor, initialize variables and adjList
	public BoardCell(int row, int col, char initial) {
		this.row = row;
		this.col = col;
		this.initial = initial;
		this.secretPassage = 0;
		adjList = new HashSet<BoardCell>();

	}
	//Add adjacent cell
	public void addAdjacency(BoardCell cell) {
		adjList.add(cell);
	}

	public boolean equals(BoardCell cell) {
		return(row == cell.getRow() && col == cell.getCol() && initial == cell.getInitial());
	}

	public void draw(int cellSize, Graphics g ) {
		//if a walkway, fill in the rectangle and grid layout
		if (initial == 'W') {
			g.setColor(new Color(205,133,63));
			g.fillRect(col * cellSize , row * cellSize , cellSize, cellSize);
			g.setColor(Color.BLACK);
			g.drawRect(col * cellSize , row * cellSize , cellSize, cellSize);
		}
		//if unused space, fill in black
		else if (initial == 'X') {
			g.setColor(Color.BLACK);
			g.fillRect(col * cellSize , row * cellSize , cellSize, cellSize);
		}
		//if a room, fill in gray (no outline)
		else {
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(col * cellSize , row * cellSize , cellSize, cellSize);
		}
		
	}

	public void drawSecretPassage(int cellSize, Graphics g) {
		g.setColor(Color.YELLOW);
		g.fillRect(col * cellSize , row * cellSize , (int) Math.round(0.9*cellSize), cellSize);
		g.setFont(new Font("Calibri", Font.BOLD, (int) (0.9*cellSize)));
		g.setColor(Color.BLACK);
		g.drawString( secretPassage + "", col * cellSize + (int) Math.round(0.25*cellSize), row * cellSize + (int) Math.round(0.75*cellSize));
	}
	
	//Draw the cell onto the board Panel with a specified color
	public void drawTarget(int cellSize, Graphics g) {
		g.setColor(Color.CYAN);
		g.fillRect(col * cellSize , row * cellSize , cellSize, cellSize);
		repaint();
	}

	/*
	 * Draws doorways according to their direction
	 */
	public void drawDoorways( int cellSize, Graphics g) {
		//if cell is a doorway, then draw a small rectangle showing its direction
		if(isDoorway()) {
			switch(doorDirection) {
			case UP:	
				g.setColor(Color.BLUE);
				//Adjust the rectangle (door) so it draws a little bit above the cell
				g.fillRect(col * cellSize, row * cellSize -(int) Math.round(0.15*cellSize), cellSize, (int) Math.round(0.15*cellSize));
				repaint();
				break;
			case DOWN:
				g.setColor(Color.BLUE);
				//Adjust the rectangle (door) so it draws a little bit below the cell
				g.fillRect(col * cellSize, row * cellSize + cellSize, cellSize, (int) Math.round(0.15*cellSize));
				repaint();
				break;

			case RIGHT:
				g.setColor(Color.BLUE);
				//Adjust the rectangle (door) so it draws a little bit to the right of the cell 
				g.fillRect(col * cellSize + cellSize , row * cellSize , (int) Math.round(0.15*cellSize), cellSize);
				repaint();
				break;

			case LEFT:
				g.setColor(Color.BLUE);
				//Adjust the rectangle (door) so it draws a little bit to the left of the cell
				g.fillRect(col * cellSize - (int) Math.round(0.15*cellSize), row * cellSize , (int) Math.round(0.15*cellSize), cellSize);
				repaint();
				break;
			}
		}
	}
	/*
	 * Checks where the mouse is clicked on the board and checks if it clicks on a cell 
	 */

	public boolean containsClick(int cellSize, int mouseX, int mouseY) {
		Rectangle rect = new Rectangle(col*cellSize, row*cellSize, cellSize, cellSize);
		if(rect.contains(new Point(mouseX, mouseY))) {
			return true;
		}
		return false;
	}




	//Getter and setters for relevant variables
	public boolean isDoorway() {
		return (!doorDirection.equals(DoorDirection.NONE));
	}
	public DoorDirection getDoorDirection() {
		return doorDirection;
	}
	public void setDoorDirection(DoorDirection direction) {
		this.doorDirection = direction;
	}

	public boolean isLabel() {
		return roomLabel;
	}
	public void setLabel(Boolean label) {
		this.roomLabel = label;
	}

	public boolean isRoomCenter() {
		return roomCenter;
	}
	public void setRoomCenter(Boolean center) {
		this.roomCenter = center;
	}

	public void setSecretPassage(char secretPassage) {
		this.secretPassage = secretPassage;
	}
	public char getSecretPassage() {
		return secretPassage;
	}

	public char getInitial() {
		return initial;
	}
	public Set<BoardCell> getAdjList(){
		return adjList;
	}

	//Set the cell's occupancy status
	public void setOccupied(boolean isOccupied) {
		this.isOccupied = isOccupied;
	}
	//return if the room is occupied
	public boolean isOccupied() {
		return isOccupied;
	}
	public int getRow() {
		return row;
	}
	public int getCol() {
		return col;
	}



}
