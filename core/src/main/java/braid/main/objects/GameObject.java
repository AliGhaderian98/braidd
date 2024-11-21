package braid.main.objects;

import braid.main.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

/***********
 Abstrakte Basisklasse für alle Spielobjekte, die von Actor erben. Darunter fallen u.a.
 Spieler und Gegner, aber auch Schlüssel u.ä.
 ***********/

public abstract class GameObject extends Actor {
    // Gemeinsame Eigenschaft für Geschwindigkeit, die von Spieler und Gegner verwendet werden kann
    protected float speed;
    protected Texture texture;
    protected Sprite sprite;
    //protected RewindController rewindController; //Copy-Paste für Rewind

    public World world;
    public Body b2body;//Copy-Paste für Rewind

    public GameObject() {}

    // Konstruktoren
    public GameObject(Texture texture) {
        this.texture = texture;
        sprite = new Sprite(texture);
        sprite.setSize(sprite.getWidth() * 0.1f, sprite.getHeight() * 0.1f);

        //this.rewindController = new RewindController(new RewindableBody(b2body)); //Copy-Paste für Rewind
    }

    public GameObject(World world) {
        this.world = world;
        //this.rewindController = new RewindController(new RewindableBody(b2body)); ////Copy-Paste für Rewind
    }


    // Methoden

    // Überschreiben der `act()`-Methode, um die Logik jedes Frames zu aktualisieren
    @Override
    public void act(float delta) {
        super.act(delta); // Aufruf der übergeordneten Methode, um die grundlegende Actor-Logik auszuführen
        // Zusätzliche Logik zur Aktualisierung der Position oder anderer Eigenschaften kann hier hinzugefügt werden

    }

    @Override
    public void setPosition(float x, float y) {
        sprite.setPosition(x, y);
    }

    @Override
    public void setColor(Color color) {
        sprite.setColor(color);
    }

    public void defineBody() {
    }


    // Getter und Setter
    public float getSpeed() {
        return speed;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }
/* //Copy-Paste für Rewind
    public RewindController getRewindController() {
        return rewindController;
    }
    public void setRewindController(RewindController rewindController) {
        this.rewindController = rewindController;
    }

 */
}


