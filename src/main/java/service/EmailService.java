package service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.ResourceBundle;

import exception.ErrorFactory;

public class EmailService {
	
	private static final String remitente;
    private static final String password;

    static {
        ResourceBundle config = ResourceBundle.getBundle("config");
        remitente = config.getString("mail.user").trim();
        password = config.getString("mail.password").trim();
    }
    private final Session session;

    public EmailService() {
    	Properties props = new Properties();
    	props.put("mail.smtp.auth", "true");
    	props.put("mail.smtp.starttls.enable", "true");
    	props.put("mail.smtp.starttls.required", "true");
    	props.put("mail.smtp.host", "smtp.gmail.com");
    	props.put("mail.smtp.port", "587");
    	props.put("mail.smtp.connectiontimeout", "10000");
    	props.put("mail.smtp.timeout", "10000");
    	props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
    	props.put("mail.smtp.ssl.protocols", "TLSv1.2");

    	this.session = Session.getInstance(props, new Authenticator() {
    	    @Override
    	    protected PasswordAuthentication getPasswordAuthentication() {
    	        return new PasswordAuthentication(remitente, password);
    	    }
    	});
    }

    public void sendEmail(String destinatario, String asunto, String cuerpoHtml) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(remitente, "FatMovies")); 
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(asunto);
            message.setContent(cuerpoHtml, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("Correo enviado con éxito a: " + destinatario);

        } catch (Exception e) {
            throw ErrorFactory.internal("Error al enviar correo");
        }
    }
}