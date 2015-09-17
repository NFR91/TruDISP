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

import javafx.animation.FadeTransition;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.ArrayList;

/** Esta clase se encarga de manejar los eventos del escenario 3D */
public class FaultViewer {

    /**Constantes */
    private final Long              SLEEP_TIME_MILLIS = 10l;        // Tiempo de espera entre rotaciones en millisegundos.
    private final Double            ROT_INCREMENT=0.005;            // Incremento de la rotación

    /**Variables */
    private Stage                   viewer;             // Ventana para mostrar el dibujo.
    private Canvas                  canvas;             // Objeto para dibujar sobre el.
    private Double                  xold=0.0,yold=0.0;  // Posiciones del cursor para saber cuanto rotar.
    private Task<Double>            task;               // Tarea que maneja la rotación.
    private FaultBlock3D            faultBlock;         // Bloque de falla.
    private ArrayList<My3DObject>   objects;            // Arreglo de objetos a dibujar.
    private HBox                    rotCtrl;            // Contenedor de controles de rotación.
    private GraphicsContext         gc;                 // Contexto gráfico.
    private Button                  rxpos,rxneg,        // Botones de control
                                    rypos,ryneg,
                                    rzpos,rzneg;

    /** Constructor */
    public FaultViewer()
    {
        // Creamos la ventana
        viewer = new Stage(StageStyle.DECORATED);
        viewer.setTitle("Fault");
        viewer.setWidth(300);
        viewer.setHeight(300);

        // Creamos el canvas para dibujar.
        canvas = new Canvas();
        canvas.heightProperty().bind(viewer.heightProperty());
        canvas.widthProperty().bind(viewer.widthProperty());
        // Ponemos al centro el canvas.
        StackPane.setAlignment(canvas, Pos.CENTER);

        // Obtenemos el contexto gráfico del canvas.
        gc = canvas.getGraphicsContext2D();

        // Generamos los objetos de control.
        initFVControls();

        // Generamos la lista de objetos de la escena
        faultBlock = new FaultBlock3D();
        faultBlock.rotateObject(My3DObject.Y,20d);
        objects = new ArrayList<>();
        objects.add(faultBlock);
        this.drawScene(gc,objects);

        // Agremgamos la escena.
        viewer.setScene(new Scene(new StackPane(canvas, rotCtrl)));
    }

    /******************************************************************************************************************/
    /** Métodos. */
    /******************************************************************************************************************/

