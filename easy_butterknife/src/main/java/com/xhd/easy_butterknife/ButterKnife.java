package com.xhd.easy_butterknife;

import android.app.Activity;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ButterKnife {

    static final Map<Class<?>, Constructor<? extends UnBinder>> BINDINGS = new LinkedHashMap<>();//缓存Constructor,提高效率

    @NonNull
    @UiThread
    public static UnBinder bind(Activity activity) {
        Class<? extends Activity> activityClass = activity.getClass();
        Constructor<? extends UnBinder> constructor = findBindingConstructorForClass(activityClass);
        try {
            if (constructor != null)
                return constructor.newInstance(activity);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    @CheckResult
    @UiThread
    private static Constructor<? extends UnBinder> findBindingConstructorForClass(Class<?> cls) {
        Constructor<? extends UnBinder> bindingCtor = BINDINGS.get(cls);
        if (bindingCtor != null) {
            return bindingCtor;
        }
        String clsName = cls.getName();
        if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
            return null;
        }
        try {
            Class<?> bindingClass = cls.getClassLoader().loadClass(clsName + "_ViewBinding");//类加载
            bindingCtor = (Constructor<? extends UnBinder>) bindingClass.getConstructor(cls);
        } catch (ClassNotFoundException e) {
            bindingCtor = findBindingConstructorForClass(cls);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find binding constructor for " + clsName, e);
        }
        BINDINGS.put(cls, bindingCtor);//保存构造器
        return bindingCtor;
    }
}
