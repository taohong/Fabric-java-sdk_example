package com.ccccit.hyperledge.fabric.sdk;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;

import java.util.Collection;
import java.util.Properties;

/**
 * @author hongt@ccccit.com.cn
 * @description Java SDK入口类
 * @date 2020/3/13 11:23
 */
public class FabCarSdkMain {
    //合约名字
    private static final String CHAIN_CODE_NAME = "mycc";

    public static void main(String[] args) throws Exception {
//        enroll("admin", "adminpw", "cert");
//        queryCar();
//        updateCar();
//        Thread.sleep(5000);
//        queryCar();
//        initLedger();
//        Thread.sleep(10000);
        queryAllCars();
    }

    /**
     * 查询所有账本中所有汽车信息
     * @throws Exception
     */
    private static void queryAllCars() throws Exception {
        HFClient client = HFClient.createNewInstance();
        Channel channel = initChannel(client);

        // 构建proposal
        QueryByChaincodeRequest req = client.newQueryProposalRequest();
        // 指定要调用的chaincode
        ChaincodeID cid = ChaincodeID.newBuilder().setName(CHAIN_CODE_NAME).build();
        req.setChaincodeID(cid);
        //合约中提供的方法
        req.setFcn("queryAllCars");
        System.out.println("Querying for queryAllCars");
        Collection<ProposalResponse> resps = channel.queryByChaincode(req);

        for (ProposalResponse resp : resps) {
            String payload = new String(resp.getChaincodeActionResponsePayload());
            System.out.println("response: " + payload);
        }
    }

    /**
     * 更新账本
     * @throws Exception
     */
    private static void updateCar() throws Exception {
        HFClient client = HFClient.createNewInstance();
        Channel channel = initChannel(client);

        // 构建proposal
        TransactionProposalRequest req = client.newTransactionProposalRequest();
        // 指定要调用的chaincode,177上可用chaincode有fabcar mycc
        ChaincodeID cid = ChaincodeID.newBuilder().setName(CHAIN_CODE_NAME).build();
        req.setChaincodeID(cid);
        //合约中提供的方法
        req.setFcn("changeCarOwner");
        //合约方法参数
        req.setArgs(new String[]{"CAR1", "Brad"});
        System.out.println("Executing for " + "CAR1");
        // 发送proprosal
        Collection<ProposalResponse> resps = channel.sendTransactionProposal(req);

        // 提交给orderer节点
        channel.sendTransaction(resps);
    }

    /**
     * 初始化账本，合约安装后需要调用一下初始化数据
     * @throws Exception
     */
    private static void initLedger() throws Exception {
        HFClient client = HFClient.createNewInstance();
        Channel channel = initChannel(client);

        // 构建proposal
        TransactionProposalRequest req = client.newTransactionProposalRequest();
        // 指定要调用的chaincode
        ChaincodeID cid = ChaincodeID.newBuilder().setName(CHAIN_CODE_NAME).build();
        req.setChaincodeID(cid);
        //合约中提供的方法
        req.setFcn("initLedger");
        req.setChaincodeLanguage(TransactionRequest.Type.JAVA);
        System.out.println("Executing for initLedger");
        // 发送proprosal
        Collection<ProposalResponse> resps = channel.sendTransactionProposal(req);

        // 提交给orderer节点
        channel.sendTransaction(resps);
    }

    /**
     * 查询账本
     * @throws Exception
     */
    private static void queryCar() throws Exception {
        HFClient client = HFClient.createNewInstance();
        Channel channel = initChannel(client);

        String key = "CAR1";

        // 构建proposal
        QueryByChaincodeRequest req = client.newQueryProposalRequest();
        // 指定要调用的chaincode
        ChaincodeID cid = ChaincodeID.newBuilder().setName(CHAIN_CODE_NAME).build();
        req.setChaincodeID(cid);
        //合约中提供的方法
        req.setFcn("queryCar");
        //合约方法参数
        req.setArgs(new String[] { key });
        System.out.println("Querying for " + key);
        Collection<ProposalResponse> resps = channel.queryByChaincode(req);

        for (ProposalResponse resp : resps) {
            String payload = new String(resp.getChaincodeActionResponsePayload());
            System.out.println("response: " + payload);
        }
    }

    /**
     * 用户注册, 保存证书和私钥
     *
     * @param username Fabric CA Admin用户的用户名
     * @param password Fabric CA Admin用户的密码
     * @param certDir 目录名, 用来保存证书和私钥
     * @throws Exception
     */
    private static void enroll(String username, String password, String certDir) throws Exception {
        HFClient client = HFClient.createNewInstance();
        CryptoSuite cs = CryptoSuite.Factory.getCryptoSuite();
        client.setCryptoSuite(cs);

        Properties prop = new Properties();
        prop.put("verify", false);
        //CA地址和端口
        HFCAClient caClient = HFCAClient.createNewInstance("http://192.168.1.177:7054", prop);
        caClient.setCryptoSuite(cs);


        // enrollment保存了证书和私钥
        Enrollment enrollment = caClient.enroll(username, password);
        System.out.println(enrollment.getCert());

        // 保存到本地文件
        CertUtils.saveEnrollment(enrollment, certDir, username);
    }

    private static Channel initChannel(HFClient client) throws Exception {
        CryptoSuite cs = CryptoSuite.Factory.getCryptoSuite();
        client.setCryptoSuite(cs);

        client.setUserContext(
                new CarUser(
                        "admin",
                        CertUtils.loadEnrollment("cert", "admin")
                )
        );

        // 初始化channel
        Channel channel = client.newChannel("mychannel");
        // 指定背书peer
        channel.addPeer(client.newPeer("peer", "grpc://192.168.1.177:7051"));
        // 指定排序节点地址, 无论是后面执行查询还是更新都必须指定排序节点
        channel.addOrderer(client.newOrderer("orderer", "grpc://192.168.1.177:7050"));
        channel.initialize();

        return channel;
    }
}
