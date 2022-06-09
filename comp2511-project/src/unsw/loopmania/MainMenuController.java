package unsw.loopmania;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * controller for the main menu. TODO = you could extend this, for example with
 * a settings menu, or a menu to load particular maps.
 */
public class MainMenuController {
    /**
     * facilitates switching to main game
     */
    private MenuSwitcher gameSwitcher;
    private MenuSwitcher gameSurviveSwitcher;
    private MenuSwitcher gameBerserkerSwitcher;
    private MenuSwitcher gameConfusingSwitcher;

    // private String mode;

    @FXML
    private Button suviveModeButton;

    @FXML
    private Button berserkerModeButton;

    @FXML
    private Button confusingModeButton;

    @FXML
    private Button startGameButton;

    public void setGameSwitcher(MenuSwitcher gameSwitcher) {
        this.gameSwitcher = gameSwitcher;
    }

    public void setSurviveGameSwitcher(MenuSwitcher gameSwitcher) {
        this.gameSurviveSwitcher = gameSwitcher;
    }

    public void setBerserkerGameSwitcher(MenuSwitcher gameSwitcher) {
        this.gameBerserkerSwitcher = gameSwitcher;
    }

    public void setConfusingGameSwitcher(MenuSwitcher gameSwitcher) {
        this.gameConfusingSwitcher = gameSwitcher;
    }

    /**
     * facilitates switching to main game upon button click
     * 
     * @throws IOException
     */
    @FXML
    private void switchToGame() throws IOException {
        // mode = "standard";
        gameSwitcher.switchMenu();
    }

    @FXML
    private void switchToSurviveGame() throws IOException {
        // mode = "survive";
        gameSurviveSwitcher.switchMenu();
    }

    @FXML
    private void switchToBerserkerGame() throws IOException {
        // mode = "berserker";
        gameBerserkerSwitcher.switchMenu();
    }

    @FXML
    private void switchToConfusingMode() throws IOException {
        // mode = "confusing";
        gameConfusingSwitcher.switchMenu();
    }

    // public String getMode() {
    // return mode;
    // }
}
