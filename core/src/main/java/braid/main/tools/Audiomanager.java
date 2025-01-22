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
        audiomanager.load("audio/music/WEGZURUNI/WEGZURUNI2.mp3", Music.class);
        audiomanager.load("audio/music/WEGZURUNI/FutureMap2.mp3", Music.class);


        /*
        licence: UniLevelMusic.ogg
                Eigene Musik (Alister)
        */
        audiomanager.load("audio/music/UniLevelMusic.mp3", Music.class);

        /*
        licence: victory-fanfare.ogg
                Music: Orchestral Victory Fanfare
                https://freesound.org/s/470083/
                License: Attribution 4.0
        */
        audiomanager.load("audio/music/victory-fanfare.mp3", Music.class);

        /*
        licence: lofi-loop.ogg
                Aesthetic Lofi Loop
                https://freesound.org/s/679187/
                License: CC0
        */
        audiomanager.load("audio/music/lofi-loop.mp3", Music.class);

        /*
        licence: menuSound.mp3
                Scissors one hit by ibaffette --
                https://freesound.org/s/737698/ --
                License: Creative Commons 0
         */
        audiomanager.load("audio/sound/menuSound.mp3", Sound.class);

        /*
        licence: buttonClick.ogg
                Button Clicking 2 (Single)
                https://freesound.org/s/494490/
                License: CC0
        */
        audiomanager.load("audio/sound/buttonClick.ogg", Sound.class);
        audiomanager.load("audio/sound/jump.mp3", Sound.class);
        audiomanager.load("audio/sound/kill.mp3", Sound.class);
        audiomanager.load("audio/sound/land.mp3", Sound.class);
        audiomanager.load("audio/sound/Steps.mp3", Music.class);



        audiomanager.finishLoading();

        /*
        Author	NicoleMarieT
            */
        audiomanager.load("audio/music/ZooSoundtrack.mp3", Music.class);
        audiomanager.finishLoading();

        /*
        licence: menuSound.mp3
               Author: OSFX
                https://freesound.org
         */
        audiomanager.load("audio/music/SchlossBurgSoundtrack.mp3", Music.class);
        audiomanager.finishLoading();
    }

}
