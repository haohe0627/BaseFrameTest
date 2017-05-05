package exception;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by JuQiu
 * on 2016/9/13.
 */

public class AppCrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String DEFAULT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/yttx";
    public static final String TAG = "CrashHandler";

    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private static AppCrashHandler INSTANCE = new AppCrashHandler();
    // 用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<String, String>();
    private Context mContext;

    private AppCrashHandler() {
    }

    public static AppCrashHandler getInstance() {
        return INSTANCE;
    }

    public void init(Context context) {
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
            //ex.printStackTrace();
        }
    }

    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        ex.printStackTrace();

        // 收集设备参数信息
        collectDeviceInfo(mContext);
        // 保存日志文件
        saveCrashInfo2File(ex);

        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "出现未知异常,即将退出.", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }
    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称,便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable ex) {
//        if (base.BaseApplication.context() == null) return null;
//        if (!PermissionCode.isGranted(base.BaseApplication.context(), PermissionCode.READ_EXTERNAL_STORAGE)) return null;

        StringBuffer sb = new StringBuffer();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        String time = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second + "\r\n";
        sb.append(time);
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        try {
            String fileName = "errorLog.txt";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = DEFAULT_PATH;
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(path + File.separator + fileName);
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileWriter fileWriter = new FileWriter(file, true);
                fileWriter.write(sb.toString());
                fileWriter.close();
            }
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
            return null;
        }
    }

}
