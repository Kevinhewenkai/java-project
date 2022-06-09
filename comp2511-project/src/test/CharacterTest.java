package test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.*;
import unsw.loopmania.Character;





public class CharacterTest {

    private LoopManiaWorld d;
    private List<Pair<Integer, Integer>> path;
    private PathPosition pp;

    @BeforeEach
    public void setUp() {
        path = new ArrayList<Pair<Integer, Integer>>();
        path.add(new Pair<Integer, Integer>(1, 1));
        path.add(new Pair<Integer, Integer>(1, 2));
        path.add(new Pair<Integer, Integer>(1, 3));
        path.add(new Pair<Integer, Integer>(1, 4));
        path.add(new Pair<Integer, Integer>(1, 5));
        path.add(new Pair<Integer, Integer>(1, 6));
        path.add(new Pair<Integer, Integer>(1, 7));

        path.add(new Pair<Integer, Integer>(1, 8));
        path.add(new Pair<Integer, Integer>(2, 8));
        path.add(new Pair<Integer, Integer>(3, 8));
        path.add(new Pair<Integer, Integer>(4, 8));
        path.add(new Pair<Integer, Integer>(5, 8));
        path.add(new Pair<Integer, Integer>(6, 8));
        path.add(new Pair<Integer, Integer>(7, 8));

        path.add(new Pair<Integer, Integer>(8, 8));
        path.add(new Pair<Integer, Integer>(8, 7));
        path.add(new Pair<Integer, Integer>(8, 6));
        path.add(new Pair<Integer, Integer>(8, 5));
        path.add(new Pair<Integer, Integer>(8, 4));
        path.add(new Pair<Integer, Integer>(8, 3));
        path.add(new Pair<Integer, Integer>(8, 2));

        path.add(new Pair<Integer, Integer>(8, 1));
        path.add(new Pair<Integer, Integer>(7, 1));
        path.add(new Pair<Integer, Integer>(6, 1));
        path.add(new Pair<Integer, Integer>(5, 1));
        path.add(new Pair<Integer, Integer>(4, 1));
        path.add(new Pair<Integer, Integer>(3, 1));
        path.add(new Pair<Integer, Integer>(2, 1));
        d = new LoopManiaWorld(10, 10, path);
        
        pp = new PathPosition(0, path);
    }


    @AfterEach
    public void clear() {
        d = null;
        path = null;
    }

