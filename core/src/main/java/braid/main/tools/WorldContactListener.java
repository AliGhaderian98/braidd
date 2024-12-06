package braid.main.tools;

import braid.main.enemies.PatrollingEnemy;
import braid.main.objects.Enemy;
import braid.main.objects.Ladder;
import braid.main.objects.Player;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.physics.box2d.*;

import java.util.Objects;

public class WorldContactListener implements ContactListener {
    private Player player;
    public WorldContactListener(Player player) {
        this.player = player;
    }

    @Override
    public void beginContact(Contact contact) {
        UserData userDataA = (UserData) contact.getFixtureA().getUserData();
        UserData userDataB = (UserData) contact.getFixtureB().getUserData();

        if ("PlayerFeet".equals(userDataA.getName()) || "PlayerFeet".equals(userDataB.getName())) {
            UserData enemyData = (userDataA.getObject() instanceof Enemy) ? userDataA : userDataB;

            if (enemyData.getObject() instanceof Enemy enemy) {
                enemy.die();
                player.b2body.setLinearVelocity(player.b2body.getLinearVelocity().x, 0);
                player.jump(1.25f);
            }
        }

        if ("PlayerBody".equals(userDataA.getName()) || "PlayerBody".equals(userDataB.getName())) {
            if (userDataA.getObject() instanceof Ladder || userDataB.getObject() instanceof Ladder) {
                player.atLadder(true);
            }

            UserData enemyData = (userDataA.getObject() instanceof Enemy) ? userDataA : userDataB;
            if ("EnemyBody".equals(enemyData.getName())) {
                player.die();
            }
        }

        if ("SideSensor".equals(userDataA.getName()) || "SideSensor".equals(userDataB.getName())) {
            UserData sideSensor = "SideSensor".equals(userDataA.getName()) ? userDataA : userDataB;
            if ("Wall".equals(userDataA.getName()) || "Wall".equals(userDataB.getName()))  {
                 PatrollingEnemy enemy = (PatrollingEnemy) sideSensor.getObject();
                 enemy.changeDirection();
                 System.out.println("AUFGERUFEN... aber direction ändert sich nicht?");
             }
        }
    }

    @Override
    public void endContact(Contact contact) {
        UserData userDataA = (UserData) contact.getFixtureA().getUserData();
        UserData userDataB = (UserData) contact.getFixtureB().getUserData();

        if (userDataA.getObject() instanceof Ladder || userDataB.getObject() instanceof Ladder) {
            player.atLadder(false);
            player.stopClimbing();
        }

        if ("EdgeSensor".equals(userDataA.getName()) || "EdgeSensor".equals(userDataB.getName()) ) {
            UserData edgeSensor = "EdgeSensor".equals(userDataA.getName()) ? userDataA : userDataB;

            if (edgeSensor.getObject() instanceof PatrollingEnemy enemy) {
                enemy.changeDirection();
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {

    }
}
