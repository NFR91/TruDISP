
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


package TruDisp;

/** Esta clase se encarga de manejar el cálculo de la falla y sus datos. */
public class TDData {

    /** Constantes */
    private final int       DATA=0,                         // Constante de posición para los arreglos del valor de la variable.
                            ERROR=1;                        // Constante de posición para los arreglos del valor del error.
    private final Double    PRECITION=10.0;                 // Precisión requerida para desplegar los datos.
    private final Double    DIR_COS_PRECITION=10000.0;      // Precisión requerida para los Cosenos Direcotres.
    private final Double    MINANGLE=Math.toRadians(0.1);   // Ángulo minimo permitido
    private final Double    MAXANGLE=Math.toRadians(89.9);  // Ángulo máximo permitido.

    /** Variables */
    private TDStatusPane    statusPane;                     // Objeto que apunta al statusPane.
    private String[]        errorValue;                     // Arreglo que contendrá los datos separados de la etiqueta de error que se introduce
    private String          experiment;                     // Nombre de la falla.
    private String          notes;                          // Notas de la falla.
    private Double[]        smh,smd,                        // Valores de desplazamiento
                            smA,smB, dAB,
                            s,sv,sd,ss,sh,sm,sl,sn;                  // Valores de resultado
    private Double          theta,thetanull;                // Valores de los ángulos theta y thetanull

    // Metodo 1
    private Double[]    beta,gamma,phi,alpha;               // Arreglos que contienen el valor y el error de cada pitch
    private String      betaO,gammaO,phiO,MapView;          // Valores de las orientaciones de los pitchs y el tipo de cálculo

    // Metodo 2
    private Double[]    fod,fostk,opod, opostk,             // Arreglos que contieen el valor y el error para cada ángulo de dip y strike.
                        smo1d, smo1stk,smo2d,
                        smo2stk,ostrend, osplunge;

    // Metodo 3
    private Double[]    fpm,fpn,fpl,                        // Arreglos que contienen el valor y el error para cada elemento del coseno director de cada plano.
                        opm,opn,opl,
                        apm,apn,apl,
                        bpm,bpn,bpl;

    /** Constructor */
    public TDData(TDStatusPane stbr)
    {
        statusPane = stbr;
        initVariables();
    }

    /** Constructor */
    public TDData(String dataset, TDStatusPane stbr)
    {
        statusPane = stbr;
        initVariables();

        // Creamos un buffer para separar cada uno de los elemntos de la falla.
        String[] buffer = dataset.split(";");

        // Asginamos a cada variable su valor correspondiente del buffer.
        experiment =buffer[0];
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

        notes = buffer[78];
    }

    /** Función encargada de calcular el desplazamiento de la falla. */
    public Boolean Calculate(Integer method)
    {
        try {
            // Revisamos que método esta seleccionado.
            switch (method) {
                case 1:
                {
                    // El método 1 se encuentra seleccionado y se calcula la distancia.
                    s[DATA] = method1Calculate(beta[DATA], gamma[DATA], phi[DATA], smA[DATA], smd[DATA], smh[DATA]);
                    // Calculamos los desplazamientos.
                    method1CalculateDistance();
                    // Calculamos los errores.
                    method1calculateDistanceErrors();
                    break;
                }
                case 2:
                {
                    // El método 2 se encuentra seleccionado y se calculan los cosenos directores.
                    method2CalculateDirCosOfPlanes();
                    method2CalculateDirCosOfPlanesError();

                    // Revisamos si se han introducido dos planos.
                    if(bpn[DATA]!=1 && apn[DATA]!=1)
                    {
                        //Si no se cuenta con la direción de la estria se requieren dos planos y empleamos el método 2
                        Double[] so= method2Calculate(fpl[DATA], fpm[DATA], -fpn[DATA], opl[DATA], opm[DATA], -opn[DATA], apl[DATA], apm[DATA], -apn[DATA],
                                bpl[DATA], bpm[DATA], -bpn[DATA], dAB[DATA], smA[DATA], smB[DATA]);

                        sm[DATA]=so[0];
                        sl[DATA]=so[1];
                        sn[DATA]=so[2];
                        s[DATA] = normOfVector(so);

                        // Obtenemos las distancias.
                        method2CalculateDistances();
                        method2CalculateDistanceError();
                    }
                    else
                    {
                        // A partir de los cosenos directores de los planos obtenemos los valores del método 1.
                        method2CalculatePitchs();
                        method2CalculatePitchsErrors();

                        // Revisamos si la persona a introducido un valor de plunge que permita se encuentre dentro el plano.
                        // Y ademas que no se ha introducido la orientación de la estria mediante gamma.
                        if(!isPlungeValid() && gamma[DATA]==0)
                        {statusPane.setNotification(" Striae is not on the fault plane ",TruDisp.WARNING_ICON);}

                        if(isDataValidForMethod1()) // Revisamos si los valores son correctos para el cálculo
                        {
                            // Calculamos el desplazamiento de la falla.
                            s[DATA] = method1Calculate(beta[DATA], gamma[DATA], phi[DATA], smA[DATA], smd[DATA], smh[DATA]);
                            // Calculamos las distancias.
                            method1CalculateDistance();
                            // Calculamos los errores.
                            method1calculateDistanceErrors();
                        }
                    }
                    break;
                }
            }
            return true;
        }catch(Exception e){System.err.println(e);return false;}
    }

    /******************************************************************************************************************/
    /** Funciones Generales.*/
    /******************************************************************************************************************/

    /** Función que inicializa las varialbes. */
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
        sm=new Double[ ] {0.0,0.0};
        sl=new Double[ ] {0.0,0.0};
        sn=new Double[ ] {0.0,0.0};
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

    /******************************************************************************************************************/
    /** Métodos Generales.*/
    /******************************************************************************************************************/

    /** Función que calcula el producto el coseno inverso del producto punto. */
    public Double dotProductAngle(Double[] a, Double[] b){

        // Calculamos con el prodcuto punto el pitch.
        return Math.acos((a[0]*b[0])+(a[1]*b[1])+(a[2]*b[2]));
    }

    /** Función que calcula el producto cruz entre dos vectores. */
    public Double[] crossProduct(Double[] a, Double[] b)
    {
        Double[] axb = new Double[3];

        axb[0] = a[1]*b[2] - a[2]*b[1];
        axb[1] = a[2]*b[0] - a[0]*b[2];
        axb[2] = a[0]*b[1] - a[1]*b[0];

        return axb;
    }

    /** Función que Verifica si los datos adquiridos son validos para calcular.. */
    public Boolean isDataValidForMethod1()
    {
        // Almenos alguno de los desplazamientos debe ser mayor a cero.
        if((smA[DATA] != 0)||(smh[DATA] !=0)||(smd[DATA]!=0) )
        {
            // Verificamos si es posible la orientación especificada.
            orientationIndeterminated();

            // Verificamos si la linea beta esta cerca de la gamma, es decir si el ángulo theta es pequeño.
            if(isBetaNearGamma())
            {
                statusPane.setNotification(" WARNING\n" +
                    "Θ is small.\n In this condition a very small error in the angular measures will produce a large deviation in the results." +
                    "\nWe do not recommend calculating with Θ < 20º. ", TruDisp.WARNING_ICON);
            }

            // Verificamos si es el caso de linea arbitraria.
            if(MapView.equalsIgnoreCase("Arbitrary Line"))
            {
                // Como es el caso verificamos si phi esta cerca de beta, es decir si thetanull es pequeño.
                if(isBetaNearPhi())
                {
                    statusPane.setNotification(" WARNING\n " +
                        "You are near the \"null line \".\nIn this condition (β near γ) a very small error in the angular measures will produce" +
                        "a large deviaton in the results." +
                        "\nWe do not recommend calculating with Θnull < 10º. ", TruDisp.WARNING_ICON);
                }
            }
            return  true;
        }
        else{
            // Ninguno de los desplazamientos es mayor que cero.
            statusPane.setStatus(" Error: At least one displacement component must be higher than zero ", TruDisp.ERROR_ICON);
            return false;
        }
    }