    // Test picking up a sword and storing in unequipped inventory
    @Test
    public void testUnequippedWeapon() {
        Character character = new Character(pp, 100, 100, 100, 100);
        Sword sword = new Sword(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
        character.pickupItem(sword);
        assertTrue(character.getUnequipped().getItems().size() == 1);
        assertTrue(character.getUnequipped().getItemByIndex(0).getType().equals("Weapon"));
    }

    // Test picking up Armour and a RareItem in unequipped inventory
    @Test
    public void testTwoUnequippedItems() {
        Character character = new Character(pp, 100, 100, 100, 100);
        Armour armour = new Armour(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
        OneRing oneRing = new OneRing(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
        character.pickupItem(armour);
        character.pickupItem(oneRing);
        assert(character.getUnequipped().getItems().size() == 2);
        assertTrue(character.getUnequipped().getItemByIndex(0).getType().equals("Armour"));
        assertTrue(character.getUnequipped().getItemByIndex(1).getType().equals("RareItem"));
    }

    // Test picking up a sword and equipping it
    @Test
    public void testEquippingSword() {
        Character character = new Character(pp, 100, 100, 100, 100);
        Sword sword = new Sword(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
        character.pickupItem(sword);
        character.equipItem(sword);
        assertTrue(character.getEquipped().getItems().size() == 1);
        assertTrue(character.getUnequipped().getItems().size() == 0);
        assertTrue(character.getEquipped().getItemByIndex(0).equals(sword));
    }

    // Test that coordinates of equipped sword are assigned correctly
    @Test
    public void testCoordinatesEquippedSword() {
        Character character = new Character(pp, 100, 100, 100, 100);
        Sword sword = new Sword(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
        character.pickupItem(sword);
        character.equipItem(sword);
        assertTrue(sword.getX() == 0);
        assertTrue(sword.getY() == 0);
        assertTrue(character.getEquipped().getItemByIndex(0).equals(sword));
    }

    // Test that coordinates of equipped armour are assigned correctly
    @Test
    public void testCoordinatesEquippedArmour() {
        Character character = new Character(pp, 100, 100, 100, 100);
        Armour armour = new Armour(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
        character.pickupItem(armour);
        character.equipItem(armour);
        System.out.println(armour.getX());
        System.out.println(armour.getY());
        assertTrue(armour.getX() == 1);
        assertTrue(armour.getY() == 1);
    }

    // Test that adding a second weapon drops the first
    @Test
    public void testEquippingTwoWeapons() {
        Character character = new Character(pp, 100, 100, 100, 100);
        
        // aim is to check that sword coordinates change, stake coordinates do not.
        Sword sword = new Sword(new SimpleIntegerProperty(0), new SimpleIntegerProperty(1));
        Sword sword2 = new Sword(new SimpleIntegerProperty(0), new SimpleIntegerProperty(2));
        Stake stake = new Stake(new SimpleIntegerProperty(2), new SimpleIntegerProperty(0));

        character.pickupItem(sword);
        character.pickupItem(sword2);
        character.pickupItem(stake);

        character.equipItem(sword);
        character.equipItem(sword2);

        assertTrue(character.getEquipped().getItems().size() == 1);
        assertTrue(character.getUnequipped().getItems().size() == 1);

        assertTrue(character.getEquipped().getItemByIndex(0).equals(sword2));
        assertTrue(character.getUnequipped().getItemByIndex(0).equals(stake));

        assertTrue(stake.getX() == 2);
        assertTrue(stake.getY() == 0);
        assertTrue(sword2.getX() == 0);
        assertTrue(sword2.getY() == 0);

    }

    // Test equipping two rare items ==> second should replace first
    @Test
    public void testEquippingTwoRare() {
        Character character = new Character(pp, 100, 100, 100, 100);
        OneRing oneRing = new OneRing(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
        DenimShorts denimShorts = new DenimShorts(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));

        character.pickupItem(oneRing);
        character.pickupItem(denimShorts);

        character.equipItem(oneRing);
        assertTrue(character.getEquipped().getItems().size() == 1);
        assertTrue(character.getUnequipped().getItems().size() == 1);

        character.equipItem(denimShorts);
        assertTrue(character.getEquipped().getItems().size() == 1);
        assertTrue(character.getUnequipped().getItems().size() == 0);

        assertTrue(denimShorts.getX() == 0);
        assertTrue(denimShorts.getY() == 1);
    }

    // Test that equipping a sword increases the character's damage by 15
    @Test
    public void testDamageEquipSword() {
        Character character = new Character(pp, 100, 100, 100, 100);
        Sword sword = new Sword(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
        assertTrue(character.getStats().getDamage() == 100);

        character.pickupItem(sword);
        character.equipItem(sword);
        assertTrue(character.getTotalStats().getDamage() == 115);
    }

    // Test that equipping helmet and armour increases character's scalarDefence by 10, 
    // decreases damage by 2, increases defence by 20.
    @Test 
    public void testStatsEquipHelmetArmour() {
        Character character = new Character(pp, 100, 100, 100, 100);

        Helmet helmet = new Helmet(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
        Armour armour = new Armour(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));

        character.pickupItem(helmet);
        character.pickupItem(armour);

        character.equipItem(helmet);
        character.equipItem(armour);

        System.out.println(character.getEquipped().getItems().size());

        Stats characterStats = character.getTotalStats();
        assertTrue(characterStats.getDamage() == 98);
        assertTrue(characterStats.getDefence() == 120);
        assertTrue(characterStats.getScalarDefence() == 3); 
    }

    @Test
    public void testUnequipItem() {
        Character character = new Character(pp, 100, 100, 100, 100);

        Helmet helmet = new Helmet(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
        Armour armour = new Armour(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));

        character.pickupItem(helmet);
        character.pickupItem(armour);

        character.equipItem(helmet);
        character.equipItem(armour);

        character.unequipItem(helmet);

        List<Item> equipped = character.getEquipped().getItems();
        

        List<Item> list = new ArrayList<Item>();
        list.add(armour);

        System.out.println(equipped);
        System.out.println("    ");
        System.out.println(list);
        

        assertTrue(equipped.equals(list));
    }
    // Test adding allies
    @Test
    public void testAddingAllies() {
        Character character = new Character(pp, 100, 100, 100, 100);
        Ally a = new Ally(pp, character.getStats());
        character.addAlly(a);
        assert(character.getAllies().size() == 1);
        character.removeAlly(a);
        assert(character.getAllies().size() == 0);
    }

    // Inventory tests --> adding and remove
    @Test
    public void genericInventory() {
        Inventory newInventory = new Inventory(5, 3);
        RareItem denimShorts = new DenimShorts(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        newInventory.addItem(denimShorts);
        assert(newInventory.getItems().size() == 1);

        newInventory.removeItem(denimShorts);
        assert(newInventory.getItems().size() == 0);
    }

    // Test adding an extra item to a full inventory ==> removes first (oldest) item in list
    @Test
    public void fullInventory() {
        Inventory newInventory = new Inventory(1, 2);
        Sword sword1 = new Sword(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        Sword sword2 = new Sword(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        newInventory.addItem(sword1);
        newInventory.addItem(sword2);
        assertTrue(newInventory.isFull());
        Sword sword3 = new Sword(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        newInventory.addItem(sword3);
        assert(newInventory.getItems().size() == 2);
    }

    @Test
    public void getFirstAvailableSlot() {
        Inventory newInventory = new Inventory(3, 5);
        Sword sword1 = new Sword(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        Sword sword2 = new Sword(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        newInventory.addItem(sword1);
        newInventory.addItem(sword2);
        assert(newInventory.getFirstAvailableSlot().getKey() == 2);
        assert(newInventory.getFirstAvailableSlot().getValue() == 0);
        assert(newInventory.getItemByCoordinates(1, 0).equals(sword2));
        assert(newInventory.itemExists(sword1));
        assert(newInventory.getType().equals("Inventory"));
        assert(newInventory.getY() == -1);
        assert(newInventory.getY() == -1);
    }

    
}


