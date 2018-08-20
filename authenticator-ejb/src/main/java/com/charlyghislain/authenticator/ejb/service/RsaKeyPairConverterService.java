package com.charlyghislain.authenticator.ejb.service;


import com.charlyghislain.authenticator.domain.domain.RsaKeyPair;
import com.charlyghislain.authenticator.domain.domain.exception.AuthenticatorRuntimeException;

import javax.ejb.Stateless;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;

@Stateless
public class RsaKeyPairConverterService {

    public RsaKeyPair generateNewKeyPair() {
        KeyPairGenerator keyPairGenerator = this.getKeyPairGenerator();
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        BigInteger modulus = privateKey.getModulus();
        BigInteger privateExponent = privateKey.getPrivateExponent();
        BigInteger publicExponent = publicKey.getPublicExponent();

        byte[] modulusBytes = modulus.toByteArray();
        byte[] privateExponentBytes = privateExponent.toByteArray();
        byte[] publicExponentBytes = publicExponent.toByteArray();

        RsaKeyPair rsaKeyPair = new RsaKeyPair();
        rsaKeyPair.setCreationTime(LocalDateTime.now());
        rsaKeyPair.setModulus(modulusBytes);
        rsaKeyPair.setPrivateExponent(privateExponentBytes);
        rsaKeyPair.setPublicExponent(publicExponentBytes);
        rsaKeyPair.setActive(true);
        return rsaKeyPair;
    }


    public RSAPublicKey loadPublicKey(RsaKeyPair rsaKeyPair) {
        byte[] modulusBytes = rsaKeyPair.getModulus();
        byte[] publicExponentBytes = rsaKeyPair.getPublicExponent();

        BigInteger modulus = new BigInteger(modulusBytes);
        BigInteger publicExponent = new BigInteger(publicExponentBytes);

        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(modulus, publicExponent);
        KeyFactory keyFactory = getKeyFactory();
        try {
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
            return (RSAPublicKey) publicKey;
        } catch (InvalidKeySpecException e) {
            throw new AuthenticatorRuntimeException("Unable to load public key", e);
        }
    }

    public String encodePublicKeyToPem(RsaKeyPair keyPair) {
        RSAPublicKey rsaPublicKey = loadPublicKey(keyPair);

        Base64.Encoder base64Encoder = Base64.getEncoder();
        byte[] encodedBase64Bytes = base64Encoder.encode(rsaPublicKey.getEncoded());
        String base64Pem = new String(encodedBase64Bytes, StandardCharsets.UTF_8);

        String[] linesArray = base64Pem.split("(?<=\\G.{64})");
        String pemLines = Arrays.stream(linesArray)
                .map(line -> line + "\n")
                .reduce(new StringBuilder(), StringBuilder::append, StringBuilder::append)
                .toString();

        return "-----BEGIN RSA PUBLIC KEY-----\n" +
                pemLines +
                "-----END RSA PUBLIC KEY-----\n";
    }

    public RSAPrivateKey loadPrivateKey(RsaKeyPair rsaKeyPair) {
        byte[] modulusBytes = rsaKeyPair.getModulus();
        byte[] privateExponentBytes = rsaKeyPair.getPrivateExponent();

        BigInteger modulus = new BigInteger(modulusBytes);
        BigInteger privateExponent = new BigInteger(privateExponentBytes);

        RSAPrivateKeySpec privateKeySpec = new RSAPrivateKeySpec(modulus, privateExponent);
        KeyFactory keyFactory = this.getKeyFactory();
        try {
            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
            return (RSAPrivateKey) privateKey;
        } catch (InvalidKeySpecException e) {
            throw new AuthenticatorRuntimeException("Unable to load private key", e);
        }
    }

    private KeyFactory getKeyFactory() {
        try {
            return KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new AuthenticatorRuntimeException("Unable to load RSA algorithm", e);
        }
    }

    private KeyPairGenerator getKeyPairGenerator() {
        try {
            return KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new AuthenticatorRuntimeException("Unable to load RSA algorithm", e);
        }
    }
}
