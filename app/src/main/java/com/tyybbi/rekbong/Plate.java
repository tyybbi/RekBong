package com.tyybbi.rekbong;

public class Plate {
    private int id;
    private String plate;
    private long datetime;

    public void setId(int id) {
        this.id = id;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }

    public int getId() {
        return id;
    }

    public String getPlate() {
        return plate;
    }

    public long getDatetime() {
        return datetime;
    }
}
