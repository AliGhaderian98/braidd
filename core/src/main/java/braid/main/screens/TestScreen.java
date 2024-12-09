
package braid.main.screens;

import braid.main.Braid;

public class TestScreen extends LevelScreen {

    public TestScreen(Braid game) {
        super(game, "maps/wintermap.tmx", "packedimages/sprites.atlas");

    }

    @Override
    protected LevelScreen getNewInstance() {
        return new TestScreen(game);
    }

    @Override
    public void show() {
        // Custom behavior for showing TestScreen

    }

    @Override
    public void hide() {

    }
}
