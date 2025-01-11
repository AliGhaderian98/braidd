package braid.main.screens.levels;

import braid.main.Braid;
import braid.main.objects.MovingPlatform;
import braid.main.rewind.RewindController;
import braid.main.rewind.RewindableBody;
import braid.main.objects.Enemy;

public class FreudenbergLevel extends LevelScreen {

    public FreudenbergLevel(Braid game) {
        super(game, "maps/wintermap.tmx", "packedimages/sprites.atlas", "audio/music/background_music.mp3");

        player.setPosition(32/Braid.PPM, 32/Braid.PPM);
    }

    @Override
    protected LevelScreen getNewInstance() {
        return new TestLevel(game);
    }


    @Override
    public void show() {
        // Custom behavior for showing TestScreen

    }

    @Override
    public void hide() {

    }
}

