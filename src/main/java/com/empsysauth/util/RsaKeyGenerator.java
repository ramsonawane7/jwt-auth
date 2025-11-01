package com.empsysauth.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class RsaKeyGenerator {
    
    private static final String ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;
    
    public static void generateKeys(String rootPath) throws NoSuchAlgorithmException, IOException {
        // Generate RSA key pair
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGenerator.initialize(KEY_SIZE);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();
        
        // Encode keys to Base64
        String privateKeyBase64 = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        
        // Save private key
        Path privateKeyPath = Paths.get(rootPath, "private_key.pem");
        try (FileOutputStream fos = new FileOutputStream(privateKeyPath.toFile())) {
            fos.write("-----BEGIN PRIVATE KEY-----\n".getBytes());
            fos.write(formatKeyForPEM(privateKeyBase64).getBytes());
            fos.write("-----END PRIVATE KEY-----\n".getBytes());
        }
        
        // Save public key
        Path publicKeyPath = Paths.get(rootPath, "public_key.pem");
        try (FileOutputStream fos = new FileOutputStream(publicKeyPath.toFile())) {
            fos.write("-----BEGIN PUBLIC KEY-----\n".getBytes());
            fos.write(formatKeyForPEM(publicKeyBase64).getBytes());
            fos.write("-----END PUBLIC KEY-----\n".getBytes());
        }
        
        System.out.println("RSA keys generated successfully!");
        System.out.println("Private key saved to: " + privateKeyPath);
        System.out.println("Public key saved to: " + publicKeyPath);
    }
    
    private static String formatKeyForPEM(String key) {
        // Split key into 64 character lines for PEM format
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < key.length(); i += 64) {
            int end = Math.min(i + 64, key.length());
            formatted.append(key.substring(i, end)).append("\n");
        }
        return formatted.toString();
    }
}

