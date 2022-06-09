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




public class AllyTest {
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
    @Test
    public void getStatsTest() {

        Stats stats = new Stats();
        stats.setHp(10);
        stats.setDamage(10);
        Ally ally = new Ally(pp, stats);

        Stats stats2 = new Stats();
        stats2.setHp(10);
        stats2.setDamage(10);

        assertTrue(ally.getStats().getHp() == stats2.getHp());
    }
}
