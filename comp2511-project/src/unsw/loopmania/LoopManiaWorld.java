package unsw.loopmania;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.javatuples.Pair;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.lang.Math;

/**
 * A backend world.
 *
 * A world can contain many entities, each occupy a square. More than one entity
 * can occupy the same square.
 */
public class LoopManiaWorld {

    // UNEQUIPPED INVENTORY DIMENSIONS
    public static final int UNEQUIPPED_INVENTORY_WIDTH = 5;
    public static final int UNEQUIPPED_INVENTORY_HEIGHT = 3;

    // CARD INVENTORY DIMENSIONS
    public static final int CARD_INVENTORY_WIDTH = 5;
    public static final int CARD_INVENTORY_HEIGHT = 3;

    private static final ArrayList<Integer> RANDOM_SPAWN_RATE = new ArrayList<Integer>(Arrays.asList(3, 6));

    private static final int VAMP_STAKE_BONUS = 70;
    
    // BOSSES
    private static final int ELAN_MUSKE_SPAWN_CYCLES = 40;
    private static final int ELAN_MUSKE_SPAWN_XP = 10000;

    private static final int DOGGIE_SPAWN_CYCLES = 20;

    private boolean isElanSpawned = false;
    private boolean isElanJumped = false;

    // GAME WORLD DIMENSIONS
    private int width;
    private int height;

    // LOOP AND ROUND COUNTERS
    private int loopCount;
    private int roundCount;
    private List<Integer> roundNumbers;

    // ENTITIES
    private Character character;
    private List<Enemy> enemies;
    private List<MapItem> mapItems;

    // CARDS
    private CardInventory cardInventory;

    // BUILDINGS
    private List<Building> allBuildings;
    private List<ZombiePitBuilding> zombiePits;
    private List<VampireCastleBuilding> vampireCastles;
    private List<Building> allBarracks;
    private HerosCastleBuilding herosCastle;

    // GOALS
    private GoalNode goalNodes;
    private int bossDefeatCount;

    // RARE ITEM SETTINGS
    List<String> rareItemSettings = new ArrayList<String>();

    // List of x,y coordinate pairs in the order by which moving entities traverse
    // them
    private List<Pair<Integer, Integer>> orderedPath;

    // STATS TEXT
    private SimpleStringProperty statsText = new SimpleStringProperty();

    // CONFUSING MODE FLAG
    private boolean confusing;

    /**
     * CONSTRUCTOR (create the world)
     * 
     * @param width       width of world in number of cells
     * @param height      height of world in number of cells
     * @param orderedPath ordered list of x, y coordinate pairs representing
     *                    position of path cells in world
     */
    public LoopManiaWorld(int width, int height, List<Pair<Integer, Integer>> orderedPath) {
        // GENERAL
        this.width = width;
        this.height = height;
        this.orderedPath = orderedPath;

        // COUNTERS
        loopCount = 0;
        roundCount = 1;
        bossDefeatCount = 0;
        roundNumbers = new ArrayList<>();

        Integer curr = 1;
        Integer next = 1;
        for (int i = 0; i < 100; i++) {
            next += curr;
            roundNumbers.add(next);
            curr++;
        }

        // ENTITIES
        character = null;
        enemies = new ArrayList<>();
        mapItems = new ArrayList<>();

        // CARDS
        cardInventory = new CardInventory(CARD_INVENTORY_WIDTH, CARD_INVENTORY_HEIGHT);

        // BUILDINGS
        allBuildings = new ArrayList<>();
        zombiePits = new ArrayList<>();
        vampireCastles = new ArrayList<>();
        allBarracks = new ArrayList<>();
        herosCastle = null;

        // PATH
        this.orderedPath = orderedPath;

        newStatsText("Initialised world!");
    }

    /* GENERAL GETTERS AND SETTERS */

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public CardInventory getCardInventory() {
        return cardInventory;
    }

    public int getLoopCount() {
        return loopCount;
    }

    public int getRoundCount() {
        return roundCount;
    }

    public void updateLoopAndRound() {
        for (VampireCastleBuilding vampireCastle : vampireCastles) {
            vampireCastle.incrementLoopCount();
        }

        loopCount++;
        if (roundNumbers.contains(loopCount)) {
            character.pickUpXp(100);
            roundCount++;
        }

        // update totalCycles for rareItems:
        if (character.isItemEquipped(GameShark.class)) {
            GameShark gameShark = (GameShark) character.getItemOfType(GameShark.class);
            gameShark.incrementTotalCycles();
            if (gameShark.getTotalCycles() == 3) {
                character.unequipItem(gameShark);
                gameShark.destroy();
            }
        } else if (character.isItemEquipped(LuckyClover.class)) {
            LuckyClover luckyClover = (LuckyClover) character.getItemOfType(LuckyClover.class);
            luckyClover.incrementTotalCycles();
            if (luckyClover.getTotalCycles() == 4) {
                character.unequipItem(luckyClover);
                luckyClover.destroy();
            }
        }
    }

    public Character getCharacter() {
        return character;
    }

    /**
     * set the character. This is necessary because it is loaded as a special entity
     * out of the file
     * 
     * @param character the character
     */
    public void setCharacter(Character character) {
        this.character = character;
    }

    public void setHerosCastle(HerosCastleBuilding herosCastle) {
        this.herosCastle = herosCastle;
        allBuildings.add(herosCastle);
    }

    /* OTHER METHODS */

