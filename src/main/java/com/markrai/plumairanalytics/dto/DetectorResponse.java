package com.markrai.plumairanalytics.dto;

public class DetectorResponse {
    private Float p_0_3_um;
    private Float p_0_3_um_b;
    private Float p_2_5_um;
    private Float p_2_5_um_b;
    private Float gas_680;

    // Getters and Setters


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

    @Override
    public String toString() {
        return "DetectorResponse{" +
                "p_0_3_um=" + p_0_3_um +
                ", p_0_3_um_b='" + p_0_3_um_b + '\'' +
                ", p_2_5_um=" + p_2_5_um +
                ", p_2_5_um_b='" + p_2_5_um_b + '\'' +
                ", gas_680=" + gas_680 +
                '}';
    }
}
