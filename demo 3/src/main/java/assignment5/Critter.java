/*
 * CRITTERS Critter.java
 * EE422C Project 4 submission by
 * Replace <...> with your actual data.
 * <Cole Berkley>
 * <cb52459>
 * <idk>
 * Slip days used: <1>
 * Summer 2022
 */

package assignment5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.lang.reflect.*;

/* 
 * See the PDF for descriptions of the methods and fields in this
 * class. 
 * You may add fields, methods or inner classes to Critter ONLY
 * if you make your additions private; no new public, protected or
 * default-package code or data can be added to Critter.
 */

/**
 * Parent class Critter for all Critters.
 * Includes important fields such as x_coord, y_coord, energy.
 * Contains all Critter methods for simulation.
 * @author Cole Berkley
 */
public abstract class Critter {
    public enum CritterShape {
        CIRCLE,
        SQUARE,
        TRIANGLE,
        DIAMOND,
        STAR
    }

    /* the default color is white, which I hope makes critters invisible by default
     * If you change the background color of your View component, then update the default
     * color to be the same as you background
     *
     * critters must override at least one of the following three methods, it is not
     * proper for critters to remain invisible in the view
     *
     * If a critter only overrides the outline color, then it will look like a non-filled
     * shape, at least, that's the intent. You can edit these default methods however you
     * need to, but please preserve that intent as you implement them.
     */
    public javafx.scene.paint.Color viewColor() {
        return javafx.scene.paint.Color.WHITE;
    }

    public javafx.scene.paint.Color viewOutlineColor() {
        return viewColor();
    }

    public javafx.scene.paint.Color viewFillColor() {
        return viewColor();
    }

    public abstract CritterShape viewShape();

    protected final String look(int direction, boolean steps) {
        this.energy-=Params.LOOK_ENERGY_COST;
        Critter fake = new Clover();
        fake.energy = Integer.MAX_VALUE;
        fake.alive = true;
        fake.hasMoved = false;
        
        if (worldStepHappened || this.fighting) {
            fake.x_coord = this.x_coord;
            fake.y_coord = this.y_coord;
        } else {
            fake.x_coord = this.old_x;
            fake.y_coord = this.old_y;
        }

        if (steps) {
            fake.run(direction);
        } else {
            fake.walk(direction);
        }

        if (worldStepHappened || this.fighting) {
            for (Critter c : population) {
                if (c.x_coord==fake.x_coord && c.y_coord==fake.y_coord) return c.toString();
            }
        } else {
            for (Critter c : population) {
                if (c.old_x==fake.x_coord && c.old_y==fake.y_coord) return c.toString();
            }   
        }
        return null;
    }

    public static void displayWorld(Object pane) {
        Platform.runLater(() -> {
            GridPane grid = (GridPane) pane;
            grid.getChildren().clear();

            int COLS = Params.WORLD_WIDTH;
            int ROWS = Params.WORLD_HEIGHT;
            double maxCellWidth = App.GRID_MAX_WIDTH/COLS;
            double maxCellHeight = App.GRID_MAX_HEIGHT/ROWS;
            double maxImgWidth = Math.min(maxCellWidth,maxCellHeight)-3;
            double maxImgHeight = maxImgWidth;

            Border simpleBorder = new Border(new BorderStroke(
                Color.BLACK,
                BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                new BorderWidths(1)));
            
            StackPane[][] allCells = new StackPane[ROWS][COLS];
            for (int x = 0; x < COLS; x++) {
                for (int y = 0; y < ROWS; y++) {
                    StackPane cell = new StackPane();
                    cell.setPrefSize(maxCellWidth, maxCellHeight);
                    cell.setMaxSize(maxCellWidth, maxCellHeight);
                    // cell.setBorder(simpleBorder);
                    cell.setAlignment(Pos.CENTER);
                    cell.setPadding(new Insets(0));
                    grid.add(cell, x, y);
                    allCells[y][x] = cell;
                }
            }
            for (Critter c : population) {
                if (c.y_coord >= ROWS || c.y_coord <= -1) continue;
                if (c.x_coord >= COLS || c.x_coord <= -1) continue;
                StackPane parent = allCells[c.y_coord][c.x_coord];
                if (parent.getChildren().isEmpty()) {
                    ImageView iv = new ImageView(App.getImage(c.viewShape()));
                    parent.getChildren().add(iv);
                    iv.maxWidth(maxImgWidth);
                    iv.maxHeight(maxImgHeight);
                }
            }
        });
    }

