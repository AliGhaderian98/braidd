package braid.main.screens.levels;

import braid.main.Braid;
import braid.main.objects.MovingPlatform;
import braid.main.rewind.RewindController;
import braid.main.rewind.RewindableBody;
import braid.main.objects.Enemy;

public class ZooLevel extends LevelScreen {

    public ZooLevel(Braid game) {
        super(game, "maps/ZooLevel.tmx", "packedimages/sprites.atlas", "audio/music/background_music.mp3");
        levelName = "ZOO";

        // debugSettings
       //player.setPosition(4550/Braid.PPM, 1050/Braid.PPM); // in dem Flying Platforms
        //player.setPosition(2000/Braid.PPM, 1000 /Braid.PPM); // oben in der Kuppel
        player.setPosition(7200/Braid.PPM, 1000 /Braid.PPM); // vor dem löwengehege

        // real Settings
       //player.setPosition(32/Braid.PPM, 54/Braid.PPM);

    }

    @Override
    protected LevelScreen getNewInstance() {
    return new ZooLevel(game);
    }


    @Override
    public void show() {
        // Custom behavior for showing TestScreen

    }

    @Override
    public void hide() {

    }
}

