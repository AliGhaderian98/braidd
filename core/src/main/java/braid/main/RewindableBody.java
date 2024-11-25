package braid.main;
/*******
 * Schnittstelle zwischen RewindController und GameObjects
 *******/

import braid.main.objects.GameObject;
import braid.main.objects.Player.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class RewindableBody implements Rewindable{
    private final Body b2body;
    private float stateTimer;
    private AnimationState currentState;

    public RewindableBody(Body b2body){
        this.b2body = b2body;
        this.currentState = AnimationState.IDLE;
        this.stateTimer = 0;
    }

    @Override
    public Vector2 getPosition(){ return b2body.getPosition().cpy(); }

    @Override
    public Vector2 getVelocity(){ return b2body.getLinearVelocity().cpy(); }

    @Override
    public float getStateTimer() {
        return stateTimer; // >= 0 ? stateTimer : 0;
    }

    @Override
    public AnimationState getCurrentState() { //Die nervigste Getter-Methode, verantwortlich gewesen für 90% der Fehler:)
        return currentState;     //!= null ? currentState : AnimationState.IDLE;
    }

    //@Override
    //public float getStateTimer(){ return this.stateTimer; }

    @Override
    public void setPosition(Vector2 position){ b2body.setTransform(position, b2body.getAngle()); }

    @Override
    public void setVelocity(Vector2 velocity){ b2body.setLinearVelocity(velocity); }

    @Override
    public void setStateTimer(float stateTimer) { //todo: funktioniert das überhaupt?, siehe Print-Ausgaben
        this.stateTimer = stateTimer;
    }

    @Override
    public void setCurrentState(AnimationState animationState) { //Die nervigste Setter-Methode, auch verantwortlich gewesen für 90% der Fehler:)
        this.currentState = animationState;
    }
}
