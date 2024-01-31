package com.smart.services;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
	
	
	 public boolean sendEamil(String subject, String message, String to){

	        // rest of code

	        boolean flag = false;
	        String from = "demobhaiya15@gmail.com";
	        
	        // variable for gmail
	        String host = "smtp.gmail.com";
	       

	        // get the system properties
	        Properties properties = System.getProperties();

	        System.out.println("PROPEERTIES " + properties);

	        // setting imp info to properties object

	        // host set
	        properties.put("mail.smtp.host", host);
	        properties.put("mail.smtp.port", "465");
	        properties.put("mail.smtp.ssl.enable", "true");
	        properties.put("mail.smtp.auth", "true");

	        // step 1: to get the session object

	        Session session = Session.getInstance(properties, new Authenticator() {

	            @Override
	            protected PasswordAuthentication getPasswordAuthentication() {

	                return new PasswordAuthentication("demobhaiya15@gmail.com", "Suraj@2000");
	            }

	        });

	        session.setDebug(true);
	        // step 2: compos message the message[doc, song etc]

	        MimeMessage mimeMessage = new MimeMessage(session);

	        try {
	            // from emailid

	            mimeMessage.setFrom(from);

	            // adding recipient to message
	            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

	            // adding subject to message
	            mimeMessage.setSubject(subject);

	            // adding text to message
	          //  mimeMessage.setText(message);
	            mimeMessage.setContent(message,"text/html");
	            
	            // send
	            // step 3 : sending by transport class

	            Transport.send(mimeMessage);

	            System.out.println("Sent Success !!!!");

	            flag=true;
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	          return flag;

	    }
	

}
