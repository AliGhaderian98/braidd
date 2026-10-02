package braid.main.screens.levels;

import braid.main.Braid;
import braid.main.objects.MovingPlatform;
import braid.main.rewind.RewindController;
import braid.main.rewind.RewindableBody;
import braid.main.objects.Enemy;
import braid.main.tools.Savemanager;

public class FreudenbergLevel extends LevelScreen {

    public FreudenbergLevel(Braid game) {
        super(game, "maps/FreudenbergLevel.tmx", "packedimages/sprites.atlas", "audio/music/Freudenberg.mp3");
        levelName = "FREUDENBERG";

        player.setPosition(32/Braid.PPM, 32/Braid.PPM);
    }

    @Override
    protected LevelScreen getNewInstance() {
        return new FreudenbergLevel(game);
    }


    @Override
    public void show() {
        // Custom behavior for showing TestScreen

    }

    @Override
    public void hide() {

    }
}

