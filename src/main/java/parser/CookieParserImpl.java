package parser;

import org.apache.http.HttpResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class CookieParserImpl implements CookieParser{
    @Override
    public String jSessionParser(HttpResponse httpResponse) {
        return new StringTokenizer(httpResponse.getHeaders("Set-Cookie")[0].getValue().substring(11), ";").nextToken();
    }

    @Override
    public String tokenParser(HttpResponse httpResponse) {
        StringTokenizer st = null;
        StringBuffer sb = new StringBuffer();

        st = new StringTokenizer( httpResponse.getHeaders("Set-Cookie")[0].getValue().substring(13),";");
        sb.append("access_token="+st.nextToken()+"; ");

        st = new StringTokenizer(httpResponse.getHeaders("Set-Cookie")[1].getValue().substring(14),";");
        sb.append("refresh_token"+st.nextToken());

        return sb.toString();



    }
}
