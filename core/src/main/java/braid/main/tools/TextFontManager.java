package braid.main.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class TextFontManager {
    public static BitmapFont gettextFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("textFonts/MenuTextFont.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 150;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();

        return font;
    }

    public static BitmapFont getmidTextFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("textFonts/MenuTextFont.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 80;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();

        return font;
    }

    public static BitmapFont getlittleTextFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("textFonts/MenuTextFont.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 60;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();

        return font;
    }

    public static BitmapFont getPixelFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("textFonts/pixelfont.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 40;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();

        return font;
    }
}
