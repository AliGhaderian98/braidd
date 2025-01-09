package braid.main.enemies;

import braid.main.Braid;
import braid.main.objects.Enemy;
import braid.main.objects.Player;
import braid.main.screens.levels.LevelScreen;
import braid.main.tools.UserData;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.util.logging.Level;

public class UnhingedEnemy extends Enemy implements EnemyAI {
    private float stateTimer = 0;
    private Player player;
    private boolean playerIsInRange = false;
    public UnhingedEnemy(World world, LevelScreen screen, Player player, float x, float y, boolean rewindable, String type) {
        super(world, screen,x,y, rewindable, type);
        this.player = player;
        defineBody();

        speed = 0.15f;

        setSprite(screen.getAtlas());
    }

    @Override
    public void setSprite(TextureAtlas atlas) {
        switch (type) {
            default -> {
                sprite = new Sprite(atlas.findRegion("wissenschaftler"));
                idle = new Animation<>(0.1f, atlas.findRegions("wissenschaftler"), Animation.PlayMode.LOOP);
                sprite.setBounds(0,0,24/ Braid.PPM, 24/Braid.PPM);
                sprite.setRegion(idle.getKeyFrame(0, true));
            }
        }
    }

    public void update(float dt){
        super.update(dt);
        sprite.setRegion(getFrame(dt));

        if (playerIsInRange) {
            attack();
        } else
            idle();
    }
    @Override
    public void idle() {

    }

    @Override
    public void attack() {
        if (getSprite().getX() < player.getSprite().getX()) {
            b2body.applyLinearImpulse(new Vector2(getSpeed() * 5f, 0), b2body.getWorldCenter(), true);
        } else if (getSprite().getX() > player.getSprite().getX()) {
            b2body.applyLinearImpulse(new Vector2(-getSpeed() * 5f, 0), b2body.getWorldCenter(), true);
        }

        if (getSprite().getY() < player.getSprite().getY()) {
            b2body.applyLinearImpulse(new Vector2(0, getSpeed() * 5f), b2body.getWorldCenter(), true);
        } else if (getSprite().getY() > player.getSprite().getY()) {
            b2body.applyLinearImpulse(new Vector2(0, -getSpeed() * 5f), b2body.getWorldCenter(), true);
        }
    }

    @Override
    public void defineBody() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX() / Braid.PPM, getY() / Braid.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        // Create EnemyBody
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(8 / Braid.PPM);
        shape.setPosition(new Vector2(0, -3/Braid.PPM));

        fdef.shape = shape;
        fdef.friction = 1f;
        Fixture bodyFixture = b2body.createFixture(fdef);
        bodyFixture.setUserData(new UserData("EnemyBody", this));

        // Create EnemyRadius
        FixtureDef enemyRadiusDef = new FixtureDef();
        shape.setRadius(70 / Braid.PPM);
        enemyRadiusDef.shape = shape;
        enemyRadiusDef.friction = 1f;
        enemyRadiusDef.isSensor = true;
        Fixture radiusFixture = b2body.createFixture(enemyRadiusDef);
        radiusFixture.setUserData(new UserData("EnemyRadius", this));

        shape.dispose();
    }

    public TextureRegion getFrame(float dt){
        // currently only has idle animation
        stateTimer += dt;
        return idle.getKeyFrame(stateTimer, true);
    }

    public void setPlayerIsInRange(boolean inRange) {
        playerIsInRange = inRange;
    }
    @Override
    public void die() {

    }

}
