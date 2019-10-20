package com.apps.juzhihua.notes;

public class notes {
    private int id;
    private String title;
    private String body;
    private String background;
    private String color;
    private String favoris;
    private String label;
    private String notification;
    private String type;
    private String VoiceFilePath;


    public notes(int id, String title, String body, String background, String color, String favoris, String label, String notification, String type, String VoiceFilePath) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.background = background;
        this.color = color;
        this.favoris = favoris;
        this.label = label;
        this.notification = notification;
        this.type = type;
        this.VoiceFilePath = VoiceFilePath;
    }


    public int getId() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getbody() {
        return body;
    }

    public void setbody(String body) {
        this.body = body;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFavoris() {
        return favoris;
    }

    public void setFavoris(String favoris) {this.favoris = favoris;}

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {this.label = label;}

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {this.notification = notification;}

    public String getType() {
        return type;
    }

    public void setType(String type) {this.type = type;}

    public String getVoiceFilePath() {
        return VoiceFilePath;
    }

    public void setVoiceFilePath(String VoiceFilePath) {this.VoiceFilePath = VoiceFilePath;}
}
