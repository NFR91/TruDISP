
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

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

/** Esta clase crea un objeto tridimensional y maneja sus propiedades.*/
public abstract class My3DObject {

    /** Variables */
    private ArrayList<Double[]>     objVert;        // Vertices del objeto
    private ArrayList<int[]>        objFace;        // Arreglo de las caras ( vertices que componen cada cara )
    private ArrayList<Double[]>     objProj;        // Projección del objeto.
    private ArrayList<Double[]>     objnormVect;    // Vectores normales a las caras.
    private ArrayList<Color>        objFaceColor;   // Colores de las caras.
    private Double[]                objPos;         // Posición del objeto.
    private Double[]                rotMatrix;      // Matriz de rotación

    /** Constantes */
    public static final int     X=0,Y=1,Z=2;            // Constantes de posición en los arreglos.
    public static final Double  FL=.0000004d;            // Distancia Focal
    public static final Double  CAMERA_POS=500d;      // Posición de la cámara
    /** Constructor */
    public My3DObject()
    {
        setObject(new ArrayList<>(), new ArrayList<>(),new ArrayList<>());
        objnormVect = new ArrayList<>();
        rotMatrix = new Double[]{1.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,1.0};
        objPos = new Double[]{0.0, 0.0, 0.0};
    }

    /******************************************************************************************************************/
    /** Métodos */
    /******************************************************************************************************************/

    /**Función encargada de dibujar el objeto*/
    public synchronized void draw(GraphicsContext gc,Double[] max, Double[] min, Double w, Double h){

        // Puntos del canvas de cada cara.
        ArrayList<Double[]> points;
        // Contenedores de las componentes de los puntos.
        double[] xs, ys;

        // Para cada cara del objeto
        for(int i=0; i<objFace.size(); i++)
        {
            points = new ArrayList<>();

            // Para cada vertice de la cara generamos un punto
            for(int n=0; n<objFace.get(i).length;n++)
                {points.add(n, FaultViewer.getCanvasPoint(objProj.get(objFace.get(i)[n]), max, min, w, h));}

            xs = new double[points.size()]; ys = new double[points.size()];

            // Para cada punto obtenemos sus componentes.
            for(int l=0; l<points.size(); l++)
            {
                xs[l] = points.get(l)[My3DObject.X];
                ys[l] = points.get(l)[My3DObject.Y];
            }

            // Dibujamos el objeto
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            gc.strokePolygon(xs, ys, points.size());
            gc.setFill(objFaceColor.get(i));
            gc.fillPolygon(xs, ys, points.size());

        }
    }

    /** Función encargada restablecer los valores por defecto */
    public synchronized void reset()
    {
        rotMatrix = new Double[]{1.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,1.0};
        objProj= project(this.getTObjVert());
        //objPos = new Double[]{0.0,0.0,0.0};
    }

    /**Función encargada de calcular el producto criz de dos vectores tridimensionales*/
    public synchronized static Double[] crossProduct(Double[] a, Double[] b)
    {
        Double[] axb = new Double[3];

        axb[0] = a[1]*b[2] - a[2]*b[1];
        axb[1] = a[2]*b[0] - a[0]*b[2];
        axb[2] = a[0]*b[1] - a[1]*b[0];

        return axb;
    }

    /**Función encargada de calcular el producto punto de dos vectores n-dimensionales*/
    public synchronized static Double dotProduct(Double[]a, Double[]b)
    {
        Double adotb=0.0;

        for (int i=0;i<a.length;i++)
        {
            adotb += a[i]*b[i];
        }

        return adotb;
    }

    /**Funcion encargada de calcular la norma de un vector n-dimensional*/
    public synchronized Double normOfVector(Double[] a)
    {
        Double n =0.0;
        for(int i=0; i<a.length;i++)
        {
            n += a[i]*a[i];
        }

        n = Math.sqrt(n);

        return n;
    }

