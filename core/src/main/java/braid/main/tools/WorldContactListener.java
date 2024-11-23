package braid.main.tools;

import braid.main.objects.Ladder;
import braid.main.objects.Player;
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

        if (fixA.getUserData() instanceof Ladder || fixB.getUserData() instanceof Ladder) {
            player.atLadder(true);
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        if ((fixA.getUserData() instanceof Ladder || fixB.getUserData() instanceof Ladder) && !player.isClimbing()) {
            player.atLadder(false);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {

    }
}
