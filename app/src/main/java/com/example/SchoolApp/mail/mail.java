package com.example.SchoolApp.mail;

import android.os.AsyncTask;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by Amr on 07/03/2018.
 */
public class mail {
    private Session session = null;
    private String rec, subject, textMessage;

    public void sendMail(String rec, String subject, String textMessage  ) {

	
        this.rec = rec;
        this.subject = subject;
        this.textMessage = textMessage;
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
	

        session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("school.learning2020@gmail.com", "A@a123456");
            }
        });

        RetreiveFeedTask task = new RetreiveFeedTask();
        task.execute();

    }



    class RetreiveFeedTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {

                Message message = new MimeMessage(session);

                message.setFrom(new InternetAddress("school.learning2020@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(rec));

                message.setSubject(subject);
                message.setContent(textMessage, "text/html; charset=utf-8");

                Transport.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }

}
