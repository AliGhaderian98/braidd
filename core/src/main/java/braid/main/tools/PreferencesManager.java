package braid.main.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

// used to have a static preference for static Methods
public class PreferencesManager {
    private static com.badlogic.gdx.Preferences preferences;

    public static Preferences getPreferences() {
        if (preferences == null) {
            preferences = Gdx.app.getPreferences("KeyBindingsPreferences");
        }
        return preferences;
    }
}
