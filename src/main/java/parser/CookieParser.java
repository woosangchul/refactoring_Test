package parser;

import org.apache.http.HttpResponse;

import java.util.Map;

public interface CookieParser {

    String jSessionParser(HttpResponse httpResponse);
    String tokenParser(HttpResponse httpResponse);
}
