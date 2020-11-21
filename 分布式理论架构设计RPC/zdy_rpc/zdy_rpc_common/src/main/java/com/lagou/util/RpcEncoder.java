package com.lagou.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RpcEncoder extends MessageToByteEncoder {


    private JsonSerializer jsonSerializer;
    public RpcEncoder(){

        jsonSerializer = new JsonSerializer();
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {

        byte[] serialize = jsonSerializer.serialize(o);
        int length = serialize.length;
        //写入消息头
        byteBuf.writeInt(length);
        //写入消息体
        byteBuf.writeBytes(serialize);


    }
}
