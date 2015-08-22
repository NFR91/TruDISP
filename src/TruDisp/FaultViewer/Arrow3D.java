package TruDisp.FaultViewer;

import javafx.scene.paint.Color;

import java.util.ArrayList;

/**
 * Created by Nieto on 22/08/15.
 */
public class Arrow3D extends My3DObject {

    public Arrow3D()
    {
        super();

        ArrayList objVert = new ArrayList<>();
        objVert.add(0, new Double[]{-.05, -.05, -.25});
        objVert.add(1, new Double[]{-.05, .05, -.25});
        objVert.add(2, new Double[]{.05, .05, -.25});
        objVert.add(3, new Double[]{.05, -.05, -.25});
        objVert.add(4, new Double[]{0.0, 0.0, .25});


        ArrayList objFace = new ArrayList();
        objFace.add(0, new int[]{0, 1, 2, 3});
        objFace.add(1, new int[]{0, 1, 4});
        objFace.add(2, new int[]{1, 4, 2});
        objFace.add(3, new int[]{2, 4, 3});
        objFace.add(4, new int[]{3, 0, 4});


        ArrayList objFaceColor = new ArrayList<>();
        Color color = Color.rgb(0,0,0,.1);
        objFaceColor.add(0, color);
        objFaceColor.add(1, color);
        objFaceColor.add(2,color);
        objFaceColor.add(3,color);
        objFaceColor.add(4, color);


        setObject(objVert, objFace, objFaceColor);

        setNormalVect(setNormalVect(objVert, objFace));

        this.setObjPos(new Double[]{1.0, 0., 0.0});

        projectObject();
    }
}
