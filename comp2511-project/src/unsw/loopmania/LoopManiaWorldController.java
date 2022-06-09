package unsw.loopmania;

import javafx.util.Duration;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;

import org.codefx.libfx.listener.handle.ListenerHandle;
import org.codefx.libfx.listener.handle.ListenerHandles;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.control.TextArea;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.event.ActionEvent;

import java.io.File;
import java.io.IOException;

// The draggable types
enum DRAGGABLE_TYPE {
    CAMPFIRE_CARD,
    TRAP_CARD,
    VILLAGE_CARD,
    TOWER_CARD,
    BARRACKS_CARD,
    VAMPIRE_CASTLE_CARD,
    ZOMBIE_PIT_CARD,
    DESTROYER_CARD,
    
    ITEM,
}

/**
 * A JavaFX controller for the world.
 * 
 * All event handlers and the timeline in JavaFX run on the JavaFX application thread:
 *     https://examples.javacodegeeks.com/desktop-java/javafx/javafx-concurrency-example/
 *     Note in https://openjfx.io/javadoc/11/javafx.graphics/javafx/application/Application.html under heading "Threading", it specifies animation timelines are run in the application thread.
 * This means that the starter code does not need locks (mutexes) for resources shared between the timeline KeyFrame, and all of the  event handlers (including between different event handlers).
 * This will make the game easier for you to implement. However, if you add time-consuming processes to this, the game may lag or become choppy.
 * 
 * If you need to implement time-consuming processes, we recommend:
 *     using Task https://openjfx.io/javadoc/11/javafx.graphics/javafx/concurrent/Task.html by itself or within a Service https://openjfx.io/javadoc/11/javafx.graphics/javafx/concurrent/Service.html
 * 
 *     Tasks ensure that any changes to public properties, change notifications for errors or cancellation, event handlers, and states occur on the JavaFX Application thread,
 *         so is a better alternative to using a basic Java Thread: https://docs.oracle.com/javafx/2/threads/jfxpub-threads.htm
 *     The Service class is used for executing/reusing tasks. You can run tasks without Service, however, if you don't need to reuse it.
 *
 * If you implement time-consuming processes in a Task or thread, you may need to implement locks on resources shared with the application thread (i.e. Timeline KeyFrame and drag Event handlers).
 * You can check whether code is running on the JavaFX application thread by running the helper method printThreadingNotes in this class.
 * 
 * NOTE: http://tutorials.jenkov.com/javafx/concurrency.html and https://www.developer.com/design/multithreading-in-javafx/#:~:text=JavaFX%20has%20a%20unique%20set,in%20the%20JavaFX%20Application%20Thread.
 * 
 * If you need to delay some code but it is not long-running, consider using Platform.runLater https://openjfx.io/javadoc/11/javafx.graphics/javafx/application/Platform.html#runLater(java.lang.Runnable)
 *     This is run on the JavaFX application thread when it has enough time.
 */

public class LoopManiaWorldController {

    // GAME WORLD
    @FXML
    private GridPane squares;

    // CARD INVENTORY
    @FXML
    private GridPane cardInventory;

    // ITEM INVENTORY
    @FXML
    private GridPane equippedItems;
    @FXML
    private GridPane unequippedInventory;

    // STATS PANEL
    @FXML
    private TextArea statsText;
    
    // LOOP AND ROUND COUNTERS
    @FXML
    private Label loopCount;
    @FXML
    private Label roundCount;
    @FXML
    private ProgressBar loopProgressBar;

    private double loopProgress;
    private int pathSize;

    // SPEED, PLAY, PAUSE TOGGLES
    @FXML
    private Button speedToggle;
    private double speed;
    @FXML
    private Button playToggle;
    @FXML
    private Button pauseToggle;

    // GAME STATE ELEMENTS
    @FXML
    private Label gameStatusText;
    @FXML
    private ImageView gameStatusImage;
    @FXML
    private Button visitStoreButton;
    @FXML
    private VBox colourPanel;
    @FXML
    private Label goalsText;
    @FXML
    private Label gameModeText;

    // FRONTEND STATS ELEMENTS
    @FXML
    private ProgressBar healthBar;
    @FXML
    private Label healthVal;
    @FXML
    private Label goldCount;
    @FXML
    private Label xpCount;
    @FXML
    private Label alliesCount;
    @FXML
    private Label damageVal;
    @FXML
    private Label defenceVal;

    // GRAVESTONES
    private List<ImageView> graveStones;
    private Image graveStoneImage;

    /**
     * This is the "background". It is useful since anchorPaneRoot stretches over the entire game world,
     * so we can detect dragging of cards/items over this and accordingly update DragIcon coordinates
     */
    @FXML
    private AnchorPane anchorPaneRoot;

    /**
     * All image views including tiles, character, enemies, cards... even though cards in separate gridpane...
     */
    private List<ImageView> entityImages;

    /**
     * When we drag a card/item, the picture for whatever we're dragging is set here and we actually drag this node
     */
    private DragIcon draggedEntity;

    private boolean isPaused;
    private LoopManiaWorld world;
    private Character character;

    /**
     * Runs the periodic game logic - second-by-second moving of character through maze, as well as enemies, and running of battles
     */
    private Timeline timeline;

    // CARD IMAGES
    private Image campfireCardImage;
    private Image trapCardImage;
    private Image villageCardImage;
    private Image towerCardImage;
    private Image barracksCardImage;
    private Image vampireCastleCardImage;
    private Image zombiePitCardImage;
    private Image destroyerCardImage;
    
    // BUILDING IMAGES
    private Image campfireBuildingImage;
    private Image trapBuildingImage;
    private Image villageBuildingImage;
    private Image towerBuildingImage;
    private Image barracksBuildingImage;
    private Image vampireCastleBuildingImage;
    private Image zombiePitBuildingImage;

    // ENEMY IMAGES
    private Image slugImage;
    private Image zombieImage;
    private Image vampireImage;
    private Image doggieImage;
    private Image elanMuskeImage;

    // ITEM IMAGES
    private Image goldImage;
    private Image healthPotionImage;

    private ArrayList<Integer> availableLoopEnterHeroCastle = new ArrayList<Integer>();

    /**
     * The image currently being dragged, if there is one, otherwise null.
     * Holding the ImageView being dragged allows us to spawn it again in the drop location if appropriate.
     */
    private ImageView currentlyDraggedImage;
    
    /**
     * Null if nothing is being dragged, or the type of item being dragged
     */
    private DRAGGABLE_TYPE currentlyDraggedType;

    /**
     * Mapping from draggable type enum CARD/TYPE to the event handler triggered when the draggable type is dropped over its appropriate gridpane
     */
    private EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>> gridPaneSetOnDragDropped;
    /**
     * Mapping from draggable type enum CARD/TYPE to the event handler triggered when the draggable type is dragged over the background
     */
    private EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>> anchorPaneRootSetOnDragOver;
    /**
     * Mapping from draggable type enum CARD/TYPE to the event handler triggered when the draggable type is dropped in the background
     */
    private EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>> anchorPaneRootSetOnDragDropped;
    /**
     * Mapping from draggable type enum CARD/TYPE to the event handler triggered when the draggable type is dragged into the boundaries of its appropriate gridpane
     */
    private EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>> gridPaneNodeSetOnDragEntered;
    /**
     * Mapping from draggable type enum CARD/TYPE to the event handler triggered when the draggable type is dragged outside of the boundaries of its appropriate gridpane
     */
    private EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>> gridPaneNodeSetOnDragExited;

