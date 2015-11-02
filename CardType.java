package com.seamus.codenames;

import java.awt.Color;
/**
 * @author Seamus
 *	Enum for the card types used to mark the key in the Code Names game. 
 */
public enum CardType{ 
	RED_TEAM(8), BLUE_TEAM(8), ASSASSIN(1), NEUTRAL(7); 
	private CardType(int count){
		this.setCount(count);
	}
	private int count;
	
	public Color getColour(){
		switch(ordinal()){
			case 0: return Color.RED; 
			case 1: return Color.BLUE;
			case 2: return Color.BLACK;
			default: return Color.WHITE;
		}
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public void decreaseCount() {
		this.count--;
	}
}