    /** Función que Verifica si son posibles las orientaciones */
    public void orientationIndeterminated()
    {
        /**
         * Debido al cálculo de errores no podemos tener valores de 0 o 90 grados, por lo que
         * se requiere un ángulo minimo y un máximo.
         * */

        // Verificamos si el angulo es menor que el mínimo.
        beta[DATA] = (beta[DATA] < MINANGLE)? (MINANGLE):(beta[DATA]);
        gamma[DATA] = (gamma[DATA] < MINANGLE)? (MINANGLE):(gamma[DATA]);
        phi[DATA] = (phi[DATA] < MINANGLE)? (MINANGLE):(phi[DATA]);

        // Verificamos si el ángulo es mayotr que el máximo.
        beta[DATA] = (beta[DATA] > MAXANGLE)? (MAXANGLE):(beta[DATA]);
        gamma[DATA] = (gamma[DATA] > MAXANGLE)? (MAXANGLE):(gamma[DATA]);
        phi[DATA] = (phi[DATA] > MAXANGLE)? (MAXANGLE):(phi[DATA]);

    }

    /** Función que Verifica si el ángulo theta es pequeño.*/
    public Boolean isBetaNearGamma()
    {
        /**
         * Si la línea beta es muy cercana a la gamma un pequeño error en las mediciones provoca un
         * error grande en los cálculos, para saber la fiabilidad de las mediciones calculamos el valor
         * del ángulo entre las dos líneas, theta.
         * */

        // Si tienen direcciones opuestas entonces usamos 180-(gamm+beta) para calcular theta.
        if(betaO.equalsIgnoreCase(gammaO))
            {theta = Math.abs(beta[DATA]-gamma[DATA]);}
        else
            {theta = Math.PI - (beta[DATA]+gamma[DATA]);}

        // Verificamos si theta es menor que 20 grados.
        if(theta < (Math.toRadians(20)))
        {
            // Si theta es cero las líneas son pararelas entonces para calcular debemos aumentar el ángulo de una.
            if(theta == 0)
            { beta[DATA] = (1+0.01)*beta[DATA];}

            // Las lineas se encuentran muy cerca
            return true;
        }

        // Las líneas no se encuentran cerca.
        return false;
    }

    /** Función que Verifica si el ángulo thetanull es pequeño.*/
    public Boolean isBetaNearPhi()
    {
        /**
         * Si la línea beta es muy cercana a la phi un pequeño error en las mediciones provoca un
         * error grande en los cálculos, para saber la fiabilidad de las mediciones calculamos el valor
         * del ángulo entre las dos líneas, thetanull.
         * */

        // Si tienen direcciones opuestas entonces usamos (180-(beta+phi)) para calcular thetanull.
        if(betaO.equalsIgnoreCase(phiO))
            { thetanull = Math.abs(beta[DATA]-phi[DATA]);}
        else
            {thetanull = Math.PI-(beta[DATA]+phi[DATA]);}

        // Revisamos si el ángulo es menor que 10 grados.;
        if(thetanull < (Math.PI/18))
        {
            // Si thetanull es cero las líneas son paralelas, para poder calcular aumentamos el ángulo de una.
            if(thetanull == 0)
            {beta[DATA] = (1+0.01)*beta[DATA];}

            // Las líneas se encuentran muy cerca.
            return true;
        }

        // las líneas no se encuentran cerca.
        return false;
    }

    /** Función que Verifica si el ángulo es valido.*/
    public Boolean isAngleValid(String data)
    {
        // El ángulo deve estar entre cero y 90.
        return (isNumber(data)) ? ( (Double.parseDouble(data)<0 || Double.parseDouble(data)>90) ? false : true ) : false;
    }

    /** Función que Verifica si la distancia es valida.*/
    public Boolean isDisplacementValid(String data)
    {
        // La distancia debe ser mayot que cero.
        return (isNumber(data)) ? ((Double.parseDouble(data)>=0) ? true : false ) : false;
    }

    /** Función que Verifica el dato introducido es un número.*/
    public Boolean isNumber(String data)
    {
        try{
            Double.parseDouble(data);
        }catch (Exception e)
        {
            // No es un número.
            return false;
        }

        // Es un número.
        return true;
    }

    /** Función que Verifica si el ángulo de Strike es valido.*/
    public Boolean isStrikeAngleValid(String data)
    {
        // El ángulo strike debe estar entre 0 y 360;
        return (isNumber(data)) ? ((Double.parseDouble(data)<0||Double.parseDouble(data)>360)? false:true):false;
    }

    /** Función que Verifica si el ángulo Plunge es valido.*/
    public Boolean isPlungeValid()
    {
        /**
         * Es usual que exista error en los cálculos a que la gente no introduce un trend y plunge que corresponden a la
         * orientación de la línea de la estría sobre el plano por lo que se realiza el cálculo de la misma, esta
         * función se encarga de comprobar si los datos que introdujo el usuario corresponden con una linea que vive sobre
         * el plano.
         * */

        // Calculamos el plunge para asegurar que la estria está siempre sobre el plano.
        Double plunge = Math.atan(Math.tan(fod[DATA]) * Math.sin(((ostrend[DATA] - fostk[DATA]) > Math.PI / 2) ? (Math.PI - (ostrend[DATA] - fostk[DATA])) : (ostrend[DATA] - fostk[DATA])));

        // Verificamos si el la diferencia entre el dato introducido por el usuario y el cálculado es inferior a una tolerancia.
        if( Math.abs(osplunge[DATA]-plunge)> Math.toRadians(4))
        {return false;}
        else
        {return  true;}
    }

    /******************************************************************************************************************/
    /** Métodos 1.*/
    /******************************************************************************************************************/


