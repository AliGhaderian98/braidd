
package braid.main.screens;

import braid.main.Braid;
import braid.main.RewindController;
import braid.main.RewindableBody;
import braid.main.objects.Enemy;
import com.badlogic.gdx.utils.Array;

public class TestScreen extends LevelScreen {

    public TestScreen(Braid game) {
        super(game, "maps/wintermap.tmx", "packedimages/sprites.atlas");

        player.setPosition(32/Braid.PPM, 32/Braid.PPM);


        Enemy enemy = new Enemy(world, this);
        enemy.setRewindController(new RewindController(new RewindableBody(enemy.b2body, enemy)));
        enemies.add(enemy);

        rewindObjects.add(player.getRewindController());
        for (Enemy e : enemies) {
            rewindObjects.add(e.getRewindController());
        }
    }

    @Override
    protected LevelScreen getNewInstance() {
        return new TestScreen(game);
    }

    @Override
    public void show() {
        // Custom behavior for showing TestScreen

    }

    @Override
    public void hide() {

    }
}
