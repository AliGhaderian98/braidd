package braid.main;

import com.badlogic.gdx.scenes.scene2d.Actor;

// Abstrakte Basisklasse für alle Spielobjekte, die von Actor erben
public abstract class GameObject extends Actor {
    // Gemeinsame Eigenschaft für Geschwindigkeit, die von Spieler und Gegner verwendet werden kann
    protected float speed;

    // Konstruktor für GameObject, Initialisierung kann hier hinzugefügt werden
    public GameObject() {
    }

    // Überschreiben der `act()`-Methode, um die Logik jedes Frames zu aktualisieren
    @Override
    public void act(float delta) {
        super.act(delta); // Aufruf der übergeordneten Methode, um die grundlegende Actor-Logik auszuführen
        // Zusätzliche Logik zur Aktualisierung der Position oder anderer Eigenschaften kann hier hinzugefügt werden
    }
}

