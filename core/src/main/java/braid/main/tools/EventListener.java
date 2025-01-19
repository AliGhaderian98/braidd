package braid.main.tools;

import braid.main.Braid;
import braid.main.screens.levels.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;

import java.util.Objects;

public class EventListener {
    private Braid game;
    private LevelScreen screen;
    private TiledMap map;

    //Checks
    private boolean check1, check2;
    private int repairButtonCount;

    public EventListener(Braid game, LevelScreen screen, TiledMap map) {
        this.game = game;
        this.screen = screen;
        this.map = map;
    }

    public void hasPressedButton(boolean pressed) {
        this.check1 = pressed;
        if (canCompleteMap()) {
            loadMap();
        }
    }

    public void hasPressedAllButtons() {
        System.out.println("gotSignal");
        repairButtonCount--;
        System.out.println("Count: "+ repairButtonCount);
        if(repairButtonCount == 0) {
            this.check1 = true;
            if (canCompleteMap()) {
                loadMap();
            }
        }
    }

    public void hasTriggeredNPC(boolean trigger) {
        this.check2 = trigger;

        if (canCompleteMap()) {
            loadMap();
        }
    }

    private boolean canCompleteMap() {
        return check1 && check2;
    }

    public void addButtonCount() {
        repairButtonCount++;
    }

    public void loadMap() {
        System.out.println("loadMap");
        if (screen instanceof WegZurUniLevel && canCompleteMap()) {

            Screen newScreen = new MountainMap(game, 1);
            game.setScreen(newScreen);
        } else if (screen instanceof MountainMap && canCompleteMap()) {

           Screen newScreen = new PastMap(game);
           game.setScreen(newScreen);

        } else if (screen instanceof PastMap && canCompleteMap()) {

            Screen newScreen = new MountainMap(game, 2);
            game.setScreen(newScreen);
        } // else if (screen instanceof FutureMap && canCompleteMap()) {
//            Screen newScreen = new Finale(game);
//            game.setScreen(newScreen);
//        }
    }
}

