package TruDisp.FaultViewer;

import javafx.scene.paint.Color;

import java.util.ArrayList;

/**
 * Created by Nieto on 17/08/15.
 */
public abstract class My3DObject {

    private ArrayList<Double[]> objVert;
    private ArrayList<Double[]> objTVert;
    private ArrayList<int[]> objFace;
    private ArrayList<Double[]> objProj;
    private ArrayList<Double[]> objnormVect;
    private ArrayList<Color> objFaceColor;
    private Double[] objPos;
    private Double[] rotMatrix;

    public static final int X=0,Y=1,Z=2;

    public My3DObject()
    {
        setObject(new ArrayList<>(), new ArrayList<>(),new ArrayList<>());
        rotMatrix = new Double[]{1.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,1.0};
        setObjPos(new Double[]{0.0, 0.0, 0.0});
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

        ArrayList<Double[]> rotated = new ArrayList<>(objTVert.size());

        for(int i=0; i< objTVert.size();i++)
        {rotated.add(i,rotate(rotMatrix, objTVert.get(i)));}

        objProj = projection(rotated);

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
        rt[3]=r[1]; rt[4]=r[4]; rt[5]=r[7];
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
        objTVert = new ArrayList<>(objVert);
        objProj= projection(objTVert);
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

    public ArrayList<Double[]> getObjNormalVect(){return  objnormVect;}

    public void setObject(ArrayList<Double[]> vert,ArrayList<int[]> face,ArrayList<Color> faceColor)
    {
        objTVert = vert;
        objVert = new ArrayList<>(vert);
        objFace = face;
        objFaceColor = faceColor;
        objnormVect = setNormalVect(objTVert, objFace);
        objProj = projection(objTVert);
    }

    public void setRotMatrix(Double[]rotm)
    {rotMatrix =rotm;}
    public Double[] getRotMatrix()
    {return  rotMatrix;}

    public ArrayList<Double[]> setNormalVect(ArrayList<Double[]> vert, ArrayList<int[]> face)
    {
        ArrayList<Double[]> normvect= new ArrayList<>(face.size());
        Double[] v1 = new Double[3];
        Double[] v2 = new Double[3];

        for(int i=0;i<face.size();i++)
        {
            v1[X] = vert.get(face.get(i)[1])[X]-vert.get(face.get(i)[0])[X];
            v1[Y] = vert.get(face.get(i)[1])[Y]-vert.get(face.get(i)[0])[Y];
            v1[Z] = vert.get(face.get(i)[1])[Z]-vert.get(face.get(i)[0])[Z] ;

            v2[X] = -vert.get(face.get(i)[0])[X] +vert.get(face.get(i)[2])[X];
            v2[Y] = -vert.get(face.get(i)[0])[Y] + vert.get(face.get(i)[2])[Y];
            v2[Z] = -vert.get(face.get(i)[0])[Z] + vert.get(face.get(i)[2])[Z];

            normvect.add(i,crossProduct(v2,v1));
        }

        normvect.stream().forEach(nv-> {
            nv[X] = nv[X]/ normOfVector(nv);
            nv[Y] = nv[Y]/ normOfVector(nv);
            nv[Z] = nv[Z]/ normOfVector(nv);
        });

        return normvect;

    }
    public void setNormalVect(ArrayList<Double[]> nv)
    {
        objnormVect = nv;
    }

    public Double[] crossProduct(Double[] a, Double[] b)
    {
        Double[] axb = new Double[3];

        axb[0] = a[1]*b[2] - a[2]*b[1];
        axb[1] = a[2]*b[0] - a[0]*b[2];
        axb[2] = a[0]*b[1] - a[1]*b[0];

        return axb;
    }
    public Double dotProduct(Double[]a, Double[]b)
    {
        Double adotb=0.0;

        for (int i=0;i<a.length;i++)
        {
            adotb += a[i]*b[i];
        }

        return adotb;
    }
    public Double normOfVector(Double[] a)
    {
        Double n =0.0;
        for(int i=0; i<a.length;i++)
        {
            n += a[i]*a[i];
        }

        n = Math.sqrt(n);

        return n;
    }

    public ArrayList<Double[]> getObjTVert(){return objTVert;}

    public int[] planeOrderVertices(ArrayList<Double[]> vert, Double[] nv,Double[] pos)
    {
        int[] face = new int[vert.size()];
        double[] vc= new double[]{0.0,0.0,0.0};
        ArrayList<Double> angle = new ArrayList<>(vert.size()),angleTemp=new ArrayList<>(vert.size());

        vc[X]=pos[X];vc[Y]=pos[Y];vc[Z]=pos[Z];

        Double[] l1 = new Double[3], l2=new Double[3];

        l1[X]=(vert.get(0)[X]-vc[X]);l1[Y]=(vert.get(0)[Y]-vc[Y]);l1[Z]=(vert.get(0)[Z]-vc[Z]);
        angle.add(0.0);

        for(int i=1; i<vert.size();i++)
        {
            l2[X]=(vert.get(i)[X]-vc[X]);l2[Y]=(vert.get(i)[Y]-vc[Y]);l2[Z]=(vert.get(i)[Z]-vc[Z]);

            if(dotProduct(l1,l2)==0)
            {
                if(dotProduct(nv,crossProduct(l2,l1))>0)
                {angle.add(Math.toDegrees(Math.PI/2));}
                else if (dotProduct(nv,crossProduct(l2,l1))<0)
                {angle.add(Math.toDegrees(3*Math.PI/2));}

            }
            else
            {
                if(normOfVector(crossProduct(l2,l1))==0)
                {angle.add(Math.toDegrees(Math.acos(-1)));}
                else if(dotProduct(nv,crossProduct(l2,l1))<0)
                {angle.add(360-Math.toDegrees(Math.acos(dotProduct(l2,l1)/(normOfVector(l1)*normOfVector(l2)))));}
                else
                {angle.add(Math.toDegrees(Math.acos(dotProduct(l2,l1)/(normOfVector(l1)*normOfVector(l2)))));}

            }
        }

        angle.stream().forEach(a->angleTemp.add(a));
        angleTemp.sort((o1, o2) -> o1.compareTo(o2));

        int temp=0;
        for(int i=0; i<face.length; i++)
        {
            temp=i;
            for (int j=0;j<vert.size();j++)
            {
                if(angleTemp.get(i)==angle.get(j))
                    temp = j;
            }
            face[i]=temp;
        }

        return face;
    }

    public void setObjPos(Double[] objPos) {
        this.objPos = objPos;

        objTVert.stream().forEach(vert -> {
            vert[X] += objPos[X];
            vert[Y] += objPos[Y];
            vert[Z] += objPos[Z];
        });
    }

    public void setObjFaceColor(ArrayList<Color> c)
    {
        objFaceColor = c;
    }

    public ArrayList<Color> getObjFaceColor(){return objFaceColor;}

    public void projectObject()
    {
        objProj = projection(objTVert);
    }
}