    /**
     * Run moves which occur with every tick without needing to spawn anything
     * immediately
     */
    public List<Enemy> runTickMoves() {
        character.moveDownPath();
        // Check if character passed through village, barracks, or campfire,
        for (Building b : allBarracks) {
            if (b.entityOnBuilding(character)) {
                Stats s = new Stats();
                s.setDamage(10);
                s.setHp(25);

                Ally a = new Ally(new PathPosition(character.getLocation(), orderedPath), s);
                character.addAlly(a);
            }
        }

        for (Building b : allBuildings) {
            if (b.getType() == "Village" && b.entityOnBuilding(character)) {
                int newHp = character.getStats().getHp() + 20;
                if (newHp > 100)
                    newHp = 100;
                character.getStats().setHp(newHp);
            }
        }

        if (inCampfireRadius(character) && !character.getInCampfireRadius()) {
            character.setInCampfireRadius(true);
            character.getTotalStats().setDamage(character.getTotalStats().getDamage() * 2);
        } else if (!inCampfireRadius(character) && character.getInCampfireRadius()) {
            character.setInCampfireRadius(false);
            character.getTotalStats().setDamage(character.getTotalStats().getDamage() / 2);
        }

        // Handling enemies
        List<Enemy> defeatedEnemies = new ArrayList<>();
        defeatedEnemies = moveBasicEnemies();
        for (Enemy defeatedEnemy: defeatedEnemies) {
            character.pickUpGold(defeatedEnemy.getGold());
            character.pickUpXp(10);
        }
        return defeatedEnemies;
    }

    public int collectSpawnedItems() {
        int newPotions = 0;
        for (MapItem mapItem : mapItems) {
            if (mapItem.getX() == character.getX() && mapItem.getY() == character.getY()) {
                if (mapItem.getType() == "gold") {
                    Random rand = new Random();
                    character.pickUpGold(rand.nextInt((5 - 1) + 5) + 1);
                    mapItem.destroy();
                } else if (mapItem.getType() == "potion") {
                    newPotions++;
                    mapItem.destroy();
                }
            }
        }
        return newPotions;
    }

    /**
     * add a generic entity (without it's own dedicated method for adding to the
     * world)
     * 
     * @param entity
     */
    public void addEntity(Entity entity) {
        // for adding non-specific entities (ones without another dedicated list)
        if (entity instanceof EnemyVampire) {
            EnemyVampire vamp = (EnemyVampire) entity;
            if (inCampfireRadius(vamp)) {
                vamp.setInCampfireRadius(true);
            }
            enemies.add(vamp);

        } else if (entity instanceof EnemyZombie) {
            enemies.add((EnemyZombie) entity);
        } else if (entity instanceof EnemySlug) {
            enemies.add((EnemySlug) entity);
        }
    }

    /**
     * spawns enemies if the conditions warrant it, adds to world
     * 
     * @return list of the enemies to be displayed on screen
     */
    public List<Enemy> possiblySpawnEnemies() {
        // What should be happening here. Zombies spawn every cycle with zombie pit.
        // Vampires every five cycles.
        List<Enemy> spawningEnemies = new ArrayList<>();
        if (character.getLocation() != 0) {
            return spawningEnemies;
        }

        Random r = new Random();

        int spawns = r.nextInt(RANDOM_SPAWN_RATE.get(1) - RANDOM_SPAWN_RATE.get(0) + 1) + RANDOM_SPAWN_RATE.get(0);

        for (int i = 0; i < spawns; ++i) {
            Pair<Integer, Integer> slugPos = getRandomSpawnPosition();
            if (slugPos != null) {
                int indexInPath = orderedPath.indexOf(slugPos);
                EnemySlug slug = new EnemySlug(new PathPosition(indexInPath, orderedPath), rareItemSettings);
                enemies.add(slug);
                spawningEnemies.add(slug);
            }
        }
        for (ZombiePitBuilding b : zombiePits) {
            Pair<Integer, Integer> zombiePos = GetAdjacentPathPosition(b);
            int indexInPath = orderedPath.indexOf(zombiePos);
            EnemyZombie zombie = new EnemyZombie(new PathPosition(indexInPath, orderedPath), rareItemSettings);
            enemies.add(zombie);
            spawningEnemies.add(zombie);
        }

        for (VampireCastleBuilding b : vampireCastles) {
            if (b.getLoopCount() == 1) {
                // Spawn vampire
                Pair<Integer, Integer> vampirePos = GetAdjacentPathPosition(b);
                int indexInPath = orderedPath.indexOf(vampirePos);
                EnemyVampire vamp = new EnemyVampire(new PathPosition(indexInPath, orderedPath), rareItemSettings);
                if (inCampfireRadius(vamp)) {
                    vamp.setInCampfireRadius(true);
                }

                enemies.add(vamp);
                spawningEnemies.add(vamp);
            }
        }

        if (loopCount == (DOGGIE_SPAWN_CYCLES)) {
            Pair<Integer, Integer> doggiePos = getRandomSpawnPosition();
            int indexInPath = orderedPath.indexOf(doggiePos);
            BossDoggie doggie = new BossDoggie(new PathPosition(indexInPath, orderedPath), rareItemSettings);
            enemies.add(doggie);
            spawningEnemies.add(doggie);
        }

        if (isElanSpawned == false && loopCount >= (ELAN_MUSKE_SPAWN_CYCLES)
                && character.getXp() >= ELAN_MUSKE_SPAWN_XP) {
            Pair<Integer, Integer> elanMuskePos = getRandomSpawnPosition();
            int indexInPath = orderedPath.indexOf(elanMuskePos);
            BossElanMuske elanMuske = new BossElanMuske(new PathPosition(indexInPath, orderedPath), rareItemSettings);
            enemies.add(elanMuske);
            spawningEnemies.add(elanMuske);
            isElanSpawned = true;
            character.revalueDoggieCoin();
        }

        return spawningEnemies;
    }

