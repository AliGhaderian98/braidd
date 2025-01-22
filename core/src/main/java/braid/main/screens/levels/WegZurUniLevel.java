package braid.main.screens.levels;

import braid.main.Braid;

public class WegZurUniLevel extends LevelScreen {

    public WegZurUniLevel(Braid game) {
        super(game, "maps/tilesets/WEGZURUNI/Finale/maps/Finale.tmx", "packedimages/sprites.atlas", "audio/music/WEGZURUNI/Finale.mp3");
        levelName = "WEGZURUNI";

        player.setPosition(704/Braid.PPM, 48/Braid.PPM);
    }

    @Override
    protected LevelScreen getNewInstance() {
        return new WegZurUniLevel(game);
    }


    @Override
    public void show() {
        // Custom behavior for showing TestScreen

    }

    @Override
    public void hide() {

    }
}

