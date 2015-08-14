package TruDisp;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Created by Nieto on 07/08/15.
 */

  public  class MyUpdater{

    private VBox udLayout;
    private Stage updateStage,splashStage;
    private Scene updateScene;
    private ProgressBar progress;
    private Label status;
    private Button Yes,No;
    private Boolean update;
    private  Task<String> checkupdates;
    private SimpleBooleanProperty isready = new SimpleBooleanProperty(false);

    public MyUpdater(ProgressBar prog, Label stat,Stage st)
    {

        updateStage = new Stage(StageStyle.TRANSPARENT);
        status = stat;
        progress = prog;
        splashStage = st;
        udLayout = new VBox();

        Yes = new Button("yes");
        Yes.setOnAction(event1 -> downloadupdate());
        Yes.setPrefWidth(50);
        No = new Button("No");
        No.setOnAction(event -> {
            updateStage.close();
            splashStage.hide();
            isready.set(true);
        });
        No.setPrefWidth(50);
        udLayout.getChildren().addAll(Yes,No);
        updateScene = new Scene(udLayout);
        updateStage.setScene(updateScene);

    }


    private void downloadupdate()
    {
        // El usuario aceptó la actualización
        final File file = new DirectoryChooser().showDialog(updateStage);
        final  File filetowrite = new File(file.getAbsolutePath()+"/"+"TruDisp.jar");
        updateStage.close();

        Task download= new Task()
        {
            @Override
            protected Object call() throws Exception
            {
                updateMessage("Iniciando Descarga");
                URL source = new URL(checkupdates.get());
                if(source!=null)
                {
                    Files.copy(source.openStream(), filetowrite.toPath(),StandardCopyOption.REPLACE_EXISTING);
                    updateMessage("Se ha descargado la actualización");
                }
                else{cancel();}

                return null;
            }
        };

        download.setOnSucceeded(event -> {
            Task timer = new Task()
            {
                @Override
                protected Object call() throws Exception
                {
                    updateMessage("Descarga Exitosa");
                    Thread.sleep(1000);
                    return null;
                }
            };
            status.textProperty().bind(timer.messageProperty());

            timer.setOnSucceeded(event1 -> System.exit(0));

            new Thread(timer).start();
        });

        download.setOnFailed(event -> {
            Task timer = new Task()
            {
                @Override
                protected Object call() throws Exception
                {
                    updateMessage("Descarga Fallida");
                    Thread.sleep(1000);
                    return null;
                }
            };
            status.textProperty().bind(timer.messageProperty());

            timer.setOnSucceeded(event1 -> {
                splashStage.close();
                isready.set(true);
            });

            new Thread(timer).start();
        });

        status.textProperty().bind(download.messageProperty());

        new Thread(download).start();

    }

    private void checkforupdates()
    {
        checkupdates= new Task<String>(){
            @Override
            protected String call() throws Exception {
                updateMessage("Comprobando si hay actualizaciones");
                Thread.sleep(500);
                URL updatefile = new URL(TruDisp.UPDATE_TXT);
                if(updatefile != null)
                {
                    BufferedReader in = new BufferedReader(new InputStreamReader(updatefile.openStream()));
                    String str;
                    while ((str = in.readLine())!=null)
                    {
                        String[] buf = str.split("; ");
                        if((Double.parseDouble(buf[1])>TruDisp.TRU_DISP_VERSION)&&(buf[0].equalsIgnoreCase(TruDisp.REPOSITORY)))
                        {
                            updateMessage(" Hay una nueva actualización\n V_: " + buf[1]+" ¿Desea Descargarla?");
                            update = true;
                            return buf[2];
                        }
                        else{update=false;}
                    }
                }
                else
                {
                    updateMessage("No se ha podido contactar con el servidor");
                    Thread.sleep(500);
                    update = false;
                    cancel();
                }
                return null;
            }
        };

        checkupdates.setOnSucceeded(event -> {if(update){ updateStage.show();}else{splashStage.close();isready.set(true);}});

        checkupdates.setOnFailed(event -> {
            splashStage.close();
            isready.set(true);
        });

        status.textProperty().bind(checkupdates.messageProperty());

        new Thread(checkupdates).start();

    }
    public void updateApp()
    {
        Task wait =new Task<Boolean>()
        {
            @Override
            protected Boolean call() throws Exception {

                updateMessage("Inicializando");
                Thread.sleep(1000);
                return null;
            }
        };

        wait.setOnSucceeded(event -> checkforupdates());

        status.textProperty().bind(wait.messageProperty());

        new Thread(wait).start();
    }

    public Boolean isReady() {return isready.get();}

    public SimpleBooleanProperty getIsreadyProperty()
    {
        return isready;
    }

}
