package braid.main.tools;

import braid.main.screens.menus.KeybindsMenu;
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
        KeyBindingsMap = new HashMap<>();
        KeyBindingsMap.put("UP_KEY", Input.Keys.W);
        KeyBindingsMap.put("LEFT_KEY", Input.Keys.A);
        KeyBindingsMap.put("DOWN_KEY", Input.Keys.S);
        KeyBindingsMap.put("RIGHT_KEY", Input.Keys.D);
        KeyBindingsMap.put("SPACEBAR", Input.Keys.SPACE);
        KeyBindingsMap.put("SHIFT", Input.Keys.SHIFT_LEFT);
        KeyBindingsMap.put("INTERACT", Input.Keys.E);
        KeyBindingsMap.put("ENTER", Input.Keys.ENTER);
        KeyBindingsMap.put("ESC", Input.Keys.ESCAPE);
        KeyBindingsMap.put("HAMMER", Input.Keys.Q);
    }

    // insert an action and get the Key to do this action
    public static int getKey(String action){
        return  KeyBindingsMap.get(action);
    }

    // set an action with a key in the Hashmap
    public static void setKey(String action, int Key){
        KeyBindingsMap.put(action,Key);
    }

    // just returns the KeyBindingsMap
    public static Map<String,Integer> getKeyBindingsMap(){
        return  KeyBindingsMap;
    }

    public static void changeKeyBinding(String action){
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if(!alreadyInUse(keycode,action)) {
                    KeyBindings.setKey(action, keycode);
                    saveKeyBindings();
                    loadKeyBindings();
                }else {
                    KeybindsMenu.changeNotPossible = true;
                }
                Gdx.input.setInputProcessor(null); // reset InputProcessor
                KeybindsMenu.OverlayActive = false;
                return true;
            }
        });

    }

    // saves KeyBinds to KeyBindingsPreferences to save it over the runtime
    public static void saveKeyBindings() {
        // iterates throw the entire Map and load it into the KeyBindingsPreferences
        for (Map.Entry<String,Integer> entry : KeyBindings.getKeyBindingsMap().entrySet()){
            PreferencesManager.getKeyBindingPreferences().putInteger(entry.getKey(), entry.getValue());
        }
            PreferencesManager.getKeyBindingPreferences().flush();
    }

    // load the saved changes to the KeybindingsMap
    public static void loadKeyBindings(){
        // iterates throw the entire KeyBindingsPreferences and load it into the KeyBindingsMap
        for(String action : KeyBindings.getKeyBindingsMap().keySet()){
            int key = PreferencesManager.getKeyBindingPreferences().getInteger(action, KeyBindings.getKey(action));
            KeyBindings.setKey(action,key);
        }
    }

    // checks if another action is using this keycode
    public static boolean alreadyInUse(int keycode, String newAction){
        for(String action : KeyBindings.getKeyBindingsMap().keySet()){
            // checks if the Keycode is already in use
            if(keycode == PreferencesManager.getKeyBindingPreferences().getInteger(action, KeyBindings.getKey(action))){
                // checks if the Keycode is used by the same action
                if(keycode == PreferencesManager.getKeyBindingPreferences().getInteger(newAction)){
                    return false;
                }else {
                    return true;
                }

            }
        }

        return false;
    }

}
