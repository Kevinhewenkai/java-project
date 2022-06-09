package unsw.loopmania;

import javafx.beans.property.SimpleIntegerProperty;

public class MapItem extends Entity {

    private PathPosition position;
    private String type;

    public MapItem(PathPosition position, String type) {
        super();
        this.position = position;
        this.type = type;
    }

    public SimpleIntegerProperty x() {
        return position.getX();
    }

    public SimpleIntegerProperty y() {
        return position.getY();
    }

    public int getX() {
        return x().get();
    }

    public int getY() {
        return y().get();
    }

    public PathPosition getPosition() {
        return position;
    }

    public String getType() {
        return type;
    }

}