    /** Función que inicializa los controles */
    public void initFVControls()
    {
        // Cada vez qie se redimensione el canvas, volvemos a dibujar la escena.
        canvas.widthProperty().addListener(observable -> {
            drawScene(gc, objects);
        });
        canvas.heightProperty().addListener(observable -> {
            drawScene(gc, objects);
        });

        // Cuando damos mas de dos clicks la escena se reinicia a la posición por defecto.
        canvas.setOnMouseClicked(event1 -> {
            if (event1.getClickCount() > 2) {
                objects.stream().forEach(obj -> obj.reset());
                drawScene(gc, objects);
            }
        });

        /**
         * Las coordenadas del objeto son [X Y Z], las de la proyección son [X Y] y las del canvas son [Y X]
         *
         * Objeto
         *
         * x
         * |  z
         * | /
         * |/_ _ _ Y
         *
         * Proyección
         *
         * x
         * |
         * |
         * |_ _ _ Y
         *
         * Canvas
         *
         * _ _ _ _ X
         * |
         * |
         * |Y
         *
         *
         * */

        /**Botones de posición para el eje X*/
        rxpos = new Button("+");
        rxneg = new Button("-");
        rxpos.setStyle("-fx-base:red;");
        rxneg.setStyle("-fx-base:red;");

        // Cuando se presiona el botón.
        rxpos.setOnMousePressed(event1 -> {

            // Revisamos si se ha dado click con el botón derecho y sólo con un botón.
            if(event1.isPrimaryButtonDown()&& !event1.isSecondaryButtonDown())
            {
                // Tarea encargada de manejar la rotación.
                task = new Task<Double>() {
                    @Override
                    protected Double call() throws Exception {

                        // Mientras la tarea este activa rotamos.
                        while (!isCancelled()) {

                            objects.stream().forEach(obj -> {
                                obj.rotateObject(ROT_INCREMENT,0.0, 0.0);
                            });
                            drawScene(gc, objects);

                            try
                            {
                                Thread.sleep(SLEEP_TIME_MILLIS);
                            }
                            catch (InterruptedException interrupted)
                            {
                                if (isCancelled()) {
                                    updateMessage("Cancelled");
                                    break;
                                }
                            }
                        }
                        return null;
                    }
                };

                // Creamos e iniciamos el hilo.
                new Thread(task).start();
            }
        });
        // Al liberar el botón se cancela la tarea.
        rxpos.setOnMouseReleased(event1 -> {
            if(task!=null)
                task.cancel();
        });

        // Cuando se presiona el botón.
        rxneg.setOnMousePressed(event1 -> {

            // Revisamos si se ha dado click con el botón derecho y sólo con un botón.
            if(event1.isPrimaryButtonDown()&& !event1.isSecondaryButtonDown())
            {
                // Tarea encargada de manejar la rotación.
                task = new Task<Double>() {
                    @Override
                    protected Double call() throws Exception {

                        // Mientras la tarea este activa roitamos.
                        while (!isCancelled()) {
                            objects.stream().forEach(obj -> {
                                obj.rotateObject( -ROT_INCREMENT,0.0,  0.0);
                            });
                            drawScene(gc, objects);

                            try
                            {
                                Thread.sleep(SLEEP_TIME_MILLIS);
                            }
                            catch (InterruptedException interrupted)
                            {
                                if (isCancelled()) {
                                    updateMessage("Cancelled");
                                    break;
                                }

                            }
                        }
                        return null;
                    }
                };

                // Creamos e iniciamos el hilo.
                new Thread(task).start();
            }

        });
        // Al liberar el botón se cancela la tarea.
        rxneg.setOnMouseReleased(event1 -> {
            if(task!=null)
                task.cancel();
        });

        /**Botones de posición para el eje Y */
        rypos = new Button("+");
        ryneg = new Button("-");
        rypos.setStyle("-fx-base:green;");
        ryneg.setStyle("-fx-base:green;");

        rypos.setOnMousePressed(event1 -> {
            if(event1.isPrimaryButtonDown()&& !event1.isSecondaryButtonDown())
            {
                task = new Task<Double>() {
                    @Override
                    protected Double call() throws Exception {

                        while (!isCancelled()) {
                            objects.stream().forEach(obj -> {
                                obj.rotateObject( 0.0,ROT_INCREMENT,  0.0);
                            });
                            drawScene(gc, objects);

                            try
                            {
                                Thread.sleep(SLEEP_TIME_MILLIS);
                            }
                            catch (InterruptedException interrupted)
                            {
                                if (isCancelled()) {
                                    updateMessage("Cancelled");
                                    break;
                                }

                            }
                        }

                        return null;
                    }
                };
                new Thread(task).start();
            }

        });
        rypos.setOnMouseReleased(event1 -> {
            if(task!=null)
                task.cancel();
        });
        ryneg.setOnMousePressed(event1 -> {
            if(event1.isPrimaryButtonDown()&& !event1.isSecondaryButtonDown())
            {
                task = new Task<Double>() {
                    @Override
                    protected Double call() throws Exception {

                        while (!isCancelled()) {
                            objects.stream().forEach(obj -> {
                                obj.rotateObject( 0.0, -ROT_INCREMENT, 0.0);
                            });
                            drawScene(gc, objects);

                            try
                            {
                                Thread.sleep(SLEEP_TIME_MILLIS);
                            }
                            catch (InterruptedException interrupted)
                            {
                                if (isCancelled()) {
                                    updateMessage("Cancelled");
                                    break;
                                }

                            }
                        }

                        return null;
                    }
                };
                new Thread(task).start();
            }

        });
        ryneg.setOnMouseReleased(event1 -> {
            if(task!=null)
                task.cancel();
        });

        /**Botones de posición para el eje Z*/
        rzpos = new Button("+");
        rzneg = new Button("-");
        rzpos.setStyle("-fx-base:blue;");
        rzneg.setStyle("-fx-base:blue;");

        rzpos.setOnMousePressed(event1 -> {
            if(event1.isPrimaryButtonDown()&& !event1.isSecondaryButtonDown())
            {
                task = new Task<Double>() {
                    @Override
                    protected Double call() throws Exception {

                        while (!isCancelled()) {
                            objects.stream().forEach(obj -> {
                                obj.rotateObject(0.0, 0.0, ROT_INCREMENT);
                            });
                            drawScene(gc, objects);

                            try
                            {
                                Thread.sleep(SLEEP_TIME_MILLIS);
                            }
                            catch (InterruptedException interrupted)
                            {
                                if (isCancelled()) {
                                    updateMessage("Cancelled");
                                    break;
                                }

                            }
                        }

                        return null;
                    }
                };
                new Thread(task).start();
            }

        });
        rzpos.setOnMouseReleased(event1 -> {
            if(task!=null)
                task.cancel();
        });
        rzneg.setOnMousePressed(event1 -> {
            if(event1.isPrimaryButtonDown()&& !event1.isSecondaryButtonDown())
            {
                task = new Task<Double>() {
                    @Override
                    protected Double call() throws Exception {

                        while (!isCancelled()) {
                            objects.stream().forEach(obj -> {
                                obj.rotateObject(0.0, 0.0, -ROT_INCREMENT);
                            });
                            drawScene(gc, objects);

                            try
                            {
                                Thread.sleep(SLEEP_TIME_MILLIS);
                            }
                            catch (InterruptedException interrupted)
                            {
                                if (isCancelled()) {
                                    updateMessage("Cancelled");
                                    break;
                                }

                            }
                        }

                        return null;
                    }
                };
                new Thread(task).start();
            }

        });
        rzneg.setOnMouseReleased(event1 -> {
            if(task!=null)
                task.cancel();
        });

        /**Agregamos los botones.*/

        // Creamos el contenedor de los botones.
        rotCtrl = new HBox();
        rotCtrl.setFillHeight(false);
        rotCtrl.setAlignment(Pos.BOTTOM_CENTER);
        rotCtrl.prefHeight(20);
        rotCtrl.pickOnBoundsProperty().set(false);
        rotCtrl.setOpacity(0.1);
        rotCtrl.getChildren().addAll(new HBox(rxpos, rxneg), new HBox(rypos, ryneg), new HBox(rzpos, rzneg));

        // Creamos una transición que desvanece cuando no esta activo.
        rotCtrl.setOnMouseEntered(event -> {
            FadeTransition fd = new FadeTransition(Duration.seconds(.5), rotCtrl);
            fd.setFromValue(0.1);
            fd.setToValue(.8);
            fd.play();

        });
        rotCtrl.setOnMouseExited(event -> {
            FadeTransition fd = new FadeTransition(Duration.seconds(.5), rotCtrl);
            fd.setFromValue(.8);
            fd.setToValue(0.1);
            fd.play();
        });


        // Cuando se arrastra el ratón el cubo se rota.
        canvas.setOnMouseDragged(event -> {

            // Obtenemos el desplazamiento.
            Double xr = (event.getX()-xold);
            Double yr = (event.getY()-yold);

            // Si se ha desplazamod más de un margen, aumentamos en 0.01 la rotación manteniendo el signo.
            if(Math.abs(xr)>0.01)
            {xold = event.getX(); xr = ROT_INCREMENT*(xr/Math.abs(xr));}

            if(Math.abs(yr)>0.01)
            {yold=event.getY();yr=ROT_INCREMENT*(yr/Math.abs(yr)); }

            // Para cada objeto realizamos la rotación.
            for(int i=0;i<objects.size();i++)
            {objects.get(i).rotateObject(xr,yr,0.0);}

            // Dibujamos.
            drawScene(gc, objects);
        });
    }

