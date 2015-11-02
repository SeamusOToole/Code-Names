/**
 * Class for static methods.
 * mostly created for demo purposes
 */
package com.seamus.codenames;

import java.util.Random;

/**
 * @author Séamus O'Toole
 * This is a utility class that I've used for other small projects. 
 * Using the shuffleArray and swapItems methods in this project. 
 */
public class ArrayStuff {
	public static Object[] twoDArrayToOneD(Object[][] source){
		int destinationLength = 0;
		for (Object[] array:source){
			destinationLength += array.length;
		}
		Object[] destination = new Object[destinationLength];
		int offset = 0;
		for (int i=0; i<source.length; ++i){
			for (int j=0; j<source[i].length; ++j){
				destination[j+offset] = source[i][j];
			}
			offset += source[i].length;
		}
		return destination;
	}
	public static Object[] oneDArrayToTwoD(Object[] source, Object[][] destination){
		int offset = 0;
		for (int i=0; i<source.length; ++i){
			for (int j=0; j<destination[i].length; ++j){
				 destination[i][j] = destination[j+offset];
			}
			offset += destination[i].length;
		}
		return destination;
	}

	public static void shuffleArray(Object[] source){
		Random rand = new Random();
		for (int i=0; i<source.length; ++i){
			ArrayStuff.swapItems(source, i, i + rand.nextInt(source.length - i));
		}
	}
	private static void swapItems(Object[] source, int i, int j){
		if (i==j) return;
		Object temp = source[i];
		source [i] = source [j];
		source [j] = temp;
	}
}
