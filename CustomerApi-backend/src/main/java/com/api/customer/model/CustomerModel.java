package com.api.customer.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "customerTable")
public class CustomerModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "client")
    private int client;

    @Column(name = "name")
    private String name;

    @Column(name = "last_modified_date")
    private Timestamp lastModifiedDate;

    @Column(name = "create_date")
    private Timestamp createDate;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "customer_code")
    private String customerCode;

    @Column(name = "email")
    private String email;

    @Column(name = "enable")
    private boolean enable;
}