    /** Función que dibuja en el canvas. */
    public synchronized void drawScene(GraphicsContext gc,ArrayList<My3DObject> objects)
    {
        // Obtenemos el tamaño del canvas.
        Double w = gc.getCanvas().getWidth();
        Double h = gc.getCanvas().getHeight();

        // Obtenemos el valor minimo y el máximo.
        Double[] min = getMinVal(objects);
        Double[] max = getMaxVal(objects);

        //max = new Double[]{2.0,2.0};
        //min = new Double[]{-2.0,-2.0};

        // Limpiamos la escena.
        gc.clearRect(0, 0, w, h);

        // Dibujamos los objetos.
        for(int i=0;i<objects.size();i++)
        {
            objects.get(i).draw(gc,min,max,w,h);
        }
    }

    /** Función que muestra la venta para graficar.. */
    public void show()
    {
        viewer.show();
        viewer.requestFocus();
    }


    /******************************************************************************************************************/
    /** Métodos SET. */
    /******************************************************************************************************************/

    /** Función que asigna los planos */
    public void setPlanes(Double[] fp,Double[] ap,Double[] bp)
    {
        // Los tres planos
        Plane3D faultPlane, marker1Plane, marker2Plane;

        faultPlane = new Plane3D(fp,Color.rgb(200,0,0,.3), faultBlock);
        marker1Plane = new Plane3D(ap,Color.rgb(200,0,200,.3), faultBlock);
        marker2Plane = new Plane3D(bp,Color.rgb(0,200,200,.3), faultBlock);

        // Limpiamos la lista
        objects.clear();
        // Agregamos los planos
        objects.add(faultPlane);
        objects.add(marker1Plane);
        objects.add(marker2Plane);
        // Agregamos los bloques básicos.
        objects.add(faultBlock);

        // Dibujamos la escena.
        drawScene(gc, objects);

    }

