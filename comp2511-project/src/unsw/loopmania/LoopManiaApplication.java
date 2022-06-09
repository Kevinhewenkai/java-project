package unsw.loopmania;

import java.io.IOException;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

/**
 * the main application run main method from this class
 */
public class LoopManiaApplication extends Application {
    /**
     * the controller for the game. Stored as a field so can terminate it when click
     * exit button
     */
    private LoopManiaWorldController mainController;
    private LoopManiaWorldController mainSurviveController;
    HeroCastleController heroCastleController;

    MediaPlayer mediaPlayer;

    public void music() {
        String path = "src/music/bgm.m4a";
        Media media = new Media(Paths.get(path).toUri().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        music();
        // set title on top of window bar
        primaryStage.setTitle("Loop Mania");
        System.setProperty("javafx.userAgentStylesheetUrl", "CASPIAN");

        // prevent human player resizing game window (since otherwise would see white
        // space)
        // alternatively, you could allow rescaling of the game (you'd have to program
        // resizing of the JavaFX nodes)
        primaryStage.setResizable(false);

        // load the main game
        LoopManiaWorldControllerLoader loopManiaLoader = new LoopManiaWorldControllerLoader(
                "world_with_twists_and_turns.json");
        mainController = loopManiaLoader.loadController();
        FXMLLoader gameLoader = new FXMLLoader(getClass().getResource("LoopManiaView.fxml"));
        gameLoader.setController(mainController);
        Parent gameRoot = gameLoader.load();

        // load the survive mode main game
        LoopManiaWorldControllerLoader loopManiaSurviveLoader = new LoopManiaWorldControllerLoader(
                "world_with_twists_and_turns.json");
        mainSurviveController = loopManiaSurviveLoader.loadController();
        FXMLLoader surviveGameLoader = new FXMLLoader(getClass().getResource("LoopManiaView.fxml"));
        surviveGameLoader.setController(mainSurviveController);
        Parent gameSurviveRoot = surviveGameLoader.load();

        // load the Berserker mode main game
        LoopManiaWorldControllerLoader loopManiaBerserkerLoader = new LoopManiaWorldControllerLoader(
                "world_with_twists_and_turns.json");
        LoopManiaWorldController mainBerserkerController = loopManiaBerserkerLoader.loadController();
        FXMLLoader berserkerGameLoader = new FXMLLoader(getClass().getResource("LoopManiaView.fxml"));
        berserkerGameLoader.setController(mainBerserkerController);
        Parent gameBerserkerRoot = berserkerGameLoader.load();

        // load the Confusing mode main game
        LoopManiaWorldControllerLoader loopManiaConfusingLoader = new LoopManiaWorldControllerLoader(
                "world_with_twists_and_turns.json");
        LoopManiaWorldController mainConfusingController = loopManiaConfusingLoader.loadController();
        FXMLLoader ConfusingGameLoader = new FXMLLoader(getClass().getResource("LoopManiaView.fxml"));
        ConfusingGameLoader.setController(mainConfusingController);
        Parent gameConfusingRoot = ConfusingGameLoader.load();

        // load the main menu
        MainMenuController mainMenuController = new MainMenuController();
        FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("MainMenuView.fxml"));
        menuLoader.setController(mainMenuController);
        Parent mainMenuRoot = menuLoader.load();

        // load main game's heroCastle
        heroCastleController = new HeroCastleController(this.mainController.getCharacter());
        FXMLLoader heroCastleLoader = new FXMLLoader(getClass().getResource("HeroCastleView.fxml"));
        heroCastleLoader.setController(heroCastleController);
        Parent heroCastleRoot = heroCastleLoader.load();

        // load survive mode main game's heroCastle
        HeroCastleController heroCastleSurviveController = new HeroCastleController(
                mainSurviveController.getCharacter());
        FXMLLoader heroCastleSurviveLoader = new FXMLLoader(getClass().getResource("HeroCastleView.fxml"));
        heroCastleSurviveLoader.setController(heroCastleSurviveController);
        Parent heroCastleSurviveRoot = heroCastleSurviveLoader.load();

        // load Berserker mode main game's heroCastle
        HeroCastleController heroCastleBerserkerController = new HeroCastleController(
                mainBerserkerController.getCharacter());
        FXMLLoader heroCastleBerserkerLoader = new FXMLLoader(getClass().getResource("HeroCastleView.fxml"));
        heroCastleBerserkerLoader.setController(heroCastleBerserkerController);
        Parent heroCastleBerserkerRoot = heroCastleBerserkerLoader.load();

        // load Confusing mode main game's heroCastle
        HeroCastleController heroCastleConfusingController = new HeroCastleController(
                mainConfusingController.getCharacter());
        FXMLLoader heroCastleConfusingLoader = new FXMLLoader(getClass().getResource("HeroCastleView.fxml"));
        heroCastleConfusingLoader.setController(heroCastleConfusingController);
        Parent heroCastleConfusingRoot = heroCastleConfusingLoader.load();

        // create new scene with the main menu (so we start with the main menu)
        Scene scene = new Scene(mainMenuRoot);
        Scene heroCastleScene = new Scene(heroCastleRoot);
        Scene heroCastleSurviveScene = new Scene(heroCastleSurviveRoot);
        Scene heroCastleBerserkerScene = new Scene(heroCastleBerserkerRoot);
        Scene heroCastleConfusingScene = new Scene(heroCastleConfusingRoot);
        // set functions which are activated when button click to switch menu is pressed
        // e.g. from main menu to start the game, or from the game to return to main
        // menu

        // set the switcher from different game mode LoopManiaController to
        // HerocastleController
        mainController.setHeroCastleSwitcher(() -> {
            heroCastleController.setGameMode("standard");
            switchToRoot(heroCastleScene, heroCastleRoot, primaryStage);
            heroCastleController.updateGold();
            heroCastleController.updateInventory();
        });

        mainSurviveController.setHeroCastleSwitcher(() -> {
            heroCastleSurviveController.setGameMode("survive");
            switchToRoot(heroCastleSurviveScene, heroCastleSurviveRoot, primaryStage);
            heroCastleSurviveController.updateGold();
            heroCastleSurviveController.updateInventory();
            heroCastleSurviveController.activeBuyingPotion();
        });

        mainBerserkerController.setHeroCastleSwitcher(() -> {
            heroCastleBerserkerController.setGameMode("berserker");
            switchToRoot(heroCastleBerserkerScene, heroCastleBerserkerRoot, primaryStage);
            heroCastleBerserkerController.updateGold();
            heroCastleBerserkerController.updateInventory();
            heroCastleBerserkerController.activeDefenceItem();
        });

        mainConfusingController.setHeroCastleSwitcher(() -> {
            heroCastleConfusingController.setGameMode("confusing");
            switchToRoot(heroCastleConfusingScene, heroCastleConfusingRoot, primaryStage);
            heroCastleConfusingController.updateGold();
            heroCastleConfusingController.updateInventory();
        });

        // load switcher from heroCastleController to main game
        heroCastleController.setGameSwitcher(() -> {
            switchToRoot(scene, gameRoot, primaryStage);
            mainController.startTimer();
            try {
                mainController.updateInventoryFromHeroCastle();
            } catch (Exception e) {
                // Auto-generated catch block
                e.printStackTrace();
            }
        });

        heroCastleSurviveController.setGameSwitcher(() -> {
            switchToRoot(scene, gameSurviveRoot, primaryStage);
            mainSurviveController.startTimer();
            try {
                mainSurviveController.updateInventoryFromHeroCastle();
            } catch (Exception e) {
                // Auto-generated catch block
                e.printStackTrace();
            }
        });

        heroCastleBerserkerController.setGameSwitcher(() -> {
            switchToRoot(scene, gameBerserkerRoot, primaryStage);
            mainBerserkerController.startTimer();
            try {
                mainBerserkerController.updateInventoryFromHeroCastle();
            } catch (Exception e) {
                // Auto-generated catch block
                e.printStackTrace();
            }
        });

        heroCastleConfusingController.setGameSwitcher(() -> {
            switchToRoot(scene, gameConfusingRoot, primaryStage);
            mainConfusingController.startTimer();
            try {
                mainConfusingController.updateInventoryFromHeroCastle();
            } catch (Exception e) {
                // Auto-generated catch block
                e.printStackTrace();
            }
        });

        // load switcher from main game to main menu
        mainController.setMainMenuSwitcher(() -> {
            switchToRoot(scene, mainMenuRoot, primaryStage);
        });

        mainSurviveController.setMainMenuSwitcher(() -> {
            switchToRoot(scene, mainMenuRoot, primaryStage);
        });

        mainBerserkerController.setMainMenuSwitcher(() -> {
            switchToRoot(scene, mainMenuRoot, primaryStage);
        });

        mainConfusingController.setMainMenuSwitcher(() -> {
            switchToRoot(scene, mainMenuRoot, primaryStage);
        });

        // load Switcher from main Menu to main game
        mainMenuController.setGameSwitcher(() -> {
            switchToRoot(scene, gameRoot, primaryStage);
            // mainController.setMode("Standard")
            mainController.startTimer();
        });
        // add more game mode switcher
        mainMenuController.setSurviveGameSwitcher(() -> {
            switchToRoot(scene, gameSurviveRoot, primaryStage);
            mainSurviveController.startTimer();
        });

        mainMenuController.setBerserkerGameSwitcher(() -> {
            switchToRoot(scene, gameBerserkerRoot, primaryStage);
            mainBerserkerController.startTimer();
        });

        mainMenuController.setConfusingGameSwitcher(() -> {
            switchToRoot(scene, gameConfusingRoot, primaryStage);
            mainConfusingController.startTimer();
        });

        // deploy the main onto the stage
        gameRoot.requestFocus();
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        // wrap up activities when exit program
        mainController.terminate();
    }

    /**
     * switch to a different Root
     */
    private void switchToRoot(Scene scene, Parent root, Stage stage) {
        scene.setRoot(root);
        root.requestFocus();
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
