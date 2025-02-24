package tests;

import static org.junit.Assert.*;
import java.awt.Color;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import clueGame.Board;
import clueGame.Card;
import clueGame.CardType;


public class GameSetupTests {
	//one instance of the board
	private static Board board;

	@BeforeAll
	public static void setUp() {
		// Board is singleton, get the only instance
		board = Board.getInstance();
		// set the file names to use my config files
		board.setConfigFiles("data/ClueLayout.csv", "data/ClueSetup.txt");
		board.initialize();
		
	}
	@Test
	public void testDeck() {
		
		//Check deck size
		assertEquals(21, board.getDeck().size());
		//tests to see if rooms are in the deck
		Card card = new Card ("Lobby", CardType.ROOM);
		assertTrue(board.getDeck().get(0).equals(card));
		card = new Card("Pool Table", CardType.ROOM);
		assertTrue(board.getDeck().get(1).equals(card));
		card = new Card("Dorm", CardType.ROOM);
		assertTrue(board.getDeck().get(7).equals(card));
		
		//tests to see if players are in the deck
		card = new Card ("Mable the Moose", CardType.PERSON);
		assertTrue(board.getDeck().get(9).equals(card));
		card = new Card("Resident Baiza", CardType.PERSON);
		assertTrue(board.getDeck().get(13).equals(card));
		card = new Card("RA Michael", CardType.PERSON);
		assertTrue(board.getDeck().get(10).equals(card));

		//tests to see if weapons are in the deck
		card = new Card ("Pool Stick", CardType.WEAPON);
		assertTrue(board.getDeck().get(15).equals(card));
		card = new Card("Bleach", CardType.WEAPON);
		assertTrue(board.getDeck().get(17).equals(card));
		card = new Card("Frying Pan", CardType.WEAPON);
		assertTrue(board.getDeck().get(16).equals(card));
	}
	
	@Test
	public void testWeapons() {
		//Test to see if weapons have been loaded
		assertEquals("Pool Stick", board.getWeapons().get(0).getCardName());
		assertEquals(CardType.WEAPON, board.getWeapons().get(0).getCardType());
		
		assertEquals("Frying Pan", board.getWeapons().get(1).getCardName());
		assertEquals(CardType.WEAPON, board.getWeapons().get(5).getCardType());
		
		assertEquals("Bleach", board.getWeapons().get(2).getCardName());
		assertEquals(CardType.WEAPON, board.getWeapons().get(2).getCardType());
	}
	
	
	
	@Test
	public void testPlayersData() {
		//tests to make sure the players are within the file with correct data
		
		//checks the full data of Mable (human player)
		assertEquals("Mable the Moose", board.getPlayers().get(0).getName());
		assertEquals(Color.green, board.getPlayers().get(0).getColor());
		assertEquals(0, board.getPlayers().get(0).getRow());
		assertEquals(16, board.getPlayers().get(0).getCol());
		//checks for other qualities of various computer players;
		assertEquals("Resident Baiza", board.getPlayers().get(4).getName());
		assertEquals(Color.yellow, board.getPlayers().get(1).getColor());
		assertEquals(18, board.getPlayers().get(3).getRow());
		assertEquals(24, board.getPlayers().get(3).getCol());
	}
	
	@Test
	public void testSolution() {
		//Make sure none of the solution cards are the same
		assertFalse(board.getSolution().getPerson().equals(board.getSolution().getRoom()));
		assertFalse(board.getSolution().getPerson().equals(board.getSolution().getWeapon()));
		assertFalse(board.getSolution().getWeapon().equals(board.getSolution().getRoom()));		
	}
	@Test
	public void testPlayersHands() {
		//Testing all players hands to make sure its 3
		for (int i = 0; i < board.getPlayers().size(); i++) {
			assertEquals(3, board.getPlayers().get(i).getHand().size());
		}
	}
}
