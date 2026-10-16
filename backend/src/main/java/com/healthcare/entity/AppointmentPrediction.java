package com.healthcare.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointment_predictions")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AppointmentPrediction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;
    private Double noShowProbability;
    private String predictionLabel;
    private LocalDateTime createdAt;
    @PrePersist
    protected void onCreate(){ if(createdAt==null) createdAt=LocalDateTime.now();}
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public Appointment getAppointment(){return appointment;}
    public void setAppointment(Appointment appointment){this.appointment=appointment;}
    public Double getNoShowProbability(){return noShowProbability;}
    public void setNoShowProbability(Double p){this.noShowProbability=p;}
    public String getPredictionLabel(){return predictionLabel;}
    public void setPredictionLabel(String l){this.predictionLabel=l;}
    public LocalDateTime getCreatedAt(){return createdAt;}
    public void setCreatedAt(LocalDateTime c){this.createdAt=c;}
    // convenience for service expecting double
    public void setNoShowProbability(double p){this.noShowProbability=p;}
}