    /**Función encargada de calcular los vectores normales a las caras del objeto*/
    public synchronized ArrayList<Double[]> normalVect(ArrayList<Double[]> vert, ArrayList<int[]> face) {

        // Arreglo que almacena los vectores normales.
        ArrayList<Double[]> normvect= new ArrayList<>();

        // Variables de dos vectores no paralelos.
        Double[] v1 = new Double[3];
        Double[] v2 = new Double[3];

        // para cada cara calculamos el vector normal.  // TODO Verificar que sean dos vectores no paralelos.
        for(int i=0;i<face.size();i++)
        {
            // Vector de vertice 1 con vertice 0
            v1[X] = vert.get(face.get(i)[1])[X]-vert.get(face.get(i)[0])[X];
            v1[Y] = vert.get(face.get(i)[1])[Y]-vert.get(face.get(i)[0])[Y];
            v1[Z] = vert.get(face.get(i)[1])[Z]-vert.get(face.get(i)[0])[Z] ;

            // Vector de vertice 2 con vertice 0;
            v2[X] = -vert.get(face.get(i)[0])[X] +vert.get(face.get(i)[2])[X];
            v2[Y] = -vert.get(face.get(i)[0])[Y] + vert.get(face.get(i)[2])[Y];
            v2[Z] = -vert.get(face.get(i)[0])[Z] + vert.get(face.get(i)[2])[Z];

            // Obtenemos el vector normal.
            normvect.add(i,crossProduct(v1,v2));
        }

        // Normalizamos los vectores
        normvect.stream().forEach(nv-> {
            Double norm = normOfVector(nv);
            nv[X] /=norm;
            nv[Y] /=norm;
            nv[Z] /=norm;
        });

        // Regresamos los vectores normales unitarios.
        return normvect;
    }

    /******************************************************************************************************************/
    /** Métodos para ordenar */
    /******************************************************************************************************************/

    /**Función encargada de ordenar los vertices de una cara.*/
    public synchronized int[] orderFaceVertices(ArrayList<Double[]> vert, Double[] nv)
    {
        // Variable de la cara.
        int[] face = new int[vert.size()];
        // Arreglos de los ángulos.
        ArrayList<Double> angle = new ArrayList<>(vert.size()),angleTemp=new ArrayList<>(vert.size());
        // Vectores de lineas
        Double[] l1 = new Double[3], l2=new Double[3];

        Double[] vc = Plane3D.getPlaneCenter(vert);

        // vector que va del centro de la cara a el primer vértice.
        l1[X]=(vert.get(0)[X] - vc[X]);l1[Y]=(vert.get(0)[Y]-vc[Y]);l1[Z]=(vert.get(0)[Z]-vc[Z]);
        // Corresponde al ángulo 0;
        angle.add(0.0);

        // Para los demás vertices comprobamos su ángulo con respecto al vertice 0.
        for(int i=1; i<vert.size();i++)
        {
            // Obtenemos el vector que va del centro del plano al vertice.
            l2[X]=(vert.get(i)[X]-vc[X]);l2[Y]=(vert.get(i)[Y]-vc[Y]);l2[Z]=(vert.get(i)[Z]-vc[Z]);

            // Revisamos si el producto punto es cero, lo que significa que estamos en 90 o 270;
            if(dotProduct(l1,l2)==0)
            {
                // Si es positivo es 90 si es negatuvio es 270
                if(dotProduct(nv,crossProduct(l2,l1))>0)
                {angle.add(90d);}
                else if (dotProduct(nv,crossProduct(l2,l1))<0)
                {angle.add(270d);}

            }
            else // No es ninguno de los casos especiales.
            {
                if(normOfVector(crossProduct(l2, l1))==0)  // Revisamos si son paraleleas es decir estan a 180º;
                {angle.add(Math.toDegrees(Math.acos(-1)));}
                else if(dotProduct(nv,crossProduct(l2,l1))<0)   // Revisamos si el ángulo es mayor a  180º
                {angle.add(360-Math.toDegrees(Math.acos(dotProduct(l2,l1)/(normOfVector(l1)*normOfVector(l2)))));}
                else    // el ángulo es menor a 180
                {angle.add(Math.toDegrees(Math.acos(dotProduct(l2,l1)/(normOfVector(l1)*normOfVector(l2)))));}
            }
        }

        // Almacenamos temporalmente los ángulos.
        angle.stream().forEach(a->angleTemp.add(a));
        // Organizamos los valores de menor a mayor;
        angleTemp.sort((o1, o2) -> o1.compareTo(o2));

        // Obtenemos el orden de los vertices en la cara.
        int temp=0;
        for(int i=0; i<face.length; i++)
        {
            // Para cada vertice.
            temp=i;
            for (int j=0;j<vert.size();j++)
            {
                // Si el ángulo del vertice es el siguiente almacenamos número de vertice en orden.
                if(angleTemp.get(i)==angle.get(j))
                    temp = j;
            }
            face[i]=temp;
        }

        // Regresamos el orden de los vertices.
        return face;
    }

