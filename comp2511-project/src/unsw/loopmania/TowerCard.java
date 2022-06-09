package unsw.loopmania;

import javafx.beans.property.SimpleIntegerProperty;

/**
 * represents a card in the backend game world
 */
public class TowerCard extends Card {
    public TowerCard(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y);
    }    
}