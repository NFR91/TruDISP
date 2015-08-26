/**
 * Created by Nieto on 05/08/15.
 */


package TruDisp;


public class TDData {

    // Variables
    private final int DATA=0,ERROR=1;
    private final Double PRECITION=10.0;
    private final Double DIR_COS_PRECITION=10000.0;
    private TruDispStatusPane statusPane;
    private final Double MINANGLE=0.00157; //.1 grados
    private final Double MAXANGLE=1.5691; //89.9 grados
    private String[] errorValue;
    private String experiment;
    private String notes;
    private Double[] alpha,smh,smd, smA,smB,dAB,s,sv,sd,ss,sh;
    private Double theta,thetanull;
    private String sO;

    // Metodo 1
    private Double[] beta,gamma,phi;
    private String betaO,gammaO,phiO,MapView;


    // Metodo 2
    private Double[] fod,fostk,opod, opostk,smo1d, smo1stk,smo2d, smo2stk,ostrend, osplunge;

    // Metodo 3
    private Double[] fpm,fpn,fpl,opm,opn,opl,apm,apn,apl,bpm,bpn,bpl;


    // Constructor

    public TDData(TruDispStatusPane stbr)
    {
        statusPane = stbr;

        initVariables();


    }

    public TDData(String dataset, TruDispStatusPane stbr)
    {
        statusPane = stbr;
        initVariables();

        String[] buffer = dataset.split(";");

        setExperiment(buffer[0]);
        beta = new Double[] {Double.parseDouble(buffer[1]),Double.parseDouble(buffer[2])};
        betaO= buffer[3];
        gamma =new Double[] {Double.parseDouble(buffer[4]),Double.parseDouble(buffer[5])};
        gammaO= buffer[6];
        phi =new Double[] {Double.parseDouble(buffer[7]),Double.parseDouble(buffer[8])};
        gammaO = buffer[9];
        alpha = new Double[] {Double.parseDouble(buffer[10]),Double.parseDouble(buffer[1])};
        smA =new Double[] {Double.parseDouble(buffer[12]),Double.parseDouble(buffer[13])};
        smd= new Double[] {Double.parseDouble(buffer[14]),Double.parseDouble(buffer[15])};
        smh =new Double[] {Double.parseDouble(buffer[16]),Double.parseDouble(buffer[17])};
        s =  new Double[] {Double.parseDouble(buffer[18]),Double.parseDouble(buffer[19])};
        ss = new Double[] {Double.parseDouble(buffer[20]),Double.parseDouble(buffer[21])};
        sd = new Double[] {Double.parseDouble(buffer[22]),Double.parseDouble(buffer[23])};
        sv = new Double[] {Double.parseDouble(buffer[24]),Double.parseDouble(buffer[25])};
        sh = new Double[] {Double.parseDouble(buffer[26]),Double.parseDouble(buffer[27])};
        theta = Double.parseDouble(buffer[28]);
        thetanull = Double.parseDouble(buffer[29]);

        fod = new Double[] {Double.parseDouble(buffer[30]),Double.parseDouble(buffer[31])};
        fostk = new Double[] {Double.parseDouble(buffer[32]),Double.parseDouble(buffer[33])};
        opod = new Double[] {Double.parseDouble(buffer[34]),Double.parseDouble(buffer[35])};
        opostk = new Double[] {Double.parseDouble(buffer[36]),Double.parseDouble(buffer[37])};
        smo1d = new Double[] {Double.parseDouble(buffer[38]),Double.parseDouble(buffer[39])};
        smo1stk = new Double[] {Double.parseDouble(buffer[40]),Double.parseDouble(buffer[41])};
        smo2d = new Double[] {Double.parseDouble(buffer[42]),Double.parseDouble(buffer[43])};
        smo2stk = new Double[] {Double.parseDouble(buffer[44]),Double.parseDouble(buffer[45])};
        ostrend = new Double[] {Double.parseDouble(buffer[46]),Double.parseDouble(buffer[47])};
        osplunge = new Double[] {Double.parseDouble(buffer[48]),Double.parseDouble(buffer[49])};

        smB = new Double[] {Double.parseDouble(buffer[50]),Double.parseDouble(buffer[51])};
        dAB = new Double[] {Double.parseDouble(buffer[52]),Double.parseDouble(buffer[53])};

        fpl = new Double[] {Double.parseDouble(buffer[54]),Double.parseDouble(buffer[55])};
        fpm = new Double[] {Double.parseDouble(buffer[56]),Double.parseDouble(buffer[57])};
        fpn = new Double[] {Double.parseDouble(buffer[58]),Double.parseDouble(buffer[59])};
        opl = new Double[] {Double.parseDouble(buffer[60]),Double.parseDouble(buffer[61])};
        opm = new Double[] {Double.parseDouble(buffer[62]),Double.parseDouble(buffer[63])};
        opn = new Double[] {Double.parseDouble(buffer[64]),Double.parseDouble(buffer[65])};
        apl = new Double[] {Double.parseDouble(buffer[66]),Double.parseDouble(buffer[67])};
        apm = new Double[] {Double.parseDouble(buffer[68]),Double.parseDouble(buffer[69])};
        apn = new Double[] {Double.parseDouble(buffer[70]),Double.parseDouble(buffer[71])};
        bpl = new Double[] {Double.parseDouble(buffer[72]),Double.parseDouble(buffer[73])};
        bpn = new Double[] {Double.parseDouble(buffer[74]),Double.parseDouble(buffer[75])};
        bpm = new Double[] {Double.parseDouble(buffer[76]),Double.parseDouble(buffer[77])};

        setNotes(buffer[78]);
    }



    public Boolean Calculate(Integer method)
    {
        try {
            // calculo
            switch (method) {
                case 1:
                {
                    s[DATA] = method1Calculate(beta[DATA], gamma[DATA], phi[DATA], smA[DATA], smd[DATA], smh[DATA]);
                    method1CalculateDistance();
                    calculateDistanceErrors();
                    break;
                }
                case 2:
                {
                    method2CalculateDirCosOfPlanes();
                    method2ConvertDirCos2Method1();
                    if(bpn[DATA]!=1 && apn[DATA]!=1)
                    {
                        System.out.println("Calculamos con el método tres.");
                    }
                    else if(isDataValidForCalcuating())
                    {
                        if(!isPlungeValid())
                        {statusPane.setNotification("La estria esta fuera del plano !!!!!",TruDisp.WARNING_ICON);}

                        s[DATA] = method1Calculate(beta[DATA], gamma[DATA], phi[DATA], smA[DATA], smd[DATA], smh[DATA]);
                        method1CalculateDistance();
                        calculateDistanceErrors();
                    }
                    else{return false;}

                    break;
                }
                case 3:
                {
                    break;
                }
            }
            return true;
        }catch(Exception e){System.err.println(e);return false;}

    }

    //*********************************************************************