    /**Función encargada de ordenar los vertices de cada cara para todas las caras.*/
    public synchronized void orderObjectFaces()
    {
        // Variable que almacena los vertices de cada cara.
        ArrayList<Double[]> points ;
        // Varialbes del orden de los vertices de cada cara.
        int[] newface, oldface, faceorder;

        for(int i=0; i<objFace.size(); i++)
        {
            points = new ArrayList<>();

            // Para cada cara obtenemos los vertices que la componen
            for(int j=0; j < objFace.get(i).length; j++)
            {
                points.add(j,objVert.get(objFace.get(i)[j]));
            }

            // Obtenemos el orden de los vertices.
            faceorder = orderFaceVertices(points, objnormVect.get(i));
            // Obtenemos el orden anterior.
            oldface = objFace.get(i);

            // Nuevo orden;
            newface = new int[oldface.length];

            // Para cada punto obtenemos el orden
            for(int k=0; k<points.size();k++)
            { newface[k] = oldface[faceorder[k]]; }

            // Asignamos la nueva cara.
            objFace.set(i, newface);
        }

    }

    /******************************************************************************************************************/
    /** Métodos de rotación */
    /******************************************************************************************************************/

    /** Función encargada de rotar el objeto. */
    public synchronized void rotateObject(Double xd, Double yd,Double zd)
    {
        // Matriz de rotación es la multiplicación de la matriz de rotación por la nueva matriz infinitesimal;
        rotMatrix = matrixPxN(rotMatrix,matrixRotUpdate(xd,yd,zd));


        // Vectores rotados.
        ArrayList<Double[]> rotated = new ArrayList<>(objVert.size());

        // Rotamos el objeto y lo trasladamos a su posición.
        for(int i=0; i< objVert.size();i++)
        {
            rotated.add(i, rotate(rotMatrix, objVert.get(i)));
            rotated.get(i)[X] += objPos[X];
            rotated.get(i)[Y] += objPos[Y];
            rotated.get(i)[Z] += objPos[Z];
        }

        // Projectamos el objeto.
        objProj = project(rotated);

    }

    /** Función encargada de rotar el objeto en grados */
    public synchronized void rotateObject(Integer axis,Double angle)
    {
        // Convertimos a radianes el ángulo
        angle = Math.toRadians(angle);

        // Verificamos sobre cual eje se realiza la rotación y realizamos la rotación
        switch (axis)
        {
            case X:
            {
                for (double i=0,xd=0.0001; i<=angle;i+=xd)
                    {rotMatrix = matrixPxN(rotMatrix,matrixRotUpdate(xd,0d,0d));}
                break;
            }

            case Y:
            {
                for (double i=0,yd=0.0001; i<=angle;i+=yd)
                    {rotMatrix = matrixPxN(rotMatrix,matrixRotUpdate(0d,yd,0d));}
                break;
            }
            case Z:
            {
                for (double i=0,zd=0.0001; i<=angle;i+=zd)
                    {rotMatrix = matrixPxN(rotMatrix,matrixRotUpdate(0d,0d,zd));}
                break;
            }
        }

        // Actualizamos el objeto
        this.rotateObject(0d,0d,0d);
    }

