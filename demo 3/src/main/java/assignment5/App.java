package assignment5;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.crypto.spec.RC2ParameterSpec;

import assignment5.Critter.CritterShape;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    public static double SCREEN_MAX_HEIGHT = Screen.getPrimary().getBounds().getHeight();
    public static double SCREEN_MAX_WIDTH = Screen.getPrimary().getBounds().getWidth();
    public static double GRID_MAX_HEIGHT = 600;
    public static double GRID_MAX_WIDTH = 600;
    public static Image circleImage;
    public static Image squareImage;
    public static Image triangleImage;
    public static Image diamondImage;
    public static Image starImage;

    public static Image getImage(CritterShape critterShape) {
        switch(critterShape) {
            case CIRCLE: return circleImage;
            case SQUARE: return squareImage;
            case TRIANGLE: return triangleImage;
            case DIAMOND: return diamondImage;
            case STAR: return starImage;
            default: return null;
        }
    }
    
    @Override
    public void start(Stage stage) throws IOException {
        GridPane root = new GridPane();
        root.setHgap(0);
        root.setVgap(0);
        root.setAlignment(Pos.CENTER);
        root.setPrefSize(SCREEN_MAX_WIDTH, SCREEN_MAX_HEIGHT);
        root.setMaxSize(SCREEN_MAX_WIDTH, SCREEN_MAX_HEIGHT);
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setMaximized(true);

        Border simpleBorder = new Border(new BorderStroke(
            Color.BLACK,
            BorderStrokeStyle.SOLID,
            CornerRadii.EMPTY,
            new BorderWidths(1)));


        GridPane grid = new GridPane();
        grid.setPrefSize(GRID_MAX_WIDTH, GRID_MAX_HEIGHT);
        grid.setMaxSize(GRID_MAX_WIDTH, GRID_MAX_HEIGHT);
        grid.setBorder(simpleBorder);
        grid.setHgap(0);
        grid.setVgap(0);
        grid.setAlignment(Pos.TOP_LEFT);

        int COLS = Params.WORLD_WIDTH;
        int ROWS = Params.WORLD_HEIGHT;

        double maxCellWidth = App.GRID_MAX_WIDTH/COLS;
        double maxCellHeight = App.GRID_MAX_HEIGHT/ROWS;
        double maxImgWidth = Math.min(maxCellWidth,maxCellHeight)-3;
        double maxImgHeight = maxImgWidth;

        // download images
        circleImage = new Image("https://webstockreview.net/images/circle-vector-png-2.png", maxImgWidth, maxImgHeight, false, false, false);
        squareImage = new Image("https://www.freeiconspng.com/uploads/red-square-png-14.png", maxImgWidth, maxImgHeight, false, false, false);
        triangleImage = new Image("https://pngimg.com/uploads/triangle/triangle_PNG17.png", maxImgWidth, maxImgHeight, false, false, false);
        diamondImage = new Image("https://images.onlinelabels.com/images/clip-art/Graveman/Rhombus%20section%20marker-193077.png", maxImgWidth, maxImgHeight, false, false, false);
        starImage = new Image("https://upload.wikimedia.org/wikipedia/commons/thumb/e/e5/Full_Star_Yellow.svg/1200px-Full_Star_Yellow.svg.png", maxImgWidth, maxImgHeight, false, false, false);

        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                StackPane cell = new StackPane();
    
                cell.setPrefSize(maxCellWidth, maxCellHeight);
                cell.setMaxSize(maxCellWidth, maxCellHeight);
                cell.setBorder(simpleBorder);
                cell.setAlignment(Pos.CENTER);
                cell.setPadding(new Insets(0));
            }
        }
        root.add(grid, 0, 0);

        StackPane controls = new StackPane();
        controls.setPrefSize(SCREEN_MAX_WIDTH-GRID_MAX_WIDTH, SCREEN_MAX_HEIGHT);
        controls.setMaxSize(SCREEN_MAX_WIDTH-GRID_MAX_WIDTH, SCREEN_MAX_HEIGHT);
        controls.setBorder(simpleBorder);
        controls.setAlignment(Pos.TOP_LEFT);
        root.add(controls, 1, 0);

        {
            TextArea runStatsArea = new TextArea();
            runStatsArea.setEditable(false);

            //list of all buttons
            List<Button> allButtons = new ArrayList<Button>();

            //create a Critter button
            ArrayList<String> c = returnCritters();
            ComboBox<String> cList = new ComboBox<String>(FXCollections.observableArrayList(c));
            cList.setPromptText("Select a Critter type");
            cList.setStyle("-fx-font-family: Arial;");
            TextField numC = new TextField ();
            numC.setPromptText("amount");
            numC.setFont(new Font("Arial", numC.getFont().getSize()));
            //forces input to be integer
            numC.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if (!newValue.matches("\\d*")) {
                        numC.setText(newValue.replaceAll("[^\\d]", ""));
                    }
                }
            });
            Label errorMsg = new Label("");
            errorMsg.setTextFill(Color.RED);
            Button makeC= new Button("Make Critter");
            allButtons.add(makeC);
            makeC.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 12));
            makeC.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    createCritterFX(cList, numC, errorMsg, grid);
                }
            });
            FlowPane createCritBtns = new FlowPane((Orientation.HORIZONTAL));
            createCritBtns.getChildren().add(makeC);
            createCritBtns.getChildren().add(cList);
            createCritBtns.getChildren().add(numC);
            
            
            
            //Step Button 
            TextField numSteps = new TextField ();
            numSteps.setPromptText("amount");
            numSteps.setStyle("-fx-font-family: Arial;");
            // force the field to be numeric only
            numSteps.textProperty().addListener(new ChangeListener<String>() {
                        @Override
                        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                            if (!newValue.matches("\\d*")) {
                                numSteps.setText(newValue.replaceAll("[^\\d]", ""));
                            }
                        }
                    });
            Button stepBtn = new Button("Step");
            allButtons.add(stepBtn);
            stepBtn.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 12));
            stepBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if(!(numSteps.getText().equals("")) ){
                        errorMsg.setText("");
                        for (int i=0; i< Integer.valueOf(numSteps.getText()); i++)
                            try {
                                Critter.worldTimeStep();
                            } catch (Exception e) {}
                        numSteps.clear();
                    }
                    else{	//if no step specified
                        Critter.worldTimeStep();
                    }
                    Critter.displayWorld(grid);
                    List<String> allCritNames = returnCritters();
                    List<Critter> allCrits = new LinkedList<>();
                    for (String s : allCritNames) {
                        try {
                            allCrits.addAll(
                            Critter.getInstances(s));
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.exit(-1);
                        }
                    }
                    runStatsArea.setText(Critter.runStats(allCrits));
                }
            });
            FlowPane stepWorld = new FlowPane((Orientation.HORIZONTAL));
            stepWorld.getChildren().add(stepBtn);
            stepWorld.getChildren().add(numSteps);

            

            //Quit Button
            Button quitGame = new Button("Quit Game");
            quitGame.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 12));
            quitGame.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    System.exit(0);
                }
            });
            
            
            
            //Animate Button
            Button animationStart = new Button("Animate");
            animationStart.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 12));
            animationStart.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {            	 
                    App.startAnimation(allButtons, animationStart, grid, runStatsArea);
                }
            });

            Button animationStop = new Button("Stop");
            animationStop.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 12));
            animationStop.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    App.animationTimeline.stop(); 
                    for (Button b: allButtons){
                        b.setDisable(false);
                    }
                    animationStart.setDisable(false);
                }
            });
            FlowPane animationBtns = new FlowPane();
            animationBtns.getChildren().add(animationStart);
            animationBtns.getChildren().add(animationStop);
            
            
            //Animation Slider
            Slider s = new Slider();
            s.setStyle("-fx-font-family: Arial;");
            s.valueProperty().addListener((obs, oldVal, newVal) -> s.setValue(newVal.intValue())); // make it discrete values
            s.setMin(0);
            s.setMax(20);
            s.setValue(animationSpeed);
            s.setShowTickLabels(true);
            s.setShowTickMarks(true);
            s.setMajorTickUnit(5);
            s.setMinorTickCount(5);
            s.setBlockIncrement(10);
            s.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable,
                        Number oldVal, Number newVal) {
                    animationSpeed = newVal.intValue();	
                }
            });
            
            
            
            // Seed Button
            TextField seedNum = new TextField ();
            seedNum.setPromptText("seed");
            seedNum.setStyle("-fx-font-family: Arial;");
            // Force integers
            seedNum.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldVal, String newVal) {
                    if (!newVal.matches("\\d*")) {
                        seedNum.setText(newVal.replaceAll("[^\\d]", ""));
                    }
                }
            });
            Button setSeed = new Button("Set Seed");
            allButtons.add(setSeed);
            setSeed.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 12));
            setSeed.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (!seedNum.getText().equals("")){
                        Critter.setSeed(Integer.valueOf(seedNum.getText()));
                        seedNum.clear();
                    }
                }
            });
            FlowPane seedBtns = new FlowPane();
            seedBtns.getChildren().add(setSeed);
            seedBtns.getChildren().add(seedNum);
            
            
            
            //Clear World Button
            Button clearWorld = new Button("Clear World");
            allButtons.add(clearWorld);
            clearWorld.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 12));
            clearWorld.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                        Critter.clearWorld();
                        Critter.displayWorld(grid);
                        List<String> allCritNames = returnCritters();
                        List<Critter> allCrits = new LinkedList<>();
                        for (String s : allCritNames) {
                            try {
                                allCrits.addAll(
                                Critter.getInstances(s));
                            } catch (Exception e) {
                                e.printStackTrace();
                                System.exit(-1);
                            }
                        }
                        runStatsArea.setText(Critter.runStats(allCrits));
                }
            });
            
            
            
            Label innerTitle = new Label("Create Your Critter World!");
		    innerTitle.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));
            final VBox buttonBox = new VBox();
            buttonBox.getChildren().add(innerTitle);
            buttonBox.getChildren().add(new Label());
            buttonBox.getChildren().add(createCritBtns);
            buttonBox.getChildren().add(errorMsg);
            buttonBox.getChildren().add(stepWorld);
            buttonBox.getChildren().add(new Label());
            buttonBox.getChildren().add(animationBtns);
            buttonBox.getChildren().add(new Label("Animation Speed:"));
            buttonBox.getChildren().add(s);
            buttonBox.getChildren().add(new Label());
            buttonBox.getChildren().add(seedBtns);
            buttonBox.getChildren().add(new Label());
            buttonBox.getChildren().add(clearWorld);
            buttonBox.getChildren().add(new Label());
            buttonBox.getChildren().add(quitGame);    
            buttonBox.getChildren().add(runStatsArea);

            final GridPane conGrid = new GridPane();	//makes controller grid
            conGrid.setGridLinesVisible(false);
            conGrid.add(buttonBox, 1, 1 ,1, 1);  				
            conGrid.getColumnConstraints().add(new ColumnConstraints(10)); 
            conGrid.getRowConstraints().add(new RowConstraints(10));
            conGrid.getRowConstraints().add(new RowConstraints(350));
            conGrid.setStyle("-fx-font-family: Arial;");
            controls.getChildren().add(conGrid);
        }

        stage.show();

        // Thread thread = new Thread(new Runnable(){
        //     public void run(){
        //         Project4_Main.commandInterpreter(new Scanner(System.in), grid);
        //     }
        // });
        // thread.start();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

    /**
     * Returns all critter types in Critter. Used in createCritter button. 
     * @param Files
     */
	private static ArrayList<String> returnCritters(){
		String myPackage = Critter.class.getPackage().toString().split(" ")[1];
		File critterFolder = new File(System.getProperty("user.dir") + "/src/main/java/assignment5/" );
		File[] critterFiles= critterFolder.listFiles();
		ArrayList<String> critterList = new ArrayList<String>();
		for (int i = 0; i < critterFiles.length; i++) {
			if (critterFiles[i].isFile()) {
				String cFile = critterFiles[i].getName();
				if (cFile.length() >4){															//checks if long enough to contain .java
					if (cFile.substring(cFile.length() - 4, cFile.length()).equals(".java"));{	//checks if last 5 digits equal .java
						String className =cFile.substring(0 , cFile.length() - 5);
						Class<?> cClass;
						try {
							cClass = Class.forName(myPackage+"."+className);
							if (!Modifier.isAbstract(cClass.getModifiers()) && (Critter.class.isAssignableFrom(cClass))){
								critterList.add(className);
							}
							
						} catch (Exception e) {}
	
					}
				}
			}
		}
		return critterList;
	}

    /**
	 * Helper class for createCritter. Processes request in JavaFX.
	 * @param cList
	 * @param numCrit
	 * @param errorMsg
	 */
    static void createCritterFX (ComboBox<String> cList, TextField numCrit, Label errorMsg, Object pane){
        if((cList.getValue() != null) && !(numCrit.getText().equals("")) ){
            errorMsg.setText("");
            for (int i=0; i< Integer.valueOf(numCrit.getText()); i++)
                try {
                    Critter.createCritter((String)cList.getValue());
                } catch (InvalidCritterException e) {}
            numCrit.clear();
            cList.getSelectionModel().clearSelection();
            // Critter.displayWorld();
            Critter.displayWorld(pane);
        }
        else{
            errorMsg.setText("Select a critter and enter a number");
        }
    }

    /**
     * Implements the ongoing animation; worldTimeStep and displayWorld are called indefinitely until stopped.
     * @param allButtons
     * @param animationStart
     */
	static void startAnimation(List<Button> allButtons, Button animationStart, Object pane, TextArea runStatsArea){
		animationTimeline = new Timeline(new KeyFrame(Duration.seconds(0.5), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				for (Button butt: allButtons){
					butt.setDisable(true);
				}
				for (int i=0; i<animationSpeed; i++){
					Critter.worldTimeStep();}
					// Critter.displayWorld();
                    Critter.displayWorld(pane);
					animationStart.setDisable(true);
                    List<String> allCritNames = returnCritters();
                    List<Critter> allCrits = new LinkedList<>();
                    for (String s : allCritNames) {
                        try {
                            allCrits.addAll(
                            Critter.getInstances(s));
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.exit(-1);
                        }
                    }
                    runStatsArea.setText(Critter.runStats(allCrits));
			}
		}));
		animationTimeline.setCycleCount(Timeline.INDEFINITE);
		animationTimeline.play(); 

	}

    private static Timeline animationTimeline;
	private static Integer animationSpeed=1;

}