    public void initVariables()
    {
        /**Método 1*/
        beta = new Double[ ] {0.0,0.0};
        betaO = new String();

        gamma= new Double[ ] {0.0,0.0};
        gammaO = new String();

        phi= new Double[ ] {0.0,0.0};
        phiO= new String();

        alpha= new Double[ ] {0.0,0.0};
        smA = new Double[ ] {0.0,0.0};
        smd= new Double[ ] {0.0,0.0};
        smh= new Double[ ] {0.0,0.0};

        s = new Double[ ] {0.0,0.0};
        ss= new Double[ ] {0.0,0.0};
        sd = new Double[ ] {0.0,0.0};
        sv = new Double[ ] {0.0,0.0};
        sh = new Double[ ] {0.0,0.0};

        theta = 0.0;
        thetanull=0.0;

        setMapView("Map & Sec");

        /**Método 2*/
        fod = new Double[ ] {0.0,0.0};
        fostk = new Double[ ] {0.0,0.0};

        opod= new Double[ ] {0.0,0.0};
        opostk = new Double[ ] {0.0,0.0};
        smo1d= new Double[ ] {0.0,0.0};
        smo1stk = new Double[ ] {0.0,0.0};
        smo2d= new Double[ ] {0.0,0.0};
        smo2stk = new Double[ ] {0.0,0.0};

        osplunge = new Double[ ] {0.0,0.0};
        ostrend = new Double[ ] {0.0,0.0};

        smB = new Double[ ] {0.0,0.0};
        dAB =  new Double[ ] {0.0,0.0};

        /**Método 3*/
        fpn  = new Double[ ] {0.0,0.0};
        fpl = new Double[ ] {0.0,0.0};
        fpm= new Double[ ] {0.0,0.0};

        opl= new Double[ ] {0.0,0.0};
        opn= new Double[ ] {0.0,0.0};
        opm= new Double[ ] {0.0,0.0};

        apn= new Double[ ] {0.0,0.0};
        apl= new Double[ ] {0.0,0.0};
        apm= new Double[ ] {0.0,0.0};

        bpl= new Double[ ] {0.0,0.0};
        bpm= new Double[ ] {0.0,0.0};
        bpn= new Double[ ] {0.0,0.0};

        experiment="";
        notes="";

    }

    /**Set Methods*/

    // Método 1
    public Boolean setBeta(String data,String error,String orientation)
    {
        if(isAngleValid(data))
        {
            beta[DATA]= Math.toRadians(Double.parseDouble(data));
            errorValue = error.split(" ");
            beta[ERROR]= Math.toRadians(Double.parseDouble(errorValue[1]));
            betaO = orientation;
            return true;
        }
        else
        {
            statusPane.setStatus(" ERROR angle beta is not valid", TruDisp.ERROR_ICON);
            return false;
        }
    }
    public Boolean setGamma(String data,String error, String orientation)
    {
        if(isAngleValid(data))
        {
            gamma[DATA]= Math.toRadians(Double.parseDouble(data));
            errorValue = error.split(" ");
            gamma[ERROR]= Math.toRadians(Double.parseDouble(errorValue[1]));
            gammaO = orientation;
            return true;
        }
        else
        {
            statusPane.setStatus(" ERROR angle gamma is not valid", TruDisp.ERROR_ICON);
            return false;
        }
    }
    public Boolean setPhi(String data,String error, String orientation)
    {
        if(isAngleValid(data))
        {
            phi[DATA]= Math.toRadians(Double.parseDouble(data));
            errorValue = error.split(" ");
            phi[ERROR]= Math.toRadians(Double.parseDouble(errorValue[1]));
            phiO = orientation;
            return true;
        }
        else
        {
            statusPane.setStatus(" ERROR angle phi is not valid", TruDisp.ERROR_ICON);
            return false;
        }
    }
    public Boolean setApha(String data,String error)
    {
        if(isAngleValid(data))
        {
            alpha[DATA]= Math.toRadians(Double.parseDouble(data));
            errorValue = error.split(" ");
            alpha[ERROR]=Math.toRadians(Double.parseDouble(errorValue[1]));
            return true;
        }
        else
        {
            statusPane.setStatus("ERROR angle alpha is not valid", TruDisp.ERROR_ICON);
            return false;
        }

    }
    public Boolean setSmA(String data, String error)
    {
        if(isDisplacementValid(data))
        {
            smA[DATA]=Double.parseDouble(data);
            errorValue = error.split(" ");
            smA[ERROR]=Double.parseDouble(errorValue[1]);
            return true;
        }
        else
        {
            statusPane.setStatus("ERROR distance Sm is not valid", TruDisp.ERROR_ICON);
            return false;
        }

    }
    public Boolean setSmd(String data,String error)
    {
        if(isDisplacementValid(data))
        {
            smd[DATA]=Double.parseDouble(data);
            errorValue = error.split(" ");
            smd[ERROR]=Double.parseDouble(errorValue[1]);
            return true;
        }
        else
        {
            statusPane.setStatus("ERROR distance Smd is not valid", TruDisp.ERROR_ICON);
            return false;
        }

    }
    public Boolean setSmh(String data,String error)
    {
        if(isDisplacementValid(data))
        {
            smh[DATA]=Double.parseDouble(data);
            errorValue = error.split(" ");
            smh[ERROR]=Double.parseDouble(errorValue[1]);
            return true;
        }
        else
        {
            statusPane.setStatus("ERROR distance Smh is not valid", TruDisp.ERROR_ICON);
            return false;
        }

    }
    public Boolean setMapView(String view)
    {
        MapView=view;
        return true;
    }

    // Método 2

