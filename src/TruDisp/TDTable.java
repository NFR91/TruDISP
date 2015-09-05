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

/** Esta clase se encarga de mostrar en una tabla el historial de las diferentes fallas calculadas con el programa.*/
public class TDTable
{
    /** Variables*/
    private final TableView<TDDataWrapper>  table;          // Tabla que contendrá la información
    private ObservableList<TDDataWrapper>   data;           // Lista observable que contendrá las fallas calculadas.

    // Columnas que muestran los datos.
    private TableColumn                     faultCol,       // Nombre dela falla
                                            sCol, notesCol, // Distancia y notas de la falla
                                            thetaCol, thetanullCol;
    // Subcolumnas
    private TableColumn                     svlCol,seCol;   // Valor y error de las medidas.

    private Stage                           stage;          // Ventana del historial
    private Button                          removeButton,   // Botón para eliminar un elemento de la lista.
                                            clearbutton;    // Botón para borrar la lista.
    private VBox                            tableLayout;    // Contenedor de los elementos de la gui de la tabla.

    /** Constructor */
    public TDTable()
    {
        // Historial de datos.
        data = FXCollections.observableArrayList();

        // Iniciamos la tabla.
        table = new TableView<>();
        table.setItems(data);
        initTable();
        table.getColumns().addAll(faultCol, sCol, thetaCol, thetanullCol, notesCol);
        table.setEditable(true);

        // Definimos el tamaño Preferido de las celdas
        table.getColumns().stream().forEach(item -> item.setPrefWidth(50));
        table.getColumns().stream().forEach(item -> item.getColumns().stream().forEach(col -> col.setPrefWidth(50)));
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
        tableLayout = new VBox();
        VBox.setVgrow(table, Priority.ALWAYS);
        HBox.setHgrow(removeButton, Priority.ALWAYS);
        HBox.setHgrow(clearbutton,Priority.ALWAYS);
        tableLayout.getChildren().addAll(table, new HBox(removeButton, clearbutton));

        // Agregamos el contendor a la escena.
        Scene scene = new Scene(tableLayout);
        scene.getStylesheets().add(TruDisp.TRUDISP_STYLE_SHEET);

        // Creamos la ventana
        stage = new Stage(StageStyle.DECORATED);
        stage.setTitle("History");
        stage.setWidth(400);
        stage.setX((Screen.getPrimary().getVisualBounds().getWidth() / 2) + (stage.getWidth() / 2));
        stage.setY((Screen.getPrimary().getVisualBounds().getHeight() / 2) - (stage.getHeight() / 2));

        // Agregamos la escena a la venta.
        stage.setScene(scene);

        // Mostramos el historial
        stage.show();

    }

    /** Función que agrega una falla al historial */
    public void addData(TDData dt)
    {
        // Creamos un dummy de nombre de experimento con el tamaño del arreglo.
        dt.setExperiment(String.valueOf(data.size() + 1));
        // Cramos un dummy de las notas.
        dt.setNotes("-");
        // Agregamos el dato mediante un cascaron.
        data.add(new TDDataWrapper(dt));
    }

    /** Función que muestra el historial */
    public void show()
    {
        stage.show();
        stage.requestFocus();
    }

    /** Función que regresa la lista de fallas calculadas. */
    public ArrayList<TDData> getDataList()
    {
        // Creamos una lista
        ArrayList<TDData> dt = new ArrayList<>();
        // A partir de la lista observable creamos la lista de fallas.
        data.stream().forEach(item -> dt.add(item.getTDData()));
        // Regresamos la lista de fallas.
        return dt;
    }

    /** Función que genera una lista observable a partir de una lista de fallas. */
    public void setObservableList(ArrayList<TDData> td)
    {
        data.clear();
        data.addAll(td.stream().map(t -> new TDDataWrapper(t)).collect(Collectors.toList()));
    }

    /** Funcion encargada de regresar la tabla */
    public TableView<TDDataWrapper> getTable()
    {
        return table;
    }

    /** Función que inicializa los elementos de la tabla. */
    private void initTable()
    {
        /**Columnas*/

        // S
        sCol = new TableColumn("S");
        svlCol= new TableColumn();
        seCol = new TableColumn("±");
        sCol.getColumns().addAll(svlCol, seCol);
        svlCol.setCellValueFactory(new PropertyValueFactory<>("s"));
        seCol.setCellValueFactory(new PropertyValueFactory<>("sError"));

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
        // Para que se pueda modificar el contenido de la celda
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
        // Para que se pueda modificar el contenido de la celda
        notesCol.setOnEditCommit(new EventHandler<CellEditEvent>() {
            @Override
            public void handle(CellEditEvent t) {
                ((TDDataWrapper) t.getTableView().getItems().get(t.getTablePosition().getRow())).setNotes(t.getNewValue().toString());
            }
        });
    }

    /******************************************************************************************************************/
    /** Clase encargada de encapsular el objerto del cálculo de la falla */
    /******************************************************************************************************************/
    public static class TDDataWrapper{

        /** Variables */
        private SimpleDoubleProperty    s,sError,theta,thetanull;
        private SimpleStringProperty    experiment,notes;
        private TDData                  tdData;                     // Objeto que contiene el cálculo de la falla.

        /** Constructor */
        private TDDataWrapper(TDData dt)
        {
            // Adquirimos el objeto
            tdData = dt;

            // Obtenemos los valores de desplazamiento
            s = new SimpleDoubleProperty(tdData.getS());
            sError= new SimpleDoubleProperty(tdData.getSError());

            // Obtenemos los valores de los ángulos de error.
            thetanull =new SimpleDoubleProperty(tdData.getThetaNull());
            theta = new SimpleDoubleProperty(tdData.getTheta());

            // Obtenemos el nombre del experimento
            experiment = new SimpleStringProperty(tdData.getExperiment());

            // Obtenemos las notas del experimento.
            notes = new SimpleStringProperty(tdData.getNotes());
        }

        /** Método que regresa el objeto de falla*/
        public TDData getTDData()
        { return tdData;}

        /******************************************************************************************************************/
        /** Métodos get para la tabla */
        /******************************************************************************************************************/
        public Double getS()
        {return s.get();}
        public Double getSError()
        {return sError.get();}
        public String getExperiment()
        {return experiment.get();}
        public String getNotes()
        {return notes.get();}
        public Double getTheta()
        {return theta.get();}
        public Double getThetanull()
        {return thetanull.get();}

        /******************************************************************************************************************/
        /** Métodos set */
        /******************************************************************************************************************/

        /** Función que asigna el nuevo valor del experimento tanto para la tabla como para el objeto */
        public void setExperiment(String vl)
        {experiment.set(vl); tdData.setExperiment(vl);}
        /** Función que asigna el nuevo valor de la nota tanto para la tabla como para el objeto */
        public void setNotes(String nts)
        {notes.setValue(nts); tdData.setNotes(nts);}
    }
}






