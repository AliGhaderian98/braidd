package braid.main.tools;

import braid.main.Braid;
import braid.main.Items.Item;
import braid.main.enemies.Knight;
import braid.main.enemies.PatrollingEnemy;
import braid.main.enemies.UnhingedEnemy;
import braid.main.objects.*;
import braid.main.objects.End;
import braid.main.objects.Enemy;
import braid.main.objects.Ladder;
import braid.main.objects.Player;
import braid.main.screens.levels.LevelScreen;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Timer;

import java.util.Objects;

public class WorldContactListener implements ContactListener {
    private final Player player;
    private Braid game;
    private LevelScreen screen;

    public WorldContactListener(Player player, Braid game, LevelScreen screen) {
        this.game = game;
        this.player = player;
        this.screen = screen;
    }

    @Override
    public void beginContact(Contact contact) {
        UserData userDataA = (UserData) contact.getFixtureA().getUserData();
        UserData userDataB = (UserData) contact.getFixtureB().getUserData();

        if (userDataA.getObject() instanceof Player || userDataB.getObject() instanceof Player) {
            if ("RechteHammerHitBox".equals(userDataA.getName()) || "RechteHammerHitBox".equals(userDataB.getName())
                || "LinkeHammerHitBox".equals(userDataA.getName()) || "LinkeHammerHitBox".equals(userDataB.getName())) {
                hammerHitBoxWithBrick (userDataA, userDataB);
            }

            if ("PlayerBody".equals(userDataA.getName()) || "PlayerBody".equals(userDataB.getName())) {
                playerWithItem(userDataA, userDataB);
                playerWithLadder(userDataA, userDataB);
                playerWithEnd(userDataA,userDataB);
                playerWithSchalter(userDataA, userDataB);
                playerBodyWithEnemy(userDataA, userDataB, contact);
                playerWithUnhingedEnemyRadius(userDataA, userDataB);
                playerWithKnightEnemyRadius(userDataA, userDataB);
                playerWithKnightAttack(userDataA, userDataB);
                playerWithSign(userDataA, userDataB);
                playerBodyWithDoor(userDataA, userDataB);
                playerWithButton(userDataA, userDataB);
                playerWithNPC(userDataA, userDataB);
            }

            if ("PlayerFeet".equals(userDataA.getName()) || "PlayerFeet".equals(userDataB.getName())) {
                playerFeetWithMovingPlatform(userDataA, userDataB);
                playerFeetWithGround(userDataA, userDataB);
                playerFeetWithBrick(userDataA, userDataB);
                playerFeetWithEnemy(userDataA, userDataB);
            }
        }

        patrollingEnemyWithWall(userDataA,userDataB);
    }


