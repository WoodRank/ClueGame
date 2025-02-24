/*
 * Authors: Thomas Manfredo and Zaudan Wawhkyung
 * Purpose: Design the board of the clue game with relevant methods and variables. This implements the singleton pattern, so only one
 * board should ever exist.
 * 
 */

package clueGame;

import java.util.Random;
import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JPanel;






public class Board extends JPanel{

	private BoardCell[][] board;
	private int numRows, numColumns;
	private String layoutConfigFile;
	private String setupConfigFile;
	private Map<Character, Room> roomMap;
	private Set<BoardCell> targets;
	private Set<BoardCell> visited;
	private ArrayList<Player> players;
	private ArrayList<Card> playerCards;
	private ArrayList<Card> weaponCards;
	private ArrayList<Room> rooms;
	private ArrayList<Card> roomCards;
	private Map<String, Color> colors;
	private Map<String, Player> playerMap;
	private ArrayList<Card> deck;
	private Solution theAnswer;
	private int disprovingPlayerIndex;


	/*
	 * variable and methods used for singleton pattern
	 */
	private static Board theInstance = new Board();
	// constructor is private to ensure only one can be created
	private Board() {
		super() ;
	}
	// this method returns the only Board
	public static Board getInstance() {
		return theInstance;
	}
	/*
	 * initialize the board (since we are using singleton pattern)
	 */
	public void initialize()
	{
		
		try {

			loadSetupConfig();			//Loads the Clue Setup file
			loadLayoutConfig();		//Loads the ClueLayout csv file


		}
		catch(FileNotFoundException exception) {
			System.out.println(exception.getMessage());

		}catch(BadConfigFormatException exception) {
			System.out.println(exception);
		}
		distributeCards();
		calcAdjacency();
		targets = new HashSet<BoardCell>();
	}

	//calcAdjacency will calculate the adjacency of each cell in the grid, using the adjList in BoardCell
	public void calcAdjacency() {
		for(int row = 0; row < numRows; row++) {
			for(int col = 0; col < numColumns; col++) {
				//checks to see if the cell should have an adjacency (not an unused cell or a non-center room cell
				if(board[row][col].getInitial() != 'X' && ((board[row][col].getInitial() != 'W' && board[row][col].isRoomCenter())
						|| board[row][col].getInitial() == 'W')) { 
					//gets the center cell of the secret passage
					if(board[row][col].getSecretPassage() != 0) {

						board[row][col].addAdjacency(getRoom(board[row][col].getSecretPassage()).getCenterCell()); 
					}
					//checks if the space is a doorway and needs to be adjacent to a room's center cell
					if(board[row][col].isDoorway()) { 
						doorwayAdj(row, col, board[row][col].getDoorDirection());
					}
					//If surrounding cells are walkway add to the adjacency list
					if(row > 0 && board[row-1][col].getInitial() == 'W') board[row][col].addAdjacency(board[row-1][col]);
					if(col > 0 && board[row][col-1].getInitial() == 'W') board[row][col].addAdjacency(board[row][col-1]);
					if(row < numRows-1 && board[row+1][col].getInitial() == 'W') board[row][col].addAdjacency(board[row+1][col]);
					if(col < numColumns-1 && board[row][col+1].getInitial() == 'W') board[row][col].addAdjacency(board[row][col+1]);
				}
			}
		}
	}
	/*
	 * Helper function for calculating adjacency when the cell is a doorway
	 */

	private void doorwayAdj(int row, int col, DoorDirection direction) {

		switch(direction) {
		case DOWN:
			board[row][col].addAdjacency(getRoom(board[row+1][col]).getCenterCell());
			getRoom(board[row+1][col]).getCenterCell().addAdjacency(board[row][col]);
			break;
		case LEFT:
			board[row][col].addAdjacency(getRoom(board[row][col-1]).getCenterCell());
			getRoom(board[row][col-1]).getCenterCell().addAdjacency(board[row][col]);
			break;
		case NONE:
			break;
		case RIGHT:
			board[row][col].addAdjacency(getRoom(board[row][col+1]).getCenterCell());
			getRoom(board[row][col+1]).getCenterCell().addAdjacency(board[row][col]);
			break;
		case UP:
			board[row][col].addAdjacency(getRoom(board[row-1][col]).getCenterCell());
			getRoom(board[row-1][col]).getCenterCell().addAdjacency(board[row][col]);
			break;
		default:
			break;
		}
	}

