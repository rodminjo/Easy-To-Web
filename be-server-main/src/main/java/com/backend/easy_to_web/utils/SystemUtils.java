package com.backend.easy_to_web.utils;

public class SystemUtils {

  // ARM 아키텍처 확인
  public static boolean isArmArchitecture() {
    return System.getProperty("os.arch").contains("aarch64");
  }

  // 윈도우 운영 체제 확인
  public static boolean isWindows() {
    return System.getProperty("os.name").toLowerCase().contains("win");
  }

  // 리눅스 운영 체제 확인
  public static boolean isLinux() {
    String osName = System.getProperty("os.name").toLowerCase();
    return osName.contains("nix") || osName.contains("nux");
  }
}
