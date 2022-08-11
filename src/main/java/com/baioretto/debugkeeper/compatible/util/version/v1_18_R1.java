package com.baioretto.debugkeeper.compatible.util.version;

import com.baioretto.debugkeeper.compatible.util.IGameUtil;

@SuppressWarnings("unused")
public class v1_18_R1 extends IGameUtil {
    @Override
    protected String getGetMillisMethodName() {
        return "c";
    }
}
