package braid.main.tools;

import braid.main.Braid;
import braid.main.screens.levels.LevelScreen;
import braid.main.screens.levels.MountainMap;
import braid.main.screens.levels.WegZurUniLevel;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.maps.MapLayer;

public class EventListener {
    private Braid game;
    private LevelScreen screen;
    private boolean check1, check2;

    public EventListener(Braid game, LevelScreen screen) {
        this.screen = screen;
    }

    public void hasPressedButton(boolean pressed) {
        this.check1 = pressed;
        if(canCompleteMap()) {
            loadMap();
        }
    }

    public void hasTriggeredNPC(boolean trigger) {
        this.check2 = trigger;

        if(canCompleteMap()) {
            loadMap();
        }
    }

    private boolean canCompleteMap() {
        return check1 && check2;
    }

    public void loadMap() {
        if (screen instanceof WegZurUniLevel && canCompleteMap()) {

            Screen newScreen = new MountainMap(game);
            Gdx.app.postRunnable(() -> game.setScreen(newScreen));
        }
    }
}
