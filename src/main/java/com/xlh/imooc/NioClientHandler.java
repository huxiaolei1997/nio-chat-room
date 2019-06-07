package com.xlh.imooc;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * 客户端线程类，专门接收服务器端响应信息
 *
 * @author xiaolei hu
 * @date 2019/6/7 17:00
 **/
public class NioClientHandler implements Runnable {
    private Selector selector;

    public NioClientHandler(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {

        /**
         * 6. 循环等待新接入的连接
         */
        try {
            for (; ; ) {
                /**
                 * 获取可用channel数量
                 */
                int readyChannels = selector.select();

                if (readyChannels == 0) {
                    continue;
                }

                /**
                 * 获取可用channel的集合
                 */
                Set<SelectionKey> selectionKeys = selector.selectedKeys();

                Iterator<SelectionKey> iterator = selectionKeys.iterator();

                while (iterator.hasNext()) {
                    /**
                     * selectionKey 实例
                     */
                    SelectionKey selectionKey = iterator.next();

                    /**
                     * 移除Set中的当前 SelectionKey 重点注意
                     */
                    iterator.remove();

                    /**
                     * 7. 根据就绪状态，调用对应方法处理业务逻辑
                     */

                    /**
                     * 如果是 可读事件
                     */
                    if (selectionKey.isReadable()) {
                        readHandler(selector, selectionKey);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 可读事件处理器
     */
    private void readHandler(Selector selector, SelectionKey selectionKey) throws IOException {
        /**
         * 要从 selectionKey 中获取到已经就绪的channel
         */
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        /**
         * 创建buffer
         */
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        /**
         * 循环读取服务端响应信息
         */
        String response = "";

        while (socketChannel.read(byteBuffer) > 0) {
            /**
             * 切换buffer为读模式
             */
            byteBuffer.flip();

            /**
             * 读取buffer中的内容
             */
            response += Charset.forName("UTF-8").decode(byteBuffer);
        }
        /**
         * 将 channel 再次注册到 selector上，监听它的可读事件
         */
        socketChannel.register(selector, SelectionKey.OP_READ);
        /**
         * 将服务器端响应信息打印到本地
         */
        if (response.length() > 0) {
            // 广播给其它客户端
            System.out.println("::" + response);
        }
    }
}
