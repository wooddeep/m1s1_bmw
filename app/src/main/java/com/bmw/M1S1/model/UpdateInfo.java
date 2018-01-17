package com.bmw.M1S1.model;

public class UpdateInfo
{
        private String version;
        private String description;
        private String url;
        private String apkName;

        public String getApkName() {
                return apkName;
        }

        public void setApkName(String apkName) {
                this.apkName = apkName;
        }

        public String getVersion()
        {
                return version;
        }
        public void setVersion(String version)
        {
                this.version = version;
        }
        public String getDescription()
        {
                return description;
        }
        public void setDescription(String description)
        {
                this.description = description;
        }
        public String getUrl()
        {
                return url;
        }
        public void setUrl(String url)
        {
                this.url = url;
        }

        @Override
        public String toString() {
                return "UpdateInfo{" +
                        "version='" + version + '\'' +
                        ", description='" + description + '\'' +
                        ", url='" + url + '\'' +
                        '}';
        }
}
