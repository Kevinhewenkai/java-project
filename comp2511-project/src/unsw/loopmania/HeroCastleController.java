package unsw.loopmania;

import java.util.List;

import org.codefx.libfx.listener.handle.ListenerHandle;
import org.codefx.libfx.listener.handle.ListenerHandles;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
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
import javafx.scene.layout.GridPane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;

import java.util.ArrayList;
import java.util.EnumMap;
import java.io.File;
import java.io.IOException;

public class HeroCastleController {
    /**
     * facilitates switching to main game
     */
    private MenuSwitcher gameSwitcher;

    public void setGameSwitcher(MenuSwitcher gameSwitcher) {
        this.gameSwitcher = gameSwitcher;
    }

    /**
     * facilitates switching to main game upon button click
     * 
     * @throws IOException
     */
    @FXML
    private void switchToGame() throws IOException {
        gameSwitcher.switchMenu();
    }

    @FXML
    private AnchorPane anchorPaneRoot;

    @FXML
    private Button buySwordButton;

    @FXML
    private Button buyStaffButton;

    @FXML
    private Button buyHelmetButton;

    @FXML
    private Button buyStakeButton;

    @FXML
    private Button buyAmourButton;

    @FXML
    private Button buyPotionButton;

    @FXML
    private Button buyShieldButton;

    @FXML
    private Button buyLuckyCoverButton;

    @FXML
    private Button leaveButton;

    @FXML
    private GridPane unequipedInventory;

    @FXML
    private Label goldCount;

    @FXML
    private GridPane pawnshop;

    private HeroCastle heroCastle;
    private Character character;
    private static String gameMode;
    /**
     * when we drag a card/item, the picture for whatever we're dragging is set here
     * and we actually drag this node
     */
    private DragIcon draggedEntity;

    // ITEM IMAGES
    private Image swordImage;
    private Image stakeImage;
    private Image staffImage;
    private Image armourImage;
    private Image shieldImage;
    private Image helmetImage;
    private Image healthPotionImage;
    private Image pawnshopImage;

    // RARE ITEM IMAGES
    private Image oneRingImage;
    private Image luckyCloverImage;
    private Image denimShortsImage;
    private Image gamesharkImage;

    private Image doggieCoinImage;
    // all image views including tiles, character, enemies, cards... even though
    // cards in separate gridpane...
    private List<ImageView> entityImages;

    /**
     * the image currently being dragged, if there is one, otherwise null. Holding
     * the ImageView being dragged allows us to spawn it again in the drop location
     * if appropriate.
     */
    // TODO = it would be a good idea for you to instead replace this with the
    // building/item which should be dropped
    private ImageView currentlyDraggedImage;

    /**
     * null if nothing being dragged, or the type of item being dragged
     */
    private DRAGGABLE_TYPE currentlyDraggedType;

    /**
     * mapping from draggable type enum CARD/TYPE to the event handler triggered
     * when the draggable type is dropped over its appropriate gridpane
     */
    private EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>> gridPaneSetOnDragDropped;
    /**
     * mapping from draggable type enum CARD/TYPE to the event handler triggered
     * when the draggable type is dragged over the background
     */
    private EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>> anchorPaneRootSetOnDragOver;
    /**
     * mapping from draggable type enum CARD/TYPE to the event handler triggered
     * when the draggable type is dropped in the background
     */
    private EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>> anchorPaneRootSetOnDragDropped;
    /**
     * mapping from draggable type enum CARD/TYPE to the event handler triggered
     * when the draggable type is dragged into the boundaries of its appropriate
     * gridpane
     */
    private EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>> gridPaneNodeSetOnDragEntered;
    /**
     * mapping from draggable type enum CARD/TYPE to the event handler triggered
     * when the draggable type is dragged outside of the boundaries of its
     * appropriate gridpane
     */
    private EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>> gridPaneNodeSetOnDragExited;

