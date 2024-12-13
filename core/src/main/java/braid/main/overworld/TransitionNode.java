package braid.main.overworld;

import braid.main.Braid;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;

public class TransitionNode extends OverworldNode {
    String name;

    String neighborA, neighborB;


    public TransitionNode(RectangleMapObject base) {
        name = base.getName().toUpperCase();

        position = new Vector2(base.getRectangle().getX() /Braid.PPM, base.getRectangle().getY() /Braid.PPM);

        MapObject temp = (MapObject) base.getProperties().get("neighborA");
        neighborA = temp.getName().toUpperCase();
        temp = (MapObject) base.getProperties().get("neighborB");
        neighborB = temp.getName().toUpperCase();
    }

    public String getName() { return name; }

    public String getNeighborA() { return neighborA; }
    public String getNeighborB() { return neighborB; }
}
