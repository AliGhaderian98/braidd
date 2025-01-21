package braid.main.tools;

import braid.main.Braid;
import braid.main.objects.Enemy;
import braid.main.screens.levels.*;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Array;

public class EventListener {
    private final Braid game;
    private final LevelScreen screen;
    private final TiledMap map;

    private Array<Enemy> enemies;

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
        repairButtonCount--;
        System.out.println("ButtonCountAll: " + repairButtonCount );
        if (repairButtonCount == 0) {
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
        } else if (allEnemiesDead()) {
            this.check1 = true;

            loadMap();
        }
    }

    public void hasChangedMusic(String musicName) {
        screen.changeMusic(musicName);
    }

    public boolean allEnemiesDead() {
        this.enemies = screen.getEnemies();

        if (enemies.isEmpty()) return false;
        for (Enemy enemy : enemies) {
            if (!enemy.isDead()) {
                System.out.println("false");
                return false;
            }
        }
        System.out.println("true");
        return true;
    }

    private boolean canCompleteMap() {
        return check1 && check2;
    }

    public void addButtonCount() {
        repairButtonCount++;
        System.out.println("ButtonCountAdd: " + repairButtonCount );

    }

    public void spawnStuff(String objectName) {
        switch (objectName) {
            case "spawnItems" -> {
                map.getLayers().get("Items").setVisible(true);
                screen.getB2WC().spawnItems();
            }
            case "spawnMovingplatform" -> {
                map.getLayers().get("MovingPlatform").setVisible(true);
                map.getLayers().get("MovingPlatformLayer").setVisible(true);
                screen.getB2WC().spawnMovingPlatform();
            }
        }
    }

    public void loadMap() {
        if (screen instanceof WegZurUniLevel && canCompleteMap()) {

            Screen newScreen = new MountainMap(game, 1);
            game.setScreen(newScreen);
        } else if (screen instanceof MountainMap && canCompleteMap()) {
            if (map.getLayers().get("PresentDoor") == null) {
                Screen newScreen = new PastMap(game);
                game.setScreen(newScreen);
            } else if (map.getLayers().get("FutureDoor") == null) {
                Screen newScreen = new PresentMap(game);
                game.setScreen(newScreen);
            } else if (map.getLayers().get("FutureDoor") != null) {
                Screen newScreen = new FutureMap(game);
                game.setScreen(newScreen);
            }

        } else if (screen instanceof PastMap && canCompleteMap()) {

            Screen newScreen = new MountainMap(game, 2);
            game.setScreen(newScreen);
        } else if (screen instanceof PresentMap && canCompleteMap()) {
            Screen newScreen = new MountainMap(game, 3);
            game.setScreen(newScreen);
        }
    }
}

