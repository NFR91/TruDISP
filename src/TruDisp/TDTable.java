package TruDisp;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.stream.Collectors;


public class TDTable
{
    private final TableView<TDDataWrapper> table;
    private ObservableList<TDDataWrapper> data;
    private TableColumn faultCol, sCol,notesCol,ssCol,sdCol,svCol,shCol, mapviewCol,betaCol, gammaCol, phiCol, alphaCol,smCol,smdCol,smhCol,thetaCol,thetanullCol;
    private TableColumn svlCol,seCol,ssvlCol,sseCol,sdvlCol,sdeCol,svvlCol,sveCol,shvlCol,sheCol,betavlCol,betaoCol,betaeCol,
                        gammavlCol,gammaeCol,gammaoCol,phivlCol,phieCol,phioCol,alphavlCol,alphaeCol,smvlCol,smeCol,smdvlCol,smdeCol, smhvlCol,smheCol;

    private Stage stage;
    private VBox  tableLayout = new VBox();
    private HBox buttonsLayout= new HBox();
    private Button removeButton,clearbutton;

    public TDTable()
    {
        // Historial de datos.
        data = FXCollections.observableArrayList();

        // Iniciamos la tabla.
        table = new TableView<>();
        table.setItems(data);
        initMethod1Table();
        table.getColumns().addAll(faultCol, sCol,thetaCol,thetanullCol,notesCol);
        table.setEditable(true);

        // Definimos el tamaño Preferido de las celdas
        table.getColumns().stream().forEach(item -> item.setPrefWidth(50));
        table.getColumns().stream().forEach(item-> item.getColumns().stream().forEach(col-> col.setPrefWidth(50)));
        faultCol.setPrefWidth(100);
        notesCol.setPrefWidth(100);
        table.setMinHeight(200);
        table.setMinWidth(200);

        // Botones
        removeButton = new Button("Remove");
        removeButton.setOnAction(event -> {
            if (table.getSelectionModel().getSelectedItem() != null) {
                data.remove(table.getSelectionModel().getSelectedItem());
            }
        });
        removeButton.setMaxWidth(Double.MAX_VALUE);
        clearbutton = new Button("Clear History");
        clearbutton.setOnAction(event -> {
            data.clear();
        });
        // Layouts

        VBox.setVgrow(table, Priority.ALWAYS);
        HBox.setHgrow(removeButton, Priority.ALWAYS);
        HBox.setHgrow(clearbutton,Priority.ALWAYS);
        tableLayout.getChildren().addAll(table, new HBox(removeButton, clearbutton));

        // Agragamos el contendor a la escena.
        Scene scene = new Scene(tableLayout);
        scene.getStylesheets().add(TruDisp.TRUDISP_STYLE_SHEET);

        // Agregamos la escena a la venta.
        stage = new Stage(StageStyle.DECORATED);
        stage.setTitle("History");

        stage.setScene(scene);
        stage.setWidth(400);
        stage.show();
        stage.setX((Screen.getPrimary().getVisualBounds().getWidth()/2) + (stage.getWidth()/2));
        stage.setY((Screen.getPrimary().getVisualBounds().getHeight()/2) - (stage.getHeight()/2));
    }

    public void addData(TDData dt)
    {
        dt.setExperiment(String.valueOf(data.size() + 1));
        dt.setNotes("-");
        data.add(new TDDataWrapper(dt));

    }

    public void show()
    {
        stage.show();
        stage.requestFocus();
    }

    public ArrayList<TDData> getDataList()
    {
       ArrayList<TDData> dt = new ArrayList<>();

        data.stream().forEach(item -> dt.add(item.getTDData()));

        return dt;
    }

    public void setObservableList(ArrayList<TDData> td)
    {
        data.clear();
        data.addAll(td.stream().map(t -> new TDDataWrapper(t)).collect(Collectors.toList()));
    }

    public TableView<TDDataWrapper> getTable()
    {
        return table;
    }

