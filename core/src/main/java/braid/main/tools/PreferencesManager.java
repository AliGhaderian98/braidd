package braid.main.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

// used to have a static preference for static Methods
public class PreferencesManager {
    private static com.badlogic.gdx.Preferences keyBindingPreferences;
    private static com.badlogic.gdx.Preferences SliderPreferences;
    private static com.badlogic.gdx.Preferences FullscreenPreferences;

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
}
