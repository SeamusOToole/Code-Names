package com.seamus.codenames;

import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class CluesPanel extends JPanel {

	private ArrayList<String> clues = new ArrayList<String>();

	public CluesPanel() {
		this.setLayout(null);
	}
	public ArrayList<String> getCluesList(){//changed to return a copy of the list rather than the original
		ArrayList<String> temp = new ArrayList<String>(clues);
		return temp;
	}
	public void addWord(String clue){
		JLabel clueLabel = new JLabel(clue);
		this.add(clueLabel);
		clueLabel.setBounds(5, 5 + clues.size() * 15, 100, 15);
		clueLabel.setVisible(true);
		clues.add(clue);
	}
	public void reDrawWords(){//note that this method was not used in final version
		this.removeAll();
		ArrayList<String> temp = this.clues;
		this.clues = new ArrayList<String>();
		for (String s: temp){
			this.addWord(s);
		}
	}
}
