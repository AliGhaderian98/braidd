package braid.main.objects;

import braid.main.Braid;
import braid.main.screens.TestScreen;
import braid.main.*;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;


/***********
 Diese Klasse soll einen ersten spezifischen Entwurf für einen Gegner darstellen
 und später generalisert werden, sodass aus ihr verschiedene Arten an Gegnern
 erstellt werden können.
 ***********/

public class Enemy extends DynamicGameObject{

    public enum AnimationState {
        DEAD,
        ALIVE;
    }

    //private final TextureRegion stand;
    private AnimationState currentState = AnimationState.ALIVE;
    private final Animation<TextureRegion> idle;


    // Konstruktor für die Initialisierung des Gegners
    public Enemy(World world, TestScreen screen) {
        super(world);

        speed = 0.15f;

        defineBody();

        sprite = new Sprite(screen.getAtlas().findRegion("lion-idle"));

        idle = new Animation<>(0.2f, screen.getAtlas().findRegions("lion-idle"), Animation.PlayMode.LOOP);
        sprite.setBounds(0,0,24/ Braid.PPM, 24/Braid.PPM);
        sprite.setRegion(idle.getKeyFrame(0, false));

    }

    // Überschreiben der `act()`-Methode, um die Gegnerlogik zu aktualisieren
    @Override
    public void act(float delta) {
        super.act(delta); // Aufruf der GameObject-Logik
        // Gegner-spezifische Logik, z.B. Bewegungsmuster oder Interaktion mit dem Spieler
    }

    public void defineBody() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(180 / Braid.PPM, 32 / Braid.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(8 / Braid.PPM);

        fdef.shape = shape;
        fdef.friction = 1f;
        Fixture bodyFixture = b2body.createFixture(fdef);
        bodyFixture.setUserData("EnemyBody");

        // Create head collider
        FixtureDef headFdef = new FixtureDef();
        PolygonShape head = new PolygonShape();
        head.setAsBox(4 / Braid.PPM, 1 / Braid.PPM, new Vector2(0, 10 / Braid.PPM), 0);
        headFdef.shape = head;
        headFdef.friction = 1f;
        Fixture headFixture = b2body.createFixture(headFdef);
        headFixture.setUserData(this);
    }

    public void update(float dt) {
        setPosition(b2body.getPosition().x - sprite.getWidth()/2, b2body.getPosition().y - sprite.getHeight()/2);

        rewindController.update();

        if (currentState == AnimationState.DEAD) {
            b2body.setActive(false);
        }
        else{
            b2body.setActive(true);
        }
    }

    // todo: animationen für gegner einbauen
    @Override
    public float getStateTimer() {
        return 0;
    }

    @Override
    public void setStateTimer(float stateTimer) {

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
}
