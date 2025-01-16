package braid.main.Items;

import braid.main.Braid;
import braid.main.objects.InteractiveGameObject;
import braid.main.objects.Player;
import braid.main.screens.levels.LevelScreen;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public abstract class Item extends InteractiveGameObject {
    protected LevelScreen screen;

    protected Vector2 velocity;
    protected boolean toDestroy;
    protected boolean destroyed;

    protected float x,y;
    protected Sprite sprite;

    public Item(LevelScreen screen, float x, float y, float radius){
        super(screen.getWorld(), radius, true, x, y);
        this.screen = screen;
        this.x = x;
        this.y = y;

        toDestroy = false;
        destroyed = false;
    }

    public abstract void use(Player player);


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
    }

}