    public Boolean setFod(String data, String error)
    {
        if(isAngleValid(data))
        {
            fod[DATA] = Math.toRadians(Double.parseDouble(data));
            errorValue = error.split(" ");
            fod[ERROR]= Math.toRadians(Double.parseDouble(errorValue[1]));
            return true;
        }
        else
        {
            statusPane.setStatus("ERROR angle d D of fault plane is not valid",TruDisp.ERROR_ICON);
            return false;
        }

    }
    public Boolean setFoStk(String data, String error)
    {
        if(isStrikeAngleValid(data))
        {
            fostk[DATA] = Math.toRadians(Double.parseDouble(data));
            errorValue = error.split(" ");
            fostk[ERROR]= Math.toRadians(Double.parseDouble(errorValue[1]));
            return true;
        }
        else
        {
            statusPane.setStatus("ERROR angle  DD of fault plane is not valid",TruDisp.ERROR_ICON);
            return false;
        }

    }
    public Boolean setOpod(String data, String error)
    {
        if(isAngleValid(data))
        {
            opod[DATA] = Math.toRadians(Double.parseDouble(data));
            errorValue = error.split(" ");
            opod[ERROR]= Math.toRadians(Double.parseDouble(errorValue[1]));
            return true;
        }
        else
        {
            statusPane.setStatus("ERROR angle D of observation plane is not valid",TruDisp.ERROR_ICON);
            return false;
        }
    }
    public Boolean setOpoStk(String data, String error)
    {
        if(isStrikeAngleValid(data))
        {
            opostk[DATA] = Math.toRadians(Double.parseDouble(data));
            errorValue = error.split(" ");
            opostk[ERROR]= Math.toRadians(Double.parseDouble(errorValue[1]));
            return true;
        }
        else
        {
            statusPane.setStatus("ERROR angle d DD of observation plane is not valid",TruDisp.ERROR_ICON);
            return false;
        }
    }
    public Boolean setSmo1d(String data, String error)
    {
        if(isAngleValid(data))
        {
            smo1d[DATA] = Math.toRadians(Double.parseDouble(data));
            errorValue = error.split(" ");
            smo1d[ERROR]= Math.toRadians(Double.parseDouble(errorValue[1]));
            return true;
        }
        else
        {
            statusPane.setStatus("ERROR angle D of marker 1  is not valid",TruDisp.ERROR_ICON);
            return false;
        }
    }
    public Boolean setSmo1Stk(String data, String error)
    {

        if(isStrikeAngleValid(data))
        {
            smo1stk[DATA] = Math.toRadians(Double.parseDouble(data));
            errorValue = error.split(" ");
            smo1stk[ERROR]= Math.toRadians(Double.parseDouble(errorValue[1]));
            return true;
        }
        else
        {
            statusPane.setStatus("ERROR angle DD of marker 1  is not valid",TruDisp.ERROR_ICON);
            return false;
        }
    }
    public Boolean setSmo2d(String data, String error)
    {

        if(isAngleValid(data))
        {
            smo2d[DATA] = Math.toRadians(Double.parseDouble(data));
            errorValue = error.split(" ");
            smo2d[ERROR]= Math.toRadians(Double.parseDouble(errorValue[1]));
            return true;
        }
        else
        {
            statusPane.setStatus("ERROR angle D of marker 2  is not valid",TruDisp.ERROR_ICON);
            return false;
        }
    }
    public Boolean setSmo2Stk(String data, String error)
    {

        if(isStrikeAngleValid(data))
        {
            smo2stk[DATA] = Math.toRadians(Double.parseDouble(data));
            errorValue = error.split(" ");
            smo2stk[ERROR]= Math.toRadians(Double.parseDouble(errorValue[1]));
            return true;
        }
        else
        {
            statusPane.setStatus("ERROR angle DD of marker 2  is not valid",TruDisp.ERROR_ICON);
            return false;
        }
    }
    public Boolean setOsTrend(String data,String error)
    {

        if(isStrikeAngleValid(data))
        {
            ostrend[DATA] = Math.toRadians(Double.parseDouble(data));
            errorValue = error.split(" ");
            ostrend[ERROR]= Math.toRadians(Double.parseDouble(errorValue[1]));
            return true;
        }
        else
        {
            statusPane.setStatus("ERROR angle Trend is not valid",TruDisp.ERROR_ICON);
            return false;
        }
    }
    public Boolean setOsPlunge(String data, String error)
    {
        if(isAngleValid(data))
        {
            osplunge[DATA] = Math.toRadians(Double.parseDouble(data));
            errorValue = error.split(" ");
            osplunge[ERROR]= Math.toRadians(Double.parseDouble(errorValue[1]));
            return true;
        }
        else
        {
            statusPane.setStatus("ERROR angle Plunge  is not valid",TruDisp.ERROR_ICON);
            return false;
        }
    }

    public Boolean setSmB(String data,String error)
    {
        if(isDisplacementValid(data))
        {
            smB[DATA]=Double.parseDouble(data);
            errorValue = error.split(" ");
            smB[ERROR]=Double.parseDouble(errorValue[1]);
            return true;
        }
        else
        {
            statusPane.setStatus("ERROR distance SmB is not valid", TruDisp.ERROR_ICON);
            return false;
        }
    }
    public Boolean setdAB(String data,String error)
    {
        if(isDisplacementValid(data))
        {
            dAB[DATA]=Double.parseDouble(data);
            errorValue = error.split(" ");
            dAB[ERROR]=Double.parseDouble(errorValue[1]);
            return true;
        }
        else
        {
            statusPane.setStatus("ERROR distance dAB is not valid", TruDisp.ERROR_ICON);
            return false;
        }
    }

    // Método 3


    public Boolean setFpl(String data, String error)
    {
        if(isDirCosineValid(data))
        {
            fpl[DATA] = (Double.parseDouble(data));
            errorValue = error.split(" ");
            fpl[ERROR]= (Double.parseDouble(errorValue[1]));
            return true;
        }
        else
        {
            statusPane.setStatus("ERROR fpl is not valid",TruDisp.ERROR_ICON);
            return false;
        }
    }
    public Boolean setFpm(String data, String error)
    {
        if(isDirCosineValid(data))
        {
            fpm[DATA] = (Double.parseDouble(data));
            errorValue = error.split(" ");
            fpm[ERROR]= (Double.parseDouble(errorValue[1]));
            return true;
        }
        else
        {
            statusPane.setStatus("ERROR fpm is not valid",TruDisp.ERROR_ICON);
            return false;
        }
    }
    public Boolean setFpn(String data, String error)
    {
        if(isDirCosineValid(data))
        {
            fpn[DATA] = (Double.parseDouble(data));
            errorValue = error.split(" ");
            fpn[ERROR]= (Double.parseDouble(errorValue[1]));
            return true;
        }
        else
        {
            statusPane.setStatus("ERROR fpn is not valid",TruDisp.ERROR_ICON);
            return false;
        }
    }
    public Boolean setOpl(String data, String error)
    {
        if(isDirCosineValid(data))
        {
            opl[DATA] = (Double.parseDouble(data));
            errorValue = error.split(" ");
            opl[ERROR]= (Double.parseDouble(errorValue[1]));
            return true;
        }
        else
        {
            statusPane.setStatus("ERROR opl is not valid",TruDisp.ERROR_ICON);
            return false;
        }
    }
    public Boolean setOpn(String data, String error)
    {

        if(isDirCosineValid(data))
        {
            opn[DATA] = (Double.parseDouble(data));
            errorValue = error.split(" ");
            opn[ERROR]= (Double.parseDouble(errorValue[1]));
            return true;
        }
        else
        {
            statusPane.setStatus("ERROR opn is not valid",TruDisp.ERROR_ICON);
            return false;
        }
    }
    public Boolean setOpm(String data, String error)
    {

        if(isDirCosineValid(data))
        {
            opm[DATA] = (Double.parseDouble(data));
            errorValue = error.split(" ");
            opm[ERROR]= (Double.parseDouble(errorValue[1]));
            return true;
        }
        else
        {
            statusPane.setStatus("ERROR opm is not valid",TruDisp.ERROR_ICON);
            return false;
        }
    }
    public Boolean setApl(String data, String error)
    {

        if(isDirCosineValid(data))
        {
            apl[DATA] = (Double.parseDouble(data));
            errorValue = error.split(" ");
            apl[ERROR]= (Double.parseDouble(errorValue[1]));
            return true;
        }
        else
        {
            statusPane.setStatus("ERROR apl is not valid",TruDisp.ERROR_ICON);
            return false;
        }
    }
    public Boolean setApn(String data, String error)
    {
        if(isDirCosineValid(data))
        {
            apn[DATA] = (Double.parseDouble(data));
            errorValue = error.split(" ");
            apn[ERROR]= (Double.parseDouble(errorValue[1]));
            return true;
        }
        else
        {
            statusPane.setStatus("ERROR apn is not valid",TruDisp.ERROR_ICON);
            return false;
        }
    }
    public Boolean setApm(String data, String error)
    {
        if(isDirCosineValid(data))
        {
            apm[DATA] = (Double.parseDouble(data));
            errorValue = error.split(" ");
            apm[ERROR]= (Double.parseDouble(errorValue[1]));
            return true;
        }
        else
        {
            statusPane.setStatus("ERROR apm is not valid",TruDisp.ERROR_ICON);
            return false;
        }
    }
    public Boolean setBpl(String data, String error)
    {
        if(isDirCosineValid(data))
        {
            bpl[DATA] = (Double.parseDouble(data));
            errorValue = error.split(" ");
            bpl[ERROR]= (Double.parseDouble(errorValue[1]));
            return true;
        }
        else
        {
            statusPane.setStatus("ERROR bpl is not valid",TruDisp.ERROR_ICON);
            return false;
        }
    }
    public Boolean setBpn(String data, String error)
    {
        if(isDirCosineValid(data))
        {
            bpn[DATA] = (Double.parseDouble(data));
            errorValue = error.split(" ");
            bpn[ERROR]= (Double.parseDouble(errorValue[1]));
            return true;
        }
        else
        {
            statusPane.setStatus("ERROR bpn is not valid",TruDisp.ERROR_ICON);
            return false;
        }
    }
    public Boolean setBpm(String data, String error)
    {
        if(isDirCosineValid(data))
        {
            bpm[DATA] = (Double.parseDouble(data));
            errorValue = error.split(" ");
            bpm[ERROR]= (Double.parseDouble(errorValue[1]));
            return true;
        }
        else
        {
            statusPane.setStatus("ERROR bpm is not valid",TruDisp.ERROR_ICON);
            return false;
        }
    }

