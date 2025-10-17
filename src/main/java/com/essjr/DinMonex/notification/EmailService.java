package com.essjr.DinMonex.notification;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.*;

/**
 * Serviço responsável por abstrair a lógica de envio de e-mails
 * usando o AWS Simple Email Service (SES).
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    // O Spring Cloud AWS cria e configura este cliente automaticamente para nós
    // com base nas credenciais do nosso ficheiro application.properties.
    private final SesV2Client sesV2Client;

    // Injeta o e-mail do remetente a partir do ficheiro application.properties.
    @Value("${cloud.aws.ses.from}")
    private String senderEmail;

    @Autowired
    public EmailService(SesV2Client sesV2Client) {
        this.sesV2Client = sesV2Client;
    }

    /**
     * Envia um e-mail de texto simples.
     *
     * @param to      O e-mail do destinatário.
     * @param subject O assunto do e-mail.
     * @param body    O corpo do e-mail em texto plano.
     */
    public void sendEmail(String to, String subject, String body) {
        try {
            // 1. Cria o objeto de destino.
            Destination destination = Destination.builder()
                    .toAddresses(to)
                    .build();

            // 2. Cria o conteúdo do e-mail.
            Content emailSubject = Content.builder().data(subject).build();
            Content emailBody = Content.builder().data(body).build();
            Body messageBody = Body.builder().text(emailBody).build();

            // 3. Junta o assunto e o corpo no objeto de conteúdo.
            EmailContent emailContent = EmailContent.builder()
                    .simple(Message.builder()
                            .subject(emailSubject)
                            .body(messageBody)
                            .build())
                    .build();

            // 4. Cria o pedido de envio de e-mail completo.
            SendEmailRequest request = SendEmailRequest.builder()
                    .fromEmailAddress(senderEmail)
                    .destination(destination)
                    .content(emailContent)
                    .build();

            // 5. Usa o cliente do SES para enviar o e-mail.
            sesV2Client.sendEmail(request);

            log.info("E-mail enviado com sucesso para {}", to);

        } catch (SesV2Exception e) {
            // Em caso de erro, regista uma mensagem detalhada.
            log.error("Erro ao enviar e-mail para {}: {}", to, e.awsErrorDetails().errorMessage());
            // Em um projeto real, você poderia lançar uma exceção personalizada aqui.
        }
    }
}

