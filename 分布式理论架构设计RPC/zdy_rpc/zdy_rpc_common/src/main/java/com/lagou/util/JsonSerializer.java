package com.lagou.util;

import com.alibaba.fastjson.JSON;

public class JsonSerializer {


    public byte[] serialize(Object o){
        byte[] bytes = JSON.toJSONBytes(o);
        return bytes;

    }


    public Object deserialize(byte[] bytes,Class<?> clazz){
        Object o = JSON.parseObject(bytes, clazz);
        return o;
    }


}
