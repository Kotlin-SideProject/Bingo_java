package com.angus.bingo_java;

public class Member {
    int avatarId;
    String uid;
    String nickName;
    String displayName;

    public int getAvatar() {
        return avatarId;
    }

    public void setAvatar(int avatar) {
        this.avatarId = avatar;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickName;
    }

    public void setNickname(String nickname) {
        this.nickName = nickname;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