    /**
     * get a randomly generated position which could be used to spawn an enemy
     * 
     * @return null if random choice is that wont be spawning an enemy or it isn't
     *         possible, or random coordinate pair if should go ahead
     */
    private Pair<Integer, Integer> getRandomSpawnPosition() {

        // has a chance spawning a basic enemy on a tile the character isn't on or
        // immediately before or after (currently space required = 2)...
        List<Pair<Integer, Integer>> orderedPathSpawnCandidates = new ArrayList<>();
        int indexPosition = orderedPath.indexOf(new Pair<Integer, Integer>(character.getX(), character.getY()));
        // inclusive start and exclusive end of range of positions not allowed
        int startNotAllowed = (indexPosition - 2 + orderedPath.size()) % orderedPath.size();
        int endNotAllowed = (indexPosition + 3) % orderedPath.size();
        // note terminating condition has to be != rather than < since wrap around...
        for (int i = endNotAllowed; i != startNotAllowed; i = (i + 1) % orderedPath.size()) {
            orderedPathSpawnCandidates.add(orderedPath.get(i));
        }

        Random rand = new Random();
        // choose random choice
        Pair<Integer, Integer> spawnPosition = orderedPathSpawnCandidates
                .get(rand.nextInt(orderedPathSpawnCandidates.size()));

        return spawnPosition;
    }

    /**
     * Gets first adjacent pathposition to the given building
     * 
     * @param b
     * @return
     */
    private Pair<Integer, Integer> GetAdjacentPathPosition(Building b) {
        for (Pair<Integer, Integer> square : orderedPath) {
            if (java.lang.Math.abs(b.getX() - square.getValue0())
                    + java.lang.Math.abs((b.getY() - square.getValue1())) == 1) {
                return square;
            }
        }
        return null;
    }

    /**
     * kill an enemy
     * 
     * @param enemy enemy to be killed
     */
    public void killEnemy(Enemy enemy) {
        enemy.destroy();
        enemies.remove(enemy);
    }

