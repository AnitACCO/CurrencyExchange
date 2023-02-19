package com.currencyExchange.audit.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Data
@Entity
@Table(name = "audit")
public class Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long requestId;

    @Column(name = "status")
    private String status;

    @Column(name = "request")
    private String  request;

    @CreationTimestamp
    private Date createdTime;

    @CreationTimestamp
    private Date updatedTime;

}

