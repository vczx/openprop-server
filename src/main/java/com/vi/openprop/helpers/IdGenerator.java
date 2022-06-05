package com.vi.openprop.helpers;

import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Slf4j
public class IdGenerator {
    public static Optional<String> generateId(String input){
        try {
            return Optional.of(md5Hash(input));
        } catch (NoSuchAlgorithmException e) {
            log.info("Error during hashing process ",e);
        }
        return Optional.empty();
    }

    private static String md5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(input.getBytes());
        byte[] digest = md.digest();
        return DatatypeConverter.printHexBinary(digest).toUpperCase();
    }

}
