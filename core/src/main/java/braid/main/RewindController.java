package braid.main;
//Logik fürs Rewinden (Command1-Klasse? (siehe Wikipedia))
import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.math.Vector2;
// import com.badlogic.gdx.Input; siehe Braid.java Controls

public class RewindController {
    private boolean isRewinding = false;
    private List<State> states = new ArrayList<>();
    private int maxRewindLength = 600; //Anzahl der Frames/Minuten die wir saven wollen todo: immer noch schauen wie man 10 min genau misst (oder ob wir das mit Frames machen wollen)
    private Rewindable rewindable;

    public RewindController(Rewindable rewindable){
        this.rewindable = rewindable;
    }

    public void update(){
        if(isRewinding){
            applyRewind(); //
        } else{
            recordState();
        }
    }

    public void startRewinding(){
        isRewinding = true;
        //rewindable.setVelocity(new Vector2(0,0));
    }

    public void stopRewinding(){
        isRewinding = false;
        //Bugfix Versuch
        //rewindable.setVelocity(new Vector2(0, 0));
    }

    public boolean isRewinding() {
        return isRewinding;
    }

    public void recordState(){
        if (states.size() >= maxRewindLength){
            states.remove(0);
        }
        states.add(new State(rewindable.getPosition(), rewindable.getVelocity())); //füge in jedem Frame den State in die Liste hinzu
    }

    public void applyRewind(){
        if(!states.isEmpty()){//solange man nicht am Start ist bzw. nichts gespeichert hat
            State rewindState = states.remove(states.size() - 1);//Logik: Wir setzen die States vom vorherigen Frame hin
            rewindable.setPosition(rewindState.getPosition()); //Einmal die Position
            rewindable.setVelocity(rewindState.getVelocity()); //Einmal die Velocity
        } else{
            stopRewinding();
        }
    }

    // folgende Funktion ist nur dafür da, um für den Vorzeigeprototypen eine simple visuelle Änderung zeigen zu können
    public boolean hasRewindStorage() {
        return !states.isEmpty();
    }

    //Neu: Klasse für die States
    private static class State{
        private final Vector2 position;
        private final Vector2 velocity;

        public State(Vector2 position, Vector2 velocity){
            this.position = position;
            this.velocity = velocity;
        }

        public Vector2 getPosition(){ return position; }

        public Vector2 getVelocity(){ return velocity; }
    }
}

