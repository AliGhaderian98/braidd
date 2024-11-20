package braid.main;
//Getter/Setter-Klasse zum Sprites Rewinden (Schnittstelle fÃ¼r Sprite und Rewindable (Receiver1?))
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class RewindableSprite implements Rewindable{
    private final Sprite sprite;
    private Vector2 velocity = new Vector2(0, 0);
    public RewindableSprite(Sprite sprite){
        this.sprite = sprite;
    }

    @Override
    public Vector2 getPosition(){ return new Vector2(sprite.getX(), sprite.getY()); }

    @Override
    public Vector2 getVelocity(){ return velocity; }

    @Override
    public void setPosition(Vector2 position){ sprite.setPosition(position.x, position.y); }

    @Override
    public void setVelocity(Vector2 velocity){ this.velocity = velocity; }

}