    // General

    public Boolean setExperiment(String exp)
    {
        experiment = exp;
        return true;
    }

    public Boolean setNotes(String nts)
    {notes=nts; return true;}

    public Double dotProductAngle(Double[] a, Double[] b){
        // Calculamos con el prodcuto punto el pitch.

        return Math.acos((a[0]*b[0])+(a[1]*b[1])+(a[2]*b[2]));
    }

    public Double[] crossProduct(Double[] a, Double[] b)
    {
        Double[] axb = new Double[3];

        axb[0] = a[1]*b[2] - a[2]*b[1];
        axb[1] = a[2]*b[0] - a[0]*b[2];
        axb[2] = a[0]*b[1] - a[1]*b[0];

        return axb;
    }



    /**Métodos de validación*/

    public Boolean isDataValidForCalcuating()
    {
        if((smA[DATA] != 0)||(smh[DATA] !=0)||(smd[DATA]!=0) )
        {
            orientationIndeterminated();

            if(isBetaNearGamma())
            {statusPane.setNotification(" WARNING\n" +
                    "Θ is small.\n In this condition a very small error in the angular measures will produce a large devuation in the resutls." +
                    "\nWe do not recommend calculating with Θ < 20º. ", TruDisp.WARNING_ICON);}

            if(MapView.equalsIgnoreCase("Arbitrary Line"))
            {
                if(isBetaNearPhi())
                {statusPane.setNotification(" WARNING\n " +
                        "You are near the \"null line \".\nIn this condition (β near γ) a very small error in the angular measures will produce" +
                        "a large deviaton in the results." +
                        "\nWe do not recommend calculating with Θnull < 10º. ", TruDisp.WARNING_ICON);}
            }

            return  true;
        }
        else{
            statusPane.setStatus("ERROR al menos uno de los desplazamientos debe ser mayor a 0", TruDisp.ERROR_ICON);
            return false;
        }
    }

    public void orientationIndeterminated()
    {
        beta[DATA] = (beta[DATA] < MINANGLE)? (MINANGLE):(beta[DATA]);
        gamma[DATA] = (gamma[DATA] < MINANGLE)? (MINANGLE):(gamma[DATA]);
        phi[DATA] = (phi[DATA] < MINANGLE)? (MINANGLE):(phi[DATA]);

        beta[DATA] = (beta[DATA] > MAXANGLE)? (MAXANGLE):(beta[DATA]);
        gamma[DATA] = (gamma[DATA] > MAXANGLE)? (MAXANGLE):(gamma[DATA]);
        phi[DATA] = (phi[DATA] > MAXANGLE)? (MAXANGLE):(phi[DATA]);


    }

    public Boolean isBetaNearGamma()
    {
        // If its opposite direction we use 180-(gamma+betta) if its the same position we use gamma-betta.
        if(betaO.equalsIgnoreCase(gammaO))
        {theta = Math.abs(beta[DATA]-gamma[DATA]);}
        else
        {theta = Math.PI - (beta[DATA]+gamma[DATA]);}
        // we check if <20;
        if(theta < (Math.PI/9))
        {
            // If theta is equal to cero it means they are paralels thers no way to calculate that, so we add the 1% to the beta angle.
            if(theta == 0)
            { beta[DATA] = (1+0.01)*beta[DATA];}
            return true;
        }
        return false;
    }

    public Boolean isBetaNearPhi()
    {
        if(betaO.equalsIgnoreCase(phiO))
        { thetanull = Math.abs(beta[DATA]-phi[DATA]);}
        else
        {thetanull = Math.PI-(beta[DATA]+phi[DATA]);}

        // We check if its <10;
        if(thetanull < (Math.PI/18))
        {
            if(thetanull == 0)
            {beta[DATA] = (1+0.01)*beta[DATA];}
            return true;
        }
        return false;
    }

    public Boolean isAngleValid(String data)
    {
        return (isNumber(data)) ? ( (Double.parseDouble(data)<0 || Double.parseDouble(data)>90) ? false : true ) : false;
    }

    public Boolean isDisplacementValid(String data)
    {
        return (isNumber(data)) ? ((Double.parseDouble(data)>=0) ? true : false ) : false;
    }

    public Boolean isNumber(String data)
    {
        try{
            Double.parseDouble(data);
        }catch (Exception e)
        {
            return false;
        }

        return true;
    }

    public Boolean isStrikeAngleValid(String data)
    {
        return (isNumber(data)) ? ((Double.parseDouble(data)<0||Double.parseDouble(data)>360)? false:true):false;
    }

    public Boolean isDirCosineValid(String data)
    {
        return (isNumber(data))? ((Double.parseDouble(data)<0||Double.parseDouble(data)>1)? false:true ):false;
    }

    public Boolean isPlungeValid()
    {
        // Calculamos el plunge para asegurar que la estria está siempre sobre el plano.
        Double plunge = Math.atan(Math.tan(fod[DATA]) * Math.sin(((ostrend[DATA] - fostk[DATA]) > Math.PI / 2) ? (Math.PI - (ostrend[DATA] - fostk[DATA])) : (ostrend[DATA] - fostk[DATA])));

        if( Math.abs(osplunge[DATA]-plunge)> Math.toRadians(4))
        {return false;}
        else
        {return  true;}
    }

