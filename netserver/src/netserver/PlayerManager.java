package netserver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {

    private static Map<String,Channel> sessionMap = new ConcurrentHashMap<String, Channel>();

    public static void addSession(Channel channel){
        String ips = channel.remoteAddress().toString();
        ips = ips.substring(1,ips.indexOf(":"));
        if(sessionMap.containsKey(ips)){
            getSession(ips).closeFuture();
            sessionMap.remove(ips);
        }
        sessionMap.put(ips, channel);
    }

    public static void removeSession(int playerId){
        if(sessionMap.containsKey(playerId)){
            sessionMap.remove(playerId);
        }
    }

    public static Channel getSession(String ips){
        if(!sessionMap.containsKey(ips)){
            return null;
        }
        return sessionMap.get(ips);
    }

    public static void sendMessage(String str){
    	try {
    		byte[] content = RequestUtil.getEncryptBytes(str);
            byte[] requestHeader = RequestUtil.getRequestHeader(content, 1, 1001);
            byte[] requestBody = RequestUtil.getRequestBody(requestHeader, content);
    		for(Entry<String,Channel> map:sessionMap.entrySet()) {
    			map.getValue().writeAndFlush(Unpooled.copiedBuffer(requestBody));
    		}
    		System.out.println("发送消息成功."+str);
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
        
    }

    public static void close(int playerId){
        if(sessionMap.containsKey(playerId)){
            sessionMap.get(playerId).close();
        }
    }
    public static Map getMap(){
        return sessionMap;
    }
}