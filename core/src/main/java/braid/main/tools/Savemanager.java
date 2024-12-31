package braid.main.tools;

import braid.main.screens.menus.SavegameMenu;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.files.FileHandle;

import java.util.HashMap;
import java.util.Map;


public class Savemanager {

    // These Parts will be Saved over Runtime
    public static class Savegame {
        public int SaveGameKEY;
        public String SaveGAmeName;
        public Map<String, Array<Boolean>> Collectables;
        public Map<String, Boolean> UnlockedLevels;
        public int numUnlockedBeforeWegZurUni;
        public long Playtime;

        public Savegame() {}
    }

    // Savegame in use
    public static Savegame currentsavegame;

    private static final Json SaveGamesDoc = new Json();

    // used to calculate over all Playtime
    public static long playtimeStart;
    public static long totalSessionTime;

    // LevelNodes for creation
    public static Array<String> LevelNodes;

    // dummyArray to fill with Information
    public static Array<Boolean> dummyCollectablesBool;


    // to create dummyObjects
    public static void createSavegame(){
        fillLevelNodes();
        createDummySavegame();
        saveGame();
    }

    private static void createDummySavegame() {
        currentsavegame.SaveGameKEY = SavegameMenu.currentSavegamKey;
        currentsavegame.Collectables = new HashMap<>();
        for(int i=0; i<10;i++){
            dummyCollectablesBool = new Array<>();
            currentsavegame.Collectables.put(LevelNodes.get(i),dummyCollectablesBool);
            for(int z = 0; z< 10; z++){
                dummyCollectablesBool.add(false);
            }
        }

        currentsavegame.UnlockedLevels = new HashMap<>();
        for(int i=0; i<10;i++){
            currentsavegame.UnlockedLevels.put(LevelNodes.get(i),true);
        }

        currentsavegame.numUnlockedBeforeWegZurUni = 0;
    }


    private static void fillLevelNodes(){
        LevelNodes = new Array<>();
        LevelNodes.add("HBF", "UNI", "FREUDENBERG", "LUISENVIERTEL");
        LevelNodes.add("ARKADEN", "OBERBARMEN", "WEGZURUNI", "SCHLOSSBURG");
        LevelNodes.add("BAYER", "ZOO");
    }

    // saves the Game into an external File
    public static void saveGame() {
        Savegame saveData = new Savegame();
        updatePlaytime();
        saveData.UnlockedLevels = currentsavegame.UnlockedLevels;
        saveData.SaveGameKEY = currentsavegame.SaveGameKEY;
        saveData.Collectables = currentsavegame.Collectables;
        saveData.Playtime = currentsavegame.Playtime;
        saveData.numUnlockedBeforeWegZurUni = currentsavegame.numUnlockedBeforeWegZurUni;

        FileHandle file = Gdx.files.local("SaveGameFiles/SaveGame"+ currentsavegame.SaveGameKEY + ".json");
        file.writeString(SaveGamesDoc.toJson(saveData),false);
    }

    // load from the external file (into Overworld)
    public static Savegame loadGame(int SaveGameKEY){
        FileHandle file = Gdx.files.local("SaveGameFiles/SaveGame"+ SaveGameKEY + ".json");

        if(file.exists()){
            return SaveGamesDoc.fromJson(Savegame.class, file.readString());
        } else{
           currentsavegame = new Savegame();
            createSavegame();
            return currentsavegame;
        }
    }

    public static boolean existGame(int SaveGameKEY){
        FileHandle file = Gdx.files.local("SaveGameFiles/SaveGame"+ SaveGameKEY + ".json");
        return file.exists();
    }

    public static void updatePlaytime(){
        totalSessionTime = System.currentTimeMillis() - playtimeStart;
        currentsavegame.Playtime += totalSessionTime;
    }

    public static void unlockNextLevel(String currentLevel) {
        switch (currentLevel) {
            case "UNI" -> currentsavegame.UnlockedLevels.put("FREUDENBERG", true);
            case "FREUDENBERG" -> currentsavegame.UnlockedLevels.put("ARKADEN", true);
            case "ARKADEN" -> currentsavegame.UnlockedLevels.put("LUISENVIERTEL", true);
            case "LUISENVIERTEL" -> currentsavegame.UnlockedLevels.put("HBF", true);
            case "HBF" -> {
                currentsavegame.UnlockedLevels.put("OBERBARMEN", true);
                currentsavegame.UnlockedLevels.put("ZOO", true);
                currentsavegame.UnlockedLevels.put("BAYER", true);
            }
            case "OBERBARMEN", "BAYER", "ZOO" -> {
                currentsavegame.numUnlockedBeforeWegZurUni += 1;
                unlockWegZurUni();
            }
            default -> throw new IllegalStateException("Unknown Leve Name: " + currentLevel);
        }
    }

    public static void unlockSchlossBurg() {
        Savemanager.currentsavegame.UnlockedLevels.put("SCHLOSSBURG", true);
    }

    private static void unlockWegZurUni() {
        if (currentsavegame.numUnlockedBeforeWegZurUni >= 3)
            currentsavegame.UnlockedLevels.put("WEGZURUNI", true);
    }


    // debug Methode
    public void printSavegame(Savegame savegame){
        System.out.println("Savegame: " + savegame.SaveGameKEY);
        System.out.println(savegame.Playtime);

        for(int i=0;i<10;i++ ){
            System.out.println(LevelNodes.get(i) + ": "+ savegame.UnlockedLevels.get(LevelNodes.get(i)));
            System.out.println(LevelNodes.get(i) + ": "+ savegame.Collectables.get(LevelNodes.get(i)));
        }
        System.out.println(" ");

    }

}
