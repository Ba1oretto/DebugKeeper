package com.baioretto.debugkeeper.compatible.player.version;

import com.baioretto.debugkeeper.compatible.player.IPlayerUtil;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class v1_18_R2 extends IPlayerUtil {
    @Override
    protected @NotNull String getKeepAliveTimeFieldName() {
        return "g";
    }
}