package braid.main.objects;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;

public class Ladder extends InteractiveGameObject{
    public Ladder(World world, TiledMap map, Rectangle boundary) {
        super(world,map,boundary, true);
        fixture.setUserData(this);
    }

    @Override
    public void defineBody() {

    }
}
