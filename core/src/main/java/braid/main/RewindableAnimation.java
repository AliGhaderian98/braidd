package braid.main;

import braid.main.objects.Player.*;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class RewindableAnimation{
    private Animation<TextureRegion> animation;
    private float stateTimer;
    private boolean isRewinding;

    public RewindableAnimation(Animation<TextureRegion> animation){
        this.animation = animation;
        this.stateTimer = 0;
        this.isRewinding = false;
    }

    public TextureRegion getKeyFrame(float stateTimer, boolean isRewinding){
        return animation.getKeyFrame(stateTimer, !isRewinding);
    }

    public void updateStateTimer(float dt){
        stateTimer += isRewinding ? -dt : dt;
        stateTimer = Math.max(stateTimer, 0);
    }

    public void setRewinding(boolean isRewinding){
        this.isRewinding = isRewinding;
    }

    public float getStateTimer(){
        return stateTimer;
    }

    public void setStateTimer(float stateTimer){
        this.stateTimer = stateTimer;
    }
}
