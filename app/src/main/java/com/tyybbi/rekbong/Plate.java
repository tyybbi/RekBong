package com.tyybbi.rekbong;

class Plate {
    private int id;
    private String letterPart;
    private int numberPart;
    private long datetime;

    public void setId(int id) {
        this.id = id;
    }

    public void setLetterPart(String letterPart) {
        this.letterPart = letterPart;
    }

    public void setNumberPart(int numberPart) {
        this.numberPart = numberPart;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }

    public int getId() {
        return id;
    }

    public String getLetterPart() {
        return letterPart;
    }

    public int getNumberPart() {
        return numberPart;
    }

    public long getDatetime() {
        return datetime;
    }
}
