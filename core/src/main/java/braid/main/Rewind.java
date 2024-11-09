package braid.main;
//Logik fürs Rewinden (Command1-Klasse? (siehe Wikipedia))
import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.math.Vector2;
// import com.badlogic.gdx.Input; siehe Braid.java Controls

public class Rewind{
    private boolean isRewinding = false;
    private List<State> states = new ArrayList<>();
    private int buffer = 600; //Anzahl der Frames/Minuten die wir saven wollen todo: immer noch schauen wie man 10 min genau misst (oder ob wir das mit Frames machen wollen)
    private Rewindable rewindObject;

    public Rewind(Rewindable rewindObject){
        this.rewindObject = rewindObject;
    }
    //Vorher: (Key-Bind ist in Braid.java)
    //@Override
    //public boolean keyUp(int keycode){
    //    if(keycode == Input.Keys.SPACE){
    //        startRewinding();
    //        return true;
    //    }
    //    return false;
    //}

    //@Override
    //public boolean keyDown(int keycode){
    //   if(keycode == Input.Keys.SPACE){
    //        stopRewinding();
    //        return true;
    //    }
    //    return false;
    //}

    public void update(){
        if(isRewinding){
            applyRewind(); //
        } else{
            recordState();
        }
    }

    public void startRewinding(){ isRewinding = true; }

    public void stopRewinding(){ isRewinding = false; }

    public void recordState(){
        if (states.size() >= buffer){
            states.remove(0);
        }
        states.add(new State(rewindObject.getPosition(), rewindObject.getVelocity()));
        //Vorher: (Data/s in State/s umbenannt Data data = new data(obj.getPosition(), obj.getVelocity()); //Die wichtigsten Sachen speichern, z.B obj.getPosition(), obj.getVelocity()
        //datas.add(data); //Speichere data im Array
    }

    public void applyRewind(){ //Logik lass ich nochmal da, soon im (todo: UML) bzw. PDF zu finden
        if(!states.isEmpty()){//solange man nicht am Start ist bzw. nichts gespeichert hat
            State rewindState = states.remove(states.size() - 1);//Logik: Wir setzen die States vom vorherigen Frame hin
            rewindObject.setPosition(rewindState.getPosition()); //Einmal die Position
            rewindObject.setVelocity(rewindState.getVelocity()); //Einmal die Velocity

            //Vorher: setDatas(datas.get(datas.size() - 1)); //Logik: Wir setzen die Daten vom vorherigen Frame hin von gespeicherten Elementen
            //datas.remove(datas.size() - 1); //Wir löschen die most recent Daten
        } else{
            stopRewinding();
        }
    }

    // folgende Funktion ist nur dafür da, um für den Vorzeigeprototypen eine simple visuelle Änderung zeigen zu können
    public boolean hasRewindStorage() {
        return !states.isEmpty();
    }

    //Vorher, nun das meiste vorher/in anderen Klassen zu finden (siehe Rewindable): public void setDatas(){
    //    obj.setData1(obj.data1, obj.data2); //Positionen von den gespeicherten Objekten, unser Char, Gegner usw
    //}

    //Neu: Klasse für die States (vorher Data) hier (Hauptgrund warum v1 nicht kompilierbar war/ermöglicht nun das Rewinden)
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
//TODO: kleines Jump and Run erstellen/testen, Rewind testen, UML Diagramm ergänzen (siehe Command Pattern (wäre gut für Präsentation))
//Ideen: Background-Musik verlangesamen beim rewinden (siehe Soundeffektklasse von libgdx)
