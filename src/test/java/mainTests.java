import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.junit.jupiter.api.Test;
import service.UserServiceImpl;

import java.io.IOException;
import java.util.*;


public class mainTests {

    String jSessionParser(HttpResponse httpResponse){
        StringTokenizer st = new StringTokenizer(httpResponse.getHeaders("Set-Cookie")[0].getValue().substring(11), ";");
        return st.nextToken();
    }



    Map<String, String> tokenParser(HttpResponse httpResponse){
        Map<String, String> map = new HashMap<>();
        StringTokenizer st = null;

        st = new StringTokenizer( httpResponse.getHeaders("Set-Cookie")[0].getValue().substring(13),";");
        map.put("access_token", st.nextToken());

        st = new StringTokenizer(httpResponse.getHeaders("Set-Cookie")[1].getValue().substring(14),";");
        map.put("refresh_token", st.nextToken());

        return map;
    }

    String makeCookieHeader(String jsession, String access_token, String refresh_token){
        return "JSESSIONID="+jsession+"; access_token="+access_token+"; refresh_token="+refresh_token;
    }

    HttpResponse getHttpGetResult(String requestUrl, HashMap<String,String> header) throws IOException {

        HttpClient httpClient = HttpClientBuilder.create().disableRedirectHandling().build();
        HttpGet httpGet = new HttpGet(requestUrl);
        for (String key : header.keySet()){
            httpGet.addHeader(key, header.get(key));
        }
        return httpClient.execute(httpGet);

    }

    HttpResponse getHttpPostResult(String request_url, HashMap<String, String> header, HashMap<String,String> params) throws IOException {

        HttpClient httpClient = HttpClientBuilder.create().disableRedirectHandling().build();

        HttpPost httpPost = new HttpPost(request_url);
        for (String key : header.keySet()){
            httpPost.addHeader(key, header.get(key));
        }

        List<NameValuePair> parameter = new ArrayList<NameValuePair>();
        for (String key : params.keySet()){
            parameter.add(new BasicNameValuePair(key,params.get(key)));
        }

        httpPost.setEntity(new UrlEncodedFormEntity(parameter));
        return httpClient.execute(httpPost);


    }


