package com.ccccit.hyperledge.fabric.sdk;

import org.hyperledger.fabric.sdk.Enrollment;

import java.security.PrivateKey;

/**
 * @author hongt@ccccit.com.cn
 * @description MyEnrollment
 * @date 2020/3/13 11:54
 */
class MyEnrollment implements Enrollment {
    private PrivateKey privateKey;
    private String cert;

    public MyEnrollment(PrivateKey privateKey, String cert) {
        this.privateKey = privateKey;
        this.cert = cert;
    }

    @Override
    public PrivateKey getKey() {
        return this.privateKey;
    }

    @Override
    public String getCert() {
        return this.cert;
    }
}
