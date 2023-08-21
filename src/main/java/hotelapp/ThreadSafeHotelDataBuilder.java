package hotelapp;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Threadsafe hashmap builder class extends HotelDataBuilder
 * By: Gandhar Kulkarni
 */
public class ThreadSafeHotelDataBuilder extends HotelDataBuilder {
    private ReentrantReadWriteLock lock;

    /**
     * Constructor for threadsafe hashmap builder class
     */
    public ThreadSafeHotelDataBuilder() {
        this.lock = new ReentrantReadWriteLock();
    }

    /**
     * Implements updateHotelHashMap using locks
     *
     * @param hotels List of Hotel objects
     */
    @Override
    public void updateHotelHashMap(List<Hotel> hotels) {
        try {
            lock.writeLock().lock();
            super.updateHotelHashMap(hotels);
        } catch (Exception ex) {
            LogHelper.getLogger().error(ex);
        } finally {
            lock.writeLock().unlock();

        }
    }

    /**
     * Implements updateHotelReviewHashMap using locks
     *
     * @param reviews List of Review objects
     */
    @Override
    public void updateHotelReviewHashMap(List<Review> reviews) {
        try {
            lock.writeLock().lock();
            super.updateHotelReviewHashMap(reviews);
        } catch (Exception ex) {
            LogHelper.getLogger().error(ex);
        } finally {
            lock.writeLock().unlock();

        }
    }

    /**
     * Implements updateWordHashMap using locks
     *
     * @param reviews List of Review objects
     */
    @Override
    public void updateWordHashMap(List<Review> reviews) {
        try {
            lock.writeLock().lock();
            super.updateWordHashMap(reviews);
        } catch (Exception ex) {
            LogHelper.getLogger().error(ex);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Implements updateHotelReview using locks
     *
     * @param updatedReview
     */
    @Override
    public void updateHotelReview(Review updatedReview) {
        try {
            lock.writeLock().lock();
            super.updateHotelReview(updatedReview);
        } catch (Exception ex) {
            LogHelper.getLogger().error(ex);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Implements deleteHotelReview using locks
     *
     * @param deleteReview
     */
    @Override
    public void deleteHotelReview(Review deleteReview) {
        try {
            lock.writeLock().lock();
            super.deleteHotelReview(deleteReview);
        } catch (Exception ex) {
            LogHelper.getLogger().error(ex);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Returns a set of all existing review ids
     *
     * @return
     */
    @Override
    public Set<String> getExistingReviewId() {
        Set<String> reviewIdSet = null;
        try {
            lock.readLock().lock();
            reviewIdSet = super.getExistingReviewId();
        } catch (Exception ex) {
            LogHelper.getLogger().error(ex);
        } finally {
            lock.readLock().unlock();
        }
        return reviewIdSet;
    }

    /**
     * Returns a set of all valid HotelIds
     *
     * @return Set<String>
     */
    @Override
    public Set<String> getAllHotelIds() {
        Set<String> hotelIdSet = null;
        try {
            lock.readLock().lock();
            hotelIdSet = super.getAllHotelIds();
        } catch (Exception ex) {
            LogHelper.getLogger().error(ex);
        } finally {
            lock.readLock().unlock();
        }
        return hotelIdSet;
    }

    /**
     * Returns Hotel for given HotelId
     *
     * @param hotelId
     * @return Hotel
     */
    public Hotel getHotelByHotelId(String hotelId) {
        Hotel hotel = null;
        try {
            lock.readLock().lock();
            hotel = super.getHotelByHotelId(hotelId);
        } catch (Exception ex) {
            LogHelper.getLogger().error(ex);
        } finally {
            lock.readLock().unlock();
        }
        return hotel;
    }

    /**
     * Returns set of Reviews of Hotel for given HotelId
     *
     * @param hotelId
     * @return SortedSet<Review>
     */
    public Set<Review> getAllReviewsForHotel(String hotelId) {
        Set<Review> reviewSet = null;
        try {
            lock.readLock().lock();
            reviewSet = super.getAllReviewsForHotel(hotelId);
        } catch (Exception ex) {
            LogHelper.getLogger().error(ex);
        } finally {
            lock.readLock().unlock();
        }
        return reviewSet;
    }

    /**
     * Returns a set of reviews containing a specific word
     *
     * @param word
     * @return SortedSet<Review>
     */
    public Set<Review> getReviewsContainingSpecificWord(String word) {
        Set<Review> reviewSet = null;
        try {
            lock.readLock().lock();
            reviewSet = super.getReviewsContainingSpecificWord(word);
        } catch (Exception ex) {
            LogHelper.getLogger().error(ex);
        } finally {
            lock.readLock().unlock();
        }
        return reviewSet;
    }

    /**
     * Returns hotel details in Json format
     *
     * @param hotelId
     * @return JsonObject
     */
    public JsonObject getHotelByHotelIdInJsonFormat(String hotelId) {
        JsonObject hotelInfoResponse = null;
        try {
            lock.readLock().lock();
            hotelInfoResponse = super.getHotelByHotelIdInJsonFormat(hotelId);
        } catch (Exception ex) {
            LogHelper.getLogger().error(ex);
        } finally {
            lock.readLock().unlock();
        }
        return hotelInfoResponse;
    }

    /**
     * Returns set of reviews in Json format
     *
     * @param hotelId        String
     * @param requestedCount int
     * @return JsonObject
     */
    public JsonObject getAllReviewsForHotelInJsonFormat(String hotelId, int requestedCount) {
        JsonObject reviewInfoResponse = null;
        try {
            lock.readLock().lock();
            reviewInfoResponse = super.getAllReviewsForHotelInJsonFormat(hotelId, requestedCount);
        } catch (Exception ex) {
            LogHelper.getLogger().error(ex);
        } finally {
            lock.readLock().unlock();
        }
        return reviewInfoResponse;
    }

    /**
     * Returns hotel and weather details in Json format
     *
     * @param hotelId String
     * @return JsonObject
     */
    public JsonObject getWeatherDataInJsonFormat(String hotelId) {
        JsonObject weatherInfoResponse = null;
        try {
            lock.readLock().lock();
            weatherInfoResponse = super.getWeatherDataInJsonFormat(hotelId);
        } catch (Exception ex) {
            LogHelper.getLogger().error(ex);
        } finally {
            lock.readLock().unlock();
        }
        return weatherInfoResponse;
    }

    /**
     * Returns set of reviews containing a specific word in Json Format
     *
     * @param word           String
     * @param requestedCount int
     * @return JsonObject
     */
    public JsonObject getReviewsContainingSpecificWordInJsonFormat(String word, int requestedCount) {
        JsonObject wordResponse = null;
        try {
            lock.readLock().lock();
            wordResponse = super.getReviewsContainingSpecificWordInJsonFormat(word, requestedCount);
        } catch (Exception ex) {
            LogHelper.getLogger().error(ex);
        } finally {
            lock.readLock().unlock();
        }
        return wordResponse;
    }

}
