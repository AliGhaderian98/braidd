package braid.main.objects;


import braid.main.Braid;
import braid.main.tools.UserData;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;

import java.awt.*;

public class GoalMovingPlatform extends MovingPlatform {
    public enum AnimationState {
        LEFT, RIGHT
    }

    private MovingPlatform.AnimationState currentState = MovingPlatform.AnimationState.RIGHT;
    private final boolean rewindable;
    private final int goalPosX;
    private final int goalPosY;
    private final int schalterPosX;
    private final int schalterPosY;
    private final float speed;
    private final Sprite sprite;
    private boolean isActive = true;
    private Player player;

    public GoalMovingPlatform(World world, TextureRegion region, Rectangle boundary, float speed,
                              int schalterPosX, int schalterPosY, int goalPosX, int goalPosY, boolean rewindable) {
        super(world, region, boundary, 0, speed, rewindable);
        this.goalPosX = goalPosX;
        this.goalPosY = goalPosY;
        this.schalterPosX = schalterPosX;
        this.schalterPosY = schalterPosY;
        this.rewindable = rewindable;
        this.speed = speed;


        sprite = new Sprite(region);
        sprite.setBounds(0,0,region.getRegionWidth()/2f/ Braid.PPM, region.getRegionHeight()/2f/Braid.PPM);
        sprite.setPosition(b2body.getPosition().x-sprite.getWidth()/2, b2body.getPosition().y-sprite.getHeight()/2);

        fixture.setUserData(new UserData("GoalMovingPlatform", this));

    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

   /* @Override
    public void update(float dt) {
        super.update(dt);

        if (b2body.getPosition().x >= goalPosX && b2body.getPosition().y >= goalPosY) {
            setActive(false);
        }
    }*/

    public void update(float dt) {

        if (!isActive) {
            b2body.setLinearVelocity(0, 0);
            return;
        }

        // determine movement direction
       /* float currentX = b2body.getPosition().x;
        if (movingRight() && currentX > startX + rangeX) {
            currentState = MovingPlatform.AnimationState.LEFT;
        } else if (!movingRight() && currentX < startX - rangeX) {
            currentState = MovingPlatform.AnimationState.RIGHT;
        }*/

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