    /**
     * run the expected battles in the world, based on current world state
     * 
     * @return list of enemies which have been killed
     */
    public List<Enemy> runBattles() {
        List<Enemy> defeatedEnemies = new ArrayList<>();
        Boolean gameOver = false;

        // DONE: SCALAR DEFENCE - reducing enemy attack by certain value
        // DONE: DEFENCE - if enemy attack > defence then character loses hp
        // NEEDS TEST DONE: STAKE - double damage to vamps
        // TRANCE - creates ally from enemy for a certain number of moves
        // ZOMBIES - turn allies into zombies when critical hit

        makeElanJump();

        for (Enemy e : enemies) {
            // Keeps track of zombies to add to the enemies list ==> avoids changing the
            // list we are looping over
            // int infected_allies = 0;

            if (Math.sqrt(Math.pow((character.getX() - e.getX()), 2) + Math.pow((character.getY() - e.getY()), 2)) <= e.getBattleRadius() && !defeatedEnemies.contains(e)) {

                List<Enemy> cronies = getCronies(e, defeatedEnemies);

                // Character get Ally Traitor.
                Enemy traitor = character.getAllyTraitor();
                Stats traitorStats = null;
                if (traitor != null) {
                    traitorStats = traitor.getStats();
                    System.out.println(traitorStats.getHp());
                }

                // Can't have more than one ally traitor.

                // If a staff is equipped, the first enemy in the cronies list is put into a
                // trance *spooky*
                // for 10 moves and then added back to the enemy team
                // Has to be within chance of trance.

                if (cronies.size() > 0 && traitor == null && !(e instanceof Boss) && character.isItemEquipped(Staff.class)) {
                    traitor = cronies.remove(0);
                    character.setAllyTraitor(traitor);
                    character.setTraitorMoves(10);
                    traitorStats = traitor.getStats();
                    Stats s = new Stats();
                    s.setDamage(traitorStats.getDamage());
                    s.setHp(traitorStats.getHp());
                }

                // If enemy is a zombie, 20% critical chance of transforming allied soldier into
                // a zombie
                if (e instanceof EnemyZombie && character.getAllies().size() > 0) {

                    // check if randomly generated number between 0 and 100 is less than
                    // CRITICAL_CHANCE
                    Random rand = new Random();
                    if (rand.nextInt(100) < e.getStats().getCriticalChance()) {
                        // Turn oldest ally into a zombie
                        Ally a = character.getAllies().remove(0);
                        a.destroy();
                        addStatsText("Ally infected!\n");

                        // Add a zombie to enemy list
                        EnemyZombie infected_ally = new EnemyZombie(character.getPosition(), rareItemSettings);

                        // TODO: Potentially distorting list here. Fix.
                        enemies.add(infected_ally);
                    }
                }

                // Get combined stats for enemy team.
                Stats enemyTeamStats = new Stats();
                enemyTeamStats.addStats(e.getStats());

                // if enemy is a vampire and shield is equipped, lower critical vampire attack
                // chance by 60%
                if (e instanceof EnemyVampire && character.isItemEquipped(Shield.class)) {
                    int newCriticalChance = (int) 0.6 * ((EnemyVampire) e).getCriticalChance();
                    enemyTeamStats.setCriticalChance(newCriticalChance);
                    addStatsText("Vampire crit chance lowered by 60%\n");
                }

                for (Enemy c : cronies)
                    enemyTeamStats.addStats(c.getStats());

                // Get combined stats for character team.
                Stats characterTeamStats = new Stats();
                characterTeamStats.addStats(character.getTotalStats());

                // if enemy is a Boss and Tree stump equipped, defence increased by 15
                if (e instanceof Boss && character.isItemEquipped(TreeStump.class)) {
                    Stats increasedDefence = new Stats();
                    increasedDefence.setDefence(15);
                    characterTeamStats.addStats(increasedDefence);
                }

                for (Ally a : character.getAllies()) {
                    characterTeamStats.addStats(a.getStats());
                }

                if (traitor != null) {
                    characterTeamStats.addStats(traitorStats);
                }

                newStatsText("NEW BATTLE\n\n");

                int alliesDamageTaken = 0;
                int enemyAttackDamage = 0;
                int CTAttackDamage = 0;
                int enemiesDamageTaken = 0;
                while (characterTeamStats.getHp() > 0 && enemyTeamStats.getHp() > 0) {


                    enemyAttackDamage = enemyTeamStats.getAttackDamage() - characterTeamStats.getScalarDefence();
                    characterTeamStats.applyDamage(enemyAttackDamage);

                    // Damage applied to enemy.

                    characterTeamStats.setCriticalDamage(character.getCriticalDamage());
                    // If gameShark equipped, CharacterTeam crit chance is 50%
                    if (character.isItemEquipped(GameShark.class)
                            || (confusing && character.isItemEquipped(OneRing.class))) {
                        characterTeamStats.setCriticalChance(50);

                        // 10% chance of character harming themselves
                        Random rand = new Random();
                        if (rand.nextInt(10) == 1) {
                            characterTeamStats.applyDamage(CTAttackDamage);
                        }
                    } else {
                        characterTeamStats.setCriticalChance(5);
                    }

                    CTAttackDamage = characterTeamStats.getAttackDamage();

                    // If luckyClover equipped, Enemy chance of dropping rare item doubles
                    if (character.isItemEquipped(LuckyClover.class)
                            || (confusing && character.isItemEquipped(DenimShorts.class))) {
                        e.doubleRareChance();
                        for (Enemy c : cronies) {
                            c.doubleRareChance();
                        }
                    }

                    // Doggie does not stun character apply damage.
                    if (!(e instanceof BossDoggie && (new Random()).nextInt(100) < e.getStats().getCriticalChance())) {

                        if (e instanceof EnemyVampire && character.isItemEquipped(Stake.class)) {
                            CTAttackDamage += VAMP_STAKE_BONUS;
                        }

                        // Anduril causes triple damage against Bosses
                        if (e instanceof Boss && character.isItemEquipped(Anduril.class)) {
                            Anduril anduril = (Anduril) character.getItemOfType(Anduril.class);
                            CTAttackDamage += 2 * anduril.getDamage();
                        }

                        enemyTeamStats.applyDamage(CTAttackDamage);
                        enemiesDamageTaken += CTAttackDamage;
                    } else {
                        addStatsText("DOGGIE STUN!\n");
                    }

                    alliesDamageTaken += enemyAttackDamage;

                    // Decrements the moves before the traitor is killed
                    if (traitor != null) {
                        // If zero then the traitor is added back to the enemies list and the enemy
                        // stats updated
                        if (character.getTraitorMoves() == 0) {
                            character.setAllyTraitor(null);
                            cronies.add(traitor);
                            enemyTeamStats.addStats(traitor.getStats());
                        } else
                            character.decrementTraitorMoves();
                    }
                }

                //// APPLYING DEFENCE ////
                int prevAlliesDamageTaken = alliesDamageTaken;
                // Remove damage because of defence here instead!
                alliesDamageTaken -= character.getTotalStats().getDefence();
                if (alliesDamageTaken < 0) {
                    alliesDamageTaken = 0;
                }
                characterTeamStats.setHp(characterTeamStats.getHp() + (prevAlliesDamageTaken - alliesDamageTaken));
                /************************/

                // Comparing speed if ambiguous result. i.e. both teams have below 0 hp.
                if (characterTeamStats.getHp() <= 0 && enemyTeamStats.getHp() <= 0) {

                    // Character wins if it's speed is equal to or greater than the enemy team.
                    // Else, the enemy team wins.
                    if (characterTeamStats.getSpeed() >= enemyTeamStats.getSpeed()
                            || character.isItemEquipped(DenimShorts.class)
                            || (confusing && character.isItemEquipped(GameShark.class))) {
                        characterTeamStats.setHp(characterTeamStats.getHp() + enemyAttackDamage);
                        alliesDamageTaken -= enemyAttackDamage;
                    } else { // Enemy team wins.
                        enemyTeamStats.setHp(enemyTeamStats.getHp() + CTAttackDamage);
                        enemiesDamageTaken -= CTAttackDamage;

                        character.getStats().setHp(0);
                        addStatsText("Character attacks!\n Damage applied to enemy: " + enemiesDamageTaken + "\n");
                        addStatsText("<3 Enemy HP: " + enemyTeamStats.getHp() + "\n\n");

                        addStatsText("Enemy attacks!\nDamage applied to character: " + alliesDamageTaken + "\n");

                        if (character.getStats().getHp() < 0) {
                            addStatsText("<3 Character HP: 0\n\n");
                        } else {
                            addStatsText("<3 Character HP: " + character.getStats().getHp() + "\n\n");
                        }

                        // GAME OVER!
                        gameOver = true;
                        return useOneRing(defeatedEnemies);
                    }
                }

                if (characterTeamStats.getHp() <= 0) {
                    // GAME OVER!
                    character.getStats().setHp(0);
                    addStatsText("Enemy attacks!\nDamage applied to character: " + alliesDamageTaken + "\n");

                    if (character.getStats().getHp() < 0) {
                        addStatsText("<3 Character HP: 0\n\n");
                    } else {
                        addStatsText("<3 Character HP: " + character.getStats().getHp() + "\n\n");
                    }

                    addStatsText("Character attacks!\n Damage applied to enemy: " + enemiesDamageTaken + "\n");
                    if (enemyTeamStats.getHp() < 0) {
                        enemyTeamStats.setHp(0);
                    }
                    addStatsText("<3 Enemy HP: " + enemyTeamStats.getHp() + "\n\n");

                    gameOver = true;
                    return useOneRing(defeatedEnemies);
                }

                List<Ally> defeatedAllies = new ArrayList<>();

                // Deal with damage to character's allies. Reducing hp by damage dealt in
                // battle, until no more damage left to dealt,
                for (Ally a : character.getAllies()) {
                    Stats allyStats = a.getStats();
                    int allyHp = allyStats.getHp();

                    // Damage dealt is greater than ally's hp.
                    if (alliesDamageTaken >= allyStats.getHp()) {
                        defeatedAllies.add(a);
                    } else {
                        allyStats.setHp(allyHp - alliesDamageTaken);
                        a.setStats(allyStats);
                        alliesDamageTaken = 0;
                        break;
                    }
                    alliesDamageTaken -= allyStats.getHp();
                }

                Stats characterStats = character.getStats();

                characterStats.applyDamage(alliesDamageTaken);

                addStatsText("Enemy attacks!\nDamage applied to character: " + alliesDamageTaken + "\n");
                if (characterStats.getHp() < 0) {
                    addStatsText("<3 Character HP: 0\n\n");
                } else {
                    addStatsText("<3 Character HP: " + characterStats.getHp() + "\n\n");
                }

                if (characterStats.getHp() <= 0) {
                    addStatsText("Character attacks!\n Damage applied to enemy: " + enemiesDamageTaken + "\n");
                    if (enemyTeamStats.getHp() < 0) {
                        enemyTeamStats.setHp(0);
                    }
                    addStatsText("<3 Enemy HP: " + enemyTeamStats.getHp() + "\n\n");

                    characterStats.setHp(0);
                    // GAME OVER!
                    gameOver = true;
                    return useOneRing(defeatedEnemies);
                }

                addStatsText("Character attacks!\n Damage applied to enemy: " + enemiesDamageTaken + "\n");
                enemyTeamStats.setHp(0);
                addStatsText("<3 Enemy HP: " + enemyTeamStats.getHp() + "\n\n");

                // Add enemy and cronies to defeated list.
                defeatedEnemies.add(e);
                for (Enemy c : cronies) {
                    defeatedEnemies.add(c);
                }

                // Allies killed.
                for (Ally a : defeatedAllies) {
                    character.removeAlly(a);
                    a.destroy();
                }
            }
        }

        // Enemies killed.
        for (Enemy e : defeatedEnemies) {
            // IMPORTANT = we kill enemies here, because killEnemy removes the enemy from
            // the enemies list
            // if we killEnemy in prior loop, we get
            // java.util.ConcurrentModificationException
            // due to mutating list we're iterating over

            character.pickUpGold(e.getGold());
            character.pickUpXp(10);
            if (e instanceof BossElanMuske)
                character.revalueDoggieCoin();
            // Add cards and add stuff to inventory
        }

        return defeatedEnemies;
    }

