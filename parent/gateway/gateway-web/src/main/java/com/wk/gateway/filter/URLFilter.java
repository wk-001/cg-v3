package com.wk.gateway.filter;

/**
 * 不需要认证就能访问的路径校验
 */
public class URLFilter {

    //不需要拦截的URL
    private static final String allurl="/user/add,/user/login";

    /**
     * 判断当前访问路径是否需要验证权限
     * 需要验证返回false
     * 不需要验证返回true
     * @param url
     * @return
     */
    public static boolean hasAuthorize(String url){
        String[] urls = allurl.split(",");
        for (String uri : urls) {
            if(uri.equals(url)){
                return true;
            }
        }
        return false;
    }

}
