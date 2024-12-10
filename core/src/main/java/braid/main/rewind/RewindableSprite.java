/*package braid.main;
/*******
 * Schnittstelle zwischen RewindController und Sprites
 *******
import braid.main.objects.Player;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class RewindableSprite implements Rewindable{
    private final Sprite sprite;
    private Vector2 velocity = new Vector2(0, 0);
    private float stateTimer;

    public RewindableSprite(Sprite sprite){
        this.sprite = sprite;
    }

    @Override
    public Vector2 getPosition(){ return new Vector2(sprite.getX(), sprite.getY()); }

    @Override
    public Vector2 getVelocity(){ return velocity; }

    @Override
    public float getStateTimer(){ return this.stateTimer; }

    @Override
    public Player.AnimationState getCurrentState() {
        return null;
    }

    @Override
    public void setPosition(Vector2 position){ sprite.setPosition(position.x, position.y); }

    @Override
    public void setVelocity(Vector2 velocity){ this.velocity = velocity; }

    @Override
    public void setStateTimer(float stateTimer){ this.stateTimer = stateTimer; }

    @Override
    public void setCurrentState(Player.AnimationState animationState) {

    }

}
*/
