package braid.main.tools;

import braid.main.Braid;
import braid.main.screens.levels.LevelScreen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class SubtitleManager {

    private final Array<Subtitle> subtitles;
    private final BitmapFont font;
    private final SpriteBatch batch;

    private final float timeBetweenLetters = 0.05f;
    private float timeElapsed = 0;


    public SubtitleManager(LevelScreen screen) {
        subtitles = new Array<>();
        //font = new BitmapFont();
        font = TextFontManager.getPixelFont();
        batch = new SpriteBatch();

    }

    public void addSubtitle(Subtitle text) {
        subtitles.add(text);
    }


    public void update(float delta) {
        for (Subtitle subtitle : subtitles) {
            if (subtitle.getFullText() != null && subtitle.isShowing()) {
                subtitle.setTime(subtitle.getTime()+delta);

                int lettersToShow = (int) (subtitle.getTime() / timeBetweenLetters);

                if (lettersToShow > subtitle.getFullText().length()) {
                    lettersToShow = subtitle.getFullText().length();
                }
                subtitle.setCurrentText(subtitle.getFullText().substring(0, lettersToShow));
            } else {
                subtitle.setCurrentText("");
                subtitle.setTime(0);
            }
        }
    }

    public void render(float delta) {
        update(delta);
        batch.begin();
        for (Subtitle subtitle : subtitles) {
            if (subtitle.getCurrentText() != null && subtitle.isShowing()) {
                font.draw(batch, subtitle.getCurrentText(), subtitle.posX,subtitle.posY);
            }
        }
        batch.end();
    }

}
