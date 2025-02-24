package gui;

import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import clueGame.Board;
import clueGame.Card;
import clueGame.CardType;
import clueGame.Player;
import clueGame.Room;
import clueGame.Solution;

public class Suggestion extends JDialog{
	Solution solutionSuggestion;
	Room room = null;
	JComboBox<String> personCombo;
	JComboBox<String> weaponCombo;
	Suggestion suggestion;
	Board board;
	
	public Suggestion(ClueGameFrame clueFrame) {
		super(clueFrame, "Make a Suggestion", true);
		suggestion = this;

		board = clueFrame.getBoard();
		setLayout(new GridLayout(0,2));
		setSize(250,200);
		
		//set up the roomLabel and the option for the room which can ONLY be where they currently are;
		JLabel roomLabel = new JLabel("Room");
		roomLabel.setHorizontalAlignment(JTextField.CENTER);
		add(roomLabel);
		room = board.getRoom(board.getPlayers().get(Player.getCurrPlayer()).getLocation(board));
		JTextField roomTextField = new JTextField(room.getName());
		roomTextField.setEditable(false);
		add(roomTextField);
		
		//set up the person label and dropdown menu
		JLabel personLabel = new JLabel("Person");
		personLabel.setHorizontalAlignment(JTextField.CENTER);
		add(personLabel);
		personCombo = new JComboBox<String>();
		for(int i = 0; i < clueFrame.getBoard().getPlayers().size(); i++) {
			personCombo.addItem(clueFrame.getBoard().getPlayers().get(i).getName());
		}
		add(personCombo);

		//setup the weapon label and dropdown menu
		JLabel weaponLabel = new JLabel("Weapon");
		weaponLabel.setHorizontalAlignment(JTextField.CENTER);
		add(weaponLabel);
		weaponCombo = new JComboBox<String>();
		for(int i = 0; i < clueFrame.getBoard().getWeapons().size(); i++) {
			weaponCombo.addItem(clueFrame.getBoard().getWeapons().get(i).getCardName());
		}
		add(weaponCombo);

		//setup the submit button and listener
		JButton submitButton = new JButton("Submit");
		add(submitButton);
		submitButton.addActionListener(new SubmitButtonListener());
		
		//setup the cancel button and listener
		JButton cancelButton = new JButton("Cancel");
		add(cancelButton);
		cancelButton.addActionListener(new CancelButtonListener());
		
		suggestion.setIconImage(clueFrame.getIconImage());
		
		//this is so that it is centered in the main frame
		pack();
		setLocationRelativeTo(clueFrame);
		suggestion.setVisible(true);
	}

	private class SubmitButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			//create cards based on the input from the player
			Card roomCard = new Card(room.getName(), CardType.ROOM);
			Card personCard = new Card((String) personCombo.getSelectedItem(), CardType.PERSON);
			Card weaponCard = new Card((String) weaponCombo.getSelectedItem(), CardType.WEAPON);

			//make the suggestion and close the dialog
			solutionSuggestion = new Solution(roomCard, personCard, weaponCard);
			suggestion.dispose();
		}
	}

	private class CancelButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			//if they decide to not make a suggestion, close the dialog
			suggestion.dispose();
		}
	}
	
	//return the suggestion, if null this is handled elsewhere
	public Solution getSuggestion() {
		return solutionSuggestion;
	}

}
