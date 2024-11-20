package braid.main;
/*******
 * Schnittstelle zwischen RewindController und GameObjects
 *******/

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class RewindableBody implements Rewindable{
    private final Body b2body;
    private Vector2 velocity = new Vector2(0, 0);

    public RewindableBody(Body b2body){ this.b2body = b2body; }

    @Override
    public Vector2 getPosition(){ return b2body.getPosition().cpy(); }

    @Override
    public Vector2 getVelocity(){ return b2body.getLinearVelocity().cpy(); }

    @Override
    public void setPosition(Vector2 position){ b2body.setTransform(position, b2body.getAngle()); }

    @Override
    public void setVelocity(Vector2 velocity){ b2body.setLinearVelocity(velocity); }

}
