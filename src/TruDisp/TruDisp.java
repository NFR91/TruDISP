package TruDisp;

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
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;


/**
 * Created by Nieto on 29/07/15.
 */

// Inicio de la clase;
public class TruDisp extends Application {

    public static final String UPDATE_TXT = "http://nfr91.github.io/Updater/TruDisp/TruDispVersions.txt";
    public static final Double TRU_DISP_VERSION = 1.93;
    public static final String REPOSITORY ="beta";
    /** Variables*/


    /**
     * Constantes de la interfaz
     */

    private Stage mainTruDispStage;
    private Scene mainScene;
    private BorderPane mainBorderLayout;
    private VBox mainVBox;
    private HBox methodsHBox;
    private MenuBar menuBar;
    private Menu trudispMenu, methodsMenu, panelsMenu,helpMenu;
    private MenuItem trudispMenuCloseItem,trudispMenuSaveItem,trudispMenuOpenItem,panelMenuHistoryItem,
            panelMenuOutputItem,panelMenuInputItem,panelMenuLabelsItems,trudispMenuAboutItem,
            helpMenuHow2Use,panelMenuFaultViewrItem,panelMenuLMNCoordItem;
    private CheckMenuItem method1CheckMenuItem, method2CheckMenuItem, method3CheckMenuItem;

    private TDTable TDTable;
    private FaultViewer TDFaultV;
    private TDOpenSave truDispOpenSave;
    private Boolean shakeStageFlag = true;
    private TDDialogs trudispTDDialogs;


    // Método 1;
    private GridPane method1GridLayout;
    private Label betaLabel, phiLabel, gammaLabel;
    private Label betaErrorLabel, phiErrorLabel, gammaErrorLabel;
    private Label method1TitleLabel;
    private TextField betaTextField, phiTextField, gammaTextField;
    private ComboBox betaComboBox, gammaComboBox, phiComboBox;
    private ToggleButton mapSectionToggleButton, arbitraryLineToggleButton;


    // Método 2;
    private GridPane method2GridLayout;
    private Label method2TitleLabel;
    private Label foLabel, opoLabel, smoLabel, osLabel;
    private Label fodLabel, foddLabel, opodLabel, opoddLabel, smo1dLabel, smo1ddLabel, smo2dLabel, smo2ddLabel, ostrendLabel, osplunchLabel;
    private Label fodErrorLabel, foddErrorLabel, opodErrorLabel, opoddErrorLabel, smo1dErrorLabel, smo1ddErrorLabel,
            smo2dErrorLabel, smo2ddErrorLabel, ostrendErrorLabel, osplunchErrorLabel;
    private TextField fodTextField, foddTextField, opodTextField, opoddTextField, smo1dTextField, smo1ddTextField,
            smo2dTextField, smo2ddTextField, ostrendTextField, osplunchTextField;

    // Método 3;

    private GridPane method3GridLayout;
    private Label method3TitleLabel;
    private Label fpLabel, opLabel, apLabel, bpLabel;
    private Label fpmLabel, fpnLabel, fplLabel, opmLabel, opnLabel, oplLabel, apmLabel, apnLabel, aplLabel, bpmLabel, bpnLabel, bplLabel;
    private Label fpmErrorLabel, fpnErrorLabel, fplErrorLabel, opmErrorLabel, opnErrorLabel, oplErrorLabel, apmErrorLabel,
            apnErrorLabel, aplErrorLabel, bpmErrorLabel, bpnErrorLabel, bplErrorLabel;
    private TextField fpmTextField, fpnTextField, fplTextField, opmTextField, opnTextField, oplTextField, apmTextField, apnTextField,
            aplTextField, bpmTextField, bpnTextField, bplTextField;


    // Distancias

    private GridPane displacementGridLayout;
    private Label alphaLabel, smLabel, smhLabel, smdLabel;
    private Label alphaErrorLabel, smErrorLabel, smhErrorLabel, smdErrorLabel;
    private TextField alphaTextField, smTextField, smhTextField, smdTextField;

    // Resultados

    private GridPane resultGridLayout;
    private Label resultTitleLabel;
    private Label sLabel, svLabel, sdLabel, shLabel, ssLabel, thetaLabel, thetanullLabel;
    private Label sErrorLabel, svErrorLabel, sdErrorLabel, shErrorLabel, ssErrorLabel;
    private TextField sTextField, svTextField, sdTextField, shTextField, ssTextField, thetaTextField, thetanullTextField;
    private Button calculateButton,resetButton;
    private TruDispStatusPane statusPane;


    // SplashScreen
    private static final String SPLASH_IMAGE = "Images/splashimage.png";
    private final int SPLASH_WIDTH = 500;


    /**
     * Constantes
     */

    // Styles
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

    // Methods
    public static final Integer METHOD_1 = 1, METHOD_2 = 2, METHOD_3 = 3;
    public static final Integer N=0,E=1,D=2;

    // Icons;
    public static final String WELCOME_ICON = "Icons/welcomeicon.png";
    public static final String ADVICE_ICON = "Icons/adviceicon.png";
    public static final String CALCULATING_ICON = "Icons/calculatingicon.png";
    public static final String WARNING_ICON = WELCOME_ICON;
    public static final String ERROR_ICON = WELCOME_ICON;


    @Override
    public void start(Stage mainStage) throws InterruptedException {

        /**Splash Screen*/
        mainStage = new Stage(StageStyle.TRANSPARENT);

        // Creamos el contenedor de la imagen de splash;
        ImageView splashImageView = new ImageView(new Image(SPLASH_IMAGE));
        splashImageView.setFitWidth(SPLASH_WIDTH);
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

        Scene splashScene = new Scene(splashVBoxLayout);
        splashScene.getStylesheets().add(TRUDISP_STYLE_SHEET);

        splashScene.setFill(Color.WHITE);
        splashVBoxLayout.setBackground(Background.EMPTY);


        mainStage.setScene(splashScene);
        mainStage.show();

        // Revisamos si hay actualizaciones.
        update(splashProgressBar,splashProgressText,mainStage);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }


