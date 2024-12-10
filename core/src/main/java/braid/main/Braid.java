package braid.main;
//Steuerung und Logik vom Char und erste Logik für Gegner (Invoker?)
import braid.main.screens.OptionMenu;
import braid.main.screens.StartMenu;
//import braid.main.screens.PauseScreen;    Auskommentiert, da die KLasse noch nicht existiert bei mir (Mike)
import braid.main.screens.TestScreen;
import braid.main.tools.Audiomanager;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Map;

/***********
 Hauptklasse des Spiels, die zum Management aller anderen Teile dient.
 ***********/

/**
 * {@link ApplicationListener} implementation shared by all platforms.
 */
public class Braid extends Game {

    // Virtual Resolution
    public static final int V_WIDTH = 3840; // alt: 240
    public static final int V_HEIGHT = 2160; // alt: 160
    public static final float PPM = 100;

    public static final Color BUWColor =  new Color(153f / 255f, 182f / 255f, 66f / 255f, 1);


    public SpriteBatch batch;

    @Override
    public void create() {

        Gdx.graphics.setWindowedMode(1280, 720);
        batch = new SpriteBatch();
        Audiomanager.audiomanager();
        setScreen(new StartMenu(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }



}
