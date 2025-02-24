/*
 * Authors: Zaudan Wawhkyung and Thomas Manfredo
 * Purpose: A class for holding the answer and suggestion made by players.
 * 
 */

package clueGame;

public class Solution {
	Card room;
	Card person;
	Card weapon;
	
	public Solution(Card room, Card person, Card weapon){
		this.room = room;
		this.person = person;
		this.weapon = weapon;
	}


	public boolean equals(Solution solution) {
		return(this.room.equals(solution.getRoom()) && this.person.equals(solution.getPerson()) && this.weapon.equals(solution.getWeapon()));
	}
	
	public boolean contains(Card card) {
		
		if(card.equals(room) || card.equals(weapon) || card.equals(person)) return true;
		
		return false;
	}
	
	public Card getRoom() {
		return room;
	}
	
	public Card getPerson() {
		return person;
	}


	public Card getWeapon() {
		return weapon;
	}
	
	@Override
	public String toString() {
		return room.getCardName() + ", " + person.getCardName() + ", " + weapon.getCardName();
	}
}
