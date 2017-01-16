package yp.dev.tools;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import yp.dev.tools.controller.MainController;
import yp.dev.tools.ui.dialog.FXOptionPane;

/**
 * Created by yp on 2016/6/9.
 */
public class Main extends Application {
    private Stage mainStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/config.fxml"));
            Parent root = loader.load();
            this.mainStage = primaryStage;
            MainController controller = loader.getController();
            controller.setMain(this);
            primaryStage.setTitle("Mybatis辅助工具(作者:叶佩)");
            Image image = null;
            try {
                image = new Image("/image/icon.png");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (image != null) {
                primaryStage.getIcons().add(image);
            }
            primaryStage.setScene(new Scene(root, 800, 600));
            primaryStage.show();
        } catch (Exception e) {
            FXOptionPane.showMessageDialog(primaryStage, e.getMessage(), "错误");
        }
    }

    public Stage getMainStage() {
        return mainStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
