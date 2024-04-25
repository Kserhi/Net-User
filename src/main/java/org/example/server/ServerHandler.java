package org.example.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.example.domains.clientDomain.Client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<String> {

    static final List<Channel> channels = new ArrayList<>();

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        System.out.println("Client joined - " + ctx);
        channels.add(ctx.channel());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) {
        System.out.println("Message received: " + msg);

        // Створюємо об'єкт ObjectMapper для десеріалізації
        ObjectMapper mapper = new ObjectMapper();

        try {
            // Десеріалізуємо JSON-рядок у список клієнтів
            List<Client> clients = mapper.readValue(msg, new TypeReference<List<Client>>() {
            });

            // Виводимо інформацію про кожного клієнта на сервері

            clients.forEach(client ->
                    System.out.println("Client: " + client.getName() + ", Email: " + client.getEmail()));


            // Пересилаємо отриманий список клієнтів іншим клієнтам
            StringBuilder sb = new StringBuilder();
            clients.forEach(client ->
                    sb.append(client.toString()).append("\n"));
            channels.forEach(channel ->
                    channel.writeAndFlush("Received clients:\n" + sb));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("Closing connection for client - " + ctx);
        ctx.close();
    }
}
