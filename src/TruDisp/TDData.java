/**
 * Created by Nieto on 05/08/15.
 */


package TruDisp;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;


public class TDData {

    // Variables
    private final int DATA=0,ERROR=1;
    private Double PRECITION=10.0;
    private TruDispStatusPane statusPane;
    private final Double MINANGLE=0.00157; //.1 grados
    private final Double MAXANGLE=1.5691; //89.9 grados
    private String[] errorValue;
    private String experiment;
    private String notes;
    private Double[] alpha,smh,smd,sm,s,sv,sd,ss,sh;
    private Double theta,thetanull;
    private String sO;

    // Metodo 1
    private Double[] beta,gamma,phi;
    private String betaO,gammaO,phiO,MapView;


    // Metodo 2
    private Double[] fod,fodd,opod,opodd,smo1d,smo1dd,smo2d,smo2dd,ostrend,osplunch;

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
        sm =new Double[] {Double.parseDouble(buffer[12]),Double.parseDouble(buffer[13])};
        smd= new Double[] {Double.parseDouble(buffer[14]),Double.parseDouble(buffer[15])};
        smh =new Double[] {Double.parseDouble(buffer[16]),Double.parseDouble(buffer[17])};
        s =  new Double[] {Double.parseDouble(buffer[18]),Double.parseDouble(buffer[19])};
        ss = new Double[] {Double.parseDouble(buffer[20]),Double.parseDouble(buffer[21])};
        sd = new Double[] {Double.parseDouble(buffer[22]),Double.parseDouble(buffer[23])};
        sv = new Double[] {Double.parseDouble(buffer[24]),Double.parseDouble(buffer[25])};
        sh = new Double[] {Double.parseDouble(buffer[26]),Double.parseDouble(buffer[27])};
        theta = Double.parseDouble(buffer[28]);
        thetanull = Double.parseDouble(buffer[29]);
        setNotes(buffer[30]);


    }




    public void initVariables()
    {
        beta = new Double[ ] {0.0,0.0};
        betaO = new String();

        gamma= new Double[ ] {0.0,0.0};
        gammaO = new String();

        phi= new Double[ ] {0.0,0.0};
        phiO= new String();

        alpha= new Double[ ] {0.0,0.0};
        sm= new Double[ ] {0.0,0.0};
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

        experiment="";
        notes="";


    }
    // setMethods

    public void set(String data){

    };


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

    public Boolean setSm(String data,String error)
    {
        if(isDisplacementValid(data))
        {
            sm[DATA]=Double.parseDouble(data);
            errorValue = error.split(" ");
            sm[ERROR]=Double.parseDouble(errorValue[1]);
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

    public Boolean setExperiment(String exp)
    {
        experiment = exp;
        return true;
    }

    public Boolean setNotes(String nts)
    {notes=nts; return true;}


    // Métodos de validación

    public Boolean isDataValidForCalcuating()
    {
        if((sm[DATA] != 0)||(smh[DATA] !=0)||(smd[DATA]!=0) )
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


    // Métodos de procesamiento de datos

    public Boolean Calculate(Integer method)
    {
        try {
            // calculo
            switch (method) {
                case 1:
                {
                    s[DATA] = calculateMethod1(beta[DATA], gamma[DATA], phi[DATA], sm[DATA], smd[DATA], smh[DATA]);
                    calculateDistance();
                    calculateErrors();
                    break;
                }
            }
            return true;
        }catch(Exception e){System.err.println(e);return false;}

    }



    // Método 1
    public Double calculateMethod1(Double beta,Double gamma,Double phi,Double sm, Double smd, Double smh)
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

    public Double calculateSs(Double s,Double gamma)
    { return s*Math.cos(gamma);}
    public Double calculateSd(Double s, Double gamma)
    {return s*Math.sin(gamma);}
    public Double calculateSv(Double s, Double gamma, Double alpha)
    {return s*Math.sin(gamma)*Math.sin(alpha);}
    public Double calculateSh(Double s, Double gamma, Double alpha)
    {return s*Math.sin(gamma)*Math.cos(alpha);}

    public void calculateDistance()
    {
        ss[DATA] = calculateSs(s[DATA],gamma[DATA]);
        sd[DATA] = calculateSd(s[DATA], gamma[DATA]);
        sv[DATA] = calculateSv(s[DATA],gamma[DATA],alpha[DATA]);
        sh[DATA] = calculateSh(s[DATA], gamma[DATA], alpha[DATA]);
    }

    public void calculateErrors()
    {
        Double h=0.001,f,i,pdbeta,pdgamma,pdphi,pdsm,pdsmd,pdsmh;


        // Derivada parcial  ds/db
        f = calculateMethod1(beta[DATA]+h,gamma[DATA],phi[DATA],sm[DATA],smd[DATA],smh[DATA]);
        i = calculateMethod1(beta[DATA] - h, gamma[DATA], phi[DATA], sm[DATA], smd[DATA], smh[DATA]);
        pdbeta= Math.abs((f - i) / (2 * h));

        // Derivada parcial ds/dgamma
        f = calculateMethod1(beta[DATA],gamma[DATA]+h,phi[DATA],sm[DATA],smd[DATA],smh[DATA]);
        i = calculateMethod1(beta[DATA], gamma[DATA] - h, phi[DATA], sm[DATA], smd[DATA], smh[DATA]);
        pdgamma= Math.abs((f - i) / (2 * h));

        // Derivada parcial ds/dphi
        f = calculateMethod1(beta[DATA],gamma[DATA],phi[DATA]+h,sm[DATA],smd[DATA],smh[DATA]);
        i = calculateMethod1(beta[DATA], gamma[DATA], phi[DATA] - h, sm[DATA], smd[DATA], smh[DATA]);
        pdphi = Math.abs((f - i) / (2 * h));

        // Derivada parcial ds/dsm
        f = calculateMethod1(beta[DATA],gamma[DATA],phi[DATA],sm[DATA]+h,smd[DATA],smh[DATA]);
        i = calculateMethod1(beta[DATA], gamma[DATA], phi[DATA], sm[DATA] - h, smd[DATA], smh[DATA]);
        pdsm= Math.abs((f - i) / (2 * h));

        // Derivada parcial ds/dsmd
        f = calculateMethod1(beta[DATA],gamma[DATA],phi[DATA],sm[DATA],smd[DATA]+h,smh[DATA]);
        i = calculateMethod1(beta[DATA], gamma[DATA], phi[DATA], sm[DATA], smd[DATA] - h, smh[DATA]);
        pdsmd= Math.abs((f - i) / (2 * h));

        // Derivada parcial ds/dsmh
        f = calculateMethod1(beta[DATA],gamma[DATA],phi[DATA],sm[DATA],smd[DATA],smh[DATA]+h);
        i = calculateMethod1(beta[DATA], gamma[DATA], phi[DATA], sm[DATA], smd[DATA], smh[DATA] - h);
        pdsmh= Math.abs((f - i) / (2 * h));


        s[ERROR] = (pdbeta*beta[ERROR])+(pdgamma*gamma[ERROR])+(pdphi*phi[ERROR])+(pdsm*sm[ERROR])+(pdsmd*smd[ERROR])+(pdsmh*smh[ERROR]);

        //
        Double pds,pdalpha;

        // Derivada parcial dss/ds
        f = calculateSs(s[DATA]+h, gamma[DATA]);
        i = calculateSs(s[DATA]-h, gamma[DATA]);
        pds = Math.abs((f - i) / (2 * h));
        // Derivada parcial dss/dgamma
        f = calculateSs(s[DATA],gamma[DATA]+h);
        i = calculateSs(s[DATA],gamma[DATA]-h);
        pdgamma = Math.abs((f - i) / (2 * h));

        ss[ERROR] = (pds*s[ERROR])+(pdgamma*gamma[ERROR]);

        // Derivada parcial dsd/ds
        f = calculateSd(s[DATA] + h, gamma[DATA]);
        i = calculateSd(s[DATA] - h, gamma[DATA]);
        pds = Math.abs((f - i) / (2 * h));
        // Derivada parcial dsd/dgamma
        f = calculateSd(s[DATA], gamma[DATA] + h);
        i = calculateSd(s[DATA], gamma[DATA] - h);
        pdgamma = Math.abs((f - i) / (2 * h));

        sd[ERROR] = (pds*s[ERROR])+(pdgamma*gamma[ERROR]);

        // Derivada parcial dsv/ds
        f = calculateSv(s[DATA] + h, gamma[DATA], alpha[DATA]);
        i = calculateSv(s[DATA] - h, gamma[DATA], alpha[DATA]);
        pds = Math.abs((f - i) / (2 * h));
        // Derivada parcial dsv/dgamma
        f = calculateSv(s[DATA], gamma[DATA] + h,alpha[DATA]);
        i = calculateSv(s[DATA], gamma[DATA] - h,alpha[DATA]);
        pdgamma = Math.abs((f - i) / (2 * h));
        // Derivada parcial dsv/dalpha
        f = calculateSv(s[DATA], gamma[DATA],alpha[DATA]+h);
        i = calculateSv(s[DATA], gamma[DATA],alpha[DATA]-h);
        pdalpha = Math.abs((f - i) / (2 * h));

        sv[ERROR] = (pds*s[ERROR])+(pdgamma*gamma[ERROR])+(pdalpha*alpha[ERROR]);

        // Derivada parcial dsh/ds
        f = calculateSh(s[DATA] + h, gamma[DATA], alpha[DATA]);
        i = calculateSh(s[DATA] - h, gamma[DATA], alpha[DATA]);
        pds = Math.abs((f - i) / (2 * h));
        // Derivada parcial dsh/dgamma
        f = calculateSh(s[DATA], gamma[DATA] + h, alpha[DATA]);
        i = calculateSh(s[DATA], gamma[DATA] - h, alpha[DATA]);
        pdgamma = Math.abs((f - i) / (2 * h));
        // Derivada parcial dsh/dalpha
        f = calculateSh(s[DATA], gamma[DATA], alpha[DATA] + h);
        i = calculateSh(s[DATA], gamma[DATA], alpha[DATA] - h);
        pdalpha = Math.abs((f - i) / (2 * h));

        sh[ERROR] = (pds*s[ERROR])+(pdgamma*gamma[ERROR])+(pdalpha*alpha[ERROR]);

    }

// Getters

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
    public Double getSm()
    {return (double)Math.round(sm[DATA]*PRECITION)/PRECITION;}
    public Double getSmError()
    {return (double)Math.round(sm[ERROR]*PRECITION)/PRECITION;}
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



    // ToString


    @Override
    public String toString() {

        String sp=";";

        return experiment +sp+
                getBeta() +sp+ getBetaError() +sp+ getBetaOrientation() +sp+
                getGamma()+sp+ getGammaError()+sp+ getGammaOrientation()+sp+
                getPhi()  +sp+ getPhiError()  +sp+ getPhiError()        +sp+
                getAlpha()+sp+ getAlphaError()+sp+
                getSm()   +sp+ getSmError()   +sp+
                getSmd()  +sp+ getsmdError()  +sp+
                getSmh()  +sp+ getSmhError()  +sp+
                getS()    +sp+ getSError()    +sp+
                getSs()   +sp+ getSsError()   +sp+
                getSd()   +sp+ getSdError()   +sp+
                getSv()   +sp+ getSvError()   +sp+
                getSh()   +sp+ getShError()   +sp+
                getTheta()+sp+
                getThetaNull()                +sp+
                getNotes();
    }


}


