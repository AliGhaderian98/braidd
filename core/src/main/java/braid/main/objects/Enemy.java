package braid.main.objects;

import com.badlogic.gdx.graphics.Texture;

// Enemy-Klasse, die von GameObject erbt und gegner-spezifische Funktionen enthält
public class Enemy extends GameObject {

    // Konstruktor für die Initialisierung des Gegners
    public Enemy() {
        speed = 3;
    }

    public Enemy(Texture texture) {
        super(texture);
        speed = 3;
    }

    // Überschreiben der `act()`-Methode, um die Gegnerlogik zu aktualisieren
    @Override
    public void act(float delta) {
        super.act(delta); // Aufruf der GameObject-Logik
        // Gegner-spezifische Logik, z.B. Bewegungsmuster oder Interaktion mit dem Spieler
    }
}
