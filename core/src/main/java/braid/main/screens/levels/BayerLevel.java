package braid.main.screens.levels;

import braid.main.Braid;
import braid.main.objects.MovingPlatform;
import braid.main.rewind.RewindController;
import braid.main.rewind.RewindableBody;
import braid.main.objects.Enemy;

public class BayerLevel extends LevelScreen {

    public BayerLevel(Braid game) {
        super(game, "maps/Bayer.tmx", "packedimages/sprites.atlas", "audio/music/bayersoundtrack.mp3");
        levelName = "BAYER";

        player.setPosition(32/Braid.PPM, 100/Braid.PPM);
    }

    @Override
    protected LevelScreen getNewInstance() {
        return new BayerLevel(game);
    }


    @Override
    public void show() {
        // Custom behavior for showing TestScreen

    }

    @Override
    public void hide() {

    }
}

