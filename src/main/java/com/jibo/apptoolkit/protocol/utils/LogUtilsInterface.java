package com.jibo.apptoolkit.protocol.utils;

/*
 * Created by Jibo, Inc. on 1/26/18.
 */
/** @hide */
public interface LogUtilsInterface {
    public String makeLogTag(String str);
    public String makeLogTag(Class cls);
    public void LOGD(final String tag, String message);
    public void LOGD(final String tag, String message, Throwable cause);
    public void LOGV(final String tag, String message);
    public void LOGV(final String tag, String message, Throwable cause);
    public void LOGI(final String tag, String message);
    public void LOGI(final String tag, String message, Throwable cause);
    public void LOGW(final String tag, String message);
    public void LOGW(final String tag, String message, Throwable cause);
    public void LOGE(final String tag, String message);
    public void LOGE(final String tag, String message, Throwable cause);
}
