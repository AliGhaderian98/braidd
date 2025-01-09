package braid.main.objects;

import braid.main.tools.UserData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;

public class Brick extends InteractiveGameObject {
    public Brick(World world, TiledMap map, Rectangle boundary) {
        super(world,map,boundary, true);
        fixture.setUserData(new UserData("Brick", this));
    }

    public void disappear () {

    }
    @Override
    public void defineBody() {

    }
}
