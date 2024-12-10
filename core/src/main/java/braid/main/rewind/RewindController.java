package braid.main.rewind;
import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.math.Vector2;

/***********
 Hauptlogik für die Rewindmech, wir speichern alle States (Pos und Velos) und ersetzen diese beim Rewinden
 ***********/

public class RewindController {
    private boolean isRewinding = false;
    private List<State> states = new ArrayList<>();
    private int maxRewindLength = 36000; //Anzahl der Frames/Minuten die wir saven wollen
    private Rewindable rewindable;

    public RewindController(Rewindable rewindable){
        if(rewindable != null) {
            this.rewindable = rewindable;
        }
    }

    public void update(){
        if(isRewinding){
            applyRewind();
        } else{
            recordState();
        }
    }

    public void startRewinding(){
        isRewinding = true;
    }

    public void stopRewinding(){
        isRewinding = false;
    }

    public boolean isRewinding() {
        return isRewinding;
    }

    public void recordState(){
        if (states.size() >= maxRewindLength){
            states.remove(0);
        }
            states.add(new State(rewindable.getPosition(), rewindable.getVelocity(), rewindable.getStateTimer(), rewindable.getCurrentState()));
    }

    public void applyRewind(){
        int rewindSpeed = 0;

        if(!states.isEmpty() && rewindable != null) {// NullPointer
            for(int i = 0; i < rewindSpeed && !states.isEmpty(); i++) {
                states.remove(states.size() - 1);
            }
            if(!states.isEmpty() && rewindable != null) {
                State rewindState = states.remove(states.size() - 1);

                rewindable.setPosition(rewindState.position());
                rewindable.setVelocity(rewindState.velocity());
                rewindable.setStateTimer(rewindState.stateTimer());
                rewindable.setCurrentState(rewindState.animationState());
            }
        } else{
            stopRewinding();
        }

    }

    public boolean hasRewindStorage() {
        return !states.isEmpty();
    }

    private record State(Vector2 position, Vector2 velocity, float stateTimer, Object animationState) {}
}

