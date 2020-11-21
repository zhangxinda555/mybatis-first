package com.lagou.handler;

import com.lagou.application.SpringApplication;
import com.lagou.model.RpcRequest;
import com.lagou.model.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Method;

public class UserServerHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        RpcRequest request = (RpcRequest) msg;
        System.out.println("request+++++="+request);

        String className = request.getClassName();
        String methodName = request.getMethodName();
        String requestId = request.getRequestId();
        Object[] parameters = request.getParameters();
        Class<?>[] parameterTypes = request.getParameterTypes();
        if (className !=null && className.trim()!=""){
            Class classz = Class.forName(className);
            Object bean = SpringApplication.getBean(classz);
            Method method = classz.getMethod(methodName,parameterTypes);
            Object invoke = method.invoke(bean,parameters);
            RpcResponse response = new RpcResponse();
            response.setRequestId(requestId);
            response.setResult(invoke);
            ctx.writeAndFlush(response);
        }





    }
}