    public void setObjects(ArrayList<My3DObject> obj)
    {
        // Limpiamos la lista
        objects.clear();
        // Agregamos los objetos
        obj.stream().forEach(o -> objects.add(o));
        //Agregamos los bloques básicos
        objects.add(faultBlock);
        // dibujamos la escena
        drawScene(gc,objects);
    }
    /******************************************************************************************************************/
    /** Métodos GET */
    /******************************************************************************************************************/

    /** Función que obtiene el valor máximo de un conjunto de arreglos.. */
    public Double[] getMaxVal(ArrayList<My3DObject> objects)
    {
        // Variable que almacenará el valor máximo.
        Double[] max=new Double[] {Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY};

        // para cada punto de cada objeto verificamos si el valor es más grande.
        objects.stream().forEach(obj -> obj.getProjection().stream().forEach(point -> {

            if (point[obj.X] > max[obj.X]) {
                max[obj.X] = point[obj.X];
            }
            if (point[obj.Y] > max[obj.Y]) {
                max[obj.Y] = point[obj.Y];
            }
        }));

        // Regresamos el valor máximo.
        return max;
    }

    /** Función que obtiene el valor mínimo de un conjunto de arreglos.. */
    public Double[] getMinVal(ArrayList<My3DObject> objects)
    {
        // Variables que almacenarán los valores mínimos.
        Double[] min=new Double[] {Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY};

        // Para cada punto de cada objeto verificamos si el valor es menor.
        objects.stream().forEach(obj -> obj.getProjection().stream().forEach(point -> {

            if (point[obj.X] < min[obj.X]) {
                min[obj.X] = point[obj.X];
            }
            if (point[obj.Y] < min[obj.Y]) {
                min[obj.Y] = point[obj.Y];
            }

        }));

        // Regresamos el valor mínimo encontrado.
        return min;
    }

    /** Función que obtiene el valor mínimo de un conjunto de arreglos. */
    public static Double[] getCanvasPoint(Double[] point, Double[] max, Double[] min, Double w, Double h)
    {
        // Varialbes temporales.
        Double x,y;

        // normalizamos mediante. (y-min)/(max-min) y (x-max)/(min-max) e invertimos los ejes para el canvas
        y = (point[My3DObject.X]-min[My3DObject.X])/(max[My3DObject.X]-min[My3DObject.X]);
        x = (point[My3DObject.Y]-max[My3DObject.Y])/(min[My3DObject.Y]-max[My3DObject.Y]);
        //y=(point[My3DObject.Y]-min[My3DObject.Y])/(max[My3DObject.Y]-min[My3DObject.Y]);

        // Colocamos un margen del 10 % ;
        x = ((0.1)+(x*0.8))*w;
        y = ((0.1)+(y*0.8))*h;

        // Regresamos el valor normalizado multiplicado por las dimensiones de la ventana.
        return  new Double[]{x,y};

    }

    public Canvas getCanvas()
    {
        return canvas;
    }
    public HBox getRotCtrl()
    {
        return rotCtrl;
    }

    public FaultBlock3D getFaultBlock()
    {
        return faultBlock;
    }
}