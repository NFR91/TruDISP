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
import javafx.util.Duration;

import java.util.ArrayList;

/**
 * Created by Nieto on 17/08/15.
 */
public class FaultViewer extends Stage {
    private Canvas canvas;
    private Double xold=0.0,yold=0.0;
    private Long SLEEP_TIME= 20l;
    private Task<Double> task;
    private FaultBlock3D block;
    private Plane3D plane;
    private Arrow3D arrow3D;
    private ArrayList<Double[]> points;
    private ArrayList<My3DObject> objects;
    private HBox rotCtrl;
    private GraphicsContext gc;

    Button rxpos,rxneg,rypos,ryneg,rzpos,rzneg;

    public FaultViewer()
    {

        canvas = new Canvas();
        canvas.heightProperty().bind(this.heightProperty());
        canvas.widthProperty().bind(this.widthProperty());

        this.setTitle("Fault");
        this.setWidth(300);
        this.setHeight(300);

        gc = canvas.getGraphicsContext2D();


        block = new FaultBlock3D();
        plane = new Plane3D();
        Plane3D p2 = new Plane3D();
        p2.setObjFaceColor(Color.rgb(0, 0, 255, 0.3));
        p2.setNormalVector(new Double[]{Math.cos(Math.toRadians(90)), Math.cos(Math.toRadians(30)), Math.toRadians(40)});
        p2.setContainer(block);
        p2.intersectPlaneWidthContainer();
        plane.setContainer(block);
        plane.intersectPlaneWidthContainer();
        plane.setObjFaceColor(Color.rgb(0, 255, 0, 0.3));
        arrow3D = new Arrow3D();


        objects = new ArrayList<>();
        objects.add(plane);
        objects.add(p2);
        objects.add(arrow3D);
        objects.add(block);

        drawScene(gc, objects);

        initFVControls();

        this.setScene(new Scene(new StackPane(canvas, rotCtrl)));
        this.show();

    }

    public void setPlanes(Double[] fp,Double[] op, Double[] ap,Double[] bp)
    {
        Plane3D faultPlane, observationPlane, marker1Plane, marker2Plane;

        faultPlane = new Plane3D(fp,Color.rgb(200,0,0,.3),block);
        observationPlane = new Plane3D(op,Color.rgb(0,200,0,.3),block);
        marker1Plane = new Plane3D(ap,Color.rgb(200,0,200,.3),block);
        marker2Plane = new Plane3D(bp,Color.rgb(0,200,200,.3),block);

        objects.clear();
        objects.add(faultPlane);
        objects.add(observationPlane);
        objects.add(marker1Plane);
        objects.add(marker2Plane);
        objects.add(arrow3D);
        objects.add(block);

        drawScene(gc,objects);

    }
    public void initFVControls()
    {

        canvas.widthProperty().addListener(observable -> {
            drawScene(gc, objects);
        });
        canvas.heightProperty().addListener(observable -> {
            drawScene(gc, objects);
        });

        canvas.setOnMouseClicked(event1 -> {
            if (event1.getClickCount() > 2) {
                objects.stream().forEach(obj -> obj.reset());
                drawScene(gc, objects);
            }
        });


        rxpos = new Button("+");
        rxneg = new Button("-");
        rxpos.setStyle("-fx-base:red;");
        rxneg.setStyle("-fx-base:red;");

        rxpos.setOnMousePressed(event1 -> {

            if(event1.isPrimaryButtonDown()&& !event1.isSecondaryButtonDown())
            {
                task = new Task<Double>() {
                    @Override
                    protected Double call() throws Exception {

                        while (!isCancelled()) {
                            objects.stream().forEach(obj -> obj.rotateObject(0.01, 0.0, 0.0));
                            drawScene(gc, objects);
                            Thread.sleep(SLEEP_TIME);
                        }

                        return null;
                    }
                };
                new Thread(task).start();
            }

        });
        rxpos.setOnMouseReleased(event1 -> {
            if(task!=null)
                task.cancel();
        });
        rxneg.setOnMousePressed(event1 -> {

            if(event1.isPrimaryButtonDown()&& !event1.isSecondaryButtonDown())
            {
                task = new Task<Double>() {
                    @Override
                    protected Double call() throws Exception {

                        while (!isCancelled()) {
                            objects.stream().forEach(obj -> obj.rotateObject(-0.01, 0.0, 0.0));
                            drawScene(gc, objects);
                            Thread.sleep(SLEEP_TIME);
                        }

                        return null;
                    }
                };
                new Thread(task).start();
            }

        });
        rxneg.setOnMouseReleased(event1 -> {
            if(task!=null)
                task.cancel();
        });

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
                            objects.stream().forEach(obj -> obj.rotateObject(0.0, 0.01, 0.0));
                            drawScene(gc, objects);
                            Thread.sleep(SLEEP_TIME);
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
                            objects.stream().forEach(obj -> obj.rotateObject(0.0, -0.01, 0.0));
                            drawScene(gc, objects);
                            Thread.sleep(SLEEP_TIME);
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
                            objects.stream().forEach(obj -> obj.rotateObject(0.0, 0.0, 0.01));
                            drawScene(gc, objects);
                            Thread.sleep(SLEEP_TIME);
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
                            objects.stream().forEach(obj -> obj.rotateObject(0.0, 0.0, -0.01));
                            drawScene(gc, objects);
                            Thread.sleep(SLEEP_TIME);
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


        rotCtrl = new HBox();
        rotCtrl.setFillHeight(false);
        rotCtrl.setAlignment(Pos.BOTTOM_CENTER);
        rotCtrl.prefHeight(20);
        rotCtrl.pickOnBoundsProperty().set(false);
        rotCtrl.getChildren().addAll(new HBox(rxpos, rxneg), new HBox(rypos, ryneg),new HBox(rzpos,rzneg));
        rotCtrl.setOnMouseEntered(event -> {
            FadeTransition fd = new FadeTransition(Duration.seconds(.5),rotCtrl);
            fd.setFromValue(0.1);
            fd.setToValue(.8);
            fd.play();

        });
        rotCtrl.setOnMouseExited(event -> {
            FadeTransition fd = new FadeTransition(Duration.seconds(.5),rotCtrl);
            fd.setFromValue(.8);
            fd.setToValue(0.1);
            fd.play();
        });
        rotCtrl.setOpacity(0.1);

        StackPane.setAlignment(canvas, Pos.CENTER);

        canvas.setOnMouseDragged(event -> {

            Double xr = event.getX()-xold;
            Double yr = event.getY()-yold;

            if(Math.abs(xr)>0.01)
            {xold = event.getX(); xr = 0.01*(xr/Math.abs(xr));}

            if(Math.abs(yr)>0.01)
            {yold=event.getY();yr=0.01*(yr/Math.abs(yr)); }

            for(int i=0;i<objects.size();i++)
            {objects.get(i).rotateObject(-xr,-yr,0.0);}

            drawScene(gc, objects);
        });

    }

