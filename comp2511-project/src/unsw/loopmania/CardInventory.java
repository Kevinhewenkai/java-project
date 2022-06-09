package unsw.loopmania;

import java.util.*;
import org.javatuples.Pair;

import javafx.beans.property.SimpleIntegerProperty;

/**
 * The inventory of cards currently held by the character
 */

public class CardInventory {
    
    /* ATTRIBUTES */
    private List<Card> inventory;

    private int cardInventoryWidth;
    private int cardInventoryHeight;

    
    /* CONSTRUCTOR */
    public CardInventory(int cardInventoryWidth, int cardInventoryHeight) {
        this.cardInventoryHeight = cardInventoryHeight;
        this.cardInventoryWidth = cardInventoryWidth;
        inventory = new ArrayList<>();
    }


    public List<Card> getInventory() {
        return inventory;
    }


    public Card loadCard(String type) {
        Pair<Integer, Integer> firstAvailableSlot = getFirstAvailableSlot();
        if (firstAvailableSlot == null) {
            // All slots are taken, so destroy the oldest card
            removeCardByIndex(0);
            firstAvailableSlot = getFirstAvailableSlot();
        }   
        // Now at least one slot is available
        
        // Loading the card
        Card newCard = null;
        switch (type) {
            case "Campfire":
                newCard = new CampfireCard(new SimpleIntegerProperty(firstAvailableSlot.getValue0()), new SimpleIntegerProperty(firstAvailableSlot.getValue1()));
                break;
            
            case "Trap":
                newCard = new TrapCard(new SimpleIntegerProperty(firstAvailableSlot.getValue0()), new SimpleIntegerProperty(firstAvailableSlot.getValue1())); 
                break;

            case "Village":
                newCard = new VillageCard(new SimpleIntegerProperty(firstAvailableSlot.getValue0()), new SimpleIntegerProperty(firstAvailableSlot.getValue1())); 
                break;

            case "Tower":
                newCard = new TowerCard(new SimpleIntegerProperty(firstAvailableSlot.getValue0()), new SimpleIntegerProperty(firstAvailableSlot.getValue1())); 
                break;

            case "Barracks":
                newCard = new BarracksCard(new SimpleIntegerProperty(firstAvailableSlot.getValue0()), new SimpleIntegerProperty(firstAvailableSlot.getValue1())); 
                break;

            case "VampireCastle":
                newCard = new VampireCastleCard(new SimpleIntegerProperty(firstAvailableSlot.getValue0()), new SimpleIntegerProperty(firstAvailableSlot.getValue1()));
                break;

            case "ZombiePit":
                newCard = new ZombiePitCard(new SimpleIntegerProperty(firstAvailableSlot.getValue0()), new SimpleIntegerProperty(firstAvailableSlot.getValue1())); 
                break;
            case "Destroyer":
                newCard = new DestroyerCard(new SimpleIntegerProperty(firstAvailableSlot.getValue0()), new SimpleIntegerProperty(firstAvailableSlot.getValue1()));
                break;
        }
        
        if (newCard != null) {
            inventory.add(newCard);
        }

        return newCard;
    }


    public Pair<Integer, Integer> getFirstAvailableSlot() {
        // Finding index of first available slot
        for (int y = 0; y < cardInventoryHeight; y++){
            for (int x = 0; x < cardInventoryWidth; x++){
                if (getCardByCoordinates(x, y) == null) {
                    return new Pair<Integer, Integer>(x, y);
                }
            }
        }
        return null;
    }


    public Card getCardByCoordinates(int x, int y   ) {
        // Checking if a card is currently stowed at the slot with the given coordinates
        for (Card c: inventory) {
            if ((c.getX() == x) && (c.getY() == y)) {
                return c;
            }
        }
        return null;
    }


    public void removeCardByIndex(int index) {
        Card card = inventory.get(index);
        card.destroy();
        inventory.remove(index);
    }


    public void removeCardByObject(Card card) {
        inventory.remove(card);
        card.destroy();
        return;

    }

}