    /** Función que calcula con el método 1 el desplazamiento de la falla (S).*/
    public Double method1Calculate(Double beta, Double gamma, Double phi, Double sm, Double smd, Double smh)
    {
        /**
         * Esta función se encarga de tomar la decisión de que ecuación utilizar basandose en el diagrama de flujo
         * publicado en el artículo. Revisa 18 casos y elije el indicado basandose en las orientaciones de beta, gamma y
         * phi, así como de la distancia indroducida.
         * */

        // Revisamos en cual de los dos casos nos encotramos.
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

    /** Función que calcula el desplazamiento ss.*/
    public Double method1CalculateSs(Double s, Double gamma)
    { return s*Math.cos(gamma);}

    /** Función que calcula el desplazamiento sd.*/
    public Double method1CalculateSd(Double s, Double gamma)
    {return s*Math.sin(gamma);}

    /** Función que calcula el desplazamiento sv.*/
    public Double method1CalculateSv(Double s, Double gamma, Double alpha)
    {return s*Math.sin(gamma)*Math.sin(alpha);}

    /** Función que calcula el desplazamiento sh.*/
    public Double method1CalculateSh(Double s, Double gamma, Double alpha)
    {return s*Math.sin(gamma)*Math.cos(alpha);}

    /** Función que calculalos desplazamiento en base a S..*/
    public void method1CalculateDistance()
    {
        ss[DATA] = method1CalculateSs(s[DATA], gamma[DATA]);
        sd[DATA] = method1CalculateSd(s[DATA], gamma[DATA]);
        sv[DATA] = method1CalculateSv(s[DATA], gamma[DATA], alpha[DATA]);
        sh[DATA] = method1CalculateSh(s[DATA], gamma[DATA], alpha[DATA]);
    }

    /** Función que calcula el error de las distancias, propagando el error de las mediciones.*/
    public void method1calculateDistanceErrors()
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

    /******************************************************************************************************************/
    /** Métodos 2.*/
    /******************************************************************************************************************/

    public void method2CalculateDistances()
    {
        // Cosenos Directores de línea de strike del Plano de falla;
        Double[] stkLine = new Double[]{Math.cos(fostk[DATA]),Math.sin(fostk[DATA]),0.0};

        gamma[DATA] = dotProductAngle(stkLine,new Double[]{sm[DATA],sl[DATA],sn[DATA]});

        alpha[DATA] = fod[DATA];

        ss[DATA] = method1CalculateSs(s[DATA], gamma[DATA]);
        sd[DATA] = method1CalculateSd(s[DATA], gamma[DATA]);
        sv[DATA] = method1CalculateSv(s[DATA], gamma[DATA], alpha[DATA]);
        sh[DATA] = method1CalculateSh(s[DATA], gamma[DATA], alpha[DATA]);

    }

    /**Función que calcula los errores en el cálculo de pitchs*/
    public void method2CalculatePitchsErrors()
    {
        Double f, i,h=0.001;

        // Derivadas parciales de beta.
        i= dotProductAngle(crossProduct(new Double[]{fpm[DATA]-h,fpl[DATA],fpn[DATA]}, new Double[]{apm[DATA],apl[DATA],apn[DATA]}),new Double[]{Math.cos(fostk[DATA]),Math.sin(fostk[DATA]),0.0});
        f= dotProductAngle(crossProduct(new Double[]{fpm[DATA]+h,fpl[DATA],fpn[DATA]}, new Double[]{apm[DATA],apl[DATA],apn[DATA]}),new Double[]{Math.cos(fostk[DATA]),Math.sin(fostk[DATA]),0.0});
        Double pdfpm = Math.abs((f-i)/(2*h));
        i= dotProductAngle(crossProduct(new Double[]{fpm[DATA],fpl[DATA]-h,fpn[DATA]}, new Double[]{apm[DATA],apl[DATA],apn[DATA]}),new Double[]{Math.cos(fostk[DATA]),Math.sin(fostk[DATA]),0.0});
        f= dotProductAngle(crossProduct(new Double[]{fpm[DATA],fpl[DATA]+h,fpn[DATA]}, new Double[]{apm[DATA],apl[DATA],apn[DATA]}),new Double[]{Math.cos(fostk[DATA]),Math.sin(fostk[DATA]),0.0});
        Double pdfpl = Math.abs((f-i)/(2*h));
        i= dotProductAngle(crossProduct(new Double[]{fpm[DATA],fpl[DATA],fpn[DATA]-h}, new Double[]{apm[DATA],apl[DATA],apn[DATA]}),new Double[]{Math.cos(fostk[DATA]),Math.sin(fostk[DATA]),0.0});
        f= dotProductAngle(crossProduct(new Double[]{fpm[DATA],fpl[DATA],fpn[DATA]+h}, new Double[]{apm[DATA],apl[DATA],apn[DATA]}),new Double[]{Math.cos(fostk[DATA]),Math.sin(fostk[DATA]),0.0});
        Double pdfpn = Math.abs((f-i)/(2*h));
        i= dotProductAngle(crossProduct(new Double[]{fpm[DATA],fpl[DATA],fpn[DATA]}, new Double[]{apm[DATA]-h,apl[DATA],apn[DATA]}),new Double[]{Math.cos(fostk[DATA]),Math.sin(fostk[DATA]),0.0});
        f= dotProductAngle(crossProduct(new Double[]{fpm[DATA],fpl[DATA],fpn[DATA]}, new Double[]{apm[DATA]+h,apl[DATA],apn[DATA]}),new Double[]{Math.cos(fostk[DATA]),Math.sin(fostk[DATA]),0.0});
        Double pdapm = Math.abs((f-i)/(2*h));
        i= dotProductAngle(crossProduct(new Double[]{fpm[DATA],fpl[DATA],fpn[DATA]}, new Double[]{apm[DATA],apl[DATA]-h,apn[DATA]}),new Double[]{Math.cos(fostk[DATA]),Math.sin(fostk[DATA]),0.0});
        f= dotProductAngle(crossProduct(new Double[]{fpm[DATA],fpl[DATA],fpn[DATA]}, new Double[]{apm[DATA],apl[DATA]+h,apn[DATA]}),new Double[]{Math.cos(fostk[DATA]),Math.sin(fostk[DATA]),0.0});
        Double pdapl = Math.abs((f-i)/(2*h));
        i= dotProductAngle(crossProduct(new Double[]{fpm[DATA],fpl[DATA],fpn[DATA]}, new Double[]{apm[DATA],apl[DATA],apn[DATA]-h}),new Double[]{Math.cos(fostk[DATA]),Math.sin(fostk[DATA]),0.0});
        f= dotProductAngle(crossProduct(new Double[]{fpm[DATA],fpl[DATA],fpn[DATA]}, new Double[]{apm[DATA],apl[DATA],apn[DATA]+h}),new Double[]{Math.cos(fostk[DATA]),Math.sin(fostk[DATA]),0.0});
        Double pdapn = Math.abs((f-i)/(2*h));
        i= dotProductAngle(crossProduct(new Double[]{fpm[DATA],fpl[DATA],fpn[DATA]}, new Double[]{apm[DATA],apl[DATA],apn[DATA]}),new Double[]{Math.cos(fostk[DATA]-h),Math.sin(fostk[DATA]-h),0.0});
        f= dotProductAngle(crossProduct(new Double[]{fpm[DATA],fpl[DATA],fpn[DATA]}, new Double[]{apm[DATA],apl[DATA],apn[DATA]}),new Double[]{Math.cos(fostk[DATA]+h),Math.sin(fostk[DATA]+h),0.0});
        Double pdfostk = Math.abs((f-i)/(2*h));

        beta[ERROR] = pdfpm*fpm[ERROR] + pdfpl*fpl[ERROR] + pdfpn*fpn[ERROR] + pdapm*apm[ERROR] + pdapl*apl[ERROR] + pdapn*apn[ERROR] + pdfostk*fostk[ERROR];


        // Derivadas parciales de phi
        i= dotProductAngle(crossProduct(new Double[]{fpm[DATA]-h,fpl[DATA],fpn[DATA]},  new Double[]{opm[DATA],opl[DATA],opn[DATA]}),new Double[]{Math.cos(fostk[DATA]),Math.sin(fostk[DATA]),0.0});
        f= dotProductAngle(crossProduct(new Double[]{fpm[DATA]+h,fpl[DATA],fpn[DATA]},  new Double[]{opm[DATA],opl[DATA],opn[DATA]}),new Double[]{Math.cos(fostk[DATA]),Math.sin(fostk[DATA]),0.0});
        pdfpm = Math.abs((f - i) / (2 * h));
        i= dotProductAngle(crossProduct(new Double[]{fpm[DATA], fpl[DATA] - h, fpn[DATA]}, new Double[]{opm[DATA], opl[DATA], opn[DATA]}), new Double[]{Math.cos(fostk[DATA]), Math.sin(fostk[DATA]), 0.0});
        f= dotProductAngle(crossProduct(new Double[]{fpm[DATA], fpl[DATA] + h, fpn[DATA]}, new Double[]{opm[DATA], opl[DATA], opn[DATA]}), new Double[]{Math.cos(fostk[DATA]), Math.sin(fostk[DATA]), 0.0});
        pdfpl = Math.abs((f - i) / (2 * h));
        i= dotProductAngle(crossProduct(new Double[]{fpm[DATA], fpl[DATA], fpn[DATA] - h}, new Double[]{opm[DATA], opl[DATA], opn[DATA]}), new Double[]{Math.cos(fostk[DATA]), Math.sin(fostk[DATA]), 0.0});
        f= dotProductAngle(crossProduct(new Double[]{fpm[DATA],fpl[DATA],fpn[DATA]+h},  new Double[]{opm[DATA],opl[DATA],opn[DATA]}),new Double[]{Math.cos(fostk[DATA]),Math.sin(fostk[DATA]),0.0});
        pdfpn = Math.abs((f - i) / (2 * h));
        i= dotProductAngle(crossProduct(new Double[]{fpm[DATA],fpl[DATA],fpn[DATA]}, new Double[]{opm[DATA]-h,opl[DATA],opn[DATA]}),new Double[]{Math.cos(fostk[DATA]),Math.sin(fostk[DATA]),0.0});
        f= dotProductAngle(crossProduct(new Double[]{fpm[DATA],fpl[DATA],fpn[DATA]}, new Double[]{opm[DATA]+h,opl[DATA],opn[DATA]}),new Double[]{Math.cos(fostk[DATA]),Math.sin(fostk[DATA]),0.0});
        Double pdopm = Math.abs((f-i)/(2*h));
        i= dotProductAngle(crossProduct(new Double[]{fpm[DATA],fpl[DATA],fpn[DATA]}, new Double[]{opm[DATA],opl[DATA]-h,opn[DATA]}),new Double[]{Math.cos(fostk[DATA]),Math.sin(fostk[DATA]),0.0});
        f= dotProductAngle(crossProduct(new Double[]{fpm[DATA],fpl[DATA],fpn[DATA]}, new Double[]{opm[DATA],opl[DATA]+h,opn[DATA]}),new Double[]{Math.cos(fostk[DATA]),Math.sin(fostk[DATA]),0.0});
        Double pdopl = Math.abs((f-i)/(2*h));
        i= dotProductAngle(crossProduct(new Double[]{fpm[DATA],fpl[DATA],fpn[DATA]}, new Double[]{opm[DATA],opl[DATA],opn[DATA]-h}),new Double[]{Math.cos(fostk[DATA]),Math.sin(fostk[DATA]),0.0});
        f= dotProductAngle(crossProduct(new Double[]{fpm[DATA],fpl[DATA],fpn[DATA]}, new Double[]{opm[DATA],opl[DATA],opn[DATA]+h}),new Double[]{Math.cos(fostk[DATA]),Math.sin(fostk[DATA]),0.0});
        Double pdopn = Math.abs((f-i)/(2*h));
        i= dotProductAngle(crossProduct(new Double[]{fpm[DATA],fpl[DATA],fpn[DATA]},  new Double[]{opm[DATA],opl[DATA],opn[DATA]}),new Double[]{Math.cos(fostk[DATA]-h),Math.sin(fostk[DATA]-h),0.0});
        f= dotProductAngle(crossProduct(new Double[]{fpm[DATA],fpl[DATA],fpn[DATA]},  new Double[]{opm[DATA],opl[DATA],opn[DATA]}),new Double[]{Math.cos(fostk[DATA]+h),Math.sin(fostk[DATA]+h),0.0});
        pdfostk = Math.abs((f - i) / (2 * h));

        phi[ERROR] = pdfpm*fpm[ERROR] + pdfpl*fpl[ERROR] + pdfpn*fpn[ERROR] + pdopm*opm[ERROR] + pdopl*opl[ERROR] + pdopn*opn[ERROR] + pdfostk*fostk[ERROR];

        // Error de alpha
        alpha[ERROR] = fod[ERROR];

        //******** Error de gamma ()**********************//

        // Calculamos el plunge para asegurar que la estria está siempre sobre el plano.
        Double plunge = Math.atan(Math.tan(fod[DATA]) * Math.sin(((ostrend[DATA] - fostk[DATA]) > Math.toRadians(90)) ? (Math.PI - (ostrend[DATA] - fostk[DATA])) : (ostrend[DATA] - fostk[DATA])));

        i= Math.atan(Math.tan(fod[DATA]-h) * Math.sin(((ostrend[DATA] - fostk[DATA]) > Math.toRadians(90)) ? (Math.PI - (ostrend[DATA] - fostk[DATA])) : (ostrend[DATA] - fostk[DATA])));
        f= Math.atan(Math.tan(fod[DATA]+h) * Math.sin(((ostrend[DATA] - fostk[DATA]) > Math.toRadians(90)) ? (Math.PI - (ostrend[DATA] - fostk[DATA])) : (ostrend[DATA] - fostk[DATA])));
        Double pdfod = Math.abs((f - i) / (2 * h));
        i= Math.atan(Math.tan(fod[DATA]) * Math.sin((( (ostrend[DATA]-h) - fostk[DATA]) > Math.toRadians(90)) ? (Math.PI - ((ostrend[DATA]-h) - fostk[DATA])) : ((ostrend[DATA]-h) - fostk[DATA])));
        f= Math.atan(Math.tan(fod[DATA]) * Math.sin((( (ostrend[DATA]+h) - fostk[DATA]) > Math.toRadians(90)) ? (Math.PI - ((ostrend[DATA]+h) - fostk[DATA])) : ((ostrend[DATA]+h) - fostk[DATA])));
        Double pdostrend = Math.abs((f - i) / (2 * h));
        i= Math.atan(Math.tan(fod[DATA]) * Math.sin(((ostrend[DATA] - (fostk[DATA]-h) ) > Math.toRadians(90)) ? (Math.PI - (ostrend[DATA] - (fostk[DATA]-h))) : (ostrend[DATA] - (fostk[DATA]-h))));
        f= Math.atan(Math.tan(fod[DATA]) * Math.sin(((ostrend[DATA] - (fostk[DATA] + h)) > Math.toRadians(90)) ? (Math.PI - (ostrend[DATA] - (fostk[DATA] + h))) : (ostrend[DATA] - (fostk[DATA] + h))));
        pdfostk = Math.abs((f - i) / (2 * h));

        Double plungeError = pdfod*fod[ERROR] + pdostrend*ostrend[ERROR] + pdfostk*fostk[ERROR];


        Double[] in =method2CalculateDirCosLine(ostrend[DATA]-h, plunge);
        Double[] fn =method2CalculateDirCosLine(ostrend[DATA] + h, plunge);
        Double[] pdtrend = method2DerivateVector(in, fn, h);
        in =method2CalculateDirCosLine(ostrend[DATA], plunge-h);
        fn =method2CalculateDirCosLine(ostrend[DATA], plunge+h);
        Double[] pdplunge = method2DerivateVector(in, fn, h);

        // Cosenos directores de LÍNEA de la estria.
        Double[] striae = method2CalculateDirCosLine(ostrend[DATA], plunge);
        Double[] strieaeError = new Double[]{pdtrend[0]*ostrend[ERROR]+pdplunge[0]*plungeError,pdtrend[1]*ostrend[ERROR]+pdplunge[1]*plungeError,pdtrend[2]*ostrend[ERROR]+pdplunge[2]*plungeError};


        // Ángulo entre la Linea de strike del plano de falla y la estría.
        i= dotProductAngle(new Double[]{striae[0] - h, striae[1], striae[2]}, new Double[]{Math.cos(fostk[DATA]), Math.sin(fostk[DATA]), 0.0});
        f= dotProductAngle(new Double[]{striae[0] + h, striae[1], striae[2]}, new Double[]{Math.cos(fostk[DATA]), Math.sin(fostk[DATA]), 0.0});
        Double pdstriae0 = Math.abs((f - i) / (2 * h));
        i= dotProductAngle(new Double[]{striae[0],striae[1]-h,striae[2]}, new Double[]{Math.cos(fostk[DATA]),Math.sin(fostk[DATA]),0.0});
        f= dotProductAngle(new Double[]{striae[0],striae[1]+h,striae[2]}, new Double[]{Math.cos(fostk[DATA]),Math.sin(fostk[DATA]),0.0});
        Double pdstriae1 = Math.abs((f - i) / (2 * h));
        i= dotProductAngle(new Double[]{striae[0],striae[1],striae[2]-h}, new Double[]{Math.cos(fostk[DATA]),Math.sin(fostk[DATA]),0.0});
        f= dotProductAngle(new Double[]{striae[0],striae[1],striae[2]+h}, new Double[]{Math.cos(fostk[DATA]),Math.sin(fostk[DATA]),0.0});
        Double pdstriae2 = Math.abs((f - i) / (2 * h));
        i= dotProductAngle(new Double[]{striae[0],striae[1],striae[2]}, new Double[]{Math.cos(fostk[DATA]-h),Math.sin(fostk[DATA]-h),0.0});
        f= dotProductAngle(new Double[]{striae[0],striae[1],striae[2]}, new Double[]{Math.cos(fostk[DATA]+h),Math.sin(fostk[DATA]+h),0.0});
        pdfostk = Math.abs((f - i) / (2 * h));

        gamma[ERROR] = pdstriae0*strieaeError[0] + pdstriae1*strieaeError[1] + pdstriae2*strieaeError[2] + pdfostk*fostk[ERROR];


        //*******************************//
    }
    /** Función encargada de calcular los cosenos directores de los planos. */
    public void method2CalculateDirCosOfPlanes()
    {
        // Plano de falla.
        Double[] temp = method2CalculateDirCosPlane(fostk[DATA], fod[DATA]);
        fpl[DATA] = temp[TruDisp.E];
        fpm[DATA] = temp[TruDisp.N];
        fpn[DATA] = temp[TruDisp.D];

        // Plano de observación
        temp = method2CalculateDirCosPlane(opostk[DATA], opod[DATA]);
        opl[DATA]=temp[TruDisp.E];
        opm[DATA]=temp[TruDisp.N];
        opn[DATA]=temp[TruDisp.D];

        // Plano A
        temp = method2CalculateDirCosPlane(smo1stk[DATA], smo1d[DATA]);
        apl[DATA]=temp[TruDisp.E];
        apm[DATA]=temp[TruDisp.N];
        apn[DATA]=temp[TruDisp.D];

        // Plano B
        temp = method2CalculateDirCosPlane(smo2stk[DATA], smo2d[DATA]);
        bpl[DATA]=temp[TruDisp.E];
        bpm[DATA]=temp[TruDisp.N];
        bpn[DATA]=temp[TruDisp.D];
    }

    /**Calculamos los errores de los cosenos directores*/
    public void method2CalculateDirCosOfPlanesError()
    {
        Double h=0.001;
        Double [] f,i,pdstk,pdd;

        //Derivada de plano de falla.
        i =method2CalculateDirCosPlane(fostk[DATA]-h, fod[DATA]);
        f =method2CalculateDirCosPlane(fostk[DATA]+h, fod[DATA]);
        pdstk = method2DerivateVector(i, f, h);
        i =method2CalculateDirCosPlane(fostk[DATA], fod[DATA]-h);
        f =method2CalculateDirCosPlane(fostk[DATA], fod[DATA]+h);
        pdd = method2DerivateVector(i, f, h);

        fpm[ERROR] = pdstk[TruDisp.N]*fostk[ERROR] + pdd[TruDisp.N]*fod[ERROR];
        fpl[ERROR] = pdstk[TruDisp.E]*fostk[ERROR] + pdd[TruDisp.E]*fod[ERROR];
        fpn[ERROR] = pdstk[TruDisp.D]*fostk[ERROR] + pdd[TruDisp.D]*fod[ERROR];

        //Derivada de plano de observación.
        i =method2CalculateDirCosPlane(opostk[DATA]-h, opod[DATA]);
        f =method2CalculateDirCosPlane(opostk[DATA]+h, opod[DATA]);
        pdstk = method2DerivateVector(i, f, h);
        i =method2CalculateDirCosPlane(opostk[DATA], opod[DATA]-h);
        f =method2CalculateDirCosPlane(opostk[DATA], opod[DATA]+h);
        pdd = method2DerivateVector(i, f, h);

        opm[ERROR] = pdstk[TruDisp.N]*opostk[ERROR] + pdd[TruDisp.N]*opod[ERROR];
        opl[ERROR] = pdstk[TruDisp.E]*opostk[ERROR] + pdd[TruDisp.E]*opod[ERROR];
        opn[ERROR] = pdstk[TruDisp.D]*opostk[ERROR] + pdd[TruDisp.D]*opod[ERROR];

        //Derivada de plano A.
        i =method2CalculateDirCosPlane(smo1stk[DATA]-h, smo1d[DATA]);
        f =method2CalculateDirCosPlane(smo1stk[DATA] + h, smo1d[DATA]);
        pdstk = method2DerivateVector(i, f, h);
        i =method2CalculateDirCosPlane(smo1stk[DATA], smo1d[DATA]-h);
        f =method2CalculateDirCosPlane(smo1stk[DATA], smo1d[DATA] + h);
        pdd = method2DerivateVector(i, f, h);

        apm[ERROR] = pdstk[TruDisp.N]*smo1stk[ERROR] + pdd[TruDisp.N]*smo1d[ERROR];
        apl[ERROR] = pdstk[TruDisp.E]*smo1stk[ERROR] + pdd[TruDisp.E]*smo1d[ERROR];
        apn[ERROR] = pdstk[TruDisp.D]*smo1stk[ERROR] + pdd[TruDisp.D]*smo1d[ERROR];

        //Derivada de plano B.
        i =method2CalculateDirCosPlane(smo2stk[DATA]-h, smo2d[DATA]);
        f =method2CalculateDirCosPlane(smo2stk[DATA] + h, smo2d[DATA]);
        pdstk = method2DerivateVector(i, f, h);
        i =method2CalculateDirCosPlane(smo2stk[DATA], smo2d[DATA]-h);
        f =method2CalculateDirCosPlane(smo2stk[DATA], smo2d[DATA] + h);
        pdd = method2DerivateVector(i, f, h);

        bpm[ERROR] = pdstk[TruDisp.N]*smo2stk[ERROR] + pdd[TruDisp.N]*smo2d[ERROR];
        bpl[ERROR] = pdstk[TruDisp.E]*smo2stk[ERROR] + pdd[TruDisp.E]*smo2d[ERROR];
        bpn[ERROR] = pdstk[TruDisp.D]*smo2stk[ERROR] + pdd[TruDisp.D]*smo2d[ERROR];


    }

    public Double[] method2DerivateVector(Double[] i, Double[] f,Double h)
    {
        Double[] pd = new Double[i.length];

        for(int n=0;n<i.length;n++)
        {
            pd[n] = Math.abs((f[n]-i[n])/(2*h));
        }

        return pd;
    }


    public void method2CalculateDistanceError()
    {
        Double h=0.001;
        int m=0,l=1,n=2;
        Double[] i,f;

        // Distance Error
        i = method2Calculate(fpl[DATA]-h, fpm[DATA], -fpn[DATA], opl[DATA], opm[DATA], -opn[DATA], apl[DATA], apm[DATA], -apn[DATA], bpl[DATA], bpm[DATA], -bpn[DATA], dAB[DATA], smA[DATA], smB[DATA]);
        f = method2Calculate(fpl[DATA] + h, fpm[DATA], -fpn[DATA], opl[DATA], opm[DATA], -opn[DATA], apl[DATA], apm[DATA], -apn[DATA], bpl[DATA], bpm[DATA], -bpn[DATA], dAB[DATA], smA[DATA], smB[DATA]);
        Double[] pdfpl= method2DerivateVector(i, f, h);
        i = method2Calculate(fpl[DATA], fpm[DATA] - h, -fpn[DATA], opl[DATA], opm[DATA], -opn[DATA], apl[DATA], apm[DATA], -apn[DATA], bpl[DATA], bpm[DATA], -bpn[DATA], dAB[DATA], smA[DATA], smB[DATA]);
        f = method2Calculate(fpl[DATA], fpm[DATA] + h, -fpn[DATA], opl[DATA], opm[DATA], -opn[DATA], apl[DATA], apm[DATA], -apn[DATA], bpl[DATA], bpm[DATA], -bpn[DATA], dAB[DATA], smA[DATA], smB[DATA]);
        Double[] pdfpm = method2DerivateVector(i, f, h);
        i = method2Calculate(fpl[DATA], fpm[DATA], -fpn[DATA] - h, opl[DATA], opm[DATA], -opn[DATA], apl[DATA], apm[DATA], -apn[DATA], bpl[DATA], bpm[DATA], -bpn[DATA], dAB[DATA], smA[DATA], smB[DATA]);
        f = method2Calculate(fpl[DATA], fpm[DATA], -fpn[DATA] + h, opl[DATA], opm[DATA], -opn[DATA], apl[DATA], apm[DATA], -apn[DATA], bpl[DATA], bpm[DATA], -bpn[DATA], dAB[DATA], smA[DATA], smB[DATA]);
        Double[] pdfpn = method2DerivateVector(i, f, h);
        i = method2Calculate(fpl[DATA], fpm[DATA], -fpn[DATA], opl[DATA] - h, opm[DATA], -opn[DATA], apl[DATA], apm[DATA], -apn[DATA], bpl[DATA], bpm[DATA], -bpn[DATA], dAB[DATA], smA[DATA], smB[DATA]);
        f = method2Calculate(fpl[DATA], fpm[DATA], -fpn[DATA], opl[DATA] + h, opm[DATA], -opn[DATA], apl[DATA], apm[DATA], -apn[DATA], bpl[DATA], bpm[DATA], -bpn[DATA], dAB[DATA], smA[DATA], smB[DATA]);
        Double[] pdopl = method2DerivateVector(i, f, h);
        i = method2Calculate(fpl[DATA], fpm[DATA], -fpn[DATA], opl[DATA], opm[DATA] - h, -opn[DATA], apl[DATA], apm[DATA], -apn[DATA], bpl[DATA], bpm[DATA], -bpn[DATA], dAB[DATA], smA[DATA], smB[DATA]);
        f = method2Calculate(fpl[DATA], fpm[DATA], -fpn[DATA], opl[DATA], opm[DATA] + h, -opn[DATA], apl[DATA], apm[DATA], -apn[DATA], bpl[DATA], bpm[DATA], -bpn[DATA], dAB[DATA], smA[DATA], smB[DATA]);
        Double[] pdopm = method2DerivateVector(i, f, h);
        i = method2Calculate(fpl[DATA], fpm[DATA], -fpn[DATA], opl[DATA], opm[DATA], -opn[DATA] - h, apl[DATA], apm[DATA], -apn[DATA], bpl[DATA], bpm[DATA], -bpn[DATA], dAB[DATA], smA[DATA], smB[DATA]);
        f = method2Calculate(fpl[DATA], fpm[DATA], -fpn[DATA], opl[DATA], opm[DATA], -opn[DATA]+h, apl[DATA], apm[DATA], -apn[DATA], bpl[DATA], bpm[DATA], -bpn[DATA], dAB[DATA], smA[DATA], smB[DATA]);
        Double[] pdopn = method2DerivateVector(i, f, h);
        i = method2Calculate(fpl[DATA], fpm[DATA], -fpn[DATA], opl[DATA], opm[DATA], -opn[DATA], apl[DATA] - h, apm[DATA], -apn[DATA], bpl[DATA], bpm[DATA], -bpn[DATA], dAB[DATA], smA[DATA], smB[DATA]);
        f = method2Calculate(fpl[DATA], fpm[DATA], -fpn[DATA], opl[DATA], opm[DATA], -opn[DATA], apl[DATA]+h, apm[DATA], -apn[DATA], bpl[DATA], bpm[DATA], -bpn[DATA], dAB[DATA], smA[DATA], smB[DATA]);
        Double[] pdapl = method2DerivateVector(i, f, h);;
        i = method2Calculate(fpl[DATA], fpm[DATA], -fpn[DATA], opl[DATA], opm[DATA], -opn[DATA], apl[DATA], apm[DATA] - h, -apn[DATA], bpl[DATA], bpm[DATA], -bpn[DATA], dAB[DATA], smA[DATA], smB[DATA]);
        f = method2Calculate(fpl[DATA], fpm[DATA], -fpn[DATA], opl[DATA], opm[DATA], -opn[DATA], apl[DATA], apm[DATA]+h, -apn[DATA], bpl[DATA], bpm[DATA], -bpn[DATA], dAB[DATA], smA[DATA], smB[DATA]);
        Double[] pdapm = method2DerivateVector(i, f, h);
        i = method2Calculate(fpl[DATA], fpm[DATA], -fpn[DATA], opl[DATA], opm[DATA], -opn[DATA], apl[DATA], apm[DATA], -apn[DATA] - h, bpl[DATA], bpm[DATA], -bpn[DATA], dAB[DATA], smA[DATA], smB[DATA]);
        f = method2Calculate(fpl[DATA], fpm[DATA], -fpn[DATA], opl[DATA], opm[DATA], -opn[DATA], apl[DATA], apm[DATA], -apn[DATA]+h, bpl[DATA], bpm[DATA], -bpn[DATA], dAB[DATA], smA[DATA], smB[DATA]);
        Double[] pdapn = method2DerivateVector(i, f, h);
        i = method2Calculate(fpl[DATA], fpm[DATA], -fpn[DATA], opl[DATA], opm[DATA], -opn[DATA], apl[DATA], apm[DATA], -apn[DATA], bpl[DATA] - h, bpm[DATA], -bpn[DATA], dAB[DATA], smA[DATA], smB[DATA]);
        f = method2Calculate(fpl[DATA], fpm[DATA], -fpn[DATA], opl[DATA], opm[DATA], -opn[DATA], apl[DATA], apm[DATA], -apn[DATA], bpl[DATA]+h, bpm[DATA], -bpn[DATA], dAB[DATA], smA[DATA], smB[DATA]);
        Double[] pdbpl = method2DerivateVector(i, f, h);
        i = method2Calculate(fpl[DATA], fpm[DATA], -fpn[DATA], opl[DATA], opm[DATA], -opn[DATA], apl[DATA], apm[DATA], -apn[DATA], bpl[DATA], bpm[DATA] - h, -bpn[DATA], dAB[DATA], smA[DATA], smB[DATA]);
        f = method2Calculate(fpl[DATA], fpm[DATA], -fpn[DATA], opl[DATA], opm[DATA], -opn[DATA], apl[DATA], apm[DATA], -apn[DATA], bpl[DATA], bpm[DATA]+h, -bpn[DATA], dAB[DATA], smA[DATA], smB[DATA]);
        Double[] pdbpm = method2DerivateVector(i, f, h);
        i = method2Calculate(fpl[DATA], fpm[DATA], -fpn[DATA], opl[DATA], opm[DATA], -opn[DATA], apl[DATA], apm[DATA], -apn[DATA], bpl[DATA], bpm[DATA], -bpn[DATA] - h, dAB[DATA], smA[DATA], smB[DATA]);
        f = method2Calculate(fpl[DATA], fpm[DATA], -fpn[DATA], opl[DATA], opm[DATA], -opn[DATA], apl[DATA], apm[DATA], -apn[DATA], bpl[DATA], bpm[DATA], -bpn[DATA] + h, dAB[DATA], smA[DATA], smB[DATA]);
        Double[] pdbpn = method2DerivateVector(i, f, h);
        i = method2Calculate(fpl[DATA], fpm[DATA], -fpn[DATA], opl[DATA], opm[DATA], -opn[DATA], apl[DATA], apm[DATA], -apn[DATA], bpl[DATA], bpm[DATA], -bpn[DATA], dAB[DATA] - h, smA[DATA], smB[DATA]);
        f = method2Calculate(fpl[DATA], fpm[DATA], -fpn[DATA], opl[DATA], opm[DATA], -opn[DATA], apl[DATA], apm[DATA], -apn[DATA], bpl[DATA], bpm[DATA], -bpn[DATA], dAB[DATA]+h, smA[DATA], smB[DATA]);
        Double[] pddab = method2DerivateVector(i, f, h);
        i = method2Calculate(fpl[DATA], fpm[DATA], -fpn[DATA], opl[DATA], opm[DATA], -opn[DATA], apl[DATA], apm[DATA], -apn[DATA], bpl[DATA], bpm[DATA], -bpn[DATA], dAB[DATA], smA[DATA] - h, smB[DATA]);
        f = method2Calculate(fpl[DATA], fpm[DATA], -fpn[DATA], opl[DATA], opm[DATA], -opn[DATA], apl[DATA], apm[DATA], -apn[DATA], bpl[DATA], bpm[DATA], -bpn[DATA], dAB[DATA], smA[DATA]+h, smB[DATA]);
        Double[] pdsma = method2DerivateVector(i, f, h);
        i = method2Calculate(fpl[DATA], fpm[DATA], -fpn[DATA], opl[DATA], opm[DATA], -opn[DATA], apl[DATA], apm[DATA], -apn[DATA], bpl[DATA], bpm[DATA], -bpn[DATA], dAB[DATA], smA[DATA], smB[DATA]-h);
        f = method2Calculate(fpl[DATA], fpm[DATA], -fpn[DATA], opl[DATA], opm[DATA], -opn[DATA], apl[DATA], apm[DATA], -apn[DATA], bpl[DATA], bpm[DATA], -bpn[DATA], dAB[DATA], smA[DATA], smB[DATA]+h);
        Double[] pdsmb = method2DerivateVector(i, f, h);

        sm[ERROR] = pdfpl[m]*fpl[ERROR] + pdfpm[m]*fpm[ERROR] + pdfpn[m]*fpn[ERROR] + pdopl[m]*opl[ERROR] + pdopm[m]*opm[ERROR] + pdopn[m]*opn[ERROR] +
                    pdapl[m]*apl[ERROR] + pdapm[m]*apm[ERROR] + pdapn[m]*apn[ERROR] + pdbpl[m]*bpl[ERROR] + pdbpm[m]*bpm[ERROR] + pdbpn[m]*bpn[ERROR] +
                    pddab[m]*dAB[ERROR] + pdsma[m]*smA[ERROR] + pdsmb[m]*smB[ERROR];
        sl[ERROR] = pdfpl[l]*fpl[ERROR] + pdfpm[l]*fpm[ERROR] + pdfpn[l]*fpn[ERROR] + pdopl[l]*opl[ERROR] + pdopm[l]*opm[ERROR] + pdopn[l]*opn[ERROR] +
                pdapl[l]*apl[ERROR] + pdapm[l]*apm[ERROR] + pdapn[l]*apn[ERROR] + pdbpl[l]*bpl[ERROR] + pdbpm[l]*bpm[ERROR] + pdbpn[l]*bpn[ERROR] +
                pddab[l]*dAB[ERROR] + pdsma[l]*smA[ERROR] + pdsmb[l]*smB[ERROR];
        sn[ERROR] = pdfpl[n]*fpl[ERROR] + pdfpm[n]*fpm[ERROR] + pdfpn[n]*fpn[ERROR] + pdopl[n]*opl[ERROR] + pdopm[n]*opm[ERROR] + pdopn[n]*opn[ERROR] +
                pdapl[n]*apl[ERROR] + pdapm[n]*apm[ERROR] + pdapn[n]*apn[ERROR] + pdbpl[n]*bpl[ERROR] + pdbpm[n]*bpm[ERROR] + pdbpn[n]*bpn[ERROR] +
                pddab[n]*dAB[ERROR] + pdsma[n]*smA[ERROR] + pdsmb[n]*smB[ERROR];

        Double in,fn;

        in=normOfVector(new Double[]{sm[DATA]-h, sl[DATA], sn[DATA]});
        fn=normOfVector(new Double[]{sm[DATA]+h, sl[DATA], sn[DATA]});
        Double pdsm = Math.abs((fn-in)/(2*h));
        in=normOfVector(new Double[]{sm[DATA], sl[DATA]-h, sn[DATA]});
        fn=normOfVector(new Double[]{sm[DATA], sl[DATA]+h, sn[DATA]});
        Double pdsl = Math.abs((fn-in)/(2*h));
        in=normOfVector(new Double[]{sm[DATA],sl[DATA],sn[DATA]-h});
        fn=normOfVector(new Double[]{sm[DATA],sl[DATA],sn[DATA]+h});
        Double pdsn = Math.abs((fn-in)/(2*h));

        s[ERROR] = pdsm*sm[ERROR] + pdsl*sl[ERROR] + pdsn*sn[ERROR];


        alpha[ERROR] = fod[ERROR];



        // Gamma Partial Derivative
        Double[] stkLine = new Double[]{Math.cos(fostk[DATA]),Math.sin(fostk[DATA]),0.0};

        in = dotProductAngle(new Double[]{Math.cos(fostk[DATA]-h),Math.sin(fostk[DATA] - h),0.0},new Double[]{sm[DATA],sl[DATA],sn[DATA]});
        fn = dotProductAngle(new Double[]{Math.cos(fostk[DATA]+h),Math.sin(fostk[DATA] + h),0.0},new Double[]{sm[DATA],sl[DATA],sn[DATA]});
        Double pdstkline=Math.abs((fn-in)/(2*h));
        in = dotProductAngle(new Double[]{Math.cos(fostk[DATA]),Math.sin(fostk[DATA]),0.0},new Double[]{sm[DATA]-h,sl[DATA],sn[DATA]});
        fn = dotProductAngle(new Double[]{Math.cos(fostk[DATA]),Math.sin(fostk[DATA]),0.0},new Double[]{sm[DATA]+h,sl[DATA],sn[DATA]});
        Double pdssm=Math.abs((fn-in)/(2*h));
        in = dotProductAngle(new Double[]{Math.cos(fostk[DATA]),Math.sin(fostk[DATA]),0.0},new Double[]{sm[DATA],sl[DATA]-h,sn[DATA]});
        fn = dotProductAngle(new Double[]{Math.cos(fostk[DATA]),Math.sin(fostk[DATA]),0.0},new Double[]{sm[DATA],sl[DATA]+h,sn[DATA]});
        Double pdssl=Math.abs((fn-in)/(2*h));
        in = dotProductAngle(new Double[]{Math.cos(fostk[DATA]),Math.sin(fostk[DATA]),0.0},new Double[]{sm[DATA],sl[DATA],sn[DATA]-h});
        fn = dotProductAngle(new Double[]{Math.cos(fostk[DATA]),Math.sin(fostk[DATA]),0.0},new Double[]{sm[DATA],sl[DATA],sn[DATA]+h});
        Double pdssn=Math.abs((fn-in)/(2*h));

        gamma[ERROR] = pdstkline*fostk[ERROR] + pdssm*sm[ERROR] + pdssl*sl[ERROR] + pdssn*sn[ERROR];


        //
        Double pds,pdalpha,pdgamma;

        // Derivada parcial dss/ds
        fn = method1CalculateSs(s[DATA] + h, gamma[DATA]);
        in = method1CalculateSs(s[DATA] - h, gamma[DATA]);
        pds = Math.abs((fn-in)/(2*h));
        // Derivada parcial dss/dgamma
        fn = method1CalculateSs(s[DATA], gamma[DATA] + h);
        in = method1CalculateSs(s[DATA], gamma[DATA] - h);
        pdgamma = Math.abs((fn-in)/(2*h));

        ss[ERROR] = (pds*s[ERROR])+(pdgamma*gamma[ERROR]);

        // Derivada parcial dsd/ds
        fn = method1CalculateSd(s[DATA] + h, gamma[DATA]);
        in = method1CalculateSd(s[DATA] - h, gamma[DATA]);
        pds = Math.abs((fn-in)/(2*h));
        // Derivada parcial dsd/dgamma
        fn = method1CalculateSd(s[DATA], gamma[DATA] + h);
        in = method1CalculateSd(s[DATA], gamma[DATA] - h);
        pdgamma = Math.abs((fn-in)/(2*h));

        sd[ERROR] = (pds*s[ERROR])+(pdgamma*gamma[ERROR]);

        // Derivada parcial dsv/ds
        fn = method1CalculateSv(s[DATA] + h, gamma[DATA], alpha[DATA]);
        in = method1CalculateSv(s[DATA] - h, gamma[DATA], alpha[DATA]);
        pds = Math.abs((fn-in)/(2*h));
        // Derivada parcial dsv/dgamma
        fn = method1CalculateSv(s[DATA], gamma[DATA] + h, alpha[DATA]);
        in = method1CalculateSv(s[DATA], gamma[DATA] - h, alpha[DATA]);
        pdgamma =Math.abs((fn-in)/(2*h));
        // Derivada parcial dsv/dalpha
        fn = method1CalculateSv(s[DATA], gamma[DATA], alpha[DATA] + h);
        in = method1CalculateSv(s[DATA], gamma[DATA], alpha[DATA] - h);
        pdalpha = Math.abs((fn-in)/(2*h));

        sv[ERROR] = (pds*s[ERROR])+(pdgamma*gamma[ERROR])+(pdalpha*alpha[ERROR]);

        // Derivada parcial dsh/ds
        fn = method1CalculateSh(s[DATA] + h, gamma[DATA], alpha[DATA]);
        in = method1CalculateSh(s[DATA] - h, gamma[DATA], alpha[DATA]);
        pds = Math.abs((fn-in)/(2*h));
        // Derivada parcial dsh/dgamma
        fn = method1CalculateSh(s[DATA], gamma[DATA] + h, alpha[DATA]);
        in = method1CalculateSh(s[DATA], gamma[DATA] - h, alpha[DATA]);
        pdgamma = Math.abs((fn-in)/(2*h));
        // Derivada parcial dsh/dalpha
        fn = method1CalculateSh(s[DATA], gamma[DATA], alpha[DATA] + h);
        in = method1CalculateSh(s[DATA], gamma[DATA], alpha[DATA] - h);
        pdalpha = Math.abs((fn-in)/(2*h));

        sh[ERROR] = (pds*s[ERROR])+(pdgamma*gamma[ERROR])+(pdalpha*alpha[ERROR]);

    }

    /** Función encargada de calcular las mediciones del método 1 empleando los cosenos directores. */
    public void method2CalculatePitchs()
    {
        /** Calculamos los pitch */

        // Cosenos Directores del plano de Falla (Fault Plane)
        Double[] fp = new Double[]{fpm[DATA],fpl[DATA],fpn[DATA]};

        // Cosenos Directores de línea de strike del Plano de falla;
        Double[] stkLine = new Double[]{Math.cos(fostk[DATA]),Math.sin(fostk[DATA]),0.0};

        // Cosenos Directores del plano A (Marcador 1)
        Double[] mrkr = new Double[]{apm[DATA],apl[DATA],apn[DATA]};

        // Cosenos Directores del plano de observación.(Obsevation Plane)
        Double[] op = new Double[]{opm[DATA],opl[DATA],opn[DATA]};

        // Calculamos el plunge para asegurar que la estria está siempre sobre el plano.
        Double plunge = Math.atan(Math.tan(fod[DATA]) * Math.sin(((ostrend[DATA] - fostk[DATA]) > Math.toRadians(90)) ? (Math.PI - (ostrend[DATA] - fostk[DATA])) : (ostrend[DATA] - fostk[DATA])));

        // Cosenos directores de LÍNEA de la estria.
        Double[] striae = method2CalculateDirCosLine(ostrend[DATA], plunge);

        // Dirección de la LINEA stk en grados, con la que se compara si esta hacia el norte o el sur
        Double stk = fostk[DATA];

        // Angulo entre la LINEA de strike del plano de falla y la intersección del plano de falla y el plano A.
        beta[DATA] = dotProductAngle(crossProduct(fp, mrkr), stkLine);
        // Obtemos la orientación
        betaO = (stk< (Math.toRadians(90)) || stk>(Math.toRadians(270)))? ((beta[DATA]>(Math.toRadians(90)))? "S":"N"):((beta[DATA]>(Math.toRadians(90)))? "N":"S");
        // Si el ángulo es mayor de 90 entonces el ángulo es 180-angulo.
        beta[DATA]=(beta[DATA]>(Math.toRadians(90)))? (Math.PI-beta[DATA]):(beta[DATA]);

        // Angulo entre la LINEA de strike del plano de falla y la intersección del plano de falla y el plano de observación.
        phi[DATA] = dotProductAngle(crossProduct(fp, op), stkLine);
        // Obtenemos la orientación.
        phiO =(stk< (Math.toRadians(90)) || stk>(Math.toRadians(270)))? ((phi[DATA]>(Math.toRadians(90)))? "S":"N"):((phi[DATA]>(Math.toRadians(90)))? "N":"S");
        // Si el ángulo es mayor de 90 entonces el ángulo es 180-angulo.
        phi[DATA]=(phi[DATA]>(Math.toRadians(90)))? (Math.PI-phi[DATA]):(phi[DATA]);

        // Angulo del plano de falla.
        alpha[DATA] = fod[DATA];

        /* No estoy seguro como se mas conveniente realizr esta comprobación, por el momento le damos prioridad a gamma
        * si existe algún dato en gamma se calcula con ese.*/

        // Verificamos si el usuario ha introducido algún dato de gamma, si es así no se requiere de calcular.
        if(gamma[DATA]==0 && ostrend[DATA]!=0)
        {
            // Ángulo entre la Linea de strike del plano de falla y la estría.
            gamma[DATA] = dotProductAngle(striae, stkLine);
            // Obtenemos la orientación.
            gammaO =(stk< (Math.toRadians(90)) || stk>(Math.toRadians(270)))? ((gamma[DATA]>(Math.toRadians(90)))? "S":"N"):((gamma[DATA]>(Math.toRadians(90)))? "N":"S");
            // Si el ángulo es mayor de 90 entonces el ángulo es 180-angulo.
            gamma[DATA]=(gamma[DATA]>(Math.toRadians(90)))? (Math.PI-gamma[DATA]):(gamma[DATA]);
        }

        MapView = "Arbitrary Line";

    }

    /** Función para calcular los cosenos directores de un plano con strike y dip */
    public static Double[] method2CalculateDirCosPlane(Double strike, Double dip)
    {
        Double[] temp= new Double[] {0.0,0.0,0.0};

        temp[TruDisp.N] = Math.sin(strike)*Math.sin(dip);
        temp[TruDisp.E] = -Math.cos(strike)*Math.sin(dip);
        temp[TruDisp.D] = Math.sqrt(1 - ((temp[0] * temp[0]) + (temp[1] * temp[1])));

        return temp;
    }


    public  Double normOfVector(Double[] a)
    {
        Double n =0.0;
        for(int i=0; i<a.length;i++)
        {
            n += a[i]*a[i];
        }

        n = Math.sqrt(n);

        return n;
    }

    /** Funcioón para calcular los cosenos directores de una línea con trend y plinge.*/
    public static Double[] method2CalculateDirCosLine(Double trend, Double plunge)
    {
        Double[] temp= new Double[] {0.0,0.0,0.0};

        temp[TruDisp.N] = Math.cos(trend)*Math.cos(plunge);
        temp[TruDisp.E] = Math.sin(trend)*Math.cos(plunge);
        temp[TruDisp.D] = Math.sqrt(1 - ((temp[0] * temp[0]) + (temp[1] * temp[1])));

        return temp;
    }

    /** Función encargada de calcular el desplazamiento empleando el método 2;*/
    public Double[] method2Calculate(Double li, Double mi, Double ni, Double lo, Double mo, Double no,  Double lj, Double mj, Double nj,
                                   Double lk, Double mk, Double nk, Double d, Double a, Double b)
    {
        // Director cosines of the line of intersection of fp and op

        Double X = Math.sqrt(Math.pow((mi*no)-(ni*mo),2)+Math.pow((ni*lo)-(li*no),2)+Math.pow((li*mo)-(mi*lo),2));

        Double lp= ((mi*no)-(ni*mo))/X , mp = ((ni*lo)-(li*no))/X, np =((li*mo)-(mi*lo))/X;

        Double D = -lk*mj*ni + lj*mk*ni + lk*mi*nj - li*mk*nj - lj*mi*nk + li*mj*nk ; //Determinante

        Double x1 = (-d*(lk*lp + mk*mp + nk*np)*(mj*ni - nj*mi))/D;
        Double y1 = (-d*(lk*lp + mk*mp + nk*np)*(nj*li - lj*ni))/D;
        Double z1 = (-d*(lk*lp + mk*mp + nk*np)*(lj*mi - mj*li))/D;

        Double x2 = ((a*(lj*lp + mj*mp + nj*np)*(mk*ni - nk*mi))-((d + b)*(lk*lp + mk*mp + nk*np)*(mj*ni - nk*mi)))/D;
        Double y2 = ((a*(lj*lp + mj*mp + nj*np)*(nk*li - lk*ni))-((d + b)*(lk*lp + mk*mp + nk*np)*(nj*li - lj*ni)))/D;
        Double z2 = ((a*(lj*lp + mj*mp + nj*np)*(lk*mi - mk*li))-((d + b)*(lk*lp + mk*mp + nk*np)*(lj*mi - mj*li)))/D;


        return new Double[]{y2-y1,x2-x1,z2-z1};
    }

    /******************************************************************************************************************/
    /** Métodos SET.*/
    /******************************************************************************************************************/

    /** Método 1. */
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

    /** Método 2. */

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

    /** Generales */
    public Boolean setExperiment(String exp)
    {
        experiment = exp;
        return true;
    }
    public Boolean setNotes(String nts)
    {notes=nts; return true;}

    /******************************************************************************************************************/
    /** Métodos GET.*/
    /******************************************************************************************************************/

    /** Método 1. */
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


    /** Método 2. */
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

    /** Generales */
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



    /******************************************************************************************************************/
    /** Métodos To.*/
    /******************************************************************************************************************/

    /** To String*/
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


