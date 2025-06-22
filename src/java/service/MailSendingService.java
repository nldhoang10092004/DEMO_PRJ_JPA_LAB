package service;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import model.User;
import jakarta.servlet.http.HttpSession;

public class MailSendingService {

    // Gmail SMTP
    private final String from = "nldhoang10092004@gmail.com"; // ✅ Gmail của bạn
    private final String password = "jpjd hdgz wnoj dyxg"; // ✅ App Password từ Google
    private final String host = "smtp.gmail.com";

    
    public void sendOrderConfirmation(HttpSession httpSession, int orderId) throws MessagingException {
        User user = (User) httpSession.getAttribute("user");

        if (user == null) {
            throw new MessagingException("❌ Không tìm thấy người dùng trong session.");
        }

        String to = user.getEmail();

        if (to == null || to.isBlank()) {
            throw new MessagingException("❌ Email người nhận bị null hoặc rỗng.");
        }

        to = to.trim();
        InternetAddress toAddress = new InternetAddress(to);
        toAddress.validate();

        // Cấu hình SMTP cho Gmail
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        // Session Gmail
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        // Soạn email
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipient(Message.RecipientType.TO, toAddress);
        message.setSubject("Xác nhận đơn hàng");
        message.setText("Xin chào " + user.getUsername() + ",\n\n"
            + "Cảm ơn bạn đã đặt hàng tại hệ thống của chúng tôi.\n"
            + "Mã đơn hàng của bạn là: #" + orderId + "\n\n"
            + "Trân trọng,\nSmartHome Store");

        // Gửi email
        Transport.send(message);
        System.out.println("✅ Đã gửi email xác nhận đến: " + to);
    }
}
