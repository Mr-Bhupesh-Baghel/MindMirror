package com.mindmirror.dataservice;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "user_data")
public class UserData {
    @Id
    private String userId;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String dataJson;

    @Column(nullable = false)
    private Instant updatedAt;

    protected UserData() {
    }

    public UserData(String userId, String dataJson) {
        this.userId = userId;
        this.dataJson = dataJson;
        this.updatedAt = Instant.now();
    }

    public String getDataJson() {
        return dataJson;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
        this.updatedAt = Instant.now();
    }
}
