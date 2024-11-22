package pszerszenowicz.b2c2.test.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import lombok.extern.slf4j.Slf4j;
import pszerszenowicz.b2c2.test.server.util.ServerUtil;

@Slf4j
public class Server {

    static final boolean SSL = System.getProperty("ssl") != null;
    private final int port = 8081;

    public void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        final SslContext sslCtx = ServerUtil.buildSslContext();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(
                            new WebSocketServerInitializer(sslCtx));

            Channel ch = b.bind(port).sync().channel();

            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.start();
    }

}