    private int energy = 0;
    
    /**
     * checks energy of critter
     * @return energy
     */
    protected int getEnergy() {
    	return energy;
    	}

    private int x_coord;
    private int y_coord;

    /**
     *  Population of all critters.
     */
    private static List<Critter> population = new ArrayList<Critter>();
    /**
     * Population of all critter babies, to be added to population and cleared each world
     * time step.
     */
    private static List<Critter> babies = new ArrayList<Critter>();

    /* Gets the package name.  This assumes that Critter and its
     * subclasses are all in the same package. */
    private static String myPackage;
    
    /**
     *  hasMoved checks whether critter has moved before.
     *  Makes sure critter cannot run/walk multiple times in one step .
     */
    protected boolean hasMoved=false;
    private boolean alive;
    
    /**
     * Checks whether a critter is alive.
     * @return alive
     */
    private boolean isAlive() {
    	return alive;
    }

    static {
        myPackage = Critter.class.getPackage().toString().split(" ")[1];
    }

    private static Random rand = new Random();

    /**
     * Returns a random integer of maximum value one under "max".
     * @param max
     * @return random value
     */
    public static int getRandomInt(int max) {
        return rand.nextInt(max);
    }
    
    /**
     * Sets seed as new_seed
     * @param new_seed
     */
    public static void setSeed(long new_seed) {
        rand = new Random(new_seed);
    }

    /**
     * Create and initialize a Critter subclass.
     * critter_class_name must be the unqualified name of a concrete
     * subclass of Critter, if not, an InvalidCritterException must be
     * thrown.
     * Prepends critter_class_name with myPackage before retrieving class name.
     * Creates an instance of this critter's constructor and type-casts instance to new critter.
     * Checks for lots of exception types and throws InvalidCritterException if caught.
     * Once new critter is created, coordinates are set and critter is added to population.
     * @param critter_class_name
     * @throws InvalidCritterException
     */
    public static void createCritter(String critter_class_name)
            throws InvalidCritterException {
    	
    	String thisCritter=myPackage+"."+critter_class_name;
    	Class <?> thisClass=null;
    	Constructor<?> thisCon=null;
    	Object thisInstance=null;
    	
    	try {
    		thisClass=Class.forName(thisCritter);
    	}
    	catch(ClassNotFoundException e) {
    		throw new InvalidCritterException(critter_class_name);
    	}
    	
    	try {
    		thisCon=thisClass.getConstructor();
    		thisInstance=thisCon.newInstance();
    	}
    	catch(InstantiationException e1){
    		throw new InvalidCritterException(critter_class_name);
    	}
    	catch(NoSuchMethodException e2) {
    		throw new InvalidCritterException(critter_class_name);
    	}
    	catch(IllegalAccessException e3) {
    		throw new InvalidCritterException(critter_class_name);
    	}
    	catch(InvocationTargetException e4) {
    		throw new InvalidCritterException(critter_class_name);
    	}
    	catch(IllegalArgumentException e5) {
    		throw new InvalidCritterException(critter_class_name);
    	}
    	catch(SecurityException e6) {
    		throw new InvalidCritterException(critter_class_name);
    	}
    	
    	Critter me = (Critter)thisInstance;
    	me.energy=Params.START_ENERGY;
    	me.x_coord=getRandomInt(Params.WORLD_WIDTH);
    	me.y_coord=getRandomInt(Params.WORLD_HEIGHT);
    	population.add(me);
    	
    	
    }

    /**
     * Gets a list of critters of a specific type.
     * Prepends critter_class_name with myPackage.
     * Loops through all critters in population and adds to critters if
     * a critter is an instance of critter_class_name.
     * @param critter_class_name
     * @return critters
     * @throws InvalidCritterException
     */
    public static List<Critter> getInstances(String critter_class_name)
            throws InvalidCritterException {
    	
    	List<Critter> critters=new ArrayList<Critter>();
    	String className=myPackage+"."+critter_class_name;
    	
    	for(Critter c : population) {
    		Class<?> cClass=null;
    		try {
    			cClass=Class.forName(className);
    		}catch(Exception anyE) {
    			throw new InvalidCritterException(critter_class_name);
    		}
    		if(cClass.isInstance(c)) {
    			critters.add(c);
    		}
    	}
        return critters;
    }

