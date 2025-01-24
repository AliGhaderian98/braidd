package braid.main.screens.levels;

import braid.main.Braid;
import braid.main.objects.MovingPlatform;
import braid.main.rewind.RewindController;
import braid.main.rewind.RewindableBody;
import braid.main.objects.Enemy;

public class OberbarmenLevel extends LevelScreen {

    public OberbarmenLevel(Braid game) {
        super(game, "maps/oberbarmen.tmx", "packedimages/sprites.atlas", "audio/music/oberbarmen.mp3");
        levelName = "OBERBARMEN";

        player.setPosition(32/Braid.PPM, 32/Braid.PPM);
    }

    @Override
    protected LevelScreen getNewInstance() {
        return new OberbarmenLevel(game);
    }


    @Override
    public void show() {
        // Custom behavior for showing TestScreen

    }

    @Override
    public void hide() {

    }
}

