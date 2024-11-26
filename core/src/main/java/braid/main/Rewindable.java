package braid.main;
import braid.main.objects.Player.AnimationState;
import com.badlogic.gdx.math.Vector2;
public interface Rewindable {
    Vector2 getPosition();
    Vector2 getVelocity();
    float getStateTimer();
    Object getCurrentState();
    void setPosition(Vector2 position);
    void setVelocity(Vector2 velocity);
    void setStateTimer(float stateTimer);
    void setCurrentState(Object animationState);
}

