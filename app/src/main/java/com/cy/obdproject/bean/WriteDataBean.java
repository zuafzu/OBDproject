package com.cy.obdproject.bean;

public class WriteDataBean {

    private String fileId;
    private String fileName;
    private String filePath;
    private String isLocal;
    private String localHas;

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

    @Override
    public String toString() {
        return "WriteDataBean{" +
                "fileId='" + fileId + '\'' +
                ", fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", isLocal='" + isLocal + '\'' +
                ", localHas='" + localHas + '\'' +
                '}';
    }
}
