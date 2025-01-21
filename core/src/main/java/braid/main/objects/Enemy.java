package braid.main.objects;

import braid.main.screens.levels.LevelScreen;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;

import java.util.logging.Level;


/***********
 Diese Klasse soll einen ersten spezifischen Entwurf für einen Gegner darstellen
 und später generalisert werden, sodass aus ihr verschiedene Arten an Gegnern
 erstellt werden können.
 ***********/

public abstract class Enemy extends DynamicGameObject{
    private final float x,y;
    private final boolean rewindable;
    protected final String type;
    private float stateTimer = 0;

    public enum AnimationState {
        DEAD,
        ALIVE;
    }

    //private final TextureRegion stand;
    private AnimationState currentState = AnimationState.ALIVE;
    protected Animation<TextureRegion> idle, walking, running, attacking;

    // Konstruktor für die Initialisierung des Gegners
    public Enemy(World world, LevelScreen screen, float x, float y, boolean rewindable, String type) {
        super(world);
        this.x = x;
        this.y = y;
        this.rewindable = rewindable;
        this.type = type;
    }

    // Überschreiben der `act()`-Methode, um die Gegnerlogik zu aktualisieren
    @Override
    public void act(float delta) {
        super.act(delta); // Aufruf der GameObject-Logik
    }

    public abstract void defineBody();

    public abstract void setSprite(TextureAtlas atlas);

    public void update(float dt) {
        sprite.setPosition(b2body.getPosition().x - sprite.getWidth()/2, b2body.getPosition().y - sprite.getHeight()/2);

        b2body.setActive(currentState != AnimationState.DEAD);
    }

    @Override
    public float getStateTimer() {
        return 0;
    }

    @Override
    public void setStateTimer(float stateTimer) {
        this.stateTimer = stateTimer;
    }

    @Override
    public Object getCurrentState() {
        if (isDead()){
            return AnimationState.DEAD;
        }
        else if(!isDead()){
            return AnimationState.ALIVE;
        }
        else return null;
    }

    @Override
    public void setCurrentState(Object animationStates) {
        if (animationStates instanceof Enemy.AnimationState) {
            this.currentState = (Enemy.AnimationState) animationStates;
        }
    }

    public void die () {
        currentState = AnimationState.DEAD;
    }

    //Getter
    public boolean isDead() { return currentState == AnimationState.DEAD; }

    public void setPosition() {

    }

    public float getX() { return x;}
    public float getY() { return y;}

    public TextureRegion getFrame(float dt, Animation<TextureRegion> animation){
        // currently only has idle animation
        stateTimer += dt;
        return animation.getKeyFrame(stateTimer, true);
    }

    public boolean isRewindable() { return rewindable; }

}
