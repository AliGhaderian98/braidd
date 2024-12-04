package braid.main.tools;

import braid.main.objects.Enemy;
import braid.main.objects.Ladder;
import braid.main.objects.Player;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.physics.box2d.*;

public class WorldContactListener implements ContactListener {
    private Player player;
    public WorldContactListener(Player player) {
        this.player = player;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        if ("PlayerFeet".equals(fixA.getUserData()) || "PlayerFeet".equals(fixB.getUserData())) {
            Fixture enemyFixture = (fixA.getUserData() instanceof Enemy) ? fixA : fixB;
            if (enemyFixture.getUserData() instanceof Enemy enemy) {
                enemy.die();
            }
        }

        if ("PlayerBody".equals(fixA.getUserData()) || "PlayerBody".equals(fixB.getUserData())) {
            if (fixA.getUserData() instanceof Ladder || fixB.getUserData() instanceof Ladder) {
                player.atLadder(true);
            }

            Fixture enemyFixture = (fixA.getUserData() instanceof Enemy) ? fixA : fixB;
            if (enemyFixture.getUserData() instanceof Enemy enemy && !enemy.isDead()) {
                player.die();
            }
        }

    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        if (fixA.getUserData() instanceof Ladder || fixB.getUserData() instanceof Ladder) {
            player.atLadder(false);
            player.stopClimbing();
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {

    }
}
