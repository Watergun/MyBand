package org.kurthen.myband;

import android.content.SharedPreferences;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Created by Leonhard on 06.10.2016.
 */

public class AccountSecurity {

    private AccountSecurity(){
    }

    public static String[] logCredentials(String email, String password){
        String strHash = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] binHash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (Byte b : binHash){
                sb.append(Integer.toHexString(b & 0xFF));
            }
            strHash = sb.toString();
        }
        catch(NoSuchAlgorithmException e){
            Log.d("ERROR", "Account security failed to create a hash");
            return null;
        }

        return new String[]{email, strHash};
    }



}
