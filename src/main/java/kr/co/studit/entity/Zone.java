package kr.co.studit.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Zone {
    @Id
    @GeneratedValue
    @Column(name = "zone_id")
    private Long id;

    private String zone;
}
