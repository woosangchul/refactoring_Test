package service;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


public class UserServiceImpl implements UserService{


    String jSessionParser(String jSessionId){
        StringTokenizer st = new StringTokenizer(jSessionId.substring(11), ";");

        return st.nextToken();

    }
    Map<String, String> tokenParser(String access_token, String refresh_token){
        Map<String, String> map = new HashMap<>();
        StringTokenizer st = null;



        st = new StringTokenizer(access_token.substring(13),";");
        map.put("access_token", st.nextToken());

        st = new StringTokenizer(refresh_token.substring(14),";");
        map.put("refresh_token", st.nextToken());

        return map;
    }
    String makeCookieHeader(String jsession, String access_token, String refresh_token){
        return "JSESSIONID="+jsession+"; access_token="+access_token+"; refresh_token="+refresh_token;
    }

    @Override
    public boolean loginCheck(String id, String pwd) throws IOException {
        String url = "https://sso1.mju.ac.kr/mju/userCheck.do";


        HttpClient httpClient2 = HttpClientBuilder.create().disableRedirectHandling().build();
        String request_url = "https://sso1.mju.ac.kr/mju/userCheck.do";
        HttpPost httpPost = new HttpPost(request_url);
        httpPost.addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36");
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");


        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("passwrd", pwd));
        httpPost.setEntity(new UrlEncodedFormEntity(params));
        HttpResponse httpResponse = httpClient2.execute(httpPost);
        System.out.println("asdf");

        BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(),"UTF-8"));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while((inputLine = reader.readLine()) != null){
            response.append(inputLine);
        }
        reader.close();

        try{
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(response.toString());

            if ( jsonObject.get("error").toString().equals("VL-3130") || jsonObject.get("error").toString().equals("0000") ){
                return true;
            }
        }catch (Exception e){

        }



        return false;


    }

    @Override
    public String getToken(String id, String pwd) throws IOException {

        /*
        apach HTTPClient를 사용해서 토큰 가져오는 로직
        기존  HTTPURLCONNECTION을 사용하면 Origin헤더를 추가해야하는데 보안상 추가가안돼서 api를 못가져오는 문제발생
         */
        String request_url = "https://home.mju.ac.kr/ssoChk.jsp";

        HttpClient httpClient1 = HttpClientBuilder.create().disableRedirectHandling().build();
        HttpGet httpGet = new HttpGet(request_url);
        httpGet.addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36");
        httpGet.addHeader("Host","home.mju.ac.kr");
        httpGet.addHeader("Referer","https://home.mju.ac.kr/user/index.action");
        httpGet.addHeader("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
        HttpResponse httpResponse = httpClient1.execute(httpGet);
        String mainJsessionId = jSessionParser(httpResponse.getHeaders("Set-Cookie")[0].getValue());
        System.out.println("asdf");

        request_url = "https://sso1.mju.ac.kr/login.do?redirect_uri=https://home.mju.ac.kr/user/index.action";
        httpGet = new HttpGet(request_url);
        httpGet.addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36");
        httpGet.addHeader("Host","sso1.mju.ac.kr");
        httpGet.addHeader("Referer","https://home.mju.ac.kr/");
        httpGet.addHeader("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
        httpResponse = httpClient1.execute(httpGet);
        String loginJsessionId = jSessionParser(httpResponse.getHeaders("Set-Cookie")[0].getValue());






        HttpClient httpClient2 = HttpClientBuilder.create().disableRedirectHandling().build();
        request_url = "https://sso1.mju.ac.kr/mju/userCheck.do";
        HttpPost httpPost = new HttpPost(request_url);
        httpPost.addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36");
        httpPost.addHeader("Host","sso1.mju.ac.kr");
        httpPost.addHeader("Origin","https://sso1.mju.ac.kr");
        httpPost.addHeader("Referer","https://sso1.mju.ac.kr/login.do?redirect_uri=https://home.mju.ac.kr/user/index.action");
        httpPost.addHeader("Cookie","JSESSIONID="+loginJsessionId);
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        httpPost.addHeader("sec-ch-ua","\"Chromium\";v=\"106\", \"Google Chrome\";v=\"106\", \"Not;A=Brand\";v=\"99\"");
        httpPost.addHeader("sec-ch-ua-mobile","?0");
        httpPost.addHeader("Sec-Fetch-Dest","empty");
        httpPost.addHeader("Sec-Fetch-Mode","cors");
        httpPost.addHeader("Sec-Fetch-Site","same-origin");
        httpPost.addHeader("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
        httpPost.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");


        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("passwrd", pwd));
        httpPost.setEntity(new UrlEncodedFormEntity(params));
        httpResponse = httpClient2.execute(httpPost);




        httpClient2 = HttpClientBuilder.create().disableRedirectHandling().build();
        request_url = "https://sso1.mju.ac.kr/login/ajaxActionLogin2.do";
        httpPost = new HttpPost(request_url);
        httpPost.addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36");
        httpPost.addHeader("Host","sso1.mju.ac.kr");
        httpPost.addHeader("Origin","https://sso1.mju.ac.kr");
        httpPost.addHeader("Referer","https://sso1.mju.ac.kr/login.do?redirect_uri=https://home.mju.ac.kr/user/index.action");
        httpPost.addHeader("Cookie","JSESSIONID="+loginJsessionId);
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.addHeader("sec-ch-ua","\"Chromium\";v=\"106\", \"Google Chrome\";v=\"106\", \"Not;A=Brand\";v=\"99\"");
        httpPost.addHeader("Sec-Fetch-Dest","document");
        httpPost.addHeader("Sec-Fetch-Mode","navigate");
        httpPost.addHeader("Sec-Fetch-Site","same-origin");
        httpPost.addHeader("Sec-Fetch-User","?1");
        httpPost.addHeader("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
        httpPost.addHeader("Upgrade-Insecure-Requests", "1");
        httpPost.addHeader("Sec-ch-ua-platform", "Windows");

        params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", id));
        params.add(new BasicNameValuePair("user_pwd", pwd));
        params.add(new BasicNameValuePair("redirect_uri", "https://home.mju.ac.kr/user/index.action"));
        httpPost.setEntity(new UrlEncodedFormEntity(params));
        httpResponse = httpClient2.execute(httpPost);








        request_url = "https://sso1.mju.ac.kr/oauth2/token2.do";
        httpPost = new HttpPost(request_url);
        httpPost.addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36");
        httpPost.addHeader("Host","sso1.mju.ac.kr");
        httpPost.addHeader("Origin","https://sso1.mju.ac.kr");
        httpPost.addHeader("Referer","https://sso1.mju.ac.kr/login.do?redirect_uri=https://home.mju.ac.kr/user/index.action");
        httpPost.addHeader("Cookie","JSESSIONID="+loginJsessionId);
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.addHeader("sec-ch-ua","\"Chromium\";v=\"106\", \"Google Chrome\";v=\"106\", \"Not;A=Brand\";v=\"99\"");
        httpPost.addHeader("Sec-Fetch-Dest","document");
        httpPost.addHeader("Sec-Fetch-Mode","navigate");
        httpPost.addHeader("Sec-Fetch-Site","same-origin");
        httpPost.addHeader("Sec-Fetch-User","?1");
        httpPost.addHeader("Upgrade-Insecure-Requests", "1");
        httpPost.addHeader("Sec-ch-ua-platform", "Windows");
        httpPost.addHeader("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");


        httpPost.setEntity(new UrlEncodedFormEntity(params));
        httpResponse = httpClient2.execute(httpPost);
        Map<String, String> map = tokenParser(httpResponse.getHeaders("Set-Cookie")[0].getValue(), httpResponse.getHeaders("Set-Cookie")[1].getValue());
        String s_cookie = makeCookieHeader(mainJsessionId, map.get("access_token"), map.get("refresh_token"));

                /*
        BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while((inputLine = reader.readLine()) != null){
            response.append(inputLine);
        }
        reader.close();

                 */



        request_url = "https://home.mju.ac.kr/user/index.action";

        httpGet = new HttpGet(request_url);
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


        /*
        BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(),"UTF-8"));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while((inputLine = reader.readLine()) != null){
            response.append(inputLine);
        }
        reader.close();
*/
        System.out.println(s_cookie);
        return s_cookie;

    }




}
