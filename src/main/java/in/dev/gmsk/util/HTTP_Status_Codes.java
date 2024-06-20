package in.dev.gmsk.util;

import java.util.concurrent.ConcurrentHashMap;

public class HTTP_Status_Codes {

    private static final ConcurrentHashMap<String, Integer> CODES = new ConcurrentHashMap<>();

    public static int httpStatus(String reasonPhrase) {

        // 2xx Success
        CODES.put("OK", 200);
        CODES.put("Created", 201);
        CODES.put("Accepted", 202);
        CODES.put("Non-Authoritative Information", 203);
        CODES.put("No Content", 204);

        // 4xx Client Error
        CODES.put("Bad Request", 400);
        CODES.put("Unauthorized", 401);
        CODES.put("Forbidden", 403);
        CODES.put("Not Found", 404);
        CODES.put("Method Not Allowed", 405);
        CODES.put("Not Acceptable", 406);

        // 5xx Server Error
        CODES.put("Internal Server Error", 500);
        CODES.put("Not Implemented", 501);
        CODES.put("Bad Gateway", 502);
        CODES.put("Service Unavailable", 503);
        CODES.put("Gateway Timeout", 504);
        CODES.put("Not Extended", 510);

        return CODES.getOrDefault(reasonPhrase, CODES.get("Not Found"));
    }
}