	//calcTargets will wipe any old visited and targets list and add the startCell to the visited list
	//then calls the recursive findAllTargets function
	public void calcTargets(BoardCell startCell, int pathLength) {
		
		targets = new HashSet<BoardCell>();
		visited = new HashSet<BoardCell>();
		if (!players.get(Player.getCurrPlayer()).wasTeleported()) visited.add(startCell);
		else {
			players.get(Player.getCurrPlayer()).setWasTeleported(false);
		}
		findAllTargets(startCell, pathLength);
		if (targets.isEmpty()) players.get(Player.getCurrPlayer()).setIsFinished(true);
	}

	//private so that the user only has access to the main, calcTargets function

	//	This function will loop through the adjacency list of the starting cell, checking to see if it can
	//	continue on based on the pathLength, and if the cell is a room or occupied, it will add to targets 
	//	either when it reaches the end of its roll or when it reaches a room

	private void findAllTargets(BoardCell thisCell, int pathLength) {
		for(BoardCell adjCell : thisCell.getAdjList()) {
			//Checks if not visited and is either not occupied or is a room
			if(!visited.contains(adjCell) && (!adjCell.isOccupied() || adjCell.isRoomCenter()) ) {
				visited.add(adjCell);
				if((pathLength == 1 || adjCell.isRoomCenter())) {
					targets.add(adjCell);
				}
				else findAllTargets(adjCell, pathLength-1);
				visited.remove(adjCell);
			}
		}

	}





	//Setting ConfigFiles
	public void setConfigFiles(String layoutConfigFile, String setupConfigFile){
		this.layoutConfigFile = layoutConfigFile;
		this.setupConfigFile = setupConfigFile;
	}

	/*
	 * Reads through the ClueLayout file and sets the numRows and numColumns
	 */
	public void setRowsColsFromFile() throws FileNotFoundException, BadConfigFormatException{
		//Setup Scanner
		FileReader tempRead = new FileReader(layoutConfigFile);
		Scanner tempReader = new Scanner(tempRead);

		//Initialized numRows and currCols
		numRows = 0;
		int currCol = 0;

		String tempString = tempReader.nextLine();			//Set a temp reader to set the columns to compare later
		String[] tempParts = tempString.split(",");		
		//Initialized numColumns to compare to currCol later
		numColumns = tempParts.length;
		//Close Scanner so it doesn't interfere
		tempReader.close();

		//Open Scanner again to read through every row and column to see if columns match
		FileReader reader = new FileReader(layoutConfigFile);
		Scanner layout = new Scanner(reader);

		String line;

		while(layout.hasNextLine()) {
			line = layout.nextLine();
			String[] parts = line.split(",");
			currCol = parts.length;

			if(currCol != numColumns) {						//Compares columns and if not same then throw badconfig exception
				throw new BadConfigFormatException("Board must be rectangular but current layout has differing amount of columns");
			}

			numRows++;

		}
		layout.close();

	}


