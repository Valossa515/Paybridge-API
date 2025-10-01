package br.com.aftersunrise.paybridge.application.customers.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PicPayBuyer {
    private String firstName;
    private String lastName;
    private String document;
    private String email;
    private String phone;
}
