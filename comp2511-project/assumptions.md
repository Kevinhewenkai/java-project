### Definitions

- GAME WORLD: the panel of UI in the *Game* scene which is home to the character and looping path, able to spawn enemies, cards, and items, etc.

- ITEM SLOT: a square UI element where a single item can be stowed
- EQUIPPED ITEM INVENTORY: the grid of 6 item slots available for equipping: 1x helmet item, 1 x armour item, 1x weapon item, 1x shield item, and 2x rare items
- UNEQUIPPED ITEM INVENTORY: the grid of item slots for storing items which are not currently equipped

- CARD SLOT: a square UI element where a single card can be stowed

- STATS PANEL: a block UI element found in the *Game* and *Hero's Castle Store* scenes where text is printed based on the status of the game, fights, and/or the position of the cursor over items/cards/enemies.

- HERO'S CASTLE: the building card which is found in the Game World, where the character spawns.

- HERO'S CASTLE *STORE*: the UI window (*'scene'*) which can be opened at the start of each new round, gaining the human player the ability to purchase and sell items.



# Assumptions


**GAME MODES**
- The player can only play in a single game mode at a time.
- Game modes cannot be changed during the game.
- Game modes cannot be saved and resumed other than when the character visits the *Hero's Castle Store*.
- Game Modes influence the behaviour of the Hero's Castle Store scene, the winning goal, as well as the starting HP of the character.


**ROUNDS AND LOOPS**
- The game is automatically paused each time the character returns to the Hero's Castle (i.e. at the start of each new *loop*)
- The Hero's Castle *Store* is only made available to the human player at the start of each new round.


**ITEM INVENTORY**
- There is a fixed, limited number of equipped and unequipped item slots.
- Only a single item can be placed in each unequipped/equipped item slot.
- When all unequipped item slots are occupied, a first-in first-out replacement method is used.
- Items that are destroyed (either as a result of the first-in first-out replacement method, or if they're dragged to the bin) do not return any gold for being destroyed.


**RARE ITEMS**
- Rare items are equipped by dragging and dropping them into an equipped item slot.
- The rare item's effect is only registered for the character once it is equipped.
- Once rare items are equipped, they cannot be unequipped.
- Rare items can, however, be *replaced* by other rare items if another rare item is dragged-and-dropped over a currently equipped rare item (thus destroying the rare item which was originally equipped).
- Each enemy has the chance of dropping a particular rare item when killed, with varying drop rates for each enemy.


**CARDS**
- There is a fixed, limited number of card slots.
- When all slots are occupied, a first-in first-out replacement method is used.
- Building cards can be placed in the Game World independent of whether the game is paused or running.


**NICHE ITEM BEHAVIOURS**
- Potions are used by pressing `Enter`, and are used in the order in which they have been collected (i.e. the first collected potion is used first).
- When the 'One Ring' rare item is equipped, the character will respawn at the place they died, and the status of the enemy doesn't change.
- Staff can only trance one enemy at a time. 
- Staff can trance all enemies (excluding Bosses). 
- Staff trance turns enemy into full health ally soldier.


**HERO'S CASTLE STORE**
- Multiple of the same item can be purchased from the store (except where game mode specifications detail otherwise).
- The human player can sell as many items as they like from the store.
- All functionality of the store (e.g. the ability to sell items for gold) is present everytime it's made available to the human player.
- All items are available for sale everytime the store is made available to the human player.


**CHARACTER**
- The character starts with full HP (value dependent on the Game Mode).
- Once the character has 0 HP (and there are no One Rings currently equipped), the game is immediately lost.


**FIGHTS**
- Fight radius is calculated as per the euclidean range inclusive of the number.
- Fights occur instantaneously, and the details of the fight (the order in which characters/soldiers/enemies hit, the points in the fight at which enemies have died, etc.) are displayed in the *'STATS'* UI element.
- Equipment cannot be changed during a fight (since the fights are instantaneous).
- The game cannot be paused during a fight (since the fights are are instantaneous).
- The character and enemy "team's" will fight with stats combined.
- Combined stats will involve addition of damage, hp and defence stats, and the highest speed of the enemy and character party.
- Enemies will target allied soldiers first.
- Allies will be attacked in the chronological order (i.e. order in which they were added to character's party).
- If the character enters the battle radius of two or more enemies, then any enemies mutually in the support radius of all enemies, will join in the fight with first enemy.
- Critical hits cannot be stacked. Critical hits are only applied by the main enemy i.e. extra damage and chance is calculated from main enemy stats.


**ENEMIES**
- The character obtains a specified amount of gold for each enemy killed.
- Enemies have the chance of dropping items (including rare items) and cards when killed.
- Enemies will drop gold and xp when killed by tower or trap, as well as in combat.
- Enemies spawned from a building will be placed on the first adjacent block in the clockwise direction to their corresponding card.


**MOVEMENT TYPES AND MOVEMENT SPEED**
- The character moves one tile per tick, in a clockwise direction, down the path.
- Slugs move in a randomised fashion, with a 33(1/3)% chance of not moving, and a 33(1/3)% chance of moving up or down a tile (per tick).
- Zombies move in a randomised fashion, with a 50% chance of not moving, and a 25% chance of moving up or down a tile (per tick).
- Vampires start by travelling in the counterclockwise direction (one tile per tick), but when in the effect radius of a campfire, they reverse direction and move clockwise (initially) or in the opposite direction to which they entered the campfire radius, until outside of radius, where the vampire continues in the direction which they reversed to.
- Doggie moves in an randomised fashion. Doggie moves in straight lines up and down path, randomly calcualted between 0 and 9.
- Elan Muske moves slowly. 10% of moving up, 10% chance of moving down, and 80% chance of not moving (per tick).



**ALLIED SOLDIERS**
- Allied soldiers can't equip items, but are still affected by rare items which the character has equipped.
- When an allied soldier is killed during a fight, it is turned into a zombie and instantly starts fighting with the other enemies against the character.
- When the soldier is spawned from barrack, the soldier will immediately join character.