    private void update(ProgressBar splashProgressBar, Label splashProgressText,Stage stage) {

        MyUpdater up = new MyUpdater(splashProgressBar,splashProgressText,stage);
        up.getIsreadyProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue)
            {
                showMainStage();
            }
        });

        up.updateApp();

    }


    private void showMainStage() {
        this.initcomponents();

        // Titulo
        mainTruDispStage.setTitle("TruDISP" + TRU_DISP_VERSION);

        // Agregamos el contenido
        mainTruDispStage.setScene(mainScene);
        mainTruDispStage.setMinHeight(700);
        mainTruDispStage.setHeight(700);
        mainTruDispStage.setMinWidth(550);
        mainTruDispStage.setWidth(550);


        mainTruDispStage.setX((Screen.getPrimary().getVisualBounds().getWidth() / 2) - (500));
        mainTruDispStage.setY((Screen.getPrimary().getVisualBounds().getHeight() / 2) - (mainTruDispStage.getHeight() / 2));

        mainTruDispStage.show();

    }




    /** Iniciamos los componentes  */

    private void initcomponents() {
        mainTruDispStage = new Stage(StageStyle.DECORATED);
        mainTruDispStage.setOnCloseRequest(event1 -> {System.exit(0);});

        /**Dialogos*/
        // Historial
        TDTable = new TDTable();
        // Guardado y apertura
        truDispOpenSave = new TDOpenSave(mainTruDispStage);
        // Dialogos de entradas, salidas e información
        trudispTDDialogs = new TDDialogs();
        // Visor;
        TDFaultV = new FaultViewer();

        /** Cuerpo de la aplicación*/
        // Cuerpo Principal
        mainBorderLayout = new BorderPane();
        // Contenedor de metodos, distancias y resultados, asi como de la barra de estado
        mainVBox = new VBox();
        mainVBox.getStyleClass().add(IO_VBOX);
        // Cotenedor expandible de métodos
        methodsHBox = new HBox();
        methodsHBox.getStyleClass().add(IO_HBOX);
        // Barra de estado
        statusPane = new TruDispStatusPane();
        statusPane.setStatus("Bienvenido a TruDISP", WELCOME_ICON);

        /** Método 1 */
        initMethod1ComponentsGrid();
        /** Método 2 */
        initMethod2ComponentsGrid();
        /**Método 3*/
        initMethod3ComponentsGrid();
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
        HBox.setHgrow(method3GridLayout, Priority.SOMETIMES);
        VBox.setVgrow(displacementGridLayout, Priority.ALWAYS);
        VBox.setVgrow(methodsHBox, Priority.ALWAYS);
        // Resultados
        VBox.setVgrow(resultGridLayout, Priority.ALWAYS);

        /** Establemcemos los componentes en sus contenedres*/

        // Agregamos los métodos
        methodsHBox.getChildren().addAll(method1GridLayout);
        // Agregamos los métodos y los desplazamientos
        mainVBox.getChildren().addAll(methodsHBox, displacementGridLayout, resultGridLayout);

        statusPane.setStatusPane(mainVBox);

        // Agregamos la sección central de la aplicación
        mainBorderLayout.setCenter(statusPane);


        // Agregamos a la escena.
        mainScene = new Scene(mainBorderLayout);
        mainScene.getStylesheets().add(TRUDISP_STYLE_SHEET);

        /**Barra de menú*/
        initMenuBar();

        /**Colocamos las acciones de los diferentes botones de la interface*/
        initGUIActions();
        statusPane.setStatus(".... Bienvenido a TruDisp", WELCOME_ICON);
    }

    private  void initMenuBar()
    {
        /** Barra de menu */
        menuBar = new MenuBar();

        // Menú
        trudispMenu = new Menu("TruDisp");
        trudispMenuCloseItem = new MenuItem("Exit");
        trudispMenuCloseItem.setOnAction(event -> System.exit(0));
        trudispMenuSaveItem = new MenuItem("Save Session");
        trudispMenuSaveItem.setOnAction(event -> { truDispOpenSave.Save(TDTable.getDataList());} );
        trudispMenuOpenItem = new MenuItem("Open Session");
        trudispMenuOpenItem.setOnAction(even-> TDTable.setObservableList(truDispOpenSave.Open(statusPane)));
        trudispMenuAboutItem = new MenuItem("About");
        trudispMenuAboutItem.setOnAction(event -> trudispTDDialogs.showAbout());
        trudispMenu.getItems().addAll(trudispMenuAboutItem, trudispMenuOpenItem, trudispMenuSaveItem, trudispMenuCloseItem);

        // Métodos
        methodsMenu = new Menu("Methods");
        method2CheckMenuItem = new CheckMenuItem("Method 2");
        method2CheckMenuItem.selectedProperty().addListener(new MethodsDisplayedChangeListener(2, method2GridLayout, methodsHBox, mainTruDispStage));
        method3CheckMenuItem = new CheckMenuItem("Method 3");
        method3CheckMenuItem.selectedProperty().addListener(new MethodsDisplayedChangeListener(3, method3GridLayout, methodsHBox, mainTruDispStage));
        methodsMenu.getItems().addAll(method2CheckMenuItem, method3CheckMenuItem);

        // Paneles.
        panelsMenu = new Menu("Panels");
        panelMenuHistoryItem = new MenuItem("History");
        panelMenuHistoryItem.setOnAction(event -> TDTable.show());
        panelMenuInputItem = new MenuItem("Input");
        panelMenuInputItem.setOnAction(event-> trudispTDDialogs.showInput());
        panelMenuOutputItem = new MenuItem("Output");
        panelMenuOutputItem.setOnAction(event-> trudispTDDialogs.showOutput());
        panelMenuLabelsItems = new MenuItem("Labels");
        panelMenuLabelsItems.setOnAction(event -> trudispTDDialogs.showLables());
        panelMenuFaultViewrItem = new MenuItem("Fault Viewer");
        panelMenuFaultViewrItem.setOnAction(event-> {TDFaultV.show();TDFaultV.requestFocus();});
        panelMenuLMNCoordItem = new MenuItem("Director Cosines");
        panelMenuLMNCoordItem.setOnAction(event->trudispTDDialogs.showLMNCoord());
        panelsMenu.getItems().addAll(panelMenuHistoryItem,panelMenuFaultViewrItem, panelMenuLabelsItems, panelMenuInputItem, panelMenuOutputItem,panelMenuLMNCoordItem);

        // ?
        helpMenu = new Menu("Help");
        helpMenuHow2Use = new MenuItem("How2Use");
        helpMenuHow2Use.setOnAction(event->trudispTDDialogs.showHelp());
        helpMenu.getItems().addAll(helpMenuHow2Use);

        // Añadimos los menus.
        menuBar.getMenus().addAll(trudispMenu, methodsMenu, panelsMenu,helpMenu);
        mainBorderLayout.setTop(menuBar);


    }

    private void initMethod1ComponentsGrid() {

        /** Etiquetas del método uno*/

        //Titulo
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

        foddLabel = new Label("Stk");
        foddLabel.getStyleClass().add(IO_LABEL);
        foddLabel.setTooltip(new Tooltip("DipDirection"));


        fodErrorLabel = new Label();
        fodErrorLabel.setOnScroll(new ValueChangeScrollListener(fodErrorLabel));
        fodErrorLabel.setTooltip(new Tooltip("Dip Error"));
        fodErrorLabel.getStyleClass().add(IO_ERROR_LABEL);


        foddErrorLabel = new Label();
        foddErrorLabel.setOnScroll(new ValueChangeScrollListener(foddErrorLabel));
        foddErrorLabel.setTooltip(new Tooltip("Dip Direction Error"));
        foddErrorLabel.getStyleClass().add(IO_ERROR_LABEL);

        // Observation Plane Orientation
        opoLabel = new Label("Observation Plane Orientation");
        opoLabel.getStyleClass().add(SUB_TITLE_LABEL);

        opodLabel = new Label("D");
        opodLabel.getStyleClass().add(IO_LABEL);
        opodLabel.setTooltip(new Tooltip("Dip"));

        opoddLabel = new Label("Stk");
        opoddLabel.getStyleClass().add(IO_LABEL);
        opoddLabel.setTooltip(new Tooltip("Dip Direction"));


        opodErrorLabel = new Label();
        opodErrorLabel.setOnScroll(new ValueChangeScrollListener(opodErrorLabel));
        opodErrorLabel.setTooltip(new Tooltip("Dip Error"));
        opodErrorLabel.getStyleClass().add(IO_ERROR_LABEL);


        opoddErrorLabel = new Label();
        opoddErrorLabel.setOnScroll(new ValueChangeScrollListener(opoddErrorLabel));
        opoddErrorLabel.setTooltip(new Tooltip("Dip Direction Error"));
        opoddErrorLabel.getStyleClass().add(IO_ERROR_LABEL);

        // Statigraphic marker orientation

        smoLabel = new Label("Stratigraphic Marker Orientation");
        smoLabel.getStyleClass().add(SUB_TITLE_LABEL);

        smo1dLabel = new Label("D");
        smo1dLabel.getStyleClass().add(IO_LABEL);
        smo1dLabel.setTooltip(new Tooltip("Dip"));

        smo1ddLabel = new Label("Stk");
        smo1ddLabel.getStyleClass().add(IO_LABEL);
        smo1ddLabel.setTooltip(new Tooltip("Dip Direction"));


        smo1dErrorLabel = new Label();
        smo1dErrorLabel.setOnScroll(new ValueChangeScrollListener(smo1dErrorLabel));
        smo1dErrorLabel.setTooltip(new Tooltip("Marker 1 Dip Error"));
        smo1dErrorLabel.getStyleClass().add(IO_ERROR_LABEL);


        smo1ddErrorLabel = new Label();
        smo1ddErrorLabel.setOnScroll(new ValueChangeScrollListener(smo1ddErrorLabel));
        smo1ddErrorLabel.setTooltip(new Tooltip("Marker 1 Dip Direction Error"));
        smo1ddErrorLabel.getStyleClass().add(IO_ERROR_LABEL);

        smo2dLabel = new Label("D");
        smo2dLabel.getStyleClass().add(IO_LABEL);
        smo2dLabel.setTooltip(new Tooltip("Dip"));

        smo2ddLabel = new Label("Stk");
        smo2ddLabel.getStyleClass().add(IO_LABEL);
        smo2ddLabel.setTooltip(new Tooltip("Dip Direction"));


        smo2dErrorLabel = new Label();
        smo2dErrorLabel.setOnScroll(new ValueChangeScrollListener(smo2dErrorLabel));
        smo2dErrorLabel.setTooltip(new Tooltip("Marker 2 Dip Error"));
        smo2dErrorLabel.getStyleClass().add(IO_ERROR_LABEL);


        smo2ddErrorLabel = new Label();
        smo2ddErrorLabel.setOnScroll(new ValueChangeScrollListener(smo2ddErrorLabel));
        smo2ddErrorLabel.setTooltip(new Tooltip("Marker 2 Dip Direction Error"));
        smo2ddErrorLabel.getStyleClass().add(IO_ERROR_LABEL);

        // Orientation of Striae;
        osLabel = new Label("Orientation Of Striae");
        osLabel.getStyleClass().add(SUB_TITLE_LABEL);

        ostrendLabel = new Label("Trnd");
        ostrendLabel.getStyleClass().add(IO_LABEL);
        ostrendLabel.setTooltip(new Tooltip("Trend"));

        osplunchLabel = new Label("Plng");
        osplunchLabel.getStyleClass().add(IO_LABEL);
        osplunchLabel.setTooltip(new Tooltip("Plng"));


        ostrendErrorLabel = new Label();
        ostrendErrorLabel.setOnScroll(new ValueChangeScrollListener(ostrendErrorLabel));
        ostrendErrorLabel.setTooltip(new Tooltip("Trend Error"));
        ostrendErrorLabel.getStyleClass().add(IO_ERROR_LABEL);


        osplunchErrorLabel = new Label();
        osplunchErrorLabel.setOnScroll(new ValueChangeScrollListener(osplunchErrorLabel));
        osplunchErrorLabel.setTooltip(new Tooltip("Plunch D Error"));
        osplunchErrorLabel.getStyleClass().add(IO_ERROR_LABEL);


        /**Text Fields*/

        // FaultOrientation
        fodTextField = new TextField();
        fodTextField.setOnScroll(new ValueChangeScrollListener(fodTextField));
        fodTextField.getStyleClass().add(IO_TEXT_FIELD);
        fodTextField.setTooltip(new Tooltip("Fault Dip Input"));

        foddTextField = new TextField();
        foddTextField.setOnScroll(new ValueChangeScrollListener(foddTextField));
        foddTextField.getStyleClass().add(IO_TEXT_FIELD);
        foddTextField.setTooltip(new Tooltip("Fault Dip Direction Input"));

        // Observation Plane Orientation

        opodTextField = new TextField();
        opodTextField.setOnScroll(new ValueChangeScrollListener(opodTextField));
        opodTextField.getStyleClass().add(IO_TEXT_FIELD);
        opodTextField.setTooltip(new Tooltip("Observation Plane Dip Input"));

        opoddTextField = new TextField();
        opoddTextField.setOnScroll(new ValueChangeScrollListener(opoddTextField));
        opoddTextField.getStyleClass().add(IO_TEXT_FIELD);
        opoddTextField.setTooltip(new Tooltip("Observation Plane Dip DDirection Input"));

        // Statigraphic marker orientation

        smo1dTextField = new TextField();
        smo1dTextField.setOnScroll(new ValueChangeScrollListener(smo1dTextField));
        smo1dTextField.getStyleClass().add(IO_TEXT_FIELD);
        smo1dTextField.setTooltip(new Tooltip("Marker 1 Dip Input"));

        smo1ddTextField = new TextField();
        smo1ddTextField.setOnScroll(new ValueChangeScrollListener(smo1ddTextField));
        smo1ddTextField.getStyleClass().add(IO_TEXT_FIELD);
        smo1ddTextField.setTooltip(new Tooltip("Marker 1 Dip Direction Input"));

        smo2dTextField = new TextField();
        smo2dTextField.setOnScroll(new ValueChangeScrollListener(smo2dTextField));
        smo2dTextField.getStyleClass().add(IO_TEXT_FIELD);
        smo2dTextField.setTooltip(new Tooltip("Marker 2 Dip Input"));

        smo2ddTextField = new TextField();
        smo2ddTextField.setOnScroll(new ValueChangeScrollListener(smo2ddTextField));
        smo2ddTextField.getStyleClass().add(IO_TEXT_FIELD);
        smo2ddTextField.setTooltip(new Tooltip("Marker 2 Dip Direction Input"));

        // Orientation of Striae

        ostrendTextField = new TextField();
        ostrendTextField.setOnScroll(new ValueChangeScrollListener(ostrendTextField));
        ostrendTextField.getStyleClass().add(IO_TEXT_FIELD);
        ostrendTextField.setTooltip(new Tooltip("Trend Input"));

        osplunchTextField = new TextField();
        osplunchTextField.setOnScroll(new ValueChangeScrollListener(osplunchTextField));
        osplunchTextField.getStyleClass().add(IO_TEXT_FIELD);
        osplunchTextField.setTooltip(new Tooltip("Plunch Input"));


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
        GridPane.setConstraints(foddLabel, 0, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);
        GridPane.setConstraints(foddTextField, 1, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(foddErrorLabel, 2, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(fodLabel, 3, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);
        GridPane.setConstraints(fodTextField, 4, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(fodErrorLabel, 5, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);


        // Observation Plane Orientation
        row = 3;
        GridPane.setConstraints(opoLabel, 0, row, 6, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
        row = 4;
        GridPane.setConstraints(opoddLabel, 0, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);
        GridPane.setConstraints(opoddTextField, 1, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(opoddErrorLabel, 2, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(opodLabel, 3, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);
        GridPane.setConstraints(opodTextField, 4, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(opodErrorLabel, 5, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);


        // SMO
        row = 5;
        GridPane.setConstraints(smoLabel, 0, row, 6, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
        row = 6;
        GridPane.setConstraints(smo1ddLabel, 0, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);
        GridPane.setConstraints(smo1ddTextField, 1, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(smo1ddErrorLabel, 2, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(smo1dLabel, 3, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);
        GridPane.setConstraints(smo1dTextField, 4, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(smo1dErrorLabel, 5, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);

        row = 7;
        GridPane.setConstraints(smo2ddLabel, 0, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);
        GridPane.setConstraints(smo2ddTextField, 1, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(smo2ddErrorLabel, 2, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
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
        GridPane.setConstraints(osplunchLabel, 3, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);
        GridPane.setConstraints(osplunchTextField, 4, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(osplunchErrorLabel, 5, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);



        method2GridLayout.getChildren().addAll(method2TitleLabel
                , foLabel, fodLabel, fodTextField, fodErrorLabel, foddLabel, foddTextField, foddErrorLabel
                , opoLabel, opodLabel, opodTextField, opodErrorLabel, opoddLabel, opoddTextField, opoddErrorLabel
                , smoLabel, smo1dLabel, smo1dTextField, smo1dErrorLabel, smo1ddLabel, smo1ddTextField, smo1ddErrorLabel
                , smo2dLabel, smo2dTextField, smo2dErrorLabel, smo2ddLabel, smo2ddTextField, smo2ddErrorLabel
                , osLabel, ostrendLabel, ostrendTextField, ostrendErrorLabel, osplunchLabel, osplunchTextField, osplunchErrorLabel
        );

        method2GridLayout.getStyleClass().add(IO_GRID_PANE);

    }

    private void initMethod3ComponentsGrid() {
        /**Labels*/

        // Title
        method3TitleLabel = new Label("Method 3");
        method3TitleLabel.getStyleClass().add(TITLE_LABEL);
        method3TitleLabel.setMaxWidth(Double.MAX_VALUE);

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
        fpnErrorLabel.setOnScroll(new ValueChangeScrollListener(fpnErrorLabel,METHOD_3));
        fpnErrorLabel.setTooltip(new Tooltip("Error in measurement of n"));
        fpnErrorLabel.getStyleClass().add(IO_ERROR_LABEL);


        fpmErrorLabel = new Label();
        fpmErrorLabel.setOnScroll(new ValueChangeScrollListener(fpmErrorLabel,METHOD_3));
        fpmErrorLabel.setTooltip(new Tooltip("Error in measurement of m"));
        fpmErrorLabel.getStyleClass().add(IO_ERROR_LABEL);


        fplErrorLabel = new Label();
        fplErrorLabel.setOnScroll(new ValueChangeScrollListener(fplErrorLabel,METHOD_3));
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
        opnErrorLabel.setOnScroll(new ValueChangeScrollListener(opnErrorLabel,METHOD_3));
        opnErrorLabel.setTooltip(new Tooltip("Error in measurement of n"));
        opnErrorLabel.getStyleClass().add(IO_ERROR_LABEL);


        opmErrorLabel = new Label();
        opmErrorLabel.setOnScroll(new ValueChangeScrollListener(opmErrorLabel,METHOD_3));
        opmErrorLabel.setTooltip(new Tooltip("Error in measurement of m"));
        opmErrorLabel.getStyleClass().add(IO_ERROR_LABEL);


        oplErrorLabel = new Label();
        oplErrorLabel.setOnScroll(new ValueChangeScrollListener(oplErrorLabel,METHOD_3));
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
        apnErrorLabel.setOnScroll(new ValueChangeScrollListener(apnErrorLabel,METHOD_3));
        apnErrorLabel.setTooltip(new Tooltip("Error in measurement of n"));
        apnErrorLabel.getStyleClass().add(IO_ERROR_LABEL);


        apmErrorLabel = new Label();
        apmErrorLabel.setOnScroll(new ValueChangeScrollListener(apmErrorLabel,METHOD_3));
        apmErrorLabel.setTooltip(new Tooltip("Error in measurement of m"));
        apmErrorLabel.getStyleClass().add(IO_ERROR_LABEL);


        aplErrorLabel = new Label();
        aplErrorLabel.setOnScroll(new ValueChangeScrollListener(aplErrorLabel,METHOD_3));
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
        bpnErrorLabel.setOnScroll(new ValueChangeScrollListener(bpnErrorLabel,METHOD_3));
        bpnErrorLabel.setTooltip(new Tooltip("Error in measurement of n"));
        bpnErrorLabel.getStyleClass().add(IO_ERROR_LABEL);


        bpmErrorLabel = new Label();
        bpmErrorLabel.setOnScroll(new ValueChangeScrollListener(bpmErrorLabel,METHOD_3));
        bpmErrorLabel.setTooltip(new Tooltip("Error in measurement of m"));
        bpmErrorLabel.getStyleClass().add(IO_ERROR_LABEL);


        bplErrorLabel = new Label();
        bplErrorLabel.setOnScroll(new ValueChangeScrollListener(bplErrorLabel,METHOD_3));
        bplErrorLabel.setTooltip(new Tooltip("Error in measurement of l"));
        bplErrorLabel.getStyleClass().add(IO_ERROR_LABEL);

        /**Text Field*/

        // F Plane
        fpmTextField = new TextField();
        fpmTextField.setOnScroll(new ValueChangeScrollListener(fpmTextField,METHOD_3));
        fpmTextField.getStyleClass().add(IO_TEXT_FIELD);
        fpmTextField.setTooltip(new Tooltip("FOD D input"));

        fpnTextField = new TextField();
        fpnTextField.setOnScroll(new ValueChangeScrollListener(fpnTextField,METHOD_3));
        fpnTextField.getStyleClass().add(IO_TEXT_FIELD);
        fpnTextField.setTooltip(new Tooltip("FOD DD input"));

        fplTextField = new TextField();
        fplTextField.setOnScroll(new ValueChangeScrollListener(fplTextField,METHOD_3));
        fplTextField.getStyleClass().add(IO_TEXT_FIELD);
        fplTextField.setTooltip(new Tooltip("FOD DD input"));

        // O Plane
        opmTextField = new TextField();
        opmTextField.setOnScroll(new ValueChangeScrollListener(opmTextField,METHOD_3));
        opmTextField.getStyleClass().add(IO_TEXT_FIELD);
        opmTextField.setTooltip(new Tooltip("FOD D input"));

        opnTextField = new TextField();
        opnTextField.setOnScroll(new ValueChangeScrollListener(opnTextField,METHOD_3));
        opnTextField.getStyleClass().add(IO_TEXT_FIELD);
        opnTextField.setTooltip(new Tooltip("FOD DD input"));

        oplTextField = new TextField();
        oplTextField.setOnScroll(new ValueChangeScrollListener(oplTextField,METHOD_3));
        oplTextField.getStyleClass().add(IO_TEXT_FIELD);
        oplTextField.setTooltip(new Tooltip("FOD DD input"));

        // A Plane
        apmTextField = new TextField();
        apmTextField.setOnScroll(new ValueChangeScrollListener(apmTextField,METHOD_3));
        apmTextField.getStyleClass().add(IO_TEXT_FIELD);
        apmTextField.setTooltip(new Tooltip("FOD D input"));

        apnTextField = new TextField();
        apnTextField.setOnScroll(new ValueChangeScrollListener(apnTextField,METHOD_3));
        apnTextField.getStyleClass().add(IO_TEXT_FIELD);
        apnTextField.setTooltip(new Tooltip("FOD DD input"));

        aplTextField = new TextField();
        aplTextField.setOnScroll(new ValueChangeScrollListener(aplTextField,METHOD_3));
        aplTextField.getStyleClass().add(IO_TEXT_FIELD);
        aplTextField.setTooltip(new Tooltip("FOD DD input"));

        // B Plane
        bpmTextField = new TextField();
        bpmTextField.setOnScroll(new ValueChangeScrollListener(bpmTextField,METHOD_3));
        bpmTextField.getStyleClass().add(IO_TEXT_FIELD);
        bpmTextField.setTooltip(new Tooltip("FOD D input"));

        bpnTextField = new TextField();
        bpnTextField.setOnScroll(new ValueChangeScrollListener(bpnTextField,METHOD_3));
        bpnTextField.getStyleClass().add(IO_TEXT_FIELD);
        bpnTextField.setTooltip(new Tooltip("FOD DD input"));

        bplTextField = new TextField();
        bplTextField.setOnScroll(new ValueChangeScrollListener(bplTextField,METHOD_3));
        bplTextField.getStyleClass().add(IO_TEXT_FIELD);
        bplTextField.setTooltip(new Tooltip("FOD DD input"));

        /** Malla */

        method3GridLayout = new GridPane();
        method3GridLayout.getStyleClass().add(IO_GRID_PANE);

        // Title
        Integer row = 0;
        GridPane.setConstraints(method3TitleLabel, 0, row, 9, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);

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

        method3GridLayout.getChildren().addAll(
                method3TitleLabel
                , fpLabel, fplLabel, fplTextField, fplErrorLabel, fpmLabel, fpmTextField, fpmErrorLabel, fpnLabel, fpnTextField, fpnErrorLabel
                , opLabel, oplLabel, oplTextField, oplErrorLabel, opmLabel, opmTextField, opmErrorLabel, opnLabel, opnTextField, opnErrorLabel
                , apLabel, aplLabel, aplTextField, aplErrorLabel, apmLabel, apmTextField, apmErrorLabel, apnLabel, apnTextField, apnErrorLabel
                , bpLabel, bplLabel, bplTextField, bplErrorLabel, bpmLabel, bpmTextField, bpmErrorLabel, bpnLabel, bpnTextField, bpnErrorLabel
        );
    }

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

        // Sm;
        smLabel = new Label("Sm");
        smLabel.getStyleClass().add(IO_LABEL);
        smLabel.setTooltip(new Tooltip("Apparent displacement along observation line"));


        smErrorLabel = new Label();
        smErrorLabel.getStyleClass().add(IO_ERROR_LABEL);
        smErrorLabel.setOnScroll(new ValueChangeScrollListener(smErrorLabel));
        smErrorLabel.setTooltip(new Tooltip("Error in measurement of Sm"));

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

        // Sm
        smTextField = new TextField();
        smTextField.setOnScroll(new ValueChangeScrollListener(smTextField));
        smTextField.getStyleClass().add(IO_TEXT_FIELD);
        smTextField.setTooltip(new Tooltip("Sm input"));

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


        /**Malla*/

        displacementGridLayout = new GridPane();
        displacementGridLayout.getStyleClass().add(IO_GRID_PANE);


        Integer row = 0;
        GridPane.setConstraints(alphaLabel, 0, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);
        GridPane.setConstraints(alphaTextField, 1, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(alphaErrorLabel, 2, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);

        GridPane.setConstraints(smLabel, 3, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);
        GridPane.setConstraints(smTextField, 4, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(smErrorLabel, 5, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);

        row = 1;

        GridPane.setConstraints(smhLabel, 0, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);
        GridPane.setConstraints(smhTextField, 1, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(smhErrorLabel, 2, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);

        GridPane.setConstraints(smdLabel, 3, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);
        GridPane.setConstraints(smdTextField, 4, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(smdErrorLabel, 5, row, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);


        displacementGridLayout.getChildren().addAll(
                alphaLabel, alphaTextField, alphaErrorLabel
                , smLabel, smTextField, smErrorLabel
                , smhLabel, smhTextField, smhErrorLabel
                , smdLabel, smdTextField, smdErrorLabel
        );


    }

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

        resultGridLayout.getChildren().addAll(
                resultTitleLabel
                , thetaLabel, thetaTextField, thetanullLabel, thetanullTextField
                , sLabel, sTextField, sErrorLabel
                , svLabel, svTextField, svErrorLabel
                , ssLabel, ssTextField, ssErrorLabel
                , shLabel, shTextField, shErrorLabel
                , sdLabel, sdTextField, sdErrorLabel
                , calculateButton,resetButton
        );


    }

    private void clearcomponents() {
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
        foddErrorLabel.setText(angleErrorString);
        opodErrorLabel.setText(angleErrorString);
        opoddErrorLabel.setText(angleErrorString);
        smo1dErrorLabel.setText(angleErrorString);
        smo1ddErrorLabel.setText(angleErrorString);
        smo2dErrorLabel.setText(angleErrorString);
        smo2ddErrorLabel.setText(angleErrorString);
        ostrendErrorLabel.setText(angleErrorString);
        osplunchErrorLabel.setText(angleErrorString);

        fodTextField.setText(defaultValue);
        foddTextField.setText(defaultValue);
        opodTextField.setText(defaultValue);
        opoddTextField.setText(defaultValue);
        smo1dTextField.setText(defaultValue);
        smo1ddTextField.setText(defaultValue);
        smo2dTextField.setText(defaultValue);
        smo2ddTextField.setText(defaultValue);
        ostrendTextField.setText(defaultValue);
        osplunchTextField.setText(defaultValue);

        /** Metodo 3 **/

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

        /** DisplacementComponens*/

        alphaErrorLabel.setText(angleErrorString);
        smErrorLabel.setText(defaultErrorString);
        smhErrorLabel.setText(defaultErrorString);
        smdErrorLabel.setText(defaultErrorString);

        alphaTextField.setText(defaultValue);
        smTextField.setText(defaultValue);
        smhTextField.setText(defaultValue);
        smdTextField.setText(defaultValue);

        /** Results **/

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

    public void initGUIActions() {

        /** Activamos los campos que no se utilizan  */

        // CAMBIAMOS ENTRE LOS DISTINTOS CASOS DEL MÉTODO 1
        arbitraryLineToggleButton.setOnAction(event -> {

            if (arbitraryLineToggleButton.isSelected()) {

                mapSectionToggleButton.setSelected(false);
                //Habilimtaos
                phiComboBox.setDisable(false);
                phiErrorLabel.setDisable(false);
                phiLabel.setDisable(false);
                phiTextField.setDisable(false);
                smTextField.setDisable(false);
                smLabel.setDisable(false);
                smErrorLabel.setDisable(false);
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
        arbitraryLineToggleButton.fire();


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
                smTextField.setDisable(true);
                smLabel.setDisable(true);
                smErrorLabel.setDisable(true);
                // Desplegamos que es;
                statusPane.setStatus("Este es un caso particular", ADVICE_ICON);
                //Limpiamos
                clearcomponents();
            } else {
                mapSectionToggleButton.setSelected(true);
            }
        });


        // Cambiamos entre los métodos
        method1TitleLabel.setOnMouseClicked(event -> {

            if (mapSectionToggleButton.isSelected()) {
                mapSectionToggleButton.fire();
            } else {
                arbitraryLineToggleButton.fire();
            }

            method1GridLayout.getChildren().stream().forEach(item -> item.setDisable(false));
            method2GridLayout.getChildren().stream().filter(item -> !item.getStyleClass().contains(TITLE_LABEL)).forEach(item -> item.setDisable(true));
            method3GridLayout.getChildren().stream().filter(item -> !item.getStyleClass().contains(TITLE_LABEL)).forEach(item -> item.setDisable(true));
            smdTextField.setDisable(false);smdErrorLabel.setDisable(false);smdLabel.setDisable(false);

        });

        method2TitleLabel.setOnMouseClicked(event1 -> {

            method2GridLayout.getChildren().stream().forEach(item -> item.setDisable(false));
            method1GridLayout.getChildren().stream().filter(item -> !item.getStyleClass().contains(TITLE_LABEL)).forEach(item -> item.setDisable(true));
            method3GridLayout.getChildren().stream().filter(item -> !item.getStyleClass().contains(TITLE_LABEL)).forEach(item -> item.setDisable(true));

            smhErrorLabel.setDisable(true);smhTextField.setDisable(true);smhLabel.setDisable(true);
            smErrorLabel.setDisable(false);smTextField.setDisable(false);smLabel.setDisable(false);
            smdErrorLabel.setDisable(true);smdTextField.setDisable(true);smdLabel.setDisable(true);

        });

        method3TitleLabel.setOnMouseClicked(event1 -> {

            method3GridLayout.getChildren().stream().forEach(item -> item.setDisable(false));
            method1GridLayout.getChildren().stream().filter(item -> !item.getStyleClass().contains(TITLE_LABEL)).forEach(item -> item.setDisable(true));
            method2GridLayout.getChildren().stream().filter(item -> !item.getStyleClass().contains(TITLE_LABEL)).forEach(item -> item.setDisable(true));

            smhErrorLabel.setDisable(true);smhTextField.setDisable(true);smhLabel.setDisable(true);
            smErrorLabel.setDisable(false);smTextField.setDisable(false);smLabel.setDisable(false);
            smdErrorLabel.setDisable(true);smdTextField.setDisable(true);smdLabel.setDisable(true);

        });

        method1GridLayout.getChildren().stream().forEach(item -> item.setDisable(false));
        method2GridLayout.getChildren().stream().filter(item -> !item.getStyleClass().contains(TITLE_LABEL)).forEach(item -> item.setDisable(true));
        method3GridLayout.getChildren().stream().filter(item -> !item.getStyleClass().contains(TITLE_LABEL)).forEach(item -> item.setDisable(true));

        //


        /** Calculamos */
        calculateButton.setOnMouseClicked(event -> {

                // TODO
                TDData data = new TDData(statusPane);


            // Obtenemos si esta activo o inactivo el método1
            if(!method1GridLayout.getChildren().get(2).isDisable())
            {
                if (data.setBeta(betaTextField.getText(), betaErrorLabel.getText(), betaComboBox.getSelectionModel().getSelectedItem().toString())) {
                    if (data.setGamma(gammaTextField.getText(), gammaErrorLabel.getText(), gammaComboBox.getSelectionModel().getSelectedItem().toString())) {
                        if (data.setPhi(phiTextField.getText(), phiErrorLabel.getText(), phiComboBox.getSelectionModel().getSelectedItem().toString())) {
                            if (data.setApha(alphaTextField.getText(), alphaErrorLabel.getText())) {
                                if (data.setSm(smTextField.getText(), smErrorLabel.getText())) {
                                    if (data.setSmd(smdTextField.getText(), smdErrorLabel.getText())) {
                                        if (data.setSmh(smhTextField.getText(), smhErrorLabel.getText())) {
                                            if (data.setMapView((mapSectionToggleButton.isSelected()) ? mapSectionToggleButton.getText() : arbitraryLineToggleButton.getText())) {
                                                if (data.isDataValidForCalcuating()) {
                                                    if (data.Calculate(METHOD_1)) {
                                                        displayData(data);
                                                        TDTable.addData(data);
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

            if(!method2GridLayout.getChildren().get(2).isDisable())
            {
                if(data.setFod(fodTextField.getText(),fodErrorLabel.getText()))
                {
                    if(data.setFodd(foddTextField.getText(),foddErrorLabel.getText()))
                    {
                        if(data.setOpod(opodTextField.getText(),opodErrorLabel.getText()))
                        {
                            if(data.setOpodd(opoddTextField.getText(),opoddErrorLabel.getText()))
                            {
                                if(data.setSmo1d(smo1dTextField.getText(),smo1dErrorLabel.getText()))
                                {
                                    if(data.setSmo1dd(smo1ddTextField.getText(),smo1ddErrorLabel.getText()))
                                    {
                                        if(data.setSmo2d(smo2dTextField.getText(),smo2dErrorLabel.getText()))
                                        {
                                            if(data.setSmo2dd(smo2ddTextField.getText(),smo2ddErrorLabel.getText()))
                                            {
                                                if(data.setOsTrend(ostrendTextField.getText(),ostrendErrorLabel.getText()))
                                                {
                                                    if(data.setOsPlunch(osplunchTextField.getText(),osplunchErrorLabel.getText()))
                                                    {
                                                        if(data.setSm(smTextField.getText(),smErrorLabel.getText()))
                                                        {
                                                            if(data.setApha(alphaTextField.getText(),alphaErrorLabel.getText()))
                                                            {
                                                                if(data.Calculate(METHOD_2))
                                                                {
                                                                    displayData(data);
                                                                    TDTable.addData(data);
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
        TDTable.getTable().setOnMouseClicked(event -> {
            if (TDTable.getTable().getSelectionModel().getSelectedItem() != null) {
                TDData data = TDTable.getTable().getSelectionModel().getSelectedItem().getTDData();
                displayData(data);
            }

        });


        /**Borrar contenido*/

        resetButton.setOnMouseClicked(event -> {
            clearcomponents();
            statusPane.setStatus("Se ha Borrado la información");
        });



    }


    public void shakeStage() {

        Timeline timelineX = new Timeline(new KeyFrame(Duration.seconds(0.1), t -> {
            if (shakeStageFlag) {
                mainTruDispStage.setX(mainTruDispStage.getX() + 10);
            } else {
                mainTruDispStage.setX(mainTruDispStage.getX() - 10);
            }
            shakeStageFlag = !shakeStageFlag;
        }));

        timelineX.setCycleCount(10);
        timelineX.setAutoReverse(false);
        timelineX.play();
    }


    private void displayData(TDData data) {

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

        smTextField.setText(data.getSm().toString());
        smErrorLabel.setText("± " + data.getSmError());

        smdTextField.setText(data.getsmdError().toString());
        smdErrorLabel.setText("± " + data.getsmdError());

        smhTextField.setText(data.getSmh().toString());
        smhErrorLabel.setText("± " + data.getSmhError());

        fodTextField.setText(data.getFod().toString());
        fodErrorLabel.setText("± "+ data.getFodError()+" º");

        foddTextField.setText(data.getFodd().toString());
        foddErrorLabel.setText("± "+ data.getFoddError()+" º");

        opodTextField.setText(data.getOpod().toString());
        opodErrorLabel.setText("± "+ data.getOpodError()+" º");

        opoddTextField.setText(data.getOpodd().toString());
        opoddErrorLabel.setText("± "+ data.getOpoddError()+" º");

        smo1dTextField.setText(data.getSmo1d().toString());
        smo1dErrorLabel.setText("± "+ data.getSmo1dError()+" º");

        smo1ddTextField.setText(data.getSmo1dd().toString());
        smo1ddErrorLabel.setText("± "+ data.getSmo1ddError()+" º");

        smo2dTextField.setText(data.getSmo2d().toString());
        smo2dErrorLabel.setText("± "+ data.getSmo2dError()+" º");

        smo2ddTextField.setText(data.getSmo2dd().toString());
        smo2ddErrorLabel.setText("± "+ data.getSmo2ddError()+" º");

        ostrendTextField.setText(data.getOsTrend().toString());
        ostrendErrorLabel.setText("± "+ data.getOsTrendError()+" º");

        osplunchTextField.setText(data.getOsPlunch().toString());
        osplunchErrorLabel.setText("± "+ data.getOsPlunchError()+" º");

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

            switch (methodsHBox.getChildren().size())
            {
                case 1:{
                    // Solo queda el principal
                    mainStage.setMinWidth(550);
                    mainStage.setWidth(550);
                    break;
                }

                case 2:{

                    switch (methodSelected) {

                        case 2: {
                            // Queda el método 3;
                            mainStage.setMinWidth(900);
                            mainStage.setWidth(900);
                            break;
                        }

                        case 3: {
                            // Queda el método 2;
                            mainStage.setMinWidth(700);
                            mainStage.setWidth(700);
                            break;
                        }
                    }

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

                    switch (methodSelected)
                    {
                        case 2:
                        {
                            mainStage.setMinWidth(750);
                            mainStage.setWidth(750);
                            break;
                        }
                        case 3:
                        {
                            mainStage.setMinWidth(950);
                            mainStage.setWidth(950);
                            break;
                        }
                    }
                    break;
                }
                case 3:
                {
                    mainStage.setMinWidth(1350);
                    mainStage.setWidth(1350);
                    break;

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



