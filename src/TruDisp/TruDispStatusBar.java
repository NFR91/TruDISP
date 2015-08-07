package TruDisp;

import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Created by Nieto on 03/08/15.
 */
class TruDispStatusBar extends GridPane
{

    private Label status;
    private ProgressBar progress;
    private Image icon;
    private ImageView iconView;
    private Integer duration;
    private Task<?> statustimer;
    private static final String WELCOMEICON="Icons/welcomeicon.png";
    private static final Integer DEFAULT_DURATION=5000;

    public TruDispStatusBar(String im, String stat) {
        initComponents();
        setStatus(stat, im);

    }

    private void initComponents()
    {
        duration =DEFAULT_DURATION;

        status = new Label();
        status.getStyleClass().add("statusbartext");
        GridPane.setConstraints(status, 0, 0, 1, 1, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);



        this.getChildren().add(status);
        this.getStyleClass().add("statusbargrid");




    }


    public void setStatus(String stat)
    {

        if(statustimer != null)
            statustimer.cancel();

        statustimer = new Task<String>()
        {

            @Override
            protected String call() throws Exception {

                for (int i=0;i<duration;i++)
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException interrpueted) {
                        if (isCancelled())
                            break;
                    }
                return "";
            }
        };

        statustimer.stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                status.setText("");
                this.setIcon(WELCOMEICON);
                duration = DEFAULT_DURATION;
            }
        });

        new Thread(statustimer).start();


        status.setText(stat);


    }

    public void setIcon(String im)
    {
        icon = new Image(im);
        iconView = new ImageView();
        iconView.setImage(icon);
        iconView.setFitHeight(40);
        iconView.setPreserveRatio(true);
        status.setGraphic(iconView);


    }
    public void setStatus(String stat,String im) {

        this.setIcon(im);
        this.setStatus(stat);

    }

    public void setDuration(Integer drtn)
    {
        duration = drtn;
    }


    public String getStatusText()
    {
        return status.getText();
    }

}