    private List<Enemy> useOneRing(List<Enemy> defeatedEnemies) {
        if (character.isItemEquipped(OneRing.class)) {
            Item oneRing = character.getItemOfType(OneRing.class);
            character.getStats().setHp(100);
            character.getTotalStats().setHp(100);
            character.unequipItem(oneRing);
            oneRing.destroy();
            addStatsText("OneRing used");
            return defeatedEnemies;
        } else {
            return null;
        }
    }

    private List<Enemy> getCronies(Enemy mainEnemy, List<Enemy> defeatedEnemies) {
        List<Enemy> cronies = new ArrayList<>();

        for (Enemy c : enemies) {
            if (!mainEnemy.equals(c) && !defeatedEnemies.contains(c) && (Math.pow((mainEnemy.getX() - c.getX()), 2)
                    + Math.pow((mainEnemy.getY() - c.getY()), 2) <= c.getSupportRadius())) {
                cronies.add(c);
            }
        }

        return cronies;
    }

    private void makeElanJump() {
        for (Enemy e : enemies) {
            if (e.getType().equals("Elan Muske") && isElanJumped == false && Math.sqrt(
                    Math.pow((character.getX() - e.getX()), 2) + Math.pow((character.getY() - e.getY()), 2)) <= 7) {
                // Move elanMuske two times battle radius, and outside radius of vampire battle
                // radius.
                while (Math.sqrt(
                        Math.pow((character.getX() - e.getX()), 2) + Math.pow((character.getY() - e.getY()), 2)) <= 8) {
                    e.moveUpPath();
                }
                isElanJumped = true;
                break;
            }
        }
    }

