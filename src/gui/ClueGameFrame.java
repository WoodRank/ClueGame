/*
 * Authors: Zaudan Wawhkyung and Thomas Manfredo
 * Purpose: Holds all the other panel and instantiate an instance of board
 * 
 */


package gui;

import java.awt.BorderLayout;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


import clueGame.Board;
import clueGame.Card;
import clueGame.ComputerPlayer;
import clueGame.Player;
import clueGame.Solution;

public class ClueGameFrame extends JFrame {
	private BoardPanel boardPanel;
	private GameControlPanel gameControlPanel;
	private KnownCardsPanel knownCardsPanel;
	private static Board board;
	private ClueGameFrame clueFrame;
	Instant startTime;
	String musicName = "baiza.wav";
	public ClueGameFrame() {
		//create an instance of the class for use in other classes and methods
		clueFrame = this;
		Image img = new ImageIcon(Suggestion.class.getResource("MapleLeaf.jpeg")).getImage();
		clueFrame.setIconImage(img);

		// Board is singleton, get the only instance
		board = Board.getInstance();
		// set the file names to use my config files
		board.setConfigFiles("data/ClueLayout.csv", "data/ClueSetup.txt");		
		// Initialize will load config files 
		board.initialize();

		setTitle("Maple Hall Clue!");
		setSize(1000, 800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		boardPanel = new BoardPanel(board, clueFrame);
		gameControlPanel = new GameControlPanel(board, clueFrame);
		knownCardsPanel = new KnownCardsPanel();

		//add components
		add(boardPanel, BorderLayout.CENTER);
		add(gameControlPanel, BorderLayout.SOUTH);
		add(knownCardsPanel, BorderLayout.EAST);		

		//get hand cards for the human player
		for(Card card: board.getPlayers().get(0).getHand()) {
			knownCardsPanel.createFieldFromCard( board.getPlayers().get(0), card, true);
		}
		//update the cards panel
		knownCardsPanel.updateFields();
		clueFrame.startMusic();
	}

	public static void main(String[] args) {
		ClueGameFrame clueFrame = new ClueGameFrame();
		//System.out.println(board.getSolution());
		clueFrame.setVisible(true);

		//initial popup message for the player
		JOptionPane.showMessageDialog(clueFrame, "You are Mable the Moose.\nCan you prove who committed \nthe murder in our peaceful residence!?"
				, "Welcome to Maple Hall!", JOptionPane.INFORMATION_MESSAGE);
		
		//set up the timer used for how long it takes the player to do it
		Instant startTime = Instant.now();
		clueFrame.setStartTime(startTime);

		//set up the first turn
		int roll = board.rollDice();
		board.calcTargets(board.getPlayers().get(0).getLocation(board), roll);
		clueFrame.getGameControlPanel().setTurn(board.getPlayers().get(0), roll);
		clueFrame.getBoardPanel().repaint();

	}

	/*
	 * handles making suggestions from either the player or the computer and teleports them when it happens
	 */
	public void makeSuggestion(boolean suggestionStatus){
		//blank suggestion
		Solution suggestion = null;
		
		//if they haven't already made a suggestion and move themselves into the same room
		if(!board.getPlayers().get(Player.getCurrPlayer()).getSuggestionStatus()) {
			//if computer player, use their logic to create a suggestion
			if (Player.getCurrPlayer() != 0) {
				ComputerPlayer tempComp = (ComputerPlayer) board.getPlayers().get(Player.getCurrPlayer());
				suggestion = tempComp.createSuggestion(board);
			}
			//if human, create a new suggestion from on screen input
			else {
				//human suggestion
				Suggestion humanSuggestion = new Suggestion(clueFrame);
				suggestion = humanSuggestion.getSuggestion();
			}
		}
		//after they make a suggestion, indicate that they have
		board.getPlayers().get(Player.getCurrPlayer()).setSuggestionStatus(true);
		
		//if they choose to make a suggestion, do the following
		if(suggestion != null) {
			//teleport the player in the suggestion
			boardPanel.movePlayer(board.getPlayers().get(Player.getCurrPlayer()).getLocation(board), board.getPlayer(suggestion.getPerson().getCardName()));
			//if the player doesn't teleport themselves, then set their status to be teleported for when targets are calculated
			//this ensures that the player who gets teleported (not themselves) 
			if(!board.getPlayers().get(Player.getCurrPlayer()).equals(board.getPlayer(suggestion.getPerson().getCardName()))) {
				board.getPlayer(suggestion.getPerson().getCardName()).setWasTeleported(true);
			}
			//show their guess on the panel
			getGameControlPanel().setGuess(suggestion.toString());

			//grab the card that disproves the suggestion
			Card disprovingCard = board.handleSuggestion(suggestion, board.getPlayers().get(Player.getCurrPlayer()));

			//if there is a disproving card
			if (disprovingCard != null ) {
				//if it is the human player, update the seen cards on the gui
				if(Player.getCurrPlayer() == 0 && !board.getPlayers().get(Player.getCurrPlayer()).getSeenCards().contains(disprovingCard)) {
					knownCardsPanel.createFieldFromCard(board.getPlayers().get(board.getDisprovingPlayerIndex()), disprovingCard, false);
					knownCardsPanel.updateFields();
				}
				//update the players seen cards
				board.getPlayers().get(Player.getCurrPlayer()).updateSeen(disprovingCard);
				//update the guess result gui with the color of the player who disproved
				getGameControlPanel().setGuessResult("Suggestion Disproven", board.getDisprovingPlayerIndex());
			}
			//if not disproved, display as such
			else {
				getGameControlPanel().setGuessResult("Suggestion Cannot Be Disproven!");
			}
		}
	}

	//load music (defined at the start of the class)
	public void startMusic() {
		try {
			File musicFile = new File("./data/" + musicName);
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile.toURI().toURL());
			Clip clip = AudioSystem.getClip();
			clip.open(audioStream);
			clip.loop(clip.LOOP_CONTINUOUSLY);
			clip.start();

		} catch(Exception e) {

			System.out.println(e.getMessage());

		}

	}

	//grab the boardPanel made by this class for use in other classes and methods
	public BoardPanel getBoardPanel() {
		return boardPanel;
	}
	public GameControlPanel getGameControlPanel() {
		return gameControlPanel;
	}
	public Board getBoard() {
		return board;
	}

	public void setStartTime(Instant start) {
		startTime = start;
	}
	public Instant getStartTime() {
		return startTime;
	}
}