	/*
	 * Loading the ClueSetup.txt and putting the appropriate rooms into the roomMap
	 */
	public void loadSetupConfig() throws FileNotFoundException, BadConfigFormatException {
		roomMap = new HashMap<>();
		playerMap = new HashMap<>();
		players = new ArrayList<>();
		deck = new ArrayList<>();
		rooms = new ArrayList<>();
		weaponCards = new ArrayList<>();
		playerCards = new ArrayList<>();
		roomCards = new ArrayList<>();

		//Set Colors in a map
		setColor();
		FileReader reader = new FileReader(setupConfigFile);
		Scanner setup = new Scanner(reader);
		String line;

		while(setup.hasNextLine()) {

			line = setup.nextLine();
			//Skip the comment line
			if(line.charAt(0) != '/') {
				String[] setupParts = line.split(", ");			//Parses the commas
				//Checks to make sure there is 3 parts to the type of space
				//Check to make sure the last part is a Character for the space
				//Make sure that the only categories are room or space
				if(setupParts[0].equals("Human") || setupParts[0].equals("Computer")) {
					String[] locParts = setupParts[3].split(" ");
					if(!colors.containsKey(setupParts[2]) || setupParts.length != 4) {
						throw new BadConfigFormatException();
					}
					if(setupParts[0].equals("Human")) {
						HumanPlayer player = new HumanPlayer(setupParts[1], colors.get(setupParts[2]), Integer.parseInt(locParts[0]), Integer.parseInt(locParts[1]));
						players.add(player);
						playerMap.put(player.getName(), player);
						Card card = new Card(setupParts[1], CardType.PERSON);
						deck.add(card);
						playerCards.add(card);
					}
					if(setupParts[0].equals("Computer")) {
						ComputerPlayer player = new ComputerPlayer(setupParts[1], colors.get(setupParts[2]), Integer.parseInt(locParts[0]), Integer.parseInt(locParts[1]));
						players.add(player);
						playerMap.put(player.getName(), player);
						Card card = new Card(setupParts[1], CardType.PERSON);
						deck.add(card);
						playerCards.add(card);
					}

				}
				else if(setupParts[0].equals("Weapon")) {
					if(setupParts.length != 2) {
						throw new BadConfigFormatException();
					}
					Card card = new Card(setupParts[1], CardType.WEAPON);
					weaponCards.add(card);
					deck.add(card);
				}
				else {
					if(setupParts.length != 3 || setupParts[2].length() != 1 || (!setupParts[0].equals("Room") 
							&& !setupParts[0].equals("Space") )){
						throw new BadConfigFormatException();
					}
					//Since the ClueSetup.txt has a certain structure we know that setupParts[1] has room name and the substring is to ignore the first space
					Room room = new Room(setupParts[1]);
					roomMap.put(setupParts[2].charAt(0), room);
					if(setupParts[0].equals("Room")) {
						rooms.add(room);
						Card card = new Card(setupParts[1], CardType.ROOM);
						deck.add(card);
						roomCards.add(card);
					}
				}
			}
		}
		setup.close();
	}

	/*
	 * Initialize a map with colors to setup each player's color
	 */
	public void setColor() {
		colors = new HashMap<>();
		colors.put("green" , Color.green);
		colors.put("yellow" , Color.yellow);
		colors.put("red" , new Color(241,128,126));
		colors.put("blue" , new Color(82, 210, 255));
		colors.put("orange" , Color.orange);
		colors.put("gray" , Color.LIGHT_GRAY);


	}


