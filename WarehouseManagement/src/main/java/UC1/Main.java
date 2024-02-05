package UC1;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args){
        SQLiteJDBC.connect();
        WelcomePage welc = new WelcomePage();
    }
}