    /**
     * Clear the world of all critters, dead and alive.
     * Clears both population and babies.
     */
    public static void clearWorld() {
    	population.clear();
    	babies.clear();
    }
    
    /**
     * Loops through all critters and pushes a time step for each critter.
     * Resets hasMoved to false assuming method is called every new world time step.
     * Subtracts REST_ENERGY_COST after each time step, before checking for dead.
     * If energy reaches <=0, critter is set to dead to be removed soon after.
     */
    private static void critterTimeSteps() {
    	for(Critter c : population) {
    		c.hasMoved=false;
    		c.doTimeStep();
    		c.energy-=Params.REST_ENERGY_COST;
    		if(c.energy<=0) {
    			c.alive=false;
    		}
    		else {
    			c.alive=true;
    		}
    	}
    }
    
    /**
     * Checks if a critA matches the location of parameter critB in format
     * critA.sameSpot(critB).
     * Returns true if x and y coordinates match each other, false otherwise.
     * @param critA
     * @return boolean
     */
    
    private boolean sameSpot(Critter critB) {
    	if(this.x_coord==critB.x_coord && this.y_coord == critB.y_coord) {
    		return true;
    	}
    	else {
    		return false;
    	}
    }
    
    /**
     * Gets all encounters at location of CritA.
     * Creates an array list of critters to store each critter sharing critA's location.
     * Loops through all critters in population to check locations and add to critters.
     * CritB denotes current critter in population getting its location compared with CritA.
     * Returns encounters of critA when all critters are checked.
     * @param critA
     * @return encounters
     */
    private static ArrayList<Critter> getEncounters(Critter critA){
    	ArrayList<Critter> encounters=new ArrayList<>();
    	for(Critter critB : population) {
    		if(!critB.equals(critA)) {
    			if(critA.sameSpot(critB)) {
    				encounters.add(critB);
    			}
    		}
    	}
    	return encounters;
    }
    
    
    /**
     * Resolves all encounters between all critters in population that share same spot.
     * Loops through each critter, as critA, in population and puts all encounters of critA in list "encounters".
     * If there are encounters, an inner loop thru encounters settles each encounter of critA.
     * critA, and its current encounter, critB, each get a roll: rollA and rollB. 
     * Calls fight method for both critA and critB.
     * If both critters are still alive after calling fight method and neither have ran (both remain in same spot),
     * their rolls are calculated.
     * If a critter's fight returned true, they roll an integer up to their energy level.
     * If a critter's fight returned false, their roll automatically becomes 0. Can't win a fight without fighting, nature scary.
     * The higher roll between critA and critB wins the fight, and if they're equal, critB arbitrarily wins, nature isn't fair.
     * Winner gets half the loser's energy added to their energy, loser gets set to dead to be removed soon after encounters are resolved.
     * No return value, no critter in population shares a spot once executed.
     */
    private static void doEncounters() {
    	ArrayList<Critter> encounters=new ArrayList<>();
    	
    	for(Critter critA : population) {
    		encounters=getEncounters(critA);
    		if(!encounters.isEmpty()) {
    			
    			for(Critter critB : encounters) {
    				int rollA;
    				int rollB;
                    critA.fighting = true;
                    critB.fighting = true;
    				boolean fightA=critA.fight(critB.toString());
    				boolean fightB=critB.fight(critA.toString());
                    critA.fighting = false;
                    critB.fighting = false;
    				while(critA.alive && critB.alive && critA.sameSpot(critB)) {
    					
    					if(fightA==true) {
    						rollA=Critter.getRandomInt(critA.getEnergy());
    					}else {rollA=0;}
    					
    					if(fightB==true) {
    						rollB=Critter.getRandomInt(critB.getEnergy());
    					}else {rollB=0;}
    					
    					if(rollA>rollB) {
    						critA.energy+=critB.energy/2;
    						critB.energy=0;
    						critB.alive=false;
    					}
    					else {
    						critB.energy+=critA.energy/2;
    						critA.energy=0;
    						critA.alive=false;
    					}
    				}
    			}
    		}
    	}
    }
    
