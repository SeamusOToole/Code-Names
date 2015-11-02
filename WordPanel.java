package com.seamus.codenames;

import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class WordPanel extends JPanel{
	/**
	 * Each WordPanel is a small panel with a button for a single word. 
	 * Panel colour can be set in the main CodeNamesGame object for showing key etc. 
	 */
	JButton button = new JButton(); 
	
	WordPanel(final int i, final GameWindow gameWindow){
//		button.setBorder(null);	//this or below line accomplish the goal of removing the margin
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setBounds(10, 10, 80, 80);
		button.setText(gameWindow.getWord(i));
		if (button.getPreferredSize().getWidth() > 80) {//change from default font size for very long words
			button.setToolTipText(gameWindow.getWord(i));
			reduceFontSize();
		}
		button.setEnabled(false);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				button.setEnabled(false);	//each word can only be guessed once
				gameWindow.setPreviousGuesses(i, true);	//record that it has been guessed so other methods know not to re-enable this button
				button.setBackground(gameWindow.getKey(i).getColour());	//display the colour
				gameWindow.guessWord(gameWindow.getKey(i));	//determine the game effect of the guess
			}
		});
		this.add(button);
	}
	private void reduceFontSize(){
		Font font = button.getFont();
		int textWidth = (int) button.getPreferredSize().getWidth();
		for (int j=1; textWidth > 80; ++j){
			Font buttonFont = font.deriveFont ((float)font.getSize() - j);
            button.setFont (buttonFont);
            textWidth = (int) Math.ceil (button.getPreferredSize().getWidth());
		}
	}
	//void buttonPressed(){}	//This was added by Eclipse, doesn't seem to be required
	
}
