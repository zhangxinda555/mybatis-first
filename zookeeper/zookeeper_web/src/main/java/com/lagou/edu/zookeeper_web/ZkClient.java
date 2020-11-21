package com.lagou.edu.zookeeper_web;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZkClient {

    public static Map<String,String> dataSourceConfigMap = new HashMap<>();

    private static CuratorFramework CURATORFRAMEWORK;

    private static String CONFIGPATH = "/config";

    private static String CLASSNAME = "/config/className";

    private static String URL = "/config/url";
    private static String USERNAME = "/config/userName";
    private static String PASSWORD = "/config/password";




    public static void initClient() throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString("49.234.146.31:2181")
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(2000)
                .retryPolicy(retryPolicy)

                .build();


        curatorFramework.start();


        CURATORFRAMEWORK = curatorFramework;

        //创建父节点
        Stat stat = curatorFramework.checkExists().forPath(CONFIGPATH);
        if (stat == null){
            System.out.println("节点不存在");
            curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(CONFIGPATH);

        }

        //创建四个字节点
        byte[] className = "com.mysql.jdbc.Driver".getBytes("utf-8");
        String classNamePath = curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(CLASSNAME,className);
        byte[] url = "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=gbk&serverTimezone=UTC".getBytes("utf-8");
        String urlPath =curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(URL,url);
        byte[] userName = "root".getBytes("utf-8");
        String userNamePath =curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(USERNAME,userName);
        byte[] password = "123456".getBytes("utf-8");
        String passwordPath =curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(PASSWORD,password);

        dataSourceConfigMap.put(classNamePath.substring(CONFIGPATH.length()+1), new String(className));
        dataSourceConfigMap.put(urlPath.substring(CONFIGPATH.length()+1),new String(url));
        dataSourceConfigMap.put(userNamePath.substring(CONFIGPATH.length()+1),new String(userName));
        dataSourceConfigMap.put(passwordPath.substring(CONFIGPATH.length()+1),new String(password));

        System.out.println("dataSourceConfigMap===="+dataSourceConfigMap);



    }


    public static void initListener(){
        System.out.println(CURATORFRAMEWORK+"=================");
        PathChildrenCache pathChildrenCache = new PathChildrenCache(CURATORFRAMEWORK, CONFIGPATH, true);
        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                System.out.println("节点发生数据改变：");
                ChildData data = pathChildrenCacheEvent.getData();
                if (data != null && PathChildrenCacheEvent.Type.CHILD_UPDATED.compareTo(pathChildrenCacheEvent.getType()) == 0) {
                    byte[] data1 = data.getData();
                    String s = data1.toString();
                    String path = pathChildrenCacheEvent.getData().getPath();
                    System.out.println(path+"节点发生数据改变："+new String(data1));
                    dataSourceConfigMap.put(path.substring(CONFIGPATH.length()+1),new String(data1));
                    MyDataSource.initDataSource();
                    System.out.println("dataSourceConfigMap===="+dataSourceConfigMap);
                }

            }
        });

        try {
            pathChildrenCache.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
