package netserver;

import io.netty.channel.ChannelHandler;

/**
 * 
 * �ͻ��˵�ChannelHandler���ϣ�������ʵ�֣��������ĺô���
 * �̳�����ӿڵ�����������Ժܷ���ػ�ȡChannelPipeline�е�Handlers
 * ��ȡ��handlers֮�󷽱�ChannelPipeline�е�handler�ĳ�ʼ������������ʱ��Ҳ�ܷܺ���
 * �ػ�ȡ���е�handlers
 */
public interface ChannelHandlerHolder {

    ChannelHandler[] handlers();
}