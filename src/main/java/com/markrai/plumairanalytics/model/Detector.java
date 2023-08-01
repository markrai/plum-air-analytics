package com.markrai.plumairanalytics.model;

import jakarta.persistence.*;


@Entity
@Table(name = "detector")
public class Detector {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "ip_addr")
    private String ipAddr;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    // Getters and Setters

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // toString method if necessary
    @Override
    public String toString() {
        return "Detector{" +
                "id=" + id +
                ", ipAddr='" + ipAddr + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
