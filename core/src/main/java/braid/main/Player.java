package braid.main;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;

public class Player {
    // Player variables
    static final int playerSpeed = 5;
    static final int enemySpeed = 3;
    static boolean isJumping = false;
    static final int jumpVelocity = 20;
    static int velocityY;
    static final int gravity = 1;

    private Texture loewe;
    private Sprite loeweSprite;
    private Sprite boeserLoeweSprite;

    private Rewind rewindLoewe;
    private Rewind rewindBoeserLoewe;

    private Array<Rewind> rewindObjects;

    //Getter und Setter
    public static final int getPlayerSpeed() {
        return playerSpeed;
    }

    public static final int getEnemySpeed() {
        return enemySpeed;
    }

    public static boolean isIsJumping() {
        return isJumping;
    }

    public static void setIsJumping(boolean isJumping) {
        Player.isJumping = isJumping;
    }

    public static final int getJumpVelocity () {
        return jumpVelocity;
    }
    public static int getVelocityY() {
        return velocityY;
    }

    public static void setVelocityY(int velocityY) {
        Player.velocityY = velocityY;
    }

    public static final int getGravity() {
        return gravity;
    }

    public Texture getLoewe() {
        return loewe;
    }

    public void setLoewe(Texture loewe) {
        this.loewe = loewe;
    }

    public Sprite getLoeweSprite() {
        return loeweSprite;
    }

    public void setLoeweSprite(Sprite loeweSprite) {
        this.loeweSprite = loeweSprite;
    }

    public Sprite getBoeserLoeweSprite() {
        return boeserLoeweSprite;
    }

    public void setBoeserLoeweSprite(Sprite boeserLoeweSprite) {
        this.boeserLoeweSprite = boeserLoeweSprite;
    }

    public braid.main.Rewind getRewindLoewe() {
        return rewindLoewe;
    }

    public void setRewindLoewe(braid.main.Rewind rewindLoewe) {
        this.rewindLoewe = rewindLoewe;
    }

    public braid.main.Rewind getRewindBoeserLoewe() {
        return rewindBoeserLoewe;
    }

    public void setRewindBoeserLoewe(braid.main.Rewind rewindBoeserLoewe) {
        this.rewindBoeserLoewe = rewindBoeserLoewe;
    }

    public Array<braid.main.Rewind> getRewindObjects() {
        return rewindObjects;
    }

    public void setRewindObjects(Array<braid.main.Rewind> rewindObjects) {
        this.rewindObjects = rewindObjects;
    }
}
