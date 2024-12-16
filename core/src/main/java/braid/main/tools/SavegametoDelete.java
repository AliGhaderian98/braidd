package braid.main.tools;

import java.util.Map;

public class SavegametoDelete {

    public static int SaveGameKEY;
    //public  Map<LevelNode, CollectableItem> Collectables;
    public  Map<String,Boolean> UnlockedLevels;
    //public Timer Playtime;



    public SavegametoDelete() {}

        /*
    public Timer getTimer(){
        return Playtime;
    }

    public void  setPlaytime(Timer Playtime){
        this.Playtime = Playtime;
    }

   public  Map<LevelNode, CollectableItem> getCollectables(){
        return Collectables;
    }
   public void  setCollectables(Map<LevelNode, CollectableItem> Collectables){
        this.Collectables = Collectables;
    }


    public void setSavegame(Map<LevelNode, CollectableItem> Collectables, Map<LevelNode,Boolean> UnlockedLevels, Timer Playtime){
        this.Collectables = Collectables;
        this.UnlockedLevels = UnlockedLevels;
        this.Playtime = Playtime;
    }
*/


    public void  setUnlockedLevels(Map<String,Boolean> UnlockedLevels){
        this.UnlockedLevels = UnlockedLevels;
    }

    public  Map<String,Boolean> getUnlockedLevels(){
        return UnlockedLevels;
    }





}
