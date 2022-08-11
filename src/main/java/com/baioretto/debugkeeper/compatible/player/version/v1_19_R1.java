package com.baioretto.debugkeeper.compatible.player.version;

import com.baioretto.debugkeeper.compatible.player.IPlayerUtil;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class v1_19_R1 extends IPlayerUtil {
    @Override
    protected @NotNull String getKeepAliveTimeFieldName() {
        return "k";
    }
}
