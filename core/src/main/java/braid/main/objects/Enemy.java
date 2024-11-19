package braid.main.objects;

import braid.main.Braid;
import braid.main.screens.TestScreen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;

// Enemy-Klasse, die von GameObject erbt und gegner-spezifische Funktionen enthält
public class Enemy extends GameObject {

    private TextureRegion stand;

    // Konstruktor für die Initialisierung des Gegners
    public Enemy(World world, TestScreen screen) {
        super(world);

        speed = 5;

        sprite = new Sprite(screen.getAtlas().findRegion("lion-idle"));
        stand = new TextureRegion(sprite.getTexture(), 0, 0, 24, 24);
        sprite.setBounds(0,0,24/ Braid.PPM, 24/Braid.PPM);
        sprite.setRegion(stand);
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
