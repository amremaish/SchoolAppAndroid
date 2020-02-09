package com.example.SchoolApp.Models;

public class Message {
    private String messageText;
    private String receivedEmail , sendEmail;
    private String send_user_name , received_user_name;
    private String messageTime;
    private String seenFromSender  = "false" , seenFromReceiver = "false" ;

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getSendEmail() {
        return sendEmail;
    }

    public void setSendEmail(String sendEmail) {
        this.sendEmail = sendEmail;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public String getReceivedEmail() {
        return receivedEmail;
    }

    public void setReceivedEmail(String receivedEmail) {
        this.receivedEmail = receivedEmail;
    }

    public String getSend_user_name() {
        return send_user_name;
    }

    public void setSend_user_name(String send_user_name) {
        this.send_user_name = send_user_name;
    }

    public String getReceived_user_name() {
        return received_user_name;
    }

    public void setReceived_user_name(String received_user_name) {
        this.received_user_name = received_user_name;
    }


    public String getSeenFromSender() {
        return seenFromSender;
    }

    public void setSeenFromSender(String seenFromSender) {
        this.seenFromSender = seenFromSender;
    }

    public String getSeenFromReceiver() {
        return seenFromReceiver;
    }

    public void setSeenFromReceiver(String seenFromReceiver) {
        this.seenFromReceiver = seenFromReceiver;
    }
}
