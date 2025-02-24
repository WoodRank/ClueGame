/*
 * Authors: Zaudan Wawhkyung and Thomas Manfredo
 * Purpose: Control the state of the game and moves players' turn to the next, make accusation, display suggestion results and shows other
 * misc. stuff
 * 
 */



package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.time.Instant;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import clueGame.Board;
import clueGame.BoardCell;
import clueGame.ComputerPlayer;
import clueGame.Player;

public class GameControlPanel extends JPanel{
	private JTextField turnTextField;
	private JTextField theGuessTextField;
	private JTextField theGuessResultTextField;
	private JTextField rollTextField;
	private GameControlPanel gameControlGUI;
	private Board board;
	private ClueGameFrame clueFrame;

	public GameControlPanel(Board board, ClueGameFrame clueFrame) {
		//create instance of the clueFrame for use in other methods
		this.clueFrame = clueFrame;
		//instance of the board used for accessing relevant elements
		this.board = board;
		//create instance of this class for use in other methods
		gameControlGUI = this;

		setLayout(new GridLayout(2,0));

		//top half of the GameControlPanel
		JPanel topControlPanel = new JPanel();
		topControlPanel.setLayout(new GridLayout(1,4));
		add(topControlPanel);

		//turn panel
		JPanel turnPanel = new JPanel();
		topControlPanel.add(turnPanel);
		//turn label
		JLabel turnLabel = new JLabel("Whose turn");
		turnPanel.add(turnLabel, BorderLayout.NORTH);
		//turn textfield
		turnTextField = new JTextField(15);
		turnTextField.setHorizontalAlignment(JTextField.CENTER);
		turnTextField.setEditable(false); //players should not edit
		turnPanel.add(turnTextField, BorderLayout.SOUTH);

		//roll panel
		JPanel rollPanel = new JPanel();
		topControlPanel.add(rollPanel);
		//roll label
		JLabel rollLabel = new JLabel("Roll:");
		rollPanel.add(rollLabel);
		//roll text field
		rollTextField = new JTextField(5);
		rollTextField.setEditable(false);
		rollTextField.setHorizontalAlignment(JTextField.CENTER);
		rollPanel.add(rollTextField);

		//Accusation button goes here
		JButton accusationButton = new JButton("Make Accusation");
		accusationButton.addActionListener(new AccusationButtonListener());
		topControlPanel.add(accusationButton);

		// Next button
		JButton nextButton = new JButton("NEXT!");
		nextButton.addActionListener(new NextButtonListener());
		topControlPanel.add(nextButton);



		//bottom half of the GameControlPanel
		JPanel bottomControlPanel = new JPanel();
		bottomControlPanel.setLayout(new GridLayout(0,2));
		add(bottomControlPanel);

		//guess panel
		JPanel guessPanel = new JPanel();
		guessPanel.setLayout(new GridLayout(1,0));
		guessPanel.setBorder(new TitledBorder(new EtchedBorder(),"Guess"));
		bottomControlPanel.add(guessPanel);
		//guess text field
		theGuessTextField = new JTextField(20);
		theGuessTextField.setEditable(false);
		guessPanel.add(theGuessTextField);

		//guess result panel
		JPanel guessResultPanel = new JPanel();
		guessResultPanel.setLayout(new GridLayout(1,0));
		guessResultPanel.setBorder(new TitledBorder(new EtchedBorder(),"Guess Result"));
		bottomControlPanel.add(guessResultPanel);
		//guess result text field
		theGuessResultTextField = new JTextField();
		theGuessResultTextField.setEditable(false);
		guessResultPanel.add(theGuessResultTextField);
	}

	private class NextButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if(Player.getCurrPlayer() == 0 && board.getTargets().isEmpty()) {
				board.getPlayers().get(0).setIsFinished(true);
			}

