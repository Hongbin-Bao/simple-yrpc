package com.simple;

/**
 * @author Hongbin BAO
 * @Date 2024/1/5 21:25
 */
public class ServiceConfig<T> {

    private Class<T> interfaceProvider;
    private Object ref;



    public Class<T> getInterface() {
        return interfaceProvider;
    }

    public void setInterface(Class<T> interfaceProvider) {
        this.interfaceProvider = interfaceProvider;
    }

    public Object getRef() {
        return ref;
    }

    public void setRef(Object ref) {
        this.ref = ref;
    }
}