    /**Métodos de procesamiento de datos.*/


    // Método 1
    public Double method1Calculate(Double beta, Double gamma, Double phi, Double sm, Double smd, Double smh)
    {
        switch (MapView)
        {
            case "Map & Sec" :
            {
                if(betaO.equalsIgnoreCase(gammaO)) // Same Direction
                {
                    if(beta > gamma)
                    {
                        if(smd != 0)// Vertical Cross-section
                        { return  smd/((Math.cos(gamma)*Math.tan(beta))-Math.sin(gamma));}

                        if(smh !=0 )// Map View
                        { return  smh/((Math.tan(beta)*Math.cos(gamma))-Math.sin(gamma));}
                    }

                    if(beta < gamma)
                    {
                        if(smd != 0) // Vertical Crooss-section
                        { return smd/(Math.sin(gamma)-(Math.cos(gamma)*Math.tan(beta)));}

                        if(smh != 0) // Mapv view
                        { return (smh*Math.tan(beta))/(Math.sin(gamma)*(Math.tan(beta)*Math.cos(gamma)));}
                    }
                }
                else // Oposite Direction
                {
                    if(smd != 0) // Vertical cross-section
                    { return smd/ (Math.sin(gamma)+ (Math.cos(gamma)*Math.tan(beta))); }

                    if(smh != 0) // Mapview;
                    { return (smh*Math.tan(beta))/( (Math.tan(beta)*Math.cos(gamma)) + Math.sin(gamma));}
                }

                break;
            }


            case "Arbitrary Line":
            {
                if(betaO.equalsIgnoreCase(gammaO)) // Same Direction
                {
                    if(beta > gamma)
                    {
                        if(phiO.equalsIgnoreCase(gammaO))// Same Direction
                        {
                            if(sm != 0)
                            {
                                if(phi > beta)
                                { return (sm*Math.sin(phi-beta))/Math.sin(beta-gamma);}

                                if(phi < beta)
                                { return (sm*Math.sin(beta-phi))/Math.sin(beta-gamma);}
                            }

                            if(smd != 0)
                            { return (smd*Math.cos(beta))/Math.sin(beta-gamma);}
                        }
                        else // Opposite Direction
                        { return (sm*Math.sin(phi+beta))/Math.sin(beta-gamma);}
                    }

                    if(beta < gamma)
                    {
                        if(phiO.equalsIgnoreCase(gammaO)) // Same Direction
                        {
                            if(sm != 0)
                            {
                                if(phi > beta)
                                { return (sm*Math.sin(phi-beta))/Math.sin(gamma-beta);}

                                if(phi < beta)
                                { return (sm*Math.sin(beta-phi))/Math.sin(gamma-beta);}
                            }

                            if(smd != 0)
                            { return (smd*Math.cos(beta))/Math.sin(gamma-beta);}
                        }
                        else // Opposite Direction
                        {return (sm*Math.sin(phi+beta))/Math.sin(gamma-beta);}

                    }

                }
                else // Opposite direction
                {
                    if(phiO.equalsIgnoreCase(gammaO))// Same Direction
                    {
                        if(sm != 0)
                        { return (sm*Math.sin(phi+beta))/Math.sin(gamma+beta);}

                        if(smd != 0)
                        { return (smd*Math.cos(beta))/Math.sin(gamma+beta);}
                    }
                    else // Opposite direction
                    {
                        if(phi > beta)
                        { return (sm*Math.sin(phi-beta))/Math.sin(gamma+beta);}

                        if(phi < beta)
                        { return (sm*Math.sin(beta-phi))/Math.sin(gamma+beta);}
                    }
                }
                break;
            }
        }
        return 0.0;
    }
    public Double method1CalculateSs(Double s, Double gamma)
    { return s*Math.cos(gamma);}
    public Double method1CalculateSd(Double s, Double gamma)
    {return s*Math.sin(gamma);}
    public Double method1CalculateSv(Double s, Double gamma, Double alpha)
    {return s*Math.sin(gamma)*Math.sin(alpha);}
    public Double method1CalculateSh(Double s, Double gamma, Double alpha)
    {return s*Math.sin(gamma)*Math.cos(alpha);}

    public void method1CalculateDistance()
    {
        ss[DATA] = method1CalculateSs(s[DATA], gamma[DATA]);
        sd[DATA] = method1CalculateSd(s[DATA], gamma[DATA]);
        sv[DATA] = method1CalculateSv(s[DATA], gamma[DATA], alpha[DATA]);
        sh[DATA] = method1CalculateSh(s[DATA], gamma[DATA], alpha[DATA]);
    }

