package com.lagou.application;

import com.lagou.annotation.Autowired;
import com.lagou.annotation.Service;
import com.lagou.handler.UserServerHandler;
import com.lagou.model.RpcRequest;
import com.lagou.util.RpcDecoder;
import com.lagou.util.RpcEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.*;


public class SpringApplication {


    private static Map<Class<?>, Object> map = new HashMap<>();  // 存储有value值的bean的value和类型
    private static Map<Class<?>, Object> mapBean = new HashMap<>();  // 存储对象和对象的类型
    public SpringApplication(){
    }


    public static void  run(Class<?> aclasz,Object[] objects) {
        initBeans(aclasz);
        try {
            initBootStrap();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    private static void initBootStrap() throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new RpcDecoder(RpcRequest.class));
                        pipeline.addLast(new RpcEncoder());
                        pipeline.addLast(new UserServerHandler());

                    }
                });
        serverBootstrap.bind("127.0.0.1",8990).sync();
    }

    private static void initBeans(Class<?> aclasz) {
        String basePackageUrl = aclasz.getPackageName();
        System.out.println(basePackageUrl);

        try {
            String basePackage = basePackageUrl;
            Enumeration<URL> resources = SpringApplication.class.getClassLoader().getResources(basePackage.replace(".", "/"));
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                String protocol = url.getProtocol();
                if (protocol.equals("file")) {
                    String filePath = URLDecoder.decode(url.getPath(), Charset.defaultCharset());
                    File file = new File(filePath);

                    Set<String> allReferenceClassName = getAllClassName(basePackage, file, "", new HashSet<>());
                    for (String className : allReferenceClassName) {
                        //有Service、Repository、Component的注解的类才会被加载进BeanFactory

                        Class<?> classBean = Class.forName(className);
                        Service annotationService = classBean.getAnnotation(Service.class);

                        if (annotationService != null ) {

                            //如果这些注解有值的话，那么增加一个值对应对象的entry
                            String value = "";
                            if (annotationService != null && annotationService.value() != "" && annotationService.value() != null) {
                                value = annotationService.value();
                            }

                            //接口不能实例化，所以这里只考虑了注解放在实现类上的情况
                            //todo
                            map.put(classBean, value);
                            Object o = classBean.newInstance();

                            mapBean.put(classBean,o);



                        }

                    }
                } else if (protocol.equals("jar")) {
                    //todo
                }

            }

            //拿到所有的bean后，可以给属性中的Autowored设置对象
            for (Class<?> classObject : map.keySet()) {
                //如果value有值的话，并且注入的注解也有值的话
                if (classObject.isInterface()) {
                    continue;
                }
                Field[] declaredFields = classObject.getDeclaredFields();
                for (Field declaredField : declaredFields) {
                    Autowired annotation = declaredField.getAnnotation(Autowired.class);

                    if (annotation != null) {
                        //在map里面找到这个带有Autowired注解的属性的类型
                        for (Class<?> aClass : map.keySet()) {
                            if (declaredField.getType().isAssignableFrom(aClass)) {
                                declaredField.setAccessible(true);
                                declaredField.set(mapBean.get(classObject), mapBean.get(aClass));
                                break;

                            }
                        }

                    }
                }


            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static Set<String> getAllClassName(String basePackage, File file, String packageName, HashSet<String> set) {


        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                String newPackageName = packageName;
                newPackageName = newPackageName.concat(".").concat(files[i].getName());

                getAllClassName(basePackage, files[i], newPackageName, set);
            }
        } else {

            String className = packageName.replace(".class", "");
            set.add(basePackage.concat(className));

        }
        return set;
    }


    //对外提供获取实例对象的接口 根据type获取
    public static Object getBean(Class<?> aClass) {
        if (mapBean.get(aClass) != null){
            return mapBean.get(aClass);
        }
        for (Class<?> aClass1 : mapBean.keySet()) {
            if (aClass.isAssignableFrom(aClass1)){
                return mapBean.get(aClass1);
            }
        }
        return null;
    }

    public static void main(String[] args) {


    }

}
