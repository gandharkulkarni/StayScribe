package hotelapp;

import hotelappserver.DatabaseHelper;
import org.apache.logging.log4j.LogManager;

import javax.xml.crypto.Data;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/**
 * This class has methods to parse the directory to search for Json file recursively.
 * By: Gandhar Kulkarni
 */
public class DirectoryParser {
    private Phaser phaser;
    private ExecutorService poolThreads;
    private ThreadSafeHotelDataBuilder hashMapBuilder;
    private boolean isOutputPathProvided;

    /**
     * Directory parser constructor
     * @param threadLimit Number of threads to parse reviews concurrently
     * @param isOutputPathProvided  true if Output file path is provided otherwise false
     * @param hashMapBuilder Hashmap builder object
     */
    public DirectoryParser(int threadLimit, boolean isOutputPathProvided, ThreadSafeHotelDataBuilder hashMapBuilder){
        phaser = new Phaser();
        poolThreads = Executors.newFixedThreadPool(threadLimit);
        this.isOutputPathProvided =isOutputPathProvided;
        this.hashMapBuilder = hashMapBuilder;
    }

    /**
     * Worker class to parse Json files concurrently.
     */
    public class FileWorker implements Runnable{
        private Path filePath;
        private List<Review> localReviewsList;

        public FileWorker(Path filePath){
            this.filePath = filePath;
        }

        /**
         * Parses review Json file and calls updateHashmap internally
         */
        @Override
        public void run(){
            try {
                localReviewsList = JsonFileParser.parseReviewJsonFile(this.filePath);
                DatabaseHelper dbHelper = DatabaseHelper.getInstance();
                dbHelper.insertRecordIntoReviewsTable(localReviewsList);
                //buildReviewHashMaps(localReviewsList);
            }
            catch (Exception ex){
                LogHelper.getLogger().error(ex);
            }
            finally {
                phaser.arriveAndDeregister();
            }

        }
    }

    /**
     * Parses hotel json file and updates hotel hashmaps internally.
     * @param path Hotel Json file path
     * @return
     */
    public boolean parseHotelJsonFile(String path){
        boolean returnValue = false;
        try{
            List<Hotel> hotelsList = JsonFileParser.parseHotelJsonFile(Paths.get(path));
            DatabaseHelper dbHelper = DatabaseHelper.getInstance();
            dbHelper.insertRecordIntoHotelsTable(hotelsList);
            //hashMapBuilder.updateHotelHashMap(hotelsList);
            returnValue=true;
        }
        catch (Exception ex){
            LogHelper.getLogger().error(ex);
            returnValue = false;
        }
        return returnValue;
    }
    /**
     * Parse a directory recursively to check any Json files in the directory.
     * Returns true if Directory is parsed successfully. Otherwise false
     * @param path Path to check for Json files
     * @return Boolean - True if parsing is successful otherwise false.
     */
    public boolean searchReviewJsonFileInDirectory(String path) {
        boolean returnValue = false;
        Path filePath = Paths.get(path);
        if (Files.isDirectory(filePath) == false) {
            filePath = filePath.getParent();
        }
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(filePath)) {
            for (Path folder : directoryStream) {
                if (Files.isDirectory(folder)) {
                    returnValue = searchReviewJsonFileInDirectory(folder.toString());
                }
                else {
                    if (folder.toString().endsWith("json")) {
                        FileWorker worker = new FileWorker(folder);
                        poolThreads.submit(worker);
                        phaser.register();
                        returnValue=true;
                    }
                }
            }
        } catch (Exception ex) {
            LogHelper.getLogger().error(ex);
            returnValue = false;
        }

        return returnValue;
    }

    /**
     * Initialize shutdown for phaser and executor
     */
    public void initializeShutdownSequence(){
        phaser.awaitAdvance(phaser.getPhase());
        shutdownExecutor();
    }

    /**
     * Shutdowns executor
     */
    private void shutdownExecutor()
    {
        try {
            poolThreads.shutdown();
            poolThreads.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException ieException) {
            LogManager.getLogger().error(ieException);
        } catch (Exception ex) {
            LogHelper.getLogger().error(ex);
        }
    }

    /**
     * Builds and updates hashmaps for reviews
     * @param localReviewList List of review objects
     */
    private void buildReviewHashMaps(List<Review> localReviewList){
        hashMapBuilder.updateHotelReviewHashMap(localReviewList);
        if(!isOutputPathProvided) {
            hashMapBuilder.updateWordHashMap(localReviewList);
        }
    }
}
