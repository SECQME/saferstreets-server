package com.secqme.util.cache;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.internal.InternalUtils;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.dynamodbv2.util.Tables;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.secqme.crimedata.domain.model.CrimeTypeVO;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Edmund on 13/11/2015.
 */
public class DynamoDBCacheUtil implements CacheUtil {

    private final static Logger myLog = Logger.getLogger(DynamoDBCacheUtil.class);
    private AWSCredentials awsCredentials;
    private AmazonDynamoDBClient dynamoDBClient;
    private String tableName;


    public DynamoDBCacheUtil(String accessKeyId, String secretAccessKey, String tableName) {
        awsCredentials = new BasicAWSCredentials(accessKeyId,secretAccessKey);
        dynamoDBClient = new AmazonDynamoDBClient(awsCredentials);
        this.tableName = tableName;
        Region usEast1 = Region.getRegion(Regions.US_EAST_1);
        dynamoDBClient.setRegion(usEast1);
        checkTable();

    }

    @Override
    public Object getCachedObject(String key, Class className) {

        ObjectMapper mapper = new ObjectMapper();
        Object result = null;
        try {
//            myLog.debug("key : " + key);
            HashMap<String, Condition> filter = new HashMap<String, Condition>();
            Condition condition = new Condition()
                    .withComparisonOperator(ComparisonOperator.EQ.toString())
                    .withAttributeValueList(new AttributeValue().withS(key));
//            myLog.debug("condition : "+condition);
            filter.put("Key", condition);
//            myLog.debug("scanFilter : " + filter);
            QueryRequest queryRequest = new QueryRequest(tableName)
                    .withReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL)
                    .withKeyConditions(filter);
            QueryResult queryResult = dynamoDBClient.query(queryRequest);
//            myLog.debug("queryResult : " + queryResult);
            if (queryResult.getItems() != null) {
                if(queryResult.getItems().size() > 0) {
//                    myLog.debug("item got retrieved from dynamodb");
                    Map<String, String> attribute = InternalUtils.toSimpleMapValue(queryResult.getItems().get(0));
                    result = mapper.readValue(new JSONObject(attribute).get(key).toString(), className);
                    delay(queryResult.getConsumedCapacity().getCapacityUnits());
                }else{
                    myLog.debug("No item inside");
                }
//        result = mapper.readValue(scanResult.get,className);
//            myLog.debug("scanResult : " + scanResult.getConsumedCapacity().getCapacityUnits());
            } else {
                myLog.debug("Didn't get to retrieve item");
            }
        }catch(IOException ex){
            ex.printStackTrace();
        }catch(JSONException ex){
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public void storeObjectIntoCache(String key, Object obj) {
        try {
//            Convert Object to String then store as AttributeValue
            ObjectMapper mapper = new ObjectMapper();
//            Delete current report and regenerate
            expireCachedObject(key);
            Item item = new Item().withJSON(key,mapper.writeValueAsString(obj));
            Map<String, AttributeValue> attribute = InternalUtils.toAttributeValues(item);
            attribute.put("Key",new AttributeValue(key));

//            Add item to DB
            PutItemRequest putItemRequest = new PutItemRequest(tableName,attribute);
            putItemRequest.withReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL);
            PutItemResult putItemResult = dynamoDBClient.putItem(putItemRequest);
//            myLog.debug("putItemRequest : " + putItemResult.getConsumedCapacity().getCapacityUnits());
            delay(putItemResult.getConsumedCapacity().getCapacityUnits());

        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void expireCachedObject(String key) {
        Map<String,AttributeValue> attribute = new HashMap<String, AttributeValue>();
        attribute.put("Key", new AttributeValue(key));
        DeleteItemRequest  deleteItemRequest = new DeleteItemRequest()
                .withTableName(tableName)
                .withKey(attribute)
                .withReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL);
        DeleteItemResult result = dynamoDBClient.deleteItem(deleteItemRequest);
        delay(result.getConsumedCapacity().getCapacityUnits());
    }

    @Override
    public void expireAllCacheObject() {

    }

    @Override
    public void displayDetails() {

    }

    private void checkTable(){
        if(Tables.doesTableExist(dynamoDBClient,tableName)){
            myLog.debug(tableName + " table is active in DynamoDB");
        }else{
            myLog.debug(tableName + " table doesn't exist in DynamoDB");
        }
    }

    private void delay(Double capacity){
        try {
//            myLog.debug("Capacity : " + capacity);
            if(capacity >= 10) {
//                myLog.debug("Delay 1 Sec");
                Thread.sleep(1000);
            }
        }catch(InterruptedException ex){
            Thread.currentThread().interrupt();
        }
    }


}
