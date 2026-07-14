package com.machy.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final String from;

    public EmailService(JavaMailSender mailSender,
                        @Value("${app.smtp.from}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    public boolean sendPasswordRecovery(String to, String nombre, String username, String newPassword) {
        if (from == null || from.isBlank()) {
            log.warn("SMTP no configurado (app.smtp.from vacio). No se envio correo a {}", to);
            return false;
        }
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("Libreria Machy - Recuperacion de contrasena");

            String html = """
                <div style="font-family:Arial,sans-serif;max-width:480px;margin:0 auto;padding:24px;background:#0D1F3C;border-radius:12px;color:#fff">
                  <div style="text-align:center;margin-bottom:20px">
                    <div style="font-size:2rem;margin-bottom:4px">%s</div>
                    <div style="font-size:1.2rem;font-weight:700;color:#F59E0B">Libreria Machy</div>
                  </div>
                  <p style="color:rgba(255,255,255,.7);font-size:.9rem">Hola <strong style="color:#fff">%s</strong>,</p>
                  <p style="color:rgba(255,255,255,.7);font-size:.9rem">Tu contrasena ha sido restablecida. Usa estos datos para iniciar sesion:</p>
                  <div style="background:rgba(245,158,11,.1);border:1px solid rgba(245,158,11,.3);border-radius:8px;padding:16px;margin:16px 0;text-align:center">
                    <div style="font-size:.78rem;color:rgba(255,255,255,.4);margin-bottom:4px">USUARIO</div>
                    <div style="font-size:1.1rem;font-weight:700;color:#F59E0B;font-family:monospace">%s</div>
                    <div style="font-size:.78rem;color:rgba(255,255,255,.4);margin:12px 0 4px">NUEVA CONTRASENA</div>
                    <div style="font-size:1.1rem;font-weight:700;color:#F59E0B;font-family:monospace">%s</div>
                  </div>
                  <p style="color:rgba(255,255,255,.5);font-size:.78rem">Recomendamos cambiar la contrasena despues de iniciar sesion.</p>
                  <hr style="border:none;border-top:1px solid rgba(255,255,255,.08);margin:16px 0">
                  <p style="color:rgba(255,255,255,.25);font-size:.72rem;text-align:center">Libreria Machy SVM v4.0</p>
                </div>
                """.formatted("\uD83D\uDD10", nombre, username, newPassword);

            helper.setText(html, true);
            mailSender.send(msg);
            log.info("Correo de recuperacion enviado a {}", to);
            return true;
        } catch (MessagingException e) {
            log.error("Error al enviar correo a {}: {}", to, e.getMessage());
            return false;
        }
    }
}