    @Override
    public void endContact(Contact contact) {
        UserData userDataA = (UserData) contact.getFixtureA().getUserData();
        UserData userDataB = (UserData) contact.getFixtureB().getUserData();

        if (userDataA.getObject() instanceof Player || userDataB.getObject() instanceof Player) {
            if ("RechteHammerHitBox".equals(userDataA.getName()) || "RechteHammerHitBox".equals(userDataB.getName())
                || "LinkeHammerHitBox".equals(userDataA.getName()) || "LinkeHammerHitBox".equals(userDataB.getName())) {
                endHammerBoxWithBrick(userDataA,userDataB);
            }

            if ("PlayerBody".equals(userDataA.getName()) || "PlayerBody".equals(userDataB.getName())) {
                contactEndedPlayerWithLadder(userDataA, userDataB);
                contactEndedPlayerWithUnhingedEnemyRadius(userDataA, userDataB);
                contactEndedPlayerWithKnightEnemyRadius(userDataA, userDataB);
                contactEndedPlayerWithKnightAttack(userDataA, userDataB);
                playerWithSignEnds(userDataA, userDataB);
                contactEndedPlayerWithSchalter(userDataA,userDataB);
                playerWithNPCEnds(userDataA, userDataB);
                contactEndedPlayerWithButton(userDataA,userDataB);
            }

            if ("PlayerFeet".equals(userDataA.getName()) || "PlayerFeet".equals(userDataB.getName())) {
                contactEndedPlayerWithMovingPlatform(userDataA,userDataB);
                contactEndedPlayerWithGround(userDataA, userDataB);
                contactEndedPlayerWithBrick(userDataA, userDataB);
            }
        }

        patrollingEnemyOnEdge(userDataA, userDataB);
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {

    }

    private void playerWithSchalter(UserData userDataA, UserData userDataB) {
        if (userDataA.getObject() instanceof Schalter || userDataB.getObject() instanceof Schalter) {
            Schalter schalter = userDataA.getObject() instanceof Schalter
                ? (Schalter) userDataA.getObject()
                : (Schalter) userDataB.getObject();
            player.isAtSchalter(true, schalter);
        }
    }

    private void playerWithButton(UserData userDataA, UserData userDataB) {
        if(userDataA.getObject() instanceof Button || userDataB.getObject() instanceof Button) {
            Button button = userDataA.getObject() instanceof Button
                ? (Button)  userDataA.getObject()
                : (Button) userDataB.getObject();
            player.isAtButton(true, button);
        }
    }

    private void contactEndedPlayerWithSchalter(UserData userDataA, UserData userDataB) {
        if (userDataA.getObject() instanceof Schalter || userDataB.getObject() instanceof Schalter) {
            Schalter schalter = userDataA.getObject() instanceof Schalter
                ? (Schalter) userDataA.getObject()
                : (Schalter) userDataB.getObject();
            player.isAtSchalter(false, null);
        }
    }

    private void contactEndedPlayerWithButton(UserData userDataA, UserData userDataB) {
        if (userDataA.getObject() instanceof Button || userDataB.getObject() instanceof Button) {
            Button button = userDataA.getObject() instanceof Button
                ? (Button) userDataA.getObject()
                : (Button) userDataB.getObject();
            player.isAtButton(false, null);
        }
    }

    private void contactEndedPlayerWithKnightAttack(UserData userDataA, UserData userDataB) {
        if ("KnightEnemyAttackingRadius".equals(userDataA.getName()) || "KnightEnemyAttackingRadius".equals(userDataB.getName()) &&
            (userDataA.getObject() instanceof Player || userDataB.getObject() instanceof Player)) {
            Knight knight = ("KnightEnemyAttackingRadius".equals(userDataA.getName())) ?  (Knight) userDataA.getObject() : (Knight) userDataB.getObject();

            knight.setPlayerIsInAttackingRange(false);
        }
    }

    private void playerWithKnightAttack(UserData userDataA, UserData userDataB) {
        if ("KnightEnemyAttackingRadius".equals(userDataA.getName()) || "KnightEnemyAttackingRadius".equals(userDataB.getName()) &&
            (userDataA.getObject() instanceof Player || userDataB.getObject() instanceof Player)) {
            Knight knight = ("KnightEnemyAttackingRadius".equals(userDataA.getName())) ?  (Knight) userDataA.getObject() : (Knight) userDataB.getObject();

            knight.setPlayerIsInAttackingRange(true);
        }
    }

    private void playerWithKnightEnemyRadius(UserData userDataA, UserData userDataB) {
        if ("KnightEnemyRadius".equals(userDataA.getName()) || "KnightEnemyRadius".equals(userDataB.getName()) &&
            (userDataA.getObject() instanceof Player || userDataB.getObject() instanceof Player)) {
            Knight knight = ("KnightEnemyRadius".equals(userDataA.getName())) ?  (Knight) userDataA.getObject() : (Knight) userDataB.getObject();

            knight.setPlayerIsInRange(true);
        }
    }

    private void contactEndedPlayerWithKnightEnemyRadius(UserData userDataA, UserData userDataB) {
        if ("KnightEnemyRadius".equals(userDataA.getName()) || "KnightEnemyRadius".equals(userDataB.getName()) &&
            (userDataA.getObject() instanceof Player || userDataB.getObject() instanceof Player)) {
            Knight knight = ("KnightEnemyRadius".equals(userDataA.getName())) ?  (Knight) userDataA.getObject() : (Knight) userDataB.getObject();

            knight.setPlayerIsInRange(false);
        }
    }

    private void playerWithSignEnds(UserData userDataA, UserData userDataB) {
        if ((userDataA.getObject() instanceof Sign || userDataB.getObject() instanceof Sign)) {
            Sign sign = (userDataA.getObject() instanceof Sign) ? (Sign) userDataA.getObject() : (Sign) userDataB.getObject();

            sign.setShowing(false);
            sign.getSubtitle().setCurrentText("");
        }
    }

    private void playerWithNPCEnds(UserData userDataA, UserData userDataB) {
        if ((userDataA.getObject() instanceof Player || userDataB.getObject() instanceof Player) &&
            (userDataA.getName().equals("NPCTrigger") || userDataB.getName().equals("NPCTrigger"))) {
            NPC npc = (userDataA.getName().equals("NPCTrigger")) ? (NPC) userDataA.getObject() : (NPC) userDataB.getObject();

            npc.getSubtitle().setCurrentText("");
            if(npc.getType().equals("FinalNPC") && !player.getRewindController().isRewinding()) {
                npc.trigger(false);
            }
            if (!Objects.equals(npc.getType(), "FinalNPC")) {
                npc.trigger(false);
            }
        } else if ((userDataA.getObject() instanceof Player || userDataB.getObject() instanceof Player) &&
            (userDataA.getObject() instanceof NPC || userDataB.getObject() instanceof NPC)) {
            NPC npc = (userDataA.getObject() instanceof NPC) ? (NPC) userDataA.getObject() : (NPC) userDataB.getObject();
            npc.trigger(false);
        }

    }

    private void playerWithSign(UserData userDataA, UserData userDataB) {
        if ((userDataA.getObject() instanceof Sign || userDataB.getObject() instanceof Sign)) {
            Sign sign = (userDataA.getObject() instanceof Sign) ? (Sign) userDataA.getObject() : (Sign) userDataB.getObject();
            sign.setShowing(true);
        }
    }

    private void playerWithNPC(UserData userDataA, UserData userDataB) {
        if ((userDataA.getObject() instanceof Player || userDataB.getObject() instanceof Player) &&
            (userDataA.getName().equals("NPCTrigger") || userDataB.getName().equals("NPCTrigger"))) {
            NPC npc = (userDataA.getName().equals("NPCTrigger")) ? (NPC) userDataA.getObject() : (NPC) userDataB.getObject();
            if (Objects.equals(npc.getType(), "FinalNPC") && player.getRewindController().isRewinding()) {
                npc.trigger(true);
            }

            if (!Objects.equals(npc.getType(), "FinalNPC")) {
                npc.trigger(true);
            }
        } else if ((userDataA.getObject() instanceof Player || userDataB.getObject() instanceof Player) &&
            (userDataA.getObject() instanceof NPC || userDataB.getObject() instanceof NPC)) {
            NPC npc = (userDataA.getObject() instanceof NPC) ? (NPC) userDataA.getObject() : (NPC) userDataB.getObject();
            npc.trigger(true);
        }
    }

    private void playerWithUnhingedEnemyRadius(UserData userDataA, UserData userDataB) {
        if ("EnemyRadius".equals(userDataA.getName()) || "EnemyRadius".equals(userDataB.getName())) {
            UnhingedEnemy unhingedEnemy = ("EnemyRadius".equals(userDataA.getName())) ?  (UnhingedEnemy) userDataA.getObject() : (UnhingedEnemy) userDataB.getObject();

            unhingedEnemy.setPlayerIsInRange(true);

        }
    }

    private void contactEndedPlayerWithUnhingedEnemyRadius(UserData userDataA, UserData userDataB) {
        if ("EnemyRadius".equals(userDataA.getName()) || "EnemyRadius".equals(userDataB.getName())) {
            UnhingedEnemy unhingedEnemy = ("EnemyRadius".equals(userDataA.getName())) ?  (UnhingedEnemy) userDataA.getObject() : (UnhingedEnemy) userDataB.getObject();

            unhingedEnemy.setPlayerIsInRange(false);

        }
    }

    private void playerWithItem(UserData userDataA, UserData userDataB) {
        UserData itemData = (userDataA.getObject() instanceof Item) ? userDataA : userDataB;

        if (itemData.getObject() instanceof Item item) {
            item.use(player);
        }
    }

    private void playerFeetWithGround(UserData userDataA, UserData userDataB) {
        if (("PlayerFeet".equals(userDataA.getName()) || "PlayerFeet".equals(userDataB.getName())) &&
            (("Ground".equals(userDataA.getName()) || "Ground".equals(userDataB.getName()))))  {

            player.onGround(true);
            player.land();
        }
    }

    private void playerFeetWithBrick(UserData userDataA, UserData userDataB) {
        if (("PlayerFeet".equals(userDataA.getName()) || "PlayerFeet".equals(userDataB.getName())) &&
            (("Brick".equals(userDataA.getName()) || "Brick".equals(userDataB.getName()))))  {
            if (!player.isGrounded()) {
                player.land();
            }
        }
    }

    private void hammerHitBoxWithBrick(UserData userDataA, UserData userDataB) {
        if ("Brick".equals(userDataA.getName()) || "Brick".equals(userDataB.getName())) {
            if (userDataA.getObject() instanceof Brick || userDataB.getObject() instanceof Brick) {
                if (player.isHammerActive()) {
                    Brick brick = (userDataA.getObject() instanceof Brick) ? (Brick) userDataA.getObject() : (Brick) userDataB.getObject();
                    player.setCollidingBrick(brick);
                }
            }
        }
    }

    private void endHammerBoxWithBrick (UserData userDataA, UserData userDataB) {
        if ("Brick".equals(userDataA.getName()) || "Brick".equals(userDataB.getName())) {
            player.resetCollidingBrick();
        }
    }

    private void playerFeetWithEnemy(UserData userDataA, UserData userDataB) {
        if ("PlayerFeet".equals(userDataA.getName()) || "PlayerFeet".equals(userDataB.getName())) {
            UserData enemyData = (userDataA.getObject() instanceof Enemy) ? userDataA : userDataB;

            if (enemyData.getObject() instanceof UnhingedEnemy && "EnemyBody".equals(enemyData.getName())) {
                player.die();
            }
            else if (enemyData.getObject() instanceof Enemy enemy && "EnemyHead".equals(enemyData.getName())) {
                enemy.die();
                player.b2body.setLinearVelocity(player.b2body.getLinearVelocity().x, 0);
                player.jump(1.25f);
            }
        }
    }

    private void playerFeetWithMovingPlatform(UserData userDataA, UserData userDataB) {
        if (("PlayerFeet".equals(userDataA.getName()) || "PlayerFeet".equals(userDataB.getName())) &&
            (userDataA.getObject() instanceof MovingPlatform || userDataB.getObject() instanceof MovingPlatform)) {

            MovingPlatform platform = (userDataA.getObject() instanceof MovingPlatform)
                ? (MovingPlatform) userDataA.getObject()
                : (MovingPlatform) userDataB.getObject();

            player.onMovingPlatform(true);
            player.land();
            platform.setPlayer(player);
        }
    }

    private void contactEndedPlayerWithMovingPlatform(UserData userDataA, UserData userDataB) {
        if (("PlayerFeet".equals(userDataA.getName()) || "PlayerFeet".equals(userDataB.getName())) &&
            (userDataA.getObject() instanceof MovingPlatform || userDataB.getObject() instanceof MovingPlatform)) {
            MovingPlatform platform = (userDataA.getObject() instanceof MovingPlatform)
                ? (MovingPlatform) userDataA.getObject()
                : (MovingPlatform) userDataB.getObject();

            player.onMovingPlatform(false);
            platform.setPlayer(null);
            player.setPlatformVelocity(0);

            if (player.isGrounded() && !player.onGround()) {
                player.fall();
            }
        }
    }



    private void playerWithLadder(UserData userDataA, UserData userDataB) {
        if (userDataA.getObject() instanceof Ladder || userDataB.getObject() instanceof Ladder) {
            UserData ladderData = (userDataA.getObject() instanceof Ladder) ? userDataA : userDataB;
            player.atLadder(true);

            Vector2 max = new Vector2(0,0);
            Vector2 min = new Vector2(0,0);

            PolygonShape polygonShape = (PolygonShape) ((Ladder) ladderData.getObject()).b2body.getFixtureList().first().getShape();
            Vector2 vertex = new Vector2();

            for (int i = 0; i < polygonShape.getVertexCount(); i++) {
                polygonShape.getVertex(i, vertex);
                // Transform vertex to world coordinates
                vertex = (((Ladder) ladderData.getObject()).b2body).getWorldPoint(vertex);

                min.x = Math.min(min.x, vertex.x);
                min.y = Math.min(min.y, vertex.y);
                max.x = Math.max(max.x, vertex.x);
                max.y = Math.max(max.y, vertex.y);
            }

            player.setMoveLimits(max, min);
        }
    }

    private void playerBodyWithEnemy(UserData userDataA, UserData userDataB, Contact contact) {
        if ("PlayerBody".equals(userDataA.getName()) || "PlayerBody".equals(userDataB.getName())) {
            UserData enemyData = (userDataA.getObject() instanceof Enemy) ? userDataA : userDataB;
            WorldManifold worldManifold = contact.getWorldManifold();
            Vector2 normal = worldManifold.getNormal();

            if (enemyData.getObject() instanceof Enemy enemy ) {
                if (normal.y < -0.5f) {
                    enemy.die();
                    player.b2body.setLinearVelocity(player.b2body.getLinearVelocity().x, 0);
                    player.jump(1.25f);
                }
                else if ("EnemyBody".equals(enemyData.getName())) {
                    player.die();
                }
            }
        }
    }

    private void contactEndedPlayerWithGround(UserData userDataA, UserData userDataB) {
        if (("PlayerFeet".equals(userDataA.getName()) || "PlayerFeet".equals(userDataB.getName())) &&
            (("Ground".equals(userDataA.getName()) || "Ground".equals(userDataB.getName()))))  {

            player.onGround(false);

            if (player.isGrounded() && !player.onMovingPlatform()) {
                player.fall();
            }
        }
    }

    private void contactEndedPlayerWithBrick(UserData userDataA, UserData userDataB) {
        if (("PlayerFeet".equals(userDataA.getName()) || "PlayerFeet".equals(userDataB.getName())) &&
            (("Brick".equals(userDataA.getName()) || "Brick".equals(userDataB.getName()))))  {
            if (player.isGrounded()) {
                player.fall();
            }
        }
    }

    private void patrollingEnemyWithWall(UserData userDataA, UserData userDataB) {
        if ("SideSensor".equals(userDataA.getName()) || "SideSensor".equals(userDataB.getName())) {
            UserData sideSensor = "SideSensor".equals(userDataA.getName()) ? userDataA : userDataB;
            if ("Wall".equals(userDataA.getName()) || "Wall".equals(userDataB.getName()))  {
                PatrollingEnemy enemy = (PatrollingEnemy) sideSensor.getObject();
                enemy.changeDirection();
            }
        }
    }

    private void contactEndedPlayerWithLadder(UserData userDataA, UserData userDataB) {
        if (userDataA.getObject() instanceof Ladder || userDataB.getObject() instanceof Ladder) {
            player.atLadder(false);
            player.stopClimbing();
        }
    }

    private void playerWithEnd(UserData userDataA, UserData userDataB) {
        if (userDataA.getObject() instanceof End || userDataB.getObject() instanceof End) {
            player.atEnd(true);

            // switch to overworld after 0.2 second delay
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    screen.finish();
                }
            }, 0.2f);
        }
    }

    private void playerBodyWithDoor(UserData userDataA, UserData userDataB) {
        if ("PlayerBody".equals(userDataA.getName()) || "PlayerBody".equals(userDataB.getName())) {
            UserData doorData = (userDataA.getObject() instanceof Door) ? userDataA : userDataB;
            if (player.hasKey() && doorData.getObject() instanceof Door door) {
                door.open();
                player.hasKey(false);
            }
        }
    }

    private void patrollingEnemyOnEdge(UserData userDataA, UserData userDataB) {
        if ("EdgeSensor".equals(userDataA.getName()) || "EdgeSensor".equals(userDataB.getName())) {
            UserData edgeSensor = "EdgeSensor".equals(userDataA.getName()) ? userDataA : userDataB;
            if ("Ground".equals(userDataA.getName()) || "Ground".equals(userDataB.getName()))  {
                PatrollingEnemy enemy = (PatrollingEnemy) edgeSensor.getObject();
                enemy.changeDirection();
            }
        }
    }
}
