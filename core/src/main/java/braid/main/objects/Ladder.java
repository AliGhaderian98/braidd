package braid.main.objects;

import braid.main.Braid;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Ladder extends InteractiveObject{

    public Ladder(World world, TiledMap map, Rectangle boundary) {
        super(world,map,boundary, true);
        fixture.setUserData(this);
    }

}
