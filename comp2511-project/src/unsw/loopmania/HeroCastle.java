package unsw.loopmania;

import javafx.beans.property.SimpleIntegerProperty;

// enum HEROCASTLE_ITEM {
//     SWORD,
//     HELMET,
//     STAKE,
//     ARMOUR,
//     POTION,
//     STAFF;

//     public int getValue(HEROCASTLE_ITEM item) {
//         switch (item) {
//             case SWORD:
//                 return 50;
//             case HELMET:
//                 return 50;
//             case STAFF:
//                 return 50;
//             case STAKE:
//                 return 50;
//             case POTION:
//                 return 50;
//             case ARMOUR:
//                 return 50;
//             default:
//                 return 10000000;
//         }
//     }

// 	public Item getItem(HEROCASTLE_ITEM item) {
// 		switch (item) {
//             case SWORD:
//                 Item woodenSword = new Sword(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
//                 return woodenSword;
//             case HELMET:
//                 return 50;
//             case STAFF:
//                 return 50;
//             case STAKE:
//                 return 50;
//             case POTION:
//                 return 50;
//             case ARMOUR:
//                 return 50;
//             default:
//                 return 10000000;
//         }
// 	}
// }
public class HeroCastle extends Building {
    // private Inventory castleInventory = new Inventory(3,2);
    private Item woodenSword;
    private Item metalHelmet;
    private Item stake;
    private Item staff;
    private Item armour;
    private Item potion;
    private Item shield;
    private Item luckyCover;

    public HeroCastle() {
        super(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
        // add all equipments into the list
        // now assume there is only one equipments
        woodenSword = new Sword(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
        metalHelmet = new Helmet(new SimpleIntegerProperty(0), new SimpleIntegerProperty(1));
        stake = new Stake(new SimpleIntegerProperty(0), new SimpleIntegerProperty(2));
        staff = new Staff(new SimpleIntegerProperty(1), new SimpleIntegerProperty(0));
        armour = new Armour(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        potion = new HealthPotion(new SimpleIntegerProperty(1), new SimpleIntegerProperty(2));
        shield = new Shield(new SimpleIntegerProperty(2), new SimpleIntegerProperty(0));
        luckyCover = new LuckyClover(new SimpleIntegerProperty(2), new SimpleIntegerProperty(1));
        // castleInventory.addItem(woodenSword);
        // castleInventory.addItem(metalHelmet);
        // castleInventory.addItem(staff);
        // castleInventory.addItem(stake);
        // castleInventory.addItem(armour);
        // castleInventory.addItem(potion);
    }

    // public Inventory getcastleInventory() {
    // return castleInventory;
    // }

    public Item loadItem(String type) {
        switch (type.toLowerCase()) {
            case "sword":
                return woodenSword;
            case "helmet":
                return metalHelmet;
            case "stake":
                return stake;
            case "staff":
                return staff;
            case "armour":
                return armour;
            case "healthpotion":
                return potion;
            case "shield":
                return shield;
            case "luckycover":
                return luckyCover;
            default:
                return null;
        }
    }
}
