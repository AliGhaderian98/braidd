package braid.main;
//Logik fürs Rewinden (Command1-Klasse? (siehe Wikipedia))
import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.math.Vector2;
import braid.main.objects.Player.*;
// import com.badlogic.gdx.Input; siehe Braid.java Controls

/***********
 Hauptlogik für die Rewindmech, wir speichern alle States (Pos und Velos) und ersetzen diese beim Rewinden
 ***********/

public class RewindController {
    private boolean isRewinding = false;
    private List<State> states = new ArrayList<>();
    private int maxRewindLength = 6000; //Anzahl der Frames/Minuten die wir saven wollen
    private Rewindable rewindable;

    public RewindController(Rewindable rewindable){
        if(rewindable != null) {
            this.rewindable = rewindable;
        }
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
        //System.out.println("Recording State - Timer: " + rewindable.getStateTimer());
    }

    public void applyRewind(){
        int RewindSpeed = 0; //RewindSpeed

        if(!states.isEmpty() && rewindable != null) {// NullPointer
            for(int i = 0; i < RewindSpeed && !states.isEmpty(); i++) {
                states.remove(states.size() - 1);
            }
            if(!states.isEmpty() && rewindable != null) {
                State rewindState = states.remove(states.size() - 1);

                rewindable.setPosition(rewindState.position()); //Einmal die Position
                rewindable.setVelocity(rewindState.velocity());
                rewindable.setStateTimer(rewindState.stateTimer());
                rewindable.setCurrentState(rewindState.animationState());
                //System.out.println("Rewinding - Restoring Timer: " + rewindState.getStateTimer()); //Ruhig ergänzen falls Nullpointer auftauchen
            }
            //Einmal die Velocity
        } else{
            stopRewinding();
        }

    }

    // folgende Funktion ist nur dafür da, um für den Vorzeigeprototypen eine simple visuelle Änderung zeigen zu können
    public boolean hasRewindStorage() {
        return !states.isEmpty();
    }

    //Klasse für die States (anscheinend ne Record Klasse, glaube funktioniert auch ganz gut, bis auf method namen)
        private record State(Vector2 position, Vector2 velocity, float stateTimer, AnimationState animationState) {}
}

