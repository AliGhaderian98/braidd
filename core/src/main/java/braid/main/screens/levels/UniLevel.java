package braid.main.screens.levels;

import braid.main.Braid;
import braid.main.objects.MovingPlatform;
import braid.main.rewind.RewindController;
import braid.main.rewind.RewindableBody;
import braid.main.objects.Enemy;

public class UniLevel extends LevelScreen {

    public UniLevel(Braid game) {
        super(game, "maps/UniLevel.tmx", "packedimages/sprites.atlas", "audio/music/UniLevelMusic.mp3");
        levelName = "UNI";

        player.setPosition(32/Braid.PPM, 898/Braid.PPM);
    }

    @Override
    protected LevelScreen getNewInstance() {
        return new UniLevel(game);
    }


    @Override
    public void show() {
        // Custom behavior for showing TestScreen

    }

    @Override
    public void hide() {

    }
}

