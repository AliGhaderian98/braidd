package braid.main.tools;

import braid.main.overworld.Overworld;
import braid.main.screens.huds.LevelHUD;
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
        public String SaveGameName;
        public Map<String, Boolean> UnlockedLevels;
        public String lastLevel;
        public boolean wegZurUniJustUnlocked;
        public Map<String, Boolean> FinishedLevels;
        public long Playtime;
        public Map<String, Array<Boolean>> Collectables;
        public Map<String, Long> BestTimes;

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
    public static void createSavegame(int freeSpot){

        // already given
        currentsavegame.SaveGameName = SavegameMenu.newSavegameName;
        currentsavegame.SaveGameKEY = freeSpot;

        fillLevelNodes();
        if (currentsavegame.SaveGameName.equals("testAllLevelsUnlocked"))
            createUnlockedSavegame();
        else
            createDummySavegame();
        saveGame(false);
    }

    // DummyCreation
    private static void createDummySavegame() {
        currentsavegame.Collectables = new HashMap<>();
        for(int i=0; i<10;i++){
            dummyCollectablesBool = new Array<>();
            currentsavegame.Collectables.put(LevelNodes.get(i),dummyCollectablesBool);
            for(int z = 0; z < 5; z++){
                dummyCollectablesBool.add(false);
            }
        }

        currentsavegame.BestTimes = new HashMap<>();
        for(int i=0; i<10;i++){
            currentsavegame.BestTimes.put(LevelNodes.get(i), 0L);
        }

        currentsavegame.UnlockedLevels = new HashMap<>();
        for(int i=0; i<10;i++){
            currentsavegame.UnlockedLevels.put(LevelNodes.get(i),false);
        }
        currentsavegame.UnlockedLevels.put("UNI", true);
        currentsavegame.lastLevel = "UNI";

        currentsavegame.wegZurUniJustUnlocked = false;

        currentsavegame.FinishedLevels = new HashMap<>();
        for(int i=0; i<10;i++){
            currentsavegame.FinishedLevels.put(LevelNodes.get(i),false);
        }
    }

    private static void createUnlockedSavegame() {
        currentsavegame.Collectables = new HashMap<>();
        for(int i=0; i<10;i++){
            dummyCollectablesBool = new Array<>();
            currentsavegame.Collectables.put(LevelNodes.get(i),dummyCollectablesBool);
            for(int z = 0; z < 5; z++){
                dummyCollectablesBool.add(false);
            }
        }

        currentsavegame.BestTimes = new HashMap<>();
        for(int i=0; i<10;i++){
            currentsavegame.BestTimes.put(LevelNodes.get(i), 0L);
        }

        currentsavegame.UnlockedLevels = new HashMap<>();
        for(int i=0; i<10;i++){
            currentsavegame.UnlockedLevels.put(LevelNodes.get(i),true);
        }
        currentsavegame.UnlockedLevels.put("UNI", true);
        currentsavegame.lastLevel = "UNI";

        currentsavegame.wegZurUniJustUnlocked = false;

        currentsavegame.FinishedLevels = new HashMap<>();
        for(int i=0; i<10;i++){
            currentsavegame.FinishedLevels.put(LevelNodes.get(i),false);
        }
    }


    private static void fillLevelNodes(){
        LevelNodes = new Array<>();
        LevelNodes.add("HBF", "UNI", "FREUDENBERG", "LUISENVIERTEL");
        LevelNodes.add("ARKADEN", "OBERBARMEN", "WEGZURUNI", "SCHLOSSBURG");
        LevelNodes.add("BAYER", "ZOO");
    }

    // saves the Game into an external File
    public static void saveGame(boolean updatePlaytime) {
        Savegame saveData = new Savegame();
        if (updatePlaytime) { updatePlaytime(); }

        saveData.SaveGameName = currentsavegame.SaveGameName;
        saveData.UnlockedLevels = currentsavegame.UnlockedLevels;
        saveData.SaveGameKEY = currentsavegame.SaveGameKEY;
        saveData.Collectables = currentsavegame.Collectables;
        saveData.Playtime = currentsavegame.Playtime;
        saveData.lastLevel = currentsavegame.lastLevel;
        saveData.wegZurUniJustUnlocked = currentsavegame.wegZurUniJustUnlocked;
        saveData.FinishedLevels = currentsavegame.FinishedLevels;
        saveData.BestTimes = currentsavegame.BestTimes;

        FileHandle file = Gdx.files.local("SaveGameFiles/SaveGame"+ currentsavegame.SaveGameKEY + ".json");
        file.writeString(SaveGamesDoc.prettyPrint(saveData),false);
    }

    // load from the external file (into Overworld)
    public static Savegame loadGame(int SaveGameKEY){
        FileHandle file = Gdx.files.local("SaveGameFiles/SaveGame"+ SaveGameKEY + ".json");

        if(file.exists()){
            return SaveGamesDoc.fromJson(Savegame.class, file.readString());
        } else{
            currentsavegame = new Savegame();
            createSavegame(SaveGameKEY);
            return currentsavegame;
        }
    }

    public static void deleteSavegame(int SaveGameKeY){
        FileHandle file = Gdx.files.local("SaveGameFiles/SaveGame"+ SaveGameKeY + ".json");

        if(file.exists()){
            file.delete();
        }
    }

    public static boolean existGame(int SaveGameKEY){
        FileHandle file = Gdx.files.local("SaveGameFiles/SaveGame"+ SaveGameKEY + ".json");
        return file.exists();
    }

    public static int findOpenspot() {
        for (int i=1; 11>=i; i++){
            if(!existGame(i)){
                return i;
            }
        }
        return 0;
    }

    public static void rebalanceSavegames() {
        for (int i = 1; i <= 11; i++) {
            if (findOpenspot() <= CountTotalSavegames() && existGame(i) && i > 1 && CountTotalSavegames() != 0) {
                currentsavegame = loadGame(i);
                currentsavegame.SaveGameKEY = findOpenspot();
                saveGame(false);
                deleteSavegame(i);
            }
        }
    }

    public static int CountTotalSavegames(){
        int totalSavegame = 0;
        for(int i=1;i<=10;i++){
            if(existGame(i)){
                totalSavegame++;
            }
        }
        return totalSavegame;
    }

    public static void updatePlaytime(){
        totalSessionTime = System.currentTimeMillis() - playtimeStart;
        currentsavegame.Playtime += totalSessionTime;
    }

    public static void unlockNextLevel(String currentLevel) {
        currentsavegame.FinishedLevels.put(currentLevel, true);
        checkForBestTime(currentLevel, LevelHUD.getElapsedTime());

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
            case "OBERBARMEN", "BAYER", "ZOO" -> unlockWegZurUni();
            case "SCHLOSSBURG", "WEGZURUNI" -> {}
            default -> throw new IllegalStateException("Unknown Level Name: " + currentLevel);
        }
    }

    public static void unlockSchlossBurg() {
        Savemanager.currentsavegame.UnlockedLevels.put("SCHLOSSBURG", true);
    }

    private static void unlockWegZurUni() {
        if (currentsavegame.FinishedLevels.get("OBERBARMEN") &&
            currentsavegame.FinishedLevels.get("ZOO") &&
            currentsavegame.FinishedLevels.get("BAYER")) {
            if (!currentsavegame.UnlockedLevels.get("WEGZURUNI"))
                currentsavegame.wegZurUniJustUnlocked = true;
            currentsavegame.UnlockedLevels.put("WEGZURUNI", true);
        }
    }

    private static void checkForBestTime(String levelName, long time) {
        // only update stored time as best if no best has been recorded or if it is smaller than recorded best
        long currentBest = currentsavegame.BestTimes.get(levelName);
        if (currentBest == 0L || currentBest > time)
            currentsavegame.BestTimes.put(levelName, time);
    }

    public static void unlockCollectable(int ID, String level) {
        if (currentsavegame.Collectables.containsKey(level)) {
            Array<Boolean> collectablesArray = currentsavegame.Collectables.get(level);
            if (collectablesArray != null && collectablesArray.size > ID) {
                collectablesArray.set(ID, true);
            } else {
                System.err.println("Given Collectable ID of '"+ID+"' is out of range: max of 5 Collectables per Level allowed!");
            }
        }
    }

    public static int AmountUnlockedLevels(Savegame savegame){
        int unlockedlevels = 0;
        for(Boolean entry : savegame.UnlockedLevels.values()){
            if(entry)
                unlockedlevels++;
        }
    return unlockedlevels;
    }

    public static long SumBestTimes(Savegame savegame){
        long sumBestTimes = 0L;
        for(long time : savegame.BestTimes.values()){
            sumBestTimes += time;
        }
        return sumBestTimes;
    }

    public static int AmountFoundCollectables(Savegame savegame){
        int FoundCollectables = 0;
        for(Array<Boolean> entry : savegame.Collectables.values()){
            for(Boolean Value : entry){
                if(Value){
                    FoundCollectables++;

                }
            }
        }
        return FoundCollectables;
    }

    public static String getSavegameName(Savegame savegame){
        if(savegame.SaveGameName == null){
            return "Savegame " + savegame.SaveGameKEY;
        } else {
            return savegame.SaveGameName;
        }
    }

    public static String getSavegamePlaytimeTOString(Savegame savegame){
        // init var for calculation and String return

        long PlaytimeInMilli = savegame.Playtime;
        int h = 0;
        int min = 0;
        int sec = 0 ;

        while (PlaytimeInMilli > 1000){ // as long as Playtime is grader than 1 second
            if(PlaytimeInMilli >=  3600000){  // 1 hour = 3.600.000
                h++;
                PlaytimeInMilli = PlaytimeInMilli - 3600000;

            }else if (PlaytimeInMilli >= 60000 ){  // 1 minute = 60.000
                min++;
                PlaytimeInMilli = PlaytimeInMilli - 60000;


            } else {   // 1 second
                sec++;
                PlaytimeInMilli = PlaytimeInMilli - 1000;
            }
        }
        return h + "h "+ min + "min " + sec + "sec ";
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
