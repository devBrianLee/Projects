package sokoban.ui;

import application.Main;
import application.Main.SokobanPropertyType;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JEditorPane;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;

import sokoban.file.SokobanFileLoader;
import sokoban.game.SokobanGameData;
import sokoban.game.SokobanGameStateManager;
import application.Main.SokobanPropertyType;
import properties_manager.PropertiesManager;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;

import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javax.swing.JScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class SokobanUI extends Pane {

    /**
     * The SokobanUIState represents the four screen states that are possible
     * for the Sokoban game application. Depending on which state is in current
     * use, different controls will be visible.
     */
    public enum SokobanUIState {

        SPLASH_SCREEN_STATE, PLAY_GAME_STATE, VIEW_STATS_STATE, VIEW_HELP_STATE,
        HANG1_STATE, HANG2_STATE, HANG3_STATE, HANG4_STATE, HANG5_STATE, HANG6_STATE,
    }

    // mainStage
    private Stage primaryStage;

    // mainPane
    private BorderPane mainPane;
    private BorderPane hmPane;

    // SplashScreen
    private ImageView splashScreenImageView;
    private Pane splashScreenPane;
    private Label splashScreenImageLabel;
    private FlowPane levelSelectionPane;
    private ArrayList<Button> levelButtons;

    // NorthToolBar
    private HBox northToolbar;
    private Button gameButton;
    private Button statsButton;
    private Button helpButton;
    private Button exitButton;

    // GamePane
    private Label SokobanLabel;
    private Button newGameButton;
    private HBox letterButtonsPane;
    private HashMap<Character, Button> letterButtons;
    private BorderPane gamePanel = new BorderPane();

    //StatsPane
    private ScrollPane statsScrollPane;
    private JEditorPane statsPane;

    //HelpPane
    private BorderPane helpPanel;
    private JScrollPane helpScrollPane;
    private JEditorPane helpPane;
    private Button homeButton;
    private Pane workspace;

    // Padding
    private Insets marginlessInsets;

    // Image path
    private String ImgPath = "file:images/";

    // mainPane weight && height
    private int paneWidth;
    private int paneHeigth;

    // THIS CLASS WILL HANDLE ALL ACTION EVENTS FOR THIS PROGRAM
    private SokobanEventHandler eventHandler;
    private SokobanErrorHandler errorHandler;
    private SokobanDocumentManager docManager;

    SokobanGameStateManager gsm;
    GridRenderer gridRenderer = new GridRenderer();
    GraphicsContext gc;

    
    int gridColumns;
    int gridRows;
    int grid[][];
    int gridCopy[][];
    int savedGrid[][];
    
    int SokobanRow;
    int SokobanColumn;
    
    int amountOfRedSquares;
    
    java.util.ArrayList<int[][]> list;
    int amountOfGames;
    int amountOfWins;
    int amountOfLosses;
    double winningPercentage;
    
    public void setWinningPercentage(){
        if(amountOfWins != 0){
        winningPercentage = ((double)(amountOfWins) / (double)(amountOfWins + amountOfLosses)) * 100;
        }
    }
    /**
     * This class renders the grid for us. Note that we also listen for mouse
     * clicks on it.
     */
    class GridRenderer extends Canvas {

        // PIXEL DIMENSIONS OF EACH CELL
        int cellWidth;
        int cellHeight;

        // images
        Image wallImage = new Image("file:images/wall.png");
        Image boxImage = new Image("file:images/box.png");
        Image placeImage = new Image("file:images/place.png");
        Image sokobanImage = new Image("file:images/Sokoban.png");

        /**
         * Default constructor.
         */
        public GridRenderer() {
            this.setWidth(500);
            this.setHeight(500);
            repaint();
        }

        public void repaint() {
            gc = this.getGraphicsContext2D();
            gc.clearRect(0, 0, this.getWidth(), this.getHeight());

            // CALCULATE THE GRID CELL DIMENSIONS
            double w = this.getWidth() / gridColumns;
            double h = this.getHeight() / gridRows;

            gc = this.getGraphicsContext2D();

            // NOW RENDER EACH CELL
            double x = 0, y = 0;
            for (int i = 0; i < gridColumns; i++) {
                y = 0;
                for (int j = 0; j < gridRows; j++) {
                    // DRAW THE CELL
                    gc.setFill(Color.LIGHTBLUE);
                    gc.strokeRoundRect(x, y, w, h, 10, 10);

                    switch (grid[i][j]) {
                        case 0:
                            gc.strokeRoundRect(x, y, w, h, 10, 10);
                            break;
                        case 1:
                            gc.drawImage(wallImage, x, y, w, h);
                            break;
                        case 2:
                            gc.drawImage(boxImage, x, y, w, h);
                            break;
                        case 3:
                            gc.drawImage(placeImage, x, y, w, h);
                            break;
                        case 4:
                            gc.drawImage(sokobanImage, x, y, w, h);
                            break;
                    }

                    // THEN RENDER THE TEXT
                    String numToDraw = "" + grid[i][j];
                    double xInc = (w / 2) - (10 / 2);
                    double yInc = (h / 2) + (10 / 4);
                    x += xInc;
                    y += yInc;
                    gc.setFill(Color.RED);
                    //gc.fillText(numToDraw, x, y);
                    x -= xInc;
                    y -= yInc;

                    // ON TO THE NEXT ROW
                    y += h;
                }
                // ON TO THE NEXT COLUMN
                x += w;
            }
        }

    }
    public SokobanUI() {
        gsm = new SokobanGameStateManager(this);
        eventHandler = new SokobanEventHandler(this);
        errorHandler = new SokobanErrorHandler(primaryStage);
        docManager = new SokobanDocumentManager(this);
        initMainPane();
        initSplashScreen();
    }

    public void SetStage(Stage stage) {
        primaryStage = stage;
    }

    public BorderPane GetMainPane() {
        return this.mainPane;
    }

    public SokobanGameStateManager getGSM() {
        return gsm;
    }

    public SokobanDocumentManager getDocManager() {
        return docManager;
    }

    public SokobanErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public JEditorPane getHelpPane() {
        return helpPane;
    }

    public void initMainPane() {
        marginlessInsets = new Insets(5, 5, 5, 5);
        mainPane = new BorderPane();

        PropertiesManager props = PropertiesManager.getPropertiesManager();
        paneWidth = Integer.parseInt(props
                .getProperty(SokobanPropertyType.WINDOW_WIDTH));
        paneHeigth = Integer.parseInt(props
                .getProperty(SokobanPropertyType.WINDOW_HEIGHT));
        mainPane.resize(paneWidth, paneHeigth);
        mainPane.setPadding(marginlessInsets);
    }

    public void initSplashScreen() {

        // INIT THE SPLASH SCREEN CONTROLS
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String splashScreenImagePath = props
                .getProperty(SokobanPropertyType.SPLASH_SCREEN_IMAGE_NAME);
        props.addProperty(SokobanPropertyType.INSETS, "5");
        String str = props.getProperty(SokobanPropertyType.INSETS);

        // GET THE LIST OF LEVEL OPTIONS
        ArrayList<String> levels = props
                .getPropertyOptionsList(SokobanPropertyType.LEVEL_OPTIONS);
        ArrayList<String> levelImages = props
                .getPropertyOptionsList(SokobanPropertyType.LEVEL_IMAGE_NAMES);
        ArrayList<String> levelFiles = props
                .getPropertyOptionsList(SokobanPropertyType.LEVEL_FILES);

        levelSelectionPane = new FlowPane(0.0,0.0);
        levelSelectionPane.setAlignment(Pos.CENTER);
        // add key listener
        levelButtons = new ArrayList<Button>();
        for (int i = 0; i < levels.size(); i++) {

            // GET THE LIST OF LEVEL OPTIONS
            String level = levels.get(i);
            String levelImageName = levelImages.get(i);
            Image levelImage = loadImage(levelImageName);
            ImageView levelImageView = new ImageView(levelImage);

            // AND BUILD THE BUTTON
            Button levelButton = new Button(level);
            levelButton.setGraphic(levelImageView);
            levelImageView.setFitHeight(100);
            levelImageView.setFitWidth(100);
            
            // CONNECT THE BUTTON TO THE EVENT HANDLER
            levelButton.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    // TODO
                    amountOfGames++;
                    eventHandler.respondToSelectLevelRequest(level);
                    gsm.setCurrentGameState = GAME_IN_PROGRESS;
                }
            });
            // TODO
           levelSelectionPane.getChildren().add(levelButton);
            // TODO: enable only the first level
            levelButton.setDisable(false);
        }

        mainPane.setCenter(levelSelectionPane);
    }

    /**
     * This method initializes the language-specific game controls, which
     * includes the three primary game screens.
     */
    public void initSokobanUI() {
        // FIRST REMOVE THE SPLASH SCREEN
        mainPane.getChildren().clear();
        // GET THE UPDATED TITLE
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String title = props.getProperty(SokobanPropertyType.GAME_TITLE_TEXT);
        primaryStage.setTitle(title);
        // THIS GUY RENDERS OUR GRID
        copyInitialGrid();
        java.util.ArrayList<int[][]> moveList = new java.util.ArrayList<int[][]>();
        list = moveList;
        gridRenderer = new GridRenderer();
        // THEN ADD ALL THE STUFF WE MIGHT NOW USE
        initNorthToolbar();

        // OUR WORKSPACE WILL STORE EITHER THE GAME, STATS,
        // OR HELP UI AT ANY ONE TIME
        initWorkspace();
        //initGameScreen();
        //initStatsPane();
        //initHelpPane();

        // WE'LL START OUT WITH THE GAME SCREEN
        //changeWorkspace(SokobanUIState.PLAY_GAME_STATE);

    }
    
    /**
     * This function finds Sokoban Character
     */
    
    public void initStatsPane(){
        HBox storeStats = new HBox();
        String strAmountOfGames = Integer.toString(amountOfGames);
        String games = "Amount of Games Played: ";
        Button gameButton = new Button(games + strAmountOfGames);
        String wins = "Amount of Wins: ";
        String strAmountOfWins = Integer.toString(amountOfWins);
        Button winButton = new Button(wins + strAmountOfWins);
        String losses ="Amount of Losses: ";
        String strAmountOfLosses = Integer.toString(amountOfLosses);
        Button loseButton = new Button(losses + strAmountOfLosses);
        setWinningPercentage();
        String strWinningPercentage = Double.toString(winningPercentage);
        String winningPercentages = "Winning Percentage: ";
        Button percentageButton = new Button(winningPercentages + strWinningPercentage + "%");
        storeStats.getChildren().add(gameButton);
        storeStats.getChildren().add(winButton);
        storeStats.getChildren().add(loseButton);
        storeStats.getChildren().add(percentageButton);
        mainPane.setCenter(storeStats);
    }
    public void winPane(){
        HBox winSelectionPane = new HBox();
        winSelectionPane.setSpacing(0.0);
        winSelectionPane.setAlignment(Pos.CENTER);
        Image winImage = loadImage("Untitled1.png");
        ImageView winImageView = new ImageView(winImage);
        Button winButton = new Button();
        winButton.setGraphic(winImageView);
        winButton.setFocusTraversable(false);
            //winImageView.setFitHeight(100);
            //winImageView.setFitWidth(100);
        winSelectionPane.getChildren().add(winButton);
        mainPane.setLeft(winSelectionPane);
    }
    public void losePane(){
        HBox loseSelectionPane = new HBox();
        loseSelectionPane.setSpacing(0.0);
        loseSelectionPane.setAlignment(Pos.CENTER);
        Image loseImage = loadImage("Untitled2.png");
        ImageView loseImageView = new ImageView(loseImage);
        Button loseButton = new Button();
        loseButton.setGraphic(loseImageView);
        loseButton.setFocusTraversable(false);
            //loseImageView.setFitHeight(100);
            //loseImageView.setFitWidth(100);
        loseSelectionPane.getChildren().add(loseButton);
        mainPane.setLeft(loseSelectionPane);
    }
    private void findSokoban(){
        for (int i = 0; i < gridColumns; i++) {
                        for (int j = 0; j < gridRows; j++) {
                            if(grid[i][j] == 4){
                                SokobanColumn = i;
                                SokobanRow = j;
                            }
            }
        }
    }
    
    /**
     * This function counts the red squares
     */
    
    private void countRedSquares(){
        int counter = 0;
        for (int i = 0; i < gridColumns; i++) {
                        for (int j = 0; j < gridRows; j++) {
                            if(grid[i][j] == 3){
                                counter++;
                            }
            }
        }
        amountOfRedSquares = counter;
    }
    
    private void winGameCheck(){
        countRedSquares();
        if(amountOfRedSquares == 0 && gridCopy[SokobanColumn][SokobanRow + 1] != 3){
            amountOfWins++;
            //System.out.println("WINTEST DOWN");
            winPane();
        }
    
        else if(amountOfRedSquares == 0 && gridCopy[SokobanColumn][SokobanRow - 1] != 3){
            amountOfWins++;
            //System.out.println("WINTEST UP");
            winPane();
        }
    
        else if(amountOfRedSquares == 0 && gridCopy[SokobanColumn - 1][SokobanRow] != 3){
            amountOfWins++;
            //System.out.println("WINTEST LEFT");
            winPane();
        }
    
        else if(amountOfRedSquares == 0 && gridCopy[SokobanColumn + 1][SokobanRow] != 3){
            amountOfWins++;
            //System.out.println("WINTEST RIGHT");
            winPane();
        }
    }
    
    private void loseGameCheckDown(){
        if((grid[SokobanColumn][SokobanRow + 3] == 1 && grid[SokobanColumn + 1][SokobanRow + 2] == 1 && gridCopy[SokobanColumn][SokobanRow + 2] != 3)
            || (grid[SokobanColumn][SokobanRow + 3] == 1 && grid[SokobanColumn - 1][SokobanRow + 2] == 1 && gridCopy[SokobanColumn][SokobanRow + 2] != 3)){
            amountOfLosses++;
        System.out.println("LOSETEST DOWN");
        losePane();
        }
    }
    private void loseGameCheckUp(){
        if((grid[SokobanColumn][SokobanRow - 3] == 1 && grid[SokobanColumn - 1][SokobanRow - 2] == 1 && gridCopy[SokobanColumn][SokobanRow - 2] != 3)
            || (grid[SokobanColumn][SokobanRow - 3] == 1 && grid[SokobanColumn + 1][SokobanRow - 2] == 1 && gridCopy[SokobanColumn][SokobanRow - 2] != 3)){
            amountOfLosses++;
        System.out.println("LOSETEST UP");
        losePane();
        }
    }
    private void loseGameCheckLeft(){
        if((grid[SokobanColumn - 3][SokobanRow] == 1 && grid[SokobanColumn - 2][SokobanRow + 1 ] == 1 && gridCopy[SokobanColumn - 2][SokobanRow] != 3 )
            || (grid[SokobanColumn - 3][SokobanRow] == 1 && grid[SokobanColumn - 2][SokobanRow - 1] == 1 && gridCopy[SokobanColumn - 2][SokobanRow] != 3 )){
            amountOfLosses++;
        System.out.println("LOSETEST LEFT");
        losePane();
        }
    }
    private void loseGameCheckRight(){
        if((grid[SokobanColumn + 3][SokobanRow] == 1 && grid[SokobanColumn + 2][SokobanRow + 1] == 1 && gridCopy[SokobanColumn + 2][SokobanRow] != 3 )
            || (grid[SokobanColumn + 3][SokobanRow] == 1 && grid[SokobanColumn + 2][SokobanRow - 1] == 1 && gridCopy[SokobanColumn + 2][SokobanRow] != 3 )){
            amountOfLosses++;
        System.out.println("LOSETEST RIGHT");
        losePane();
        }
    }
     /**
     * This function counts the red squares
     */
    
    public void copyInitialGrid(){
        for (int i = 0; i < gridColumns; i++) {
                        for (int j = 0; j < gridRows; j++) {
                            gridCopy[i][j] = grid[i][j];
                        }
    }
    }
    
    public int[][] copyGrid(){
        int[][] tempGrid = new int[gridColumns][gridRows];
        for (int i = 0; i < gridColumns; i++) {
                        for (int j = 0; j < gridRows; j++) {
                            tempGrid[i][j] = grid[i][j];
                        }
    }
        return tempGrid;
    }
    /**
     * This function initializes all the controls that go in the north toolbar.
     */
    private void initNorthToolbar() {
        // MAKE THE NORTH TOOLBAR, WHICH WILL HAVE FOUR BUTTONS
        northToolbar = new HBox();
        northToolbar.setStyle("-fx-background-color:lightgray");
        northToolbar.setAlignment(Pos.CENTER);
        northToolbar.setPadding(marginlessInsets);
        northToolbar.setSpacing(10.0);

        PropertiesManager props = PropertiesManager.getPropertiesManager();
        if(gsm.isGameInProgress() == true){
        props.addProperty(SokobanPropertyType.GAME_IMG_NAME, "Back.png");
        } else {
        props.addProperty(SokobanPropertyType.RESUME_IMG_NAME, "Resume.png");
        }
        props.addProperty(SokobanPropertyType.STATS_IMG_NAME, "Stats.png");
        props.addProperty(SokobanPropertyType.HELP_IMG_NAME, "Help.png");
        props.addProperty(SokobanPropertyType.EXIT_IMG_NAME, "Exit.png");
        
        // MAKE AND INIT THE GAME BUTTON
        gameButton = initToolbarButton(northToolbar,
                SokobanPropertyType.GAME_IMG_NAME);
        //setTooltip(gameButton, SokobanPropertyType.GAME_TOOLTIP);
        gameButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                // TODO Auto-generated method stub
                amountOfLosses++;
                mainPane.getChildren().clear();
                initSplashScreen();
            }
        });

        // MAKE AND INIT THE STATS BUTTON
        statsButton = initToolbarButton(northToolbar,
                SokobanPropertyType.STATS_IMG_NAME);
        //setTooltip(statsButton, SokobanPropertyType.STATS_TOOLTIP);

        statsButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                // TODO Auto-generated method stub

                    initStatsPane();
            }

        });
        // MAKE AND INIT THE HELP BUTTON
        helpButton = initToolbarButton(northToolbar,
                SokobanPropertyType.HELP_IMG_NAME);
        //setTooltip(helpButton, SokobanPropertyType.HELP_TOOLTIP);
        helpButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                // TODO Auto-generated method stub
                if(list.isEmpty() != true){
                        grid = list.get(list.size() - 1);
                        list.remove(list.size() - 1);
                        gridRenderer.repaint();
                    }
            }

        });

        // MAKE AND INIT THE EXIT BUTTON
        exitButton = initToolbarButton(northToolbar,
                SokobanPropertyType.EXIT_IMG_NAME);
        //setTooltip(exitButton, SokobanPropertyType.EXIT_TOOLTIP);
        exitButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                // TODO Auto-generated method stub
                eventHandler.respondToExitRequest(primaryStage);
            }

        });

        // AND NOW PUT THE NORTH TOOLBAR IN THE FRAME
        gameButton.setFocusTraversable(false);
        statsButton.setFocusTraversable(false);
        helpButton.setFocusTraversable(false);
        exitButton.setFocusTraversable(false);
        mainPane.setTop(northToolbar);
        //mainPane.getChildren().add(northToolbar);
    }

    /**
     * This method helps to initialize buttons for a simple toolbar.
     *
     * @param toolbar The toolbar for which to add the button.
     *
     * @param prop The property for the button we are building. This will
     * dictate which image to use for the button.
     *
     * @return A constructed button initialized and added to the toolbar.
     */
    private Button initToolbarButton(HBox toolbar, SokobanPropertyType prop) {
        // GET THE NAME OF THE IMAGE, WE DO THIS BECAUSE THE
        // IMAGES WILL BE NAMED DIFFERENT THINGS FOR DIFFERENT LANGUAGES
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String imageName = props.getProperty(prop);

        // Active game = return to game
        // else Back button to return to main page
        
        // LOAD THE IMAGE
        Image image = loadImage(imageName);
        ImageView imageIcon = new ImageView(image);

        // MAKE THE BUTTON
        Button button = new Button();
        button.setGraphic(imageIcon);
        button.setPadding(marginlessInsets);

        // PUT IT IN THE TOOLBAR
        toolbar.getChildren().add(button);

        // AND SEND BACK THE BUTTON
        return button;
    }

    /**
     * The workspace is a panel that will show different screens depending on
     * the user's requests.
     */
    private void initWorkspace() {
        // THE WORKSPACE WILL GO IN THE CENTER OF THE WINDOW, UNDER THE NORTH
        // TOOLBAR
        workspace = new Pane();
        //Media media = new Media("move.wav");
        //MediaPlayer moveSound = new MediaPlayer(media);

        mainPane.setCenter(gridRenderer);
        gridRenderer.setFocusTraversable(true);
        gridRenderer.setOnKeyPressed(e -> {
            switch (e.getCode()){
                case DOWN:
                   findSokoban();
                   if(grid[SokobanColumn][SokobanRow + 1] == 0){ // Checks to see if the space below is empty
                       savedGrid = copyGrid();
                       list.add(savedGrid);
                       if(gridCopy[SokobanColumn][SokobanRow] == 3){ // Checks to see if Sokoban is standing on a red space
                           grid[SokobanColumn][SokobanRow] = 3; // Change Sokoban's old location to a red square
                           grid[SokobanColumn][SokobanRow + 1] = 4; // Move Sokoban to new location
                           //moveSound.play();
                           gridRenderer.repaint();
                       } else {
                       grid[SokobanColumn][SokobanRow] = 0; // Change Sokoban's old location to empty space
                       grid[SokobanColumn][SokobanRow + 1] = 4; // Move Sokoban to new location
                       //moveSound.play();
                       gridRenderer.repaint();
                   }
                   }
                   
                   if(grid[SokobanColumn][SokobanRow + 1] == 2){ // Checks to see if the space below is a box
                       if(grid[SokobanColumn][SokobanRow + 2] == 0 || grid[SokobanColumn][SokobanRow + 2] == 3){ // Only moves if the space below the box is empty or red square
                           savedGrid = copyGrid();
                           list.add(savedGrid);
                           if(gridCopy[SokobanColumn][SokobanRow] == 3){ // Checks to see if Sokoban is standing on a red space
                           grid[SokobanColumn][SokobanRow] = 3; // Change Sokoban's old location to a red square
                           grid[SokobanColumn][SokobanRow + 1] = 4; // Move Sokoban to new location
                           grid[SokobanColumn][SokobanRow + 2] = 2; // Move the box to the new location
                           winGameCheck();
                           loseGameCheckDown();
                           gridRenderer.repaint();
                       } else {
                           grid[SokobanColumn][SokobanRow] = 0; // Change Sokoban's old location to empty space
                           grid[SokobanColumn][SokobanRow + 1] = 4; // Move Sokoban to new location
                           grid[SokobanColumn][SokobanRow + 2] = 2; // Move the box to the new location
                           winGameCheck();
                           loseGameCheckDown();
                           gridRenderer.repaint();
                   }
                   }
                   }
                   if(grid[SokobanColumn][SokobanRow + 1] == 3){
                       savedGrid = copyGrid();
                       list.add(savedGrid);
                       if(gridCopy[SokobanColumn][SokobanRow] == 3){ // Checks to see if Sokoban is standing on a red square
                           grid[SokobanColumn][SokobanRow] = 3; // Change Sokoban's old location to red space
                           grid[SokobanColumn][SokobanRow + 1] = 4; // Move Sokoban to new location
                           gridRenderer.repaint();
                       } else {
                       grid[SokobanColumn][SokobanRow] = 0; // Change Sokoban's old location to empty space
                       grid[SokobanColumn][SokobanRow + 1] = 4; // Move Sokoban to new location
                       gridRenderer.repaint();
                   }
                   }
                   break;
                case UP:
                    findSokoban();
                    if(grid[SokobanColumn][SokobanRow - 1] == 0){ // Checks to see if the space above is empty
                        savedGrid = copyGrid();
                        list.add(savedGrid);
                        if(gridCopy[SokobanColumn][SokobanRow] == 3){
                           grid[SokobanColumn][SokobanRow] = 3; // Change Sokoban's old location to empty space
                           grid[SokobanColumn][SokobanRow - 1] = 4; // Move Sokoban to new location
                           gridRenderer.repaint();
                       } else {
                       grid[SokobanColumn][SokobanRow] = 0; // Change Sokoban's old location to empty space
                       grid[SokobanColumn][SokobanRow - 1] = 4; // Move Sokoban to new location
                       gridRenderer.repaint();
                   }
                   }
                   if(grid[SokobanColumn][SokobanRow - 1] == 2){ // Checks to see if the space above is a box
                       if(grid[SokobanColumn][SokobanRow - 2] == 0 || grid[SokobanColumn][SokobanRow - 2] == 3){ // Only moves if the space below the box is empty or red square
                           savedGrid = copyGrid();
                           list.add(savedGrid);
                           if(gridCopy[SokobanColumn][SokobanRow] == 3){ // Checks to see if Sokoban is standing on a red space
                           grid[SokobanColumn][SokobanRow] = 3; // Change Sokoban's old location to a red square
                           grid[SokobanColumn][SokobanRow - 1] = 4; // Move Sokoban to new location
                           grid[SokobanColumn][SokobanRow - 2] = 2; // Move the box to the new location
                           winGameCheck();
                           loseGameCheckUp();
                           gridRenderer.repaint();
                       } else {
                           grid[SokobanColumn][SokobanRow] = 0; // Change Sokoban's old location to empty space
                           grid[SokobanColumn][SokobanRow - 1] = 4; // Move Sokoban to new location
                           grid[SokobanColumn][SokobanRow - 2] = 2; // Move the box to the new location
                           winGameCheck();
                           loseGameCheckUp();
                           gridRenderer.repaint();
                   }
                   }
                   }
                   if(grid[SokobanColumn][SokobanRow - 1] == 3){
                       savedGrid = copyGrid();
                       list.add(savedGrid);
                       if(gridCopy[SokobanColumn][SokobanRow] == 3){
                           grid[SokobanColumn][SokobanRow] = 3; // Change Sokoban's old location to empty space
                           grid[SokobanColumn][SokobanRow - 1] = 4; // Move Sokoban to new location
                           gridRenderer.repaint();
                       } else {
                       grid[SokobanColumn][SokobanRow] = 0; // Change Sokoban's old location to empty space
                       grid[SokobanColumn][SokobanRow - 1] = 4; // Move Sokoban to new location
                       gridRenderer.repaint();
                   
                   }
                   }
                break;
                case LEFT:
                    findSokoban();
                    if(grid[SokobanColumn - 1][SokobanRow] == 0){ // Checks to see if the space to the left is empty
                        savedGrid = copyGrid();
                        list.add(savedGrid);
                        if(gridCopy[SokobanColumn][SokobanRow] == 3){
                           grid[SokobanColumn][SokobanRow] = 3; // Change Sokoban's old location to empty space
                           grid[SokobanColumn - 1][SokobanRow] = 4; // Move Sokoban to new location
                           gridRenderer.repaint();
                       } else {
                       grid[SokobanColumn][SokobanRow] = 0; // Change Sokoban's old location to empty space
                       grid[SokobanColumn - 1][SokobanRow] = 4; // Move Sokoban to new location
                       gridRenderer.repaint();
                   }
                   }
                   if(grid[SokobanColumn - 1][SokobanRow] == 2){ // Checks to see if the space to the left is a box
                       if(grid[SokobanColumn - 2][SokobanRow] == 0 || grid[SokobanColumn - 2][SokobanRow] == 3){ // Only moves if the space to the left of the box is empty or red square
                           savedGrid = copyGrid();
                           list.add(savedGrid);
                           if(gridCopy[SokobanColumn][SokobanRow] == 3){ // Checks to see if Sokoban is standing on a red space
                           grid[SokobanColumn][SokobanRow] = 3; // Change Sokoban's old location to a red square
                           grid[SokobanColumn - 1][SokobanRow] = 4; // Move Sokoban to new location
                           grid[SokobanColumn - 2][SokobanRow] = 2; // Move the box to the new location
                           winGameCheck();
                           loseGameCheckLeft();
                           gridRenderer.repaint();
                       } else {
                           grid[SokobanColumn][SokobanRow] = 0; // Change Sokoban's old location to empty space
                           grid[SokobanColumn - 1][SokobanRow] = 4; // Move Sokoban to new location
                           grid[SokobanColumn - 2][SokobanRow] = 2; // Move the box to the new location
                           winGameCheck();
                           loseGameCheckLeft();
                           gridRenderer.repaint();
                   }
                   }
                   }
                   if(grid[SokobanColumn - 1][SokobanRow] == 3){
                       savedGrid = copyGrid();
                       list.add(savedGrid);
                       if(gridCopy[SokobanColumn][SokobanRow] == 3){
                           grid[SokobanColumn][SokobanRow] = 3; // Change Sokoban's old location to empty space
                           grid[SokobanColumn - 1][SokobanRow] = 4; // Move Sokoban to new location
                           gridRenderer.repaint();
                       } else {
                       grid[SokobanColumn][SokobanRow] = 0; // Change Sokoban's old location to empty space
                       grid[SokobanColumn - 1][SokobanRow] = 4; // Move Sokoban to new location
                       gridRenderer.repaint();
                   
                   }
                   }
                break;
                case RIGHT:
                    findSokoban(); 
                    if(grid[SokobanColumn + 1][SokobanRow] == 0){ // Checks to see if the space to the left is empty
                        savedGrid = copyGrid();
                        list.add(savedGrid);
                        if(gridCopy[SokobanColumn][SokobanRow] == 3){
                           grid[SokobanColumn][SokobanRow] = 3; // Change Sokoban's old location to empty space
                           grid[SokobanColumn + 1][SokobanRow] = 4; // Move Sokoban to new location
                           gridRenderer.repaint();
                       } else {
                       grid[SokobanColumn][SokobanRow] = 0; // Change Sokoban's old location to empty space
                       grid[SokobanColumn + 1][SokobanRow] = 4; // Move Sokoban to new location
                       gridRenderer.repaint();
                   }}
                   
                   if(grid[SokobanColumn + 1][SokobanRow] == 2){ // Checks to see if the space to the left is a box
                       if(grid[SokobanColumn + 2][SokobanRow] == 0 || grid[SokobanColumn + 2][SokobanRow] == 3){ // Only moves if the space to the left of the box is empty or red square
                           savedGrid = copyGrid();
                           list.add(savedGrid);
                           if(gridCopy[SokobanColumn][SokobanRow] == 3){ // Checks to see if Sokoban is standing on a red space
                           grid[SokobanColumn][SokobanRow] = 3; // Change Sokoban's old location to a red square
                           grid[SokobanColumn + 1][SokobanRow] = 4; // Move Sokoban to new location
                           grid[SokobanColumn + 2][SokobanRow] = 2; // Move the box to the new location
                           winGameCheck();
                           loseGameCheckRight();
                           gridRenderer.repaint();
                       } else {
                           grid[SokobanColumn][SokobanRow] = 0; // Change Sokoban's old location to empty space
                           grid[SokobanColumn + 1][SokobanRow] = 4; // Move Sokoban to new location
                           grid[SokobanColumn + 2][SokobanRow] = 2; // Move the box to the new location
                           winGameCheck();
                           loseGameCheckRight();
                           gridRenderer.repaint();
                   }
                   }
                   }
                   if(grid[SokobanColumn + 1][SokobanRow] == 3){
                       savedGrid = copyGrid();
                       list.add(savedGrid);
                       if(gridCopy[SokobanColumn][SokobanRow] == 3){
                           grid[SokobanColumn][SokobanRow] = 3; // Change Sokoban's old location to empty space
                           grid[SokobanColumn + 1][SokobanRow] = 4; // Move Sokoban to new location
                           gridRenderer.repaint();
                       } else {
                       grid[SokobanColumn][SokobanRow] = 0; // Change Sokoban's old location to empty space
                       grid[SokobanColumn + 1][SokobanRow] = 4; // Move Sokoban to new location
                       gridRenderer.repaint();
                   }
                   }
                break;
                case U:
                    if(list.isEmpty() != true){
                        grid = list.get(list.size() - 1);
                        list.remove(list.size() - 1);
                        gridRenderer.repaint();
                    }
                break;
                default:
            }
});
        //mainPane.getChildren().add(workspace);
        System.out.println("in the initWorkspace");
    }


    public Image loadImage(String imageName) {
        Image img = new Image(ImgPath + imageName);
        return img;
    }

    /**
     * This function selects the UI screen to display based on the uiScreen
     * argument. Note that we have 3 such screens: game, stats, and help.
     *
     * @param uiScreen The screen to be switched to.
     */
    public void changeWorkspace(SokobanUIState uiScreen) {
        switch (uiScreen) {
            case VIEW_HELP_STATE:
                mainPane.setCenter(helpPanel);
                break;
            case PLAY_GAME_STATE:
                mainPane.setCenter(gamePanel);
                break;
            case VIEW_STATS_STATE:
                mainPane.setCenter(statsScrollPane);
                break;
            default:
        }

    }


}
