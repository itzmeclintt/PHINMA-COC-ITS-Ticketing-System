package gui;



public class UserSession {
     private static int userId;
    private static String username;
    
     public static void setUser(int id, String user) {
        userId = id;
        username = user;
    }

    public static int getUserId() {
        return userId;
    }

    public static String getUsername() {
        return username;
    }

    public static void clearSession() {
        userId = 0;
        username = null;
    }
}