    /**
     * Object handling switching to the Main Menu
     */
    private MenuSwitcher mainMenuSwitcher;
    private MenuSwitcher heroCastleSwitcher;


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @param world world object loaded from file
     * @param initialEntities the initial JavaFX nodes (ImageViews) which should be loaded into the GUI
     */
    public LoopManiaWorldController(LoopManiaWorld world, List<ImageView> initialEntities) {
        this.world = world;
        this.character = world.getCharacter();
        entityImages = new ArrayList<>(initialEntities);

        loopProgress = 0;
        pathSize = world.getOrderedPath().size();

        // GRAVE STONES
        graveStones = new ArrayList<>();
        graveStoneImage = new Image((new File("src/images/gravestone.png")).toURI().toString());

        // CARD IMAGES
        campfireCardImage = new Image((new File("src/images/campfire_card.png")).toURI().toString());
        trapCardImage = new Image((new File("src/images/trap_card.png")).toURI().toString());
        villageCardImage = new Image((new File("src/images/village_card.png")).toURI().toString());
        towerCardImage = new Image((new File("src/images/tower_card.png")).toURI().toString());
        barracksCardImage = new Image((new File("src/images/barracks_card.png")).toURI().toString());
        vampireCastleCardImage = new Image((new File("src/images/vampire_castle_card.png")).toURI().toString());
        zombiePitCardImage = new Image((new File("src/images/zombie_pit_card.png")).toURI().toString());
        destroyerCardImage = new Image((new File("src/images/destroyer_card.png")).toURI().toString());

        // ENEMY IMAGES
        slugImage = new Image((new File("src/images/slug_new.png")).toURI().toString());
        zombieImage = new Image((new File("src/images/zombie_new.png")).toURI().toString());
        doggieImage = new Image((new File ("src/images/doggie.png")).toURI().toString());
        elanMuskeImage = new Image((new File("src/images/ElanMuske.png")).toURI().toString());
        vampireImage = new Image((new File("src/images/vampire.gif")).toURI().toString());

        // BUILDING IMAGES
        campfireBuildingImage = new Image((new File("src/images/campfire_new.png")).toURI().toString());
        trapBuildingImage = new Image((new File("src/images/trap.png")).toURI().toString());
        villageBuildingImage = new Image((new File("src/images/village.png")).toURI().toString());
        towerBuildingImage = new Image((new File("src/images/tower.png")).toURI().toString());
        barracksBuildingImage = new Image((new File("src/images/barracks.png")).toURI().toString());
        vampireCastleBuildingImage = new Image((new File("src/images/vampire_castle_building_purple_background.png")).toURI().toString());
        zombiePitBuildingImage = new Image((new File("src/images/zombie_pit.png")).toURI().toString());

        // ITEM IMAGES
        goldImage = new Image((new File("src/images/map_gold.gif")).toURI().toString());
        healthPotionImage = new Image((new File("src/images/map_potion.png")).toURI().toString());

        // OTHER
        currentlyDraggedImage = null;
        currentlyDraggedType = null;
        speed = 0.4;

        // AVAILABLE LOOP TO ENTER HEROCASTLE
        int currentCount = 1;
        int nextAvailableCount = 1;
        for (int i = 0; i <100; i++) {
            nextAvailableCount += currentCount;
            availableLoopEnterHeroCastle.add(nextAvailableCount);
            currentCount++;
        }

        gridPaneSetOnDragDropped = new EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>>(DRAGGABLE_TYPE.class);
        anchorPaneRootSetOnDragOver = new EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>>(DRAGGABLE_TYPE.class);
        anchorPaneRootSetOnDragDropped = new EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>>(DRAGGABLE_TYPE.class);
        gridPaneNodeSetOnDragEntered = new EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>>(DRAGGABLE_TYPE.class);
        gridPaneNodeSetOnDragExited = new EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>>(DRAGGABLE_TYPE.class);
    }

    @FXML
    public void initialize() {
        Image pathTilesImage = new Image((new File("src/images/32x32GrassAndDirtPath.png")).toURI().toString());
        Image inventorySlotImage = new Image((new File("src/images/empty_slot.png")).toURI().toString());
        Rectangle2D imagePart = new Rectangle2D(0, 0, 32, 32);

        // Add the ground first so it is below all other entities (inculding all the twists and turns)
        for (int x = 0; x < world.getWidth(); x++) {
            for (int y = 0; y < world.getHeight(); y++) {
                ImageView groundView = new ImageView(pathTilesImage);
                groundView.setViewport(imagePart);
                squares.add(groundView, x, y);
            }
        }

        // Load entities loaded from the file in the loader into the squares gridpane
        for (ImageView entity : entityImages){
            squares.getChildren().add(entity);
        }
        
        // Add empty slot images for the card inventory
        for (int x=0; x < LoopManiaWorld.CARD_INVENTORY_WIDTH; x++) {
            for (int y=0; y < LoopManiaWorld.CARD_INVENTORY_HEIGHT; y++) {
                ImageView emptySlotView = new ImageView(inventorySlotImage);
                cardInventory.add(emptySlotView, x, y);
            }
        }

        // Add the empty slot images for the unequipped item inventory
        for (int x=0; x<LoopManiaWorld.UNEQUIPPED_INVENTORY_WIDTH; x++){
            for (int y=0; y<LoopManiaWorld.UNEQUIPPED_INVENTORY_HEIGHT; y++){
                ImageView emptySlotView = new ImageView(inventorySlotImage);
                unequippedInventory.add(emptySlotView, x, y);
            }
        }

        // Binding front end bits and pieces to backend data
        statsText.textProperty().bind(world.getStatsText());
        statsText.setStyle("-fx-text-fill: white ; -fx-background-color: #676767#676767");

        visitStoreButton.setDisable(true);
        goalsText.setText(world.getGoalString());

        healthVal.textProperty().bind(world.getCharacter().getStats().getHpProperty().asString());
        healthBar.progressProperty().bind(world.getCharacter().getStats().getHpProperty().divide(100.0));
        healthBar.setStyle("-fx-accent: red");
        loopProgressBar.setStyle("-fx-accent: #FF9500#FF9500");

        goldCount.textProperty().bind(world.getCharacter().getGoldProperty().asString());
        xpCount.textProperty().bind(world.getCharacter().getXpProperty().asString());
        damageVal.textProperty().bind(world.getCharacter().getTotalStats().getDamageProperty().asString());
        defenceVal.textProperty().bind(world.getCharacter().getTotalStats().getDefenceProperty().asString());

        // Creating the draggable icon
        draggedEntity = new DragIcon();
        draggedEntity.setVisible(false);
        draggedEntity.setOpacity(0.7);
        anchorPaneRoot.getChildren().add(draggedEntity);
    }

    @FXML
    public void handleSpeedToggle(ActionEvent event) {
        speedToggle.setTranslateX(-10);
        Timeline clickedAnimation = new Timeline();
        clickedAnimation.setCycleCount(1);
        KeyValue kv = new KeyValue(speedToggle.translateXProperty(), 0, Interpolator.EASE_IN);
        KeyFrame kf = new KeyFrame(Duration.millis(200), kv);
        clickedAnimation.getKeyFrames().addAll(kf);
        clickedAnimation.play();

        if (speed == 0.4) {
            speed = 0.2;
            speedToggle.setText("2x");
        } else if (speed == 0.2) {
            speed = 0.1;
            speedToggle.setText("4x");
        } else if (speed == 0.1) {
            speed = 0.4;
            speedToggle.setText("1x");
        }
        pause();
        startTimer();
    }

