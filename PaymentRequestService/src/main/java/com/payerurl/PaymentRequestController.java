package com.payerurl;

import jakarta.servlet.http.HttpServletRequest;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


@RestController
public class PaymentRequestController {
    @GetMapping(value = "/payment-request")
    public ResponseEntity<Object> paymentRequest() {
        String invoiceid = "123123";
        String amount = "50.00";
        String currency = "usd";
        String billing_fname = "muhit";
        String billing_lname = "muhit";
        String billing_email = "bc@gmail.com";
        String redirect_to = "https://anycodeunlock.com/recharge/";
        String notify_url = "https://test.payerurl.com/api/payment";
        String type = "purl";

        /**********Do not share the credencials*********/
        // get your API key : https://dashboard.payerurl.com/profile/api-management
        String payerurl_secret_key = "a7a16cbd7252290fc95c16ba48156a95";
        String payerurl_public_key = "e07c8f534f11ce794c3062ebb4fc7101";
        /***********************************************/

        String signature;

        Map<String, Object> argsMap = new HashMap<String, Object>();
        argsMap.put("order_id", invoiceid);
        argsMap.put("amount", amount);
        argsMap.put("currency", currency == "" ? "usd" : currency.toLowerCase());
        argsMap.put("billing_fname", billing_fname == "" ? "undefine" : billing_fname);
        argsMap.put("billing_lname", billing_lname == "" ? "undefine" : billing_lname);
        argsMap.put("billing_email", billing_email == "" ? "undefine@email.com" : billing_email);
        argsMap.put("redirect_to", redirect_to);
        argsMap.put("notify_url", notify_url);
        argsMap.put("type", type);

        TreeMap<String, Object> sortedMap = new TreeMap<String, Object>(argsMap);

        String argsString = "";

        for (Map.Entry<String, Object> entry : sortedMap.entrySet()) {
            argsString += URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "=" + URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8) + "&";
        }

        argsString = argsString.substring(0, argsString.length() - 1);

        try {
            final Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            final SecretKeySpec secret_key = new SecretKeySpec(payerurl_secret_key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            signature = Hex.encodeHexString(sha256_HMAC.doFinal(argsString.getBytes(StandardCharsets.UTF_8)));
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Failed to calculate hmac-sha256", e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        String authStr = Base64.getEncoder()
                .encodeToString((payerurl_public_key + ":" + signature)
                        .getBytes());

        HttpResponse<String> response = Unirest.post("https://dashboard.payerurl.com/api/payment")
                .header("Authorization", "Bearer " + authStr)
                .multiPartContent()
                .field("order_id", invoiceid)
                .field("amount", amount)
                .field("billing_fname", billing_fname)
                .field("billing_lname", billing_lname)
                .field("billing_email", billing_email)
                .field("redirect_to", redirect_to)
                .field("notify_url", notify_url)
                .field("currency", currency)
                .field("type", type)
                .asString();

        if (response.getStatus() == 200) { //success
            JSONObject jsonResponse = new JSONObject(response.getBody());
            if (jsonResponse.has("redirectTO") && !jsonResponse.getString("redirectTO").isEmpty()) {
                URI redirectUrl = null;
                HttpHeaders httpHeaders;

                try {
                    redirectUrl = new URI(jsonResponse.getString("redirectTO"));
                    httpHeaders = new HttpHeaders();
                    httpHeaders.setLocation(redirectUrl);

                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
                return new ResponseEntity<>(httpHeaders, HttpStatusCode.valueOf(HttpStatus.SC_SEE_OTHER));
            }

        }
        return new ResponseEntity<>(response.getBody(), HttpStatusCode.valueOf(response.getStatus()));
    }



}


