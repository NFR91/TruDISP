package TruDisp.FaultViewer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

/**
 * Created by Nieto on 12/09/15.
 */
public class Line3D extends My3DObject {

    private My3DObject container;   // Contenedor de la linea
    private Color color;
    private Double[] point;

    public Line3D (Double[] p0, Double[] p1,Double[] rotmat)
    {
        super();

        ArrayList<Double[]> lv = new ArrayList<>();

        lv.add(p0);
        lv.add(p1);

        this.setObjVertices(lv);
        Double[] nv = new Double[3];

        nv[X]=p1[X]-p0[X];
        nv[Y]=p1[Y]-p0[Y];
        nv[Z]=p1[Z]-p0[Z];

        nv[X]/=this.normOfVector(nv);
        nv[Y]/=this.normOfVector(nv);
        nv[Z]/=this.normOfVector(nv);

        this.setLineNormalVector(nv);
        this.setObjPos(new Double[]{0d, 0d, 0d});
        this.setLinePoint(p0);
        this.setRotMatrix(rotmat);
        this.rotateObject(0d, 0d, 0d);
        this.setLineColor(Color.rgb(0, 0, 0, .3));
        this.project();
    }

    public Line3D (Double[] nv, Double[] point,My3DObject container,Color c)
    {
        this.setLineNormalVector(nv);
        this.setRotMatrix(container.getRotMatrix());
        this.setContainer(container);
        this.setLinePoint(point);
        this.setObjPos(new Double[]{0d,0d,0d});
        this.intersecLineWithContainer();
        this.rotateObject(0d, 0d, 0d);
        this.setLineColor(c);
    }

    public void setLineColor(Color c)
    {
        color=c;
    }
    public void intersecLineWithContainer()
    {
        Double[] val;
        ArrayList<Double[]> vert= new ArrayList<>();

        int[] cf ;
        ArrayList<Double[]> cfv;

        // Para cada cara del contenedor.
        for (int i=0;i< container.getObjFace().size();i++)
        {
            cf = container.getObjFace().get(i);
            cfv = new ArrayList<>();
            for (int j=0; j<cf.length;j++)
            {
                cfv.add(container.getTObjVert().get(cf[j]));
            }

            val = Plane3D.intersectPlaneWithLine(new Plane3D(cfv,null),this);

            // revisamos si no esta repetido el vertice.
            boolean addval = true;
            //!!! Importante, al calcularse las itersecciones con las caras se encuentran las intersecciones fuera del objeto, hay que arreglar eso

            if(val!=null)
            {
                if(normOfVector(val)>Math.sqrt(3)+.01)
                    addval =false;
                // Agregamos el nuevo vertice del plano.
                if (addval) { vert.add(val);}
                // Si el denominador es cero la linea vive en el plano, si el denominador es cero el plano y la linea son paralelos.

            }

        }

        this.setObjVertices(vert);
        this.project();
    }

    public void setContainer(My3DObject cnt)
    {
        container = cnt;
    }
    public Double[] getLineNormalVector()
    {
        return  this.getObjNormalVectors().get(0);
    }

    public void setLineNormalVector(Double[] nv)
    {
        ArrayList<Double[]> normalVector= new ArrayList<>();
        normalVector.add(nv);

        this.setNormalVect(normalVector);
    }

    public Double[] getLinePoint()
    {
        return point;
    }
    public void setLinePoint(Double[] p)
    {
        point = p;
    }

    /**Función encargada de dibujar el objeto*/
    public synchronized void draw(GraphicsContext gc,Double[] max, Double[] min, Double w, Double h){

        gc.setLineWidth(5);
        gc.setStroke(color);

        Double[] p0 = FaultViewer.getCanvasPoint(this.getProjection().get(0),max,min,w,h);
        Double[] p1 = FaultViewer.getCanvasPoint(this.getProjection().get(1),max,min,w,h);

        gc.strokeLine(p0[X],p0[Y],p1[X],p1[Y]);

    }


}