    /**
     * Removes all dead critters from population.
     * Creates an array list of critters called deceased.
     * Loops through all critters in population.
     * Any critter with energy<=0 or alive set to false will be added to deceased.
     * Since population is array list, we can remove all critters in deceased from population with removeAll.
     */
    private static void deadCritters() {
    	ArrayList<Critter> deceased=new ArrayList<>();
    	for(Critter c : population) {
    		if(c.alive==false || c.energy<=0) {
    			deceased.add(c);
    		}
    	}
    	population.removeAll(deceased);
    }
    
    /**
     * Updates clover, the only critter meant to refresh its count per world time step.
     * Implements a loop that runs REFRESH_CLOVER_COUNT times, creating a new Clover per iteration.
     * Will throw an InvalidCritterException if Clover cannot be created for some reason.
     */
    private static void updateClover() {
    	for(int i=0; i<Params.REFRESH_CLOVER_COUNT; i++) {
    		try {
    			Critter.createCritter("Clover");
    		}catch(InvalidCritterException e) {
    			e.printStackTrace();
    		}
    	}
    }
    
    private void cacheOld() {
        old_x = x_coord;
        old_y = y_coord;
    }
    private int old_x;
    private int old_y;

    /**
     * worldTimeStep steps the whole simulated world through a time step by:
     * iterating a time step for each critter, 
     * Adding any newly-made babies from critterTimeSteps to population, 
     * Clearing babies in preparation for next worldTimeStep, 
     * Resolving all encounters after all critters have time-stepped, 
     * Removing all critters read as dead from population, 
     * Adding new clover to finish worldTimeStep.
     */
    public static void worldTimeStep() {
        worldStepHappened = false;
        for(Critter c : population) c.cacheOld();
    	critterTimeSteps();
    	population.addAll(babies);
    	babies.clear();
    	doEncounters();
    	deadCritters();
    	updateClover();
        worldStepHappened = true;
    }
    private static boolean worldStepHappened = false;

    /**
     * Displays the world that is to be output to the console.
     * Basically prints each spot of world in such steps:
     * Prints top left corner.
     * Prints top border.
     * Prints top right corner + a new line.
     * Nested for-loops for inner area will print a border character when on left/right border,
     * else will print empty space IF no critters occupy such space. If a critter does occupy such space,
     * their toString character will print instead of a white space.
     * New lines will be printed alongside the right-side border.
     * After nested for-loops finish, all that is left is bottom border.
     * Prints bottom left corner.
     * Prints bottom border.
     * Finishes with bottom right corner + a new line.
     */
    public static void displayWorld() {
    	
        System.out.print("+");								
        
        for(int top=0; top<Params.WORLD_WIDTH; top++) {		
        	System.out.print("-");
        }
        
        System.out.println("+");							
        
        for(int y=0; y<Params.WORLD_HEIGHT; y++) {			
        	for(int x=0; x<Params.WORLD_WIDTH; x++) {
        										
        		if(x==0) {System.out.print("|");}
        		
        		String spot=" ";
        		for(Critter c : population) {				
        			if((c.x_coord==x) && (c.y_coord==y)) {spot=c.toString();}
        		}
        		System.out.print(spot);	
        		
        		if(x==Params.WORLD_WIDTH-1) {System.out.println("|");}
        	}
        }
        
        System.out.print("+");		
        
    	for(int bot=0; bot<Params.WORLD_WIDTH; bot++) {		
    		System.out.print("-");
    	} 	
    	
    	System.out.println("+");
    }

    /**
     * Prints out how many Critters of each type there are on the
     * board.
     * Has not been modified.
     * @param critters List of Critters.
     */
    public static String runStats(List<Critter> critters) {
        String retStr = "";
        System.out.print("" + critters.size() + " critters as follows -- ");
        retStr += "" + critters.size() + " critters as follows -- ";
        Map<String, Integer> critter_count = new HashMap<String, Integer>();
        for (Critter crit : critters) {
            String crit_string = crit.toString();
            critter_count.put(crit_string,
                    critter_count.getOrDefault(crit_string, 0) + 1);
        }
        String prefix = "";
        for (String s : critter_count.keySet()) {
            System.out.print(prefix + s + ":" + critter_count.get(s));
            retStr += prefix + s + ":" + critter_count.get(s);
            prefix = ", ";
        }
        System.out.println();
        return retStr;
    }
    
