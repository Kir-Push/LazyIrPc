package com.push.lazyir.modules.notifications;

/**
 * Created by buhalo on 26.03.17.
 */

public class Notification {

    private String text;
    private String title;
    private String pack;
    private String ticker;
    private String id;

    public Notification() {
    }

    public Notification(String text, String title, String pack, String ticker, String id) {
        this.text = text;
        this.title = title;
        this.pack = pack;
        this.ticker = ticker;
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPack() {
        return pack;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Notification that = (Notification) o;

        if (text != null ? !text.equals(that.text) : that.text != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (pack != null ? !pack.equals(that.pack) : that.pack != null) return false;
        if (ticker != null ? !ticker.equals(that.ticker) : that.ticker != null) return false;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        int result = text != null ? text.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (pack != null ? pack.hashCode() : 0);
        result = 31 * result + (ticker != null ? ticker.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }
}
