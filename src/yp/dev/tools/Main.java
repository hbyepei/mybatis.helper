package yp.dev.tools;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;

import java.util.concurrent.CountDownLatch;

/**
 * Author:yepei@meituan.com
 * Date:2017/5/27
 * Time:12:25
 * ------------------------------------
 * Desc:
 */
public class Main extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        final ClassLoader mainLoader = Thread.currentThread().getContextClassLoader();
        final String[] args = new String[]{};
        final ClassLoader subLoader = JavaFxBootStrap.class.getClassLoader();
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.currentThread().setContextClassLoader(subLoader);
                    JavaFxBootStrap.main(args);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                    Thread.currentThread().setContextClassLoader(mainLoader);
                }
            }
        }.start();
        try {
            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