	/*
	 * Loading the ClueLayout.csv file and creating the board
	 */
	public void loadLayoutConfig() throws FileNotFoundException, BadConfigFormatException {
		setRowsColsFromFile();	//Set rows and cols
		board = new BoardCell[numRows][numColumns];
		FileReader reader = new FileReader(layoutConfigFile);
		Scanner boardLayout = new Scanner(reader);
		String line;

		int row = 0;

		Set<Character> roomTypes = new HashSet<Character>();
		//List of all the possible symbols with the boardcell
		roomTypes.addAll(Arrays.asList('^','<','>','v','#','*'));

		while(boardLayout.hasNextLine()) {
			line = boardLayout.nextLine();
			String[] layoutParts = line.split(",");	//Parses the commas
			//Goes through column since while loop goes through rows
			for(int col = 0; col < numColumns; col++) {
				//Checks that the cell is actually a room that exists
				if(!roomMap.containsKey(layoutParts[col].charAt(0))) {
					throw new BadConfigFormatException("This room is invalid and is not currently a part of the board setup");
				}
				//Make sure that if there is a symbol with the cell then that symbol is valid
				if(layoutParts[col].length() > 1 && (!roomTypes.contains(layoutParts[col].charAt(1)) && 
						!roomMap.containsKey(layoutParts[col].charAt(1)))) {
					throw new BadConfigFormatException();
				}
				//If cell has a symbol then check for the symbol before setting
				if (layoutParts[col].length() > 1) {
					//Initialize a boardcell and put it into the board
					board[row][col] = new BoardCell(row, col, layoutParts[col].charAt(0));
					board[row][col].setDoorDirection(checkDirection(layoutParts[col]));
					board[row][col].setLabel(checkRoomLabel(layoutParts[col]));
					board[row][col].setRoomCenter(checkRoomCenter(layoutParts[col]));
					//Checks for secret passage
					if(roomMap.containsKey(layoutParts[col].charAt(1))) {
						board[row][col].setSecretPassage(layoutParts[col].charAt(1));
					}
					//Check Room label
					if(layoutParts[col].charAt(1) == '#') {
						roomMap.get(layoutParts[col].charAt(0)).setLabelCell(board[row][col]);
					}
					//Check Room Center
					if(layoutParts[col].charAt(1) == '*') {
						roomMap.get(layoutParts[col].charAt(0)).setCenterCell(board[row][col]);

					}
					//Cells with no symbols
				}else {
					board[row][col] = new BoardCell(row, col, layoutParts[col].charAt(0));
					board[row][col].setDoorDirection(DoorDirection.NONE);
					board[row][col].setLabel(false);
					board[row][col].setRoomCenter(false);
				}
			}
			row++;	
		}	
		boardLayout.close();
		//Running through again to make sure that the secretPassage cell is connected to the center cell
		for(row = 0; row < numRows; row++) {
			for(int col = 0; col < numColumns; col++) {
				if(board[row][col].getSecretPassage() != 0) {
					getRoom(board[row][col]).getCenterCell().setSecretPassage(board[row][col].getSecretPassage());
				}
			}
		}
	}
	/*
	 * Checks for DoorDirection at the second character
	 */
	public DoorDirection checkDirection(String doorWay) {

		switch (doorWay.charAt(1)) {
		case '^':
			return DoorDirection.UP;

		case '>':
			return DoorDirection.RIGHT;

		case '<':
			return DoorDirection.LEFT;

		case 'v':
			return DoorDirection.DOWN;

		default:
			return DoorDirection.NONE;
		}
	}
	/*
	 * Checks for RoomLabel at second character
	 */
	public Boolean checkRoomLabel(String roomLabel) {
		if (roomLabel.charAt(1) == '#') {
			return true;
		}
		return false;
	}
	/*
	 * Checks for RoomCenter at second character
	 */
	public Boolean checkRoomCenter(String roomCenter) {
		if (roomCenter.charAt(1) == '*') {
			return true;
		}
		return false;
	}
	/*
	 * Make the solution and distribute the rest to the players.
	 */
	public void distributeCards() {
		Random random = new Random();
		//Make a deep copy of deck
		ArrayList<Card> deckCopy = new ArrayList<>();
		for (Card card : deck) {
			deckCopy.add(card);
		}
		//Pick one random card from each type of cards in the solution
		theAnswer = new Solution(roomCards.get(random.nextInt(roomCards.size())), playerCards.get(random.nextInt(playerCards.size())), weaponCards.get(random.nextInt(weaponCards.size())));

		deckCopy.remove(theAnswer.getRoom());
		deckCopy.remove(theAnswer.getPerson());
		deckCopy.remove(theAnswer.getWeapon());
		//Since we are removing cards after distributing when deckCopy.size() = 0 we know we distributed all cards
		while (deckCopy.size() > 0) { 
			for (int i = 0; i < players.size(); i++) {
				int rand = random.nextInt(deckCopy.size());
				players.get(i).updateHand(deckCopy.get(rand));
				//remove cards after distributing
				deckCopy.remove(rand);
			}
		}
	}
/*
 * Checks if the accusation is equal to the answer
 */
	public boolean checkAccusation(Solution accusation) {
		return accusation.equals(theAnswer);
	}
	//checking the accusation with custom answer (testing purposes)
	public boolean checkAccusation(Solution accusation, Solution theAnswer) {
		return accusation.equals(theAnswer);
	}
/*
 * Handle Suggestion by looping through players arrayList
 */
	public Card handleSuggestion(Solution suggestion, Player suggestingPlayer) {
		//Set the index to the player who is making the suggestion
		disprovingPlayerIndex = players.indexOf(suggestingPlayer);
		int counter = 0; //used to keep track of the loop
		
		//When the counter is equal to players.size() we know that the we looped around all players for suggestion
		while(counter < players.size()) {
			//Looping back to the beginning of the players arrayList
			if(disprovingPlayerIndex > players.size()-1) disprovingPlayerIndex = 0;
			//Disprove suggestion if it is disprovable 
			if(players.get(disprovingPlayerIndex).disproveSuggestion(suggestion) != null && !players.get(disprovingPlayerIndex).equals(suggestingPlayer)) {
				return players.get(disprovingPlayerIndex).disproveSuggestion(suggestion);
			}	
			disprovingPlayerIndex++;
			counter++;
		}
		return null;
	}
	
