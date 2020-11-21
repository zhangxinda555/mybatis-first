package com.lagou.edu.zookeeper_web;

import com.alibaba.druid.pool.DruidPooledConnection;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLException;

@SpringBootApplication
public class ZookeeperWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZookeeperWebApplication.class, args);

        //1.启动zookeeper客户端获取节点的配置信息

        try {
            ZkClient.initClient();

            MyDataSource.initDataSource();

            ZkClient.initListener();
        } catch (Exception e) {
            e.printStackTrace();
        }


        //获取连接
        DruidPooledConnection connection = null;
        try {
            connection = MyDataSource.druidDataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("获取到的数据库连接："+connection);




    }

}
