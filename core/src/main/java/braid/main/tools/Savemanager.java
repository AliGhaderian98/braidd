package braid.main.tools;

import braid.main.overworld.LevelNode;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.files.FileHandle;

import java.util.HashMap;
import java.util.Map;


public class Savemanager {

    public static class Savegame {
        public int SaveGameKEY;
        public  Map<String, Array<Boolean>> Collectables;
        public Map<String, Boolean> UnlockedLevels;
        public long Playtime;

        public Savegame() {}
    }

    private static final Json SaveGamesDoc = new Json();

    public static Array<String> LevelNodes;
    public Map<String,Boolean> dummyUnlockedLevels;
    public Map<String, Array<Boolean>> dummyCollectables;
    public Array<Boolean> dummyCollectablesBool;
    public long dummyPlaytime;


    public void createSavegame(){
        fillLevelNodes();
        createDummyUnlockedLevels();
        createDummyCollectables();
        createDummyPlaytime();
        for(int i=1; i<11;i++){
            saveGame(i,dummyUnlockedLevels,dummyCollectables,dummyPlaytime);
        }
    }

    private void createDummyPlaytime() {
        dummyPlaytime = 9;
    }

    private void createDummyCollectables(){
        dummyCollectables = new HashMap<>();
        for(int i=0; i<10;i++){
            dummyCollectablesBool = new Array<>();
            dummyCollectables.put(LevelNodes.get(i),dummyCollectablesBool);
            for(int z = 0; z< 10; z++){
                dummyCollectablesBool.add(true);
            }
        }
    }

    private void createDummyUnlockedLevels() {
        dummyUnlockedLevels = new HashMap<>();
        for(int i=0; i<10;i++){
            dummyUnlockedLevels.put(LevelNodes.get(i),true);
        }
    }

    private void fillLevelNodes(){
        LevelNodes = new Array<>();
        LevelNodes.add("HBF", "UNI", "FREUDENBERG", "LUISENVIERTEL");
        LevelNodes.add("ARKADEN", "OBERBARMEN", "WEGZURUNI", "SCHLOSSBURG");
        LevelNodes.add("BAYER", "ZOO");
    }



    public static void saveGame(int SaveGameKEY, Map<String,Boolean> UnlockedLevels, Map<String, Array<Boolean>> Collectables, long Playtime) {
        Savegame saveData = new Savegame();
        saveData.UnlockedLevels = UnlockedLevels;
        saveData.SaveGameKEY = SaveGameKEY;
        saveData.Collectables = Collectables;
        saveData.Playtime = Playtime;

        FileHandle file = Gdx.files.local("SaveGameFiles/SaveGame"+ SaveGameKEY + ".json");
        file.writeString(SaveGamesDoc.toJson(saveData),false);
    }

    public Savegame loadGame(int SaveGameKEY){
        FileHandle file = Gdx.files.local("SaveGameFiles/SaveGame"+ SaveGameKEY + ".json");

        if(file.exists()){
            return SaveGamesDoc.fromJson(Savegame.class, file.readString());
        } else{
            return new Savegame();
        }
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
