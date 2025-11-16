package ledgerly.app.model;

public class User {
    private final int id;
    private final String name;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getInitials() {
        if (name == null || name.trim().isEmpty()) {
            return "?";
        }
        return name.substring(0, 1).toUpperCase();
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }
}
