package braid.main.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.World;

public abstract class DynamicGameObject extends GameObject {

    // Gemeinsame Eigenschaft für Geschwindigkeit, die von Spieler und Gegner verwendet werden kann
    protected float speed;
    protected Sprite sprite;

    //Konstruktor
    public DynamicGameObject(World world) {
        super(world);
    }

    //Methoden
    @Override
    public void setPosition(float x, float y) {
        b2body.setTransform(x,y,0);
        b2body.setLinearVelocity(0, 0);
        b2body.setAngularVelocity(0);
    }

    @Override
    public void setColor(Color color) {
        sprite.setColor(color);
    }

    // Getter und Setter
    public float getSpeed() {return speed;}

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public abstract float getStateTimer();

    public abstract void setStateTimer(float stateTimer);

    public abstract Object getCurrentState();

    public abstract void setCurrentState(Object currentState);

}
