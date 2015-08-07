package TruDisp;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.control.TableColumn.CellEditEvent;

import java.util.ArrayList;
import java.util.stream.Collectors;


public class TruDispTable
{
    private final TableView<TDDataWrapper> table;
    private ObservableList<TDDataWrapper> data;
    private TableColumn expCol, sCol,notesCol,ssCol,sdCol,svCol,shCol, mapviewCol,betaCol, gammaCol, phiCol, alphaCol,smCol,smdCol,smhCol;
    private TableColumn svlCol,seCol,ssvlCol,sseCol,sdvlCol,sdeCol,svvlCol,sveCol,shvlCol,sheCol,betavlCol,betaoCol,betaeCol,
                        gammavlCol,gammaeCol,gammaoCol,phivlCol,phieCol,phioCol,alphavlCol,alphaeCol,smvlCol,smeCol,smdvlCol,smdeCol, smhvlCol,smheCol;

    private Stage stage;
    private VBox  tableLayout = new VBox();
    private HBox buttonsLayout= new HBox();
    private Button removeButton;

    public TruDispTable()
    {
        // Historial de datos.
        data = FXCollections.observableArrayList();

        // Iniciamos la tabla.
        table = new TableView<>();
        initMethod1Table();
        table.getColumns().addAll(expCol, sCol, notesCol);
        table.setEditable(true);

        // Definimos el tamaño Preferido de las celdas
        table.getColumns().stream().forEach(item -> item.setPrefWidth(100));

        // Botones
        removeButton = new Button("Remove");
        removeButton.setOnAction(event -> {
            if(table.getSelectionModel().getSelectedItem()!=null) {
                data.remove(table.getSelectionModel().getSelectedItem());
                table.setItems(data);
            }
        });

        // Layouts

        VBox.setVgrow(table, Priority.ALWAYS);
        tableLayout.getChildren().addAll(table, removeButton);

        // Agragamos el contendor a la escena.
        Scene scene = new Scene(tableLayout);
        scene.getStylesheets().add(TruDisp.TRUDISP_STYLE_SHEET);

        // Agregamos la escena a la venta.
        stage = new Stage(StageStyle.DECORATED);
        stage.setTitle("History");

        stage.setScene(scene);
        stage.setMaxWidth(400);
        stage.show();
        stage.setX((Screen.getPrimary().getVisualBounds().getWidth()/2) + (stage.getWidth()/2));
        stage.setY((Screen.getPrimary().getVisualBounds().getHeight()/2) - (stage.getHeight()/2));
    }

    public void addData(TDData dt)
    {
        dt.setExperiment(String.valueOf(data.size()+1));
        dt.setNotes("-");
        data.add(new TDDataWrapper(dt));

        table.setItems(data);

    }

    public void show()
    {
        stage.show();
    }
    public void hide()
    {
        stage.hide();
    }

    public ArrayList<TDData> getDataList()
    {
       ArrayList<TDData> dt = new ArrayList<>();

        data.stream().forEach(item -> dt.add(item.getTDData()));

        return dt;
    }

    public void setObservableList(ArrayList<TDData> td)
    {
        data.removeAll();

        data.addAll(td.stream().map(t -> new TDDataWrapper(t)).collect(Collectors.toList()));

        table.setItems(data);
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
        smvlCol.setCellValueFactory(new PropertyValueFactory<>("sm"));
        smeCol.setCellValueFactory(new PropertyValueFactory<>("smError"));

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

        // Experiment
         expCol= new TableColumn("EXP");
        expCol.setCellFactory(TextFieldTableCell.<TDDataWrapper>forTableColumn());
        expCol.setOnEditCommit(new EventHandler<CellEditEvent>() {
            @Override
            public void handle(CellEditEvent event) {
                ((TDDataWrapper) event.getTableView().getItems().get(event.getTablePosition().getRow())).setExperiment(event.getNewValue().toString());
            }
        });
        expCol.setCellValueFactory(new PropertyValueFactory<>("experiment"));

        // Notes
        notesCol = new TableColumn("Notes");
        notesCol.setCellFactory(TextFieldTableCell.<TDDataWrapper>forTableColumn());
        notesCol.setOnEditCommit(new EventHandler<CellEditEvent>() {
            @Override
            public void handle(CellEditEvent event) {
                ((TDDataWrapper) event.getTableView().getItems().get(event.getTablePosition().getRow())).setNotes(event.getNewValue().toString());

            }
        });
        notesCol.setCellValueFactory(new PropertyValueFactory<>("notes"));


    }


    public static class TDDataWrapper{

        private SimpleDoubleProperty beta,betaError,gamma,gammaError,phi,phiError,alpha,alphaError,sm,smError,smd,smdError,smh,smhError,s,sError,ss,ssError,sd,sdError,sv,svError,sh,shError;
        private SimpleStringProperty betao,gammao,phio,mapview,experiment,notes;

        private TDData data;

        private TDDataWrapper(TDData dt)
        {
            data = dt;

            beta = new SimpleDoubleProperty(data.getBeta());
            betaError = new SimpleDoubleProperty(data.getBetaError());
            betao = new SimpleStringProperty(data.getBetaOrientation());

            gamma = new SimpleDoubleProperty(data.getGamma());
            gammaError = new SimpleDoubleProperty(data.getGammaError());
            gammao = new SimpleStringProperty(data.getGammaOrientation());

            phi = new SimpleDoubleProperty(data.getPhi());
            phiError = new SimpleDoubleProperty(data.getPhiError());
            phio = new SimpleStringProperty(data.getPhiOrientation());

            alpha = new SimpleDoubleProperty(data.getAlpha());
            alphaError = new SimpleDoubleProperty(data.getAlphaError());

            sm = new SimpleDoubleProperty(data.getSm());
            smError = new SimpleDoubleProperty(data.getSmError());
            smd = new SimpleDoubleProperty(data.getSmd());
            smdError = new SimpleDoubleProperty(data.getsmdError());
            smh= new SimpleDoubleProperty(data.getSmh());
            smhError = new SimpleDoubleProperty(data.getSmhError());

            s = new SimpleDoubleProperty(data.getS());
            sError= new SimpleDoubleProperty(data.getSError());

            ss = new SimpleDoubleProperty(data.getSs());
            ssError = new SimpleDoubleProperty(data.getSsError());
            sv = new SimpleDoubleProperty(data.getSv());
            svError = new SimpleDoubleProperty(data.getSvError());
            sd = new SimpleDoubleProperty(data.getSd());
            sdError = new SimpleDoubleProperty(data.getSdError());
            sh = new SimpleDoubleProperty(data.getSh());
            shError = new SimpleDoubleProperty(data.getShError());

            mapview = new SimpleStringProperty(data.getMapView());
            experiment = new SimpleStringProperty(data.getExperiment());

            notes = new SimpleStringProperty(data.getNotes());


        }

        public TDData getTDData()
        { return data;}


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
        public Double getSm()
        {return sm.get();}
        public Double getSmError()
        {return smError.get();}
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
        {return sh.get();}

        public String getExperiment()
        {return experiment.get();}
        public String getNotes()
        {return notes.get();}

        // SEters

        public void setExperiment(String vl)
        {experiment.set(vl); data.setExperiment(vl);}
        public void setNotes(String nts)
        {notes.setValue(nts); data.setNotes(nts);}




    }
}

