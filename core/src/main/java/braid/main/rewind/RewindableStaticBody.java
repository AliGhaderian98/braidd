package braid.main.rewind;
/*******
 * Schnittstelle zwischen RewindController und GameObjects
 *******/

import braid.main.objects.DynamicGameObject;
import braid.main.objects.InteractiveGameObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class RewindableStaticBody implements Rewindable{
    private final Body b2body;
    private final InteractiveGameObject gameObject;

    public RewindableStaticBody(Body b2body, InteractiveGameObject gameObject){
        this.b2body = b2body;
        this.gameObject = gameObject;
    }

    @Override
    public Vector2 getPosition(){ return b2body.getPosition().cpy(); }

    @Override
    public Vector2 getVelocity(){ return b2body.getLinearVelocity().cpy(); }

    @Override
    public float getStateTimer() {
        return 0;
    }

    @Override
    public Object getCurrentState() {
        return null;
    }


    @Override
    public void setPosition(Vector2 position){ b2body.setTransform(position, b2body.getAngle()); }

    @Override
    public void setVelocity(Vector2 velocity){ b2body.setLinearVelocity(velocity); }

    @Override
    public void setStateTimer(float stateTimer) {

    }

    @Override
    public void setCurrentState(Object animationState) {

    }
}
