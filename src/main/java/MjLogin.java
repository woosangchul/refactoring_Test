import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

public class MjLogin {
    private String id;
    private String pwd;
    private String accessToken;
    private String refreshToken;
    private String mainLoginJSession;
    private String loginJSession;


    public MjLogin(String id, String pwd) {
        this.id = id;
        this.pwd = pwd;
    }

    private HttpResponse getHttpGetResult(String requestUrl, HashMap<String,String> header) throws IOException {

        HttpClient httpClient = HttpClientBuilder.create().disableRedirectHandling().build();
        HttpGet httpGet = new HttpGet(requestUrl);
        for (String key : header.keySet()){
            httpGet.addHeader(key, header.get(key));
        }
        return httpClient.execute(httpGet);

    }

    private HttpResponse getHttpPostResult(String request_url, HashMap<String, String> header, HashMap<String,String> params) throws IOException {
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

    public String jSessionParser(HttpResponse httpResponse) {
        return new StringTokenizer(httpResponse.getHeaders("Set-Cookie")[0].getValue().substring(11), ";").nextToken();
    }

    public void tokenParser(HttpResponse httpResponse){
        StringTokenizer st = null;
        StringBuffer sb = new StringBuffer();

        st = new StringTokenizer( httpResponse.getHeaders("Set-Cookie")[0].getValue().substring(13),";");
        this.accessToken = st.nextToken();

        st = new StringTokenizer(httpResponse.getHeaders("Set-Cookie")[1].getValue().substring(14),";");
        this.refreshToken = st.nextToken();
    }

    private void getMainJSession() throws IOException {
        HashMap<String,String> header = new HashMap();
        header.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36");
        header.put("Host","home.mju.ac.kr");
        header.put("Referer","https://home.mju.ac.kr/user/index.action");
        header.put("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");

        this.mainLoginJSession = this.jSessionParser(this.getHttpGetResult( "https://home.mju.ac.kr/ssoChk.jsp", header));
    }

    private void getLoginJSession() throws IOException {
        HashMap<String,String> header = new HashMap();
        header.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36");
        header.put("Host","sso1.mju.ac.kr");
        header.put("Referer","https://home.mju.ac.kr/");
        header.put("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");

        this.loginJSession = this.jSessionParser(this.getHttpGetResult("https://sso1.mju.ac.kr/login.do?redirect_uri=https://home.mju.ac.kr/user/index.action", header));
    }

    private void loginProcStep1() throws IOException {
        HashMap<String,String> header = new HashMap<String,String>();
        header.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36");
        header.put("Host","sso1.mju.ac.kr");
        header.put("Origin","https://sso1.mju.ac.kr");
        header.put("Referer","https://sso1.mju.ac.kr/login.do?redirect_uri=https://home.mju.ac.kr/user/index.action");
        header.put("Cookie","JSESSIONID="+this.loginJSession);
        header.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        header.put("sec-ch-ua","\"Chromium\";v=\"106\", \"Google Chrome\";v=\"106\", \"Not;A=Brand\";v=\"99\"");
        header.put("sec-ch-ua-mobile","?0");
        header.put("Sec-Fetch-Dest","empty");
        header.put("Sec-Fetch-Mode","cors");
        header.put("Sec-Fetch-Site","same-origin");
        header.put("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
        header.put("Accept", "application/json, text/javascript, */*; q=0.01");

        HashMap<String,String> params = new HashMap<>();
        params.put("id", this.id);
        params.put("passwrd", this.pwd);

        getHttpPostResult("https://sso1.mju.ac.kr/mju/userCheck.do", header, params);

    }
    private void loginProcStep2() throws IOException {
        HashMap<String,String> header = new HashMap<String,String>();
        header.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36");
        header.put("Host","sso1.mju.ac.kr");
        header.put("Origin","https://sso1.mju.ac.kr");
        header.put("Referer","https://sso1.mju.ac.kr/login.do?redirect_uri=https://home.mju.ac.kr/user/index.action");
        header.put("Cookie","JSESSIONID="+this.loginJSession);
        header.put("Content-Type", "application/x-www-form-urlencoded");
        header.put("sec-ch-ua","\"Chromium\";v=\"106\", \"Google Chrome\";v=\"106\", \"Not;A=Brand\";v=\"99\"");
        header.put("Sec-Fetch-Dest","document");
        header.put("Sec-Fetch-Mode","navigate");
        header.put("Sec-Fetch-Site","same-origin");
        header.put("Sec-Fetch-User","?1");
        header.put("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
        header.put("Upgrade-Insecure-Requests", "1");
        header.put("Sec-ch-ua-platform", "Windows");

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", this.id);
        params.put("user_pwd", this.pwd);
        params.put("redirect_uri", "https://home.mju.ac.kr/user/index.action");

        getHttpPostResult("https://sso1.mju.ac.kr/login/ajaxActionLogin2.do", header, params);

    }
    private void loginProcStep3() throws IOException {
        HashMap<String, String> header = new HashMap<String,String>();
        header.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36");
        header.put("Host","sso1.mju.ac.kr");
        header.put("Origin","https://sso1.mju.ac.kr");
        header.put("Referer","https://sso1.mju.ac.kr/login.do?redirect_uri=https://home.mju.ac.kr/user/index.action");
        header.put("Cookie","JSESSIONID="+this.loginJSession);
        header.put("Content-Type", "application/x-www-form-urlencoded");
        header.put("sec-ch-ua","\"Chromium\";v=\"106\", \"Google Chrome\";v=\"106\", \"Not;A=Brand\";v=\"99\"");
        header.put("Sec-Fetch-Dest","document");
        header.put("Sec-Fetch-Mode","navigate");
        header.put("Sec-Fetch-Site","same-origin");
        header.put("Sec-Fetch-User","?1");
        header.put("Upgrade-Insecure-Requests", "1");
        header.put("Sec-ch-ua-platform", "Windows");
        header.put("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", this.id);
        params.put("user_pwd", this.pwd);
        params.put("redirect_uri", "https://home.mju.ac.kr/user/index.action");

        tokenParser(getHttpPostResult("https://sso1.mju.ac.kr/oauth2/token2.do", header, params));
    }

    public void execute() throws IOException {
        getMainJSession();
        getLoginJSession();
        loginProcStep1();
        loginProcStep2();
        loginProcStep3();
    }


    @Override
    public String toString() {
        return "mainJsession:"+this.mainLoginJSession + "\n" +
                "loginJsession:" + this.loginJSession + "\n" +
                "accessToken:" + this.accessToken + "\n" +
                "refreshToken" + this.refreshToken;
    }
}
