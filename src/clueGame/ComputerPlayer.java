/*
 * Authors: Zaudan Wawhkyung and Thomas Manfredo
 * Purpose: A child of Player and holds important functionality for computer players like creating suggestion and selecting targets, and 
 * holds the computer movements
 * 
 */

package clueGame;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

public class ComputerPlayer extends Player{

	public ComputerPlayer(String name, Color color, int row, int col) {
		super(name, color, row, col);
	}


	public Solution createSuggestion(Board board) {
		Random random = new Random();

		//determines the room to be suggested based on their current location
		Card roomSuggestion = new Card(board.getRoom(getLocation(board)).getName(), CardType.ROOM);

		Card personSuggestion;
		Card weaponSuggestion;
		ArrayList<Card> suggestionOptions = new ArrayList<>();



		//checks for unseen player cards
		for(Card person : board.getPlayerCards()) {
			if(!seenCards.contains(person)) suggestionOptions.add(person);
		}
		//randomly chooses a person if there are multiple unseen
		if(suggestionOptions.size()>1) {
			int rand = random.nextInt(suggestionOptions.size());
			personSuggestion = suggestionOptions.get(rand);
		}
		//chooses the one room if that is the only one unseen
		else personSuggestion = suggestionOptions.get(0);
		suggestionOptions.clear();


		//checks for unseen weapon cards
		for(Card weapon : board.getWeaponCards()) {
			if(!seenCards.contains(weapon)) suggestionOptions.add(weapon);
		}
		//randomly chooses a weapon if there are multiple unseen
		if(suggestionOptions.size()>1) {
			int rand = random.nextInt(suggestionOptions.size());
			weaponSuggestion = suggestionOptions.get(rand);
		}
		//chooses the one weapon if that is the only one unseen
		else weaponSuggestion = suggestionOptions.get(0);


		return new Solution(roomSuggestion, personSuggestion, weaponSuggestion);
	}
	@Override
	public BoardCell selectTarget(Board board) {
		Random random = new Random();
		ArrayList<BoardCell> unseenRooms = new ArrayList<>();

		if(!board.getTargets().isEmpty()) {
			//loops through the target set to check if it is a room that hasn't been seen yet
			for(BoardCell target: board.getTargets()) {

				//if it is a room and hasn't been seen, that cell will be added to the unseenRooms
				if(target.isRoomCenter()){
					if(!seenCards.contains(new Card(board.getRoom(target).getName(), CardType.ROOM))) {
						unseenRooms.add(target);
					}
				}
			}
			//unseen rooms are prioritized and selected first
			if(!unseenRooms.isEmpty()) {
				int rand = random.nextInt(unseenRooms.size());
				return unseenRooms.get(rand);
			}
			//all other cells (including seen rooms) will be randomly chosen
			else{
				int rand = random.nextInt(board.getTargets().size());
				int counter = 0;
				for(BoardCell cell : board.getTargets())
				{
					if (counter == rand)
						return cell;
					counter++;
				}
			}
		}
		//return statement needed so it will compile, but this will actually never be reached
		return null;

	}
	public Solution makeAccusation(Board board) {
		Card room = null;
		Card weapon = null;
		Card person = null;
		for (Card card : board.getDeck()) {
			if (!seenCards.contains(card)) {
				switch (card.getCardType()){
				case ROOM:
					room = card;
					break;
				case WEAPON:
					weapon = card;
					break;
				case PERSON:
					person = card;
					break;
				}
			}
		}
		return new Solution(room, person, weapon);
	}


	/*
	 * Never Really call these methods but need it for code to compile
	 */
	@Override
	public boolean getIsFinished() {
		return false;
	}
	@Override
	public void setIsFinished(boolean turn) {}

}
