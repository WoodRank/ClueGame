package tests;

import static org.junit.Assert.*;

import java.awt.Color;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import clueGame.Board;
import clueGame.Solution;
import clueGame.Card;
import clueGame.CardType;
import clueGame.ComputerPlayer;
import clueGame.HumanPlayer;
import clueGame.Player;

public class GameSolutionTest {
	private static Board board;
	private static Card baizaCard;
	private static Card dormCard;
	private static Card bleachCard;
	private static Card michaelCard;
	private static Card elevatorCard;
	private static Card cableCard;
	private static Card mableCard;
	private static Card lobbyCard;
	private static Card pianoCard;
	private static Card blasterCard;
	private static Card kitchenCard;
	private static Card bikeCard;
	@BeforeAll
	public static void setUp() {
		// Board is singleton, get the only instance
		board = Board.getInstance();
		// set the file names to use my config files
		board.setConfigFiles("data/ClueLayout.csv", "data/ClueSetup.txt");
		board.initialize();
		
	}
	@BeforeAll
	public static void setUpCards() {
		//initialize test cards
		baizaCard = new Card("Resident Baiza", CardType.PERSON);
		dormCard = new Card("Dorm", CardType.ROOM);
		bleachCard = new Card("Bleach", CardType.WEAPON);
		michaelCard = new Card("RA Michael", CardType.PERSON);
		elevatorCard = new Card("Elevator", CardType.ROOM);
		cableCard = new Card("Cable", CardType.WEAPON);
		mableCard = new Card("Mable the Moose", CardType.PERSON);
		lobbyCard = new Card("Lobby", CardType.ROOM);
		pianoCard = new Card("Piano", CardType.WEAPON);
		blasterCard = new Card("Blaster", CardType.PERSON);
		kitchenCard = new Card("Kitchen", CardType.ROOM);
		bikeCard = new Card("Bike", CardType.WEAPON);
	}
	
	//@Test
	public void checkAccusation() {
		
		Solution testAnswer = new Solution(dormCard, baizaCard, bleachCard);
		
		//check to make sure if the solution is the same, then it will return true
		Solution testSolution = new Solution(dormCard, baizaCard, bleachCard);
		assertTrue(board.checkAccusation(testSolution, testAnswer));
		
		//check for if the room is wrong false is returned
		testSolution = new Solution(elevatorCard, baizaCard, bleachCard);
		assertFalse(board.checkAccusation(testSolution, testAnswer));
		
		//check for if the person is wrong false is returned
		testSolution = new Solution(dormCard, michaelCard, bleachCard);
		assertFalse(board.checkAccusation(testSolution, testAnswer));
		
		//check for if the weapon is wrong false is returned
		testSolution = new Solution(dormCard, baizaCard, cableCard);
		assertFalse(board.checkAccusation(testSolution, testAnswer));
	}
	@Test
	public void disproveSuggestionTest() {
		//Testing if one card is matching

		HumanPlayer testPlayer = new HumanPlayer("Mable the Moose", Color.green, 0, 16);
		
		Solution testSuggestion = new Solution(elevatorCard, baizaCard, cableCard);
		
		testPlayer.updateHand(baizaCard);
		testPlayer.updateHand(bleachCard);
		testPlayer.updateHand(dormCard);
		
		assertEquals(testPlayer.disproveSuggestion(testSuggestion), baizaCard);
		
		//Testing if more than one card is matching
		int cardOneMatch = 0;
		int cardTwoMatch = 0;
		int cardThreeMatch = 0;
		testSuggestion = new Solution(dormCard, baizaCard, bleachCard);
		for(int i = 0; i < 30; i++) {
			if (testPlayer.disproveSuggestion(testSuggestion).equals(baizaCard)) {
				cardOneMatch++;
			}
			if (testPlayer.disproveSuggestion(testSuggestion).equals(bleachCard)) {
				cardTwoMatch++;
			}
			if (testPlayer.disproveSuggestion(testSuggestion).equals(dormCard)) {
				cardThreeMatch++;
			}
		}
		assertTrue(cardOneMatch > 0 && cardTwoMatch > 0 && cardThreeMatch > 0);
		
		//Testing if no card matches
		testSuggestion = new Solution(board.getRoomCards().get(1), board.getPlayerCards().get(1), board.getWeaponCards().get(1));
		assertEquals(testPlayer.disproveSuggestion(testSuggestion), null);
	}
	
	
	@Test
	public void handleSuggestion() {
		//Initialize a known player list
		HumanPlayer mable = new HumanPlayer("Mable the Moose", Color.green, 0, 16);
		ComputerPlayer michael = new ComputerPlayer("RA Michael", Color.yellow, 7,0);
		ComputerPlayer baiza = new ComputerPlayer("Resident Baiza", Color.orange, 25, 17);
		ArrayList<Player> players = new ArrayList<>();
		players.add(mable);
		players.add(michael);
		players.add(baiza);
		
		//initialize a known suggestion
		Solution suggestion = new Solution(dormCard, baizaCard, bleachCard);
		
		//give each player a hand, none of which match the suggestion
		mable.updateHand(elevatorCard);
		mable.updateHand(blasterCard);
		mable.updateHand(kitchenCard);
		michael.updateHand(bikeCard);
		michael.updateHand(cableCard);
		michael.updateHand(lobbyCard);
		baiza.updateHand(pianoCard);
		baiza.updateHand(michaelCard);
		baiza.updateHand(mableCard);
		//ensures that null is returned after checking the players (using the disproveSuggestion method
		assertEquals(null, board.handleSuggestion(suggestion, players, mable));
	
		//new suggestion that 2 players could disprove
		suggestion = new Solution(lobbyCard, pianoCard, bleachCard);
		
		//ensure that only michael will return his card since he came first
		assertEquals(lobbyCard, board.handleSuggestion(suggestion, players, mable));
		//make sure baiza does not have a chance to return his card with this suggestion
		assertFalse(pianoCard.equals(board.handleSuggestion(suggestion, players, mable)));
		
		//new suggestion that only mable (the one suggesting) can disprove
		suggestion = new Solution(elevatorCard, baizaCard, bleachCard);
		
		//make sure null is returned since mable is the only one who can disprove it
		assertEquals(null, board.handleSuggestion(suggestion, players, mable));
		
		//make sure the card is returned in mable is not the one suggesting
		assertEquals(elevatorCard, board.handleSuggestion(suggestion, players, baiza));
	}
}
