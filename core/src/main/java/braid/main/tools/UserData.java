package braid.main.tools;

public class UserData {
    private final String name;
    private final Object object;

    public UserData(String name, Object object) {
        this.name = name;
        this.object = object;
    }

    public String getName() { return name; }
    public Object getObject() { return object; }
}
