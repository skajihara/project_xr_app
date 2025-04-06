package com.skajihara.project_xr_app.domain.entity.record;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Entity
@Table(name = "ACCOUNTS")
public class AccountRecord {

    @Id
    @Column(name = "id", length = 20, nullable = false)
    private String id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "bio", length = 200, nullable = false)
    private String bio;

    @Column(name = "icon", length = 100, nullable = false)
    private String icon;

    @Column(name = "header_photo", length = 100, nullable = false)
    private String headerPhoto;

    @Column(name = "location", length = 50)
    private String location;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "registered", nullable = false)
    private LocalDate registered;

    @Column(name = "following", nullable = false)
    private int following;

    @Column(name = "follower", nullable = false)
    private int follower;

    @Column(name = "valid_flag", nullable = false)
    private int validFlag;

    @Column(name = "delete_flag", nullable = false)
    private int deleteFlag;

    public AccountRecord() {
    }
}