    private void initMethod1Table()
    {
        /**Columnas*/

        // Beta
        betaCol = new TableColumn("β");
         betavlCol = new TableColumn("º");
         betaoCol  = new TableColumn("N/S");
         betaeCol = new TableColumn("±");
        betaCol.getColumns().addAll(betavlCol, betaeCol, betaoCol);
        betavlCol.setCellValueFactory(new PropertyValueFactory<>("beta"));
        betaoCol.setCellValueFactory(new PropertyValueFactory<>("betao"));
        betaeCol.setCellValueFactory(new PropertyValueFactory<>("betaError"));

        // Gamma
         gammaCol = new TableColumn("γ");
         gammavlCol = new TableColumn("º");
         gammaoCol  = new TableColumn("N/S");
         gammaeCol = new TableColumn("±");
        gammaCol.getColumns().addAll(gammavlCol, gammaeCol, gammaoCol);
        gammavlCol.setCellValueFactory(new PropertyValueFactory<>("gamma"));
        gammaeCol.setCellValueFactory(new PropertyValueFactory<>("gammao"));
        gammaoCol.setCellValueFactory(new PropertyValueFactory<>("gammaError"));

        // Phi
         phiCol = new TableColumn("φ");
         phivlCol = new TableColumn("º");
         phioCol  = new TableColumn("N/S");
         phieCol = new TableColumn("±");
        phiCol.getColumns().addAll(phivlCol, phieCol, phioCol);
        phivlCol.setCellValueFactory(new PropertyValueFactory<>("phi"));
        phioCol.setCellValueFactory(new PropertyValueFactory<>("phio"));
        phieCol.setCellValueFactory(new PropertyValueFactory<>("phiError"));

        // alpha
         alphaCol= new TableColumn("α");
         alphavlCol= new TableColumn("º");
         alphaeCol = new TableColumn("±");
        alphaCol.getColumns().addAll(alphavlCol, alphaeCol);
        alphaeCol.setCellValueFactory(new PropertyValueFactory<>("alphaError"));
        alphavlCol.setCellValueFactory(new PropertyValueFactory<>("alpha"));

        // Sm
         smCol = new TableColumn("Sm");
         smvlCol = new TableColumn();
         smeCol = new TableColumn("±");
        smCol.getColumns().addAll(smvlCol, smeCol);
        smvlCol.setCellValueFactory(new PropertyValueFactory<>("smA"));
        smeCol.setCellValueFactory(new PropertyValueFactory<>("smAError"));

        // Smd
         smdCol = new TableColumn("Smd");
         smdvlCol = new TableColumn();
         smdeCol = new TableColumn("±");
        smdCol.getColumns().addAll(smdvlCol, smdeCol);
        smdvlCol.setCellValueFactory(new PropertyValueFactory<>("smd"));
        smdeCol.setCellValueFactory(new PropertyValueFactory<>("smdError"));

        // Smh
         smhCol = new TableColumn("Smh");
         smhvlCol= new TableColumn();
         smheCol = new TableColumn("±");
        smhCol.getColumns().addAll(smhvlCol, smheCol);
        smhvlCol.setCellValueFactory(new PropertyValueFactory<>("smh"));
        smheCol.setCellValueFactory(new PropertyValueFactory<>("smhError"));

        // S
         sCol = new TableColumn("S");
         svlCol= new TableColumn();
         seCol = new TableColumn("±");
        sCol.getColumns().addAll(svlCol, seCol);
        svlCol.setCellValueFactory(new PropertyValueFactory<>("s"));
        seCol.setCellValueFactory(new PropertyValueFactory<>("sError"));

        // Ss
         ssCol = new TableColumn("Ss");
         ssvlCol= new TableColumn();
         sseCol = new TableColumn("±");
        ssCol.getColumns().addAll(ssvlCol, sseCol);
        ssvlCol.setCellValueFactory(new PropertyValueFactory<>("ss"));
        sseCol.setCellValueFactory(new PropertyValueFactory<>("ssError"));

        // Sd
         sdCol = new TableColumn("Sd");
         sdvlCol= new TableColumn();
         sdeCol = new TableColumn("±");
        sdCol.getColumns().addAll(sdvlCol, sdeCol);
        sdvlCol.setCellValueFactory(new PropertyValueFactory<>("sd"));
        sdeCol.setCellValueFactory(new PropertyValueFactory<>("sdError"));

        // Sh
         shCol = new TableColumn("Sh");
         shvlCol= new TableColumn();
         sheCol = new TableColumn("±");
        shCol.getColumns().addAll(shvlCol, sheCol);
        shvlCol.setCellValueFactory(new PropertyValueFactory<>("sh"));
        sheCol.setCellValueFactory(new PropertyValueFactory<>("shError"));

        // Sv
         svCol = new TableColumn("Sv");
         svvlCol= new TableColumn();
         sveCol = new TableColumn("±");
        svCol.getColumns().addAll(svvlCol, sveCol);
        svvlCol.setCellValueFactory(new PropertyValueFactory<>("sv"));
        sveCol.setCellValueFactory(new PropertyValueFactory<>("svError"));

        // MapView
         mapviewCol= new TableColumn("View");
        mapviewCol.setCellValueFactory(new PropertyValueFactory<>("mapview"));


        //Theta
        thetaCol = new TableColumn<>("θ");
        thetaCol.setCellValueFactory(new PropertyValueFactory<>("theta"));


        // Thetanull

        thetanullCol = new TableColumn("θ null");
        thetanullCol.setCellValueFactory(new PropertyValueFactory<>("thetanull"));


        // Experiment
         faultCol = new TableColumn("Fault");
        faultCol.setCellValueFactory(new PropertyValueFactory<>("experiment"));
        faultCol.setCellFactory(TextFieldTableCell.<TDDataWrapper>forTableColumn());
        faultCol.setOnEditCommit(new EventHandler<CellEditEvent>() {
            @Override
            public void handle(CellEditEvent t) {
                ((TDDataWrapper) t.getTableView().getItems().get(t.getTablePosition().getRow())).setExperiment(t.getNewValue().toString());
            }
        });


        // Notes
        notesCol = new TableColumn("Notes");
        notesCol.setCellValueFactory(new PropertyValueFactory<>("notes"));
        notesCol.setCellFactory(TextFieldTableCell.<TDDataWrapper>forTableColumn());
        notesCol.setOnEditCommit(new EventHandler<CellEditEvent>() {
            @Override
            public void handle(CellEditEvent t) {
                ((TDDataWrapper) t.getTableView().getItems().get(t.getTablePosition().getRow())).setNotes(t.getNewValue().toString());
            }
        });


    }


