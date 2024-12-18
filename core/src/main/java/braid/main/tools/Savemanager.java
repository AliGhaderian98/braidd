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
        //public  Map<LevelNode, CollectableItem> Collectables;
        public Map<String, Boolean> UnlockedLevels;
        //public Timer Playtime;

        public Savegame() {}
    }

    private static final Json SaveGamesDoc = new Json();

    public static Map<String,Savegame> SavegamesMap;
    public static Array<String> LevelNodes;
    public Map<String,Boolean> dummyUnlockedLevels;



    public void createSavegame(){
        createDummyUnlockedLevels();

        for(int i=1; i<11;i++){
            saveGame(i,dummyUnlockedLevels);
        }
        fillLevelNodes();
        printSavegame(loadGame(1));
        printSavegame(loadGame(2));
        printSavegame(loadGame(6));

    }

    private void createDummyUnlockedLevels() {
        fillLevelNodes();
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



    public static void saveGame(int SaveGameKEY, Map<String,Boolean> UnlockedLevels) {
        Savegame saveData = new Savegame();
        saveData.UnlockedLevels = UnlockedLevels;
        saveData.SaveGameKEY = SaveGameKEY;

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

    public void printSavegame(Savegame savegame){
        System.out.println("Savegame: " + savegame.SaveGameKEY);

        for(int i=0;i<10;i++ ){
            System.out.println(LevelNodes.get(i) + ": "+ savegame.UnlockedLevels.get(LevelNodes.get(i)));
        }
        System.out.println(" ");
    }

}
