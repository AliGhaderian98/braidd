package braid.main;
//Steuerung und Logik vom Char und erste Logik für Gegner (Invoker?)
import braid.main.screens.StartMenu;
//import braid.main.screens.PauseScreen;    Auskommentiert, da die KLasse noch nicht existiert bei mir (Mike)
import braid.main.screens.TestScreen;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/***********
 Hauptklasse des Spiels, die zum Management aller anderen Teile dient.
 ***********/

/**
 * {@link ApplicationListener} implementation shared by all platforms.
 */
public class Braid extends Game {

  public static final int V_WIDTH = 240; // alt: 240
  public static final int V_HEIGHT = 160; // alt: 160
  public static final float PPM = 100;

  public SpriteBatch batch;


  @Override
  public void create() {

    Gdx.graphics.setWindowedMode(800, 600);
    batch = new SpriteBatch();
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
