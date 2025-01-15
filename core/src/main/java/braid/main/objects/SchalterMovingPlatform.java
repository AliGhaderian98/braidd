package braid.main.objects;


import braid.main.Braid;
import braid.main.screens.levels.LevelScreen;
import braid.main.tools.UserData;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class SchalterMovingPlatform extends MovingPlatform {

    public enum AnimationState {
        WAITING, TOGOAL, TOORIGIN
    }

    private SchalterMovingPlatform.AnimationState currentState = AnimationState.WAITING;


    public SchalterMovingPlatform(LevelScreen screen, World world, TextureRegion region, Rectangle boundary, float speed,
                                  float schalterPosX, float schalterPosY, float goalPosX, float goalPosY, boolean rewindable) {
        super(world, region, boundary, 0, speed, rewindable);

        goalPos = new Vector2(goalPosX/Braid.PPM, goalPosY/Braid.PPM);

        fixture.setUserData(new UserData("GoalMovingPlatform", this));

        Rectangle rect = new Rectangle(schalterPosX, schalterPosY, 10,10);
        screen.addSchalter(new Schalter(world, screen.getAtlas().findRegion("schalter"), rect, this));
    }

    public void setActive() {
        if (!isActive()) {
            if (b2body.getPosition().dst2(goalPos) > b2body.getPosition().dst2(originPos))
                currentState = AnimationState.TOGOAL;
            else
                currentState = AnimationState.TOORIGIN;
        }
    }

    private boolean isActive() { return currentState != AnimationState.WAITING; }

    @Override
    public void update(float dt) {
        if (!isActive()) {
            b2body.setLinearVelocity(0, 0);
            return;
        }

        Vector2 goal = movingToGoal() ? goalPos : originPos;

        Vector2 distance = new Vector2(goal).sub(b2body.getPosition());

        // If the distance is very small, stop moving
        if (distance.len2() < 0.001f) {
            b2body.setLinearVelocity(0, 0);
            currentState = AnimationState.WAITING;
            return;
        }

        // Adjust velocity based on the remaining distance to avoid overshooting
        Vector2 limitedVelocity = distance.nor().scl(Math.min(speed, distance.len() / dt));

        // Apply the calculated velocity to the body
        b2body.setLinearVelocity(limitedVelocity);

        sprite.setPosition(b2body.getPosition().x-sprite.getWidth()/2, b2body.getPosition().y-sprite.getHeight()/2);

        giveVelocityToPlayer();
    }

    private boolean movingToGoal() { return currentState == AnimationState.TOGOAL; }

    @Override
    public Object getCurrentState() {
        return currentState;
    }

    @Override
    public void setCurrentState(Object animationStates) {
        if (animationStates instanceof SchalterMovingPlatform.AnimationState) {
            this.currentState = (SchalterMovingPlatform.AnimationState) animationStates;
        }
    }


}