    /**
     * Move all enemies
     */
    private List<Enemy> moveBasicEnemies() {

        List<Enemy> defeatedEnemies = new ArrayList<>();

        Enemy elanMuske = null;
        for (Enemy e : enemies) {
            e.move();
            // Check if enemy is in radius of a campfire
            if (inCampfireRadius(e)) {
                e.setInCampfireRadius(true);
            } else if (e.getInCampfireRadius()) {
                e.reverseDirection();
                e.setInCampfireRadius(false);
            }

            // Check if enemy is in radius of a tower
            if (inTowerRadius(e)) {
                e.getStats().setHp(e.getStats().getHp() - 2);
            }

            // Check if enemy is on a trap
            List<Building> destroyedTraps = new ArrayList<Building>();
            for (Building b : allBuildings) {
                if (b.getType() == "Trap" && b.entityOnBuilding(e)) {
                    e.getStats().setHp(e.getStats().getHp() - 10);
                    destroyedTraps.add(b);
                }
            }

            for (Building trap : destroyedTraps) {
                allBuildings.remove(trap);
                trap.destroy();
            }

            if (e.getStats().getHp() <= 0) {
                defeatedEnemies.add(e);
            } else if (e.getType().equals("Elan Muske")) {
                elanMuske = e;
            }
        }

        elanHealEnemies(elanMuske, defeatedEnemies);
        for (Enemy e : defeatedEnemies) {
            if (e instanceof BossElanMuske)
                character.revalueDoggieCoin();
        }

        return defeatedEnemies;
    }

    private boolean inCampfireRadius(MovingEntity e) {
        for (Building building : allBuildings) {
            if (building.getInCampfireRadius(e)) {
                return true;
            }
        }
        return false;
    }

