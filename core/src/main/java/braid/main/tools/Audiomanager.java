package braid.main.tools;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class Audiomanager {
    public static AssetManager audiomanager;

    public static void audiomanager() {
        audiomanager = new AssetManager();


        /*
        licence: background_music.mp3
                Orchestral Funny | Christmas Time by Alex-Productions |
                https://youtu.be/fXkl1yEw0R0
                Music promoted by https://onsound.eu/
         */
            audiomanager.load("audio/music/background_music.mp3", Music.class);


        /*
        licence: menuSound.mp3
                Scissors one hit by ibaffette --
                https://freesound.org/s/737698/ --
                License: Creative Commons 0
         */
        audiomanager.load("audio/sound/menuSound.mp3", Sound.class);
        audiomanager.finishLoading();
    }

}
