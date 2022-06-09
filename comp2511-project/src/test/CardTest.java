package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.*;
//import org.junit.*;

import org.javatuples.Pair;
//import javafx.beans.property.SimpleIntegerProperty;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unsw.loopmania.*;


/**
 * Notes
 *  - Only one building can be placed in a single-slot on the map
 *  - Dropping a building card on another building already on the map returns the card to the inventory, and the map remains unchanged
 *  - The card inventory (should be) refreshed everytime it is updated, ensuring it is sorted by oldest-to-newest cards, 
 *    emptying the bottom/right-most slots for new cards to be stowed
 *  - Cards can't be moved after being placed
 */


public class CardTest {
    
    private LoopManiaWorld world;
    @BeforeEach
    public void setUp() {
        world = new LoopManiaWorld(10, 10, new ArrayList<>());
    }

    @AfterEach
    public void clear() {
        world = null;
    }


    @Test // Picking up a new card when no other card slots are occupied
    public void pickingUpCardEmptyInventory() {
        CardInventory inventory = world.getCardInventory();

        Pair<Integer, Integer> slot = new Pair<>(0, 0);
        assertEquals(inventory.getFirstAvailableSlot(), slot);

        world.loadCard("Tower");

        assertEquals(inventory.getInventory().size(), 1);
        Card newCard = inventory.getInventory().get(0);
        
        String type = newCard.getClass().getSimpleName();
        assertEquals(type, "TowerCard");

        assertEquals(newCard.getX(), 0);
        assertEquals(newCard.getY(), 0);

        Pair<Integer, Integer> slot2 = new Pair<>(1, 0);
        assertEquals(inventory.getFirstAvailableSlot(), slot2);
    }


    @Test // Picking up a new card when some (but not all) card slots are occupied
    public void pickingUpCardPartiallyEmptyInventory() {
        CardInventory inventory = world.getCardInventory();

        Pair<Integer, Integer> slot = new Pair<>(0, 0);
        assertEquals(inventory.getFirstAvailableSlot(), slot);

        world.loadCard("Tower");
        slot = new Pair<>(1, 0);
        assertEquals(inventory.getFirstAvailableSlot(), slot);
        world.loadCard("Trap");
        world.loadCard("Barracks");

        assertEquals(inventory.getInventory().size(), 3);

        Pair<Integer, Integer> slot2 = new Pair<>(3, 0);
        assertEquals(inventory.getFirstAvailableSlot(), slot2);

        world.loadCard("Barracks");
        
        assertEquals(inventory.getInventory().size(), 4);

        Card newCard = inventory.getInventory().get(3);
        String type = newCard.getClass().getSimpleName();
        assertEquals(type, "BarracksCard");
        assertEquals(newCard.getX(), 3);
        assertEquals(newCard.getY(), 0);
    }


    @Test // Picking up a new card when all slots are occupied
    public void pickingUpCardFullInventory() {
        CardInventory inventory = world.getCardInventory();

        world.loadCard("Tower");
        world.loadCard("Trap");
        world.loadCard("Barracks");
        world.loadCard("VampireCastle");
        world.loadCard("ZombiePit");
        world.loadCard("Campfire");
        world.loadCard("Village");
        world.loadCard("Barracks");
        world.loadCard("Trap");
        world.loadCard("Barracks");
        world.loadCard("VampireCastle");
        world.loadCard("ZombiePit");
        world.loadCard("Campfire");
        world.loadCard("Village");
        world.loadCard("Barracks"); // 15 cards -> full inventory

        Card firstCard = world.getCardInventory().getCardByCoordinates(0, 0);

        assertEquals(inventory.getInventory().size(), 15);

        Pair<Integer, Integer> slot = null;
        assertEquals(inventory.getFirstAvailableSlot(), slot);

        world.loadCard("Village");

        assertEquals(inventory.getInventory().size(), 15);
        Pair<Integer, Integer> slot2 = null;
        assertEquals(inventory.getFirstAvailableSlot(), slot2);

        List<Card> cardsList = inventory.getInventory();

        Card newCard = cardsList.get(0);
        String type = newCard.getClass().getSimpleName();
        assertEquals(type, "TrapCard");
        assertEquals(newCard.getX(), 1);
        assertEquals(newCard.getY(), 0);

        Card newCard2 = cardsList.get(14);
        String type2 = newCard2.getClass().getSimpleName();
        assertEquals(type2, "VillageCard");
        assertEquals(newCard2.getX(), 0);
        assertEquals(newCard2.getY(), 0);

        assertEquals(cardsList.contains(firstCard), false);
    }

/*
    @Test
    /
     * Placing a card on the map (assuming it is an empty and valid slot),
     * removing the card from the inventory, 
     * updating fields, and "refreshing" (rearranging) the card inventory
     
     public void placingCardValid() {
        CardInventory inventory = d.getCardInventory();

        Card firstCard = new Card("Tower");
        inventory.addCard(firstCard);
        Card secondCard = new Card("Trap");
        inventory.addCard(secondCard);
        Card thirdCard = new Card("Barracks");
        inventory.addCard(thirdCard);

        firstCard.convertCardToBuildingByCoordinates(5,5); // random coordinates for now, assumed to be an empty and valid slot
        
        assertEquals(firstCard instanceof TowerBuilding, true);

        assertEquals(firstCard.x, 5);
        assertEquals(firstCard.y, 5);

        assertEquals(inventory.cards[0], secondCard);
        assertEquals(inventory.cards[1], thirdCard);
        
        Pair<Integer, Integer> slot = new Pair<>(0, 2);
        assertEquals(inventory.getFirstAvailableSlot(), slot);
        assertEquals(inventory.cards.size(), 2);
    }


    @Test
    /
     * Placing a building card on a slot in the game-world where another building already exists,
     * thus returniing the card to the inventory, leaving the map unchanged
     
     public void placingCardOnAnotherBuilding() {
        CardInventory inventory = d.getCardInventory();

        Card firstCard = new Tower(null, false);
        inventory.addCard(firstCard);
        Card secondCard = new Trap(null, false);
        inventory.addCard(secondCard);
        Card thirdCard = new Barracks(null, false);
        inventory.addCard(thirdCard, null, false);

        
        firstCard.convertCardToBuildingByCoordinates(5, 5); // random coordinates for now, assumed to be an empty and valid slot
        
        assertEquals(firstCard instanceof TowerBuilding, true);

        assertEquals(firstCard.x, 5);
        assertEquals(firstCard.y, 5);

        secondCard.convertCardToBuildingByCoordinates(5, 5);

        assertEquals(secondCard instanceof Card, true);

        secondCard.convertCardToBuildingByCoordinates(5, 5);

        assertEquals(inventory.cards[0], secondCard);

        Pair<Integer, Integer> slot = new Pair<>(0, 2);
        assertEquals(inventory.getFirstAvailableSlot(), slot);
        assertEquals(inventory.cards.size(), 2);
    }
    
    */

}