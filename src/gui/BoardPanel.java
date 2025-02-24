/*
 * Authors: Zaudan Wawhkyung and Thomas Manfredo
 * Purpose: To display the board and interact with the user when it is user's turn
 * 
 */

package gui;
import java.util.HashSet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import clueGame.Board;
import clueGame.BoardCell;
import clueGame.Player;
import clueGame.Room;

public class BoardPanel extends JPanel{

	private Board board;
	private int cellSize;
	private int offsetX;
	private int offsetY;
	private Graphics clueGraphics;
	private BoardPanel boardPanel;
	private ClueGameFrame clueFrame;
	private Player human;

	public BoardPanel(Board board, ClueGameFrame clueFrame) {
		//create instance of this class for use in methods
		boardPanel = this;
		human = board.getPlayers().get(0);

		this.clueFrame = clueFrame;
		//instance of the board for use in methods
		this.board = board;
		this.setBackground(Color.DARK_GRAY);

		addMouseListener(new BoardListener());

	}

	private class BoardListener implements MouseListener {


		@Override
		public void mouseClicked(MouseEvent click) {
			//calculate where they clicked on the board based on what the offset and cell size are
			int row = (click.getY()-offsetY)/cellSize;
			int col = (click.getX()-offsetX)/cellSize;
			//only do anything if in the bounds of the boared
			if(row >= 0 && row < board.getNumRows() && col >= 0 && col < board.getNumColumns()) {
				//if it isn't the players turn, do nothing
				if(Player.getCurrPlayer() != 0) {
					return;
				}
				//if it is the players turn, and they aren't finished
				else if(!human.getIsFinished()){
					//go through the target options
					for(BoardCell target: board.getTargets()) {

						//if the player moves to a regular cell and where they clicked is a target
						if(!board.getCell(row, col).isRoomCenter() && board.getCell(row, col).equals(target)) {
							//move the player and set them to finished
							movePlayer(target,human);
							human.setIsFinished(true);
							return;
						}
						//if the room isn't a walkway (a room), then make sure what they click is in the bounds of the target's room
						//this way it will work if the player clicks anywhere in a target room
						else if(board.getCell(row, col).getInitial() != 'W' && board.getCell(row, col).getInitial() == target.getInitial()){
							movePlayer(target, human);
							return;
						}	

					}	
				}	
				//if they click somewhere not a target, then display an error message to them
				if(!board.getTargets().contains(board.getCell(row, col))) {
					JOptionPane.showMessageDialog(clueFrame, "Please select a valid target"
							, "Invalid Target", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}

		//extra mouse press methods not needed
		@Override
		public void mouseReleased(MouseEvent e) {}
		@Override
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		//class graphics object instantiated here so that we have access to this graphics object in moving the player
		clueGraphics = g;
		//calculate the cell size (depending on which one is smaller) based on the size of the component and resize each time it repaints
		if(getHeight() < getWidth()) {
			cellSize = getHeight() / board.getNumRows();
		}else {
			cellSize = getWidth() / board.getNumColumns();
		}

		//width and height of the board calculated for centering the board in the panel
		int width = cellSize*board.getNumColumns();
		int height = cellSize*board.getNumRows();
		offsetX = getWidth()/2 - width/2;
		offsetY = getHeight()/2- height/2;
		//translate the starting point so that we can have the board always be centered
		g.translate(offsetX, offsetY);
		//loop through the board initially to draw the base layer
		for (int row = 0; row < board.getNumRows(); row++) {
			for(int col = 0; col < board.getNumColumns(); col++) {
				board.getCell(row, col).draw(cellSize, clueGraphics);
			}
		}

		//if human's turn (and not finished) display targets
		if(!human.getIsFinished()) {
			displayPlayerTargets();
		}
		//loop through the board again to draw the doorways, room labels, and players

		for (int row = 0; row < board.getNumRows(); row++) {
			for(int col = 0; col < board.getNumColumns(); col++) {
				//if cell is a doorway, draw on the cell adjacent (room cell)
				board.getCell(row, col).drawDoorways(cellSize, g);
				//if cell is a label, draw the text labels
				if(board.getCell(row, col).isLabel()) {
					board.getRoom(board.getCell(row, col)).drawRoomLabels(row, col, cellSize, g);
				}
				//draw the secret passages last
				if(board.getCell(row, col).getSecretPassage() != 0 && !board.getCell(row, col).isRoomCenter()) {
					board.getCell(row, col).drawSecretPassage(cellSize, g);
				}
			}
		}
		
		//create a list of all players that will be not drawn yet after the room players are drawn
		Set<Player> notDrawn = new HashSet<>();
		for(Player player : board.getPlayers()) {
			notDrawn.add(player);
		}

		//loop through all rooms
		for(Room room : board.getRooms()) {
			//if there are players in the room
			if(!room.getPlayersInRoom().isEmpty()) {
				//loop through every player in the specific room
				for(int i = 0; i < room.getPlayersInRoom().size(); i++) {
					//remove from the player list from above
					notDrawn.remove(room.getPlayersInRoom().get(i));
					//draw the players in the room with an offset based on how many players are in the room
					room.getPlayersInRoom().get(i).draw(cellSize, i, g);
				}
			}
		}
		//draw all remaining players
		for(Player player : notDrawn) {
			player.draw(cellSize, g);
		}
		//clear set
		notDrawn.clear();
	}
	//update the location of the player and redraw their location on the board
	public void movePlayer(BoardCell target, Player player) {
		//If target is room center and player is not in that room before then increment the room player numbers
		if(target.isRoomCenter() && !target.equals(player.getLocation(board))) {
			board.getRoom(target).getPlayersInRoom().add(player);
		}
		//decrement room player number if they leave the room
		if (player.getLocation(board).isRoomCenter() && !target.equals(player.getLocation(board))) {
			board.getRoom(player.getLocation(board)).getPlayersInRoom().remove(player);
		}
		
		//once they leave the room, unoccupy that space (only matters for walkways)
		player.getLocation(board).setOccupied(false);
		//move the player location to the target
		player.updateLocation(target.getRow(), target.getCol());
		//draw the player in their new location and set it to occupied
		player.draw(cellSize, clueGraphics);
		player.getLocation(board).setOccupied(true);
		repaint();
		//if the player is in a room, then set their status to be true,
		//and let them make a suggestion if they haven't already that turn
		if(player.getLocation(board).isRoomCenter() ) {

			if(Player.getCurrPlayer() == 0) human.setIsFinished(true);

			clueFrame.makeSuggestion(!human.getSuggestionStatus());
		}
	}
	//this method is for lighting up the targets for the human player
	public void displayPlayerTargets() {
		
		//loop through all targets
		for(BoardCell target: board.getTargets()) {
			//if the target is a center, light up all the cells in the room for the player to click on
			if(target.isRoomCenter()) {
				for(int row = 0; row < board.getNumRows(); row++) {
					for(int col = 0; col < board.getNumColumns(); col++) {
						if(board.getCell(row, col).getInitial()==target.getInitial()){
							board.getCell(row, col).drawTarget(cellSize, clueGraphics);
						}
					}
				}
			}
			//otherwise, light up the cell and redraw the border around the cell
			else {
				target.drawTarget(cellSize, clueGraphics);
				clueGraphics.setColor(Color.BLACK);
				clueGraphics.drawRect(target.getCol()*cellSize, target.getRow()*cellSize, cellSize, cellSize);
			}
		}
	}


}
