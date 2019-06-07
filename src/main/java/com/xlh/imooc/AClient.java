package com.xlh.imooc;

import java.io.IOException;

/**
 * @author xiaolei hu
 * @date 2019/6/7 17:18
 **/
public class AClient {
    public static void main(String[] args) throws IOException {
        new NioClient().start("AClient");
    }
}
