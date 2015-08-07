package TruDisp;

import javafx.animation.FadeTransition;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.util.NavigableMap;

/**
 * Created by Nieto on 05/08/15.
 */
public class TruDispStatusPane extends StackPane{

    private HBox statusBar;
    private VBox notificationsLayout;
    private Label statusLabel;
    private ImageView iconView;
    private Integer statusDuration;
    private Task<?> statusTimer,notificationTimer;

    private static final Integer DEFAULT_DURATION=5000,NOTIFICATION_DURATION=10000;
    private static final String WELCOME_ICON="Icons/welcomeicon.png";

    public TruDispStatusPane()
    {
        initComponents();
    }

    private void initComponents()
    {
        statusDuration = DEFAULT_DURATION;

        // Barra de estado
        statusLabel = new Label();
        statusLabel.getStyleClass().addAll("statusbartext");
        HBox.setHgrow(statusLabel, Priority.ALWAYS);

        iconView = new ImageView();
        iconView.setFitHeight(40);
        iconView.setPreserveRatio(true);

        statusBar = new HBox();
        statusBar.getChildren().addAll(iconView, statusLabel);

        BorderPane.setAlignment(this, Pos.CENTER);

        // Notificaciones
        notificationsLayout = new VBox();
        VBox.setVgrow(notificationsLayout,Priority.NEVER);
        StackPane.setAlignment(notificationsLayout, Pos.TOP_CENTER);
        // Para que se pueda manejar los elementos debajo de las notificaciones.
        notificationsLayout.setPickOnBounds(false); //NO QUITAR debe ser false

    }

    public void setStatus(String stat)
    {
        if(statusTimer != null)
        {statusTimer.cancel();}

        statusTimer = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {

                for(int i=0; i<statusDuration;i++)
                {
                    try {
                        Thread.sleep(1);
                    }catch(InterruptedException interrupted){
                        if(isCancelled())
                        {break;}
                    }
                }
                return 0;
            }
        };

        statusTimer.stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                statusLabel.setText("");
                setIcon(WELCOME_ICON);
                statusDuration = DEFAULT_DURATION;
            }
        });

        new Thread((statusTimer)).start();

        statusLabel.setText(stat);
    }

    public void setIcon(String icon)
    {
        iconView.setImage(new Image(icon));
        statusLabel.setGraphic(iconView);
    }

    public void setStatus(String stat, String im)
    {
        setIcon(im);
        setStatus(stat);
    }

    public void setStatusPane(VBox vbox)
    {
        vbox.getChildren().add(statusBar);
        this.getChildren().addAll(vbox, notificationsLayout);

    }

    public void setNotification(String not,String im)
    {

        notificationTimer = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {

                for(int i=0; i<NOTIFICATION_DURATION;i++)
                {
                    try {
                        Thread.sleep(1);
                    }catch(InterruptedException interrupted){
                        if(isCancelled())
                        {break;}
                    }
                }

                return 0;
            }
        };

        Label notificationLabel = new Label(not);
        notificationLabel.getStyleClass().add("notificationlabel");
        notificationLabel.setMaxWidth(Double.MAX_VALUE);

        if(im!=null) {
            ImageView notificationicon = new ImageView(new Image(im));
            notificationicon.setFitHeight(40);
            notificationicon.setPreserveRatio(true);
            notificationLabel.setGraphic(notificationicon);
        }

        notificationTimer.stateProperty().addListener((observable, oldValue, newValue) ->
        {

            if (newValue == Worker.State.SCHEDULED) {

                notificationsLayout.getChildren().add(notificationLabel);
                FadeTransition ft = new FadeTransition(Duration.millis(1000), notificationLabel);
                ft.setFromValue(0.0);
                ft.setToValue(1.0);
                ft.play();
            }
            if (newValue == Worker.State.SUCCEEDED) {
                FadeTransition ft = new FadeTransition(Duration.millis(1000), notificationLabel);
                ft.setFromValue(1.0);
                ft.setToValue(0.0);
                ft.play();
                ft.setOnFinished(event -> {
                    notificationsLayout.getChildren().remove(notificationLabel);
                });
            }

        });

        new Thread(notificationTimer).start();

    }

}
