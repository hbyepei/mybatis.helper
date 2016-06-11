package yp.dev.tools.ui.dialog;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Created by yp on 2016/6/11.
 */
public class FXOptionPane {
    public enum Response {
        NO, YES, CANCEL
    }

    private static Response buttonSelected = Response.CANCEL;
    private static ImageView icon = new ImageView();

    static class Dialog extends Stage {
        public Dialog(String title, Stage owner, Scene scene, String iconFile) {
            setTitle(title);
            initStyle(StageStyle.UTILITY);
            initModality(Modality.APPLICATION_MODAL);
            initOwner(owner);
            setResizable(false);
            setScene(scene);
            try {
                icon.setImage(new Image(getClass().getResourceAsStream(iconFile)));
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        public void showDialog() {
            sizeToScene();
            centerOnScreen();
            showAndWait();
        }
    }

    static class Message extends Text {
        public Message(String msg) {
            super(msg);
            setWrappingWidth(250);
        }
    }

    public static Response showConfirmDialog(Stage owner, String message,
                                             String title) {
        VBox vb = new VBox();
        Scene scene = new Scene(vb);
        final Dialog dial = new Dialog(title, owner, scene, "/yp/dev/tools/ui/image/ok.png");
        vb.setPadding(new Insets(15, 10, 15, 10));
        vb.setSpacing(10);
        Button yesButton = new Button("确  定");
        yesButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                dial.close();
                buttonSelected = Response.YES;
            }
        });
        Button noButton = new Button("取  消");
        noButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                dial.close();
                buttonSelected = Response.NO;
            }
        });
        BorderPane bp = new BorderPane();
        HBox buttons = new HBox();
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(10);
        buttons.getChildren().addAll(yesButton, noButton);
        bp.setCenter(buttons);
        HBox msg = new HBox();
        msg.setSpacing(5);
        msg.getChildren().addAll(icon, new Message(message));
        vb.getChildren().addAll(msg, bp);
        dial.showDialog();
        return buttonSelected;
    }

    public static void showMessageDialog(Stage owner, String message, String title) {

        showMessageDialog(owner, new Message(message), title);
    }

    public static void showMessageDialog(Stage owner, Node message, String title) {
        VBox vb = new VBox();
        try {
            Scene scene = new Scene(vb);
            final Dialog dial = new Dialog(title, owner, scene, "/yp/dev/tools/ui/image/info.png");
            vb.setPadding(new Insets(15, 10, 15, 10));
            vb.setSpacing(10);
            Button okButton = new Button("确 定");
            okButton.setAlignment(Pos.CENTER);
            okButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    dial.close();
                }
            });
            BorderPane bp = new BorderPane();
            bp.setCenter(okButton);
            HBox msg = new HBox();
            msg.setSpacing(5);
            msg.getChildren().addAll(icon, message);
            vb.getChildren().addAll(msg, bp);
            dial.showDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
