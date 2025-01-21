package braid.main.objects;

import braid.main.rewind.RewindController;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

/***********
 Abstrakte Basisklasse für alle Spielobjekte, die von Actor erben. Darunter fallen u.a.
 Spieler und Gegner, aber auch Schlüssel u.ä.
 ***********/

public abstract class GameObject extends Actor {
    protected RewindController rewindController;

    protected World world;
    public Body b2body;

    // Konstruktor

    public GameObject(World world) {
        this.world = world;
    }

    // Methoden

    // Überschreiben der `act()`-Methode, um die Logik jedes Frames zu aktualisieren
    @Override
    public void act(float delta) {
        super.act(delta);
        rewindController.update(delta);
    }

    public abstract void defineBody();

    // Getter und Setter
    public RewindController getRewindController() { return rewindController; }

    public void setRewindController(RewindController rewindController) { this.rewindController = rewindController; }

    public float getStateTimer() { return 0; }
    public void setStateTimer(float stateTimer) {}

    public Object getCurrentState() { return  null; }
    public void setCurrentState(Object currentState) {}

}


