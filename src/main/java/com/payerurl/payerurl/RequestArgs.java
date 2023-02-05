package com.payerurl.payerurl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class RequestArgs{
    private String order_id;
    //[required field] [String] [Merchant order ID/ Invoice ID]
    private String amount;
    //[required field] [String] [Price of the product]
    private String currency;
    //[required field] [String] [Currency of the price of the product]
    private String billing_fname;
    //[Optional field] [String] [Customer billing first name]
    private String billing_lname;
    //[Optional field] [String] [Customer billing last name]
    private String billing_email;
    //[Optional field] [String] [Customer billing email]
    private String redirect_to;
    //[required field] [String] [After making a purchase, go back to the merchant's website endpoint]
    private String notify_url;
    //[required field] [String] [After making a purchase, send notification with payment details]
    private String type;
    //[required field] [String] [The way of the customer request]


}
