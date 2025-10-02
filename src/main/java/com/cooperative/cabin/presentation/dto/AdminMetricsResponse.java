package com.cooperative.cabin.presentation.dto;

public class AdminMetricsResponse {

    // Métricas de reservas
    private long totalReservations;
    private long pendingReservations;
    private long confirmedReservations;
    private long inUseReservations;
    private long completedReservations;
    private long cancelledReservations;

    // Métricas de usuarios
    private long totalUsers;
    private long activeUsers;

    // Métricas de cabañas
    private long totalCabins;
    private long activeCabins;

    // Métricas calculadas
    private double occupancyRate;

    // Métricas de Micrometer (opcionales)
    private int reservationsCreated;
    private int reservationsCancelled;
    private int statusTransitions;
    private int schedulerTransitions;

    // Constructores
    public AdminMetricsResponse() {
    }

    public AdminMetricsResponse(long totalReservations, long pendingReservations, long confirmedReservations,
            long inUseReservations, long completedReservations, long cancelledReservations,
            long totalUsers, long activeUsers, long totalCabins, long activeCabins,
            double occupancyRate) {
        this.totalReservations = totalReservations;
        this.pendingReservations = pendingReservations;
        this.confirmedReservations = confirmedReservations;
        this.inUseReservations = inUseReservations;
        this.completedReservations = completedReservations;
        this.cancelledReservations = cancelledReservations;
        this.totalUsers = totalUsers;
        this.activeUsers = activeUsers;
        this.totalCabins = totalCabins;
        this.activeCabins = activeCabins;
        this.occupancyRate = occupancyRate;
    }

    // Getters y Setters
    public long getTotalReservations() {
        return totalReservations;
    }

    public void setTotalReservations(long totalReservations) {
        this.totalReservations = totalReservations;
    }

    public long getPendingReservations() {
        return pendingReservations;
    }

    public void setPendingReservations(long pendingReservations) {
        this.pendingReservations = pendingReservations;
    }

    public long getConfirmedReservations() {
        return confirmedReservations;
    }

    public void setConfirmedReservations(long confirmedReservations) {
        this.confirmedReservations = confirmedReservations;
    }

    public long getInUseReservations() {
        return inUseReservations;
    }

    public void setInUseReservations(long inUseReservations) {
        this.inUseReservations = inUseReservations;
    }

    public long getCompletedReservations() {
        return completedReservations;
    }

    public void setCompletedReservations(long completedReservations) {
        this.completedReservations = completedReservations;
    }

    public long getCancelledReservations() {
        return cancelledReservations;
    }

    public void setCancelledReservations(long cancelledReservations) {
        this.cancelledReservations = cancelledReservations;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(long activeUsers) {
        this.activeUsers = activeUsers;
    }

    public long getTotalCabins() {
        return totalCabins;
    }

    public void setTotalCabins(long totalCabins) {
        this.totalCabins = totalCabins;
    }

    public long getActiveCabins() {
        return activeCabins;
    }

    public void setActiveCabins(long activeCabins) {
        this.activeCabins = activeCabins;
    }

    public double getOccupancyRate() {
        return occupancyRate;
    }

    public void setOccupancyRate(double occupancyRate) {
        this.occupancyRate = occupancyRate;
    }

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
