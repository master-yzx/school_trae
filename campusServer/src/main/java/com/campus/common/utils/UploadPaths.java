package com.campus.common.utils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class UploadPaths {

    private UploadPaths() {
    }

    public static Path getUploadDir() {
        Path cwd = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
        String last = cwd.getFileName() == null ? "" : cwd.getFileName().toString();

        // dev 环境经常从子模块目录启动（campusServer/campusWeb），统一回到项目根目录的 upload/
        if ("campusServer".equalsIgnoreCase(last) || "campusWeb".equalsIgnoreCase(last)) {
            Path parent = cwd.getParent();
            if (parent != null) {
                return parent.resolve("upload").toAbsolutePath().normalize();
            }
        }
        return cwd.resolve("upload").toAbsolutePath().normalize();
    }
}

