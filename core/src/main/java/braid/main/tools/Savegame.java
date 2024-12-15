package braid.main.tools;

import braid.main.Items.CollectableItem;
import braid.main.overworld.LevelNode;

import java.util.Map;
import java.util.Timer;

public class Savegame {

    //public  Map<LevelNode, CollectableItem> Collectables;
    public  Map<LevelNode,Boolean> UnlockedLevels;
    //public Timer Playtime;

    public Savegame(Map<LevelNode,Boolean> UnlockedLevels) {
        // this.Collectables = Collectables;
        this.UnlockedLevels = UnlockedLevels;
        //this.Playtime = Playtime;
    }
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


    public void  setUnlockedLevels(Map<LevelNode,Boolean> UnlockedLevels){
        this.UnlockedLevels = UnlockedLevels;
    }

    public Map<LevelNode,Boolean> getUnlockedLevels(){
        return UnlockedLevels;
    }



}
