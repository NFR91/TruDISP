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


import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;


/** Esta clase se encarga de guardar y abrir los archivos de TruDisp*/
public class TDOpenSave {

    /** Variables */
    private FileChooser     fileChooser;    // Objeto encargado de abrir una venta de selección de archivos
    private Stage           stage;          // Ventana principal de la cual se despliega el filsechooser.

    /** Constructor */
    public TDOpenSave(Stage st)
    {
        fileChooser = new FileChooser();
        stage = st;
    }

    /** Función encargada de guardar los archivos. */
    public void Save(ArrayList<TDData> data )
    {
        // titulo de la ventana emergente
        fileChooser.setTitle("Save TruDisp Session");


        // Archivo al que se escribe.
        FileWriter file = null;
        try
        {
            // Obtenemos el archivo al que se escribe a partir de la seleción del filechooser.
            file = new FileWriter(fileChooser.showSaveDialog(stage));

            // Objeto para escribir el archivo.

            PrintWriter pw = new PrintWriter(file);
            // Para cada objeto de la lista se escribe la información.
            data.stream().forEach(tdData -> {
                pw.println(tdData);
            });

            // Cerramos el archivo
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /** Función encargada de abrir los archivos. */
    public ArrayList<TDData> Open(TDStatusPane tdpane)
    {
        // Titulo de la ventana
        fileChooser.setTitle("Open TruDisp Session");

        // Lista que contrendrá los distitos objetos de la sesión a cargar.
        ArrayList<TDData> arrayListTDData = new ArrayList<>();

        // Objeto de lectura
        FileReader filereader= null;
        try
        {
            // Abrimos el objeto que se indica mediante el filechooser.
            File file = new File (fileChooser.showOpenDialog(stage).getAbsolutePath());

            // Creamos el objeto para leer.
            filereader = new FileReader (file); // Create a FileReader
            BufferedReader bufferedreader = new BufferedReader(filereader); // Create the BufferedReader

            // Leemos linea por linea y creamos  el objeto.
            String line;
            while((line=bufferedreader.readLine())!=null)
            {arrayListTDData.add(new TDData(line,tdpane));}
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        // REgresamos la lista que contiene todos los objetos
        return arrayListTDData;
    }
}
