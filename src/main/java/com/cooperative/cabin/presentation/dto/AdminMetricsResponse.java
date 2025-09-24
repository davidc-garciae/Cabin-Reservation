package com.cooperative.cabin.presentation.dto;

public class AdminMetricsResponse {

    private int reservationsCreated;
    private int reservationsCancelled;
    private int statusTransitions;
    private int schedulerTransitions;

    public int getReservationsCreated() {
        return reservationsCreated;
    }

    public void setReservationsCreated(int reservationsCreated) {
        this.reservationsCreated = reservationsCreated;
    }

    public int getReservationsCancelled() {
        return reservationsCancelled;
    }

    public void setReservationsCancelled(int reservationsCancelled) {
        this.reservationsCancelled = reservationsCancelled;
    }

    public int getStatusTransitions() {
        return statusTransitions;
    }

    public void setStatusTransitions(int statusTransitions) {
        this.statusTransitions = statusTransitions;
    }

    public int getSchedulerTransitions() {
        return schedulerTransitions;
    }

    public void setSchedulerTransitions(int schedulerTransitions) {
        this.schedulerTransitions = schedulerTransitions;
    }
}
