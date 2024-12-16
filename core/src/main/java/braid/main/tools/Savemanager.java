package braid.main.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.files.FileHandle;

import java.util.HashMap;
import java.util.Map;


public class Savemanager {

    public static class Savegame {
        public static int SaveGameKEY;
        //public  Map<LevelNode, CollectableItem> Collectables;
        public Map<String, Boolean> UnlockedLevels;
        //public Timer Playtime;

        public Savegame() {}
    }

    private static final Json SaveGamesDoc = new Json();

    public static Map<String,Savegame> SavegamesMap;
    public static Array<String> LevelNodes;
    public Map<String,Boolean> dummyUnlockedLevels;



    public void creaateSavegameMap(){
        createDummyUnlockedLevels();
        SavegamesMap = new HashMap<>();

        for(int i=1; i<11;i++){
            SavegamesMap.put("SaveGame"+i, new Savegame());
            Savegame.SaveGameKEY = i;
        }
    }

    private void createDummyUnlockedLevels() {
        fillLevelNodes();
        dummyUnlockedLevels = new HashMap<>();
        for(int i=0; i<10;i++){
            dummyUnlockedLevels.put(LevelNodes.get(0),true);

        }
    }

    private void fillLevelNodes(){
        LevelNodes.add("HBF", "UNI", "FREUDENBERG", "LUISENVIERTEL");
        LevelNodes.add("ARKADEN", "OBERBARMEN", "WEGZURUNI", "SCHLOSSBURG");
        LevelNodes.add("BAYER", "ZOO");
    }



    public static void saveGame(int SaveGameKEY, Map<String,Boolean> UnlockedLevels) {
        Savegame saveData = new Savegame();
        saveData.UnlockedLevels = UnlockedLevels;

        FileHandle file = Gdx.files.local("SaveGame"+ SaveGameKEY + ".json");
        file.writeString(SaveGamesDoc.toJson(saveData),false);
    }

    public Savegame loadGame(int SaveGameKEY){
        FileHandle file = Gdx.files.local("SaveGame"+ SaveGameKEY + ".json");

        if(file.exists()){
            return SaveGamesDoc.fromJson(Savegame.class, file.readString());
        } else{
            return new Savegame();
        }
    }

}
