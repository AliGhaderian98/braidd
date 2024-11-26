package braid.main;
/*******
 * Schnittstelle zwischen RewindController und GameObjects
 *******/

import braid.main.objects.GameObject;
import braid.main.objects.Player;
import braid.main.objects.Player.AnimationState;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class RewindableBody implements Rewindable{
    private final Body b2body;
    private final Player player;

    public RewindableBody(Body b2body, Player player){
        this.b2body = b2body;
        this.player = player;
    }

    @Override
    public Vector2 getPosition(){ return b2body.getPosition().cpy(); }

    @Override
    public Vector2 getVelocity(){ return b2body.getLinearVelocity().cpy(); }

    @Override
    public float getStateTimer() { return player.getStateTimer(); }

    @Override
    public AnimationState getCurrentState() { return player.getCurrentState(); }

    @Override
    public void setPosition(Vector2 position){ b2body.setTransform(position, b2body.getAngle()); }

    @Override
    public void setVelocity(Vector2 velocity){ b2body.setLinearVelocity(velocity); }

    @Override
    public void setStateTimer(float stateTimer) { player.setStateTimer(stateTimer); }

    @Override
    public void setCurrentState(AnimationState animationState) { player.setCurrentState(animationState); }
}
