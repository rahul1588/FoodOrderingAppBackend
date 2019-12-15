package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(name = "stateByUUID", query = "SELECT s from StateEntity s WHERE  s.uuid = :uuid"),
        @NamedQuery(name = "getAllStates", query = "SELECT s FROM StateEntity s")
})

@Table(name="state")
public class StateEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    @Column(name="id")
    private Integer id;

    @Column(name="uuid")
    @Size(max=200)
    @NotNull
    private String uuid;

    @Column(name="state_name")
    @Size(max=30)
    @NotNull
    private String stateName;

    public StateEntity() {}

    public StateEntity(String uuid, String name) {
        this.uuid = uuid;
        this.stateName = name;
    }

    @OneToMany(mappedBy = "stateId", cascade= CascadeType.ALL, fetch= FetchType.LAZY)
    private List<AddressEntity> Addresses = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }
}
