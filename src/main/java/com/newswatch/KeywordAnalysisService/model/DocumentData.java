package com.newswatch.KeywordAnalysisService.model;

public  class DocumentData {
        private String title;
        private String date;
        private String url;

        // Constructor
        public DocumentData(String title, String date, String url) {
            this.title = title;
            this.date = date;
            this.url = url;
        }

        // Getters and Setters
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }