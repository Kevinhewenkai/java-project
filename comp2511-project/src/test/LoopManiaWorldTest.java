package test;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.javatuples.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.LoopManiaWorld;
import unsw.loopmania.PathPosition;
import unsw.loopmania.EnemySlug;
import unsw.loopmania.EnemyVampire;
import unsw.loopmania.EnemyZombie;
import unsw.loopmania.ZombiePitBuilding;
import unsw.loopmania.Building;
import unsw.loopmania.Card;
import unsw.loopmania.Character;
import unsw.loopmania.Enemy;
import unsw.loopmania.HerosCastleBuilding;

public class LoopManiaWorldTest {
    
    private LoopManiaWorld d;
    private List<Pair<Integer, Integer>> path;

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

    // PossiblySpawnTests //
    @Test
    public void notSpawnEnemiesTest() {
        // Make a new character at 15th position.
        Character character = new Character(new PathPosition(15, path), 70, 7, 2, 10);
        
        d.setCharacter(character);

        List<Enemy> spawnedEnemies = d.possiblySpawnEnemies();

        // No spawns, because character is not at start of loop.
        assertTrue(spawnedEnemies.size() == 0);
    }

    @Test
    public void randomSpawnSlugTest() {
        
        Character character = new Character(new PathPosition(27, path), 70, 7, 2, 10);
        d.setCharacter(character);

        character.moveDownPath();
        List<Enemy> spawnedEnemies = d.possiblySpawnEnemies();

        // Enemies spawned in loop amounts between 3 and 6.
        assertTrue(3 <= spawnedEnemies.size() && spawnedEnemies.size() <= 6);
    }

    @Test
    public void onlySpawnSlugTest() {
        
        Character character = new Character(new PathPosition(27, path), 70, 7, 2, 10);
        d.setCharacter(character);

        character.moveDownPath();
        List<Enemy> spawnedEnemies = d.possiblySpawnEnemies();

        boolean onlySlug = true;

        for (Enemy spawnedEnemy: spawnedEnemies) {
            if (!(spawnedEnemy instanceof EnemySlug)) {
                onlySlug = false;
            }
        }

       assertTrue(onlySlug);
    }

    @Test
    public void possiblySpawnZombieTest() {
        
        Character character = new Character(new PathPosition(27, path), 70, 7, 2, 10);
        d.setCharacter(character);
        

        Card zombiePit = d.loadCard("ZombiePit");
        d.convertCardToBuildingByCoordinates(zombiePit.getX(), zombiePit.getY(), 9, 8);

        character.moveDownPath();

        List<Enemy> spawnedEnemies = d.possiblySpawnEnemies();

        // FIND ZOMBIE IN SPAWNED ENEMIES LIST.
        boolean zombieInSpawnedEnemies = false;

        for (Enemy spawnedEnemy: spawnedEnemies) {
            if (spawnedEnemy instanceof EnemyZombie) {
                zombieInSpawnedEnemies = true;
            }
        }
        
        assertTrue(zombieInSpawnedEnemies);
    }

    @Test
    public void possiblySpawnVampireTest() {
        
        Character character = new Character(new PathPosition(0, path), 70, 7, 2, 10);
        d.setCharacter(character);
        
        
        Card vampireCastle = d.loadCard("VampireCastle");
        d.convertCardToBuildingByCoordinates(vampireCastle.getX(), vampireCastle.getY(), 9, 8);

        List<Enemy> spawnedEnemies = d.possiblySpawnEnemies();

        // FIND Vampire IN SPAWNED ENEMIES LIST.
        boolean vampireInSpawnedEnemies = false;

        for (Enemy spawnedEnemy: spawnedEnemies) {
            if (spawnedEnemy instanceof EnemyVampire) {
                vampireInSpawnedEnemies = true;
            }
        }
        
        assertTrue(vampireInSpawnedEnemies);
    }

    @Test
    public void possiblySpawnVampireAfterLoopsTest() {
        
        Character character = new Character(new PathPosition(0, path), 70, 7, 2, 10);
        d.setCharacter(character);
        
        
        Card vampireCastle = d.loadCard("VampireCastle");
        d.convertCardToBuildingByCoordinates(vampireCastle.getX(), vampireCastle.getY(), 9, 8);
        // Loop Count = 5.

        for (int i = 28*5; i > 0; i--) {
            d.runTickMoves();
        }
        List<Enemy> spawnedEnemies = d.possiblySpawnEnemies();

        // FIND Vampire IN SPAWNED ENEMIES LIST.
        boolean vampireInSpawnedEnemies = false;

        for (Enemy spawnedEnemy: spawnedEnemies) {
            if (spawnedEnemy instanceof EnemyVampire) {
                vampireInSpawnedEnemies = true;
            }
        }
        
        assertTrue(vampireInSpawnedEnemies);
    }

    // convertCardToBuildingCoordinates //
    
    @Test
    public void convertCardToBuildingCoordinatesTest() {

        Card zombiePit = d.loadCard("ZombiePit");
        Building zombiePitBuilding = d.convertCardToBuildingByCoordinates(zombiePit.getX(), zombiePit.getY(), 9, 8);
        
        assertTrue(zombiePitBuilding instanceof ZombiePitBuilding);

    }

