/*
 * Authors: Thomas Manfredo and Zaudan Wawhkyung
 * Purpose: This class will how rooms are set and retrieved
 */

package clueGame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JComponent;

public class Room extends JComponent{
	private String name;
	private BoardCell centerCell;
	private BoardCell labelCell;
	private ArrayList<Player> playersInRoom;
	
	
	public Room(String name) {
		this.name = name;
		playersInRoom = new ArrayList<>();
		
	}

	/*
	 * Draw room labels and adjust according to the resize
	 */
	public void drawRoomLabels(int row, int col, int cellSize, Graphics g) {
		g.setColor(Color.black);
		g.setFont(new Font("Calibri", Font.BOLD, (int) (0.75*cellSize)));
		g.drawString(name, col * cellSize + (int)(0.25*cellSize) , row * cellSize);
		repaint();
	}
	
	//Setters and Getters
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public BoardCell getCenterCell() {
		return centerCell;
	}
	public void setCenterCell(BoardCell centerCell) {
		this.centerCell = centerCell;
	}
	public BoardCell getLabelCell() {
		return labelCell;
	}
	public void setLabelCell(BoardCell labelCell) {
		this.labelCell = labelCell;
	}	
	public ArrayList<Player> getPlayersInRoom() {
		return playersInRoom;
	}
}
