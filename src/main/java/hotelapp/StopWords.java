package hotelapp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This class has method to populate List of stop words.
 * By: Gandhar Kulkarni
 */
public class StopWords {
    private static final Set<String> stopWordsList= new HashSet<>();

    /**
     * Reads stop words from a file and generates a list
     * @return ArrayList<String> stopWordsList
     */
    public Set<String> populateStopWordList(){
        //String stopWordsFilePath = "input/stopwords/stopwords.txt";
        String stopWordsFilePath = "/Users/gandharkulkarni/Desktop/Captain GAK/USFCA/CS_601/Assignments/project4-gandharkulkarni/input/stopwords/stopwords.txt";
        String line="";
        try(BufferedReader reader = new BufferedReader(new FileReader(stopWordsFilePath))){
            while((line = reader.readLine())!=null){
                stopWordsList.add(line.trim());
            }
        }
        catch (IOException ioException){
            LogHelper.getLogger().error(ioException);
        }
        catch (Exception ex){
            LogHelper.getLogger().error(ex);
        }
        return Collections.unmodifiableSet(stopWordsList);
    }
}
