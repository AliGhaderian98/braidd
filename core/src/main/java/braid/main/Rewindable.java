package braid.main;
import com.badlogic.gdx.math.Vector2;
public interface Rewindable {
    Vector2 getPosition();
    Vector2 getVelocity();
    void setPosition(Vector2 position);
    void setVelocity(Vector2 velocity);
}
