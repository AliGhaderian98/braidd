package braid.main.objects;

import braid.main.Braid;
import braid.main.tools.KeyBindings;
import braid.main.tools.UserData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Brick extends InteractiveGameObject {

    public enum AnimationState {
        UNDAMAGED, BROKEN
    }

    private Brick.AnimationState currentState = Brick.AnimationState.UNDAMAGED;
    private final boolean rewindable;
    private final Sprite sprite;


    public Brick(World world, Rectangle boundary, TextureRegion region, boolean rewindable) {
        super(world, boundary, false);
        this.rewindable = rewindable;

        sprite = new Sprite(region);
        sprite.setBounds(0, 0,
            sprite.getRegionWidth() / Braid.PPM,
            sprite.getRegionHeight() / Braid.PPM);
        sprite.setPosition(b2body.getPosition().x - sprite.getWidth() / 2, b2body.getPosition().y - sprite.getHeight() / 2);

        fixture.setUserData(new UserData("Brick", this));
    }

    public void update(float dt) {
        if (currentState == Brick.AnimationState.BROKEN) {
            b2body.setActive(false);
        } else {
            b2body.setActive(true);
        }
    }


    public void use() {
        currentState = AnimationState.BROKEN;
    }

    @Override
    public void defineBody() {}

    public void draw(Batch batch) {
        if (currentState == Brick.AnimationState.UNDAMAGED)
            sprite.draw(batch);
    }

    public boolean isBroken() {
        return currentState == AnimationState.BROKEN;
    }

    public boolean isRewindable() { return rewindable; }

    public Sprite getSprite() { return sprite; }

    @Override
    public Object getCurrentState() {
        if (isBroken()) {
            return Brick.AnimationState.BROKEN;
        } else {
            return Brick.AnimationState.UNDAMAGED;
        }
    }

    @Override
    public void setCurrentState(Object animationStates) {
        if (animationStates instanceof Brick.AnimationState) {
            this.currentState = (Brick.AnimationState) animationStates;
        }
    }

}
