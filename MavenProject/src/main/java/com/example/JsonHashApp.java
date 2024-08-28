package com.example;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class JsonHashApp {
	
	 public static void main(String[] args) {
	        if (args.length != 2) {
	            System.out.println("Usage: java -jar test.jar <PRN Number> <Path to JSON File>");
	            return;
	        }
	        
	        String prnNumber = args[0].toLowerCase().replaceAll("\\s+", "");
	        String jsonFilePath = args[1];
	        
	        try {
	            String destinationValue = findDestinationValue(jsonFilePath);
	            if (destinationValue != null) {
	                String randomString = generateRandomString(8);
	                String concatenatedString = prnNumber + destinationValue + randomString;
	                String md5Hash = generateMD5Hash(concatenatedString);

	                System.out.println(md5Hash + ";" + randomString);
	            } else {
	                System.out.println("Key 'destination' not found in the JSON file.");
	            }
	        } catch (IOException | NoSuchAlgorithmException e) {
	            e.printStackTrace();
	        }
	    }
	 
	 private static String findDestinationValue(String filePath) throws IOException {
	        ObjectMapper objectMapper = new ObjectMapper();
	        JsonNode rootNode = objectMapper.readTree(new File(filePath));

	        return traverseJson(rootNode);
	    }
	 private static String traverseJson(JsonNode node) {
	        if (node.isObject()) {
	            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
	            while (fields.hasNext()) {
	                Map.Entry<String, JsonNode> field = fields.next();
	                if (field.getKey().equals("destination")) {
	                    return field.getValue().asText();
	                }
	                String result = traverseJson(field.getValue());
	                if (result != null) {
	                    return result;
	                }
	            }
	        } else if (node.isArray()) {
	            for (JsonNode arrayItem : node) {
	                String result = traverseJson(arrayItem);
	                if (result != null) {
	                    return result;
	                }
	            }
	        }
	        return null;
	    }
	 
	 private static String generateRandomString(int length) {
	        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	        Random random = new Random();
	        StringBuilder sb = new StringBuilder(length);
	        for (int i = 0; i < length; i++) {
	            sb.append(characters.charAt(random.nextInt(characters.length())));
	        }
	        return sb.toString();
	    }

	 private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
	        MessageDigest md = MessageDigest.getInstance("MD5");
	        byte[] hashInBytes = md.digest(input.getBytes());
	        StringBuilder sb = new StringBuilder();
	        for (byte b : hashInBytes) {
	            sb.append(String.format("%02x", b));
	        }
	        return sb.toString();
	    }
	}




