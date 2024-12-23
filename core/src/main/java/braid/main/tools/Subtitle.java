package braid.main.tools;

public class Subtitle {
    private final String fullText;
    private String currentText;
    public final float posX, posY;
    private boolean isShowing;
    private float timeELapsed = 0;

    public Subtitle(String text, float x, float y) {
        this.fullText = text;
        this.currentText = "";
        this.posX = x;
        this.posY = y;
        isShowing = false;
    }

    public void setShowing(boolean showing) { isShowing = showing; }
    public String getFullText() { return fullText; }
    public String getCurrentText() { return currentText; }
    public void setCurrentText(String text) { currentText = text;}

    public boolean isShowing() { return isShowing;}

    public float getTime() { return timeELapsed; }
    public void setTime(float time) { timeELapsed = time; }
}
