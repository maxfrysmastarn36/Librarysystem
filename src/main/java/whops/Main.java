package whops;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import kong.unirest.Unirest;
import kong.unirest.HttpResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Main {
    static List<Book> books = new ArrayList<>();
    static List<Magazinis> magazines = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        while (true){
            System.out.println("\n=== Bibliotek Meny ===");
            System.out.println("1. Hämta böcker");
            System.out.println("2. Hämta tidningar");
            System.out.println("3. Skriv ut böcker");
            System.out.println("4. Skriv ut tidningar");
            System.out.println("5. Lägg till bok");
            System.out.println("6. Lägg till tidning");
            System.out.println("7. Avsluta");
            System.out.print("Välj: ");

            String choise = scanner.nextLine();

            switch (choise){
                case "1" -> {
                    books = getTheMedia.getRequest("books", Book.class);
                    System.out.println("Hämtade " + books.size() + " böcker.");
                }
                case "2" -> {
                    magazines = getTheMedia.getRequest("magazines", Magazinis.class);
                    System.out.println("Hämtade " + magazines.size() + " tidningar.");
                }
                case "3" -> {
                    if (books.isEmpty()) {
                        System.out.println("Inga böcker hämtade, välj alternativ 1 först.");
                    } else {
                        books.forEach(System.out::println);
                    }
                }
                case "4" -> {
                    if (magazines.isEmpty()) {
                        System.out.println("Inga tidningar hämtade, välj alternativ 2 först.");
                    } else {
                        magazines.forEach(System.out::println);
                    }
                }
                case "5" -> {
                    System.out.print("Titel: ");
                    String title = scanner.nextLine();
                    System.out.print("Författare: ");
                    String author = scanner.nextLine();
                    System.out.print("Genre: ");
                    String genre = scanner.nextLine();
                    System.out.print("Antal sidor: ");
                    int pages = Integer.parseInt(scanner.nextLine());

                    Book book = new Book(pages, author, "", genre, title, true);
                    books.add(book);
                    book.post(); // postar till API och sparar lokalt
                    System.out.println("Bok tillagd!");
                }
                case "6" -> {
                    System.out.print("Titel: ");
                    String title = scanner.nextLine();
                    System.out.print("Nummer: ");
                    int issueNumber = Integer.parseInt(scanner.nextLine());
                    System.out.print("Utgivningsår: ");
                    int publishedYear = Integer.parseInt(scanner.nextLine());
                    System.out.print("Kategori: ");
                    String category = scanner.nextLine();

                    Magazinis mag = new Magazinis(issueNumber, publishedYear, category, "", title, true);
                    magazines.add(mag);
                    mag.post();
                    System.out.println("Tidning tillagd!");
                }
                case "7" -> {
                    System.out.println("Avslutar...");
                    return;
                }
                default -> System.out.println("Ogiltigt val, försök igen.");
            }
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

    protected String adressEnd(){
        return "media";
    }

    public void post(){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(this);
        HttpResponse<String> response = Unirest.post(config.BaseURL + adressEnd()).header("Content-Type", "application/json").body(json).asString();
        try{
            java.nio.file.Path path = Paths.get(adressEnd() + ".json");

            List<Object> list;
            if (Files.exists(path)) {
                Type listType = new TypeToken<List<Object>>(){}.getType();
                list = gson.fromJson(Files.readString(path), listType);
            } else {
                list = new ArrayList<>();
            }

            list.add(gson.fromJson(json, Object.class));
            Files.writeString(path, gson.toJson(list));
            System.out.println("Saved to " + path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (response.getStatus() == 201) {
            System.out.println("Posted successfully");
            try{
                Files.writeString(Paths.get(adressEnd() + ".json"), json);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else{
            System.out.println("Failed: " + response.getStatus());
        }
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

    @Override
    protected String adressEnd(){
        return "books";
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

    @Override
    protected String adressEnd(){
        return "magazines";
    }
}

class getTheMedia{
    public static <T> List<T> getRequest(String mediaType, Class<T> classType){
        try {
            HttpResponse<String> response = Unirest.get(config.BaseURL + mediaType).asString();
            Gson gson = new Gson();
            String json = response.getBody();

            Files.writeString(Paths.get(mediaType + ".json"), json);
            System.out.println("Saved to " + mediaType +".json");

            Type listType = TypeToken.getParameterized(List.class, classType).getType();
            return gson.fromJson(json, listType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}


//Konstanter
class config{
    public static final String BaseURL = "http://10.151.168.5:3147/";
}