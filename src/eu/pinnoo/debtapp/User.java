package eu.pinnoo.debtapp;

/**
 *
 * @author see /AUTHORS
 */
public class User {

    private String name;
    private int id;

    public User(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public User(String name) {
        this(name, -1);
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
