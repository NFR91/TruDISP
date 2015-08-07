package TruDisp;

import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Nieto on 07/08/15.
 */
public class TDOpenSave {

    private FileChooser fileChooser;
    private Stage stage;

    public TDOpenSave(Stage st)
    {
        fileChooser = new FileChooser();
        stage = st;
    }

    public void Save(ArrayList<TDData> data )
    {
        fileChooser.setTitle("Save TruDisp Session");

        FileWriter file = null;
        try {
            file = new FileWriter(fileChooser.showSaveDialog(stage));
            PrintWriter pw = new PrintWriter(file);
            data.stream().forEach(tdData -> {
                pw.println(tdData);
            });
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<TDData> Open(TruDispStatusPane tdpane)
    {
        fileChooser.setTitle("Open TruDisp Session");

        FileReader filereader= null;

        ArrayList<TDData> arrayListTDData = new ArrayList<>();

        try
        {
            File file = new File (fileChooser.showOpenDialog(stage).getAbsolutePath()); // Open the file
            filereader = new FileReader (file); // Create a FileReader
            BufferedReader bufferedreader = new BufferedReader(filereader); // Create the BufferedReader

            // Here we read each line of the file.
            String line;
            while((line=bufferedreader.readLine())!=null)
                arrayListTDData.add(new TDData(line,tdpane));   // Each line is a data set.
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return arrayListTDData;
    }

}
