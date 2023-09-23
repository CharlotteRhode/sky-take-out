package com.sky;


import com.sky.Annotation.AutoFill;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.mockito.exceptions.misusing.CannotStubVoidMethodWithReturnValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

//@SpringBootTest
public class test {


    //password
    @Test
    public void MD5Test() {
        System.out.println(DigestUtils.md5DigestAsHex("123456".getBytes()));
    }


    //http client - POST
    @Test
    public void testPost() throws IOException {

        //1.构造httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        //2.构造请求对象
        HttpPost httpPost = new HttpPost("http://localhost:8080/admin/employee/login");
        HttpEntity entity = new StringEntity("{\"username\":\"admin\", \"password\":\"123456\"}");
        // {"username":"admin", "password":"123456"}
        httpPost.setEntity(entity);//请求体
        httpPost.setHeader("Content-type", "application/json");//请求头

        //3.发送请求
        CloseableHttpResponse response = httpClient.execute(httpPost);
        //4。获取响应结果
        System.out.println(response.getStatusLine());//状态码
        HttpEntity responseEntity = response.getEntity();
        System.out.println(EntityUtils.toString(responseEntity));//响应体
        //5.释放资源
        response.close();
        httpClient.close();
    }


    //http client - GET
    @Test
    public void testGet() throws IOException {
        //1.构造httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        //2.构造请求对象 // http://localhost:8080/admin/category.page
        HttpGet httpGet = new HttpGet("http://localhost:8080/admin/category/page?page=1&pageSize=5");
        httpGet.setHeader("token", "eyJhbGciOiJIUzI1NiJ9.eyJlbXBJZCI6MSwiZXhwIjoxNjk0Nzg5MjQzfQ.ic0BconFHs4UBD4oMN0wao_a6oHSHXp3hcfFls7SjMA" ); //设置请求头 - 令牌
        

        //3.发送请求
        CloseableHttpResponse response = httpClient.execute(httpGet);


        //4。获取响应结果
        System.out.println(response.getStatusLine());//状态码
        HttpEntity responseEntity = response.getEntity();
        System.out.println(EntityUtils.toString(responseEntity));//响应体

        //5.释放资源
        response.close();
        httpClient.close();
    }


}
