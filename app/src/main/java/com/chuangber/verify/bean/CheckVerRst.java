package com.chuangber.verify.bean;

/**
 * Created by jinyh on 2018/5/2.
 * 检查更新版本
 */

public class CheckVerRst extends BaseRst {

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    Version version;

    public class Version{
        long createtime;
        String l_version; //版本号
        String download_url;
        String detail;

        public long getCreatetime() {
            return createtime;
        }

        public void setCreatetime(long createtime) {
            this.createtime = createtime;
        }

        public String getL_version() {
            return l_version;
        }

        public void setL_version(String l_version) {
            this.l_version = l_version;
        }

        public String getDownload_url() {
            return download_url;
        }

        public void setDownload_url(String download_url) {
            this.download_url = download_url;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }
    }
}
