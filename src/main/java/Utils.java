import com.google.gson.Gson;

/**
 * @author a-vasin
 */
public class Utils {
    private Utils() {
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return new Gson().fromJson(json, clazz);
    }

    public static <T> String toJson(T object) {
        return new Gson().toJson(object);
    }
}
