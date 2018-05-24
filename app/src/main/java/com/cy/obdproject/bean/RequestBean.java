package com.cy.obdproject.bean;

public class RequestBean {
    private String requestId;
    private String requestName;

    public RequestBean() {
        super();
    }

    public RequestBean(String requestId, String requestName) {
        this.requestId = requestId;
        this.requestName = requestName;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestName() {
        return requestName;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }

    @Override
    public String toString() {
        return "RequestBean{" +
                "requestId='" + requestId + '\'' +
                ", requestName='" + requestName + '\'' +
                '}';
    }
}
