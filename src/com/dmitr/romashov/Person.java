package com.dmitr.romashov;

import java.util.Objects;

public class Person {
    private String login;
    private int person_id;
    private int role_id;
    private String role;

    public Person(String login, int person_id) {
        this.login = login;
        this.person_id = person_id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getPerson_id() {
        return person_id;
    }

    public void setPerson_id(int person_id) {
        this.person_id = person_id;
    }

    public int getRole_id() {
        return role_id;
    }

    public void setRole_id(int role_id) {
        this.role_id = role_id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return getPerson_id() == person.getPerson_id() &&
                Objects.equals(getLogin(), person.getLogin());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLogin(), getPerson_id());
    }
}