    /**
     * doTimeStep() is to be implemented in each subclass of critter.
     */
    public abstract void doTimeStep();

    /**
     * fight is to be implemented in each subclass of critter.
     * @param oponent
     * @return
     */
    public abstract boolean fight(String oponent);
    private boolean fighting = false;

    /* a one-character long string that visually depicts your critter
     * in the ASCII interface */
    public String toString() {
        return "";
    }

    /**
     * Moves a critter a number of steps left or right.
     * If distance + x coordinate are under zero, critter crossed left border and must appear on the right.
     * If distance + x coordinate are over WORLD_WIDTH-1, critter crossed right border and must appear on the left.
     * If distance + x coordinate are within the borders, the sum is the new location.
     * @param distance
     * @return x_coord
     */
    private final int moveX(int distance) {
    	
    	if((distance+x_coord)<0) {return(Params.WORLD_WIDTH-distance);}
    	
    	else if((distance+x_coord>(Params.WORLD_WIDTH-1))){return (distance-1);}
    	
    	else {return x_coord+=distance;}
    }
    
    /**
     * Moves a critter a number of steps up or down.
     * If distance + y coordinate are under zero, critter crossed bottom border and must appear at the top.
     * If distance + y coordinate are over WORLD_HEIGHT-1, critter crossed top border and must appear at bottom.
     * If distance + y coordinate are within the borders, the sum is the new location.
     * @param distance
     * @return y_coord
     */
    private final int moveY(int distance) {
    	
    	if((distance+y_coord)<0) {return(Params.WORLD_HEIGHT-distance);}
    	
    	else if((distance+y_coord>(Params.WORLD_HEIGHT-1))){return (distance-1);}
    	
    	else {return y_coord+=distance;}
    }
    
    /**
     * Moves a critter one spot in a choice of 8 directions.
     * Possible directions are numbered 0-7.
     * Direction moves counterclockwise as it increases to 7, starts at center right when = 0.
     * Critters can't move if already moved during the current time step.
     * Whether a critter can move or not, WALK_ENERGY_COST will be deducted.
     * @param direction
     */
    protected final void walk(int direction) {
    	if(hasMoved==true) {
    		energy-=Params.WALK_ENERGY_COST;
    		return;
    	}
    	if(direction==0) {
    		x_coord=moveX(1);
    	}
    	else if(direction==1) {
    		x_coord=moveX(1);
    		y_coord=moveY(1);
    	}
    	else if(direction==2) {
    		y_coord=moveY(1);
    	}
    	else if(direction==3) {
    		x_coord=moveX(-1);
    		y_coord=moveY(1);
    	}
    	else if(direction==4) {
    		x_coord=moveX(-1);
    	}
    	else if(direction==5) {
    		x_coord=moveX(-1);
    		y_coord=moveY(-1);
    	}
    	else if(direction==6) {
    		y_coord=moveY(-1);
    	}
    	else if(direction==7) {
    		x_coord=moveX(1);
    		y_coord=moveY(-1);
    	}
    	energy-=Params.WALK_ENERGY_COST;
    	hasMoved=true;
    }

    /**
     * Moves a critter two spots in a choice of 8 directions.
     * Possible directions are numbered 0-7.
     * Direction moves counter clockwise as it increases to 7, starts at center right when = 0.
     * Critters can't move if already moved during the current time step.
     * Whether a critter can move or not, RUN_ENERGY_COST will be deducted.
     * @param direction
     */
    protected final void run(int direction) {
    	if(hasMoved==true) {
    		energy-=Params.RUN_ENERGY_COST;
    		return;
    	}
    	if(direction==0) {
    		x_coord=moveX(2);
    	}
    	else if(direction==1) {
    		x_coord=moveX(2);
    		y_coord=moveY(2);
    	}
    	else if(direction==2) {
    		y_coord=moveY(2);
    	}
    	else if(direction==3) {
    		x_coord=moveX(-2);
    		y_coord=moveY(2);
    	}
    	else if(direction==4) {
    		x_coord=moveX(-2);
    	}
    	else if(direction==5) {
    		x_coord=moveX(-2);
    		y_coord=moveY(-2);
    	}
    	else if(direction==6) {
    		y_coord=moveY(-2);
    	}
    	else if(direction==7) {
    		x_coord=moveX(2);
    		y_coord=moveY(-2);
    	}
    	energy-=Params.RUN_ENERGY_COST;
    	hasMoved=true;

    }

