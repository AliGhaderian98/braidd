package braid.main.tools;

import braid.main.Braid;
import braid.main.Items.Item;
import braid.main.enemies.PatrollingEnemy;
import braid.main.enemies.UnhingedEnemy;
import braid.main.objects.End;
import braid.main.objects.Enemy;
import braid.main.objects.Ladder;
import braid.main.objects.Player;
import braid.main.overworld.Overworld;
import braid.main.screens.levels.LevelScreen;
import braid.main.screens.menus.SavegameMenu;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class WorldContactListener implements ContactListener {
    private final Player player;
    private Braid game;
    public WorldContactListener(Player player, Braid game) {
        this.game = game;
        this.player = player;
    }

    @Override
    public void beginContact(Contact contact) {
        UserData userDataA = (UserData) contact.getFixtureA().getUserData();
        UserData userDataB = (UserData) contact.getFixtureB().getUserData();

        if (userDataA.getObject() instanceof Player || userDataB.getObject() instanceof Player) {
            playerWithItem(userDataA, userDataB);
            playerWithLadder(userDataA, userDataB);
            playerFeetWithGround(userDataA, userDataB);
            playerFeetWithEnemy(userDataA, userDataB);
            playerBodyWithEnemy(userDataA, userDataB, contact);
            playerWithUnhingedEnemyRadius(userDataA, userDataB);
        }

        patrollingEnemyWithWall(userDataA,userDataB);
        playerWithEnd(userDataA,userDataB);

    }


    @Override
    public void endContact(Contact contact) {
        UserData userDataA = (UserData) contact.getFixtureA().getUserData();
        UserData userDataB = (UserData) contact.getFixtureB().getUserData();

        contactEndedPlayerWithGround(userDataA, userDataB);
        contactEndedPlayerWithLadder(userDataA, userDataB);
        patrollingEnemyOnEdge(userDataA, userDataB);
        endPlayerWithUnhingedEnemyRadius(userDataA, userDataB);
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {

    }

    private void playerWithUnhingedEnemyRadius(UserData userDataA, UserData userDataB) {
        if ("EnemyRadius".equals(userDataA.getName()) || "EnemyRadius".equals(userDataB.getName())) {
            UnhingedEnemy unhingedEnemy = ("EnemyRadius".equals(userDataA.getName())) ?  (UnhingedEnemy) userDataA.getObject() : (UnhingedEnemy) userDataB.getObject();

            unhingedEnemy.setPlayerIsInRange(true);

        }
    }

    private void endPlayerWithUnhingedEnemyRadius(UserData userDataA, UserData userDataB) {
        if ("EnemyRadius".equals(userDataA.getName()) || "EnemyRadius".equals(userDataB.getName())) {
            UnhingedEnemy unhingedEnemy = ("EnemyRadius".equals(userDataA.getName())) ?  (UnhingedEnemy) userDataA.getObject() : (UnhingedEnemy) userDataB.getObject();

            unhingedEnemy.setPlayerIsInRange(false);

        }
    }

    private void playerWithItem(UserData userDataA, UserData userDataB) {
        UserData itemData = (userDataA.getObject() instanceof Item) ? userDataA : userDataB;

        if (itemData.getObject() instanceof Item item) {
            item.use(player);
        }
    }

    private void playerFeetWithGround(UserData userDataA, UserData userDataB) {
        if (("PlayerFeet".equals(userDataA.getName()) || "PlayerFeet".equals(userDataB.getName())) &&
            (("Ground".equals(userDataA.getName()) || "Ground".equals(userDataB.getName()))))  {
            if (!player.isGrounded()) {
                player.land();
            }
        }
    }

    private void playerFeetWithEnemy(UserData userDataA, UserData userDataB) {
        if ("PlayerFeet".equals(userDataA.getName()) || "PlayerFeet".equals(userDataB.getName())) {
            UserData enemyData = (userDataA.getObject() instanceof Enemy) ? userDataA : userDataB;

            if (enemyData.getObject() instanceof UnhingedEnemy && "EnemyBody".equals(enemyData.getName())) {
                player.die();
            }
            else if (enemyData.getObject() instanceof Enemy enemy && "EnemyHead".equals(enemyData.getName())) {
                enemy.die();
                player.b2body.setLinearVelocity(player.b2body.getLinearVelocity().x, 0);
                player.jump(1.25f);
            }
        }
    }

    private void playerWithLadder(UserData userDataA, UserData userDataB) {
        if (userDataA.getObject() instanceof Ladder || userDataB.getObject() instanceof Ladder) {
            UserData ladderData = (userDataA.getObject() instanceof Ladder) ? userDataA : userDataB;
            player.atLadder(true);

            Vector2 max = new Vector2(0,0);
            Vector2 min = new Vector2(0,0);

            PolygonShape polygonShape = (PolygonShape) ((Ladder) ladderData.getObject()).b2body.getFixtureList().first().getShape();
            Vector2 vertex = new Vector2();

            for (int i = 0; i < polygonShape.getVertexCount(); i++) {
                polygonShape.getVertex(i, vertex);
                // Transform vertex to world coordinates
                vertex = (((Ladder) ladderData.getObject()).b2body).getWorldPoint(vertex);

                min.x = Math.min(min.x, vertex.x);
                min.y = Math.min(min.y, vertex.y);
                max.x = Math.max(max.x, vertex.x);
                max.y = Math.max(max.y, vertex.y);
            }

            player.setMoveLimits(max, min);
        }
    }

    private void playerBodyWithEnemy(UserData userDataA, UserData userDataB, Contact contact) {
        if ("PlayerBody".equals(userDataA.getName()) || "PlayerBody".equals(userDataB.getName())) {
            UserData enemyData = (userDataA.getObject() instanceof Enemy) ? userDataA : userDataB;
            WorldManifold worldManifold = contact.getWorldManifold();
            Vector2 normal = worldManifold.getNormal();

            if (enemyData.getObject() instanceof Enemy enemy ) {
                if (normal.y < -0.5f) {
                    enemy.die();
                    player.b2body.setLinearVelocity(player.b2body.getLinearVelocity().x, 0);
                    player.jump(1.25f);
                }
                else if ("EnemyBody".equals(enemyData.getName())) {
                    player.die();
                }
            }
        }
    }

    private void contactEndedPlayerWithGround(UserData userDataA, UserData userDataB) {
        if (("PlayerFeet".equals(userDataA.getName()) || "PlayerFeet".equals(userDataB.getName())) &&
            (("Ground".equals(userDataA.getName()) || "Ground".equals(userDataB.getName()))))  {
            if (player.isGrounded())
                player.fall();
        }
    }

    private void patrollingEnemyWithWall(UserData userDataA, UserData userDataB) {
        if ("SideSensor".equals(userDataA.getName()) || "SideSensor".equals(userDataB.getName())) {
            UserData sideSensor = "SideSensor".equals(userDataA.getName()) ? userDataA : userDataB;
            if ("Wall".equals(userDataA.getName()) || "Wall".equals(userDataB.getName()))  {
                PatrollingEnemy enemy = (PatrollingEnemy) sideSensor.getObject();
                enemy.changeDirection();
            }
        }
    }

    private void contactEndedPlayerWithLadder(UserData userDataA, UserData userDataB) {
        if (userDataA.getObject() instanceof Ladder || userDataB.getObject() instanceof Ladder) {
            player.atLadder(false);
            player.resetMoveLimits();
            player.stopClimbing();
        }
    }

    private void playerWithEnd(UserData userDataA, UserData userDataB) {
        if (userDataA.getObject() instanceof End || userDataB.getObject() instanceof End) {
            player.atEnd(true);

            // switch to overworld after 0.2 second delay
            new Thread(() -> {
                long time = System.currentTimeMillis();
                while (System.currentTimeMillis() < time + 200){}
                Gdx.app.postRunnable(() -> game.setScreen(new Overworld(game)));
            }).start();
        }
    }

    private void patrollingEnemyOnEdge(UserData userDataA, UserData userDataB) {
        if ("EdgeSensor".equals(userDataA.getName()) || "EdgeSensor".equals(userDataB.getName())) {
            UserData sideSensor = "SideSensor".equals(userDataA.getName()) ? userDataA : userDataB;
            if ("Ground".equals(userDataA.getName()) || "Ground".equals(userDataB.getName()))  {
                PatrollingEnemy enemy = (PatrollingEnemy) sideSensor.getObject();
                enemy.changeDirection();
            }
        }
    }
}
