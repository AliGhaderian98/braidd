package braid.main.tools;

import braid.main.RewindController;
import braid.main.objects.Enemy;
import braid.main.objects.Player;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;


public class PlayerInputHandler {
    private final Player player;
    private final Enemy enemy;
    private final World world;
    private final Game game;
    private final Array<RewindController> rewindObjects;
    private boolean gameIsPaused;


    public PlayerInputHandler(Player player, Enemy enemy, World world, Game game, Array<RewindController> rewindObjects) {
        this.player = player;
        this.enemy = enemy;
        this.world = world;
        this.game = game;
        this.rewindObjects = rewindObjects;
        this.gameIsPaused = false;
    }

    public void handleInput() {
        KeyBindings.loadKeyBindings();

        if(!player.getRewindController().isRewinding()) {
            handleMovement();
            handleJumping();
            handleClimbing();

            // move enemy based on the position of the player
            if (enemy.getSprite().getX() < player.getSprite().getX()) {
                enemy.b2body.applyLinearImpulse(new Vector2(enemy.getSpeed() * .5f, 0), enemy.b2body.getWorldCenter(), true);
            } else if (enemy.getSprite().getX() > player.getSprite().getX()) {
                enemy.b2body.applyLinearImpulse(new Vector2(-enemy.getSpeed() * .5f, 0), enemy.b2body.getWorldCenter(), true);
            } else {
                if (player.isClimbing()) {
                    world.setGravity(new Vector2(0, 0));
                }
            }
        }

        handlePause();
        handleRewind();
    }


    private void handlePause() {
        if (Gdx.input.isKeyJustPressed(KeyBindings.getKey("ESC")) && !gameIsPaused) {
            gameIsPaused = true;
            game.pause();
        }
        else if (gameIsPaused) {
            gameIsPaused = false;
        }
    }

    private void handleClimbing() {
        if (Gdx.input.isKeyPressed(KeyBindings.getKey("UP_KEY")) && (player.isAtLadder() || player.isClimbing())) {
            // Climbing up the ladder
            player.climbUp();
        } else if (Gdx.input.isKeyPressed(KeyBindings.getKey("DOWN_KEY")) && player.isClimbing()) {
            // Climbing down the ladder
            player.climbDown();
        } else if (player.isClimbing()) {
            player.b2body.setLinearVelocity(player.b2body.getLinearVelocity().x,0);
        }
    }

    private void handleJumping() {
        if (Gdx.input.isKeyJustPressed(KeyBindings.getKey("SPACEBAR")) && !player.isJumping()) {
            player.jump();
        }
    }

    private void handleRewind() {
        // Zeitmechanik für Rewind-Funktion
        if (Gdx.input.isKeyPressed(KeyBindings.getKey("SHIFT"))) {
            for (RewindController r : rewindObjects) {
                r.startRewinding();
            }
            // einfache Verfärbung der Sprites, um Rewind visuell deutlich zu machen
            if (player.getRewindController().hasRewindStorage()) {
                player.setColor(Color.BLUE);
                enemy.setColor(Color.BLUE);
            } else {
                player.setColor(Color.WHITE);
                enemy.setColor(Color.WHITE);
            }
        } else {
            for (RewindController r : rewindObjects) {
                r.stopRewinding();
            }
            player.setColor(Color.WHITE);
            enemy.setColor(Color.WHITE);
        }
    }

    private void handleMovement() {
        if (Gdx.input.isKeyPressed(KeyBindings.getKey("RIGHT_KEY")) || Gdx.input.isKeyPressed(KeyBindings.getKey("LEFT_KEY"))) {
            if (Gdx.input.isKeyPressed(KeyBindings.getKey("RIGHT_KEY")) && !Gdx.input.isKeyPressed(KeyBindings.getKey("LEFT_KEY")) && Math.abs(player.b2body.getLinearVelocity().x) <= player.getSpeed()) {
                KeyBindings.loadKeyBindings();
                player.moveRight();
            }
            if (Gdx.input.isKeyPressed(KeyBindings.getKey("LEFT_KEY")) && !Gdx.input.isKeyPressed(KeyBindings.getKey("RIGHT_KEY")) && Math.abs(player.b2body.getLinearVelocity().x) <= player.getSpeed()) {
                player.moveLeft();
            }
        } else {
            player.b2body.setLinearVelocity(0, player.b2body.getLinearVelocity().y);
        }
    }
}
