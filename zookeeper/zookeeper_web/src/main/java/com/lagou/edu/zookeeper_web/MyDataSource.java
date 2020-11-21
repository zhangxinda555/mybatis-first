package com.lagou.edu.zookeeper_web;

import com.alibaba.druid.pool.DataSourceClosedException;
import com.alibaba.druid.pool.DruidAbstractDataSource;
import com.alibaba.druid.pool.DruidDataSource;

public class MyDataSource {

    protected static DruidDataSource druidDataSource ;


    public synchronized static void initDataSource(){

        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(ZkClient.dataSourceConfigMap.get("className"));
        dataSource.setUrl(ZkClient.dataSourceConfigMap.get("url"));
        dataSource.setUsername(ZkClient.dataSourceConfigMap.get("userName"));
        dataSource.setPassword(ZkClient.dataSourceConfigMap.get("password"));
        druidDataSource = dataSource;
    }
}