    public HeroCastleController(Character character) {
        heroCastle = new HeroCastle();
        this.character = character;
        entityImages = new ArrayList<>();
        // ITEM IMAGES
        swordImage = new Image((new File("src/images/basic_sword.png")).toURI().toString());
        stakeImage = new Image((new File("src/images/stake.png")).toURI().toString());
        staffImage = new Image((new File("src/images/staff.png")).toURI().toString());
        armourImage = new Image((new File("src/images/armour.png")).toURI().toString());
        shieldImage = new Image((new File("src/images/shield.png")).toURI().toString());
        helmetImage = new Image((new File("src/images/helmet.png")).toURI().toString());
        pawnshopImage = new Image((new File("src/images/gold.png")).toURI().toString());
        healthPotionImage = new Image((new File("src/images/brilliant_blue_new.png")).toURI().toString());

        // RARE ITEM IMAGES
        oneRingImage = new Image((new File("src/images/the_one_ring.png")).toURI().toString());
        luckyCloverImage = new Image((new File("src/images/lucky_clover.png")).toURI().toString());
        denimShortsImage = new Image((new File("src/images/denim_shorts.png")).toURI().toString());
        gamesharkImage = new Image((new File("src/images/gameshark.png")).toURI().toString());

        doggieCoinImage = new Image((new File("src/images/doggie_coin.png")).toURI().toString());
        // OTHER
        currentlyDraggedImage = null;
        currentlyDraggedType = null;

        gridPaneSetOnDragDropped = new EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>>(DRAGGABLE_TYPE.class);
        anchorPaneRootSetOnDragOver = new EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>>(DRAGGABLE_TYPE.class);
        anchorPaneRootSetOnDragDropped = new EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>>(DRAGGABLE_TYPE.class);
        gridPaneNodeSetOnDragEntered = new EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>>(DRAGGABLE_TYPE.class);
        gridPaneNodeSetOnDragExited = new EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>>(DRAGGABLE_TYPE.class);
    }

    @FXML
    public void initialize() throws Exception {
        Image inventorySlotImage = new Image((new File("src/images/empty_slot.png")).toURI().toString());

        // add the empty slot images for the unequipped inventory
        for (int x = 0; x < LoopManiaWorld.UNEQUIPPED_INVENTORY_WIDTH; x++) {
            for (int y = 0; y < LoopManiaWorld.UNEQUIPPED_INVENTORY_HEIGHT; y++) {
                ImageView emptySlotView = new ImageView(inventorySlotImage);
                unequipedInventory.add(emptySlotView, x, y);
            }
        }

        // Initialise the image of pawnshop
        ImageView pawnShopView = new ImageView(pawnshopImage);
        pawnshop.getChildren().add(pawnShopView);

        // create the draggable icon
        draggedEntity = new DragIcon();
        draggedEntity.setVisible(false);
        draggedEntity.setOpacity(0.7);
        anchorPaneRoot.getChildren().add(draggedEntity);
    }

    public void updateGold() {
        goldCount.setText(String.valueOf(character.getGold()));
    }

    @FXML
    void buyArmour(ActionEvent event) {
        Armour armour = (Armour) character.buy(heroCastle.loadItem("armour"));
        if (armour != null) {
            onLoad(armour, "armour", unequipedInventory);
        }
        updateGold();
        if (gameMode.equals("berserker")) {
            buyAmourButton.setDisable(true);
            buyHelmetButton.setDisable(true);
            buyShieldButton.setDisable(true);
        }
    }

    @FXML
    void buyHelmet(ActionEvent event) {
        Helmet helmet = (Helmet) character.buy(heroCastle.loadItem("helmet"));
        if (helmet != null) {
            onLoad(helmet, "helmet", unequipedInventory);
        }
        updateGold();
        if (gameMode.equals("berserker")) {
            buyAmourButton.setDisable(true);
            buyHelmetButton.setDisable(true);
            buyShieldButton.setDisable(true);
        }
    }

    @FXML
    void buyPotion(ActionEvent event) {
        HealthPotion potion = (HealthPotion) character.buy(heroCastle.loadItem("healthpotion"));
        if (potion != null) {
            onLoad(potion, "healthpotion", unequipedInventory);
        }
        updateGold();
        if (HeroCastleController.gameMode.equals("survive"))
            buyPotionButton.setDisable(true);
    }

    @FXML
    void buyStaff(ActionEvent event) {
        Staff staff = (Staff) character.buy(heroCastle.loadItem("staff"));
        if (staff != null) {
            onLoad(staff, "staff", unequipedInventory);
        }
        updateGold();
    }

    @FXML
    void buyStake(ActionEvent event) {
        Stake stake = (Stake) character.buy(heroCastle.loadItem("stake"));
        if (stake != null) {
            onLoad(stake, "stake", unequipedInventory);
        }
        updateGold();
    }

