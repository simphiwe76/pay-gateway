package util;

import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

public final class Util {
    private Util() {
    }
    public static String createCheckSum(String formFields,String encryptionKey) {

        final var str  = String.join("", formFields + encryptionKey);

        return DigestUtils.md5DigestAsHex(str.getBytes());

    }

    public  static Map<String,String> extractData(String responseBody){

        Map<String, String> data = new HashMap<>();

        if (responseBody != null){
            for (String pair : responseBody.split("&")) {
                String[] keyValue = pair.split("=");
                data.put(keyValue[0], keyValue[1]);
            }
        }
        return  data;
    }
}

