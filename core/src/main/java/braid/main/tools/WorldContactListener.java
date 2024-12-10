package braid.main.tools;

import braid.main.enemies.PatrollingEnemy;
import braid.main.objects.Enemy;
import braid.main.objects.Ladder;
import braid.main.objects.Player;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.util.Objects;

public class WorldContactListener implements ContactListener {
    private final Player player;
    public WorldContactListener(Player player) {
        this.player = player;
    }

    @Override
    public void beginContact(Contact contact) {
        UserData userDataA = (UserData) contact.getFixtureA().getUserData();
        UserData userDataB = (UserData) contact.getFixtureB().getUserData();




        playerFeetWithEnemy(userDataA, userDataB, contact);
        playerBodyWithEnemy(userDataA, userDataB);
        patrollingEnemyWithWall(userDataA,userDataB);
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

    private void playerFeetWithEnemy(UserData userDataA, UserData userDataB, Contact contact) {
        WorldManifold worldManifold = contact.getWorldManifold();
        Vector2 normal = worldManifold.getNormal();

        if ("PlayerFeet".equals(userDataA.getName()) || "PlayerFeet".equals(userDataB.getName())) {
            UserData enemyData = (userDataA.getObject() instanceof Enemy) ? userDataA : userDataB;

            if (enemyData.getObject() instanceof Enemy enemy) {
                if (normal.y < 0) {
                    enemy.die();
                    player.b2body.setLinearVelocity(player.b2body.getLinearVelocity().x, 0);
                    player.jump(1.25f);
                }
            }
        }
    }

    private void playerBodyWithEnemy(UserData userDataA, UserData userDataB) {
        if ("PlayerBody".equals(userDataA.getName()) || "PlayerBody".equals(userDataB.getName())) {
            if (userDataA.getObject() instanceof Ladder || userDataB.getObject() instanceof Ladder) {
                player.atLadder(true);
            }

            UserData enemyData = (userDataA.getObject() instanceof Enemy) ? userDataA : userDataB;
            if ("EnemyBody".equals(enemyData.getName())) {
                player.die();
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

    private void patrollingEnemyOnEdge(UserData userDataA, UserData userDataB) {
        if ("EdgeSensor".equals(userDataA.getName()) || "EdgeSensor".equals(userDataB.getName()) ) {
            UserData edgeSensor = "EdgeSensor".equals(userDataA.getName()) ? userDataA : userDataB;

            if (edgeSensor.getObject() instanceof PatrollingEnemy enemy) {
                enemy.changeDirection();
            }
        }
    }
}