    public static class TDDataWrapper{

        private SimpleDoubleProperty beta,betaError,gamma,gammaError,phi,phiError,alpha,alphaError, smA, smAError,smd,smdError,smh,smhError,
                s,sError,ss,ssError,sd,sdError,sv,svError,sh,shError,theta,thetanull;
        private SimpleStringProperty betao,gammao,phio,mapview,experiment,notes;

        private TDData tdData;

        private TDDataWrapper(TDData dt)
        {
            tdData = dt;

            beta = new SimpleDoubleProperty(tdData.getBeta());
            betaError = new SimpleDoubleProperty(tdData.getBetaError());
            betao = new SimpleStringProperty(tdData.getBetaOrientation());

            gamma = new SimpleDoubleProperty(tdData.getGamma());
            gammaError = new SimpleDoubleProperty(tdData.getGammaError());
            gammao = new SimpleStringProperty(tdData.getGammaOrientation());

            phi = new SimpleDoubleProperty(tdData.getPhi());
            phiError = new SimpleDoubleProperty(tdData.getPhiError());
            phio = new SimpleStringProperty(tdData.getPhiOrientation());

            alpha = new SimpleDoubleProperty(tdData.getAlpha());
            alphaError = new SimpleDoubleProperty(tdData.getAlphaError());

            smA = new SimpleDoubleProperty(tdData.getSmA());
            smAError = new SimpleDoubleProperty(tdData.getSmAError());
            smd = new SimpleDoubleProperty(tdData.getSmd());
            smdError = new SimpleDoubleProperty(tdData.getsmdError());
            smh= new SimpleDoubleProperty(tdData.getSmh());
            smhError = new SimpleDoubleProperty(tdData.getSmhError());

            s = new SimpleDoubleProperty(tdData.getS());
            sError= new SimpleDoubleProperty(tdData.getSError());

            ss = new SimpleDoubleProperty(tdData.getSs());
            ssError = new SimpleDoubleProperty(tdData.getSsError());
            sv = new SimpleDoubleProperty(tdData.getSv());
            svError = new SimpleDoubleProperty(tdData.getSvError());
            sd = new SimpleDoubleProperty(tdData.getSd());
            sdError = new SimpleDoubleProperty(tdData.getSdError());
            sh = new SimpleDoubleProperty(tdData.getSh());
            shError = new SimpleDoubleProperty(tdData.getShError());

            thetanull =new SimpleDoubleProperty(tdData.getThetaNull());
            theta = new SimpleDoubleProperty(tdData.getTheta());

            mapview = new SimpleStringProperty(tdData.getMapView());
            experiment = new SimpleStringProperty(tdData.getExperiment());

            notes = new SimpleStringProperty(tdData.getNotes());


        }

        public TDData getTDData()
        { return tdData;}


        public String getMapview()
        {return mapview.get();}

        public Double getBeta()
        {return beta.get();}
        public Double getBetaError()
        {return betaError.get();}
        public String getBetao()
        {return betao.get();}
        public Double getGamma()
        {return gamma.get();}
        public Double getGammaError()
        {return gammaError.get();}
        public String getGammao()
        {return  gammao.get();}
        public Double getPhi()
        {return  phi.get();}
        public Double getPhiError()
        {return  phiError.get();}
        public String getPhio()
        {return phio.getName();}
        public Double getAlpha()
        {return alpha.get();}
        public Double getAlphaError()
        {return alphaError.get();}
        public Double getSmA()
        {return smA.get();}
        public Double getSmAError()
        {return smAError.get();}
        public Double getSmd()
        {return smd.get();}
        public Double getSmdError()
        {return  smdError.get();}
        public Double getSmh()
        {return smh.get();}
        public Double getSmhError()
        {return smhError.get();}

        public Double getS()
        {return s.get();}
        public Double getSError()
        {return sError.get();}

        public Double getSs()
        {return ss.get();}
        public Double getSsError()
        {return ssError.get();}
        public Double getSd()
        {return sd.get();}
        public Double getSdError()
        {return sdError.get();}
        public Double getSv()
        {return sv.get();}
        public Double getSvError()
        {return svError.get();}
        public Double getSh()
        {return sh.get();}
        public Double getShError()
        {return shError.get();}


        public String getExperiment()
        {return experiment.get();}
        public String getNotes()
        {return notes.get();}

        public Double getTheta()
        {return theta.get();}

        public Double getThetanull()
        {return thetanull.get();}


        // SEters

        public void setExperiment(String vl)
        {experiment.set(vl); tdData.setExperiment(vl);}
        public void setNotes(String nts)
        {notes.setValue(nts); tdData.setNotes(nts);}

    }


}