    // runTickMoves //
    // Need to test that character has moved down path.
    // Need to test that if character steps on a bararcks an ally gets added
    // Need to test if an enemy steps on a trap it gets defeated and trap disappears.
    @Test 
    public void characterMovesDownTickMoveTest() {
        
        Character character = new Character(new PathPosition(0, path), 70, 7, 2, 10);
        d.setCharacter(character);

        d.runTickMoves();

        assertTrue(character.getLocation() == 1);
    }

    @Test
    public void characterBarracksTickMoveTest() {

        Character character = new Character(new PathPosition(0, path), 70, 7, 2, 10);
        d.setCharacter(character);

        Card barracksCard = d.loadCard("Barracks");
        d.convertCardToBuildingByCoordinates(barracksCard.getX(), barracksCard.getY(), 1, 2);

        d.runTickMoves();

        assertTrue(character.getAllies().size() == 1);
    }

    @Test
    public void enemytrapTickMoveTest() {

        Character character = new Character(new PathPosition(0, path), 70, 7, 2, 10);
        d.setCharacter(character);

        EnemyVampire vampire = new EnemyVampire(new PathPosition(20, path), new ArrayList<>());
        int originalHp = vampire.getStats().getHp();

        d.addEntity(vampire);
        Card trapCard = d.loadCard("Trap");
        d.convertCardToBuildingByCoordinates(trapCard.getX(), trapCard.getY(), 8, 3);
        
        d.runTickMoves();

        assertTrue(originalHp - vampire.getStats().getHp() == 10);

        assertTrue(d.squareEmpty(8, 3));
    }
    
    // squareEmpty() //
    @Test
    public void squareEmptyTrueTest() {
        
        assertTrue(d.squareEmpty(8, 8));

    }

    @Test
    public void squareEmptyFalseHeroCastleTest() {
        
        HerosCastleBuilding herosCastleBuilding = new HerosCastleBuilding(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        d.setHerosCastle(herosCastleBuilding);
        assertTrue(!d.squareEmpty(1, 1));

    }

    // onPath //
    @Test
    public void onPathTrueTest() {
        assertTrue(d.onPath(3, 8));
    }

    @Test
    public void onPathFalseTest() {
        assertTrue(!d.onPath(5,5));
    }

    // adjacentToPath //

    @Test
    public void adjacentToPathTrueTest() {
        assertTrue(d.adjacentToPath(2, 4));
    }

    @Test
    public void adjacentToPathFalseTest() {
        assertTrue(!d.adjacentToPath(6, 6));
    }

    // isPlayerAtHerosCastle //
    @Test
    public void playerAtHerosCastleTest() {
        HerosCastleBuilding herosCastleBuilding = new HerosCastleBuilding(new SimpleIntegerProperty(8), new SimpleIntegerProperty(8));
        d.setHerosCastle(herosCastleBuilding);

        Character character = new Character(new PathPosition(14, path), 70, 7, 2, 10);
        d.setCharacter(character);

        assertTrue(d.isPlayerAtHerosCastle());
    }

    @Test
    public void playerNotAtHerosCastleTest() {
        HerosCastleBuilding herosCastleBuilding = new HerosCastleBuilding(new SimpleIntegerProperty(1), new SimpleIntegerProperty(7));
        d.setHerosCastle(herosCastleBuilding);

        Character character = new Character(new PathPosition(2, path), 70, 7, 2, 10);
        d.setCharacter(character);

        assertTrue(!d.isPlayerAtHerosCastle());
    }

    // runBattles //
    // runBattles needs to return a list of defeated enemies.
    @Test
    public void runBattlesWinNoEnemyTest() {
        Character character = new Character(new PathPosition(6, path), 70, 7, 2, 10);
        d.setCharacter(character);

        List<Enemy> defeatedEnemies = d.runBattles();

        assertTrue(defeatedEnemies.size() == 0);
    }
    
    @Test
    public void runBattlesWinTest() {
        Character character = new Character(new PathPosition(15, path), 300, 50, 2, 10);
        d.setCharacter(character);

        EnemySlug slug = new EnemySlug(new PathPosition(16, path), new ArrayList<>());
        d.addEntity(slug);

        EnemyVampire vampire = new EnemyVampire(new PathPosition(18, path), new ArrayList<>());
        d.addEntity(vampire);


        EnemyZombie zombie = new EnemyZombie(new PathPosition(17, path), new ArrayList<>());
        d.addEntity(zombie);

        List<Enemy> defeatedEnemies = d.runBattles();

        assertTrue(defeatedEnemies.size() == 3);

    }

    @Test
    public void runBattlesLoseTest() {
        Character character = new Character(new PathPosition(15, path), 10, 5, 2, 10);
        d.setCharacter(character);
        EnemyVampire vampire = new EnemyVampire(new PathPosition(16, path), new ArrayList<>());
        d.addEntity(vampire);


        EnemyZombie zombie = new EnemyZombie(new PathPosition(17, path), new ArrayList<>());
        d.addEntity(zombie);

        assertTrue(d.runBattles() == null);
    }
}
