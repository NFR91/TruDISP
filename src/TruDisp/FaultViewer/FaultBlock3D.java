package TruDisp.FaultViewer;

import javafx.scene.paint.Color;

import java.util.ArrayList;

/**
 * Created by Nieto on 20/08/15.
 */
public class FaultBlock3D extends My3DObject {



    public FaultBlock3D()
    {

       ArrayList objAxis = new ArrayList<>(3);
        objAxis.add(0,new Double[]{.5,0.0,0.0});
        objAxis.add(1,new Double[]{0.0,.5,0.0});
        objAxis.add(2,new Double[]{0.0,0.0,.5});


        ArrayList objVert = new ArrayList<>(8);
        objVert.add(0, new Double[]{-1.0, -1.0, -1.0});
        objVert.add(1, new Double[]{1.0, -1.0, -1.0});
        objVert.add(2, new Double[]{1.0, 1.0, -1.0});
        objVert.add(3, new Double[]{-1.0, 1.0, -1.0});
        objVert.add(4, new Double[]{-1.0, -1.0, 1.0});
        objVert.add(5, new Double[]{1.0, -1.0, 1.0});
        objVert.add(6, new Double[]{1.0, 1.0, 1.0});
        objVert.add(7, new Double[]{-1.0, 1.0, 1.0});


        ArrayList objFace = new ArrayList(6);
        objFace.add(0, new int[]{0, 1, 2, 3});
        objFace.add(1, new int[]{1, 5, 6, 2});
        objFace.add(2, new int[]{4, 5, 6, 7});
        objFace.add(3, new int[]{0, 4, 7, 3});
        objFace.add(4, new int[]{3, 2, 6, 7});
        objFace.add(5, new int[]{0, 1, 5, 4});


        ArrayList objFaceColor = new ArrayList<>(6);
        Color color = Color.rgb(0,0,0,.02);
        objFaceColor.add(0, color);
        objFaceColor.add(1, color);
        objFaceColor.add(2,color);
        objFaceColor.add(3,color);
        objFaceColor.add(4,color);
        objFaceColor.add(5, color);

        setObject(objVert, objFace,objFaceColor);

        ArrayList objnormVect = setNormalVect(objVert, objFace);

        setNormalVect(objnormVect);
        reset();
    }
}
