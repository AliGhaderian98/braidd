package braid.main.objects;

import braid.main.Braid;
import braid.main.tools.UserData;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class MovingPlatform extends InteractiveGameObject {

    public enum AnimationState {
        LEFT, RIGHT
    }

    private MovingPlatform.AnimationState currentState = MovingPlatform.AnimationState.RIGHT;

    private final boolean rewindable;
    private final float rangeX;
    private final float speed;
    private final float startX;
    private final Sprite sprite;
    private Player player;

    public MovingPlatform(World world, TextureRegion region, Rectangle boundary, float rangeX, float speed, boolean rewindable) {
        super(world, boundary, region,false);
        this.rewindable = rewindable;
        this.rangeX = rangeX / 2;
        this.speed = speed;
        this.startX = b2body.getPosition().x;

        sprite = new Sprite(region);
        sprite.setBounds(0,0,region.getRegionWidth()/2f/ Braid.PPM, region.getRegionHeight()/2f/Braid.PPM);
        sprite.setPosition(b2body.getPosition().x-sprite.getWidth()/2, b2body.getPosition().y-sprite.getHeight()/2);

        fixture.setUserData(new UserData("MovingPlatform", this));
    }

    @Override
    public void defineBody() {}

    public void update(float dt) {
        // determine movement direction
        float currentX = b2body.getPosition().x;
        if (movingRight() && currentX > startX + rangeX) {
            currentState = MovingPlatform.AnimationState.LEFT;
        } else if (!movingRight() && currentX < startX - rangeX) {
            currentState = MovingPlatform.AnimationState.RIGHT;
        }

        // update speed and sprite position
        float velocityX = movingRight() ? speed : -speed;
        b2body.setLinearVelocity(velocityX*0.005f, 0);
        sprite.setPosition(b2body.getPosition().x-sprite.getWidth()/2, b2body.getPosition().y-sprite.getHeight()/2);

        // give this platform's velocity over to player to ensure player stays moving with platform
        if (player != null) {
            if (player.getPlatformVelocity() != b2body.getLinearVelocity().x)
                player.setPlatformVelocity(b2body.getLinearVelocity().x);
        }
    }

    public void setPlayer(Player p) {
        player = p;
    }

    public void draw(Batch batch) {
        sprite.draw(batch);
    }

    private boolean movingRight() { return currentState == AnimationState.RIGHT; }

    @Override
    public Object getCurrentState() {
        if (movingRight())
            return MovingPlatform.AnimationState.RIGHT;
        else
            return MovingPlatform.AnimationState.LEFT;
    }

    @Override
    public void setCurrentState(Object animationStates) {
        if (animationStates instanceof MovingPlatform.AnimationState) {
            this.currentState = (MovingPlatform.AnimationState) animationStates;
        }
    }

    public boolean isRewindable() { return rewindable; }
}
