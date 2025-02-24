package tests;


/*
 * This tests that the files are loaded properly and that everything is labeled as it should be
 */

import static org.junit.Assert.*;

import java.awt.Color;
import java.io.FileNotFoundException;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;



import clueGame.BadConfigFormatException;
import clueGame.Board;
import clueGame.BoardCell;
import clueGame.Card;
import clueGame.CardType;
import clueGame.DoorDirection;
import clueGame.Room;
import clueGame.CardType;

public class FileInitTests {
	// constants for the board
	public static final int NUM_ROWS = 26;
	public static final int NUM_COLUMNS = 25;
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
	public void testLabels() {
		// tests to see if all the labels match the symbols
		assertEquals("Lobby", board.getRoom('L').getName() );
		assertEquals("Pool Table", board.getRoom('P').getName() );
		assertEquals("Laundry", board.getRoom('Y').getName() );
		assertEquals("Kitchen", board.getRoom('K').getName() );
		assertEquals("Walkway", board.getRoom('W').getName() );
		assertEquals("Elevator", board.getRoom('E').getName() );
		assertEquals("Bike Lockers", board.getRoom('B').getName() );
		assertEquals("Dorm", board.getRoom('D').getName() );
		assertEquals("Stairwell", board.getRoom('S').getName() );
		assertEquals("Unused", board.getRoom('X').getName() );
		assertEquals("Music Room", board.getRoom('M').getName() );
	}

	@Test
	public void testBoardDimensions() {
		//make sure board is set up right
		assertEquals(NUM_ROWS, board.getNumRows());
		assertEquals(NUM_COLUMNS, board.getNumColumns());
	}

	@Test
	public void FourDoorDirections() {
		//test for each direction of doorway based on our cluelayout
		BoardCell cell = board.getCell(2, 4);
		assertTrue(cell.isDoorway());
		assertEquals(DoorDirection.LEFT, cell.getDoorDirection());
		cell = board.getCell(5, 11);
		assertTrue(cell.isDoorway());
		assertEquals(DoorDirection.UP, cell.getDoorDirection());
		cell = board.getCell(12, 7);
		assertTrue(cell.isDoorway());
		assertEquals(DoorDirection.RIGHT, cell.getDoorDirection());
		cell = board.getCell(18, 3);
		assertTrue(cell.isDoorway());
		assertEquals(DoorDirection.DOWN, cell.getDoorDirection());
		// make sure that a non-doorway is not marked as a doorway
		cell = board.getCell(0, 4);
		assertFalse(cell.isDoorway());
	}
	

	// make sure the total number of doorways is accounted for
	@Test
	public void testNumberOfDoorways() {
		int numDoors = 0;
		for (int row = 0; row < board.getNumRows(); row++)
			for (int col = 0; col < board.getNumColumns(); col++) {
				BoardCell cell = board.getCell(row, col);
				if (cell.isDoorway())
					numDoors++;
			}
		Assert.assertEquals(15, numDoors);
	}

	// Test a few room cells to ensure the room initial is correct.
	@Test
	public void testRooms() {
		// regular room label cell
		BoardCell cell = board.getCell(0, 1);
		Room room = board.getRoom(cell) ;
		assertTrue( room != null );
		assertEquals( room.getName(), "Stairwell" ) ;
		assertFalse( cell.isLabel() );
		assertFalse( cell.isRoomCenter() ) ;
		assertFalse( cell.isDoorway()) ;

		// room label cell test
		cell = board.getCell(2, 0);
		room = board.getRoom( cell ) ;
		assertTrue( room != null );
		assertEquals( room.getName(), "Stairwell" ) ;
		assertTrue( cell.isLabel() );
		assertTrue( room.getLabelCell() == cell );
		
		// room center cell test
		cell = board.getCell(23, 1);
		room = board.getRoom( cell ) ;
		assertTrue( room != null );
		assertEquals( room.getName(), "Music Room" ) ;
		assertTrue( cell.isRoomCenter() );
		assertTrue( room.getCenterCell() == cell );
		
		// secret passage cell test
		cell = board.getCell(14, 14);
		room = board.getRoom( cell ) ;
		assertTrue( room != null );
		assertEquals( room.getName(), "Elevator" ) ;
		assertTrue( cell.getSecretPassage() == 'Y' );
		
		// walkway cell test
		cell = board.getCell(7, 0);
		room = board.getRoom( cell ) ;
		assertTrue( room != null );
		assertEquals( room.getName(), "Walkway" ) ;
		assertFalse( cell.isRoomCenter() );
		assertFalse( cell.isLabel() );
		
		// unused cell test
		cell = board.getCell(25, 24);
		room = board.getRoom( cell ) ;
		assertTrue( room != null );
		assertEquals( room.getName(), "Unused" ) ;
		assertFalse( cell.isRoomCenter() );
		assertFalse( cell.isLabel() );
		
	}

}
