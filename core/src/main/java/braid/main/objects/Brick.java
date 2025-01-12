package braid.main.objects;

import braid.main.tools.KeyBindings;
import braid.main.tools.UserData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;

public class Brick extends InteractiveGameObject {
    private boolean breakable = false;

    public Brick(World world, TiledMap map, Rectangle boundary) {
        super(world,map,boundary, true);
        fixture.setUserData(new UserData("Brick", this));
    }

    public void update(float dt) {
        if (Gdx.input.isKeyJustPressed(KeyBindings.getKey("HAMMER")))
            disappear();
    }

    public void disappear () {
        if (isBreakable()) {
            world.destroyBody(b2body);
        }
    }
    @Override
    public void defineBody() {

    }

    public boolean isBreakable() {
        return breakable;
    }

    public void setBreakable(boolean breakable) {
        this.breakable = breakable;
    }
}