    @Test
    void printTest() throws IOException {
        String id = "60190525";
        String pwd   = "wjddk1633@";

/*
apach HTTPClient를 사용해서 토큰 가져오는 로직
기존  HTTPURLCONNECTION을 사용하면 Origin헤더를 추가해야하는데 보안상 추가가안돼서 api를 못가져오는 문제발생
*/

        HashMap<String,String> header = new HashMap();
        header.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36");
        header.put("Host","home.mju.ac.kr");
        header.put("Referer","https://home.mju.ac.kr/user/index.action");
        header.put("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
        String mainJsessionId = jSessionParser(getHttpGetResult("https://home.mju.ac.kr/ssoChk.jsp", header));


        header = new HashMap();
        header.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36");
        header.put("Host","sso1.mju.ac.kr");
        header.put("Referer","https://home.mju.ac.kr/");
        header.put("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");

        String loginJsessionId = jSessionParser(getHttpGetResult("https://sso1.mju.ac.kr/login.do?redirect_uri=https://home.mju.ac.kr/user/index.action", header));


        header = new HashMap<String,String>();
        header.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36");
        header.put("Host","sso1.mju.ac.kr");
        header.put("Origin","https://sso1.mju.ac.kr");
        header.put("Referer","https://sso1.mju.ac.kr/login.do?redirect_uri=https://home.mju.ac.kr/user/index.action");
        header.put("Cookie","JSESSIONID="+loginJsessionId);
        header.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        header.put("sec-ch-ua","\"Chromium\";v=\"106\", \"Google Chrome\";v=\"106\", \"Not;A=Brand\";v=\"99\"");
        header.put("sec-ch-ua-mobile","?0");
        header.put("Sec-Fetch-Dest","empty");
        header.put("Sec-Fetch-Mode","cors");
        header.put("Sec-Fetch-Site","same-origin");
        header.put("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
        header.put("Accept", "application/json, text/javascript, */*; q=0.01");

        HashMap<String,String> params = new HashMap<>();
        params.put("id", id);
        params.put("passwrd", pwd);
        HttpResponse httpResponse = getHttpPostResult("https://sso1.mju.ac.kr/mju/userCheck.do", header, params);



        header = new HashMap<String,String>();
        header.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36");
        header.put("Host","sso1.mju.ac.kr");
        header.put("Origin","https://sso1.mju.ac.kr");
        header.put("Referer","https://sso1.mju.ac.kr/login.do?redirect_uri=https://home.mju.ac.kr/user/index.action");
        header.put("Cookie","JSESSIONID="+loginJsessionId);
        header.put("Content-Type", "application/x-www-form-urlencoded");
        header.put("sec-ch-ua","\"Chromium\";v=\"106\", \"Google Chrome\";v=\"106\", \"Not;A=Brand\";v=\"99\"");
        header.put("Sec-Fetch-Dest","document");
        header.put("Sec-Fetch-Mode","navigate");
        header.put("Sec-Fetch-Site","same-origin");
        header.put("Sec-Fetch-User","?1");
        header.put("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
        header.put("Upgrade-Insecure-Requests", "1");
        header.put("Sec-ch-ua-platform", "Windows");

        params = new HashMap<>();
        params.put("user_id", id);
        params.put("user_pwd", pwd);
        params.put("redirect_uri", "https://home.mju.ac.kr/user/index.action");

        httpResponse = getHttpPostResult("https://sso1.mju.ac.kr/login/ajaxActionLogin2.do", header, params);







        header = new HashMap<String,String>();
        header.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36");
        header.put("Host","sso1.mju.ac.kr");
        header.put("Origin","https://sso1.mju.ac.kr");
        header.put("Referer","https://sso1.mju.ac.kr/login.do?redirect_uri=https://home.mju.ac.kr/user/index.action");
        header.put("Cookie","JSESSIONID="+loginJsessionId);
        header.put("Content-Type", "application/x-www-form-urlencoded");
        header.put("sec-ch-ua","\"Chromium\";v=\"106\", \"Google Chrome\";v=\"106\", \"Not;A=Brand\";v=\"99\"");
        header.put("Sec-Fetch-Dest","document");
        header.put("Sec-Fetch-Mode","navigate");
        header.put("Sec-Fetch-Site","same-origin");
        header.put("Sec-Fetch-User","?1");
        header.put("Upgrade-Insecure-Requests", "1");
        header.put("Sec-ch-ua-platform", "Windows");
        header.put("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");

        params = new HashMap<>();
        params.put("user_id", id);
        params.put("user_pwd", pwd);
        params.put("redirect_uri", "https://home.mju.ac.kr/user/index.action");

        httpResponse = getHttpPostResult("https://sso1.mju.ac.kr/oauth2/token2.do", header, params);
        Map<String, String> map = tokenParser(httpResponse);

        String s_cookie = makeCookieHeader(mainJsessionId, map.get("access_token"), map.get("refresh_token"));



                /*
                원래 삭제되어잇던부분
        BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while((inputLine = reader.readLine()) != null){
            response.append(inputLine);
        }
        reader.close();

                 */


/*
        request_url = "https://home.mju.ac.kr/user/index.action";

        HttpGet httpGet = new HttpGet(request_url);
        httpGet.addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36");
        httpGet.addHeader("Host","home.mju.ac.kr");
        httpGet.addHeader("Referer","https://sso1.mju.ac.kr/");
        httpGet.addHeader("Cookie", s_cookie);
        httpResponse = httpClient2.execute(httpGet);




        request_url = "https://home.mju.ac.kr/mainIndex/myHomeworkList.action?command=&tab=homework";

        httpGet = new HttpGet(request_url);
        httpGet.addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36");
        httpGet.addHeader("Host","home.mju.ac.kr");
        httpGet.addHeader("Referer","https://home.mju.ac.kr/user/index.action");
        httpGet.addHeader("Cookie", s_cookie);
        httpResponse = httpClient2.execute(httpGet);

        System.out.println("asdf");
*/

        /*
        원래삭제되어잇던부분
        BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(),"UTF-8"));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while((inputLine = reader.readLine()) != null){
            response.append(inputLine);
        }
        reader.close();
*/


        //System.out.println(s_cookie);


    }

}
