package braid.main.tools;

import braid.main.Items.CollectableItem;
import braid.main.overworld.LevelNode;
import braid.main.overworld.Overworld;
import braid.main.overworld.OverworldNode;
import braid.main.overworld.TransitionNode;
import braid.main.screens.menus.KeybindsMenu;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import static braid.main.tools.KeyBindings.KeyBindingsMap;
import static braid.main.tools.KeyBindings.alreadyInUse;

public class Savegames {

    public static Savegame Savegame1;
    public static Savegame Savegame2;
    public static Savegame Savegame3;

    public Array<OverworldNode> Levelnodes;

    public  Map<LevelNode,Boolean> dummyUnlockedLevels;



    public Savegames(){
        createdummyUnlockedLevels();
        Savegame1 = new Savegame(dummyUnlockedLevels);
       /* Savegame1 = new Savegame( dummyCollectables, dummyUnlockedLevels, dummyPlaytime);
        Savegame2 = new Savegame( dummyCollectables, dummyUnlockedLevels, dummyPlaytime);
        Savegame3 = new Savegame( dummyCollectables, dummyUnlockedLevels, dummyPlaytime);
        */

    }

    private void createdummyUnlockedLevels() {
        dummyUnlockedLevels = new HashMap<LevelNode,Boolean>();
        for(int i=0; i<=12; i++){
            //dummyUnlockedLevels.put(Overworld.getNode(i),true);
        }
    }


    // just returns the Savegame
    public static Savegame getSavegame(Savegame savegame){

        if(savegame == Savegame1){
            return  Savegame1;
        } else if (savegame == Savegame2) {
            return  Savegame2;
        } else if (savegame == Savegame3){
            return  Savegame3;
        }
        return Savegame3;
    }



    // saves a Game to SavegamePreferences to save it over the runtime
    public static void saveGame(Savegame savegame) {
        if(savegame == Savegame1){
            for(int i=0; i<=12; i++){
              //  PreferencesManager.getSavegame1Preferences().putBoolean(Overworld.getNode(i),Overworld.getNode(i).isUnlocked());
            }
        } else if (savegame == Savegame2) {

        } else if (savegame == Savegame3){

        }
        PreferencesManager.getKeyBindingPreferences().flush();
    }


    public static void loadKeyBindings(Savegame savegame){

    }
}
