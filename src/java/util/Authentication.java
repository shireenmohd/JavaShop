/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import org.apache.commons.codec.binary.Base64;

/**
 * This class is for Hashing and verifying the password
 */
public class Authentication {

    // The higher the number of iterations the more 
    // expensive computing the hash is for us and
    // also for an attacker.
    private static final int iterations = 20 * 1000;
    private static final int saltLen = 32;
    private static final int desiredKeyLen = 256;

    /**
     * Computes a salted PBKDF2 hash of given plaintext password suitable for
     * storing in a database. Empty passwords are not supported.
     */
    public static String getSaltedHash(String password) throws Exception {
        byte[] salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLen);
        // store the salt with the password
        return Base64.encodeBase64String(salt) + "$" + hash(password, salt);
    }

    /**
     * Checks whether given plaintext password corresponds to a stored salted
     * hash of the password.
     */
    public static boolean checkPassword(String password, String stored) throws Exception {
        String[] saltAndPass = stored.split("\\$");
        if (saltAndPass.length != 2) {
            throw new IllegalStateException(
                    "The stored password have the form 'salt$hash'");
        }
        String hashOfInput = hash(password, Base64.decodeBase64(saltAndPass[0]));
        return hashOfInput.equals(saltAndPass[1]);
    }

    /**
     * Hash the password using PBKDF2 from Sun
     * 
     * @param password
     * @param salt
     * @return hashed password
     * @throws Exception 
     */
    private static String hash(String password, byte[] salt) throws Exception {
        if (password == null || password.length() == 0) {
            throw new IllegalArgumentException("Empty passwords are not supported.");
        }
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey key = f.generateSecret(new PBEKeySpec(
                password.toCharArray(), salt, iterations, desiredKeyLen)
        );
        return Base64.encodeBase64String(key.getEncoded());
    }
    
    /*public static void main(String[] args) throws Exception {
        String saltedHash = Authentication.getSaltedHash("4uIdo0!");
        boolean isAuthenticated = Authentication.checkPassword("4uIdo0!", saltedHash);
        
        System.out.println(saltedHash);
        System.out.println(isAuthenticated);
    }*/
}
