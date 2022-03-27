package com.mobile.core.utils;

import com.mobile.core.annotations.DataProviderParams;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TestDataProvider {

    private static final String TEST_DATA = "Test_Data";

    @DataProvider(name="JsonDataProvider")
    public static synchronized Object[][] jsonDataProvider(Method testMethod){

        DataProviderParams dataProviderParams = testMethod.getAnnotation(DataProviderParams.class);

        List<Object> list = readJson(dataProviderParams.fileName(), dataProviderParams.jsonKey());
        Object[][] data = new Object[list.size()][1];

        for(int i=0; i< list.size(); i++){
            data[i][0] = list.get(i);
        }

        return data;

    }

    public static synchronized List<Object> readJson(final String fileName, final String tableName){

        JSONParser parser = new JSONParser();
        JSONObject testDataObject = null;

        List<Object> jsonResult = new ArrayList<>();
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        File file = new File(classLoader.getResource(TEST_DATA+File.separator+fileName).getFile());

        try{

            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(file));
            JSONArray jsonArray = (JSONArray) jsonObject.get("tableName");
            for(int i=0; i<jsonArray.size();i++){
                jsonResult.add((JSONObject) jsonArray.get(i));
            }

        }catch (FileNotFoundException e){
            throw new RuntimeException("File not found: ["+fileName+"]");
        }catch (IOException | ParseException e){
            throw new RuntimeException(e);
        }
        return jsonResult;
    }

}
