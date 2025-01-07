package braid.main.enemies;

import braid.main.Braid;
import braid.main.objects.Enemy;
import braid.main.objects.Player;
import braid.main.screens.levels.LevelScreen;
import braid.main.tools.UserData;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Knight extends Enemy implements EnemyAI {
    private boolean playerIsInRange = false;
    private boolean isAttacking = false;
    private float elapsedTime = 0;
    private final Player player;
    private int direction = 0;

    public Knight(World world, LevelScreen screen, Player player, float x, float y) {
        super(world, screen, x, y);
        this.player = player;

        speed = 0.5f;

        sprite = new Sprite(screen.getAtlas().findRegion("knight-idle"));

        idle = new Animation<>(0.2f, screen.getAtlas().findRegions("knight-idle"), Animation.PlayMode.LOOP);
        walking = new Animation<>(0.2f, screen.getAtlas().findRegions("knight-walk"), Animation.PlayMode.LOOP);
        running = new Animation<>(0.2f, screen.getAtlas().findRegions("knight-run"), Animation.PlayMode.LOOP);
        attacking = new Animation<>(0.2f, screen.getAtlas().findRegions("knight-attack"), Animation.PlayMode.LOOP);

        sprite.setBounds(0,0,48 / Braid.PPM, 48 / Braid.PPM);
        sprite.setRegion(idle.getKeyFrame(0, false));

        defineBody();
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
                direction = (Math.random() * 2 == 0 ? 1 : -1); // 1 = rechts, -1 = links
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
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX() / Braid.PPM, getY() / Braid.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        // Create EnemyBody
        FixtureDef fdef = new FixtureDef();
        PolygonShape bodyShape = new PolygonShape();
        bodyShape.setAsBox(5 / Braid.PPM, 15 / Braid.PPM);

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

        shape.dispose();
    }
}
