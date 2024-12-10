package braid.main.screens.levels;

import braid.main.Braid;
import braid.main.rewind.RewindController;
import braid.main.rewind.RewindableBody;
import braid.main.objects.Enemy;


public class TestLevel extends LevelScreen {

    public TestLevel(Braid game) {
        super(game, "maps/wintermap.tmx", "packedimages/sprites.atlas", "audio/music/background_music.mp3");

        player.setPosition(32/Braid.PPM, 32/Braid.PPM);


        rewindObjects.add(player.getRewindController());
        for (Enemy e : enemies) {
            rewindObjects.add(e.getRewindController());
        }
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
