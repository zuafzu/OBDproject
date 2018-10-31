package com.cy.obdproject.bean;

public class WriteDataBean {

    private String fileId;
    private String fileName;
    private String filePath;
    private String isLocal;
    private String localHas;
    private String needCheck;// 是否需要审核，1需要，其它不需要
    private String checkState;// 审核状态，0待审核，1审核中，2审核成功，3审核失败
    private String stateName;// 审核状态名称

    public WriteDataBean() {
        super();
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getIsLocal() {
        return isLocal;
    }

    public void setIsLocal(String isLocal) {
        this.isLocal = isLocal;
    }

    public String getLocalHas() {
        return localHas;
    }

    public void setLocalHas(String localHas) {
        this.localHas = localHas;
    }

    public String getNeedCheck() {
        return needCheck;
    }

    public void setNeedCheck(String needCheck) {
        this.needCheck = needCheck;
    }

    public String getCheckState() {
        return checkState;
    }

    public void setCheckState(String checkState) {
        this.checkState = checkState;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    @Override
    public String toString() {
        return "WriteDataBean{" +
                "fileId='" + fileId + '\'' +
                ", fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", isLocal='" + isLocal + '\'' +
                ", localHas='" + localHas + '\'' +
                ", needCheck='" + needCheck + '\'' +
                ", checkState='" + checkState + '\'' +
                ", stateName='" + stateName + '\'' +
                '}';
    }
}
