package netserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class HeartBeatServer implements NettyListener{
    
    private final AcceptorIdleStateTrigger idleStateTrigger = new AcceptorIdleStateTrigger();
    
    private int port;

    private ChannelFuture future=null;
    
    private NettyListener listener = this;


    public HeartBeatServer(int port) {
        this.port = port;
    }

    public void start() {
    	
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        
        try {
            ServerBootstrap sbs = new ServerBootstrap().group(bossGroup, workerGroup).option(ChannelOption.SO_KEEPALIVE,true)
                    .channel(NioServerSocketChannel.class).handler(new LoggingHandler(LogLevel.INFO))
                    .localAddress(new InetSocketAddress(port)).childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new IdleStateHandler(20, 0, 0, TimeUnit.SECONDS));
                            ch.pipeline().addLast(idleStateTrigger);

                            ch.pipeline().addLast(new HeartBeatServerHandler(listener));
                        };

                    }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);

            future = sbs.bind(port).sync();

            System.out.println("Server start listen at " + port);
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

	@Override
	public void onMessageResponse(ByteBuf byteBuf) {
        byte[] bytes=new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        
        if (0xFE == ByteUtil.unsignedByteToInt(bytes[0])
                && 0xED == ByteUtil.unsignedByteToInt(bytes[1])
                && 0xFE == ByteUtil.unsignedByteToInt(bytes[2])) {
            if (1 == bytes[3]) {
                // 忽略bytes[4],bytes[5]。作用是接口升级
                int cardinal = (int)ByteUtil.unsigned4BytesToInt(bytes, 8);
                int len = byteBuf.writerIndex();
                // 前12个字节是请求头，后4个字节是校验值
                int realLen = cardinal + 12 + 4;
                // 返回的数据有可能会粘包，只需要判断数据的长度大于或者等于真实的长度即可
                if (len >= realLen) {
                    int word = ByteUtil.bytesToShort(ByteUtil.subBytes(bytes, 6, 2));
                    if (word == 1001) {
                        byte[] data = new byte[cardinal];
                        System.arraycopy(bytes, 12, data, 0, data.length);
                            String result = new String(data);
                            try {
                                System.out.println(result);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                    } else {
                        String log = "undefined response type";
                        System.out.println(log);
                    }
                } else {
                    String log = String.format("response byte array content length inequality, realLen=%d, len=%d", realLen, len);
                    System.out.println(log);
                }
            } else if (2 == bytes[3]) {
            	System.out.println("心跳11");
            }
        } else {
            System.out.println("unknown");
            
        }
		
	}

	@Override
	public void onServiceStatusConnectChanged(int statusCode) {
		// TODO Auto-generated method stub
		
	}



}