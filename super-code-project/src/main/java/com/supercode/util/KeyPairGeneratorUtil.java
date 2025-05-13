package com.supercode.util;

import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class KeyPairGeneratorUtil {

    public static void main(String[] args) throws Exception {
        // Buat pasangan kunci RSA 2048 bit
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();

        // Simpan private key (PKCS#8 format)
        savePemFile("src/main/resources/privateKey.pem", keyPair.getPrivate(), "PRIVATE KEY");

        // Simpan public key (X.509 format)
        savePemFile("src/main/resources/publicKey.pem", keyPair.getPublic(), "PUBLIC KEY");

        System.out.println("Key pair berhasil dibuat!");
    }

    private static void savePemFile(String filename, Object key, String type) throws IOException {
        byte[] encoded;
        if (key instanceof PrivateKey) {
            encoded = ((PrivateKey) key).getEncoded();
        } else if (key instanceof PublicKey) {
            encoded = ((PublicKey) key).getEncoded();
        } else {
            throw new IllegalArgumentException("Tipe key tidak dikenali");
        }

        String pemContent = "-----BEGIN " + type + "-----\n"
                + Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(encoded)
                + "\n-----END " + type + "-----";

        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(pemContent);
        }
    }
}
