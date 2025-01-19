package braid.main.rewind;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

/***********
 Hauptlogik für die Rewindmech, wir speichern alle States (Pos und Velos) und ersetzen diese beim Rewinden
 ***********/

public class RewindController {
    private boolean isRewinding = false;
    private List<State> states = new ArrayList<>();
    private float maxRewindTime = 5f;
    private float recordedTime = 0f;
    private float elapsedTime = 0f;
    private Rewindable rewindable;

    public RewindController(Rewindable rewindable){
        if(rewindable != null) {
            this.rewindable = rewindable;
        }
    }

    public void update(float dt) {
        if (isRewinding) {
            if (dt > 0 && !states.isEmpty()) {
                elapsedTime -= dt;
                recordedTime = Math.max(recordedTime - dt, 0);
                applyRewind();
                if(elapsedTime < 0) {
                    elapsedTime = 1f;
                }
            }

        } else {
            elapsedTime += dt;
            if (elapsedTime >= 1f) {
                recordedTime += elapsedTime;
                elapsedTime = 0f;

            }
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
        if (recordedTime >= maxRewindTime){
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

