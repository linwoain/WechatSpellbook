package com.linwoain.demo.utils;

import java.io.DataOutputStream;

/***
 * root cmd命令
 */
public class RootCmd {

  //翻译并执行相应的adb命令
  public static boolean execCmd(String command) {
    Process process = null;
    DataOutputStream os = null;
    try {
      process = Runtime.getRuntime().exec("su");
      os = new DataOutputStream(process.getOutputStream());
      os.writeBytes(command + "\n");
      os.writeBytes("exit\n");
      os.flush();
      //            Lo.e("updateFile", "======000==writeSuccess======");
      process.waitFor();
    } catch (Exception e) {
      //            Log.e("updateFile", "======111=writeError======" + e.toString());
      return false;
    } finally {
      try {
        if (os != null) {
          os.close();
        }
        if (process != null) {
          process.destroy();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return true;
  }

  //移动文件
  public static void moveFileToSystem(String filePath, String sysFilePath) {
    execCmd("mount -o rw,remount /system");
    execCmd("chmod 777 /system");
    execCmd("cp  " + filePath + " " + sysFilePath);
    execCmd("chmod 777 /system/app/LimiTools.apk");
  }

  //删除文件
  public static void deleteFile() {
    execCmd("mount -o rw,remount /system");
    execCmd("chmod 777 /system/app");
    execCmd("rm /system/app/*LimiTools_*.apk");
  }
}