    /** Función encargada de multiplicar dos matrices de 3x3 , p y n. */
    public synchronized  Double[] matrixPxN(Double[] pr, Double[] nr)
    {
        Double [] cr = new Double[9];

        cr[0] = (pr[0]*nr[0]) + (pr[1]*nr[3]) + (pr[2]*nr[6]);
        cr[1] = (pr[0]*nr[1]) + (pr[1]*nr[4]) + (pr[2]*nr[7]);
        cr[2] = (pr[0]*nr[2]) + (pr[1]*nr[5]) + (pr[2]*nr[8]);

        cr[3] = (pr[3]*nr[0]) + (pr[4]*nr[3]) + (pr[5]*nr[6]);
        cr[4] = (pr[3]*nr[1]) + (pr[4]*nr[4]) + (pr[5]*nr[7]);
        cr[5] = (pr[3]*nr[2]) + (pr[4]*nr[5]) + (pr[5]*nr[8]);

        cr[6] = (pr[6]*nr[0]) + (pr[7]*nr[3]) + (pr[8]*nr[6]);
        cr[7] = (pr[6]*nr[1]) + (pr[7]*nr[4]) + (pr[8]*nr[7]);
        cr[8] = (pr[6]*nr[2]) + (pr[7]*nr[5]) + (pr[8]*nr[8]);

        return cr;
    }

    /** Función encargada de realizar la transpuesta de una matriz 3x3 */
    public synchronized Double[] matrixRotTranspose(Double[] r)
    {
        Double[] rt= new Double[9];

        rt[0]=r[0]; rt[1]=r[3]; rt[2]=r[6];
        rt[3]=r[1]; rt[4]=r[4]; rt[5]=r[7];
        rt[6]=r[2]; rt[7]=r[5]; rt[8]=r[8];

        return rt;
    }

    /** Función encargada obtener la nueva rotación */
    public synchronized Double[] matrixRotUpdate(Double x, Double y, Double z) {
        // Variable que almacena el vector de rotación.
        Double[] a = new Double[]{x,y,z};

        // Transformamos el vector de rotación al sistema de coordenadas del objeto.
        a = rotate(matrixRotTranspose(rotMatrix),a);
        //a = rotate(rotMatrix,a);

        // Creamos la matriz de rotación con el vector de rotación
        Double[] rd = new Double[9];

        rd[0] =   1.0;  rd[1] = -a[Z];  rd[2] =   a[Y];
        rd[3] =  a[Z];  rd[4] =   1.0;  rd[5] =  -a[X];
        rd[6] = -a[Y];  rd[7] =  a[X];  rd[8] =    1.0;

        // Regresamos la matriz de rotación
        return rd;
    }

    /** Función encargada obtener la nueva rotación */
    public  synchronized Double[] rotate(Double[] r,Double[] point)
    {
        Double [] vl = new Double [3];

        vl[0] = (r[0]*point[X]) + (r[1]*point[Y]) + (r[2]*point[Z]);
        vl[1] = (r[3]*point[X]) + (r[4]*point[Y]) + (r[5]*point[Z]);
        vl[2] = (r[6]*point[X]) + (r[7]*point[Y]) + (r[8]*point[Z]);

        return vl;

    }

    /******************************************************************************************************************/
    /** Métodos Projección */
    /******************************************************************************************************************/

