package tests;

import static org.junit.Assert.*;

import java.awt.Color;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import clueGame.Board;
import clueGame.BoardCell;
import clueGame.Card;
import clueGame.CardType;
import clueGame.ComputerPlayer;
import clueGame.Solution;

public class ComputerAITest {
	private static Board board;
	private static ComputerPlayer baiza;
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
	private static Card poolStickCard;
	private static Card fryingPanCard;
	private static Card custodianCard;
	private static Card deskAssistantCard;
	
	@BeforeAll
	public static void setUp() {
		// Board is singleton, get the only instance
		board = Board.getInstance();
		// set the file names to use my config files
		board.setConfigFiles("data/ClueLayout.csv", "data/ClueSetup.txt");
		board.initialize();

	}

	@BeforeEach
	public void setUpCardsPlayers() {

		//make a new baiza player for each test to reset his data
		baiza = new ComputerPlayer("Resident Baiza",Color.orange, 25, 17);
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

		poolStickCard = new Card("Pool Stick", CardType.WEAPON);
		fryingPanCard = new Card("Frying Pan", CardType.WEAPON);
		custodianCard = new Card("Custodian", CardType.PERSON);
		deskAssistantCard = new Card("Desk Assistant", CardType.PERSON);
	}

	@Test
	public void computerSuggestionTests() {
		baiza.updateHand(michaelCard);
		baiza.updateHand(dormCard);
		baiza.updateHand(elevatorCard);

		//ensure that baiza will make a suggestion using the room he is in (dorm)
		baiza.updateLocation(23, 20);
		assertEquals(dormCard, baiza.createSuggestion(board).getRoom());

		//update seen with more weapons and more people
		baiza.updateSeen(baizaCard);
		baiza.updateSeen(mableCard);
		baiza.updateSeen(blasterCard);
		baiza.updateSeen(pianoCard);
		baiza.updateSeen(bleachCard);
		baiza.updateSeen(bikeCard);
		baiza.updateSeen(cableCard);

		//ensure that the remaining weapons (pool stick and frying pan) are chosen randomly
		int card1 = 0;
		int card2 = 0;
		for(int i = 0; i < 30; i++) {
			if(baiza.createSuggestion(board).getWeapon().equals(poolStickCard)) {
				card1 = 1;
			}
			if(baiza.createSuggestion(board).getWeapon().equals(fryingPanCard)) {
				card2 = 1;
			}
		}

		assertEquals(2, card1+card2);

		//ensure that the remaining people (custodian and Desk Assistant) are chosen randomly
		card1 = 0;
		card2 = 0;
		for(int i = 0; i < 30; i++) {
			if(baiza.createSuggestion(board).getPerson().equals(custodianCard)) {
				card1 = 1;
			}
			if(baiza.createSuggestion(board).getPerson().equals(deskAssistantCard)) {
				card2 = 1;
			}
		}
		assertEquals(2, card1+card2);
		
		//ensure that with only one weapon and person being seen, those cards are chosen
		//the desk assistant and the pool stick should be chosen (along with the dorm)
		baiza.updateSeen(custodianCard);
		baiza.updateSeen(fryingPanCard);
	
		//this should be the suggestion that baiza gives based on what he knows
		Solution suggestion = new Solution(dormCard, deskAssistantCard, poolStickCard);
		
		assertTrue(suggestion.equals(baiza.createSuggestion(board)));
	}

	@Test
	public void computerTargetTests(){
		//initialize known values of a player and a startcell for the tests
		BoardCell startCell = board.getCell(7, 5);

		//calculate the targets if there are no rooms in sight
		board.calcTargets(startCell, 1);

		//test to ensure that all of the possible targets get selected at least once when randomly selected
		int target1 = 0;
		int target2 = 0;
		int target3 = 0;
		int target4 = 0;
		for(int i = 0; i < 30; i++) {
			if(baiza.selectTarget(board).equals(board.getCell(6, 5))) {
				target1 = 1;
			}
			if(baiza.selectTarget(board).equals(board.getCell(8, 5))) {
				target2 = 1;
			}
			if(baiza.selectTarget(board).equals(board.getCell(7, 6))) {
				target3 = 1;
			}
			if(baiza.selectTarget(board).equals(board.getCell(7, 4))) {
				target4 = 1;
			}
		}
		//ensure that each target gets chosen at least once from cells that aren't rooms
		assertEquals(4, target1 + target2 + target3 + target4);

		//new startCell will have the option to enter two rooms
		startCell = board.getCell(13, 5);

		//calculate the targets now that there are two rooms in sight
		board.calcTargets(startCell, 4);

		//ensure that the two rooms will both be chosen when picked randomly
		target1 = 0;
		target2 = 0;

		for(int i = 0; i < 10; i++) {
			if(baiza.selectTarget(board).equals(board.getCell(11, 10))) {
				target1 = 1;
			}
			if(baiza.selectTarget(board).equals(board.getCell(14, 1))) {
				target2 = 1;
			}
		}
		assertEquals(2, target1 + target2);

		//change the room to be in the seen list so that it won't be chosen if two rooms are the options
		Card elevatorCard = new Card("Elevator", CardType.ROOM);
		baiza.updateSeen(elevatorCard);

		//ensure that only one room is chosen
		target1 = 0;
		target2 = 0;

		for(int i = 0; i < 10; i++) {
			//elevator
			if(baiza.selectTarget(board).equals(board.getCell(11, 10))) {
				target1 = 1;
			}
			//pool table
			if(baiza.selectTarget(board).equals(board.getCell(14, 1))) {
				target2 = 1;
			}
		}
		assertEquals(1, target1 + target2);

		//add the other room to the seen list so that both the rooms and walkways will be randomly chosen from
		Card poolTableCard = new Card("Pool Table", CardType.ROOM);
		baiza.updateSeen(poolTableCard);

		//ensure that the rooms and a few of the cells should be options to be randomly chosen
		target1 = 0;
		target2 = 0;
		target3 = 0;
		target4 = 0;
		int target5 = 0;
		for(int i = 0; i < 100; i++) {
			//elevator
			if(baiza.selectTarget(board).equals(board.getCell(11, 10))) {
				target1 = 1;
			}
			//pool table
			if(baiza.selectTarget(board).equals(board.getCell(14, 1))) {
				target2 = 1;
			}
			//random cells
			if(baiza.selectTarget(board).equals(board.getCell(16, 4))) {
				target3 = 1;
			}
			if(baiza.selectTarget(board).equals(board.getCell(11, 5))) {
				target4 = 1;
			}
			if(baiza.selectTarget(board).equals(board.getCell(13, 7))) {
				target5 = 1;
			}
		}
		assertEquals(5, target1 + target2 + target3 + target4 + target5);
	}
}
