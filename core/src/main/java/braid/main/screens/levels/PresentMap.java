package braid.main.screens.levels;

import braid.main.Braid;

public class PresentMap extends LevelScreen{

    public PresentMap(Braid game) {
        super(game, "maps/tilesets/WEGZURUNI/Present/maps/WEGZURUNI2.tmx", "packedimages/sprites.atlas", "audio/music/background_music.mp3");

        player.setPosition(272/Braid.PPM, 320/Braid.PPM);
    }
    @Override
    protected LevelScreen getNewInstance() {
        return new PastMap(game);
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }
}
