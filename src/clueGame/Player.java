/*
 * Authors: Zaudan Wawhkyung and Thomas Manfredo
 * Purpose: A parent class for different players (human, computer) and holds important methods like disproving suggestion and draw
 * 
 */


package clueGame;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.swing.JPanel;

public abstract class Player extends JPanel{
	protected String name;
	protected Color color;
	protected int row, col;
	protected ArrayList<Card> hand;
	protected Set<Card> seenCards;
	private static int currPlayer = 0;
	protected boolean suggestionStatus = false;
	protected boolean wasTeleported = false;
	//Suggestion Solution cards
	


	protected Player(String name, Color color, int row, int col) {
		super();
		this.name = name;
		this.color = color;
		this.row = row;
		this.col = col;
		hand = new ArrayList<>();
		seenCards = new HashSet<>();
	}
	
	public void updateHand(Card card) {
		hand.add(card);
		updateSeen(card);
		
	}
	public void updateSeen(Card seenCard) {
		seenCards.add(seenCard);
	}
	
	/*
	 * Checks for the suggestion made and returns a random card to disprove otherwise return null
	 */
	public Card disproveSuggestion(Solution suggestion) {
		ArrayList<Card> disprovenHand = new ArrayList<>();
		for(Card card:hand) {
			//Checks for rooms, person, and weapon
			if (card.equals(suggestion.getRoom()) || card.equals(suggestion.getPerson()) || card.equals(suggestion.getWeapon())) {
				disprovenHand.add(card);
			}
		}
		if (disprovenHand.size() == 1) return disprovenHand.get(0); //Only 1 card to disprove
		else if (disprovenHand.size() > 1){
			//Randomly selects a card to disprove
			Random random = new Random();
			int rand = random.nextInt(disprovenHand.size());
			return disprovenHand.get(rand);
		}
		return null;
	}
	
	//draws the player based on its location and the current cell size
	public void draw(int cellSize, Graphics g) {
		g.setColor(color);
		g.fillOval(col * cellSize , row * cellSize, cellSize, cellSize);
		g.setColor(Color.black);
		g.drawOval(col * cellSize , row * cellSize, cellSize, cellSize);
		repaint();
	}
	public void draw(int cellSize, int offSet, Graphics g) {
		g.setColor(color);
		g.fillOval(col * cellSize + offSet * ((int) Math.round(cellSize * 0.3)) , row * cellSize, cellSize, cellSize);
		g.setColor(Color.black);
		g.drawOval(col * cellSize + offSet * ((int) Math.round(cellSize * 0.3)) , row * cellSize, cellSize, cellSize);
		repaint();
	}
	public abstract BoardCell selectTarget(Board board);
	
	public boolean equals(Player player) {
		return (name.equals(player.getName()) && color.equals(player.getColor()));
	}
	
	public void updateLocation(int row, int col) {
		this.row = row;
		this.col = col;
	}
	
	public BoardCell getLocation(Board board) {
		return board.getCell(row, col);
	}
	
	public String getName() {
		return this.name;
	}

	public Color getColor() {
		return this.color;
	}

	public int getRow() {
		return this.row;
	}

	public int getCol() {
		return this.col;
	}
	public ArrayList<Card> getHand(){
		return this.hand;
	}

	public abstract boolean getIsFinished();
	public abstract void setIsFinished(boolean turn);
	
	
	public static int getCurrPlayer() {
		return currPlayer;
	}

	public static void updateCurrPlayer() {
		if(currPlayer == 5) currPlayer = 0;
		else currPlayer++;
	}

	public Set<Card> getSeenCards() {
		return seenCards;
	}

	public boolean getSuggestionStatus() {
		return suggestionStatus;
	}

	public void setSuggestionStatus(boolean suggestionStatus) {
		this.suggestionStatus = suggestionStatus;
	}

	public boolean wasTeleported() {
		return wasTeleported;
	}

	public void setWasTeleported(boolean wasTeleported) {
		this.wasTeleported = wasTeleported;
	}
	
	
}
