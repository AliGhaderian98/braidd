package braid.main.objects;

import braid.main.tools.UserData;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;

public class End extends InteractiveGameObject {
    public End(World world, Rectangle boundary) {
        super(world, boundary, true);
        fixture.setUserData(new UserData("Ladder", this));
    }

    @Override
    public void defineBody() {

    }
}
