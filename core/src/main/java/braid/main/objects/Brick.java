package braid.main.objects;

import braid.main.Braid;
import braid.main.tools.KeyBindings;
import braid.main.tools.UserData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;

public class Brick extends InteractiveGameObject {
    private final Sprite sprite;
    private boolean breakable = false;
    private TextureRegion region;

    public Brick(World world, TiledMap map, Rectangle boundary, boolean isSensor, TextureRegion region) {
        super(world,map, boundary, true);
        this.region = region;

        sprite = new Sprite(region);
        sprite.setBounds(0,0,
            sprite.getRegionWidth()/ Braid.PPM,
            sprite.getRegionHeight()/Braid.PPM);
        sprite.setPosition(b2body.getPosition().x-sprite.getWidth()/2, b2body.getPosition().y-sprite.getHeight()/2);



        fixture.setUserData(new UserData("Brick", this));


    }

    public void update(float dt) {
    //    if (Gdx.input.isKeyJustPressed(KeyBindings.getKey("HAMMER")))
            disappear();
    }

    public void disappear () {
        if (isBreakable()) {
  //          world.destroyBody(b2body);
        }
    }
    @Override
    public void defineBody() {

    }
    public void draw(Batch batch) {
        sprite.draw(batch);
    }

    public boolean isBreakable() {
        return breakable;
    }

    public void setBreakable(boolean breakable) {
        this.breakable = breakable;
    }
}
