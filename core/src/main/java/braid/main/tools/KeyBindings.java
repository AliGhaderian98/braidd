package braid.main.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

import java.util.HashMap;
import java.util.Map;


public class KeyBindings {
    // KeyBindings used in the Game
    public static Map<String, Integer> KeyBindingsMap;

    // standard Keybindings
    public static void standardKeybindings(){
        KeyBindingsMap = new HashMap<String, Integer>();
        KeyBindingsMap.put("UP_KEY", Input.Keys.W);
        KeyBindingsMap.put("LEFT_KEY", Input.Keys.A);
        KeyBindingsMap.put("DOWN_KEY", Input.Keys.S);
        KeyBindingsMap.put("RIGHT_KEY", Input.Keys.D);
        KeyBindingsMap.put("SPACEBAR", Input.Keys.SPACE);
        KeyBindingsMap.put("SHIFT", Input.Keys.SHIFT_LEFT);
        KeyBindingsMap.put("INTERACT", Input.Keys.E);
    }

    // insert an action and get the Key to do this action
    public static int getKey(String action){
        return KeyBindingsMap.getOrDefault(action,-1);
    }

    // set an action with a key in the Hashmap
    public static void setKey(String action, int Key){
        KeyBindingsMap.put(action,Key);
    }

    // just returns the KeyBindingsMap
    public static Map<String,Integer> getKeyBindingsMap(){
        return KeyBindingsMap;
    }

    // TODO fix changeBinding (irgendwo hängt es noch)
    public static void changeKeyBinding(String action){
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                //System.out.println("enter new KEy");
                KeyBindings.setKey(action, keycode);
                Gdx.input.setInputProcessor(null); // reset InputProcessor
                return true;
            }
        });

    }

    // saves KeyBinds to KeyBindingsPreferences to save it over the runtime
    public static void saveKeyBindings() {

        // iterates throw the entire Map and load it into the KeyBindingsPreferences
        for (Map.Entry<String,Integer> entry : KeyBindings.getKeyBindingsMap().entrySet()){
            PreferencesManager.getPreferences().putInteger(entry.getKey(), entry.getValue());
        }
            PreferencesManager.getPreferences().flush();
    }

    // load the saved changes to the KeybindingsMap
    public static void loadKeyBindings(){
        // iterates throw the entire KeyBindingsPreferences and load it into the KeyBindingsMap
        for(String action : KeyBindings.getKeyBindingsMap().keySet()){
            int key = PreferencesManager.getPreferences().getInteger(action, KeyBindings.getKey(action));
            KeyBindings.setKey(action,key);
        }
    }

}