    private boolean inTowerRadius(Enemy e) {
        for (Building building : allBuildings) {
            if (building.getInTowerRadius(e)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Elan Muske heals enemies if within support radius of Elan Muske.
     * 
     * @param elanMuske
     * @param defeatedEnemies
     */
    private void elanHealEnemies(Enemy elanMuske, List<Enemy> defeatedEnemies) {
        if (elanMuske == null) {
            return;
        }

        for (Enemy e : enemies) {
            if (!defeatedEnemies.contains(e) && !e.equals(elanMuske)
                    && Math.sqrt(Math.pow((elanMuske.getX() - e.getX()), 2)
                            + Math.pow((elanMuske.getY() - e.getY()), 2)) <= elanMuske.getSupportRadius()) {
                e.heal();
            }
        }
    }

    /* ITEM FUNCTIONS */

    public List<MapItem> spawnNewMapItem() {
        List<MapItem> spawnedItems = new ArrayList<>();
        Random rand = new Random();

        // GOLD
        int spawn = (rand.nextInt(20));
        if (spawn == 0) {
            List<Pair<Integer, Integer>> orderedPathSpawnCandidates = new ArrayList<>();
            int indexPosition = orderedPath.indexOf(new Pair<Integer, Integer>(character.getX(), character.getY()));
            // inclusive start and exclusive end of range of positions not allowed
            int startNotAllowed = (indexPosition - 2 + orderedPath.size()) % orderedPath.size();
            int endNotAllowed = (indexPosition + 3) % orderedPath.size();
            // note terminating condition has to be != rather than < since wrap around...
            for (int i = endNotAllowed; i != startNotAllowed; i = (i + 1) % orderedPath.size()) {
                orderedPathSpawnCandidates.add(orderedPath.get(i));
            }
            // choose random choice
            Pair<Integer, Integer> spawnPosition = orderedPathSpawnCandidates
                    .get(rand.nextInt(orderedPathSpawnCandidates.size()));

            // create new item
            if (spawnPosition != null) {
                int indexInPath = orderedPath.indexOf(spawnPosition);
                MapItem newMapGold = new MapItem(new PathPosition(indexInPath, orderedPath), "gold");
                mapItems.add(newMapGold);
                spawnedItems.add(newMapGold);
            }
        }

        // HEALTH POTION
        spawn = rand.nextInt(800);
        if (spawn == 0) {
            List<Pair<Integer, Integer>> orderedPathSpawnCandidates = new ArrayList<>();
            int indexPosition = orderedPath.indexOf(new Pair<Integer, Integer>(character.getX(), character.getY()));
            // inclusive start and exclusive end of range of positions not allowed
            int startNotAllowed = (indexPosition - 2 + orderedPath.size()) % orderedPath.size();
            int endNotAllowed = (indexPosition + 3) % orderedPath.size();
            // note terminating condition has to be != rather than < since wrap around...
            for (int i = endNotAllowed; i != startNotAllowed; i = (i + 1) % orderedPath.size()) {
                orderedPathSpawnCandidates.add(orderedPath.get(i));
            }
            // choose random choice
            Pair<Integer, Integer> spawnPosition = orderedPathSpawnCandidates
                    .get(rand.nextInt(orderedPathSpawnCandidates.size()));

            // create new item
            if (spawnPosition != null) {
                int indexInPath = orderedPath.indexOf(spawnPosition);
                MapItem newMapPotion = new MapItem(new PathPosition(indexInPath, orderedPath), "potion");
                mapItems.add(newMapPotion);
                spawnedItems.add(newMapPotion);
            }
        }
        return spawnedItems;
    }

    public Item loadItem(String type) {
        switch (type) {
            case "Sword":
                Sword sword = new Sword(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
                sword = (Sword) character.pickupItem(sword);
                return sword;
            case "Stake":
                Stake stake = new Stake(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
                character.pickupItem(stake);
                return stake;
            case "Staff":
                Staff staff = new Staff(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
                character.pickupItem(staff);
                return staff;
            case "Armour":
                Armour armour = new Armour(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
                character.pickupItem(armour);
                return armour;
            case "Shield":
                Shield shield = new Shield(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
                character.pickupItem(shield);
                return shield;
            case "Helmet":
                Helmet helmet = new Helmet(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
                character.pickupItem(helmet);
                return helmet;
            case "HealthPotion":
                HealthPotion healthPotion = new HealthPotion(new SimpleIntegerProperty(0),
                        new SimpleIntegerProperty(0));
                character.pickupItem(healthPotion);
                return healthPotion;
            case "DoggieCoin":
                DoggieCoin doggieCoin = new DoggieCoin(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
                character.pickupItem(doggieCoin);
                return doggieCoin;

            // RARE ITEMS
            case "OneRing":
                OneRing oneRing = new OneRing(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
                character.pickupItem(oneRing);
                return oneRing;
            case "LuckyClover":
                LuckyClover luckyClover = new LuckyClover(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
                character.pickupItem(luckyClover);
                return luckyClover;
            case "DenimShorts":
                DenimShorts denimShorts = new DenimShorts(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
                character.pickupItem(denimShorts);
                return denimShorts;
            case "Gameshark":
                GameShark gameshark = new GameShark(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
                character.pickupItem(gameshark);
                return gameshark;
            case "Anduril":
                Anduril anduril = new Anduril(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
                character.pickupItem(anduril);
                return anduril;
            case "TreeStump":
                TreeStump treeStump = new TreeStump(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
                character.pickupItem(treeStump);
                return treeStump;
        }
        return null;

    }

    public Boolean useHealthPotion() {
        for (Item i : character.getUnequipped().getItems()) {
            if (i.getType() == "HealthPotion") {
                character.getStats().setHp(100);
                character.getTotalStats().setHp(100);
                character.getUnequipped().removeAndDestroyItem(i);
                return true;
            }
        }
        return false;
    }

    /* CARD AND BUILDING FUNCTIONS */

    /**
     * Spawn a card in the world and return the card entity
     * 
     * @return a card to be spawned in the controller as a JavaFX node
     */
    public Card loadCard(String type) {
        if (cardInventory.getInventory().size() == 15) { // Pickup gold/xp when destroying item (NEED TO TIDY THIS)
            character.pickUpGold(10);
            character.pickUpXp(10);
        }
        return cardInventory.loadCard(type);
    }

    /**
     * remove a card by its x, y coordinates
     * 
     * @param cardNodeX     x index from 0 to width-1 of card to be removed
     * @param cardNodeY     y index from 0 to height-1 of card to be removed
     * @param buildingNodeX x index from 0 to width-1 of building to be added
     * @param buildingNodeY y index from 0 to height-1 of building to be added
     */
    public Building convertCardToBuildingByCoordinates(int cardNodeX, int cardNodeY, int buildingNodeX,
            int buildingNodeY) {
        Card card = cardInventory.getCardByCoordinates(cardNodeX, cardNodeY);

        String type = card.getClass().getSimpleName();
        cardInventory.removeCardByObject(card);

        // Spawn building
        switch (type) {
            case "CampfireCard":
                CampfireBuilding newCampfire = new CampfireBuilding(new SimpleIntegerProperty(buildingNodeX),
                        new SimpleIntegerProperty(buildingNodeY));
                allBuildings.add(newCampfire);
                return newCampfire;

            case "TrapCard":
                TrapBuilding newTrap = new TrapBuilding(new SimpleIntegerProperty(buildingNodeX),
                        new SimpleIntegerProperty(buildingNodeY));
                allBuildings.add(newTrap);
                return newTrap;

            case "VillageCard":
                VillageBuilding newVillage = new VillageBuilding(new SimpleIntegerProperty(buildingNodeX),
                        new SimpleIntegerProperty(buildingNodeY));
                allBuildings.add(newVillage);
                return newVillage;

            case "TowerCard":
                TowerBuilding newTower = new TowerBuilding(new SimpleIntegerProperty(buildingNodeX),
                        new SimpleIntegerProperty(buildingNodeY));
                allBuildings.add(newTower);
                return newTower;

            case "BarracksCard":
                BarracksBuilding newBarracks = new BarracksBuilding(new SimpleIntegerProperty(buildingNodeX),
                        new SimpleIntegerProperty(buildingNodeY));
                allBuildings.add(newBarracks);
                allBarracks.add(newBarracks);
                return newBarracks;

            case "VampireCastleCard":
                VampireCastleBuilding newVampireCastle = new VampireCastleBuilding(
                        new SimpleIntegerProperty(buildingNodeX), new SimpleIntegerProperty(buildingNodeY));
                allBuildings.add(newVampireCastle);
                vampireCastles.add(newVampireCastle);
                return newVampireCastle;

            case "ZombiePitCard":
                ZombiePitBuilding newZombiePit = new ZombiePitBuilding(new SimpleIntegerProperty(buildingNodeX),
                        new SimpleIntegerProperty(buildingNodeY));
                allBuildings.add(newZombiePit);
                zombiePits.add(newZombiePit);
                return newZombiePit;
        }
        return null;
    }

    public void destroyCard(int cardNodeX, int cardNodeY) {
        Card card = cardInventory.getCardByCoordinates(cardNodeX, cardNodeY);
        cardInventory.removeCardByObject(card);
    }

    // Checks whether the given slot is on the path
    public Boolean onPath(int x, int y) {
        Pair<Integer, Integer> givenSlot = new Pair<>(x, y);

        for (Pair<Integer, Integer> pathSquare : orderedPath) {
            if (pathSquare.equals(givenSlot)) {
                return true;
            }
        }

        return false;
    }

    // Checks whether the given slot is adjacent to the path
    public Boolean adjacentToPath(int x, int y) {
        Pair<Integer, Integer> givenSlot = new Pair<>(x, y);

        Boolean adjacentCheck = false;
        Pair<Integer, Integer> adjacent1 = new Pair<>(x, y + 1);
        Pair<Integer, Integer> adjacent2 = new Pair<>(x, y - 1);
        Pair<Integer, Integer> adjacent3 = new Pair<>(x + 1, y);
        Pair<Integer, Integer> adjacent6 = new Pair<>(x - 1, y);

        for (Pair<Integer, Integer> pathSquare : orderedPath) {
            if (pathSquare.equals(givenSlot)) {
                return false;
            } else if (pathSquare.equals(adjacent1) || pathSquare.equals(adjacent2) || pathSquare.equals(adjacent3)
                    || pathSquare.equals(adjacent6)) {
                adjacentCheck = true;
            }
        }

        return adjacentCheck;
    }

    // Checks whether the given slot is already occupied with a building
    public Boolean squareEmpty(int x, int y) {
        for (Building b : allBuildings) {
            if (b.getX() == x && b.getY() == y) {
                return false;
            }
        }
        return true;
    }

    public void destroyBuildingCoord(int x, int y) {
        Building destroyedBuilding = null;
        for (Building b : allBuildings) {
            if (!b.getType().equals("HerosCastle") && b.getX() == x && b.getY() == y) {
                destroyedBuilding = b;
            }
        }

        if (destroyedBuilding != null) {
            allBuildings.remove(destroyedBuilding);
            if (destroyedBuilding.getType().equals("ZombiePit")) {
                zombiePits.remove(destroyedBuilding);
            } else if (destroyedBuilding.getType().equals("VampireCastle")) {
                vampireCastles.remove(destroyedBuilding);
            } else if (destroyedBuilding.getType().equals("Barracks")) {
                allBarracks.remove(destroyedBuilding);
            }
            destroyedBuilding.destroy();
        }
    }

    public boolean isHerosCastleCoord(int x, int y) {
        if (herosCastle.getX() == x && herosCastle.getY() == y) {
            return true;
        }
        return false;
    }

    // private List<Building> allBuildings;
    // private List<ZombiePitBuilding> zombiePits;
    // private List<VampireCastleBuilding> vampireCastles;
    // private List<Building> allBarracks;
    // private HerosCastleBuilding herosCastle;

    // Checks whether the player is at the herosCastle
    public Boolean isPlayerAtHerosCastle() {
        if (herosCastle.entityOnBuilding(character)) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<Item> extraItem(Inventory usedUnequippedInventory, Inventory afterBuyInventory) {
        ArrayList<Item> result = new ArrayList<Item>();
        for (Item item : afterBuyInventory.getItems()) {
            if (usedUnequippedInventory.getItems().contains(item))
                continue;
            result.add(item);
        }
        return result;
    }

    /* GOALS */

    /**
     * Load goal sets the composite pattern goalNodes structure to correspond to the
     * json file input.
     * 
     * @param goal
     */
    public void loadGoal(GoalNode goalNodes) {
        this.goalNodes = goalNodes;
    }

    /**
     * Are goals satisfied checks whether the goals stored within goalNodes have
     * been satisfied. Returns true if satisfied.
     * 
     * @return
     */
    public boolean areGoalsSatisfied() {
        Goal goal = new Goal();
        goal.setBosses(bossDefeatCount);
        goal.setXp(character.getXp());
        goal.setCycles(loopCount);
        goal.setGold(character.getGold());
        return goalNodes.isSatisfied(goal);
    }

    public String getGoalString() {
        return goalNodes.prettyPrint();
    }

    public void incrementBossDefeatCount() {
        bossDefeatCount += 1;
    }

    /* RARE ITEMS */

    /**
     * Given a list of enabled rare items, goes through list of enemies and toggles
     * 
     * @param rareItemNames
     */
    public void loadRareItem(String rareItemName) {
        rareItemSettings.add(rareItemName);
    }

    /* STATS PANEL */
    public void newStatsText(String newText) {
        statsText.set("STATS\n_______________________________\n\n" + newText);
    }

    public void addStatsText(String newText) {
        String currText = statsText.get();
        statsText.set(currText + newText);
    }

    public SimpleStringProperty getStatsText() {
        return statsText;
    }

    public List<Pair<Integer, Integer>> getOrderedPath() {
        return orderedPath;
    }

    public void confuse() {
        this.confusing = true;
    }

}
