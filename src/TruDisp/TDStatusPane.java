
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

/** Esta clase se encarga de crear un stackpane que contendrá un centro de notificaciones,
 * la aplicación y la barra de estado.
 */
public class TDStatusPane extends StackPane{

    /** Variables */
    private HBox        statusBar;              // Contenedor de la barra de estado.
    private VBox        notificationsLayout;    // Contenedor de las notificaciones.
    private Label       statusLabel;            // Etiqueta que mostrara el texto de estado.
    private ImageView   iconView;               // Imagen que se mostrará en la notificación o el estado.
    private Integer     statusDuration,         // Duración del estado.
                        notificationDuration;   // Duración de la notificación
    private Task<?>     statusTimer;            // Timer del estado.

    /** Constantes */
    private static final Integer DEFAULT_DURATION=5000,NOTIFICATION_DURATION=10000;
    private static final String WELCOME_ICON="Icons/welcomeicon.png";

    /** Constructor */
    public TDStatusPane()
    {
        initComponents();
    }

    /** Componentes */
    private void initComponents()
    {
        // Duración del el estado.
        statusDuration = DEFAULT_DURATION;
        notificationDuration = NOTIFICATION_DURATION;

        // Estado
        statusLabel = new Label();
        statusLabel.getStyleClass().addAll("statusbartext");
        HBox.setHgrow(statusLabel, Priority.ALWAYS);

        // Icono
        iconView = new ImageView();
        iconView.setFitHeight(40);
        iconView.setPreserveRatio(true);

        // Barra de estado
        statusBar = new HBox();
        statusBar.getChildren().addAll(iconView, statusLabel);
        BorderPane.setAlignment(this, Pos.CENTER);

        // Notificaciones
        notificationsLayout = new VBox();
        VBox.setVgrow(notificationsLayout,Priority.NEVER);
        HBox.setHgrow(notificationsLayout,Priority.ALWAYS);
        StackPane.setAlignment(notificationsLayout, Pos.TOP_CENTER);

        // Para que se pueda manejar los elementos debajo de las notificaciones.
        notificationsLayout.setPickOnBounds(false); //NO QUITAR debe ser false

    }

    /** Función encargada de desplegar un estado */
    public void setStatus(String stat)
    {
        // Revisamos si se esta corriendo algun otro estado, de ser así se cancela para mostrar el nuevo
        if(statusTimer != null)
        {statusTimer.cancel();}

        /** Tarea encargada de contar el tiempo de duración del estado. */
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

        // Asginamos listeners a las propiedades de estado de la tarea.
        statusTimer.stateProperty().addListener((observable, oldValue, newValue) -> {

            // Si la tarea terminó correctamente se reinician los valores
            if (newValue == Worker.State.SUCCEEDED) {
                statusLabel.setText("");
                setIcon(WELCOME_ICON);
                statusDuration = DEFAULT_DURATION;
            }
        });

        // Creamos una nuevo hilo con la tarea (temporizador) y le iniciamos.
        new Thread((statusTimer)).start();

        // desplegamos el estado.
        statusLabel.setText(stat);
    }

    /** Función encargada de cambiar el ícono un estado */
    public void setIcon(String icon)
    {
        iconView.setImage(new Image(icon));
        statusLabel.setGraphic(iconView);
    }

    /** Función encargada de desplegar un estado pero acepta el estado y la imagen */
    public void setStatus(String stat, String im)
    {
        setIcon(im);
        setStatus(stat);
    }

    /** Función encargada de agregar el contenedor de la aplicación */
    public void setStatusPane(VBox vbox)
    {
        // Agregamos la barra de estado
        vbox.getChildren().add(statusBar);
        // Agregamos al objeto, la aplicación principal y el contenedor de notificaciones.
        this.getChildren().addAll(vbox, notificationsLayout);

    }

    /** Función encargada de desplegar una notificación */
    public void setNotification(String not,String im)
    {

        // Creamos un nuevo objeto de tarea encargada de llevar el progreso de la notificación
        Task notificationTimer = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {

                for(int i=0; i<notificationDuration;i++)
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

        // Texto de la notifiación
        Label notificationLabel = new Label(not);
        notificationLabel.getStyleClass().add("notificationlabel");
        HBox.setHgrow(notificationLabel, Priority.ALWAYS);
        notificationLabel.setMaxWidth(Double.MAX_VALUE);

        // Botón para cancelar la notificación
        Button notificationCloseButton= new Button("X");
        notificationCloseButton.setMaxHeight(Double.MAX_VALUE);
        notificationCloseButton.setOnAction(event1 -> notificationTimer.cancel());

        // Contendor de la notificación.
        HBox notificationBox = new HBox();
        HBox.setHgrow(notificationBox,Priority.ALWAYS);
        notificationBox.getChildren().addAll(notificationLabel, notificationCloseButton);

        // Si la imagen es valida
        if(im!=null) {

            // Creamos una umagen.
            ImageView notificationicon = new ImageView(new Image(im));
            // Dimensiones
            notificationicon.setFitHeight(40);
            notificationicon.setPreserveRatio(true);
            // Le agregamos al texto
            notificationLabel.setGraphic(notificationicon);
        }

        // Escuchamos los cambios en el estado de la tarea creada.
        notificationTimer.stateProperty().addListener((observable, oldValue, newValue) ->
        {
            // Si se ha creado la tarea
            if (newValue == Worker.State.SCHEDULED) {

                // Agregamos la tarea al contenedor de notficaciones
                notificationsLayout.getChildren().add(notificationBox);

                // Creamos una transición  para que aparezca suvaemente
                FadeTransition ft = new FadeTransition(Duration.millis(1000), notificationBox);
                ft.setFromValue(0.0);
                ft.setToValue(1.0);
                ft.play();
            }

            // Cuando se ha completado la tarea
            if (newValue == Worker.State.SUCCEEDED) {

                // Creamos una transición para que desaparezca suavemente
                FadeTransition ft = new FadeTransition(Duration.millis(1000), notificationBox);
                ft.setFromValue(1.0);
                ft.setToValue(0.0);
                ft.play();
                // Al terminar la transición se elimna la tarea del contenedor de notificaciones.
                ft.setOnFinished(event -> {
                    notificationsLayout.getChildren().remove(notificationBox);
                });
            }

            // Cuando se ha cancelado la tarea se elimina del contenedor de notificaciones.
            if(newValue == Worker.State.CANCELLED)
            {notificationsLayout.getChildren().remove(notificationBox);}
        });

        // Creamos un hilo con la tarea y se inicia
        new Thread(notificationTimer).start();
    }
}
