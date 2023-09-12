package com.mbs.mclient.utils;

import java.util.UUID;


public class StringUtils {

    public static String generateMResourceId() {
        return "MResource_" + UUID.randomUUID();
    }

    public static String generateMFunctionId() {
        return "MFunction_" + UUID.randomUUID();
    }

    public static String generateMBusinessId() {
        return "MBusiness_" + UUID.randomUUID();
    }
}
