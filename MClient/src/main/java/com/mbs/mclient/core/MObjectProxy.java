package com.mbs.mclient.core;

import com.mbs.mclient.base.MObject;
import com.mbs.mclient.annotation.MApiFunction;
import com.mbs.mclient.utils.RequestUtils;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.List;



public class MObjectProxy implements MethodInterceptor {

    private static Logger logger = LogManager.getLogger(MObjectProxy.class);

    private MObject target;

    public MObjectProxy() {

    }

    public MObject getInstance(MObject mObject) {
        this.target = mObject;
        Enhancer en = new Enhancer();
        en.setSuperclass(mObject.getClass());
        en.setCallback(this);
        return (MObject)en.create();
    }

    public <T extends MObject> T getInstance(Class<T> tClass) throws IllegalAccessException, InstantiationException {
        this.target = tClass.newInstance();
        Enhancer en = new Enhancer();
        en.setSuperclass(tClass);
        en.setCallback(this);
        return tClass.cast(en.create());
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        if (method.getAnnotation(MApiFunction.class) != null) {
            logger.debug("=================== WE ARE HERE !!! ====================");
        }

        Object result = null;
        if (MClientSkeleton.isRestNeeded(this.target.getId(), method.getName())) {
            List<String> paramNameList = RequestUtils.getMethodParamNames(method);
            Object[] argNamesAndValues = new Object[args.length * 2];
            for (int i = 0; i < args.length; ++i) {
                argNamesAndValues[i] = paramNameList.get(i);
                argNamesAndValues[i+1] = args[i];
            }
            result = MClientSkeleton.restRequest(this.target.getId(), method.getName(), method.getReturnType().toString(), argNamesAndValues);
        } else {
            result = methodProxy.invoke(target, args);
        }
        return result;
    }
}
