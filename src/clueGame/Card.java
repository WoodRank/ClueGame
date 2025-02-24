/*
 * Authors: Zaudan Wawhkyung and Thomas Manfredo
 * Purpose: To turn different players, weapons, and rooms into a card which is part of the game
 * 
 */
package clueGame;

public class Card {

	private String cardName;
	private CardType cardType;

	public Card(String cardName, CardType cardType) {
		super();
		this.cardName = cardName;
		this.cardType = cardType;
		}
	
	@Override
	public boolean equals(Object objectCard) {
		//Made the object type so it could compare
		Card card = (Card) objectCard;
		return(cardName.equals( card.getCardName()) && cardType.equals(card.getCardType()));
	}

	@Override
	public int hashCode() {
		//Override the hashCode so .equals would work for set
	    return (cardName + cardType.toString()).hashCode();
	}
	
	public String getCardName() {
		return cardName;
	}

	public CardType getCardType() {
		return cardType;
	}
}
