package test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import unsw.loopmania.LoopManiaWorld;
import unsw.loopmania.PathPosition;
import unsw.loopmania.EnemySlug;
import unsw.loopmania.EnemyVampire;
import unsw.loopmania.EnemyZombie;
import unsw.loopmania.Card;
import unsw.loopmania.Character;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EnemyTest {

    // Set up loop mania world and then certify conditions.

    // Check randomised conditions of slug, zombie and vampire movement.
    
    private LoopManiaWorld d;
    private ArrayList<Pair<Integer, Integer>> path;

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
    }

    @AfterEach
    public void clear() {
        d = null;
        path = null;
    }

    // Test randomised movement of slug.
    @ParameterizedTest
    @ValueSource(ints = {
        0,1,2,3,4,5,6,
        7,8,9,10,11,12,13,
        14,15,16,17,18,19,20,
        21,22,23,24,25,26,27
    })
    public void testSlugMovement(int input) {
        // Add slug.
        EnemySlug slug = new EnemySlug(new PathPosition(input, path), new ArrayList<>());

        // Move slug.
        slug.move();

        // Assert that slug has moved within one tile of original location.
        assertTrue(Math.floorMod(input + 1, 28) == slug.getLocation() || input == slug.getLocation() || Math.floorMod(input - 1, 28) == slug.getLocation());
    }

    // Test randomised movement of zombie.
    @ParameterizedTest
    @ValueSource(ints = {
        0,1,2,3,4,5,6,
        7,8,9,10,11,12,13,
        14,15,16,17,18,19,20,
        21,22,23,24,25,26,27
    })
    public void testZombieMovement(int input) {
        // Add zombie.
        EnemyZombie zombie = new EnemyZombie(new PathPosition(input, path), new ArrayList<>());
        
        // Move zombie.
        zombie.move();
        
        // Assert that zombie has moved within one tile of original location.
        assertTrue(Math.floorMod(input + 1, 28) == zombie.getLocation() || input == zombie.getLocation() || Math.floorMod(input - 1, 28) == zombie.getLocation());
    }
    
    // Test movement of vampire in anticlockwise direction.
    @ParameterizedTest
    @ValueSource(ints = {
        0,1,2,3,4,5,6,
        7,8,9,10,11,12,13,
        14,15,16,17,18,19,20,
        21,22,23,24,25,26,27
    })
    public void testVampireMovement(int input) {
        // Add vampire.
        EnemyVampire vampire = new EnemyVampire(new PathPosition(input, path), new ArrayList<>());

        // Move vampire.
        vampire.move();

        // Assert that vampire has moved within one tile of original location, in anticlockwise direction.
        assertTrue(Math.floorMod(input - 1, 28) == vampire.getLocation());
    }

    // Test movement of vampire when encountering campfire radius.
    @Test
    public void testVampireCampfireMovement() {
        // load a card and then convert cardto building by coordinates
        // Add campfire card to location (9, 1)
        
        Card campfire = d.loadCard("Campfire");
        d.convertCardToBuildingByCoordinates(campfire.getX(), campfire.getY(), 9, 1);

        // Add campfire card to location (9, 1)
        

        // Add vampire.
        EnemyVampire vampire = new EnemyVampire(new PathPosition(27, path), new ArrayList<>());

        // Add vampire to world.
        d.addEntity(vampire);
        
        // Add character.
        d.setCharacter(new Character(new PathPosition(7, path), 70, 7, 2, 10));

        // Move vampire to index 25.
        for (int moveIndex = 2; moveIndex > 0; moveIndex--) {
            d.runTickMoves();
        }

        assertTrue(vampire.getLocation() == 25);

        d.runTickMoves();

        // Assert vampire has reversed movement.
        assertTrue(vampire.getLocation() == 26);

    }

    // Test movement of vampire when caught between campfires.
    @Test
    public void testVampireCampfiresMovement() {
        
        Card campfire1 = d.loadCard("Campfire");
        d.convertCardToBuildingByCoordinates(campfire1.getX(), campfire1.getY(), 8, 9);

        Card campfire2 = d.loadCard("Campfire");
        d.convertCardToBuildingByCoordinates(campfire2.getX(), campfire2.getY(), 0, 1);

        // Add vampire.
        EnemyVampire vampire = new EnemyVampire(new PathPosition(23, path), new ArrayList<>());

        // Add vampire to world.
        d.addEntity(vampire);
        
        // Add character.
        d.setCharacter(new Character(new PathPosition(2, path), 70, 7, 2, 10));

        // Move vampire to index 18 and back.
        for (int moveIndex = 7; moveIndex > 0; moveIndex--) {
            d.runTickMoves();
        }

        // Assert vampire has reversed in clockwise direction.
        assertTrue(vampire.getLocation() == 20);

        for (int moveIndex = 5; moveIndex > 0; moveIndex--) {
            d.runTickMoves();
        }

        // Assert vampire has reversed movement.
        assertTrue(vampire.getLocation() == 23);

    }

    // If vampire starts in campfire radius, it will run in clockwise direction.
    @Test
    public void testVampireInCampfireMovement() {
        
        Card campfire = d.loadCard("Campfire");
        d.convertCardToBuildingByCoordinates(campfire.getX(), campfire.getY(), 9, 1);

        // Add vampire (in campfire radius).
        EnemyVampire vampire = new EnemyVampire(new PathPosition(21, path), new ArrayList<>());

        // Add character.
        d.setCharacter(new Character(new PathPosition(8, path), 70, 7, 2, 10));

        d.addEntity(vampire);

        for (int moveIndex = 6; moveIndex > 0; moveIndex--) {
            d.runTickMoves();
        }
        assertTrue(vampire.getLocation() == 27);
    }
    
    // If vampire starts in campfire radius, it will run in clockwise direction, and then reverse when meeting
    // another campfire radius.
    @Test
    public void testVampireInCampfiresMovement() {
        
        Card campfire1 = d.loadCard("Campfire");
        d.convertCardToBuildingByCoordinates(campfire1.getX(), campfire1.getY(), 0, 8);

        Card campfire2 = d.loadCard("Campfire");
        d.convertCardToBuildingByCoordinates(campfire2.getX(), campfire2.getY(), 9, 1);

        // Add vampire (in campfire radius).
        EnemyVampire vampire = new EnemyVampire(new PathPosition(8, path), new ArrayList<>());

        // Add character.
        d.setCharacter(new Character(new PathPosition(21, path), 70, 7, 2, 10));

        d.addEntity(vampire);
        for (int i = 9; i > 0; i--) {
            d.runTickMoves();
        }

        assertTrue(vampire.getLocation() == 17);

        d.runTickMoves();
        
        assertTrue(vampire.getLocation() == 16);
    }

    @Test
    public void equalsSameTest() {

        EnemyVampire vampire = new EnemyVampire(new PathPosition(26, path), new ArrayList<>());
        assertTrue(vampire.equals(vampire));
    }

    @Test
    public void statsEqualsTest() {
        EnemyVampire vampire1 = new EnemyVampire(new PathPosition(8, path), new ArrayList<>());
        EnemyVampire vampire2 = new EnemyVampire(new PathPosition(8, path), new ArrayList<>());
        
        // Vampire not equal to vampire with equal stats. Different instances.
        assertTrue(!vampire1.equals(vampire2));
    }

    @Test
    public void differentEqualsTest() {
        EnemyZombie zombie = new EnemyZombie(new PathPosition(7, path), new ArrayList<>());

        EnemySlug slug = new EnemySlug(new PathPosition(9, path), new ArrayList<>());

        assertTrue(!zombie.equals(slug));
    }

    @Test
    public void slugGetDropTest() {
        EnemySlug slug = new EnemySlug(new PathPosition(10, path), new ArrayList<>());

        assertTrue(slug.getDrop().size() == 1);
    }

    @Test
    public void zombieGetDropTest() {
        EnemyZombie zombie = new EnemyZombie(new PathPosition(5, path), new ArrayList<>());

        assertTrue(zombie.getDrop().size() == 1);
    }

    @Test
    public void vampireGetDropTest() {
        EnemyVampire vampire = new EnemyVampire(new PathPosition(19, path), new ArrayList<>());
        List<String> drops = vampire.getDrop();

        assertTrue(2 <= drops.size() && drops.size() <= 3);
    }

}
