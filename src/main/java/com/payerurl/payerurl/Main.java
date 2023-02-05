package com.payerurl.payerurl;

import kong.unirest.json.JSONObject;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Main {
    public static void main(String[] args) throws Exception {
        String invoiceid = "123123";
        String amount = "50.00";
        String currency = "usd";
        String billing_fname = "muhit";
        String billing_lname = "muhit";
        String billing_email = "bc@gmail.com";
        String redirect_to = "https://anycodeunlock.com/recharge/";
        String notify_url = "https://test.payerurl.com/api/payment";
        String type = "purl";
        String payerurl_secret_key = "a7a16cbd7252290fc95c16ba48156a95";
        String payerurl_public_key = "e07c8f534f11ce794c3062ebb4fc7101";

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
        String signature;
        for (Map.Entry<String, Object> entry : sortedMap.entrySet()) {
            argsString += URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "=" + URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8) + "&";
        }

        argsString = argsString.substring(0, argsString.length() - 1);

        SecretKeySpec secretKey = new SecretKeySpec(payerurl_secret_key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKey);

        byte[] signatureBytes = mac.doFinal(argsString.getBytes(StandardCharsets.UTF_8));
        signature = Hex.encodeHexString(signatureBytes);

        String authStr = Base64.getEncoder()
                .encodeToString((payerurl_public_key + ":" + signature).getBytes());

        try {
            URL url = new URL("https://dashboard.payerurl.com/api/payment");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("Authorization", "Bearer " + authStr);

            OutputStream os = con.getOutputStream();
            os.write(argsString.getBytes());
            os.flush();
            os.close();

            int responseCode = con.getResponseCode();
//            System.out.println("POST Response Code :: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) { //success
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // print result
//                System.out.println(response.toString());
                JSONObject jsonResponse = new JSONObject(response.toString());
                if (jsonResponse.has("redirectTO") && !jsonResponse.getString("redirectTO").isEmpty()) {

                    System.out.println("window.location.replace(\"" + jsonResponse.getString("redirectTO") + "\");");
//                    System.out.println("Redirect URL: " + jsonResponse.getString("redirectTO"));
//                    RedirectView redirectView = new RedirectView();
//                    redirectView.setUrl(jsonResponse.getString("redirectTO"));

                }
            } else {
                System.out.println("POST request did not work.");
            }


        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }

    }
}