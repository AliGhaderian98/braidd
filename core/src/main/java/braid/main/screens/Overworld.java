package braid.main.screens;

import braid.main.Braid;

import braid.main.objects.OverworldNode;
import braid.main.objects.OverworldPlayer;
import braid.main.tools.KeyBindings;
import braid.main.tools.OverworldCamera;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
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
    public static boolean gameIsPaused = false;

    // Camera and Map variables
    private final OverworldCamera camera;
    private final TmxMapLoader mapLoader;
    private final TiledMap map;
    private final OrthogonalTiledMapRenderer renderer;


    private Stage stage;
    private OverworldPlayer player;

    private Array<OverworldNode> nodes;


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

        setupMapNodes();

        stage = new Stage();
        player = new OverworldPlayer(this, nodes.first().getPosition().x, nodes.first().getPosition().y);
        player.setCurrentNode(nodes.first());
        stage.addActor(player);

        // Final setup steps
        camera = new OverworldCamera(35*16, 35*9, player);
        camera.setMap(map);

        setupInput();

    }

    private void setupInput() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown (int keycode) {
                String newNode = "";
                switch (keycode) {
                    case Input.Keys.W:
                        if (player.getCurrentNode().hasNeighborNorth())
                            newNode = player.getCurrentNode().getNeighborNorth();
                        break;
                    case Input.Keys.D:
                        if (player.getCurrentNode().hasNeighborEast())
                            newNode = player.getCurrentNode().getNeighborEast();
                        break;
                    case Input.Keys.S:
                        if (player.getCurrentNode().hasNeighborSouth())
                            newNode = player.getCurrentNode().getNeighborSouth();
                        break;
                    case Input.Keys.A:
                        if (player.getCurrentNode().hasNeighborWest())
                            newNode = player.getCurrentNode().getNeighborWest();
                        break;
                };
                OverworldNode node = player.getCurrentNode();
                for (OverworldNode n : nodes) {
                    if (Objects.equals(n.getName(), newNode))
                        node = n;
                }

                if (node != player.getCurrentNode()) {
                    player.setCurrentNode(node);
                    player.moveToCurrentNode();
                }

                return true;
            }
        });

    }

    private void setupMapNodes() {
        MapLayer nodeLayer = map.getLayers().get("nodes");

        nodes = new Array<>();

        for (MapObject mo : nodeLayer.getObjects()) {
            EllipseMapObject cmo = (EllipseMapObject) mo;
            nodes.add(new OverworldNode(cmo));
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        delta = Math.min(1 / 10f, Gdx.graphics.getDeltaTime());

        KeyBindings.loadKeyBindings();

        stage.act(delta);
        player.update();

        updateCamera();

        clearScreen();
        renderWorld();
    }

    private void clearScreen() {
        ScreenUtils.clear(0, 0, 0, 1);
    }

    private void renderWorld() {
        renderer.setView(camera.getCamera());
        for (int i = 0; i < 3; i++)
            renderer.render(new int[] {i});
        game.batch.setProjectionMatrix(camera.getCamera().combined);

        // draw game objects
        game.batch.begin();
        player.getSprite().draw(game.batch);
        game.batch.end();

        for (int i = 3; i < map.getLayers().getCount(); i++) {
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

    }

    @Override
    public void resume() {

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