    public void drawScene(GraphicsContext gc,ArrayList<My3DObject> objects)
    {
        Double w = gc.getCanvas().getWidth();
        Double h = gc.getCanvas().getHeight();

        Double[] min = getMinVal(objects);
        Double[] max = getMaxVal(objects);

        max = new Double[]{2.0,2.0};
        min = new Double[]{-2.0,-2.0};

        gc.setLineWidth(2);
        gc.clearRect(0, 0, w, h);


        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);


        for(int i=0;i<objects.size();i++)
        {
            for(int j=0;j<objects.get(i).getObjFace().size();j++)
            {
                points = new ArrayList<>();

                for(int n=0;n<objects.get(i).getObjFace().get(j).length;n++)
                {
                    points.add(n,getCanvasPoint(objects.get(i).getProjection().get(objects.get(i).getObjFace().get(j)[n]),max,min,w,h));
                }
                double[] xs = new double[points.size()], ys = new double[points.size()];

                for (int l=0;l<points.size();l++)
                {
                    xs[l] = points.get(l)[My3DObject.X];
                    ys[l] = points.get(l)[My3DObject.Y];
                }
                //gc.setStroke(objects.get(i).getObjFaceColor().get(j));
                gc.strokePolygon(xs, ys, points.size());
                gc.setFill(objects.get(i).getObjFaceColor().get(j));
                gc.fillPolygon(xs,ys,points.size());

            }
        }


        // Draw Coordinates

        Double[] zero = getCanvasPoint(min, max, min, w, h);
        gc.setStroke(Color.RED);
        gc.strokeLine(zero[My3DObject.X], zero[My3DObject.Y], zero[My3DObject.X], zero[My3DObject.Y] - 100);
        gc.setStroke(Color.GREEN);
        gc.strokeLine(zero[My3DObject.X], zero[My3DObject.Y], zero[My3DObject.X] + 100, zero[My3DObject.Y]);
        gc.setStroke(Color.BLUE);
        gc.strokeLine(zero[My3DObject.X], zero[My3DObject.Y], zero[My3DObject.X]+20, zero[My3DObject.Y] - 20);


    }


    public Double[] getMaxVal(ArrayList<My3DObject> objects)
    {
        Double[] max=new Double[] {Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY};


        objects.stream().forEach(obj -> obj.getProjection().stream().forEach(point -> {

            if (point[obj.X] > max[obj.X]) {
                max[obj.X] = point[obj.X];
            }
            if (point[obj.Y] > max[obj.Y]) {
                max[obj.Y] = point[obj.Y];
            }

        }));

        return max;
    }

    public Double[] getMinVal(ArrayList<My3DObject> objects)
    {
        Double[] min=new Double[] {Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY};

        objects.stream().forEach(obj -> obj.getProjection().stream().forEach(point -> {

            if (point[obj.X] < min[obj.X]) {
                min[obj.X] = point[obj.X];
            }
            if (point[obj.Y] < min[obj.Y]) {
                min[obj.Y] = point[obj.Y];
            }

        }));

        return min;
    }

    public Double[] getCanvasPoint(Double[] point, Double[] max, Double[] min, Double w, Double h)
    {
        Double x,y;

        x = (point[My3DObject.X]-min[My3DObject.X])/(max[My3DObject.X]-min[My3DObject.X]);
        y = (point[My3DObject.Y]-max[My3DObject.Y])/(min[My3DObject.Y]-max[My3DObject.Y]);

        x = (0.1)+(x*0.8);
        y = (0.1)+(y*0.8);


        return  new Double[]{x*w,y*h};

    }
}