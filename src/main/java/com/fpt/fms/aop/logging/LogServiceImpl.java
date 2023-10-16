package com.fpt.fms.aop.logging;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Component
public class LogServiceImpl implements LogService{

    private static final Logger logger = LoggerFactory.getLogger(LogInterceptor.class);
    private static final Gson gson = new Gson();

    @Override
    public void requestLogging(HttpServletRequest request, Object body) {
        StringBuilder stringBuilder =  new StringBuilder();
        Map<String, String> parameters = buildParametersMap(request);

        stringBuilder.append("\nMethod=[").append(request.getMethod()).append("] ");
        stringBuilder.append("\nPath=[").append(request.getRequestURI()).append("] ");
        stringBuilder.append("\nRequestHeaders=[").append(buildHeadersMap(request)).append("] ");

        if (!parameters.isEmpty()) {
            stringBuilder.append("\nparameters=[").append(parameters).append("] ");
        }

        if (body != null) {
            String strBody = gson.toJson(body);
            stringBuilder.append("\nbody=[\n" + strBody + "\n]");
        }

        logger.info(stringBuilder.toString());
    }

    @Override
    public void responseLogging(HttpServletRequest request, HttpServletResponse response, Object body) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("\nRESPONSE ");
        stringBuilder.append("\nmethod=[").append(request.getMethod()).append("] ");
        stringBuilder.append("\npath=[").append(request.getRequestURI()).append("] ");
        stringBuilder.append("\nresponseHeaders=[").append(buildHeadersMap(response)).append("] ");

        String strBody = gson.toJson(body);
        stringBuilder.append("\nresponseBody=[\n").append(strBody).append("\n] ");

        logger.info(stringBuilder.toString());
    }

    private Map<String, String> buildParametersMap(HttpServletRequest httpServletRequest) {
        Map<String, String> resultMap = new HashMap<>();
        Enumeration<String> parameterNames = httpServletRequest.getParameterNames();

        while (parameterNames.hasMoreElements()) {
            String key = parameterNames.nextElement();
            String value = httpServletRequest.getParameter(key);
            resultMap.put(key, value);
        }

        return resultMap;
    }

    @SuppressWarnings("rawtypes")
    private Map<String, String> buildHeadersMap(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();

        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }

        return map;
    }

    private Map<String, String> buildHeadersMap(HttpServletResponse response) {
        Map<String, String> map = new HashMap<>();

        Collection<String> headerNames = response.getHeaderNames();
        for (String header : headerNames) {
            map.put(header, response.getHeader(header));
        }

        return map;
    }

}
