package org.socialhistoryservices.delivery.request.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.image.ImageCaptchaService;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CaptchaController {
    @Autowired
    protected ImageCaptchaService captchaService;

    @RequestMapping("/captcha")
    public void showCaptcha(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            String id = request.getSession().getId();
            BufferedImage imageChallenge = captchaService.getImageChallengeForID(id, LocaleContextHolder.getLocale());
            ImageIO.write(imageChallenge, "jpeg", outputStream);
        }
        catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        catch (CaptchaServiceException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");

        ServletOutputStream responseOutputStream = response.getOutputStream();
        responseOutputStream.write(outputStream.toByteArray());
        responseOutputStream.flush();
        responseOutputStream.close();
    }
}
