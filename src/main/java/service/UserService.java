package service;


import dto.ResultDTO;

import java.io.IOException;
import java.util.ArrayList;

public interface UserService {

    boolean loginCheck(String id, String pwd) throws IOException;

    String getToken(String id, String pwd) throws IOException;

}
