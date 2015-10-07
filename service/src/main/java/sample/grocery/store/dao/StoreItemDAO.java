package sample.grocery.store.dao;

import sample.grocery.store.service.pojo.StoreItem;

import java.util.List;

/**
 * Created by kopelevi on 04/09/2015.
 */
public interface StoreItemDAO {

    StoreItem getItem(int itemId);

    List<StoreItem> getItems();

    void putItem(StoreItem item);

    void removeItem(int itemId);

    void clear();
}