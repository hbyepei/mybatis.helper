package yp.dev.tools;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;

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
        final String[] args = new String[]{};
        final ClassLoader loader = JavaFxBootStrap.class.getClassLoader();
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.currentThread().setContextClassLoader(loader);
                    JavaFxBootStrap.main(args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
