package org.example;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@WebServlet(value = "/time")
public class ThymeleafController extends HttpServlet {
    private TemplateEngine engine;
    private static final String TIMEZONE = "timezone";

    @Override
    public void init() throws ServletException {
        engine = new TemplateEngine();

        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix("C:\\Users\\HP\\IdeaProjects\\Task9_dev\\templates\\");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        if(session == null) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        String timezoneValue = req.getParameter(TIMEZONE);
        if(!StringUtils.isEmpty(timezoneValue)) {
            session.setAttribute(TIMEZONE, timezoneValue);

            Context simpleContext = new Context(
                    req.getLocale(),
                    Map.of("time", someTimeMethod(timezoneValue))
            );

            engine.process("test", simpleContext, resp.getWriter());
            resp.getWriter().close();
            return;
        }

        Object attribute = session.getAttribute(TIMEZONE);
        if(!(attribute instanceof String)) {
//            resp.getWriter().println("There is no saved timezone");

            Context simpleContext = new Context(
                    req.getLocale(),
                    Map.of("time", someTimeMethod("UTC 0"))
            );
            engine.process("test", simpleContext, resp.getWriter());
            resp.getWriter().close();

            return;
        }

        String currentTimezone = (String)attribute;

        Context simpleContext = new Context(
                req.getLocale(),
                Map.of("time", someTimeMethod(currentTimezone))
        );
        engine.process("test", simpleContext, resp.getWriter());
        resp.getWriter().close();
    }
    public String someTimeMethod(String timezoneValue) {
        String result = "";
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[] split = timezoneValue.split(" ");
        int hour = Integer.parseInt(split[1]);
        cal.add(Calendar.HOUR_OF_DAY, hour - 2);
        Date calTime = cal.getTime();
            String format = dateFormat.format(calTime);
            result = format + " " + timezoneValue;
        return result;
    }
}


