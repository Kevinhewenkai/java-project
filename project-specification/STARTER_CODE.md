# Starter Code Explanations 🧸

## Contents

[[_TOC_]]

## 0. Change Log

None

## Overview

This README is to assist you in understanding the starter code. It is in the style of an FAQ, and we will extend it based on queries.

The aim is for this README to cover a high-level of detail, to explain technical questions you are likely to have, as well as providing further details for the curious mind. You will not need to understand it in full detail to complete the basic specification, particularly because you should split tasks up between your team members. We recommend referring to it when you want to understand something in more detail, or are confused about how something works.

We have provided substantial frontend starter code using industry best-practices and a robust initial OOP design, to help you get stuck more quickly into Object Oriented Programming of your backend entities, and to help you build something awesome more quickly!

The following features (some of which are a bit fiddly) have been setup in the starter code for you:
* Handling drag-and-drop of cards onto the game map/items into the equipped inventory
* Loading the path
* Loading of the Character/enemies/cards/items/path/buildings
* Spawning a building upon dropping a dragged card onto the game world
* Removal of defeated enemies/used cards
* A main menu which can switch to the main game using a button click, and vice versa
* Character moves clockwise through the path around the world
* The enemies move randomly around the path
* Basic but incomplete implementations of interactions such as the player killing enemies, and obtaining swords/cards as a reward
* Game is paused upon pressing <kbd>Spacebar</kbd>, and can be resumed upon pressing <kbd>Spacebar</kbd>

Below are elaborations on how different components of the starter code work (including the above features), and delves into some questions you might have regarding it.

You should feel free to modify anything in the starter code you wish. However, when doing so, keep in mind best practices, and good OOP design. We have marked some items we believe you may need to modify with a TODO comment.

For example, you will need to add a new Hero's Castle shopping menu to purchase/sell items upon reaching the Hero's Castle (perhaps accessible via a button similarly to our setup), add new game entities (e.g. different types of equipment, new varieties of enemy), implement more complex backend behaviour e.g. for battles, and add a health bar and some other features.

You will need to make some modifications to the starter code in parts which are already written, such as storing more information about what is being dragged during drag-and-drop, or ensuring dropping of cards to spawn buildings only occurs on or off the path (depending on building type), or ensuring that when a weapon is equipped in a slot with an existing weapon then the existing weapon is returned to the inventory.

When implementing a shopping menu, if you intend to utilize drag-and-drop, you should be able to inspect the starter code and mimic its drag-and-drop functionality. A simpler (but perhaps less intuitive for the user) way to implement a drag-and-drop shopping menu that doesn't require detailed understanding of the starter code would be to utilize existing JavaFX widgets such as drag-and-drop interfaces/buttons in a menu. You should be able to achieve the latter by doing some basic research.

You will likely find these explanations particularly useful when implementing extensions - you should modify and re-engineer the starter code when implementing extensions.

We recommend asking your tutor during project-checkins, or posting questions on the forum, if you have any confusions regarding any of this.

## 1. How does Drag-and-Drop work?

The drag-and-drop functionality in the starter code utilizes event-driven programming. We have implemented this substantial feature for you so that you can focus on Object Oriented Programming in your backend entities more. You should not have to modify this much to implement the basic functionality, although some tweaks may be needed for features such as ensuring traps can only be placed on the path. However, depending on your choice of extensions, you may want to develop a deep understanding to implement them.

There are 6 major events related to drag-and-drop processed in the [`LoopManiaWorldController` class](src/unsw/loopmania/LoopManiaWorldController.java):