    public void calculateDistanceErrors()
    {
        Double h=0.001,f,i,pdbeta,pdgamma,pdphi,pdsm,pdsmd,pdsmh;


        // Derivada parcial  ds/db
        f = method1Calculate(beta[DATA] + h, gamma[DATA], phi[DATA], smA[DATA], smd[DATA], smh[DATA]);
        i = method1Calculate(beta[DATA] - h, gamma[DATA], phi[DATA], smA[DATA], smd[DATA], smh[DATA]);
        pdbeta= Math.abs((f - i) / (2 * h));

        // Derivada parcial ds/dgamma
        f = method1Calculate(beta[DATA], gamma[DATA] + h, phi[DATA], smA[DATA], smd[DATA], smh[DATA]);
        i = method1Calculate(beta[DATA], gamma[DATA] - h, phi[DATA], smA[DATA], smd[DATA], smh[DATA]);
        pdgamma= Math.abs((f - i) / (2 * h));

        // Derivada parcial ds/dphi
        f = method1Calculate(beta[DATA], gamma[DATA], phi[DATA] + h, smA[DATA], smd[DATA], smh[DATA]);
        i = method1Calculate(beta[DATA], gamma[DATA], phi[DATA] - h, smA[DATA], smd[DATA], smh[DATA]);
        pdphi = Math.abs((f - i) / (2 * h));

        // Derivada parcial ds/dsm
        f = method1Calculate(beta[DATA], gamma[DATA], phi[DATA], smA[DATA] + h, smd[DATA], smh[DATA]);
        i = method1Calculate(beta[DATA], gamma[DATA], phi[DATA], smA[DATA] - h, smd[DATA], smh[DATA]);
        pdsm= Math.abs((f - i) / (2 * h));

        // Derivada parcial ds/dsmd
        f = method1Calculate(beta[DATA], gamma[DATA], phi[DATA], smA[DATA], smd[DATA] + h, smh[DATA]);
        i = method1Calculate(beta[DATA], gamma[DATA], phi[DATA], smA[DATA], smd[DATA] - h, smh[DATA]);
        pdsmd= Math.abs((f - i) / (2 * h));

        // Derivada parcial ds/dsmh
        f = method1Calculate(beta[DATA], gamma[DATA], phi[DATA], smA[DATA], smd[DATA], smh[DATA] + h);
        i = method1Calculate(beta[DATA], gamma[DATA], phi[DATA], smA[DATA], smd[DATA], smh[DATA] - h);
        pdsmh= Math.abs((f - i) / (2 * h));


        s[ERROR] = (pdbeta*beta[ERROR])+(pdgamma*gamma[ERROR])+(pdphi*phi[ERROR])+(pdsm* smA[ERROR])+(pdsmd*smd[ERROR])+(pdsmh*smh[ERROR]);

        //
        Double pds,pdalpha;

        // Derivada parcial dss/ds
        f = method1CalculateSs(s[DATA] + h, gamma[DATA]);
        i = method1CalculateSs(s[DATA] - h, gamma[DATA]);
        pds = Math.abs((f - i) / (2 * h));
        // Derivada parcial dss/dgamma
        f = method1CalculateSs(s[DATA], gamma[DATA] + h);
        i = method1CalculateSs(s[DATA], gamma[DATA] - h);
        pdgamma = Math.abs((f - i) / (2 * h));

        ss[ERROR] = (pds*s[ERROR])+(pdgamma*gamma[ERROR]);

        // Derivada parcial dsd/ds
        f = method1CalculateSd(s[DATA] + h, gamma[DATA]);
        i = method1CalculateSd(s[DATA] - h, gamma[DATA]);
        pds = Math.abs((f - i) / (2 * h));
        // Derivada parcial dsd/dgamma
        f = method1CalculateSd(s[DATA], gamma[DATA] + h);
        i = method1CalculateSd(s[DATA], gamma[DATA] - h);
        pdgamma = Math.abs((f - i) / (2 * h));

        sd[ERROR] = (pds*s[ERROR])+(pdgamma*gamma[ERROR]);

        // Derivada parcial dsv/ds
        f = method1CalculateSv(s[DATA] + h, gamma[DATA], alpha[DATA]);
        i = method1CalculateSv(s[DATA] - h, gamma[DATA], alpha[DATA]);
        pds = Math.abs((f - i) / (2 * h));
        // Derivada parcial dsv/dgamma
        f = method1CalculateSv(s[DATA], gamma[DATA] + h, alpha[DATA]);
        i = method1CalculateSv(s[DATA], gamma[DATA] - h, alpha[DATA]);
        pdgamma = Math.abs((f - i) / (2 * h));
        // Derivada parcial dsv/dalpha
        f = method1CalculateSv(s[DATA], gamma[DATA], alpha[DATA] + h);
        i = method1CalculateSv(s[DATA], gamma[DATA], alpha[DATA] - h);
        pdalpha = Math.abs((f - i) / (2 * h));

        sv[ERROR] = (pds*s[ERROR])+(pdgamma*gamma[ERROR])+(pdalpha*alpha[ERROR]);

        // Derivada parcial dsh/ds
        f = method1CalculateSh(s[DATA] + h, gamma[DATA], alpha[DATA]);
        i = method1CalculateSh(s[DATA] - h, gamma[DATA], alpha[DATA]);
        pds = Math.abs((f - i) / (2 * h));
        // Derivada parcial dsh/dgamma
        f = method1CalculateSh(s[DATA], gamma[DATA] + h, alpha[DATA]);
        i = method1CalculateSh(s[DATA], gamma[DATA] - h, alpha[DATA]);
        pdgamma = Math.abs((f - i) / (2 * h));
        // Derivada parcial dsh/dalpha
        f = method1CalculateSh(s[DATA], gamma[DATA], alpha[DATA] + h);
        i = method1CalculateSh(s[DATA], gamma[DATA], alpha[DATA] - h);
        pdalpha = Math.abs((f - i) / (2 * h));

        sh[ERROR] = (pds*s[ERROR])+(pdgamma*gamma[ERROR])+(pdalpha*alpha[ERROR]);

    }

    // Método 2

    public void method2CalculateDirCosOfPlanes()
    {
        Double[] temp = method2CalculateDirCosPlane(fostk[DATA], fod[DATA]);
        fpl[DATA] = temp[TruDisp.E];
        fpm[DATA] = temp[TruDisp.N];
        fpn[DATA] = temp[TruDisp.D];

        temp = method2CalculateDirCosPlane(opostk[DATA], opod[DATA]);
        opl[DATA]=temp[TruDisp.E];
        opm[DATA]=temp[TruDisp.N];
        opn[DATA]=temp[TruDisp.D];

        temp = method2CalculateDirCosPlane(smo1stk[DATA], smo1d[DATA]);
        apl[DATA]=temp[TruDisp.E];
        apm[DATA]=temp[TruDisp.N];
        apn[DATA]=temp[TruDisp.D];

        temp = method2CalculateDirCosPlane(smo2stk[DATA], smo2d[DATA]);
        bpl[DATA]=temp[TruDisp.E];
        bpm[DATA]=temp[TruDisp.N];
        bpn[DATA]=temp[TruDisp.D];


    }

    public void method2ConvertDirCos2Method1()
    {
        // Obtenmos los pitch;

        Double[] fp = new Double[]{fpm[DATA],fpl[DATA],fpn[DATA]}; // Cosenos Directores del plano de Falla (Fault Plane)
        Double[] stkLine = new Double[]{Math.cos(fostk[DATA]),Math.sin(fostk[DATA]),0.0}; // Cosenos Directores de línea de strike del Plano de falla;
        Double[] mrkr = new Double[]{apm[DATA],apl[DATA],apn[DATA]}; // Cosenos Directores del plano A (Marcador 1)
        Double[] op = new Double[]{opm[DATA],opl[DATA],opn[DATA]}; // Cosenos Directores del plano de observación.(Obsevation Plane)

        // Calculamos el plunge para asegurar que la estria está siempre sobre el plano.
        Double plunge = Math.atan(Math.tan(fod[DATA]) * Math.sin(((ostrend[DATA] - fostk[DATA]) > Math.PI / 2) ? (Math.PI - (ostrend[DATA] - fostk[DATA])) : (ostrend[DATA] - fostk[DATA])));
        Double[] striae = method2CalculateDirCosLine(ostrend[DATA], plunge); // Cosenos directores de linea de la estria.

        Double stk = fostk[DATA];   // Dirección de la linea stk en grados, con la que se compara si esta hacia el norte o el sur

        beta[DATA] = dotProductAngle(crossProduct(fp, mrkr), stkLine);
        betaO = (stk< (Math.PI/2) || stk>(3*Math.PI/2))? ((beta[DATA]>(Math.PI/2))? "S":"N"):((beta[DATA]>(Math.PI/2))? "N":"S");
        beta[DATA]=(beta[DATA]>(Math.PI/2))? (Math.PI-beta[DATA]):(beta[DATA]);

        phi[DATA] = dotProductAngle(crossProduct(fp, op), stkLine);
        phiO =(stk< (Math.PI/2) || stk>(3*Math.PI/2))? ((phi[DATA]>(Math.PI/2))? "S":"N"):((phi[DATA]>(Math.PI/2))? "N":"S");
        phi[DATA]=(phi[DATA]>(Math.PI/2))? (Math.PI-phi[DATA]):(phi[DATA]);

        alpha[DATA] = fod[DATA];

        if(gamma[DATA]==0) // Necesitamos calcular gamma.
        {
            gamma[DATA] = dotProductAngle(striae, stkLine);
            gammaO =(stk< (Math.PI/2) || stk>(3*Math.PI/2))? ((gamma[DATA]>(Math.PI/2))? "S":"N"):((gamma[DATA]>(Math.PI/2))? "N":"S");
            gamma[DATA]=(gamma[DATA]>(Math.PI/2))? (Math.PI-gamma[DATA]):(gamma[DATA]);
        }

        MapView = "Arbitrary Line";

    }