    @FXML
    public void handlePlayToggle(ActionEvent event) {
        startTimer();
    }

    @FXML
    public void handlePauseToggle(ActionEvent event) {
        pause();
    }

    /**
     * Create and run the timer
     */
    public void startTimer(){
        System.out.println("starting timer");
        gameStatusText.setText("PLAYING    ");
        colourPanel.setStyle("-fx-background-color: #0F3941#0F3941");
        visitStoreButton.setDisable(true);
        gameStatusImage.setImage(new Image((new File("src/images/running.gif")).toURI().toString()));
        playToggle.setDisable(true);
        pauseToggle.setDisable(false);
        isPaused = false;

        // Trigger adding code to process main game logic to queue. JavaFX will target framerate of (speed) seconds
        timeline = new Timeline(new KeyFrame(Duration.seconds(speed), event -> {

            alliesCount.setText(String.valueOf(world.getCharacter().getAllies().size()));    

            for (ImageView graveStone: graveStones) {
                graveStone.setOpacity(graveStone.getOpacity() - 0.15);
                if (graveStone.getOpacity() <= 0) {
                    squares.getChildren().remove(graveStone);
                }
            }

            List<Enemy> defeatedEnemies = world.runBattles();
            if (defeatedEnemies == null) {
                gameOver();
                return;
            } else {
                for (Enemy e: defeatedEnemies) {
                    reactToEnemyDefeat(e);
                }
            }
            
            loopProgress++;
            if (world.isPlayerAtHerosCastle()) {
                world.updateLoopAndRound();
                loopProgress = 0;
                loopCount.setText(String.valueOf(world.getLoopCount()));
                roundCount.setText(String.valueOf(world.getRoundCount()));
                if (availableLoopEnterHeroCastle.contains(world.getLoopCount())) {
                    pause();
                    gameStatusText.setText("NEW ROUND");
                    gameStatusImage.setImage(new Image((new File("src/images/heros_castle.png")).toURI().toString()));
                    colourPanel.setStyle("-fx-background-color: #5F3B08#5F3B08");
                    visitStoreButton.setDisable(false);

                    Timeline emphasiseAnimation = new Timeline();
                    emphasiseAnimation.setCycleCount(4);
                    emphasiseAnimation.setAutoReverse(true);
                    KeyValue kv1 = new KeyValue(visitStoreButton.scaleXProperty(), 1.1, Interpolator.EASE_BOTH);
                    KeyValue kv2 = new KeyValue(visitStoreButton.scaleYProperty(), 1.1, Interpolator.EASE_BOTH);
                    KeyFrame kf = new KeyFrame(Duration.millis(350), kv1, kv2);
                    emphasiseAnimation.getKeyFrames().addAll(kf);
                    emphasiseAnimation.play();
                }
            }
            loopProgressBar.setProgress(loopProgress / pathSize * 1.0);

            List<Enemy> newEnemies = world.possiblySpawnEnemies();
            for (Enemy newEnemy: newEnemies) {
                onLoad(newEnemy);
            }

            List<Enemy> buildingKilledEnemies = world.runTickMoves();
            for (Enemy e: buildingKilledEnemies) {
                reactToEnemyDefeat(e);
            }

            int newPotions = world.collectSpawnedItems(); 
            for (int i = 0; i < newPotions; i++) {
                loadItem("HealthPotion");
            }

            List<MapItem> spawnedItems = world.spawnNewMapItem();
            for (MapItem mapItem: spawnedItems) {
                onLoad(mapItem);
            }

            alliesCount.setText(String.valueOf(world.getCharacter().getAllies().size()));    

            if (world.areGoalsSatisfied()) {
                gameWon();
            }

            if (world.getCharacter().getStats().getHp() <= 0) {
                gameOver();
            }

            printThreadingNotes("HANDLED TIMER");
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * pause the execution of the game loop
     * the human player can still drag and drop items during the game pause
     */
    public void pause() {
        gameStatusText.setText("PLANNING");
        gameStatusImage.setImage(new Image((new File("src/images/running_paused.png")).toURI().toString()));
        pauseToggle.setDisable(true);
        playToggle.setDisable(false);
        isPaused = true;
        System.out.println("pausing");
        timeline.stop();
    }

    public void stopGame() {
        pause();
        
        cardInventory.setDisable(true);
        cardInventory.setOpacity(0.5);

        unequippedInventory.setDisable(true);
        unequippedInventory.setOpacity(0.5);

        equippedItems.setDisable(true);
        equippedItems.setOpacity(0.5);
        
        playToggle.setDisable(true);
        pauseToggle.setDisable(true);
        speedToggle.setDisable(true);
        visitStoreButton.setDisable(true);

        squares.setDisable(true);
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(-0.5);
        squares.setEffect(colorAdjust);
    }

    public void gameOver() {
        stopGame();
        gameStatusText.setText("GAME OVER");
        gameStatusImage.setImage(new Image((new File("src/images/skull.png")).toURI().toString()));
        colourPanel.setStyle("-fx-background-color: #AA0000#AA0000");

        world.addStatsText("\nGAME OVER\n\n==============\n\nSurvived " + world.getRoundCount() + " round(s)\n\nClick EXIT below to return to the Main Menu\n");
    }

    public void gameWon() {
        stopGame();
        gameStatusText.setText("GAME WON");
        gameStatusImage.setImage(new Image((new File("src/images/character.gif")).toURI().toString()));
        colourPanel.setStyle("-fx-background-color: #00804A#00804A");
        world.newStatsText("\nGAME WON!\n\n==============\n\nSurvived " + world.getRoundCount() + " round(s)\n\nClick EXIT below to return to the Main Menu\n");
    }

    public void terminate(){
        pause();
    }

    /**
     * Pair the entity an view so that the view copies the movements of the entity.
     * add view to list of entity images
     * @param entity backend entity to be paired with view
     * @param view frontend imageview to be paired with backend entity
     */
    private void addEntity(Entity entity, ImageView view) {
        trackPosition(entity, view);
        entityImages.add(view);
    }

    /**
     * Load a card from the world, and pair it with an image in the GUI
     */
    private void loadCard(String type) {
        Card newCard = world.loadCard(type);
        //System.err.println("This card is being loaded: " + newCard);
        onLoad(newCard, type);

    }

    /**
     * Load an item from the world, and pair it with an image in the GUI
     */
    private void loadItem(String type) {
        Item item = world.loadItem(type);
        //System.err.println("This item is being loaded" + item);
        onLoad(item, type);
    }

    /**
     * Run GUI events after an enemy is defeated, such as spawning items/experience/gold
     * @param enemy defeated enemy for which we should react to the death of
     */
    private void reactToEnemyDefeat(Enemy enemy){
        List<String> drops = enemy.getDrop();
        
        int x = enemy.getX();
        int y = enemy.getY();
        ImageView graveStone = new ImageView(graveStoneImage);
        graveStone.setFitWidth(32);
        graveStone.setFitHeight(32);
        squares.add(graveStone, x, y);
        graveStones.add(graveStone);

        ImageView coin = new ImageView(goldImage);
        coin.setFitWidth(16);
        coin.setFitHeight(16);
        squares.add(coin, x, y);

        Timeline coinCollectAnimation = new Timeline();
        coinCollectAnimation.setCycleCount(1);
        coin.setTranslateY(0);
        coin.setTranslateX(8);
        KeyValue kv1 = new KeyValue(coin.translateYProperty(), -40, Interpolator.EASE_IN);
        KeyValue kv2 = new KeyValue(coin.opacityProperty(), 0, Interpolator.LINEAR);

        KeyFrame kf = new KeyFrame(Duration.millis(700), kv1, kv2);
        coinCollectAnimation.getKeyFrames().addAll(kf);
        coinCollectAnimation.play();

        for (String drop: drops) {
            loadDrop(drop);
        }

        // If enemy is a boss - increment defeat count.
        if (enemy instanceof Boss) {
            world.incrementBossDefeatCount();
        }

        world.killEnemy(enemy);
    }

    private void loadDrop(String type) {
        if (type.equals("Sword") || type.equals("Stake") || type.equals("Staff") || type.equals("Armour") 
        || type.equals("Shield") || type.equals("Helmet") || type.equals("HealthPotion") || type.equals("OneRing")
        || type.equals("LuckyClover") || type.equals("GameShark") || type.equals("DenimShorts") || type.equals("Anduril") 
        || type.equals("TreeStump") || type.equals("DoggieCoin")) {
            loadItem(type);
        } else if (type.equals("Village") || type.equals("Barracks") || type.equals("ZombiePit") || type.equals("VampireCastle") || 
                    type.equals("Trap") || type.equals("Campfire") || type.equals("Tower") || type.equals("Destroyer")) {
            loadCard(type);
        }
    }

    /**
     * Load a CARD into the GUI
     * Particularly, we must connect to the drag detection event handler,
     * and load the image into the cardInventory GridPane
     */
    private void onLoad(Card card, String type) {
        ImageView view;
        switch (type) {            
            case "Campfire": 
                view = new ImageView(campfireCardImage);
                addDragEventHandlers(view, DRAGGABLE_TYPE.CAMPFIRE_CARD, cardInventory, squares);
                break;
            case "Trap": 
                view = new ImageView(trapCardImage);
                addDragEventHandlers(view, DRAGGABLE_TYPE.TRAP_CARD, cardInventory, squares);
                break;
            case "Village": 
                view = new ImageView(villageCardImage);
                addDragEventHandlers(view, DRAGGABLE_TYPE.VILLAGE_CARD, cardInventory, squares);
                break;
            case "Tower": 
                view = new ImageView(towerCardImage);
                addDragEventHandlers(view, DRAGGABLE_TYPE.TOWER_CARD, cardInventory, squares);
                break;
            case "Barracks": 
                view = new ImageView(barracksCardImage);
                addDragEventHandlers(view, DRAGGABLE_TYPE.BARRACKS_CARD, cardInventory, squares);
                break;
            case "VampireCastle": 
                view = new ImageView(vampireCastleCardImage);
                addDragEventHandlers(view, DRAGGABLE_TYPE.VAMPIRE_CASTLE_CARD, cardInventory, squares);
                break;
            case "ZombiePit":
                view = new ImageView(zombiePitCardImage);
                addDragEventHandlers(view, DRAGGABLE_TYPE.ZOMBIE_PIT_CARD, cardInventory, squares);
                break;
            case "Destroyer":
                view = new ImageView(destroyerCardImage);
                addDragEventHandlers(view, DRAGGABLE_TYPE.DESTROYER_CARD, cardInventory, squares);
                break;
            default:
                return;
        }
        Timeline pickUpAnimation = new Timeline();
        pickUpAnimation.setCycleCount(1);
        view.setTranslateY(-10);
        KeyValue kv = new KeyValue(view.translateYProperty(), 0, Interpolator.EASE_OUT);
        KeyFrame kf = new KeyFrame(Duration.millis(250), kv);
        pickUpAnimation.getKeyFrames().addAll(kf);
        pickUpAnimation.play();

        addEntity(card, view);
        cardInventory.getChildren().add(view);
    }

    /**
     * Load an ITEM into the GUI
     * Particularly, we must connect to the drag detection event handler,
     * and load the image into the unequippedInventory GridPane.
     * @param sword
     */
    private void onLoad(Item item, String type) {
        ImageView view;
        Image image = item.getImage();
        view = new ImageView(image);
        switch (type) {
            case "Sword": 
                addEntity((Sword) item, view);
                break;
            case "Stake": 
                addEntity((Stake) item, view);
                break;
            case "Staff": 
                addEntity((Staff) item, view);
                break;
            case "Armour": 
                addEntity((Armour) item, view);
                break;
            case "Shield": 
                addEntity((Shield) item, view);
                break;
            case "Helmet": 
                addEntity((Helmet) item, view);
                break;
            case "HealthPotion": 
                addEntity((HealthPotion) item, view);
                break;
            case "OneRing": 
                addEntity((OneRing) item, view);
                break;
            case "LuckyClover": 
                addEntity((LuckyClover) item, view);
                break;
            case "DenimShorts": 
                addEntity((DenimShorts) item, view);
                break;   
            case "Gameshark": 
                addEntity((GameShark) item, view);
                break;  
            case "DoggieCoin":
                addEntity((DoggieCoin) item, view);
                break;
            case "Anduril":
                addEntity((Anduril) item, view);
                break;
            case "TreeStump":
                addEntity((TreeStump) item, view);
                break;
            default:
                return;
        }
        Timeline pickUpAnimation = new Timeline();
        pickUpAnimation.setCycleCount(1);
        view.setTranslateY(-10);
        KeyValue kv = new KeyValue(view.translateYProperty(), 0, Interpolator.EASE_OUT);
        KeyFrame kf = new KeyFrame(Duration.millis(250), kv);
        pickUpAnimation.getKeyFrames().addAll(kf);
        pickUpAnimation.play();

        addDragEventHandlers(view, DRAGGABLE_TYPE.ITEM, unequippedInventory, equippedItems);
        unequippedInventory.getChildren().add(view);
    }

    /**
     * Load an ENEMY into the GUI
     * @param enemy
     */
    private void onLoad(Enemy enemy) {
        ImageView enemyView = null;
        String type = enemy.getType();
        Random rand = new Random();
        int x = 0;
        switch (type) {
            case "Slug":
                enemyView = new ImageView(slugImage);
                enemyView.setFitWidth(16);
                enemyView.setFitHeight(16);
                x = rand.nextInt((16 - 0) + 1) + 0;
                enemyView.setTranslateX(x);
                enemyView.setTranslateY(rand.nextInt((8 - (-8)) + 1) + -8);

                addEntity(enemy, enemyView);
                squares.getChildren().add(enemyView);
                break;
            case "Zombie":
                enemyView = new ImageView(zombieImage);
                enemyView.setFitWidth(20);
                enemyView.setFitHeight(20);
                x = rand.nextInt((12 - 0) + 1) + 0;
                enemyView.setTranslateX(x);
                enemyView.setTranslateY(rand.nextInt((6 - (-6)) + 1) + -6);

                addEntity(enemy, enemyView);
                squares.getChildren().add(enemyView);
                break;
            case "Vampire":
                enemyView = new ImageView(vampireImage);
                enemyView.setFitWidth(20);
                enemyView.setFitHeight(20);
                x = rand.nextInt((12 - 0) + 1) + 0;
                enemyView.setTranslateX(x);
                enemyView.setTranslateY(rand.nextInt((6 - (-6)) + 1) + -6);

                addEntity(enemy, enemyView);
                squares.getChildren().add(enemyView);
                break;
            case "Doggie":
                enemyView = new ImageView(doggieImage);
                enemyView.setFitWidth(32);
                enemyView.setFitHeight(32);

                addEntity(enemy, enemyView);
                squares.getChildren().add(enemyView);
                break;
            case "Elan Muske":
                enemyView = new ImageView(elanMuskeImage);
                enemyView.setFitWidth(32);
                enemyView.setFitHeight(32);

                addEntity(enemy, enemyView);
                squares.getChildren().add(enemyView);
                break;
        }
        Timeline wobbleAnimation = new Timeline();
        wobbleAnimation.setCycleCount(Timeline.INDEFINITE);
        wobbleAnimation.setAutoReverse(true);
        KeyValue kv = new KeyValue(enemyView.translateXProperty(), x+5, Interpolator.EASE_BOTH);
        KeyFrame kf = new KeyFrame(Duration.millis(rand.nextInt((2250 - 1000) + 2250) + 1000), kv);
        wobbleAnimation.getKeyFrames().addAll(kf);
        wobbleAnimation.play();
    }

    /**
     * Load an ITEM ON THE MAP into the GUI
     * @param enemy
     */
    private void onLoad(MapItem item) {
        ImageView itemView;
        int y = 0;
        Random rand = new Random();
        if (item.getType() == "gold") {
            itemView = new ImageView(goldImage);
            itemView.setFitWidth(16);
            itemView.setFitHeight(16);
            itemView.setTranslateX(rand.nextInt((16 - 0) + 1) + 0);
            y = rand.nextInt((8 - (-8)) + 1) + -8;
            itemView.setTranslateY(y);
            addEntity(item, itemView);
            squares.getChildren().add(itemView);
        } else {
            itemView = new ImageView(healthPotionImage);
            itemView.setFitWidth(16);
            itemView.setFitHeight(16);
            itemView.setTranslateX(rand.nextInt((16 - 0) + 1) + 0);
            y = rand.nextInt((8 - (-8)) + 1) + -8;
            itemView.setTranslateY(y);
            addEntity(item, itemView);
            squares.getChildren().add(itemView);
        }
        Timeline wobbleAnimation = new Timeline();
        wobbleAnimation.setCycleCount(Timeline.INDEFINITE);
        wobbleAnimation.setAutoReverse(true);
        KeyValue kv = new KeyValue(itemView.translateYProperty(), y+5, Interpolator.EASE_BOTH);
        KeyFrame kf = new KeyFrame(Duration.millis(1000), kv);
        wobbleAnimation.getKeyFrames().addAll(kf);
        wobbleAnimation.play();
    }

    /**
     * Load a BUILDING into the GUI
     * @param building
     */
    private void onLoad(Building building, String type) {
        ImageView view;
        switch (type) {            
            case "Campfire": 
                view = new ImageView(campfireBuildingImage);
                break;
            case "Trap": 
                view = new ImageView(trapBuildingImage);
                break;
            case "Village": 
                view = new ImageView(villageBuildingImage);
                break;
            case "Tower": 
                view = new ImageView(towerBuildingImage);
                break;
            case "Barracks": 
                view = new ImageView(barracksBuildingImage);
                break;
            case "VampireCastle": 
                view = new ImageView(vampireCastleBuildingImage);
                break;
            case "ZombiePit": 
                view = new ImageView(zombiePitBuildingImage);
                break;

            default:
                return;
        }
        view.setFitWidth(32);
        view.setFitHeight(32);
        addEntity(building, view);
        squares.getChildren().add(view);
    }

    /**
     * add drag event handlers for dropping into gridpanes, dragging over the background, dropping over the background.
     * These are not attached to invidual items such as swords/cards.
     * @param draggableType the type being dragged - card or item
     * @param sourceGridPane the gridpane being dragged from
     * @param targetGridPane the gridpane the human player should be dragging to (but we of course cannot guarantee they will do so)
     */
    private void buildNonEntityDragHandlers(DRAGGABLE_TYPE draggableType, GridPane sourceGridPane, GridPane targetGridPane){
        gridPaneSetOnDragDropped.put(draggableType, new EventHandler<DragEvent>() {
            
            public void handle(DragEvent event) {
                if (currentlyDraggedType == draggableType) {
                    // problem = event is drop completed is false when should be true...
                    // https://bugs.openjdk.java.net/browse/JDK-8117019
                    // putting drop completed at start not making complete on VLAB...

                    // Data dropped
                    // If there is an image on the dragboard, read it and use it
                    Dragboard db = event.getDragboard(); 
                    Node node = event.getPickResult().getIntersectedNode();
                    if (node != targetGridPane && db.hasImage()) {

                        Integer cIndex = GridPane.getColumnIndex(node);
                        Integer rIndex = GridPane.getRowIndex(node);
                        int x = cIndex == null ? 0 : cIndex;
                        int y = rIndex == null ? 0 : rIndex;
                        // Places at 0,0 - will need to take coordinates once that is implemented

                        int nodeX = GridPane.getColumnIndex(currentlyDraggedImage);
                        int nodeY = GridPane.getRowIndex(currentlyDraggedImage);
                        Building newBuilding;
                        
                        switch (draggableType){

                            // CARDS ////////////////////////////////////////////////////////////////////////

                            case CAMPFIRE_CARD:
                                if (!world.onPath(x, y) && world.squareEmpty(x, y)) {
                                    removeDraggableDragEventHandlers(draggableType, targetGridPane);
                                    newBuilding = convertCardToBuilding(nodeX, nodeY, x, y);
                                    onLoad(newBuilding, "Campfire");
                                } else {
                                    draggedEntity.setVisible(false);
                                    draggedEntity.setMouseTransparent(false);
                                    node.setOpacity(1);
                                    event.setDropCompleted(false);
                                    return;
                                }
                                break;

                            case TRAP_CARD:
                                if (world.onPath(x, y) && world.squareEmpty(x, y)) {
                                    removeDraggableDragEventHandlers(draggableType, targetGridPane);
                                    newBuilding = convertCardToBuilding(nodeX, nodeY, x, y);
                                    onLoad(newBuilding, "Trap");
                                } else {
                                    draggedEntity.setVisible(false);
                                    draggedEntity.setMouseTransparent(false);
                                    node.setOpacity(1);
                                    event.setDropCompleted(false);
                                    return;
                                }
                                break;
                                
                            case VILLAGE_CARD:
                                if (world.onPath(x, y) && world.squareEmpty(x, y)) {
                                    newBuilding = convertCardToBuilding(nodeX, nodeY, x, y);
                                    removeDraggableDragEventHandlers(draggableType, targetGridPane);
                                    onLoad(newBuilding, "Village");
                                } else {
                                    draggedEntity.setVisible(false);
                                    draggedEntity.setMouseTransparent(false);
                                    node.setOpacity(1);
                                    event.setDropCompleted(false);
                                    return;
                                }
                                break;

                            case TOWER_CARD:
                                if (world.adjacentToPath(x, y) && world.squareEmpty(x, y)) {
                                    removeDraggableDragEventHandlers(draggableType, targetGridPane);
                                    newBuilding = convertCardToBuilding(nodeX, nodeY, x, y);
                                    onLoad(newBuilding, "Tower");
                                } else {
                                    draggedEntity.setVisible(false);
                                    draggedEntity.setMouseTransparent(false);
                                    node.setOpacity(1);
                                    event.setDropCompleted(false);
                                    return;
                                }
                                break;

                            case BARRACKS_CARD:
                                if (world.onPath(x, y) && world.squareEmpty(x, y)) {
                                    removeDraggableDragEventHandlers(draggableType, targetGridPane);
                                    newBuilding = convertCardToBuilding(nodeX, nodeY, x, y);
                                    onLoad(newBuilding, "Barracks");
                                } else {
                                    draggedEntity.setVisible(false);
                                    draggedEntity.setMouseTransparent(false);
                                    node.setOpacity(1);
                                    event.setDropCompleted(false);
                                    return;
                                }    
                                break;

                            case ZOMBIE_PIT_CARD:
                                if (world.adjacentToPath(x, y) && world.squareEmpty(x, y)) {
                                    removeDraggableDragEventHandlers(draggableType, targetGridPane);
                                    newBuilding = convertCardToBuilding(nodeX, nodeY, x, y);
                                    onLoad(newBuilding, "ZombiePit");
                                } else {
                                    draggedEntity.setVisible(false);
                                    draggedEntity.setMouseTransparent(false);
                                    node.setOpacity(1);
                                    event.setDropCompleted(false);
                                    return;
                                }
                                break;

                            case VAMPIRE_CASTLE_CARD:
                                if (world.adjacentToPath(x, y) && world.squareEmpty(x, y)) {
                                    removeDraggableDragEventHandlers(draggableType, targetGridPane);
                                    newBuilding = convertCardToBuilding(nodeX, nodeY, x, y);
                                    onLoad(newBuilding, "VampireCastle");
                                } else {
                                    draggedEntity.setVisible(false);
                                    draggedEntity.setMouseTransparent(false);
                                    node.setOpacity(1);
                                    event.setDropCompleted(false);
                                    return;
                                }
                                break;
                            
                            case DESTROYER_CARD:
                                if (!world.squareEmpty(x, y) && !world.isHerosCastleCoord(x, y)) {
                                    removeDraggableDragEventHandlers(draggableType, targetGridPane);
                                    world.destroyCard(nodeX, nodeY);
                                    world.destroyBuildingCoord(x, y);
                                    draggedEntity.setVisible(false);
                                } else {
                                    draggedEntity.setVisible(false);
                                    draggedEntity.setMouseTransparent(false);
                                    node.setOpacity(1);
                                    event.setDropCompleted(false);
                                    return;
                                }
                                break;
                            // ITEMS /////////////////////////////////////////////////////////////////////////

                            case ITEM:
                                Item unequippedItem = world.getCharacter().getUnequipped().getItemByCoordinates(nodeX, nodeY);
                                String type = unequippedItem.getClass().getSimpleName();
                                if (x == unequippedItem.getEquipSlotX() && y == unequippedItem.getEquipSlotY()) {
                                    removeDraggableDragEventHandlers(draggableType, targetGridPane);
                                    
                                    world.getCharacter().equipItem(unequippedItem);
                                    Item equippedItem = world.getCharacter().getEquipped().getItemByCoordinates(x, y);
                                    
                                    ImageView view;
                                    view = new ImageView(equippedItem.getImage());
                                    switch (type) {
                                        case "Sword": 
                                            addEntity((Sword) equippedItem, view);
                                            break;
                                        case "Stake": 
                                            addEntity((Stake) equippedItem, view);
                                            break;
                                        case "Staff": 
                                            addEntity((Staff) equippedItem, view);
                                            break;
                                        case "Armour": 
                                            addEntity((Armour) equippedItem, view);
                                            break;
                                        case "Shield": 
                                            addEntity((Shield) equippedItem, view);
                                            break;
                                        case "Helmet": 
                                            addEntity((Helmet) equippedItem, view);
                                            break;
                                        case "HealthPotion": 
                                            addEntity((HealthPotion) equippedItem, view);
                                            break;
                                        case "OneRing": 
                                            addEntity((OneRing) equippedItem, view);
                                            break;
                                        case "LuckyClover": 
                                            addEntity((LuckyClover) equippedItem, view);
                                            break;
                                        case "DenimShorts": 
                                            addEntity((DenimShorts) equippedItem, view);
                                            break;   
                                        case "Gameshark": 
                                            addEntity((GameShark) equippedItem, view);
                                            break;  
                                        case "Anduril":
                                            addEntity((Anduril) equippedItem, view);
                                            break; 
                                        case "TreeStump":
                                            addEntity((TreeStump) equippedItem, view);
                                            break;
                                    }
                                    equippedItems.getChildren().add(view);
                                    break;
                                
                                } else if (x == 2 && y == 1) {
                                    world.getCharacter().getUnequipped().removeItemByCoordinates(nodeX, nodeY);
                                    node.setOpacity(1);
                                    break;
                                
                                } else {
                                    draggedEntity.setVisible(false);
                                    draggedEntity.setMouseTransparent(false);
                                    node.setOpacity(1);
                                    event.setDropCompleted(false);
                                    return;
                                }

                            // DEFAULT //////////////////////////////////////////////////////////////////////

                            default:
                                draggedEntity.setVisible(false);
                                draggedEntity.setMouseTransparent(false);
                                node.setOpacity(1);
                                event.setDropCompleted(false);
                                return;
                        }
                        
                        node.setOpacity(1);
                        draggedEntity.setVisible(false);
                        draggedEntity.setMouseTransparent(false);
                        
                        // Remove drag event handlers before setting currently dragged image to null
                        currentlyDraggedImage = null;
                        currentlyDraggedType = null;
                        printThreadingNotes("DRAG DROPPED ON GRIDPANE HANDLED");
                    }
                }
                event.setDropCompleted(true);
                // Consuming prevents the propagation of the event to the anchorPaneRoot (as a sub-node of anchorPaneRoot, GridPane is prioritized)
                // https://openjfx.io/javadoc/11/javafx.base/javafx/event/Event.html#consume()
                // to understand this in full detail, ask your tutor or read https://docs.oracle.com/javase/8/javafx/events-tutorial/processing.htm
                event.consume();
            }
        });

        // This doesn't fire when we drag over GridPane because in the event handler for dragging over GridPanes, we consume the event
        anchorPaneRootSetOnDragOver.put(draggableType, new EventHandler<DragEvent>(){
            // https://github.com/joelgraff/java_fx_node_link_demo/blob/master/Draggable_Node/DraggableNodeDemo/src/application/RootLayout.java#L110
            @Override
            public void handle(DragEvent event) {
                if (currentlyDraggedType == draggableType){
                    if(event.getGestureSource() != anchorPaneRoot && event.getDragboard().hasImage()){
                        event.acceptTransferModes(TransferMode.MOVE);
                    }
                }
                if (currentlyDraggedType != null){
                    draggedEntity.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
                }
                event.consume();
            }
        });

        // This doesn't fire when we drop over GridPane because in the event handler for dropping over GridPanes, we consume the event
        anchorPaneRootSetOnDragDropped.put(draggableType, new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                if (currentlyDraggedType == draggableType){
                    // Data dropped
                    // If there is an image on the dragboard, read it and use it
                    Dragboard db = event.getDragboard();
                    Node node = event.getPickResult().getIntersectedNode();
                    if(node != anchorPaneRoot && db.hasImage()){
                        // Places at 0,0 - will need to take coordinates once that is implemented
                        currentlyDraggedImage.setVisible(true);
                        draggedEntity.setVisible(false);
                        draggedEntity.setMouseTransparent(false);
                        // Remove drag event handlers before setting currently dragged image to null
                        removeDraggableDragEventHandlers(draggableType, targetGridPane);
                        
                        currentlyDraggedImage = null;
                        currentlyDraggedType = null;
                    }
                }
                // Let the source know whether the image was successfully transferred and used
                event.setDropCompleted(true);
                event.consume();
            }
        });
    }

    /**
     * Remove the card from the world, and spawn and return a building instead where the card was dropped
     * @param cardNodeX the x coordinate of the card which was dragged, from 0 to width-1
     * @param cardNodeY the y coordinate of the card which was dragged (in starter code this is 0 as only 1 row of cards)
     * @param buildingNodeX the x coordinate of the drop location for the card, where the building will spawn, from 0 to width-1
     * @param buildingNodeY the y coordinate of the drop location for the card, where the building will spawn, from 0 to height-1
     * @return building entity returned from the world
     */
    private Building convertCardToBuilding(int cardNodeX, int cardNodeY, int buildingNodeX, int buildingNodeY) {
        return world.convertCardToBuildingByCoordinates(cardNodeX, cardNodeY, buildingNodeX, buildingNodeY);
    }

    /**
     * Add drag event handlers to an ImageView
     * @param view the view to attach drag event handlers to
     * @param draggableType the type of item being dragged - card or item
     * @param sourceGridPane the relevant gridpane from which the entity would be dragged
     * @param targetGridPane the relevant gridpane to which the entity would be dragged to
     */
    private void addDragEventHandlers(ImageView view, DRAGGABLE_TYPE draggableType, GridPane sourceGridPane, GridPane targetGridPane){
        view.setOnDragDetected(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                currentlyDraggedImage = view; // Set image currently being dragged, so squares setOnDragEntered can detect it...
                currentlyDraggedType = draggableType;
                // Drag was detected, start drap-and-drop gesture
                // Allow any transfer node
                Dragboard db = view.startDragAndDrop(TransferMode.MOVE);
    
                // Put ImageView on dragboard
                ClipboardContent cbContent = new ClipboardContent();
                cbContent.putImage(view.getImage());
                db.setContent(cbContent);
                view.setVisible(false);

                buildNonEntityDragHandlers(draggableType, sourceGridPane, targetGridPane);

                draggedEntity.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));

                int nodeX = GridPane.getColumnIndex(currentlyDraggedImage);
                int nodeY = GridPane.getRowIndex(currentlyDraggedImage);

                Item item = world.getCharacter().getUnequipped().getItemByCoordinates(nodeX, nodeY);

                switch (draggableType){
                    case CAMPFIRE_CARD:
                        draggedEntity.setImage(campfireCardImage);
                        break;
                    case TRAP_CARD:
                        draggedEntity.setImage(trapCardImage);
                        break;
                    case VILLAGE_CARD:
                        draggedEntity.setImage(villageCardImage);
                        break;
                    case TOWER_CARD:
                        draggedEntity.setImage(towerCardImage);
                        break;
                    case BARRACKS_CARD:
                        draggedEntity.setImage(barracksCardImage);
                        break;
                    case VAMPIRE_CASTLE_CARD:
                        draggedEntity.setImage(vampireCastleCardImage);
                        break;
                    case ZOMBIE_PIT_CARD:
                        draggedEntity.setImage(zombiePitCardImage);
                        break;
                    case DESTROYER_CARD:
                        draggedEntity.setImage(destroyerCardImage);
                        break;
                    case ITEM:
                        draggedEntity.setImage(item.getImage());
                        break;
                }
                
                draggedEntity.setVisible(true);
                draggedEntity.setMouseTransparent(true);
                draggedEntity.toFront();

                // IMPORTANT!!! --> to be able to remove event handlers, need to use addEventHandler
                // https://stackoverflow.com/a/67283792
                targetGridPane.addEventHandler(DragEvent.DRAG_DROPPED, gridPaneSetOnDragDropped.get(draggableType));
                anchorPaneRoot.addEventHandler(DragEvent.DRAG_OVER, anchorPaneRootSetOnDragOver.get(draggableType));
                anchorPaneRoot.addEventHandler(DragEvent.DRAG_DROPPED, anchorPaneRootSetOnDragDropped.get(draggableType));

                for (Node n: targetGridPane.getChildren()){
                    // Events for entering and exiting are attached to squares children because that impacts opacity change
                    // These do not affect visibility of original image...
                    // https://stackoverflow.com/questions/41088095/javafx-drag-and-drop-to-gridpane
                    gridPaneNodeSetOnDragEntered.put(draggableType, new EventHandler<DragEvent>() {
                        // Selective about whether highlighting changes - if it cannot be dropped in the location, the location shouldn't be highlighted!
                        public void handle(DragEvent event) {
                            if (currentlyDraggedType == draggableType){
                            // The drag-and-drop gesture entered the target --> show the user that it is an actual gesture target
                                if (event.getGestureSource() != n && event.getDragboard().hasImage()){
                                    Integer cIndex = GridPane.getColumnIndex(n);
                                    Integer rIndex = GridPane.getRowIndex(n);
                                    int x = cIndex == null ? 0 : cIndex;
                                    int y = rIndex == null ? 0 : rIndex;

                                    switch(draggableType) {
                                        case CAMPFIRE_CARD:
                                            if (! world.onPath(x, y) && world.squareEmpty(x, y)) n.setOpacity(0.5);
                                            break;
                                        
                                        case TRAP_CARD:
                                        case VILLAGE_CARD:
                                        case BARRACKS_CARD:
                                            if (world.onPath(x, y) && world.squareEmpty(x, y)) n.setOpacity(0.5);
                                            break;
            
                                        case TOWER_CARD:
                                        case ZOMBIE_PIT_CARD:
                                        case VAMPIRE_CASTLE_CARD:
                                            if (world.adjacentToPath(x, y) && world.squareEmpty(x, y)) n.setOpacity(0.5);
                                            break;
                                        case DESTROYER_CARD:
                                            if (!world.squareEmpty(x, y) && !world.isHerosCastleCoord(x, y)) n.setOpacity(0.5);
                                            break;
                                        case ITEM:
                                            if ((x == item.getEquipSlotX() && y == item.getEquipSlotY()) || (x == 2 && y == 1)) n.setOpacity(0.5);
                                            break;
                                    }
                                }
                            }
                            event.consume();
                        }
                    });
                    gridPaneNodeSetOnDragExited.put(draggableType, new EventHandler<DragEvent>() {
                        // TODO = since being more selective about whether highlighting changes, you could program the game so if the new highlight location is invalid the highlighting doesn't change, or leave this as-is
                        public void handle(DragEvent event) {
                            if (currentlyDraggedType == draggableType){
                                n.setOpacity(1);
                            }
                
                            event.consume();
                        }
                    });
                    n.addEventHandler(DragEvent.DRAG_ENTERED, gridPaneNodeSetOnDragEntered.get(draggableType));
                    n.addEventHandler(DragEvent.DRAG_EXITED, gridPaneNodeSetOnDragExited.get(draggableType));
                }
                event.consume();
            }
            
        });
    }

    /**
     * Remove drag event handlers so that we don't process redundant events
     * this is particularly important for slower machines such as over VLAB.
     * @param draggableType either cards, or items in unequipped inventory
     * @param targetGridPane the gridpane to remove the drag event handlers from
     */
    private void removeDraggableDragEventHandlers(DRAGGABLE_TYPE draggableType, GridPane targetGridPane){
        // Remove event handlers from nodes in children squares, from anchorPaneRoot, and squares
        targetGridPane.removeEventHandler(DragEvent.DRAG_DROPPED, gridPaneSetOnDragDropped.get(draggableType));

        anchorPaneRoot.removeEventHandler(DragEvent.DRAG_OVER, anchorPaneRootSetOnDragOver.get(draggableType));
        anchorPaneRoot.removeEventHandler(DragEvent.DRAG_DROPPED, anchorPaneRootSetOnDragDropped.get(draggableType));

        for (Node n: targetGridPane.getChildren()){
            n.removeEventHandler(DragEvent.DRAG_ENTERED, gridPaneNodeSetOnDragEntered.get(draggableType));
            n.removeEventHandler(DragEvent.DRAG_EXITED, gridPaneNodeSetOnDragExited.get(draggableType));
        }
    }

    /**
     * Handle the pressing of keyboard keys.
     * Specifically, we should pause when pressing SPACE
     * @param event some keyboard key press
     */
    @FXML
    public void handleKeyPress(KeyEvent event) {
        switch (event.getCode()) {
        case SPACE:
            if (isPaused) {
                startTimer();
            }
            else {
                pause();
            }
            break;
        case ENTER:
            if (world.useHealthPotion()) {
                Timeline emphasiseAnimation = new Timeline();
                emphasiseAnimation.setCycleCount(4);
                emphasiseAnimation.setAutoReverse(true);
                KeyValue kv1 = new KeyValue(healthBar.scaleXProperty(), 1.1, Interpolator.EASE_BOTH);
                KeyValue kv2 = new KeyValue(healthBar.scaleYProperty(), 1.1, Interpolator.EASE_BOTH);
                KeyFrame kf = new KeyFrame(Duration.millis(100), kv1, kv2);
                emphasiseAnimation.getKeyFrames().addAll(kf);
                emphasiseAnimation.play();
            }
            
        default:
            break;
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setMainMenuSwitcher(MenuSwitcher mainMenuSwitcher){
        this.mainMenuSwitcher = mainMenuSwitcher;
    }

    /**
     * This method is triggered when click button to go to main menu in FXML
     * @throws IOException
     */
    @FXML
    private void switchToMainMenu() throws IOException {
        pause();
        mainMenuSwitcher.switchMenu();
    }

    public void setHeroCastleSwitcher(MenuSwitcher heroCastleSwitcher) {
        this.heroCastleSwitcher = heroCastleSwitcher;
    }

    @FXML
    private void switchToHeroCastle() throws IOException {
        heroCastleSwitcher.switchMenu();
    }

    public Character getCharacter() {
        return this.character;
    }

    public void updateInventoryFromHeroCastle() throws Exception {
        ArrayList<Item> boughItems = character.getBoughItems();
        for (Item item: boughItems) {
            String type = item.getStats().getItemName();
            if (type.equals("Sword") || type.equals("Stake") || type.equals("Staff") || type.equals("Armour") 
                || type.equals("Shield") || type.equals("Helmet") || type.equals("HealthPotion") || type.equals("OneRing")
                || type.equals("LuckyClover") || type.equals("Gameshark") || type.equals("DenimShorts")) {
                        onLoad(item, type);
            }                           
        }       
        character.clearBoughtItem();
    }

    public void updateGameModeText(String mode) {
        gameModeText.setText(mode);
    }
    
    /*
     * Set a node in a GridPane to have its position track the position of an
     * entity in the world.
     *
     * By connecting the model with the view in this way, the model requires no
     * knowledge of the view and changes to the position of entities in the
     * model will automatically be reflected in the view.
     * 
     * note that this is put in the controller rather than the loader because we need to track positions of spawned entities such as enemy
     * or items which might need to be removed should be tracked here
     * 
     * NOTE teardown functions setup here also remove nodes from their GridPane. So it is vital this is handled in this Controller class
     * @param entity
     * @param node
     */
    private void trackPosition(Entity entity, Node node) {
        // TODO = tweak this slightly to remove items from the equipped inventory?
        GridPane.setColumnIndex(node, entity.getX());
        GridPane.setRowIndex(node, entity.getY());

        ChangeListener<Number> xListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable,
                    Number oldValue, Number newValue) {
                GridPane.setColumnIndex(node, newValue.intValue());
            }
        };
        ChangeListener<Number> yListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable,
                    Number oldValue, Number newValue) {
                GridPane.setRowIndex(node, newValue.intValue());
            }
        };

        // if need to remove items from the equipped inventory, add code to remove from equipped inventory gridpane in the .onDetach part
        ListenerHandle handleX = ListenerHandles.createFor(entity.x(), node)
                                               .onAttach((o, l) -> o.addListener(xListener))
                                               .onDetach((o, l) -> {
                                                    o.removeListener(xListener);
                                                    entityImages.remove(node);
                                                    squares.getChildren().remove(node);
                                                    cardInventory.getChildren().remove(node);
                                                    equippedItems.getChildren().remove(node);
                                                    unequippedInventory.getChildren().remove(node);
                                                })
                                               .buildAttached();
        ListenerHandle handleY = ListenerHandles.createFor(entity.y(), node)
                                               .onAttach((o, l) -> o.addListener(yListener))
                                               .onDetach((o, l) -> {
                                                   o.removeListener(yListener);
                                                   entityImages.remove(node);
                                                   squares.getChildren().remove(node);
                                                   cardInventory.getChildren().remove(node);
                                                   equippedItems.getChildren().remove(node);
                                                   unequippedInventory.getChildren().remove(node);
                                                })
                                               .buildAttached();
        handleX.attach();
        handleY.attach();

        // this means that if we change boolean property in an entity tracked from here, position will stop being tracked
        // this wont work on character/path entities loaded from loader classes
        entity.shouldExist().addListener(new ChangeListener<Boolean>(){
            @Override
            public void changed(ObservableValue<? extends Boolean> obervable, Boolean oldValue, Boolean newValue) {
                handleX.detach();
                handleY.detach();
            }
        });
    }

    /**
     * We added this method to help with debugging so you could check your code is running on the application thread.
     * By running everything on the application thread, you will not need to worry about implementing locks, which is outside the scope of the course.
     * Always writing code running on the application thread will make the project easier, as long as you are not running time-consuming tasks.
     * We recommend only running code on the application thread, by using Timelines when you want to run multiple processes at once.
     * EventHandlers will run on the application thread.
     */
    private void printThreadingNotes(String currentMethodLabel) {
        System.out.println("\n###########################################");
        System.out.println("current method = "+currentMethodLabel);
        System.out.println("In application thread? = "+Platform.isFxApplicationThread());
        System.out.println("Current system time = "+java.time.LocalDateTime.now().toString().replace('T', ' '));
    }
}