package sample.task.list.service;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by kopelevi on 04/09/2015.
 */
public class ItemServiceClientImpl implements ItemsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemServiceClientImpl.class.getName());

    private static final String DEFAULT_PROTOCOL = "http";
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_CONTEXT = "";
    private static final String DEFAULT_MEDIA_TYPE = MediaType.APPLICATION_JSON;
    private static final String RESOUCEE = "items";
    private static final String APP_PATH = "rest";
    private static final int MAX_PORT = 65535;

    private final WebTarget webTarget; //thread safe

    public ItemServiceClientImpl(String protpcol, String hostname, int port, String context) {
        //init client
        try {
            String serviceURL = buildServiceURL(protpcol, hostname, port, context);
            Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build(); // register Jackson JSON providers to automatically handled Object <--> JSON
            this.webTarget = client.target(serviceURL.toString());
            LOGGER.info("Initiated client with target " + serviceURL);
        } catch (Exception e) {
            String msg = "Failed to init client";
            LOGGER.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }

    public ItemServiceClientImpl(String hostname, int port) {
        this(DEFAULT_PROTOCOL, hostname, port, DEFAULT_CONTEXT);
    }

    private String buildServiceURL(String protocol, String hostname, int port, String context) {
        StringBuffer serviceURL = new StringBuffer(protocol).append("://").append(hostname);
        // validate and concat port
        if (port > 0 && port <= MAX_PORT) {
            serviceURL.append(":").append(port);
        } else {
            String msg = "Given port: " + port + " is invalid, should be in the range of 1-" + MAX_PORT;
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }
        // handle context
        if (context != null && !context.isEmpty()) {
            serviceURL.append("/").append(context);
        }
        // concat rest app and resource path
        serviceURL.append("/").append(APP_PATH).append("/").append(RESOUCEE);

        return serviceURL.toString();
    }


    public List<TaskItem> getAllItems() {
        try {
            Invocation.Builder invocationBuilder = webTarget.request(DEFAULT_MEDIA_TYPE);
            TaskItems taskItems = invocationBuilder.get(TaskItems.class);
            return taskItems.getItems();
        } catch (Exception e) {
            throw new ItemServiceException(ItemServiceErrorMessages.FAILED_TO_GET_ALL, e);
        }
    }

    public TaskItem getItem(int itemId) {
        try {
            Invocation.Builder invocationBuilder = webTarget.path(String.valueOf(itemId)).request(DEFAULT_MEDIA_TYPE);
            return invocationBuilder.get(TaskItem.class);
        } catch (NotFoundException notFoundException) {
            throw new ItemServiceItemNotFoundException(itemId);
        } catch (Exception e) {
            throw new ItemServiceException(ItemServiceErrorMessages.FAILED_TO_GET_ITEM + itemId, e);
        }
    }

    public void addItem(TaskItem item) {
        Response response;
        try {
            Invocation.Builder invocationBuilder = webTarget.request(DEFAULT_MEDIA_TYPE);
            response = invocationBuilder.post(Entity.entity(item, DEFAULT_MEDIA_TYPE));
        } catch (Exception e) {
            throw new ItemServiceException(ItemServiceErrorMessages.FAILED_TO_ADD_ITEM + item.toString(), e);
        }

        if (Response.Status.BAD_REQUEST.getStatusCode() == response.getStatus()) {
            throw new ItemServiceInvalidParamException(response.getStatusInfo().getReasonPhrase());
        }
    }

    public void removeItem(int itemId) {
        try {
            Invocation.Builder invocationBuilder = webTarget.path(String.valueOf(itemId)).request();
            invocationBuilder.delete();
        } catch (Exception e) {
            throw new ItemServiceException(ItemServiceErrorMessages.FAILED_TO_REMOVE_ITEM + itemId, e);
        }
    }

    public void updateItem(TaskItem item) {
        Response response;
        try {
            Invocation.Builder invocationBuilder = webTarget.request(DEFAULT_MEDIA_TYPE);
            response = invocationBuilder.put(Entity.entity(item, DEFAULT_MEDIA_TYPE));
        } catch (Exception e) {
            throw new ItemServiceException(ItemServiceErrorMessages.FAILED_TO_UPDATE_ITEM + item.toString(), e);
        }

        if (Response.Status.BAD_REQUEST.getStatusCode() == response.getStatus()) {
            throw new ItemServiceInvalidParamException(response.getStatusInfo().getReasonPhrase());
        }
    }

    public void clearAll() {
        try {
            Invocation.Builder invocationBuilder = webTarget.request();
            invocationBuilder.delete();
        } catch (Exception e) {
            throw new ItemServiceException(ItemServiceErrorMessages.FAILED_TO_CLEAR_ALL, e);
        }
    }
}
