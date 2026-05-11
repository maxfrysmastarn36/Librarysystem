package whops;
import com.google.gson.Gson; // För att skapa en gson objekt att läsa in
import com.google.gson.GsonBuilder; // Skapa Gson med mer lättläst output
import com.google.gson.reflect.TypeToken; // Skapa TypeToken

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import kong.unirest.Unirest;
import kong.unirest.HttpResponse;

import java.nio.file.Files; // Filhantering
import java.nio.file.Paths; // Filhantering
import java.lang.reflect.Type; // För att Spara Typen från TypeToken
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        for (Book book : getTheMedia.getRequest("books", Book.class)){
            System.out.println(book);
        }
    }
}

class Media {
    private String id;
    private String title;
    private boolean isAvailable;

    public Media(String id, String title, boolean isAvailable) {
        this.id = id;
        this.title = title;
        this.isAvailable = isAvailable;
    }

    @Override
    public String toString(){
        return "id= "+ id +  "title= "+ title + ", isAvailable = " + isAvailable;
    }
}

class Book extends Media{
    private int pages;
    private String author;
    private String genre;

    public Book(int pages, String author, String genre, String id, String title, boolean isAvailable){
        super(id, title, isAvailable);
        this.pages = pages;
        this.author = author;
        this.genre = genre;
    }
    @Override
    public String toString(){
        return super.toString() + ", pages = " + pages + " ,Author = " + author + ", genre = " + genre;
    }
}

class Magazinis extends Media{
    private int issueNumber;
    private int publishedYear;
    private String category;

    public Magazinis(int issueNumber, int publishedYear, String category, String id, String title, boolean isAvailable){
        super(id, title, isAvailable);
        this.issueNumber = issueNumber;
        this.publishedYear = publishedYear;
        this.category = category;
    }

    @Override
    public String toString(){
        return super.toString() + ", Issue number = " + issueNumber + ", Publish year = " + publishedYear + ", Category = " + category;
    }
}

class getTheMedia{
    private static String BaseURL =  "http://10.151.168.5:3147/";
    public static <T> List<T> getRequest(String mediaType, Class<T> classType){
        try {
            HttpResponse<String> response = Unirest.get(BaseURL + mediaType).asString();
            Gson gson = new Gson();
            Type listType = TypeToken.getParameterized(List.class, classType).getType();
            return gson.fromJson(response.getBody(), listType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
