package braid.main.tools;

import braid.main.rewind.RewindController;
import braid.main.objects.Player;
import braid.main.screens.levels.LevelScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;


public class PlayerInputHandler {
    private final Player player;
    private final World world;
    private final Game game;
    private LevelScreen screen;
    private boolean gameIsPaused;


    public PlayerInputHandler(Player player, World world, Game game, LevelScreen screen) {
        this.player = player;
        this.world = world;
        this.game = game;
        this.screen = screen;
        this.gameIsPaused = false;
    }

    public void handleInput() {
        if(!player.getRewindController().isRewinding()) {
            handleMovement();
            handleJumping();
            handleClimbing();
        }

        handlePause();
        handleRewind();

        // En-/Disable Box2D Debug renderer, delete later
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_9))
            LevelScreen.debugRendererEnabled = !LevelScreen.debugRendererEnabled;
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
        if (Gdx.input.isKeyPressed(KeyBindings.getKey("SPACEBAR"))) {
            player.holdingJump = true;
        } else {
            player.holdingJump = false;
        }
    }

    private void handleRewind() {
        // Zeitmechanik für Rewind-Funktion
        if (Gdx.input.isKeyPressed(KeyBindings.getKey("SHIFT"))) {
            for (RewindController r : screen.rewindObjects) {
                r.startRewinding();
            }
            // apply rewind shader
            if (player.getRewindController().hasRewindStorage()) {
                screen.setRewindShader();
            } else {
                screen.removeRewindShader();
            }
        } else {
            for (RewindController r : screen.rewindObjects) {
                r.stopRewinding();
            }
            screen.removeRewindShader();
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
            //player.b2body.setLinearVelocity(0, player.b2body.getLinearVelocity().y);
            player.stopMovement();
        }
    }
}