	//handling the suggestion with custom player list (for testing purposes)
	public Card handleSuggestion(Solution suggestion, ArrayList<Player> players, Player suggestingPlayer) {
		//Set the index to the player who is making the suggestion
		disprovingPlayerIndex = players.indexOf(suggestingPlayer);
		int counter = 0; //used to keep track of the loop
		
		//When the counter is equal to players.size() we know that the we looped around all players for suggestion
		while(counter < players.size()) {
			//Looping back to the beginning of the players arrayList
			if(disprovingPlayerIndex > players.size()-1) disprovingPlayerIndex = 0;
			//Disprove suggestion if it is disprovable 
			if(players.get(disprovingPlayerIndex).disproveSuggestion(suggestion) != null && !players.get(disprovingPlayerIndex).equals(suggestingPlayer)) {
				return players.get(disprovingPlayerIndex).disproveSuggestion(suggestion);
			}	
			disprovingPlayerIndex++;
			counter++;
		}
		return null;
	}
	
	/*
	 * Returns random int for a dice roll
	 */
	public int rollDice() {
		Random random = new Random();
		return random.nextInt(6)+1;
	}
	public Set<BoardCell> getTargets(){
		return targets;
	}
	//return rows and columns
	public int getNumRows() {
		return numRows;
	}
	public int getNumColumns() {
		return numColumns;
	}
	public Room getRoom(char symbol) {
		return roomMap.get(symbol);
	}
	public Room getRoom(BoardCell cell) {
		return roomMap.get(cell.getInitial());
	}
	public BoardCell getCell(int row, int col) {
		return board[row][col];
	}
	public Set<BoardCell> getAdjList(int row, int col){
		return board[row][col].getAdjList();
	}
	public Player getPlayer(String player) {
		return playerMap.get(player);
	}
	
	public ArrayList<Player> getPlayers(){
		return players;
	}
	public ArrayList<Card> getWeapons() {
		return weaponCards;
	}
	public ArrayList<Room> getRooms() {
		return rooms;
	}
	public ArrayList<Card> getPlayerCards() {
		return playerCards;
	}
	public ArrayList<Card> getWeaponCards() {
		return weaponCards;
	}
	public ArrayList<Card> getRoomCards() {
		return roomCards;
	}
	public ArrayList<Card> getDeck(){
		return deck;
	}
	public Solution getSolution() {
		return theAnswer;
	}
	public int getDisprovingPlayerIndex() {
		return disprovingPlayerIndex;
	}
}
