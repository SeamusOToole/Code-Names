/**
 * @author Seamus O'Toole
 * 
 * Simple Java implementation of the board game Code Names. 
 * Game designed by Vlaada Chvatil
 * Used without permission. This is entirely for me to mess around with learning Java, not to be published. 
 * 
 */
package com.seamus.codenames;

import java.awt.Dialog.ModalityType;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class CodeNamesGame implements ActionListener{

	private JButton btnNewGame;
	private JButton btnLoadGame;
	private JButton btnResumeGame;
	private JButton btnSaveGame;
	private JComboBox<String> startPlayerBox;
	private CardType startPlayer = CardType.RED_TEAM;
	private JFrame frame;

	private GameWindow game;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CodeNamesGame window = new CodeNamesGame();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public CodeNamesGame() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setLayout(null);
		frame.setBounds(100, 100, 220, 150);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		btnNewGame = new JButton("New Game");
		btnNewGame.addActionListener(this); 
		btnNewGame.setBounds(10, 10, 90, 30);
		btnNewGame.setBorder(null);
		frame.add(btnNewGame);

		btnResumeGame = new JButton("Resume Game");
		btnResumeGame.addActionListener(this); 
		btnResumeGame.setBounds(10, 40, 90, 30);
		btnResumeGame.setBorder(null);
		frame.add(btnResumeGame);

		btnLoadGame = new JButton("Load Game");
		btnLoadGame.addActionListener(this); 
		btnLoadGame.setBounds(10, 70, 90, 30);
		btnLoadGame.setBorder(null);
		frame.add(btnLoadGame);

		btnSaveGame = new JButton("Save Game");
		btnSaveGame.addActionListener(this); 
		btnSaveGame.setBounds(100, 70, 90, 30);
		btnSaveGame.setBorder(null);
		frame.add(btnSaveGame);

		startPlayerBox = new JComboBox<String>();
		startPlayerBox.addActionListener(this);
		startPlayerBox.addItem("Red");
		startPlayerBox.addItem("Blue");
		startPlayerBox.addItem("Random");
		startPlayerBox.setBounds(100, 10, 90, 30);
		frame.add(startPlayerBox);

	}
	private void loadGame(){
		JFileChooser chooser = new JFileChooser();
		int returnVal = chooser.showOpenDialog(btnLoadGame);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			game = new GameWindow(file);
			game.setModalityType(ModalityType.APPLICATION_MODAL);
			game.setVisible(true);
		}
	}

	void saveGame(){
		JFileChooser chooser = new JFileChooser();
		int returnVal2 = chooser.showSaveDialog(btnSaveGame);
		if (returnVal2 == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			game.saveGame(file);
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == btnNewGame){
			game = new GameWindow(this.startPlayer);
			game.setModalityType(ModalityType.APPLICATION_MODAL);
			game.setVisible(true);
		} else if (event.getSource() == startPlayerBox){
			switch((String) startPlayerBox.getSelectedItem()){
				case "Red": startPlayer = CardType.RED_TEAM; break;
				case "Blue": startPlayer = CardType.BLUE_TEAM; break;
				default: startPlayer = CardType.NEUTRAL; break;
			}
		} else if(event.getSource() == btnResumeGame){
			if (game != null){
				game.showHideKey(false); 
				game.setVisible(true);
			}
		} else if (event.getSource() == btnLoadGame){
			loadGame();
		} else if (event.getSource() == btnSaveGame){
			saveGame();
		}
	}
}

