package sample.task.list.service;

/**
 * Created by kopelevi on 19/10/2015.
 */
public class ItemServiceItemNotFoundException extends ItemServiceException {

    public ItemServiceItemNotFoundException(int itemId) {
        super(ItemServiceErrorMessages.ITEM_WITH_GIVEN_ID_WAS_NOT_FOUND + itemId);
    }
}
