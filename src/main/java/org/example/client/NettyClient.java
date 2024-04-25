package org.example.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.example.domains.clientDomain.Client;
import org.example.domains.clientDomain.ClientDataProvider;

import java.util.List;

public final class NettyClient {

    static final String HOST = "127.0.0.1";
    static final int PORT = 8001;

    public static void main(String[] args) throws Exception {

        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new StringDecoder());
                            p.addLast(new StringEncoder());
                            p.addLast(new ClientHandler());
                        }
                    });

            ChannelFuture f = b.connect(HOST, PORT).sync();

            // Створюємо список клієнтів
            List<Client> clients = ClientDataProvider.getData();


            // Створюємо об'єкт ObjectMapper для серіалізації
            ObjectMapper mapper = new ObjectMapper();
            String clientsJson = mapper.writeValueAsString(clients);

            Channel channel = f.sync().channel();
            channel.writeAndFlush(clientsJson);
            channel.flush();

            // Очікуємо закриття з'єднання
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
