package braid.main.tools;

import braid.main.Items.Item;
import braid.main.enemies.MadScientist;
import braid.main.enemies.PatrollingEnemy;
import braid.main.objects.End;
import braid.main.objects.Enemy;
import braid.main.objects.Ladder;
import braid.main.objects.Player;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class WorldContactListener implements ContactListener {
    private final Player player;
    private final MadScientist madScientist;

    public WorldContactListener(Player player, MadScientist madScientist) {
        this.player = player;
        this.madScientist = (MadScientist) madScientist;
    }

    @Override
    public void beginContact(Contact contact) {
        UserData userDataA = (UserData) contact.getFixtureA().getUserData();
        UserData userDataB = (UserData) contact.getFixtureB().getUserData();

        if (userDataA.getObject() instanceof Player || userDataB.getObject() instanceof Player) {
            playerWithItem(userDataA, userDataB);
            playerWithLadder(userDataA, userDataB);
            playerFeetWithEnemy(userDataA, userDataB);
            playerBodyWithEnemy(userDataA, userDataB, contact);
        }

        patrollingEnemyWithWall(userDataA,userDataB);
        playerWithEnd(userDataA,userDataB);

        if (userDataA.getObject() instanceof Enemy || userDataB.getObject() instanceof Enemy) {
            MadScientistWithLadder(userDataA, userDataB);
            MadScientistFeetWithEnemy(userDataA, userDataB);
        }
    }


    @Override
    public void endContact(Contact contact) {
        UserData userDataA = (UserData) contact.getFixtureA().getUserData();
        UserData userDataB = (UserData) contact.getFixtureB().getUserData();

        contactEndedPlayerWithLadder(userDataA,userDataB);
        patrollingEnemyOnEdge(userDataA,userDataB);
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {

    }

    private void playerWithItem(UserData userDataA, UserData userDataB) {
        UserData itemData = (userDataA.getObject() instanceof Item) ? userDataA : userDataB;

        if (itemData.getObject() instanceof Item item) {
            item.use();
        }
    }

    private void playerFeetWithEnemy(UserData userDataA, UserData userDataB) {
        if ("PlayerFeet".equals(userDataA.getName()) || "PlayerFeet".equals(userDataB.getName())) {
            UserData enemyData = (userDataA.getObject() instanceof Enemy) ? userDataA : userDataB;

            if (enemyData.getObject() instanceof Enemy enemy) {
                enemy.die();
                player.b2body.setLinearVelocity(player.b2body.getLinearVelocity().x, 0);
                player.jump(1.25f);
            }
        }
    }

    private void MadScientistFeetWithEnemy(UserData userDataA, UserData userDataB) {
        if ("MadScientistFeet".equals(userDataA.getName()) || "MadScientistFeet".equals(userDataB.getName())) {
            UserData enemyData = (!(userDataA.getObject() instanceof MadScientist)) ? userDataA : userDataB;

            if (enemyData.getObject() instanceof Enemy enemy) {
                enemy.die();
                madScientist.b2body.setLinearVelocity(madScientist.b2body.getLinearVelocity().x, 0);
                madScientist.jump(1.25f);

            }
        }
    }

    private void playerWithLadder(UserData userDataA, UserData userDataB) {
        if (userDataA.getObject() instanceof Ladder || userDataB.getObject() instanceof Ladder) {
            player.atLadder(true);
        }
    }

    private void MadScientistWithLadder(UserData userDataA, UserData userDataB) {
        if (userDataA.getObject() instanceof Ladder || userDataB.getObject() instanceof Ladder) {
            madScientist.atLadder(true);
        }
    }

    private void playerBodyWithEnemy(UserData userDataA, UserData userDataB, Contact contact) {
        if ("PlayerBody".equals(userDataA.getName()) || "PlayerBody".equals(userDataB.getName())) {
            UserData enemyData = (userDataA.getObject() instanceof Enemy) ? userDataA : userDataB;
            WorldManifold worldManifold = contact.getWorldManifold();
            Vector2 normal = worldManifold.getNormal();

            if (enemyData.getObject() instanceof Enemy enemy) {
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
            player.stopClimbing();
        }
    }

    private void playerWithEnd(UserData userDataA, UserData userDataB) {
        if (userDataA.getObject() instanceof End || userDataB.getObject() instanceof End) {
            player.atEnd(true);
            player.jump(1.25f); // Zur Kontrolle
        }
    }

    private void patrollingEnemyOnEdge(UserData userDataA, UserData userDataB) {
        if ("EdgeSensor".equals(userDataA.getName()) || "EdgeSensor".equals(userDataB.getName()) ) {
            UserData edgeSensor = "EdgeSensor".equals(userDataA.getName()) ? userDataA : userDataB;

            if (edgeSensor.getObject() instanceof PatrollingEnemy enemy) {
                enemy.changeDirection();
            }
        }
    }
}

