package com.example.studentportal.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.Enumeration;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        // Collect all available error information
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        String path = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        // Set default values if attributes are null
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        }
        if (message == null) {
            message = "No error message available";
        }
        if (path == null) {
            path = "Unknown path";
        }

        // Add information to the model
        model.addAttribute("status", status);
        model.addAttribute("error", "HTTP " + status);
        model.addAttribute("message", message);
        model.addAttribute("path", path);
        model.addAttribute("timestamp", new Date());

        // Add exception details if available
        if (exception != null && exception instanceof Throwable) {
            Throwable ex = (Throwable) exception;
            model.addAttribute("exception", ex.getClass().getName());
            model.addAttribute("trace", getStackTrace(ex));
            model.addAttribute("rootCause", getRootCause(ex));
        }

        // Add request details
        model.addAttribute("requestDetails", getRequestDetails(request));

        return "error";
    }

    private String getStackTrace(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append("\tat ").append(element).append("\n");
        }
        return sb.toString();
    }

    private String getRootCause(Throwable throwable) {
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause.toString();
    }

    private String getRequestDetails(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("Request Method: ").append(request.getMethod()).append("\n");
        sb.append("Request URL: ").append(request.getRequestURL()).append("\n");

        // Headers
        sb.append("\nHeaders:\n");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            sb.append(header).append(": ").append(request.getHeader(header)).append("\n");
        }

        // Parameters
        sb.append("\nParameters:\n");
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String param = params.nextElement();
            sb.append(param).append(": ").append(request.getParameter(param)).append("\n");
        }

        return sb.toString();
    }
}