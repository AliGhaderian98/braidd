package braid.main.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;

public class Subtitle {
    private final String fullText;
    private String currentText;
    public float posX, posY;
    private float wrapWidth;
    private boolean isShowing;
    private float timeELapsed = 0;

    // place text at specific position and with specified wrap width
    public Subtitle(String text, float x, float y, float maxWidth) {
        fullText = text;
        currentText = "";
        posX = x;
        posY = y;
        wrapWidth = maxWidth;
        isShowing = false;
    }

    // place text at the bottom center of the screen
    public Subtitle(String text) {
        fullText = text;
        currentText = "";
        posX = Gdx.graphics.getWidth() / 2f;
        posY = 50;
        wrapWidth = Gdx.graphics.getWidth() * 0.5f;
        isShowing = false;
    }

    public void setShowing(boolean showing) { isShowing = showing; }
    public String getFullText() { return fullText; }
    public String getCurrentText() { return currentText; }
    public void setCurrentText(String text) { currentText = text;}

    public boolean isShowing() { return isShowing;}

    public float getTime() { return timeELapsed; }
    public void setTime(float time) { timeELapsed = time; }

    public void draw(SpriteBatch batch, ShapeRenderer shapeRenderer, BitmapFont font) {
        if (!isShowing) return;

        // Use GlyphLayout to calculate text dimensions
        GlyphLayout layout = new GlyphLayout();
        layout.setText(font, fullText, Color.BLACK, wrapWidth, Align.left, true);

        float textHeight = layout.height;
        float boxWidth =  Math.min(layout.width, wrapWidth) + 20;
        float boxHeight = textHeight + 20;
        float boxX = posX - boxWidth / 2f;
        float boxY = posY - boxHeight / 2f;

        // Draw white box
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(boxX, boxY, boxWidth, boxHeight);
        shapeRenderer.end();

        // Draw thick black border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rectLine(boxX, boxY, boxX + boxWidth, boxY, 3); // Bottom
        shapeRenderer.rectLine(boxX, boxY + boxHeight, boxX + boxWidth, boxY + boxHeight, 3); // Top
        shapeRenderer.rectLine(boxX, boxY, boxX, boxY + boxHeight, 3); // Left
        shapeRenderer.rectLine(boxX + boxWidth, boxY, boxX + boxWidth, boxY + boxHeight, 3); // Right
        shapeRenderer.end();

        // Draw the text
        batch.begin();
        font.setColor(Color.BLACK);
        font.draw(batch, fullText, boxX + 10, boxY + boxHeight - 10, wrapWidth, Align.left, true);
        batch.end();
    }
}
