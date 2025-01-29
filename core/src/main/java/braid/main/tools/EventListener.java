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

    //Checks
    private boolean check1, check2;
    private int repairButtonCount;

    public EventListener(Braid game, LevelScreen screen, TiledMap map) {
        this.game = game;
        this.screen = screen;
        this.map = map;

        this.repairButtonCount = 0;

    }

    public int getRepairButton() {
        return repairButtonCount;
    }

    public void hasPressedButton(boolean pressed) {
        this.check1 = pressed;
        if (canCompleteMap()) {
            loadMap();
        }
    }

    public void hasPressedAllButtons() {
        if (repairButtonCount > 0) {
            repairButtonCount--;
        } else {
            repairButtonCount = 0;
        }

        if (repairButtonCount == 0) {
            this.check1 = true;
            if (canCompleteMap()) {
                loadMap();
            }
        } else if (screen instanceof FinaleMap && repairButtonCount <= 5) { // Es gibt insgesamt 11 interaktive Tafeln im finalen Level (2 Fake), um die Schwierigkeit moderat zu halten werden nur 6 gefordert (Diese Zahl hat eine Bedeutung und kann man im Spiel herausfinden ;-), gerne auf 0 setzen wenn es zu leicht ist.
            spawnStuff("spawnEnd");
        }
    }

    public void hasTriggeredNPC(boolean trigger) {
        this.check2 = trigger;

        // check for edge case to unlock schloss burg level from oberbarmen
        if (screen instanceof OberbarmenLevel && !Savemanager.currentsavegame.UnlockedLevels.get("SCHLOSSBURG")) {
            ((OberbarmenLevel) screen).unlockSchlossBurg();
        }

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
        Array<Enemy> enemies = screen.getEnemies();

        if (enemies.isEmpty()) return false;
        for (Enemy enemy : enemies) {
            if (!enemy.isDead()) {
                return false;
            }
        }
        return true;
    }

    private boolean canCompleteMap() {
        return check1 && check2;
    }

    public void addButtonCount() {
        repairButtonCount++;

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
            case "spawnEnd" -> {
                map.getLayers().get("End").setVisible(true);
                screen.getB2WC().spawnEnd();
            }
        }
    }


    public void loadMap() {
        if (screen instanceof WegZurUniLevel && canCompleteMap()) {
            screen.stopMusic();
            Screen newScreen = new MountainMap(game, 1);
            game.setScreen(newScreen);

        } else if (screen instanceof MountainMap && canCompleteMap()) {
            if (map.getLayers().get("PresentDoor") == null) {
                screen.stopMusic();
                Screen newScreen = new PastMap(game);
                game.setScreen(newScreen);
            } else if (map.getLayers().get("FutureDoor") == null) {
                screen.stopMusic();
                Screen newScreen = new PresentMap(game);
                game.setScreen(newScreen);
            } else if (map.getLayers().get("FutureDoor") != null) {
                screen.stopMusic();
                Screen newScreen = new FutureMap(game);
                game.setScreen(newScreen);
            }

        } else if (screen instanceof PastMap && canCompleteMap()) {
            screen.stopMusic();
            Screen newScreen = new MountainMap(game, 2);
            game.setScreen(newScreen);

        } else if (screen instanceof PresentMap && canCompleteMap()) {
            screen.stopMusic();
            Screen newScreen = new MountainMap(game, 3);
            game.setScreen(newScreen);

        } else if (screen instanceof FutureMap && !map.getLayers().get("BlackScreen").isVisible() && check2) {
            screen.stopMusic();
            Screen newScreen = new FinaleMap(game);
            game.setScreen(newScreen);
        }
    }
}

