package braid.main.screens.levels;

import braid.main.Braid;

public class MountainMap extends LevelScreen{

    public MountainMap(Braid game) {
        super(game, "maps/tilesets/WEGZURUNI/QuantenEbene/maps/Mountain.tmx", "packedimages/sprites.atlas", "audio/music/background_music.mp3");
        levelName = "Mountain";

        player.setPosition(64/Braid.PPM, 160/Braid.PPM);
    }
    @Override
    protected LevelScreen getNewInstance() {
        return new MountainMap(game);
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }
}