    public Double[] method2CalculateDirCosPlane(Double strike, Double dip)
    {
        Double[] temp= new Double[] {0.0,0.0,0.0};

        temp[TruDisp.N] = Math.sin(strike)*Math.sin(dip);
        temp[TruDisp.E] = -Math.cos(strike)*Math.sin(dip);
        temp[TruDisp.D] = Math.sqrt(1 - ((temp[0] * temp[0]) + (temp[1] * temp[1])));

        return temp;
    }

    public Double[] method2CalculateDirCosLine(Double trend, Double plunge)
    {
        Double[] temp= new Double[] {0.0,0.0,0.0};

        temp[TruDisp.N] = Math.cos(trend)*Math.cos(plunge);
        temp[TruDisp.E] = Math.sin(trend)*Math.cos(plunge);
        temp[TruDisp.D] = Math.sqrt(1 - ((temp[0] * temp[0]) + (temp[1] * temp[1])));

        return temp;
    }


    /**Metodos Get*/

    public Double getS()
    {return (double)Math.round(s[DATA]*PRECITION)/PRECITION;}
    public Double getSError()
    {return (double)Math.round(s[ERROR]*PRECITION)/PRECITION;}
    public Double getSs()
    {return (double)Math.round(ss[DATA]*PRECITION)/PRECITION;}
    public Double getSsError()
    {return (double)Math.round(ss[ERROR]*PRECITION)/PRECITION;}
    public Double getSd()
    {return (double)Math.round(sd[DATA]*PRECITION)/PRECITION;}
    public Double getSdError()
    {return (double)Math.round(sd[ERROR]*PRECITION)/PRECITION;}
    public Double getSv()
    {return (double)Math.round(sv[DATA]*PRECITION)/PRECITION;}
    public Double getSvError()
    {return (double)Math.round(sv[ERROR]*PRECITION)/PRECITION;}
    public Double getSh()
    {return (double)Math.round(sh[DATA]*PRECITION)/PRECITION;}
    public Double getShError()
    {return (double)Math.round(sh[ERROR]*PRECITION)/PRECITION;}

    public Double getTheta()
    {return  Math.round(Math.toDegrees(theta)*PRECITION)/PRECITION;}
    public Double getThetaNull()
    {return  Math.round(Math.toDegrees(thetanull)*PRECITION)/PRECITION;}


    public Double getBeta()
    {return (double)Math.round(Math.toDegrees(beta[DATA])*PRECITION)/PRECITION;}
    public Double getBetaError()
    {return (double)Math.round(Math.toDegrees(beta[ERROR])*PRECITION)/PRECITION;}
    public String getBetaOrientation()
    {return  betaO;}
    public Double getGamma()
    {return (double)Math.round(Math.toDegrees(gamma[DATA])*PRECITION)/PRECITION;}
    public Double getGammaError()
    {return (double)Math.round(Math.toDegrees(gamma[ERROR])*PRECITION)/PRECITION;}
    public String getGammaOrientation()
    {return gammaO;}
    public Double getPhi()
    {return (double)Math.round(Math.toDegrees(phi[DATA])*PRECITION)/PRECITION;}
    public Double getPhiError()
    {return (double)Math.round(Math.toDegrees(phi[ERROR])*PRECITION)/PRECITION;}
    public String getPhiOrientation()
    {return phiO;}
    public Double getAlpha()
    {return (double)Math.round(Math.toDegrees(alpha[DATA])*PRECITION)/PRECITION;}
    public Double getAlphaError()
    {return (double)Math.round(Math.toDegrees(alpha[ERROR])*PRECITION)/PRECITION;}
    public Double getSmA()
    {return (double)Math.round(smA[DATA]*PRECITION)/PRECITION;}
    public Double getSmAError()
    {return (double)Math.round(smA[ERROR]*PRECITION)/PRECITION;}
    public Double getSmd()
    {return (double)Math.round(smd[DATA]*PRECITION)/PRECITION;}
    public Double getsmdError()
    {return (double)Math.round(smd[ERROR]*PRECITION)/PRECITION;}
    public Double getSmh()
    {return (double)Math.round(smh[DATA]*PRECITION)/PRECITION;}
    public Double getSmhError()
    {return (double)Math.round(smh[ERROR]*PRECITION)/PRECITION;}
    public String getMapView()
    {return MapView;}
    public String getExperiment()
    {return experiment;}
    public String getNotes()
    {return notes;}


    // Metodo 2

    public Double getFod()
    {return Math.round(Math.toDegrees(fod[DATA])*PRECITION)/PRECITION;}
    public Double getFodError()
    {return Math.round(Math.toDegrees(fod[ERROR])*PRECITION)/PRECITION;}
    public Double getFostk()
    {return Math.round(Math.toDegrees(fostk[DATA])*PRECITION)/PRECITION;}
    public Double getFoddError()
    {return Math.round(Math.toDegrees(fostk[ERROR])*PRECITION)/PRECITION;}
    public Double getOpod()
    {return Math.round(Math.toDegrees(opod[DATA])*PRECITION)/PRECITION;}
    public Double getOpodError()
    {return Math.round(Math.toDegrees(opod[ERROR])*PRECITION)/PRECITION;}
    public Double getOpostk()
    {return Math.round(Math.toDegrees(opostk[DATA])*PRECITION)/PRECITION;}
    public Double getOpoddError()
    {return Math.round(Math.toDegrees(opostk[ERROR])*PRECITION)/PRECITION;}
    public Double getSmo1d()
    {return Math.round(Math.toDegrees(smo1d[DATA])*PRECITION)/PRECITION;}
    public Double getSmo1dError()
    {return Math.round(Math.toDegrees(smo1d[ERROR])*PRECITION)/PRECITION;}
    public Double getSmo1stk()
    {return Math.round(Math.toDegrees(smo1stk[DATA])*PRECITION)/PRECITION;}
    public Double getSmo1ddError()
    {return Math.round(Math.toDegrees(smo1stk[ERROR])*PRECITION)/PRECITION;}
    public Double getSmo2d()
    {return Math.round(Math.toDegrees(smo2d[DATA])*PRECITION)/PRECITION;}
    public Double getSmo2dError()
    {return Math.round(Math.toDegrees(smo2d[ERROR])*PRECITION)/PRECITION;}
    public Double getSmo2stk()
    {return Math.round(Math.toDegrees(smo2stk[DATA])*PRECITION)/PRECITION;}
    public Double getSmo2ddError()
    {return Math.round(Math.toDegrees(smo2stk[ERROR])*PRECITION)/PRECITION;}
    public Double getOsTrend()
    {return Math.round(Math.toDegrees(ostrend[DATA])*PRECITION)/PRECITION;}
    public Double getOsTrendError()
    {return Math.round(Math.toDegrees(ostrend[ERROR])*PRECITION)/PRECITION;}
    public Double getOsPlunch()
    {return Math.round(Math.toDegrees(osplunge[DATA])*PRECITION)/PRECITION;}
    public Double getOsPlunchError()
    {return Math.round(Math.toDegrees(osplunge[ERROR])*PRECITION)/PRECITION;}
    public Double getSmB()
    {return Math.round(smB[DATA]*PRECITION)/PRECITION;}
    public Double getSmBError()
    {return Math.round(smB[ERROR]*PRECITION)/PRECITION;}
    public Double getdAB()
    {return Math.round(dAB[DATA]*PRECITION)/PRECITION;}
    public Double getdABError()
    {return Math.round(dAB[ERROR]*PRECITION)/PRECITION;}

