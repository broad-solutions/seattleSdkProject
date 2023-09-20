package com.example.seattlesdk.model;

import java.util.List;

public class AppConfigDTO {
    private int code;
    private String msg;
    private String error;
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public static class Data {
        private int webviewApiEnabled;
        private List<String> websites;
        private String adManagerAppId;
        private int urlSwitchTime;

        public int getWebviewApiEnabled() {
            return webviewApiEnabled;
        }

        public void setWebviewApiEnabled(int webviewApiEnabled) {
            this.webviewApiEnabled = webviewApiEnabled;
        }

        public List<String> getWebsites() {
            return websites;
        }

        public void setWebsites(List<String> websites) {
            this.websites = websites;
        }

        public String getAdManagerAppId() {
            return adManagerAppId;
        }

        public void setAdManagerAppId(String adManagerAppId) {
            this.adManagerAppId = adManagerAppId;
        }

        public int getUrlSwitchTime() {
            return urlSwitchTime;
        }

        public void setUrlSwitchTime(int urlSwitchTime) {
            this.urlSwitchTime = urlSwitchTime;
        }
    }
}
