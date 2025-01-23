package com.bookstore.util;

public class BaseResponse<T> {
    private String requestId;
    private T response;
    private ResponseMessage responseMessage;

    // Constructors
    public BaseResponse() {
    }

    public BaseResponse(String requestId, T response, ResponseMessage responseMessage) {
        this.requestId = requestId;
        this.response = response;
        this.responseMessage = responseMessage;
    }

    // Getters and Setters
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }

    public ResponseMessage getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(ResponseMessage responseMessage) {
        this.responseMessage = responseMessage;
    }

    // Inner class: ResponseMessage
    public static class ResponseMessage {
        private String status;
        private String message;
        private String trace;

        // Constructors
        public ResponseMessage() {
        }

        public ResponseMessage(String status, String message, String trace) {
            this.status = status;
            this.message = message;
            this.trace = trace;
        }

        // Getters and Setters
        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getTrace() {
            return trace;
        }

        public void setTrace(String trace) {
            this.trace = trace;
        }
    }
}
