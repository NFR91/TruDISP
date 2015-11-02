
/**
 * TruDISP v: X.X
 * RICARDO NIETO FUENTES
 * 2015
 *
 * Este programa realiza el cálculo del desplazamiento neto de una falla empleando
 * dos métodos
 *
 * 1: El publicado en :
 *
 * “Software for determining the true displacement of faults”,
 * R. Nieto-Fuentes, Á.F. Nieto-Samaniego,S.-S. Xu, S.A. Alaniz-Álvarez,
 * Computers & Geosciences, Volume 64, March 2014, Pages 35-40, ISSN 0098-3004,
 * doi: 10.1016/j.cageo.2013.11.010
 *
 * 2: Calculando a partir de las mediciones de campo y conociendo la orientación de la estría
 * los cosenos directores de los diferentes planos y empleando :
 *
 * "Fault-slip calculation from separations."
 * Yamada, E., Sakaguchi, K., 1995.  J. Struct. Geol. 17, 1065–1070.
 *
 * El programa principalmente se centra en facilitar el cálculo mediante revisión de errores
 * en los datos de entrada, selección de ecuaciones del árbol de decisiones del método 1 y
 * almacenamiento de los datos.
 */


/**
 *  The MIT License (MIT)
 *
 * Copyright (c) [2015] [RICARDO NIETO FUENTES]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package TruDisp;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/** Esta clase se encarga de manejar el evento de actualización de la aplicación. */
  public  class MyUpdater{

    /** Variables */
    private Stage                   splashStage;                // Ventana del splash
    private VBox                    splashLayout;               // contenedor de objetos del splash
    private HBox                    updateLayout;               // Contenedor de objetos del update.
    private ProgressBar             progressBar;                // Barra de progreso
    private Label                   status;                     // Estado
    private Button                  Yes,No;                     // Controles.
    private Boolean                 isupdating;                 // Bandera
    private Task<String>            checkupdates;               // Tarea encargada de revisar si existen actualizaciones.
    private SimpleBooleanProperty   isready = new SimpleBooleanProperty(false); // Bandera para ver si se ha finalizado la actualización.

    /** Constantes */

    private static final Long       LONG_WAIT_IN_MILLIS = 1000l;            // Tiempo largo de espera para actividades en millisengundos
    private static final Long       SHORT_WAIT_MILLIS = 500l;               // Tiempo corto de espera para actividades en millisengundos
    private static final String     UPDATE_TXT = TruDisp.UPDATE_TXT;        // Liga al documento de texto que contiene la información de actualizacón
    private static final Double     VERSION = TruDisp.TRU_DISP_VERSION;     // Versión del programa.
    private static final String     REPOSITORY = TruDisp.REPOSITORY;        // Repositorio del programa.
    private static final String     FILE_NAME = "TruDISP.jar";              // Nombre del archivo

    /** Constructor */
    public MyUpdater(Label stat,Stage st,VBox sl,ProgressBar bar)
    {
        status = stat;
        progressBar=bar;
        splashLayout = sl;
        splashStage = st;

        Yes = new Button("yes");
        Yes.setOnAction(event1 -> downloadupdate());
        Yes.setMaxWidth(Double.MAX_VALUE);
        No = new Button("No");
        No.setOnAction(event -> {
            splashStage.hide();
            isready.set(true);
        });
        No.setMaxWidth(Double.MAX_VALUE);

        updateLayout = new HBox(Yes,No);
        HBox.setHgrow(Yes, Priority.ALWAYS);
        HBox.setHgrow(No, Priority.ALWAYS);

    }


    /** Función encargada de mostrar que se va a actualizar */
    public void updateApp()
    {
        // Temporizador para mostrar el mensaje de inicio de la aplicación.
        Task wait =new Task<Boolean>()
        {
            @Override
            protected Boolean call() throws Exception {

                updateMessage(" . . . L O A D I N G . . . ");
                Thread.sleep(LONG_WAIT_IN_MILLIS);
                return null;
            }
        };

        // Hacemos que la tarea pueda manejar el texto de la barra de estado del splash
        status.textProperty().bind(wait.messageProperty());

        // Animación barra de estado.
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);

        // Cuando termine la espera llamamos a la función para revisar si existen actualizaciones.
        wait.setOnSucceeded(event -> checkforupdates());

        // Creamos e iniciamos un hilo.
        new Thread(wait).start();
    }

    /** Función encargada de revisar si existen actualizaciones. */
    private void checkforupdates()
    {
        /**
         * Esta Función descarga un archivo txt que contiene la información y enlace de descarga de las
         * distintas versiones de la aplicación, compara la versión del programa y si encuentra una versión
         * mas reciente en el archivo manda llamar a la función de descarga.
         * */

        // Tarea encargada de revisar si hay actualización.
        checkupdates= new Task<String>(){
            @Override
            protected String call() throws Exception {

                // Avisamos al usuario que se van a revisar las actualziaciones.
                updateMessage("CHECKING FOR UPDATES");
                Thread.sleep(500);

                // Creamos una dirección para el archivo de texto.
                URL updatefile = new URL(UPDATE_TXT);

                // Si existe el archivo de texto.
                if(updatefile != null)
                {
                    // Creamos un lector.
                    BufferedReader in = new BufferedReader(new InputStreamReader(updatefile.openStream()));

                    // Buffer.
                    String str;
                    // Para cada linea.
                    while ((str = in.readLine())!=null)
                    {
                        // Partimos cada linea "repositorio; version; URL" notese que el separador es "; "
                        String[] buf = str.split("; ");

                        // Si la versión es mayor y el repositorio corresponde entonces preguntamos si se desea descargar.
                        if((Double.parseDouble(buf[1])>VERSION)&&(buf[0].equalsIgnoreCase(REPOSITORY)))
                        {
                            updateMessage(" A NEW VERSION OF TRUDISP IS AVAILABLE\n V_: " + buf[1]+"\n DOWNLOAD?");
                            isupdating = true;
                            return buf[2];
                        }
                        else{
                            isupdating =false;}
                    }
                }
                else // No se ha podido establecer conexión con el archivo de texto.
                {
                    updateMessage("CONNECTION ERROR");
                    Thread.sleep(SHORT_WAIT_MILLIS);
                    isupdating = false;
                    cancel();
                }
                return null;
            }
        };

        // Permitimos que la tarea maneje los mensajes de la barra de estado.
        status.textProperty().bind(checkupdates.messageProperty());

        // Si se ha terminado la tarea
        checkupdates.setOnSucceeded(event ->
        {
            if (isupdating) {
                // Agregamos los botones de control.
                splashLayout.getChildren().add(updateLayout);
                splashStage.sizeToScene();
                progressBar.setProgress(0);
            } else {
                // Cerramos el splash y avisamos que se ha terminado.
                splashStage.close();
                isready.set(true);
            }
        });

        // Si ha fallado latarea.
        checkupdates.setOnFailed(event -> {
            splashStage.close();
            isready.set(true);
        });

        // Creamos e iniciamos el hilo.
        new Thread(checkupdates).start();
    }

    /** Función encargada de descargar la actualización */
    private void downloadupdate()
    {
        // El usuario aceptó la actualización entonces se le pide seleccione un directirio para guardar
        final File file = new DirectoryChooser().showDialog(splashStage);

        // Creamos el archivo para escribir.
        final  File filetowrite = new File(file.getAbsolutePath()+"/"+ FILE_NAME);

        // Eliminamos los botones de control.
        splashLayout.getChildren().remove(updateLayout);

        // Animamos la barra de progreso.
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);

        // Tarea encargada de realizar la descarga.
        Task download= new Task()
        {
            @Override
            protected Object call() throws Exception
            {
                updateMessage(". . . D O W N L O A D I N G . . ");

                // Creamos el objeto que contendrá el enlace.
                URL source = new URL(checkupdates.get());
                if(source!=null)
                {
                    // Copiamos el arhcivo del enlace.
                    Files.copy(source.openStream(), filetowrite.toPath(),StandardCopyOption.REPLACE_EXISTING);
                    updateMessage(" READY ");
                    Thread.sleep(SHORT_WAIT_MILLIS);
                }
                else{cancel();}
                return null;
            }
        };

        // Permitimos que la tarea maneje el texto del splash
        status.textProperty().bind(download.messageProperty());

        // Si se ha terminado la descarga.
        download.setOnSucceeded(event -> {

            // Tarea encargada de mostrar que se a terminado la descarga.
            Task timer = new Task() {
                @Override
                protected Object call() throws Exception {
                    updateMessage("PLEASE START THE NEW VERSION");
                    Thread.sleep(SHORT_WAIT_MILLIS);
                    return null;
                }
            };

            // Permitimos que la tarea maneje el texto del splash
            status.textProperty().bind(timer.messageProperty());

            // cuando termine la tarea cerramos la aplicación.
            timer.setOnSucceeded(event1 -> System.exit(0));

            // Mostramos animación de la barra de progreso.
            showFinishProgress(timer);
        });

        // Si ha fallado la descarga.
        download.setOnFailed(event -> {

            // Tarea encargada de mostrar que ha fallado la descarga.
            Task timer = new Task() {
                @Override
                protected Object call() throws Exception {
                    updateMessage("Descarga Fallida");
                    Thread.sleep(SHORT_WAIT_MILLIS);
                    return null;
                }
            };

            // Permitimos que la tarea maneje el texto del splash
            status.textProperty().bind(timer.messageProperty());

            timer.setOnSucceeded(event1 -> {
                splashStage.close();
                isready.set(true);
            });
            // Mostramos animación de la barra de progreso.
            showFinishProgress(timer);
        });

        // Creamos el hilo y lo iniciamos.
        new Thread(download).start();

    }

    /** Función encargada de Mostrar la finalización de la actividad. */
    private void showFinishProgress(Task onFinishTask)
    {
        // Timer que se encarga de mostrar un progreso de 0 a 100 en 1 segundo.
        Task progress = new Task() {
            @Override
            protected Object call() throws Exception {

                for(int i=0; i<=100; i++)
                {
                    updateProgress(i,100);
                    Thread.sleep(10);
                }

                return null;
            }
        };

        // Mostramos que terminó la descarga en la barra de progreso
        progressBar.progressProperty().bind(progress.progressProperty());

        // Cuando termina ese progreso, llamamos a la función que se desea llamar después.
        progress.setOnSucceeded(event2 -> new Thread(onFinishTask).start());

        new Thread(progress).start();
    }

    /**Función encargada de regresar el valor de la actualización.*/
    public SimpleBooleanProperty getIsreadyProperty()
    {
        return isready;
    }

}
