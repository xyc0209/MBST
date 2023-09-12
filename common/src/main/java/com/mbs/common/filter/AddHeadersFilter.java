package com.mbs.common.filter;


import org.joda.time.DateTime;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;


/**
 *  setting three attributes which are used for tracing the http request
 */

@WebFilter
public class AddHeadersFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String traceId = httpServletRequest.getHeader("traceId");
        String id;
        String parentId;
        Map<String, String> map = new HashMap<>();

        if (null == traceId || "".equals(traceId) || "null".equals(traceId) ){

            //TraceId represents the Id of the entire link request, id represents the Id of the current request, parentId represents the Id of the parent request
            traceId = String.valueOf(UUID.randomUUID());
            id = traceId;
            parentId = null;
            map.put("traceId",traceId);
            map.put("id",id);
            map.put("parentId",parentId);
            System.out.println("traceID"+traceId);
            System.out.println("id"+id);
            System.out.println("parentId"+parentId);
        }
        else {
            id = String.valueOf(UUID.randomUUID());
            parentId = httpServletRequest.getHeader("id");
            map.put("id",id);
            map.put("parentId",parentId);
        }


        filterChain.doFilter(modifyHeaders(map,httpServletRequest),servletResponse);

    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }



    private CustomHttpServletRequestWrapper modifyHeaders(Map<String, String> headers, HttpServletRequest request){


//        if (headers == null || headers.isEmpty()) {
//            return;
//        }
//        Class<? extends HttpServletRequest> requestClass = request.getClass();
//        try {
//            Field request1 = requestClass.getDeclaredField("request");
//            request1.setAccessible(true);
//            Object o = request1.get(request);
//            Field coyoteRequest = o.getClass().getDeclaredField("coyoteRequest");
//            coyoteRequest.setAccessible(true);
//            Object o1 = coyoteRequest.get(o);
//            Field headersH = o1.getClass().getDeclaredField("headers");
//            headersH.setAccessible(true);
//            MimeHeaders o2 = (MimeHeaders)headersH.get(o1);
//            for (Map.Entry<String, String> entry : headers.entrySet()) {
//                o2.removeHeader(entry.getKey());
//                o2.addValue(entry.getKey()).setString(entry.getValue());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        if (headers == null || headers.isEmpty()) {
            return new CustomHttpServletRequestWrapper(request);
        }
        CustomHttpServletRequestWrapper requestWrapper = new CustomHttpServletRequestWrapper(request);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            requestWrapper.addHeader(entry.getKey(), entry.getValue());
        }
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            System.out.println("Header name: " + name);
            Enumeration<String> values = request.getHeaders(name);
            while (values.hasMoreElements()) {
                String value = values.nextElement();
                System.out.println("Header value: " + value);
            }
        }
        return requestWrapper;

    }

    public static void main(String[] args) {
        System.out.println(DateTime.now());
    }
}


