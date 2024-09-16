package com.example.busbookingapplication.users.busOwner.BusFragments.BusTimeTable;

import java.util.ArrayList;

public class BusTimeTableModel {
    private String date, time, route, bus, seat, price;
    private ArrayList<String> bookedSeats;

    public BusTimeTableModel() {
        bookedSeats = new ArrayList<>();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getBus() {
        return bus;
    }

    public void setBus(String bus) {
        this.bus = bus;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public ArrayList<String> getBookedSeats() {
        return bookedSeats;
    }

    public void setBookedSeats(ArrayList<String> bookedSeats) {
        this.bookedSeats = bookedSeats;
    }

    public void addBookedSeat(String seat) {
        this.bookedSeats.add(seat);
    }
}
