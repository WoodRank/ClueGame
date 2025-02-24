/*
 * Authors: Zaudan Wawhkyung and Thomas Manfredo
 * Purpose: Show the cards that the player have and seen cards from other players
 * 
 */




package gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;

import clueGame.Card;
import clueGame.CardType;
import clueGame.ComputerPlayer;
import clueGame.HumanPlayer;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import clueGame.Player;

public class KnownCardsPanel extends JPanel{
	//variables for the different fields for hands/seen
	private ArrayList<JTextField> handPeopleTextFields;
	private ArrayList<JTextField> handRoomsTextFields;
	private ArrayList<JTextField> handWeaponsTextFields;
	private ArrayList<JTextField> seenPeopleTextFields;
	private ArrayList<JTextField> seenRoomsTextFields;
	private ArrayList<JTextField> seenWeaponsTextFields;

	//JPanels to be accessed for updateFields
	private JPanel peoplePanel;
	private JPanel roomsPanel;
	private JPanel weaponsPanel;
	private KnownCardsPanel cardsPanel;

	public KnownCardsPanel() {
		//create an instance of this class for use in other methods
		cardsPanel = this;
		
		//arraylist of text fields for each area of the known cards (hand/seen and the 3 card types)
		handPeopleTextFields = new ArrayList<>();
		handRoomsTextFields = new ArrayList<>();
		handWeaponsTextFields = new ArrayList<>();
		seenPeopleTextFields = new ArrayList<>();
		seenRoomsTextFields = new ArrayList<>();
		seenWeaponsTextFields = new ArrayList<>();


		setLayout(new GridLayout(3,1));
		setBorder(new TitledBorder(new EtchedBorder(), "Known Cards"));

		//Top Panel with people cards
		peoplePanel = new JPanel();
		peoplePanel.setLayout(new GridLayout(0,1));
		peoplePanel.setBorder(new TitledBorder(new EtchedBorder(), "People"));
		add(peoplePanel);


		//Middle Panel with Rooms
		roomsPanel = new JPanel();
		roomsPanel.setLayout(new GridLayout(0,1));
		roomsPanel.setBorder(new TitledBorder(new EtchedBorder(), "Rooms"));
		add(roomsPanel);
		
		//Lower Panel with Weapons
		weaponsPanel = new JPanel();
		weaponsPanel.setLayout(new GridLayout(0,1));
		weaponsPanel.setBorder(new TitledBorder(new EtchedBorder(), "Weapons"));
		add(weaponsPanel);	
		
		updateFields();
	}

	//update the fields all at once 
	public void updateFields() {
		updateFieldsHelper(peoplePanel, handPeopleTextFields, seenPeopleTextFields);
		updateFieldsHelper(roomsPanel, handRoomsTextFields, seenRoomsTextFields);		
		updateFieldsHelper(weaponsPanel, handWeaponsTextFields, seenWeaponsTextFields);
	}
	
	private void updateFieldsHelper(JPanel panel, ArrayList<JTextField> handFields, ArrayList<JTextField> seenFields) {
		//clear the panel
		panel.removeAll();

		//add the In Hand label
		JLabel inHandLabel = new JLabel("In Hand:");
		panel.add(inHandLabel);
		
		//loop through the cards in the hands if it's not empty
		if(handFields.isEmpty()) {
			JTextField none = new JTextField(15);
			none.setText("None");
			none.setEditable(false);
			panel.add(none);
		}
		else {
			for(JTextField hand: handFields) {
				panel.add(hand);
			}
		}

		//add the Seen label
		JLabel seenLabel = new JLabel("Seen:");
		panel.add(seenLabel);

		//loop through the seen cards for that type if not empty
		if(seenFields.isEmpty()) {
			JTextField none = new JTextField(15);
			none.setText("None");
			none.setEditable(false);
			panel.add(none);
		}
		else {
			for(JTextField seen: seenFields) {
				panel.add(seen);
			}
		}
		//revalidate the panel so that the cards show up and update properly
		panel.revalidate();
	}

	//color coding cards that the player sees and creating the cards into the text fields seen in the panel
	public JTextField createFieldFromCard(Player cardOwner, Card card, Boolean isHuman) {
		JTextField cardField = new JTextField(15);
		cardField.setText(card.getCardName());
		cardField.setBackground(cardOwner.getColor());
		cardField.setEditable(false);
		
		//check for what type of card and who owns it (human or computer) and add to arraylists as needed
		switch(card.getCardType()){
		case ROOM:
			if(isHuman) cardsPanel.updateHandRoomsTextFields(cardField);
			else cardsPanel.updateSeenRoomsTextFields(cardField);
			break;
		case PERSON:
			if(isHuman) cardsPanel.updateHandPeopleTextFields(cardField);
			else cardsPanel.updateSeenPeopleTextFields(cardField);
			break;
		case WEAPON:
			if(isHuman) cardsPanel.updateHandWeaponsTextFields(cardField);
			else cardsPanel.updateSeenWeaponsTextFields(cardField);
			break;
		}
		return cardField;
	}


