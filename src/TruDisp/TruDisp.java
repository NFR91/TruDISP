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

package TruDisp;

// Importamos las librerias necesarias.
import TruDisp.FaultViewer.FaultViewer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;


// Clase aplicación la cual corre el programa.
public class TruDisp extends Application {

    /******************************************************************************************************************/
    /** Variables de programa */
    /******************************************************************************************************************/

    /** Constantes finales.*/

    // Programa.
    public static final String UPDATE_TXT = "http://nfr91.github.io/TruDISP/TruDISP/TruDispVersions.txt"; //Dirección de la lista de actualizaciones.
    public static final Double TRU_DISP_VERSION = 1.94;     // Versión del programa.
    public static final String REPOSITORY ="beta";          // Repositorio al que pertenece.

    // Methods
    public static final Integer METHOD_1 = 1, METHOD_2 = 2; // Constantes de métodos.
    public static final Integer N=0,E=1,D=2;                // Constantes de posición para los ejes.

    // Icons;
    public static final String WELCOME_ICON = "Icons/welcomeicon.png";  // Icono de bienvenida.
    public static final String ADVICE_ICON = "Icons/adviceicon.png";    // Icono de tip
    public static final String WARNING_ICON = WELCOME_ICON;             // Icono de advertencia.
    public static final String ERROR_ICON = WELCOME_ICON;               // Icono de error.
    public static final String SPLASH_IMAGE = "Images/splashimage.png";

    // Estilos
    public static final String TRUDISP_STYLE_SHEET = "TruDisp/TruDispStyleSheet.css";
    private static final String IO_ERROR_LABEL = "ioerrorlabel";
    private static final String IO_TEXT_FIELD = "iotextfield";
    private static final String IO_COMBO_BOX = "iocombobox";
    private static final String TITLE_LABEL = "titlelabel";
    private static final String SPLASH_PROGRESS_TEXT = "splashprogresstext";
    private static final String SPLASH_PROGRESS_BAR = "splashprogressbar";
    private static final String SUB_TITLE_LABEL = "subtitlelabel";
    private static final String IO_HBOX = "iohbox";
    private static final String IO_VBOX = "iovbox";
    private static final String IO_LABEL = "iolabel";
    private static final String IO_GRID_PANE = "iogridpane";

    /** Variables principales*/

    private Stage           mainTruDispStage;       // Escenario de la aplicación
    private Scene           mainScene;              // Escena principal.
    private BorderPane      mainBorderLayout;       // BorderPane que organiza el contenido de la aplicación.
    private VBox            mainVBox;               // VBox principal que organiza el contenido.
    private HBox            methodsHBox;            // HBox que contiene los dos métodos empleado.

    private MenuBar         menuBar;                // Barra de menú del programa.
    private Menu            trudispMenu;            // Menu principal del programa.
    private MenuItem        trudispMenuAboutItem,   // Muestra la información del programa.
                            trudispMenuCloseItem,   // Cierra el programa
                            trudispMenuSaveItem,    // Salvar los datos capturados
                            trudispMenuOpenItem;    // Abrir datos capturados en una sesión anterior.
    private Menu            methodsMenu;            // Menú para mostrar o ocultar los métodos.
    private CheckMenuItem   method1CheckMenuItem;   // Muestra y oculta el método 1.
    private Menu            panelsMenu;             // Menú que contiene las herramientas.
    private MenuItem        panelMenuHistoryItem,   // Muestra el historial de los calculos realizados.
                            panelMenuFaultViewrItem,// Muestra el Graficador de falla.
                            panelCosDirItem;   // Muestra los cosenos directores.
    private Menu            helpMenu;               // Menú que contiene los dialogos de ayuda del programa.
    private MenuItem        helpMenuOutputItem,     // Muestra las variables de salida.
                            helpMenuInputItem,      // Muestra las variables de entrada.
                            helpMenuLabelsItems,    // Muestra el significado de las variables.
                            helpMenuHow2Use,        // Muestra como utilizar TruDISP.
                            helpMenuLMNCoordItem;   // Muestra la orientacion de las coordenadas de los cosenos directores.

    private TDTable         tableTD;                // Objeto encargado de manejar los datos procesados.
    private FaultViewer     faultViewerTD;          // Objeto encargado de graficar los planos.
    private TDOpenSave      openSaveTD;             // Objeto encargado de abrir y guardar los datos procesados.
    private TDDialogs       dialogsTD;              // Objeto encargado de manejar los dialogos.
    private TDStatusPane    statusPane;             // Objeto encargado de controlar las notificaciones.
    private Stage           dirCosWindow;           // TODO Mover este dialogo a dialogsTD.

    private Boolean         shakeStageFlag = true;  // Bandera para realizar el sacudido de la venta.


    /** Método 1*/
    private GridPane        method1GridLayout;                              // Malla que contiene los campos de entrada del método 1.
    private Label           method1TitleLabel;                              // Label que tiene el nombre del método.
    private Label           betaLabel, phiLabel, gammaLabel;                // Etiquetas de pitch.
    private Label           betaErrorLabel, phiErrorLabel, gammaErrorLabel; // Campos de error de pitch.
    private TextField       betaTextField, phiTextField, gammaTextField;    // Campos de entrada para los pitchs.
    private ComboBox        betaComboBox, gammaComboBox, phiComboBox;       // Campos de entrada para la orientación de los pitchs.
    private ToggleButton    mapSectionToggleButton,                         // Botón para cambiar al caso map and section.
                            arbitraryLineToggleButton;                      // Botón para cambiar al caso arbitrario.

    /** Método 2*/
    private GridPane        method2GridLayout;                              // Malla que contiene los campos de entrada del método 2.
    private Label           method2TitleLabel;                              // Label que tiene l enombre del método 2.
    private Label           foLabel, opoLabel, smoLabel, osLabel;           // Nombres de los planos.

    private Label           fodLabel,       fostkLabel,                     // Etiquetas de los datos de entrada.
                            opodLabel,      opostkLabel,
                            smo1dLabel,     smo1stkLabel,
                            smo2dLabel,     smo2stkLabel,
                            ostrendLabel,   osplungeLabel;

    private Label           fodErrorLabel,      fostkErrorLabel,            // Errores de los campos.
                            opodErrorLabel,     opostkErrorLabel,
                            smo1dErrorLabel,    smo1stkErrorLabel,
                            smo2dErrorLabel,    smo2stkErrorLabel,
                            ostrendErrorLabel,  osplungeErrorLabel;

    private TextField       fodTextField,       fostkTextField,             // Campos de entrada de los datos de campo.
                            opodTextField,      opostkTextField,
                            smo1dTextField,     smo1stkTextField,
                            smo2dTextField,     smo2stkTextField,
                            ostrendTextField,   osplungeTextField;

    /** Cosenos Directores*/

    private GridPane        cosDirGridLayout;                               // Malla que contiene los datos de los cosenos directores.
    private Label           cosDirTitleLabel;                               // titulo de los cosenos directores.
    private Label           fpLabel, opLabel, apLabel, bpLabel;             // titulo de cada plano de cosenos directores.

    private Label           fpmLabel, fpnLabel, fplLabel,                   // Etiquetas de los cosenos.
                            opmLabel, opnLabel, oplLabel,
                            apmLabel, apnLabel, aplLabel,
                            bpmLabel, bpnLabel, bplLabel;

    private Label           fpmErrorLabel, fpnErrorLabel, fplErrorLabel,    // Errores de los cosenos.
                            opmErrorLabel, opnErrorLabel, oplErrorLabel,
                            apmErrorLabel, apnErrorLabel, aplErrorLabel,
                            bpmErrorLabel, bpnErrorLabel, bplErrorLabel;

    private TextField       fpmTextField, fpnTextField, fplTextField,       // Campos de despliegue para los cosenos directores.
                            opmTextField, opnTextField, oplTextField,
                            apmTextField, apnTextField, aplTextField,
                            bpmTextField, bpnTextField, bplTextField;


    /** Distancias */

    private GridPane        displacementGridLayout;         // Malla que contiene los campos de entrada de desplazamiento.

    private Label           alphaLabel, smALabel,smBLabel,  // Etiquetas de distancias.
                            dABLabel ,smhLabel, smdLabel;

    private Label           alphaErrorLabel, smAErrorLabel, // Errores de las distancias.
                            smBErrorLabel,dABErrorLabel,
                            smhErrorLabel, smdErrorLabel;

    private TextField       alphaTextField, smATextField,   // Campos de entrada de las distancias.
                            smBTextField,dABTextField,
                            smhTextField, smdTextField;

    /** Resultados */

    private GridPane        resultGridLayout;               // Malla que contiene los campos de resultado.
    private Label           resultTitleLabel;               // Titulo de resultados.

    private Label           sLabel, svLabel, sdLabel,       // Etiquetas de resultados.
                            shLabel, ssLabel, thetaLabel,
                            thetanullLabel;

    private Label           sErrorLabel, svErrorLabel,      // Errores de resultados.
                            sdErrorLabel, shErrorLabel,
                            ssErrorLabel;

    private TextField       sTextField, svTextField,        // Campos de despliegue de los resultados.
                            sdTextField, shTextField,
                            ssTextField, thetaTextField,
                            thetanullTextField;

    private Button          calculateButton,resetButton;    // Botones de control para calcular y poner en ceros el programa.


    /******************************************************************************************************************/
    /** Métodos principales de la aplicación. */
    /******************************************************************************************************************/

    @Override
    public void start(Stage mainStage) throws InterruptedException {

        /**Splash Screen*/
        // Hacemos transparente la ventana principal.
        mainStage = new Stage(StageStyle.TRANSPARENT);

        // Creamos el contenedor de la imagen de splash;
        ImageView splashImageView = new ImageView(new Image(SPLASH_IMAGE));
        splashImageView.setFitWidth(500);
        splashImageView.setPreserveRatio(true);

        // Creamos el contenedor del texto ;
        Label splashProgressText = new Label("Iniciando TruDISP");
        splashProgressText.setId(SPLASH_PROGRESS_TEXT);
        splashProgressText.setMaxWidth(Double.MAX_VALUE);

        // Creamos el contenedor de la barra de progreso;
        ProgressBar splashProgressBar = new ProgressBar(0);
        splashProgressBar.setId(SPLASH_PROGRESS_BAR);
        splashProgressBar.setMaxWidth(Double.MAX_VALUE);


        // Creamos el layout del splash;
        VBox splashVBoxLayout = new VBox();
        splashVBoxLayout.getChildren().addAll(splashImageView, splashProgressText, splashProgressBar);

        // Creamos la escena del splash
        Scene splashScene = new Scene(splashVBoxLayout);
        splashScene.getStylesheets().add(TRUDISP_STYLE_SHEET);
        splashScene.setFill(Color.WHITE);
        splashVBoxLayout.setBackground(Background.EMPTY);

        // agregamos la escena
        mainStage.setScene(splashScene);
        // Mostramos la ventana de splash.
        mainStage.show();

        // Mandamos llamar la rutina de actualización.
        update(splashProgressBar,splashProgressText,mainStage);
    }

    public static void main(String[] args) {
        launch(args);
    }

