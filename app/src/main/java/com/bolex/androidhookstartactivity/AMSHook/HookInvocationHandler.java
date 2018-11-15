package com.bolex.androidhookstartactivity.AMSHook;

import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class HookInvocationHandler implements InvocationHandler {

    private Object mAmsObj;
    private String mPackageName;
    private String cls;

    public HookInvocationHandler(Object amsObj, String packageName, String cls) {
        this.mAmsObj = amsObj;
        this.mPackageName = packageName;
        this.cls = cls;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("startActivity")) {
            int index = 0;
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent) {
                    index = i;
                    break;
                }
            }


            //伪造intent替换真实intent，通过AMS的activity manifest清单注册校验
            Intent originallyIntent = (Intent) args[index];
            //自己伪造一个配置文件已注册过的Activity Intent
            Intent proxyIntent = new Intent();
            ComponentName componentName = new ComponentName(mPackageName, cls);
            proxyIntent.setComponent(componentName);
            // 在这里把未注册的Intent先存起来 一会儿我们需要在Handle里取出来用，通过检验后还需要替换回真实intent
            proxyIntent.putExtra("originallyIntent", originallyIntent);
            args[index] = proxyIntent;
        }
        return method.invoke(mAmsObj, args);
    }
}