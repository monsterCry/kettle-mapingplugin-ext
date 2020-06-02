package com.yzziot.kettle.plugin;

/**
 * @Description:
 * @Author: admin
 * @Date: 2020/6/2
 * @Modified By:
 */
public class Pair<T0,T1> {
    private T0 obj0;
    private T1 obj1;

    public Pair(T0 t0,T1 t1) {
        obj0 = t0;
        obj1 = t1;
    }

    public T0 getObj0() {
        return obj0;
    }

    public void setObj0(T0 obj0) {
        this.obj0 = obj0;
    }

    public T1 getObj1() {
        return obj1;
    }

    public void setObj1(T1 obj1) {
        this.obj1 = obj1;
    }
}
