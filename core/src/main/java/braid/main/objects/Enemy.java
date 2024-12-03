package braid.main.objects;

import braid.main.Braid;
import braid.main.screens.TestScreen;
import braid.main.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;


/***********
 Diese Klasse soll einen ersten spezifischen Entwurf für einen Gegner darstellen
 und später generalisert werden, sodass aus ihr verschiedene Arten an Gegnern
 erstellt werden können.
 ***********/

public class Enemy extends DynamicGameObject{

    private final TextureRegion stand;
    private RewindController rewindController;
    private boolean dead = false;


    // Konstruktor für die Initialisierung des Gegners
    public Enemy(World world, TestScreen screen) {
        super(world);

        speed = 0.15f;

        defineBody();

        sprite = new Sprite(screen.getAtlas().findRegion("lion-idle"));
        // Hier wird die Region vom ersten idle frame hardgecodet, später Rausnehmen wenn der gegner auch animiert ist
        stand = new TextureRegion(sprite.getTexture(), 28, 2, 24, 24);
        sprite.setBounds(0,0,24/ Braid.PPM, 24/Braid.PPM);
        sprite.setRegion(stand);

    }

    // Überschreiben der `act()`-Methode, um die Gegnerlogik zu aktualisieren
    @Override
    public void act(float delta) {
        super.act(delta); // Aufruf der GameObject-Logik
        // Gegner-spezifische Logik, z.B. Bewegungsmuster oder Interaktion mit dem Spieler
    }

    public void defineBody() {
        BodyDef bdef = new BodyDef();
        bdef.position.set((Braid.V_WIDTH - 32) / Braid.PPM, 32 / Braid.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(9 / Braid.PPM);

        fdef.shape = shape;
        fdef.friction = 1f;
        Fixture bodyFixture = b2body.createFixture(fdef);
        bodyFixture.setUserData(this);
    }

    public void update(float dt) {
        setPosition(b2body.getPosition().x - sprite.getWidth()/2, b2body.getPosition().y - sprite.getHeight()/2);

        rewindController.update();

        if (dead) {
            b2body.setActive(false);
        }
    }

    public RewindController getRewindController() {
        return rewindController;
    }
    public void setRewindController(RewindController rewindController) { this.rewindController = rewindController; }

    @Override
    public float getStateTimer() {
        return 0;
    }

    @Override
    public void setStateTimer(float stateTimer) {

    }

    @Override
    public Object getCurrentState() {
        return null;
    }

    @Override
    public void setCurrentState(Object currentState) {

    }

    public void die () {
        dead = true;
    }

    //Getter
    public boolean isDead() { return dead; }
}
