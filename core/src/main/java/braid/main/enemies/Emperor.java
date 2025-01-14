package braid.main.enemies;

import braid.main.objects.Enemy;
import braid.main.screens.levels.LevelScreen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;

public class Emperor extends Enemy implements EnemyAI{
    public Emperor(World world, LevelScreen screen, float x, float y, boolean rewindable, String type) {
        super(world, screen, x, y, rewindable, type);
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

    @Override
    public void setSprite(TextureAtlas atlas) {

    }
}