	public static void main(String[] args) {
		Card bleachCard = new Card("Bleach", CardType.WEAPON);
		Card michaelCard = new Card("RA Michael", CardType.PERSON);
		Card elevatorCard = new Card("Elevator", CardType.ROOM);
		Card cableCard = new Card("Cable", CardType.WEAPON);
		Card mableCard = new Card("Mable the Moose", CardType.PERSON);
		Card lobbyCard = new Card("Lobby", CardType.ROOM);
		Card pianoCard = new Card("Piano", CardType.WEAPON);
		Card blasterCard = new Card("Blaster", CardType.PERSON);
		Card kitchenCard = new Card("Kitchen", CardType.ROOM);;
		Card bikeCard = new Card("Bike", CardType.WEAPON);
		Card stairwellCard = new Card("Stairwell", CardType.ROOM);
		Card dormCard = new Card("Dorm", CardType.ROOM);
		Card baizaCard = new Card("Resident Baiza", CardType.PERSON);
		Card musicCard = new Card("Music Room", CardType.ROOM);
		Card poolTableCard = new Card("Pool Table ", CardType.ROOM);
		Card poolStickCard = new Card("Pool Stick", CardType.WEAPON);
		Card custodianCard = new Card("Custodian", CardType.PERSON);
		Card bikeLockersCard = new Card("Bike Lockers", CardType.ROOM);

		KnownCardsPanel panel = new KnownCardsPanel();  // create the panel
		JFrame frame = new JFrame();  // create the frame 
		frame.setContentPane(panel); // put the panel in the frame
		frame.setSize(240, 750);  // size the frame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // allow it to close
		frame.setVisible(true); // make it visible
		
		ComputerPlayer blaster = new ComputerPlayer("Blaster", Color.LIGHT_GRAY, 0,0);
		ComputerPlayer custodian = new ComputerPlayer("Custodian", Color.cyan, 0,0);
		ComputerPlayer baiza = new ComputerPlayer("Resident Baiza", Color.orange, 0,0);
		ComputerPlayer deskAssistant = new ComputerPlayer("Desk Assistant", Color.red, 0,0);
		ComputerPlayer michael = new ComputerPlayer("RA Michael", Color.yellow, 0,0);
		HumanPlayer mable = new HumanPlayer("Mable the Moose", Color.green, 0,0);

		panel.createFieldFromCard(baiza, baizaCard, false);
		panel.createFieldFromCard(baiza, blasterCard, false);
		panel.createFieldFromCard(baiza, poolStickCard, false);
		panel.createFieldFromCard(mable, bleachCard, true);
		panel.createFieldFromCard(mable, cableCard, true);
		panel.createFieldFromCard(mable, stairwellCard, true);
		panel.createFieldFromCard(michael, dormCard, false);
		panel.createFieldFromCard(michael, custodianCard, false);
		panel.createFieldFromCard(michael, bikeLockersCard, false);
		panel.createFieldFromCard(custodian, michaelCard, false);
		panel.createFieldFromCard(custodian, kitchenCard, false);
		panel.createFieldFromCard(custodian, bikeCard, false);
		panel.createFieldFromCard(blaster, bleachCard, false);
		panel.createFieldFromCard(blaster, elevatorCard, false);
		panel.createFieldFromCard(blaster, lobbyCard, false);
		panel.createFieldFromCard(deskAssistant, musicCard, false);
		panel.createFieldFromCard(deskAssistant, mableCard, false);
		panel.createFieldFromCard(deskAssistant, poolTableCard, false);
	
		panel.updateFields();
	}
	//adding textfields to each field list as we see cards come through
	private void updateSeenPeopleTextFields(JTextField textField) {
		seenPeopleTextFields.add(textField);
	}
	private void updateSeenRoomsTextFields(JTextField textField) {
		seenRoomsTextFields.add(textField);
	}
	private void updateSeenWeaponsTextFields(JTextField textField) {
		seenWeaponsTextFields.add(textField);
	}
	private void updateHandPeopleTextFields(JTextField textField) {
		handPeopleTextFields.add(textField);
	}
	private void updateHandRoomsTextFields(JTextField textField) {
		handRoomsTextFields.add(textField);
	}
	private void updateHandWeaponsTextFields(JTextField textField) {
		handWeaponsTextFields.add(textField);
	}	
}
