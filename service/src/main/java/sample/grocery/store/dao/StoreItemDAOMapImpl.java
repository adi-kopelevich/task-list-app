package sample.grocery.store.dao;

import sample.grocery.store.service.pojo.StoreItem;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by kopelevi on 04/09/2015.
 */
public class StoreItemDAOMapImpl implements StoreItemDAO {

    // ConcurrentHashMap for handing concurrency,
    // its drawbacks are:
    //      - larger memory footprint compared to a regular has map and
    //      - handling null keys which are allowed as contrast to regular hash map, as it  might be returned while iterating over them
    private final Map<Integer, StoreItem> itemsMap = new ConcurrentHashMap<>();

    private static final StoreItemDAOMapImpl INSTANCE = new StoreItemDAOMapImpl();

    // hide c'tor
    private StoreItemDAOMapImpl() {
    }

    public static StoreItemDAO getInstance() {
        return INSTANCE;
    }

    public StoreItem getItem(int itemId) {
        return itemsMap.get(itemId);
    }

    public List<StoreItem> getItems() {// ConcurrentHashMap might contain null keys
        return itemsMap.entrySet().stream()
                .filter(x -> x != null)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    public void putItem(StoreItem item) {
        itemsMap.put(item.getId(), item);
    }

    public void removeItem(int itemId) {
        itemsMap.remove(itemId);
    }

    public void clear() {
        itemsMap.clear();
    }
}