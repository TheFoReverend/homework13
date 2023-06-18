import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UserAPI {
    private static final String BASE_URL = "https://jsonplaceholder.typicode.com/users";

    public String createUser(String userData) throws IOException {
        URL url = new URL(BASE_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        connection.getOutputStream().write(userData.getBytes());
        connection.getOutputStream().flush();
        connection.getOutputStream().close();

        int responseCode = connection.getResponseCode();
        if (responseCode == 201) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return response.toString();
        } else {
            return null;
        }
    }

    public String updateUser(String userId, String updatedData) throws IOException {
        URL url = new URL(BASE_URL + "/" + userId);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        connection.getOutputStream().write(updatedData.getBytes());
        connection.getOutputStream().flush();
        connection.getOutputStream().close();

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return response.toString();
        } else {
            return null;
        }
    }

    public boolean deleteUser(String userId) throws IOException {
        URL url = new URL(BASE_URL + "/" + userId);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");

        int responseCode = connection.getResponseCode();
        return responseCode / 100 == 2;
    }

    public String getAllUsers() throws IOException {
        URL url = new URL(BASE_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return response.toString();
        } else {
            return null;
        }
    }

    public String getUserById(String userId) throws IOException {
        URL url = new URL(BASE_URL + "/" + userId);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return response.toString();
        } else {
            return null;
        }
    }

    public String getUserByUsername(String username) throws IOException {
        URL url = new URL(BASE_URL + "?username=" + username);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return response.toString();
        } else {
            return null;
        }
    }

    public void getAndSaveComments(int userId, int postId) throws IOException {
        String postUrl = BASE_URL + "/" + userId + "/posts";
        String postResponse = sendGetRequest(postUrl);
        if (postResponse == null) {
            System.out.println("Не вдалося отримати пости користувача");
            return;
        }

        String lastPostId = findLastPostId(postResponse);
        if (lastPostId == null) {
            System.out.println("Не знайдено жодного поста користувача");
            return;
        }

        String commentsUrl = BASE_URL + "/" + userId + "/posts/" + lastPostId + "/comments";
        String commentsResponse = sendGetRequest(commentsUrl);
        if (commentsResponse == null) {
            System.out.println("Не вдалося отримати коментарі");
            return;
        }

        String fileName = "user-" + userId + "-post-" + lastPostId + "-comments.json";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(commentsResponse);
            System.out.println("Коментарі збережено у файл: " + fileName);
        } catch (IOException e) {
            System.out.println("Помилка запису у файл");
        }
    }

    private String sendGetRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return response.toString();
        } else {
            return null;
        }
    }

    private String findLastPostId(String postResponse) {
        String[] posts = postResponse.split("\\},");
        String lastPostId = null;
        for (String post : posts) {
            if (post.contains("\"id\":")) {
                lastPostId = post.replaceAll("[^0-9]", "");
            }
        }
        return lastPostId;
    }

    public void getOpenTasksForUser(int userId) throws IOException {
        String url = BASE_URL + "/" + userId + "/todos";
        String response = sendGetRequest(url);

        if (response != null) {
            System.out.println("Відкриті задачі для користувача з ідентифікатором " + userId + ":");
            String[] todos = response.split("\\},");
            for (String todo : todos) {
                if (todo.contains("\"completed\": false")) {
                    System.out.println(todo);
                }
            }
        } else {
            System.out.println("Не вдалося отримати задачі для користувача з ідентифікатором " + userId);
        }
    }

    public static void main(String[] args) {
        UserAPI api = new UserAPI();

        String userData = "{\"name\": \"John Doe\", \"username\": \"johndoe\", \"email\": \"johndoe@example.com\"}";
        try {
            String createdUser = api.createUser(userData);
            System.out.println("Створено нового користувача: " + createdUser);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String updatedData = "{\"name\": \"John Doe\", \"username\": \"johndoe\", \"email\": \"johndoe@example.com\"}";
        try {
            String updatedUser = api.updateUser("1", updatedData);
            System.out.println("Оновлено користувача: " + updatedUser);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            boolean deleted = api.deleteUser("1");
            System.out.println("Видалено користувача: " + deleted);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            String allUsers = api.getAllUsers();
            System.out.println("Всі користувачі: " + allUsers);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            String userById = api.getUserById("1");
            System.out.println("Користувач за ідентифікатором 1: " + userById);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            String userByUsername = api.getUserByUsername("johndoe");
            System.out.println("Користувач за ім'ям користувача 'johndoe': " + userByUsername);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            api.getAndSaveComments(1, 10);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            api.getOpenTasksForUser(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
