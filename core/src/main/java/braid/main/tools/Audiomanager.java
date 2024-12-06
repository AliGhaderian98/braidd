package braid.main.tools;

import braid.main.screens.OptionMenu;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class Audiomanager {
    public static AssetManager audiomanager;

    public static void audiomanager() {
        audiomanager = new AssetManager();
        audiomanager.load("audio/music/background_music.mp3", Music.class);
        audiomanager.load("audio/sound/menuSound.mp3", Sound.class);
        audiomanager.finishLoading();
    }

}
