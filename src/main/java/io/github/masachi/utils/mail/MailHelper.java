package io.github.masachi.utils.mail;

import io.github.masachi.exceptions.ServerErrorException;
import io.github.masachi.utils.BaseUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;

//@Component
@PropertySource({"classpath:core.properties"})
@Log4j2
public class MailHelper {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${mail.fromMail.addr}")
    private String sendFrom;

    @Value("${mail.toMail.addrs}")
    private String sendTo;

    private static String LOCAL_HOST = "localhost";
    private static String DEV = "dev";

    @Autowired
    Environment environment;

    @Autowired
    HttpServletRequest request;

    public void send(Throwable error) {
        String activeProfile = environment.getProperty("spring.profiles.active");

        if (DEV.equalsIgnoreCase(activeProfile)) {
            log.info("no need to send alarm emails in dev env !!!");
            return;
        }

        //server ip
        String localhost;
        try {
            localhost = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            localhost = LOCAL_HOST;
            log.warn(e.getMessage());
        }

        int servirity = 3;
        String title;
        if (error instanceof OutOfMemoryError) {
            servirity = 1;
            title = "OutOfMemory Error!!!";
        } else if (error instanceof ServerErrorException) {
            servirity = ((ServerErrorException) error).getSeverity();
            title = ((ServerErrorException) error).getTitle();
        } else if (error instanceof RedisConnectionFailureException) {
            servirity = 1;
            title = "Redis is out-of-connection!!!";
        } else if (error instanceof DataAccessException || error instanceof NullPointerException) {
            servirity = 1;
            title = error.getClass().getSimpleName();
        } else {
            // Ignore the other error
            log.info(error.getClass().getSimpleName() + " is ignored!!!");
            return;
        }

        title = "【Sev-" + activeProfile + "-" + servirity + "】 " + title + " from " + localhost;

        //send html
        String requestURL = request.getRequestURL().toString();
        if (requestURL.contains(LOCAL_HOST)) {
            requestURL = requestURL.replace(LOCAL_HOST, localhost);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<table border = \"1\">");
        addRow(sb, "ClientIP", request.getRemoteAddr());
        addRow(sb, "URL", requestURL);
        addRow(sb, "Parameters", request.getParameterMap().toString());
        addRow(sb, "CallStack", error.getMessage() + getStackTrace(error));
        sb.append("</table>");

        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            helper.setFrom(sendFrom);
            helper.setTo(sendTo.split(","));
            helper.setSubject(title);
            helper.setText(sb.toString(), true);
            mailSender.send(msg);
        } catch (MessagingException e) {
            log.error(e);
        }
    }

    public void send(String sendTo, String title, String body, boolean isHtml) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            helper.setFrom(sendFrom);
            helper.setTo(sendTo.split(","));
            helper.setSubject(title);
            helper.setText(body, isHtml);
            mailSender.send(msg);
        } catch (MessagingException e) {
            log.error(e);
        }
    }


    public String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    private void addRow(StringBuilder sb, String title, String message) {
        sb.append("<tr>")
                .append("<th>" + title + "</th>")
                .append("<td>")
                .append(message)
                .append("</td>")
                .append("</tr>");
    }
}