    /** Función encargada de proyectar el objeto tridimensional */
    public synchronized ArrayList<Double[]> project(ArrayList<Double[]> v)
    {
        // Areglo que contendrá los puntos en 2 dimensiones de la projección del objeto
        ArrayList<Double[]> proj = new ArrayList<>(v.size());

        // Para cada punto obtenemos los valores X e Y de cada punto del objeto 3D;
        for(int i=0; i<v.size();i++)
        {
            //proj.add(i,new Double[]{FL*(v.get(i)[X]/(CAMERA_POS+v.get(i)[Z])),FL*(v.get(i)[Y]/(CAMERA_POS+v.get(i)[Z]))});
            proj.add(i,new Double[]{v.get(i)[X],v.get(i)[Y]});
        }

        // Regresamos la proyeción.
        return proj;
    }

    /** Función encargada de proyectar el objeto tridimensional */
    public synchronized void project()
    {
        objProj = project(getTObjVert());
    }

    /******************************************************************************************************************/
    /** Métodos GET */
    /******************************************************************************************************************/

    /** Función encargada obtener regresar la projección. */
    public synchronized ArrayList<Double[]> getProjection()
    {
        return objProj;
    }

    /** Función encargada obtener regresar la las caras. */
    public synchronized ArrayList<int[]> getObjFace()
    {
        return objFace;
    }

    /** Función encargada obtener regresar la posición del objeto. */
    public synchronized Double[] getObjPos()
    {
        return objPos;
    }

    /** Función encargada obtener regresar el vector normal a las caras. */
    public synchronized ArrayList<Double[]> getObjNormalVectors(){return  objnormVect;}

    /** Función encargada de reresar la matriz de rotación*/
    public synchronized Double[] getRotMatrix()
    {return  rotMatrix;}

    /** Función encargada de reresar los vertices trasladados*/
    public  synchronized ArrayList<Double[]> getTObjVert(){

        // Objeto que regresa el vertice trasladado
        ArrayList<Double[]> traslated = new ArrayList<>();

        // Inicializamos el arreglo
        for (int i=0; i<objVert.size() ;i++)
            {traslated.add(new Double[]{0d,0d,0d});}

        // Trasladamos los vertices
        for(int i=0; i< traslated.size(); i++)
        {
            traslated.get(i)[X]= objVert.get(i)[X]+objPos[X];
            traslated.get(i)[Y]= objVert.get(i)[Y]+objPos[Y];
            traslated.get(i)[Z]= objVert.get(i)[Z]+objPos[Z];
        }

        // Regresamos los vertices trasladados.
        return traslated;

    }

    /**Función encargada de regresar el valor de la posición del objeto*/
    public synchronized ArrayList<Color> getObjFaceColor(){return objFaceColor;}

    public synchronized ArrayList<Double[]> getObjVertices()
    {
        return objVert;
    }

    /******************************************************************************************************************/
    /** Métodos SET */
    /******************************************************************************************************************/

    /** Función encargada de iniciar los valores del objeto. */
    public synchronized void setObject(ArrayList<Double[]> vert,ArrayList<int[]> face,ArrayList<Color> faceColor) {
        this.setObjVertices(vert);
        objFace = face;
        objFaceColor = faceColor;
        objnormVect = normalVect(objVert, objFace);
        objProj = project(objVert);
    }

    /** Función encargada de asignar el valor de la matriz de rotación. */
    public synchronized void setRotMatrix(Double[] rotm) {rotMatrix =rotm;}

    /**Función encargada de asignar  los vectores normales a las caras del objeto*/
    public synchronized void setNormalVect(ArrayList<Double[]> nv)  {objnormVect = nv;}

    /**Función encargada de asginar el valor de la posición del objeto*/
    public synchronized void setObjPos(Double[] pos) { objPos = pos;}

    /**Función encargada de asginar el valor del color de cada cara del objeto*/
    public synchronized void setObjFaceColor(ArrayList<Color> c)  { objFaceColor = c; }

    /**Función encargada de asginar los vertices del objeto*/
    public synchronized void setObjVertices(ArrayList<Double[]> v)
    {
        objVert = v;
    }

}
