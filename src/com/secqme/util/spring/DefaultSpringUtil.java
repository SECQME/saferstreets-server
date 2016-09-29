package com.secqme.util.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Locale;

/**
 *
 * @author jameskhoo
 */
public final class DefaultSpringUtil implements SpringUtil {

   // private static String SPRING_CONFIG_FILE = "/spring/secqMeConfig.xml";
   // private final static ApplicationContext context = new ClassPathXmlApplicationContext(SPRING_CONFIG_FILE);
    private MessageSource msgRes= null;
    private ApplicationContext context = null;
    private static final DefaultSpringUtil instance = new DefaultSpringUtil();
//    private static final LocaleManager localeManager = (LocaleManager) instance.getBean(BeanType.localeManager);
    
    private DefaultSpringUtil() {
        // Default empty constructor
    }


    public void setApplicationContext(ApplicationContext ctx) {
        context = ctx;
        msgRes = (MessageSource) context;
    }

    public ApplicationContext getContext() {
        return context;
    }

    public void setContext(ApplicationContext context) {
        this.context = context;
    }

    


    public String getMessage(String key) {
        return msgRes.getMessage(key, null, Locale.getDefault());
    }

//    public String getMessage(String key, String langCode) {
//          return msgRes.getMessage(key, null, localeManager.getLocale(langCode));
//    }

    public String getMessage(String key, String langCode) {
        return msgRes.getMessage(key, null, new Locale(langCode));
    }
    
    public String getMessage(String key, Locale locale) {
        return msgRes.getMessage(key, null, locale);
    }
    
    public String getMessage(String key, Object [] params) {
        return msgRes.getMessage(key, params, Locale.getDefault());
    }

//    public String getMessage(String key, Object [] params, String langCode) {
//        return msgRes.getMessage(key, params, localeManager.getLocale(langCode));
//    }

    public String getMessage(String key, String langCode, Object [] params) {
        return msgRes.getMessage(key, params, new Locale(langCode));
    }
    
    public String getMessage(String key, Object [] params, Locale locale) {
        return msgRes.getMessage(key, params, locale);
    }

    public Object getBean(BeanType springBean) {
        if(context== null) {
            String ctxFileName = "/WOMDataSpringConfig.xml";
            ApplicationContext ctx = new ClassPathXmlApplicationContext(ctxFileName);
            this.setApplicationContext(ctx);
        }
        return context.getBean(springBean.getBeanName());

    }

    public static DefaultSpringUtil getInstance() {
        return instance;
    }


}