    /** Rutina encargada de realizar la actualización.*/
    private void update(ProgressBar splashProgressBar, Label splashProgressText,Stage stage) {

        // Creamos un objeto de actualización.
        MyUpdater up = new MyUpdater(splashProgressBar,splashProgressText,stage);

        // Si el actualizador ha terminado entonces mostramos la aplicación.
        up.getIsreadyProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                showMainStage();
            }
        });

        // Comenzamos a actualizar.
        up.updateApp();

    }

    /** Función que muestra la aplicación.*/
    private void showMainStage() {

        // Inicializamos los componentes.
        this.initcomponents();

        // Titulo
        mainTruDispStage.setTitle("TruDISP" + TRU_DISP_VERSION);

        // Agregamos el contenido
        mainTruDispStage.setScene(mainScene);
        mainTruDispStage.setMinHeight(700);
        mainTruDispStage.setHeight(700);
        mainTruDispStage.setMinWidth(550);
        mainTruDispStage.setWidth(550);

        // Definimos la posición.
        mainTruDispStage.setX((Screen.getPrimary().getVisualBounds().getWidth() / 2) - (550));
        mainTruDispStage.setY((Screen.getPrimary().getVisualBounds().getHeight() / 2) - (mainTruDispStage.getHeight() / 2));

        // Mostramos la aplicación.
        mainTruDispStage.show();

    }

    /** Función que inicializa los componentes. */
    private void initcomponents() {

        // Creamos la venta.
        mainTruDispStage = new Stage(StageStyle.DECORATED);
        // Definimos que hace la ventana cuando se cierra.
        mainTruDispStage.setOnCloseRequest(event1 -> {System.exit(0);});

        /**Dialogos*/
        // Historial
        tableTD = new TDTable();
        // Guardado y apertura
        openSaveTD = new TDOpenSave(mainTruDispStage);
        // Dialogos de entradas, salidas e información
        dialogsTD = new TDDialogs();
        // Graficador;
        faultViewerTD = new FaultViewer();

        /** Cuerpo de la aplicación*/
        // Cuerpo Principal
        mainBorderLayout = new BorderPane();

        // Contenedor de metodos, distancias y resultados, asi como de la barra de estado
        mainVBox = new VBox();
        mainVBox.getStyleClass().add(IO_VBOX); // Definimos el estilo.

        // Cotenedor expandible de métodos
        methodsHBox = new HBox();
        methodsHBox.getStyleClass().add(IO_HBOX); // Definimos el estilo.

        // Notificaciones.
        statusPane = new TDStatusPane();
        statusPane.setStatus(".... Bienvenido a TruDisp", WELCOME_ICON);

        /** Método 1 */

        initMethod1ComponentsGrid();

        /** Método 2 */

        initMethod2ComponentsGrid();

        /**Método 3*/

        initDirCosComponentsGrid();

        /**Displacement*/

        initDisplacementComponentsGrid();

        /**Resultados*/

        initResultComponentsGrid();

        /**Ponemos los valores por defecto*/

        clearcomponents();

        /** Definimos las características de crecimiento de los contenedores. */

        // Entradas
        HBox.setHgrow(methodsHBox, Priority.ALWAYS);
        HBox.setHgrow(method1GridLayout, Priority.SOMETIMES);
        HBox.setHgrow(method2GridLayout, Priority.ALWAYS);
        VBox.setVgrow(displacementGridLayout, Priority.ALWAYS);
        VBox.setVgrow(methodsHBox, Priority.ALWAYS);

        // Resultados
        VBox.setVgrow(resultGridLayout, Priority.ALWAYS);

        /** Establemcemos los componentes en sus contenedres*/

        // Agregamos los métodos
        methodsHBox.getChildren().addAll(method2GridLayout);

        // Agregamos los métodos y los desplazamientos
        mainVBox.getChildren().addAll(methodsHBox, displacementGridLayout, resultGridLayout);

        // Agregamos el contenedor principal al centro de notificaciones.
        statusPane.setStatusPane(mainVBox);

        // Agregamos la sección central de la aplicación
        mainBorderLayout.setCenter(statusPane);


        // Agregamos la escena
        mainScene = new Scene(mainBorderLayout);
        mainScene.getStylesheets().add(TRUDISP_STYLE_SHEET); //Hoja css del programa.

        /**Barra de menú*/
        initMenuBar();

        /**Colocamos las acciones de los diferentes botones de la interface*/
        initGUIActions();
    }

    /** Función que inicializa la barra de menu. */
    private  void initMenuBar() {

        /** Barra de menu*/

        menuBar = new MenuBar();

        /** Menu principal*/

        trudispMenu = new Menu("TruDisp");

        trudispMenuCloseItem = new MenuItem("Exit");
        // Agregamos la acción se cerrará la aplicación.
        trudispMenuCloseItem.setOnAction(event -> System.exit(0));

        trudispMenuSaveItem = new MenuItem("Save Session");
        // Se agrega la acción de guardar los datos.
        trudispMenuSaveItem.setOnAction(event -> { openSaveTD.Save(tableTD.getDataList());} );

        trudispMenuOpenItem = new MenuItem("Open Session");
        // Se agerga la acción de abrir los datos.
        trudispMenuOpenItem.setOnAction(even -> tableTD.setObservableList(openSaveTD.Open(statusPane)));

        trudispMenuAboutItem = new MenuItem("About");
        // Se agrega la acción de desplegar el dialogo about
        trudispMenuAboutItem.setOnAction(event -> dialogsTD.showAbout());

        // Se agergan los items al menu
        trudispMenu.getItems().addAll(trudispMenuAboutItem, trudispMenuOpenItem, trudispMenuSaveItem, trudispMenuCloseItem);

        /**Menu Métodos*/

        methodsMenu = new Menu("Methods");

        method1CheckMenuItem = new CheckMenuItem("Method 1");
        // Se agrega la función que redimensiona la aplicación y agrega o elimina el método 1.
        method1CheckMenuItem.selectedProperty().addListener(new MethodsDisplayedChangeListener(1, method1GridLayout, methodsHBox, mainTruDispStage));

        // Agregamos el item al menu.
        methodsMenu.getItems().addAll(method1CheckMenuItem);


        /**Menu de paneles*/

        panelsMenu = new Menu("Panels");

        panelMenuHistoryItem = new MenuItem("History");
        // Se agrega la función que muestra el historial
        panelMenuHistoryItem.setOnAction(event -> tableTD.show());

        panelMenuFaultViewrItem = new MenuItem("Fault Viewer");
        // Se agrega la función que muestra el graficador.
        panelMenuFaultViewrItem.setOnAction(event -> {
            faultViewerTD.show();
            faultViewerTD.requestFocus();
        });

        panelCosDirItem = new MenuItem("Method 3");
        // Se agrega la función que muestra el dialogo de cosenos directores.
        panelCosDirItem.setOnAction(event1 -> {
            dirCosWindow.show();
            dirCosWindow.requestFocus();
        });

        // Agregamos los items al menu,.
        panelsMenu.getItems().addAll(panelMenuHistoryItem,panelMenuFaultViewrItem, panelCosDirItem);

        /**Menu ayuda*/

        helpMenu = new Menu("Help");

        helpMenuHow2Use = new MenuItem("How2Use");
        // Agregamos el despliegue de ayuda.
        helpMenuHow2Use.setOnAction(event-> dialogsTD.showHelp());

        helpMenuLMNCoordItem = new MenuItem("Director Cosines");
        // Agregamos el despigeuge del dialogo del sistema de coordenadas de los cosenos directores.
        helpMenuLMNCoordItem.setOnAction(event -> dialogsTD.showLMNCoord());

        helpMenuInputItem = new MenuItem("Input");
        // Agregamos el despliegue del dialogo de los datos de entrada.
        helpMenuInputItem.setOnAction(event -> dialogsTD.showInput());

        helpMenuOutputItem = new MenuItem("Output");
        // Agregamos el despliegue del dialogo de los datos de salida.
        helpMenuOutputItem.setOnAction(event -> dialogsTD.showOutput());

        helpMenuLabelsItems = new MenuItem("Labels");
        // Agregamos el despliegue del dialogo del significado de las etiquetas
        helpMenuLabelsItems.setOnAction(event -> dialogsTD.showLables());

        // Agregamos los items al menu.
        helpMenu.getItems().addAll(helpMenuHow2Use,helpMenuLabelsItems, helpMenuInputItem, helpMenuOutputItem, helpMenuLMNCoordItem);

        // Añadimos los menus a la barra de menu.
        menuBar.getMenus().addAll(trudispMenu, methodsMenu, panelsMenu,helpMenu);

        /**Agregamos la barra de menu al contendor principal*/
        mainBorderLayout.setTop(menuBar);

    }

    /** Función que inicializa los componentes del método 1 */
    private void initMethod1ComponentsGrid() {

        /** Etiquetas del método uno*/

        /**Menu de paneles*/
        method1TitleLabel = new Label("Method 1");
        method1TitleLabel.getStyleClass().add(TITLE_LABEL);
        method1TitleLabel.setMaxWidth(Double.MAX_VALUE);

        // Beta;
        betaLabel = new Label("β");
        betaLabel.getStyleClass().add(IO_LABEL);
        betaLabel.setTooltip(new Tooltip("Pitch of the cut-off marker"));


        betaErrorLabel = new Label();
        betaErrorLabel.getStyleClass().add(IO_ERROR_LABEL);
        betaErrorLabel.setOnScroll(new ValueChangeScrollListener(betaErrorLabel));
        betaErrorLabel.setTooltip(new Tooltip("Error in measurement of β"));

        // Gamma
        gammaLabel = new Label("γ");
        gammaLabel.getStyleClass().add(IO_LABEL);
        gammaLabel.setTooltip(new Tooltip("Pitch of the slip lineation (slickenline)"));


        gammaErrorLabel = new Label();
        gammaErrorLabel.setOnScroll(new ValueChangeScrollListener(gammaErrorLabel));
        gammaErrorLabel.getStyleClass().add(IO_ERROR_LABEL);
        gammaErrorLabel.setTooltip(new Tooltip("Error in measurement of γ"));

        //Phi
        phiLabel = new Label("φ");
        phiLabel.getStyleClass().add(IO_LABEL);
        phiLabel.setTooltip(new Tooltip("Pitch of the observation line"));


        phiErrorLabel = new Label();
        phiErrorLabel.setOnScroll(new ValueChangeScrollListener(phiErrorLabel));
        phiErrorLabel.getStyleClass().add(IO_ERROR_LABEL);
        phiErrorLabel.setTooltip(new Tooltip("Error in measurement of φ"));


        /**Campos de entrada*/

        // Beta
        betaTextField = new TextField();
        betaTextField.setOnScroll(new ValueChangeScrollListener(betaTextField));
        betaTextField.getStyleClass().add(IO_TEXT_FIELD);
        betaTextField.setTooltip(new Tooltip("β input"));

        // Gamma
        gammaTextField = new TextField();
        gammaTextField.setOnScroll(new ValueChangeScrollListener(gammaTextField));
        gammaTextField.getStyleClass().add(IO_TEXT_FIELD);
        gammaTextField.setTooltip(new Tooltip("γ input"));

        // Phi
        phiTextField = new TextField();
        phiTextField.setOnScroll(new ValueChangeScrollListener(phiTextField));
        phiTextField.getStyleClass().add(IO_TEXT_FIELD);
        phiTextField.setTooltip(new Tooltip("φ input"));


        /** Combo Box de los ángulos*/

        // Beta
        betaComboBox = new ComboBox<>();
        betaComboBox.getItems().addAll("N", "S");
        betaComboBox.setMaxWidth(Double.MAX_VALUE);
        betaComboBox.getStyleClass().add(IO_COMBO_BOX);
        betaComboBox.setTooltip(new Tooltip("β orientation"));

        // Gamma
        gammaComboBox = new ComboBox();
        gammaComboBox.getItems().addAll("N", "S");
        gammaComboBox.setMaxWidth(Double.MAX_VALUE);
        gammaComboBox.getStyleClass().add(IO_COMBO_BOX);
        gammaComboBox.setTooltip(new Tooltip("γ orientation"));

        // Phi
        phiComboBox = new ComboBox();
        phiComboBox.getItems().addAll("N", "S");
        phiComboBox.setMaxWidth(Double.MAX_VALUE);
        phiComboBox.getStyleClass().add(IO_COMBO_BOX);
        phiComboBox.setTooltip(new Tooltip("φ orientation"));

        /**Botones*/

        mapSectionToggleButton = new ToggleButton("Map & Sec");
        mapSectionToggleButton.setId("mapsectiontogglebutton");
        arbitraryLineToggleButton = new ToggleButton("Arbitrary Line");
        arbitraryLineToggleButton.setId("arbitrarylinetogglebutton");


        /** Creamos la malla que contiene todos los elementos del método */
        method1GridLayout = new GridPane();

        /**Set constraints nos permite hacer que la celda que contiene el nodo se redimencione al redimensionar
         * la ventana*/

        // Titulo
        Integer row = 0;
        GridPane.setConstraints(method1TitleLabel, 0, row, 4, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);

        // Toggle Buttons;
        row = 1;
        GridPane.setConstraints(mapSectionToggleButton, 0, row, 2, 1, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
        GridPane.setConstraints(arbitraryLineToggleButton, 2, row, 2, 1, HPos.RIGHT, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);

        // Phi
        row = 3;
        GridPane.setConstraints(betaLabel, 0, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);
        GridPane.setConstraints(betaTextField, 1, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
        GridPane.setConstraints(betaErrorLabel, 2, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
        GridPane.setConstraints(betaComboBox, 3, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);

        // Gamma
        row = 4;
        GridPane.setConstraints(gammaLabel, 0, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);
        GridPane.setConstraints(gammaTextField, 1, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
        GridPane.setConstraints(gammaErrorLabel, 2, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
        GridPane.setConstraints(gammaComboBox, 3, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);

        // Beta
        row = 5;
        GridPane.setConstraints(phiLabel, 0, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);
        GridPane.setConstraints(phiTextField, 1, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
        GridPane.setConstraints(phiErrorLabel, 2, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
        GridPane.setConstraints(phiComboBox, 3, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);


        // Agregamos los elementos de control a la malla.
        method1GridLayout.getChildren().addAll(
                method1TitleLabel, arbitraryLineToggleButton, mapSectionToggleButton
                , betaLabel, betaTextField, betaErrorLabel, betaComboBox
                , gammaLabel, gammaTextField, gammaErrorLabel, gammaComboBox
                , phiLabel, phiTextField, phiErrorLabel, phiComboBox);


        // Le damos las características deseadas a la malla;
        method1GridLayout.getStyleClass().add(IO_GRID_PANE);

    }

    /** Función que inicializa los componentes del método 2. */
    private void initMethod2ComponentsGrid() {

        /** Etiquetas del método dos*/

        // Titulo
        method2TitleLabel = new Label("Method 2");
        method2TitleLabel.getStyleClass().add(TITLE_LABEL);
        method2TitleLabel.setMaxWidth(Double.MAX_VALUE);


        // Fault Orientation
        foLabel = new Label("Fault Orientation");
        foLabel.getStyleClass().add(SUB_TITLE_LABEL);

        fodLabel = new Label("D");
        fodLabel.getStyleClass().add(IO_LABEL);
        fodLabel.setTooltip(new Tooltip("Dip"));

        fostkLabel = new Label("Stk");
        fostkLabel.getStyleClass().add(IO_LABEL);
        fostkLabel.setTooltip(new Tooltip("DipDirection"));


        fodErrorLabel = new Label();
        fodErrorLabel.setOnScroll(new ValueChangeScrollListener(fodErrorLabel));
        fodErrorLabel.setTooltip(new Tooltip("Dip Error"));
        fodErrorLabel.getStyleClass().add(IO_ERROR_LABEL);


        fostkErrorLabel = new Label();
        fostkErrorLabel.setOnScroll(new ValueChangeScrollListener(fostkErrorLabel));
        fostkErrorLabel.setTooltip(new Tooltip("Dip Direction Error"));
        fostkErrorLabel.getStyleClass().add(IO_ERROR_LABEL);

        // Observation Plane Orientation
        opoLabel = new Label("Observation Plane Orientation");
        opoLabel.getStyleClass().add(SUB_TITLE_LABEL);

        opodLabel = new Label("D");
        opodLabel.getStyleClass().add(IO_LABEL);
        opodLabel.setTooltip(new Tooltip("Dip"));

        opostkLabel = new Label("Stk");
        opostkLabel.getStyleClass().add(IO_LABEL);
        opostkLabel.setTooltip(new Tooltip("Dip Direction"));


        opodErrorLabel = new Label();
        opodErrorLabel.setOnScroll(new ValueChangeScrollListener(opodErrorLabel));
        opodErrorLabel.setTooltip(new Tooltip("Dip Error"));
        opodErrorLabel.getStyleClass().add(IO_ERROR_LABEL);


        opostkErrorLabel = new Label();
        opostkErrorLabel.setOnScroll(new ValueChangeScrollListener(opostkErrorLabel));
        opostkErrorLabel.setTooltip(new Tooltip("Dip Direction Error"));
        opostkErrorLabel.getStyleClass().add(IO_ERROR_LABEL);

        // Statigraphic marker orientation

        smoLabel = new Label("Stratigraphic Marker Orientation");
        smoLabel.getStyleClass().add(SUB_TITLE_LABEL);

        smo1dLabel = new Label("D");
        smo1dLabel.getStyleClass().add(IO_LABEL);
        smo1dLabel.setTooltip(new Tooltip("Dip"));

        smo1stkLabel = new Label("Stk");
        smo1stkLabel.getStyleClass().add(IO_LABEL);
        smo1stkLabel.setTooltip(new Tooltip("Dip Direction"));


        smo1dErrorLabel = new Label();
        smo1dErrorLabel.setOnScroll(new ValueChangeScrollListener(smo1dErrorLabel));
        smo1dErrorLabel.setTooltip(new Tooltip("Marker 1 Dip Error"));
        smo1dErrorLabel.getStyleClass().add(IO_ERROR_LABEL);


        smo1stkErrorLabel = new Label();
        smo1stkErrorLabel.setOnScroll(new ValueChangeScrollListener(smo1stkErrorLabel));
        smo1stkErrorLabel.setTooltip(new Tooltip("Marker 1 Dip Direction Error"));
        smo1stkErrorLabel.getStyleClass().add(IO_ERROR_LABEL);

        smo2dLabel = new Label("D");
        smo2dLabel.getStyleClass().add(IO_LABEL);
        smo2dLabel.setTooltip(new Tooltip("Dip"));

        smo2stkLabel = new Label("Stk");
        smo2stkLabel.getStyleClass().add(IO_LABEL);
        smo2stkLabel.setTooltip(new Tooltip("Dip Direction"));


        smo2dErrorLabel = new Label();
        smo2dErrorLabel.setOnScroll(new ValueChangeScrollListener(smo2dErrorLabel));
        smo2dErrorLabel.setTooltip(new Tooltip("Marker 2 Dip Error"));
        smo2dErrorLabel.getStyleClass().add(IO_ERROR_LABEL);


        smo2stkErrorLabel = new Label();
        smo2stkErrorLabel.setOnScroll(new ValueChangeScrollListener(smo2stkErrorLabel));
        smo2stkErrorLabel.setTooltip(new Tooltip("Marker 2 Dip Direction Error"));
        smo2stkErrorLabel.getStyleClass().add(IO_ERROR_LABEL);

        // Orientation of Striae;
        osLabel = new Label("Orientation Of Striae");
        osLabel.getStyleClass().add(SUB_TITLE_LABEL);

        ostrendLabel = new Label("Trnd");
        ostrendLabel.getStyleClass().add(IO_LABEL);
        ostrendLabel.setTooltip(new Tooltip("Trend"));

        osplungeLabel = new Label("Plng");
        osplungeLabel.getStyleClass().add(IO_LABEL);
        osplungeLabel.setTooltip(new Tooltip("Plng"));


        ostrendErrorLabel = new Label();
        ostrendErrorLabel.setOnScroll(new ValueChangeScrollListener(ostrendErrorLabel));
        ostrendErrorLabel.setTooltip(new Tooltip("Trend Error"));
        ostrendErrorLabel.getStyleClass().add(IO_ERROR_LABEL);


        osplungeErrorLabel = new Label();
        osplungeErrorLabel.setOnScroll(new ValueChangeScrollListener(osplungeErrorLabel));
        osplungeErrorLabel.setTooltip(new Tooltip("Plunch D Error"));
        osplungeErrorLabel.getStyleClass().add(IO_ERROR_LABEL);


        /**Text Fields*/

        // FaultOrientation
        fodTextField = new TextField();
        fodTextField.setOnScroll(new ValueChangeScrollListener(fodTextField));
        fodTextField.getStyleClass().add(IO_TEXT_FIELD);
        fodTextField.setTooltip(new Tooltip("Fault Dip Input"));

        fostkTextField = new TextField();
        fostkTextField.setOnScroll(new ValueChangeScrollListener(fostkTextField));
        fostkTextField.getStyleClass().add(IO_TEXT_FIELD);
        fostkTextField.setTooltip(new Tooltip("Fault Dip Direction Input"));

        // Observation Plane Orientation

        opodTextField = new TextField();
        opodTextField.setOnScroll(new ValueChangeScrollListener(opodTextField));
        opodTextField.getStyleClass().add(IO_TEXT_FIELD);
        opodTextField.setTooltip(new Tooltip("Observation Plane Dip Input"));

        opostkTextField = new TextField();
        opostkTextField.setOnScroll(new ValueChangeScrollListener(opostkTextField));
        opostkTextField.getStyleClass().add(IO_TEXT_FIELD);
        opostkTextField.setTooltip(new Tooltip("Observation Plane Dip DDirection Input"));

        // Statigraphic marker orientation

        smo1dTextField = new TextField();
        smo1dTextField.setOnScroll(new ValueChangeScrollListener(smo1dTextField));
        smo1dTextField.getStyleClass().add(IO_TEXT_FIELD);
        smo1dTextField.setTooltip(new Tooltip("Marker 1 Dip Input"));

        smo1stkTextField = new TextField();
        smo1stkTextField.setOnScroll(new ValueChangeScrollListener(smo1stkTextField));
        smo1stkTextField.getStyleClass().add(IO_TEXT_FIELD);
        smo1stkTextField.setTooltip(new Tooltip("Marker 1 Dip Direction Input"));

        smo2dTextField = new TextField();
        smo2dTextField.setOnScroll(new ValueChangeScrollListener(smo2dTextField));
        smo2dTextField.getStyleClass().add(IO_TEXT_FIELD);
        smo2dTextField.setTooltip(new Tooltip("Marker 2 Dip Input"));

        smo2stkTextField = new TextField();
        smo2stkTextField.setOnScroll(new ValueChangeScrollListener(smo2stkTextField));
        smo2stkTextField.getStyleClass().add(IO_TEXT_FIELD);
        smo2stkTextField.setTooltip(new Tooltip("Marker 2 Dip Direction Input"));

        // Orientation of Striae
        ostrendTextField = new TextField();
        ostrendTextField.setOnScroll(new ValueChangeScrollListener(ostrendTextField));
        ostrendTextField.getStyleClass().add(IO_TEXT_FIELD);
        ostrendTextField.setTooltip(new Tooltip("Trend Input"));

        osplungeTextField = new TextField();
        osplungeTextField.setOnScroll(new ValueChangeScrollListener(osplungeTextField));
        osplungeTextField.getStyleClass().add(IO_TEXT_FIELD);
        osplungeTextField.setTooltip(new Tooltip("Plunch Input"));


        /**Set constraints nos permite hacer que la celda que contiene el nodo se redimencione al redimensionar
         * la ventana*/

        /**Malla*/

        method2GridLayout = new GridPane();
        method2GridLayout.getStyleClass().add(IO_GRID_PANE);

        // Title
        Integer row = 0;
        GridPane.setConstraints(method2TitleLabel, 0, row, 6, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);

        // Fault Orientation
        row = 1;
        GridPane.setConstraints(foLabel, 0, row, 6, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
        row = 2;
        GridPane.setConstraints(fostkLabel, 0, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);
        GridPane.setConstraints(fostkTextField, 1, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(fostkErrorLabel, 2, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(fodLabel, 3, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);
        GridPane.setConstraints(fodTextField, 4, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(fodErrorLabel, 5, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);


        // Observation Plane Orientation
        row = 3;
        GridPane.setConstraints(opoLabel, 0, row, 6, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
        row = 4;
        GridPane.setConstraints(opostkLabel, 0, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);
        GridPane.setConstraints(opostkTextField, 1, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(opostkErrorLabel, 2, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(opodLabel, 3, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);
        GridPane.setConstraints(opodTextField, 4, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(opodErrorLabel, 5, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);


        // SMO
        row = 5;
        GridPane.setConstraints(smoLabel, 0, row, 6, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
        row = 6;
        GridPane.setConstraints(smo1stkLabel, 0, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);
        GridPane.setConstraints(smo1stkTextField, 1, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(smo1stkErrorLabel, 2, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(smo1dLabel, 3, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);
        GridPane.setConstraints(smo1dTextField, 4, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(smo1dErrorLabel, 5, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);

        row = 7;
        GridPane.setConstraints(smo2stkLabel, 0, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);
        GridPane.setConstraints(smo2stkTextField, 1, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(smo2stkErrorLabel, 2, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(smo2dLabel, 3, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);
        GridPane.setConstraints(smo2dTextField, 4, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(smo2dErrorLabel, 5, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);


        // Striae Orientation
        row = 8;
        GridPane.setConstraints(osLabel, 0, row, 6, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
        row = 9;
        GridPane.setConstraints(ostrendLabel, 0, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);
        GridPane.setConstraints(ostrendTextField, 1, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(ostrendErrorLabel, 2, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(osplungeLabel, 3, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);
        GridPane.setConstraints(osplungeTextField, 4, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(osplungeErrorLabel, 5, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);

        // Agregamos los campos de entrada.
        method2GridLayout.getChildren().addAll(method2TitleLabel
                , fostkLabel, fostkTextField, fostkErrorLabel
                , foLabel, fodLabel, fodTextField, fodErrorLabel
                , opostkLabel, opostkTextField, opostkErrorLabel
                , opoLabel, opodLabel, opodTextField, opodErrorLabel
                , smo1stkLabel, smo1stkTextField, smo1stkErrorLabel
                , smoLabel, smo1dLabel, smo1dTextField, smo1dErrorLabel
                , smo2stkLabel, smo2stkTextField, smo2stkErrorLabel
                , smo2dLabel, smo2dTextField, smo2dErrorLabel
                , osLabel, ostrendLabel, ostrendTextField, ostrendErrorLabel
                , osplungeLabel, osplungeTextField, osplungeErrorLabel
        );

    }

    /** Función que inicializa los compoentes del dialogo de cosenos directores. */
    private void initDirCosComponentsGrid() {
        /**Labels*/

        // Title
        cosDirTitleLabel = new Label("Method 3");
        cosDirTitleLabel.getStyleClass().add(TITLE_LABEL);
        cosDirTitleLabel.setMaxWidth(Double.MAX_VALUE);

        // F Plane
        fpLabel = new Label("F Plane");
        fpLabel.getStyleClass().add(SUB_TITLE_LABEL);

        fpmLabel = new Label("m(fp)");
        fpmLabel.getStyleClass().add(IO_LABEL);
        fpmLabel.setTooltip(new Tooltip("F Plane m"));

        fpnLabel = new Label("n(fp)");
        fpnLabel.getStyleClass().add(IO_LABEL);
        fpnLabel.setTooltip(new Tooltip("F Plane n"));

        fplLabel = new Label("l(fp)");
        fplLabel.getStyleClass().add(IO_LABEL);
        fplLabel.setTooltip(new Tooltip("F Plane l"));


        fpnErrorLabel = new Label();
        //fpnErrorLabel.setOnScroll(new ValueChangeScrollListener(fpnErrorLabel,METHOD_3));
        fpnErrorLabel.setTooltip(new Tooltip("Error in measurement of n"));
        fpnErrorLabel.getStyleClass().add(IO_ERROR_LABEL);


        fpmErrorLabel = new Label();
        //fpmErrorLabel.setOnScroll(new ValueChangeScrollListener(fpmErrorLabel,METHOD_3));
        fpmErrorLabel.setTooltip(new Tooltip("Error in measurement of m"));
        fpmErrorLabel.getStyleClass().add(IO_ERROR_LABEL);


        fplErrorLabel = new Label();
        //fplErrorLabel.setOnScroll(new ValueChangeScrollListener(fplErrorLabel,METHOD_3));
        fplErrorLabel.setTooltip(new Tooltip("Error in measurement of l"));
        fplErrorLabel.getStyleClass().add(IO_ERROR_LABEL);

        // O Plane
        opLabel = new Label("O Plane");
        opLabel.getStyleClass().add(SUB_TITLE_LABEL);

        opmLabel = new Label("m(op)");
        opmLabel.getStyleClass().add(IO_LABEL);
        opmLabel.setTooltip(new Tooltip("O Plane m"));

        opnLabel = new Label("n(op)");
        opnLabel.getStyleClass().add(IO_LABEL);
        opnLabel.setTooltip(new Tooltip("O Plane n"));

        oplLabel = new Label("l(op)");
        oplLabel.getStyleClass().add(IO_LABEL);
        oplLabel.setTooltip(new Tooltip("O Plane l"));


        opnErrorLabel = new Label();
        //opnErrorLabel.setOnScroll(new ValueChangeScrollListener(opnErrorLabel,METHOD_3));
        opnErrorLabel.setTooltip(new Tooltip("Error in measurement of n"));
        opnErrorLabel.getStyleClass().add(IO_ERROR_LABEL);


        opmErrorLabel = new Label();
        //opmErrorLabel.setOnScroll(new ValueChangeScrollListener(opmErrorLabel,METHOD_3));
        opmErrorLabel.setTooltip(new Tooltip("Error in measurement of m"));
        opmErrorLabel.getStyleClass().add(IO_ERROR_LABEL);


        oplErrorLabel = new Label();
        //oplErrorLabel.setOnScroll(new ValueChangeScrollListener(oplErrorLabel,METHOD_3));
        oplErrorLabel.setTooltip(new Tooltip("Error in measurement of l"));
        oplErrorLabel.getStyleClass().add(IO_ERROR_LABEL);

        // A Plane
        apLabel = new Label("A Plane");
        apLabel.getStyleClass().add(SUB_TITLE_LABEL);

        apmLabel = new Label("m(ap)");
        apmLabel.getStyleClass().add(IO_LABEL);
        apmLabel.setTooltip(new Tooltip("A Plane m"));

        apnLabel = new Label("n(ap)");
        apnLabel.getStyleClass().add(IO_LABEL);
        apnLabel.setTooltip(new Tooltip("A Plane n"));

        aplLabel = new Label("l(ap)");
        aplLabel.getStyleClass().add(IO_LABEL);
        aplLabel.setTooltip(new Tooltip("A Plane l"));


        apnErrorLabel = new Label();
        //apnErrorLabel.setOnScroll(new ValueChangeScrollListener(apnErrorLabel,METHOD_3));
        apnErrorLabel.setTooltip(new Tooltip("Error in measurement of n"));
        apnErrorLabel.getStyleClass().add(IO_ERROR_LABEL);


        apmErrorLabel = new Label();
        //apmErrorLabel.setOnScroll(new ValueChangeScrollListener(apmErrorLabel,METHOD_3));
        apmErrorLabel.setTooltip(new Tooltip("Error in measurement of m"));
        apmErrorLabel.getStyleClass().add(IO_ERROR_LABEL);


        aplErrorLabel = new Label();
        //aplErrorLabel.setOnScroll(new ValueChangeScrollListener(aplErrorLabel,METHOD_3));
        aplErrorLabel.setTooltip(new Tooltip("Error in measurement of l"));
        aplErrorLabel.getStyleClass().add(IO_ERROR_LABEL);

        // B Plane
        bpLabel = new Label("B Plane");
        bpLabel.getStyleClass().add(SUB_TITLE_LABEL);

        bpmLabel = new Label("m(bp)");
        bpmLabel.getStyleClass().add(IO_LABEL);
        bpmLabel.setTooltip(new Tooltip("B Plane m"));

        bpnLabel = new Label("n(bp)");
        bpnLabel.getStyleClass().add(IO_LABEL);
        bpnLabel.setTooltip(new Tooltip("B Plane n"));

        bplLabel = new Label("l(bp)");
        bplLabel.getStyleClass().add(IO_LABEL);
        bplLabel.setTooltip(new Tooltip("B Plane l"));


        bpnErrorLabel = new Label();
        //bpnErrorLabel.setOnScroll(new ValueChangeScrollListener(bpnErrorLabel,METHOD_3));
        bpnErrorLabel.setTooltip(new Tooltip("Error in measurement of n"));
        bpnErrorLabel.getStyleClass().add(IO_ERROR_LABEL);


        bpmErrorLabel = new Label();
        //bpmErrorLabel.setOnScroll(new ValueChangeScrollListener(bpmErrorLabel,METHOD_3));
        bpmErrorLabel.setTooltip(new Tooltip("Error in measurement of m"));
        bpmErrorLabel.getStyleClass().add(IO_ERROR_LABEL);


        bplErrorLabel = new Label();
        //bplErrorLabel.setOnScroll(new ValueChangeScrollListener(bplErrorLabel,METHOD_3));
        bplErrorLabel.setTooltip(new Tooltip("Error in measurement of l"));
        bplErrorLabel.getStyleClass().add(IO_ERROR_LABEL);

        /**Text Field*/

        // F Plane
        fpmTextField = new TextField();
        //fpmTextField.setOnScroll(new ValueChangeScrollListener(fpmTextField,METHOD_3));
        fpmTextField.getStyleClass().add(IO_TEXT_FIELD);
        fpmTextField.setTooltip(new Tooltip("FOD D input"));
        fpmTextField.setEditable(false);

        fpnTextField = new TextField();
        //fpnTextField.setOnScroll(new ValueChangeScrollListener(fpnTextField,METHOD_3));
        fpnTextField.getStyleClass().add(IO_TEXT_FIELD);
        fpnTextField.setTooltip(new Tooltip("FOD DD input"));
        fpnTextField.setEditable(false);

        fplTextField = new TextField();
        //fplTextField.setOnScroll(new ValueChangeScrollListener(fplTextField,METHOD_3));
        fplTextField.getStyleClass().add(IO_TEXT_FIELD);
        fplTextField.setTooltip(new Tooltip("FOD DD input"));
        fplTextField.setEditable(false);

        // O Plane
        opmTextField = new TextField();
        //opmTextField.setOnScroll(new ValueChangeScrollListener(opmTextField,METHOD_3));
        opmTextField.getStyleClass().add(IO_TEXT_FIELD);
        opmTextField.setTooltip(new Tooltip("FOD D input"));
        opmTextField.setEditable(false);

        opnTextField = new TextField();
        //opnTextField.setOnScroll(new ValueChangeScrollListener(opnTextField,METHOD_3));
        opnTextField.getStyleClass().add(IO_TEXT_FIELD);
        opnTextField.setTooltip(new Tooltip("FOD DD input"));
        opnTextField.setEditable(false);

        oplTextField = new TextField();
        //oplTextField.setOnScroll(new ValueChangeScrollListener(oplTextField,METHOD_3));
        oplTextField.getStyleClass().add(IO_TEXT_FIELD);
        oplTextField.setTooltip(new Tooltip("FOD DD input"));
        oplTextField.setEditable(false);

        // A Plane
        apmTextField = new TextField();
        //apmTextField.setOnScroll(new ValueChangeScrollListener(apmTextField,METHOD_3));
        apmTextField.getStyleClass().add(IO_TEXT_FIELD);
        apmTextField.setTooltip(new Tooltip("FOD D input"));
        apmTextField.setEditable(false);

        apnTextField = new TextField();
        //apnTextField.setOnScroll(new ValueChangeScrollListener(apnTextField,METHOD_3));
        apnTextField.getStyleClass().add(IO_TEXT_FIELD);
        apnTextField.setTooltip(new Tooltip("FOD DD input"));
        apnTextField.setEditable(false);

        aplTextField = new TextField();
        //aplTextField.setOnScroll(new ValueChangeScrollListener(aplTextField,METHOD_3));
        aplTextField.getStyleClass().add(IO_TEXT_FIELD);
        aplTextField.setTooltip(new Tooltip("FOD DD input"));
        aplTextField.setEditable(false);

        // B Plane
        bpmTextField = new TextField();
        //bpmTextField.setOnScroll(new ValueChangeScrollListener(bpmTextField,METHOD_3));
        bpmTextField.getStyleClass().add(IO_TEXT_FIELD);
        bpmTextField.setTooltip(new Tooltip("FOD D input"));
        bpmTextField.setEditable(false);

        bpnTextField = new TextField();
        //bpnTextField.setOnScroll(new ValueChangeScrollListener(bpnTextField,METHOD_3));
        bpnTextField.getStyleClass().add(IO_TEXT_FIELD);
        bpnTextField.setTooltip(new Tooltip("FOD DD input"));
        bpnTextField.setEditable(false);

        bplTextField = new TextField();
        //bplTextField.setOnScroll(new ValueChangeScrollListener(bplTextField,METHOD_3));
        bplTextField.getStyleClass().add(IO_TEXT_FIELD);
        bplTextField.setTooltip(new Tooltip("FOD DD input"));
        bplTextField.setEditable(false);

        /** Malla */

        cosDirGridLayout = new GridPane();
        cosDirGridLayout.getStyleClass().add(IO_GRID_PANE);

        // Title
        Integer row = 0;
        GridPane.setConstraints(cosDirTitleLabel, 0, row, 9, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);

        // FPlane
        row = 1;
        GridPane.setConstraints(fpLabel, 0, row, 9, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);

        row = 2;
        GridPane.setConstraints(fplLabel, 0, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(fplTextField, 1, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(fplErrorLabel, 2, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(fpmLabel, 3, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(fpmTextField, 4, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(fpmErrorLabel, 5, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(fpnLabel, 6, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(fpnTextField, 7, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(fpnErrorLabel, 8, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);

        // O Plane
        row = 3;
        GridPane.setConstraints(opLabel, 0, row, 9, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);

        row = 4;
        GridPane.setConstraints(oplLabel, 0, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(oplTextField, 1, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(oplErrorLabel, 2, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(opmLabel, 3, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(opmTextField, 4, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(opmErrorLabel, 5, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(opnLabel, 6, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(opnTextField, 7, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(opnErrorLabel, 8, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);

        // A Plane
        row = 5;
        GridPane.setConstraints(apLabel, 0, row, 9, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);

        row = 6;
        GridPane.setConstraints(aplLabel, 0, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(aplTextField, 1, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(aplErrorLabel, 2, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(apmLabel, 3, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(apmTextField, 4, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(apmErrorLabel, 5, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(apnLabel, 6, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(apnTextField, 7, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(apnErrorLabel, 8, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);

        // B Plane
        row = 7;
        GridPane.setConstraints(bpLabel, 0, row, 9, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);

        row = 8;
        GridPane.setConstraints(bplLabel, 0, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(bplTextField, 1, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(bplErrorLabel, 2, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(bpmLabel, 3, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(bpmTextField, 4, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(bpmErrorLabel, 5, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(bpnLabel, 6, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(bpnTextField, 7, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(bpnErrorLabel, 8, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);

        cosDirGridLayout.getChildren().addAll(
                cosDirTitleLabel
                , fpLabel, fplLabel, fplTextField, fplErrorLabel, fpmLabel, fpmTextField, fpmErrorLabel, fpnLabel, fpnTextField, fpnErrorLabel
                , opLabel, oplLabel, oplTextField, oplErrorLabel, opmLabel, opmTextField, opmErrorLabel, opnLabel, opnTextField, opnErrorLabel
                , apLabel, aplLabel, aplTextField, aplErrorLabel, apmLabel, apmTextField, apmErrorLabel, apnLabel, apnTextField, apnErrorLabel
                , bpLabel, bplLabel, bplTextField, bplErrorLabel, bpmLabel, bpmTextField, bpmErrorLabel, bpnLabel, bpnTextField, bpnErrorLabel
        );

        // Stage
        dirCosWindow = new Stage(StageStyle.DECORATED);
        dirCosWindow.setScene(new Scene(cosDirGridLayout));
        dirCosWindow.getScene().getStylesheets().addAll(TRUDISP_STYLE_SHEET);

    }

    /** Función que inicializa los componentes del desplazamiento */
    private void initDisplacementComponentsGrid() {

        /**Etiquetas*/

        // Alpha;
        alphaLabel = new Label("α");
        alphaLabel.getStyleClass().add(IO_LABEL);
        alphaLabel.setTooltip(new Tooltip("Dip of fault plane"));


        alphaErrorLabel = new Label();
        alphaErrorLabel.getStyleClass().add(IO_ERROR_LABEL);
        alphaErrorLabel.setOnScroll(new ValueChangeScrollListener(alphaErrorLabel));
        alphaErrorLabel.setTooltip(new Tooltip("Error in measurement of α"));

        // SmA;
        smALabel = new Label("SmA");
        smALabel.getStyleClass().add(IO_LABEL);
        smALabel.setTooltip(new Tooltip("Apparent displacement along observation line"));


        smAErrorLabel = new Label();
        smAErrorLabel.getStyleClass().add(IO_ERROR_LABEL);
        smAErrorLabel.setOnScroll(new ValueChangeScrollListener(smAErrorLabel));
        smAErrorLabel.setTooltip(new Tooltip("Error in measurement of Sm"));

        //SmB
        smBLabel = new Label("SmB");
        smBLabel.getStyleClass().add(IO_LABEL);
        smBLabel.setTooltip(new Tooltip("Apparent displacemtn along observation line"));

        smBErrorLabel = new Label();
        smBErrorLabel.getStyleClass().addAll(IO_ERROR_LABEL);
        smBErrorLabel.setOnScroll(new ValueChangeScrollListener(smBErrorLabel));
        smBErrorLabel.setTooltip(new Tooltip("Error in measrment"));

        //dAB
        dABLabel = new Label("dAB");
        dABLabel.getStyleClass().add(IO_LABEL);
        dABLabel.setTooltip(new Tooltip("distancias entre planos A y B"));

        dABErrorLabel = new Label();
        dABErrorLabel.getStyleClass().addAll(IO_ERROR_LABEL);
        dABErrorLabel.setOnScroll(new ValueChangeScrollListener(smBErrorLabel));
        dABErrorLabel.setTooltip(new Tooltip("Error in measrment"));


        // Smh;
        smhLabel = new Label("Smh");
        smhLabel.getStyleClass().add(IO_LABEL);
        smhLabel.setTooltip(new Tooltip("Apparent dip displacement (separation) along strike line"));


        smhErrorLabel = new Label();
        smhErrorLabel.getStyleClass().add(IO_ERROR_LABEL);
        smhErrorLabel.setOnScroll(new ValueChangeScrollListener(smhErrorLabel));
        smhErrorLabel.setTooltip(new Tooltip("Error in measurement of Sm"));

        // Smd;
        smdLabel = new Label("Smd");
        smdLabel.getStyleClass().add(IO_LABEL);
        smdLabel.setTooltip(new Tooltip("Apparent dip displacement (separation) along dip line"));


        smdErrorLabel = new Label();
        smdErrorLabel.getStyleClass().add(IO_ERROR_LABEL);
        smdErrorLabel.setOnScroll(new ValueChangeScrollListener(smdErrorLabel));
        smdErrorLabel.setTooltip(new Tooltip("Error in measurement of Sm"));

        /**TextField*/

        // Alpha
        alphaTextField = new TextField();
        alphaTextField.setOnScroll(new ValueChangeScrollListener(alphaTextField));
        alphaTextField.getStyleClass().add(IO_TEXT_FIELD);
        alphaTextField.setTooltip(new Tooltip("α input"));

        // SmA
        smATextField = new TextField();
        smATextField.setOnScroll(new ValueChangeScrollListener(smATextField));
        smATextField.getStyleClass().add(IO_TEXT_FIELD);
        smATextField.setTooltip(new Tooltip("smA input"));

        // SmB
        smBTextField = new TextField();
        smBTextField.setOnScroll(new ValueChangeScrollListener(smBTextField));
        smBTextField.getStyleClass().add(IO_TEXT_FIELD);
        smBTextField.setTooltip(new Tooltip("smB input"));

        //dAB
        dABTextField = new TextField();
        dABTextField.setOnScroll(new ValueChangeScrollListener(dABTextField));
        dABTextField.getStyleClass().add(IO_TEXT_FIELD);
        dABTextField.setTooltip(new Tooltip("dab input"));

        // Smh
        smhTextField = new TextField();
        smhTextField.setOnScroll(new ValueChangeScrollListener(smhTextField));
        smhTextField.getStyleClass().add(IO_TEXT_FIELD);
        smhTextField.setTooltip(new Tooltip("Sm input"));

        // Smd
        smdTextField = new TextField();
        smdTextField.setOnScroll(new ValueChangeScrollListener(smdTextField));
        smdTextField.getStyleClass().add(IO_TEXT_FIELD);
        smdTextField.setTooltip(new Tooltip("Sm input"));


        /**Set constraints nos permite hacer que la celda que contiene el nodo se redimencione al redimensionar
         * la ventana*/

        /**Malla*/

        displacementGridLayout = new GridPane();
        displacementGridLayout.getStyleClass().add(IO_GRID_PANE);


        Integer row = 0;
        GridPane.setConstraints(alphaLabel, 0, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);
        GridPane.setConstraints(alphaTextField, 1, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(alphaErrorLabel, 2, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);

        GridPane.setConstraints(smALabel, 3, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);
        GridPane.setConstraints(smATextField, 4, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(smAErrorLabel, 5, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);

        row = 1;

        GridPane.setConstraints(smhLabel, 0, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);
        GridPane.setConstraints(smhTextField, 1, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(smhErrorLabel, 2, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);

        GridPane.setConstraints(smdLabel, 3, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);
        GridPane.setConstraints(smdTextField, 4, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(smdErrorLabel, 5, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);

        row = 1;

        GridPane.setConstraints(dABLabel, 0, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);
        GridPane.setConstraints(dABTextField, 1, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(dABErrorLabel, 2, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);

        GridPane.setConstraints(smBLabel, 3, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);
        GridPane.setConstraints(smBTextField, 4, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(smBErrorLabel, 5, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);


        // Agregamos los componentes.
        displacementGridLayout.getChildren().addAll(
                alphaLabel, alphaTextField, alphaErrorLabel
                , smALabel, smATextField, smAErrorLabel
                , smhLabel, smhTextField, smhErrorLabel
                , smdLabel, smdTextField, smdErrorLabel
        );

    }

    /** Función que inicializa los componentes de resultado */
    private void initResultComponentsGrid() {

        /**Etiquetas*/

        // Titulo
        resultTitleLabel = new Label("Result");
        resultTitleLabel.getStyleClass().add(TITLE_LABEL);
        // S
        sLabel = new Label("S");
        sLabel.getStyleClass().add(IO_LABEL);
        sLabel.setId("slabel");

        sErrorLabel = new Label();
        sErrorLabel.getStyleClass().add(IO_ERROR_LABEL);
        // sv
        svLabel = new Label("Sv");
        svLabel.getStyleClass().add(IO_LABEL);

        svErrorLabel = new Label();
        svErrorLabel.getStyleClass().add(IO_ERROR_LABEL);
        // sd
        sdLabel = new Label("Sd");
        sdLabel.getStyleClass().add(IO_LABEL);

        sdErrorLabel = new Label();
        sdErrorLabel.getStyleClass().add(IO_ERROR_LABEL);
        // sh
        shLabel = new Label("Sh");
        shLabel.getStyleClass().add(IO_LABEL);

        shErrorLabel = new Label();
        shErrorLabel.getStyleClass().add(IO_ERROR_LABEL);
        // ss
        ssLabel = new Label("Ss");
        ssLabel.getStyleClass().add(IO_LABEL);

        ssErrorLabel = new Label();
        ssErrorLabel.getStyleClass().add(IO_ERROR_LABEL);
        // theta
        thetaLabel = new Label("θ");
        thetaLabel.getStyleClass().add(IO_LABEL);
        // thetanull
        thetanullLabel = new Label("θnull");
        thetanullLabel.getStyleClass().add(IO_LABEL);


        /**Text Fields*/

        // S
        sTextField = new TextField();
        sTextField.getStyleClass().add(IO_TEXT_FIELD);
        // sv
        svTextField = new TextField();
        svTextField.getStyleClass().add(IO_TEXT_FIELD);
        //sh
        shTextField = new TextField();
        shTextField.getStyleClass().add(IO_TEXT_FIELD);
        //Sd
        sdTextField = new TextField();
        sdTextField.getStyleClass().add(IO_TEXT_FIELD);
        //Sh
        ssTextField = new TextField();
        ssTextField.getStyleClass().add(IO_TEXT_FIELD);
        //theta
        thetaTextField = new TextField();
        thetaTextField.setId("thetatextfield");
        thetaTextField.getStyleClass().add(IO_TEXT_FIELD);
        //thetanull
        thetanullTextField = new TextField();
        thetanullTextField.setMaxWidth(100);
        thetanullTextField.getStyleClass().add(IO_TEXT_FIELD);
        thetanullTextField.setId("thetanulltextfield");

        /**Botones*/
        calculateButton = new Button("Calculate");
        calculateButton.setMaxWidth(380);
        resetButton = new Button("C");
        calculateButton.setMinWidth(50);



        /**Malla*/

        resultGridLayout = new GridPane();
        resultGridLayout.getStyleClass().add(IO_GRID_PANE);


        // Theta y Thetanull
        Integer row = 0;

        GridPane.setConstraints(thetaLabel, 1, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
        GridPane.setConstraints(thetaTextField, 0, row, 1, 1, HPos.RIGHT, VPos.CENTER, Priority.NEVER, Priority.NEVER);

        GridPane.setConstraints(thetanullLabel, 7, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
        GridPane.setConstraints(thetanullTextField, 6, row, 1, 1, HPos.RIGHT, VPos.CENTER, Priority.NEVER, Priority.NEVER);


        row = 1;
        GridPane.setConstraints(resultTitleLabel, 0, row, 8, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);

        //S
        row = 2;
        GridPane.setConstraints(sLabel, 1, row, 1, 1, HPos.RIGHT, VPos.CENTER, Priority.NEVER, Priority.NEVER);
        GridPane.setConstraints(sTextField, 2, row, 4, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(sErrorLabel, 6, row, 1, 1, HPos.LEFT, VPos.CENTER, Priority.NEVER, Priority.NEVER);

        // ss sv
        row = 3;
        GridPane.setConstraints(ssLabel, 1, row, 1, 1, HPos.RIGHT, VPos.CENTER, Priority.NEVER, Priority.NEVER);
        GridPane.setConstraints(ssTextField, 2, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
        GridPane.setConstraints(ssErrorLabel, 3, row, 1, 1, HPos.LEFT, VPos.CENTER, Priority.NEVER, Priority.NEVER);

        GridPane.setConstraints(svLabel, 4, row, 1, 1, HPos.RIGHT, VPos.CENTER, Priority.NEVER, Priority.NEVER);
        GridPane.setConstraints(svTextField, 5, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
        GridPane.setConstraints(svErrorLabel, 6, row, 1, 1, HPos.LEFT, VPos.CENTER, Priority.NEVER, Priority.NEVER);

        // sh sd
        row = 4;

        GridPane.setConstraints(shLabel, 1, row, 1, 1, HPos.RIGHT, VPos.CENTER, Priority.NEVER, Priority.NEVER);
        GridPane.setConstraints(shTextField, 2, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
        GridPane.setConstraints(shErrorLabel, 3, row, 1, 1, HPos.LEFT, VPos.CENTER, Priority.NEVER, Priority.NEVER);

        GridPane.setConstraints(sdLabel, 4, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
        GridPane.setConstraints(sdTextField, 5, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
        GridPane.setConstraints(sdErrorLabel, 6, row, 1, 1, HPos.LEFT, VPos.CENTER, Priority.NEVER, Priority.NEVER);


        // Buttons
        row = 5;
        GridPane.setConstraints(calculateButton, 2, row, 5, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);

        GridPane.setConstraints(resetButton,1,row,1,1,HPos.LEFT,VPos.CENTER,Priority.NEVER,Priority.ALWAYS);

        // Agregamos los componentes
        resultGridLayout.getChildren().addAll(
                resultTitleLabel
                , thetaLabel, thetaTextField, thetanullLabel, thetanullTextField
                , sLabel, sTextField, sErrorLabel
                , ssLabel, ssTextField, ssErrorLabel
                , svLabel, svTextField, svErrorLabel
                , shLabel, shTextField, shErrorLabel
                , sdLabel, sdTextField, sdErrorLabel
                , calculateButton,resetButton
        );


    }

    /** Función que coloca todos los campos de entrada y salida en ceros. */
    private void clearcomponents() {

        /** Valores por defecto **/
        String angleErrorString = "± " + "0.0" + " º";
        String defaultValue = "0.0";
        String defaultErrorString = "± " + "0.0";

        /** Metodo 1 **/

        betaErrorLabel.setText(angleErrorString);
        gammaErrorLabel.setText(angleErrorString);
        phiErrorLabel.setText(angleErrorString);

        betaTextField.setText(defaultValue);
        gammaTextField.setText(defaultValue);
        phiTextField.setText(defaultValue);

        betaComboBox.setValue("N");
        gammaComboBox.setValue("N");
        phiComboBox.setValue("N");

        /** Metodo 2*/

        fodErrorLabel.setText(angleErrorString);
        fostkErrorLabel.setText(angleErrorString);
        opodErrorLabel.setText(angleErrorString);
        opostkErrorLabel.setText(angleErrorString);
        smo1dErrorLabel.setText(angleErrorString);
        smo1stkErrorLabel.setText(angleErrorString);
        smo2dErrorLabel.setText(angleErrorString);
        smo2stkErrorLabel.setText(angleErrorString);
        ostrendErrorLabel.setText(angleErrorString);
        osplungeErrorLabel.setText(angleErrorString);

        fodTextField.setText(defaultValue);
        fostkTextField.setText(defaultValue);
        opodTextField.setText(defaultValue);
        opostkTextField.setText(defaultValue);
        smo1dTextField.setText(defaultValue);
        smo1stkTextField.setText(defaultValue);
        smo2dTextField.setText(defaultValue);
        smo2stkTextField.setText(defaultValue);
        ostrendTextField.setText(defaultValue);
        osplungeTextField.setText(defaultValue);

        /** Cosenos Directores de los planos**/

        fpnErrorLabel.setText(defaultErrorString);
        fpmErrorLabel.setText(defaultErrorString);
        fplErrorLabel.setText(defaultErrorString);
        opnErrorLabel.setText(defaultErrorString);
        opmErrorLabel.setText(defaultErrorString);
        oplErrorLabel.setText(defaultErrorString);
        apnErrorLabel.setText(defaultErrorString);
        apmErrorLabel.setText(defaultErrorString);
        aplErrorLabel.setText(defaultErrorString);
        bpnErrorLabel.setText(defaultErrorString);
        bpmErrorLabel.setText(defaultErrorString);
        bplErrorLabel.setText(defaultErrorString);

        fpmTextField.setText(defaultValue);
        fpnTextField.setText(defaultValue);
        fplTextField.setText(defaultValue);
        opnTextField.setText(defaultValue);
        oplTextField.setText(defaultValue);
        opmTextField.setText(defaultValue);
        apnTextField.setText(defaultValue);
        apmTextField.setText(defaultValue);
        aplTextField.setText(defaultValue);
        bpnTextField.setText(defaultValue);
        bpmTextField.setText(defaultValue);
        bplTextField.setText(defaultValue);

        /** Componentes de desplazamiento*/

        alphaErrorLabel.setText(angleErrorString);
        smAErrorLabel.setText(defaultErrorString);
        smBErrorLabel.setText(defaultErrorString);
        dABErrorLabel.setText(defaultErrorString);
        smhErrorLabel.setText(defaultErrorString);
        smdErrorLabel.setText(defaultErrorString);

        alphaTextField.setText(defaultValue);
        smATextField.setText(defaultValue);
        smBTextField.setText(defaultValue);
        dABTextField.setText(defaultValue);
        smhTextField.setText(defaultValue);
        smdTextField.setText(defaultValue);

        /** Resultados **/

        sErrorLabel.setText(defaultErrorString);
        ssErrorLabel.setText(defaultErrorString);
        sdErrorLabel.setText(defaultErrorString);
        svErrorLabel.setText(defaultErrorString);
        shErrorLabel.setText(defaultErrorString);

        sTextField.setText(defaultValue);
        ssTextField.setText(defaultValue);
        sdTextField.setText(defaultValue);
        svTextField.setText(defaultValue);
        shTextField.setText(defaultValue);
        thetanullTextField.setText(defaultValue);
        thetaTextField.setText(defaultValue);


    }

    /** Función que coloca las acciones de los botones de la aplicación. */
    public void initGUIActions() {

        /** Activamos los campos que no se utilizan  */

        // CAMBIAMOS ENTRE LOS DISTINTOS CASOS DEL MÉTODO 1
        arbitraryLineToggleButton.setOnAction(event -> {

            if (arbitraryLineToggleButton.isSelected()) {

                mapSectionToggleButton.setSelected(false);
                //Habilitamos
                phiComboBox.setDisable(false);
                phiErrorLabel.setDisable(false);
                phiLabel.setDisable(false);
                phiTextField.setDisable(false);
                smATextField.setDisable(false);
                smALabel.setDisable(false);
                smAErrorLabel.setDisable(false);

                //Desabilitamos
                smhTextField.setDisable(true);
                smhLabel.setDisable(true);
                smhErrorLabel.setDisable(true);

                // Desplegamos información
                statusPane.setStatus("Este es un caso general", ADVICE_ICON);

                // Limpiamos
                clearcomponents();

            } else {
                arbitraryLineToggleButton.setSelected(true);
            }

        });
        // Seleccionamos el modo arbitrario por defecto.
        arbitraryLineToggleButton.fire();

        // Modo MapAndSection.
        mapSectionToggleButton.setOnAction(event -> {

            if (mapSectionToggleButton.isSelected()) {

                arbitraryLineToggleButton.setSelected(false);
                // Habilitamos
                smhTextField.setDisable(false);
                smhLabel.setDisable(false);
                smhErrorLabel.setDisable(false);
                // Dsabilitamos
                phiComboBox.setDisable(true);
                phiErrorLabel.setDisable(true);
                phiLabel.setDisable(true);
                phiTextField.setDisable(true);
                smATextField.setDisable(true);
                smALabel.setDisable(true);
                smAErrorLabel.setDisable(true);
                // Desplegamos que es;
                statusPane.setStatus("Este es un caso particular", ADVICE_ICON);
                //Limpiamos
                clearcomponents();
            } else {
                mapSectionToggleButton.setSelected(true);
            }
        });


        /** Cambiamos entre la selección de los distitnos métodos. */

        // Si se da click en el título del método 1 entonces se habilita este metodo.
        method1TitleLabel.setOnMouseClicked(event -> {

            // Revisamos en que modo esta el método 1.
            if (mapSectionToggleButton.isSelected()) {
                mapSectionToggleButton.fire();
            } else {
                arbitraryLineToggleButton.fire();
            }

            // Habilitamos los componentes del método 1.
            method1GridLayout.getChildren().stream().forEach(item -> item.setDisable(false));
            // Desabilitamos los componentes del método 2.
            method2GridLayout.getChildren().stream().filter(item -> !item.getStyleClass().contains(TITLE_LABEL)).forEach(item -> item.setDisable(true));

            // Actibamos el campo de alpha.
            alphaErrorLabel.setDisable(false);alphaTextField.setDisable(false);alphaErrorLabel.setDisable(false);

            // Modificamos el la malla de desplazamientos para agregar los campos correspondientes al método 1.
            displacementGridLayout.getChildren().clear();
            displacementGridLayout.getChildren().addAll(alphaLabel, alphaTextField, alphaErrorLabel
                    , smALabel, smATextField, smAErrorLabel
                    , smhLabel, smhTextField, smhErrorLabel
                    , smdLabel, smdTextField, smdErrorLabel);


        });

        method2TitleLabel.setOnMouseClicked(event1 -> {

            //Habilitamos los componetes del método 2
            method2GridLayout.getChildren().stream().forEach(item -> item.setDisable(false));
            // Desabilitamos los componentes del método 1
            method1GridLayout.getChildren().stream().filter(item -> !item.getStyleClass().contains(TITLE_LABEL)).forEach(item -> item.setDisable(true));

            // Habilitamos el campo smA.
            smAErrorLabel.setDisable(false);smATextField.setDisable(false);smALabel.setDisable(false);
            // Habilitamos al campo gamma pues la gente puede medir este dato ye s lo mas usual en lugar de usar trend and plunge.
            gammaLabel.setDisable(false);gammaTextField.setDisable(false);gammaErrorLabel.setDisable(false);gammaComboBox.setDisable(false);
            // Desabilitamos el campo alpha.
            alphaErrorLabel.setDisable(true);alphaTextField.setDisable(true);alphaErrorLabel.setDisable(true);

            // Modificamos el la malla de desplazamientos para agregar los campos correspondientes al método 2
            displacementGridLayout.getChildren().clear();
            displacementGridLayout.getChildren().addAll(alphaLabel, alphaTextField, alphaErrorLabel
                    , smALabel, smATextField, smAErrorLabel
                    , dABLabel, dABTextField, dABErrorLabel
                    , smBLabel, smBTextField, smBErrorLabel);
        });
        // Seleccionamos el método 2 por defecto.
        method2TitleLabel.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED,0,0,0,0, MouseButton.PRIMARY,1,true,true,true,true,true,true,true,true,true,true,null));
        //


        /** Calculamos */
        calculateButton.setOnMouseClicked(event -> {

            // Creamos un nuevo objeto de dato.
            TDData data = new TDData(statusPane);


            // Obtenemos si esta activo o inactivo el método1
            if(!method1GridLayout.getChildren().get(2).isDisable())
            {
                // Serie de comprobaciones de los datos. //TODO crear campos que verifiquen los datos de entrada.
                if (data.setBeta(betaTextField.getText(), betaErrorLabel.getText(), betaComboBox.getSelectionModel().getSelectedItem().toString())) {
                    if (data.setGamma(gammaTextField.getText(), gammaErrorLabel.getText(), gammaComboBox.getSelectionModel().getSelectedItem().toString())) {
                        if (data.setPhi(phiTextField.getText(), phiErrorLabel.getText(), phiComboBox.getSelectionModel().getSelectedItem().toString())) {
                            if (data.setApha(alphaTextField.getText(), alphaErrorLabel.getText())) {
                                if (data.setSmA(smATextField.getText(), smAErrorLabel.getText())) {
                                    if (data.setSmd(smdTextField.getText(), smdErrorLabel.getText())) {
                                        if (data.setSmh(smhTextField.getText(), smhErrorLabel.getText())) {
                                            if (data.setMapView((mapSectionToggleButton.isSelected()) ? mapSectionToggleButton.getText() : arbitraryLineToggleButton.getText())) {
                                                if (data.isDataValidForCalcuating()) {
                                                    if (data.Calculate(METHOD_1)) {
                                                        displayData(data);
                                                        tableTD.addData(data);
                                                    } else {shakeStage();}
                                                } else {shakeStage();}
                                            } else {shakeStage(); }
                                        } else {shakeStage();}
                                    } else {shakeStage();}
                                } else {shakeStage();}
                            } else {shakeStage();}
                        } else {shakeStage();}
                    } else {shakeStage();}
                } else {shakeStage();}
            }

            // Obtenemos si esta activo o inactivo el método2
            if(!method2GridLayout.getChildren().get(2).isDisable())
            {
                // Serie de comprobaciones de los datos de entrada.
                if(data.setFod(fodTextField.getText(),fodErrorLabel.getText()))
                {if(data.setFoStk(fostkTextField.getText(), fostkErrorLabel.getText()))
                        {if(data.setOpod(opodTextField.getText(),opodErrorLabel.getText()))
                            {if(data.setOpoStk(opostkTextField.getText(), opostkErrorLabel.getText()))
                                {if(data.setSmo1d(smo1dTextField.getText(),smo1dErrorLabel.getText()))
                                    {if(data.setSmo1Stk(smo1stkTextField.getText(), smo1stkErrorLabel.getText()))
                                        {if(data.setSmo2d(smo2dTextField.getText(),smo2dErrorLabel.getText()))
                                            {if(data.setSmo2Stk(smo2stkTextField.getText(), smo2stkErrorLabel.getText()))
                                                {if(data.setOsTrend(ostrendTextField.getText(),ostrendErrorLabel.getText()))
                                                    {if(data.setOsPlunge(osplungeTextField.getText(), osplungeErrorLabel.getText()))
                                                        {if(data.setGamma(gammaTextField.getText(),gammaErrorLabel.getText(),gammaComboBox.getSelectionModel().getSelectedItem().toString()))
                                                            {if(data.setSmA(smATextField.getText(), smAErrorLabel.getText()))
                                                                {if(data.setSmB(smBTextField.getText(),smBErrorLabel.getText()))
                                                                    {if(data.setdAB(dABTextField.getText(),dABErrorLabel.getText())) {
                                                                        if(data.setApha(alphaTextField.getText(),alphaErrorLabel.getText())) {
                                                                            if(data.Calculate(METHOD_2)) {
                                                                                    displayData(data);
                                                                                    tableTD.addData(data);
                                                                                }else{shakeStage();}
                                                                            }else{shakeStage();}
                                                                        }else{shakeStage();}
                                                                    }else{shakeStage();}
                                                                }else{shakeStage();}
                                                            }else{shakeStage();}
                                                        }else{shakeStage();}
                                                    }else{shakeStage();}
                                                }else{shakeStage();}
                                            }else{shakeStage();}
                                        }else{shakeStage();}
                                    }else{shakeStage();}
                                }else{shakeStage();}
                            }else{shakeStage();}
                        }else{shakeStage();}
                    }else{shakeStage();}
            }
        });


        /**Manejo de la tabla*/

        // Cuando se selecciona un elemento de la tabla se despliegan los datos de ese objeto.
        tableTD.getTable().setOnMouseClicked(event -> {
            if (tableTD.getTable().getSelectionModel().getSelectedItem() != null) {
                // Obtenemos el objeto de datos.
                TDData data = tableTD.getTable().getSelectionModel().getSelectedItem().getTDData();
                // Desplegamos los datos.
                displayData(data);
            }
        });


        /**Borrar contenido*/

        // Cuando se da click en el boton de reset.
        resetButton.setOnMouseClicked(event -> {
            // Limpiamos los componentes.
            clearcomponents();
            // Notificamos.
            statusPane.setStatus("Se ha Borrado la información");
        });



    }

    /** Función que mueve la pantalla para alertar */
    public void shakeStage() {

        // Objeto encargado de mover la pantalla.
        Timeline timelineX = new Timeline(new KeyFrame(Duration.seconds(0.1), t -> {
            if (shakeStageFlag) {
                // Movemos mas x
                mainTruDispStage.setX(mainTruDispStage.getX() + 10);
            } else {
                // Movemos menos x.
                mainTruDispStage.setX(mainTruDispStage.getX() - 10);
            }
            // Altermanos el movimiento al negar la bandera.
            shakeStageFlag = !shakeStageFlag;
        }));

        // Nuero de repeticiones.
        timelineX.setCycleCount(10);
        // Iniciamos la animación.
        timelineX.play();
    }

    /** Función que despliega los datos del objeto en los campos correspondientes de la aplicación. */
    private void displayData(TDData data) {

        // Seleccionamos el caso si es arbitrario o map and section.
        if(data.getMapView().equalsIgnoreCase("Arbitrary Line"))
        {arbitraryLineToggleButton.fire();}
        else
        {mapSectionToggleButton.fire();}

        // Mostramos los resultados
        sTextField.setText(data.getS().toString());
        sErrorLabel.setText("± " + data.getSError().toString());
        ssTextField.setText(data.getSs().toString());
        ssErrorLabel.setText("± " + data.getSsError().toString());
        sdTextField.setText(data.getSd().toString());
        sdErrorLabel.setText("± " + data.getSdError().toString());
        svTextField.setText(data.getSv().toString());
        svErrorLabel.setText("± " + data.getSvError().toString());
        shTextField.setText(data.getSh().toString());
        shErrorLabel.setText("± " + data.getShError().toString());

        thetaTextField.setText(data.getTheta().toString());
        thetanullTextField.setText(data.getThetaNull().toString());


        //  Datos de entrada
        betaErrorLabel.setText("± " + data.getBetaError() + " º");
        betaTextField.setText(data.getBeta().toString());
        betaComboBox.setValue(data.getBetaOrientation());

        gammaTextField.setText(data.getGamma().toString());
        gammaErrorLabel.setText("± " + data.getGammaError() + " º");
        gammaComboBox.setValue(data.getGammaOrientation());

        phiTextField.setText(data.getPhi().toString());
        phiErrorLabel.setText("± " + data.getPhiError() + " º");
        phiComboBox.setValue(data.getPhiOrientation());

        alphaTextField.setText(data.getAlpha().toString());
        alphaErrorLabel.setText("± " + data.getAlphaError() + " º");

        smATextField.setText(data.getSmA().toString());
        smAErrorLabel.setText("± " + data.getSmAError());

        smBTextField.setText(data.getSmB().toString());
        smBErrorLabel.setText("± " + data.getSmBError());

        dABTextField.setText(data.getdAB().toString());
        dABErrorLabel.setText("± " + data.getdABError());

        smdTextField.setText(data.getsmdError().toString());
        smdErrorLabel.setText("± " + data.getsmdError());

        smhTextField.setText(data.getSmh().toString());
        smhErrorLabel.setText("± " + data.getSmhError());

        fodTextField.setText(data.getFod().toString());
        fodErrorLabel.setText("± "+ data.getFodError()+" º");

        fostkTextField.setText(data.getFostk().toString());
        fostkErrorLabel.setText("± " + data.getFoddError() + " º");

        opodTextField.setText(data.getOpod().toString());
        opodErrorLabel.setText("± "+ data.getOpodError()+" º");

        opostkTextField.setText(data.getOpostk().toString());
        opostkErrorLabel.setText("± " + data.getOpoddError() + " º");

        smo1dTextField.setText(data.getSmo1d().toString());
        smo1dErrorLabel.setText("± "+ data.getSmo1dError()+" º");

        smo1stkTextField.setText(data.getSmo1stk().toString());
        smo1stkErrorLabel.setText("± " + data.getSmo1ddError() + " º");

        smo2dTextField.setText(data.getSmo2d().toString());
        smo2dErrorLabel.setText("± "+ data.getSmo2dError()+" º");

        smo2stkTextField.setText(data.getSmo2stk().toString());
        smo2stkErrorLabel.setText("± " + data.getSmo2ddError() + " º");

        ostrendTextField.setText(data.getOsTrend().toString());
        ostrendErrorLabel.setText("± "+ data.getOsTrendError()+" º");

        osplungeTextField.setText(data.getOsPlunch().toString());
        osplungeErrorLabel.setText("± " + data.getOsPlunchError() + " º");

        // Mostramos los cosenos directores de los planos.

        fplTextField.setText(data.getFpl().toString());
        fplErrorLabel.setText("± "+ data.getFplError());
        fpmTextField.setText(data.getFpm().toString());
        fpmErrorLabel.setText("± "+ data.getFpmError());
        fpnTextField.setText(data.getFpn().toString());
        fpnErrorLabel.setText("± "+ data.getFpnError());

        oplTextField.setText(data.getOpl().toString());
        oplErrorLabel.setText("± "+ data.getOplError());
        opmTextField.setText(data.getOpm().toString());
        opmErrorLabel.setText("± "+ data.getOpmError());
        opnTextField.setText(data.getOpn().toString());
        opnErrorLabel.setText("± "+ data.getOpnError());

        aplTextField.setText(data.getApl().toString());
        aplErrorLabel.setText("± "+ data.getAplError());
        apmTextField.setText(data.getApm().toString());
        apmErrorLabel.setText("± "+ data.getApmError());
        apnTextField.setText(data.getApn().toString());
        apnErrorLabel.setText("± "+ data.getApnError());

        bplTextField.setText(data.getBpl().toString());
        bplErrorLabel.setText("± "+ data.getBplError());
        bpmTextField.setText(data.getBpm().toString());
        bpmErrorLabel.setText("± "+ data.getBpmError());
        bpnTextField.setText(data.getBpn().toString());
        bpnErrorLabel.setText("± "+ data.getBpnError());

        // Mostramos los planos.
        faultViewerTD.setPlanes(data.getFaultPlane(), data.getPlaneA(), data.getPlaneB());
    }
}

class ValueChangeScrollListener implements EventHandler<ScrollEvent>
{

     private Label label;
     private Double value;
    private  int method;
     private TextField textfield;
     private String[] error;

    public ValueChangeScrollListener(Label l)
    {
        label = l;
        value=0.0;

    }

    public ValueChangeScrollListener(Label l,int met){label=l;method=met; value=0.0;}

    public ValueChangeScrollListener(TextField tf)
     {
        textfield = tf;
     }

    public ValueChangeScrollListener(TextField tf,int met){textfield=tf; method=met;}

    @Override
    public void handle(ScrollEvent event) {

        if (value != null)
        {
            // Eveneto de scroll que le el valor de la etiqueta y le aimenta el desplaamiento para los errores
                error = label.getText().split(" ");
            if(method==3)
            {value = (double) Math.round((Double.parseDouble(error[1]) - (event.getDeltaY()/Math.abs(event.getDeltaY()))*0.01) * 1000) / 1000;}
            else {value = (double) Math.round((Double.parseDouble(error[1]) - (event.getDeltaY()/Math.abs(event.getDeltaY()))*0.1) * 10) / 10;}

            if (value >= 0)
                {
                    if(error.length==3)
                    {label.setText(error[0]+" "+value.toString()+" "+error[2]);}
                    else
                    {label.setText(error[0]+" "+value.toString());}
                }

        }
        else
        {
            // Evento de scroll que lee el valor de la etiqueta y le aumenta el desplazamiento para los campos de entrada
            Double vl = 0.0;

            if (method == 3)
            {vl = (double) Math.round((Double.parseDouble(textfield.getText()) - (event.getDeltaY()/Math.abs(event.getDeltaY())/ 100)) * 1000) / 1000;}
            else{vl= (double) Math.round((Double.parseDouble(textfield.getText()) - (event.getDeltaY()/Math.abs(event.getDeltaY())/10)) * 10) / 10;}


            if (vl >= 0) {
                textfield.setText(vl.toString());
            }
        }


    }
}
class MethodsDisplayedChangeListener implements ChangeListener<Boolean>
{
    private GridPane methodGridLayout;
    private HBox methodsHBox;
    private Stage mainStage;
    private Integer methodSelected;

    public MethodsDisplayedChangeListener(Integer methodselected,GridPane method,HBox hbox, Stage stage)
    {
        methodGridLayout=method;
        methodsHBox=hbox;
        mainStage=stage;
        methodSelected = methodselected;
    }
    @Override
    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

        Integer heigth=700, width;


        if (oldValue)
        {
            // Eliminamos el método
            methodsHBox.getChildren().remove(methodGridLayout);

            switch (methodsHBox.getChildren().size()) {
                case 1: {
                    // Solo queda el principal
                    mainStage.setMinWidth(550);
                    mainStage.setWidth(550);
                    break;
                }
            }
        }
        else
        {
            // Agregamos el método
            methodsHBox.getChildren().add(methodGridLayout);
            switch (methodsHBox.getChildren().size())
            {
                case 2:{
                    mainStage.setMinWidth(750);
                    mainStage.setWidth(750);
                }
            }
        }
    }
}

class TDDialogs {

    private Stage stageinput,stageoutput,stageabout,stagelabels,stagehelp,stageLMNCoord;
    private ImageView im;
    private TextArea txtarea;

    public TDDialogs()
    {
        stageinput = new Stage(StageStyle.DECORATED);
        stageoutput = new Stage(StageStyle.DECORATED);
        stageabout = new Stage(StageStyle.DECORATED);
        stagelabels = new Stage(StageStyle.DECORATED);
        stagehelp = new Stage(StageStyle.DECORATED);
        stageLMNCoord = new Stage(StageStyle.DECORATED);
    }

    public void showInput()
    {
        im = new ImageView(new Image("Images/data.jpg"));
        im.fitWidthProperty().bind(stageinput.widthProperty());
        im.fitHeightProperty().bind(stageinput.heightProperty());
        im.setPreserveRatio(true);
        StackPane.setAlignment(im, Pos.CENTER);
        stageinput.setScene(new Scene(new StackPane(im)));
        stageinput.setTitle("Input Variables Diagram");
        stageinput.setWidth(500);
        stageinput.setHeight(500*(im.getImage().getHeight()/im.getImage().getWidth()));
        stageinput.show();
        stageinput.requestFocus();
    }
    public void showOutput()
    {
        im = new ImageView(new Image("Images/outcome.jpg"));
        im.fitWidthProperty().bind(stageoutput.widthProperty());
        im.fitHeightProperty().bind(stageoutput.heightProperty());
        im.setPreserveRatio(true);
        StackPane.setAlignment(im, Pos.CENTER);
        stageoutput.setScene(new Scene(new StackPane(im)));
        stageoutput.setTitle("Output Variables Diagram");
        stageoutput.setHeight(500 * (im.getImage().getHeight() / im.getImage().getWidth()));
        stageoutput.setWidth(500);
        stageoutput.show();
        stageoutput.requestFocus();
    }
    public void showAbout()
    {
        txtarea= new TextArea();
        txtarea.setWrapText(true);
        txtarea.setEditable(false);
        txtarea.setText(
                "© (2013) Nieto-Samaniego A. F. and Xu S.-S." +
                        "\n\nAuthored by:\n\nR. Nieto-Fuentes\n" +
                        "Universidad Nacional Autónoma de México, Centro de Física Aplicada y Tecnología Avanzada, " +
                        "Boulevard Juriquilla No. 3001, Querétaro, Qro., CP 76230, México.\n\n" +
                        "Nieto-Samaniego A. F., Xu S.-S., Alaniz-Alvarez, S. A.\n" +
                        "Universidad Nacional Autónoma de México, Centro de Geociencias, Boulevard Juriquilla No. 3001, Querétaro, Qro., CP 76230, México.\n\n" +
                        "TruDisp 1.0 is a program that calculates the true displacement (S) using the apparent displacement (Sm), which is measured from an arbitrary " +
                        "“observation line” on a fault plane. For the cases when Sm is unknown, the application uses apparent displacement along the dip line (Smd)." +
                        "\nSpecial cases are map view and transversal section; for these cases, the apparent displacement along the dip (Smd) or the strike (Smh) are used. " +
                        "The application does not consider the sense of movement and the obtained displacements are the absolute values of the vector displacement.\n\n" +
                        "The program analyses 18 different combinations of the input data. The theory was published in: \n\n" +
                        "Xu, S.-S., Velasquillo-Martinez, L. G., Grajales-Nishimura, J. M., Murillo-Muñetón, G., Nieto-Samaniego, A. F., 2007, " +
                        "Methods for quantitatively determining fault displacement using fault separation: Journal of Structural Geology, v. 29, p. 1709-1720.\n\n" +
                        "Xu, S.-S., Nieto-Samaniego, A. F., y Alaniz-Álvarez, S. A, 2009, Quantification of true displacement using apparent displacement along an " +
                        "arbitrary line on a fault plane: Tectonophysics, v 467, p. 107-118.\n\nTruDisp is a tool for applied geologists, students and academics. " +
                        "It was developed in Java SE 6.0.\n\nInput data\n\nThe application is designed for data obtained in the field, or from maps and sections. " +
                        "The measuring tool is usually a compass or protractor. The angular data is in decimal degrees format. " +
                        "β, γ and φ are angles measured on the fault plane. For that reason they must be in the same, or in two opposite quadrants. \n"
        );
        VBox.setVgrow(txtarea, Priority.ALWAYS);
        stageabout.setScene(new Scene(new VBox(txtarea)));
        stageabout.setTitle("About TruDisp");
        stageabout.setWidth(400);
        stageabout.setHeight(500);
        stageabout.centerOnScreen();
        stageabout.show();
        stageabout.requestFocus();
    }
    public void showLables()
    {
        txtarea=null;
        txtarea = new TextArea();
        txtarea.setWrapText(true);
        txtarea.setEditable(false);
        txtarea.setText("  θ :      180 - (  β + γ  )  or  (  β - γ  )\n" +
                "θnull:   180 - (  β + φ )  or  (  β + φ )\n  " +
                "β :      Pitch of the cut-off marker\n  " +
                "γ :      Pitch of the slip lineation (slickenline)\n  " +
                "φ:      Pitch of the observation line\n  " +
                "α :      Dip of the fault plane\n  " +
                "Sm:    Apparent displacement along the observation line\n  " +
                "Smd:  Apparent dip displacement (separation) along the dip line\n  " +
                "Smh:  Apparent dip displacement (separation) along the strike line\n  " +
                "S:       Total true displacement\n  " +
                "St:      Horizontal displacement (heave)\n  " +
                "Sv:     Vertical displacement (throw)\n  " +
                "Sd:     Dip-slip displacement\n  " +
                "Ss:     Strike-slip displacement");
        VBox.setVgrow(txtarea, Priority.ALWAYS);
        stagelabels.setScene(new Scene(new VBox(txtarea)));
        stagelabels.setTitle("Labels");
        stagelabels.setWidth(400);
        stagelabels.setHeight(400);
        stagelabels.show();
        stagelabels.requestFocus();
    }

    public void showHelp()
    {
        txtarea=null;
        txtarea = new TextArea();
        txtarea.setWrapText(true);
        txtarea.setEditable(false);

        txtarea.setText("The input fields activates according to selected option: “Map and Section” or “Arbitrary Line” " +
                "\n\nAngles are in degrees and decimals. Minutes and seconds are not used." +
                "\n\nAt least one of the displacements Sm, Smh or Smd must be introduced." +
                "\n\nThe model considers that the fault plane is divided into two halves by the line at which the dip is " +
                "measured; one half is located to the northerly strike direction, labeled \"northern half\" " +
                "(N) and the other is labeled \"shouther half\" (S). See the figure of input data. \n");

        VBox.setVgrow(txtarea, Priority.ALWAYS);
        stagehelp.setScene(new Scene(new VBox(txtarea)));
        stagehelp.setTitle("?");
        stagehelp.setWidth(400);
        stagehelp.setHeight(400);
        stagehelp.show();
        stagehelp.requestFocus();
    }
public void showLMNCoord()
{
    im = new ImageView(new Image("Images/lmn.jpg"));
    im.fitWidthProperty().bind(stageLMNCoord.widthProperty());
    im.fitHeightProperty().bind(stageLMNCoord.heightProperty());
    im.setPreserveRatio(true);
    StackPane.setAlignment(im, Pos.CENTER);
    stageLMNCoord.setScene(new Scene(new StackPane(im)));
    stageLMNCoord.setTitle("Output Variables Diagram");
    stageLMNCoord.setHeight(550 * (im.getImage().getHeight() / im.getImage().getWidth()));
    stageLMNCoord.setWidth(500);
    stageLMNCoord.show();
    stageLMNCoord.requestFocus();
}
}



