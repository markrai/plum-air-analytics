package com.markrai.plumairanalytics.model;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "metrics")
public class Metrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "timestamp")
    private Timestamp timestamp;

    @Column(name = "detector_id")
    private int detectorId;

    @Column(name = "placement")
    private String placement;

    @Column(name = "p_0_3_um")
    private Float p_0_3_um;

    @Column(name = "p_0_3_um_b")
    private Float p_0_3_um_b;

    @Column(name = "p_2_5_um")
    private Float p_2_5_um;

    @Column(name = "p_2_5_um_b")
    private Float p_2_5_um_b;

    @Column(name = "gas_680")
    private Float gas_680;

    @Column(name = "temperature")
    private Float temperature;

    @Column(name = "humidity")
    private Float humidity;

    @Column(name = "co2")
    private Integer co2;

    @Column(name = "outdoor_temperature")
    private Float outdoorTemperature;

    @Column(name = "outdoor_humidity")
    private Float outdoorHumidity;


    // Getters & Setters


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public int getDetectorId() {
        return detectorId;
    }

    public void setDetectorId(int detectorId) {
        this.detectorId = detectorId;
    }

    public String getPlacement() {
        return placement;
    }

    public void setPlacement(String placement) {
        this.placement = placement;
    }

    public Float getP_0_3_um() {
        return p_0_3_um;
    }

    public void setP_0_3_um(Float p_0_3_um) {
        this.p_0_3_um = p_0_3_um;
    }

    public Float getP_0_3_um_b() {
        return p_0_3_um_b;
    }

    public void setP_0_3_um_b(Float p_0_3_um_b) {
        this.p_0_3_um_b = p_0_3_um_b;
    }

    public Float getP_2_5_um() {
        return p_2_5_um;
    }

    public void setP_2_5_um(Float p_2_5_um) {
        this.p_2_5_um = p_2_5_um;
    }

    public Float getP_2_5_um_b() {
        return p_2_5_um_b;
    }

    public void setP_2_5_um_b(Float p_2_5_um_b) {
        this.p_2_5_um_b = p_2_5_um_b;
    }

    public Float getGas_680() {
        return gas_680;
    }

    public void setGas_680(Float gas_680) {
        this.gas_680 = gas_680;
    }

    public Float getTemperature() {
        return temperature;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    public Float getHumidity() {
        return humidity;
    }

    public void setHumidity(Float humidity) {
        this.humidity = humidity;
    }

    public Integer getCo2() {
        return co2;
    }

    public void setCo2(Integer co2) {
        this.co2 = co2;
    }

    public Float getOutdoorTemperature() {
        return outdoorTemperature;
    }

    public void setOutdoorTemperature(Float outdoorTemperature) {
        this.outdoorTemperature = outdoorTemperature;
    }

    public Float getOutdoorHumidity() {
        return outdoorHumidity;
    }

    public void setOutdoorHumidity(Float outdoorHumidity) {
        this.outdoorHumidity = outdoorHumidity;
    }

    @Override
    public String toString() {
        return "Metrics{" +
                "id=" + id +
                ", timestamp='" + timestamp + '\'' +
                ", detectorId=" + detectorId +
                ", placement=" + placement +
                ", p_0_3_um=" + p_0_3_um +
                ", p_2_5_um=" + p_2_5_um +
                ", gas_680=" + gas_680 +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", co2=" + co2 +
                ", outdoor_temperature=" + outdoorTemperature +
                ", outdoor_humidity=" + outdoorHumidity +
                '}';
    }
}