* The item being dragged detects that it is being dragged: *view.setOnDragDetected*
    * When handling this event, we load the image being dragged into the clipboard, setup all other drag [event handlers](https://openjfx.io/javadoc/11/javafx.base/javafx/event/EventHandler.html), make the item which was dragged invisible, and change the image of the draggable item to match the item the user started dragging
* The appropriate target of the item to be dropped detects its boundaries have intersected the boundaries of the item: *n.addEventHandler(DragEvent.DRAG_ENTERED, gridPaneNodeSetOnDragEntered.get(draggableType));*
    * When handling this event, the target of the item (such as a slot in the equipped inventory pane) has its opacity set to 0.7 so the user sees visually that the item can be dropped there.
    * **When ensuring that cards can only be placed on particular slots, you will want to modify this so that it only changes opacity if the card can be dropped in the slot.**
* The appropriate target of the item to be dropped detects its boundaries, where previously intersecting, are now disjoint from the boundaries of the item: *n.addEventHandler(DragEvent.DRAG_EXITED, gridPaneNodeSetOnDragExited.get(draggableType));*
    * When handling this event, the target of the item (such as a slot in the equipped inventory panel) has its opacity set to 1 (as normal) so that the user sees if they drop the item, it will no longer be placed into this slot
* The background detects the dragged item has been dragged over it: *anchorPaneRoot.addEventHandler(DragEvent.DRAG_OVER, anchorPaneRootSetOnDragOver.get(draggableType));*
    * When handling this event, the coordinates of the item are updated to follow the mouse
* The background detects the dragged item has been dropped onto it: *anchorPaneRoot.addEventHandler(DragEvent.DRAG_DROPPED, anchorPaneRootSetOnDragDropped.get(draggableType))*
    * When handling this event, the original dragged image (if it hasn't been removed already by the target removing the image) is made visible, the information about the dragged items is reset to null, the icon being dragged is made invisible, and all drag event handlers except for the item detecting a drag are removed to avoid spurious drag events being triggered
    * Note that if an object is dropped onto it's appropriate destination [`GridPane`](https://openjfx.io/javadoc/11/javafx.graphics/javafx/scene/layout/GridPane.html), the event handler for the background will not be run because the event will have been consumed (**note the section below** [**When I drop a card onto the game map, will the event handler for dropping the card onto the anchorPaneRoot be triggered?**](#11-when-i-drop-a-card-onto-the-game-map-will-the-event-handler-for-dropping-the-card-onto-the-anchorpaneroot-be-triggered))
* The appropriate target pane of the item (such as the entire equipped inventory panel, rather than a cell in it) to be dropped detects the item has been dropped within its boundaries: *targetGridPane.addEventHandler(DragEvent.DRAG_DROPPED, gridPaneSetOnDragDropped.get(draggableType));*
    * When handling this event, the appropriate cell of the target pane is determined and the image on the clipboard is placed in this cell, the original item image is removed from the source pane (such as the unequipped inventory pane), and the information about the dragged items is reset to null
    * **When ensuring that cards can only be placed on particular slots, you will want to modify this so that it only processes the drop and spawns the building if the card is allowed to be dropped in the slot.**

The method `removeDraggableDragEventHandlers` removes all drag [event handlers](https://openjfx.io/javadoc/11/javafx.base/javafx/event/EventHandler.html) (except for detecting the item is being dragged). This helps avoid spurious drag events being triggered after the item is dropped.

A good article to read about this in more detail is at: https://docs.oracle.com/javafx/2/drag_drop/jfxpub-drag_drop.htm

## 2. How does Path Loading work?

The method `loadPathTiles` in the class [`LoopManiaWorldLoader`](src/unsw/loopmania/LoopManiaWorldLoader.java) produces a list of (x, y) [`GridPane`](https://openjfx.io/javadoc/11/javafx.graphics/javafx/scene/layout/GridPane.html) cell coordinate pairs of the path tiles, and during this process loads the correct path tile (with correct direction) using the method `onLoad` in the class [`LoopManiaWorldControllerLoader`](src/unsw/loopmania/LoopManiaWorldControllerLoader.java). The method translates the directions stored in the input json file into coordinates, by iterating through the directions, starting with the coordinates of the start position and adding the x and y offsets for each direction to get each subsequent coordinate pair.

*LoopManiaWorldControllerLoader.onLoad* loads a JavaFX ImageView which is "zoomed in" to the correct path tile within the path tiles image [src/images/32x32GrassAndDirtPath.png](src/images/32x32GrassAndDirtPath.png) (containing all 8 path tiles).

Precise details on path-tile coordinates are in [this file](TILES_README.md).

## 3. How does the loading of the character/enemies/cards/items/path/buildings work?

In the starter code, we follow an architecture known as [MVC (Model-View-Controller)](https://www.codecademy.com/articles/mvc) as much as possible, to help decouple the frontend and backend.

This means that the backend classes such as the [`LoopManiaWorld` class](src/unsw/loopmania/LoopManiaWorld.java) (the model), the [frontend FXML files](src/unsw/loopmania/LoopManiaView.fxml) and JavaFX images (the view), and the controller (the [`LoopManiaWorldController` class](src/unsw/loopmania/LoopManiaWorldController.java)) should be split up as much as possible, and the controller should handle all interactions between the model and view.

Within this, we use the *Observer Pattern*, so that the images of entities follow their backend entities in the `LoopManiaWorld`. We do this by attaching [*change listeners*](https://openjfx.io/javadoc/11/javafx.base/javafx/beans/value/ChangeListener.html) to the [`SimpleIntegerProperties`](https://openjfx.io/javadoc/11/javafx.base/javafx/beans/property/SimpleIntegerProperty.html) representing coordinates held by the backend entities, so that if the backend coordinates change, the frontend coordinates change. These are attached in the method `trackPositionOfNonSpawningEntities` in the [`LoopManiaWorldControllerLoader` class](src/unsw/loopmania/LoopManiaWorldControllerLoader.java) for non-spawning entities (such as player and path tiles), and in the method *LoopManiaWorldController.trackPosition* for entities which spawn or must be able to be removed (such as enemies, cards, and inventory items).

More precisely, this adheres to the Observer Pattern since the frontend image is the observer *observing* the subject (the backend entities coordinates). Here, the backend entity has no knowledge of the frontend image, so the code is nicely decoupled.

For entities loaded from the file, the correct image is loaded into the GUI in `LoopManiaWorldControllerLoader`, and for entities which spawn, they are loaded in `LoopManiaController`. For both, the class which loaded them in is also responsible for attaching the change listeners and adding them to the `LoopManiaWorld` object.

## 4. How do the character and enemies move around the game path?

As described for the above question, the images representing all entities track the coordinates of their backend entity through the addition of change listeners.

For entities moving along the path (enemies and the player), the coordinates are stored within a [`PathPosition` object](src/unsw/loopmania/PathPosition.java) which represents the position in the path.

The `PathPosition` object holds the current position of the moving entity within the path, a reference to the ordered path cell coordinates, and the [`SimpleIntegerProperties`](https://openjfx.io/javadoc/11/javafx.base/javafx/beans/property/SimpleIntegerProperty.html) representing the current x and y coordinates (for which the [*change listeners*](https://openjfx.io/javadoc/11/javafx.base/javafx/beans/value/ChangeListener.html) are attached).

This is best practice since the path cell coordinates are hidden away from the moving entities, and the moving entities are able to move through the path using the simple methods *PathPosition.moveDownPath* and *PathPosition.moveUpPath*.

## 5. How are cards/enemies/items and their images removed from the game?

Entities which can be removed have [listener handles](https://github.com/nipafx/LibFX/wiki/ListenerHandle) attached in the `trackPosition` method in the [`LoopManiaWorldController` class](src/unsw/loopmania/LoopManiaWorldController.java).

These listener handles first manage setup of the change listeners, so that the frontend images match the coordinates stored in the backend entity.

The listener handles then manage attachment of the teardown code, through the [`onDetach`](https://github.com/nipafx/LibFX/wiki/ListenerHandle#examples) method. This ensures that all possible [`GridPanes`](https://openjfx.io/javadoc/11/javafx.graphics/javafx/scene/layout/GridPane.html) and collections which could store the image remove the image, and ensures that the change listeners are removed.

The *LoopManiaWorldController.trackPosition* method, after attaching the listener handles, then attaches a change listener to the `shouldExist` [BooleanProperty](https://openjfx.io/javadoc/11/javafx.base/javafx/beans/property/BooleanProperty.html) attribute in the [`Entity` class](src/unsw/loopmania/Entity.java). The change listener here, when triggered, will trigger the `onDetach` method of the listener handles, so that the teardown function is triggered.

The *Entity.shouldExist* BooleanProperty is switched to false by the *Entity.destroy()* method. Thus, by running the *.destroy()* method on a backend entity, the frontend image will be removed.

This setup, through use of the Observer Pattern, means that the frontend image representations of backend entities can be removed from within the backend, whilst the backend still has no direct knowledge of the frontend.

## 6. How are buildings spawned when a card is dropped?

Within the [event handler](https://openjfx.io/javadoc/11/javafx.base/javafx/event/EventHandler.html) defined at *gridPaneSetOnDragDropped.put* in the [`LoopManiaWorldController` class](src/unsw/loopmania/LoopManiaWorldController.java), the `convertCardToBuildingByCoordinates` method in the [`LoopManiaWorld` class](src/unsw/loopmania/LoopManiaWorld.java) is executed, which removes the card entity from the world, and adds and returns a corresponding [`VampireCastleBuilding` entity](src/unsw/loopmania/VampireCastleBuilding.java) object.

The tracking of the building entity coordinates by the building image, is then triggered, and is identical to that for enemies/cards/swords, through the *LoopManiaWorldController.trackPosition* method.

You may want to consider a similar process for ensuring items, once equipped, track the correct position in the equipped inventory.

## 7. How does the player receive cards/swords when an enemy is defeated?

Within the repeating timeline code defined within the `startTimer` method in the [`LoopManiaWorldController` class](src/unsw/loopmania/LoopManiaWorldController.java), the execution of the `runBattles` method for the [`LoopManiaWorld` class](src/unsw/loopmania/LoopManiaWorld.java) will return the list of defeated enemies (which have also been removed from the world).

The method *LoopManiaWorldController.reactToEnemyDefeat* is then run for every defeated enemy, running `loadVampireCard` and `loadSword` to spawn cards and swords respectively.

Within `loadVampireCard`, the method *LoopManiaWorld.loadVampireCard* inserts a new card onto the end of the list of cards, removing and destroying the oldest card and shifting all existing cards 1 cell to the left if there is no space. The method *LoopManiaWorld.loadVampireCard* returns the newly inserted card so that the image representing the card can track the backend card entity using the method *LoopManiaWorldController.trackPosition*.

Within `loadSword`, the method *LoopManiaWorld.addUnequippedSword* inserts a new sword into the top-left empty slot (firstly looking at the top rows, then the leftmost column). If there is no space, the oldest sword (the first item in the list LoopManiaWorld.unequippedInventoryItems) is removed from the game and destroyed, and the new sword takes its slot in the unequippedInventory. *LoopManiaWorld.addUnequippedSword* finishes by returning the new sword so that the image representing the sword can track the backend sword entity using the method *LoopManiaWorldController.trackPosition*.

## 8. How does transferring between menus work?

Most of the GUI code for the menus is setup in the relevant FXML files, to decouple the frontend view from the controller and backend. This is industry best-practice, as per [MVC (Model-View-Controller)](https://www.codecademy.com/articles/mvc) architecture.

The [FXML `Button`](https://openjfx.io/javadoc/11/javafx.controls/javafx/scene/control/Button.html) tags specify Controller methods to run when they are clicked under the `onAction` attribute.

The `switchToMainMenu` method in the [`LoopManiaWorldController` class](src/unsw/loopmania/LoopManiaWorldController.java) triggers pausing the game and switching to the main menu. The `switchToGame` method in the [`MainMenuController` class](src/unsw/loopmania/MainMenuController.java) triggers switching to the game and resuming it.

We use the abstraction of a [`MenuSwitcher` object](src/unsw/loopmania/MenuSwitcher.java) for both of these transitions so that the `MainMenuController` and `LoopManiaWorldController` classes have no knowledge of each other (they are decoupled). The code run during the transition is set in the `start` method in the [`LoopManiaApplication` class](src/unsw/loopmania/LoopManiaApplication.java) through adding a lambda function performing this functionality, using the methods *LoopManiaWorldController.setMainMenuSwitcher* and *MainMenuController.setGameSwitcher*.

Note that Java 11 allows storing lambda functions (anonymous functions) within references to interfaces which have a single method, as is the case for `MenuSwitcher`.

Note that even when switching between the main menu and the game, both still exist, so it is important that the game is paused/resumed appropriately. This has the advantage of the game retaining its current state when we exit to the main menu.

## 9. How does SPACEBAR trigger game pausing/resuming of the game?

Within the file [*LoopManiaView.fxml*](src/unsw/loopmania/LoopManiaView.fxml), the `onKeyPressed` attribute of the AnchorPane `anchorPaneRoot` is set to the `handleKeyPress` method of the [`LoopManiaWorldController` class](src/unsw/loopmania/LoopManiaWorldController.java).

This is then tracked to the method `handleKeyPress` which is run in the [`LoopManiaWorldController` class](src/unsw/loopmania/LoopManiaWorldController.java) through the *@FXML* tag. This method ensures that when the <kbd>Spacebar</kbd> key is pressed, the *LoopManiaWorldController.startTimer* or *LoopManiaWorldController.pause* method is called to resume or pause the game respectively.

The method to pause triggers the timeline to stop, pausing the game. The method to resume creates a new timeline and starts it, resuming the game.

## 10. Why do we not need to include locks to avoid concurrency issues?

**Short answer = everything in the starter code runs on a single thread, so there are no concurrency issues**

Concurrency issues are logical errors which arise when multiple processes are updating and/or reading shared data at the same time. They are often very hard to debug, and solving these is outside of the scope of this course!

Before answering the question fully, we recommend that most students:
* Always use a JavaFX timeline when running multiple processes at once, to avoid concurrency issues with threads
* Do not implement long-running processes (there shouldn't be any need to implement any for the basic functionality in the specification)

If you follow the above, you will not need to worry about concurrency issues, and can ignore the following detailed answer!

### Detailed answer

We do not need to include [*locks*](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/locks/Lock.html), because all [*event handlers*](https://openjfx.io/javadoc/11/javafx.base/javafx/event/EventHandler.html) and the [*timeline*](https://openjfx.io/javadoc/11/javafx.graphics/javafx/animation/Timeline.html) in JavaFX run on the single JavaFX application thread, as per:
* https://examples.javacodegeeks.com/desktop-java/javafx/javafx-concurrency-example/
* https://openjfx.io/javadoc/11/javafx.graphics/javafx/application/Application.html under heading "Threading"

JavaFX is able to do this by adding event handlers and timeline code to a queue, where the items in the queue are run sequentially. So we can guarantee, that event handlers are not running at the same time as other event handlers, and event handlers are not running at the same time as the main game loop in the timeline (although we cannot predict the order in which these will be run).

This means that the starter code does not need locks (mutexes) for resources shared between the timeline [`KeyFrame`](https://openjfx.io/javadoc/11/javafx.graphics/javafx/animation/KeyFrame.html) in the [`LoopManiaWorldController` class](src/unsw/loopmania/LoopManiaWorldController.java), and all of the  event handlers (including between different event handlers).

This will make the game easier for you to implement. However, if you add time-consuming processes to this, the game may lag/become choppy/hang, because a long-running process will prevent event handlers, the timeline main game logic, and the updating of the screen from running.

If you need to implement time-consuming processes (e.g. for an extension), we recommend using [*Task*](https://openjfx.io/javadoc/11/javafx.graphics/javafx/concurrent/Task.html) by itself or within a [*Service*](https://openjfx.io/javadoc/11/javafx.graphics/javafx/concurrent/Service.html).

Tasks ensure that any changes to public properties, change notifications for errors or cancellation, event handlers, and states occur on the JavaFX Application thread, so is a better alternative to using a basic [*Java Thread*](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Thread.html), as explained in [*this article*](https://docs.oracle.com/javafx/2/threads/jfxpub-threads.htm).

The Service class is used for executing/reusing tasks. You can run tasks without Service, however, if you don't need to reuse it.

If you implement time-consuming processes in a Task or thread, you may need to implement locks on resources shared with the application thread (i.e. Timeline KeyFrame and drag Event handlers).

To help with this, we added the method `printThreadingNotes` to the [`LoopManiaWorldController` class](src/unsw/loopmania/LoopManiaWorldController.java), so you can run it to ensure your code is running in the main JavaFX application thread (you just have to run it with a String label of your choice, to help read the terminal output).

If you need to delay some code but it is not long-running, consider using [*Platform.runLater*](https://openjfx.io/javadoc/11/javafx.graphics/javafx/application/Platform.html#runLater(java.lang.Runnable)) . This is run on the JavaFX application thread when it has enough time.

## 11. When I drop a card onto the game map, will the event handler for dropping the card onto the anchorPaneRoot be triggered?

**Answer = No**

The following explanation is a simplified version of this reference:  https://docs.oracle.com/javase/8/javafx/events-tutorial/processing.htm

When JavaFX processes a drag event (using a mouse), it creates a path from the root (top-level) [*node*](https://openjfx.io/javadoc/11/javafx.graphics/javafx/scene/Node.html) of your JavaFX application, to the lowest-level node which has been clicked. For the example of dropping a card onto the game, based on the file [*LoopManiaView.fxml*](src/unsw/loopmania/LoopManiaView.fxml), this path (leftmost is top-level, rightmost is bottom-level) is:

**Stage -> Scene -> AnchorPane (fx:id = "anchorPaneRoot") -> HBox -> VBox -> GridPane (fx:id = "squares")**

JavaFX will then initiate an "Event Capturing Phase" going from the [`Stage`](https://openjfx.io/javadoc/11/javafx.graphics/javafx/stage/Stage.html) to the [`GridPane`](https://openjfx.io/javadoc/11/javafx.graphics/javafx/scene/layout/GridPane.html), triggering "filters". However, we haven't implemented any filters in the starter code, so this is not relevant.

JavaFX will then initiate an "Event Bubbling Phase" going from the GridPane to the Stage, triggering "handlers". Our [event handlers](https://openjfx.io/javadoc/11/javafx.base/javafx/event/EventHandler.html) are handlers. So in the starter code, the lowest-level node which is involved in a drag event (here, the "squares" GridPane) will process its event handler before the higher-level nodes (such as the [`AnchorPane`](https://openjfx.io/javadoc/11/javafx.graphics/javafx/scene/layout/AnchorPane.html) *anchorPaneRoot*).

The above cycle of performing an event capturing phase, and an event bubbling phase, is repeated infinitely until the event is *consumed*.

Within our code for handling drag events, we always call the [`event.consume()`](https://openjfx.io/javadoc/11/javafx.base/javafx/event/Event.html#consume()) method. This consumes the event, and ensures that no further nodes will be able to run their event handlers.

Thus, the GridPane will run its event handler before the background AnchorPane if it is involved, and the background AnchorPane will not run its event handler as the GridPane event handler will consume the event.
