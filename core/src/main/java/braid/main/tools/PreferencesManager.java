package braid.main.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

// used to have a static preference for static Methods
public class PreferencesManager {
    private static com.badlogic.gdx.Preferences keyBindingPreferences;
    private static com.badlogic.gdx.Preferences SliderPreferences;
    private static com.badlogic.gdx.Preferences FullscreenPreferences;
    private static com.badlogic.gdx.Preferences Savegame1Preferences;
    private static com.badlogic.gdx.Preferences Savegame2Preferences;
    private static com.badlogic.gdx.Preferences Savegame3Preferences;

    public static Preferences getKeyBindingPreferences() {
        if (keyBindingPreferences == null) {
            keyBindingPreferences = Gdx.app.getPreferences("KeyBindingsPreferences");
        }
        return keyBindingPreferences;
    }

    public static Preferences getSliderPreferences() {
        if (SliderPreferences == null) {
            SliderPreferences = Gdx.app.getPreferences("SliderPreferences");
        }
        return SliderPreferences;
    }

    public static Preferences getFullscreenPreferences(){
        if (FullscreenPreferences == null) {
            FullscreenPreferences = Gdx.app.getPreferences("FullscreenPreferences");
        }
        return FullscreenPreferences;
    }

    public static Preferences getSavegame1Preferences(){
        if (Savegame1Preferences == null) {
            Savegame1Preferences = Gdx.app.getPreferences("Savegame1Preferences");
        }
        return Savegame1Preferences;
    }

    public static Preferences getSavegame2Preferences(){
        if (Savegame2Preferences == null) {
            Savegame2Preferences = Gdx.app.getPreferences("Savegame2Preferences");
        }
        return Savegame2Preferences;
    }

    public static Preferences getSavegame3Preferences(){
        if (Savegame3Preferences == null) {
            Savegame3Preferences = Gdx.app.getPreferences("Savegame3Preferences");
        }
        return Savegame3Preferences;
    }

}
