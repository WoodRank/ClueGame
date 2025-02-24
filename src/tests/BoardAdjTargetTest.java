package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import clueGame.Board;
import clueGame.BoardCell;

public class BoardAdjTargetTest {
	// We make the Board static because we can load it one time and 
	// then do all the tests. 
	private static Board board;
	
	@BeforeAll
	public static void setUp() throws FileNotFoundException {
		// Board is singleton, get the only instance
		board = Board.getInstance();
		// set the file names to use my config files
		board.setConfigFiles("data/ClueLayout.csv", "data/ClueSetup.txt");		
		// Initialize will load config files 
		board.initialize();
	}

	// Ensure that player does not move around within room
	// 
	@Test
	public void testAdjacenciesRoomsDoors()
	{
		// we want to test a couple of different rooms.
		// Testing on a doorway right next to a pool table
		Set<BoardCell> testList = board.getAdjList(13, 4);
		assertEquals(3, testList.size());
		assertTrue(testList.contains(board.getCell(14, 4)));
		assertTrue(testList.contains(board.getCell(13, 5)));
		assertTrue(testList.contains(board.getCell(14, 1))); //Room
		
		// test elevator for Secret room and stuff
		testList = board.getAdjList(11, 10);
		assertEquals(3, testList.size());
		assertTrue(testList.contains(board.getCell(12, 7)));
		assertTrue(testList.contains(board.getCell(12, 15)));
		assertTrue(testList.contains(board.getCell(12, 21)));
		
		
		// Elevator doorway
		testList = board.getAdjList(12, 15);
		assertEquals(4, testList.size());
		assertTrue(testList.contains(board.getCell(11, 10)));
		assertTrue(testList.contains(board.getCell(13, 15)));
		
		//lobby doorway
		testList = board.getAdjList(20, 11);
		assertEquals(2, testList.size());
		assertTrue(testList.contains(board.getCell(23, 11)));
		assertTrue(testList.contains(board.getCell(20, 12)));
	}
	
	// Test a variety of walkway scenarios
	// These tests are Dark Orange on the planning spreadsheet
	@Test
	public void testAdjacencyWalkways()
	{
		// Test on bottom edge of board, just one walkway piece
		Set<BoardCell> testList = board.getAdjList(25, 6);
		assertEquals(1, testList.size());
		assertTrue(testList.contains(board.getCell(24, 6)));
		
		// Test near a door but not adjacent
		testList = board.getAdjList(0, 4);
		assertEquals(2, testList.size());
		assertTrue(testList.contains(board.getCell(1, 4)));
		assertTrue(testList.contains(board.getCell(0, 5)));

		// Test adjacent to walkways
		testList = board.getAdjList(16, 16);
		assertEquals(4, testList.size());
		assertTrue(testList.contains(board.getCell(16, 17)));
		assertTrue(testList.contains(board.getCell(15, 16)));
		assertTrue(testList.contains(board.getCell(17, 16)));
		assertTrue(testList.contains(board.getCell(16, 15)));

		// Test next to doorway (adjacent)
		testList = board.getAdjList(18,1);
		assertEquals(3, testList.size());
		assertTrue(testList.contains(board.getCell(18, 0)));
		assertTrue(testList.contains(board.getCell(19, 1)));
		assertTrue(testList.contains(board.getCell(18, 2)));
	
	}
	
	
	// Tests out of room center, 1, 3 and 4
	// These are LIGHT BLUE on the planning spreadsheet
	//These will make sure the secret passage works as well
	@Test
	public void testTargetsInDorm() {
		// test a roll of 1
		board.calcTargets(board.getCell(23, 20), 1);
		Set<BoardCell> targets= board.getTargets();
		assertEquals(2, targets.size());
		assertTrue(targets.contains(board.getCell(20, 18)));
		assertTrue(targets.contains(board.getCell(3, 1)));	//passage to the stairwell
		
		// test a roll of 3
		board.calcTargets(board.getCell(23, 20), 3);
		targets= board.getTargets();
		assertEquals(7, targets.size());
		assertTrue(targets.contains(board.getCell(20, 16)));
		assertTrue(targets.contains(board.getCell(22, 18)));	
		assertTrue(targets.contains(board.getCell(19, 17)));
		assertTrue(targets.contains(board.getCell(21, 17)));	
		assertTrue(targets.contains(board.getCell(3, 1)));	//passage to the stairwell
		
		// test a roll of 4
		board.calcTargets(board.getCell(23, 20), 4);
		targets= board.getTargets();
		assertEquals(11, targets.size());
		assertTrue(targets.contains(board.getCell(21, 18)));
		assertTrue(targets.contains(board.getCell(22, 17)));	
		assertTrue(targets.contains(board.getCell(18, 17)));
		assertTrue(targets.contains(board.getCell(19, 16)));
		assertTrue(targets.contains(board.getCell(3, 1)));	//passage to the stairwell
	}

