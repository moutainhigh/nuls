package io.nuls.network.service.impl.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.nuls.core.context.NulsContext;
import io.nuls.core.utils.log.Log;
import io.nuls.core.utils.network.IpUtil;
import io.nuls.core.utils.spring.lite.annotation.Autowired;
import io.nuls.network.constant.NetworkConstant;
import io.nuls.network.entity.Node;
import io.nuls.network.service.NetworkService;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Map;

public class ClientChannelHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    private NetworkService networkService;

    public ClientChannelHandler() {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String channelId = ctx.channel().id().asLongText();
        SocketChannel channel = (SocketChannel) ctx.channel();
        String nodeId = IpUtil.getNodeId(channel.remoteAddress());
        Node node = getNetworkService().getNode(nodeId);
        //check node exist
        if (node == null || (node != null && node.getStatus() != Node.WAIT)) {
            ctx.channel().close();
            return;
        }

        Map<String, Node> outNodes = networkService.getNodeGroup(NetworkConstant.NETWORK_NODE_OUT_GROUP).getNodes();
        for (Node n : outNodes.values()) {
            if (n.getIp().equals(channel.remoteAddress().getHostString()) && n.getPort() != channel.remoteAddress().getPort()) {
                ctx.channel().close();
                return;
            }
        }

        NioChannelMap.add(channelId, channel);
        node.setChannelId(channelId);
        node.setStatus(Node.CONNECT);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Log.debug("----------------------client channelInactive ------------------------- ");
        String channelId = ctx.channel().id().asLongText();
        SocketChannel channel = (SocketChannel) ctx.channel();
        NioChannelMap.remove(channelId);
        String nodeId = IpUtil.getNodeId(channel.remoteAddress());
        Node node = getNetworkService().getNode(nodeId);
        if (node != null) {
            if (node.getChannelId() == null || channelId.equals(node.getChannelId())) {
                getNetworkService().removeNode(node.getId());
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {
        SocketChannel channel = (SocketChannel) ctx.channel();
        String nodeId = IpUtil.getNodeId(channel.remoteAddress());
        Node node = getNetworkService().getNode(nodeId);
        if (node != null && node.isAlive()) {
            ByteBuf buf = (ByteBuf) msg;
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            buf.release();
            ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
            buffer.put(bytes);
            getNetworkService().receiveMessage(buffer, node);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Log.debug("----------------ClientChannelHandler exceptionCaught:" + cause.getMessage());
        ctx.channel().close();
    }

    private NetworkService getNetworkService() {
        if (networkService == null) {
            networkService = NulsContext.getServiceBean(NetworkService.class);
        }
        return networkService;
    }

}
