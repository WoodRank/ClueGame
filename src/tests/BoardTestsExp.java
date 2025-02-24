package tests;

import experiment.TestBoard;
import experiment.TestBoardCell;
import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;


public class BoardTestsExp {
	private TestBoard board;
	
	
	@BeforeEach
	public void setUp() {
		board = new TestBoard();
	}
	
	/*
	 * Test adjacencies for several different locations
	 * Test centers and edges
	 */
	@Test
	public void TestAdjacency() {
		
		TestBoardCell cell = board.getCell(0, 0);
		Set<TestBoardCell> testList = cell.getAdjList();
		Assert.assertTrue(testList.contains(board.getCell(0, 1)));
		Assert.assertTrue(testList.contains(board.getCell(1, 0)));
		Assert.assertEquals(2, testList.size());
		
		cell = board.getCell(3, 3);
		testList = cell.getAdjList();
		Assert.assertTrue(testList.contains(board.getCell(3, 2)));
		Assert.assertTrue(testList.contains(board.getCell(2, 3)));
		Assert.assertEquals(2, testList.size());
		
		cell = board.getCell(2, 2);
		testList = cell.getAdjList();
		Assert.assertTrue(testList.contains(board.getCell(1, 2)));
		Assert.assertTrue(testList.contains(board.getCell(2, 1)));
		Assert.assertTrue(testList.contains(board.getCell(2, 3)));
		Assert.assertTrue(testList.contains(board.getCell(3, 2)));
		Assert.assertEquals(4, testList.size());
	}
	/*
	 * Test targets with several rolls and start locations
	 */
	@Test
	public void testTargetsNormal() {
		TestBoardCell cell = board.getCell(0, 0);
		board.calcTargets(cell, 3);
		Set<TestBoardCell> targets = board.getTargets();
		Assert.assertEquals(6, targets.size());
		Assert.assertTrue(targets.contains(board.getCell(3, 0)));
		Assert.assertTrue(targets.contains(board.getCell(2, 1)));
		Assert.assertTrue(targets.contains(board.getCell(0, 1)));
		Assert.assertTrue(targets.contains(board.getCell(1, 2)));
		Assert.assertTrue(targets.contains(board.getCell(0, 3)));
		Assert.assertTrue(targets.contains(board.getCell(1, 0)));
		
	}
	
	/*
	 * Test for a complex situation where there may be a cell that represents a room and another that is occupied by an opponent?
	 */
	@Test
	public void testTargetsMixed() {
		//setup occupied cells
		board.getCell(2, 1).setOccupied(true);
		board.getCell(2, 2).setRoom(true);
		TestBoardCell cell = board.getCell(0, 1);
		board.calcTargets(cell, 4);
		Set<TestBoardCell> targets = board.getTargets();
		Assert.assertEquals(6, targets.size());
		Assert.assertTrue(targets.contains(board.getCell(1, 0)));
		Assert.assertTrue(targets.contains(board.getCell(0, 3)));
		Assert.assertTrue(targets.contains(board.getCell(2, 2)));
		Assert.assertFalse(targets.contains(board.getCell(2,1))); //make sure the occupied space isn't a target
		Assert.assertFalse(targets.contains(board.getCell(3, 2))); //makes sure it doesn't keep going past the room for another target
	}
	/*
	 * Test for targets for rooms, false if room and true if not room
	 */
	@Test
	public void testTargetsRoom() {
		TestBoardCell cell = board.getCell(0,1);
		board.getCell(2, 2).setRoom(true);
		board.calcTargets(cell, 6);
		Set<TestBoardCell> targets = board.getTargets();
		Assert.assertTrue(targets.contains(board.getCell(2, 2))); //make sure it stops early at the room
		Assert.assertTrue(targets.contains(board.getCell(3, 0)));		
	}
	
	@Test
	public void testTargetsOccupied() {
		TestBoardCell cell = board.getCell(0,1);
		board.getCell(2, 3).setOccupied(true);
		board.calcTargets(cell, 6);
		Set<TestBoardCell> targets = board.getTargets();
		Assert.assertFalse(targets.contains(board.getCell(2, 3)));		//should not be a target; person there
		Assert.assertTrue(targets.contains(board.getCell(3, 0)));		//no person there; can be a target
	}
	
	
	
	
	
}
