package ru.mail.park.model;

import ru.mail.park.Utility;

public class Forum {
    public int id; //TODO set id
    public String name;
    public String shortName;
    public Object user;

    public Forum(String name, String shortName, Object user) {
        this.name = name;
        this.shortName = shortName;
        this.user = user;
    }

    @Override
    public String toString() {
        return Utility.o2j(this);
    }
}
