package braid.main.enemies;

import braid.main.Braid;
import braid.main.objects.Enemy;
import braid.main.objects.Player;
import braid.main.screens.levels.LevelScreen;
import braid.main.tools.UserData;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.util.Objects;

public class Knight extends Enemy implements EnemyAI {
    private boolean playerIsInRange = false;
    private boolean isAttacking = false;
    private float elapsedTime = 0;
    private final Player player;
    private int direction = 0;

    public Knight(World world, LevelScreen screen, Player player, float x, float y, boolean rewindable, String type) {
        super(world, screen, x, y, rewindable, type);
        this.player = player;

        speed = 0.5f;

        setSprite(screen.getAtlas());

        defineBody();
    }

    @Override
    public void setSprite(TextureAtlas atlas) {
        switch (type) {
            case ("Lion") -> {
                sprite = new Sprite(atlas.findRegion("Lion-idle"));
                idle = new Animation<>(0.1f, atlas.findRegions("Lion-idle"), Animation.PlayMode.LOOP);
                walking = new Animation<>(0.2f, atlas.findRegions("Lion-go"), Animation.PlayMode.LOOP);
                running = new Animation<>(0.1f, atlas.findRegions("Lion-run"), Animation.PlayMode.LOOP);
                attacking = new Animation<>(0.1f, atlas.findRegions("Lion-attack"), Animation.PlayMode.LOOP);

                sprite.setBounds(0,0,48 / Braid.PPM, 48 / Braid.PPM);
                sprite.setRegion(idle.getKeyFrame(0, false));
            }
            case ("rotatingCoffeeBean") -> {
                sprite = new Sprite(atlas.findRegion("rotatingCoffeeBean"));
                idle = new Animation<>(0.1f, atlas.findRegions("rotatingCoffeeBean"), Animation.PlayMode.LOOP);
                walking = new Animation<>(0.2f, atlas.findRegions("rotatingCoffeeBean"), Animation.PlayMode.LOOP);
                running = new Animation<>(0.1f, atlas.findRegions("rotatingCoffeeBean"), Animation.PlayMode.LOOP);
                attacking = new Animation<>(0.1f, atlas.findRegions("rotatingCoffeeBean" ), Animation.PlayMode.LOOP);

                sprite.setBounds(0,0,48 / Braid.PPM, 48 / Braid.PPM);
                sprite.setRegion(idle.getKeyFrame(0, false));
            }
            default -> {
                sprite = new Sprite(atlas.findRegion("knight-idle"));
                idle = new Animation<>(0.1f, atlas.findRegions("knight-idle"), Animation.PlayMode.LOOP);
                walking = new Animation<>(0.1f, atlas.findRegions("knight-walk"), Animation.PlayMode.LOOP);
                running = new Animation<>(0.1f, atlas.findRegions("knight-run"), Animation.PlayMode.LOOP);
                attacking = new Animation<>(0.1f, atlas.findRegions("knight-attack"), Animation.PlayMode.LOOP);

                sprite.setBounds(0,0,48 / Braid.PPM, 48 / Braid.PPM);
                sprite.setRegion(idle.getKeyFrame(0, false));
            }
        }
    }

    public void update(float dt) {
        super.update(dt);
        sprite.setRegion(getFrame(dt, idle));

        if (isAttacking) {
            attack(dt);
        } else if (playerIsInRange) {
            run(dt);
        } else {
            walking(dt);
        }
        sprite.flip(b2body.getLinearVelocity().x<0,false);

    }

    private void run(float dt) {
        elapsedTime += dt;

        sprite.setRegion(running.getKeyFrame(elapsedTime, true));
        if (getSprite().getX() < player.getSprite().getX()) {
            b2body.setLinearVelocity(new Vector2(speed*2, 0));
        } else if (getSprite().getX() > player.getSprite().getX()) {
            b2body.setLinearVelocity(new Vector2(-speed*2, 0));
        }
    }

    private void attack(float dt) {
        elapsedTime += dt;

        sprite.setRegion(attacking .getKeyFrame(elapsedTime, true));
    }

    private void walking(float dt) {
        elapsedTime += dt;
        int standingTime = 5;
        int walkingTime = 10;

        // Richtung speichern
        if (elapsedTime < standingTime) {
            sprite.setRegion(idle.getKeyFrame(elapsedTime, true));
            direction = 0;
        } else if (elapsedTime < standingTime + walkingTime) {
            sprite.setRegion(walking.getKeyFrame(elapsedTime - standingTime, true));

            if (direction == 0) {
                direction = (Math.random() < 0.5 ? 1 : -1); // 1 = rechts, -1 = links
            }

            b2body.setLinearVelocity(new Vector2(direction*speed, 0));
        } else {
            elapsedTime = 0;
        }
    }

    @Override
    public void idle() {

    }

    @Override
    public void attack() {


    }

    public void setPlayerIsInRange(boolean inRange) {
        playerIsInRange = inRange;
    }

    public void setPlayerIsInAttackingRange(boolean inRange) { isAttacking = inRange;}

    @Override
    public void defineBody() {

        // defult
        float position =-3 /Braid.PPM;
        float bodyShapehx = 5 / Braid.PPM;
        float bodyShapehy = 16 / Braid.PPM;
        float yPoint = -3/Braid.PPM;
        float Headhx = 7 / Braid.PPM;
        float Headhy = 1 / Braid.PPM;
        float HeadPosy = 15 / Braid.PPM;

        if(Objects.equals(type, "Lion")){
            bodyShapehx = 20/ Braid.PPM;
            bodyShapehy = 12/ Braid.PPM;
            yPoint = -10 / Braid.PPM;
            Headhx =0.2f;
            HeadPosy = 0.03f;
        }

        BodyDef bdef = new BodyDef();
        bdef.position.set(getX() / Braid.PPM, getY() / Braid.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        // Create EnemyBody
        FixtureDef fdef = new FixtureDef();
        PolygonShape bodyShape = new PolygonShape();
        bodyShape.setAsBox(bodyShapehx, bodyShapehy, new Vector2(0, yPoint), 0);

        fdef.shape = bodyShape;
        fdef.friction = 1f;
        Fixture bodyFixture = b2body.createFixture(fdef);
        bodyFixture.setUserData(new UserData("EnemyBody", this));

        FixtureDef enemyRadiusDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(80 / Braid.PPM);
        enemyRadiusDef.shape = shape;
        enemyRadiusDef.friction = 1f;
        enemyRadiusDef.isSensor = true;
        Fixture radiusFixture = b2body.createFixture(enemyRadiusDef);
        radiusFixture.setUserData(new UserData("KnightEnemyRadius", this));

        FixtureDef enemyAttackingRadius = new FixtureDef();
        shape.setRadius(15 / Braid.PPM);
        enemyAttackingRadius.shape = shape;
        enemyAttackingRadius.friction = 1f;
        enemyAttackingRadius.isSensor = true;
        Fixture attackingRadiusFixture = b2body.createFixture(enemyAttackingRadius);
        attackingRadiusFixture.setUserData(new UserData("KnightEnemyAttackingRadius", this));

        // create Head
            FixtureDef headFdef = new FixtureDef();
            PolygonShape head = new PolygonShape();
            head.setAsBox(Headhx, Headhy , new Vector2(0, HeadPosy), 0);
            headFdef.shape = head;
            headFdef.friction = 1f;
            Fixture headFixture = b2body.createFixture(headFdef);
            headFixture.setUserData(new UserData("EnemyHead", this));


        shape.dispose();
    }
}
