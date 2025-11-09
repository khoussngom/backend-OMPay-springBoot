package com.khouss.UsersMicroservice.constant;

import java.util.HashMap;
import java.util.Map;

public class OMPayResponse {

    public static <T> Map<String, Object> success(T data, OMPayMessages message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message.getMessage());
        response.put("data", data);
        return response;
    }

    public static Map<String, Object> error(OMPayMessages message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message.getMessage());
        return response;
    }
}

