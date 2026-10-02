package braid.main.screens.levels;

import braid.main.Braid;

public class FutureMap extends LevelScreen{

    public FutureMap(Braid game) {
        super(game, "maps/tilesets/WEGZURUNI/Future/maps/Future.tmx", "packedimages/sprites.atlas", "audio/music/WEGZURUNI/FutureMap1.mp3");
        levelName = "WEGZURUNI";

        player.setPosition(864/Braid.PPM, 576/Braid.PPM);
    }

    @Override
    protected LevelScreen getNewInstance() {
        return new FutureMap(game);
    }


    @Override
    public void show() {
        // Custom behavior for showing TestScreen

    }

    @Override
    public void hide() {

    }
}