			if (Player.getCurrPlayer() == 0 && !board.getPlayers().get(0).getIsFinished()) {
				JOptionPane.showMessageDialog(clueFrame, "You need to Finish your Turn!","", JOptionPane.WARNING_MESSAGE);
			}else {
				Player.updateCurrPlayer();
				int currPlayer = Player.getCurrPlayer();
				int diceRoll = board.rollDice();
				gameControlGUI.setTurn(board.getPlayers().get(currPlayer), diceRoll);
				board.calcTargets(board.getPlayers().get(currPlayer).getLocation(board), diceRoll);
				//computer player turn
				if (Player.getCurrPlayer() != 0) {
					ComputerPlayer tempComp = (ComputerPlayer) board.getPlayers().get(Player.getCurrPlayer());
					//Do Accusation? 
					//if seen all cards (21-3 = 18 cards) then make accusation
					if (tempComp.getSeenCards().size() == 18) {
						//check to see if their accusation = answer (which it should)
						if (board.checkAccusation(tempComp.makeAccusation(board))) {
							//display the player winning prompt
							JOptionPane.showMessageDialog(clueFrame, "You Lose! The Computer Player won!" 
									, tempComp.getName() + " wins!" , JOptionPane.WARNING_MESSAGE);
							//close out of the game
							clueFrame.dispose();
						}
					}
					//if the targets are not empty, move them based on their target logic
					if(!board.getTargets().isEmpty()) {
						clueFrame.getBoardPanel().movePlayer(board.getPlayers().get(currPlayer).selectTarget(board), board.getPlayers().get(currPlayer));
					}
					//at the end of their turn, reset their suggestion status
					tempComp.setSuggestionStatus(false);
				}

				else {
					//if player turn, reset finished status and suggestion status
					board.getPlayers().get(0).setIsFinished(false);
					board.getPlayers().get(0).setSuggestionStatus(false);
					clueFrame.getBoardPanel().repaint();

				}

			}
		}
	}
	private class AccusationButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			//if human is not finished, allow them to make an accusation
			if(!board.getPlayers().get(0).getIsFinished()) {
				//make a new accusation
				Accusation accusation = new Accusation(clueFrame);
				//if they don't cancel the accusation, continue
				if(accusation.getSolution() != null) {
					//if their accusation = the answer
					if(board.checkAccusation(accusation.getSolution())){
						
						//end the timer started at the beginning of the game
						Instant finish = Instant.now();
						//calculate the solve time
						long timeElapsed = Duration.between(clueFrame.getStartTime(), finish).toMillis();
						int timeSeconds = (int) (timeElapsed/1000);
						String solveTime;
						
						//depending on how long it took them, change the wording of the text
						if(timeSeconds < 60) {
							solveTime = "You solved the murder in " + timeElapsed/1000 + " seconds!";
						}
						else if(timeSeconds < 120){
							solveTime = "You solved the murder in " + Math.floorDiv(timeSeconds, 60) + " minute, "  + timeSeconds % 60 + " seconds!";
						}
						else {
							solveTime = "You solved the murder in " + Math.floorDiv(timeSeconds, 60) + " minutes, "  + timeSeconds % 60 + " seconds!";
						}
						//display win text and how long it took them to solve
						JOptionPane.showMessageDialog(clueFrame, "That's right! You're quite the sleuth! ( ͡° ͜ʖ ͡°)\n" 
								+ solveTime,
								"Correct Accusation!", JOptionPane.INFORMATION_MESSAGE);
						//close game
						clueFrame.dispose();
					}
					else {
						//show the solution if incorrect
						JOptionPane.showMessageDialog(clueFrame, "That solution was incorrect! "
								+ "\nThe answer was " + board.getSolution() 
								, "Incorrect Accusation!", JOptionPane.WARNING_MESSAGE);
						//close game
						clueFrame.dispose();
					}
				}
			}
			//if it is not their time to accuse, show a message explaining they are not able to right then
			else if(Player.getCurrPlayer() != 0) {
				JOptionPane.showMessageDialog(clueFrame, "You are not able to accuse right now!"
						, "", JOptionPane.WARNING_MESSAGE);
			}

		}

	}



	//	public static void main(String[] args) {
	//		GameControlPanel panel = new GameControlPanel();  // create the panel
	//				JFrame frame = new JFrame();  // create the frame 
	//				frame.setContentPane(panel); // put the panel in the frame
	//				frame.setSize(750, 180);  // size the frame
	//				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // allow it to close
	//				frame.setVisible(true); // make it visible
	//				
	//				// test filling in the data
	//				
	//				panel.setTurn(new ComputerPlayer( "Resident Baiza",Color.orange, 25, 17), 5);
	//				panel.setGuess( "I have no guess!");
	//				panel.setGuessResult( "So you have nothing?");
	//
	//	}

	//Sets the turn of the player in the panel and the roll amount
	public void setTurn(Player player, Integer rollAmount) {
		turnTextField.setText(player.getName());
		turnTextField.setBackground(player.getColor());
		rollTextField.setText(Integer.toString(rollAmount));
		repaint();
	}

	//sets what the suggestion was from the player
	public void setGuess(String guess) {
		theGuessTextField.setText(guess);
		theGuessTextField.setBackground(board.getPlayers().get(Player.getCurrPlayer()).getColor());
		repaint();
	}

	//shows the result of the suggestion
	public void setGuessResult(String guessResult, int disprovingIndex) {
		theGuessResultTextField.setText(guessResult);
		theGuessResultTextField.setBackground(board.getPlayers().get(disprovingIndex).getColor());
		repaint();
	}
	public void setGuessResult(String guessResult) {
		theGuessResultTextField.setText(guessResult);
		theGuessResultTextField.setBackground(null);
		repaint();
	}

}
