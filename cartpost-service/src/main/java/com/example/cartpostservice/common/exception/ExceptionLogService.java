package com.example.cartpostservice.common.exception;


import feign.FeignException;
import feign.RetryableException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ExceptionLogService {

    public CustomStatusCode logExternalServerException(Exception ex, String calledServiceName) {
        CustomStatusCode errorCode = CustomStatusCode.INTERNAL_MODULE_SERVER_ERROR;
        String serviceUrl = "Unknown Feign Service";
        String logMessage;

        if (ex instanceof FeignException feignException) {
            int status = feignException.status();

            if (feignException.request() != null) {
                serviceUrl = feignException.request().url();
            }

            if (status == HttpStatus.NOT_FOUND.value()) {
                errorCode = CustomStatusCode.NOT_FOUND_INTERNAL_MODULE_SERVER;
            } else if (status >= 400 && status < 500) {
                errorCode = CustomStatusCode.BAD_REQUEST_INTERNAL_MODULE_SERVER;
            } else if (status >= 500) {
                errorCode = CustomStatusCode.INTERNAL_MODULE_SERVER_ERROR;
            }

            logMessage = String.format("Feign HTTP Error (Status: %d): %s", status, feignException.getMessage());
        }

        if (ex instanceof RetryableException || ex instanceof ConnectException
                || ex instanceof SocketTimeoutException) {
            errorCode = CustomStatusCode.SERVICE_MODULE_UNAVAILABLE;
            logMessage = ex.getClass().getSimpleName() + ": " + ex.getMessage();
        } else {
            logMessage = "Unknown External Error: " + ex.getMessage();
        }

        log.error("--- Feign Client Call Failed ---");
        log.error("External Server Error Occurred ({}) :", calledServiceName);
        log.error("Service URL: {}", serviceUrl);
        log.error("Log Message: {}", logMessage);
        log.error("Exception Trace: ", ex);
        log.error("------------------------------");

        return errorCode;
    }
}
