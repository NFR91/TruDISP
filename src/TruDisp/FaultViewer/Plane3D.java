
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
import javafx.scene.paint.Color;

import java.util.ArrayList;

/** Extensión de la clase objeto 3d para un plano */
public class Plane3D extends My3DObject {

    private My3DObject container;   // Contenedor del plano

    /******************************************************************************************************************/
    /** Constructores. */
    /******************************************************************************************************************/

    public Plane3D ()
    {

        ArrayList<Double[]> nv = new ArrayList<>();
        ArrayList<Color> color = new ArrayList<>();

        // Definimos el vector normal.
        Double[] vector = new Double[]{1.0,1.0,1.0};
        Double vectornorm=normOfVector(vector);
        vector[X]/=vectornorm;vector[Y]/=vectornorm;vector[Z]/=vectornorm;

        nv.add(vector);

        color.add(Color.rgb(255, 0, 0, .5));

        setObjFaceColor(color);
        setNormalVect(nv);
        this.reset();
    }

    public Plane3D(Double[] normalVector, Color color,My3DObject container)
    {
        super();

        Double[] nv = new Double[]{-normalVector[2],normalVector[1],normalVector[0]};

        this.getObjNormalVect().add(nv);
        this.getObjFaceColor().add(color);
        this.setContainer(container);
        this.setRotMatrix(container.getRotMatrix());
        this.setObjPos(new Double[]{0d,0d,0d});
        this.intersectPlaneWidthContainer();
        this.rotateObject(0d, 0d, 0d);
    }

    /******************************************************************************************************************/
    /** Métodos SET */
    /******************************************************************************************************************/

    /** Asignamos el contenedor del plano*/
    public void setContainer(My3DObject object)
    {
        container = object;
    }

    /** Asignamos el color del plano*/
    public void setObjFaceColor(Color color)
    { getObjFaceColor().set(0,color);}

    /** Asignamos el vector normal al plano */
    public void setNormalVector(Double[] nv)
    {
        this.getObjNormalVect().set(0,nv);
    }


    /******************************************************************************************************************/
    /** Métodos de intersección. */
    /******************************************************************************************************************/

    /** Función que obtene las intersecciones del plano con el contenedor */
    public void intersectPlaneWidthContainer()
    {
        // Vector normal del plano
        Double[] nv = this.getObjNormalVect().get(0);
        // Arreglo de vertices del plano
        ArrayList<Double[]> vert = new ArrayList<>();
        // Arreglo de la cara del plano
        ArrayList<int[]> face = new ArrayList<>();
        // Origen del plano
        Double[] planeOrigin= this.getObjPos();

        // Arista
        Double[] l = new Double[3];     // Arista;
        // Un punto de la línea (arista)
        Double[] linePoint;
        // Punto de intersección entre el plano y la arista.
        Double[] val;
        // Diferencia entre el punto del plano y el de la recta.
        Double[] po_pl= new Double[3];
        Double a,num,den;

        // Para cada cara del contenedor.
        for (int i=0;i< container.getObjFace().size();i++)
        {
            // Para cada vertice de la cara del contenedor
            for(int j=0;j< container.getObjFace().get(i).length;j++)
            {
                // Obtenemos la arista
                if(j== container.getObjFace().get(i).length-1)
                {
                    l[X]= container.getTObjVert().get(container.getObjFace().get(i)[0])[X]- container.getTObjVert().get(container.getObjFace().get(i)[j])[X];
                    l[Y]= container.getTObjVert().get(container.getObjFace().get(i)[0])[Y]- container.getTObjVert().get(container.getObjFace().get(i)[j])[Y];
                    l[Z]= container.getTObjVert().get(container.getObjFace().get(i)[0])[Z]- container.getTObjVert().get(container.getObjFace().get(i)[j])[Z];
                }
                else
                {
                    l[X] = container.getTObjVert().get(container.getObjFace().get(i)[j + 1])[X] - container.getTObjVert().get(container.getObjFace().get(i)[j])[X];
                    l[Y] = container.getTObjVert().get(container.getObjFace().get(i)[j + 1])[Y] - container.getTObjVert().get(container.getObjFace().get(i)[j])[Y];
                    l[Z] = container.getTObjVert().get(container.getObjFace().get(i)[j + 1])[Z] - container.getTObjVert().get(container.getObjFace().get(i)[j])[Z];
                }

                // obtenemos un punto de la arista.
                linePoint = container.getTObjVert().get(container.getObjFace().get(i)[j]);

                // punto del plano - punto de la arista.
                po_pl[X]= planeOrigin[X]-linePoint[X]; po_pl[Y]=planeOrigin[Y]-linePoint[Y]; po_pl[Z]=planeOrigin[Z]-linePoint[Z];

                // Numerador y denominador de la intersección de un plano y una recta.
                num = dotProduct(po_pl,nv);
                den = dotProduct(l,nv);

                // Si el denominador es cero la linea vive en el plano, si el denominador es cero el plano y la linea son paralelos.
                if(den!=0 && num !=0)
                {
                    // Obtenemos el escalar que nos da la intersección de la arista con el plano.
                    a = num/den;

                    // Obtenemos el punto de intersección.
                    val = new Double[]{linePoint[X]+(a*l[X]),linePoint[Y]+(a*l[Y]),linePoint[Z]+(a*l[Z])};

                    // revisamos si no esta repetido el vertice.
                    boolean addval = true;
                    for (int n =0;n<vert.size();n++)
                    {
                        if(normOfVector(new Double[]{vert.get(n)[X] - val[X], vert.get(n)[Y] - val[Y], vert.get(n)[Z] - val[Z]})==0)
                        {
                            addval=false;
                            break;
                        }
                    }

                    //!!! Importante, al calcularse las itersecciones con las caras se encuentran las intersecciones fuera del objeto, hay que arreglar eso
                    if(normOfVector(val)>Math.sqrt(3)+.01)
                        addval =false;

                    // Agregamos el nuevo vertice del plano.
                    if (addval) { vert.add(val);}
                }
            }
        }
        // Ordenamos los vertices y agregamos la cara
        face.add(orderFaceVertices(vert, nv));
        //Asginamos los valores del objeto
        this.setObject(vert, face, this.getObjFaceColor());
        // Poroyectamos el objeto.
        this.project();
    }
}
