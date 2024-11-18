package braid.main;

import com.badlogic.gdx.graphics.g2d.Batch;

// Enemy-Klasse, die von GameObject erbt und gegner-spezifische Funktionen enthält
public class Enemy extends GameObject {

    // Konstruktor für die Initialisierung des Gegners
    public Enemy() {
        // Initialisierungscode für den Gegner, z.B. Festlegen von Geschwindigkeit oder anderen Eigenschaften
    }

    // Überschreiben der `act()`-Methode, um die Gegnerlogik zu aktualisieren
    @Override
    public void act(float delta) {
        super.act(delta); // Aufruf der GameObject-Logik
        // Gegner-spezifische Logik, z.B. Bewegungsmuster oder Interaktion mit dem Spieler
    }

    // Überschreiben der `draw()`-Methode, um den Gegner zu zeichnen
    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Zeichnen des Gegners mit einer bestimmten Textur, Position und Transparenz
//        batch.draw(/* Texture, x-Position, y-Position, Breite, Höhe */);
    }
}
