package braid.main.objects;

import braid.main.Braid;
import braid.main.tools.UserData;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class MovingPlatform extends InteractiveGameObject {
    private enum Type {
        RANGE, GOAL
    }

    public enum AnimationState {
        LEFT, RIGHT
    }

    private MovingPlatform.AnimationState currentState = MovingPlatform.AnimationState.RIGHT;

    protected final boolean rewindable;
    protected final float speed;
    protected final Sprite sprite;
    private final Type type;

    private final float rangeX;
    protected final Vector2 originPos;
    protected Vector2 goalPos;

    private Player player;

    public MovingPlatform(World world, TextureRegion region, Rectangle boundary, float rangeX, float speed, boolean rewindable) {
        super(world, boundary, region,false);
        this.rewindable = rewindable;
        this.rangeX = rangeX / 2;
        this.speed = speed;

        type = Type.RANGE;
        originPos = new Vector2(b2body.getPosition().cpy());

        sprite = new Sprite(region);
        sprite.setBounds(0,0,region.getRegionWidth()/ Braid.PPM, region.getRegionHeight()/Braid.PPM);
        sprite.setPosition(b2body.getPosition().x-sprite.getWidth()/2, b2body.getPosition().y-sprite.getHeight()/2);

        fixture.setUserData(new UserData("MovingPlatform", this));
    }

    public MovingPlatform(World world, TextureRegion region, Rectangle boundary, float goalPosX, float goalPosY, float speed, boolean rewindable) {
        super(world, boundary, region,false);
        this.rewindable = rewindable;
        this.speed = speed;

        type = Type.GOAL;
        rangeX = 0;
        originPos = new Vector2(b2body.getPosition().cpy());
        goalPos = new Vector2(goalPosX/Braid.PPM, goalPosY/Braid.PPM);

        sprite = new Sprite(region);
        sprite.setBounds(0,0,region.getRegionWidth()/ Braid.PPM, region.getRegionHeight()/Braid.PPM);
        sprite.setPosition(b2body.getPosition().x-sprite.getWidth()/2, b2body.getPosition().y-sprite.getHeight()/2);

        fixture.setUserData(new UserData("MovingPlatform", this));
    }


    @Override
    public void defineBody() {}

    public void update(float dt) {
        if (type == Type.RANGE)
            setVelocityRangeType(dt);
        else
            setVelocityGoalType(dt);

        sprite.setPosition(b2body.getPosition().x-sprite.getWidth()/2, b2body.getPosition().y-sprite.getHeight()/2);

        giveVelocityToPlayer();
    }

    private void setVelocityRangeType(float dt) {
        // determine movement direction
        float currentX = b2body.getPosition().x;
        if (movingRight() && currentX > originPos.x + rangeX) {
            currentState = MovingPlatform.AnimationState.LEFT;
        } else if (!movingRight() && currentX < originPos.x - rangeX) {
            currentState = MovingPlatform.AnimationState.RIGHT;
        }

        float velocityX = movingRight() ? speed : -speed;
        b2body.setLinearVelocity(velocityX*0.005f, 0);
    }

    private void setVelocityGoalType(float dt) {
        Vector2 goal = movingRight() ? goalPos : originPos;
        Vector2 distance = new Vector2(goal).sub(b2body.getPosition());

        // If the distance is very small, change movement direction
        if (distance.len2() < 0.001f) {
            if (currentState == AnimationState.LEFT)
                currentState = AnimationState.RIGHT;
            else
                currentState = AnimationState.LEFT;
        }

        // Adjust velocity based on the remaining distance to avoid overshooting
        Vector2 limitedVelocity = distance.nor().scl(Math.min(speed, distance.len() / dt));
        b2body.setLinearVelocity(limitedVelocity);
    }

    // give this platform's velocity over to player to ensure player stays moving with platform
    protected void giveVelocityToPlayer() {
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

    public Sprite getSprite() { return sprite; }
}
