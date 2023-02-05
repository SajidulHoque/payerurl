//package com.payerurl.payerurl;
//
//import jakarta.servlet.http.HttpServletRequest;
//import org.apache.commons.codec.binary.Hex;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.crypto.Mac;
//import javax.crypto.spec.SecretKeySpec;
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.security.InvalidKeyException;
//import java.security.NoSuchAlgorithmException;
//import java.util.Base64;
//import java.util.Map;
//import java.util.TreeMap;
//
//@RestController
//public class PaymentResponseController {
//    private final String payerurlSecretKey = "secret_key";
//    private final String payerurlPublicKey = "public_key";
//
//    @RequestMapping(value = "/api/payments", method = RequestMethod.POST)
//    public Map<String, Object> payments(HttpServletRequest request) {
//
//        Map<String, String[]> GETDATA = request.getParameterMap();
//        String[] authStr = {};
//        if (request.getHeader("Authorization") == null || request.getHeader("Authorization").isEmpty()) {
//            byte[] decodedAuthStr = Base64.getDecoder().decode(request.getParameter("authStr"));
//            authStr = new String(decodedAuthStr).split(":");
//        } else {
//            byte[] decodedAuthStr = Base64.getDecoder().decode(request.getHeader("Authorization").replace("Bearer ", ""));
//            authStr = new String(decodedAuthStr).split(":");
//        }
//
//        Map<String, Object> getData = new TreeMap<>();
//        getData.put("order_id", request.getParameter("order_id"));
//        getData.put("ext_transaction_id", request.getParameter("ext_transaction_id") != null ? request.getParameter("ext_transaction_id") : "");
//        getData.put("transaction_id", request.getParameter("transaction_id"));
//        getData.put("status_code", request.getParameter("status_code") != null ? Integer.parseInt(request.getParameter("status_code")) : 0);
//        getData.put("note", request.getParameter("note") != null ? request.getParameter("note") : "");
//        getData.put("confirm_rcv_amnt", request.getParameter("confirm_rcv_amnt") != null ? Float.parseFloat(request.getParameter("confirm_rcv_amnt")) : 0);
//        getData.put("confirm_rcv_amnt_curr", request.getParameter("confirm_rcv_amnt_curr") != null ? request.getParameter("confirm_rcv_amnt_curr") : "");
//        getData.put("coin_rcv_amnt", request.getParameter("coin_rcv_amnt") != null ? Float.parseFloat(request.getParameter("coin_rcv_amnt")) : 0);
//        getData.put("coin_rcv_amnt_curr", request.getParameter("coin_rcv_amnt_curr") != null ? request.getParameter("coin_rcv_amnt_curr") : "");
//        getData.put("authStr", request.getParameter("authStr") != null ? request.getParameter("authStr") : "");
//
//        // Sort the parameters
//        TreeMap<String, Object> sortedParams = new TreeMap<>(getData);
//
//        // Generate the HMAC signature
//        String stringToSign = "";
//        for (Map.Entry<String, Object> entry : sortedParams.entrySet()) {
//            stringToSign += URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "=" + URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8) + "&";
//        }
//        stringToSign = stringToSign.substring(0, stringToSign.length() - 1);
//        String signature;
//        try {
//            final Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
//            final SecretKeySpec secret_key = new javax.crypto.spec.SecretKeySpec(payerurlSecretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
//            sha256_HMAC.init(secret_key);
//            signature = Hex.encodeHexString(sha256_HMAC.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8)));
//        } catch (InvalidKeyException e) {
//            throw new RuntimeException("Failed to calculate hmac-sha256", e);
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException(e);
//        }
//
//
////        String authStr = Base64.getEncoder().encodeToString((payerurlPublicKey + ":" + signature).getBytes());
////
////        // Verify the signature
////        String[] auth = authStr.split(":");
////        if (!signature.equals(auth[1])) {
////            return Map.of("status", 2030, "message", "Signature doesn't match");
////        }
////
////        // Check if public key matches
////        if (!payerurlPublicKey.equals(auth[0])) {
////            return Map.of("status", 2030, "message", "Public key doesn't match");
////        }
////
////        // Check if transaction_id is present
////        if (!GETDATA.containsKey("transaction_id") || GETDATA.get("transaction_id").toString().isEmpty()) {
////            return Map.of("status", 2050, "message", "Transaction ID not found");
////        }
////
////        // Check if order_id is present
////        if (!GETDATA.containsKey("order_id") || GETDATA.get("order_id").toString().isEmpty()) {
////            return Map.of("status", 2050, "message", "Order ID not found");
////        }
//
//        // Check the status code
////            if (GETDATA.containsKey("status_code") && GETDATA.get("status_code").toString().equals("20000")) {
////                return Map.of("status", 20000
//        return Map.of("status", 2030, "message", "Signature doesn't match");
//    }
//}