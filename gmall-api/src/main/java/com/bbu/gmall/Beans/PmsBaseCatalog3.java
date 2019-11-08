package com.bbu.gmall.Beans;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

public class PmsBaseCatalog3 implements Serializable {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @Column
    private String name;
    @Column
    private String catalog2_id;

    @Transient
    private List<PmsBaseCatalog3> catalog3List;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCatalog2_id() {
        return catalog2_id;
    }

    public void setCatalog2_id(String catalog2_id) {
        this.catalog2_id = catalog2_id;
    }

}
