package braid.main;
import com.badlogic.gdx.math.Vector2;
//Stellt Methoden fÃ¼r Rewindbare (bis jetzt nur Sprites) dar (Command-Klasse bzw Interface)
public interface Rewindable {
    Vector2 getPosition();
    Vector2 getVelocity();
    void setPosition(Vector2 position);
    void setVelocity(Vector2 velocity);
}
