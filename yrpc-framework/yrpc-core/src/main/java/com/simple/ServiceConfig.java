package com.simple;

/**
 * @author Hongbin BAO
 * @Date 2024/1/5 21:25
 */
public class ServiceConfig<T> {
    private Class<?> interfaceProvider;
    private Object ref;

    public Class<?> getInterface() {
        return interfaceProvider;
    }

    public void setInterface(Class<?> interfaceProvider) {
        this.interfaceProvider = interfaceProvider;
    }

    public Object getRef() {
        return ref;
    }

    public void setRef(Object ref) {
        this.ref = ref;
    }
}
