package com.angus.bingo_java;

public class GameRoom {
    String id;
    int status;
    String title;
    Member init;
    Member join;

    public GameRoom() {
    }

    public GameRoom(String title, Member init) {
        this.title = title;
        this.init = init;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Member getInit() {
        return init;
    }

    public void setInit(Member init) {
        this.init = init;
    }

    public Member getJoin() {
        return join;
    }

    public void setJoin(Member join) {
        this.join = join;
    }
}
