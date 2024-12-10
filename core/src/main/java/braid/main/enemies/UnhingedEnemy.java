package braid.main.enemies;

import braid.main.objects.Enemy;
import braid.main.screens.levels.LevelScreen;
import com.badlogic.gdx.physics.box2d.World;

import java.util.logging.Level;

public class UnhingedEnemy extends Enemy implements EnemyAI {

    public UnhingedEnemy(World world, LevelScreen screen, float x, float y) {
        super(world, screen, x, y);
    }

    @Override
    public void idle() {

    }

    @Override
    public void attack() {

    }

    @Override
    public void defineBody() {

    }
}
