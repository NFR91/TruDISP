package TruDisp;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;

/**
 * Created by Nieto on 17/08/15.
 */
public class My3DObject {

    private ArrayList<Double[]> objVert;
    private ArrayList<int[]> objFace;
    private ArrayList<Double[]> objAxis;
    private ArrayList<Double[]> objProj;
    private ArrayList<Double[]> objAxisProj;
    private Double[] objPos;
    private Double[] rotMatrix;

    public static final int X=0,Y=1,Z=2;

    public My3DObject()
    {

        objAxis = new ArrayList<>(3);
        objAxis.add(0,new Double[]{.5,0.0,0.0});
        objAxis.add(1,new Double[]{0.0,.5,0.0});
        objAxis.add(2,new Double[]{0.0,0.0,.5});

        objVert = new ArrayList<>(8);
        objVert.add(0, new Double[]{-1.0, -1.0, -1.0});
        objVert.add(1, new Double[]{1.0, -1.0, -1.0});
        objVert.add(2, new Double[]{1.0, 1.0, -1.0});
        objVert.add(3, new Double[]{-1.0, 1.0, -1.0});
        objVert.add(4, new Double[]{-1.0, -1.0, 1.0});
        objVert.add(5, new Double[]{1.0, -1.0, 1.0});
        objVert.add(6, new Double[]{1.0, 1.0, 1.0});
        objVert.add(7, new Double[]{-1.0, 1.0, 1.0});

        objFace = new ArrayList(5);
        objFace.add(0, new int[]{0, 1, 2, 3});
        objFace.add(1, new int[]{1, 5, 6, 2});
        objFace.add(2, new int[]{4, 5, 6, 7});
        objFace.add(3, new int[]{0, 4, 7, 3});
        objFace.add(4, new int[]{3, 2, 6, 7});
        objFace.add(5, new int[]{0, 1, 5, 4});



        reset();
    }


    public ArrayList<Double[]> projection(ArrayList<Double[]> v)
    {
        ArrayList<Double[]> proj = new ArrayList<>(v.size());

        for(int i=0; i<v.size();i++)
        {proj.add(i,new Double[]{v.get(i)[Y],v.get(i)[X]});}

        return proj;
    }

    public  void rotateObject(Double xd, Double yd,Double zd)
    {
        rotMatrix = matrixPxN(rotMatrix,matrixRotUpdate(xd,yd,zd));

        ArrayList<Double[]> rotated = new ArrayList<>(objVert.size());

        for(int i=0; i< objVert.size();i++)
        {rotated.add(i,rotate(rotMatrix,objVert.get(i)));}

        objProj = projection(rotated);

        rotated.clear();
        for(int i=0; i< objAxis.size();i++)
        {rotated.add(i,rotate(rotMatrix,objAxis.get(i)));}

        objAxisProj = projection(rotated);

    }

    public Double[] matrixPxN(Double[] pr, Double[] nr)
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

    public Double[] matrixRotTranspose(Double[] r)
    {
        Double[] rt= new Double[9];

        rt[0]=r[0]; rt[1]=r[3]; rt[2]=r[6];
        rt[3]=r[1]; rt[4]=r[4]; rt[5]=r[8];
        rt[6]=r[2]; rt[7]=r[5]; rt[8]=r[8];

        return rt;
    }

    public Double[] matrixRotUpdate(Double x,Double y,Double z)
    {
        Double[] a = new Double[]{x,y,z};
        a = rotate(matrixRotTranspose(rotMatrix),a);

        Double[] rd = new Double[9];

        rd[0] =   1.0;  rd[1] = -a[Z];  rd[2] =   a[Y];
        rd[3] =  a[Z];  rd[4] =   1.0;  rd[5] =  -a[X];
        rd[6] = -a[Y];  rd[7] =  a[X];  rd[8] =    1.0;

        return rd;

    }

    public Double[] rotate(Double[] r,Double[] point)
    {
        Double [] vl = new Double [3];

        vl[0] = (r[0]*point[X]) + (r[1]*point[Y]) + (r[2]*point[Z]);
        vl[1] = (r[3]*point[X]) + (r[4]*point[Y]) + (r[5]*point[Z]);
        vl[2] = (r[6]*point[X]) + (r[7]*point[Y]) + (r[8]*point[Z]);

        return vl;

    }

    public void reset()
    {
        rotMatrix = new Double[]{1.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,1.0};
        objProj= projection(objVert);
        objAxisProj= projection(objAxis);
        objPos = new Double[]{0.0,0.0,0.0};
    }
    public ArrayList<Double[]> getProjection()
    {
        return objProj;
    }
    public ArrayList<int[]> getObjFace()
    {
        return objFace;
    }
    public Double[] getObjPos()
    {
        return objPos;
    }
    public ArrayList<Double[]> getObjAxisProj()
    {
        return objAxisProj;
    }
}
