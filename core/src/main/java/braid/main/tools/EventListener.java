package braid.main.tools;

public class EventListener {
    private boolean check1, check2;

    public EventListener() {}

    public void hasPressedButton(boolean pressed) {
        this.check1 = pressed;
    }

    public void hasTriggeredNPC(boolean trigger) {
        this.check2 = trigger;
    }

    public boolean canCompleteMap() {
        return check1 && check2;
    }
}
