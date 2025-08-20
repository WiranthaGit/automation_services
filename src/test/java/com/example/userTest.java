//package com.example;
//
//
//import base.BaseTestObject;
//import com.cloud.core.annotations.TestCaseID;
//import com.cloud.core.annotations.TestCaseType;
//import com.cloud.core.config.enums.ConfigKeys;
//import com.cloud.core.config.provider.Config;
//import com.cloud.core.config.provider.DefaultConfig;
//import com.cloud.core.enums.TestType;
//import com.cloud.core.testdataprovider.enums.DataProviderType;
//import com.cloud.core.testdataprovider.utils.DataProviderUtil;
//import com.example.service.ApiService;
//import io.restassured.http.Header;
//import io.restassured.http.Headers;
//import io.restassured.response.Response;
//import org.testng.annotations.BeforeClass;
//import org.testng.annotations.Test;
//
//public class userTest extends BaseTestObject {
//    private static ApiService apiService;
//    protected static Headers headers;
//
//
//    @BeforeClass
//    public void serviceSetUp() {
//        try {
//            apiService = new ApiService();
//            headers = new Headers(
//                   new Header("Content-Type","application/json")
//            );
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//    }
//    @TestCaseID(id = {1})
//    @TestCaseType(types = {TestType.BVT, TestType.REGRESSION})
//    @Test()
//    public void verifyGetUsers() throws Exception {
//
////        Response response = apiService.getUsers(headers,Response.class);
////        System.out.printf(response.toString());
//    }
//}
