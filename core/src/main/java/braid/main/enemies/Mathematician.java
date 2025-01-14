package braid.main.enemies;

import braid.main.Braid;
import braid.main.objects.Enemy;
import braid.main.objects.Player;
import braid.main.screens.levels.LevelScreen;
import braid.main.tools.Subtitle;
import braid.main.tools.SubtitleManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;

public class Mathematician extends Enemy implements EnemyAI{
    private SubtitleManager subtitleManager;
    private Subtitle subtitle;
    private boolean playerIsInRange = false;
    private boolean isAttacking = false;

    public Mathematician(World world, LevelScreen screen, Player player, float x, float y, boolean rewindable, String type) {
        super(world, screen, x, y, rewindable, type);
        this.subtitleManager = subtitleManager;
        defineBody();

        speed = 0.15f;

        setSprite(screen.getAtlas());

        idle = new Animation<>(0.1f, screen.getAtlas().findRegions("wissenschaftler"), Animation.PlayMode.LOOP);
        sprite.setBounds(0,0,24/ Braid.PPM, 24/Braid.PPM);
        sprite.setRegion(idle.getKeyFrame(0, true));
    }

    @Override
    public void idle() {
        this.subtitleManager = subtitleManager;
        this.subtitle = new Subtitle(text);

        subtitleManager.addSubtitle(subtitle);

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