    @FXML
    void buySword(ActionEvent event) {
        Sword sword = (Sword) character.buy(heroCastle.loadItem("sword"));
        if (sword != null) {
            onLoad(sword, "sword", unequipedInventory);
        }
        updateGold();
    }

    @FXML
    void buyShield(ActionEvent event) {
        Shield shield = (Shield) character.buy(heroCastle.loadItem("shield"));
        if (shield != null) {
            onLoad(shield, "shield", unequipedInventory);
        }
        updateGold();
        if (gameMode.equals("berserker")) {
            buyAmourButton.setDisable(true);
            buyHelmetButton.setDisable(true);
            buyShieldButton.setDisable(true);
        }
    }

    @FXML
    void buyLuckyCover(ActionEvent event) {
        LuckyClover luckyClover = (LuckyClover) character.buy(heroCastle.loadItem("luckycover"));
        if (luckyClover != null) {
            onLoad(luckyClover, "luckycover", unequipedInventory);
        }
        updateGold();
    }

    @FXML
    void leave(ActionEvent event) throws IOException {
        switchToGame();
    }

    /**
     * handle the pressing of keyboard keys. Specifically, we should pause when
     * pressing SPACE
     * 
     * @param event some keyboard key press
     * @throws IOException
     */
    @FXML
    public void handleKeyPress(KeyEvent event) throws IOException {
        switch (event.getCode()) {
            case ESCAPE:
                switchToGame();
                break;
            default:
                break;
        }
    }

    /**
     * load an item into the GUI. Particularly, we must connect to the drag
     * detection event handler, and load the image into the unequippedInventory
     * GridPane.
     * 
     * @param sword
     */
    private void onLoad(Item item, String type, GridPane inventory) {
        ImageView view;
        switch (type) {
            case "sword":
                view = new ImageView(swordImage);
                addDragEventHandlers(view, DRAGGABLE_TYPE.ITEM, inventory, pawnshop);
                addEntity((Sword) item, view);
                break;
            case "stake":
                view = new ImageView(stakeImage);
                addDragEventHandlers(view, DRAGGABLE_TYPE.ITEM, inventory, pawnshop);
                addEntity((Stake) item, view);
                break;
            case "staff":
                view = new ImageView(staffImage);
                addDragEventHandlers(view, DRAGGABLE_TYPE.ITEM, inventory, pawnshop);
                addEntity((Staff) item, view);
                break;
            case "armour":
                view = new ImageView(armourImage);
                addDragEventHandlers(view, DRAGGABLE_TYPE.ITEM, inventory, pawnshop);
                addEntity((Armour) item, view);
                break;
            case "shield":
                view = new ImageView(shieldImage);
                addDragEventHandlers(view, DRAGGABLE_TYPE.ITEM, inventory, pawnshop);
                addEntity((Shield) item, view);
                break;
            case "helmet":
                view = new ImageView(helmetImage);
                addDragEventHandlers(view, DRAGGABLE_TYPE.ITEM, inventory, pawnshop);
                addEntity((Helmet) item, view);
                break;
            case "healthpotion":
                view = new ImageView(healthPotionImage);
                addDragEventHandlers(view, DRAGGABLE_TYPE.ITEM, inventory, pawnshop);
                addEntity((HealthPotion) item, view);
                break;
            case "onering":
                view = new ImageView(oneRingImage);
                addDragEventHandlers(view, DRAGGABLE_TYPE.ITEM, inventory, pawnshop);
                addEntity((OneRing) item, view);
                break;
            case "luckyclover":
                view = new ImageView(luckyCloverImage);
                addDragEventHandlers(view, DRAGGABLE_TYPE.ITEM, inventory, pawnshop);
                addEntity((LuckyClover) item, view);
                break;
            case "denimshorts":
                view = new ImageView(denimShortsImage);
                addDragEventHandlers(view, DRAGGABLE_TYPE.ITEM, inventory, pawnshop);
                addEntity((DenimShorts) item, view);
                break;
            case "gameshark":
                view = new ImageView(gamesharkImage);
                addDragEventHandlers(view, DRAGGABLE_TYPE.ITEM, inventory, pawnshop);
                addEntity((GameShark) item, view);
                break;
            case "doggie_coin":
                view = new ImageView(doggieCoinImage);
                addDragEventHandlers(view, DRAGGABLE_TYPE.ITEM, inventory, pawnshop);
                addEntity((DoggieCoin) item, view);
                break;
            default:
                return;
        }

        // inventory.add(view, item.getX(), item.getY());
        inventory.getChildren().add(view);
    }

