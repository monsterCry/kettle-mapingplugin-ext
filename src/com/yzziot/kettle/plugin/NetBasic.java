package com.yzziot.kettle.plugin;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;

/**
 * @Description:
 * @Author: admin
 * @Date: 2020/6/1
 * @Modified By:
 */
public abstract class NetBasic {

    protected HashMap<Integer,Pair<ReqParser, ExecutorService>> parserMap = new HashMap<>();

    public void processRecive(NetCmd cmd) {
        switch (cmd.getType()) {
            case REQUEST:{
                processReciveRequest(cmd);
            } break;
            case RESPONSE:{
                processReciveResponse(cmd);
            }break;
            default:{
                break;
            }
        };
    }

    private void processReciveRequest(NetCmd cmd) {

    }

    private void processReciveResponse(NetCmd cmd) {

    }

    public void addReqParser(int cmd,ExecutorService worker,ReqParser parser) {
        Pair<ReqParser,ExecutorService> pair = new Pair<>(parser,worker);
        parserMap.put(cmd,pair);
    }
}
