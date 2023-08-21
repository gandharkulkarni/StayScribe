package hotelapp;

import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Thread safe efficient search class extends efficient search
 * By: Gandhar Kulkarni
 */
public class ThreadSafeEfficientSearch extends EfficientSearch{
    private ReentrantReadWriteLock lock;

    /**
     * Constructor for Threadsafe efficient search class
     * @param hashMapBuilder Hashmap builder object
     */
    public ThreadSafeEfficientSearch(ThreadSafeHotelDataBuilder hashMapBuilder) {
        super(hashMapBuilder);
        this.lock = new ReentrantReadWriteLock();
    }

    /**
     * Implements thread safe find hotel method using locks
     * @param hotelId hotel id of a hotel
     */
    @Override
    public void findHotel(String hotelId){
        try {
            lock.readLock().lock();
            super.findHotel(hotelId);
        }
        catch (Exception ex){
            LogHelper.getLogger().error(ex);
        }
        finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Implements thread safe find reviews using locks
     * @param hotelId HotelId
     */
    @Override
    public void findReviews(String hotelId){
        try {
            lock.readLock().lock();
            super.findReviews(hotelId);
        }
        catch (Exception ex){
            LogHelper.getLogger().error(ex);
        }
        finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Implements thread safe find word using locks
     * @param word Word
     */
    @Override
    public void findWord(String word){
        try {
            lock.readLock().lock();
            super.findWord(word);
        }
        catch (Exception ex){
            LogHelper.getLogger().error(ex);
        }
        finally {
            lock.readLock().unlock();
        }
    }

}