	// Tests out of room center, 1 and 3
	// These are LIGHT BLUE on the planning spreadsheet
	@Test
	public void testTargetsAtDoor() {
		// test a roll of 1, at door
		board.calcTargets(board.getCell(20, 18), 1);
		Set<BoardCell> targets= board.getTargets();
		assertEquals(4, targets.size());
		assertTrue(targets.contains(board.getCell(23, 20))); //entry to dorm
		assertTrue(targets.contains(board.getCell(20, 17)));	
		assertTrue(targets.contains(board.getCell(21, 18)));	
		
		// test a roll of 3
		board.calcTargets(board.getCell(20, 18), 3);
		targets= board.getTargets();
		assertEquals(11, targets.size());
		assertTrue(targets.contains(board.getCell(23, 20))); //entry to dorm
		assertTrue(targets.contains(board.getCell(19, 20)));
		assertTrue(targets.contains(board.getCell(17, 18)));	
		assertTrue(targets.contains(board.getCell(18, 19)));
		assertTrue(targets.contains(board.getCell(19, 16)));	
	}

	@Test
	//test for adjacent to doorway
	public void testTargetsInWalkway1() {
		// test a roll of 1
		board.calcTargets(board.getCell(13, 5), 1);
		Set<BoardCell> targets= board.getTargets();
		assertEquals(4, targets.size());
		assertTrue(targets.contains(board.getCell(13, 4))); //doorway of pool table
		assertTrue(targets.contains(board.getCell(12, 5)));	
		assertTrue(targets.contains(board.getCell(14, 5)));
		
		// test a roll of 3
		board.calcTargets(board.getCell(13, 5), 3);
		targets= board.getTargets();
		assertEquals(12, targets.size());
		assertTrue(targets.contains(board.getCell(14, 1))); //entry to pool table
		assertTrue(targets.contains(board.getCell(12, 7))); //doorway of elevator
		assertTrue(targets.contains(board.getCell(10, 5)));	
		
		// test a roll of 4, to ensure both pool table and elevator are targets
		board.calcTargets(board.getCell(13, 5), 4);
		targets= board.getTargets();
		assertTrue(targets.contains(board.getCell(14, 1))); //entry to pool table
		assertTrue(targets.contains(board.getCell(11, 10))); //entry to elevator
	}

	@Test
	//test for "the middle of nowhere"
	public void testTargetsInWalkway2() {
		// test a roll of 1
		board.calcTargets(board.getCell(6, 16), 1);
		Set<BoardCell> targets= board.getTargets();
		assertEquals(4, targets.size());
		assertTrue(targets.contains(board.getCell(5, 16)));
		assertTrue(targets.contains(board.getCell(6, 17)));	
		
		// test a roll of 3
		board.calcTargets(board.getCell(6, 16), 3);
		targets= board.getTargets();
		assertEquals(16, targets.size());
		assertTrue(targets.contains(board.getCell(9, 16)));
		assertTrue(targets.contains(board.getCell(7, 18)));
		assertTrue(targets.contains(board.getCell(6, 17)));	
	}

	@Test
	// test to make sure occupied locations do not cause problems
	// Marked RED on the board (not counting secret passages)
	public void testTargetsOccupied() {
		// test a roll of 4 with 1 adjacent is blocked
		board.getCell(19, 18).setOccupied(true);
		board.calcTargets(board.getCell(20, 18), 4);
		board.getCell(19, 18).setOccupied(false);
		Set<BoardCell> targets = board.getTargets();
		assertEquals(11, targets.size());
		assertTrue(targets.contains(board.getCell(22, 18)));
		assertTrue(targets.contains(board.getCell(18, 18)));
		assertTrue(targets.contains(board.getCell(19, 15)));	
		assertFalse( targets.contains( board.getCell(19, 18))) ;
	
		// we want to make sure we can get into a room, even if flagged as occupied
		board.getCell(23, 20).setOccupied(true); //set dorm as occupied
		board.getCell(19, 18).setOccupied(true);
		board.calcTargets(board.getCell(20, 18), 1);
		board.getCell(23, 20).setOccupied(false);
		board.getCell(19, 18).setOccupied(false);
		targets= board.getTargets();
		assertEquals(3, targets.size());
		assertTrue(targets.contains(board.getCell(20, 17)));	
		assertTrue(targets.contains(board.getCell(21, 18)));	
		assertTrue(targets.contains(board.getCell(23, 20)));
		assertFalse( targets.contains( board.getCell(19, 18))) ;
		
		// check leaving a room with a blocked doorway (with only one door
		board.getCell(1, 18).setOccupied(true);
		board.calcTargets(board.getCell(3, 21), 1);
		board.getCell(1, 18).setOccupied(false);
		targets= board.getTargets();
		assertEquals(0, targets.size());
		assertFalse(targets.contains(board.getCell(1, 18)));


	}
}
