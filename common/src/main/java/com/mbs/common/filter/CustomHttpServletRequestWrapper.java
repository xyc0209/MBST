package com.mbs.common.filter;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;

public class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private Map<String, List<String>> headers = new HashMap<>();

    public CustomHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getHeader(String name) {
        List<String> values = headers.get(name);
        if (values != null && !values.isEmpty()) {
            return values.get(0);
        }
        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        List<String> values = headers.get(name);
        if (values != null && !values.isEmpty()) {
            return Collections.enumeration(values);
        }
        return super.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Set<String> names = new HashSet<>(headers.keySet());
        Enumeration<String> originalNames = super.getHeaderNames();
        while (originalNames.hasMoreElements()) {
            names.add(originalNames.nextElement());
        }
        return Collections.enumeration(names);
    }

    public void addHeader(String name, String value) {
        List<String> values = headers.get(name);
        if (values == null) {
            values = new ArrayList<>();
            headers.put(name, values);
        }
        values.add(value);
    }

    public void setHeader(String name, String value) {
        List<String> values = new ArrayList<>();
        values.add(value);
        headers.put(name, values);
    }

    public void removeHeader(String name) {
        headers.remove(name);
    }
}
