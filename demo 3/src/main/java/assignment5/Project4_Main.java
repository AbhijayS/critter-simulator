/*
 * CRITTERS Main.java
 * EE422C Project 4 submission by
 * Replace <...> with your actual data.
 * <Cole Berkley>
 * <cb52459>
 * <idk>
 * Slip days used: <1>
 * Summer 2022
 */

package assignment5;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Scanner;

import javafx.scene.layout.GridPane;

import java.util.List;

/*
 * Usage: java <pkg name>.Main <input file> test input file is
 * optional.  If input file is specified, the word 'test' is optional.
 * May not use 'test' argument without specifying input file.
 */

public class Project4_Main {

    /* Scanner connected to keyboard input, or input file */
    static Scanner kb;

    /* Input file, used instead of keyboard input if specified */
    private static String inputFile;

    /* If test specified, holds all console output */
    static ByteArrayOutputStream testOutputString;

    /* Use it or not, as you wish! */
    private static boolean DEBUG = false;

    /* if you want to restore output to console */
    static PrintStream old = System.out;

    /* Gets the package name.  The usage assumes that Critter and its
       subclasses are all in the same package. */
    private static String myPackage; // package of Critter file.

    /* Critter cannot be in default pkg. */
    static {
        myPackage = Critter.class.getPackage().toString().split(" ")[1];
    }

    /* Do not alter the code above for your submission. */

    /**
     * commandInterpreter interprets any given command input by the user.
     * boolean end will change to true when user exits, exiting the command-reading loop and terminating the program.
     * While end is false, commandInterpreter interprets input in such stages:
     * Prints "critters>" before any input.
     * Creates an array cArray, where each text separated with a space is its own string within cArray to be read.
     * First checks if command = quit, will print error if quit is followed by anything else, end=true if so and program terminates.
     * Checks if first string = show, prints error if followed by extra words, else will display current world.
     * Checks if first string = step, if followed by more than one command, an error prints.
     * 	If step is followed by nothing, read as an IndexOutOfBoundsException, it'll be caught to just print a singular worldTimeStep.
     * 	If neither NumberFormatException or IndexOutOfBoundsException are caught, step is followed by a number and will worldTimeStep that amount of times.
     * Checks if first string = seed and is followed by a number, will parse that number and set it as a seed if so. Catches any relative exceptions AKA no number/not number.
     * 	If there is no number following seed, a worldTimeStep will take place.
     * Checks if first string = create, will try to create singular critter if cArray = 2, exceptions thrown if not a valid class to create.
     * 	If cArray=3, attempts to create such critter cArray[2] times, exceptions thrown if not a valid class or number.
     * Checks if first string = stats, such a command must be of length two, retrieves stats from desired class if possible.
     * 	Prepends class with myPackage name before getting instances of class, cannot getInstances of parent class Critter as it cannot be instantiated.
     * 	Invokes runStats method from desired class in non-discriminatory fashion AKA type-casting.
     * Finally, checks if first string = clear, where the world will be cleared, exceptions thrown if there is extra text after clear.
     * If first string doesn't equal any of these commands, system prints that it cannot process.
     * @param kb
     */
    public static void commandInterpreter(Scanner kb, Object pane) {
    	
    	boolean end=false;
    	
    	while(!end) {
    		
    		System.out.print("critters>");
    		String command=kb.nextLine();
    		String cArray[]=command.split(" ");
    		
    		if(cArray[0].equals("quit")) {
    			if(cArray.length>1) {System.out.println("error processing: "+command);}
    			else{end=true;}
    		}
    		
    		else if(cArray[0].equals("show")) {
    			if(cArray.length>1) {System.out.println("error processing: "+command);}
    			else{Critter.displayWorld(pane);}
    		}
    		
    		else if(cArray[0].equals("step")) {
    			if(cArray.length>2) {System.out.println("error processing: "+command);}
    			try {
    				int steps=Integer.parseInt(cArray[1]);
    				for(int i=0; i<steps; i++) {Critter.worldTimeStep();}
    			}
    			catch(IndexOutOfBoundsException e) {Critter.worldTimeStep();}
    			catch(NumberFormatException e) {System.out.println("error processing: "+command);}	
    		}
    		
    		else if(cArray[0].equals("seed")) {
    			if(cArray.length>2) {System.out.println("error processing: "+command);}
    			try {
    				int thisSeed=Integer.parseInt(cArray[1]);
    				Critter.setSeed(thisSeed);
    			}
    			catch(IndexOutOfBoundsException e) {Critter.worldTimeStep();}
    			catch(NumberFormatException e) {System.out.println("error processing: "+command);}
    		}
    		
    		else if(cArray[0].equals("create")) {
    			if(cArray.length==2) {
    				try {
    					String thisCrit=cArray[1];
    					Critter.createCritter(thisCrit);
    				}
    				catch(InvalidCritterException e) {System.out.println("error processing: "+command);}
    			}
    			else if(cArray.length==3) {
    				try {
    					int count=Integer.parseInt(cArray[2]);
    					for(int i=0; i<count; i++) {
    						try {
    						String thisCrit=cArray[1];
    						Critter.createCritter(thisCrit);
    						}
    						catch(InvalidCritterException e){System.out.println("error processing: "+command);}
    					}
    				}
    				catch(NumberFormatException e) {System.out.println("error processing: "+command);}
    			}
    			else {System.out.println("error processing: "+command);}	
    		}
    		
    		else if(cArray[0].equals("stats")) {
    			if(cArray.length!=2) {System.out.println("error processing: "+command);}
    			else {
    				String thisCrit=cArray[1];
    				if(thisCrit.equals("Critter")) {System.out.println("error processing: "+command);}
    				else {
    					String thisClass=myPackage+"."+thisCrit;
    					try {
    						List<Critter> critInstances=Critter.getInstances(thisCrit);
    						Class<?> critter_class=Class.forName(thisClass);
    						Method thisMethod=critter_class.getMethod("runStats", List.class);
    						Object thisInstance=critter_class.newInstance();
    						thisMethod.invoke(thisInstance, critInstances);
    					}
    					catch(NoClassDefFoundError e) {System.out.println("error processing: "+command);}
    					catch(InvalidCritterException e) {System.out.println("error processing: "+command);}
    					catch(Exception e) {System.out.println("error processing: "+command);}
    				}
    			}
    		}
    		
    		else if(cArray[0].equals("clear")) {
    			if(cArray.length>1) {System.out.println("error processing: "+command);}
    			Critter.clearWorld();
    		}
    		
    		else {System.out.println("error processing: "+command);}
    		
    	}
    }
}
