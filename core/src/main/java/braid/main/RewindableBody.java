package braid.main;
/*******
 * Schnittstelle zwischen RewindController und GameObjects
 *******/

import braid.main.objects.DynamicGameObject;
import braid.main.objects.GameObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class RewindableBody implements Rewindable{
    private final Body b2body;
    private final DynamicGameObject gameObject;

    public RewindableBody(Body b2body, DynamicGameObject gameObject){
        this.b2body = b2body;
        this.gameObject = gameObject;
    }

    @Override
    public Vector2 getPosition(){ return b2body.getPosition().cpy(); }

    @Override
    public Vector2 getVelocity(){ return b2body.getLinearVelocity().cpy(); }

    @Override
    public float getStateTimer() { return gameObject.getStateTimer(); }

    @Override
    public Object getCurrentState() { return gameObject.getCurrentState(); }

    @Override
    public void setPosition(Vector2 position){ b2body.setTransform(position, b2body.getAngle()); }

    @Override
    public void setVelocity(Vector2 velocity){ b2body.setLinearVelocity(velocity); }

    @Override
    public void setStateTimer(float stateTimer) { gameObject.setStateTimer(stateTimer); }

    @Override
    public void setCurrentState(Object animationState) { gameObject.setCurrentState(animationState); }
}
