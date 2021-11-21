package EchoClient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

public class NettyEchoClient {
    private static int port;
    private static String host;
    private static NioEventLoopGroup workGroup;
    private static Bootstrap client;
    private static Channel clientChannel;
    private static Scanner consoleInput = new Scanner(System.in);

    public static void main(String[] args) {
        argumentSetParameters(args);
        new NettyEchoClient().run();
    }

    public void run() {
        try {
            workGroup = new NioEventLoopGroup(1);
            client = new Bootstrap()
                    .group(workGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) {
                            nioSocketChannel.pipeline().addLast(
                                    new LineBasedFrameDecoder(128),
                                    new StringDecoder(),
                                    new StringEncoder(),
                                    new NettyEchoClientHandler()
                            );
                        }
                    })
                    .option(ChannelOption.SO_KEEPALIVE, true);
            clientChannel = client.connect(host, port).sync().channel();
            consoleRead();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workGroup.shutdownGracefully();
        }
    }

    private void consoleRead() {
        String tmpStr = new String();
        while (!tmpStr.equals("exit")) {
            tmpStr = consoleInput.nextLine();
                    clientChannel.writeAndFlush(tmpStr + System.lineSeparator());
        }
    }

    private static void argumentSetParameters(String[] args) {
        if (args.length > 0) {
            host = args[0];
            port = Integer.parseInt(args[1]);
        }
        else System.out.println("Enter wright host and port parameter");
    }
}
