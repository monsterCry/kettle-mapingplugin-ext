package com.yzziot.kettle.plugin;

/**
 * @Description:
 * @Author: admin
 * @Date: 2020/5/30
 * @Modified By:
 */
public interface ReqParser {
    Object parseReq(NetCmd cmd);
}
