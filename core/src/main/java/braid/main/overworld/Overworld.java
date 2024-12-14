package braid.main.overworld;

import braid.main.Braid;

import braid.main.screens.menus.PauseMenu;
import braid.main.tools.KeyBindings;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Objects;

public class Overworld implements Screen {
    private final Braid game;
    private final TextureAtlas atlas;
    public static boolean gameIsPaused;

    // Camera and Map variables
    private final OverworldCamera camera;
    private final TmxMapLoader mapLoader;
    private final TiledMap map;
    private final OrthogonalTiledMapRenderer renderer;
    private int railLayerIndex;

    private Stage stage;
    private OverworldPlayer player;

    public Array<OverworldNode> nodes;

    private InputProcessor inputProcessor;

    public Overworld(Braid game) {
        this.game = game;
        atlas = new TextureAtlas("packedimages/sprites.atlas");

        // Load current keybindings
        KeyBindings.standardKeybindings();
        KeyBindings.loadKeyBindings();

        // Setup level map
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("maps/overworld-map.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / Braid.PPM);

        railLayerIndex = map.getLayers().getIndex("rails-img");


        setupMapNodes();

        stage = new Stage();
        OverworldNode uni = nodes.first();
        for (OverworldNode n : nodes) {
            if (Objects.equals(n.getName(), "UNI"))
                uni = n;
        }

        player = new OverworldPlayer(this, uni.getPosition().x, uni.getPosition().y);
        player.setPreviousNode(uni);
        player.setCurrentNode(uni);
        stage.addActor(player);

        // Final setup steps
        camera = new OverworldCamera(35*16, 35*9, player);
        camera.setMap(map);

        setupInput();

    }

    private void setupInput() {
        inputProcessor = new InputAdapter() {
            @Override
            public boolean keyDown (int keycode) {
                if (keycode == Input.Keys.ESCAPE && !gameIsPaused) {
                    pause();
                    return true;
                }

                if (player.isMoving())
                    return true;

                handleMovement(keycode);
                return true;
            }
        };

        Gdx.input.setInputProcessor(inputProcessor);
    }

    private void handleMovement(int keycode) {
        String newNode = "";

        OverworldNode node = player.getCurrentNode();
        LevelNode check = (LevelNode) node;

        switch (keycode) {
            case Input.Keys.W:
                if (check.hasNeighborNorth())
                    newNode = check.getNeighborNorth();
                break;
            case Input.Keys.D:
                if (check.hasNeighborEast())
                    newNode = check.getNeighborEast();
                break;
            case Input.Keys.S:
                if (check.hasNeighborSouth())
                    newNode = check.getNeighborSouth();
                break;
            case Input.Keys.A:
                if (check.hasNeighborWest())
                    newNode = check.getNeighborWest();
                break;
        }
        ;

        newNode = newNode.toUpperCase();

        for (OverworldNode n : nodes) {
            if (Objects.equals(n.getName(), newNode))
                node = n;
        }

        if (!Objects.equals(node.getName(), player.getCurrentNode().getName())) {
            player.setPreviousNode(player.getCurrentNode());
            player.setCurrentNode(node);
            player.moveToCurrentNode();
        }
    }

    private void setupMapNodes() {
        MapLayer nodeLayer = map.getLayers().get("nodes");

        nodes = new Array<>();

        for (EllipseMapObject ellipse : nodeLayer.getObjects().getByType(EllipseMapObject.class)) {
            nodes.add(new LevelNode(ellipse));
        }

        MapLayer transitionLayer = map.getLayers().get("transitions");

        for (RectangleMapObject rect : transitionLayer.getObjects().getByType(RectangleMapObject.class)) {
            nodes.add(new TransitionNode(rect));
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        if(!gameIsPaused) {
            delta = Math.min(1 / 10f, Gdx.graphics.getDeltaTime());

            KeyBindings.loadKeyBindings();

            stage.act(delta);
            player.update();

            updateCamera();

            clearScreen();
            renderWorld();
        }
    }

    private void clearScreen() {
        ScreenUtils.clear(0, 0, 0, 1);
    }

    private void renderWorld() {
        renderer.setView(camera.getCamera());
        for (int i = 0; i < railLayerIndex; i++)
            renderer.render(new int[] {i});
        game.batch.setProjectionMatrix(camera.getCamera().combined);

        // draw game objects
        game.batch.begin();
        player.getSprite().draw(game.batch);
        game.batch.end();

        for (int i = railLayerIndex; i < map.getLayers().getCount(); i++) {
            renderer.render(new int[] { i });
        }

    }

    private void updateCamera() {
        camera.followTarget();
        camera.getCamera().update();
    }

    @Override
    public void resize(int width, int height) {
        camera.resize(width, height);
    }

    @Override
    public void pause() {
        new Thread(() -> {
            long time = System.currentTimeMillis();
            while (System.currentTimeMillis() < time + 1){}
            Gdx.app.postRunnable(() -> {
                gameIsPaused = true;
                game.setScreen(new PauseMenu(game, this));
            });
        }).start();


    }

    @Override
    public void resume() {
        KeyBindings.loadKeyBindings();
        gameIsPaused = false;
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
    }

    // Getters and setters


    public TextureAtlas getAtlas() {
        return atlas;
    }
}
