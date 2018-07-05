package com.atguigu.gmall.bean;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

public class BaseAttrValue implements Serializable {
    @Id
    @Column
    private String id;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private String valueName;
    @Column
    private String attrId;

    public BaseAttrValue() {
    }

    public BaseAttrValue(String id, String valueName, String attrId) {
        this.id = id;
        this.valueName = valueName;
        this.attrId = attrId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    public String getAttrId() {
        return attrId;
    }

    public void setAttrId(String attrId) {
        this.attrId = attrId;
    }

    @Override
    public String toString() {
        return "BaseAttrValue{" +
                "id='" + id + '\'' +
                ", valueName='" + valueName + '\'' +
                ", attrId='" + attrId + '\'' +
                '}';
    }
}
