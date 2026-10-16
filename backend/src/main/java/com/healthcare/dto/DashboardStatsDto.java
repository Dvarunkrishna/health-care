package com.healthcare.dto;

public class DashboardStatsDto {

    private long totalPatients;
    private long totalDoctors;
    private long totalAppointments;
    private long totalDepartments;
    private long appointmentsToday;
    private long pendingAppointments;

    public DashboardStatsDto() {
    }

    public DashboardStatsDto(long totalPatients, long totalDoctors, long totalAppointments, long totalDepartments,
                             long appointmentsToday, long pendingAppointments) {
        this.totalPatients = totalPatients;
        this.totalDoctors = totalDoctors;
        this.totalAppointments = totalAppointments;
        this.totalDepartments = totalDepartments;
        this.appointmentsToday = appointmentsToday;
        this.pendingAppointments = pendingAppointments;
    }

    public long getTotalPatients() {
        return totalPatients;
    }

    public void setTotalPatients(long totalPatients) {
        this.totalPatients = totalPatients;
    }

    public long getTotalDoctors() {
        return totalDoctors;
    }

    public void setTotalDoctors(long totalDoctors) {
        this.totalDoctors = totalDoctors;
    }

    public long getTotalAppointments() {
        return totalAppointments;
    }

    public void setTotalAppointments(long totalAppointments) {
        this.totalAppointments = totalAppointments;
    }

    public long getTotalDepartments() {
        return totalDepartments;
    }

    public void setTotalDepartments(long totalDepartments) {
        this.totalDepartments = totalDepartments;
    }

    public long getAppointmentsToday() {
        return appointmentsToday;
    }

    public void setAppointmentsToday(long appointmentsToday) {
        this.appointmentsToday = appointmentsToday;
    }

    public long getPendingAppointments() {
        return pendingAppointments;
    }

    public void setPendingAppointments(long pendingAppointments) {
        this.pendingAppointments = pendingAppointments;
    }
}