    /**
     * reproduce initializes given offspring and moves it in requested direction.
     * First checks whether parent has enough energy for such reproduction.
     * If not, the method returns without initializing baby.
     * If enough energy is supplied, offspring retrieves half of parent's energy.
     * Parent gets rounded-up whole number if the split ends up as a fraction, while baby gets the truncated number.
     * Once energy is resolved, offspring is then moved in one of the 8 directions.
     * Cannot call walk/run to move offspring since ALL coordinates must be initialized, not just changing selected coordinates.
     * Once baby is initialized and moved, it is added to babies to be added to population for next time step.
     * @param offspring
     * @param direction
     */
    protected final void reproduce(Critter offspring, int direction) {
    	
    	if(this.energy<Params.MIN_REPRODUCE_ENERGY) {return;}
    	
    	offspring.energy=(this.energy/2);
    	this.energy=(int)Math.ceil(this.energy/2);
    	
    	if(direction==0) {
    		offspring.x_coord=moveX(1);
    		offspring.y_coord=this.y_coord;
    	}
    	else if(direction==1) {
    		offspring.x_coord=moveX(1);
    		offspring.y_coord=moveY(-1);
    	}
    	else if(direction==2) {
    		offspring.x_coord=this.x_coord;
    		offspring.y_coord=moveY(-1);
    	}
    	else if(direction==3) {
    		offspring.x_coord=moveX(-1);
    		offspring.y_coord=moveY(-1);
    	}
    	else if(direction==4) {
    		offspring.x_coord=moveX(1);
    		offspring.y_coord=this.y_coord;
    	}
    	else if(direction==5) {
    		offspring.x_coord=moveX(-1);
    		offspring.y_coord=moveY(1);
    	}
    	else if(direction==6) {
    		offspring.x_coord=this.x_coord;
    		offspring.y_coord=moveY(1);
    	}
    	else if(direction==7) {
    		offspring.x_coord=moveX(1);
    		offspring.y_coord=moveY(1);
    	}
    	babies.add(offspring);
    }

    /**
     * The TestCritter class allows some critters to "cheat". If you
     * want to create tests of your Critter model, you can create
     * subclasses of this class and then use the setter functions
     * contained here.
     * <p>
     * NOTE: you must make sure that the setter functions work with
     * your implementation of Critter. That means, if you're recording
     * the positions of your critters using some sort of external grid
     * or some other data structure in addition to the x_coord and
     * y_coord functions, then you MUST update these setter functions
     * so that they correctly update your grid/data structure.
     */
    static abstract class TestCritter extends Critter {

        protected void setEnergy(int new_energy_value) {
            super.energy = new_energy_value;
        }

        protected void setX_coord(int new_x_coord) {
            super.x_coord = new_x_coord;
        }

        protected void setY_coord(int new_y_coord) {
            super.y_coord = new_y_coord;
        }

        protected int getX_coord() {
            return super.x_coord;
        }

        protected int getY_coord() {
            return super.y_coord;
        }

        /**
         * This method getPopulation has to be modified by you if you
         * are not using the population ArrayList that has been
         * provided in the starter code.  In any case, it has to be
         * implemented for grading tests to work.
         */
        protected static List<Critter> getPopulation() {
            return population;
        }

        /**
         * This method getBabies has to be modified by you if you are
         * not using the babies ArrayList that has been provided in
         * the starter code.  In any case, it has to be implemented
         * for grading tests to work.  Babies should be added to the
         * general population at either the beginning OR the end of
         * every timestep.
         */
        protected static List<Critter> getBabies() {
            return babies;
        }
    }
}