    // Metodo 3

    public Double getFpl()
    {return Math.round(fpl[DATA]*DIR_COS_PRECITION)/DIR_COS_PRECITION;}
    public Double getFplError()
    {return Math.round(fpl[ERROR]*PRECITION)/PRECITION;}
    public Double getFpn()
    {return Math.round(fpn[DATA]*DIR_COS_PRECITION)/DIR_COS_PRECITION;}
    public Double getFpnError()
    {return Math.round(fpn[ERROR]*PRECITION)/PRECITION;}
    public Double getFpm()
    {return Math.round(fpm[DATA]*DIR_COS_PRECITION)/DIR_COS_PRECITION;}
    public Double getFpmError()
    {return Math.round(fpm[ERROR]*PRECITION)/PRECITION;}
    public Double getOpl()
    {return Math.round(opl[DATA]*DIR_COS_PRECITION)/DIR_COS_PRECITION;}
    public Double getOplError()
    {return Math.round(opl[ERROR]*PRECITION)/PRECITION;}
    public Double getOpm()
    {return Math.round(opm[DATA]*DIR_COS_PRECITION)/DIR_COS_PRECITION;}
    public Double getOpmError()
    {return Math.round(opm[ERROR]*PRECITION)/PRECITION;}
    public Double getOpn()
    {return Math.round(opn[DATA]*DIR_COS_PRECITION)/DIR_COS_PRECITION;}
    public Double getOpnError()
    {return Math.round(opn[ERROR]*PRECITION)/PRECITION;}
    public Double getApl()
    {return Math.round(apl[DATA]*DIR_COS_PRECITION)/DIR_COS_PRECITION;}
    public Double getAplError()
    {return Math.round(apl[ERROR]*PRECITION)/PRECITION;}
    public Double getApn()
    {return Math.round(apn[DATA]*DIR_COS_PRECITION)/DIR_COS_PRECITION;}
    public Double getApnError()
    {return Math.round(apn[ERROR]*PRECITION)/PRECITION;}
    public Double getApm()
    {return Math.round(apm[DATA]*DIR_COS_PRECITION)/DIR_COS_PRECITION;}
    public Double getApmError()
    {return Math.round(apm[ERROR]*PRECITION)/PRECITION;}
    public Double getBpl()
    {return Math.round(bpl[DATA]*DIR_COS_PRECITION)/DIR_COS_PRECITION;}
    public Double getBplError()
    {return Math.round(bpl[ERROR]*PRECITION)/PRECITION;}
    public Double getBpn()
    {return Math.round(bpn[DATA]*DIR_COS_PRECITION)/DIR_COS_PRECITION;}
    public Double getBpnError()
    {return Math.round(bpn[ERROR]*PRECITION)/PRECITION;}
    public Double getBpm()
    {return Math.round(bpm[DATA]*DIR_COS_PRECITION)/DIR_COS_PRECITION;}
    public Double getBpmError()
    {return Math.round(bpm[ERROR]*PRECITION)/PRECITION;}


    public Double[] getFaultPlane()
    {return new Double[]{fpm[DATA],fpl[DATA],fpn[DATA]};}
    public Double[] getObservationPlane()
    {return new Double[]{opm[DATA],opl[DATA],opn[DATA]};}
    public Double[] getPlaneA()
    {return  new Double[]{apm[DATA],apl[DATA],apn[DATA]};}
    public Double[] getPlaneB()
    {return  new Double[]{bpm[DATA],bpl[DATA],bpn[DATA]};}

    /**To String*/


    @Override
    public String toString() {

        String sp=";";

        return experiment       +sp+
                getBeta()       +sp+ getBetaError()     +sp+ getBetaOrientation() +sp+
                getGamma()      +sp+ getGammaError()    +sp+ getGammaOrientation()+sp+
                getPhi()        +sp+ getPhiError()      +sp+ getPhiError()        +sp+
                getAlpha()      +sp+ getAlphaError()    +sp+
                getSmA()         +sp+ getSmAError()       +sp+
                getSmd()        +sp+ getsmdError()      +sp+
                getSmh()        +sp+ getSmhError()      +sp+
                getS()          +sp+ getSError()        +sp+
                getSs()         +sp+ getSsError()       +sp+
                getSd()         +sp+ getSdError()       +sp+
                getSv()         +sp+ getSvError()       +sp+
                getSh()         +sp+ getShError()       +sp+
                getTheta()      +sp+
                getThetaNull()  +sp+
                getFod()        +sp+ getFodError()      +sp+
                getFostk()       +sp+ getFoddError()     +sp+
                getOpod()       +sp+ getOpodError()     +sp+
                getOpostk()      +sp+ getOpoddError()    +sp+
                getSmo1d()      +sp+ getSmo1dError()    +sp+
                getSmo1stk()     +sp+getSmo1ddError()    +sp+
                getSmo2d()      +sp+ getSmo2dError()    +sp+
                getSmo2stk()     +sp+getSmo2ddError()    +sp+
                getOsTrend()    +sp+getOsTrendError()   +sp+
                getOsPlunch()   +sp+getOsPlunchError()  +sp+
                getSmB()        +sp+ getSmBError()      +sp+
                getdAB()        +sp+ getdABError()      +sp+
                getFpl()        +sp+ getFplError()      +sp+
                getFpm()        +sp+ getFpmError()      +sp+
                getFpn()        +sp+ getFpnError()      +sp+
                getOpl()        +sp+  getOplError()     +sp+
                getOpm()        +sp+ getOpmError()      +sp+
                getOpn()        +sp+ getOpnError()      +sp+
                getApl()        +sp+ getAplError()      +sp+
                getApm()        +sp+ getApmError()      +sp+
                getApn()        +sp+ getApnError()      +sp+
                getBpl()        +sp+ getBplError()      +sp+
                getBpm()        +sp+ getBpmError()      +sp+
                getBpn()        +sp+ getBpnError()      +sp+
                getNotes();
    }


}


