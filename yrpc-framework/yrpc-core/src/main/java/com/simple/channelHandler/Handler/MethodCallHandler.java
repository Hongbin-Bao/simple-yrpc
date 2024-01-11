package com.simple.channelHandler.Handler;

import com.simple.ServiceConfig;
import com.simple.YrpcBootstrap;
import com.simple.enumeration.RequestType;
import com.simple.enumeration.RespCode;
import com.simple.transport.message.RequestPayload;
import com.simple.transport.message.YrpcRequest;
import com.simple.transport.message.YrpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Hongbin BAO
 * @Date 2024/1/10 16:44
 */
@Slf4j
public class MethodCallHandler extends SimpleChannelInboundHandler<YrpcRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, YrpcRequest yrpcRequest) throws Exception {
        // 1、获取负载内容
        RequestPayload requestPayload = yrpcRequest.getRequestPayload();

        // 2、根据负载内容进行方法调用
        Object result = callTargetMethod(requestPayload);


        if(log.isDebugEnabled()){
            log.debug("请求【{}】已经在服务端完成方法调用",yrpcRequest.getRequestId());
        }

        // 封装响应
        YrpcResponse yrpcResponse = new YrpcResponse();
        yrpcResponse.setCode(RespCode.SUCCESS.getCode());
        yrpcResponse.setRequestId(yrpcRequest.getRequestId());
        yrpcResponse.setCompressType(yrpcRequest.getCompressType());
        yrpcResponse.setSerializeType(yrpcRequest.getSerializeType());
        yrpcResponse.setBody(result);

        // 4、写出响应
        channelHandlerContext.channel().writeAndFlush(yrpcResponse);

    }

    private Object callTargetMethod(RequestPayload requestPayload) {
        String interfaceName = requestPayload.getInterfaceName();
        String methodName = requestPayload.getMethodName();
        Class<?>[] parametersType = requestPayload.getParametersType();
        Object[] parametersValue = requestPayload.getParametersValue();

        // 寻找到匹配的暴露出去的具体的实现
        ServiceConfig<?> serviceConfig = YrpcBootstrap.SERVERS_LIST.get(interfaceName);
        Object refImpl = serviceConfig.getRef();

        // 通过反射调用 1、获取方法对象  2、执行invoke方法
        Object returnValue;
        try {
            Class<?> aClass = refImpl.getClass();
            Method method = aClass.getMethod(methodName, parametersType);
            returnValue = method.invoke(refImpl, parametersValue);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            log.error("调用服务【{}】的方法【{}】时发生了异常。",interfaceName,methodName,e);
            throw new RuntimeException(e);
        }
        return returnValue;
    }
}
