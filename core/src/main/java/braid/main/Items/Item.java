package braid.main.Items;

import braid.main.Braid;
import braid.main.Scenes.Hud;
import braid.main.objects.InteractiveGameObject;
import braid.main.screens.levels.LevelScreen;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public abstract class Item extends InteractiveGameObject {
    protected LevelScreen screen;
    //protected World world;
    protected Vector2 velocity;
    protected boolean toDestroy;
    protected boolean destroyed;
    //protected Body body;
    protected float x,y;
    protected Sprite sprite;

    public Item(LevelScreen screen, float x, float y, float radius){
        super(screen.getWorld(), radius, true, x, y);
        this.screen = screen;
        //this.world = screen.getWorld();
        this.x = x;
        this.y = y;
        //setPosition(x,y);
        //setBounds(getX(),getY(), 16/ Braid.PPM, 16/ Braid.PPM);

        toDestroy = false;
        destroyed = false;
    }
    //public abstract void defineItem();
    public abstract void use();

    public void update(float dt){
        if(toDestroy && !destroyed){
            world.destroyBody(b2body);
            destroyed = true;
        }

    }
    public void draw(Batch batch){
        if(!destroyed)
            sprite.draw(batch);


    }
    public void destroy(){
        toDestroy = true;
        Hud.addScore(200);
    }

}

