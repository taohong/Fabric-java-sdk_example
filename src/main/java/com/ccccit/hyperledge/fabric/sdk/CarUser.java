package com.ccccit.hyperledge.fabric.sdk;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;

import java.util.Collections;
import java.util.Set;

/**
 * @author hongt@ccccit.com.cn
 * @description CarUser
 * @date 2020/3/13 11:58
 */
class CarUser implements User {
    private String name;
    private Enrollment enrollment;

    public CarUser(String name, Enrollment enrollment) {
        this.name = name;
        this.enrollment = enrollment;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Set<String> getRoles() {
        return Collections.emptySet();
    }

    @Override
    public String getAccount() {
        return "";
    }

    @Override
    public String getAffiliation() {
        return "";
    }

    @Override
    public Enrollment getEnrollment() {
        return this.enrollment;
    }

    @Override
    public String getMspId() {
        return "Org1MSP";
    }
}
