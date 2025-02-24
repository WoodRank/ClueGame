package gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import clueGame.Card;
import clueGame.CardType;
import clueGame.Solution;


public class Accusation extends JDialog{
	Solution solution;
	

	JComboBox<String> roomCombo;
	JComboBox<String> personCombo;
	JComboBox<String> weaponCombo;
	Accusation accusation;
	
	
	public Accusation(ClueGameFrame clueFrame)  {
		super(clueFrame, "Make an Accusation", true);
		accusation = this;
		
		setLayout(new GridLayout(0,2));
		setSize(250,200);
		
		//set up the room label and the dropdown menu with ALL options
		JLabel roomLabel = new JLabel("Room");
		roomLabel.setHorizontalAlignment(JLabel.CENTER);
		add(roomLabel);
		roomCombo = new JComboBox<String>();
		for(int i = 0; i < clueFrame.getBoard().getRooms().size(); i++) {
			roomCombo.addItem(clueFrame.getBoard().getRooms().get(i).getName());
		}
		add(roomCombo);
		
		//set up the person label and the dropdown menu
		JLabel personLabel = new JLabel("Person");
		personLabel.setHorizontalAlignment(JLabel.CENTER);
		add(personLabel);
		personCombo = new JComboBox<String>();
		for(int i = 0; i < clueFrame.getBoard().getPlayers().size(); i++) {
			personCombo.addItem(clueFrame.getBoard().getPlayers().get(i).getName());
		}
		add(personCombo);
		
		//setup the weapon labvel and the dropdown menu
		JLabel weaponLabel = new JLabel("Weapon");
		weaponLabel.setHorizontalAlignment(JLabel.CENTER);
		add(weaponLabel);
		weaponCombo = new JComboBox<String>();
		for(int i = 0; i < clueFrame.getBoard().getWeapons().size(); i++) {
			weaponCombo.addItem(clueFrame.getBoard().getWeapons().get(i).getCardName());
		}
		add(weaponCombo);
		
		//create the submit button and add the listener
		JButton submitButton = new JButton("Submit");
		add(submitButton);
		submitButton.addActionListener(new SubmitButtonListener());
		
		//create the cancel button and add the listener
		JButton cancelButton = new JButton("Cancel");
		add(cancelButton);
		cancelButton.addActionListener(new CancelButtonListener());
		
		//set the image of the accusation panel to be the same as the main frame image
		accusation.setIconImage(clueFrame.getIconImage());
		
		//ensure that the dialog box shows up centered in the main frame
		pack();
		setLocationRelativeTo(clueFrame);
		accusation.setVisible(true);
	}

	private class SubmitButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			//create cards for the options selected by the player
			Card room = new Card((String) roomCombo.getSelectedItem(), CardType.ROOM);
			Card person = new Card((String) personCombo.getSelectedItem(), CardType.PERSON);
			Card weapon = new Card((String) weaponCombo.getSelectedItem(), CardType.WEAPON);
			
			//make the accusation solution and close the dialog
			solution = new Solution(room, person, weapon);
			accusation.dispose();
		}
	}
	
	private class CancelButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			//if they cancel, do nothing and close the diaog
			accusation.dispose();
		}
	}
	//get the solution from the player, this will return if canceled, which is handled when this is instantiated
	public Solution getSolution() {
		return solution;
	}
	
}
