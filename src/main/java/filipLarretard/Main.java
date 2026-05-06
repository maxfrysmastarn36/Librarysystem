package filipLarretard;
import com.google.gson.Gson; // För att skapa en gson objekt att läsa in
import com.google.gson.GsonBuilder; // Skapa Gson med mer lättläst output
import com.google.gson.reflect.TypeToken; // Skapa TypeToken

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files; // Filhantering
import java.nio.file.Paths; // Filhantering
import java.lang.reflect.Type; // För att Spara Typen från TypeToken
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            List<book> books = getthebook.getAllbooks();
            books.forEach(System.out::println);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

class book {
    private int id;
    private String title;
    private String author;

    public book(int id, String name, String email) {
        this.id = id;
        this.title = name;
        this.author = email;
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", title='" + title + "', author='" + author + "'}";
    }
}

class getthebook {

    private static final Gson gson = new Gson();
    private static final String BASE_URL = "http://10.151.168.5:3147";

    // GET a single object
    public static book getUser(int bookid) throws IOException {
        URL url = new URL(BASE_URL + "/users/" + bookid);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        // Read the response
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            return gson.fromJson(reader, book.class); // Deserialize JSON → Object
        }
    }

    // GET a list of objects
    public static List<book> getAllbooks() throws IOException {
        URL url = new URL(BASE_URL + "/books");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            Type listType = new TypeToken<List<book>>(){}.getType();
            return gson.fromJson(reader, listType); // Deserialize JSON array → List
        }
    }
}

