/**
 * @author Seamus O'Toole
 * 
 * The main part of the Code Names game. 
 */

package com.seamus.codenames;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class GameWindow extends JDialog implements ActionListener{

	//I might implement variable number of words in later iterations, but keeping to standard rules for now
	public final static int NUM_WORDS = 25;
	//all the instance variables that are specific to the game
	private CardType[] codeKey = new CardType[NUM_WORDS];
	private String[] codeWords = new String[NUM_WORDS];
	private boolean[] previousGuesses = new boolean[NUM_WORDS];
	private CardType currentTeam;
	private int guessesRemaining;
	//variables below this point do not need to be saved.
	private boolean keyVisible = false;
	private char[] clueDropDown = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '\u221e'};//u221e is the infinity symbol
	private String clueNumber = "0";
	//below this are instance variables for the swing components
	private JButton btnPass;
	private JButton btnShowKey;
	private JButton btnClose;
	private JButton btnConfirmWord;
	private JPanel contentPane;
	private WordPanel[] panelArray;
	private CluesPanel previousCluesArea;
	private JLabel gameStateLabel;
	private JLabel guessesRemainingLabel;
	private JLabel redWordCountLabel;
	private JLabel blueWordCountLabel;
	private JTextField clueWordField;
	private JComboBox<String> clueNumberBox; 
	
	//getters here are defaulted to public, setters are as tight as they can be to improve encapsulation. 
	public boolean getPreviousGuesses(int i) {
		return previousGuesses[i];
	}
	void setPreviousGuesses(int i, boolean value) {	//not private as called by WordPanel
		this.previousGuesses[i] = value;
	}
	public CardType getCurrentTeam(){
		return this.currentTeam;
	}
	private void setCurrentTeam(CardType team){
		currentTeam = team;
	}
	public CardType getKey(int i){
		return codeKey[i];
	}
	public String getWord(int i){
		return codeWords[i];
	}
	private int getGuessesRemaining(){
		return this.guessesRemaining;
	}
	private String getGameState(){//this function is just to infer game state for the save method
		if (btnShowKey.isEnabled()){//this button only enabled on spymaster turn
			return "Spymaster";
		} else { 
			//if any of the word buttons are enabled we know the game is in a Spy team turn
			for (int i=0; i<NUM_WORDS; ++i){
				if (panelArray[i].button.isEnabled()) return "Spys";
			}
		}
		//if we haven't returned from the method yet, the game is over
		return "Over";
	}
	
	//Constructors
	public GameWindow(CardType startTeam) {
		this();
		this.initKey(startTeam);
		this.initWords();
		this.initLabels();
	}
	public GameWindow(File file) {//later iterations will throw exceptions from here if game file is invalid
		this();
		this.loadGame(file);
		//this.initLabels(); 	//call this instead from within the loadGame method, 
								//before setting phase but after arrays and variables are loaded  
	}
	private GameWindow() {//private as only called from the other constructors
				
		setBounds(100, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		contentPane.setBackground(Color.LIGHT_GRAY);
		setContentPane(contentPane);

		gameStateLabel = new JLabel();
		gameStateLabel.setBounds(553, 11, 221, 23);
		contentPane.add(gameStateLabel);
		
		this.btnClose = new JButton("Close");
		btnClose.addActionListener(this);
		btnClose.setBounds(586, 528, 89, 23);
		contentPane.add(btnClose);

		btnShowKey = new JButton("Show Key");
		btnShowKey.setBounds(685, 528, 89, 23);
		btnShowKey.addActionListener(this);
		btnShowKey.setEnabled(true);
		btnShowKey.setMargin(new Insets(0,0,0,0));
		contentPane.add(btnShowKey);

		clueWordField = new JTextField(20);
		clueWordField.setBounds(553, 46, 179, 20);
		clueWordField.setBorder(null);
		clueWordField.setColumns(20);
		contentPane.add(clueWordField);
		clueWordField.addActionListener(this);
		clueWordField.setEditable(true);
		clueWordField.setEnabled(true);
		
		btnConfirmWord = new JButton("Ok");
		btnConfirmWord.setBounds(766, 43, 24, 23);
		btnConfirmWord.addActionListener(this);
		btnConfirmWord.setBorder(null);
		contentPane.add(btnConfirmWord);

		panelArray = new WordPanel[NUM_WORDS];
		//constructing this array comes after setting up the words, so is in the initLabels method
				
		previousCluesArea = new CluesPanel();
		JScrollPane scroller = new JScrollPane(previousCluesArea);
		scroller.setBounds(550, 100, 246, 255);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		contentPane.add(scroller);
		
		clueNumberBox = new JComboBox<String>();
		for (int i=0; i<clueDropDown.length; ++i){
			clueNumberBox.addItem(String.valueOf(clueDropDown[i]));
		}
		clueNumberBox.setBounds(732, 45, 36, 20);
		clueNumberBox.addActionListener(this);
		contentPane.add(clueNumberBox);	
		
		btnPass = new JButton("Pass");
		btnPass.addActionListener(this);
		btnPass.setBounds(553, 463, 63, 54);
		btnPass.setEnabled(false);
		contentPane.add(btnPass);
		
		guessesRemainingLabel = new JLabel();
		guessesRemainingLabel.setBounds(553, 417, 199, 23);
		contentPane.add(guessesRemainingLabel);
		
		redWordCountLabel = new JLabel();
		redWordCountLabel.setBounds(553, 366, 221, 23);
		contentPane.add(redWordCountLabel);
		
		blueWordCountLabel = new JLabel();
		blueWordCountLabel.setBounds(553, 393, 221, 23);
		contentPane.add(blueWordCountLabel);

	}
	
	//methods for initialisation, file IO, and Action Listener
	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == btnShowKey){
			showHideKey(!keyVisible);
		} else if (event.getSource() == btnClose){
			this.setVisible(false);
		} else if (event.getSource() == btnConfirmWord){
			if (clueWordField.getText().length() > 0) {
				giveClueWord();
			}
		} else if (event.getSource() == clueNumberBox){
			clueNumber = (String) clueNumberBox.getSelectedItem();
		}
		else if (event.getSource() == btnPass) {
			this.endPlayerTurn();
		}

	}
	private void initLabels(){
		/* 
		 * If there was only a single constructor these could all be in that, 
		 * however as they depend on data from either the load file or initial setup,  
		 * they are collected here instead.
		 */
		this.setWordCountLabel();
		this.setGameStateLabel();
		for (int i=0; i<NUM_WORDS; ++i){
			panelArray[i] = new WordPanel(i, this);
			panelArray[i].setLayout(null);
			panelArray[i].setBounds(((i%5)*110)+10, ((i/5)*110)+10, 100, 100);
			panelArray[i].setBackground(Color.LIGHT_GRAY);
			contentPane.add(panelArray[i]);
		}
	}
	private void initKey(CardType startTeam){
		if (startTeam.equals(CardType.NEUTRAL)){
			Random rand = new Random();
			startTeam = rand.nextBoolean() ? CardType.RED_TEAM : CardType.BLUE_TEAM;
		}
		this.currentTeam = startTeam;
		codeKey[0]=startTeam;		//Start team has to get 1 additional correct answer
		for (int i=1; i<9; ++i){	//Each team then gets 8 words of their colour, the rest are set to white
			codeKey[i] = CardType.RED_TEAM;
			codeKey[i+8] = CardType.BLUE_TEAM;
			codeKey[i+16] = CardType.NEUTRAL;
		}
		codeKey[24] = CardType.ASSASSIN;		//One of the whites gets replaced with Black. 
		ArrayStuff.shuffleArray(codeKey);		//Randomises the array. Should this method be moved into this class?
		startTeam.setCount(9);
	}
	private void initWords(){
		File file = new File("dictionary.txt");
		Scanner scan = null;
		ArrayList<String> tempWordArray = new ArrayList<String>();
		try {
			scan = new Scanner(file);
			while (scan.hasNextLine()){
				tempWordArray.add(scan.nextLine());
			}
		} catch (FileNotFoundException fNFE){
			System.out.println("Unable to load dictionary file.");
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		} finally {
			if (scan != null) scan.close();
		}
		Random rand = new Random();
		for (int i=0; i<NUM_WORDS; ++i){
			int wordNum = rand.nextInt(tempWordArray.size());
			codeWords[i] = tempWordArray.get(wordNum);
			tempWordArray.remove(wordNum);
		}
	}
	void saveGame(File file){	//not private as called from Save button on CodeNamesGame window
		PrintWriter outFile = null;
		try {
			outFile = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			//save word list, tab separated, then a newline
			for (int i=0; i<GameWindow.NUM_WORDS; ++i){
				outFile.print(this.getWord(i)+"\t");
			}
			outFile.println();
			//save key ordinals followed by a newline
			for (int i=0; i<GameWindow.NUM_WORDS; ++i){
				outFile.print(this.getKey(i).ordinal());
			}
			outFile.println();
			//save previousGuesses array, true = 1, false = 0.
			for (int i=0; i<GameWindow.NUM_WORDS; ++i){
				if (this.previousGuesses[i]) {
					outFile.print(1);
				} else {
					outFile.print(0);
				}
			}
			outFile.println();
			//save wordcounts for red & blue teams, current team (ordinal), guesses remaining
			outFile.print(CardType.RED_TEAM.getCount());
			outFile.print(CardType.BLUE_TEAM.getCount());
			outFile.print(this.getCurrentTeam().ordinal());
			//Bug here as getGuessesRemaining() can return a number greater than 9
			if (this.getGuessesRemaining() < 9){
				outFile.print(this.getGuessesRemaining());
			} else {
				outFile.print(9);
			}
			if (btnPass.isEnabled()){
				outFile.println(1);
			} else {
				outFile.println(0);
			}
			//save current state of game ie spy or spymaster
			outFile.println(this.getGameState());
			//save previous clues
			for (String clue: this.previousCluesArea.getCluesList()){
				outFile.println(clue);
			}
		}
		catch (Exception e){
			e.printStackTrace();
		} finally {
			if (outFile != null) outFile.close();
		}
	}
	private void loadGame(File file){	//this is private as it is only called from the constructor
		Scanner scan = null;
		try {
			scan = new Scanner(file);
			//load word list
			codeWords = scan.nextLine().split("\t"); 
			//load key
			String temp = scan.nextLine();
			for (int i=0; i<NUM_WORDS; ++i){
				switch(temp.charAt(i)){
					case '0': codeKey[i] = CardType.RED_TEAM; break;
					case '1': codeKey[i] = CardType.BLUE_TEAM; break;
					case '2': codeKey[i] = CardType.ASSASSIN; break;
					default: codeKey[i] = CardType.NEUTRAL; break;
				}
			}
			//load previousGuesses boolean array
			temp = scan.nextLine();
			for (int i=0; i<NUM_WORDS; ++i){
				if (temp.charAt(i) == '1'){
					setPreviousGuesses(i, true);
				} else {
					setPreviousGuesses(i, false);	//not actually necessary as will be a newly declared boolean array
				}
			}
			//load wordcounts, current team, guesses remaining
			temp = scan.nextLine();
			CardType.RED_TEAM.setCount(Character.getNumericValue(temp.charAt(0)));
			CardType.BLUE_TEAM.setCount(Character.getNumericValue(temp.charAt(1)));
			switch (temp.charAt(2)){
				case '0': setCurrentTeam(CardType.RED_TEAM); break;
				case '1': setCurrentTeam(CardType.BLUE_TEAM); break;
			}
			guessesRemaining = Character.getNumericValue(temp.charAt(3));
			if (guessesRemaining > 8){
				guessesRemaining = 20;
			}
			//init labels here instead of the constructor as it needs words to exist but buttons from it are needed next
			this.initLabels();
			this.setGuessesLabel();
			//turn on colours of the buttons that are previously picked
			for (int i=0; i<NUM_WORDS; ++i){
				if (previousGuesses[i]){
					panelArray[i].button.setBackground(this.getKey(i).getColour());
				}
			}
			//load current state and enable appropriate buttons
			switch (scan.nextLine()){
				case "Spys": 
					enablePlayerButtons();
					disableSpymasterButtons();
					break;
				case "Spymaster":
					enableSpymasterButtons();
					disablePlayerButtons();
					break;
				default :
					disablePlayerButtons();
					disableSpymasterButtons();
					break;
			} 
			//possibly enable the Pass button (separate char just for this button). 
			if (temp.charAt(4) == '1') {
				btnPass.setEnabled(true);
			}
			//load previous clues area and redraw it
			while (scan.hasNextLine()){
				previousCluesArea.addWord(scan.nextLine());
			}
		}
		catch (Exception e) {	//will split this catch up later
			e.printStackTrace();
		}
		finally {
			if (scan != null) scan.close();
		}
	}
	
	//methods to display variable game information to users and enable/disable components
	private void setWordCountLabel(){
		String wordCount = ("<html><b><font color = red>Red words remaining: " + CardType.RED_TEAM.getCount() + "</color></b></html>"); 
		redWordCountLabel.setText(wordCount);
		wordCount = ("<html><b><font color = blue>Blue words remaining: " + CardType.BLUE_TEAM.getCount() + "</color></b></html>");
		blueWordCountLabel.setText(wordCount);
	}
	private void setGameStateLabel(){
		if (currentTeam.equals(CardType.RED_TEAM)){
			gameStateLabel.setText("<html><b><font color = red>Red team's turn</color></b></html>");
		} else {
			gameStateLabel.setText("<html><b><font color = blue>Blue team's turn</color></b></html>");
		}
	}
	private void setGuessesLabel(){
		if (guessesRemaining > 10){
			guessesRemainingLabel.setText("Guesses remaining: " + '\u221e');	//infinity symbol
		} else {
			guessesRemainingLabel.setText("Guesses remaining: " + guessesRemaining);
		}
	}
	private void disablePlayerButtons(){
		for (int i=0; i<NUM_WORDS; ++i) panelArray[i].button.setEnabled(false);
		btnPass.setEnabled(false);
	}
	private void enablePlayerButtons(){
		for (int i=0; i<NUM_WORDS; ++i)
			if (!previousGuesses[i]) panelArray[i].button.setEnabled(true);
	}
	private void disableSpymasterButtons(){
		if (keyVisible) showHideKey(false);
		btnConfirmWord.setEnabled(false);
		clueNumberBox.setEnabled(false);
		clueWordField.setEnabled(false);
		btnShowKey.setEnabled(false);
	}
	private void enableSpymasterButtons(){
		btnConfirmWord.setEnabled(true);
		clueNumberBox.setEnabled(true);
		clueWordField.setEnabled(true);
		btnShowKey.setEnabled(true);
	}
	
	//methods for game functionality
	private void endGame(boolean currentTeamWins){
		disablePlayerButtons();
		disableSpymasterButtons();
		//See truth table for XOR to explain the below logic
		if (currentTeamWins ^ currentTeam.equals(CardType.RED_TEAM)){
			//Blue Team wins
			gameStateLabel.setText("<html><b><font color = blue>Blue team wins!</color></b></html>");
		} else {
			//Red Team wins
			gameStateLabel.setText("<html><b><font color = red>Red team wins!</color></b></html>");
		}
		showHideKey(true);
	}
	void guessWord(CardType key){	//not private as called from button in WordPanel
		switch (key){
		case ASSASSIN: this.endGame(false); return;
		case NEUTRAL: this.endPlayerTurn(); return;
		default: key.decreaseCount(); break;
		}
		setWordCountLabel();
		if (currentTeam.equals(key)){
			btnPass.setEnabled(true);	//per the game rules, players can only pass after guessing at least one word
			--guessesRemaining;
			setGuessesLabel();
		} else {
			this.endPlayerTurn();
		}
		//  Check for victory condition. 
		//  Note that this also accounts for guessing the opponents last word as in that case
		//  the above endPlayerTurn would have triggered and changed currentTeam. 
		if (currentTeam.getCount() == 0) endGame (true);
		if (guessesRemaining < 1) endPlayerTurn();	
	}
	private void endPlayerTurn(){
		currentTeam = currentTeam.equals(CardType.RED_TEAM) ? CardType.BLUE_TEAM : CardType.RED_TEAM;
		setGameStateLabel();
		guessesRemainingLabel.setText("");
		disablePlayerButtons();
		enableSpymasterButtons();
	}	
	void showHideKey(boolean isRevealing){	//not private as called from resume button on CodeWordsGame window
		if (isRevealing){
			for (int i=0; i<panelArray.length; ++i){
				panelArray[i].setBackground(this.getKey(i).getColour());
			}
		} else {
			for (int i=0; i<panelArray.length; ++i){
				panelArray[i].setBackground(Color.LIGHT_GRAY);
			}
		}
		keyVisible = isRevealing;//I could put this in the if declaration, but I find it more readable this way
	}
	private void giveClueWord(){
		String word = currentTeam.equals(CardType.RED_TEAM) ? "<html><b><font color = red>" : "<html><b><font color = blue>";
		word = (word + clueWordField.getText() + " " + clueNumber + "</color></b></html>");
		previousCluesArea.addWord(word);
		switch(clueNumberBox.getSelectedIndex()){
			case 0:  
			case 9: guessesRemaining = 20; break;	//any larger number would also do. 
			default: guessesRemaining = clueNumberBox.getSelectedIndex() + 1;
		}
		clueWordField.setText("");
		clueNumberBox.setSelectedIndex(0);
		setGuessesLabel();
		disableSpymasterButtons();
		enablePlayerButtons();
	}
}
