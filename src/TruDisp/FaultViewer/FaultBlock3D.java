
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


package TruDisp.FaultViewer;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;

/** Extensión de objeto tridimensional que dibuja un bloque.*/
public class FaultBlock3D extends My3DObject {

    /** Constructor*/
    public FaultBlock3D()
    {
        // Vertices
        ArrayList<Double[]> objVert = new ArrayList<>(8);
        objVert.add(0, new Double[]{-1d, -1d, -1d});
        objVert.add(1, new Double[]{-1d,  1d, -1d});
        objVert.add(2, new Double[]{ 1d,  1d, -1d});
        objVert.add(3, new Double[]{ 1d, -1d, -1d});
        objVert.add(4, new Double[]{-1d, -1d,  1d});
        objVert.add(5, new Double[]{-1d,  1d,  1d});
        objVert.add(6, new Double[]{ 1d,  1d,  1d});
        objVert.add(7, new Double[]{ 1d, -1d,  1d});

        // Caras
        ArrayList<int[]> objFace = new ArrayList(6);
        objFace.add(0, new int[]{0, 1, 2, 3});
        objFace.add(1, new int[]{1, 5, 6, 2});
        objFace.add(2, new int[]{4, 5, 6, 7});
        objFace.add(3, new int[]{0, 4, 7, 3});
        objFace.add(4, new int[]{3, 2, 6, 7});
        objFace.add(5, new int[]{0, 1, 5, 4});

        // Color de las caras
        ArrayList<Color> objFaceColor = new ArrayList<>(6);
        Color color = Color.rgb(219, 140, 51,.02);
        objFaceColor.add(0, color);
        objFaceColor.add(1, color);
        objFaceColor.add(2, color);
        objFaceColor.add(3, color);
        objFaceColor.add(4, Color.rgb(255, 143, 15,0.2));
        objFaceColor.add(5, Color.rgb(99, 61, 17, .8));

        // Obtenemos el vector normal a cada cara.
        setNormalVect(normalVect(objVert, objFace));

        // Asignamos los valores del objeto.
        setObject(objVert, objFace, objFaceColor);

        // Ordenamos las caras
        orderObjectFaces();

        reset();
    }

    /**Función encargada de dibujar el objeto*/
    public void draw(GraphicsContext gc,Double[] min,Double[] max,Double w,Double h)
    {
        // Llamamos a la función padre.
        super.draw(gc, min, max, w, h);

        // Ahora dibujamos las coordenadas sobre el bloque de falla.


        // Puntos de la línea del norte.
        Double[] fp = FaultViewer.getCanvasPoint(getProjection().get(7),min,max,w,h);
        Double[] op = FaultViewer.getCanvasPoint(getProjection().get(3), min, max, w, h);

        // Definimos los colres, el tamaño y la posición de la esfera que indica el norte
        gc.setFill(Color.rgb(200, 0, 0, .8));
        gc.setStroke(Color.rgb(200, 0, 0, .8));
        gc.fillOval(fp[X] - 10, fp[Y] - 10, 20, 20);


        // Dibujamos la línea
        gc.setLineWidth(3);
        gc.strokeLine(op[X], op[Y], fp[X], fp[Y]);

        // Dibujamos el texto que indica que es el norte
        gc.setFont(new Font("Courier", 30));
        gc.setTextBaseline(VPos.CENTER);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("N", (op[X] + fp[X]) / 2, (op[Y] + fp[Y]) / 2);

    }

}