    /**
     * pair the entity an view so that the view copies the movements of the entity.
     * add view to list of entity images
     * 
     * @param entity backend entity to be paired with view
     * @param view   frontend imageview to be paired with backend entity
     */
    private void addEntity(Entity entity, ImageView view) {
        trackPosition(entity, view);
        entityImages.add(view);
    }

    /**
     * add drag event handlers for dropping into gridpanes, dragging over the
     * background, dropping over the background. These are not attached to invidual
     * items such as swords/cards.
     * 
     * @param draggableType  the type being dragged - card or item
     * @param sourceGridPane the gridpane being dragged from
     * @param targetGridPane the gridpane the human player should be dragging to
     *                       (but we of course cannot guarantee they will do so)
     */
    private void buildNonEntityDragHandlers(DRAGGABLE_TYPE draggableType, GridPane sourceGridPane,
            GridPane targetGridPane) {
        // TODO = be more selective about where something can be dropped
        // for example, in the specification, villages can only be dropped on path,
        // whilst vampire castles cannot go on the path

        gridPaneSetOnDragDropped.put(draggableType, new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                // TODO = for being more selective about where something can be dropped,
                // consider applying additional if-statement logic
                // if (targetGridPane != pawnshop) {
                // return;
                // }
                /*
                 * you might want to design the application so dropping at an invalid location
                 * drops at the most recent valid location hovered over, or simply allow the
                 * card/item to return to its slot (the latter is easier, as you won't have to
                 * store the last valid drop location!)
                 */
                if (currentlyDraggedType == draggableType) {
                    // problem = event is drop completed is false when should be true...
                    // https://bugs.openjdk.java.net/browse/JDK-8117019
                    // putting drop completed at start not making complete on VLAB...

                    // Data dropped
                    // If there is an image on the dragboard, read it and use it
                    Dragboard db = event.getDragboard();
                    Node node = event.getPickResult().getIntersectedNode();
                    if (node != targetGridPane && db.hasImage()) {
                        // Integer cIndex = GridPane.getColumnIndex(node);
                        // Integer rIndex = GridPane.getRowIndex(node);
                        // int x = cIndex == null ? 0 : cIndex;
                        // int y = rIndex == null ? 0 : rIndex;

                        int nodeX = GridPane.getColumnIndex(currentlyDraggedImage);
                        int nodeY = GridPane.getRowIndex(currentlyDraggedImage);

                        // TODO: sell()
                        removeDraggableDragEventHandlers(draggableType, targetGridPane);
                        character.sell(nodeX, nodeY);

                        node.setOpacity(1);
                        draggedEntity.setVisible(false);
                        draggedEntity.setMouseTransparent(false);
                        // remove drag event handlers before setting currently dragged image to null
                        currentlyDraggedImage = null;
                        currentlyDraggedType = null;

                        updateGold();
                        // printThreadingNotes("DRAG DROPPED ON GRIDPANE HANDLED");
                    }
                }
                event.setDropCompleted(true);
                // consuming prevents the propagation of the event to the anchorPaneRoot (as a
                // sub-node of anchorPaneRoot, GridPane is prioritized)
                // https://openjfx.io/javadoc/11/javafx.base/javafx/event/Event.html#consume()
                // to understand this in full detail, ask your tutor or read
                // https://docs.oracle.com/javase/8/javafx/events-tutorial/processing.htm
                event.consume();
            }
        });

        // this doesn't fire when we drag over GridPane because in the event handler for
        // dragging over GridPanes, we consume the event
        anchorPaneRootSetOnDragOver.put(draggableType, new EventHandler<DragEvent>() {
            // https://github.com/joelgraff/java_fx_node_link_demo/blob/master/Draggable_Node/DraggableNodeDemo/src/application/RootLayout.java#L110
            @Override
            public void handle(DragEvent event) {
                if (currentlyDraggedType == draggableType) {
                    if (event.getGestureSource() != anchorPaneRoot && event.getDragboard().hasImage()) {
                        event.acceptTransferModes(TransferMode.MOVE);
                    }
                }
                if (currentlyDraggedType != null) {
                    draggedEntity.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
                }
                event.consume();
            }
        });

        // this doesn't fire when we drop over GridPane because in the event handler for
        // dropping over GridPanes, we consume the event
        anchorPaneRootSetOnDragDropped.put(draggableType, new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                if (currentlyDraggedType == draggableType) {
                    // Data dropped
                    // If there is an image on the dragboard, read it and use it
                    Dragboard db = event.getDragboard();
                    Node node = event.getPickResult().getIntersectedNode();
                    if (node != anchorPaneRoot && db.hasImage()) {
                        // Places at 0,0 - will need to take coordinates once that is implemented
                        currentlyDraggedImage.setVisible(true);
                        draggedEntity.setVisible(false);
                        draggedEntity.setMouseTransparent(false);
                        // remove drag event handlers before setting currently dragged image to null
                        removeDraggableDragEventHandlers(draggableType, targetGridPane);

                        currentlyDraggedImage = null;
                        currentlyDraggedType = null;
                    }
                }
                // let the source know whether the image was successfully transferred and used
                event.setDropCompleted(true);
                event.consume();
            }
        });
    }

    /**
     * add drag event handlers to an ImageView
     * 
     * @param view           the view to attach drag event handlers to
     * @param draggableType  the type of item being dragged - card or item
     * @param sourceGridPane the relevant gridpane from which the entity would be
     *                       dragged
     * @param targetGridPane the relevant gridpane to which the entity would be
     *                       dragged to
     */
    private void addDragEventHandlers(ImageView view, DRAGGABLE_TYPE draggableType, GridPane sourceGridPane,
            GridPane targetGridPane) {
        view.setOnDragDetected(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                currentlyDraggedImage = view;
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
                Item item = character.getUnequipped().getItemByCoordinates(nodeX, nodeY);

                switch (draggableType) {
                    case ITEM:
                        draggedEntity.setImage(item.getImage());
                        break;

                    default:
                        break;
                }

                draggedEntity.setVisible(true);
                draggedEntity.setMouseTransparent(true);
                draggedEntity.toFront();

                // IMPORTANT!!!
                // to be able to remove event handlers, need to use addEventHandler
                // https://stackoverflow.com/a/67283792
                targetGridPane.addEventHandler(DragEvent.DRAG_DROPPED, gridPaneSetOnDragDropped.get(draggableType));
                anchorPaneRoot.addEventHandler(DragEvent.DRAG_OVER, anchorPaneRootSetOnDragOver.get(draggableType));
                anchorPaneRoot.addEventHandler(DragEvent.DRAG_DROPPED,
                        anchorPaneRootSetOnDragDropped.get(draggableType));

                for (Node n : targetGridPane.getChildren()) {
                    // events for entering and exiting are attached to squares children because that
                    // impacts opacity change
                    // these do not affect visibility of original image...
                    // https://stackoverflow.com/questions/41088095/javafx-drag-and-drop-to-gridpane
                    gridPaneNodeSetOnDragEntered.put(draggableType, new EventHandler<DragEvent>() {
                        // TODO = be more selective about whether highlighting changes - if it cannot be
                        // dropped in the location, the location shouldn't be highlighted!
                        public void handle(DragEvent event) {
                            if (currentlyDraggedType == draggableType) {
                                // The drag-and-drop gesture entered the target
                                // show the user that it is an actual gesture target
                                if (event.getGestureSource() != n && event.getDragboard().hasImage()) {
                                    n.setOpacity(0.3);
                                }
                            }
                            event.consume();
                        }
                    });
                    gridPaneNodeSetOnDragExited.put(draggableType, new EventHandler<DragEvent>() {
                        // TODO = since being more selective about whether highlighting changes, you
                        // could program the game so if the new highlight location is invalid the
                        // highlighting doesn't change, or leave this as-is
                        public void handle(DragEvent event) {
                            if (currentlyDraggedType == draggableType) {
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
     * remove drag event handlers so that we don't process redundant events this is
     * particularly important for slower machines such as over VLAB.
     * 
     * @param draggableType  either cards, or items in unequipped inventory
     * @param targetGridPane the gridpane to remove the drag event handlers from
     */
    private void removeDraggableDragEventHandlers(DRAGGABLE_TYPE draggableType, GridPane targetGridPane) {
        // remove event handlers from nodes in children squares, from anchorPaneRoot,
        // and squares
        targetGridPane.removeEventHandler(DragEvent.DRAG_DROPPED, gridPaneSetOnDragDropped.get(draggableType));

        anchorPaneRoot.removeEventHandler(DragEvent.DRAG_OVER, anchorPaneRootSetOnDragOver.get(draggableType));
        anchorPaneRoot.removeEventHandler(DragEvent.DRAG_DROPPED, anchorPaneRootSetOnDragDropped.get(draggableType));

        for (Node n : targetGridPane.getChildren()) {
            n.removeEventHandler(DragEvent.DRAG_ENTERED, gridPaneNodeSetOnDragEntered.get(draggableType));
            n.removeEventHandler(DragEvent.DRAG_EXITED, gridPaneNodeSetOnDragExited.get(draggableType));
        }
    }

    /**
     * Set a node in a GridPane to have its position track the position of an entity
     * in the world.
     *
     * By connecting the model with the view in this way, the model requires no
     * knowledge of the view and changes to the position of entities in the model
     * will automatically be reflected in the view.
     * 
     * note that this is put in the controller rather than the loader because we
     * need to track positions of spawned entities such as enemy or items which
     * might need to be removed should be tracked here
     * 
     * NOTE teardown functions setup here also remove nodes from their GridPane. So
     * it is vital this is handled in this Controller class
     * 
     * @param entity
     * @param node
     */
    private void trackPosition(Entity entity, Node node) {
        // TODO = tweak this slightly to remove items from the equipped inventory?
        GridPane.setColumnIndex(node, entity.getX());
        GridPane.setRowIndex(node, entity.getY());

        ChangeListener<Number> xListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                GridPane.setColumnIndex(node, newValue.intValue());
            }
        };
        ChangeListener<Number> yListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                GridPane.setRowIndex(node, newValue.intValue());
            }
        };

        // if need to remove items from the equipped inventory, add code to remove from
        // equipped inventory gridpane in the .onDetach part
        ListenerHandle handleX = ListenerHandles.createFor(entity.x(), node)
                .onAttach((o, l) -> o.addListener(xListener)).onDetach((o, l) -> {
                    o.removeListener(xListener);
                    entityImages.remove(node);
                    unequipedInventory.getChildren().remove(node);
                }).buildAttached();
        ListenerHandle handleY = ListenerHandles.createFor(entity.y(), node)
                .onAttach((o, l) -> o.addListener(yListener)).onDetach((o, l) -> {
                    o.removeListener(yListener);
                    entityImages.remove(node);
                    unequipedInventory.getChildren().remove(node);
                }).buildAttached();
        handleX.attach();
        handleY.attach();

        // this means that if we change boolean property in an entity tracked from here,
        // position will stop being tracked
        // this wont work on character/path entities loaded from loader classes
        entity.shouldExist().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> obervable, Boolean oldValue, Boolean newValue) {
                handleX.detach();
                handleY.detach();
            }
        });
    }

    public void updateInventory() {
        for (Item e : character.getUnequipped().getItems()) {

            String type = e.getStats().getItemName().toLowerCase();
            if (type.equals("sword") || type.equals("stake") || type.equals("staff") || type.equals("armour")
                    || type.equals("shield") || type.equals("helmet") || type.equals("healthpotion")
                    || type.equals("onering") || type.equals("luckyclover") || type.equals("denimshorts")
                    || type.equals("gameshark")) {
                onLoad(e, type, unequipedInventory);
            }
        }

    }

    public void activeBuyingPotion() {
        if (buyPotionButton.isDisable()) {
            buyPotionButton.setDisable(false);
        }
    }

    public void activeDefenceItem() {
        if (buyAmourButton.isDisable()) {
            buyAmourButton.setDisable(false);
        }
        if (buyHelmetButton.isDisable()) {
            buyHelmetButton.setDisable(false);
        }
        if (buyShieldButton.isDisable()) {
            buyShieldButton.setDisable(false);
        }
    }

    public void setGameMode(String gameMode) {
        HeroCastleController.gameMode = gameMode;
    }

    public static String getGameMode() {
        return gameMode;
    }
}
